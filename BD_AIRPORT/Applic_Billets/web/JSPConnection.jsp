    <%-- 
    Document   : JSPConnection
    Created on : 9 nov. 2017, 10:41:12
    Author     : nenoxx
--%>

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
    <body style="background-repeat:no-repeat; background-size: 100% auto" background="http://www.flydayton.com/wp-content/uploads/2015/06/day-home-slide-1-updated.jpg">
        <!-- la belle barre toute blo sur le haut de la page-->
        <nav class="navbar navbar-inverse">
        <div class="container-fluid">
          <div class="navbar-header" style="font-family:Verdana, sans-serif;">
            <a class="navbar-brand" href="JSPConnection.jsp">Inpres Airport</a> <!-- Pas très utile mais permet de faire disparaitre les messages d'infos-->
          </div>
        </nav>

<!-- Container permettant de paramétrer le positionnement des objets -->
<div class=pull-right>
    <div class="container"style="width: 500px;margin-right: auto; margin-left: auto;">
        <div class="jumbotron" style="background: transparent;" >
        
            <% if(request.getAttribute("errorMessage") != null) {  
                 if(request.getAttribute("errorMessage").equals("badlogin")) {%>
                    <font color ="red"> Le login ou le mot de passe est incorrect </font>
                    <% }
                if(request.getAttribute("errorMessage").equals("loginexists")){ %>
                    <font color ="red"> Ce compte existe déjà </font>
                <% } 
                if(request.getAttribute("errorMessage").equals("disconnectOK")){ %>
                    <font color ="green"> Vous avez bien été déconnecté </font>
                <% }
            } 
            request.setAttribute("errorMessage", null); %>
        <br/>
        
        <!-- les boutons -->
        <form action="ServletConnection">
            <label for="login">Login :</label>
            <input type="text" class="form-control" id="usr" name="login" style="max-width: 200px; display:inline-block; padding:auto; margin-left: 62px;" placeholder="Entrez le login">
            <br>
            <label for="password">Mot de passe :</label>
            <input type="password" class="form-control" id="mdp" name="password" style="max-width: 200px; display:inline-block; padding:auto; margin-left:10px;" placeholder="Entrez le mot de passe">
            <label for="inscription">Nouveau client : </label>
            <input type="checkbox" name="inscription" value="Inscription" />
            <br>
            <br>
            <button type="submit">Connexion</button>      
            </form>  
        </div>
    </div>
</div> 
    </body>
</html>
