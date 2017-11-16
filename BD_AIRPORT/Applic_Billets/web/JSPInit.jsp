<%-- 
    Document   : JSPSite
    Created on : 9 nov. 2017, 13:32:51
    Author     : nenoxx
--%>

<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <!-- permet d'aller chercher les ressources de bootstrap pour que ça soit plus joli -->
        <meta name="viewport" content="width=device-width,initial-scale=1">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.0/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>  
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootswatch/3.3.7/cerulean/bootstrap.min.css">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>INPRES AIRPORT</title>
    </head>
    <body style="background-repeat:no-repeat; background-size: 100% auto" background="stonehaven.png">
        <nav class="navbar navbar-inverse">
        <div class="container-fluid">
          <div class="navbar-header">
            <a class="navbar-brand" href="#">Menu principal</a>
            <a class="navbar-brand" href="JSPCaddie.jsp">Mon panier</a>
            <a class="navbar-brand" href="#">Contacts</a> <!-- Placeholder... -->
            <form method="post" action="ServletConnection" id="DC">
                <a class="navbar-brand" onClick="post()" href='#' style="position:absolute; right:10px;">Se déconnecter</a>
                <input type='hidden' value='disconnect' name='disconnect'/>
            </form>
            <script type="text/javascript">
                function post(){
                    var d = document.getElementById("DC");
                    d.submit();
                }
            </script>
            <p style="color:white;"><br>Connecté en tant que <% out.println("<em style=\"color:#42ebe6;\">" + request.getParameter("login") + "</em>"); %> </p>
          </div>
        </nav>
        <div class="ContainerVols">
        <%
            //On récupère la liste des vols à afficher
            ArrayList<String> listeVols = (ArrayList<String>)request.getAttribute("ListeVols");
            for(String str : listeVols){
                String[] infos = str.split(";");
                //On construit un container avec les infos du vol pour chaque vol
                out.println(""
                + "<div class=\"VolContainer\" style=\"float:left;\">  "
                + "<div class=\"VolInfos\" style=\"border: 4px black ridge; border-radius:10px; margin:auto; padding:10px; width:700px;\">  "
                + "     <h3>Voyage en destination de "+ infos[0] +"</h3>" 
                + "     <p style=\"text-align:left;\"> Vol n°" + infos[1] +"</p>"
                + "     <p style=\"text-align:right;\"> Nombre de billets :" + infos[2] + "</p>"
                + "</div>"
                + "</div>");
            }
        %>  
        </div>
        
    </body>
</html>
