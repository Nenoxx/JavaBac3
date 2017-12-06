/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serIAChat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.JTextArea;

/**
 *
 * @author Arnaud
 */
public class ThreadUDP extends Thread {

    private boolean running = true;
    private int portUDP;
    private String ipUDP;
    public JTextArea textArea;
    private InetAddress addressGroup = null;
    private MulticastSocket socketGroupe = null;
    JLabel label;
    
    public ThreadUDP(int portUDP, String ipUDP, JTextArea textArea, JLabel label) {
        this.portUDP = portUDP;
        this.ipUDP = ipUDP;
        this.textArea = textArea;
        this.label = label;
        
        try {
            socketGroupe = new MulticastSocket(this.portUDP);
            addressGroup = InetAddress.getByName(this.ipUDP); //this.ipUDP
             socketGroupe.joinGroup(addressGroup);
        } catch (IOException ex) {
            System.out.println("ERREUR THREAD UDP constructeur: " + ex);
            System.exit(1);
        }
    }
    
    @Override
    public void run() {
        while(running){
            try{
                byte[] buf = new byte[1000];
                DatagramPacket dp = new DatagramPacket(buf, buf.length, socketGroupe.getInetAddress(), portUDP);
                socketGroupe.receive(dp);
                
                String messageComplet = new String(buf);
                String message = checkDigest(messageComplet);
                if(message != null){
                    textArea.append(message + "\n");
                    if(message.contains("a quitté le chat ***")){
                        //mise à jour du compteur
                        String l = label.getText();
                        int num = Integer.parseInt(l) -1;
                        label.setText(Integer.toString(num));
                    }
                }  
            }
            catch (IOException ex){
                System.out.println("ERREUR THREAD UDP run: " + ex);
            }
        }
    }
    
    public void setRunning(boolean running){
        this.running = running;
    }
    
    public void EOC(){
        String message = "*** Le serveur de connexion n'est plus disponible ! ***";
        try {
            socketGroupe.send(new DatagramPacket(message.getBytes(), message.getBytes().length, addressGroup, portUDP));
            textArea.append(message + "\n");
        } catch (IOException ex) {
            System.out.println("ERREUR THREAD UDP EOC: " + ex);
        }
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
