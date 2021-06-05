import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.regex.*;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

//public class Crawler implements Runnable{
//    private List<String> list;
//    private static final int MAX_DEPTH = 10;
//    FileReader Read;
//    FileWriter Write;
//    BufferedReader bufferedReader;
//    public Crawler() throws IOException {
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
//        //new Crawler().Crawling();
////        String s="https://crawler-test.com/";
////        String c=s.substring(s.length() - 1);
////        System.out.println(s.substring(s.length() - 1));
//
//    }
//}
public class Crawler implements Runnable{
    public static List<String> list,Compact,visited;
    int number_of_threads;
    private static final int MAX_DEPTH = 20;
    FileWriter Write;

    public Crawler(List<String> URL_list, List<String> comp, List<String> visit, int numthreads) throws IOException {
        list=URL_list;
        Compact=comp;
        visited=visit;
        number_of_threads=numthreads;
        File myObj = new File("Visited.txt");
        Write=new FileWriter("Visited.txt");
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
        while(list.size()<MAX_DEPTH){


            if(index<list.size() && index>-1)
            {
                try {

                    Document document = Jsoup.connect(list.get(index)).get();
                    Elements linksOnPage = document.select("a[href]");

                    for (Element page : linksOnPage) {

                        synchronized(list) {
                            //System.out.println("index before"+index);
                            String href=page.attr("abs:href");
                            boolean str_bool = compact_string(href);
                            //System.out.println("index after"+index);
                            if(list.size()>=MAX_DEPTH){
                                break;
                            }
                            //&&(Disallowed(list.get(index),page.attr("abs:href")))
                            else if((!list.contains(page.attr("abs:href"))) && (Disallowed(list.get(index),page.attr("abs:href")))){
                                System.out.println(Thread.currentThread().getName());

                                list.add(page.attr("abs:href"));

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
//                	Write.write(list.get(index)+"\n");
                visited.add(list.get(index));
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
        return true;
//        if(!Compact.contains(comp))
//        	{
//        		Compact.add(comp);
//        		return true;
//        	}
//        else
//        	{
//        		return false;
//        	}
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


    public static void main(String[] args) throws IOException {
        FileReader Read;
        BufferedReader bufferedReader;
        Read=new FileReader("src/main/Intial.txt");
        bufferedReader = new BufferedReader(Read);
        String line;
        List<String> list_c=new ArrayList<String>();
        List<String> comp_str=new ArrayList<String>();
        List<String> list_visit=new ArrayList<String>();
        while ((line = bufferedReader.readLine()) != null) {
            list_c.add(line);
        }
        Read.close();
        int number_of_threads=3;
        int list_size=list_c.size()/number_of_threads;
        System.out.print("list size"+list_size);
        List<Thread> Cr= new ArrayList<Thread>();
        for (int i=0;i<number_of_threads;i++) {

            Thread th=new Thread(new Crawler(list_c,comp_str,list_visit,number_of_threads));
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

    }

}