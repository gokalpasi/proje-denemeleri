package bmt.service;

import bmt.database.MongoDBConnection;
import bmt.model.QuizQuestion;
import bmt.model.Word;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TestService {

    private final MongoCollection<Document> wordsCollection;
    // private final WordService wordService; // Eğer WordService'teki documentToWord kullanılacaksa

    public TestService() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        this.wordsCollection = database.getCollection("words");
        // this.wordService = new WordService(); 
    }

    // WordService'teki documentToWord metodunu kullanmak yerine burada yerel bir kopya oluşturalım
    // veya WordService.documentToWord static yapılırsa direkt çağrılabilir.
    // Şimdilik, quiz için gerekli temel alanları içeren yerel bir documentToWord kullanıyoruz.
    private Word documentToWord(Document doc) {
        if (doc == null) return null;
        Word word = new Word();
        word.setId(doc.getObjectId("_id"));
        word.setEnglish(doc.getString("english"));
        word.setTurkish(doc.getString("turkish"));
        // Quiz için örnek cümle, resim vb. gerekmeyebilir, gerekirse eklenebilir.
        // word.setExampleSentence(doc.getString("exampleSentence")); 
        // word.setImageUrl(doc.getString("imageUrl"));
        word.setDifficulty(doc.getInteger("difficulty", 1)); // Varsayılan zorluk 1
        return word;
    }

    public QuizQuestion generateQuizQuestion(int difficulty) {
        Word correctWord = getRandomWordByDifficulty(difficulty, null);
        if (correctWord == null) {
            System.err.println("Uygun anahtar kelime bulunamadı. Zorluk: " + difficulty);
            return null; 
        }

        List<Word> incorrectOptionWords = getDistinctRandomWordsByDifficulty(difficulty, 3, correctWord.getId());
        if (incorrectOptionWords.size() < 3) {
            System.err.println("Yeterli yanlış seçenek bulunamadı. En az 3 tane gerekli. Bulunan: " + incorrectOptionWords.size() + " Zorluk: " + difficulty);
            return null; 
        }

        List<String> turkishOptions = new ArrayList<>();
        turkishOptions.add(correctWord.getTurkish());
        incorrectOptionWords.forEach(word -> turkishOptions.add(word.getTurkish()));
        Collections.shuffle(turkishOptions);

        return new QuizQuestion(correctWord, correctWord.getEnglish(), turkishOptions, correctWord.getTurkish());
    }

    private Word getRandomWordByDifficulty(int difficulty, ObjectId excludeId) {
        List<Document> pipeline = new ArrayList<>();
        Document matchFilter = new Document("difficulty", difficulty);
        if (excludeId != null) {
            matchFilter.append("_id", new Document("$ne", excludeId));
        }
        pipeline.add(new Document("$match", matchFilter));
        pipeline.add(new Document("$sample", new Document("size", 1)));
        
        Document doc = wordsCollection.aggregate(pipeline).first();
        return documentToWord(doc);
    }

    private List<Word> getDistinctRandomWordsByDifficulty(int difficulty, int count, ObjectId excludeId) {
        List<Document> pipeline = new ArrayList<>();
        Document matchFilter = new Document("difficulty", difficulty);
        if (excludeId != null) { // excludeId her zaman sağlanmalı
            matchFilter.append("_id", new Document("$ne", excludeId));
        }
        pipeline.add(new Document("$match", matchFilter));
        pipeline.add(new Document("$sample", new Document("size", count)));

        return wordsCollection.aggregate(pipeline)
                .into(new ArrayList<>())
                .stream()
                .map(this::documentToWord)
                .filter(java.util.Objects::nonNull) // documentToWord null dönebilir
                .collect(Collectors.toList());
    }
} 