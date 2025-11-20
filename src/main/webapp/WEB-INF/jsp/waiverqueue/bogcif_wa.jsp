<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script>
  $(document).ready(function () {
  });


</script>

<div class="flex-stack border rounded  hide sancApprove px-7 py-3 mb-2">
  <div class="w-100">
    <div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="cifLink" data-bs-target="#cifContent">
      <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
             for="cifContent">
        <i class="ki-duotone ki-element-9 fs-3x me-4">
          <span class="path1"></span>
          <span class="path2"></span>
        </i>
        <span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-4">CIF ID Creation</span>
                    <span class="text-muted fw-semibold fs-7">
                     Send WI for CIF ID Creation
                    </span>
                </span>
      </label>
    </div>
    <div id="cifContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">

      <div class="row">
        <div class="col-sm-12">
          <!--begin::Repeater-->
          <div id="kt_docs_repeater_basic">
            <div class="p-5 text-center">
              <div class="card-header">
                <h5 class="mb-2 text-success">CIF Creation Mode</h5>
              </div>
              <div class="checkbox-wrapper-16">

                <label class="checkbox-wrapper">
                  <input class="checkbox-input"  name="cifMode" type="radio" value="M" checked="" disabled readonly>
                  <span class="checkbox-tile">
                                                  <span class="checkbox-icon">
                                                  </span>
                                                  <span class="checkbox-label">Manual</span>
                                                </span>
                </label>
                <label class="checkbox-wrapper pl-2">
                  <input class="checkbox-input" name="cifMode" type="radio" value="V" disabled readonly>
                  <span class="checkbox-tile">
                                                  <span class="checkbox-icon">
                                                  </span>
                                                  <span class="checkbox-label">VKYC</span>
                                                </span>
                </label>

                <div class="col-sm-12 pt-4">
                  <table class="table table-sm table-bordered table-hover">
                    <thead>
                    <tr>
                      <th>Type</th>
                      <th>Name</th>
                      <th>Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                      VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
                      List<VehicleLoanApplicant> vehicleLoanApplicants = new ArrayList<>();
                      if (vehicleLoanMaster != null) {
                        vehicleLoanApplicants = vehicleLoanMaster.getApplicants();
                      }
                      for (VehicleLoanApplicant applicant : vehicleLoanApplicants) {
                        String applicantType="";
                        if ("A".equals(applicant.getApplicantType())) {
                          applicantType = "Applicant";
                        } else if ("C".equals(applicant.getApplicantType())) {
                          applicantType = "Co-Applicant";
                        } else if ("G".equals(applicant.getApplicantType())) {
                          applicantType = "Guarantor";
                        }

                    %>
                    <tr>

                      <td><%=applicantType%>
                      </td>
                      <td><%=applicant.getBasicapplicants().getApplicantName()%>
                      </td>
                      <td>PENDING
                      </td>
                    </tr>
                    <%
                        }
                    %>
                    </tbody>
                  </table>
                </div>

<%--                <div class="text-end pt-1 kt">--%>
<%--                  <button type="button" id="cifProceed" class="btn btn-lg btn-primary" data-kt-stepper-action="next">Proceed--%>
<%--                    <i class="ki-duotone ki-double-right ">--%>
<%--                      <i class="path1"></i>--%>
<%--                      <i class="path2"></i>--%>
<%--                    </i></button>--%>
<%--                </div>--%>

              </div>
            </div>


          </div>
          <!--end::Repeater-->
        </div>
      </div>
    </div>
  </div>
</div>
