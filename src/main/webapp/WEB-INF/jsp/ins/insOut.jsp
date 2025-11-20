<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.VehicleLoanMasterDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.VehicleLoanApplicantDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.AllotmentDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.UserSelectDTO" %>
<%@ page import="com.sib.ibanklosucl.model.EligibilityDetails" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanDetails" %>
<%@ page import="com.sib.ibanklosucl.model.doc.VehicleLoanChargeWaiver" %>
<%@ page import="com.sib.ibanklosucl.repository.doc.VehicleLoanFeeWaiverRepository" %>
<%@ page import="java.util.Comparator" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ taglib prefix="los" uri="http://www.siblos.com/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en" data-bs-theme="light">
<head>
    <meta charset="UTF-8">
    <title>SIB</title>
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

    <script src="assets/js/jquery/jquery.min.js"></script>
    <script src="assets/js/jquery/jquery.validate.min.js"></script>
    <script src="assets/js/jquery/additional-methods.min.js"></script>
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
    <script src="assets/js/vendor/forms/selects/select2.min.js"></script>
    <script src="assets/demo/pages/form_select2.js"></script>

    <script src="assets/js/app.js"></script>
    <script src="assets/js/vendor/notifications/sweet_alert.min.js"></script>
    <style>
        :root {
            --primary-color: #009EF7;
            --success-color: #50CD89;
            --warning-color: #FFC700;
            --danger-color: #F1416C;
            --dark-color: #181C32;
            --gray-100: #F9F9F9;
            --gray-200: #F1F1F2;
            --gray-300: #E1E3EA;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
        }

        body {
            background-color: #f5f8fa;
            color: var(--dark-color);
            line-height: 1.5;
        }

        .header {
            background: white;
            padding: 1rem 0;
            box-shadow: 0 0 20px rgba(0,0,0,0.05);
            position: sticky;
            top: 0;
            z-index: 100;
        }

        .progress-bar {
            height: 4px;
            background: linear-gradient(to right, var(--primary-color), var(--success-color));
            width: 0;
            position: fixed;
            top: 0;
            left: 0;
            z-index: 1000;
            transition: width 0.3s;
        }

        .container {
            max-width: 1320px;
            margin: 0 auto;
            padding: 0 1rem;
        }

        .content {
            padding: 2rem 0;
        }

        .row {
            display: flex;
            flex-wrap: wrap;
            margin: -0.75rem;
        }

        .col-6 {
            flex: 0 0 50%;
            padding: 0.75rem;
        }

        .card {
            background: white;
            border-radius: 0.625rem;
            box-shadow: 0 0 20px rgba(0,0,0,0.05);
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }

        .card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }

        .card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 4px;
            background: linear-gradient(to right, var(--primary-color), var(--success-color));
            transform: scaleX(0);
            transition: transform 0.3s ease;
            transform-origin: left;
        }

        .card:hover::before {
            transform: scaleX(1);
        }

        .card-header {
            padding: 1.5rem 2rem;
            border-bottom: 1px solid var(--gray-200);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .card-body {
            padding: 2rem;
        }

        .badge {
            padding: 0.5rem 1rem;
            border-radius: 0.475rem;
            font-weight: 600;
            font-size: 0.85rem;
            position: relative;
            overflow: hidden;
        }

        .badge-success {
            background: rgba(80, 205, 137, 0.1);
            color: var(--success-color);
        }

        .badge-success::after {
            content: '';
            position: absolute;
            top: 50%;
            left: 50%;
            width: 120%;
            height: 120%;
            background: rgba(80, 205, 137, 0.2);
            border-radius: 50%;
            transform: translate(-50%, -50%) scale(0);
            animation: pulse 2s infinite;
        }

        @keyframes pulse {
            0% { transform: translate(-50%, -50%) scale(0); opacity: 1; }
            100% { transform: translate(-50%, -50%) scale(1); opacity: 0; }
        }

        .table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0 0.5rem;
        }

        .table tr {
            transition: transform 0.3s ease;
            cursor: pointer;
        }

        .table tr:hover {
            transform: scale(1.02);
        }

        .table td {
            padding: 1.25rem;
            background: var(--gray-100);
         //   border-radius: 0.475rem;
            position: relative;
            overflow: hidden;
        }

        .table td::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.05);
            opacity: 0;
            transition: opacity 0.3s;
        }

        .table tr:hover td::after {
            opacity: 1;
        }

        .highlight {
            position: relative;
            animation: highlightPulse 2s infinite;
        }

        @keyframes highlightPulse {
            0% { opacity: 1; }
            50% { opacity: 0.7; }
            100% { opacity: 1; }
        }

        .diff-indicator {
            display: inline-flex;
            align-items: center;
            padding: 0.25rem 0.5rem;
            border-radius: 0.375rem;
            font-size: 0.875rem;
            margin-left: 0.5rem;
            transition: transform 0.3s ease;
        }

        .diff-indicator:hover {
            transform: scale(1.1);
        }

        .diff-indicator i {
            margin-right: 0.25rem;
        }

        .savings-banner {
            background: linear-gradient(45deg, var(--success-color), var(--primary-color));
            color: white;
            padding: 1.5rem 2rem;
            border-radius: 0.625rem;
            margin-bottom: 2rem;
            display: flex;
            align-items: center;
            justify-content: space-between;
            position: relative;
            overflow: hidden;
        }

        .savings-banner::before {
            content: '';
            position: absolute;
            top: -50%;
            left: -50%;
            width: 200%;
            height: 200%;
            background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
            animation: rotate 10s linear infinite;
        }

        @keyframes rotate {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        .feature-comparison {
            margin-top: 2rem;
            background: white;
            border-radius: 0.625rem;
            padding: 2rem;
            box-shadow: 0 0 20px rgba(0,0,0,0.05);
        }

        .feature-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1.5rem;
            margin-top: 1.5rem;
        }

        .feature-item {
            display: flex;
            align-items: flex-start;
            padding: 1rem;
            border-radius: 0.475rem;
            background: var(--gray-100);
            transition: transform 0.3s ease;
        }

        .feature-item:hover {
            transform: translateY(-5px);
        }

        .feature-icon {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: var(--primary-color);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 1rem;
            flex-shrink: 0;
        }

        .calculator-widget {
            position: fixed;
            bottom: 2rem;
            right: 2rem;
            background: white;
            border-radius: 0.625rem;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
            padding: 1.5rem;
            width: 300px;
            transform: translateY(120%);
            transition: transform 0.3s ease;
            z-index: 1000;
        }

        .calculator-widget.active {
            transform: translateY(0);
        }

        .calculator-toggle {
            position: fixed;
            bottom: 2rem;
            right: 2rem;
            width: 60px;
            height: 60px;
            border-radius: 50%;
            background: var(--primary-color);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            z-index: 1001;
            box-shadow: 0 5px 20px rgba(0,0,0,0.2);
            transition: transform 0.3s ease;
        }

        .calculator-toggle:hover {
            transform: scale(1.1);
        }

        .floating-help {
            position: fixed;
            left: 2rem;
            bottom: 2rem;
            background: white;
            border-radius: 0.625rem;
            padding: 1rem;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
            display: flex;
            align-items: center;
            cursor: pointer;
            transition: transform 0.3s ease;
        }

        .floating-help:hover {
            transform: translateY(-5px);
        }

        .opt-out-btn {
            background: var(--danger-color);
            color: white;
            border: none;
            padding: 0.75rem 1.5rem;
            border-radius: 0.475rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
        }

        .opt-out-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(241, 65, 108, 0.3);
        }

        .confirmation-dialog {
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: white;
            padding: 2rem;
            border-radius: 0.625rem;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            z-index: 1000;
            max-width: 400px;
            width: 90%;
            display: none;
        }

        .confirmation-dialog.active {
            display: block;
            animation: fadeIn 0.3s ease;
        }

        .dialog-overlay {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0,0,0,0.5);
            z-index: 999;
            display: none;
        }

        .dialog-overlay.active {
            display: block;
            animation: fadeIn 0.3s ease;
        }

        .dialog-buttons {
            display: flex;
            gap: 1rem;
            margin-top: 1.5rem;
        }

        .dialog-btn {
            flex: 1;
            padding: 0.75rem;
            border-radius: 0.475rem;
            border: none;
            cursor: pointer;
            font-weight: 600;
            transition: all 0.3s ease;
        }

        .dialog-btn-cancel {
            background: var(--gray-200);
            color: var(--dark-color);
        }

        .dialog-btn-confirm {
            background: var(--danger-color);
            color: white;
        }

        .dialog-btn:hover {
            transform: translateY(-2px);
        }

        .floating-help i {
            margin-right: 0.5rem;
            color: var(--primary-color);
        }

        @media (max-width: 768px) {
            .col-6 {
                flex: 0 0 100%;
            }

            .calculator-widget {
                width: 100%;
                bottom: 0;
                right: 0;
                border-radius: 0.625rem 0.625rem 0 0;
            }

            .calculator-toggle {
                bottom: 1rem;
                right: 1rem;
            }

            .floating-help {
                left: 1rem;
                bottom: 1rem;
            }
        }

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
<body>
<div class="progress-bar" id="progressBar"></div>
<!-- Page header -->
<los:pageheader/>
<!-- /page header -->

<div class="content">
    <div class="container">
        <div class="savings-banner animate__animated animate__fadeIn">
            <div>
                <h2 style="margin-bottom: 0.5rem;"> Opt Out of Insurance</h2>
<%--                <p>Get more benefits with insurance-backed loan</p>--%>
            </div>
<%--            <div style="text-align: right;">--%>
<%--                <div style="font-size: 2rem; margin-bottom: 0.25rem;">₹50,000</div>--%>
<%--                <div style="font-size: 0.875rem; opacity: 0.9;">Total Savings</div>--%>
<%--            </div>--%>
        </div>

        <!-- Cards row -->
        <div class="row">
            <%
            EligibilityDetails details = (EligibilityDetails) request.getAttribute("eligibilityDetails");
            Long slno = (Long) request.getAttribute("slno");

            if (details == null) {
            %>
            <h2>No eligibility details available</h2>
<%
        } else {
        String vehicleAmount = details.getVehicleAmt() != null ? details.getVehicleAmt().toString() : "-";
        String ltvAmount = details.getLtvAmt() != null ? details.getLtvAmt().toString() : "-";
        String addltvAmount = details.getAddLtvAmt() != null ? details.getAddLtvAmt().toString() : "-";
        String tenor = String.valueOf(details.getTenor());
        String programEligibleAmount = details.getProgramEligibleAmt() != null ? details.getProgramEligibleAmt().toString() : "-";
        String emi = details.getEmi() != null ? details.getEmi().toString() : "-";
        String ltvPercent = details.getLtvPer() != null ? details.getLtvPer().toString() : "";
        String dealerAmt = details.getDealerAmt() != null ? details.getDealerAmt().toString() : "-";
        String insAmt = details.getInsAmt() != null ? details.getInsAmt().toString() : "-";
        String sancLoan=details.getSancAmountRecommended()!=null? details.getSancAmountRecommended().toString():"";
        String sancRoi= details.getSancCardRate()!=null? details.getSancCardRate().toString():"";

            EligibilityDetails insData = (EligibilityDetails) request.getAttribute("insData");
            String ltvAmount_ = insData.getLtvAmt() != null ? insData.getLtvAmt().toString() : "-";
            String addltvAmount_ = insData.getAddLtvAmt() != null ? insData.getAddLtvAmt().toString() : "-";
            String programEligibleAmount_ = insData.getProgramEligibleAmt() != null ? insData.getProgramEligibleAmt().toString() : "-";
            String emi_ = insData.getEmi() != null ? insData.getEmi().toString() : "-";
            String ltvPercent_ = insData.getLtvPer() != null ? insData.getLtvPer().toString() : "";
            String dealerAmt_ = insData.getDealerAmt() != null ? insData.getDealerAmt().toString() : "-";
            String insAmt_ = insData.getInsAmt() != null ? insData.getInsAmt().toString() : "-";
            String sancLoan_=insData.getEligibleLoanAmt()!=null? insData.getEligibleLoanAmt().toString():"";

            List<VehicleLoanChargeWaiver> feeData = (  List<VehicleLoanChargeWaiver>) request.getAttribute("feeData");
            List<VehicleLoanChargeWaiver> feeDataNew = (  List<VehicleLoanChargeWaiver>) request.getAttribute("feeDataNew");

            feeData=feeData.stream().sorted(Comparator.comparing(VehicleLoanChargeWaiver::getFeeName)).collect(Collectors.toList());
            feeDataNew=feeDataNew.stream().sorted(Comparator.comparing(VehicleLoanChargeWaiver::getFeeName)).collect(Collectors.toList());



%>
        <!-- With Insurance Card -->
            <div class="col-6">
                <div class="card animate__animated animate__fadeInLeft">
                    <div class="card-header">
                        <h3>With Insurance</h3>
                        <span class="badge badge-success">Recommended</span>
                    </div>
                    <div class="card-body">
                        <table class="table">
                            <tr>
                                <td>Vehicle On Road Price</td>
                                <td class="text-success highlight">
                                    ₹<%=vehicleAmount%>/-
                                </td>
                            </tr>
                            <tr>
                                <td>Sanctioned Roi</td>
                                <td class="text-success highlight">
                                    <%=sancRoi%>%
                                </td>
                            </tr>
                            <tr>
                                <td>Sanctioned Tenor</td>
                                <td class="text-success highlight">
                                    <%=tenor%> months
                                </td>
                            </tr>
                            <tr>
                                <td>LTV Amount</td>
                                <td class="text-success highlight">
                                    ₹<%=ltvAmount%>/-
                                </td>
                            </tr>
                            <tr>
                                <td>Additional LTV Amount</td>
                                <td class="text-success highlight">
                                    ₹<%=addltvAmount%>/-
                                </td>
                            </tr>
                            <tr>
                                <td>LTV Percentage</td>
                                <td class="text-success highlight">
                                    <%=ltvPercent%>%
                                </td>
                            </tr>
                            <tr>
                                <td>Program Based Eligibility</td>
                                <td class="text-success highlight">
                                    ₹<%=programEligibleAmount%>/-
                                </td>
                            </tr>

                            <tr>
                                <td>Sanctioned Loan Amount</td>
                                <td class="text-success highlight">
                                    ₹<%=sancLoan%>/-
                                </td>
                            </tr>
                            <tr>
                                <td>Amount to Dealer</td>
                                <td class="text-success highlight">
                                    ₹<%=dealerAmt%>/-
                                </td>
                            </tr>
                            <tr>
                                <td>Amount to Insurance</td>
                                <td class="text-success highlight">
                                    ₹<%=insAmt%>/-
                                </td>
                            </tr>

                            <tr>
                                <td>EMI</td>
                                <td class="text-success highlight">₹<%=emi%>/-</td>
                            </tr>
                            <%
                                for(VehicleLoanChargeWaiver waiver:feeData){
                                %>
                                <tr>
                                    <td><%=waiver.getFeeName()%></td>
                                    <td class="text-success highlight">
                                        ₹<%=waiver.getFinalFee()%>/-
                                    </td>
                                </tr>
                            <%
                            }%>

                        </table>
                        <div class="text-center mt-4 pb-4">

                        </div>
                    </div>
                </div>
            </div>

            <!-- Without Insurance Card -->
            <div class="col-6">
                <div class="card animate__animated animate__fadeInRight">
                    <div class="card-header">
                        <h3>Without Insurance</h3>
                    </div>
                    <div class="card-body">
                        <table class="table">
                            <tr>
                                <td>Vehicle On Road Price</td>
                                <td class="text-success highlight">
                                    ₹<%=vehicleAmount%>/-
                                </td>
                            </tr>
                            <tr>
                                <td>Sanctioned Roi</td>
                                <td class="text-success highlight">
                                    <%=sancRoi%>%
                                </td>
                            </tr>
                            <tr>
                                <td>Sanctioned Tenor</td>
                                <td class="text-success highlight">
                                    <%=tenor%> months
                                </td>
                            </tr>
                            <tr>
                                <td>LTV Amount</td>
                                <td class="text-success highlight">
                                    ₹<%=ltvAmount_%>/-
                                </td>
                            </tr>
                            <tr>
                                <td>Additional LTV Amount</td>
                                <td class="text-danger">
                                    ₹<%=addltvAmount_%>/-
                                </td>
                            </tr>
                            <tr>
                                <td>LTV Percentage</td>
                                <td class="text-success highlight">
                                    <%=ltvPercent_%>%
                                </td>
                            </tr>
                            <tr>
                                <td>Program Based Eligibility</td>
                                <td class="text-success highlight">
                                    ₹<%=programEligibleAmount_%>/-
                                </td>
                            </tr>

                            <tr>
                                <td><b>Eligible Loan Amount</b></td>
                                <td class="text-danger">
                                    ₹<%=sancLoan_%>/-
                                </td>
                            </tr>
                            <tr>
                                <td>Amount to Dealer</td>
                                <td class="text-danger">
                                    ₹<%=dealerAmt_%>/-
                                </td>
                            </tr>
                            <tr>
                                <td>Amount to Insurance</td>
                                <td class="text-danger">
                                    ₹<%=insAmt_%>/-
                                </td>
                            </tr>
                            <tr>
                                <td>EMI</td>
                                <td class="text-danger">₹<%=emi_%>/-</td>
                            </tr>
                            <%
                                for(VehicleLoanChargeWaiver waiver:feeDataNew){
                            %>
                            <tr>
                                <td><%=waiver.getFeeName()%></td>
                                <td class="text-danger">
                                    ₹<%=waiver.getFinalFee()%>/-
                                </td>
                            </tr>
                            <%
                                }%>

                        </table>
                        <div class="text-center mt-4">
                            <button class="opt-out-btn" onclick="confirmOptOut()">
                                <i class="fas fa-times-circle"></i> Opt Out of Insurance
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
<%}%>
</div>

<script>
    // Scroll Progress Bar
    window.onscroll = function() {
        let winScroll = document.body.scrollTop || document.documentElement.scrollTop;
        let height = document.documentElement.scrollHeight - document.documentElement.clientHeight;
        let scrolled = (winScroll / height) * 100;
        document.getElementById("progressBar").style.width = scrolled + "%";
    };
    $('#backbtn').on('click', function (e) {
        e.preventDefault();
        $('#backform').attr('action','dashboard');
        $('#backform').attr('method','GET');
        $('#backform').submit();
    });

function confirmOptOut(){
    var slno=<%=slno%>;
    confirmmsg("Kindly Confirm whether to Opt out of Insurance?").then(function (confirmed) {
        if (confirmed) {
            var jsonBody = {
                slno: slno
            };
            $.ajax({
                url: 'api/ins-opt-out',
                type: 'POST',
                data: JSON.stringify(jsonBody),
                async: false,
                contentType: 'application/json',
                success: function (response) {
                    if (response.status === 'S') {
                        confirmmsg('Record Saved Successfully ').then(function (confirmed) {
                            $('#backform').attr('action','dashboard');
                            $('#backform').attr('method','GET');
                            $('#backform').submit();
                        });
                    }
                     else {
                     alertmsg('Failed: ' + response.msg);
                    }
                },
                error: function (error) {
                    alertmsg(error.responseJSON.message);
                    hideLoader();
                }
            });
        }
    });
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

    function alertmsg(Msg) {
        swalInit.fire({
            html:
                '<div class="d-inline-flex p-2 mb-3 mt-1">' +
                '<img src="assets/images/siblogo.png" class="h-48px" alt="logo">' +
                '</div>' +
                '<h5 class="card-title">' + Msg + '</h5>' +
                '</div>',
            showCloseButton: true
        });
    }
    function confirmmsg(msg) {
        return swalInit.fire({
            title: msg,
            icon: "warning",
            showCancelButton: false,
            confirmButtonText: "OK",
            button: "close!"
        }).then(function (result) {
            return result.isConfirmed;
        });
}


    // Add animation on scroll
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate__animated', 'animate__fadeIn');
            }
        });
    });

    document.querySelectorAll('.feature-item').forEach((el) => observer.observe(el));
</script>
</body>
</html>
