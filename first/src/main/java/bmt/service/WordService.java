package bmt.service;

import bmt.database.MongoDBConnection;
import bmt.model.Word;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WordService {
    private final MongoCollection<Document> wordsCollection;

    public WordService() {
        this.wordsCollection = MongoDBConnection.getDatabase().getCollection("words");
    }

    public Word addWord(Word word) {
        // Initialize review stages
        List<Word.ReviewStage> stages = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            Word.ReviewStage stage = new Word.ReviewStage(i);
            stage.setNextReviewDate(calculateNextReviewDate(i));
            stages.add(stage);
        }
        word.setReviewStages(stages);

        Document wordDoc = new Document()
                .append("english", word.getEnglish())
                .append("turkish", word.getTurkish())
                .append("exampleSentence", word.getExampleSentence())
                .append("imageUrl", word.getImageUrl())
                .append("audioUrl", word.getAudioUrl())
                .append("userId", word.getUserId())
                .append("reviewStages", stages.stream()
                        .map(stage -> new Document()
                                .append("stage", stage.getStage())
                                .append("completed", stage.isCompleted())
                                .append("nextReviewDate", stage.getNextReviewDate())
                                .append("correctAnswers", stage.getCorrectAnswers())
                                .append("totalAttempts", stage.getTotalAttempts()))
                        .collect(Collectors.toList()))
                .append("createdAt", word.getCreatedAt())
                .append("lastReviewed", word.getLastReviewed())
                .append("difficulty", word.getDifficulty());

        wordsCollection.insertOne(wordDoc);
        word.setId(wordDoc.getObjectId("_id"));
        return word;
    }

    public List<Word> getWordsForReview(ObjectId userId) {
        LocalDateTime now = LocalDateTime.now();
        return wordsCollection.find(
                Filters.and(
                    Filters.eq("userId", userId),
                    Filters.elemMatch("reviewStages", 
                        Filters.and(
                            Filters.eq("completed", false),
                            Filters.lte("nextReviewDate", now)
                        )
                    )
                )
        ).into(new ArrayList<>()).stream()
        .map(this::documentToWord)
        .collect(Collectors.toList());
    }

    public void updateReviewStage(ObjectId wordId, int stage, boolean isCorrect) {
        Document wordDoc = wordsCollection.find(Filters.eq("_id", wordId)).first();
        if (wordDoc == null) return;

        List<Document> stages = wordDoc.getList("reviewStages", Document.class);
        Document stageDoc = stages.get(stage - 1);
        
        int correctAnswers = stageDoc.getInteger("correctAnswers");
        int totalAttempts = stageDoc.getInteger("totalAttempts");
        
        if (isCorrect) {
            correctAnswers++;
            if (correctAnswers >= 3) { // Need 3 correct answers to complete a stage
                stageDoc.put("completed", true);
                if (stage < 6) {
                    stages.get(stage).put("nextReviewDate", calculateNextReviewDate(stage + 1));
                }
            }
        }
        
        totalAttempts++;
        stageDoc.put("correctAnswers", correctAnswers);
        stageDoc.put("totalAttempts", totalAttempts);
        
        wordsCollection.updateOne(
            Filters.eq("_id", wordId),
            new Document("$set", new Document("reviewStages", stages)
                    .append("lastReviewed", LocalDateTime.now()))
        );
    }

    private LocalDateTime calculateNextReviewDate(int stage) {
        LocalDateTime now = LocalDateTime.now();
        switch (stage) {
            case 1: return now.plusDays(1);    // 1 day
            case 2: return now.plusHours(1);   // 1 hour
            case 3: return now.plusMonths(1);  // 1 month
            case 4: return now.plusMonths(3);  // 3 months
            case 5: return now.plusMonths(6);  // 6 months
            case 6: return now.plusYears(1);   // 1 year
            default: return now;
        }
    }

    private Word documentToWord(Document doc) {
        Word word = new Word();
        word.setId(doc.getObjectId("_id"));
        word.setEnglish(doc.getString("english"));
        word.setTurkish(doc.getString("turkish"));
        word.setExampleSentence(doc.getString("exampleSentence"));
        word.setImageUrl(doc.getString("imageUrl"));
        word.setAudioUrl(doc.getString("audioUrl"));
        word.setUserId(doc.getObjectId("userId"));
        word.setCreatedAt(doc.getDate("createdAt").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        word.setLastReviewed(doc.getDate("lastReviewed").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        word.setDifficulty(doc.getInteger("difficulty", 1));
        
        List<Document> stages = doc.getList("reviewStages", Document.class);
        List<Word.ReviewStage> reviewStages = stages.stream()
            .map(stageDoc -> {
                Word.ReviewStage stage = new Word.ReviewStage(stageDoc.getInteger("stage"));
                stage.setCompleted(stageDoc.getBoolean("completed"));
                stage.setNextReviewDate(stageDoc.getDate("nextReviewDate").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
                stage.setCorrectAnswers(stageDoc.getInteger("correctAnswers"));
                stage.setTotalAttempts(stageDoc.getInteger("totalAttempts"));
                return stage;
            })
            .collect(Collectors.toList());
        word.setReviewStages(reviewStages);
        
        return word;
    }
} 