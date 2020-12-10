package mta.Tema1;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        ICrawler c = CCrawler.getInstance();

        CCrawlerHandler cc =new CCrawlerHandler("https://mta.ro/");
        cc.download_page();
        cc.extract_links();

    }



}
