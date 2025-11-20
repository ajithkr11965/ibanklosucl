<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="los" uri="http://www.siblos.com/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <!-- Meta tags -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Vehicle Loan Dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Fonts and Icons -->
    <link href="assets/fonts/inter/inter.css" rel="stylesheet">
    <link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet">

    <!-- CSS Libraries -->
    <link href="assets/css/ltr/all.min.css" rel="stylesheet">
    <link href="assets/css/flatpickr.min.css" rel="stylesheet">
    <link href="assets/css/nouislider.css" rel="stylesheet">

    <!-- Custom Styles -->
    <style>
        /* General Styles */
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f4f6f9;
            color: #333;
            margin: 0;
            padding: 0;
        }
        .dashboard-container {
            padding: 20px;
            max-width: 1200px;
            margin: 0 auto;
        }
        /* Navbar */
        .navbar {
            background-color: #241c80; /* Changed color */
            padding: 15px 20px;
        }
        .navbar-brand {
            color: #fff;
            font-size: 26px;
            font-weight: 600;
        }
        /* Main Content */
        .main-content {
            margin-top: 30px;
        }
        .dashboard-header {
            margin-bottom: 30px;
            text-align: center;
        }
        .dashboard-header h2 {
            font-size: 32px;
            font-weight: 700;
            color: #241c80; /* Changed color */
        }
        /* KPI Cards */
        .kpi-card {
            text-align: center;
            padding: 20px;
            background-color: #fff;
            border-radius: 10px;
            margin-bottom: 75px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.05);
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .kpi-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 16px rgba(0,0,0,0.1);
        }
        .kpi-title {
            font-size: 18px;
            color: #6c757d;
        }
        .kpi-value {
            font-size: 20px;
            font-weight: 700;
            margin-top: 10px;
            color: #333;
        }
        .btn-group-toggle .btn {
            border-radius: 5px;
            margin-right: 5px;
        }
        .btn-group-toggle .btn:last-child {
            margin-right: 0;
        }
        .btn-group-toggle .btn input[type="radio"] {
            display: none;
        }
        .btn-group-toggle .btn.active {
            background-color: #241c80; /* Changed color */
            color: #fff;
            border-color: #241c80; /* Changed color */
        }
        .btn-group-toggle .btn:hover {
            background-color: #1e196b; /* Slightly darker shade */
            color: #fff;
            border-color: #1e196b;
        }
        /* Form Check Input */
        .form-check-input:checked {
            background-color: #241c80; /* Changed color */
            border-color: #241c80;
        }
        /* Filters */
        .filters-container {
            background-color: #fff;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 30px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.05);
        }
        .filters-container label {
            font-weight: 600;
            color: #495057;
        }
        .filters-container .form-control,
        .filters-container .custom-select {
            border-radius: 5px;
        }
        /* Queue Checkboxes */
        .queue-checkbox-container {
            column-count: 2;
        }
        .queue-checkbox-container .form-check {
            break-inside: avoid;
        }
        /* Chart Containers */
        .chart-container {
            background-color: #fff;
            padding: 25px;
            margin-bottom: 30px;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.05);
        }
        .chart-container h4 {
            font-size: 22px;
            font-weight: 600;
            margin-bottom: 25px;
            color: #333;
        }
        /* Loading Spinner */
        #loading-spinner {
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            z-index: 9999;
            background-color: rgba(255,255,255,0.8);
            padding: 20px;
            border-radius: 10px;
        }
        /* Error Message */
        #error-message {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 9999;
            max-width: 350px;
        }
        /* Buttons */
        .btn {
            border-radius: 5px;
            padding: 8px 16px;
        }
        .btn-primary {
            background-color: #241c80; /* Changed color */
            border-color: #241c80;
        }
        .btn-primary:hover {
            background-color: #1e196b; /* Slightly darker shade */
            border-color: #1e196b;
        }
        .btn-outline-secondary {
            color: #6c757d;
            border-color: #6c757d;
        }
        .btn-outline-secondary:hover {
            color: #fff;
            background-color: #6c757d;
            border-color: #6c757d;
        }
        /* Spinner Color */
        .spinner-border.text-primary {
            color: #241c80; /* Changed color */
        }
        /* Responsive Design */
        @media (max-width: 767.98px) {
            .kpi-card {
                margin-bottom: 15px;
            }
            .filters-container {
                margin-bottom: 20px;
            }
            .chart-container {
                padding: 20px;
                margin-bottom: 20px;
            }
            .chart-container h4 {
                font-size: 20px;
                margin-bottom: 20px;
            }
        }
    </style>
</head>
<body>
<!-- Navbar -->
<div style="background-color: #241c80">
    <los:pageheader/>
</div>
<div class="dashboard-container">
    <div class="main-content">
        <div class="dashboard-header">
            <h2>Loan Analytics</h2>
        </div>

        <!-- Hidden input for initial data -->
        <input type="hidden" id="initialData" value='${fn:escapeXml(requestScope.initialData)}'/>

        <!-- Error Message -->
        <div id="error-message" class="alert alert-danger alert-dismissible fade show" role="alert" style="display: none;">
            <span id="error-text"></span>
            <button type="button" class="close" aria-label="Close" onclick="hideError()">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>

        <!-- Loading Spinner -->
        <div id="loading-spinner" class="text-center" style="display: none;">
            <div class="spinner-border text-primary" role="status">
                <span class="sr-only"> </span>
            </div>
        </div>

        <!-- Form for Filters -->
        <form id="filterForm">
            <div class="row">
                <!-- KPI Cards -->
                <div class="col-md-6">
                    <div class="row">
                        <!-- Total Loans -->
                        <div class="col-md-6">
                            <div class="kpi-card">
                                <div class="kpi-title">Total Loans</div>
                                <div class="kpi-value" id="total-loans">0</div>
                            </div>
                        </div>
                        <!-- Total Loan Amount -->
                        <div class="col-md-6">
                            <div class="kpi-card">
                                <div class="kpi-title">Total Loan Amount</div>
                                <div class="kpi-value" id="total-loan-amount">₹0</div>
                            </div>
                        </div>
                        <!-- Average Loan Amount -->
                        <div class="col-md-6">
                            <div class="kpi-card">
                                <div class="kpi-title">Average Loan Amount</div>
                                <div class="kpi-value" id="avg-loan-amount">₹0</div>
                            </div>
                        </div>
                        <!-- Average TAT -->
                        <div class="col-md-6">
                            <div class="kpi-card">
                                <div class="kpi-title">Average TAT (Hours)</div>
                                <div class="kpi-value" id="avg-tat">0</div>
                            </div>
                        </div>
                        <!-- Sanctioned Loans -->
                        <div class="col-md-6">
                            <div class="kpi-card">
                                <div class="kpi-title">Sanctioned Loans</div>
                                <div class="kpi-value" id="sanctioned-loans">0</div>
                            </div>
                        </div>
                        <!-- Disbursed Loans -->
                        <div class="col-md-6">
                            <div class="kpi-card">
                                <div class="kpi-title">Disbursed Loans</div>
                                <div class="kpi-value" id="disbursed-loans">0</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Filters -->
                <div class="col-md-6">
                    <div class="filters-container">
                        <div class="row">
                            <!-- Date Range -->
                            <div class="col-md-6">
                                <label for="date-range">Date Range:</label>
                                <input type="text" id="date-range" class="form-control form-control-sm" placeholder="Select date range">
                            </div>
                            <!-- Aggregation -->
                            <div class="col-md-6">
                                <label>Aggregation:</label>
                                <div class="btn-group btn-group-toggle d-flex" data-toggle="buttons">
                                    <label class="btn btn-outline-primary btn-sm flex-fill">
                                        <input type="radio" name="aggregation" value="daily" autocomplete="off"> Daily
                                    </label>
                                    <label class="btn btn-outline-primary btn-sm flex-fill active">
                                        <input type="radio" name="aggregation" value="monthly" autocomplete="off"> Monthly
                                    </label>
                                    <label class="btn btn-outline-primary btn-sm flex-fill">
                                        <input type="radio" name="aggregation" value="yearly" autocomplete="off"> Yearly
                                    </label>
                                </div>
                            </div>
                            <!-- Loan Amount Slider -->
                            <div class="col-md-12 mt-3">
                                <label for="loan-amount-slider">Loan Amount Range:</label>
                                <div id="loan-amount-slider"></div>
                                <div class="d-flex justify-content-between mt-2">
                                    <span id="min-loan-amount">₹0</span>
                                    <span id="max-loan-amount">₹2,000,000</span>
                                </div>
                            </div>
                            <!-- Queues -->
                            <div class="col-md-12 mt-3">
                                <label for="queue-checkboxes">Queues:</label>
                                <div id="queue-checkboxes" class="queue-checkbox-container mt-2"></div>
                            </div>
                            <!-- Action Buttons -->
                            <div class="col-md-12 mt-3 text-right">
                                <button id="reset-filters" type="button" class="btn btn-outline-secondary btn-sm">Reset Filters</button>
                                <button id="export-data" type="button" class="btn btn-primary btn-sm">Export Data</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </form>

        <!-- Charts -->
        <div class="row">
            <!-- Monthly Statistics Chart -->
            <div class="col-md-6">
                <div class="chart-container">
                    <h4>Application Statistics</h4>
                    <div id="monthly-stats-chart"></div>
                </div>
            </div>
            <!-- Queue Processing Time Chart -->
            <div class="col-md-6">
                <div class="chart-container">
                    <h4>Queue Processing Time</h4>
                    <div id="queue-processing-chart"></div>
                </div>
            </div>
        </div>
        <div class="row">
            <!-- Loan Status Distribution Chart -->
            <div class="col-md-6">
                <div class="chart-container">
                    <h4>Loan Status Distribution</h4>
                    <div id="loan-status-chart"></div>
                </div>
            </div>
            <!-- Loan Performance Chart -->
            <div class="col-md-6">
                <div class="chart-container">
                    <h4>Loan Performance</h4>
                    <div id="loan-performance-chart"></div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Drill-Down Modal -->
<div class="modal fade" id="drillDownModal" tabindex="-1" role="dialog" aria-labelledby="drillDownModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header" style="background-color: #241c80; color: #fff;">
                <h5 class="modal-title">Detailed View</h5>
                <button type="button" class="close" aria-label="Close" onclick="closeDrillDownModal()">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <!-- Container for the drill-down chart -->
                <div id="drill-down-chart"></div>
            </div>
        </div>
    </div>
</div>

<!-- Include JS Libraries -->
<script src="assets/js/jquery/jquery.min.js"></script>
<script src="assets/js/bootstrap/bootstrap.bundle.min.js"></script>
<script src="assets/js/vendor/apexcharts.min.js"></script>
<script src="assets/js/vendor/flatpickr.js"></script>
<script src="assets/js/vendor/nouislider.min.js"></script>
<script src="assets/js/vendor/wNumb.min.js"></script>

<!-- Custom JavaScript -->
<script src="assets/js/custom/chart-script.js"></script>
</body>
</html>