
package dependencysolver;


/**
 * Write a description of class FileOpener here.
 * 
 * @author vk96 - Vincent King 
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class FileOpener
{
    String line;
    BufferedReader br;
    ArrayList<String> lines;

    /**
     * Constructor for objects of class FileOpener
     */
    public FileOpener(String filename) throws Exception{
        line = "";
        lines = new ArrayList<>();
        try{
            br = new BufferedReader(new FileReader(filename));
            while((line = br.readLine()) != null){
                lines.add(line);
            }
        } catch (Exception e){
        }finally {
            try {
                if (br != null)br.close();
            } catch (Exception ex) {
            }
        }
    }
    
    public ArrayList<String> getLines(){
        return lines;
    }
}
