//package com.example.searchengine;
//
//import com.mongodb.*;
////import java.net.UnknownHostException;
////import com.mongodb.client.MongoCollection;
////import com.mongodb.client.MongoDatabase;
////import com.mongodb.util.JSON;
////import org.bson.Document;
//import com.mongodb.MongoClient;
////import org.json.simple.JSONArray;
////import org.json.simple.JSONObject;
//import java.lang.Math;
////import java.util.*;
////import javax.print.Doc;
//
//public class DatabaseManagerInterface {
//
//    MongoClientURI uri;
//    MongoClient mongoClient;
//    DB database;
//    DBCollection collection;
//    DBCollection urlCollection;
//
//    DatabaseManagerInterface() {
//        uri = new MongoClientURI("mongodb://admin:Khaled1999@cluster0.5toq8.mongodb.net/myDatabase?retryWrites=true&w=majority");
//
//        mongoClient = new MongoClient(uri);
//        database = mongoClient.getDB("myDatabase");
//        collection = database.getCollection("indexer");
//        urlCollection = database.getCollection("urls");
//    }
//
//    public static void main( String args[] ) {
//
//
//        DatabaseManagerInterface db = new DatabaseManagerInterface();
//
//        //db.insertDocument("ahmed", "facebook.com", 50);
//        db.queryWord("ahmed");
//    }
//
//
//    public BasicDBList queryWord(String word) {
//
//        DBObject query = new BasicDBObject("word", word);
//        DBCursor cursor = collection.find(query);
//        BasicDBList list = new BasicDBList();
//
//        if(cursor.one() == null) { //word does not exist
//            System.out.println("Word does not exist");
//        }
//        else {
//            DBObject result = cursor.next();
//            if(result.get("data").getClass().getName().equals("com.mongodb.BasicDBList")) // list of urls
//            {
//                list = (BasicDBList) result.get("data");
//            }
//            else //one url
//            {
//                BasicDBObject obj = (BasicDBObject) result.get("data");
//                list = new BasicDBList();
//                list.add(obj);
//            }
//        }
//
//        return list;
//
//    }
//}
