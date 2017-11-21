<%-- 
    Document   : JSPCaddie
    Created on : 9 nov. 2017, 14:24:46
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
                //Trois fonctions basiques nous permettant de simuler l'appui
                //d'un bouton de type submit lorsqu'on clique sur le menu
                //de navigation (-> Requête de reload avec les bonnes infos à la servlet)
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
        <%
            //On récupère le caddie à afficher
            ArrayList<String> Caddie = (ArrayList<String>)request.getAttribute("Caddie");
            if(Caddie != null && !Caddie.isEmpty()){
                for(String str : Caddie){
                    String[] infos = str.split(";");
                    //On construit un container avec les infos du caddie
                    out.println(""
                    + "<div class=\"CaddieContainer\" style=\"float:left;\">  "
                    + " <div class=\"CaddieInfos\" style=\"border: 4px black ridge; border-radius:10px; margin:auto; padding:10px; width:700px; background-image:url(vintage-concrete.png);\">  "
                    + "     <h3 style=\"text-shadow: 1px 1px 1px black;\">Billets pour "+ infos[0] +" x" + infos[2] + "</h3>" 
                    + "     <p style=\"text-align:left;\"> Prix de base : " + infos[1] +"</p>"
                    + "     <p style=\"text-align:left;\"> Prix total : "
                    + "     <span style=\"float:right; position:relative; font-size:16px; font-family:Impact, sans-serif;\">" + Integer.parseInt(infos[1]) * Integer.parseInt(infos[2]) + "€</span> </p>"
                    + " </div>"
                    + "</div>");
                }
                out.println("<div class=\"ButtonRegion\" style=\"clear:both; text-align:left; padding:10px; width:500px;\">"); 
                    out.println("<button type=\"submit\">Passer à la caisse</button>");
                    out.println("<input type=\"hidden\" name=\"PayRequest\" value=\"PayRequest\"");
                out.println("</div>");
            }
            else { %>
            <p style="text-align: center; font-size: 20px; font-family: Verdana, sans-serif;"> On dirait bien que votre Caddie est vide ! Si vous aviez néanmoins passé une commande, vérifiez <br> 
                que le nombre de billets entré est correct, sinon la commande n'est pas prise en compte. </p>
            <% }
            out.println("</form>");
        %>
    </body>
</html>
