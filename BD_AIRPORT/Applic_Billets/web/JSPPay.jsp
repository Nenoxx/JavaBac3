<%-- 
    Document   : JSPPay
    Created on : 9 nov. 2017, 14:24:51
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
        <div class="FactureContainer" style="border: 4px #666666 double; box-shadow: 6px 6px 10px black; border-radius:10px; width: 800px; margin:auto; padding-bottom: 20px; background-image: url(vintage-concrete.png)">
            <form action="ServletConnection">
                <h1 style="font-family: Verdana; text-align: center;"> Adresse de facturation </h1>
                <label for="name" style="display:inline-box; padding: 20px">Nom : </label>
                <input type="name" class="form-control" id="NameInput" name="name" style="max-width: 200px; display:inline-block;" placeholder="Nom de famille">
                <label for="surname" style="display: inline-box; padding:10px;">Prenom : </label>
                <input type="surname" class="form-control" id="SurnameInput" name="surname" style="max-width: 200px; display:inline-block;" placeholder="Prénom">
                <br>
                <label for="street" style="padding:20px;">Rue : </label>
                <input type="street" class="form-control" id="StreetInput" name="street" style="max-width: 400px; display:inline-block;" placeholder="exemple : 5, rue de l'égalité">
                <br>
                <label for="town" style="padding:20px;">Commune : </label>
                <input type="town" class="form-control" id="TownInput" name="town" style="max-width: 300px; display:inline-block;" placeholder="Nom de la commune">
                <label for="postalCode" style="padding: 20px;">Code postal : </label>
                <input type="postalCode" class="form-control" id="PostalCodeInput" name="postalCode" style="max-width: 200px; display:inline-block;" placeholder="exemple : 4000">
                <br>
                <label for="password" style="padding:20px;">Confirmez votre mot de passe : </label>
                <input type="password" class="form-control" id="PasswordInput" name="password" style="max-width: 200px; display:inline-block;">
                <br>
                <button type="submit"class="btn btn-warning" style="width:100px; margin-left: 325px;">Payer</button> 
            </form>
        </div>
    </body>
</html>
