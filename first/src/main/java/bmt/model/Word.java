package bmt.model;

import org.bson.types.ObjectId;
import java.time.LocalDateTime;
import java.util.List;

public class Word {
    private ObjectId id;
    private String english;
    private String turkish;
    private String exampleSentence;
    private String imageUrl;
    private String audioUrl;
    private ObjectId userId;
    private List<ReviewStage> reviewStages;
    private LocalDateTime createdAt;
    private LocalDateTime lastReviewed;
    private int difficulty;

    public static class ReviewStage {
        private int stage; // 1-6
        private boolean completed;
        private LocalDateTime nextReviewDate;
        private int correctAnswers;
        private int totalAttempts;

        public ReviewStage(int stage) {
            this.stage = stage;
            this.completed = false;
            this.correctAnswers = 0;
            this.totalAttempts = 0;
        }

        // Getters and Setters
        public int getStage() { return stage; }
        public void setStage(int stage) { this.stage = stage; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
        public LocalDateTime getNextReviewDate() { return nextReviewDate; }
        public void setNextReviewDate(LocalDateTime nextReviewDate) { this.nextReviewDate = nextReviewDate; }
        public int getCorrectAnswers() { return correctAnswers; }
        public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }
        public int getTotalAttempts() { return totalAttempts; }
        public void setTotalAttempts(int totalAttempts) { this.totalAttempts = totalAttempts; }
    }

    public Word() {
        this.createdAt = LocalDateTime.now();
        this.lastReviewed = LocalDateTime.now();
    }

    // Getters and Setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }
    public String getEnglish() { return english; }
    public void setEnglish(String english) { this.english = english; }
    public String getTurkish() { return turkish; }
    public void setTurkish(String turkish) { this.turkish = turkish; }
    public String getExampleSentence() { return exampleSentence; }
    public void setExampleSentence(String exampleSentence) { this.exampleSentence = exampleSentence; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public ObjectId getUserId() { return userId; }
    public void setUserId(ObjectId userId) { this.userId = userId; }
    public List<ReviewStage> getReviewStages() { return reviewStages; }
    public void setReviewStages(List<ReviewStage> reviewStages) { this.reviewStages = reviewStages; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getLastReviewed() { return lastReviewed; }
    public void setLastReviewed(LocalDateTime lastReviewed) { this.lastReviewed = lastReviewed; }
    public int getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
} 