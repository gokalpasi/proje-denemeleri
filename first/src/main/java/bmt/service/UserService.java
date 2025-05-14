package bmt.service;

import bmt.database.MongoDBConnection;
import bmt.model.User;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

public class UserService {
    private final MongoCollection<Document> usersCollection;

    public UserService() {
        this.usersCollection = MongoDBConnection.getDatabase().getCollection("users");
    }

    public User register(String username, String password) {
        // Check if username already exists
        if (usersCollection.find(Filters.eq("username", username)).first() != null) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User(username, password);
        Document userDoc = new Document()
                .append("username", user.getUsername())
                .append("password", user.getPassword())
                .append("dailyWordLimit", user.getDailyWordLimit())
                .append("isActive", user.isActive());

        usersCollection.insertOne(userDoc);
        user.setId(userDoc.getObjectId("_id"));
        return user;
    }

    public User login(String username, String password) {
        Document userDoc = usersCollection.find(
                Filters.and(
                    Filters.eq("username", username),
                    Filters.eq("password", password)
                )
        ).first();

        if (userDoc == null) {
            throw new RuntimeException("Invalid username or password");
        }

        User user = new User();
        user.setId(userDoc.getObjectId("_id"));
        user.setUsername(userDoc.getString("username"));
        user.setPassword(userDoc.getString("password"));
        user.setDailyWordLimit(userDoc.getInteger("dailyWordLimit"));
        user.setActive(userDoc.getBoolean("isActive"));

        return user;
    }

    public void updateDailyWordLimit(ObjectId userId, int newLimit) {
        usersCollection.updateOne(
            Filters.eq("_id", userId),
            new Document("$set", new Document("dailyWordLimit", newLimit))
        );
    }

    public void resetPassword(String username, String newPassword) {
        Document userDoc = usersCollection.find(Filters.eq("username", username)).first();
        if (userDoc == null) {
            throw new RuntimeException("User not found");
        }

        usersCollection.updateOne(
            Filters.eq("_id", userDoc.getObjectId("_id")),
            new Document("$set", new Document("password", newPassword))
        );
    }
} 