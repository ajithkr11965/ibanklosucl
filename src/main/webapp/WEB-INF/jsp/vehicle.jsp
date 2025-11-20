<%@ page import="java.util.List" %>
<%@ page import="com.sib.ibanklosucl.model.VLVehicle" %><%--
  Created by IntelliJ IDEA.
  User: SIBL11965
  Date: 10-05-2024
  Time: 14:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Vehicle List</title>
</head>
<body>
    <h1>Vehicle List</h1>
    <table border="1">
        <thead>
            <tr>
                <th>Winum</th>
                <th>Applid</th>
                <th>Dealer State</th>
                <th>Dealer City</th>
                <!-- Add more headers for other fields as needed -->
            </tr>
        </thead>
        <tbody>
            <%
                List<VLVehicle> vehicleList = (List<VLVehicle>) request.getAttribute("vehicles");
                for (VLVehicle vehicle : vehicleList) { %>
                <tr>
                    <td><%= vehicle.getWinum() %></td>
                    <td><%= vehicle.getApplid() %></td>
                    <td><%= vehicle.getDealer_state() %></td>
                    <td><%= vehicle.getDealer_city() %></td>
                    <!-- Add more cells for other fields as needed -->
                </tr>
            <% } %>
        </tbody>
    </table>
</body>
</html>


