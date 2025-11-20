<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanBasic" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.model.integrations.VLHunterDetails" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.sib.ibanklosucl.dto.acopn.RepayAcctDTO" %>
<%@ page import="com.sib.ibanklosucl.model.NACHMandate" %>
<style>
    .nach-button {
        min-width: 150px;
        transition: all 0.3s ease;
    }

    .nach-button.d-none {
        display: none !important;
    }

    .nach-status {
        transition: all 0.3s ease;
    }

    .indicator-progress {
        display: none;
    }

    .btn.loading .indicator-label {
        display: none;
    }

    .btn.loading .indicator-progress {
        display: inline-block;
    }
</style>
<%
    // Initialize main objects from request attributes
    VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
    RepayAcctDTO vehicleLoanRepayment = (RepayAcctDTO) request.getAttribute("repaymentDetails");
    NACHMandate nachMandate = (NACHMandate) request.getAttribute("nachMandate");
    Boolean checker = request.getAttribute("checker") != null ?
                     request.getAttribute("checker").toString().equals("Y") : false;

    // Initialize default values
    String openedAccountNo = "";
    String ac_open_flg = "";
    String bank_details = "";
    String repaymentAccno = "";
    String borrowerName = "";
    String pan = "";
    String email = "";
    String mobile = "";
    String mandateStatus = "";
    String accordion_style = "btn-active-light-primary";

    // Initialize flags
    boolean isOwnBank = false;
    boolean showNachRequest = false;
    boolean showNachCancel = false;
    boolean showStatusCheck = false;
    boolean showResendNach = false;
    boolean nachCompletedStatus = false;

    // Process Vehicle Loan Master data
    if (vehicleLoanMaster != null) {
        openedAccountNo = vehicleLoanMaster.getAccNumber();
        ac_open_flg = vehicleLoanMaster.getAccOpened() != null ?
                      vehicleLoanMaster.getAccOpened() : "";

        // Process applicants
        List<VehicleLoanApplicant> applicants = vehicleLoanMaster.getApplicants().stream()
                .filter(fd -> "N".equals(fd.getDelFlg()))
                .toList();

        // Get primary applicant details
        for (VehicleLoanApplicant applicant : applicants) {
            if ("A".equals(applicant.getApplicantType())) {
                pan = applicant.getKycapplicants().getPanNo();
                email = applicant.getBasicapplicants().getEmailId();
                mobile = applicant.getBasicapplicants().getMobileNo();
                break;
            }
        }

        // Process NACH mandate status
        if (nachMandate != null) {
            mandateStatus = nachMandate.getStatus();
            switch (mandateStatus) {
                case "Authorization Request Rejected":
                case "Rejected By NPCI":
                    showNachRequest = true;
                    showStatusCheck = true;
                    nachCompletedStatus = false;
                    showResendNach = false;
                    break;
                case "Progressed by User":
                    showNachCancel = true;
                    showStatusCheck = true;
                    showResendNach = true;
                    nachCompletedStatus = false;
                    break;
                case "Authorized":
                    showStatusCheck = true;
                    showResendNach = true;
                    nachCompletedStatus = true;
                    break;
                case "Initiated":    // Added explicit handling for Initiated status
                    showStatusCheck = true;
                    showResendNach = true;  // Enable resend for Initiated status
                    nachCompletedStatus = false;
                    break;
                default:
                    showNachRequest = true;
                    showStatusCheck = true;
                    nachCompletedStatus = false;
            }
        } else {
            showNachRequest = true;
            nachCompletedStatus = false;
        }

    }

    // Process repayment details
    if (vehicleLoanRepayment != null) {
        bank_details = vehicleLoanRepayment.getBankName();
        repaymentAccno = vehicleLoanRepayment.getAccountNumber();
        borrowerName = vehicleLoanRepayment.getBorrowerName();
        isOwnBank = "THE SOUTH INDIAN BANK LIMITED".equalsIgnoreCase(bank_details);
    }
%>

<!-- Begin::NACH Section -->
<div class="border rounded px-7 py-3 mb-2">
    <!-- Hidden Fields -->
    <input type="hidden" id="hidac_open_flg" name="hidac_open_flg" value="<%=ac_open_flg%>">
    <input type="hidden" id="currentMandateStatus" value="<%=mandateStatus%>">
    <input type="hidden" id="nachCompletedStatus" value="<%=nachCompletedStatus%>">

    <!-- NACH Header -->
    <div class="accordion-header d-flex collapsed" data-bs-toggle="collapse" id="nachtableDetails" data-bs-target="#nachtable">
        <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start p-5 d-flex align-items-center mb-2 <%=accordion_style%>">
            <i class="ki-duotone ki-cheque fs-4x me-4">
                <span class="path1"></span>
                <span class="path2"></span>
                <span class="path3"></span>
                <span class="path4"></span>
                <span class="path5"></span>
                <span class="path6"></span>
                <span class="path7"></span>
            </i>
            <span class="d-block fw-semibold text-start">
                <span class="text-gray-900 fw-bold d-block fs-3">NACH</span>
                <span class="text-muted fw-semibold fs-6">
                    Send mandate request to the customer (Mandatory for Other Bank Repayment accounts)
                </span>
            </span>
        </label>
    </div>

    <!-- NACH Content -->
    <div id="nachtable" class="fs-6 collapse ps-2" data-bs-parent="#vl_rc_int">
        <% if (!isOwnBank) { %>
            <div class="row">
                <div class="col-lg-6">
                    <div class="m-3">
                        <!-- NACH Details Table -->
                        <table class="table table-sm align-middle table-row-dashed table-row-gray-400 fs-8 gy-3">
                            <tbody>
                                <!-- Borrower Details -->
                                <tr>
                                    <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase">Borrower Name</th>
                                    <td><%=borrowerName%></td>
                                </tr>
                                <tr>
                                    <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase">Bank Name</th>
                                    <td><%=bank_details%></td>
                                </tr>
                                <tr>
                                    <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase">Repayment Account</th>
                                    <td><%=repaymentAccno%></td>
                                </tr>

                                <!-- EMI Details -->
                                <tr>
                                    <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase">Tenure</th>
                                    <td id="nachtenor"></td>
                                </tr>
                                <tr>
                                    <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase">Collection Amount</th>
                                    <td id="nachemi"></td>
                                </tr>
                                <tr>
                                    <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase">EMI Start Date</th>
                                    <td id="inststartdate"></td>
                                </tr>
                                <tr>
                                    <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase">EMI End Date</th>
                                    <td id="instenddate"></td>
                                </tr>

                                <!-- Customer Details -->
                                <tr>
                                    <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase">PAN</th>
                                    <td><%=pan%></td>
                                </tr>
                                <tr>
                                    <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase">Mobile</th>
                                    <td><%=mobile%></td>
                                </tr>
                                <tr>
                                    <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase">Email</th>
                                    <td><input type="hidden" name="emailId" id="emailId" value="<%=email%>"><%=email%></td>
                                </tr>

                                <!-- Mandate Status -->
                                <tr id="mandateStatusRow">
                                    <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase">Mandate Status</th>
                                    <td>
                                        <%
                                            String statusColor = nachCompletedStatus ? "bg-light-success" : "bg-light-warning";
                                            String textColor = nachCompletedStatus ? "text-success" : "text-warning";
                                        %>
                                        <div class="d-flex align-items-center <%=statusColor%> rounded p-5 mb-7">
                                            <i class="ki-duotone ki-abstract-26 <%=textColor%> fs-1 me-5">
                                                <span class="path1"></span>
                                                <span class="path2"></span>
                                            </i>
                                            <div class="flex-grow-1 me-2">
                                                <a href="#" class="fw-bold text-gray-800 text-hover-primary fs-6">
                                                    <%=mandateStatus%>
                                                </a>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <!-- Action Buttons -->
                        <div class="mb-1 mt-1 text-end">
    <!-- Always render all buttons but initially hide them with d-none class -->


    <button id="kt_button_check_status" type="button"
            class="btn btn-sm btn-light-info nach-button"
            <%= !showStatusCheck ? "disabled" : "" %>
            data-bs-toggle="tooltip" title="Check current mandate status">
        <span class="indicator-label">Check Mandate Status</span>
        <span class="indicator-progress">Please wait...
            <span class="spinner-border spinner-border-sm align-middle ms-2"></span>
        </span>
    </button>




</div>


                        <!-- Checker Save Button -->
                        <% if (checker) { %>
                            <div class="text-end mt-3">
                                <input type="hidden" id="collapseDisable" value="1">

                            </div>
                        <% } %>
                    </div>
                </div>
            </div>
        <% } else { %>
            <div class="alert alert-info">
                <i class="ki-duotone ki-information-5 fs-2 me-2">
                    <span class="path1"></span>
                    <span class="path2"></span>
                    <span class="path3"></span>
                </i>
                <span class="text-muted fw-semibold fs-6">
                    Not applicable for SIB Customers
                </span>
            </div>
        <% } %>
    </div>
</div>
<!-- End::NACH Section -->

<!-- Custom Styles -->
<style>
    .nach-button {
        min-width: 150px;
        transition: all 0.3s ease;
    }

    .nach-status {
        transition: all 0.3s ease;
    }

    .indicator-progress {
        display: none;
    }

    .btn.loading .indicator-label {
        display: none;
    }

    .btn.loading .indicator-progress {
        display: inline-block;
    }
</style>

<!-- Initialize Tooltips -->

<script type="text/javascript">
    // Pass any server-side variables needed by JavaScript
    window.NACH_CONFIG = {
        currentStatus: '<%=mandateStatus%>',
        isOwnBank: <%=isOwnBank%>,
        nachCompletedStatus: <%=nachCompletedStatus%>
    };
</script>
<script src="assets/js/custom/nach-management.js"></script>
<!-- Include the NACH management script -->

<script>
    document.addEventListener('DOMContentLoaded', function() {
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    });
</script>
