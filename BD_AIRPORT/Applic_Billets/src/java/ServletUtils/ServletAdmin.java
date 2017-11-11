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
       name = "ServletAdmin",
       displayName = "Admin privileges Servlet",
       urlPatterns = "/ServletAdmin"
)

public class ServletAdmin extends HttpServlet {

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
            out.println("<title>Servlet ServletAdmin</title>");            
            out.println("</head>");
            out.println("<body>");
                String login = request.getParameter("login");
                String pass = request.getParameter("password");
                try{
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection conn = MyDBUtils.MyConnection(1, "root", "root"); //compte admin
                    if((request.getParameter("bt")).equals("Delete")){
                        String query = "delete from AUTHENTICATION where login like ?;";
                        PreparedStatement pst = conn.prepareStatement(query);
                        pst.setString(1, login);
                        int res = pst.executeUpdate();
                        if(res > 0){ //1 ligne a été affectée -> OK
                           request.setAttribute("successMessage", "OKlogin");
                           this.getServletContext().getRequestDispatcher("/JSPAdmin.jsp").forward(request, response);
                        }
                        else{ //Vu que le login est la seule variable, ça ne peut qu'être la cause de l'échec.
                           request.setAttribute("errorMessage", "badlogin");
                           this.getServletContext().getRequestDispatcher("/JSPAdmin.jsp").forward(request, response);
                        }
                    }
                    else{ // -> equals "Modify"
                        if(pass == null || pass.isEmpty()){
                           request.setAttribute("errorMessage", "nopassword");
                           this.getServletContext().getRequestDispatcher("/JSPAdmin.jsp").forward(request, response);
                        }
                        else{
                            String query = "update AUTHENTICATION set password = ? where login like ?;";
                            PreparedStatement pst = conn.prepareStatement(query);
                            pst.setString(1, pass);
                            pst.setString(2, login);
                            int res = pst.executeUpdate();
                            if(res > 0){
                                request.setAttribute("successMessage", "OKpassword");
                                this.getServletContext().getRequestDispatcher("/JSPAdmin.jsp").forward(request, response);
                            }
                            else{
                                request.setAttribute("errorMessage", "sqlerror");
                                this.getServletContext().getRequestDispatcher("/JSPAdmin.jsp").forward(request, response);
                            }
                        }
                    }
                }
                catch(Exception ex){
                    System.out.println(ex.getLocalizedMessage());
                    request.setAttribute("errorMessage", "sqlerror");
                    this.getServletContext().getRequestDispatcher("/JSPAdmin.jsp").forward(request, response);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
