<%@ page import="com.sib.ibanklosucl.model.integrations.VehicleLoanBREDetails" %>
<%@ page import="java.awt.*" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ page import="com.sib.ibanklosucl.model.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.List" %><%--
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
    List<Map<String, Object>> dkScoreData = (List<Map<String, Object>>) request.getAttribute("dkScoreData");
	    boolean dkScoreExists = dkScoreData  != null && !dkScoreData .isEmpty();
	Map<String, DKData> dkDataMap = new HashMap<>();//
	if(dkScoreExists) {
       showFlg = "show";
       accordion_style = "btn-active-light-success";
	}


%>
				<div class="flex-stack border rounded px-7 py-3 mb-2">
					<div class="">
						<div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="dkScoreDetails" data-bs-target="#dkScoreContent">


							<label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2  " for="dkScoreContent">
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
                          See RACE Score details
                        </span>
                    </span>
							</label>
							<%--		<span class="accordion-icon">--%>
							<%--            <i class="ki-duotone ki-plus-square fs-3 accordion-icon-off"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>--%>
							<%--            <i class="ki-duotone ki-minus-square fs-3 accordion-icon-on"><span class="path1"></span><span class="path2"></span></i>--%>
							<%--            </span>--%>
							<%--		<h4 class="text-gray-700 fw-bold cursor-pointer mb-0 ms-4">BRE Details</h4>--%>
						</div>
						<div id="dkScoreContent" class="fs-6 collapse ps-10 " data-bs-parent="#vl_checker_int">

							<div class="row">
								<div class="col-sm-12">
									<div id="dkresponsedata">
                        <%
                        if (dkScoreData != null && !dkScoreData.isEmpty()) {
                        %>
                            <table id="dkScoreTable" class="table table-sm align-middle table-row-dashed table-row-gray-400 fs-8 gy-3">
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
                                    for (Map<String, Object> item : dkScoreData) {
                                        if(item!=null) {
                                    %>
                                        <tr class="table-row-gray-400">
                                            <td><%= item.get("applicantType") %></td>
                                            <td class="text-gray-800 text-hover-primary fs-7 fw-bold"><%= item.get("applicantName") %></td>
                                            <td>
                                                <%= item.get("runDate") != null ? dateFormat.format((Date)item.get("runDate")) : "N/A" %>
                                            </td>
                                            <td><%= item.get("bureauScore") != null ? item.get("bureauScore") : "N/A" %></td>
                                            <td><%= item.get("raceScore") != null ? item.get("raceScore") : "N/A" %></td>
                                            <td>
	                                            <%if(item.get("color")!=null) {%>
	                                            <%if("red".equals(item.get("color"))) {%>
	                                            <span class="badge-light-danger">
                                                    DK score failed
                                                </span>
	                                            <%} else if ("amber".equals(item.get("color"))) {%>
	                                            <span class="badge-light-warning">
                                                    Success
                                                </span>
	                                            <%} else if ("green".equals(item.get("color"))) {%>
	                                             <span class="badge-light-success">
                                                    Success
                                                </span>
	                                            <%} else {%>
                                                <span class="badge <%= "Success".equals(item.get("status")) ? "badge-light-success" : "badge-light-warning" %>">
                                                    <%= item.get("status") %>
                                                </span>
	                                            <%}}%>
                                            </td>
                                        </tr>
                                    <%
                                    } else {
                                    %> <tr><td colspan="6">Run Race Score not found</td></tr>
	                                <%
                                    }
	                                    }
                                    %>
                                </tbody>
                            </table>
                        <%
                        }
                        else{
                            out.println("<div class='alert'>Race Score Not obtained</div>");
                        }
                        %>
                    </div>
									<div class="text-center py-5">

                    </div>



								</div>
							</div>



						</div>
					</div>

				</div>






