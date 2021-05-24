package indexer;

import com.mongodb.*;
import java.net.UnknownHostException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.bson.Document;
import com.mongodb.MongoClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.*;

import javax.print.Doc;

public class DatabaseManager {

    MongoClientURI uri;
    MongoClient mongoClient;
    DB database;
    DBCollection collection;

    DatabaseManager() {
        uri = new MongoClientURI("mongodb+srv://admin:Khaled1999@cluster0.5toq8.mongodb.net/myDatabase?retryWrites=true&w=majority");

        mongoClient = new MongoClient(uri);
        database = mongoClient.getDB("myDatabase");
        collection = database.getCollection("indexer");
    }

    public static void main( String args[] ) {


        DatabaseManager db = new DatabaseManager();

        db.insertDocument("hello", "twitter.com", 50);
    }

    public void insertDocument(String word, String url, float nf) {

        DBObject query = new BasicDBObject("word", word);
        DBCursor cursor = collection.find(query);

        if(cursor.one() == null) //word will be inserted for the first time
        {
            JSONObject json = createJSON(url, nf);

            DBObject dbObject = new BasicDBObject().append("word", word).append("data", json);

            collection.insert(dbObject);
        }
        else { //word exists, incrementally update it
            DBObject result = cursor.next();
            BasicDBList list;
            if(result.get("data").getClass().getName().equals("com.mongodb.BasicDBList"))
            {
                list = (BasicDBList) result.get("data");
                JSONObject json = createJSON(url, nf);
                list.add(json);
            }
            else
            {
                BasicDBObject obj = (BasicDBObject) result.get("data");
                JSONObject json = createJSON(url, nf);
                list = new BasicDBList();

                list.add(obj);
                list.add(json);
            }

            collection.update(new BasicDBObject("word", word), new BasicDBObject("$set", new BasicDBObject("data", list)));
        }


    }

//    public void query(String word) {
//
//        DBObject query = new BasicDBObject("word", word);
//        DBCursor cursor = collection.find(query);
//        BasicDBList obj = (BasicDBList) cursor.one().get("data");
//
//    }

    private JSONObject createJSON(String url, float NF) {
        JSONObject json = new JSONObject();
        json.put("NF", NF);
        json.put("url", url);

        return json;
    }
}