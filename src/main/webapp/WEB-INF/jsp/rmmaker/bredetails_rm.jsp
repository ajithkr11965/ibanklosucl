<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.model.integrations.VehicleLoanBREDetails" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanAmber" %>
<%@ page import="java.awt.*" %><%--
  Created by IntelliJ IDEA.
  User: SIBL12134
  Date: 02-07-2024
  Time: 18:25
  To change this template use File | Settings | File Templates.
--%>
<%
	VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
	Boolean checker = request.getAttribute("checker") != null ? request.getAttribute("checker").toString().equals("Y") : false;
	List<VehicleLoanBREDetails> vehicleLoanBREDetailsList = new ArrayList<>();
	VehicleLoanBREDetails vehicleLoanbre = null;
	String eligibilityFlag = "", breFlag = "", breContent = "", showFlg = "", accordion_style = "btn-active-light-primary";
	if (vehicleLoanMaster != null) {
		vehicleLoanBREDetailsList = vehicleLoanMaster.getVehicleLoanBREDetailsList();
		if (vehicleLoanBREDetailsList != null && !vehicleLoanBREDetailsList.isEmpty()) {
			for (VehicleLoanBREDetails loanBREDetails : vehicleLoanBREDetailsList) {
				if (loanBREDetails.getDelFlg().equals("N")) {
					vehicleLoanbre = loanBREDetails;
					showFlg = "show";
					accordion_style = "btn-active-light-success";
				}
			}
			eligibilityFlag = vehicleLoanbre.getEligibilityFlag() != null ? vehicleLoanbre.getEligibilityFlag() : "NA";
			breFlag = vehicleLoanbre.getBreFlag() != null ? vehicleLoanbre.getBreFlag() : "NA";
		}
	}
%>
<div class="kt">
	<div class="d-flex flex-stack border rounded px-7 py-3 mb-2">
		<div class="">
			<div class="accordion-header d-flex collapsed" data-bs-toggle="collapse" id="brelistDetails" data-bs-target="#bretable">

				<label class="btn btn-outline btn-outline-dashed p-7 btn-active-light-primary acdlen justify-content-start d-flex align-items-center mb-2" for="bretable">
					<i class="ki-duotone ki-finance-calculator fs-3x me-4">
						<span class="path1"></span>
						<span class="path2"></span>
						<span class="path3"></span>
						<span class="path4"></span>
						<span class="path5"></span>
						<span class="path6"></span>
						<span class="path7"></span>
					</i>
					<span class="d-block fw-semibold text-start">
                        <span class="text-gray-900 fw-bold d-block fs-4">BRE Details</span>
                        <span class="text-muted fw-semibold fs-7">
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
			<div id="bretable" class="fs-6 collapse ps-10" data-bs-parent="#vl_checker_int">

				<div class="row">
					<div class="col-sm-12">
						<%
							if (eligibilityFlag != null) {
								if ("green".equals(eligibilityFlag)) {
									if ("green".equals(breFlag)) {
						%>
						<div class="alert alert-dismissible bg-light-success d-flex flex-center flex-column py-10 px-10 px-lg-20 mb-10">
							<i class="ki-duotone ki-information-5 fs-5tx text-danger mb-5"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
							<div class="text-center">
								<h1 class="fw-bold mb-5"> Check Result: Green</h1>
							</div>
						</div>


						<% } else if ("amber".equals(breFlag)) {%>
						<div class="alert alert-dismissible bg-warning d-flex flex-center flex-column py-10 px-10 px-lg-20 mb-10">
							<i class="ki-duotone ki-shield-tick fs-2hx text-warning me-4"><span class="path1"></span><span class="path2"></span></i>
							<div class="text-center">
								<h1 class="fw-bold mb-5">BRE Check Result: Amber</h1>
							</div>
						</div>
						<%
							}
						} else if ("red".equals(eligibilityFlag)) {
						%>
						<div class="alert alert-dismissible bg-light-danger d-flex flex-center flex-column py-10 px-10 px-lg-20 mb-10">
							<i class="ki-duotone ki-shield-tick fs-2hx text-danger me-4"><span class="path1"></span><span class="path2"></span></i>
							<div class="text-center">
								<h1 class="fw-bold mb-5">Eligiblity Failed</h1>
							</div>
						</div>

						<%
								}
							}
						%>
						<div id="bre-response"></div>
					</div>
				</div>


				<div class="text-end">
					<%if (checker) {%>
					<button type="button" class="btn btn-sm btn-primary breFetch">
						<%= vehicleLoanbre != null ? "Re-run BRE" : "Run BRE" %>
					</button>
					<%}%>
				</div>
			</div>
		</div>

	</div>
</div>






