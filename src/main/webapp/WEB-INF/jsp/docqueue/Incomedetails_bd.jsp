<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.sib.ibanklosucl.model.*" %>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page import="com.fasterxml.jackson.core.type.TypeReference" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="java.math.RoundingMode" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.AllotmentDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.program.bsaBankDetails" %>
<%@ page import="java.util.stream.Collectors" %>

<%--
  Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 10-05-2024
  Time: 11:23
  To change this template use File | Settings | File Templates.
--%>
<%
	String datamode = (String) request.getAttribute("datamod");
	String apptype = (String) request.getAttribute("apptype");

	boolean completed = false, mod = false;
	String panNo = "", ocrPanNumber = "", incomemobileNo = "", incomemobCountryCode = "", incomeDOB = "";
	String appid = "", applicantype = "", residentialStatus = "";
	String applicantId = "", wiNum = "", slno = "", docType = "";
	String hidincomeConsidered = "", hidProgramCode = "", itrFlg = "", form16Flg = "", monthlyGrossIncome = "", abbIncome = "", avgSal = "", totalFDBalance = "", salaryIncome = "";


	VehicleLoanApplicant applicant = null;
	VehicleLoanBasic vehicleLoanBasic = null;
	VehicleLoanProgram vehicleLoanProgram = null;
	Optional<VehicleLoanBSA> vehicleLoanBSA = null;
	List<VehicleLoanBSA> vehicleLoanBSAList = new ArrayList<>();
	List<VehicleLoanITR> vehicleLoanITRList = new ArrayList<>();
	List<VehicleLoanFD> vehicleLoanFDList = new ArrayList<>();
	List<VehicleLoanProgramNRI> vehicleLoanProgramNRIList = new ArrayList<>();
	List<VehicleLoanProgramSalary> vehicleLoanProgramSalaryList = new ArrayList<>();
	String pattern = "yyyy-MM-dd";
	String cifId = "", sibCustomer = "", residentFlg = "";
	SimpleDateFormat formatter = new SimpleDateFormat(pattern);
	String fileclass = "file-input*";
	String panimg = "", panext = "";
	String startDate = "", endDate = "", bankId = "";
	BigDecimal totalavailBalance = BigDecimal.ZERO;
	if (request.getAttribute("init") != null)
		fileclass = "file-input";
	if (request.getAttribute("general") != null) {
		applicant = (VehicleLoanApplicant) request.getAttribute("general");
		if (applicant != null) {
			vehicleLoanBasic = applicant.getBasicapplicants();
		}
	} else {
	}

	if (applicant != null) {
		completed = "Y".equals(applicant.getIncomeComplete());
		applicantId = String.valueOf(applicant.getApplicantId());
		fileclass = "file-input";
		wiNum = applicant.getWiNum();
		slno = String.valueOf(applicant.getSlno());
		panNo = (applicant.getKycapplicants() != null) ? StringUtils.defaultString(applicant.getKycapplicants().getPanNo()) : "";
		appid = String.valueOf(applicant.getApplicantId());
		applicantype = String.valueOf(applicant.getApplicantType());
		if (applicant.getKycapplicants() != null && applicant.getKycapplicants().getPanDob() != null) {
			incomeDOB = formatter.format(applicant.getKycapplicants().getPanDob());
		}

		if (applicant.getCifId() != null) {
			cifId = applicant.getCifId();
		}
		if (applicant.getSibCustomer() != null) {
			sibCustomer = applicant.getSibCustomer();
		}
		if (applicant.getResidentFlg() != null) {
			residentialStatus = applicant.getResidentFlg();
		}
		vehicleLoanProgram = applicant.getVlProgram();

		if (vehicleLoanProgram != null) {
			itrFlg = StringUtils.defaultString(vehicleLoanProgram.getItrFlg(), "X");
			form16Flg = StringUtils.defaultString(vehicleLoanProgram.getForm16Flg(), "X");
			docType = StringUtils.defaultString(vehicleLoanProgram.getDoctype(), "");
			datamode = "MODIFY";
			if (vehicleLoanProgram.getIncomeConsidered() != null) {
				hidincomeConsidered = vehicleLoanProgram.getIncomeConsidered();
			}
			if (vehicleLoanProgram.getLoanProgram() != null) {
				hidProgramCode = vehicleLoanProgram.getLoanProgram();
				if ("LOANFD".equals(hidProgramCode)) {
					vehicleLoanFDList = vehicleLoanProgram.getVehicleLoanFDList().stream()
							.filter(fd -> "N".equals(fd.getDelFlg()))
							.toList();
				} else if ("INCOME".equals(vehicleLoanProgram.getLoanProgram())) {
					if (("R".equals(vehicleLoanProgram.getResidentType())) && "Y".equals(itrFlg)) {
						vehicleLoanITRList = vehicleLoanProgram.getVehicleLoanITRList().stream()
								.filter(itr -> "N".equals(itr.getDelFlg()))
								.toList();
						if (vehicleLoanProgram.getAvgSal() != null) {
							avgSal = vehicleLoanProgram.getAvgSal().toPlainString();
						}
					} else if (("R".equals(vehicleLoanProgram.getResidentType())) && "N".equals(itrFlg)) {
						vehicleLoanProgramSalaryList = vehicleLoanProgram.getVehicleLoanProgramSalaryList().stream()
								.filter(salary -> "N".equals(salary.getDelFlg()))
								.toList();
						if (vehicleLoanProgram.getNriNetSalary() != null && "MONTHLY".equals(vehicleLoanProgram.getDoctype())) {
							avgSal = vehicleLoanProgram.getNriNetSalary().toPlainString();
						} else if (vehicleLoanProgram.getAvgSal() != null && "OVERSEASABB".equals(vehicleLoanProgram.getDoctype())) {
							avgSal = vehicleLoanProgram.getAvgSal().toPlainString();
						}
					} else {
						vehicleLoanProgramNRIList = vehicleLoanProgram.getVehicleLoanProgramNRIList().stream()
								.filter(nri -> "N".equals(nri.getDelFlg()))
								.toList();
						if (vehicleLoanProgram.getAvgSal() != null) {
							avgSal = vehicleLoanProgram.getAvgSal().toPlainString();
						}
					}
				} else if ("SURROGATE".equals(vehicleLoanProgram.getLoanProgram())) {
					vehicleLoanBSAList = vehicleLoanProgram.getVehicleLoanBSAList();
					vehicleLoanBSA = vehicleLoanBSAList.stream().filter(bsa -> "N".equals(bsa.getDelFlg())).findFirst();
					if (vehicleLoanBSA.isPresent()) {
						startDate = vehicleLoanBSA.get().getStartDate();
						endDate = vehicleLoanBSA.get().getEndDate();
						bankId = vehicleLoanBSA.get().getBank();
					}
				}
			}


		}
	}
	if (vehicleLoanBasic != null) {
		incomemobileNo = vehicleLoanBasic.getMobileNo();
		incomemobCountryCode = vehicleLoanBasic.getMobileCntryCode();
	}

%>
<div class="program-itr">
	<form class="det form-details  Incomedetails" data-code="<%=apptype%>-5" data-completed="<%=completed%>" action="#" data-page-mode="<%=datamode%>">

		<input type="hidden" name="applicantId" class="applicantId" value="<%=applicantId%>">
		<input type="hidden" name="applicantype" class="applicantype" value="<%=applicantype%>">
		<input type="hidden" name="residentialStatus" class="residentialStatus" value="<%=residentialStatus%>">
		<input type="hidden" name="sibCustomer" class="sibCustomer" value="<%=sibCustomer%>">
		<input type="hidden" name="cifId" class="cifId" value="<%=cifId%>">
		<input type="hidden" name="wiNum" class="wiNum" value="<%=wiNum%>">
		<input type="hidden" name="slno" class="slno" value="<%=slno%>">
		<input type="hidden" name="entry_user" class="entry_user" value="">
		<input type="hidden" name="hidincomeConsidered" class="hidincomeConsidered" value="<%=hidincomeConsidered%>">
		<input type="hidden" name="hidProgramCode" class="hidProgramCode" value="<%=hidProgramCode%>">
		<input type="hidden" name="itrFlg" class="itrFlg" value="<%=itrFlg%>">
		<input type="hidden" name="form16Flg" class="form16Flg" value="<%=form16Flg%>">
		<input type="hidden" name="hidStartDate" class="hidStartDate" value="<%=startDate%>">
		<input type="hidden" name="hidEndDate" class="hidEndDate" value="<%=endDate%>">
		<input type="hidden" name="hidInstituition" class="hidInstituition" value="<%=bankId%>">
		<%--    <label class="form-check form-check-inline">--%>
		<%--                      <input type="radio" class="form-check-input uidmode" name="uidmode" value="M" required="" checked="" disabled="">--%>
		<%--                      <span class="form-check-label">Manual</span>--%>
		<%--                  </label>--%>
		<div class="row  mb-2">
			<label class="col-form-label col-lg-5">Whether income should be considered? <span class="text-danger">*</span></label>
			<div class="col-lg-7">
				<div class="form-check-horizontal">
					<label class="form-check form-check-inline">
						<% if("Y".equals(hidincomeConsidered)) {%>
							<span class='form-check-label fw-semibold'>Yes</span>
							<%} else {%>
							<span class="form-check-label fw-semibold">No</span>
							<%}%>
					</label>
				</div>
			</div>
		</div>

		<div class="program row mb-2" style=" <%= "Y".equals(hidincomeConsidered) ? "" : "display: none" %>">
			<label class="col-form-label col-lg-5">Program </label>
			<div class="col-lg-5">
				<span class="form-check-label fw-semibold"><%=hidProgramCode%></span>
			</div>
		</div>

		<br>

		<div class="incomediv" name="incomediv" style="<%= "INCOME".equals(hidProgramCode) ? "" : "display: none" %>">
			<label class="col-form-label col-lg-5"><strong>Income program </strong></label>

			<div class="row  mb-2">
				<label class="col-form-label col-lg-5">Whether Form 16 available or not? <span class="text-danger">*</span></label>
				<div class="col-lg-7">
					<div class="form-check-horizontal">
						<label class="form-check form-check-inline">
							<% if("Y".equals(form16Flg)) {%>
							<span class='form-check-label'>Yes</span>
							<%} else {%>
							<span class="form-check-label">No</span>
							<%}%>
						</label>



					</div>
				</div>
			</div>

			<div class="residentstatus" name="residentstatus" style="<%="R".equals(residentialStatus)?"":"display:none"%>">
				<label class="col-form-label col-lg-5"><strong>Resident</strong> </label>
				<div class="row  mb-2">
					<label class="col-form-label col-lg-5">Whether ITR available or not? <span class="text-danger">*</span></label>
					<div class="col-lg-7">
						<div class="form-check-horizontal">
							<label class="form-check form-check-inline">
								<% if("Y".equals(itrFlg)) {%>
							<span class='form-check-label'>Yes</span>
							<%} else {%>
							<span class="form-check-label">No</span>
							<%}%>
							</label>
						</div>
					</div>
				</div>

				<div class="itrbutton row  mb-2" style="<%= ("R".equals(residentialStatus) && "Y".equals(itrFlg)) ? "" : "display: none" %>">


					<div class="row mb-2">
						<label class="col-lg-5 col-form-label">PAN</label>
						<div class="col-lg-7">
							<%=panNo%>
						</div>
					</div>
					<div class="row mb-2">
						<label class="col-lg-5 col-form-label">DOB</label>
						<div class="col-lg-7">
							<%=incomeDOB%>
						</div>
					</div>
					<div class="row mb-2">
						<label class="col-lg-5 col-form-label">Mobile Number</label>
						<div class="col-lg-7">
							<%=incomemobCountryCode%><%=incomemobileNo%>
						</div>
					</div>
					<div class="col-lg-12 " style="justify-content: center;display: flex;">

					</div>

					<div class="row mb-2 itrMonthlyGrossDiv" style="<%= !vehicleLoanITRList.isEmpty() ? "" : "display: none" %>">
						<div class="col-lg-2"></div>
						<div class="col-lg-8">
							<div class="itrResponse m-2">
								<%
									if (vehicleLoanITRList != null && !vehicleLoanITRList.isEmpty()) {
										String tableHtml = "";
										tableHtml += "<table class='table table-border-dashed'>";
										tableHtml += "<thead><tr><th>FY</th><th>Gross Total Income</th><th>Total Income</th><th>PAN</th><th>Name</th><th>Form No</th></tr></thead>";
										tableHtml += "<tbody>";
										for (VehicleLoanITR itr : vehicleLoanITRList) {
											Map<String, Object> responseMap = null;
											if ("N".equals(itr.getDelFlg())) {
												responseMap = new ObjectMapper().readValue(itr.getFetchResponse(), new TypeReference<Map<String, Object>>() {
												});
											}
											if (responseMap != null) {
												Map<String, Object> itrDataMap = (Map<String, Object>) responseMap.get("itrData");
												List<Map<String, Object>> itrDataList = (List<Map<String, Object>>) itrDataMap.get("itrData");
												monthlyGrossIncome = (String) itrDataMap.get("monthlyGrossIncome");
												for (Map<String, Object> itrItem : itrDataList) {
													tableHtml += "<tr>";
													tableHtml += "<td>" + itrItem.get("fy") + "</td>";
													tableHtml += "<td>" + itrItem.get("grossTotalIncome") + "</td>";
													tableHtml += "<td>" + itrItem.get("totalIncome") + "</td>";
													tableHtml += "<td>" + itrItem.get("pan") + "</td>";
													tableHtml += "<td>" + itrItem.get("name") + "</td>";
													tableHtml += "<td>" + itrItem.get("formNo") + "</td>";
													tableHtml += "</tr>";
												}
											}
										}
										tableHtml += "</tbody>";
										tableHtml += "</table>";
										out.println(tableHtml);

										// Display Monthly Gross Income

									}
								%>
							</div>
							<div class="col-lg-2"></div>
						</div>
						<label class="col-lg-5 col-form-label">Monthly Gross Income</label>
						<div class="col-lg-5">
							<input type="text" name="itrMonthlyGross" id="itrMonthlyGross" readonly class="form-control itr-monthly-gross" value="<%=monthlyGrossIncome%>"
							       value="">
						</div>
					</div>

				</div>


				<div class="row mb-2 form16_div" style="<%= "Y".equals(form16Flg) ? "" : "display: none" %>">
					<label class="col-form-label col-lg-5">Upload Form 16 <span class="text-danger">*</span></label>
					<div class="col-lg-">
						<input type="file" class="form-control form16_upd base64file" name='form16_upd'>
					</div>
				</div>


				<div class="salaryupd row  mb-2" style="<%= ("R".equals(residentialStatus) && "N".equals(itrFlg)) ? "" : "display: none" %>">
					<div class="border-bottom pb-2 mb-2">
						<span class="fw-bold">Upload Salary Slip </span>
					</div>

					<div class="row mb-2 ">
						<% if (vehicleLoanProgramSalaryList != null && !vehicleLoanProgramSalaryList.isEmpty()) { %>
						<table class="table  table-bordered salarytable">
							<thead class="text-nowrap">
								<tr>
									<th>Month</th>
									<th>FILE</th>
									<th style="width: 0%!important;">
									</th>

								</tr>
							</thead>
							<tbody class="salarytable_body">
								<% for (VehicleLoanProgramSalary vehicleLoanProgramSalary : vehicleLoanProgramSalaryList) {
								%>
								<tr>
									<td>
										<select name="sal_month" id="sal_month" class="form-control select sal_month">
											<option value="<%= vehicleLoanProgramSalary.getSalMonth() %>"
											        selected><%= new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"}[vehicleLoanProgramSalary.getSalMonth() - 1] %>
											</option>
											<option value="1">January</option>
											<option value="2">February</option>
											<option value="3">March</option>
											<option value="4">April</option>
											<option value="5">May</option>
											<option value="6">June</option>
											<option value="7">July</option>
											<option value="8">August</option>
											<option value="9">September</option>
											<option value="10">October</option>
											<option value="11">November</option>
											<option value="12">December</option>
										</select>
									</td>
									<td>
										<% if (vehicleLoanProgramSalary.getSalaryDoc() != null) { %>
										<input type="file" class="form-control salaryfile base64file" accept=".pdf,.jpg,.jpeg,.png" data-max-size="2097152"
										       name="salaryfile<%= vehicleLoanProgramSalary.getSalMonth() %>" disabled>
										<span class="text-success">File uploaded</span>
										<% } else { %>
										<input type="file" class="form-control salaryfile base64file" name="salaryfile">
										<% } %>
									</td>
									<td>

									</td>
								</tr>
								<%}%>
							</tbody>
						</table>
						<% } else { %>
						<table class="table table-bordered salarytable">
							<thead class="text-nowrap">
								<tr>
									<th>Month</th>
									<th>FILE</th>
									<th style="width: 0%!important;">

									</th>
								</tr>
							</thead>
							<tbody class="salarytable_body">
								<tr>
									<td><select name="sal_month" id="sal_month" class="form-control  sal_month">
										<option value="" selected>Select Month</option>
										<option value="1">January</option>
										<option value="2">February</option>
										<option value="3">March</option>
										<option value="4">April</option>
										<option value="5">May</option>
										<option value="6">June</option>
										<option value="7">July</option>
										<option value="8">August</option>
										<option value="9">September</option>
										<option value="10">October</option>
										<option value="11">November</option>
										<option value="12">December</option>
									</select></td>
									<td><input type="file" class="form-control salaryfile base64file" name='salaryfile'></td>
									<td>

									</td>

								</tr>
							</tbody>

						</table>
						<% } %>
					</div>
					<br>
					<div class="row mb-2">
						<label class="col-form-label col-lg-5">Average Monthly Income <span class="text-danger">*</span></label>
						<div class="col-lg-7">
							<input type="text" name="AvgIncome" id="AvgIncome" class="form-control AvgIncome" placeholder="Average Monthly Income" value="<%=avgSal%>">
						</div>
					</div>

				</div>
			</div>

			<div class="nristatus" name="nristatus" style="<%= "N".equals(residentialStatus) ? "display:block" : "display: none" %>">
				<label class="col-form-label col-lg-5"><strong>NRI</strong> </label>


				<div class="row mb-2 form16_div" style="<%= "Y".equals(form16Flg) ? "" : "display: none" %>">
					<label class="col-form-label col-lg-5">Upload Form 16 <span class="text-danger">*</span></label>
					<div class="col-lg-">
						<input type="file" class="form-control form16_upd base64file" name='form16_upd'>
					</div>
				</div>


				<div class="row  mb-2">
					<label class="col-form-label col-lg-5">Whether Gross Icome or ABB? <span class="text-danger">*</span></label>
					<div class="col-lg-7">
						<div class="form-check-horizontal">
							<label class="form-check form-check-inline">
								<input type="radio" class="form-check-input monthlyorabb" <%= "MONTHLY".equals(docType) ? "checked" : "" %> name="monthlyorabb"
								       value="MonthSalary">
								<span class="form-check-label">Monthly Gross Income</span>
							</label>
							<label class="form-check form-check-inline">
								<input type="radio" class="form-check-input monthlyorabb" <%= "OVERSEASABB".equals(docType) ? "checked" : "" %> name="monthlyorabb"
								       value="ABB">
								<span class="form-check-label">ABB as per overseas Statement</span>
							</label>
						</div>
					</div>
				</div>

				<div class="monthsalary" style="<%= "MONTHLY".equals(docType) ? "display:block" : "display: none" %>">

					<div class="row mb-2">
						<label class="col-form-label col-lg-5"> Monthly salary in INR <span class="text-danger">*</span></label>
						<div class="col-lg-5">
							<input type="text" name="MonthSalary" id="MonthSalary" class="form-control MonthSalary" value="<%=avgSal%>" placeholder=" Monthly Salary"
							       onchange="calculateRemittance(this)">
						</div>
					</div>


					<div class="row mb-2 col-10">
						<table class="table  table-bordered MonthSalarytable">
							<thead class="text-nowrap">
								<tr>
									<th>Month</th>
									<th>Total remittance</th>
									<th>Bulk remittance</th>
									<th>Net remittance</th>
								</tr>
							</thead>
							<tbody class="MonthSalarytable_body">
								<%
									SimpleDateFormat sdf = new SimpleDateFormat("MMM-yyyy");
									Calendar cal = Calendar.getInstance();
									BigDecimal totalRemittanceSum = BigDecimal.ZERO;
									BigDecimal bulkRemittanceSum = BigDecimal.ZERO;
									BigDecimal netRemittanceSum = BigDecimal.ZERO;
									int count = 0;

									if (vehicleLoanProgramNRIList != null && !vehicleLoanProgramNRIList.isEmpty()) {
										for (int i = 0; i < vehicleLoanProgramNRIList.size(); i++) {
											VehicleLoanProgramNRI programNRI = vehicleLoanProgramNRIList.get(i);
											cal.set(Calendar.YEAR, programNRI.getRemitYear());
											cal.set(Calendar.MONTH, programNRI.getRemitMonth() - 1); // Assuming month is 1-based in your model
											String monthYear = sdf.format(cal.getTime());
											if (programNRI.getTotRemittance() != null) {
												totalRemittanceSum = totalRemittanceSum.add(BigDecimal.valueOf(programNRI.getTotRemittance()));
												count++;
											}
											if (programNRI.getBulkRemittance() != null) {
												bulkRemittanceSum = bulkRemittanceSum.add(BigDecimal.valueOf(programNRI.getBulkRemittance()));
											}
											if (programNRI.getNetRemittance() != null) {
												netRemittanceSum = netRemittanceSum.add(BigDecimal.valueOf(programNRI.getNetRemittance()));
											}
								%>
								<tr>
									<td><input type="text" class="form-control MonthSalary_mon" name="MonthSalary_mon<%=i%>" id="MonthSalary_mon<%=i%>" value="<%=monthYear%>"
									           readonly/></td>
									<td><input type="text" class="form-control total_remittance" name="total_remittance<%=i%>" id="total_remittance<%=i%>"
									           value="<%=programNRI.getTotRemittance()!=null?programNRI.getTotRemittance():""%>" onchange="calculateRemittance(this)"/></td>
									<td><input type="text" class="form-control bulk_remittance" name="bulk_remittance<%=i%>" id="bulk_remittance<%=i%>"
									           value="<%=programNRI.getBulkRemittance()%>" readonly/></td>
									<td><input type="text" class="form-control net_remittance" name="net_remittance<%=i%>" id="net_remittance<%=i%>"
									           value="<%=programNRI.getNetRemittance()%>" readonly/></td>
								</tr>
								<%
									}
								} else {
									for (int i = 0; i < 12; i++) {
								%>
								<tr>
									<td><input type="text" class="form-control MonthSalary_mon" name="MonthSalary_mon<%=i%>" id="MonthSalary_mon<%=i%>" readonly/></td>
									<td><input type="text" class="form-control total_remittance" name="total_remittance<%=i%>" id="total_remittance<%=i%>"
									           onchange="calculateRemittance(this)"/></td>
									<td><input type="text" class="form-control bulk_remittance" name="bulk_remittance<%=i%>" id="bulk_remittance<%=i%>" readonly/></td>
									<td><input type="text" class="form-control net_remittance" name="net_remittance<%=i%>" id="net_remittance<%=i%>" readonly/></td>
								</tr>
								<%
										}
									}
									BigDecimal avgTotalRemittance = count > 0 ? totalRemittanceSum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
									BigDecimal avgBulkRemittance = count > 0 ? bulkRemittanceSum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
									BigDecimal avgNetRemittance = count > 0 ? netRemittanceSum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
								%>

							</tbody>

						</table>


					</div>

					<div class="row mb-2">
						<label class="col-form-label col-lg-5"> Average Total remittance <span class="text-danger">*</span></label>
						<div class="col-lg-5">
							<input type="text" name="Avgtotal_remittance" id="Avgtotal_remittance" class="form-control Avgtotal_remittance"
							       value="<%= avgTotalRemittance.toPlainString() %>" readonly>
						</div>
					</div>
					<div class="row mb-2">
						<label class="col-form-label col-lg-5">Average Bulk remittance<span class="text-danger">*</span></label>
						<div class="col-lg-5">
							<input type="text" name="Avgbulk_remittance" id="Avgbulk_remittance" class="form-control Avgbulk_remittance"
							       value="<%= avgBulkRemittance.toPlainString() %>" readonly>
						</div>
					</div>
					<div class="row mb-2">
						<label class="col-form-label col-lg-5">Average Net remittance<span class="text-danger">*</span></label>
						<div class="col-lg-5">
							<input type="text" name="Avgnet_remittance" id="Avgnet_remittance" class="form-control Avgnet_remittance"
							       value="<%= avgNetRemittance.toPlainString() %>"
							       readonly>
						</div>
					</div>
				</div>


				<div class="abb_div" style="<%= "OVERSEASABB".equals(docType) ? "display:block" : "display: none" %>">

					<div class="row mb-2">
						<label class="col-form-label col-lg-5">ABB as per overseas Statement<span class="text-danger">*</span></label>
						<div class="col-lg-5">
							<input type="text" name="abb" id="abb" class="form-control abb" placeholder="Enter ABB" value="<%=avgSal%>">
						</div>
					</div>
				</div>


			</div>
		</div>


		<%--		<div class="surrogatediv" name="surrogatediv" style="display: none">--%>
		<%--			<label class="col-form-label col-lg-5"><strong>Surrogate program </strong> </label>--%>


		<%--		</div>--%>

		<div class="surrogatediv" name="surrogatediv" style="<%= "SURROGATE".equals(hidProgramCode) ? "" : "display: none" %>">
			<label class="col-form-label col-lg-5"><strong>Surrogate program </strong></label>
			<div class="row mb-2">
				<label class="col-lg-5 col-form-label">Upload the Bank statements</label>
				<div class="col-lg-7">
					<div class="row mb-2">
						<label class="col-lg-4 col-form-label">From Date</label>
						<div class="col-lg-8">
							<input type="text" name="fromDate" class="form-control fromDate" placeholder="Select From Date" value="<%=startDate%>">
						</div>
					</div>
					<div class="row mb-2">
						<label class="col-lg-4 col-form-label">To Date</label>
						<div class="col-lg-8">
							<input type="text" name="toDate" class="form-control toDate" placeholder="Select To Date" value="<%=endDate%>">
						</div>
					</div>
					<div class="row mb-2">
						<label class="col-lg-4 col-form-label">Bank Name</label>
						<div class="col-lg-8">
							<select name="bankName" class="form-control">
								<option value="">Select Bank</option>
								<%

									List<bsaBankDetails> bankDetails = (List<bsaBankDetails>) request.getAttribute("bankDetails");
									//out.println(bankDetails);
									for (bsaBankDetails bank : bankDetails) {
										out.print("<option value=\"" + bank.getInstitutionId() + "\"   " + (bank.getInstitutionId().equals(bankId) ? "selected" : "") + "  >" + bank.getName() + "</option>");
									}
								%>
								<!-- Add bank options here -->
							</select>
						</div>
					</div>
					<div class="col-lg-12 " style="justify-content: center;display: flex;">

					</div>

				</div>
				<div class="row mb-2 bsaABBDiv" style="<%= !vehicleLoanBSAList.isEmpty() ? "" : "display: none" %>">
					<div class="col-lg-12">
						<div class="bsaResponse table-responsive border rounded m-2">
            <%
                if (!vehicleLoanBSAList.isEmpty()) {
                    for (VehicleLoanBSA bsaDetails : vehicleLoanBSAList) {
                        if ("N".equals(bsaDetails.getDelFlg())) {
                            String responseValue = bsaDetails.getFetchResponse();
                            if (responseValue != null) {

		                            Map<String, Object> data = new ObjectMapper().readValue(responseValue, new TypeReference<Map<String, Object>>() {
		                            });
		                            Map<String, Object> bsaData = (Map<String, Object>) data.get("bsaData");

		                            Object avgBalance = bsaData.get("averageBankBalance");
		                            if (avgBalance != null) {
			                            double balanceValue = 0.0;
			                            if (avgBalance instanceof Integer) {
				                            balanceValue = ((Integer) avgBalance).doubleValue();
			                            } else if (avgBalance instanceof Double) {
				                            balanceValue = (Double) avgBalance;
			                            } else if (avgBalance instanceof String) {
				                            try {
					                            balanceValue = Double.parseDouble((String) avgBalance);
				                            } catch (NumberFormatException e) {
					                            balanceValue = 0.0;
				                            }
			                            }
			                            abbIncome = String.format("%.2f", balanceValue);
		                            } else {
			                            abbIncome = "0.00";
		                            }



                                String tableHtml = "";
                                tableHtml += "<table class='table table-bordered'>";
                                tableHtml += "<tr><th colspan='13'>Account Information</th></tr>";
                                tableHtml += "<tr>";
                                tableHtml += "<td>Name</td><td colspan='2'><b>" + ((Map<String, Object>) data.get("customerInfo")).get("name") + "</b></td>";
                                tableHtml += "<td>Account No</td><td>" + ((Map<String, Object>) data.get("accountInfo")).get("accountNo") + "</td>";
                                tableHtml += "<td colspan='2'>Account Type</td><td colspan='3'><b>" + ((Map<String, Object>) data.get("accountInfo")).get("accountType") + "</b></td>";
                                tableHtml += "</tr>";

                                tableHtml += "<tr><th colspan='13'>Monthly Details</th></tr>";
                                tableHtml += "<tr><th>Month</th><th>Total Credits</th><th>Total Debits</th><th>Balance Min</th><th>Balance Max</th><th>Balance Avg</th><th>Calculated ABB</th><th>Included in ABB</th><th>Penal Charges</th><th>Outward Bounces</th><th>Total Salary</th><th>Inward Bounces</th></tr>";

                                List<Map<String, Object>> monthlyDetails = (List<Map<String, Object>>) data.get("monthlyDetails");
                                int includedMonths = 0;
                                int excludedMonths = 0;
                                for (Map<String, Object> month : monthlyDetails) {
                                    boolean isTrimmed = Boolean.TRUE.equals(month.get("isTrimmed"));
                                    String rowStyle = isTrimmed ? "" : "style='background-color: #ffcccc;'";
                                    tableHtml += "<tr " + rowStyle + ">";
                                    tableHtml += "<td>" + month.get("month") + "</td>";
                                    tableHtml += "<td>" + month.get("totalCredits") + "</td>";
                                    tableHtml += "<td>" + month.get("totalDebits") + "</td>";
                                    tableHtml += "<td>" + month.get("balanceMin") + "</td>";
                                    tableHtml += "<td>" + month.get("balanceMax") + "</td>";
                                    tableHtml += "<td>" + month.get("balanceAvg") + "</td>";
                                    tableHtml += "<td><b>" + String.format("%.2f", month.get("calculatedABB")) + "</b></td>";
                                    tableHtml += "<td>" + (isTrimmed ? "Yes" : "No") + "</td>";
                                    tableHtml += "<td>" + month.get("penalCharges") + "</td>";
                                    tableHtml += "<td>" + month.get("outwBounces") + "</td>";
                                    tableHtml += "<td>" + month.get("totalSalary") + "</td>";
                                    tableHtml += "<td>" + month.get("inwBounces") + "</td>";
                                    tableHtml += "</tr>";
                                    if (isTrimmed) {
                                        includedMonths++;
                                    } else {
                                        excludedMonths++;
                                    }
                                }

                                tableHtml += "</table>";

                                tableHtml += "<div class='mt-3'>";
                                tableHtml += "<h4>ABB Calculation Summary</h4>";
                                tableHtml += "<p>Final Average Bank Balance: <b>" + abbIncome + "</b></p>";
                                tableHtml += "<p>Calculation Method: Average of monthly ABBs (excluding highest and lowest)</p>";
//                                tableHtml += "<p>Months included: " + includedMonths + "</p>";
//                                tableHtml += "<p>Months excluded: " + excludedMonths + "</p>";
                                tableHtml += "</div>";

                                out.println(tableHtml);
                            }
                        }
                    }
                }
            %>
        </div>
						<div class="col-lg-2"></div>
					</div>
					<label class="col-lg-5 col-form-label">Average Bank Balance</label>
					<div class="col-lg-7">
						<input type="text" name="bsaABB" id="bsaABB" readonly class="form-control abb-amount" placeholder="ABB Amount"
						       value="<%=abbIncome%>">
					</div>
				</div>
			</div>
		</div>

		<div class="_70_30div" name="_70_30div" style="<%= "70/30".equals(hidProgramCode) ? "" : "display: none" %>">
			<label class="col-form-label col-lg-5"><strong>70/30 program </strong> </label>
			<div class="row mb-2 inner_70_30div">
				Document uploaded
			</div>


		</div>


		<div class="fddiv" name="fddiv" style="<%= "LOANFD".equals(hidProgramCode) ? "" : "display: none" %>">
			<label class="col-form-label col-lg-5"><strong>Loan against FD program </strong> </label>

			<div class="row mb-2">
				<%--				<label class="col-form-label col-lg-5">Enter Account number<span class="text-danger">*</span></label>--%>
				<%--				<div class="col-lg-5">--%>
				<%--					<input type="text" name="fd_account_number" id="fd_account_number" class="form-control fd_account_number">--%>
				<%--				</div>--%>


				<div class="fdDetailsDiv" style="<%= !vehicleLoanFDList.isEmpty() ? "" : "display: none" %>">
					<div class="col-lg-12">
						<div class="fdResponse m-2 table-responsive">
							<%
								if (vehicleLoanFDList != null && !vehicleLoanFDList.isEmpty()) {
									String tableHtml = "";
									totalavailBalance = BigDecimal.ZERO;

									tableHtml += "<table class='table table-xs table-bordered'>" +
											"<tr><th>FD A/C</th><th>Account Status</th><th>Account Type</th><th>Account Open Date</th><th>Maturity Date</th><th>Deposit Amount</th><th>Maturity Amount</th><th>Available Balance</th><th>Eligible</th></tr>";
									for (VehicleLoanFD vehicleLoanFDItem : vehicleLoanFDList) {
										if (vehicleLoanFDItem.isEligible()) {
											String accountOpenDate = formatter.format(vehicleLoanFDItem.getAccountOpenDate());
											String maturityDate = formatter.format(vehicleLoanFDItem.getMaturityDate());
											String rowClass = vehicleLoanFDItem.isEligible() ? "" : "class='text-muted'";

											tableHtml += "<tr " + rowClass + ">";
											tableHtml += "<td>" + vehicleLoanFDItem.getFdaccnum() + "</td>";
											tableHtml += "<td>" + vehicleLoanFDItem.getFdStatus() + "</td>";
											tableHtml += "<td>" + vehicleLoanFDItem.getSingleJoint() + "</td>";
											tableHtml += "<td>" + accountOpenDate + "</td>";
											tableHtml += "<td>" + maturityDate + "</td>";
											tableHtml += "<td>" + vehicleLoanFDItem.getDepositAmount() + "</td>";
											tableHtml += "<td>" + vehicleLoanFDItem.getMaturityAmount() + "</td>";
											tableHtml += "<td>" + vehicleLoanFDItem.getAvailbalance() + "</td>";
											tableHtml += "<td>" + ((boolean) vehicleLoanFDItem.isEligible() ? "Yes" : "No") + "</td>";
											tableHtml += "</tr>";
											if (vehicleLoanFDItem.isEligible()) {
												totalavailBalance = totalavailBalance.add(vehicleLoanFDItem.getAvailbalance());
											}
										}
									}
									tableHtml += "</table>";
									out.println(tableHtml);
								}
							%>

						</div>
					</div>
					<div class="row m-2 totfdrow">
						<label class="col-lg-3 col-form-label fw-medium">Total Available Balance</label>
						<div class="col-lg-5 fw-semibold">
							<%=totalavailBalance%>
						</div>
					</div>


				</div>
			</div>


		</div>


		<div class="text-end">

			<%--			<button class="btn btn-yellow my-1 me-2 edit-button"><i class="ph-note-pencil  ms-2"></i>Edit</button>--%>


		</div>


	</form>

</div>