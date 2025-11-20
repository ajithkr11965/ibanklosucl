<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.sib.ibanklosucl.model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.sib.ibanklosucl.model.doc.VehicleLoanRepayment" %>

<%
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
    VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
    ExperianData experianData = (ExperianData) request.getAttribute("ExperianData");
     Boolean exists = (Boolean) request.getAttribute("workItemExists");
     String wiNum = (String) request.getAttribute("wiNum");



    VehicleLoanLock vehicleLoanLock = (VehicleLoanLock) request.getAttribute("vehicleLoanLock");
    List<VehicleLoanSubqueueTask> subqueueTasks = (List<VehicleLoanSubqueueTask>) request.getAttribute("subqueueTasks");
    List<Map<String, Object>> auditTrail = (List<Map<String, Object>>) request.getAttribute("auditTrail");
  //  List<Map<String, Object>> apiLogs = (List<Map<String, Object>>) request.getAttribute("apiLogs");
    String errorMessage = (String) request.getAttribute("errorMessage");
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

<%!
    // Helper method to safely get string
    public String getSafeString(String value) {
        return value != null ? value : "N/A";
    }

    // Helper method to safely format date
    public String getSafeDate(Date date, SimpleDateFormat formatter) {
        return date != null ? formatter.format(date) : "N/A";
    }

    // Helper method to check if string equals value
    public boolean safeEquals(String str, String value) {
        return str != null && str.equals(value);
    }
%>

<!-- Error Message Section -->
<% if(errorMessage != null) { %>
<div class="alert alert-danger alert-dismissible fade show" role="alert">
    <i class="bi bi-exclamation-triangle-fill me-2"></i>
    <%= errorMessage %>
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
</div>
<% } %>

<!-- Details Grid -->
<div class="row">
    <!-- Basic Info -->
    <div class="col-md-6 mb-4">
        <div class="card h-100">
            <div class="card-header bg-light text-light py-2">
                <h5 class="card-title mb-0">
                    <i class="bi bi-info-circle me-2"></i>Work Item Information
                </h5>
            </div>
            <div class="card-body">

                <!-- Explore Button -->
                <div class="text-end mb-4" style="position: absolute; right: 5%;">
                    <% if (vehicleLoanMaster != null && vehicleLoanMaster.getWiNum() != null && !vehicleLoanMaster.getWiNum().trim().isEmpty()) { %>
                    <a href="wisearch?winum=<%= java.util.Base64.getEncoder().encodeToString(vehicleLoanMaster.getWiNum().trim().getBytes(java.nio.charset.StandardCharsets.UTF_8)) %>"
                       target="_blank"
                       class="btn btn-info">
                        <i class="bi bi-search me-2"></i>Explore
                    </a>
                    <% } else { %>
                    <button class="btn btn-info" disabled>
                        <i class="bi bi-search me-2"></i>Explore
                    </button>
                    <% } %>
                </div>


                <% if(vehicleLoanMaster != null) { %>
                <dl class="row mb-0">
                    <dt class="col-sm-4">Work Item Number:</dt>
                    <dd class="col-sm-8" id="displayWiNum">
                        <%= getSafeString(vehicleLoanMaster.getWiNum()) %>
                    </dd>

                    <dt class="col-sm-4">Queue:</dt>
                    <dd class="col-sm-8" id="displayQueue">
                        <%= getSafeString(vehicleLoanMaster.getQueue()) %>
                    </dd>

                    <dt class="col-sm-4">Status:</dt>
                    <dd class="col-sm-8" id="displayStatus">
                        <%= getSafeString(vehicleLoanMaster.getStatus()) %>
                    </dd>

                    <dt class="col-sm-4">Created On:</dt>
                    <dd class="col-sm-8">
                        <%= getSafeDate(vehicleLoanMaster.getRiRcreDate(), dateFormat) %>
                    </dd>
                </dl>
                <% } else { %>
                <div class="text-center text-muted">
                    <i class="bi bi-info-circle me-2"></i>No work item information available
                </div>
                <% } %>
            </div>
        </div>
    </div>

    <!-- Lock Info -->
    <div class="col-md-6 mb-4">
        <div class="card h-100">
            <div class="card-header bg-light text-light py-2">
                <h5 class="card-title mb-0">
                    <i class="bi bi-lock me-2"></i>Lock Information
                </h5>
            </div>
            <div class="card-body">
                <% if(vehicleLoanLock != null) { %>
                <dl class="row mb-0">
                    <dt class="col-sm-4">Lock Status:</dt>
                    <dd class="col-sm-8">
                            <span id="displayLockStatus"
                                  class="status-badge <%= safeEquals(vehicleLoanLock.getLockFlg(), "Y") ?
                                      "status-locked" : "status-unlocked" %>">
                                <i class="bi bi-<%= safeEquals(vehicleLoanLock.getLockFlg(), "Y") ?
                                    "lock-fill" : "unlock-fill" %> me-1"></i>
                               <%= safeEquals(vehicleLoanLock.getLockFlg(), "Y") ? "Locked" : "Unlocked" %>
                            </span>
                    </dd>

                    <dt class="col-sm-4">Locked By:</dt>
                    <dd class="col-sm-8" id="displayLockedBy">
                        <%= getSafeString(vehicleLoanLock.getLockedBy()) %>
                    </dd>

                    <dt class="col-sm-4">Locked On:</dt>
                    <dd class="col-sm-8" id="displayLockedOn">
                        <%= getSafeDate(vehicleLoanLock.getLockedOn(), dateFormat) %>
                    </dd>
                </dl>
                <% } else { %>
                <div class="text-center text-muted">
                    <i class="bi bi-lock me-2"></i>No lock information available
                </div>
                <% } %>
            </div>
        </div>
    </div>
</div>
<!-- BD Queue Warning if applicable -->
<% if(vehicleLoanMaster != null && "BD".equals(vehicleLoanMaster.getQueue())) { %>
<div class="alert alert-warning mb-4" role="alert">
    <i class="bi bi-exclamation-triangle-fill me-2"></i>
    <strong>Important:</strong> You are working with a BD queue workitem.
    Please note that resetting documentation may require re-signing and
    could incur additional stamp charges.
</div>
<% } %>

<!-- Child Locks -->
<div class="card mb-4">
    <div class="card-header bg-light text-light py-2">
        <h5 class="card-title mb-0">
            <i class="bi bi-diagram-3 me-2"></i>Child Locks
        </h5>
    </div>
    <div class="card-body">
        <div id="childLocksContainer" class="row">
            <% if(subqueueTasks != null && !subqueueTasks.isEmpty()) {
                boolean hasLockedTasks = false;
                for(VehicleLoanSubqueueTask task : subqueueTasks) {
                    if(task != null && safeEquals(task.getLockFlg(), "Y")) {
                        hasLockedTasks = true; %>
            <div class="col-md-4 mb-3">
                <div class="card bg-light">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <h6 class="mb-0"><%= getSafeString(task.getTaskType()) %></h6>
                            <span class="status-badge status-locked">
                                            <i class="bi bi-lock-fill me-1"></i>Locked
                                        </span>
                        </div>
                        <div class="small">
                            <div class="d-flex justify-content-between mb-1">
                                <span class="text-muted">Locked By:</span>
                                <span><%= getSafeString(task.getLockedBy()) %></span>
                            </div>
                            <div class="d-flex justify-content-between">
                                <span class="text-muted">Locked On:</span>
                                <span><%= getSafeDate(task.getLockedOn(), dateFormat) %></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <% }
            }
                if (!hasLockedTasks) { %>
            <div class="col-12">
                <p class="text-center text-muted mb-0">No locked tasks found</p>
            </div>
            <% }
            } else { %>
            <div class="col-12">
                <p class="text-center text-muted mb-0">No child tasks available</p>
            </div>
            <% } %>
        </div>
    </div>
</div>

<!-- Action Buttons -->
<div class="row mb-4">
    <!-- Reset Documentation -->
    <div class="col-md-4 mb-3">
        <input type="hidden" id="workslno" value="<%= vehicleLoanMaster != null ? vehicleLoanMaster.getSlno() : "" %>"/>
                <div class="card h-100">
                    <div class="card-body text-center">
                        <i class="bi bi-credit-card fs-2 mb-3 text-dark"></i>
                        <h5 class="card-title">Repayment Details</h5>
     <p class="text-muted small mb-3">Update the Repayment Account</p>
                        <button  onclick="performAction('show-repayment')" class="btn btn-dark action-button">

                            <i class="bi bi-pencil me-2"></i>Update Repayment
                        </button>
                    </div>

                </div>
            </div>



    <div class="col-md-4 mb-3">
        <div class="card h-100">
            <div class="card-body text-center">
                <i class="bi bi-arrow-counterclockwise fs-2 mb-3 text-danger"></i>
                <h5 class="card-title">Reset Documentation</h5>
                <p class="text-muted small mb-3">Only available for BD queue</p>
                <button onclick="performAction('reset-documentation')"
                        id="resetDocBtn"
                        class="btn btn-danger action-button"
                        <%= (vehicleLoanMaster != null && "BD".equals(vehicleLoanMaster.getQueue())) ? "" : "disabled" %>>
                    <i class="bi bi-arrow-repeat me-2"></i>Reset Documentation
                </button>
            </div>
        </div>
    </div>


        <div class="col-md-4 mb-3">
            <div class="card h-100">
                <div class="card-body text-center">

                    <i class="bi bi-gear fs-2 mb-3 text-warning"></i>
                    <h5 class="card-title">Reset Experian</h5>
                      <p > &nbsp</p>
                    <button onclick="performAction('reset-experian')"
                            id="resetExpBtn"
                            class="btn btn-warning action-button"
                              <%= (Boolean.TRUE.equals(exists)) ? "" : "disabled" %>
                        <i class="bi bi-gear me-2"></i>Reset Experian
                    </button>
                </div>
            </div>
        </div>

    <!-- Release Lock -->
    <div class="col-md-4 mb-3">
        <div class="card h-100">
            <div class="card-body text-center">
                <i class="bi bi-unlock fs-2 mb-3 text-primary"></i>
                <h5 class="card-title">Release Lock</h5>
                <p class="text-muted small mb-3">Release main workitem lock</p>
                <button onclick="performAction('release-lock')"
                        id="releaseLockBtn"
                        class="btn btn-primary action-button"
                        <%= (vehicleLoanLock != null && safeEquals(vehicleLoanLock.getLockFlg(), "Y")) ? "" : "disabled" %>>
                    <i class="bi bi-unlock me-2"></i>Release Lock
                </button>
            </div>
        </div>
    </div>

    <!-- Release Child Locks -->
    <div class="col-md-4 mb-3">
        <div class="card h-100">
            <div class="card-body text-center">
                <i class="bi bi-unlock-fill fs-2 mb-3 text-warning"></i>
                <h5 class="card-title">Release Child Locks</h5>
                <p class="text-muted small mb-3">Release all subqueue locks</p>
                <button onclick="performAction('release-child-locks')"
                        id="releaseChildLocksBtn"
                        class="btn btn-warning action-button"
                        <%= (vehicleLoanMaster != null && "BD".equals(vehicleLoanMaster.getQueue()) &&
                                subqueueTasks != null && !subqueueTasks.isEmpty()) ? "" : "disabled" %>>
                    <i class="bi bi-unlock me-2"></i>Release Child Locks
                </button>
            </div>
        </div>
    </div>

    <!-- Manage WorkItem (Only for ACOPN queue) -->
    <div class="col-md-4 mb-3">
        <div class="card h-100">
            <div class="card-body text-center">
                <i class="bi bi-arrow-counterclockwise fs-2 mb-3 text-danger"></i>
                <h5 class="card-title">Manage WorkItem</h5>
                <p class="text-muted small mb-3">Only available for Account Opening Queue</p>
                <button onclick="performAction('manage-acopn')"
                        id="manageWorkItemBtn"
                        class="btn btn-danger action-button"
                        <%= (vehicleLoanMaster != null && "ACOPN".equals(vehicleLoanMaster.getQueue())) ? "" : "disabled" %>>
                    <i class="bi bi-arrow-repeat me-2"></i>Manage
                </button>
            </div>
        </div>
    </div>

    <!-- Manage WorkItem (Only for ACOPN queue) -->
    <div class="col-md-4 mb-3">
        <div class="card h-100">
            <div class="card-body text-center">
                <i class="bi bi-arrow-counterclockwise fs-2 mb-3 text-danger"></i>
                <h5 class="card-title">View API Details</h5>
                <p class="text-muted small mb-3">You can latest API Logs of the Work Item</p>
                <button class="btn btn-success dashboard-button" onclick="loadApiEndpoints()" data-bs-toggle="modal" data-bs-target="#apiModal">
                    <i class="bi bi-server me-2"></i>API Details
                </button>
            </div>
        </div>
    </div>

</div>


<!-- Audit Trail -->
<div class="card mb-4">
    <div class="card-header bg-light text-light py-2">
        <h5 class="card-title mb-0">
            <i class="bi bi-clock-history me-2"></i>Audit Trail
        </h5>
    </div>
    <div class="card-body">
        <% if(auditTrail != null && !auditTrail.isEmpty()) { %>
        <div class="table-responsive">
            <table class="table table-hover table-striped">
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
                <% for(Map<String, Object> audit : auditTrail) {
                    if(audit != null) { %>
                <tr class="audit-row">
                    <td>
                                        <span class="badge bg-primary">
                                            <%= getSafeString(String.valueOf(audit.get("ACTION_TYPE"))) %>
                                        </span>
                    </td>
                    <td><%= getSafeString(String.valueOf(audit.get("ACTED_BY"))) %></td>
                    <td><%= audit.get("ACTION_DATE") != null ?
                            dateFormat.format(audit.get("ACTION_DATE")) : "N/A" %></td>
                    <td>
                        <div class="custom-tooltip"
                             data-bs-toggle="tooltip"
                             title="<%= getSafeString(String.valueOf(audit.get("REMARKS"))) %>">
                                            <span class="truncate-text">
                                                <%= getSafeString(String.valueOf(audit.get("REMARKS"))) %>
                                            </span>
                        </div>
                    </td>
                    <td>
                        <small class="text-muted">
                            <%= getSafeString(String.valueOf(audit.get("IP_ADDRESS"))) %>
                        </small>
                    </td>
                </tr>
                <% }
                } %>
                </tbody>
            </table>
        </div>
        <% } else { %>
        <div class="text-center text-muted">
            <i class="bi bi-clock-history me-2"></i>No audit trail available
        </div>
        <% } %>
    </div>
</div>
<!-- API Logs Section -->
<%--<div class="card">--%>
<%--    <div class="card-header bg-light text-light py-2">--%>
<%--        <div class="d-flex justify-content-between align-items-center">--%>
<%--            <h5 class="card-title mb-0">--%>
<%--                <i class="bi bi-clock-history me-2"></i>API Logs--%>
<%--            </h5>--%>
<%--            <% if(apiLogs != null && apiLogs.size() > 10) { %>--%>
<%--            <button class="btn btn-sm btn-primary" onclick="showAllLogs()" id="viewAllBtn">--%>
<%--                View All--%>
<%--            </button>--%>
<%--            <% } %>--%>
<%--        </div>--%>
<%--    </div>--%>
<%--    <div class="card-body">--%>
<%--        <div class="table-responsive">--%>
<%--            <div id="loadingSpinner" class="text-center" style="display: none;">--%>
<%--                <div class="spinner-border text-primary" role="status">--%>
<%--                    <span class="visually-hidden">Loading...</span>--%>
<%--                </div>--%>
<%--            </div>--%>
<%--            <div id="errorState" class="alert alert-danger" style="display: none;"></div>--%>

<%--            <table class="table table-bordered table-striped" id="apiLogsTable">--%>
<%--                <!-- Table header remains same -->--%>
<%--                <tbody id="apiLogsBody">--%>
<%--                <% if(apiLogs != null && !apiLogs.isEmpty()) {--%>
<%--                    for(Map<String, Object> api : apiLogs) {--%>
<%--                        if(api != null) { %>--%>
<%--                <tr>--%>
<%--                    <td><%= getSafeString(String.valueOf(api.get("API_NAME"))) %></td>--%>
<%--                    <td>--%>
<%--                        <div class="json-content" id="R<%=getSafeString(String.valueOf(api.get("ID")))%>-1">--%>
<%--                            <%= getSafeString(String.valueOf(api.get("REQUEST"))) %>--%>
<%--                        </div>--%>
<%--                        <span class="copy-icon" data-bs-toggle="tooltip" data-bs-placement="top"--%>
<%--                              title="Copy to clipboard"--%>
<%--                              onclick="copyToClipboard('#R<%=getSafeString(String.valueOf(api.get("ID")))%>-1', this)">--%>
<%--                                <i class="bi bi-clipboard"></i>--%>
<%--                            </span>--%>
<%--                    </td>--%>
<%--                    <td>--%>
<%--                        <div class="json-content" id="R<%=getSafeString(String.valueOf(api.get("ID")))%>-2">--%>
<%--                            <%= getSafeString(String.valueOf(api.get("RESPONSE"))) %>--%>
<%--                        </div>--%>
<%--                        <span class="copy-icon" data-bs-toggle="tooltip" data-bs-placement="top"--%>
<%--                              title="Copy to clipboard"--%>
<%--                              onclick="copyToClipboard('#R<%=getSafeString(String.valueOf(api.get("ID")))%>-2', this)">--%>
<%--                                <i class="bi bi-clipboard"></i>--%>
<%--                            </span>--%>
<%--                    </td>--%>
<%--                </tr>--%>
<%--                <% }--%>
<%--                }--%>
<%--                } else { %>--%>
<%--                <tr><td colspan="3" class="text-center text-muted">--%>
<%--                    <i class="bi bi-clock-history me-2"></i>No Logs available--%>
<%--                </td></tr>--%>
<%--                <% } %>--%>
<%--                </tbody>--%>
<%--            </table>--%>

<%--            <% if(apiLogs != null && !apiLogs.isEmpty()) { %>--%>
<%--            <div class="d-flex justify-content-between align-items-center mt-3">--%>
<%--                <div class="d-flex align-items-center">--%>
<%--                    <select id="pageSize" class="form-select form-select-sm me-2" style="width: auto;" onchange="changePageSize(this.value)">--%>
<%--                        <option value="10" <%= request.getAttribute("pageSize") != null && request.getAttribute("pageSize").equals(10) ? "selected" : "" %>>10</option>--%>
<%--                        <option value="25" <%= request.getAttribute("pageSize") != null && request.getAttribute("pageSize").equals(25) ? "selected" : "" %>>25</option>--%>
<%--                        <option value="50" <%= request.getAttribute("pageSize") != null && request.getAttribute("pageSize").equals(50) ? "selected" : "" %>>50</option>--%>
<%--                    </select>--%>

<%--                    <span class="text-muted small">entries per page</span>--%>
<%--                </div>--%>
<%--                <div class="d-flex align-items-center">--%>
<%--                <span id="pageInfo" class="text-muted small me-3">--%>
<%--                    Showing ${(currentPage-1)*pageSize + 1} to ${Math.min(currentPage*pageSize, totalElements)}--%>
<%--                    of ${totalElements} entries--%>
<%--                </span>--%>
<%--                    <nav aria-label="API logs navigation">--%>
<%--                        <ul class="pagination pagination-sm mb-0" id="pagination"></ul>--%>
<%--                    </nav>--%>
<%--                </div>--%>
<%--            </div>--%>
<%--            <% } %>--%>
<%--        </div>--%>
<%--    </div>--%>

<%--</div>--%>

<!-- Warning Modal -->
<div class="modal fade" id="warningModal" tabindex="-1" aria-labelledby="warningModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="warningModalLabel">
                    <i class="bi bi-exclamation-triangle-fill text-warning me-2"></i>Warning
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                Loading all logs may cause performance issues or slow down your browser.
                Do you want to continue?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">No</button>
                <button type="button" class="btn btn-primary" onclick="loadAllLogs()">Yes</button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="repaymentModal" tabindex="-1" aria-labelledby="repaymentModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="repaymentModalLabel">
                    <i class="bi bi-credit-card me-2"></i>Repayment Details
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="repaymentForm" method="post">
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
                            <input type="text" class="form-control" id="accountNumber" name="accountNumber" required>
                        </div>

                        <div class="col-md-6">
                            <label class="form-label">IFSC Code</label>
                            <input type="text" class="form-control" id="ifscCode" name="ifscCode" required maxlength="11">
                        </div>

                        <div class="col-md-6">
                            <label class="form-label">Borrower Name</label>
                            <input type="text" class="form-control" id="borrowerName" name="borrowerName" required>
                        </div>

                        <div class="col-12">
                            <label class="form-label">Remarks</label>
                            <textarea class="form-control" id="remarks" name="remarks" rows="3" required
                                placeholder="Enter remarks for this change"></textarea>
                        </div>
                    </div>

                    <div class="text-end mt-4">
                        <button type="button" class="btn btn-secondary me-2" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-black" id="saveRepaymentBtn">
                            <i class="bi bi-save me-2"></i>Save
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Experian Modal -->
<div class="modal fade" id="experianModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Experian Data Details</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="experianForm"   >

                     <div class="mb-3">
                         <label for="expworkItem" class="form-label">Work Item</label>
                         <input type="text" readonly class="form-control" id="expworkItem" name="expworkItem" required
                              >
                         <div class="invalid-feedback">
                             Please enter a valid work item
                         </div>
                     </div>
                    <div class="mb-3">
                        <label for="panNumber" class="form-label">Pan Number</label>
                       <input type="text" class="form-control" id="panNumber" name="panNumber" required>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Remarks</label>
                        <textarea class="form-control" id="expremarks" name="expremarks" rows="3" required
                            placeholder="Enter remarks for this change"></textarea>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                             <button  class="btn btn-primary" id="saveExperianBtn">Reset Experian</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<!-- API Modal -->
<div class="modal fade" id="apiModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">API Details</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="apiForm" action="helpdesk/submit-api" method="post" target="_blank">
                    <div class="mb-3">
                        <label for="workItem" class="form-label">Work Item</label>
                        <input type="text" readonly class="form-control" id="workItem" name="workItem" required
                              value="<%=vehicleLoanMaster.getWiNum()%>">
                        <div class="invalid-feedback">
                            Please enter a valid work item
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="apiEndpoint" class="form-label">API Endpoint</label>
                        <select class="form-select select2" id="apiEndpoint" name="apiEndpoint" required>
                            <option value="">Select an API endpoint</option>
                        </select>
                        <div class="invalid-feedback">
                            Please select an API endpoint
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <button type="submit" class="btn btn-primary">Submit</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>












<!-- CSS Section -->
<style>
    .json-content {
        max-height: 150px;
        overflow-y: auto;
        position: relative;
        padding-right: 25px;
        font-family: monospace;
        white-space: pre-wrap;
        font-size: 0.875rem;
    }

    .copy-icon {
        position: absolute;
        right: 10px;
        top: 10px;
        cursor: pointer;
        opacity: 0.6;
        transition: opacity 0.2s;
    }

    .copy-icon:hover {
        opacity: 1;
    }

    .status-badge {
        padding: 0.25rem 0.5rem;
        border-radius: 0.25rem;
        font-size: 0.875rem;
    }

    .status-locked {
        background-color: #dc3545;
        color: white;
    }

    .status-unlocked {
        background-color: #198754;
        color: white;
    }

    .truncate-text {
        max-width: 300px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        display: inline-block;
    }

    .card-header {
        background-color: #f8f9fa !important;
    }

    .table th {
        background-color: #f8f9fa;
    }

    #loadingSpinner {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        z-index: 1000;
    }

    .swal2-icon-content{
        font-size: 3.75em;
    }

    .table-responsive {
        position: relative;
        min-height: 200px;
    }

    .badge {
        font-weight: normal;
    }

    .pagination .page-link {
        padding: 0.25rem 0.5rem;
        font-size: 0.875rem;
    }

    .custom-tooltip {
        cursor: help;
    }

    #errorState {
        margin-bottom: 1rem;
    }

    .alert {
        margin-bottom: 1rem;
    }

    .action-button {
        min-width: 160px;
    }

    .page-link:focus {
        box-shadow: none;
    }

    .disabled {
        cursor: not-allowed;
        pointer-events: none;
    }

    @media (max-width: 768px) {
        .truncate-text {
            max-width: 200px;
        }

        .json-content {
            max-height: 100px;
        }
    }
</style>
