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
        <title>Inpres Airport</title>
    </head>
    <body style="background-repeat:no-repeat; background-size: 100% auto" background="http://www.flydayton.com/wp-content/uploads/2015/06/day-home-slide-1-updated.jpg">
        <!-- la belle barre toute blo sur le haut de la page-->
        <nav class="navbar navbar-inverse">
        <div class="container-fluid">
          <div class="navbar-header">
            <a class="navbar-brand" href="#">Inpres Airport</a>
          </div>
        </nav>

<!-- Container permettant de paramétrer le positionnement des objets -->
<div  class=pull-right>
    <div class="container"style="width: 500px;margin-right: auto; margin-left: auto;">
        <div class="jumbotron" style="background: transparent;" >
        
            <% if(request.getAttribute("errorMessage") != null) {  
                 if(request.getAttribute("errorMessage").equals("badlogin")) {%>
                    <font color ="red"> Le login ou le mot de passe est incorrect </font>
                <% } %>
        <% } %>
        <br/>
        
        <!-- les boutons -->
        <form action="ServletConnection">
            <label for="login">Login :</label>
            <input type="text" class="form-control" id="usr" name="login"style="max-width: 200px" placeholder="Entrez le login">
            <br>
            <label for="password">Password :</label>
            <input type="password" class="form-control" id="mdp" name="password"style="max-width: 200px" placeholder="Entrez le mot de passe">
            <br>
            <br>
            <button type="submit" class="btn btn-warning">Connexion</button>
            <label for="inscription">Inscription : </label>
            <input type="checkbox" name="inscription" value="Inscription" />
            </form>  
        </div>
    </div>
</div> 
    </body>
</html>
