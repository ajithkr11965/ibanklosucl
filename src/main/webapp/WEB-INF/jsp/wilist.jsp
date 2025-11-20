<%--
  Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 01-06-2024
  Time: 17:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
  <title>Add/Modify Example</title>
</head>
<body>
<h2>Add/Modify Entry</h2>
<form action="wicreate" method="post">
  <input type="hidden" name="slno" value="${not empty slno ? slno : ''}"/>
  <input type="submit" name="action" value="Add" onclick="this.form.slno.value=''">
  <input type="submit" name="action" value="Modify" onclick="this.form.slno.value='60'">
<%--  <input type="submit" name="action" value="Modify" onclick="this.form.slno.value='34'">--%>
</form>
</body>
</html>

