<%--
  Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 23-09-2024
  Time: 14:17
  To change this template use File | Settings | File Templates.
--%>
<%--
  Created by IntelliJ IDEA.
  User: SIBL17971
  Date: 9/17/2024
  Time: 5:57 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>

<%@ page import="java.util.Map" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.math.RoundingMode" %>
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
  <link href="assets/icons/phosphor/styles.min.css" rel="stylesheet" type="text/css">
  <link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
  <link href="assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
  <%--		<link href="assets/css/custom/dashboard.css" rel="stylesheet" type="text/css">--%>
  <!-- /global stylesheets -->
  <!-- Core JS files -->
  <link href="assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css"/>
  <link href="assets/css/style.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
  <link href="assets/plugins/global/plugins.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
  <style>
    .dt-buttons{
      display: none !important;
    }
    #branch-maker-queue-details-table_filter{
      display: none !important;
    }
    .header-bg{
      background-image: url('assets/images/5img.jpeg') !important;
      background-size: cover;
    }
  </style>
</head>

<body id="kt_body">
<los:loader/>
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
          <div data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1" class="fs-8  fw-bold counted ms-2">Review of Concessions allowed </div>
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
          <div class="content d-flex flex-column flex-column-fluid" id="kt_content">
            <div class="col-xl-12">
              <div class="card card-xl-stretch mb-5 mb-xl-8">

                <div class="card-body py-3">
                  <div class="table-responsive">
                    <form name="wicalist" id="wicalist" method="post" action="waiverReport7">
                      <div class="d-flex p-5">
                        <div class="col-5">
                          <div class="text-center">
                            <h3>ROI Concession</h3>
                          </div>
                          <div class="p-5">
                            <div class=" mb-5">
                              <div  class="fw-semibold">
                                Please choose From date.
                              </div>
                              <input class="form-control "  type="date" name="fromdt1" id="fromdt1">
                            </div>
                            <div class=" mb-5">
                              <div  class="fw-semibold">
                                Please choose To date.
                              </div>
                              <input class="form-control "  type="date" name="todt1" id="todt1">
                            </div>

                          </div>


                        </div>
                        <div class="col-2">
                          <div class="text-center fw-semibold">OR</div>
                        </div>
                        <div class="col-5">
                          <div class="text-center">
                            <h3>UPFRONT FEE Concession</h3>
                          </div>
                          <div class="p-5">
                            <div class=" mb-5">
                              <div  class="fw-semibold">
                                Please choose From date.
                              </div>
                              <div>
                              <input class="form-control "  type="date" name="fromdt2" id="fromdt2">
                                </div>

                            </div>
                            <div class=" mb-5">
                              <div  class="fw-semibold">
                                Please choose To date.
                              </div>
                              <div>
                                <input class="form-control "  type="date" name="todt2" id="todt2">
                              </div>


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
<script src="assets/demo/demo_configurator.js"></script>
<script src="assets/js/bootstrap/bootstrap.bundle.min.js"></script>
<!-- /core JS files -->
<!-- Theme JS files -->
<script src="assets/js/vendor/visualization/d3/d3.min.js"></script>
<script src="assets/js/vendor/visualization/d3/d3_tooltip.js"></script>
<script src="assets/js/jquery/jquery.min.js"></script>

<script src="assets/js/scripts.bundle.js"></script>
<script src="assets/plugins/global/plugins.bundle.js"></script>
<script src="assets/plugins/custom/datatables/datatables.bundle.js"></script>
<!--end::Vendors Javascript-->
<!--begin::Custom Javascript(used for this page only)-->

<script>

  $(document).ready(function () {
    $('#backbtn').on('click', function (e) {
      e.preventDefault();
      window.location="rptlist";
    });

    $('#genrep').click(function (e){
      e.preventDefault();
      e.stopPropagation();
      if($('#fromdt1').val().length===0 && $('#fromdt2').val().length===0 ){
        alert('Kindly select atleast one From Date');
      }
      else if($('#fromdt1').val().length!==0 && $('#todt1').val().length ===0){
        alert('Kindly select To Date for ROI');
      }
      else if($('#fromdt2').val().length!==0 && $('#todt2').val().length ===0){
        alert('Kindly select To Date for Upfront Consession');
      }
      else{
        $('#wicalist').submit();
      }
    });
  });
</script>
</body>
</html>
<%!
  public String isEmpty(String str) {
    if(str==null)
      return "";
    else if(str.isBlank())
      return "";
    else
      return str;

  }
  public String getRoi(String init,String finalroi) {
    if(init==null || finalroi==null || init.isBlank() || finalroi.isBlank())
      return "";
    else
      return new BigDecimal(init).subtract(new BigDecimal(finalroi)).toString();
  }
  public String getWaiver(String init,String finalroi) {
    if(init==null || finalroi==null || init.isBlank() || finalroi.isBlank())
      return "-";
    else
      return ((new BigDecimal(init).subtract(new BigDecimal(finalroi))).multiply(new BigDecimal(100))).divide(new BigDecimal(init),2, RoundingMode.HALF_EVEN).toString();
  }
%>

