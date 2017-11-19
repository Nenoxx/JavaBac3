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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
public class ServletConnection extends HttpServlet {
    String[] quantities = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    HttpSession currentSession = null;
    ArrayList<String> Caddie = null;
    
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
                    currentSession.setMaxInactiveInterval(3600); //Après X temps d'inactivité (en secondes) -> session détruite.
                    // ---> Le contenu du Caddie sera donc détruit aussi ! Permet de faire un timeout.
                }
                
                if(request.getParameter("disconnect") != null){
                    request.setAttribute("disconnect", null);
                    request.getSession().invalidate();
                    request.setAttribute("errorMessage", "disconnectOK");
                    this.getServletContext().getRequestDispatcher("/JSPConnection.jsp").forward(request, response);
                }

                String newClient = request.getParameter("inscription"); //<- est null si le client n'a pas coché la checkbox
                try {
                    //Connexion à la BD MySQL
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection conn = MyDBUtils.MyConnection(1, "mich", "password"); //Compte ne pouvant faire que des select et insert
                    
                    // --- INITIALISATION DU CADDIE ---
                    if((quantities = request.getParameterValues("quantity")) != null){;
                        int i = 0;
                        
                        if(Caddie == null)
                            Caddie = new ArrayList<>();
                        
                        request.setAttribute("quantity", null); //Histoire de reset la valeur à chaque fois.
                        rs = MyDBUtils.MySelect("select * from VOLS", conn);
                        while(rs.next()){
                            
                            if(Integer.parseInt(quantities[i]) > 0){
                                String rowCaddie = rs.getString("destination") + ";" + rs.getString("prix") + ";" + quantities[i];
                                Caddie.add(rowCaddie);
                                System.out.println("Ajouté au caddie : "  + rs.getString("destination") + " x" + quantities[i]);
                            }
                            
                            i++;
                        }
                        
                        currentSession.setAttribute("Caddie", Caddie);
                        request.setAttribute("Caddie", Caddie);
                        request.setAttribute("login", currentSession.getAttribute("login"));
                        request.setAttribute("password", currentSession.getAttribute("password"));
                        this.getServletContext().getRequestDispatcher("/JSPCaddie.jsp").forward(request, response);
                    }
                    
                    if(request.getParameter("reloadCaddie") != null){ 
                        request.setAttribute("Caddie", currentSession.getAttribute("Caddie"));
                        request.setAttribute("login", currentSession.getAttribute("login"));
                        request.setAttribute("password", currentSession.getAttribute("password"));
                        this.getServletContext().getRequestDispatcher("/JSPCaddie.jsp").forward(request, response);
                    }
                    
                    if(request.getParameter("reload") != null) //Le client a cliqué sur "Menu principal"
                    {
                        System.out.println("RELOAD REQUESTED");
                        request.setAttribute("reload", null);
                        ArrayList<String> list = PrepareMainPage(request, response, conn);
                        request.setAttribute("ListeVols", list);
                        this.getServletContext().getRequestDispatcher("/JSPInit.jsp").forward(request, response);
                    }
                    
                    if(newClient == null) //Client déjà existant
                    {
                       pst = conn.prepareStatement("Select login, password from AUTHENTICATION where login=? and password=?");
                       pst.setString(1, user);
                       pst.setString(2, pass);
                       rs = pst.executeQuery();
                       if (rs.next()) {
                           //Si le resultset contient un tuple, c'est que le select a donné un résultat positif
                           out.println("Correct login credentials");
                           request.setAttribute("login", "");
                           request.setAttribute("password", "");
                           if(user.equals("admin")){
                               this.getServletContext().getRequestDispatcher("/JSPAdmin.jsp").forward(request, response);
                               
                           }else{
                               ArrayList<String> list = PrepareMainPage(request, response, conn);
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
                               System.out.println("IllegalStateException -> Aucune idée de pourquoi.");
                           }
                       }
                    }
                    else{ //Nouveau client
                       String query = "select login from AUTHENTICATION where login like ?";
                       PreparedStatement prst = conn.prepareStatement(query);
                       prst.setString(1, user);
                       ResultSet rs = prst.executeQuery();
                       if(rs.next()){
                           //Le user existe déjà
                           request.setAttribute("errorMessage", "loginexists");
                           this.getServletContext().getRequestDispatcher("/JSPConnection.jsp").forward(request, response);
                       }
                       else{
                            PreparedStatement pst = conn.prepareStatement("insert into AUTHENTICATION values ('"+user+"','"+pass+"');");
                            out.println("<br/>");
                            int res = pst.executeUpdate();
                            if(res > 0){
                                //Nouveau client correctement créé -> On redirect comme pour une connexion normale
                                out.println("New user " + user + " was successfully created");
                                ArrayList<String> list = PrepareMainPage(request, response, conn);
                                request.setAttribute("ListeVols", list);
                                this.getServletContext().getRequestDispatcher("/JSPInit.jsp").forward(request, response);
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
    
    private ArrayList<String> PrepareMainPage(HttpServletRequest request, HttpServletResponse response, Connection conn){
        String query = "select destination, numVol, nbreBillets, prix from VOLS;";
        ArrayList<String> list = null;
        try{
        request.setAttribute("login", currentSession.getAttribute("login"));
        request.setAttribute("password", currentSession.getAttribute("password"));
        pst = conn.prepareStatement(query);
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
