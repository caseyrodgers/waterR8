<!DOCTYPE html>
<html lang="en">
<!-- InstanceBegin template="/Templates/simple.dwt" codeOutsideHTMLIsLocked="false" -->
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<!-- InstanceBeginEditable name="doctitle" -->
<title>Water R8 Administration Help</title>
<!-- InstanceEndEditable -->
<!-- Bootstrap core CSS -->
<link href="libs/bootstrap/bootstrap-3.3.2-dist/css/bootstrap.css" rel="stylesheet" >

<!-- Custom styles for this template -->
<link href="assets/css/dashboard.css" rel="stylesheet">
<link href="libs/bootstrap/bootstrap-3.3.2-dist/css/bootstrap-theme.min.css"  rel="stylesheet">
<!-- InstanceBeginEditable name="head" -->
<%@page import="java.util.List"%>
<%@page import="org.apache.shiro.subject.Subject"%>
<%@page import="org.apache.shiro.authc.Account"%>
<%@page import="com.waterR8.util.ShiroStormCastUser"%>
<%@page import="org.apache.shiro.subject.PrincipalCollection"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="include.jsp"/>
<script src='js/live.js'></script>

<style>
    .main-panel {
        margin-top: 20px;
    }
</style>
<!-- InstanceEndEditable -->
</head>

<body>
<nav class="navbar navbar-inverse navbar-fixed-top">
  <div class="container-fluid">
    <div class="navbar-header"> <a class="navbar-brand" href="#">WaterR8 Administration Console</a> </div>
  </div>
</nav>
<div class='container'> <!-- InstanceBeginEditable name="MainBody" -->
  <div class='main-panel panel panel-default'>
  <div class='panel-heading'>Welcome To WaterR8 Administration Console</div>
   <div class="panel-body">
      <shiro:notAuthenticated>
     <%
        Cookie loginCookie = new Cookie("role", "");
        loginCookie.setMaxAge(0);
        response.addCookie(loginCookie);
        
        String redirectURL = "/login.jsp";
        response.sendRedirect(redirectURL);
      %>
     </shiro:notAuthenticated>
   
    <shiro:hasRole name="admin">
      <%
        Cookie loginCookie = new Cookie("role", "admin");
        loginCookie.setMaxAge(60*60*24);
        response.addCookie( loginCookie );
        
        String redirectURL = "/companies.html";
        response.sendRedirect(redirectURL);
      %>
   </shiro:hasRole>
   <shiro:hasRole name="ventura_investments">
      <%
        Cookie loginCookie = new Cookie("role", "");
        loginCookie.setMaxAge(0);
        response.addCookie(loginCookie);
        
        String redirectURL = "/company-complex.html?id=6";
        response.sendRedirect(redirectURL);
      %>
    </shiro:hasRole>
  </div>
  </div>
  <!-- InstanceEndEditable --> </div>
<!-- Bootstrap core JavaScript
    ================================================== --> 
<!-- Placed at the end of the document so the pages load faster --> 
<script src='libs/jquery/jquery-1.11.0.min.js'></script> 
<script src='libs/bootstrap/bootstrap-3.3.2-dist/js/bootstrap.min.js'></script> 
<script src='libs/knockout/knockout-3.2.0.min.js'></script> 
<script src='js/wr8.js'></script>
</body>
<!-- InstanceEnd -->
</html>
