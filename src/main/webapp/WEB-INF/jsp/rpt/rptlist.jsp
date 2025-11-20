<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="java.util.Optional" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.QueueCountDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.lang.reflect.Field" %>
<%@ page import="com.sib.ibanklosucl.dto.MenuList" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Comparator" %>

<!DOCTYPE html>
<html lang="en" dir="ltr" class="custom-scrollbars">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="_csrf" content="">
    <meta name="_csrf_header" content="">
    <title>SIB-LOS Reports</title>
    <!-- Global stylesheets -->
    <link href="assets/fonts/inter/inter.css" rel="stylesheet" type="text/css">
    <link href="assets/icons/phosphor/styles.min.css" rel="stylesheet" type="text/css">
    <link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
    <link href="assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
    <link href="assets/css/custom/dashboard.css" rel="stylesheet" type="text/css">
    <link href="assets/css/style.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
    <link href="assets/plugins/global/plugins.bundle.prefixed.css" rel="stylesheet" type="text/css"/>

    <style>
        :root {
            --primary-color: #ff0000;
            --secondary-color: #6c757d;
            --success-color: #28a745;
            --warning-color: #ffc107;
            --danger-color: #dc3545;
            --light-color: #f8f9fa;
            --dark-color: #343a40;
        }
        .searchBox {
            display: flex;
            max-width: 230px;
            align-items: center;
            justify-content: space-between;
            gap: 8px;
            background: #09092d;
            border-radius: 50px;
            position: relative;
        }

        .searchButton {
            color: white;
            position: absolute;
            right: 8px;
            width: 33px;
            height: 33px;
            border-radius: 50%;
            background: var(--gradient-2, linear-gradient(90deg, #B4405C 0%, #0C529D 100%));
            border: 0;
            display: inline-block;
            transition: all 300ms cubic-bezier(.23, 1, 0.32, 1);
        }
        /*hover effect*/
        button:hover {
            color: #fff;
            background-color: #1A1A1A;
            box-shadow: rgba(0, 0, 0, 0.5) 0 10px 20px;
            transform: translateY(-3px);
        }
        /*button pressing effect*/
        button:active {
            box-shadow: none;
            transform: translateY(0);
        }

        .searchInput {
            border: none;
            background: none;
            outline: none;
            color: white;
            font-size: 15px;
            padding: 24px 46px 24px 26px;
        }
        body {
            background-color: var(--light-color);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .navbar {
            background-color: var(--dark-color);
        }
        .navbar-brand {
            font-weight: bold;
            color: #ffffff;
        }
        .messagescroll {
            overflow-y: auto;
            scrollbar-width: none;
            -ms-overflow-style: none;
            height:30vh;
        }
        .marquee1 {
        // top: 11em;
            position: relative;
            box-sizing: border-box;
            animation: marquee1 30s linear infinite;
            margin: auto 5px;
        }
        /*.card {*/
        /*	border: none;*/
        /*	box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);*/
        /*	border-radius: 10px;*/
        /*	transition: transform 0.3s ease;*/
        /*	background-color: #ffffff;*/
        /*	margin-bottom: 20px;*/
        /*}*/
        /*.card:hover {*/
        /*	transform: translateY(-5px);*/
        /*}*/
        .status {
            text-decoration: none;
            color: inherit;
            padding: 15px;
            border-radius: 10px;
            transition: background-color 0.3s;
        }
        .status:hover {
            background-color: rgba(0, 123, 255, 0.1);
        }
        .status i {
            font-size: 2rem;
            color: var(--primary-color);
        }
        .status-count {
            font-size: 1.5rem;
            font-weight: bold;
        }
        .status-label {
            font-size: 0.875rem;
            color: var(--secondary-color);
        }
        .news-feed, .notifications {
            background-color: var(--light-color);
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
        }
        .news-feed h5, .notifications h5 {
            margin-bottom: 15px;
            font-weight: bold;
            color: var(--dark-color);
        }
        .news-feed ul, .notifications ul {
            list-style-type: none;
            padding-left: 0;
        }
        .news-feed li, .notifications li {
            margin-bottom: 15px;
            padding: 10px;
            border-bottom: 1px solid var(--secondary-color);
        }
        .news-feed li:last-child, .notifications li:last-child {
            border-bottom: none;
        }
        .progress-tracking {
            margin-top: 20px;
            padding: 20px;
            background-color: var(--light-color);
            border-radius: 10px;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
        }
        .progress-tracking h5 {
            margin-bottom: 15px;
            font-weight: bold;
            color: var(--dark-color);
        }
        .progress-tracking .progress-bar {
            height: 20px;
            border-radius: 10px;
        }
        .dashboard .row {
            margin-bottom: 20px;
        }
        .dashboard .col-md-6, .dashboard .col-md-4, .dashboard .col-md-8 {
            margin-bottom: 20px;
        }
        .dashboard .col-md-6:last-child, .dashboard .col-md-4:last-child, .dashboard .col-md-8:last-child {
            margin-bottom: 0;
        }


        .scroll-container
        {
            height:328px;
            overflow: hidden;
            position: relative;
        }
        .scroll-content
        {
            position: absolute;
            animation: marquee1 20s linear infinite;
            animation-play-state: running;

        }
        .scroll-container:hover .scroll-content
        {
            animation-play-state: paused;
        }
        @Keyframes scrollSeuqential
        {
            0%
            {
                transform:translateY(100%);
            }
            100%
            {
                transform:translateY(-100%);
            }
        }

        @keyframes marquee1 {

            0%   { -webkit-transform: translate(0, 70%); }
            100% { -webkit-transform: translate(0, -100%); }
        }


    </style>
</head>
<body>
<%
    Employee employee = new Employee();
    List<MenuList> sortmenuList=new ArrayList<>();
    if (request.getAttribute("employee") != null) {
        employee = (Employee) request.getAttribute("employee");
        List<MenuList> menuList = (List<MenuList>) request.getAttribute("menuList");
       sortmenuList= menuList.stream().filter(t->!t.getMenuID().equals("RPT") && t.getMenuID().startsWith("RPT")).sorted(Comparator.comparingLong(MenuList::getOrderid)).toList();
    }
%>

<!-- Main navbar -->
<jsp:include page="../header.jsp"/>

<!-- Page header -->
<div class="page-header">
    <div class="page-header-content d-lg-flex align-items-center justify-content-between pb-2">
        <div class="kt">
            <div class="border border-gray-300 border-dashed rounded min-w-125px py-3 px-4">
                <div class="d-flex align-items-center">
                    <div class="d-flex flex-center w-25px h-25px rounded-3 bg-light-primary bg-opacity-90 me-3">
                        <i class="ki-duotone ki-barcode text-primary fs-3x"><span class="path1"></span><span class="path2"></span></i>                </div>
                    <div class="fs-8  fw-bold counted ms-2">Reports</div>
                </div>
            </div>
        </div>

    </div>


</div>

<!-- /page header -->



<!-- Page content -->
<div class="page-content pt-0">

    <!-- Main sidebar -->
    <%request.setAttribute("TAB","RPT");%>

    <jsp:include page="../sidebar.jsp"/>

    <!-- /main sidebar -->

    <!-- Main content -->
    <div class="content-wrapper">

        <div class="content">

            <div class="dashboard row">

                <div class="kt">
                    <div id="kt_app_content" class="app-content flex-column-fluid" style="padding-top: 0">
                        <!--begin::Row-->
                        <div class="row">
                            <!--begin::Col-->
                            <div class="col-xl-12">
                                <!--begin::Misc Widget 1-->
                                <div class="row mb-5 mb-xl-8 g-5 g-xl-8">
                                    <%
                                    for(MenuList menu:sortmenuList){
                                    %>
                                        <!--begin::Col-->
                                        <div class="col-4">
                                            <!--begin::Items-->
                                            <div class="bg-gray-100 bg-opacity-70 rounded-2 px-6 py-5" style="background: white !important;box-shadow: rgba(9, 30, 66, 0.25) 0px 4px 8px -2px, rgba(9, 30, 66, 0.08) 0px 0px 0px 1px;">
                                                <!--begin::Symbol-->
                                                <div class="d-flex flex-stack mb-3">
                                                    <!--begin::Wrapper-->
                                                    <div class="me-3">
                                                        <!--begin::Icon-->
                                                        <div class="d-flex flex-center w-30px h-30px rounded-3 bg-light-primary bg-opacity-90 mb-0">
                                                            <i class="<%=menu.getIcon()%> text-primary fs-2x menu_icon"></i>
                                                            <span class="path1"></span>
                                                            <span class="path2"></span>
                                                        </div>
                                                        <!--end::Icon-->
                                                    </div>
                                                    <!--end::Wrapper-->
                                                </div>
                                                <!--end::Symbol-->
                                                <!--begin::Stats-->
                                                <div class="d-flex flex-stack">
                                                    <!--begin::Name-->
                                                    <span class="text-gray-400 fw-bold">
														<a href="<%=menu.getMenuUrl()%>" class="text-gray-800 text-hover-primary fw-bold menu_url"><%=menu.getMenuDesc()%></a>
													</span>
                                                    <!--end::Name-->
                                                </div>
                                                <!--end::Stats-->
                                            </div>
                                            <!--end::Items-->
                                        </div>
                                        <!--end::Col-->
                                <%}%>


                                    <!--end::Col-->
                                </div>
                                <!--end::Misc Widget 1-->
                                <!--begin::List Widget 5-->
                                <!--end: List Widget 5-->

                            </div>
                            <!--end::Col-->

                            <!--end::Col-->
                        </div>
                        <!--end::Row-->
                    </div>
                </div>




            </div>
        </div>
    </div>
    <!-- /main content -->
</div>
<!-- /page content -->



<div class="btn-to-top">
    <button class="btn btn-secondary btn-icon rounded-pill" type="button"><i class="ph-arrow-up"></i></button>
</div>

<!-- Footer -->
<jsp:include page="../footer.jsp"/>
<!-- /footer -->
</body>
</html>
