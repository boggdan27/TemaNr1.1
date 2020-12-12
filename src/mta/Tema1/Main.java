package mta.Tema1;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        ICrawler c = CCrawler.getInstance();

        CCrawlerHandler cc =new CCrawlerHandler("https://mta.ro/");

        CConfigure conf= new CConfigure();

        cc.extract_img();
        cc.extract_links();
        cc.extract_pdf();
        cc.extract_css();
        cc.extract_js();

    }



}
