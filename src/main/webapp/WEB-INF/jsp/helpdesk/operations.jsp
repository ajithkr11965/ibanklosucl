<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.sib.ibanklosucl.model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.sib.ibanklosucl.utilies.UserSessionData" %>
<%@ page import="com.sib.ibanklosucl.model.doc.VehicleLoanRepayment" %>
<%@ taglib prefix="los" uri="http://www.siblos.com/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // Get user details from session
    UserSessionData usd = (UserSessionData) request.getAttribute("usd");
    String currentUser = usd.getPPCNo();
    String userRole = (String) session.getAttribute("userRole");
    String errorMessage = (String) request.getAttribute("errorMessage");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");

    // Get data from request
    VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
    VehicleLoanLock vehicleLoanLock = (VehicleLoanLock) request.getAttribute("vehicleLoanLock");
    List<VehicleLoanSubqueueTask> subqueueTasks = (List<VehicleLoanSubqueueTask>) request.getAttribute("subqueueTasks");
    List<Map<String, Object>> auditTrail = (List<Map<String, Object>>) request.getAttribute("auditTrail");
    List<Misrct> bankList= (List<Misrct>) request.getAttribute("bankName");
    VehicleLoanRepayment vehicleLoanRepayment= (VehicleLoanRepayment) request.getAttribute("repaymentDetails");
    String bankName="",repaymentAccno="",ifsc="",borrowerName="";
    if (vehicleLoanRepayment != null) {
        bankName = vehicleLoanRepayment.getBankName();
        repaymentAccno = vehicleLoanRepayment.getAccountNumber();
        ifsc = vehicleLoanRepayment.getIfscCode();
        borrowerName = vehicleLoanRepayment.getBorrowerName();
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Helpdesk Operations</title>

    <!-- CSS Dependencies -->
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap5.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/sweetalert2.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
    <script src="${pageContext.request.contextPath}/assets/js/jquery/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/vendor/forms/selects/select2.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/demo/pages/form_select2.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/custom/WI/helpdeskExtra.js"></script>
    <style>
        :root {
            --primary-color: #28173c;
            --primary-light: #382150;
            --primary-dark: #1a0f28;
            --accent-color: #4a2e6b;
        }

        body {
            background-color: #f8f9fa;
            min-height: 100vh;
        }

        /* Enhanced Header Styles */
        .navbar {
            background-color: var(--primary-color);
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }

        .page-header {
            background-color: var(--primary-dark);
            position: relative;
            padding: 3.5rem 0;
            margin-bottom: 2rem;
            overflow: hidden;
        }

        .page-header::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-image: url('assets/images/5.jpeg');
            background-size: cover;
            opacity: 0.1;
            pointer-events: none;
        }

        /* Card Styles */
        .card {
            border: none;
            box-shadow: 0 2px 15px rgba(0,0,0,0.08);
            margin-bottom: 1.5rem;
            border-radius: 12px;
            transition: transform 0.2s, box-shadow 0.2s;
        }

        .card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 20px rgba(0,0,0,0.12);
        }

        .card-header {
            background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
            color: white;
            border-radius: 12px 12px 0 0 !important;
            border-bottom: none;
            padding: 1rem 1.5rem;
        }

        /* Button Styles */
        .btn-black {
            background: #000;
            color: white;
            border: none;
            padding: 0.7rem 1.5rem;
            border-radius: 8px;
            font-weight: 500;
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }

        .btn-black::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: linear-gradient(rgba(255,255,255,0.1), transparent);
            opacity: 0;
            transition: opacity 0.3s;
        }

        .btn-black:hover {
            background: #222;
            color: white;
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        }

        .btn-black:hover::after {
            opacity: 1;
        }

        /* Status Badges */
        .status-badge {
            padding: 0.4em 1em;
            border-radius: 30px;
            font-size: 0.875rem;
            font-weight: 500;
        }

        .status-locked {
            background-color: #dc3545;
            color: white;
        }

        .status-unlocked {
            background-color: #198754;
            color: white;
        }

        /* Form Styles */
        .form-control {
            border-radius: 8px;
            padding: 0.7rem 1rem;
            border: 1px solid #dee2e6;
            transition: all 0.2s;
        }

        .form-control:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 0.25rem rgba(40, 23, 60, 0.15);
        }

        /* Loading Overlay */
        .loading-overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.7);
            backdrop-filter: blur(5px);
            z-index: 9999;
        }

        /* Action Cards */
        .action-card {
            text-align: center;
            padding: 1.5rem;
        }

        .action-card i {
            font-size: 2rem;
            margin-bottom: 1rem;
            color: var(--primary-color);
        }

        /* Audit Trail Table */
        .table-hover tbody tr {
            transition: background-color 0.2s;
        }

        .table-hover tbody tr:hover {
            background-color: rgba(40, 23, 60, 0.05);
        }

        /* Child Locks Grid */
        .child-lock-card {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 1rem;
            border: 1px solid #e9ecef;
        }

        /* Responsive Adjustments */
        @media (max-width: 768px) {
            .action-buttons {
                flex-direction: column;
            }

            .action-buttons .btn {
                margin: 0.5rem 0;
                width: 100%;
            }
        }


        .json-content {
            max-height: 200px;
            overflow: auto;
            //white-space: pre-wrap;
            word-break: break-word;
        }
        .copy-btn {
            cursor: pointer;
        }
    </style>
</head>
<body>
    <!-- Loading Overlay -->
    <div id="loadingOverlay" class="loading-overlay">
        <div class="d-flex justify-content-center align-items-center h-100">
            <div class="spinner-border text-light" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
        </div>
    </div>

    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container">
            <a class="navbar-brand" href="#">
                <img src="assets/images/siblogo.png" alt="Logo" height="40">
            </a>
            <div class="ms-auto d-flex align-items-center">
                <span class="text-white">
                    <i class="bi bi-person-circle me-2"></i>
                    Welcome, <%=currentUser%>
                </span>
            </div>
        </div>
    </nav>

    <!-- Page Header with Wave Pattern -->
    <header class="page-header text-white">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h1 class="display-6 fw-bold mb-2">Helpdesk Operations</h1>
                    <p class="lead mb-0">Manage workitems and resolve operational issues</p>
                </div>
                <div class="col-md-4 text-md-end">
                    <span class="badge bg-light text-dark">
                        <i class="bi bi-clock me-2"></i>
                        Last Updated: <%=dateFormat.format(new java.util.Date())%>
                    </span>
                </div>
            </div>
        </div>
    </header>

    <!-- Main Content -->
    <main class="container py-4">
        <!-- Error Message Display -->
        <% if(errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                <strong>Error!</strong> <%=errorMessage%>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        <% } %>

        <!-- Search Card -->
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="mb-0"><i class="bi bi-search me-2"></i>Search Work Item</h5>
            </div>
            <div class="card-body">
                <form id="searchForm">
                    <div class="row align-items-end g-3">
                        <div class="col-md-9">
                            <label for="wiNum" class="form-label">Work Item Number</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light">
                                    <i class="bi bi-hash"></i>
                                </span>
                                <input type="text"
                                       class="form-control"
                                       id="wiNum"
                                       name="wiNum"
                                       value="<%=vehicleLoanMaster != null ? vehicleLoanMaster.getWiNum() : ""%>"
                                       placeholder="Enter Work Item Number"
                                       required>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <button type="submit" class="btn btn-black w-100">
                                <i class="bi bi-search me-2"></i>Search
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>


        <% if(vehicleLoanMaster != null)
        {
        %>
        <input type="hidden" id="workslno" value="<%= vehicleLoanMaster != null ? vehicleLoanMaster.getSlno() : "" %>"> />
        <input type="hidden" id="pageData"
               data-wi-num="<%= vehicleLoanMaster != null ? vehicleLoanMaster.getWiNum() : "" %>"
               data-slno="<%= vehicleLoanMaster != null ? vehicleLoanMaster.getSlno() : "" %>"
               data-current-page="<%= request.getAttribute("currentPage") != null ? request.getAttribute("currentPage") : 1 %>"
               data-page-size="<%= request.getAttribute("pageSize") != null ? request.getAttribute("pageSize") : 10 %>"
               data-total-pages="<%= request.getAttribute("totalPages") != null ? request.getAttribute("totalPages") : 1 %>"
               data-total-elements="<%= request.getAttribute("totalElements") != null ? request.getAttribute("totalElements") : 0 %>">
        <%
            }
        %>
        <!-- Work Item Details -->
        <div id="workItemDetails" class="<%=vehicleLoanMaster != null ? "" : "d-none"%>">
            <% if(vehicleLoanMaster != null) { %>
                <!-- Basic Info and Lock Status -->
                <div class="row g-4 mb-4">
                    <!-- Basic Information Card -->
                    <div class="col-md-6">
                        <div class="card h-100">
                            <div class="card-header">
                                <h5 class="mb-0"><i class="bi bi-info-circle me-2"></i>Basic Information</h5>
                            </div>
                            <div class="card-body">
                                <dl class="row mb-0">
                                    <dt class="col-sm-4">Work Item Number:</dt>
                                    <dd class="col-sm-8"><%=vehicleLoanMaster.getWiNum()%></dd>

                                    <dt class="col-sm-4">Queue:</dt>
                                    <dd class="col-sm-8"><%=vehicleLoanMaster.getQueue()%></dd>

                                    <dt class="col-sm-4">Status:</dt>
                                    <dd class="col-sm-8"><%=vehicleLoanMaster.getStatus()%></dd>

                                    <dt class="col-sm-4">Created On:</dt>
                                    <dd class="col-sm-8">
                                        <%=vehicleLoanMaster.getRiRcreDate() != null ?
                                            dateFormat.format(vehicleLoanMaster.getRiRcreDate()) : "N/A"%>
                                    </dd>
                                </dl>
                            </div>
                        </div>
                    </div>

                    <!-- Lock Information Card -->
                    <div class="col-md-6">
                        <div class="card h-100">
                            <div class="card-header">
                                <h5 class="mb-0"><i class="bi bi-lock me-2"></i>Lock Information</h5>
                            </div>
                            <div class="card-body">
                                <dl class="row mb-0">
                                    <dt class="col-sm-4">Lock Status:</dt>
                                    <dd class="col-sm-8">
                                        <span class="status-badge <%="Y".equals(vehicleLoanLock.getLockFlg()) ?
                                            "status-locked" : "status-unlocked"%>">
                                            <i class="bi bi-<%="Y".equals(vehicleLoanLock.getLockFlg()) ?
                                                "lock-fill" : "unlock-fill"%> me-1"></i>
                                            <%="Y".equals(vehicleLoanLock.getLockFlg()) ? "Locked" : "Unlocked"%>
                                        </span>
                                    </dd>

                                    <dt class="col-sm-4">Locked By:</dt>
                                    <dd class="col-sm-8">
                                        <%=vehicleLoanLock.getLockedBy() != null ?
                                            vehicleLoanLock.getLockedBy() : "N/A"%>
                                    </dd>

                                    <dt class="col-sm-4">Locked On:</dt>
                                    <dd class="col-sm-8">
                                        <%=vehicleLoanLock.getLockedOn() != null ?
                                            dateFormat.format(vehicleLoanLock.getLockedOn()) : "N/A"%>
                                    </dd>
                                </dl>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- BD Queue Warning -->
                <% if("BD".equals(vehicleLoanMaster.getQueue())) { %>
                    <div class="alert alert-warning mb-4" role="alert">
                        <i class="bi bi-exclamation-triangle-fill me-2">
						</i>
                        <strong>Important:</strong> You are working with a BD queue workitem.
                        Please note that resetting documentation may require re-signing and
                        could incur additional stamp charges.
                    </div>
                <% } %>

                <!-- Action Cards -->
                <div class="row g-4 mb-4">
                    <!-- Reset Documentation -->
                    <div class="col-md-4">
                        <div class="card h-100">
                            <div class="card-body action-card">
                                <i class="bi bi-arrow-counterclockwise text-danger"></i>
                                <h5>Reset Documentation</h5>
                                <p class="text-muted small">Only available for BD queue</p>
                                <button onclick="performAction('reset-documentation')"
                                        id="resetDocBtn"
                                        class="btn btn-black w-100"
                                     <%="BD".equals(vehicleLoanMaster.getQueue()) ? "" : "disabled"%>>

                                    <i class="bi bi-arrow-repeat me-2"></i>Reset Documentation
                                </button>
                            </div>
                        </div>
                    </div>








                    <!-- Release Lock -->
                    <div class="col-md-4">
                        <div class="card h-100">
                            <div class="card-body action-card">
                                <i class="bi bi-unlock"></i>
                                <h5>Release Lock</h5>
                                <p class="text-muted small">Release main workitem lock</p>
                                <button onclick="performAction('release-lock')"
                                        id="releaseLockBtn"
                                        class="btn btn-black w-100"
                                        <%="Y".equals(vehicleLoanLock.getLockFlg()) ? "" : "disabled"%>>
                                    <i class="bi bi-unlock me-2"></i>Release Lock
                                </button>
                            </div>
                        </div>
                    </div>

                    <!-- Release Child Locks -->
                    <div class="col-md-4">
                        <div class="card h-100">
                            <div class="card-body action-card">
                                <i class="bi bi-diagram-3"></i>
                                <h5>Release Child Locks</h5>
                                <p class="text-muted small">Only available for BD queue</p>
                                <button onclick="performAction('release-child-locks')"
                                        id="releaseChildLocksBtn"
                                        class="btn btn-black w-100"
                                        <%="BD".equals(vehicleLoanMaster.getQueue()) ? "" : "disabled"%>>
                                    <i class="bi bi-unlock-fill me-2"></i>Release Child Locks
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Child Locks -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h5 class="mb-0"><i class="bi bi-diagram-3 me-2"></i>Child Locks</h5>
                    </div>
                    <div class="card-body">
                        <div class="row g-3">
                            <% if(subqueueTasks != null && !subqueueTasks.isEmpty()) {
                                for(VehicleLoanSubqueueTask task : subqueueTasks) {
                                    if("Y".equals(task.getLockFlg())) { %>
                                        <div class="col-md-4">
                                            <div class="child-lock-card">
                                                <div class="d-flex justify-content-between align-items-center mb-2">
                                                    <h6 class="mb-0"><%=task.getTaskType()%></h6>
                                                    <span class="status-badge status-locked">
                                                        <i class="bi bi-lock-fill me-1"></i>Locked
                                                    </span>
                                                </div>
                                                <div class="small">
                                                    <div class="d-flex justify-content-between mb-1">
                                                        <span class="text-muted">Locked By:</span>
                                                        <span><%=task.getLockedBy() != null ?
                                                            task.getLockedBy() : "N/A"%></span>
                                                    </div>
                                                    <div class="d-flex justify-content-between">
                                                        <span class="text-muted">Locked On:</span>
                                                        <span><%=task.getLockedOn() != null ?
                                                            dateFormat.format(task.getLockedOn()) : "N/A"%></span>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    <% }
                                }
                            } else { %>
                                <div class="col-12">
                                    <p class="text-center text-muted mb-0">No child locks found</p>
                                </div>
                            <% } %>
                        </div>
                    </div>
                </div>
            <div class="col-md-4">
   <div class="card h-100">
       <div class="card-body action-card">
           <i class="bi bi-credit-card"></i>
           <h5>Repayment Details</h5>
           <button onclick="performAction('show-repayment')"
                   class="btn btn-black w-100">
               <i class="bi bi-pencil me-2"></i>Update Repayment
           </button>
       </div>
   </div>
</div>



                <!-- Audit Trail -->
                <% if(auditTrail != null && !auditTrail.isEmpty()) { %>
                    <div class="card">
                        <div class="card-header">
                            <h5 class="mb-0"><i class="bi bi-clock-history me-2"></i>Audit Trail</h5>
                        </div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                        <tr>
                                            <th>Action</th>
                                            <th>User</th>
                                            <th>Date & Time</th>
                                            <th>Remarks</th>
                                            <th>IP Address</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for(Map<String, Object> audit : auditTrail) { %>
                                            <tr>
                                                <td>
                                                    <span class="badge bg-primary">
                                                        <%=audit.get("ACTION_TYPE")%>
                                                    </span>
                                                </td>
                                                <td><%=audit.get("ACTED_BY")%></td>
                                                <td><%=dateFormat.format(audit.get("ACTION_DATE"))%></td>
                                                <td>
                                                    <div class="text-truncate" style="max-width: 300px;"
                                                         data-bs-toggle="tooltip"
                                                         title="<%=audit.get("REMARKS")%>">
                                                        <%=audit.get("REMARKS")%>
                                                    </div>
                                                </td>
                                                <td>
                                                    <small class="text-muted">
                                                        <%=audit.get("IP_ADDRESS")%>
                                                    </small>
                                                </td>
                                            </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                <% } %>
            <% } %>
            <div class="card mb-4 d-none" id="repaymentSection">
   <div class="card-header">
       <h5 class="mb-0"><i class="bi bi-credit-card me-2"></i>Repayment Details</h5>
   </div>
   <div class="card-body">
       <form id="repaymentForm">
           <div class="row g-3">
               <div class="col-md-6">
                   <label class="form-label">Bank Name</label>
                   <select class="form-select" id="bankName" name="bankName" required>
                       <option value="">Select Bank</option>
                       <%
                       if(bankList != null) {
                           for(Misrct bank : bankList) {
                       %>
                           <option value="<%=bank.getCodevalue()%>"><%=bank.getCodedesc()%></option>
                       <%
                           }
                       }
                       %>
                   </select>
               </div>

               <div class="col-md-6">
                   <label class="form-label">Account Number</label>
                   <input type="text" class="form-control" id="accountNumber" name="accountNumber" required
                          value="<%=vehicleLoanRepayment != null ? vehicleLoanRepayment.getAccountNumber() : ""%>">
               </div>

               <div class="col-md-6">
                   <label class="form-label">IFSC Code</label>
                   <input type="text" class="form-control" id="ifscCode" name="ifscCode" required maxlength="11"
                          value="<%=vehicleLoanRepayment != null ? vehicleLoanRepayment.getIfscCode() : ""%>">
               </div>

               <div class="col-md-6">
                   <label class="form-label">Borrower Name</label>
                   <input type="text" class="form-control" id="borrowerName" name="borrowerName" required
                          value="<%=vehicleLoanRepayment != null ? vehicleLoanRepayment.getBorrowerName() : ""%>">
               </div>
           </div>

           <div class="text-end mt-4">
               <button type="submit" class="btn btn-black" id="saveRepaymentBtn">
                   <i class="bi bi-save me-2"></i>Save
               </button>
           </div>
       </form>
   </div>
</div>

        </div>
        <%if
        (request.getAttribute("vehicleLoanMaster")==null){%>
        <%@ include file="pincode.jsp"%>
        <% } %>
    </main>

    <!-- Hidden Fields -->
    <input type="hidden" id="currentUserId" value="<%=currentUser%>">
    <input type="hidden" id="currentUserRole" value="<%=userRole%>">

    <!-- Scripts -->
    <script src="assets/js/bootstrap/bootstrap5.bundle.min.js"></script>
    <script src="assets/js/vendor/sweetalert2.all.min.js"></script>

    <script>
        const BASE_URL = '<%=request.getContextPath()%>/helpdesk';
        const CURRENT_USER = '<%=currentUser%>';
        var currentWiNum = '<%= vehicleLoanMaster != null ? vehicleLoanMaster.getWiNum() : "" %>';
        var currentPage = 1;
        var currentSize = 10;
        var totalPages = <%= request.getAttribute("totalPages") %>;
        var totalElements = <%=request.getAttribute("totalElements") %>;
        var pageLoadTime = new Date().getTime();
        // Initialize tooltips
        document.addEventListener('DOMContentLoaded', function() {
            var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
            tooltipTriggerList.map(function (tooltipTriggerEl) {
                return new bootstrap.Tooltip(tooltipTriggerEl, {
                    boundary: 'window'
                });
            });

        });

        function exploreWorkItem(wiNum) {
            if (!wiNum) {
                showNotification('error', 'Invalid work item number');
                return;
            }

            try {
                var encodedWiNum = btoa(encodeURIComponent(wiNum.trim()));
                var newWindow = window.open('wisearch?winum=' + encodedWiNum, '_blank');
                if (!newWindow) {
                    showNotification('error', 'Pop-up was blocked. Please allow pop-ups for this site.');
                }
            } catch (error) {
                console.error('Error in exploreWorkItem:', error);
                showNotification('error', 'Failed to open explore window');
            }
        }

        // Add this helper function if not already present
        function showNotification(type, message) {
            var alertClass = type === 'success' ? 'alert-success' : 'alert-danger';
            var icon = type === 'success' ? 'check-circle-fill' : 'exclamation-triangle-fill';

            var alertHtml =
                '<div class="alert ' + alertClass + ' alert-dismissible fade show" role="alert">' +
                '<i class="bi bi-' + icon + ' me-2"></i>' +
                message +
                '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>' +
                '</div>';

            var container = document.querySelector('.container');
            if (container) {
                container.insertAdjacentHTML('afterbegin', alertHtml);
            }
        }


        function copyToClipboard(targetId, element) {
            const content = document.querySelector(targetId).innerText.trim();;
            const tooltip = bootstrap.Tooltip.getInstance(element) || new bootstrap.Tooltip(element);

            navigator.clipboard.writeText(content)
                .then(() => {
                    element.setAttribute('data-bs-original-title', 'Copied!');
                    tooltip.show();
                    setTimeout(() => {
                        element.setAttribute('data-bs-original-title', 'Copy to clipboard');
                        tooltip.hide();
                    }, 2000);
                })
                .catch(err => console.error('Failed to copy: ', err));
        }


    </script>




    <script src="${pageContext.request.contextPath}/assets/js/custom/helpdesk-operations.js"></script>
</body>
</html>