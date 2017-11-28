/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServletUtils;

import databaseUtils.MyDBUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author nenoxx
 */

@WebServlet(
       name = "ServletConnection",
       displayName = "Connection handling Servlet",
       urlPatterns = "/ServletConnection"
)
public class ServletConnection extends HttpServlet{
    String[] quantities = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    HttpSession currentSession = null;
    ArrayList<String> Caddie = null;
    Connection conn = null;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                /* TODO output your page here. You may use following sample code. */
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>test</title>");            
                out.println("</head>");
                out.println("<body>");
                
                //On récupère les attributs (cachés) envoyé après avoir appuyé sur "Connexion"
                String user = request.getParameter("login");
                String pass = request.getParameter("password");
                
                currentSession = request.getSession();
                Caddie = (ArrayList<String>)currentSession.getAttribute("Caddie"); 
                //Pour ne pas écraser l'ancien caddie, on essaie de le récupérer s'il existe. Sinon = null.
                
                if(currentSession.getAttribute("login") == null){
                    //Ne doit normalement se faire qu'une seule fois par session.
                    //-> Pour la session, on enregistre le login et le password pour des nécessités ultérieures.
                    currentSession.setAttribute("login", user);
                    currentSession.setAttribute("password", pass);
                    InitTimerThread();
                }
                
                if(request.getParameter("PayRequest") != null){
                    //Calculer le montant total à payer
                    int Total = 0;
                    Total = CalculerTotal();
                    
                    request.setAttribute("Total", Total);
                    request.setAttribute("login", currentSession.getAttribute("login"));
                    this.getServletContext().getRequestDispatcher("/JSPPay.jsp").forward(request, response);
                }
                
                if(request.getParameter("disconnect") != null){
                    currentSession.setAttribute("Caddie", null);
                    RestoreTickets(Caddie);
                    String delete = "delete from PANIERS where User like ?";
                    try {
                        pst = getConnection().prepareStatement(delete);
                        pst.setString(1, (String)currentSession.getAttribute("login"));
                        pst.executeUpdate();
                    } catch (SQLException ex) {
                        System.out.println(ex.getLocalizedMessage());
                    }
                    
                    request.getSession().invalidate();
                    request.setAttribute("errorMessage", "disconnectOK");
                    this.getServletContext().getRequestDispatcher("/JSPConnection.jsp").forward(request, response);
                }

                String newClient = request.getParameter("inscription"); //<- est null si le client n'a pas coché la checkbox
                try {
                    //Connexion à la BD MySQL
                    Class.forName("com.mysql.jdbc.Driver");
                    conn = MyDBUtils.MyConnection(1, "mich", "password"); //Compte ne pouvant faire que des select, insert, update
                    
                    // --- INITIALISATION DU CADDIE ---
                    if((quantities = request.getParameterValues("quantity")) != null){
                        int i = 0;
                        
                        if(Caddie == null) //Cas où la session vient d'être créée
                            Caddie = new ArrayList<>();
                        
                        rs = MyDBUtils.MySelect("select * from VOLS", getConnection()); //Pas besoin de preparedStatement ici
                        while(rs.next()){
                            //On va regarder pour chaque vol disponible dans la table si l'utilisateur en a demandé ou non
                            //(La structure reçue en paramètre dans 'quantities' contient une valeur par champ input sur JSPInit)
                            
                            if(Integer.parseInt(quantities[i]) > 0 && 
                               Integer.parseInt(quantities[i]) <= Integer.parseInt(rs.getString("nbreBillets"))){
                                
                                //Cas où l'utilisateur a demandé au moins un billet pour cette destination
                                String rowCaddie = rs.getString("destination") + ";" + rs.getString("prix") + ";" + quantities[i] + ";" + rs.getString("numVol");
                                Caddie.add(rowCaddie);
                                System.out.println("Ajouté au caddie : "  + rs.getString("destination") + " x" + quantities[i]);
                                
                                //Et on met à jour la table afin de décrémenter le nombre maximum de billets.
                                //Billets réservés = promesse, quelqu'un d'autre ne doit pas pouvoir venir prendre les billets
                                //d'un autre dans son caddie, donc on met la BDD à jour ici.
                                String update = "update VOLS set nbreBillets = (nbreBillets - ?) where destination like ?";
                                pst = getConnection().prepareStatement(update);
                                pst.setString(1, quantities[i]);
                                pst.setString(2, rs.getString("destination"));
                                if((pst.executeUpdate()) > 0){
                                    System.out.println("Nombre de billets mit à jour correctement");
                                }
                            }
                            i++;
                        }
                        
                        //Une fois le caddie créé / initialisé, on l'enregistre dans la BD,
                        //on le donne en prochain paramètre à la requête
                        //Et on redirige vers la page de visionnement du Caddie.
                        System.out.println(UserNoCart());
                        if(UserNoCart()){
                            //L'utilisateur ne peut initier qu'un Caddie. Pour l'instant il doit terminer son caddie
                            //Avant d'en initier un autre -> Il ne pourra donc pas rajouter des objets... Pas le temps de gérer ça.
                            InsertCaddie(Caddie);
                            currentSession.setAttribute("Caddie", Caddie); //On le save quand même dans la session pour l'affichage
                            request.setAttribute("Caddie", Caddie);
                            request.setAttribute("login", currentSession.getAttribute("login"));
                            this.getServletContext().getRequestDispatcher("/JSPCaddie.jsp").forward(request, response);
                        }
                        else{
                            request.setAttribute("login", currentSession.getAttribute("login"));
                            request.setAttribute("message", "");
                            this.getServletContext().getRequestDispatcher("/JSPInit.jsp").forward(request, response);
                        }
                    }
                    
                    //Quand on clique sur l'onglet "Mon panier" de la barre bleue
                    if(request.getParameter("reloadCaddie") != null){ 
                        request.setAttribute("Caddie", currentSession.getAttribute("Caddie"));
                        request.setAttribute("login", currentSession.getAttribute("login"));
                        request.setAttribute("password", currentSession.getAttribute("password"));
                        this.getServletContext().getRequestDispatcher("/JSPCaddie.jsp").forward(request, response);
                    }
                    
                    //Quand on clique sur l'onglet "Menu principal" de la barre bleue
                    if(request.getParameter("reload") != null)
                    {
                        System.out.println("RELOAD REQUESTED");
                        ArrayList<String> list = PrepareMainPage(request, response);
                        request.setAttribute("ListeVols", list);
                        this.getServletContext().getRequestDispatcher("/JSPInit.jsp").forward(request, response);
                    }
                    
                    //2e étape du paiement, requête envoyée par JSPPay avec les infos du clients à enregistrer.
                    if(request.getParameter("PayProcess") != null){
                        String Total = "" + CalculerTotal();
                        if(request.getParameter("password").equals(currentSession.getAttribute("password"))){
                            //Comparaison du mot de passe ré-entré dans le formulaire de paiement avec celui stocké dans la session
                            if(UserNoCart() == false){ //L'utilisateur a un caddie (-> Checker en cas d'expiration !)
                                Caddie = (ArrayList<String>)currentSession.getAttribute("Caddie"); //Le caddie dans la DB est le même que celui en mémoire
                                for(String str : Caddie){
                                    String[] Values = str.split(";");
                                    for(int i = 0; i < Integer.parseInt(Values[2]) ; i++){//Générer 1 billet par quantité par destination.
                                        //1) Génération d'un billet
                                        String insertBillet = "insert into BILLETS(numBillet, numVol) values(?, ?)";
                                        String IDBillet = Values[3]+"-"+GenerateHexString();
                                        pst = getConnection().prepareStatement(insertBillet);
                                        //Values[3] vaut le champ numVol de la table Vols, enregistré dans le Caddie.
                                        pst.setString(1, IDBillet);
                                        pst.setString(2, Values[3]);
                                        
                                        //2) Générer la facture
                                        String insertFacture = "insert into FACTURES(nom, prenom, rue, commune, codePostal, paiement, IDBillet) values(?, ?, ?, ?, ?, ?, ?)";
                                        PreparedStatement pst2 = getConnection().prepareStatement(insertFacture);
                                        pst2.setString(1, request.getParameter("name"));
                                        pst2.setString(2, request.getParameter("surname"));
                                        pst2.setString(3, request.getParameter("street"));
                                        pst2.setString(4, request.getParameter("town"));
                                        pst2.setString(5, request.getParameter("postalCode"));
                                        pst2.setString(6, Total);
                                        pst2.setString(7, IDBillet);
                                        
                                        if(pst.executeUpdate() > 0){
                                            System.out.println("Billet généré et ajouté à la BD");
                                            if(pst2.executeUpdate() > 0){
                                                System.out.println("Facture générée et ajoutée à la BD");
                                                request.setAttribute("message", "payOK");
                                            
                                                //3) On supprime le caddie qui a été payé
                                                currentSession.setAttribute("Caddie", null);
                                                String delete = "delete from PANIERS where User like ?";
                                                pst = getConnection().prepareStatement(delete);
                                                pst.setString(1, (String)currentSession.getAttribute("login"));
                                                pst.executeUpdate();

                                                //Et on redirige vers JSPInit
                                                request.setAttribute("login", user);
                                                ArrayList<String> list = PrepareMainPage(request, response);
                                                request.setAttribute("ListeVols", list);
                                                this.getServletContext().getRequestDispatcher("/JSPInit.jsp").forward(request, response);

                                            }
                                            
                                        }
                                        else{
                                            request.setAttribute("message", "payNOK");
                                            this.getServletContext().getRequestDispatcher("/JSPInit.jsp").forward(request, response);
                                        }
                                    }
                                } 
                            }
                        }
                    }
                    
                    if(newClient == null) //Client déjà existant
                    {
                       pst = getConnection().prepareStatement("Select login, password from AUTHENTICATION where login=? and password=?");
                       pst.setString(1, user);
                       pst.setString(2, pass);
                       rs = pst.executeQuery();
                       if (rs.next()) {
                           //Si le resultset contient un tuple, c'est que le select a donné un résultat positif
                           out.println("Correct login credentials");
                           request.setAttribute("login", "");
                           request.setAttribute("password", "");
                           if(user.equals("admin")){ //Utilisateur admin -> Page d'administration spéciale
                               this.getServletContext().getRequestDispatcher("/JSPAdmin.jsp").forward(request, response);
                               
                           }else{ //Utilisateur normal
                               ArrayList<String> list = PrepareMainPage(request, response);
                               request.setAttribute("ListeVols", list);
                               this.getServletContext().getRequestDispatcher("/JSPInit.jsp").forward(request, response);
                           }
                       } 
                       else {
                           try{
                           out.println("Incorrect login credentials");
                           //redirect sur la page de login avec message d'erreur
                           request.setAttribute("errorMessage", "badlogin");
                           this.getServletContext().getRequestDispatcher("/JSPConnection.jsp").forward(request, response);
                           }
                           catch(IllegalStateException ex){
                               System.out.println("");
                           }
                       }
                    }
                    else{ //Nouveau client
                       String query = "select login from AUTHENTICATION where login like ?";
                       PreparedStatement prst = getConnection().prepareStatement(query);
                       prst.setString(1, user);
                       ResultSet rs = prst.executeQuery();
                       if(rs.next()){
                           //Le user existe déjà
                           request.setAttribute("errorMessage", "loginexists");
                           this.getServletContext().getRequestDispatcher("/JSPConnection.jsp").forward(request, response);
                       }
                       else{
                            //Création d'un nouvel utilisateur -> Insertion dans la table AUTHENTICATION
                            //des credentials.
                            PreparedStatement pst = getConnection().prepareStatement("insert into AUTHENTICATION values ('"+user+"','"+pass+"');");
                            out.println("<br/>");
                            int res = pst.executeUpdate();
                            if(res > 0){
                                //Nouveau client correctement créé -> On redirect comme pour une connexion normale
                                out.println("New user " + user + " was successfully created");
                                ArrayList<String> list = PrepareMainPage(request, response);
                                request.setAttribute("ListeVols", list);
                                this.getServletContext().getRequestDispatcher("/JSPInit.jsp").forward(request, response);
                            }
                            else
                                out.println("Error while creating new user " + user);
                       }
                    }
                } 
                catch (ClassNotFoundException | SQLException e) {
                    System.out.println("ERROR : " + e.getLocalizedMessage());
                }
               out.println("</body>");
               out.println("</html>");
        }
    }
    
    /*
    Fonction permettant d'aller chercher les informations qui nous intéresse dans la base de donnée
    afin de les afficher dans JSPInit.
    */
    private ArrayList<String> PrepareMainPage(HttpServletRequest request, HttpServletResponse response){
        String query = "select destination, numVol, nbreBillets, prix from VOLS;";
        ArrayList<String> list = null;
        try{
        request.setAttribute("login", currentSession.getAttribute("login"));
        request.setAttribute("password", currentSession.getAttribute("password"));
        pst = getConnection().prepareStatement(query);
        rs = pst.executeQuery();
        list = new ArrayList<>();
        while(rs.next()){
            String row = rs.getString("destination") + ";" + rs.getString("numVol") + ";" + rs.getString("nbreBillets") + ";" + rs.getString("prix");
            list.add(row);
        }
        }
        catch(Exception ex){
            System.out.println(ex.getLocalizedMessage());
        }
        
        return list;
    }
    
    private void InitTimerThread(){
        Thread CaddieChecker = new Thread() {
            public void run() {
                try {
                    Thread.sleep(60*1000);
                    String query = "select * from PANIERS";
                    ResultSet res = MyDBUtils.MySelect(query, getConnection());
                    while(res.next()){
                        Date CurrentDate, DateExpiration = res.getDate("DatePeremption");
                        CurrentDate = getCurrentDate();
                        if(CurrentDate.after(DateExpiration)){
                            //Caddie expiré ! Retirer et remettre les billets.
                            String RowCaddie = res.getString("Caddie"); //On récupère le caddie de la BD
                            ArrayList<String> OldCaddie = StringToCaddie(RowCaddie);
                            RestoreTickets(OldCaddie); //On remet les tickets préalablement enlevés
                            //Puis supprimer le caddie de la BD.
                            query = "delete from PANIERS where numID = " + res.getString("numID") + ";";
                            MyDBUtils.MyUpdate(query, getConnection());
                            System.out.println("TIMEOUT > Le caddie avec l'ID " + res.getString("numID") + " a bien été supprimé");
                        }
                    }
                    
                } catch (InterruptedException ex) {
                   System.out.println("Thread 'CaddieChecker' interrompu !");
                } catch (SQLException ex) {
                    System.out.println(ex.getLocalizedMessage());
                }
            }
        };
        
        CaddieChecker.start();
    }
    
    private int CalculerTotal(){
        int Total = 0;
        Caddie = (ArrayList<String>)currentSession.getAttribute("Caddie");
        for(String str : Caddie){
            String[] row = str.split(";");
            Total += (Integer.parseInt(row[1]) * Integer.parseInt(row[2]));
        }
        
        return Total;
    }
    
    //Converti le Caddie utilisé par la Servlet en Caddie pouvant être écrit dans la base de donnée
    private String CaddieToString(ArrayList<String> list){
        String ReturnValue = "";
        for(String str : list){
            ReturnValue += str;
            ReturnValue += "||"; //Séparateur du Caddie
        }
        return ReturnValue;
    }
    
    //Converti le Caddie lu de la base de donnée en Caddie utilisé par la servlet
    private ArrayList<String> StringToCaddie(String SQLRow){
        ArrayList<String> ReturnValue = new ArrayList<>();
        String[] Rows;
        Rows = SQLRow.split("||");
        
        int l = Rows.length;
        
        for(int i = 0; i<l; i++){
            ReturnValue.add(Rows[i]);
        }
        
        return ReturnValue;
    }
    
    private void InsertCaddie(ArrayList<String> NewCaddie) throws SQLException{
        if(UserNoCart()){
            String query = "insert into PANIERS(DateAjout, DatePeremption, User, Caddie) values(?, DATE_ADD(?, INTERVAL 30 MINUTE), ?, ?)";
            pst = getConnection().prepareStatement(query);
            pst.setDate(1, getCurrentDate());
            pst.setDate(2, getCurrentDate());
            pst.setString(3, (String)currentSession.getAttribute("login"));
            pst.setString(4, CaddieToString(NewCaddie));
            if(pst.executeUpdate() > 0){
                System.out.println("Insert> Caddie enregistré dans la BD");
            }
        }
    }
    
    private void RestoreTickets(ArrayList<String> OldCaddie){
        if(OldCaddie != null){
            for(String str : OldCaddie){
                String[] row = str.split(";");
                System.out.println("RestoreTickets > ROW : " + str);
                String update = "update VOLS set nbreBillets = (nbreBillets + ?) where destination like ?";
                try {
                    pst = getConnection().prepareStatement(update);
                    pst.setString(1, row[2]);
                    pst.setString(2, row[0]);

                    if((pst.executeUpdate()) > 0){
                        System.out.println("Nombre de billets mit à jour correctement");
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(ServletConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }   
    }
    
    //Regarde si l'utilisateur actuel a un caddie enregistré dans la BD ou non.
    private boolean UserNoCart(){
        String query = "select User from PANIERS where User like '" + (String)currentSession.getAttribute("login") + "';";
        try {
            ResultSet res = MyDBUtils.MySelect(query, getConnection());
            if(res.next())
                return false; //Faux, l'utilisateur a un caddie d'existant.
            else return true; //Vrai, l'utilisateur n'a pas de caddie déjà existant.
        } catch (SQLException ex) {
            Logger.getLogger(ServletConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    private synchronized java.sql.Date getCurrentDate(){ //Je précise que c'est un java.sql.Date et non pas un java.util.Date pour la lisibilité, sinon pas besoin
        Date CurrentDate = new Date(new java.util.Date().getTime()); //Convertir java.util.date en java.sql.date
        return CurrentDate;
    }
    
    private synchronized Connection getConnection(){
        return conn;
    }
    
    private String GenerateHexString(){
        Random rand = new Random();
        int RandValue = rand.nextInt(0xFFFFFF);
        String result = Integer.toHexString(RandValue);
        return result;
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
