package app.questionary.repository;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;
public class MongoDBConnector {

    private final MongoClient mongoClient;
    private final MongoDatabase database;

    public MongoDBConnector(String host, int port, String dbName) {
        mongoClient = new MongoClient(host, port);
        database = mongoClient.getDatabase(dbName);
    }

    public MongoCollection<Document> getQuestionerCollection() {
        return database.getCollection("questioners");
    }

    public MongoCollection<Document> getBotUserCollection() {
        return database.getCollection("users");
    }

    public MongoCollection<Document> getPartnersCollection() {
        return database.getCollection("partners");
    }
}
