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
    private String current_url;         // url-ul paginii pe care dorim sa o descarcam
    private String path_current_url;    // path-ul paginii descarcate
    private int statusCode;             // codul conexiunii url-ului prin http
    private String html;
    ArrayList<String> user_disallow = new ArrayList<String>();

    public ArrayList<String> getLinks() throws  IOException{
        ArrayList<String> links=this.extract_links();
        return links;
    }

    public ArrayList<String> getCss() throws  IOException{
        ArrayList<String> css=this.extract_css();
        return css;
    }

    public ArrayList<String> getPdf() throws  IOException{
        ArrayList<String> pdfs=this.extract_pdf();
        return pdfs;
    }

    public ArrayList<String> getImg() throws  IOException{
        ArrayList<String> imgs=this.extract_pdf();
        return imgs;
    }

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
//functie de verificare permisiuni de downloadare pentru crawler.
        String calea_robot = current_url + "/robots.txt";
        String useragent = null;
        user_disallow = null; //lista de disallow

        try{
            URL new_url = new URL(calea_robot);
            HttpURLConnection conn = (HttpURLConnection) new_url.openConnection();

            BufferedReader continut_robot = new BufferedReader(new InputStreamReader(new_url.openStream()));
            String linie = continut_robot.readLine();       //citire linie cu linie

            while(linie != null)
            {
                if(linie.contains("User-agent:")|| linie.contains("User-Agent:") || linie.toLowerCase().startsWith("user-agent"))  //daca am gasit un agent
                {
                    int start = linie.indexOf(":") + 1;
                    int end   = linie.length();
                    useragent = linie.substring(start, end).trim();  //il stochez
                }
                else if(linie.contains("Disallow:"))
                {
                    if(useragent.equals("*"))   //daca agentul e de tip *, pun ce are in disallow
                    {
                        int start = linie.indexOf(":") + 1;
                        int end   = linie.length();
                        String aux = linie.substring(start,end).trim();
                        user_disallow.add(aux);
                    }
                }
                linie = continut_robot.readLine();
            }

        }catch(IOException e)
        {
            System.out.println("Error: something is wrong with robots.txt");
        }
    }

    public ArrayList<String> getUser_disallow() {
        return user_disallow;
    }

    public String create_path(String url) throws IOException {     // functie care schimba url-ul in calea catre pagina descarcata corespunzatoare url-ului respectiv
        System.out.println("Ultimul url este: " + url);
        String url_filename = url.replace("https://","");
        url_filename = url_filename.replace("http://","");
        url_filename = url_filename.replace("http:","");
        url_filename = url_filename.replace("www.","");
        String a = url_filename.substring(url_filename.length()-1);
        if(a.equals("/")) {
            url_filename = url_filename.substring(0,url_filename.length()-1);
        }
        String[] b = url_filename.split("/");
        String mm = b[0];
        for( int i =0;i<b.length;i++)
        {
            Path path = Paths.get(Paths.get("").toAbsolutePath().toString() +"\\" + mm);
            if(Files.notExists(path)) {
                Files.createDirectory(path);
            }
            if(i!=b.length-1) {
                mm = mm + "\\" + b[i + 1];
            }
        }
        String c = url_filename.replace(b[b.length-1],"");
        Path path = Paths.get(Paths.get("").toAbsolutePath().toString() +"\\" + c);
        if(Files.notExists(path)) {
            Files.createDirectory(path);
        }
        url_filename = Paths.get("").toAbsolutePath().toString() +"\\"+ url_filename + ".html";
        return url_filename;
    }

    public void download_page() throws IOException {

        ArrayList<String> dis = this.getUser_disallow();
        URL current = new URL(this.current_url);
        boolean flag = true;

        for(String diss_pag: dis)
        {
            if(current.equals(diss_pag))
            {
                flag= false;
            }
        }

        if(flag == true) {        // functie pentru a descarca pagina web in format html
            // realizare conexiune cu url-ul curent prin http
            HttpURLConnection connect = (HttpURLConnection) current.openConnection();

            int responseCode = connect.getResponseCode();   //cod conexiuenpentru a afla daca conexiunea se poate face sau nu
            String responseMessage = connect.getResponseMessage();

            if (responseCode < 400) {       // conexiunea se poate face
                statusCode = responseCode;
                responseMessage = "COD : "+responseCode + " Message: "+ responseMessage + " conexiunea cu url " + current_url + " a fost facuta cu succes!";
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
            } else if (responseCode == 404) {     //nu se poate face conexiunea
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
                    if (parts.length != 0) {
                        String l = parts[parts.length - 1];

                        Matcher mValid = urlValid.matcher(l);   // verificare link daca este un url valid
                        if (mValid.find()) {
                            elements.add(l);
                        }

                    }
                }
            }

            return elements;
        } else {
            return null;
        }
    }

    public int search_by_word(String wrd, String htmlpage) {
        Pattern pWord = Pattern.compile("(?i).*?\\b" + wrd + "\\b.*?");
        Matcher mWord = pWord.matcher(htmlpage);
        int count = 0;

        while (mWord.find()) {
            count++;
        }
        return count;
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
               // System.out.println(imageName); // prints out name.gif||png||jpeg
                img.add(imageName);

            }
        }
        return img;
    }

    public ArrayList<String> extract_pdf() throws IOException{
        ArrayList<String> pdf = new ArrayList<String>();
        String pdfRegex = "[^\\/\'\\-]*.pdf";

        Pattern pattern = Pattern.compile(pdfRegex);
        Matcher match = pattern.matcher(this.html);

        while (match.find()) {
            if (!match.group(0).isEmpty()) { // We have a new IMG tag
                String pdfSrc = match.group(0);
                String pdfName = pdfSrc.substring(pdfSrc.lastIndexOf("/") + 1);
                //System.out.println(pdfName); // prints out name.pdf
                pdf.add(pdfName);

            }
        }

        return pdf;
    }

    public ArrayList<String> extract_js() throws IOException{
        ArrayList<String> js = new ArrayList<String>();
        String jsRegex = "[^\\/\'\\-]*.js";

        Pattern pattern = Pattern.compile(jsRegex);
        Matcher match = pattern.matcher(this.html);

        while (match.find()) {
            if (!match.group(0).isEmpty()) { // We have a new IMG tag
                String jsSrc = match.group(0);
                String jsName = jsSrc.substring(jsSrc.lastIndexOf("/") + 1);
                //System.out.println(jsName); // prints out name.js
                js.add(jsName);

            }
        }
        return js;
    }

    public ArrayList<String> extract_css() throws IOException{
        ArrayList<String> css = new ArrayList<String>();
        String cssRegex = "[^\\/\'\\-]*.css";

        Pattern pattern = Pattern.compile(cssRegex);
        Matcher match = pattern.matcher(this.html);

        while (match.find()) {
            if (!match.group(0).isEmpty()) { // We have a new IMG tag
                String cssSrc = match.group(0);
                String cssName = cssSrc.substring(cssSrc.lastIndexOf("/") + 1);
                //System.out.println(cssName); // prints out name.cs
                css.add(cssName);

            }
        }
        return css;
    }


}
