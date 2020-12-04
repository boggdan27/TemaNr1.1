package mta.Tema1;
import java.util.List;

public class CCrawler implements ICrawler {
    private static CCrawler instance = null;

    List<String> url_visited ;
    List<String> url_to_visit;
    List<CMyThread> threads;
    CConfigure conf;

    public CCrawler() {
    }

    public static CCrawler getInstance(){
        if(instance == null) {
            instance = new CCrawler();
        }
        return instance;
    }

    public void connect(){
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
