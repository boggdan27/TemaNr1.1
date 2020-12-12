package mta.Tema1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CTree {
    private String root;
    private ArrayList<String> children;
    private ArrayList<String> all;
    private int depth;
    private ArrayList<String> vizitate;

    private ArrayList<String> css=new ArrayList<>();
    private ArrayList<String> pdf=new ArrayList<>();
    private ArrayList<String> img=new ArrayList<>();
    private ArrayList<String> js=new ArrayList<>();




    public CTree(String root, int depth) throws IOException {
        this.root = root;
        this.depth = depth;
        this.children = new ArrayList<String>();
        this.all = new ArrayList<String>();
        this.vizitate = new ArrayList<String>();
        FileWriter sitemap =new FileWriter("SiteMap");
    }

    public void setChildren() throws IOException {
        CCrawlerHandler c = new CCrawlerHandler(root);
        this.children = c.extract_links();
        vizitate.add(root);

        System.out.println("Sunt root "+ root + " si am copiii: ");
        for(int i =0; i<this.children.size();i++)
        {
            System.out.println(children.get(i));
            for(String local: c.getCss())
                this.css.add(local);
            for(String local: c.getImg())
                this.img.add(local);
            for(String local: c.getPdf())
                this.pdf.add(local);
            for(String local: c.getJs())
                this.js.add(local);
            genSiteMap(root,depth);

        }
    }

    public void getChildren(ArrayList<String> list ,int depth) throws IOException {
        int d = depth-1;
        if(d != 0) {
            ArrayList<String> a = new ArrayList<String>();
            for (int i = 0; i < list.size(); i++) {
                if(list.get(i).equals("https://www.certmil.ro/") == false && list.get(i).equals( "https://main.mta.ro/SOGo/so/")==false && list.get(i).substring(list.get(i).length()-4).equals(".pdf")== false &&list.get(i).substring(list.get(i).length()-4).equals(".htm")== false &&list.get(i).substring(list.get(i).length()-4).equals(".doc")== false ){
                    if (!vizitate.contains(list.get(i))) {
                        CCrawlerHandler c = new CCrawlerHandler(list.get(i));
                        vizitate.add(list.get(i));
                        ArrayList<String> b = c.extract_links();
                        if (b != null) {
                            a.addAll(b);
                            System.out.println("adancimea este : " + depth);
                            System.out.println("Sunt root " + list.get(i) + " sii am copiii: ");
                            for (int j = 0; j < b.size(); j++) {
                                System.out.println(b.get(j));
                                for(String local: c.getCss())
                                    this.css.add(local);
                                for(String local: c.getImg())
                                    this.img.add(local);
                                for(String local: c.getPdf())
                                    this.pdf.add(local);
                                for(String local: c.getJs())
                                    this.js.add(local);
                            }
                        }
                    }
                }
            }

            this.all.addAll(a);
            getChildren(a, d);
        }
    }

    public void genSiteMap(String name,Integer nr) throws IOException{

        FileWriter sitemap =new FileWriter("SiteMap.txt");
        String[] namee=name.split("/",5);
        sitemap.write(namee[2]+"\n\n");

        sitemap.write("\t\tcss/ \n");
        for(String local: this.css) {
            if (!local.contains("err"))
            sitemap.write("\t\t\t\t-" + local + " \n");
        }

        sitemap.write("\t\timagini/ \n");
        for(String local: this.img )
            sitemap.write("\t\t\t\t-"+local+"\n");

        sitemap.write("js/ \n");
        for(String local:this.js )
            sitemap.write("\t\t\t\t-"+local+"\n");

        sitemap.write("\t\tpdf/ \n");
        for(String local: this.pdf )
            sitemap.write("\t\t\t\t-"+local+"\n");

    }
}
