import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

public class Crawler {
    private List<String> list;
    private static final int MAX_DEPTH = 10;
    FileReader Read;
    FileWriter Write;
    BufferedReader bufferedReader;
    public Crawler() throws IOException {
        list=new ArrayList<String>();
        File myObj = new File("Visited.txt");
        Read=new FileReader("src/main/Intial.txt");
        Write=new FileWriter("Visited.txt");
        bufferedReader = new BufferedReader(Read);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            list.add(line);
        }
        Read.close();
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
        Elements PRE = document.select("body");
        String pre=PRE.toString();
        String[] arrOfStr = pre.split("Disallow:");
        Arrays.sort(arrOfStr, Comparator.comparingInt(String::length));
        if(arrOfStr.length==0){
            return true;
        }else if((arrOfStr[0].split(" ", 3)[1]).equals("/")){
            return false;
        }
        for(int i=0;i<arrOfStr.length-2;i++) {
            if (URL.contains(arrOfStr[i].split(" ", 3)[1]) || URL.contains(arrOfStr[i].split("\\*", 3)[0]) || URL.contains(arrOfStr[i].split("\\*", 3)[1])) {
                return false;
            }
        }



        return true;
    }
    public void Crawling() throws IOException {
        int index=0;
        while(list.size()<MAX_DEPTH){
            Write.write(list.get(index)+"\n");
            try {

                Document document = Jsoup.connect(list.get(index)).get();
                Elements linksOnPage = document.select("a[href]");

                for (Element page : linksOnPage) {
                    if(list.size()>=MAX_DEPTH){
                        break;
                    }
                    //&&(Disallowed(list.get(index),page.attr("abs:href")))
                    else if((!list.contains(page.attr("abs:href")))){
                        list.add(page.attr("abs:href"));

                    }
                }
            } catch (IOException e) {
                System.err.println("For '" + list.get(index) + "': " + e.getMessage());
            }


            index++;
        }
        Write.close();
        FileWriter Writer=new FileWriter("Crawled.txt");
        index=0;
        while(index<list.size()){
            Writer.write(list.get(index)+"\n");
            index++;
        }
        Writer.close();
    }
    public static void main(String[] args) throws IOException {
        new Crawler().Crawling();
//        String s="https://crawler-test.com/";
//        String c=s.substring(s.length() - 1);
//        System.out.println(s.substring(s.length() - 1));

    }
}
