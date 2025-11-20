<%--
  Created by IntelliJ IDEA.
  User: SIBL12134
  Date: 13-08-2024
  Time: 11:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanBasic" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="com.sib.ibanklosucl.model.integrations.VLBlackList" %>
<%@ page import="java.util.*" %>
<%@ page import="com.sib.ibanklosucl.model.integrations.VLPartialBlackList" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanCIF" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanSubqueueTask" %>
<style>
    .modal-dialog,
    .modal-content {
        /* 80% of window height */
        height: 80%;
    }

    .modal-body {
        /* 100% = dialog height, 120px = header + footer */
        max-height: calc(100% - 120px);
        overflow-y: scroll;
    }
</style>
<%
    try {
        VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        List<VehicleLoanApplicant> vehicleLoanApplicants = new ArrayList<>();
        VehicleLoanBasic vehicleLoanBasic = null;
        String applicantId = "", wiNum = "", slno = "", applicantype = "", showFlg = "", accordion_style = "btn-active-light-primary";
        Boolean checker = request.getAttribute("checker") != null ? request.getAttribute("checker").toString().equals("Y") : false;
        if (vehicleLoanMaster != null) {
            vehicleLoanApplicants = vehicleLoanMaster.getApplicants();
            //System.out.println("aaaaaaaaaaaaaaaaaa:"+vehicleLoanApplicants.size());
            for (VehicleLoanApplicant applicant : vehicleLoanApplicants) {
                applicantId = String.valueOf(applicant.getApplicantId());
                List<VLBlackList> blacklists = applicant.getVlBlackList();
                if (blacklists != null && !blacklists.isEmpty()) {
                    VLBlackList blacklist = null;
                    if (blacklists != null) {
                        for (VLBlackList vlBlackList : blacklists) {
                            if ("N".equals(vlBlackList.getDelFlg())) {
                                blacklist = vlBlackList;
                                showFlg = "show";
                                accordion_style = "btn-active-light-success";
                            }
                        }
                    }
                } else {
                    out.println("in error - bl is empty");
                }
            }
        } else {
            out.println("in error - general is empty");
        }
%>



<div class="w-100 cifCreationDiv">
    <div class="d-flex flex-stack border rounded px-7 py-3 mb-2">


        <input type="hidden" name="applicantId" class="applicantId" value="<%=applicantId%>">
        <input type="hidden" name="applicantype" class="applicantype" value="<%=applicantype%>">
        <div class="w-100">
            <div class="accordion-header d-flex  " data-bs-toggle="collapse" id="applDetails" data-bs-target="#applTable">
                <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start p-5 d-flex align-items-center mb-2" for="applTable">
                    <i class="ki-duotone ki-plus  fs-3x me-4">
                        <span class="path1"></span>
                        <span class="path2"></span>
                        <span class="path3"></span>
                        <span class="path4"></span>
                        <span class="path5"></span>
                        <span class="path6"></span>
                    </i>
                    <span class="d-block fw-semibold text-start">
                        <span class="text-gray-900 fw-bold d-block fs-4">CIF Creation</span>
                        <span class="text-muted fw-semibold fs-7">

                        </span>
                    </span>
                </label>
            </div>
            <div id="applTable" class="fs-6 collapse  ps-10" data-bs-parent="#vl_rm_int">

                <div class="row">
                    <div class="content d-flex flex-column flex-column-fluid">
                        <div class="col-sm-12">
                            <div class="mb-3 mt-3">
                                <div class="row">
                                    <div class="col-sm-12">
                                        <div class="table-responsive">
                                            <table class="table table-sm align-middle table-row-dashed table-row-gray-400 fs-8 gy-3">
                                                <thead>
                                                <tr class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">
                                                    <th>Applicant Type</th>
                                                    <th>Applicant Name</th>
                                                    <%--                          <th>Customer ID</th>--%>
                                                    <%--                          <th>DOB</th>--%>
                                                    <%--                          <th>PAN</th>--%>
                                                    <%--                          <th>AADHAAR</th>--%>
                                                    <%--                          <th>PASSPORT</th>--%>
                                                    <th>Action</th>
                                                    <th>Run date</th>
                                                    <th>Blacklist status</th>
                                                    <th>Decision</th>
                                                    <th>Remarks</th>
                                                    <th>Action</th>

                                                    <th>CIF ID</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <%
                                                    for (VehicleLoanApplicant applicant : vehicleLoanApplicants) {
                                                        String runDate = "";
                                                        String blstatus = "";
                                                        String applicantType = "";
                                                        boolean mismatch = false;
                                                        String mismatchFields = "";
                                                        String cifid=applicant.getCifId();if(cifid==null)cifid="";
                                                        String decision="", remarks="", blFlag="";

                                                        List<VehicleLoanSubqueueTask> vehicleLoanSubqueueTasks = applicant.getVehicleLoanSubqueueTaskList();
                                                        VehicleLoanSubqueueTask task=null;
                                                        for (VehicleLoanSubqueueTask t : vehicleLoanSubqueueTasks) {
                                                            if ("CIF_CREATION".equals(t.getTaskType()) && t.getCompletedDate() == null) {//ideally only 1 record with completed date null will be available for a given applicant id
                                                                task = t;
                                                            }
                                                        }
                                                        if(task==null){
                                                            continue;
                                                        }
/*
                                                        List<VLPartialBlackList> blacklists = applicant.getVlPartialBlackList();
                                                        VLPartialBlackList blacklist = null;
                                                        if (blacklists != null) {
                                                            for (VLPartialBlackList vlBlackList : blacklists) {
                                                                if ("N".equals(vlBlackList.getDelFlg())) {
                                                                    blacklist = vlBlackList;
                                                                }
                                                            }

                                                        }*/

                                                        List<VehicleLoanCIF> vlCifs=applicant.getVehicleLoanCif();
                                                        VehicleLoanCIF vlCif=null;
                                                        for (VehicleLoanCIF c : vlCifs) {
                                                            if ("N".equals(c.getDelFlag()) && task.getTaskId().equals(c.getTaskId())) {
                                                                vlCif = c;
                                                            }
                                                        }
                                                        if(vlCif!=null) {
                                                            decision = vlCif.getDecision();
                                                            if (decision == null) decision = "";
                                                            blstatus = vlCif.getBlFlag();
                                                            if(blstatus==null)blstatus="";
                                                            if(vlCif.getBlDate()!=null){
                                                                runDate=(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).format(vlCif.getBlDate());
                                                            }
                                                            blFlag=vlCif.getBlFlag();
                                                            if(blFlag==null)blFlag="";
                                                            remarks = vlCif.getRemarks();if(remarks==null)remarks="";
                                                        }
                                                        Date dob = applicant.getKycapplicants().getPanDob();
                                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");// HH:mm:ss
                                                        String formattedDob = simpleDateFormat.format(dob);
                                                        String passport = (applicant.getKycapplicants() != null) ? StringUtils.defaultString(applicant.getKycapplicants().getPassportNumber()) : "";
                                                        applicantId = (applicant.getApplicantId() != null) ? StringUtils.defaultString(String.valueOf(applicant.getApplicantId())) : "";
                                                        if ("A".equals(applicant.getApplicantType())) {
                                                            applicantType = "Applicant";
                                                        } else if ("C".equals(applicant.getApplicantType())) {
                                                            applicantType = "Co-Applicant";
                                                        } else if ("G".equals(applicant.getApplicantType())) {
                                                            applicantType = "Guarantor";
                                                        }



                            /*
                            if (blacklist != null) {
                              runDate = new SimpleDateFormat("dd/MM/yyyy").format(blacklist.getBlCheckDate());
                              status = blacklist.getBlCheckResult();

                              if (!applicant.getKycapplicants().getPanNo().equals(blacklist.getPan())) {
                                mismatch = true;
                                mismatchFields += "PAN, ";
                              }
                              String applicantDob = dateFormat.format(applicant.getBasicapplicants().getApplicantDob());
                              String blacklistDob = dateFormat.format(dateFormat.parse(blacklist.getDob()));

                              if (!applicantDob.equals(blacklistDob)) {
                                mismatch = true;
                                mismatchFields += "DOB, ";
                              }

//														if (!applicant.getKycapplicants().getPassportNumber().equals(blacklist.getPassport())) {
//															mismatch = true;
//															mismatchFields += "Passport, ";
//														}
                              if (mismatchFields.endsWith(", ")) {
                                mismatchFields = mismatchFields.substring(0, mismatchFields.length() - 2);
                              }
                            } else {
                              //out.print("vlblack is null");
                            }

                             */

                                                        String applicantName="",lastName="";
                                                        applicantName=applicant.getBasicapplicants().getApplicantName();
                                                        if(applicantName.contains(" ")){
                                                            lastName = applicantName.substring(applicantName.lastIndexOf(' ') + 1);
                                                        }
                                                %>

                                                <tr class="table-row-gray-400 tr<%=applicantId%>">
                                                    <td><%=applicantType%>(#<%=applicant.getApplicantId()%>)
                                                    </td>
                                                    <td class="text-gray-800 text-hover-primary fs-7 fw-bold"><%=applicantName%>
                                                    </td>
                                                    <!--
                                                  <td class="text-gray-800 text-hover-primary fs-7 fw-bold"><=cifid%>
                                                  </td>
                                                  -->
                                                    <td>
                                                        <button  <%=(cifid==null || cifid.trim().isEmpty() ) ? "" : "disabled"%> id="kt_button_<%=applicantId%>" type="button" class="btn btn-sm btn-light-primary runblacklistCheck">
                                                            <!--data-bl-applicantId="
                                                        <=applicantId%"-->
                                                            <span class="indicator-label">
         <%= blFlag.equals("Y") ? "Re-run Partial Blacklist" : "Run Partial blacklist" %>
    </span>
                                                            <span class="indicator-progress">
        Please wait... <span class="spinner-border spinner-border-sm align-middle ms-2"></span>
    </span>
                                                        </button>

                                                        <input type="hidden" class="applicantData" data-applicantId="<%=applicantId%>" data-custname="<%=applicantName%>"
                                                               data-gender="<%=applicant.getBasicapplicants().getGender()%>" data-lastname="<%=lastName%>" >
                                                    </td>
                                                    <td>
                                                        <span id="rundate<%=applicantId%>"><%=runDate%></span>
                                                    </td>
                                                    <td>
                                                        <%if ("Y".equals(blstatus)) {%>
                                                        <span class="badge badge-light-success mb-1 blstatus<%=applicantId%>">Completed</span>
                                                        <%
                                                        }else {
                                                        %>
                                                        <span class="badge badge-light-warning mb-1 blstatus<%=applicantId%>">Pending</span>
                                                        <%
                                                            }
                                                        %>
                                                        <%  %>

                                                    </td>
                                                    <td>
                                                        <select name="decision" class="form-select form-select-solid decisionddl" id="decision<%=applicantId%>" <%=(cifid.trim().isEmpty() ) ? "" : "disabled"%> required>
                                                            <option value="" selected>select</option>
                                                            <option value="APPROVE" <%=decision.equals("APPROVE")?"selected":""%>>Approve</option>
                                                            <option value="REJECT"  <%=decision.equals("REJECT")?"selected":""%>>Reject</option>
                                                        </select>
                                                    </td>
                                                    <td>
                                                        <input type="text" id="remarks<%=applicantId%>" <%=(cifid.trim().isEmpty() ) ? "" : "disabled"%>  class="form-control" placeholder="Enter remarks" maxlength="300" value="<%=remarks%>" required/>
                                                    </td>
                                                    <td>
                                                        <button <%=(cifid.trim().isEmpty() ) ? "" : "disabled"%> id="kt_cifbutton_<%=applicantId%>" type="button"
                                                         class="btn btn-sm btn-light-primary submitBtn">
                                                        <span class="indicator-label">Submit</span>
                                                        <span class="indicator-progress">
        Please wait... <span class="spinner-border spinner-border-sm align-middle ms-2"></span>
    </span>
                                                        </button>
                                                    </td>
                                                    <td>
                                                        <b><span id="cifid<%=applicantId%>" class=".cifids"><%=cifid%></span></b>
                                                    </td>
                                                </tr>
                                                <%}%>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
                <div class="text-end">
                    <input type="hidden" id="collapseDisable" value="<%=checker?1:0%>">
                    <%if (checker) {%>

                    <button type="button" id="blacklistSave" class="btn btn-sm btn-primary blacklistSave">Proceed
                    </button>
                    <%}%>
                </div>

            </div>
        </div>



    </div>
</div>
<div id="alert_modal_bl" class="modal" tabindex="-1"  aria-modal="true"  data-bs-keyboard="false" data-bs-backdrop="static"  role="dialog">
    <div class="modal-dialog modal-xl  modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-danger  text-white border-0 ">
                <h5 class="modal-title">Partial Blacklist Response</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="table-responsive">
                <table class="table table-sm align-middle table-row-dashed table-row-gray-400 fs-8 gy-3 table-bordered" id="blTable">
                    <thead>
                    <tr class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">
                        <th>Slno</th>
                        <th>Age</th>
                        <th>Aliases</th>
                        <th>Companies</th>
                        <th>Countries</th>
                        <th>DOB</th>
                        <th>Firstname</th>
                        <th>Further info</th>
                        <th>Last name</th>
                        <th>Locations</th>
                        <th>Passports</th>
                        <th>Place of birth</th>
                        <th>UID</th>
                    </tr>
                    </thead>
                    <tbody></tbody>
                </table>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-link" data-bs-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<%} catch (Exception e) {
    e.printStackTrace();

}%>
