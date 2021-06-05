package indexer;

import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import opennlp.tools.stemmer.PorterStemmer;

import java.util.ArrayList;
import java.util.List;



import java.util.*;



public class Indexer implements Runnable {

    private static final double relavencyThreshold = 0.5;
    private Thread thread;
    private List<String> splitedList=new ArrayList<>();
    private List<String> relevantList=new ArrayList<>();
    private List<Document> docs=new ArrayList<Document>();
    private List<String> urls=new ArrayList<String>();


    PorterStemmer porterStemmer = new PorterStemmer();
    DatabaseManager db;


    public Indexer (List<Document> documents, DatabaseManager dbase,List<String> urlsList)
    {
        docs = documents;

        thread =new Thread(this);
        thread.start();
        db=dbase;
        urls=urlsList;

//        splitedList.add("java");
//        splitedList.add("java");
//        splitedList.add("java");
//        splitedList.add("Java");
//        splitedList.add("Java(");
//        splitedList.add("(Threading");
//        splitedList.add("Os(");
//        splitedList.add("Os");
//        splitedList.add("Lab");
//        splitedList.add("Map");
//        splitedList.add("Normal");
//        splitedList.add("Threading");
//        splitedList.add("Java");
//        splitedList.add("java");
//        splitedList.add("here");
//        splitedList.add("don’t");
//        splitedList.add("let’s");

    }

//    public static void main(String[] args)
//    {
//        Indexer indexer = new Indexer();
//        Document doc=indexer.requestDocument("https://en.wikipedia.org/wiki/Prison#:~:text=A%20prison%2C%20also%20known%20as,remand%20center%2C%20is%20a%20facility");  //
//        indexer.parseDocument(doc);
//        indexer.docProcessing();
//        indexer.relevant();
//
//        //https://www.geeksforgeeks.org/map-interface-java-examples/
//        //https://www.netflix.com/eg-en/browse/genre/7424
//        //
//
//
//        for (int i = 0; i < indexer.splitedList.size(); i++) {
//
//            System.out.println("\n" +indexer.splitedList.get(i) + "\n");
//
//        }
//
//        for (int i = 0; i < indexer.relevantList.size(); i++) {
//
//            System.out.println("\n" +indexer.relevantList.get(i) + "\n");
//
//        }
//        System.out.println("\n" + indexer.relevantList.size() + "\n");
//
////        for (int i = 0; i < indexer.relevantList.size(); i++) {
////
////           indexer.db.insertDocument(indexer.splitedList.get(i), "url", Float.parseFloat(indexer.relevantList.get(i)));
////        }
////        System.out.println("Done");
//
//    }

    @Override
    public void run()
    {
        for (int i=0;i<docs.size();i++)
        {
            parseDocument(docs.get(i));
            docProcessing();
            relevant();
            System.out.println(urls.size());
            System.out.println(docs.size());
            System.out.println("................");
            System.out.println(splitedList.size());
            System.out.println(relevantList.size());


            System.out.println(urls.get(i));

            for (int j = 0; j < relevantList.size(); j++) {

           db.insertDocument(splitedList.get(j),urls.get(i), Float.parseFloat(relevantList.get(j)));
         }
            System.out.println("Done");

            splitedList.clear();
            relevantList.clear();
        }


    }

    String wordProcessing (String s)
    {
        s = s.replaceAll("[0123456789(){}_+.!@#$%^&*?>–<-]","");
        String temp = porterStemmer.stem(s.toLowerCase());
        return temp;
    }

    void docProcessing()
    {
        boolean removed=false;


        String[] stopwords = {"comment","equals","stop","link","","p","h","i","j","k","l","m","n","o","q","r","s","t","u","w","x","b","c","d","e","f","g","a","()","=","contact","here","share","1","2","3","4", "as", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero"};

        for(int i=0;i<splitedList.size();i++)
        {
            splitedList.get(i).trim().isEmpty();

            if (splitedList.get(i).endsWith("()"))
            {
                StringBuilder sb = new StringBuilder(splitedList.get(i));

                sb.deleteCharAt(splitedList.get(i).length() - 1);

                splitedList.set(i,sb.toString());

                StringBuilder sb2 = new StringBuilder(splitedList.get(i));

                sb.deleteCharAt(splitedList.get(i).length() - 1);

                splitedList.set(i,sb2.toString());
            }

            if (splitedList.get(i).endsWith(".")||splitedList.get(i).endsWith("!")||splitedList.get(i).endsWith(",")||splitedList.get(i).endsWith(":")||splitedList.get(i).endsWith(";")||splitedList.get(i).endsWith("@")||splitedList.get(i).endsWith("#")||splitedList.get(i).endsWith("$")||splitedList.get(i).endsWith("%")||splitedList.get(i).endsWith("^")||splitedList.get(i).endsWith("&")||splitedList.get(i).endsWith("*")||splitedList.get(i).endsWith(")")||splitedList.get(i).endsWith("(")||splitedList.get(i).endsWith("}")||splitedList.get(i).endsWith("]")||splitedList.get(i).endsWith(">")||splitedList.get(i).endsWith("?")||splitedList.get(i).endsWith("'")||splitedList.get(i).endsWith("\""))
            {
                StringBuilder sb = new StringBuilder(splitedList.get(i));

                sb.deleteCharAt(splitedList.get(i).length() - 1);

                splitedList.set(i,sb.toString());
            }
            if(splitedList.get(i).startsWith("(")||splitedList.get(i).startsWith(")")||splitedList.get(i).startsWith("]")||splitedList.get(i).startsWith("}")||splitedList.get(i).startsWith("[")||splitedList.get(i).startsWith("{")||splitedList.get(i).startsWith("'")||splitedList.get(i).startsWith("\""))
            {
                StringBuilder sb = new StringBuilder(splitedList.get(i));

                sb.deleteCharAt(0);

                splitedList.set(i,sb.toString());
            }
        }


        for (int i = 0; i < splitedList.size(); i++) {

            splitedList.set(i,wordProcessing(splitedList.get(i)));

        }

        for (int i = 0; i < stopwords.length; i++) {


            stopwords[i] =stopwords[i].toLowerCase();


        }

        int j=0;

        while(j<splitedList.size())
        {
//                System.out.println("\n j= " +j + "\n");

            removed=false;

//           System.out.println("\n size = " +splitedList.size() + "\n");

            for (int i = 0; i < stopwords.length; i++) {

                if(stopwords[i].equals(splitedList.get(j))||splitedList.get(j).contains("’"))
                {
                    System.out.println("\n going to remove \n");
                    splitedList.remove(j);
                    System.out.println("\n item removed \n");
                    removed=true;
                    break;
                }

            }

            if (removed==false)
            {
                j++;
            }

//                System.out.println("\n size = " +splitedList.size() + "\n");

        }




    }



    void parseDocument(Document doc)
    {


        List<String> elementList=new ArrayList<>();
        String[] temp;


        int j=1;



        if (doc!=null) {



            System.out.println("document is not empty \n");

            Element body = doc.body();
            Elements bodyEelements=body.getAllElements();


            while (doc.select("h" + j).size()!=0) {

                Elements headers = bodyEelements.select("h" + j);



                elementList.addAll(headers.eachText());

                j++;

            }
            Elements paragraphs=bodyEelements.select("p");
            elementList.addAll(paragraphs.eachText());

            Elements spans=bodyEelements.select("span");
            elementList.addAll(spans.eachText());

            Elements orderedList=bodyEelements.select("li");
            elementList.addAll(orderedList.eachText());


            Elements definedList=bodyEelements.select("dt");
            elementList.addAll(spans.eachText());


            System.out.println(definedList.size() + "\n");

            for (int i = 0; i < elementList.size(); i++) {

                temp=elementList.get(i).split("[-\\s]");

                for (int k=0;k<temp.length;k++)
                {
                    splitedList.add(temp[k]);
                }


            }

        }
        else
        {
            System.out.println("document = null \n");
        }

    }

    void relevant()
    {


        String temp;

        float normal=splitedList.size();

        float frequency=1;

        int j=0;

        int k=0;

        while (j<splitedList.size()) {

            temp=splitedList.get(j);

            int i=j+1;


            while (i < splitedList.size()) {

                if (splitedList.get(i).equals(temp))
                {
//                    System.out.println("item "+splitedList.get(i) +" at i="+i+"\n");
                    frequency++;

                    if (j==splitedList.size())
                    {
                        break;
                    }
//                    System.out.println("item "+splitedList.get(i) +" at i="+i+" is removed"+"\n");
                    splitedList.remove(i);


                }
                else
                {
                    i++;
                }

            }


            System.out.println("\n size = " +splitedList.size() + "\n");
            relevantList.add(String.valueOf((frequency/normal)*100));
            System.out.println("\n  the word "+temp+" was repeated "+String.valueOf((frequency/normal)*100)+"\n");
            frequency = 1;
            j++;


        }

        while (k<relevantList.size()) {

            if (Float.parseFloat(relevantList.get(k))<=relavencyThreshold)
            {
                relevantList.remove(k);
                splitedList.remove(k);

            }
            else
            {
                k++;
            }


        }


    }

    public Document requestDocument(String url)
    {
        try {

            Connection con = Jsoup.connect(url);
            Document doc = con.get();

            if (con.response().statusCode() == 200) {

                System.out.println( con.response().statusCode()+"\n");

                return doc;
            }
            else {
                System.out.println( con.response().statusCode()+"\n");


                return null;
            }

        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}

