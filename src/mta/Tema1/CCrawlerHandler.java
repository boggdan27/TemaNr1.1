package mta.Tema1;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CCrawlerHandler {
    String current_url;
    String path_current_url;

    public CCrawlerHandler(String current_url) {

        this.current_url = current_url;
        this.path_current_url = create_path(this.current_url);
    }

    public String getCurrent_url() {
        return current_url;
    }

    public void verify_robot()
    {

    }

    public String create_path(String url)
    {
        String  url_filename= url.substring(7,url.length()-1);
        url_filename = Paths.get("").toAbsolutePath().toString() + url_filename + ".html";
        return url_filename;
    }

    public void download_page() throws IOException {
        URL current = new URL(this.current_url);
        URLConnection connect = current.openConnection();
        connect.connect();

        File f = new File(this.path_current_url);
        FileWriter writer = new FileWriter(this.path_current_url);

        InputStream is = current.openStream();

        try( BufferedReader page = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = page.readLine()) != null) {
                writer.write(line);
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            throw new MalformedURLException("URL is malformed!!");
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    writer.close();

    }

    public ArrayList<String> extract_links() throws IOException {

        String filename = path_current_url;
        String htmlstring = null;

        FileReader filer = new FileReader(filename);
        BufferedReader buffr = new BufferedReader(filer);

        boolean eof = false;
        while (!eof)
        {
            String s = buffr.readLine();
            if(s == null)
            {
                eof = true;
            }
            else
            {
                if(htmlstring == null)
                {
                    htmlstring = s;
                }
                else {
                    //System.out.println(s);
                    htmlstring = htmlstring + " " + s;
                }
            }
        }

        ArrayList<String> elements = new ArrayList<String>();

        Pattern pTag = Pattern.compile("(?i)<a([^>]+)>(.+?)</a>");
        Matcher mTag = pTag.matcher(htmlstring);

        Pattern pLink = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");
        Matcher mLink;

        Pattern urlValid = Pattern.compile("(?i)^(?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?$");

        while (mTag.find()) {
            String href = mTag.group(1);     // get the values of href
            String linkElem = mTag.group(2); // get the text of link Html Element

            mLink = pLink.matcher(href);

            while (mLink.find()) {
                String link = mLink.group(1);

                String[] parts = link.split("\"");
                String l = parts[1];
                //String l = link.substring(link.indexOf("(") +1 , link.indexOf(")"));

                Matcher mValid = urlValid.matcher(l);
                if(mValid.find()) {
                    elements.add(l);
                   // System.out.println(l);
                }
            }
        }

        return elements;
    }

}
