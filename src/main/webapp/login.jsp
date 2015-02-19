
<!DOCTYPE html>
<%@page import="java.util.logging.Logger"%>
<html lang="en"><!-- InstanceBegin template="/Templates/simple.dwt" codeOutsideHTMLIsLocked="false" -->
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <!-- InstanceBeginEditable name="doctitle" -->
<title>Water R8 Administration Login</title>
<!-- InstanceEndEditable -->
    <!-- Bootstrap core CSS -->
    <link href="libs/bootstrap/bootstrap-3.3.2-dist/css/bootstrap.css" rel="stylesheet" >

    <!-- Custom styles for this template -->
    <link href="assets/css/dashboard.css" rel="stylesheet">
    <link href="libs/bootstrap/bootstrap-3.3.2-dist/css/bootstrap-theme.min.css"  rel="stylesheet">
    <!-- InstanceBeginEditable name="head" -->
<jsp:include page="include.jsp"/>
<style>
   
   .login-panel {
	   margin-top:35px;
   }
   
   .error-message {
      font-size: 1.1em;
      font-weight: bold;
   }

</style>

<%
    String errorMessage =(String) request.getAttribute("shiroLoginFailure");
    Logger.getGlobal().warning("Error logging in: " + errorMessage);
%>
<!-- InstanceEndEditable -->
  </head>

  <body>

    <nav class="navbar navbar-inverse navbar-fixed-top">
      <div class="container-fluid">
        <div class="navbar-header">
 
          <a class="navbar-brand" href="#">WaterR8 Administration Console</a>
        </div>

      </div>
    </nav>
    <div class='container'>
    <!-- InstanceBeginEditable name="MainBody" -->
<div class="row">
  <div class="col-md-4 col-md-offset-4">
    <div class="login-panel panel panel-default">
      <div class="panel-heading">
        <h3 class="panel-title">Sign into WaterR8</h3>
      </div>
      <div class="panel-body">
        <form name="loginform" action="" method="POST" accept-charset="UTF-8" role="form">
          <fieldset>
            <div class="form-group">
              <input class="form-control" placeholder="Username or Email" name="username" type="text">
            </div>
            <div class="form-group">
              <input class="form-control" placeholder="Password" name="password" type="password" value="">
            </div>
            <div class="checkbox">
              <label>
                <input name="rememberMe" type="checkbox" value="true">
                Remember Me </label>
            </div>
            <input class="btn btn-lg btn-success btn-block" type="submit" value="Login">
          </fieldset>
        </form>
    <%
        if(errorMessage != null) {
        	%>
        	    <div class="alert alert-warning" role="alert">
			        Could not sign you into WaterR8.  Please try again or contact your administrator.
			     </div>
			<%
        }
    %>
      
        
      </div>
    </div>
  </div>
</div>
<!-- InstanceEndEditable -->
     </div>
    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
	<script src='libs/jquery/jquery-1.11.0.min.js'></script>
    <script src='libs/bootstrap/bootstrap-3.3.2-dist/js/bootstrap.min.js'></script>
	<script src='libs/knockout/knockout-3.2.0.min.js'></script>
	<script src='js/wr8.js'></script>
	
  </body>
<!-- InstanceEnd --></html>


