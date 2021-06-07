package indexer;

import com.mongodb.*;
//import java.net.UnknownHostException;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
//import org.bson.Document;
import com.mongodb.MongoClient;
//import org.json.simple.JSONArray;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

//import java.util.*;
//import javax.print.Doc;
import static com.mongodb.client.model.Filters.*;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoClientSettings;

import javax.swing.text.Document;

public class DatabaseManager {

    MongoClientURI uri;
    MongoClient mongoClient;
    DB database;
    DBCollection collection;
    DBCollection urlCollection;
    DBCollection compactCollection;

   public DatabaseManager() {
        uri = new MongoClientURI("mongodb+srv://admin:Khaled1999@cluster0.5toq8.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");

        mongoClient = new MongoClient(uri);
        database = mongoClient.getDB("myDatabase");
        collection = database.getCollection("indexer");
        urlCollection = database.getCollection("urls");
        compactCollection = database.getCollection("compact");

    }

    public static void main( String args[] ) {


        DatabaseManager db = new DatabaseManager();
//        db.insertURL("Ahmed");
//        db.insertURL("Ahmed");
//        db.insertComopact("Roaaaa");
//        db.insertComopact("Roaaaa");
//        db.insertComopact("Lailaa");
//        db.insertComopact("Khaaleeeeed");
//
//        List<String>text=db.retrieveCompact();
//
//        for (int i=0;i<text.size();i++)
//        {
//            System.out.println(text.get(i));
//        }

//        System.out.println(db.retrieveURLs().size());




//        //db.insertDocument("ahmed", "facebook.com", 50);
//        db.queryWord("ahmed");
    }

    public void insertDocument(String word, String url, float tf, String title, String description) {

        int totalDocumentForWord;
        double idf = (double) urlCollection.count();

        DBObject query = new BasicDBObject("word", word);
        DBCursor cursor = collection.find(query);

        if(cursor.one() == null) //word will be inserted for the first time
        {
            idf = Math.log10(idf);
            JSONObject json = createJSON(url, tf, title, description);
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
                JSONObject json = createJSON(url, tf, title, description);
                list.add(json);
            }
            else
            {
                BasicDBObject obj = (BasicDBObject) result.get("data");
                JSONObject json = createJSON(url, tf, title, description);
                list = new BasicDBList();

                list.add(obj);
                list.add(json);
            }

            collection.update(new BasicDBObject("word", word),
                    new BasicDBObject("$set", new BasicDBObject().append("data", list).append("idf", idf).append("numberOfDocuments", totalDocumentForWord)));

        }


    }
    private JSONObject createJSON(String url, float tf, String title, String description) {
        JSONObject json = new JSONObject();
        json.put("tf", tf);
        json.put("url", url);
        json.put("title", title);
        json.put("description", description);

        return json;
    }

    public void insertComopact(String Str)
    {
        DBObject query = new BasicDBObject("compact", Str);
        DBCursor cursor = compactCollection.find(query);

        if (cursor.one()==null)
        {
            DBObject dbObject = new BasicDBObject().append("compact", Str);
            compactCollection.insert(dbObject);
        }



    }

    public List<String> retrieveCompact()
    {
        DBObject query = new BasicDBObject();
        DBCursor cursor = compactCollection.find(query);

        List<String> urls=new ArrayList<String>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            urls.add((String) obj.get("compact"));
        }

        return urls;
    }

    public List<String> retrieveAllUrls()
    {
        DBObject query = new BasicDBObject();
        DBCursor cursor = urlCollection.find(query);

        List<String> urls=new ArrayList<String>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            urls.add((String) obj.get("url"));
        }

        return urls;
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




    public void insertURL(String url) {

        DBObject query = new BasicDBObject("url", url);
        DBCursor cursor = urlCollection.find(query);

            if (cursor.one()==null)
            {
                DBObject dbObject = new BasicDBObject().append("url", url).append("visited", false);
                urlCollection.insert(dbObject);
            }




    }

    public List<String> retrieveURLs(boolean flag) {
        DBObject query = new BasicDBObject("visited", flag);
        DBCursor cursor = urlCollection.find(query);

        List<String> urls=new ArrayList<String>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            urls.add((String) obj.get("url"));
        }

        return urls;
    }
    public void makeVisited(String url) {
        urlCollection.update(new BasicDBObject("url", url),
                new BasicDBObject("$set", new BasicDBObject().append("visited", true)));
    }
}