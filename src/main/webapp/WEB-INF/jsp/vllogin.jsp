<%--
  Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 29-06-2024
  Time: 13:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="los" uri="http://www.siblos.com/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Loading...</title>
    <script src="assets/js/jquery/jquery.min.js"></script>
</head>
<body>
<input type="hidden" id="loadinit-aw">
<los:loader/>
<script>
 window.onload= function(){
        showLoader();
     localStorage.setItem('vl-logout-event',Date.now().toString())
     var timer=setTimeout(function()
     {
        // hideLoader();
         clearTimeout(timer);
        location.href="dashboard";
     },3000);
    };

</script>
</body>
</html>
