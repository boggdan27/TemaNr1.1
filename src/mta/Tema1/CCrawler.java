package mta.Tema1;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CCrawler implements ICrawler {
    private static CCrawler instance = null;

    List<String> url_visited ;
    List<String> url_to_visit;
    List<CMyThread> threads;
    CConfigure conf;

    public CCrawler() throws FileNotFoundException {
        url_to_visit = this.getFirstUrls();
        url_visited = new ArrayList<String>();
    }

    public static CCrawler getInstance() throws FileNotFoundException {
        if(instance == null) {
            instance = new CCrawler();
        }
        return instance;
    }

    public void connect(){
    }

    public ArrayList<String> getFirstUrls() throws FileNotFoundException //
    {
        ArrayList<String> urls = new ArrayList<String>();
        File urlsFile = new File("listaUrl.txt");
        Scanner myReader = new Scanner(urlsFile);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            urls.add(data);
        }
        myReader.close();

        return urls;
    }

    public void crawl_page(String p){

    }

    public void run(){
    }

    public void search_by_word(String cuv){

    }
    public void list_sitemap(){

    }
    public void list_all_type(){

    }

    public void create_sitemap(){

    }

}
