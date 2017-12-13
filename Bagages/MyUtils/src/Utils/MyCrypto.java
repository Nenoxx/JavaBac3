/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.*;
import java.util.Date;
import java.security.*;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 *
 * @author Arnaud
 */
public class MyCrypto {
    private static final String codeProvider = "BC";
    
    public static void showProviders(){
        Provider prov[] = Security.getProviders();
        for(int i=0; i< prov.length; i++){
            System.out.println(prov[i].getName() + " - " + prov[i].getVersion());
        }
    }
    
    public static synchronized String createDigest(String login, String pwd) 
            throws NoSuchAlgorithmException, NoSuchProviderException, IOException{
        
        MessageDigest md = MessageDigest.getInstance("SHA-1", codeProvider);
        md.update(pwd.getBytes());
        long temps = (new Date()).getTime();
        double alea = Math.random();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream bdos = new DataOutputStream(baos);
        bdos.writeLong(temps);
        bdos.writeDouble(alea);
        md.update(baos.toByteArray());
        byte[] msgD = md.digest();
        
        String digest = login + "|" + temps+ "|" + alea+ "|" + msgD.length+ "|" + Arrays.toString(msgD);
        System.out.println("Digest créé: [" + digest + "]");
        return digest;
    }
    
    public static synchronized String createDigestFull(String login, String pwd, long temps, double alea)
            throws NoSuchAlgorithmException, NoSuchProviderException, IOException{
        
        MessageDigest md = MessageDigest.getInstance("SHA-1", codeProvider);
        md.update(pwd.getBytes());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream bdos = new DataOutputStream(baos);
        bdos.writeLong(temps);
        bdos.writeDouble(alea);
        md.update(baos.toByteArray());
        byte[] msgD = md.digest();
        
        String digest = login + "|" + temps+ "|" + alea+ "|" + msgD.length+ "|" + Arrays.toString(msgD);
        System.out.println("Digest créé: [" + digest + "]");
        return digest;
    }
    
    public static synchronized boolean compareDigest(String dig1, String dig2)
            throws NoSuchAlgorithmException, NoSuchProviderException, IOException{
                
        // récup de digest 1
        StringTokenizer parser1 = new StringTokenizer(dig1, "|");
        String login1 = parser1.nextToken();
        long temps1 = Long.parseLong(parser1.nextToken());
        double alea1 = Double.parseDouble(parser1.nextToken());
        int taille1 = Integer.parseInt(parser1.nextToken());
        byte[] msgD1 = parser1.nextToken().getBytes();
        
        // récup de digest 2
        StringTokenizer parser2 = new StringTokenizer(dig2, "|");
        String login2 = parser2.nextToken();
        long temps2 = Long.parseLong(parser2.nextToken());
        double alea2 = Double.parseDouble(parser2.nextToken());
        int taille2 = Integer.parseInt(parser2.nextToken());
        byte[] msgD2 = parser2.nextToken().getBytes();
        
        System.out.println("Digest1: [" + Arrays.toString(msgD1));
        System.out.println("Digest2: [" + Arrays.toString(msgD2));
        if(MessageDigest.isEqual(msgD1, msgD2)){
            return true;
        }
        else{
            return false;
        }
    }
}
