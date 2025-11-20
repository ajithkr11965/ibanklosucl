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
<%@ page import="io.vavr.collection.Queue" %>
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
                    <div data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1" class="fs-8  fw-bold counted ms-2">Main MIS Reporrt</div>
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
                                            <table class="table align-middle table-row-dashed fs-8 gy-3" id="branch-maker-queue-details-table">
                                                <thead>
                                                <tr class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">
                                                    <th>SLNO</th>
                                                    <th>WI_NUM</th>
                                                    <th>CURRENT_WORKSTEP_NAME</th>
                                                    <th>PREVIOUS_WORKSTEP_NAME</th>
                                                    <th>SOL_ID</th>
                                                    <th>BR_NAME</th>
                                                    <th>CLUSTER</th>
                                                    <th>REG_CODE</th>
                                                    <th>REG_NAME</th>
                                                    <th>CUST_NAME</th>
                                                    <th>LOAN_AMT</th>
                                                    <th>PROGRAM</th>
                                                    <th>MAKER_TAT</th>
                                                    <th>MAKER_ENTRY_DATE</th>
                                                    <th>MAKER_EXIT_DATE</th>
                                                    <th>CHECKER_TAT</th>
                                                    <th>SENDBACK_TAT</th>
                                                    <th>RBCMAKER_TAT</th>
                                                    <th>RBCMAKER_ENTRY_DATE</th>
                                                    <th>RBCCHEKER_TAT</th>
                                                    <th>DO_PPC</th>
                                                    <th>NAME_DO</th>
                                                    <th>SAN_USER</th>
                                                    <th>NAME_SAN</th>
                                                    <th>SAN_DATE</th>
                                                    <th>SANC_AMT</th>
                                                    <th>CRT_AMBER_TAT</th>
                                                    <th>CRT_GREEN_TAT</th>
                                                    <th>STATUS</th>
                                                    <th>SANC_AMOUNT</th>
                                                    <th>CIF_ID</th>
                                                    <th>Loan Account Open Date</th>
                                                    <th>Disbursement Amount</th>
                                                    <th>FUEL_TYPE</th>
                                                    <th>MOBILE</th>
                                                    <th>LOAN_CHARGES_INCOME_IN_RS</th>
                                                    <th>CHANNEL</th>
                                                    <th>SENDBACKTOBRANCH_COUNT</th>
                                                    <th>LATEST_QUEUE_USER</th>
                                                    <th>DST_CODE</th>
                                                    <th>DST_NAME</th>
                                                    <th>DSA_SUB_CODE</th>
                                                    <th>DEALER CODE</th>
                                                    <th>DEALER_NAME</th>
                                                    <th>DEALER SUB CODE</th>
                                                    <th>DEALER_LOCATION</th>
                                                    <th>AUTODEALER_SOURCED</th>
                                                    <th>MAKE_NAME</th>
                                                    <th>DO_LATEST_SUBMISSION_DATE</th>
                                                    <th>CREATEDBYPPC</th>
                                                    <th>CREATEDBYNAME</th>
                                                    <th>CREATEDATE</th>
                                                    <th>LTV_PER</th>
                                                    <th>FOIR_TYPE</th>
                                                    <th>STP</th>
                                                    <th>MODIFICATION_INITIATED</th>
                                                    <th>ACC NUMBER</th>
                                                    <th>ASSET_VALUE_LAKHS</th>

                                                </tr>
                                                </thead>
                                                <tbody class="fw-semibold text-gray-600">
                                                <%
                                                    List<Map<String,String>> report = (List<Map<String,String>>) request.getAttribute("report");
                                                    for (Map<String,String> item : report) {
                                                %>
                                                <tr>
                                                    <td><%=item.get("SLNO") != null ? item.get("SLNO") : "" %></td>
                                                    <td><%=item.get("WI_NUM") != null ? item.get("WI_NUM") : "" %></td>
                                                    <td><%=item.get("Current_Workstep_Name") != null ? item.get("Current_Workstep_Name") : "" %></td>
                                                    <td><%=item.get("Previous_Workstep_Name") != null ? item.get("Previous_Workstep_Name") : "" %></td>
                                                    <td><%=item.get("SOL_ID") != null ? item.get("SOL_ID") : "" %></td>
                                                    <td><%=item.get("BR_NAME") != null ? item.get("BR_NAME") : "" %></td>
                                                    <td><%=item.get("CLUSTER_NAME") != null ? item.get("CLUSTER_NAME") : "" %></td>
                                                    <td><%=item.get("REG_CODE") != null ? item.get("REG_CODE") : "" %></td>
                                                    <td><%=item.get("REG_NAME") != null ? item.get("REG_NAME") : "" %></td>
                                                    <td><%=item.get("CUST_NAME") != null ? item.get("CUST_NAME") : "" %></td>
                                                    <td><%=item.get("LOAN_AMT") != null ? item.get("LOAN_AMT") : "" %></td>
                                                    <td><%=item.get("PROGRAM") != null ? item.get("PROGRAM") : "" %></td>
                                                    <td><%=item.get("MAKER_TAT") != null ? item.get("MAKER_TAT") : "" %></td>
                                                    <td><%=item.get("MAKER_ENTRY_DATE") != null ? item.get("MAKER_ENTRY_DATE") : "" %></td>
                                                    <td><%=item.get("MAKER_EXIT_DATE") != null ? item.get("MAKER_EXIT_DATE") : "" %></td>
                                                    <td><%=item.get("CHECKER_TAT") != null ? item.get("CHECKER_TAT") : "" %></td>
                                                    <td><%=item.get("SENDBACK_TAT") != null ? item.get("SENDBACK_TAT") : "" %></td>
                                                    <td><%=item.get("RBCMAKER_TAT") != null ? item.get("RBCMAKER_TAT") : "" %></td>
                                                    <td><%=item.get("RBCMAKER_ENTRY_DATE") != null ? item.get("RBCMAKER_ENTRY_DATE") : "" %></td>
                                                    <td><%=item.get("RBCCHEKER_TAT") != null ? item.get("RBCCHEKER_TAT") : "" %></td>
                                                    <td><%=item.get("DO_PPC") != null ? item.get("DO_PPC") : "" %></td>
                                                    <td><%=item.get("Name_DO") != null ? item.get("Name_DO") : "" %></td>
                                                    <td><%=item.get("SAN_USER") != null ? item.get("SAN_USER") : "" %></td>
                                                    <td><%=item.get("Name_SAN") != null ? item.get("Name_SAN") : "" %></td>
                                                    <td>
                                                      <%= item.get("SANC_AMT") != null ? (item.get("SAN_DATE") != null ? item.get("SAN_DATE") : "") : "" %>
                                                    </td>

                                                    <td><%=item.get("SANC_AMT") != null ? item.get("SANC_AMT") : "" %></td>
                                                    <td><%=item.get("CRT_AMBER_TAT") != null ? item.get("CRT_AMBER_TAT") : "" %></td>
                                                    <td><%=item.get("CRT_GREEN_TAT") != null ? item.get("CRT_GREEN_TAT") : "" %></td>
                                                    <td><%=item.get("STATUS") != null ? item.get("STATUS") : "" %></td>
                                                    <td><%=item.get("SANC_AMT") != null ? item.get("SANC_AMT") : "" %></td>
                                                    <td><%=item.get("CIF_ID") != null ? item.get("CIF_ID") : "" %></td>
                                                    <td><%=item.get("ACC_OPEN_DATE") != null ? item.get("ACC_OPEN_DATE") : "" %></td>
                                                    <td><%=item.get("DISBURSEDAMOUNT") != null ? item.get("DISBURSEDAMOUNT") : "" %></td>
                                                    <td><%=item.get("FUEL_TYPE") != null ? item.get("FUEL_TYPE") : "" %></td>
                                                    <td><%=item.get("Mobile") != null ? item.get("Mobile") : "" %></td>
                                                    <td><%=item.get("LOAN_CHARGES_INCOME_IN_RS") != null ? item.get("LOAN_CHARGES_INCOME_IN_RS") : "" %></td>
                                                    <td><%=item.get("CHANNEL") != null ? item.get("CHANNEL") : "" %></td>
                                                    <td><%=item.get("SENDBACKTOBRANCH_COUNT") != null ? item.get("SENDBACKTOBRANCH_COUNT") : "" %></td>
                                                    <td><%=item.get("LATEST_QUEUE_USER") != null ? item.get("LATEST_QUEUE_USER") : "" %></td>
                                                    <td><%=item.get("DST_CODE") != null ? item.get("DST_CODE") : "" %></td>
                                                    <td><%=item.get("DST_NAME") != null ? item.get("DST_NAME") : "" %></td>
                                                    <td><%=item.get("DSA_CODE") != null ? item.get("DSA_CODE") : "" %></td>
                                                    <td><%=item.get("DEALER_CODE") != null ? item.get("DEALER_CODE") : "" %></td>
                                                    <td><%=item.get("DEALER_NAME") != null ? item.get("DEALER_NAME") : "" %></td>
                                                    <td><%=item.get("DEALER_SUB_CODE") != null ? item.get("DEALER_SUB_CODE") : "" %></td>
                                                    <td><%=item.get("DEALER_CITY_NAME") != null ? item.get("DEALER_CITY_NAME") : "" %></td>
                                                    <td><%=item.get("AUTODEALER_SOURCED") != null ? item.get("AUTODEALER_SOURCED") : "" %></td>
                                                    <td><%=item.get("MAKE_NAME") != null ? item.get("MAKE_NAME") : "" %></td>


                                                    <td><%=item.get("DO_LATEST_SUBMISSION_DATE") != null ? item.get("DO_LATEST_SUBMISSION_DATE") : "" %></td>
                                                    <td><%=item.get("CREATEDBYPPC") != null ? item.get("CREATEDBYPPC") : "" %></td>
                                                    <td><%=item.get("CREATEDBYNAME") != null ? item.get("CREATEDBYNAME") : "" %></td>
                                                    <td><%=item.get("CREATEDATE") != null ? item.get("CREATEDATE") : "" %></td>
                                                    <td><%=item.get("LTV_PER") != null ? item.get("LTV_PER") : "" %></td>
                                                    <td><%=item.get("FOIR_TYPE") != null ? item.get("FOIR_TYPE") : "" %></td>
                                                    <td><%=item.get("STP") != null ? item.get("STP") : "" %></td>
                                                    <td><%=item.get("MODIFICATION_INITIATED") != null ? item.get("MODIFICATION_INITIATED") : "" %></td>
                                                    <td><%=item.get("ACC_NUMBER") != null ? "#"+item.get("ACC_NUMBER") : "" %></td>
                                                    <td><%=item.get("ASSET_VALUE_LAKHS") != null ? item.get("ASSET_VALUE_LAKHS") : "" %></td>
                                                </tr>
                                                <% } %>
                                                </tbody>
                                            </table>


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
                        },
                        customize: function (xlsx) {
                            var sheet = xlsx.xl.worksheets['sheet1.xml'];
                            // Target all cells in column BE (adjust if your column position changes)
                            $('row c[r^="BE"]', sheet).each(function () {
                                $(this).attr('t', 'inlineStr');
                            });
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
            window.location="dtrpt/rpt8";
        });
    });
</script>
</body>
</html>

