<%--
  Created by IntelliJ IDEA.
  User: SIBL12071
  Date: 03-01-2025
  Time: 17:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="los" uri="http://www.siblos.com/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en" data-bs-theme="light">
<head>
  <meta charset="UTF-8">
  <title>Reports</title>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="_csrf" content="">
  <meta name="_csrf_header" content="">
  <!-- Global stylesheets -->
  <%--		<link href="assets/fonts/inter/inter.css" rel="stylesheet" type="text/css">--%>
  <%--		--%>
  <link href="../assets/icons/phosphor/styles.min.css" rel="stylesheet" type="text/css">
  <link href="../assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
  <link href="../assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
  <%--		<link href="assets/css/custom/dashboard.css" rel="stylesheet" type="text/css">--%>
  <!-- /global stylesheets -->
  <!-- Core JS files -->

  <link href="../assets/css/style.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
  <link href="../assets/plugins/global/plugins.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
  <style>
    .dt-buttons{
      display: none !important;
    }
    #branch-maker-queue-details-table_filter{
      display: none !important;
    }
    .header-bg{
      background-image: url('../assets/images/5img.jpeg') !important;
      background-size: cover;
    }
  </style>
</head>

<body id="kt_body">
<%--<los:loader/>--%>
<!-- Page header -->
<los:pageheader/>
<!-- /page header -->

<div class="page-header">
  <div class="page-header-content d-lg-flex">
    <div class="kt">
      <div class="border border-gray-300 border-dashed rounded min-w-125px py-3 px-4 me-0 ms-3 mb-3 mt-1">
        <div class="d-flex align-items-center mt-2 mb-2">
          <div class="d-flex flex-center w-25px h-25px rounded-3 bg-light-primary bg-opacity-90 me-3">
            <i class="ki-duotone ki-abstract-26 text-primary fs-3x"><span class="path1"></span><span class="path2"></span></i>                </div>
          <div data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1" class="fs-8  fw-bold counted ms-2"><%=request.getAttribute("reportName").toString()%></div>
        </div>
      </div>
    </div>

    <div class="collapse d-lg-block my-lg-auto ms-lg-auto" id="page_header1">
    </div>
  </div>
</div>
<div class="page-content pt-0">
  <!-- /main sidebar -->
  <!-- Main content -->
  <div class="content-wrapper">
    <div class="content">
      <div class="row">


        <div class="kt" id="kt_wrapper">
          <div class=" content d-flex justify-content-center" id="kt_content">
            <div class="col-6">
              <div class="card card-xl-stretch mb-5 mb-xl-8">

                <div class="card-body py-3">
                  <div class="table-responsive">
                    <form name="wistatlist" id="wistatlist" method="post" action="../<%=request.getAttribute("reportPath").toString()%>">
                      <div class="d-flex justify-content-center p-5">
                        <div class="col-12">
                          <div class="text-center">
                            <h3>Report Selection</h3>
                          </div>
                          <div class="p-5">
                            <div class=" mb-5">
                              <div  class="fw-semibold">
                                Please choose From date.
                              </div>
                              <select id="wistat" name="wistat" class="form-control col-sm-6" required>
                                <option value="BM">Branch Maker</option>
                                <option value="BC">Branch Checker</option>
                                <option value="BS">Sendback</option>
                                <option value="CS">CRT Queue</option>
                                <option value="RM">RBCPC Maker</option>
                                <option value="RC">RBCPC Checker</option>
                                <option value="CA">CRT Queue Amber</option>
                                <option value="BD">Branch Documentation</option>
                                <option value="NIL">Rejection</option>
                                <option value="ACOPN">Account Opening</option>
                                <option value="PD">Post Disbursement</option>
                                <option value="CIF_CREATION">CIF ID Creation Queue</option>
                                <option value="CONCESSION_QUEUE">Concession Queue</option>
                              </select>
                            </div>

                          </div>


                        </div>

                      </div>

                      <div class="d-flex justify-content-center align-items-center ">
                        <button id="genrep" class="btn btn-primary btn-sm  position-relative m-5">
                          Generate
                        </button>
                      </div>


                    </form>

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
<script src="../assets/demo/demo_configurator.js"></script>
<script src="../assets/js/bootstrap/bootstrap.bundle.min.js"></script>
<!-- /core JS files -->
<!-- Theme JS files -->
<script src="../assets/js/vendor/visualization/d3/d3.min.js"></script>
<script src="../assets/js/vendor/visualization/d3/d3_tooltip.js"></script>
<script src="../assets/js/jquery/jquery.min.js"></script>

<script src="../assets/js/scripts.bundle.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<!--end::Vendors Javascript-->
<!--begin::Custom Javascript(used for this page only)-->

<script>

  $(document).ready(function () {
    $('#backbtn').on('click', function (e) {
      e.preventDefault();
      window.location="../rptlist";
    });

    $('#genrep').click(function (e){
      e.preventDefault();
      e.stopPropagation();
      $('#wistatlist').submit();

    });
  });
</script>
</body>
</html>


