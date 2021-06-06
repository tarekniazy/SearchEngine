package Crawler;

import indexer.DatabaseManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import indexer.Indexer;

import java.util.concurrent.atomic.AtomicInteger;

//public class Crawler.Crawler implements Runnable{
//    private List<String> list;
//    private static final int MAX_DEPTH = 10;
//    FileReader Read;
//    FileWriter Write;
//    BufferedReader bufferedReader;
//    public Crawler.Crawler() throws IOException {
//        list=new ArrayList<String>();
//        File myObj = new File("Visited.txt");
//        Read=new FileReader("src/main/Intial.txt");
//        Write=new FileWriter("Visited.txt");
//        bufferedReader = new BufferedReader(Read);
//        String line;
//        while ((line = bufferedReader.readLine()) != null) {
//            list.add(line);
//        }
//        Read.close();
//    }
//    public boolean Disallowed(String link, String URL) {
//        String lin;
//        if(link.substring(link.length() - 1).equals("/")){
//            lin=link+"robots.txt";
//        }else{
//            lin=link+"/robots.txt";
//
//        }
//        Document document;
//        try{
//            document = Jsoup.connect(lin).get();
//        }catch (IOException e){
////            System.out.println("truuuuu");
//            return true;
//        }
//        lin=(lin+"/robots.txt").replace("//","/");
//        URL=URL.replace("//","/");
//        Elements PRE = document.select("body");
//        String pre=PRE.toString();
//        String[] arrOfStr = pre.split("User-agent:");
//        //Arrays.sort(arrOfStr, Comparator.comparingInt(String::length));
//        String s;
//        for (int i=0;i<arrOfStr.length;i++){
//            if(arrOfStr[i].contains(" *")){
//                s=arrOfStr[i];
//                arrOfStr=s.split(" Disallow:");
//                break;
//            }
//        }
//        if(arrOfStr.length==0) {
//            return true;
//        }
////        }else if((arrOfStr[1].split(" ", 3)[1]).equals("/")){
////            return false;
////        }
//        if(arrOfStr[arrOfStr.length-1].contains("\n</body>")){
//            arrOfStr[arrOfStr.length-1]=arrOfStr[arrOfStr.length-1].replace("\n</body>","");
//        }
//        for(int i=1;i<arrOfStr.length-1;i++) {
//            if(arrOfStr[i].contains("*")){
//                String S1=(link+arrOfStr[i]).replace("/ /","/");
//                S1=S1.replace("//","/");
//                S1=S1.replace("*",".*");
//                System.out.println(S1);
//                if(Pattern.matches(S1,URL)){
//                    return false;
//                }
//
//            }
//            else{
//                String S1=(link+arrOfStr[i].split(" ", 3)[1]).replace("/ /","/");
//                S1=S1.replace("//","/");
//                System.out.println(S1);
//                if ((URL.equals(S1))){
//                    return false;
//                }
//
//            }
//        }
//
//
//
//        return true;
//    }
//    public void Crawling() throws IOException {
//        int index=0;
//        while(list.size()<MAX_DEPTH){
//            Write.write(list.get(index)+"\n");
//            try {
//
//                Document document = Jsoup.connect(list.get(index)).get();
//                Elements linksOnPage = document.select("a[href]");
//
//                for (Element page : linksOnPage) {
//                    if(list.size()>=MAX_DEPTH){
//                        break;
//                    }
//                    //&&(Disallowed(list.get(index),page.attr("abs:href")))
//                    else if((!list.contains(page.attr("abs:href"))) && (Disallowed(list.get(index),page.attr("abs:href")))){
//                        list.add(page.attr("abs:href"));
//
//                    }
//                }
//            } catch (IOException e) {
//                System.err.println("For '" + list.get(index) + "': " + e.getMessage());
//            }
//
//
//            index++;
//        }
//        Write.close();
//        FileWriter Writer=new FileWriter("Crawled.txt");
//        index=0;
//        while(index<list.size()){
//            Writer.write(list.get(index)+"\n");
//            index++;
//        }
//        Writer.close();
//    }
//    @Override
//    public void run(){
//        try {
//            Crawling();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//    public static void main(String[] args) throws IOException {
//        FileReader Read;
//        BufferedReader bufferedReader;
//        Read=new FileReader("src/main/Intial.txt");
//        bufferedReader = new BufferedReader(Read);
//        String line;
//        List<String> list=new ArrayList<String>();
//        while ((line = bufferedReader.readLine()) != null) {
//            list.add(line);
//        }
//        Read.close();
//        //new Crawler.Crawler().Crawling();
////        String s="https://crawler-test.com/";
////        String c=s.substring(s.length() - 1);
////        System.out.println(s.substring(s.length() - 1));
//
//    }
//}
public class Crawler implements Runnable{
    public static List<String> list,Compact,visited;
    public static List<Document> documents;
    int number_of_threads;
    private static final int MAX_DEPTH = 20;
    private static  int Threshold;
    FileWriter Write;
    private Thread thread;
    DatabaseManager db;
    AtomicInteger indexLists=new AtomicInteger(0);
    AtomicInteger indexVisited=new AtomicInteger(0);
    int base;


//
    public Crawler(int base,AtomicInteger visitedIndex,AtomicInteger listIndex,DatabaseManager dbase,List<String> URL_list, List<String> comp, List<String> visit, int numthreads,List<Document> doc) throws IOException {

        thread =new Thread(this);
        db=dbase;
        this.base=base;
        System.out.println("The Depth is ...."+ (MAX_DEPTH-this.base));

        Threshold=(MAX_DEPTH-base)/2;

        this.list=URL_list;
        this.indexLists=listIndex;
        this.indexVisited=visitedIndex;


        Compact=comp;
        visited=visit;
        number_of_threads=numthreads;
        File myObj = new File("Visited.txt");
        Write=new FileWriter("Visited.txt");
        documents=doc;


    }

    public void saveData ()
    {

        System.out.println("Start Saving....");
        for (int i=indexLists.get();i<list.size();i++)
        {
                    db.insertURL(list.get(i));
            System.out.println(list.get(i));

        }

        indexLists.set(list.size());

        for (int j=indexVisited.get();j<visited.size();j++)
        {
            db.makeVisited(visited.get(j));

        }

        for (int j=0;j<Compact.size();j++)
        {
            db.insertComopact(Compact.get(j));

        }

        indexVisited.set(visited.size());

        System.out.println(indexLists);

    }

    public boolean Disallowed(String link, String URL) {
        String lin;
        if(link.substring(link.length() - 1).equals("/")){
            lin=link+"robots.txt";
        }else{
            lin=link+"/robots.txt";

        }
        Document document;
        try{
            document = Jsoup.connect(lin).get();
        }catch (IOException e){
            return true;
        }
        lin=(lin+"/robots.txt").replace("//","/");
        URL=URL.replace("//","/");
        Elements PRE = document.select("body");
        String pre=PRE.toString();
        String[] arrOfStr = pre.split("User-agent:");
        String s;
        for (int i=0;i<arrOfStr.length;i++){
            if(arrOfStr[i].contains(" *")){
                s=arrOfStr[i];
                arrOfStr=s.split(" Disallow:");
                break;
            }
        }
        if(arrOfStr.length==0) {
            return true;
        }

        if(arrOfStr[arrOfStr.length-1].contains("\n</body>")){
            arrOfStr[arrOfStr.length-1]=arrOfStr[arrOfStr.length-1].replace("\n</body>","");
        }
        for(int i=1;i<arrOfStr.length-1;i++) {
            if(arrOfStr[i].contains("*")){
                String S1=(link+arrOfStr[i]).replace("/ /","/");
                S1=S1.replace("//","/");
                S1=S1.replace("",".");
//                System.out.println(S1);
                if(Pattern.matches(S1,URL)){
                    return false;
                }

            }
            else{
                String S1=(link+arrOfStr[i].split(" ", 3)[1]).replace("/ /","/");
                S1=S1.replace("//","/");
//                System.out.println(S1);
                if ((URL.equals(S1))){
                    return false;
                }

            }
        }



        return true;
    }

    public void Crawling() throws IOException {
        int counter=0;
        int index=number_of_threads*counter+Integer.parseInt(Thread.currentThread().getName());
        System.out.print("Crawling");
        boolean flag = true;
        Document document= Jsoup.connect(list.get(0)).get();
        while(list.size()<(MAX_DEPTH-base)){



            if(index<list.size() && index>-1)
            {
                try {

                    document = Jsoup.connect(list.get(index)).get();
                    Elements linksOnPage = document.select("a[href]");


                    for (Element page : linksOnPage) {

                        synchronized(list) {

                                //System.out.println("index before"+index);
                                String href = page.attr("abs:href");
                                boolean str_bool = compact_string(href);
                                //System.out.println("index after"+index);
                                if (list.size() >= (MAX_DEPTH-base)) {
                                    flag =false;
                                    break;
                                }
                                //&&(Disallowed(list.get(index),page.attr("abs:href")))
                                else if (str_bool&&(!list.contains(page.attr("abs:href"))) && (Disallowed(list.get(index), page.attr("abs:href")))) {
                                    System.out.println(Thread.currentThread().getName());



                                    list.add(page.attr("abs:href"));
                                    if (list.size()%Threshold==0)
                                    {
                                        synchronized (db)
                                        {
                                            saveData();
                                        }
                                    }


                            }

                        }
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.err.println("For '" + list.get(index) + "': " + e.getMessage());
                }
                	Write.write(list.get(index)+"\n");

                if (flag) {
                    documents.add(document);
                    visited.add(list.get(index));
                }

            }

//            }


            counter++;
            index=number_of_threads*counter+Integer.parseInt(Thread.currentThread().getName());
        }
        Write.close();

    }


    public boolean compact_string(String URL) throws IOException {
        String comp="";
        Document document = Jsoup.connect(URL).get();
        Elements para = document.select("p");
        for (Element page : para) {
            if(page.text().length()>3) {
                String temp=page.text().substring(0,3);
                comp+=temp;
            }
        }
//        return true;
        if(!Compact.contains(comp))
        	{
        		Compact.add(comp);
        		return true;
        	}
        else
        	{
        		return false;
        	}
//        Compact=new FileWriter("Compact.txt");
//        Compact.write(comp +"\n");
//        Compact.close();


    }
    @Override
    public void run() {
        System.out.println("thread"+Thread.currentThread().getName());
        try {
            Crawling();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {


        DatabaseManager db=new indexer.DatabaseManager();

        FileReader Read;

        BufferedReader bufferedReader;
        Read=new FileReader("src/main/Intial.txt");
        bufferedReader = new BufferedReader(Read);
        String line;
        List<String> list_c=new ArrayList<String>();
        List<String> comp_str=new ArrayList<String>();
        List<String> list_visit=new ArrayList<String>();
        List<Document> documents=new ArrayList<>();
        AtomicInteger visitedIndex = new AtomicInteger(0);
        AtomicInteger listIndex = new AtomicInteger(0);
        int base=0;


        if (db.retrieveAllUrls().size()==0) {

            System.out.println("Enterrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
            while ((line = bufferedReader.readLine()) != null) {
                list_c.add(line);
            }



            if (db.retrieveCompact() != null) {
                comp_str = db.retrieveCompact();
            }


            for (int i = 0; i < list_c.size(); i++) {
                db.insertURL(list_c.get(i));

                String comp = "";
                Document document = Jsoup.connect(list_c.get(i)).get();
                Elements para = document.select("p");
                for (Element page : para) {
                    if (page.text().length() > 3) {
                        String temp = page.text().substring(0, 3);
                        comp += temp;
                    }
                }

                System.out.println("Compact ..............." + comp);
                comp_str.add(comp);
                db.insertComopact(comp);

            }
            Read.close();
        }
        else
        {

            list_c=db.retrieveURLs(false);
            base=db.retrieveAllUrls().size()-list_c.size();
            System.out.println("the base is ......."+base);
            comp_str=db.retrieveCompact();
            list_visit=db.retrieveURLs(true);
            System.out.println("visited ......."+list_visit.size());

        }





        int number_of_threads=3;
        int list_size=list_c.size()/number_of_threads;
        System.out.print("list size"+list_size);
        List<Thread> Cr= new ArrayList<Thread>();
        for (int i=0;i<number_of_threads;i++) {

            Thread th=new Thread(new Crawler(base,visitedIndex,listIndex,db,list_c,comp_str,list_visit,number_of_threads,documents));
            th.setName(Integer.toString(i));
            Cr.add(th);

//            C.compact_string("https://jsoup.org/cookbook/extracting-data/example-list-links");

            th.start();

        }

        for (int i=0;i<number_of_threads;i++) {
            try {
                Cr.get(i).join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        FileWriter Writer=new FileWriter("Crawled.txt");
        int index=0;
        while(index<list_c.size()){
            Writer.write(list_c.get(index)+"\n");/////to be sent to indexer
            index++;
        }
        Writer.close();

        System.out.println("hhhhhhhhhhhhhhhhh  "+String.valueOf(list_visit.size()));

       for (int i=0;i<list_visit.size();i++)
       {
           System.out.println(list_visit.get(i));
       }

        Thread th=new Thread(new Indexer(documents,db,list_visit));
        th.join();
        System.out.println("Doneeeeeeeeeeeeee");
    }




}