package Crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.regex.*;
import java.io.IOException;

public class Robot {
    public boolean Disallowed(String link,String URL) throws IOException {
        Document document = Jsoup.connect(link+"robots.txt").get();
        link=link.replace("//","/");
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
                if(Pattern.matches(S1,URL)){
                    return false;
                }

            }
            else{
                String S1=(link+arrOfStr[i].split(" ", 3)[1]).replace("/ /","/");
                S1=S1.replace("//","/");
                if ((URL.equals(S1))){
                    return false;
                }

            }
        }



        return true;
    }
    public static void main(String[] args) throws IOException {
        boolean b=new Robot().Disallowed("https://flickr.com/","https:/flickr.com/photos/tags/hxxbzb/pagehxjbjhxb");
        System.out.println(b);
    }
}
