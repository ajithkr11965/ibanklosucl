<%--
  Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 28-06-2024
  Time: 10:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <title>SIB-LOS</title>

    <!-- Global stylesheets -->
    <link href="assets/fonts/inter/inter.css" rel="stylesheet" type="text/css">
    <link href="assets/icons/phosphor/styles.min.css" rel="stylesheet" type="text/css">
    <link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
    <link href="assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
    <!-- Theme JS files -->
    <script src="assets/js/vendor/notifications/sweet_alert.min.js"></script>
    <!-- /theme JS files -->
</head>
<body>
<input type="hidden" id="msg" value="${msg}">
<input type="hidden" id="msgtype" value="${msgtype}">
<input type="hidden" id="title" value="${title}">
<form action="${redurl}" id="backform" name="backform" method="POST"></form>
</form>
<script>
    $(document).ready(function () {

        if($('#redurl').val().length<0){
            $('#backform').attr('action',"dashboard");
            $('#backform').submit();
        }
        // Defaults
        const swalInit = swal.mixin({
            buttonsStyling: false,
            customClass: {
                confirmButton: 'btn btn-primary',
                cancelButton: 'btn btn-light',
                denyButton: 'btn btn-light',
                input: 'form-control'
            }
        });

        swalInit.fire({
            title: $('#title').val(),
            text: $('#msg').val(),
            icon: $('#msgtype').val(),
            showCloseButton: true
        }).then(function (result) {
                $('#backform').attr('action',$('#redurl').val());
                $('#backform').submit();
        });
    });
</script>
</body>
</html>
