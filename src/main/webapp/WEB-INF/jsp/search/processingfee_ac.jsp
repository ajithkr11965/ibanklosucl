<%--
  Created by IntelliJ IDEA.
  User: SIBL12071
  Date: 19-08-2024
  Time: 12:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.sib.ibanklosucl.dto.acopn.ROIProcFeeDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.model.EligibilityDetails" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanDetails" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="org.springframework.security.core.parameters.P" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanSubqueueTask" %>
<%@ page import="com.sib.ibanklosucl.model.doc.VehicleLoanChargeWaiver" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="flex-stack border rounded sancApprove hide px-7 py-3 mb-2">
  <div class="w-100">
    <div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="processFeeLink" data-bs-target="#processFeeContent">
      <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
             for="processFeeContent">
        <i class="ki-duotone ki-element-9 fs-3x me-4">
          <span class="path1"></span>
          <span class="path2"></span>
        </i>
        <span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-4">Processing Fee Waiver</span>
                    <span class="text-muted fw-semibold fs-7">
                     Send WI for Processing Fee Waiver
                    </span>
                </span>
      </label>
    </div>
    <div id="processFeeContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">
      <%   VehicleLoanMaster master= (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
        EligibilityDetails eligibilityDetails= (EligibilityDetails) request.getAttribute("eligibilityDetails");
        List<ROIProcFeeDTO.ProcessFeeFinalDto> feeFinal= (List<ROIProcFeeDTO.ProcessFeeFinalDto>) request.getAttribute("processFeeFinal");
        BigDecimal Processfee;
      %>
      <div class="row">
        <div class="col-sm-12">
          <!--begin::Repeater-->
          <div id="kt_docs_repeater_basic">
            <form id="feeDetailsForm" name="feeDetailsForm" method="POST">
              <div class="feewaiveRequired>
              <div class="col-sm-12 mb-3">
                <table class="table table-sm table-bordered table-hover">
                  <thead>
                  <tr>
                    <th>Fee Name</th>
                    <th>Initial Amount</th>
                    <th>Final Amount</th>
                  </tr>
                  </thead>
                  <tbody>
                  <%

                    List<Map<String, Object>> feeLevels = (List<Map<String, Object>>) request.getAttribute("roiLevels");
                    String feeName="",feeInitialAmt="",feeFinalAmt="";
                    for(ROIProcFeeDTO.ProcessFeeFinalDto fee:feeFinal){
                      feeName=fee.getFeeName()==null?"":fee.getFeeName();
                      feeInitialAmt=fee.getFeeInitialValue()==null?"":fee.getFeeInitialValue();
                      feeFinalAmt=fee.getFeeFinalValue() == null?"":fee.getFeeFinalValue();

                  %>
                  <tr>
                    <td> <input type="text" class="form-control feeValue" id="feeName" name="feeName"  required value="<%=feeName%>" readonly /></td>
                    <td> <input type="text" class="form-control feeValue" id="feeInitialAmt" name="feeInitialAmt"  required value="<%=feeInitialAmt%>" readonly /></td>
                    <td> <input type="text" class="form-control feeValue" id="feeFinalAmt" name="feeFinalAmt"  required value="<%=feeFinalAmt%>"  readonly/></td>
                  </tr>
                  <%
                    }
                  %>
                  </tbody>
                </table>
              </div>
          </div>
          </form>

        </div>
        <!--end::Repeater-->
      </div>
    </div>
  </div>
</div>
</div>

