<%--
  Created by IntelliJ IDEA.
  User: SIBL12071
  Date: 20-08-2024
  Time: 14:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.model.doc.VehicleLoanRepayment" %>
<%@ page import="javax.persistence.Column" %>
<%@ page import="com.sib.ibanklosucl.model.Misrct" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sib.ibanklosucl.dto.acopn.RepayAcctDTO" %><%--
  Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 14-08-2024
  Time: 01:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  RepayAcctDTO vehicleLoanRepayment= (RepayAcctDTO) request.getAttribute("repaymentDetails");
  String bankName="",repaymentAccno="",ifsc="",borrowerName="";
  if(vehicleLoanRepayment!=null){
    bankName= vehicleLoanRepayment.getBankName();
    repaymentAccno= vehicleLoanRepayment.getAccountNumber();
    ifsc= vehicleLoanRepayment.getIfscCode();
    borrowerName= vehicleLoanRepayment.getBorrowerName();
  }
%>
<div class="flex-stack border rounded px-7 py-3 mb-2">
  <div class="w-100">
    <div class="accordion-header d-flex collapsed  " data-bs-toggle="collapse" id="decisionDetailslink" data-bs-target="#repayDetailsContent">
      <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
             for="repayDetailsContent">
        <i class="ki-duotone ki-wallet  fs-3x me-4">
          <span class="path1"></span>
          <span class="path2"></span>
        </i>
        <span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-4">Repayment Details</span>
                    <span class="text-muted fw-semibold fs-7">
                     Enter Repayment details.
                    </span>
                </span>
      </label>
    </div>
    <div id="repayDetailsContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">

      <div class="row">
        <div class="col-sm-12">
          <!--begin::Repeater-->
          <div id="kt_docs_repeater_basic">
            <form id="bankDetailsForm" name="bankDetailsForm" method="POST">
              <!-- Bank Name -->
              <div class="mb-3">
                <label for="bankName" class="form-label">Bank Name</label>
                <input type="text" class="form-control" id="bankName" name="bankName" required value="<%=bankName%>" readonly disabled/>
              </div>

              <!-- Account Number -->
              <div class="mb-3">
                <label for="accountNumber" class="form-label">Account Number</label>
                <input type="text" class="form-control" id="accountNumber" name="accountNumber" required value="<%=repaymentAccno%>" readonly disabled/>
              </div>

              <!-- IFSC Code -->
              <div class="mb-3">
                <label for="ifscCode" class="form-label">IFSC Code</label>
                <input type="text" class="form-control" id="ifscCode" name="ifscCode" minlength="11" maxlength="11" required value="<%=ifsc%>" readonly disabled/>
              </div>

              <!-- Borrower Name -->
              <div class="mb-3">
                <label for="borrowerName" class="form-label">Borrower Name</label>
                <input type="text" class="form-control" id="borrowerName" name="borrowerName" required value="<%=borrowerName%>" readonly disabled/>
              </div>

            </form>


          </div>
          <!--end::Repeater-->
        </div>
      </div>
    </div>
  </div>
</div>

