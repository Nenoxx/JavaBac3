/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appIAChat;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import IACOP.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.swing.JOptionPane;

/**
 *
 * @author Arnaud
 */
public class appIAChat extends javax.swing.JFrame {

    private MulticastSocket socketGroup = null;
    private Socket clientSocket= null;
    private InetAddress addressGroup = null;
    private int portChat;
    private final int portFly = 26027;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;
    
    private ThreadChat thChat = null;
    
    private boolean connected = false;
    private String user;
    private SecureRandom sr = null;
    /**
     * Creates new form appIAChat
     */
    public appIAChat() {
        initComponents();        
        sr = new SecureRandom();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ConnexionButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        ChatTextArea = new javax.swing.JTextArea();
        CommandComboBox = new javax.swing.JComboBox<>();
        MessageTextField = new javax.swing.JTextField();
        EnvoyerButton = new javax.swing.JButton();
        NumeroLabel = new javax.swing.JLabel();
        NumeroTextField = new javax.swing.JTextField();
        IPTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("appIAChat");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        ConnexionButton.setText("Connexion");
        ConnexionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConnexionButtonActionPerformed(evt);
            }
        });

        ChatTextArea.setEditable(false);
        ChatTextArea.setColumns(20);
        ChatTextArea.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        ChatTextArea.setRows(5);
        jScrollPane1.setViewportView(ChatTextArea);

        CommandComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Poser", "Répondre", "Informer" }));
        CommandComboBox.setSelectedItem(null);
        CommandComboBox.setToolTipText("");
        CommandComboBox.setEnabled(false);
        CommandComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CommandComboBoxActionPerformed(evt);
            }
        });

        MessageTextField.setToolTipText("");
        MessageTextField.setEnabled(false);
        MessageTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MessageTextFieldKeyPressed(evt);
            }
        });

        EnvoyerButton.setText("Envoyer");
        EnvoyerButton.setEnabled(false);
        EnvoyerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EnvoyerButtonActionPerformed(evt);
            }
        });

        NumeroLabel.setText("Numéro de question:");
        NumeroLabel.setEnabled(false);

        NumeroTextField.setEnabled(false);

        IPTextField.setText("localhost");

        jLabel1.setText("Ip du serveur :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(EnvoyerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(CommandComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(MessageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(NumeroLabel)
                                .addGap(18, 18, 18)
                                .addComponent(NumeroTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 80, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ConnexionButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(IPTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ConnexionButton)
                    .addComponent(IPTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CommandComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MessageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EnvoyerButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NumeroTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NumeroLabel))
                .addGap(0, 13, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ConnexionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConnexionButtonActionPerformed
        if(ConnexionButton.getText().equals("Connexion")){
            connectTCP();
            
            ConnexionDialog cd = new ConnexionDialog(this, true);
            cd.setVisible(true);
            if(cd.isOk()){
                
                //crée une requete de type login
                LoginGroup lg = new LoginGroup();
                lg.setUser(cd.getUserTF());
                if(cd.isAgent()){ // si c'est un agent -> mot de passe -> digest
                lg.setPwd(cd.getPasswordTF().getBytes());
                }
                
                try
                {
                    oos.writeObject(lg);//envoi au serveur serIAChat
                    lg = (LoginGroup)ois.readObject();//récup réponse serIAChat
                }
                catch (IOException | ClassNotFoundException ex)
                {
                    System.out.println("ERREUR CLIENT (ConnexionButton oos/ois): " + ex);
                }
                                
                if(!lg.isAuthorized()){ //login refusé !
                    JOptionPane.showMessageDialog(this, "Connexion refusée", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
                else{ // login ok
                    user = lg.getUser();
                    portChat = lg.getPort();
                    try
                    {
                        socketGroup = new MulticastSocket(portChat);
                        addressGroup = InetAddress.getByName(lg.getIp());
                        socketGroup.joinGroup(addressGroup);
                        String message = "*** " + user + " s'est connecté au chat ***";
                        socketGroup.send(new DatagramPacket(message.getBytes(), message.getBytes().length, addressGroup, portChat));
                    }
                    catch (IOException ex)
                    {
                        System.out.println("ERREUR CLIENT (ConnexionButton multicast): " + ex);
                    }
                    thChat = new ThreadChat(this.ChatTextArea, this.socketGroup, portChat);
                    thChat.start();
                    
                    this.EnvoyerButton.setEnabled(true);
                    this.MessageTextField.setEnabled(true);
                    this.CommandComboBox.setEnabled(true);
                    this.NumeroLabel.setEnabled(true);
                    this.NumeroTextField.setEnabled(true);

                    connected = true;
                    ConnexionButton.setText("Déconnexion"); 
                }
            }
        }
        else{
            Disconnect();
        }
    }//GEN-LAST:event_ConnexionButtonActionPerformed

    private void EnvoyerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EnvoyerButtonActionPerformed
        if(connected){
            if(!MessageTextField.getText().equals("")){
                try{
                    String message;
                    if(CommandComboBox.getSelectedItem().equals("Répondre")){
                        if(!NumeroTextField.getText().isEmpty()){
                            message = "<" + user + " (r:" + NumeroTextField.getText() +
                                ")> " + MessageTextField.getText();
                            message = Digest(message);
                        }
                        else{// pas de numéro de réponse
                            return;
                        }
                    }
                    else if(CommandComboBox.getSelectedItem().equals("Poser")){
                        
                        int number = sr.nextInt(10000);
                        message = "<" + user + " (q:" + number + ")> " + MessageTextField.getText();
                        message = Digest(message);
                    }
                    else if(CommandComboBox.getSelectedItem().equals("Informer")){
                        message = "<" + user + "(info)> " + MessageTextField.getText();
                    }
                    else{ // combobox vide
                        return;
                    }
                    
                    socketGroup.send(new DatagramPacket(message.getBytes(), message.getBytes().length, addressGroup, portChat));
                    System.out.println("Message envoyé: " + message);

                }
                catch (IOException ex){
                    System.out.println("ERREUR CLIENT (EnvoyerButton): " + ex);
                }
                MessageTextField.setText("");
            }
        }
    }//GEN-LAST:event_EnvoyerButtonActionPerformed

    private void MessageTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MessageTextFieldKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            EnvoyerButton.doClick();
        }
    }//GEN-LAST:event_MessageTextFieldKeyPressed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if(connected){
            Disconnect();
        }  
        System.out.println("Fin de appIAChat");
    }//GEN-LAST:event_formWindowClosing

    private void CommandComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CommandComboBoxActionPerformed
        if(CommandComboBox.getSelectedItem().equals("Répondre")){
            this.NumeroLabel.setEnabled(true);
            this.NumeroTextField.setEnabled(true);
        }
        else {
            this.NumeroLabel.setEnabled(false);
            this.NumeroTextField.setEnabled(false);
        }
    }//GEN-LAST:event_CommandComboBoxActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(appIAChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(appIAChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(appIAChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(appIAChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new appIAChat().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea ChatTextArea;
    private javax.swing.JComboBox<String> CommandComboBox;
    private javax.swing.JButton ConnexionButton;
    private javax.swing.JButton EnvoyerButton;
    private javax.swing.JTextField IPTextField;
    private javax.swing.JTextField MessageTextField;
    private javax.swing.JLabel NumeroLabel;
    private javax.swing.JTextField NumeroTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    public void Disconnect(){
        try {
            // envoyer message déconnexion
            String message = "*** " + user + " a quitté le chat ***";
            
            socketGroup.send(new DatagramPacket(message.getBytes(), message.getBytes().length, addressGroup, portChat));
            socketGroup.leaveGroup(addressGroup);
            socketGroup.close();
            
            oos.close();
            ois.close();
            clientSocket.close();
            
        } catch (IOException ex) {
            System.out.println("ERREUR CLIENT (Disconnect): " + ex);
        }
                    
        //couper thread
        thChat.setRunning(false);
        
        this.EnvoyerButton.setEnabled(false);
        this.MessageTextField.setEnabled(false);
        this.CommandComboBox.setEnabled(false);
        this.NumeroLabel.setEnabled(false);
        this.NumeroTextField.setEnabled(false);
        ConnexionButton.setText("Connexion"); 
        connected = false;
    }
    
    private void connectTCP() {
        try
        {
            clientSocket = new Socket(IPTextField.getText(), portFly);
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(clientSocket.getInputStream());
        }
        catch (IOException ex)
        {
            System.out.println("ERREUR CLIENT (appIAChat): " + ex);
        }
    }

    private String Digest(String message){
        String messageComplet = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5", "BC");
            md.update(message.getBytes());
            byte[] digest = md.digest();                    
            messageComplet = Arrays.toString(digest) + "#" + message;
            
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            System.out.println("Erreur de création de digest: " +ex);
        }
        return messageComplet;
    }
}