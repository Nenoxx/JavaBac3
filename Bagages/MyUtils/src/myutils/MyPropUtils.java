/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myutils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Arnaud
 */
public class MyPropUtils {
    public static synchronized String myGetProperty(String file, String key)throws FileNotFoundException, IOException {
        String value = null;
        Properties properties = new Properties();
        
        FileInputStream fis = new FileInputStream(file);
        properties.load(fis);
        value = properties.getProperty(key);
        fis.close();
        return value;
    }
}
