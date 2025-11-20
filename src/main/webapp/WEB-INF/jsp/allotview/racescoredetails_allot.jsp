<%@ page import="com.sib.ibanklosucl.model.integrations.VehicleLoanBREDetails" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.awt.*" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ page import="com.sib.ibanklosucl.model.*" %>
<%@ page import="java.text.SimpleDateFormat" %><%--
  Created by IntelliJ IDEA.
  User: SIBL12134
  Date: 02-07-2024
  Time: 18:25
  To change this template use File | Settings | File Templates.
--%>
<%
	VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
	Boolean checker = request.getAttribute("checker") != null ? request.getAttribute("checker").toString().equals("Y") : false;
	List<VehicleLoanAmber> vehicleLoanAmberList = (List<VehicleLoanAmber>) request.getAttribute("vehicleLoanAmberList");
	List<VehicleLoanBREDetails> vehicleLoanBREDetailsList = new ArrayList<>();
	VehicleLoanBREDetails vehicleLoanbre = null;
	String breresponse = "";
	String itemColor = "";
	String eligibilityFlag = "", breFlag = "", breContent = "", showFlg = "", accordion_style = "btn-active-light-primary";
	List<DKData> dkDataList = (List<DKData>) request.getAttribute("dkDataList");
	boolean dkScoreExists = !dkDataList.isEmpty();
	Map<String, DKData> dkDataMap = dkDataList.stream()
			.collect(Collectors.toMap(DKData::getAppid, data -> data));

%>
<div class="timeline-item">
	<!--begin::Timeline line-->
	<div class="timeline-line w-40px"></div>
	<!--end::Timeline line-->
	<!--begin::Timeline icon-->
	<div class="timeline-icon symbol symbol-circle symbol-40px">
		<div class="symbol-label">
			<i class="ki-duotone ki-pointers fs-2 ">
				<span class="path1"></span>
				<span class="path2"></span>
				<span class="path3"></span>
			</i>
		</div>
	</div>
	<!--end::Timeline icon-->
	<!--begin::Timeline content-->
	<div class="timeline-content mt-n1">
		<!--begin::Timeline heading-->
		<div class="pe-3">
			<!--begin::Title-->
			<div class="fs-5  fw-semibold mb-2">Step 3</div>
			<!--end::Title-->
			<!--begin::Description-->
			<div class="" id="timeline3">
				<div class="d-flex flex-stack border rounded px-7 py-3">
					<div class="w-100">
						<div class="accordion-header py-3 d-flex collapsed" data-bs-toggle="collapse" id="dkScoreDetails" data-bs-target="#dkScoreContent">


							<label class="btn btn-outline btn-outline-dashed <%=accordion_style%> <%=showFlg%> p-7 d-flex align-items-center mb-2" for="dkScoreContent">
								<i class="ki-duotone ki-finance-calculator fs-4x me-4">
									<span class="path1"></span>
									<span class="path2"></span>
									<span class="path3"></span>
									<span class="path4"></span>
									<span class="path5"></span>
									<span class="path6"></span>
									<span class="path7"></span>
								</i>
								<span class="d-block fw-semibold text-start">
                        <span class="text-gray-900 fw-bold d-block fs-3">RACE Score</span>
                        <span class="text-muted fw-semibold fs-6">
                          Run the RACE Score details
                        </span>
                    </span>
							</label>
							<%--		<span class="accordion-icon">--%>
							<%--            <i class="ki-duotone ki-plus-square fs-3 accordion-icon-off"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>--%>
							<%--            <i class="ki-duotone ki-minus-square fs-3 accordion-icon-on"><span class="path1"></span><span class="path2"></span></i>--%>
							<%--            </span>--%>
							<%--		<h4 class="text-gray-700 fw-bold cursor-pointer mb-0 ms-4">BRE Details</h4>--%>
						</div>
						<div id="dkScoreContent" class="fs-6 collapse ps-10 <%=showFlg%>" data-bs-parent="#vl_checker_int">

							<div class="row">
								<div class="col-sm-12">
									<% if (!dkScoreExists) { %>
            <div class="text-center py-5">
                <button id="kt_button_runDKScore" type="button" class="btn btn-sm btn-primary breFetch">

                    <span class="indicator-label">Fetch RACE Score</span>
                                                <span class="indicator-progress">
Please wait... <span class="spinner-border spinner-border-sm align-middle ms-2"></span>
</span>
                    </button>
            </div>
        <% } else { %>
            <div class="row">
                <div class="col-sm-12">
                    <div class="mb-3 mt-3">
                        <table class="table table-sm align-middle table-row-dashed table-row-gray-400 fs-8 gy-3">
                            <thead>
                                <tr class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">
                                    <th>Applicant Type</th>
                                    <th>Applicant Name</th>
                                    <th>Run Date</th>
                                    <th>Bureau Score</th>
                                    <th>Race Score</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                for (VehicleLoanApplicant applicant : vehicleLoanMaster.getApplicants()) {
                                    if ("N".equals(applicant.getDelFlg())) {
                                        String applicantId = String.valueOf(applicant.getApplicantId());
                                        String applicantType = "A".equals(applicant.getApplicantType()) ? "Applicant" :
                                                               "C".equals(applicant.getApplicantType()) ? "Co-Applicant" :
                                                               "G".equals(applicant.getApplicantType()) ? "Guarantor" : "";
                                        DKData dkData = dkDataMap.get(applicantId);
                                %>
                                <tr class="table-row-gray-400">
                                    <td><%= applicantType %></td>
                                    <td class="text-gray-800 text-hover-primary fs-7 fw-bold"><%= applicant.getApplName() %></td>
                                    <td><%= dkData != null ? dateFormat.format(dkData.getCmdate()) : "Not Available" %></td>
                                    <td><%= dkData != null ? dkData.getScoreValue() : "Not Available" %></td>
                                    <td><%= dkData != null ? dkData.getRaceScoreValue() : "Not Available" %></td>
                                    <td>
                                        <% if (dkData != null && "Y".equals(dkData.getSuccessFlg())) { %>
                                            <span class="badge badge-light-success">Success</span>
                                        <% } else if (dkData != null) { %>
                                            <span class="badge badge-light-danger">Failed</span>
                                        <% } else { %>
                                            <span class="badge badge-light-warning">Not Performed</span>
                                        <% } %>
                                    </td>
                                </tr>
                                <%
                                    }
                                }
                                %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        <% } %>


								</div>
							</div>


							<div class="text-end">
								<%if (checker) {%>

								<%}%>
							</div>
						</div>
					</div>

				</div>
			</div>
			<!--end::Description-->
		</div>
		<!--end::Timeline heading-->
	</div>
	<!--end::Timeline content-->
</div>

<!--end::Timeline item-->






