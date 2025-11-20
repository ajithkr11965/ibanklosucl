<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.model.integrations.VehicleLoanBREDetails" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanAmber" %>
<%@ page import="java.awt.*" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanAmberSub" %><%--
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
	List <VehicleLoanBREDetails> vehicleLoanBREDetailsList = new ArrayList<>();
	VehicleLoanBREDetails vehicleLoanbre =null;
    String breresponse="";
    String itemColor="";
    String eligibilityFlag="",breFlag="",breContent="",showFlg="",accordion_style="btn-active-light-primary";
    if (vehicleLoanMaster != null) {
        vehicleLoanBREDetailsList = vehicleLoanMaster.getVehicleLoanBREDetailsList();
        if(vehicleLoanBREDetailsList!=null && !vehicleLoanBREDetailsList.isEmpty()) {
	        for (VehicleLoanBREDetails loanBREDetails : vehicleLoanBREDetailsList) {
		        if (loanBREDetails.getDelFlg().equals("N")) {
			        vehicleLoanbre = loanBREDetails;
                    showFlg="show";
                    accordion_style="btn-active-light-success";
		        }
	        }
	        eligibilityFlag = vehicleLoanbre.getEligibilityFlag() != null ? vehicleLoanbre.getEligibilityFlag() : "NA";
            breresponse= vehicleLoanbre.getBreData()!= null ? vehicleLoanbre.getBreData() : "NA";
	        breFlag = vehicleLoanbre.getBreFlag() != null ? vehicleLoanbre.getBreFlag() : "NA";
        }
    }
     String overallColor = "success";
     String overallDesc = "Green";

    for (VehicleLoanAmber amber : vehicleLoanAmberList) {
        if ("amber".equals(amber.getColour()) && "success".equals(overallColor)) {
            overallColor = "warning";
            overallDesc="Amber";
        }
        if ("red".equals(amber.getColour())) {
            overallColor = "danger";
            overallDesc="Red";
            break;
        }
    }
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
			<div class="fs-5  fw-semibold mb-2">Step 4</div>
			<!--end::Title-->
			<!--begin::Description-->
			<div class="" id="timeline3">
				<div class="d-flex flex-stack border rounded px-7 py-3">
					<div class="w-100">
						<div class="accordion-header py-3 d-flex collapsed" data-bs-toggle="collapse" id="brelistDetails" data-bs-target="#bretable">


							<label class="btn btn-outline btn-outline-dashed <%=accordion_style%> <%=showFlg%> p-7 d-flex align-items-center mb-2" for="bretable">
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
                        <span class="text-gray-900 fw-bold d-block fs-3">BRE Details</span>
                        <span class="text-muted fw-semibold fs-6">
                          Run the Business Rule engine to get the eligibility
                        </span>
                    </span>
							</label>
							<%--		<span class="accordion-icon">--%>
							<%--            <i class="ki-duotone ki-plus-square fs-3 accordion-icon-off"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>--%>
							<%--            <i class="ki-duotone ki-minus-square fs-3 accordion-icon-on"><span class="path1"></span><span class="path2"></span></i>--%>
							<%--            </span>--%>
							<%--		<h4 class="text-gray-700 fw-bold cursor-pointer mb-0 ms-4">BRE Details</h4>--%>
						</div>
						<div id="bretable" class="fs-6 collapse ps-10 <%=showFlg%>" data-bs-parent="#vl_checker_int">
							<input type="hidden" name="vhidbreVal" id="vhidbreVal" value="<%=breresponse%>"/>

							<div class="row">
								<div class="col-sm-12">
									<div id="bre-response">
									<div class="bg-light bg-opacity-50 rounded-3 p-10 mx-md-5 h-md-100">
    <div class="d-flex flex-center w-60px h-60px rounded-3 bg-light-<%= overallColor %> bg-opacity-90 mb-10">
        <i class="ki-duotone ki-abstract-25 text-<%= overallColor %> fs-3x"><span class="path1"></span><span class="path2"></span></i>
    </div>
    <h2 class="mb-2"><span><p class="btn btn-lg btn-flex btn-link btn-color-<%= overallColor %>"><%= overallDesc.toUpperCase() %></p></span></h2>

    <% for (VehicleLoanAmber amber : vehicleLoanAmberList) {
        if( "green".equals(amber.getColour())) {
            itemColor="success";
        } else if ( "amber".equals(amber.getColour())) {
            itemColor="warning";
        } else{
            itemColor="danger";
        }

    %>
        <div class="card mb-5">
            <div class="card-header">
                <h3 class="card-title"><%= amber.getAmberCode() %>: <%= amber.getAmberDesc() %></h3>
                <div class="card-toolbar">
                    <span class="badge badge-<%= itemColor %>"><%= amber.getColour().toUpperCase() %></span>
                </div>
            </div>
            <div class="card-body">
                <% if (amber.getAmberSubList() != null && !amber.getAmberSubList().isEmpty()) { %>
                    <% for (VehicleLoanAmberSub sub : amber.getAmberSubList()) { %>
	            <%if ("danger".equals(overallColor)) {%>
                        <p><strong><li><%= sub.getAmberDesc()!=null?sub.getAmberDesc():"" %></strong></li> </p>
		            <%}%>
                        <div class="mb-3">
                            <p>Applicant: <%= sub.getApplicantName()!=null?sub.getApplicantName():"" %></p>
                            <p>Current Value: <%= sub.getCurrentValue()!=null?sub.getCurrentValue():"" %></p>
                            <p>Allowed Range/Value: <%= sub.getMasterValue()!=null?sub.getMasterValue():"" %></p>
                        </div>
                    <% } %>
                <% } else { %>
                    <p>This is a generic rule.</p>
                <% } %>
            </div>
        </div>
    <% } %>
</div>
											</div>
								</div>
							</div>


							<div class="text-end">
								<%if (checker) {%>
								<button id="kt_button_breDetails" type="button" class="btn btn-sm btn-primary breFetch">

									<span class="indicator-label"><%= vehicleLoanbre != null ? "Re-run BRE" : "Run BRE" %></span>
																<span class="indicator-progress">
        Please wait... <span class="spinner-border spinner-border-sm align-middle ms-2"></span>
    </span>
									</button>
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






