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
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
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
<%
  Employee employee = new Employee();
  if (request.getAttribute("employee") != null) {
    employee = (Employee) request.getAttribute("employee");
  }
%>
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
          <div data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1" class="fs-8  fw-bold counted ms-2">Review of Concessions allowed</div>
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
                <div class="card-header border-0 pt-5">
                  <div class="card-title">
                    <!--begin::Search-->
                    <div class="d-flex align-items-center position-relative my-1">
                      <i class="ki-duotone ki-magnifier fs-3 position-absolute ms-4 hover-elevate-up">
                        <span class="path1"></span>
                        <span class="path2"></span>
                      </i>
                      <input type="text" data-kt-ecommerce-order-filter="search" class="form-control form-control-solid w-250px ps-12"
                             placeholder="Search Report"/>
                    </div>

                  </div>
                  <div class="card-toolbar flex-row-fluid justify-content-end gap-5">
                    <!--begin::Export dropdown-->
                    <button type="button" class="btn btn-sm btn-light-primary" data-kt-menu-trigger="click" data-kt-menu-placement="bottom-end">
                      <i class="ki-duotone ki-exit-down fs-2"><span class="path1"></span><span class="path2"></span></i>
                      Export Report
                    </button>
                    <!--begin::Menu-->
                    <div id="kt_datatable_example_export_menu" class="menu menu-sub menu-sub-dropdown menu-column menu-rounded menu-gray-600 menu-state-bg-light-primary fw-semibold fs-7 w-200px py-4" data-kt-menu="true">
                      <!--begin::Menu item-->
                      <div class="menu-item px-3">
                        <a href="#" class="menu-link px-3" data-kt-export="copy">
                          Copy to clipboard
                        </a>
                      </div>
                      <!--end::Menu item-->
                      <!--begin::Menu item-->
                      <div class="menu-item px-3">
                        <a href="#" class="menu-link px-3"  data-kt-export="excel">
                          Export as Excel
                        </a>
                      </div>
                      <!--end::Menu item-->
                      <!--begin::Menu item-->
                      <div class="menu-item px-3">
                        <a href="#" class="menu-link px-3" data-kt-export="csv">
                          Export as CSV
                        </a>
                      </div>
                      <!--end::Menu item-->
                      <!--begin::Menu item-->
                      <div class="menu-item px-3">
                        <a href="#" class="menu-link px-3" data-kt-export="pdf">
                          Export as PDF
                        </a>
                      </div>
                      <!--end::Menu item-->
                    </div>
                  </div>
                </div>

                <div class="card-body py-3">
                  <div class="table-responsive">
                    <form name="wicalist" id="wicalist" method="post" action="#">
                      <input type="hidden" name="slno" value="" id="slnoInput">
                      <input type="hidden" name="action" value="CR" id="action_mod">
                      <table class="table align-middle table-row-dashed fs-8 gy-3" id="branch-maker-queue-details-table">
                        <thead>
                        <tr class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">
                          <th>REGION</th>
                          <th>REGION NAME</th>
                          <th>SOLID</th>
                          <th>BRANCH</th>
                          <th>CIF_ID</th>
                          <th>A/C</th>
                          <th>A/C NUMBER</th>
                          <th>WI_NUM</th>
                          <th>SANC_LMT</th>
                          <th>SANC_DATE</th>
                          <th>LOAN AMT</th>
                          <th>TENURE</th>
                          <th>INIT. ROI</th>
                          <th>SANC ROI</th>
                          <th>CONCESSION ROI</th>
                          <th>SANC ROI USER</th>
                          <th>SANC ROI DATE</th>
                          <th>UPFRONT FEE</th>
                          <th>SANC. UPFRONT FEE</th>
                          <th>% OF CONCESSION IN UPFRONT FEE</th>
                          <th>SANC. UPFRONT USER</th>
                          <th>SANC. UPFRONT DATE</th>
                          <th>APPL. EXPERIAN</th>
                        </tr>
                        </thead>
                        <tbody class="fw-semibold text-gray-600">
                        <%
                          String initroi="",finalroi="",roic="";
                          String initfee="",finalfee="",feec="";

                          if( request.getAttribute("report")!=null)  {
                          List<Map<String,String>> report = (List<Map<String,String>>) request.getAttribute("report");
                          for (Map<String,String> item : report) {
                            initroi=isEmpty(item.get("INITIAL_ROI"));
                            finalroi=isEmpty(item.get("REVISED_ROI"));
                            roic=getRoi(initroi,finalroi);

                            initfee=isEmpty(item.get("INITFEE"));
                            finalfee=isEmpty(item.get("FINALFEE"));
                            feec=getWaiver(initfee,finalfee);

                        %>
                        <tr>
                          <td><%=item.get("REG_CODE")%></td>
                          <td><%=item.get("REG_NAME").trim()%></td>
                          <td><%=item.get("SOL_ID")%></td>
                          <td><%=item.get("BR_NAME").trim()%></td>
                          <td><%=isEmpty(item.get("CUST_ID"))%></td>
                          <td><%=isEmpty(item.get("ACC_NUMBER"))%></td>
                          <td><%=isEmpty(item.get("ACCT_NAME"))%></td>
                          <td><%=item.get("WI_NUM")%></td>
                          <td><%=item.get("ELIGIBLE_LOAN_AMT")%></td>
                          <td><%=item.get("SAN_DATE")%></td>
                          <td><%=item.get("SANC_AMOUNT_RECOMMENDED")%></td>
                          <td><%=item.get("SANC_TENOR")%></td>
                          <td><%=initroi%></td>
                          <td><%=finalroi%></td>
                          <td><%=roic%></td>
                          <td><%=isEmpty(item.get("ROI_COMPLETED_USER"))%></td>
                          <td><%=isEmpty(item.get("ROI_COMPLETED_DATE"))%></td>
                          <td><%=initfee%></td>
                          <td><%=finalfee%></td>
                          <td><%=feec%></td>
                          <td><%=isEmpty(item.get("PR_COMPLETED_USER"))%></td>
                          <td><%=isEmpty(item.get("PR_COMPLETED_DATE"))%></td>
                          <td><%=isEmpty(item.get("APPL_SCORE"))%></td>
                        </tr>
                        <% } }%>
                        </tbody>

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
  "use strict";
  var IbankLOSDashboard = function() {
    var table;
    var datatable;
    var initDatatable = function () {
      datatable = $(table).DataTable({
        "info": false,
        'order': [],
        'pageLength': 10,
        dom: 'Bfrtip',
        buttons: [
          {
            extend: 'excel',
            text: 'Export to Excel',
            filename: function () {
              return 'consolidated_report_' + new Date().toISOString().slice(0, 19).replace(/:/g, "-");
            }
          },
          {
            extend: 'csv',
            text: 'Export to CSV',
            filename: function () {
              return 'consolidated_report_' + new Date().toISOString().slice(0, 19).replace(/:/g, "-");
            }
          },
          {
            extend: 'pdf',
            text: 'Export to PDF',
            filename: function () {
              return 'consolidated_report_' + new Date().toISOString().slice(0, 19).replace(/:/g, "-");
            }
          },
          {
            extend: 'copy',
            text: 'Copy to Clipboard'
          }
        ]

      });
    }
    var initDaterangepicker = () => {
      var start = moment().subtract(29, "days");
      var end = moment();
      var input = $("#branch_maker_queue_daterangepicker");

      function cb(start, end) {
        input.html(start.format("MMMM D, YYYY") + " - " + end.format("MMMM D, YYYY"));
        datatable.draw();
      }

      input.daterangepicker({
        startDate: start,
        endDate: end,
        ranges: {
          "Today": [moment(), moment()],
          "Yesterday": [moment().subtract(1, "days"), moment().subtract(1, "days")],
          "Last 7 Days": [moment().subtract(6, "days"), moment()],
          "Last 30 Days": [moment().subtract(29, "days"), moment()],
          "This Month": [moment().startOf("month"), moment().endOf("month")],
          "Last Month": [moment().subtract(1, "month").startOf("month"), moment().subtract(1, "month").endOf("month")]
        }
      }, cb);

      cb(start, end);
      $.fn.dataTable.ext.search.push(
              function(settings, data, dataIndex) {
                var selectedStart = moment(start, "MMMM D, YYYY");
                var selectedEnd = moment(end, "MMMM D, YYYY");
                var rowDate = moment(data[1], "MMMM D, YYYY"); // Assumes date is in the second column (index 1)

                return rowDate.isBetween(selectedStart, selectedEnd, null, '[]');
              }
      );

    }
    var handleSearchDatatable = () => {
      const filterSearch = document.querySelector('[data-kt-ecommerce-order-filter="search"]');
      filterSearch.addEventListener('keyup', function (e) {
        datatable.search(e.target.value).draw();
      });
    }

// Function to export datatable to Excel
    var handleExportExcel = () => {
      const exportExcelButton = document.querySelector('[data-kt-export="excel"]');
      exportExcelButton.addEventListener('click', function () {
        datatable.button('.buttons-excel').trigger();
      });
    }

// Function to export datatable to PDF
    var handleExportPDF = () => {
      const exportPDFButton = document.querySelector('[data-kt-export="pdf"]');
      exportPDFButton.addEventListener('click', function () {
        datatable.button('.buttons-pdf').trigger();
      });
    }

// Function to export datatable to CSV
    var handleExportCSV = () => {
      const exportCSVButton = document.querySelector('[data-kt-export="csv"]');
      exportCSVButton.addEventListener('click', function () {
        datatable.button('.buttons-csv').trigger();
      });
    }

// Function to copy datatable content to clipboard
    var handleExportClipboard = () => {
      const exportClipboardButton = document.querySelector('[data-kt-export="copy"]');
      exportClipboardButton.addEventListener('click', function () {
        datatable.button('.buttons-copy').trigger();
      });
    }


    return {
      init: function () {
        table = document.querySelector('#branch-maker-queue-details-table');

        if (!table) {
          return;
        }

        initDatatable();
        handleSearchDatatable();
        handleExportExcel();
        handleExportCSV();
        handleExportPDF();
        handleExportClipboard();
      }
    };
  }();
  document.addEventListener('DOMContentLoaded', function() {
    IbankLOSDashboard.init();
  });

  $(document).ready(function () {
    $('#backbtn').on('click', function (e) {
      e.preventDefault();
      window.location="rpt7";
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

