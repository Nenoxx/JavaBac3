/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseUtils;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;



/**
 *
 * @author nenoxx
 */
public class IFileReader {
    private String csvFile = "";
    private String line = "";
    private Boolean continuer = true;
    
    public IFileReader(String path){
        csvFile = path;
    }
    
    public String GetProperty(String propertyName){
        String value = "";
        
        try{
            BufferedReader br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null && continuer == true) {
                String[] r = line.split(" = ");
                if(r[0].compareTo(propertyName) == 0){ //iz ok
                    value = r[1];
                    continuer = false;
                }
            }
            if(continuer == false)//Alors on a trouvé
                return value;
            else return null;
        }
        catch(Exception ex){
            System.out.println("Han ouais " + ex.getLocalizedMessage());
        } 
        return null;
    }
    
    public String FetchRow(String key, String separator){
        String value = "";
        
        try{
            BufferedReader br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null && continuer == true) {
                String[] r = line.split(separator);
                if(r[0].compareTo(key) == 0){ //iz ok
                    value = r[0] + separator + r[1];
                    continuer = false;
                }
            }
            if(continuer == false)//Alors on a trouvé
                return value;
            else return null;
        }
        catch(Exception ex){
            System.out.println("Han ouais " + ex.getLocalizedMessage());
        } 
        return null;
    }
    
}
