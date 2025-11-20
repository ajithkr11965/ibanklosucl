<%--
  Created by IntelliJ IDEA.
  User: SIBL12071
  Date: 16-08-2024
  Time: 18:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.dto.ResponseDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="com.sib.ibanklosucl.model.VLEmployment" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.sib.ibanklosucl.dto.acopn.SanctionDetailsDTO" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="los" uri="http://www.siblos.com/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en" dir="ltr">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <title>SIB-LOS BOG</title>

    <!-- Global stylesheets -->
    <link href="assets/fonts/inter/inter.css" rel="stylesheet" type="text/css">
    <link href="assets/icons/phosphor/styles.min.css" rel="stylesheet" type="text/css">
    <link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
    <link href="assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
    <link href="assets/css/custom.css" rel="stylesheet" type="text/css" />
    <link href="assets/css/custom/wicreate.css" rel="stylesheet" type="text/css" />
    <link href="assets/css/custom/wirmmodify.css" rel="stylesheet" type="text/css" />
    <link href="assets/css/style.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
    <link href="assets/plugins/global/plugins.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
    <!-- /global stylesheets -->

    <!-- Core JS files -->

    <script src="assets/js/bootstrap/bootstrap.bundle.min.js"></script>
    <script src="assets/js/jquery/jquery.min.js"></script>

    <script src="assets/js/vendor/forms/validation/validate.min.js"></script>
    <script src="assets/js/vendor/forms/validation/additional_methods.min.js"></script>
    <script src="assets/js/vendor/split.min.js"></script>
    <!-- /core JS files -->
    <script src="assets/demo/pages/components_tooltips.js"></script>
    <!-- Theme JS files -->
    <script src="assets/js/app.js"></script>
    <script src="assets/js/vendor/uploaders/fileinput/fileinput.min.js"></script>
    <script src="assets/js/vendor/uploaders/fileinput/plugins/sortable.min.js"></script>
    <script src="assets/js/vendor/notifications/sweet_alert.min.js"></script>
    <script src="assets/js/vendor/notifications/noty.min.js"></script>
    <!-- /theme JS files -->
<style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 10px;
            background-color: #f0f2f5;
            color: #333;
            font-size: 14px;
        }
        .dashboard-container {
            max-width: 100%;
            background-color: #ffffff;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.12);
            overflow: hidden;
        }
        .dashboard-header {
            background-color: #28173c; /* Dark blue color for the header */
            padding: 15px 20px;
            border-bottom: 1px solid #34495e;
        }
        .dashboard-title {
            font-size: 1.3em;
            font-weight: 600;
            margin: 0;
            color: #ffffff; /* White text for the title */
        }
        .dashboard-row {
            display: flex;
            flex-wrap: wrap;
            border-bottom: 1px solid #e0e0e0;
        }
        .dashboard-column {
            flex: 1;
            min-width: 200px;
            padding: 10px;
        }
        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 10px;
        }
        .info-item.dealer-name {
            grid-column: 1 / -1; /* Make it span full width */
        }

    .info-item.dealer-name .value {
        white-space: nowrap;  /* Prevent line breaks */
        overflow: hidden;     /* Hide overflow */
        text-overflow: ellipsis; /* Show ... for overflow */
    }

        .info-item {
            display: flex;
            flex-direction: column;
        }
        .info-block {
            background-color: #f8f9fa;
            border: 1px solid #e0e0e0;
            border-radius: 4px;
            padding: 10px;
            margin-bottom: 10px;
        }
        .block-title {
            font-weight: 600;
            margin-bottom: 5px;
            color: #555;
        }
        .label {
            font-size: 0.85em;
            color: #666;
            margin-bottom: 2px;
        }
        .value {
            font-weight: 600;
            color: #333;
        }
        .status-pill {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 12px;
            font-size: 0.85em;
            font-weight: 600;
        }
        .status-active { background-color: #e8f5e9; color: #1b5e20; }
        .status-pending { background-color: #fff3e0; color: #e65100; }
        .status-critical { background-color: #ffebee; color: #b71c1c; }
        .status-approved { background-color: #e8f5e9; color: #1b5e20; }
        .status-rejected { background-color: #ffebee; color: #b71c1c; }
        @media (max-width: 768px) {
            .dashboard-row {
                flex-direction: column;
            }
            .dashboard-column {
                width: 100%;
            }
        }

        #remarkHist {
            cursor: pointer; /* Changes pointer to hand on hover */
            padding: 15px; /* Adds padding for better spacing */
            border-radius: 8px; /* Rounds the corners */
            background-color: #f9f9f9; /* Light background for clarity */
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); /* Subtle shadow for depth */
            transition: transform 0.3s ease, box-shadow 0.3s ease; /* Smooth hover transitions */
            padding-bottom: 0px !important;
        }
        #remarkHist:hover {
            background-color: #e0f7fa; /* Light color change on hover */
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2); /* Intensify shadow on hover */
            transform: translateY(-3px); /* Lift effect on hover */
        }
        .utr-number {
    font-family: monospace;
    font-weight: bold;
    padding: 4px 8px;
    border-radius: 4px;
    background-color: #f0f7ff;  /* Light blue background */
    border: 1px solid #cce5ff;  /* Lighter blue border */
    color: #0066cc;            /* Dark blue text */
    letter-spacing: 1px;       /* Spacing between characters */
}

.utr-number.success {
    background-color: #e8f5e9;  /* Light green background */
    border: 1px solid #c8e6c9;  /* Lighter green border */
    color: #2e7d32;            /* Dark green text */
}

.utr-number.pending {
    background-color: #fff3e0;  /* Light orange background */
    border: 1px solid #ffe0b2;  /* Lighter orange border */
    color: #ef6c00;            /* Dark orange text */
}

.utr-number.failed {
    background-color: #ffebee;  /* Light red background */
    border: 1px solid #ffcdd2;  /* Lighter red border */
    color: #c62828;            /* Dark red text */
}
.copy-btn:hover {
    opacity: 0.7;
}

.utr-container {
    position: relative;
}
</style>
</head>

<body>
<%--<los:header/>--%>
<los:loader/>


<!-- Page header -->
<los:pageheader/>
<!-- /page header -->

<div class="navbar navbar-expand-lg shadow rounded py-1 mb-3">
    <div class="container-fluid">


        <%
            boolean isSuperUser=false;

            if (request.getAttribute("employee") != null) {
                Employee employee = (Employee) request.getAttribute("employee");
                isSuperUser=employee.isSuperUser();
            }
            String activeTab=(String) request.getAttribute("activeTab");
            Boolean vehicleAvlbl=request.getAttribute("vehicleDetails")!=null;
            Employee userdt= (Employee) request.getAttribute("userdata");
            List<String> accessibleMenus = (List<String>) request.getAttribute("accessibleMenus");
            VehicleLoanMaster vehicleLoanMaster=(VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
            String currentQueue = vehicleLoanMaster.getQueue();
            List<VehicleLoanApplicant> applicantList=Collections.emptyList();
            String sol_desc = (String) request.getAttribute("sol_desc");
            String roname = (String) request.getAttribute("roname");
            String wiProgram = (String) request.getAttribute("wiProgram");
            String doPPC = (String) request.getAttribute("doPPC");
            String lockuser = (String) request.getAttribute("lockuser");
            String loanAcctNo=vehicleLoanMaster.getAccNumber();
            if(lockuser ==null || lockuser.isEmpty()) {
                lockuser="Not Locked";
            }
            List<Map<String, String>> subquePendingInfoList = (List<Map<String, String>>)request.getAttribute("subquePendingInfoList");
            List<Map<String, String>> subqueCIFPendingInfoList = (List<Map<String, String>>)request.getAttribute("subqueCIFPendingInfoList");
            SanctionDetailsDTO sanctionDetails = (SanctionDetailsDTO) request.getAttribute("sanctionDetails");
            Map<String, String> disbursementDetails = (Map<String, String>)request.getAttribute("disbursementDetails");
            List<Map<String, String>> ppcDetails = (List<Map<String, String>>) request.getAttribute("ppcDetails");
            if(vehicleLoanMaster.getApplicants()!=null)
                applicantList = vehicleLoanMaster.getApplicants();
            int coApplicantCount = 1;
            boolean modify=false;
            if(applicantList.size()>0){
                modify=true;
            }
            long coappsize=applicantList.stream().filter(d-> "C".equals(d.getApplicantType())).count();

            String bpmerr="",appurl="";
            ResponseDTO  bpmResp= (ResponseDTO) request.getAttribute("bpm");
            if(bpmResp.getStatus().equals("S"))
                appurl=bpmResp.getMsg();
            else
                bpmerr=bpmResp.getMsg();

            String channel=vehicleLoanMaster.getChannel();
            String stp = vehicleLoanMaster.getStp();
            if(vehicleLoanMaster.getStp()==null) {
                stp="N/A";
            }


    // Helper function to check if a string is null or empty


        %>

        <div class="navbar-collapse collapse order-2 order-lg-1" >
								<span class="navbar-text d-none d-lg-inline-flex align-items-lg=center me-3">
									<i class="ph-receipt me-2"></i>
									Work Item No : <b><%=vehicleLoanMaster.getWiNum()%></b>
                                  <input type="hidden" name="slno" id="slno" value="<%=vehicleLoanMaster.getSlno()%>">
                                  <input type="hidden" name="winum" id="winum" value="<%=vehicleLoanMaster.getWiNum()%>">
                                  <input type="hidden" name="modify" id="modify" value="<%=modify%>">
                                    <input type="hidden" name="search" id="search" value="1">
                                  <input type="hidden" name="lockflg" id="lockflg" value="<%=request.getAttribute("lockflg")%>">
                                  <input type="hidden" name="lockuser" id="lockuser" value="<%=request.getAttribute("lockuser")%>">
                                  <input type="hidden" name="modifycnt" id="modifycnt" value="<%=coappsize%>">
                                  <input type="hidden" id="bpmerror" value="<%=bpmerr%>"/>
     <input type="hidden" id="isCompleted" value="Y"/>
                                    <input type="hidden" id="queue" value="<%=currentQueue%>"/>
                                    <input type="hidden" name="currentQueue" id="currentQueue" value="<%=currentQueue%>">

                                  <input type="hidden" id="activeTab" value="<%=activeTab%>"/>

<%--                                  <input type="hidden" id="currentTab" value="<%=vehicleLoanMaster.getCurrentTab()%>"/>--%>


                                  <input type="hidden" id="currentTab" value="A-1"/>

<%--                                  <input type="hidden" id="currentTab" value="A-6"/>--%>
								</span>
            <%--            <button type="button" id="checkButton">Check Save Buttons</button>--%>
        </div>



    </div>
    <div class="container-fluid">

        <div class="navbar-collapse collapse order-2 order-lg-1" >
								<span class="navbar-text d-none d-lg-inline-flex align-items-lg=center me-3">
									<i class="ph-house me-2"></i>
									Branch : <b><%=vehicleLoanMaster.getSolId()%>( <%=sol_desc%> <%=roname%>)</b>
								</span>
        </div>

    </div>
    <%if(!request.getAttribute("queueName").toString().equals(""))
    {%>
    <div class="container-fluid">

        <div class="navbar-collapse collapse order-2 order-lg-1" >
								<span class="navbar-text d-none d-lg-inline-flex align-items-lg=center me-3">
									<i class="ph-hourglass-simple-medium me-2"></i>
									Currently at : <b><%=request.getAttribute("queueName")%></b>
								</span>
        </div>


    </div>
    <%}%>
    <button id="parentToggle" type="button" class="btn btn-primary ">
        <i class="ph-file-doc"></i>DOCLIST
    </button>
</div>

<!-- Page content -->
<div class="page-content pt-0">

    <!-- Main sidebar -->
    <div class="sidebar sidebar-main sidebar-expand-lg align-self-start">

        <!-- Sidebar content -->
        <div class="sidebar-content">

            <!-- Sidebar header -->
            <div class="sidebar-section">
                <div class="sidebar-section-body d-flex justify-content-center">
                    <h5 class="sidebar-resize-hide flex-grow-1 my-auto">Details</h5>

                    <div>
                        <button type="button" class="btn btn-light btn-icon btn-sm rounded-pill border-transparent sidebar-control sidebar-main-resize d-none d-lg-inline-flex">
                            <i class="ph-arrows-left-right"></i>
                        </button>
                        <button type="button" class="btn btn-light btn-icon btn-sm rounded-pill border-transparent sidebar-mobile-main-toggle d-lg-none">
                            <i class="ph-x"></i>
                        </button>


                    </div>
                </div>
            </div>
            <!-- /sidebar header -->


            <!-- Main navigation -->
            <div class="sidebar-section">
                <ul class="nav nav-sidebar" data-nav-type="accordion">
                    <li class="nav-item">
                        <a href="#" class="nav-link active details" data-code="1">
                            <i class="ph-identification-card"></i>
                            <span>General Details</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="#" class="nav-link details" data-code="2">
                            <i class="ph-identification-card"></i>
                            <span>KYC Details</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="#" class="nav-link details" data-code="3">
                            <i class="ph-user-circle"></i>
                            <span>Basic Details</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="#" class="nav-link details"   data-code="4">
                            <i class="ph-activity"></i>
                            <span>Employment Details</span>
                        </a>
                    </li>
                    <li class="nav-item" >
                        <a href="#" class="nav-link details"   data-code="5">
                            <i class="ph-money"></i>
                            <span>Program Details</span>
                        </a>
                    </li>
                    <li class="nav-item" >
                        <a href="#" class="nav-link details"   data-code="6">
                            <i class="ph-credit-card"></i>
                            <span>Credit check</span>
                        </a>
                    </li>
                    <!-- /layout -->

                </ul>
            </div>
            <!-- /main navigation -->

        </div>
        <!-- /sidebar content -->
        <article title="Car Inspection  3D Icon" class="container_EOFSc" style="display: block;border-radius: 12px;background-color: #FAFAFC;position: relative;height: 22.125rem;background-color: #F5F6FA !important;">
            <picture class="pict">
                <source type="image/webp" srcset="assets/images/car-loan.png?f=webp 1x, assets/images/car-loan.png?f=webp 2x">
                <img alt="Car Inspection  3D Icon" loading="lazy" src="assets/images/car-loan.png?f=webp" srcset="assets/images/car-loan.png?f=webp 1x, assets/images/car-loan.png?f=webp 2x" style="width: 19em;">
            </picture>
        </article>
    </div>
    <!-- /main sidebar -->

    <!-- Main content -->
    <div class="content-wrapper">

        <!-- Content area -->
        <div class="content">
            <!--             SUMMARY VIEW-->
            <div class="dashboard-container card">
                <div class="dashboard-header d-flex">
                    <h1 class="dashboard-title">Workitem Summary</h1>
                    <%if(isSuperUser){%>
                    <a type="button" href="switch"  id="context" class="btn btn-outline-primary ms-auto">

                        <i class="ph-detective "></i>
                    </a>
                    <%}%>
                </div>

                <!-- Rest of the HTML remains the same -->
                <div class="dashboard-row">
                    <div class="dashboard-column">
                        <div class="info-block">
            <div class="block-title">General Details</div>
                        <div class="info-grid">
                            <div class="info-item">
                                <span class="label">Dealing Officer</span>
                                <span class="value"><%=doPPC%></span>
                            </div>
                            <%if(ppcDetails != null && !ppcDetails.isEmpty()) {%>
                                <div class="info-item">
                                    <span class="label">CPC Checker</span>
                                <span class="value"><%=ppcDetails.get(0).get("codedesc")%></span>
                                </div>
                            <%
                            }
                            %>

                            <div class="info-item">
                                <span class="label">Locked By</span>
                                <span class="value"><%=lockuser%></span>
                            </div>
                            <div class="info-item">
                                <span class="label">Processing</span>
                                <span class="value"><%=stp%></span>
                            </div>
                            <div class="info-item">
                                <span class="label">Program</span>
                                <span class="value">
                            <span class="status-pill status-critical"><%=wiProgram%></span>
                        </span>
                            </div>
                            <% if ("PD".equals(currentQueue)) { %>
                            <div class="info-item">
                                <span class="label">Loan Account No</span>
                                <span class="value">
                            <span class="status-pill status-approved"><%=loanAcctNo%></span>
                        </span>
                            </div>
                            <%}%>

                        </div>
                    </div>
                    </div>
                    <% if ("PD".equals(currentQueue)) { %>
    <div class="dashboard-column">
        <div class="info-block">
            <div class="block-title">Disbursement Details</div>
            <div class="info-grid">


                <div class="info-item">
                    <span class="label">NEFT Date (Dealer)</span>
                    <span class="value">
                        <%= isEmpty(disbursementDetails.get("NEFTCMDATEDEALER")) ? "N/A" : disbursementDetails.get("NEFTCMDATEDEALER") %>
                    </span>
                </div>
                <div class="info-item">
    <span class="label">UTR Number (Dealer)</span>
    <span class="value">
        <%
            String utrNo = disbursementDetails.get("UTRNODEALER");
            String neftFlag = disbursementDetails.get("NEFTFLAGDEALER");
            String utrClass = "";

            if ("SUCCESS".equalsIgnoreCase(neftFlag)) {
                utrClass = "success";
            } else if ("PENDING".equalsIgnoreCase(neftFlag)) {
                utrClass = "pending";
            } else if ("FAILED".equalsIgnoreCase(neftFlag)) {
                utrClass = "failed";
            }
        %>
        <% if (!isEmpty(utrNo)) { %>
            <div class="utr-container" style="display: flex; align-items: center; gap: 8px;">
                <span class="utr-number <%= utrClass %>">
                    <%= utrNo %>
                </span>
                <button onclick="copyUTR('<%= utrNo %>')"
                        class="copy-btn"
                        style="background: none; border: none; cursor: pointer; padding: 4px;">
                    <i class="fas fa-copy"></i>  <!-- Assuming you're using Font Awesome -->
                </button>
                <span id="copyTooltip"
                      style="display: none; position: absolute; background: #333; color: white;
                             padding: 4px 8px; border-radius: 4px; font-size: 12px; margin-left: 8px;">
                    Copied!
                </span>
            </div>
        <% } else { %>
            <span>N/A</span>
        <% } %>
    </span>
</div><br/>

                <div class="info-item">
                    <span class="label">NEFT Amount (Dealer)</span>
                    <span class="value">
                        <%= isEmpty(disbursementDetails.get("NEFTAMOUNTDEALER")) ? "N/A" : disbursementDetails.get("NEFTAMOUNTDEALER") %>
                    </span>
                </div>
                <div class="info-item">
                    <span class="label">Dealer Account Number</span>
                    <span class="value">
                        <%= isEmpty(disbursementDetails.get("DEALERACCNUM")) ? "N/A" : disbursementDetails.get("DEALERACCNUM") %>
                    </span>
                </div>
                <div class="info-item">
                    <span class="label">Dealer IFSC</span>
                    <span class="value">
                        <%= isEmpty(disbursementDetails.get("DEALERIFSC")) ? "N/A" : disbursementDetails.get("DEALERIFSC") %>
                    </span>
                </div>
                <div class="info-item dealer-name">
    <span class="label">Dealer Name</span>
    <span class="value">
        <%= isEmpty(disbursementDetails.get("DEALERNAME")) ? "N/A" : disbursementDetails.get("DEALERNAME") %>
    </span>
</div>

            </div>
        </div>
    </div>
<% } %>
                    <div class="dashboard-column">
                        <div class="info-block">
                            <div class="block-title">Loan Details</div>
                            <div class="info-grid">
                                <div class="info-item">
                                    <span class="label">Recommended Sanction Amount</span>
                                    <span class="value">
                            <%= formatCurrency(sanctionDetails.getSancAmountRecommended()) %>
                        </span>
                                </div>
                                <div class="info-item">
                                    <span class="label">Sanction Card Rate</span>
                                    <span class="value">
                            <%= isEmpty(sanctionDetails.getSancCardRate()) ? "N/A" : sanctionDetails.getSancCardRate() + "%" %>
                        </span>
                                </div>
                                <div class="info-item">
                                    <span class="label">Sanctioned EMI</span>
                                    <span class="value">
                            <%= formatCurrency(sanctionDetails.getSancEmi()) %>
                        </span>
                                </div>
                                <div class="info-item">
                                    <span class="label">Sanctioned Tenor</span>
                                    <span class="value">
                            <%= isEmpty(sanctionDetails.getSancTenor()) ? "N/A" : sanctionDetails.getSancTenor() + " months" %>
                        </span>
                                </div>
                                <div class="info-item">
                                    <span class="label">LTV Amount</span>
                                    <span class="value">
                            <%= formatCurrency(sanctionDetails.getLtvAmount()) %>
                        </span>
                                </div>
                                <div class="info-item">
                                    <span class="label">LTV Percentage</span>
                                    <span class="value">
                            <%= isEmpty(sanctionDetails.getLtvPercentage()) ? "N/A" : sanctionDetails.getLtvPercentage() + "%" %>
                        </span>
                                </div>
                                <div class="info-item">
                                    <span class="label">Loan Amount</span>
                                    <span class="value">
                            <%= formatCurrency(sanctionDetails.getLoanAmt()) %>
                        </span>
                                </div>
                                <div class="info-item">
                                    <span class="label">Eligible Loan Amount</span>
                                    <span class="value">
                            <%= formatCurrency(sanctionDetails.getEligibleLoanAmt()) %>
                        </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="dashboard-row">
                    <div class="dashboard-column">
                        <div class="info-block">
                            <div class="block-title">Waivers</div>
                            <div class="info-grid">
                                <%
                                    if (subquePendingInfoList != null && !subquePendingInfoList.isEmpty()) {
                                        for (Map<String, String> waiver : subquePendingInfoList) {
                                            String taskType = waiver.get("TASK_TYPE");
                                            String status = waiver.get("STATUS");
                                %>
                                <div class="info-item">
                                    <span class="label"><%= taskType.replace("_", " ") %></span>
                                    <span class="value">
                                    <span class="status-pill status-<%= status.toLowerCase() %>"><%= status %></span>
                                </span>
                                </div>
                                <div class="info-item">
                                    <span class="label">Decision</span>
                                    <span class="value"><%= waiver.get("DECISION_DESC") %></span>
                                </div>

                                <div class="info-item">
                                    <span class="label">Locked By</span>
                                    <span class="value"><%= waiver.get("LOCKED_BY") %></span>
                                </div>
                                <div class="info-item">
                                    <span class="label">Locked On</span>
                                    <span class="value"><%= waiver.get("LOCKED_ON") %></span>
                                </div>
                                <%
                                    }
                                } else {
                                %>
                                <div class="info-item">
                                    <span class="label">No Pending Waivers</span>
                                    <span class="value">N/A</span>
                                </div>
                                <%
                                    }
                                %>
                            </div>
                        </div>
                    </div>
                    <div class="dashboard-column">
                        <div class="info-block">
                            <div class="block-title">CIF Creation Details</div>
                            <div class="info-grid">
                                <%
                                    if (subqueCIFPendingInfoList != null && !subqueCIFPendingInfoList.isEmpty()) {
                                        for (Map<String, String> cifInfo : subqueCIFPendingInfoList) {
                                %>
                                <div class="info-item">
                                    <span class="label">Queue Status</span>
                                    <span class="value">
                            <span class="status-pill status-<%= cifInfo.get("STATUS").toLowerCase() %>">
                                <%= cifInfo.get("STATUS") %>
                            </span>
                        </span>
                                </div>
                                <div class="info-item">
                                    <span class="label">BOG Decision</span>
                                    <span class="value"><%= cifInfo.get("CIF_DECISION") != null ? cifInfo.get("CIF_DECISION") : "Pending" %></span>
                                </div>

                                <div class="info-item">
                                    <span class="label">CIF ID</span>
                                    <span class="value"><%= cifInfo.get("CIFID") != null ? cifInfo.get("CIFID") : "Not Generated" %></span>
                                </div>

                                <div class="info-item">
                                    <span class="label">CIF Created By</span>
                                    <span class="value"><%= cifInfo.get("CIFUSER") != null ? cifInfo.get("CIFUSER") : "N/A" %></span>
                                </div>

                                <div class="info-item">
                                    <span class="label">CIF Created On</span>
                                    <span class="value"><%= cifInfo.get("CIFDATE") != null ? cifInfo.get("CIFDATE") : "N/A" %></span>
                                </div>

                                <div class="info-item">
                                    <span class="label">CKYC Status</span>
                                    <span class="value">
                            <span class="status-pill status-<%= cifInfo.get("CKYCFLAG") != null && cifInfo.get("CKYCFLAG").equals("Y") ? "completed" : "pending" %>">
                                <%= cifInfo.get("CKYCFLAG") != null && cifInfo.get("CKYCFLAG").equals("Y") ? "Completed" : "Pending" %>
                            </span>
                        </span>
                                </div>

                                <div class="info-item">
                                    <span class="label">CKYC Completed By</span>
                                    <span class="value"><%= cifInfo.get("CKYCUSER") != null ? cifInfo.get("CKYCUSER") : "N/A" %></span>
                                </div>

                                <div class="info-item">
                                    <span class="label">CKYC Completed On</span>
                                    <span class="value"><%= cifInfo.get("CKYCDATE") != null ? cifInfo.get("CKYCDATE") : "N/A" %></span>
                                </div>



                                <div class="info-item">
                                    <span class="label">Decision By</span>
                                    <span class="value"><%= cifInfo.get("CIF_DECISION_USER") != null ? cifInfo.get("CIF_DECISION_USER") : "N/A" %></span>
                                </div>

                                <div class="info-item">
                                    <span class="label">Decision Date</span>
                                    <span class="value"><%= cifInfo.get("CIF_DECISION_DATE") != null ? cifInfo.get("CIF_DECISION_DATE") : "N/A" %></span>
                                </div>

                                <div class="info-item">
                                    <span class="label">Remarks</span>
                                    <span class="value"><%= cifInfo.get("REMARKS") != null ? cifInfo.get("REMARKS") : "N/A" %></span>
                                </div>
                                <div class="info-item">
                                    <span class="label">CPC Workitem</span>
                                    <span class="value"><%= cifInfo.get("CPC_WORKITEM") != null ? cifInfo.get("CPC_WORKITEM") : "N/A" %></span>
                                </div>

                                <%
                                    }
                                } else {
                                %>
                                <div class="info-item">
                                    <span class="label">No CIF Creation Details Available</span>
                                    <span class="value">N/A</span>
                                </div>
                                <%
                                    }
                                %>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
            <!--             SUMMARY VIEW-->


            <!-- Main charts -->
            <div class="d-flex flex-row min-vh-100">
                <div id="appList" class="w-100">

                    <div class="card">
                        <div class="card-header d-flex align-items-center">

                            <div class="row w-100">
                                <div class="col">
                            <h5 class="mb-0">Loan Application</h5>
                                </div>
                                <div class="col-md-2">
                            <div class="bg-info bg-opacity-10 text-info text-center lh-1 rounded-pill p-2" id="remarkHist">
                                <i class="ph-git-branch"></i>
                                <p  class="badge text-info fs-base float-right" style="color: #1f5543;font-size: larger;">
                                    History
                                </p>

                            </div>
                                </div>
                            </div>
                            <div class="ms-auto">
                                <%--                <button type="button" id="addcoapp" class="btn btn-outline-primary ms-auto">--%>
                                <%--                  Co-Applicant--%>
                                <%--                  <i class="fas fa-user-plus ms-2"></i>--%>
                                <%--                </button>--%>

                                <%if(!applicantList.stream().anyMatch(a -> "G".equals(a.getApplicantType()))){%>
                                <%--                <button type="button" id="addguarantor" class="me-2 btn btn-outline-danger ms-auto">--%>
                                <%--                  GUARANTOR--%>
                                <%--                  <i class="fas fa-user-plus ms-2"></i>--%>
                                <%--                </button>--%>
                                <%}%>
                            </div>

                        </div>
                        <ul class="nav nav-tabs nav-justified mb-0" role="tablist" id="loanapp">
                            <%
                                for (VehicleLoanApplicant applicant : applicantList) {
                                    if ("A".equals(applicant.getApplicantType())) {
                            %>
                            <li class="nav-item" role="presentation"><a href="#tab-A" class="nav-link apptype active  alert alert-primary " data-bs-toggle="tab" aria-selected="false" role="tab" tabindex="-1" data-app="A"><i class="ph-user fw-semibold"></i>APPLICANT</a></li>
                            <%
                            } else if ("C".equals(applicant.getApplicantType())) {
                            %>
                            <li class="nav-item" role="presentation"><a href="#tab-C-<%=coApplicantCount%>" class="nav-link apptype   alert alert-primary  " data-bs-toggle="tab" aria-selected="false" role="tab" tabindex="-1" data-app="C-<%=coApplicantCount%>"><i class="ph-user-circle-plus "></i><span data-applicationtype="C">Co-Applicant-<%=coApplicantCount%></span>
                            </a>
                            </li>
                            <%
                                coApplicantCount++;
                            }
                            else if ("G".equals(applicant.getApplicantType())) {
                            %>
                            <li class="nav-item" role="presentation"><a href="#tab-G-1" class="nav-link apptype   alert alert-primary   " data-bs-toggle="tab" aria-selected="false" role="tab" tabindex="-1" data-app="G-1"><i class="ph-bank   fw-semibold"></i><span data-applicationtype="G">GUARANTOR</span>

                            </a></li>
                            <%

                                    }
                                }
                                if(applicantList.size()==0){
                            %>
                            <li class="nav-item" role="presentation"><a href="#tab-CA" class="nav-link apptype active  alert alert-primary " data-bs-toggle="tab" aria-selected="false" role="tab" tabindex="-1" data-app="A"><i class="ph-user fw-semibold"></i>APPLICANT</a></li>
                            <%
                                }

                            %>
                        </ul>


                        <div class="tab-content card-body" id="loanbody">


                            <%
                                coApplicantCount=1;
                                for (VehicleLoanApplicant applicant : applicantList) {
                                    request.setAttribute("general",applicant);
                                    if ("A".equals(applicant.getApplicantType())) {
                            %>
                            <div class="tab-pane fade active show" id="tab-A" role="tabpanel">
                                <%
                                    request.setAttribute("apptype","A");
                                    request.setAttribute("appurl",appurl);
                                %>
                                <jsp:include page="docqueue/generaldetails_bd.jsp">
                                    <jsp:param name="apponly" value="Y"/>
                                </jsp:include>
                                <jsp:include page="docqueue/kycdetails_bdx.jsp" />
                                <jsp:include page="docqueue/basicdetails_bd.jsp" />
                                <jsp:include page="docqueue/employmentdetails_bd.jsp" />
                                <jsp:include page="docqueue/Incomedetails_bd.jsp" />
                                <jsp:include page="docqueue/CreditCheck_bd.jsp" />
                            </div>
                            <%
                            }
                            else if ("C".equals(applicant.getApplicantType())) {
                                request.setAttribute("apptype","C-"+coApplicantCount);
                                request.setAttribute("appurl","");
                            %>
                            <div class="tab-pane fade" role="tabpanel"  id="tab-C-<%=coApplicantCount%>">
                                <jsp:include page="docqueue/generaldetails_bd.jsp">
                                    <jsp:param name="coapponly" value="Y"/>
                                </jsp:include>
                                <jsp:include page="docqueue/kycdetails_bdx.jsp" />
                                <jsp:include page="docqueue/basicdetails_bd.jsp" />
                                <jsp:include page="docqueue/employmentdetails_bd.jsp" />
                                <jsp:include page="docqueue/Incomedetails_bd.jsp" />
                                <jsp:include page="docqueue/CreditCheck_bd.jsp" />
                            </div>
                            <%
                                coApplicantCount++;
                            }
                            else if ("G".equals(applicant.getApplicantType())) {
                                request.setAttribute("apptype","G-1");
                                request.setAttribute("appurl","");
                            %>
                            <div class="tab-pane fade" role="tabpanel"  id="tab-G-1">
                                <jsp:include page="docqueue/generaldetails_bd.jsp">
                                    <jsp:param name="coapponly" value="Y"/>
                                </jsp:include>
                                <jsp:include page="docqueue/kycdetails_bdx.jsp" />
                                <jsp:include page="docqueue/basicdetails_bd.jsp" />
                                <jsp:include page="docqueue/employmentdetails_bd.jsp" />
                                <jsp:include page="docqueue/Incomedetails_bd.jsp" />
                                <jsp:include page="docqueue/CreditCheck_bd.jsp" />
                            </div>
                            <%
                                    }

                                }
                                if(applicantList.size()==0){
                            %>
                            <div class="tab-pane fade active show" id="tab-A" role="tabpanel">
                                <%
                                    request.setAttribute("apptype","A");
                                    request.setAttribute("init","Y");
                                %>
                                <jsp:include page="docqueue/generaldetails_bd.jsp">
                                    <jsp:param name="apponly" value="Y"/>
                                </jsp:include>
                                <jsp:include page="docqueue/kycdetails_bdx.jsp" />
                                <jsp:include page="docqueue/basicdetails_bd.jsp" />
                                <jsp:include page="docqueue/employmentdetails_bd.jsp" />
                                <jsp:include page="docqueue/Incomedetails_bd.jsp" />
                                <jsp:include page="docqueue/CreditCheck_bd.jsp" />
                            </div>
                            <%
                                }
                            %>


                        </div>
                    </div>
                    <div class="w-100 kt">
                        <form id="wibogassetform" class="z-index-0" >
                            <input type="hidden" id="cadecision" name="cadecision"/>

                            <div class="card-body">
                                <div class="row">
                                    <div class="1 w-100 border rounded mb-lg-4 p-0">
                                        <div class="1"  style="background: #fff !important;">
                                            <!-- User menu -->

                                            <!-- /user menu -->

                                            <div class="rounded border p-2 mb-3">
                                                <!-- Navigation -->

                                                <div class="accordion accordion-icon-collapse" id="vl_rc_int" data-state="2">
                                                    <%--                                            <div class="sidebar-section" id="bottomCard" data-state="0">--%>
                                                    <%request.setAttribute("checker","Y");
                                                        if(vehicleAvlbl)
                                                        {
                                                    %>

                                                    <jsp:include page="docqueue/vehicledetails_bd.jsp" />

                                                    <jsp:include page="docqueue/loandetails_bd.jsp" />
                                                    <jsp:include page="docqueue/eligibilitydetails_bd.jsp" />
                                                        <%if(!currentQueue.equals("BM"))
                                                        {%>
                                                    <jsp:include page="docqueue/racescoredetails_bd.jsp" />
                                                    <jsp:include page="docqueue/validationdetails_bd.jsp" />

                                                    <jsp:include page="docqueue/hunterdetails_bd.jsp" />
                                                    <jsp:include page="search/blacklistdetails_bd.jsp" />
                                                    <jsp:include page="docqueue/deviationdetails_bd.jsp" />
                                                        <%}
                                                        if(request.getAttribute("repaymentDetails")!=null)
                                                        {%>
                                                    <jsp:include page="search/repayment_ac.jsp" />
                                                        <%}
                                                            if(request.getAttribute("sancLetter")!=null){%>
                                                    <jsp:include page="search/sanction_ac.jsp" />
                                                        <%}
                                                            //System.out.println("Reached Line 410");
                                                        %>
                                                    <%--                                                        <jsp:include page="search/sanmodification_ac.jsp" />--%>
                                                    <jsp:include page="search/acctlabels.jsp" />
<%--                                                    <jsp:include page="search/accopening.jsp" />--%>
                                                        <%
                                                            if(vehicleLoanMaster.getQueue().equals("ACOPN"))
                                                            {
                                                            if(request.getAttribute("repaymentDetails")!=null)
                                                            {%>
                                                    <jsp:include page="search/nachmandate.jsp" />
                                                        <%}%>
                                                    <jsp:include page="search/lienmarking_ac.jsp" />
                                                        <%if(request.getAttribute("vehicleLoanAccount")!=null){%>
                                                    <jsp:include page="search/disbursement.jsp" />
                                                        <%
                                                            } if(vehicleAvlbl){
                                                              //  System.out.println("Reached Line 420");
                                                        %>
                                                    <jsp:include page="search/Neft.jsp" />
                                                        <%}
                                                        }
                                                           // System.out.println("Reached Line 424");
                                                        %>
                                                     <jsp:include page="search/materialtc.jsp" />
                                                    <jsp:include page="search/remarks.jsp" />
                                                        <%
                                                           // System.out.println("Reached Line Remarks");
                                                            }
                                                        %>
                                                    <%--                                                <jsp:include page="rcchecker/bredetails_rc.jsp" />--%>


                                                </div>
<%--                                                <div class="text-center">--%>
<%--                                                    <button type="button" id="pdbtnbogasset" class="btn btn-sm btn-success bogassetbtn ms-1 me-1 ">Go to Post Disbursement<i class="ph-paper-plane-tilt ms-2"></i></button>--%>
<%--                                                    <button type="button" id="rejbtnbogasset" class="btn btn-sm btn-danger bogassetbtn ms-1 me-1 ">Reject<i class="ph-paper-plane-tilt ms-2"></i></button>--%>
<%--                                                    <button type="button"  class="btn btn-sm btn-primary ms-1 me-1 backbtnbog">Back </button>--%>
<%--                                                </div>--%>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>


                </div>

                <div id="docList" >

                </div>
            </div>

            <!-- /main charts -->



        </div>
        <!-- /content area -->

    </div>
    <!-- /main content -->

</div>
<!-- /page content -->

<div class="position-absolute top-50 end-100  visible">
    <button   id="toggleList" type="button" class="btn btn-primary position-fixed top-50 end-0 translate-middle-y border-right-0" >
        <i class="ph-file-doc"></i>
    </button>
</div>
<script src="assets/js/vendor/forms/selects/select2.min.js"></script>
<script src="assets/demo/pages/form_select2.js"></script>
<!--Custom Scripts-->
<script src="assets/js/custom/WI/kycdetails.js"></script>
<script src="assets/js/custom/WI/basicdetails.js"></script>
<script src="assets/js/custom/WI/generaldetails.js"></script>
<script src="assets/js/custom/WI/employmentdetails.js"></script>
<script src="assets/js/custom/WI/Incomedetails.js"></script>
<script src="assets/js/custom/WI/creditCheck.js"></script>
<script src="assets/js/custom/WI/uploadwi.js"></script>
<script src="assets/js/custom/WI/Wicreate.js"></script>
<script>
    function copyUTR(utrNumber) {
    navigator.clipboard.writeText(utrNumber)
        .then(() => {
            // Show a temporary success message
            const tooltip = document.getElementById('copyTooltip');
            tooltip.style.display = 'block';
            setTimeout(() => {
                tooltip.style.display = 'none';
            }, 2000);
        })
        .catch(err => {
            console.error('Failed to copy UTR:', err);
        });
}
    $(document).ready(function () {

//addCoApplicantBtn.prop('disabled', true);
        function alertmsgframe() {
            $('#alert_modal .modal-header').removeClass('bg-danger').addClass('bg-success');
            $('#alert_modal .modal-header').find('.modal-title').text('Remarks');
            $('#alert_modal .modal-body').html('<iframe id="modalIframe" src="remarks?slno='+$('#slno').val()+'" width="100%" height="400" frameborder="0"></iframe>');
            $('#alert_modal').modal('show');
        }

        $("#remarkHist").on('click',function (e) {
            e.preventDefault();
            e.stopPropagation();
            alertmsgframe();
        });
        $('.backbtnbog').on('click', function (e) {
            $('#backbtn').click();
        });

        $('#rejbtnbogasset').on('click', function (e) {
            var remarks =  $("#remarks").val().trim();
            if(remarks==''){
                alertmsg('Please enter final remarks');
                return;
            }
            var msg= "Kindly note that, this WI will get permanently rejected. Are you sure?";
            confirmmsg(msg).then(function (confirmed) {
                if (confirmed) {
                    var winum = $('#winum').val();
                    var slno = $('#slno').val();
                    rejWI(winum, slno,remarks);
                }
            });
        });
        $('#pdbtnbogasset').on('click', function (e) {
            var winum = $('#winum').val();
            var slno = $('#slno').val();
            var remarks =  $("#remarks").val().trim();

            if(remarks==''){
                alertmsg('Please enter final remarks');
                return;
            }
            showLoader();
            $.ajax({
                url: 'api/checker/wibogassetpd',
                type: 'POST',
                async:false,
                data: {
                    winum: winum,
                    slno: slno,
                    remarks: remarks
                },
                success: function (updateResponse) {
                    hideLoader();
                    if (updateResponse.status === 'S') {
                        // If update succeeds, process the loan
                        blockerMsg(updateResponse.msg);
                    }else{
                        alertmsg(updateResponse.msg);
                    }
                },
                error: function (xhr) {
                    hideLoader();
                    alertmsg("Something went wrong:"+xhr.msg);
                }
            });
        });

        var activeTab=$('#activeTab').val();
        if(activeTab == 'rmk'){
            $('#decisionDetailslink').click();
        }else if(activeTab == 'neft'){
            $('#neftlink').click();
        }else if(activeTab == 'disb'){
            $('#disblink').click();
        }else if(activeTab == 'nach'){
            $('#nachtableDetails').click();
        }else if(activeTab == 'acopn'){
            $('#accopeninglink').click();
        }else if(activeTab == 'label'){
            $('#acctLabelLink').click();
        }else if(activeTab == 'sanletter'){
            $('#sanctiondetailsLink').click();
        }
        function updateCurrencyDisplay() {
            const loanAmount = 10000000; // Example: 1 crore
            const sanctionAmount = 9500000; // Example: 95 lakhs

            document.getElementById('loan-amount').textContent = formatIndianCurrency(loanAmount);
            document.getElementById('sanction-amount').textContent = formatIndianCurrency(sanctionAmount);
        }

        updateCurrencyDisplay();
    });
    function rejWI(winum,slno, remarks){

        showLoader();
        $.ajax({
            url: 'api/checker/wibogassetrej',
            type: 'POST',
            async:false,
            data: {
                winum: winum,
                slno: slno,
                remarks: remarks
            },
            success: function (updateResponse) {
                hideLoader();
                if (updateResponse.status === 'S') {
                    // If update succeeds, process the loan
                    blockerMsg(updateResponse.msg);
                }else{
                    alertmsg(updateResponse.msg);
                }
            },
            error: function (xhr) {
                hideLoader();
                alertmsg("Something went wrong:"+xhr.msg);
            }
        });
    }
</script>
<%--<script src="assets/js/custom/checker/wibog.js"></script>--%>
<%--<script src="assets/js/custom/checker/wicrtamber.js"></script>--%>
<%--<script src="assets/js/custom/WI/programdetails.js"></script>--%>
<!--Custom Scripts-->
<%--<script src="assets/plugins/global/plugins.bundle.js"></script>--%>
<los:modal/>
<los:footer/>
<div id="pdf_modal" class="modal fade" tabindex="-1"  aria-modal="true"  data-bs-keyboard="false" data-bs-backdrop="static"  role="dialog">
    <div class="modal-dialog modal-xl  modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-success  text-white border-0 ">
                <h5 class="modal-title"><div class="d-flex justify-content-center"><span class="kt "><i class="ki-duotone text-white ki-finance-calculator fs-1"><span class="path1"></span><span class="path2"></span></i></span>Sanction Letter</div></h5>
            </div>
            <div class="modal-body">
                <input type="hidden" id="pdfBase64" value="<%=request.getAttribute("sancLetter")==null?"":request.getAttribute("sancLetter").toString()%>">
                <iframe id="pdfViewer" style="width: 100%; height: 650px;" frameborder="0"></iframe>

                <div class="p-5 text-center">

                    <div class="checkbox-wrapper-16">


                        <%--                        <div class="text-danger pt-1"><span class="text-red fw-bold"><i class="ph-info ph pe-2 "></i>Kindly select above</span></div>--%>
                        <div class="text-end pt-1 kt">
                            <button type="button" id="sancProceed1" class="btn btn-lg btn-primary" data-kt-stepper-action="next">Close
                                <i class="ki-duotone ki-double-right ">
                                    <i class="path1"></i>
                                    <i class="path2"></i>
                                </i></button>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
<%!
boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    // Helper function to format currency or return a default value
    String formatCurrency(String value) {
     NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    currencyFormat.setMaximumFractionDigits(0);
        if (isEmpty(value)) {
            return "N/A";
        }
        try {
            return currencyFormat.format(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            return "Invalid Amount";
        }
    }
%>
