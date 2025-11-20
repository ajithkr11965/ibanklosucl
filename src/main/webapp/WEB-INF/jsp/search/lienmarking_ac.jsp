<%@ page import="com.sib.ibanklosucl.model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>

<%
    VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
    Boolean checker = request.getAttribute("checker") != null ? request.getAttribute("checker").toString().equals("Y") : false;
    List<VehicleLoanApplicant> vehicleLoanApplicants = new ArrayList<>();
    String collect_amount = "", borrowerName = "", phone = "", mobile = "", email = "", pan = "", showFlg = "", accordion_style = "btn-active-light-primary";
    BigDecimal totalFDBalance = BigDecimal.ZERO;

    if (vehicleLoanMaster != null) {
        vehicleLoanApplicants = vehicleLoanMaster.getApplicants().stream()
                .filter(fd -> "N".equals(fd.getDelFlg()))
                .toList();
//        for (VehicleLoanApplicant applicant : vehicleLoanApplicants) {
//            if ("A".equals(applicant.getApplicantType())) {
//                pan = applicant.getKycapplicants().getPanNo();
//                email = applicant.getBasicapplicants().getEmailId();
//                mobile = applicant.getBasicapplicants().getMobileNo();
//            }
//        }
        String loanAcctNo = vehicleLoanMaster.getAccNumber();
    } else {
    }
%>

<div class="border rounded px-7 py-3 mb-2">
    <div class="">
        <div class="accordion-header d-flex collapsed" data-bs-toggle="collapse" id="lientableDetails" data-bs-target="#lientable">
            <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start p-5 d-flex align-items-center mb-2 <%=accordion_style%> hide"
                   for="lientable">
                <i class="ki-duotone ki-key-square fs-4x me-4">
                    <span class="path1"></span>
                    <span class="path2"></span>
                </i>
                <span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-3">Collateral Lien Marking </span>
                    <span class="text-muted fw-semibold fs-6">
                      Applicable for FD Loan program
                    </span>
                </span>
            </label>
        </div>
        <div id="lientable" class="fs-6 collapse ps-2" data-bs-parent="#vl_rc_int">
            <div class="row">
                <div class="col-sm-12">
                    <%
                    boolean hasLoanFD = false;
                    for (VehicleLoanApplicant applicant : vehicleLoanApplicants) {
                        VehicleLoanProgram program = applicant.getVlProgram();
                        if (program != null && "LOANFD".equals(program.getLoanProgram())) {
                            hasLoanFD = true;
                            %>
                            <h4>FD Details for <%= applicant.getApplName() %></h4>
                            <table class="table table-bordered">
                                <thead>
                                    <tr>
                                        <th>FD Account Number</th>
                                        <th>Account Status</th>
                                        <th>Account Type</th>
                                        <th>Deposit Amount</th>
                                        <th>Available Balance</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                    for (VehicleLoanFD fd : program.getVehicleLoanFDList()) {
                                        if ("N".equals(fd.getDelFlg()) && fd.isEligible()) {
                                            totalFDBalance = totalFDBalance.add(fd.getAvailbalance());
                                    %>
                                    <tr>
                                        <td><%= fd.getFdaccnum() %></td>
                                        <td><%= fd.getFdStatus() %></td>
                                        <td><%= fd.getSingleJoint() %></td>
                                        <td><%= fd.getDepositAmount() %></td>
                                        <td><%= fd.getAvailbalance() %></td>
                                    </tr>
                                    <%
                                        }
                                    }
                                    %>
                                </tbody>
                            </table>
                            <%
                        }
                    }
                    if (!hasLoanFD) {
                    %>
                        <p>No eligible FD Loan programs found for any applicant.</p>
                    <% } %>
                </div>
            </div>

            <% if (hasLoanFD) { %>
            <div class="row mt-3">
                <div class="col-sm-6">
                    <h5>Total Available FD Balance: <%= totalFDBalance %></h5>
                </div>
            </div>
            <% } %>

            <div class="text-end">
                <input type="hidden" id="collapseDisable" value="<%=checker?1:0%>">

            </div>
        </div>
    </div>
</div>

<script>
    $(document).ready(function () {
        setupEventListeners();
    });

    function setupEventListeners() {
        $('#lientableDetails').click(fetchLienDetails);
        $('#lienMarkingSave').click(saveLienMarking);
    }

    function fetchLienDetails() {
        // Add logic to fetch lien details if needed
        console.log("Fetching lien details...");
    }

    function saveLienMarking() {
        // Add logic to save lien marking details
        console.log("Saving lien marking details...");
    }
</script>