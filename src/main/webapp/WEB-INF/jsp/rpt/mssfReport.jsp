<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="com.sib.ibanklosucl.dto.mssf.MSSFCustomerDTO" %>

<%
    List<MSSFCustomerDTO> report = (List<MSSFCustomerDTO>) request.getAttribute("report");
    String reportname = (String) request.getAttribute("reportname");
    DecimalFormat df = new DecimalFormat("#,##0.00");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>MSSF Customer Details Report</title>
    <link href="assets/css/sweetalert2.min.css" rel="stylesheet">
    <link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
    <link href="assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
    <link href="assets/css/custom.css" rel="stylesheet" type="text/css"/>
    <link href="assets/css/custom/wirmmodify.css" rel="stylesheet" type="text/css"/>
    <link href="assets/css/style.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
    <link href="assets/plugins/global/plugins.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
    <!-- DataTables CSS for Excel Export -->
    <link href="assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css"/>

    <script src="assets/js/bootstrap/bootstrap.bundle.min.js"></script>
    <script src="assets/js/jquery/jquery.min.js"></script>
    <script src="assets/js/vendor/forms/validation/validate.min.js"></script>
    <script src="assets/js/vendor/split.min.js"></script>
    <script src="assets/demo/pages/components_tooltips.js"></script>
    <script src="assets/js/vendor/forms/selects/select2.min.js"></script>
    <script src="assets/demo/pages/form_select2.js"></script>
    <script src="assets/js/app.js"></script>
    <script src="assets/js/vendor/uploaders/fileinput/fileinput.min.js"></script>
    <script src="assets/js/vendor/uploaders/fileinput/plugins/sortable.min.js"></script>
    <script src="assets/js/vendor/notifications/sweet_alert.min.js"></script>
    <script src="assets/js/vendor/notifications/noty.min.js"></script>
    <!-- DataTables JS for Excel Export -->
    <script src="assets/plugins/custom/datatables/datatables.bundle.js"></script>

    <style>
        .table-responsive {
            max-height: 600px;
            overflow-y: auto;
        }
        .locked-row {
            background-color: #fff3cd;
        }
        .badge {
            padding: 5px 10px;
            border-radius: 4px;
            font-size: 0.85em;
            font-weight: bold;
        }
        .badge-info {
            background-color: #17a2b8;
            color: white;
        }
        .badge-warning {
            background-color: #ffc107;
            color: black;
        }
        .badge-danger {
            background-color: #dc3545;
            color: white;
        }
        .badge-success {
            background-color: #28a745;
            color: white;
        }
        .badge-primary {
            background-color: #007bff;
            color: white;
        }
        .badge-secondary {
            background-color: #6c757d;
            color: white;
        }
        .dt-buttons {
            display: none !important;
        }
        @media print {
            .no-print {
                display: none;
            }
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-12">
                <h3 class="text-center"><%= reportname != null ? reportname : "MSSF Customer Details Report" %></h3>

                <div class="row mb-3">
                    <div class="col-md-6">
                        <p><strong>Total Records:</strong> <%= report != null ? report.size() : 0 %></p>
                        <p><strong>Generated On:</strong> <%= java.time.LocalDateTime.now().format(dateFormatter) %></p>
                    </div>
                    <div class="col-md-6 text-right">
                        <!-- Export Button -->
                        <div class="btn-group no-print">
                            <button type="button" class="btn btn-success dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="fas fa-download mr-auto"></i> Export Report
                            </button>
                            <ul class="dropdown-menu">
                                <li><a class="dropdown-item" href="#" id="export-excel">
                                    <i class="fas fa-file-excel"></i> Export as Excel
                                </a></li>
                                <li><a class="dropdown-item" href="#" id="export-csv">
                                    <i class="fas fa-file-csv"></i> Export as CSV
                                </a></li>
                                <li><a class="dropdown-item" href="#" id="export-pdf">
                                    <i class="fas fa-file-pdf"></i> Export as PDF
                                </a></li>
                            </ul>
                        </div>
                    </div>
                </div>

                <% if (report != null && !report.isEmpty()) { %>
                <div class="table-responsive">
                    <table class="table table-striped table-bordered table-sm" id="mssf-report-table">
                        <thead class="thead-dark">
                            <tr>
                                <th>S.No</th>
                                <th>Ref No</th>
                                <th>Customer Name</th>
                                <th>Mobile</th>
                                <th>Email</th>
                                <th>SOL ID</th>
                                <th>Dealer Code</th>
                                <th>Dealer Name</th>
                                <th>Loan Amount</th>
                                <th>Status</th>
                                <th>Work Item No</th>
                                <th>WI Status</th>
                                <th>Created Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                            int count = 1;
                            for (MSSFCustomerDTO customer : report) {
                                String rowClass = "";
                            %>
                            <tr class="<%= rowClass %>">
                                <td><%= count++ %></td>
                                <td><%= customer.getRefNo() != null ? customer.getRefNo() : "" %></td>
                                <td><%= customer.getCustomerName() != null ? customer.getCustomerName() : "" %></td>
                                <td><%= customer.getMobile() != null ? customer.getMobile() : "" %></td>
                                <td><%= customer.getEmail() != null ? customer.getEmail() : "" %></td>
                                <td data-export="<%= customer.getSolId() != null ? customer.getSolId() : "" %>">
                                    <% if (customer.getSolId() != null) { %>
                                        <span class="badge badge-info"><%= customer.getSolId() %></span>
                                    <% } %>
                                </td>
                                <td><%= customer.getDealerCode() != null ? customer.getDealerCode() : "" %></td>
                                <td><%= customer.getDealerName() != null ? customer.getDealerName() : "" %></td>
                                <td><%= customer.getLoanAmount() != null ? df.format(customer.getLoanAmount()) : "0.00" %></td>
                                <td data-export="<%= customer.getStatus() != null ? customer.getStatus() : "PENDING" %>">
                                    <%
                                    String status = customer.getStatus();
                                    String statusClass = "badge-secondary";
                                    String statusText = "PENDING";

                                    if (status != null) {
                                        statusText = status;
                                        switch (status.toUpperCase()) {
                                            case "PENDING":
                                                statusClass = "badge-warning";
                                                break;
                                            case "INITIATED":
                                                statusClass = "badge-info";
                                                break;
                                            case "APPROVED":
                                                statusClass = "badge-success";
                                                break;
                                            case "REJECTED":
                                                statusClass = "badge-danger";
                                                break;
                                            default:
                                                statusClass = "badge-secondary";
                                        }
                                    }
                                    %>
                                    <span class="badge <%= statusClass %>"><%= statusText %></span>
                                </td>
                                <td data-export="<%= customer.getWorkItemNumber() != null ? customer.getWorkItemNumber() : "" %>">
                                    <% if (customer.getWorkItemNumber() != null && !customer.getWorkItemNumber().trim().isEmpty()) { %>
                                        <span class="badge badge-primary"><%= customer.getWorkItemNumber() %></span>
                                    <% } else { %>
                                        <span class="text-muted">-</span>
                                    <% } %>
                                </td>
                                <td data-export="<%= customer.getWorkItemStatus() != null ? customer.getWorkItemStatus() : "" %>">
                                    <%
                                    if (customer.getWorkItemStatus() != null && !customer.getWorkItemStatus().trim().isEmpty()) {
                                        String wiStatusClass = "badge-secondary";
                                        String wiStatus = customer.getWorkItemStatus();

                                        // Color code work item status
                                        switch (wiStatus.toUpperCase()) {
                                            case "PENDING":
                                            case "P":
                                                wiStatusClass = "badge-warning";
                                                break;
                                            case "COMPLETED":
                                            case "C":
                                                wiStatusClass = "badge-success";
                                                break;
                                            case "IN_PROGRESS":
                                            case "I":
                                                wiStatusClass = "badge-info";
                                                break;
                                            case "REJECTED":
                                            case "R":
                                                wiStatusClass = "badge-danger";
                                                break;
                                            default:
                                                wiStatusClass = "badge-secondary";
                                        }
                                    %>
                                        <span class="badge <%= wiStatusClass %>"><%= wiStatus %></span>
                                    <% } else { %>
                                        <span class="text-muted">-</span>
                                    <% } %>
                                </td>
                                <td><%= customer.getCreatedDate() != null ? customer.getCreatedDate().format(dateFormatter) : "" %></td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <% } else { %>
                <div class="alert alert-info text-center">
                    <h4>No MSSF customer records found.</h4>
                </div>
                <% } %>

                <div class="row mt-3 no-print">
                    <div class="col-md-12 text-center">
                        <a href="${pageContext.request.contextPath}/rptlist" class="btn btn-secondary ml-2">Back to Reports</a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        "use strict";
        var MSSFReportExport = function() {
            var table;
            var datatable;

            var initDatatable = function () {
                datatable = $(table).DataTable({
                    "info": false,
                    "paging": false,
                    "searching": false,
                    'order': [],
                    dom: 'Bfrtip',
                    columnDefs: [
                        {
                            // Handle badge columns for export
                            targets: [5, 9, 10, 11], // SOL ID, Status, Work Item No, WI Status columns
                            render: function (data, type, row, meta) {
                                if (type === 'export') {
                                    // Use data-export attribute value if available
                                    var cell = $(table).find('tbody tr').eq(meta.row).find('td').eq(meta.col);
                                    var exportValue = cell.attr('data-export');
                                    return exportValue || cell.text().trim();
                                }
                                return data;
                            }
                        },
                        {
                            // Handle mobile numbers to preserve leading zeros
                            targets: [3], // Mobile column
                            render: function (data, type, row) {
                                if (type === 'export') {
                                    return '\u200C' + data; // Zero-width non-joiner to preserve formatting
                                }
                                return data;
                            }
                        }
                    ],
                    buttons: [
                        {
                            extend: 'excel',
                            text: 'Export to Excel',
                            filename: function () {
                                return 'MSSF_Customer_Report_' + new Date().toISOString().slice(0, 19).replace(/:/g, "-");
                            },
                            title: '<%= reportname != null ? reportname : "MSSF Customer Details Report" %>',
                            messageTop: 'Generated On: <%= java.time.LocalDateTime.now().format(dateFormatter) %>\nTotal Records: <%= report != null ? report.size() : 0 %>',
                            customize: function (xlsx) {
                                var sheet = xlsx.xl.worksheets['sheet1.xml'];

                                // Format mobile number column as text to preserve leading zeros
                                $('row', sheet).each(function (index) {
                                    if (index > 0) { // Skip header row
                                        var mobileCell = $('c[r^="D"]', this); // Column D (Mobile)
                                        if (mobileCell.length > 0) {
                                            mobileCell.attr('t', 'inlineStr');
                                            var cellValue = mobileCell.find('v').text();
                                            mobileCell.html('<is><t>' + cellValue + '</t></is>');
                                        }
                                    }
                                });
                            }
                        },
                        {
                            extend: 'csv',
                            text: 'Export to CSV',
                            filename: function () {
                                return 'MSSF_Customer_Report_' + new Date().toISOString().slice(0, 19).replace(/:/g, "-");
                            }
                        },
                        {
                            extend: 'pdf',
                            text: 'Export to PDF',
                            filename: function () {
                                return 'MSSF_Customer_Report_' + new Date().toISOString().slice(0, 19).replace(/:/g, "-");
                            },
                            title: '<%= reportname != null ? reportname : "MSSF Customer Details Report" %>',
                            messageTop: 'Generated On: <%= java.time.LocalDateTime.now().format(dateFormatter) %> | Total Records: <%= report != null ? report.size() : 0 %>',
                            orientation: 'landscape',
                            pageSize: 'A4'
                        }
                    ]
                });
            }

            // Export handlers
            var handleExportExcel = () => {
                const exportExcelButton = document.querySelector('#export-excel');
                if (exportExcelButton) {
                    exportExcelButton.addEventListener('click', function (e) {
                        e.preventDefault();
                        datatable.button('.buttons-excel').trigger();
                    });
                }
            }

            var handleExportCSV = () => {
                const exportCSVButton = document.querySelector('#export-csv');
                if (exportCSVButton) {
                    exportCSVButton.addEventListener('click', function (e) {
                        e.preventDefault();
                        datatable.button('.buttons-csv').trigger();
                    });
                }
            }

            var handleExportPDF = () => {
                const exportPDFButton = document.querySelector('#export-pdf');
                if (exportPDFButton) {
                    exportPDFButton.addEventListener('click', function (e) {
                        e.preventDefault();
                        datatable.button('.buttons-pdf').trigger();
                    });
                }
            }

            return {
                init: function () {
                    table = document.querySelector('#mssf-report-table');

                    if (!table) {
                        return;
                    }

                    initDatatable();
                    handleExportExcel();
                    handleExportCSV();
                    handleExportPDF();
                }
            };
        }();

        document.addEventListener('DOMContentLoaded', function() {
            MSSFReportExport.init();
        });
    </script>

</body>
</html>