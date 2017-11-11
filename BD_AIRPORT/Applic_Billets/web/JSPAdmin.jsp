<%-- 
    Document   : JSPAdmin
    Created on : 9 nov. 2017, 14:33:21
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
    <body style="background-repeat:no-repeat; background-size: 100% auto" background="https://www.walldevil.com/wallpapers/a89/web-background-wallpapers-blue.jpg">
        <!-- la belle barre toute blo sur le haut de la page-->
        <nav class="navbar navbar-inverse">
        <div class="container-fluid">
          <div class="navbar-header">
            <a class="navbar-brand" href="#">Inpres Airport administration page</a>
          </div>
        </nav>
        
  <div  class=pull-left>
    <div class="container"style="width: 500px;margin-right: auto; margin-left: auto;">
        <div class="jumbotron" style="background: transparent;" >
            <!-- les boutons -->
            <form action="ServletAdmin">
                <!-- Utilise une autre servlet -> ServletAdmin -->
                <label for="Login">(*) Login cible : </label>
                <input type="text" class="form-control" id="logincible" name ="login"style="max_width:200px"placeholder="Login">
                <label for="Password">Mot de passe associé : </label>
                <input type="text" class="form-control" id="logincible" name="password"style="max_width:200px"placeholder="Nouveau mot de passe">
                <br>
                <br>
                <button type="submit" name="bt" class="btn btn-warning" value="Delete">Supprimer</button>
                <button type="submit" name="bt" class="btn btn-warning" value="Modify">Modifier</button>
                <br>
                <%  if(request.getAttribute("errorMessage") != null) {  
                        if(request.getAttribute("errorMessage").equals("nopassword")) {%>
                            <font color ="red"> Il faut un mot de passe à modifier ! </font>
                   <%   }
                        else if(request.getAttribute("errorMessage").equals("sqlerror")) { %>
                            <font color ="red"> Une erreur SQL est apparue !</font>
                <%      }
                        else if(request.getAttribute("errorMessage").equals("badlogin")) { %>
                        <font color ="red"> Le login n'existe pas ! </font>
                    <%  } 
                    }
                    if(request.getAttribute("successMessage") != null) {
                        if(request.getAttribute("successMessage").equals("OKlogin")) { %>
                            <font color ="green"> Le login a bien été supprimé </font>
                <%      }else if(request.getAttribute("successMessage").equals("OKpassword")) {%>
                            <font color ="green"> Le mot de passe a bien été changé </font>
                <%      } 
                    }
                %>
                <br>
                <br>
                <br>
                <font color="black">Les champs marqués d'un (*) sont obligatoires</font>  
            </form>
        </div>
    </div>
  </div>
        <!-- Fonctionnement :
        Pour supprimer, on a juste besoin du login. Le login étant unique, c'est suffisant pour la requête.
        Pour modifier, on ne peut modifier que le mot de passe, pas le login. Si l'utilisateur clique sur
        modifier sans entrer de mot de passe -> Erreur, redirection avec message.
        -->
    </body>
</html>
