import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.regex.*;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

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
//            System.out.println("truuuuu");
            return true;
        }
        lin=(lin+"/robots.txt").replace("//","/");
        URL=URL.replace("//","/");
        Elements PRE = document.select("body");
        String pre=PRE.toString();
        String[] arrOfStr = pre.split("User-agent:");
        //Arrays.sort(arrOfStr, Comparator.comparingInt(String::length));
        String s;
        for (int i=0;i<arrOfStr.length;i++){
            if(arrOfStr[i].contains(" *")){
                s=arrOfStr[i];
                arrOfStr=s.split(" Disallow:");
                break;
            }
        }
        if(arrOfStr.length==0){
            return true;
        }else if((arrOfStr[1].split(" ", 3)[1]).equals("/")){
            return false;
        }
        if(arrOfStr[arrOfStr.length-1].contains("\n</body>")){
            arrOfStr[arrOfStr.length-1]=arrOfStr[arrOfStr.length-1].replace("\n</body>","");
        }
        for(int i=1;i<arrOfStr.length-1;i++) {
            if(arrOfStr[i].contains("*")){
                String S1=(link+arrOfStr[i]).replace("/ /","/");
                S1=S1.replace("//","/");
                S1=S1.replace("*",".*");
                System.out.println(S1);
                if(Pattern.matches(S1,URL)){
                    return false;
                }

            }
            else{
                String S1=(link+arrOfStr[i].split(" ", 3)[1]).replace("/ /","/");
                S1=S1.replace("//","/");
                System.out.println(S1);
                if ((URL.equals(S1))){
                    return false;
                }

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
                    else if((!list.contains(page.attr("abs:href"))) && (Disallowed(list.get(index),page.attr("abs:href")))){
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
