package indexer;

import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class Indexer implements Runnable {
    private Thread thread;
    private int Max_Depth;
    private List<String> visitedUrl;
    private List<String> elementList=new ArrayList<>();

    public Indexer ()
    {

        thread =new Thread(this);
        thread.start();

    }

    public static void main(String[] args)
    {
        Indexer indexer = new Indexer();
        Document test=indexer.requestDocument("https://www.javatpoint.com/multithreading-in-java");
        indexer.parseDocument(test);
                        System.out.println(indexer.elementList.size() + "\n");

                for (int i = 0; i < indexer.elementList.size(); i++) {
                    System.out.println("\n" + indexer.elementList.get(i) + "\n");

                }
    }


    void parseDocument(Document doc)
    {

         int j=1;



        if (doc!=null) {
            System.out.println("document is not empty \n");


            while (doc.select("h" + j).size()!=0) {

                Elements headers = doc.select("h" + j);



                   elementList.addAll(headers.eachText());

                   j++;

            }


        }
        else
            {
                System.out.println("document = null \n");
            }



    }


    @Override
    public void run()
    {

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
