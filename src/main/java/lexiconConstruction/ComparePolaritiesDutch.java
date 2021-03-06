package lexiconConstruction;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by krayush on 08-07-2015.
 */
public class ComparePolaritiesDutch {
    String rootDirectory;
    ComparePolaritiesDutch()throws IOException
    {
        rootDirectory = System.getProperty("user.dir");
        mainFunction(rootDirectory);
    }

    //public static void main(String[] args)throws IOException {
    private void mainFunction(String rootDirectory)throws IOException
    {
        File fR = new File(rootDirectory+"\\resources\\DTExpansion\\IndianSentCooc\\polarity.txt");
        //PrintWriter writer = new PrintWriter("D:\\Course\\Semester VII\\Internship\\Results\\Maggie TUD\\sentimentJavaWords.txt");
        BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(fR), "UTF-8"));

        LinkedHashMap<String, Double> cooc = new LinkedHashMap<String, Double>();
        String line;
        while((line = bf.readLine()) != null)
        {
            String tokens[] = line.split("\\|");
            cooc.put(tokens[0], Double.parseDouble(tokens[1]));
        }


        File fR1 = new File(rootDirectory+"\\resources\\DTExpansion\\HTTPResults\\sortedDT.txt");
        //PrintWriter writer = new PrintWriter("D:\\Course\\Semester VII\\Internship\\Results\\Maggie TUD\\sentimentJavaWords.txt");
        BufferedReader bf1 = new BufferedReader(new InputStreamReader(new FileInputStream(fR1), "UTF-8"));

        LinkedHashMap<String, Double> dt = new LinkedHashMap<String, Double>();
        while((line = bf1.readLine()) != null)
        {
            String tokens[] = line.split("\\|");
            //System.out.println(tokens[1]);
            dt.put(tokens[0], Double.parseDouble(tokens[1]));
        }

        //System.out.println(dt.size());

        Writer polWriter = new OutputStreamWriter(
                new FileOutputStream(rootDirectory+"\\resources\\DT_COOCHindiLexicon.txt"), "UTF-8");
        BufferedWriter pfout = new BufferedWriter(polWriter);
        Iterator polarityIterator = cooc.entrySet().iterator();
        int count=1;

        while(polarityIterator.hasNext()) {
            Map.Entry me = (Map.Entry) polarityIterator.next();
            String key = me.getKey().toString();
            Double val = Double.parseDouble(me.getValue().toString());
            //System.out.println("pqr");
            if(dt.containsKey(key))
            {
                //System.out.println("abc");
                count++;
                if(val*dt.get(key) > 0)
                {
                    if(val > 0)
                    {
                        pfout.write(key+"|"+val+"|"+dt.get(key)+"|"+"1"+"\n");
                        //pfout.write(key+"|"+"1"+"\n");
                    }
                    else
                    {
                        pfout.write(key+"|"+val+"|"+dt.get(key)+"|"+"-1"+"\n");
                        //pfout.write(key+"|"+"-1"+"\n");
                    }
                }
                else if(val ==0 && dt.get(key)==0)
                {
                    pfout.write(key+"|"+val+"|"+dt.get(key)+"|"+"0"+"\n");
                    //pfout.write(key+"|"+"0"+"\n");
                }
                //else
            }

        }
        System.out.println(count);
        pfout.close();

    }
}
