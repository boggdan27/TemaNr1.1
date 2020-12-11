package mta.Tema1;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CCrawlerHandler {
    String current_url;         // url-ul paginii pe care dorim sa o descarcam
    String path_current_url;    // path-ul paginii descarcate
    int statusCode;             // codul conexiunii url-ului prin http
    String html;

    public CCrawlerHandler(String current_url) throws IOException {

        this.current_url = current_url;
        this.path_current_url = create_path(this.current_url);
        this.statusCode = 0;
        this.download_page();
        this.html = getHtml();
    }

    public String getCurrent_url() {
        return current_url;
    }

    public void verify_robot() {

    }

    public String create_path(String url) {     // functie care schimba url-ul in calea catre pagina descarcata corespunzatoare url-ului respectiv
        String url_filename = url.substring(7, url.length() - 1);
        url_filename = Paths.get("").toAbsolutePath().toString() + url_filename + ".html";
        return url_filename;
    }

    public void download_page() throws IOException {
        // functie pentru a descarca pagina web in format html
        // realizare conexiune cu url-ul curent prin http
        URL current = new URL(this.current_url);
        HttpURLConnection connect = (HttpURLConnection) current.openConnection();

        int responseCode = connect.getResponseCode();   //cod conexiuenpentru a afla daca conexiunea se poate face sau nu
        String responseMessage = null;

        if (responseCode == 200) {      // conexiunea se poate face
            statusCode = responseCode;
            responseMessage = "Codul primit este " + responseCode + " : Conexiunea cu url " + current_url + " a fost facuta cu succes!";
            connect.connect();      //realizare conexiune

            FileWriter writer = new FileWriter(this.path_current_url);  //creare fisier daca nu exista

            // realizare scriere in fisier html a paginii web
            InputStream is = current.openStream();

            try (BufferedReader page = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = page.readLine()) != null) {
                    writer.write(line);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new MalformedURLException("URL is malformed!!");
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException();
            }
            writer.close();
        }
        else if (responseCode == 404) {     //nu se poate face conexiunea
            this.statusCode = responseCode;
            responseMessage = "Codul primit este " + responseCode + " : Url " + current_url + " nu a fost gasit!";
        }
        //creare fisier log daca nu exista deja
        File create_file = new File(Paths.get("").toAbsolutePath().toString() + "\\logFile.txt");
        create_file.createNewFile();
        boolean append = true;
        Path logFile = Paths.get(Paths.get("").toAbsolutePath().toString() + "\\logFile.txt");
        byte bytes[] = ("\r\n" + LocalDateTime.now() + ": " + responseMessage).getBytes();
        Files.write(logFile, bytes, StandardOpenOption.APPEND); //scriere in fisierul de log
    }

    public ArrayList<String> extract_links() throws IOException {       // functie pentru a extrage url-uri valide din pagina curenta

        String htmlstring = this.html;
        if (this.statusCode == 200) {       // conexiunea a fost realizata cu succes
            ArrayList<String> elements = new ArrayList<String>();       // lista url valide

            Pattern pTag = Pattern.compile("(?i)<a([^>]+)>(.+?)</a>");  // pattern pentru tagul <a>
            Matcher mTag = pTag.matcher(htmlstring);                    // un obiect care face match pattern-ului cu continutul html

            Pattern pLink = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");   //pattern pentru href
            Matcher mLink;                                              // un obiet care va face match patternului (href) cu stringul avand tagul <a>

            // pattern url valid
            Pattern urlValid = Pattern.compile("(?i)^(?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?$");

            while (mTag.find()) {   // se cauta stringuri cu tagul <a>
                String href = mTag.group(1);     // get the values of href

                mLink = pLink.matcher(href);    // match-ul pentru href

                while (mLink.find()) {      // se cauta stringuri care contin href
                    String link = mLink.group(1); // extragere link

                    String[] parts = link.split("\"");
                    String l = parts[1];

                    Matcher mValid = urlValid.matcher(l);   // verificare link daca este un url valid
                    if (mValid.find()) {
                        elements.add(l);
                    }
                }
            }

            return elements;
        } else {
            return null;
        }
    }
    public String getHtml() throws IOException {

        String filename = path_current_url;
        String htmlstring = null;

        FileReader filer = new FileReader(filename);
        BufferedReader buffr = new BufferedReader(filer);

        boolean eof = false;
        while (!eof) {
            String s = buffr.readLine();
            if (s == null) {
                eof = true;
            } else {
                if (htmlstring == null) {
                    htmlstring = s;
                } else {
                    //System.out.println(s);
                    htmlstring = htmlstring + " " + s;
                }
            }
        }
        return htmlstring;
    }
    public ArrayList<String> extract_img() throws IOException {
        ArrayList<String> img = new ArrayList<String>();
        String imgRegex = "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";

        Pattern pattern = Pattern.compile(imgRegex);
        Matcher match = pattern.matcher(this.html);

        while (match.find()){
            if (!match.group(1).isEmpty()) { // We have a new IMG tag
                String imgSrc = match.group(1);
                String imageName = imgSrc.substring(imgSrc.lastIndexOf("/") +1);
                System.out.println(imageName); // prints out name.gif||png||jpeg
                img.add(imageName);

            }
            //System.out.println(match.group(2) + ": " + match.group(4));
        }
        return img;
    }
}
