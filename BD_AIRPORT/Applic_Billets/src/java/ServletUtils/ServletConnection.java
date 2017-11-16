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
                
                if(request.getParameter("disconnect") != null){
                    request.getSession().invalidate();
                    request.setAttribute("errorMessage", "disconnectOK");
                    this.getServletContext().getRequestDispatcher("/JSPConnection.jsp").forward(request, response);
                }
                
                //On récupère les attributs (cachés) envoyé après avoir appuyé sur "Connexion"
                String user = request.getParameter("login");
                String pass = request.getParameter("password");
                String newClient = request.getParameter("inscription"); //<- est null si le client n'a pas coché la checkbox
                try {
                    //Connexion à la BD MySQL
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection conn = MyDBUtils.MyConnection(1, "mich", "password"); //Compte ne pouvant faire que des select et insert
                    if(newClient == null) //Client déjà existant
                    {
                       PreparedStatement pst = conn.prepareStatement("Select login, password from AUTHENTICATION where login=? and password=?");
                       pst.setString(1, user);
                       pst.setString(2, pass);
                       ResultSet rs = pst.executeQuery();
                       if (rs.next()) {
                           //Si le resultset contient un tuple, c'est que le select a donné un résultat positif
                           out.println("Correct login credentials");
                           request.setAttribute("login", "");
                           request.setAttribute("password", "");
                           if(user.equals("admin")){
                               this.getServletContext().getRequestDispatcher("/JSPAdmin.jsp").forward(request, response);
                               
                           }else{
                               String query = "select destination, numVol, nbreBillets from VOLS;";
                               pst = conn.prepareStatement(query);
                               rs = pst.executeQuery();
                               ArrayList<String> list = new ArrayList<String>();
                               while(rs.next()){
                                   String row = rs.getString("destination") + ";" + rs.getString("numVol") + ";" + rs.getString("nbreBillets");
                                   list.add(row);
                               }
                               request.setAttribute("ListeVols", list);
                               this.getServletContext().getRequestDispatcher("/JSPInit.jsp").forward(request, response);
                           }
                       } 
                       else {
                           out.println("Incorrect login credentials");
                           //redirect sur la page de login avec message d'erreur
                           request.setAttribute("errorMessage", "badlogin");
                           this.getServletContext().getRequestDispatcher("/JSPConnection.jsp").forward(request, response);
                       }
                    }
                    else{
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
