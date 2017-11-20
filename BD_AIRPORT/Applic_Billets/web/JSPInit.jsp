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
          <div class="navbar-header" style="font-family: Verdana, sans-serif;">
            <form method="post" action="ServletConnection" id="reload" style="display:inline;">
                <a class="navbar-brand" href="#" onClick="askReload()">Menu principal</a>
                <input type="hidden" value="reload" name="reload"/>
            </form>
            <form action="ServletConnection" id="reloadCaddie" style="display:inline;">
              <a class="navbar-brand" href="#" onClick="askReloadC()">Mon panier</a>
              <input type="hidden" value="reloadCaddie" name="reloadCaddie"/>
            </form>
            <a class="navbar-brand" href="#">Contact</a> <!-- Placeholder... -->
            <form method="post" action="ServletConnection" id="DC">
                <a class="navbar-brand" onClick="post()" href='#' style="position:absolute; right:10px;">Se déconnecter</a>
                <input type='hidden' value='disconnect' name='disconnect'/>
            </form>
            <script type="text/javascript">
                function post(){
                    var d = document.getElementById("DC");
                    d.submit();
                }
                
                function askReload(){
                    var d = document.getElementById("reload");
                    d.submit();
                }
                
                function askReloadC(){
                    var d = document.getElementById("reloadCaddie");
                    d.submit();
                }
            </script>
            <p style="color:white;"><br>Connecté en tant que <% out.println("<em style=\"color:#42ebe6;\">" + request.getAttribute("login") + "</em>"); %> </p>
          </div>
        </div>
        </nav>
        <form action="ServletConnection">
        <div class="ContainerVols">
        <%
            //On récupère la liste des vols à afficher
            ArrayList<String> listeVols = (ArrayList<String>)request.getAttribute("ListeVols");
            for(String str : listeVols){
                String[] infos = str.split(";");
                //On construit un container avec les infos du vol pour chaque vol
                out.println(""
                + "<div class=\"VolContainer\" style=\"float:left;\">  "
                + " <div class=\"VolInfos\" style=\"border: 4px black ridge; border-radius:10px; margin:auto; padding:10px; width:700px; background-image:url(vintage-concrete.png);\">  "
                + "     <h3 style=\"text-shadow: 1px 1px 1px black;\">Voyage en destination de "+ infos[0] +"</h3>" 
                + "     <p style=\"text-align:left;\"> Vol n°" + infos[1] +"</p>"
                + "     <p style=\"text-align:left;\"> Places disponibles : " + infos[2]
                + "     <span style=\"float:right; position:relative; font-size:16px; font-family:Impact, sans-serif;\">" + infos[3] + "€</span> </p>"
                + " </div>"
                + "</div>"
                + "<div class=\"UserInput\" style=\"float:right; text-align:left; margin:auto; width:500px;\">"
                    + "<label for=\"quantity\">Quantité voulue : </label>"
                    + "<input type=\"text\" class=\"form-control\" id=\"InputQuantity\" name=\"quantity\" style=\"max-width:200px;\" value=\"0\" placeholder=\"Laissez 0 si vous ne prenez rien.\">"
                + "</div>");
            }
        %>  
        </div>  
        
        <div class="ButtonRegion" style="float:right; text-align:left; margin: 45px auto; width:500px;"> 
            <button type="submit" class ="btn btn-warning">Ajouter au panier</button>
        </div>
        </form>
    </body>
</html>
