package bmt.model;

import org.bson.types.ObjectId;

public class User {
    private ObjectId id;
    private String username;
    private String password;
    private int dailyWordLimit;
    private boolean isActive;

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.dailyWordLimit = 20; // Default value
        this.isActive = true;
    }

    // Getters and Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDailyWordLimit() {
        return dailyWordLimit;
    }

    public void setDailyWordLimit(int dailyWordLimit) {
        this.dailyWordLimit = dailyWordLimit;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
} 