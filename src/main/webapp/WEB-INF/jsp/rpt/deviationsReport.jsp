<%--
  Created by IntelliJ IDEA.
  User: SIBL12134
  Date: 30-12-2024
  Time: 16:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.VehicleLoanMasterDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.VehicleLoanApplicantDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="java.util.Map" %>
<%@ taglib prefix="los" uri="http://www.siblos.com/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en" data-bs-theme="light">
<head>
  <meta charset="UTF-8">
  <title>Vehicle Loan Deviations Report</title>
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="_csrf" content="">
  <meta name="_csrf_header" content="">
  <link href="assets/icons/phosphor/styles.min.css" rel="stylesheet" type="text/css">
  <link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
  <link href="assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
  <link href="assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css"/>
  <link href="assets/css/style.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
  <link href="assets/plugins/global/plugins.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
  <style>
    .dt-buttons { display: none !important; }
    #branch-maker-queue-details-table_filter { display: none !important; }
    .header-bg { background-image: url('assets/images/5img.jpeg') !important; background-size: cover; }
  </style>
</head>
<% Employee employee = (Employee) request.getAttribute("employee"); %>
<body id="kt_body">
<los:loader/>
<los:pageheader/>

<div class="page-header">
  <div class="page-header-content d-lg-flex">
    <div class="kt">
      <div class="border border-gray-300 border-dashed rounded min-w-125px py-3 px-4 me-0 ms-3 mb-3 mt-1">
        <div class="d-flex align-items-center mt-2 mb-2">
          <div class="d-flex flex-center w-25px h-25px rounded-3 bg-light-primary bg-opacity-90 me-3">
            <i class="ki-duotone ki-abstract-26 text-primary fs-3x">
              <span class="path1"></span>
              <span class="path2"></span>
            </i>
          </div>
          <div class="fs-8 fw-bold counted ms-2">Vehicle Loan CIF Report</div>
        </div>
      </div>
    </div>
    <div class="collapse d-lg-block my-lg-auto ms-lg-auto" id="page_header1"></div>
  </div>
</div>

<div class="page-content pt-0">
  <div class="content-wrapper">
    <div class="content">
      <div class="row">
        <div class="kt" id="kt_wrapper">
          <div class="content d-flex flex-column flex-column-fluid" id="kt_content">
            <div class="col-xl-12">
              <div class="card card-xl-stretch mb-5 mb-xl-8">
                <div class="card-header border-0 pt-5">
                  <div class="card-title">
                    <div class="d-flex align-items-center position-relative my-1">
                      <i class="ki-duotone ki-magnifier fs-3 position-absolute ms-4">
                        <span class="path1"></span>
                        <span class="path2"></span>
                      </i>
                      <input type="text" data-kt-ecommerce-order-filter="search"
                             class="form-control form-control-solid w-250px ps-12"
                             placeholder="Search Report"/>
                    </div>
                  </div>
                  <div class="card-toolbar flex-row-fluid justify-content-end gap-5">
                    <button type="button" class="btn btn-sm btn-light-primary"
                            data-kt-menu-trigger="click" data-kt-menu-placement="bottom-end">
                      <i class="ki-duotone ki-exit-down fs-2">
                        <span class="path1"></span>
                        <span class="path2"></span>
                      </i>
                      Export Report
                    </button>
                    <div id="kt_datatable_example_export_menu"
                         class="menu menu-sub menu-sub-dropdown menu-column menu-rounded menu-gray-600 menu-state-bg-light-primary fw-semibold fs-7 w-200px py-4"
                         data-kt-menu="true">
                      <div class="menu-item px-3">
                        <a href="#" class="menu-link px-3" data-kt-export="copy">
                          Copy to clipboard
                        </a>
                      </div>
                      <div class="menu-item px-3">
                        <a href="#" class="menu-link px-3" data-kt-export="excel">
                          Export as Excel
                        </a>
                      </div>
                      <div class="menu-item px-3">
                        <a href="#" class="menu-link px-3" data-kt-export="csv">
                          Export as CSV
                        </a>
                      </div>
                      <div class="menu-item px-3">
                        <a href="#" class="menu-link px-3" data-kt-export="pdf">
                          Export as PDF
                        </a>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="card-body py-3">
                  <div class="table-responsive">
                    <table class="table align-middle table-row-dashed fs-8 gy-3"
                           id="branch-maker-queue-details-table">
                      <thead>
                      <tr class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">
                        <th class="min-w-10px"></th>
                        <th>REGION</th>
                        <th>BRANCH</th>
                        <th>APPLICATION_NO</th>
                        <th>CUSTOMER_NAME</th>
                        <th>LOAN_FACILITY_TYPE</th>
                        <th>REQUESTED_LOAN_AMT</th>
                        <th>SANCTIONED_AMOUNT</th>
                        <th>SANCTION_DATE</th>
                        <th>SANCTION_AUTHORITY</th>
                        <th>PRODUCT_CATEGORY</th>
                        <th>ALL_DEVIATIONS</th>
                      </tr>
                      </thead>
                      <tbody class="fw-semibold text-gray-600">
                      <% List<Map<String,String>> report = (List<Map<String,String>>) request.getAttribute("report");
                        for (Map<String,String> item : report) { %>
                      <tr>
                        <td></td>
                        <td><%=item.get("REGION")%></td>
                        <td><%=item.get("BRANCH")%></td>
                        <td><%=item.get("APPLICATION_NO")%></td>
                        <td><%=item.get("CUSTOMER_NAME")%></td>
                        <td><%=item.get("LOAN_FACILITY_TYPE")%></td>
                        <td><%=item.get("REQUESTED_LOAN_AMT")%></td>
                        <td><%=item.get("SANCTIONED_AMOUNT")%></td>
                        <td><%=item.get("SANCTION_DATE")%></td>
                        <td><%=item.get("SANCTION_AUTHORITY")%></td>
                        <td><%=item.get("PRODUCT_CATEGORY")%></td>
                        <td><%=item.get("ALL_DEVIATIONS")%></td>
                      </tr>
                      <% } %>
                      </tbody>
                    </table>
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

<!-- Scripts -->
<script src="assets/demo/demo_configurator.js"></script>
<script src="assets/js/bootstrap/bootstrap.bundle.min.js"></script>
<script src="assets/js/vendor/visualization/d3/d3.min.js"></script>
<script src="assets/js/vendor/visualization/d3/d3_tooltip.js"></script>
<script src="assets/js/jquery/jquery.min.js"></script>
<script src="assets/js/scripts.bundle.js"></script>
<script src="assets/plugins/global/plugins.bundle.js"></script>
<script src="assets/plugins/custom/datatables/datatables.bundle.js"></script>
<script>
  "use strict";
  $('#backbtn').on('click', function (e) {
    e.preventDefault();
    window.location="rptlist";
  });
  var VehicleLoanCifReport = function() {
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
              return 'deviations_report_' + new Date().toISOString().slice(0, 19).replace(/:/g, "-");
            }
          },
          {
            extend: 'csv',
            text: 'Export to CSV',
            filename: function () {
              return 'deviations_report_' + new Date().toISOString().slice(0, 19).replace(/:/g, "-");
            }
          },
          {
            extend: 'pdf',
            text: 'Export to PDF',
            filename: function () {
              return 'deviations_report_' + new Date().toISOString().slice(0, 19).replace(/:/g, "-");
            }
          },
          {
            extend: 'copy',
            text: 'Copy to Clipboard'
          }
        ]
      });
    }

    var handleSearchDatatable = () => {
      const filterSearch = document.querySelector('[data-kt-ecommerce-order-filter="search"]');
      filterSearch.addEventListener('keyup', function (e) {
        datatable.search(e.target.value).draw();
      });
    }

    var handleExportButtons = () => {
      // Excel export
      document.querySelector('[data-kt-export="excel"]').addEventListener('click', function () {
        datatable.button('.buttons-excel').trigger();
      });

      // PDF export
      document.querySelector('[data-kt-export="pdf"]').addEventListener('click', function () {
        datatable.button('.buttons-pdf').trigger();
      });

      // CSV export
      document.querySelector('[data-kt-export="csv"]').addEventListener('click', function () {
        datatable.button('.buttons-csv').trigger();
      });

      // Copy to clipboard
      document.querySelector('[data-kt-export="copy"]').addEventListener('click', function () {
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
        handleExportButtons();
      }
    };
  }();

  // Initialize when DOM is ready
  document.addEventListener('DOMContentLoaded', function() {
    VehicleLoanCifReport.init();
  });

</script>
<%!
  public String getStatusBadge(String status) {
    if(status == null) return "-";

    switch(status.toUpperCase()) {
      case "COMPLETED":
        return "<span class=\"badge badge-success\">Completed</span>";
      case "REJECTED":
        return "<span class=\"badge badge-danger\">Rejected</span>";
      case "PENDING":
        return "<span class=\"badge badge-warning\">Pending</span>";
      default:
        return status;
    }
  }

  public String formatDate(String date) {
    if(date == null || date.trim().isEmpty()) return "-";
    return date;
  }
%>
</body>
</html>
