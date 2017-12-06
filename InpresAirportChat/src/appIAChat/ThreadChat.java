/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appIAChat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import javax.swing.JTextArea;

/**
 *
 * @author Arnaud
 */
public class ThreadChat extends Thread{
    private final MulticastSocket socketGroupe;
    private int port;
    private final JTextArea textArea;
    private boolean running;
    
    public ThreadChat(JTextArea textArea, MulticastSocket s, int port){
        this.socketGroupe = s;
        this.port = port;
        this.textArea = textArea;
    }
    
    @Override
    public void run(){
        this.running = true;
        while(running){
            try{
                byte[] buf = new byte[1000];
                DatagramPacket dp = new DatagramPacket(buf, buf.length, socketGroupe.getInetAddress(), port);
                socketGroupe.receive(dp);
                
                String messageComplet = new String(buf);
                String message = checkDigest(messageComplet);
                if(message != null){
                    textArea.append(message + "\n");
                }  
            }
            catch (IOException ex){
                System.out.println("ERREUR THREAD CLIENT : " + ex);
            }
        }
    }

    public boolean isRunning(){
        return running;
    }

    public void setRunning(boolean running){
        this.running = running;
    }
    
    private String checkDigest(String messageComplet) {
        if(messageComplet.contains("#")){
            
            String[] result = messageComplet.split("#");
            String part1 = result[0].trim();
            String part2 = result[1].trim();
            try {
                MessageDigest md = MessageDigest.getInstance("MD5", "BC");
                md.update(part2.getBytes());
                byte[] newDigest = md.digest();

                System.out.println("oldDigest: " + part1);
                System.out.println("newDigest: " + Arrays.toString(newDigest));
                
                if(part1.equals(Arrays.toString(newDigest))){
                    System.out.println("Le digest correspond au message !");
                    return result[1]; 
                }
                System.out.println("Le digest ne correspond pas au message !");
            } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
                System.out.println("Erreur de comparaison de digest: " +ex);
            }
            
        }else{
            System.out.println("Message d'info du serveur, pas de digest");
            return messageComplet;
        }
        return null;
    }
}
