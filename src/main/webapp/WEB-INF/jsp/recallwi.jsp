<%--
  Created by IntelliJ IDEA.
  User: SIBL12134
  Date: 02-08-2024
  Time: 18:21
  To change this template use File | Settings | File Templates.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.VehicleLoanMasterDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.VehicleLoanApplicantDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.AllotmentDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.UserSelectDTO" %>
<%@ taglib prefix="los" uri="http://www.siblos.com/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en" data-bs-theme="light">
<head>
    <meta charset="UTF-8">
    <title>Entry Queue</title>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="_csrf" content="">
    <meta name="_csrf_header" content="">
    <!-- Global stylesheets -->
    <%--		<link href="assets/fonts/inter/inter.css" rel="stylesheet" type="text/css">--%>
    <%--		--%>
    <link href="assets/icons/phosphor/styles.min.css" rel="stylesheet" type="text/css">
    <link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
    <link href="assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
    <%--		<link href="assets/css/custom/dashboard.css" rel="stylesheet" type="text/css">--%>
    <!-- /global stylesheets -->
    <!-- Core JS files -->
    <link href="assets/css/style.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
    <link href="assets/plugins/global/plugins.bundle.prefixed.css" rel="stylesheet" type="text/css"/>

    <style>

        .asidecheck {
            position: fixed;
            top: 105px;
            bottom: 30px;
            left: 30px;
            z-index: 100;
            overflow: hidden;
            width: 300px;
            border-radius: 1.75rem;
        }

        .table-shadow
        {
            box-shadow: rgba(0, 0, 0, 0.05) 0px 6px 24px 0px, rgba(17, 17, 17, 0.05) 0px 0px 0px 1px !important;
        }
        .bodycolor
        {
            background-color: #fff !important;
        }

    </style>

    <script src="assets/js/jquery/jquery.min.js"></script>
    <script src="assets/demo/demo_configurator.js"></script>
    <script src="assets/js/bootstrap/bootstrap.bundle.min.js"></script>
    <!-- /core JS files -->
    <!-- Theme JS files -->
    <script src="assets/js/vendor/visualization/d3/d3.min.js"></script>
    <script src="assets/js/vendor/visualization/d3/d3_tooltip.js"></script>

    <script src="assets/js/scripts.bundle.js"></script>
    <script src="assets/plugins/global/plugins.bundle.js"></script>
    <!--end::Vendors Javascript-->
    <!--begin::Custom Javascript(used for this page only)-->
    <script src="assets/js/custom/dashboard.js"></script>
    <script src="assets/js/vendor/forms/selects/select2.min.js"></script>
    <script src="assets/demo/pages/form_select2.js"></script>

    <script src="assets/js/app.js"></script>
    <script src="assets/js/vendor/notifications/sweet_alert.min.js"></script>

    <script src="assets/js/custom/recallwi.js"></script>

</head>

<body id="kt_body" class="bodycolor">
<los:loader/>
<jsp:include page="header.jsp"/>
<div class="page-header">
    <div class="page-header-content d-lg-flex">
        <div class="kt">
            <div class="border border-gray-300 border-dashed rounded min-w-125px py-3 px-4 me-0 ms-3 mb-3 mt-1">
                <!--begin::Number-->
                <div class="d-flex align-items-center mt-2 mb-2">
                    <div class="d-flex flex-center w-25px h-25px rounded-3 bg-light-primary bg-opacity-90 me-3">
                        <i class="ki-duotone ki-abstract-26 text-primary fs-3x"><span class="path1"></span><span class="path2"></span></i>                </div>
                    <div data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1" class="fs-8  fw-bold counted ms-2">Allotment Page</div>
                </div>
                <!--end::Number-->
                <!--begin::Label-->
                <!--end::Label-->
            </div>
        </div>

        <div class="collapse d-lg-block my-lg-auto ms-lg-auto" id="page_header1">
        </div>
    </div>
</div>
<div class="page-content pt-0">
    <%request.setAttribute("TAB","RECALLWI");%>
    <jsp:include page="sidebar.jsp"/>

    <!-- /main sidebar -->
    <!-- Main content -->
    <div class="content-wrapper">
        <div class="content">
            <div class="row">


                <div  id="kt_wrapper" class="kt">
                    <div class="content d-flex flex-column flex-column-fluid" id="kt_content">
                        <div class="col-xl-12">
                            <div class="card card-xl-stretch mb-5 mb-xl-8 table-shadow">
                                <div class="card-header border-0 pt-5">
                                    <div class="card-title">
                                        <!--begin::Search-->
                                        Recall WI from RBCPC Checker to DO queue
                                        <!--end::Search-->
                                        <!--begin::Export buttons-->

                                        <!--end::Export buttons-->
                                    </div>
                                    <div class="card-toolbar flex-row-fluid justify-content-end gap-5">

                                        <!--begin::Filter-->

                                        <!--end::Filter-->
                                        <!--begin::Export dropdown-->

                                        <!--end::Export dropdown-->
                                    </div>
                                </div>
                                <div class="card-body py-3">
                                    <div class="row mb-3">
                                        <label class="col-form-label col-sm-3" for="winum">WI Number:</label>
                                        <div class="col-sm-3">
                                            <input type="text" id="winum" placeholder="Enter WI" class="form-control col-sm-3">
                                        </div>

                                    </div>
                                    <div class="row mb-3">
                                    <label class="col-form-label col-sm-3" for="remarks">Remarks:</label>
                                    <div class="col-sm-3">
                                        <input type="text" id="remarks" placeholder="Enter remarks" class="form-control col-sm-3">
                                    </div>
                                    <div class="col-sm-3">
                                        <a href="#" class="btn btn-primary" id="recallbtn">Recall</a>
                                    </div>
                                </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>

</div>

<jsp:include page="footer.jsp"/>

</body>
</html>