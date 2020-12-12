package mta.Tema1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CConfigure{
    private static CConfigure instance_config = null;

    int threads_nr;
    int delay;
    String root_director;
    int log_level;
    File config= new File("config.txt");;
    public CConfigure() throws FileNotFoundException {
        int i=0;
        String myInput = null;
        String myInput2 = null;
        Scanner sc =new Scanner(config);
        sc.useDelimiter("\n");

        while(sc.hasNext()) {
            i++;
            myInput = sc.next();
            Scanner sc2 = new Scanner(myInput);
            sc2.useDelimiter("=");
            sc2.next();
            //System.out.println(sc2.next());
            if(i==1)
            {
                myInput2 = sc2.next();
                //System.out.println(myInput2);
                threads_nr=strToInt(myInput2.trim());
               // threads_nr=Integer.parseInt(myInput2);
            }
            else if(i==2)
            {
                myInput2 = sc2.next();
                //System.out.println(sc2.next());
                delay=strToInt(myInput2.trim());
                //delay=Integer.parseInt(sc2.next());
            }
            else if(i==3)
            {
                myInput2 = sc2.next();
                //System.out.println(sc2.next());
                root_director =myInput2;
            }
            else if(i==4)
            {
                myInput2 = sc2.next();
                //System.out.println(sc2.next());
                log_level=strToInt(myInput2);
                //log_level=Integer.parseInt(sc2.next());
            }
        }


    }
    public static int strToInt(String str){
        int i = 0;
        int num = 0;
        boolean isNeg = false;

        // Check for negative sign; if it's there, set the isNeg flag
        if (str.charAt(0) == '-') {
            isNeg = true;
            i = 1;
        }

        // Process each character of the string;
        while( i < str.length()) {
            num *= 10;
            num += str.charAt(i++) - '0'; // Minus the ASCII code of '0' to get the value of the charAt(i++).
        }

        if (isNeg)
            num = -num;
        return num;
    }

    public static CConfigure getInstance() throws FileNotFoundException {
        if(instance_config == null) {
            instance_config = new CConfigure();
        }
        return instance_config;
    }


    public int getThreads_nr() {
        return threads_nr;
    }

    public int getDelay() {
        return delay;
    }

    public String getRoot_director() {
        return root_director;
    }

    public int getLog_level() {
        return log_level;
    }
}
