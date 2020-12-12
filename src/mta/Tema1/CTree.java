package mta.Tema1;

import java.io.IOException;
import java.util.ArrayList;

public class CTree {
    private String root;
    private ArrayList<String> children;
    private ArrayList<String> all;
    private int depth;
    private ArrayList<String> vizitate;

    public CTree(String root, int depth)
    {
        this.root = root;
        this.depth = depth;
        this.children = new ArrayList<String>();
        this.all = new ArrayList<String>();
        this.vizitate = new ArrayList<String>();
    }

    public void setChildren() throws IOException {
        CCrawlerHandler c = new CCrawlerHandler(root);
        this.children = c.extract_links();
        vizitate.add(root);
        System.out.println("Sunt root "+ root + " sii am copiii: ");
        for(int i =0; i<this.children.size();i++)
        {
            System.out.println(children.get(i));
        }
        getChildren(this.children,this.depth);

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
                            }
                        }
                    }
                }
            }

            this.all.addAll(a);
            getChildren(a, d);
        }
    }
}
