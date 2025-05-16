package bmt.service;

import bmt.database.MongoDBConnection;
import bmt.model.QuizQuestion;
import bmt.model.Word;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestService {
    private final MongoCollection<Document> wordsCollection;
    private final Random random;

    public TestService() {
        this.wordsCollection = MongoDBConnection.getDatabase().getCollection("words");
        this.random = new Random();
    }

    public QuizQuestion generateQuizQuestion(int difficulty) {
        try {
            // Zorluk seviyesine göre kelime seçimi
            List<Word> words = getWordsByDifficulty(difficulty);
            if (words.isEmpty()) {
                return null;
            }

            // Rastgele bir kelime seç
            Word correctWord = words.get(random.nextInt(words.size()));
            
            // Yanlış cevaplar için diğer kelimelerden seç
            List<String> wrongAnswers = new ArrayList<>();
            while (wrongAnswers.size() < 3) {
                Word randomWord = words.get(random.nextInt(words.size()));
                if (!randomWord.getTurkish().equals(correctWord.getTurkish())) {
                    wrongAnswers.add(randomWord.getTurkish());
                }
            }

            return new QuizQuestion(
                correctWord.getEnglish(),
                correctWord.getTurkish(),
                wrongAnswers
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Word> getWordsByDifficulty(int difficulty) {
        List<Word> words = new ArrayList<>();
        try (MongoCursor<Document> cursor = wordsCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Word word = new Word();
                word.setEnglish(doc.getString("english"));
                word.setTurkish(doc.getString("turkish"));
                word.setDifficulty(doc.getInteger("difficulty", 1));
                
                // Zorluk seviyesine göre filtrele
                if (word.getDifficulty() == difficulty) {
                    words.add(word);
                }
            }
        }
        return words;
    }
} 