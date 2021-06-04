package indexer;

import com.mongodb.*;
//import java.net.UnknownHostException;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//import com.mongodb.util.JSON;
//import org.bson.Document;
import com.mongodb.MongoClient;
//import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.lang.Math;
//import java.util.*;
//import javax.print.Doc;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoClientSettings;

public class DatabaseManager {

    MongoClientURI uri;
    MongoClient mongoClient;
    DB database;
    DBCollection collection;
    DBCollection urlCollection;

    DatabaseManager() {
        uri = new MongoClientURI("mongodb+srv://admin:Khaled1999@cluster0.5toq8.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");

        mongoClient = new MongoClient(uri);
        database = mongoClient.getDB("myDatabase");
        collection = database.getCollection("indexer");
        urlCollection = database.getCollection("urls");
    }

    public static void main( String args[] ) {


        DatabaseManager db = new DatabaseManager();

        //db.insertDocument("ahmed", "facebook.com", 50);
        db.queryWord("ahmed");
    }

    public void insertDocument(String word, String url, float tf) {

        int totalDocumentForWord;
        double idf = (double) urlCollection.count();

        DBObject query = new BasicDBObject("word", word);
        DBCursor cursor = collection.find(query);

        if(cursor.one() == null) //word will be inserted for the first time
        {
            idf = Math.log10(idf);
            JSONObject json = createJSON(url, tf);
            DBObject dbObject = new BasicDBObject().append("word", word).append("numberOfDocuments", 1).append("idf", idf).append("data", json);

            collection.insert(dbObject);
        }
        else { //word exists, incrementally update it
            DBObject result = cursor.next();
            BasicDBList list;
            totalDocumentForWord = (int) result.get("numberOfDocuments") + 1;
            idf = Math.log10(idf/totalDocumentForWord);

            if(result.get("data").getClass().getName().equals("com.mongodb.BasicDBList"))
            {
                list = (BasicDBList) result.get("data");
                JSONObject json = createJSON(url, tf);
                list.add(json);
            }
            else
            {
                BasicDBObject obj = (BasicDBObject) result.get("data");
                JSONObject json = createJSON(url, tf);
                list = new BasicDBList();

                list.add(obj);
                list.add(json);
            }

            collection.update(new BasicDBObject("word", word),
                              new BasicDBObject("$set", new BasicDBObject().append("data", list).append("idf", idf).append("numberOfDocuments", totalDocumentForWord)));


//            collection.update(new BasicDBObject("word", new BasicDBObject("$ne", word)),
//                              new BasicDBObject("set", new BasicDBObject().append("idf", 10)));
//            collection.update(new BasicDBObject("word", {"$ne": word}),
//            new BasicDBObject("set", new BasicDBObject().append("idf", Math.log10())));
        }


    }

    public void queryWord(String word) {

        DBObject query = new BasicDBObject("word", word);
        DBCursor cursor = collection.find(query);
        //BasicDBList obj = (BasicDBList) cursor.one().get("data");

        if(cursor.one() == null) { //word does not exist
            System.out.println("Word does not exist");
        }
        else {
            DBObject result = cursor.next();
            if(result.get("data").getClass().getName().equals("com.mongodb.BasicDBList")) // list of urls
            {
                BasicDBList list = (BasicDBList) result.get("data");
                for (Object o : list) {
                    BasicDBObject obj = (BasicDBObject) o;
                    System.out.println("URL IS " + obj.get("url"));
                }
            }
            else //one url
            {
                BasicDBObject obj = (BasicDBObject) result.get("data");
                System.out.println("URL IS " + obj.get("url"));
            }
        }

    }

    private JSONObject createJSON(String url, float tf) {
        JSONObject json = new JSONObject();
        json.put("tf", tf);
        json.put("url", url);

        return json;
    }
}