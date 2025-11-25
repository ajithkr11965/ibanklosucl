<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page import="com.fasterxml.jackson.core.type.TypeReference" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="java.math.RoundingMode" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanProgram" %>
<%@ page import="com.sib.ibanklosucl.dto.program.bsaBankDetails" %>
<%@ page import="java.time.YearMonth" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanFD" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanProgramNRI" %>
<%@ page import="java.time.format.TextStyle" %>
<%@ page import="java.util.Locale" %>
<%
	String datamode = (String) request.getAttribute("datamod");
	String apptype = (String) request.getAttribute("apptype");

	VehicleLoanApplicant applicant = null;
	VehicleLoanMaster hlMaster = null;
	VehicleLoanProgram hlProgram = null;
	List<VehicleLoanFD> vehicleLoanFDList = new ArrayList<>();

	if (request.getAttribute("general") != null) {
		applicant = (VehicleLoanApplicant) request.getAttribute("general");
	} else {

	}


	boolean completed = false, mod = false;
	String panNo = "", ocrPanNumber = "", incomemobileNo = "", incomemobCountryCode = "", incomeDOB = "", savedPan = "", savedDob = "";
	String appid = "", applicantype = "", liquidUploadStatus = "";
	String applicantId = "", wiNum = "", slno = "", docType = "";
	String hidincomeConsidered = "", hidProgramCode = "", itrFlg = "", form16Flg = "", monthlyGrossIncome = "", abbIncome = "", avgSal = "", liquidIncome = "", rtrIncome = "";


	String pattern = "yyyy-MM-dd";
	String cifId = "", sibCustomer = "", residentFlg = "", hidFDcifIdList = "";
	SimpleDateFormat formatter = new SimpleDateFormat(pattern);
	String fileclass = "file-input*";
	String panimg = "", panext = "";
	String startDate = "", endDate = "", bankId = "";
	String employmentType = "", propertyOwner = "", financialDiscrepancy = "", residentialStatus = "";
	BigDecimal totalavailBalance = BigDecimal.ZERO;
	String monthlySalary = "";
	BigDecimal avgTotalRemittance = BigDecimal.ZERO;
	BigDecimal avgBulkRemittance = BigDecimal.ZERO;
	BigDecimal avgNetRemittance = BigDecimal.ZERO;
	List<Map<String, String>> remittanceMonths = new ArrayList<>();
	LocalDate currentDate = LocalDate.now();
	YearMonth currentYearMonth = YearMonth.from(currentDate);

	// Start from 12 months ago - create template for 12 months
	for (int i = 11; i >= 0; i--) {
		YearMonth month = currentYearMonth.minusMonths(i);
		Map<String, String> monthData = new HashMap<>();
		monthData.put("monthYear", month.format(DateTimeFormatter.ofPattern("MMM yyyy")));
		monthData.put("monthYearValue", month.format(DateTimeFormatter.ofPattern("yyyy-MM")));
		monthData.put("totalRemittance", "");
		monthData.put("bulkRemittance", "");
		monthData.put("netRemittance", "");
		remittanceMonths.add(monthData);
	}
	if (applicant != null) {
		completed = "Y".equals(applicant.getIncomeComplete());
		applicantId = String.valueOf(applicant.getApplicantId());
		wiNum = applicant.getWiNum();
		slno = String.valueOf(applicant.getSlno());
		panNo = (applicant.getKycapplicants() != null) ? StringUtils.defaultString(applicant.getKycapplicants().getPanNo()) : "";
		appid = String.valueOf(applicant.getApplicantId());
		applicantype = String.valueOf(applicant.getApplicantType());
		if (applicant.getKycapplicants() != null && applicant.getKycapplicants().getPanDob() != null) {
			incomeDOB = formatter.format(applicant.getKycapplicants().getPanDob());
		}
		if (applicant.getResidentFlg() != null) {
			residentialStatus = applicant.getResidentFlg();
		}
		hlProgram = applicant.getVlProgram();
		if (hlProgram != null) {
			if (hlProgram.getIncomeConsidered() != null) {
				hidincomeConsidered = hlProgram.getIncomeConsidered();
			}
			if (hlProgram.getIncomeConsidered() != null) {
				hidProgramCode = hlProgram.getLoanProgram();
				if ("LIQUIDINCOME".equals(hidProgramCode)) {
					//liquidIncome = hlProgram.getLiquidMonthlyIncome().toPlainString();
					liquidUploadStatus = "uploaded";
				}

				// Load NRI remittance data if program is INCOME and resident is NRI
				if ("INCOME".equals(hidProgramCode) && "N".equals(residentialStatus)) {
					// Load monthly salary
					if (hlProgram.getNriNetSalary() != null) {
						monthlySalary = hlProgram.getNriNetSalary().toPlainString();
					}

					// Load averages
					if (hlProgram.getAvgTotalRemittance() != null) {
						avgTotalRemittance = hlProgram.getAvgTotalRemittance();
					}
					if (hlProgram.getAvgBulkRemittance() != null) {
						avgBulkRemittance = hlProgram.getAvgBulkRemittance();
					}
					if (hlProgram.getAvgNetRemittance() != null) {
						avgNetRemittance = hlProgram.getAvgNetRemittance();
					}

					// Load remittance details from VehicleLoanProgramNRI
					if (hlProgram.getVlnriList() != null && !hlProgram.getVlnriList().isEmpty()) {
						// Create a map of saved remittance data by month/year for easy lookup
						Map<String, VehicleLoanProgramNRI> savedRemittanceMap = new HashMap<>();
						for (VehicleLoanProgramNRI nri : hlProgram.getVlnriList()) {
							if ("N".equals(nri.getDelFlg())) {
								// Create key in "yyyy-MM" format for matching
								String key = String.format("%d-%02d", nri.getRemitYear(), nri.getRemitMonth());
								savedRemittanceMap.put(key, nri);
							}
						}

						// Populate the remittanceMonths list with saved data
						for (Map<String, String> monthData : remittanceMonths) {
							String monthYearValue = monthData.get("monthYearValue"); // "2024-11"
							VehicleLoanProgramNRI savedNri = savedRemittanceMap.get(monthYearValue);
							if (savedNri != null) {
								monthData.put("totalRemittance", savedNri.getTotRemittance() != null ? String.valueOf(savedNri.getTotRemittance()) : "");
								monthData.put("bulkRemittance", savedNri.getBulkRemittance() != null ? String.valueOf(savedNri.getBulkRemittance()) : "");
								monthData.put("netRemittance", savedNri.getNetRemittance() != null ? String.valueOf(savedNri.getNetRemittance()) : "");
							}
						}
					}
				}
			}

		}
	}
	if (request.getAttribute("init") != null)
		fileclass = "file-input";
	completed = true;


%>
<div class="program-itr">
	<form class="det form-details  Incomedetails" data-code="<%=apptype%>-5" data-completed="<%=completed%>" action="#" data-page-mode="<%=datamode%>">
		<div class="kt d-flex justify-content-end" style="height: 0em;">
			<button class="edit-button btn btn-icon  btn-bg-light btn-color-info btn-sm me-1">
				<i class="ki-duotone  ki-pencil fs-2"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
			</button>
		</div>
		<input type="hidden" name="applicantId" class="applicantId" value="<%=applicantId%>">
		<input type="hidden" name="applicantype" class="applicantype" value="<%=applicantype%>">
		<input type="hidden" name="sibCustomer" class="sibCustomer" value="<%=sibCustomer%>">
		<input type="hidden" name="cifId" class="cifId" value="<%=cifId%>">
		<input type="hidden" name="wiNum" class="wiNum" value="<%=wiNum%>">
		<input type="hidden" name="slno" class="slno" value="<%=slno%>">
		<input type="hidden" name="entry_user" class="entry_user" value="">
		<input type="hidden" name="hidincomeConsidered" class="hidincomeConsidered" value="<%=hidincomeConsidered%>">
		<input type="hidden" name="hidProgramCode" class="hidProgramCode" value="<%=hidProgramCode%>">
		<input type="hidden" name="hidliquidMonthlyIncome" class="hidliquidMonthlyIncome" value="<%=liquidIncome%>">
		<input type="hidden" name="hidliquidUploadStatus" class="hidliquidUploadStatus" value="<%=liquidUploadStatus%>">
		<input type="hidden" name="form16Flg" class="form16Flg" value="<%=form16Flg%>">
		<input type="hidden" name="hidStartDate" class="hidStartDate" value="<%=startDate%>">
		<input type="hidden" name="hidEndDate" class="hidEndDate" value="<%=endDate%>">
		<input type="hidden" name="hidInstituition" class="hidInstituition" value="<%=bankId%>">
		<input type="hidden" name="savedPan" class="hidInstituition" value="<%=savedPan%>">
		<input type="hidden" name="bsaProcessed" value="N">

		<div class="section-card primary-card">
			<div class="section-header">
				<i class="ph-money pe-2"></i>
				<h3 class="section-title">Program Details</h3>
			</div>
			<div class="section-body">
				<div class="field-row">
					<div class="field-group">
						<label class="field-label">Whether income should be considered? <span class="text-danger">*</span></label>
						<div class="field-value">
							<div class="form-check form-check-inline">
								<input type="radio" class="form-check-input incomeCheck" id="incomeCheck-yes" name="incomeCheck"
								       value="Y" <%= "Y".equals(hidincomeConsidered) ? "checked" : "" %> <%= apptype.equals("G") ? "disabled" : "" %>>
								<label class="form-check-label" for="incomeCheck-yes">Yes</label>
							</div>
							<div class="form-check form-check-inline">
								<input type="radio" class="form-check-input incomeCheck" id="incomeCheck-no" name="incomeCheck"
								       value="N" <%= "N".equals(hidincomeConsidered) || apptype.equals("G") ? "checked" : "" %>>
								<label class="form-check-label" for="incomeCheck-no">No</label>
							</div>
						</div>
					</div>
				</div>

				<!-- Program Selection -->
				<div class="program row mb-2" style="<%= "Y".equals(hidincomeConsidered) ? "" : "display: none" %>">
					<div class="field-group">
						<label class="field-label">Program</label>
						<div class="field-value">
							<select data-placeholder="Select Program" id="programCode" name="programCode" class="form-control select programCode">
								<option value="NONE" selected>NONE</option>
								<%
									Map<String, String> options = new HashMap<>();
									options.put("INCOME", "Income");
									options.put("SURROGATE", "Surrogate");
									options.put("LOANFD", "FD");
									options.put("NONFOIR", "Non - Foir");
									options.put("IMPUTED", "Imputed Income");
									for (Map.Entry<String, String> entry : options.entrySet()) {
										if (apptype.equals("G")) {
											out.print("<option value=NONE selected>NONE</option>");
										} else {
											out.print("<option value=\"" + entry.getKey() + "\" " + (entry.getKey().equals(hidProgramCode) ? "selected" : "") + ">" + entry.getValue() + "</option>");
										}
									}
								%>
							</select>
						</div>
					</div>
				</div>
			</div>
		</div>


		<!-- Income Program Section -->
		<div class="incomediv" name="incomediv" style="<%= "INCOME".equals(hidProgramCode) ? "" : "display: none" %>">
			<div class="section-card primary-card">
				<div class="section-header">
					<i class="ph-currency-circle-dollar pe-2"></i>
					<h3 class="section-title">Income Program</h3>
				</div>
				<div class="section-body">
					<!-- Employment Type Selection -->
					<div class="field-row">
						<div class="field-group">
							<label class="field-label">Employment Type</label>
							<div class="field-value">
								<input type="text" class="form-control empl_type" name="empl_type" readonly>
							</div>
						</div>
					</div>
					<div class="non-resident-income-section" style="<%= "N".equals(residentialStatus) ? "" : "display: none;" %>">
						<div class="section-card primary-card mt-3">
							<div class="section-header">
								<i class="ph-currency-circle-dollar pe-2"></i>
								<h6 class="section-title">Monthly Gross Income Declaration (Non-Resident)</h6>
							</div>
							<div class="section-body">

								<!-- Information Alert -->
								<div class="alert alert-info d-flex align-items-start mb-4">
									<i class="ph-info fs-4 me-2 mt-1"></i>
									<div>
										<strong>Note:</strong> For Non-Resident applicants, ITR is not applicable.
										Please provide your monthly salary and remittance details for income verification.
									</div>
								</div>

								<!-- Hidden field to indicate this is monthly salary based calculation -->
								<input type="hidden" name="monthlyorabb" value="MonthSalary">

								<!-- Monthly Salary Input -->
								<div class="field-row mb-4">
									<div class="field-group">
										<label class="field-label">
											Monthly Salary (INR) <span class="text-danger">*</span>
										</label>
										<div class="field-value">
											<input type="number"
											       class="form-control monthly-salary-nr"
											       name="monthlySalaryNR"
											       placeholder="Enter monthly salary amount"
											       value="<%=monthlySalary%>"
											       step="0.01"
											       min="0">
										</div>
									</div>
								</div>

								<!-- Remittance Data Grid -->
								<div class="remittance-grid-container">
									<div class="remittance-grid-header d-flex align-items-center justify-content-between mb-3">
										<h6 class="mb-0">
											<i class="ph-table me-2"></i>12-Month Remittance Details
										</h6>
										<span class="badge bg-primary">Last 12 Months</span>
									</div>

									<div class="table-responsive">
										<table class="table table-bordered table-hover remittance-calculation-table">
											<thead class="table-light">
												<tr>
													<th class="text-center" style="width: 5%;">S.No</th>
													<th style="width: 20%;">Month & Year</th>
													<th style="width: 25%;">
														Total Remittance (<i class="ph-currency-inr fs-6"></i>)
														<span class="text-danger">*</span>
													</th>
													<th style="width: 25%;">
														Bulk Remittance (<i class="ph-currency-inr fs-6"></i>)
													</th>
													<th style="width: 25%;">
														Net Remittance (<i class="ph-currency-inr fs-6"></i>)
													</th>
												</tr>
											</thead>
											<tbody class="remittance-table-body">
												<%
													int slNo = 1;
													for (Map<String, String> month : remittanceMonths) {
												%>
												<tr class="remittance-row" data-month="<%=month.get("monthYearValue")%>" data-row-index="<%=slNo-2%>">
													<td class="text-center"><%=slNo++%>
													</td>
													<td class="fw-bold"><%=month.get("monthYear")%>
													</td>
													<td>
														<input type="number"
														       class="form-control total-remittance"
														       name="remittanceData[<%=slNo-2%>].totalRemittance"
														       data-month="<%=month.get("monthYearValue")%>"
														       data-row-index="<%=slNo-2%>"
														       placeholder="0.00"
														       step="0.01"
														       min="0"
														       value="<%=month.get("totalRemittance")%>">
														<input type="hidden"
														       name="remittanceData[<%=slNo-2%>].monthYear"
														       class="remittance-month-year"
														       value="<%=month.get("monthYearValue")%>">
													</td>
													<td>
														<input type="number"
														       class="form-control bulk-remittance"
														       name="remittanceData[<%=slNo-2%>].bulkRemittance"
														       data-month="<%=month.get("monthYearValue")%>"
														       data-row-index="<%=slNo-2%>"
														       placeholder="0.00"
														       step="0.01"
														       min="0"
														       value="<%=month.get("bulkRemittance")%>">
													</td>
													<td>
														<input type="number"
														       class="form-control net-remittance bg-light"
														       name="remittanceData[<%=slNo-2%>].netRemittance"
														       data-month="<%=month.get("monthYearValue")%>"
														       data-row-index="<%=slNo-2%>"
														       placeholder="0.00"
														       readonly
														       value="<%=month.get("netRemittance")%>">
													</td>
												</tr>
												<%
													}
												%>
											</tbody>
											<tfoot class="table-secondary fw-bold">
												<tr>
													<td colspan="2" class="text-end">Average:</td>
													<td>
														<input type="text"
														       class="form-control avg-total-remittance bg-light fw-bold"
														       name="avgTotalRemittance"
														       readonly
														       value="<%=avgTotalRemittance%>">
													</td>
													<td>
														<input type="text"
														       class="form-control avg-bulk-remittance bg-light fw-bold"
														       name="avgBulkRemittance"
														       readonly
														       value="<%=avgBulkRemittance%>">
													</td>
													<td>
														<input type="text"
														       class="form-control avg-net-remittance bg-light fw-bold"
														       name="avgNetRemittance"
														       readonly
														       value="<%=avgNetRemittance%>">
													</td>
												</tr>
											</tfoot>
										</table>
									</div>
								</div>

								<!-- Final Monthly Gross Income Display -->
								<div class="monthly-gross-income-result mt-4">
									<div class="alert alert-success d-flex align-items-center justify-content-between">
										<div class="d-flex align-items-center">
											<i class="ph-check-circle fs-2 me-3"></i>
											<div>
												<h6 class="mb-1">Calculated Monthly Gross Income</h6>
												<small class="text-muted">Based on Average Net Remittance</small>
											</div>
										</div>
										<div class="text-end">
											<h4 class="mb-0 text-success fw-bold">
												<i class="ph-currency-inr fs-6"></i> <span class="calculated-monthly-gross-income"><%=avgNetRemittance%></span>
											</h4>
											<input type="hidden"
											       name="monthlyGrossIncomeNR"
											       class="monthly-gross-income-nr-hidden"
											       value="<%=avgNetRemittance%>">
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>


					<div class="resident-income-sections" style="<%= "R".equals(residentialStatus) ? "display: none;" : "" %>">
						<!-- Salaried Employment Type Section -->
						<div class="salaried-section" style="display: none;">
							<div class="section-card primary-card mt-3">
								<div class="section-header">
									<i class="ph-user-focus pe-2"></i>
									<h6 class="section-title">Salaried</h6>
								</div>
								<div class="section-body">
									<!-- Document Selection -->
									<div class="doc-selector">
										<label class="form-label mb-2">Select Document Type <span class="text-danger">*</span></label>

										<div class="doc-options mt-2">
											<div class="doc-option">
												<input type="radio" id="itr-option" name="docTypeSelection" value="ITR"
												       class="doc-type-selection" <%= "ITR".equals(docType) ? "checked" : "" %>>
												<label for="itr-option" class="doc-option-label">Latest ITR</label>
											</div>
											<div class="doc-option">
												<input type="radio" id="payslip-option" name="docTypeSelection" value="PAYSLIP"
												       class="doc-type-selection" <%= "PAYSLIP".equals(docType) ? "checked" : "" %>>
												<label for="payslip-option" class="doc-option-label">Last 3 Months Payslip</label>
											</div>
										</div>
									</div>

									<!-- ITR Section -->
									<div class="itr-section" style="display: none;">
										<div class="itr-options-container">
											<div class="d-flex justify-content-between align-items-center mb-2">
												<h6 class="m-0">Income Tax Return</h6>
												<span class="badge bg-secondary">Required</span>
											</div>


											<div class="itr-buttons-container">
												<button type="button" class="btn btn-outline-primary itr-fetch-btn flex-fill" data-itrmode="sms">
													<i class="ph-device-mobile pe-1"></i> Fetch ITR
												</button>
												<button type="button" class="btn btn-outline-primary itr-upload-btn flex-fill" data-itrmode="upload">
													<i class="ph-upload-simple pe-1"></i> Upload ITR
												</button>
											</div>

											<div class="check-itr-status mt-2" style="display: none;">
												<button type="button" class="btn btn-info btn-sm">
													<i class="ph-question pe-1"></i>Check ITR Status
												</button>
											</div>
										</div>

										<div class="itr-response itrMonthlyGrossDiv mt-3">
											<div class="itrResponse"></div>
										</div>

										<div class="itr-monthly-gross-div mt-3" style="display: none;">
											<div class="field-row">
												<div class="field-group">
													<label class="field-label">Monthly Gross Income</label>
													<div class="field-value">
														<div class="input-group">
															<span class="input-group-text"><i class="ph-currency-inr fs-6"></i></span>
															<input type="text" class="form-control itr-monthly-gross " name="itrMonthlyGross" readonly
															       value="<%=monthlyGrossIncome%>">
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>

									<!-- Payslip Section -->
									<div class="payslip-section" style="display: none;">
										<div class="alert-card alert-info mb-3">
											<i class="ph-info pe-2"></i>
											<span>Upload at least one and up to three months of payslips</span>
										</div>

										<div class="table-responsive">
											<table class="payslip-table">
												<thead>
													<tr>
														<th>Month</th>
														<th>Year</th>
														<th>Payslip File</th>
														<th>Amount (<i class="ph-currency-inr fs-6"></i>)</th>
														<th>Action</th>
													</tr>
												</thead>
												<tbody class="payslip-table-body">
													<!-- Payslip rows will be added dynamically -->
												</tbody>
											</table>
										</div>

										<div class="mt-2">
											<button type="button" class="btn btn-outline-primary btn-sm add-payslip-row">
												<i class="ph-plus pe-1"></i> Add Payslip
											</button>
										</div>

										<div class="form-divider"></div>

										<div class="field-row">
											<div class="field-group">
												<label class="field-label">Total Income:</label>
												<div class="field-value">
													<div class="input-group">
														<span class="input-group-text"><i class="ph-currency-inr fs-6"></i></span>
														<input type="text" class="form-control total-income" name="totalIncome" readonly>
													</div>
												</div>
											</div>
										</div>

										<div class="field-row">
											<div class="field-group">
												<label class="field-label">Average Monthly Income:</label>
												<div class="field-value">
													<div class="input-group">
														<span class="input-group-text"><i class="ph-currency-inr fs-6"></i></span>
														<input type="text" class="form-control avg-monthly-income" name="avgMonthlyIncome" readonly value="<%=avgSal%>">
													</div>
												</div>
											</div>
										</div>
									</div>
									<!-- FORM16 Data capture -->
									<div class="form-divider my-4"></div>
									<!-- NEW: FORM 16 AS SUPPORTING DOCUMENT -->
									<div class="form16-supporting-section">
										<div class="supporting-doc-card">
											<div class="supporting-doc-header">
												<i class="ph-file-text pe-2"></i>
												<h6 class="mb-0">Supporting Documentation</h6>
											</div>

											<div class="supporting-doc-body">
												<!-- Question: Do you have Form 16? -->
												<div class="field-row mb-3">
													<div class="field-group">
														<label class="field-label">Form 16 available? <span class="text-info">(Optional)</span></label>
														<div class="field-value">
															<div class="form-check form-check-inline">
																<input type="radio" class="form-check-input has-form16-check" id="has-form16-yes"
																       name="hasForm16" value="Y" <%= "Y".equals(form16Flg) ? "checked" : "" %>>
																<label class="form-check-label" for="has-form16-yes">Yes</label>
															</div>
															<div class="form-check form-check-inline">
																<input type="radio" class="form-check-input has-form16-check" id="has-form16-no"
																       name="hasForm16" value="N" <%= "N".equals(form16Flg) || StringUtils.isEmpty(form16Flg) ? "checked" : "" %>>
																<label class="form-check-label" for="has-form16-no">No</label>
															</div>
														</div>
														<small class="text-muted">
															<i class="ph-info-circle pe-1"></i>
															Form 16 serves as supporting documentation and does not replace ITR or Salary Slip requirement.
														</small>
													</div>
												</div>

												<!-- Form 16 Upload Section (Shows only when Yes is selected) -->
												<div class="form16-upload-container" style="<%= "Y".equals(form16Flg) ? "" : "display: none;" %>">
													<div class="alert alert-info d-flex align-items-start">
														<i class="ph-lightbulb fs-5 me-2"></i>
														<div>
															<strong>Note:</strong> Form 16 is being used as supporting documentation to verify your income.
															The income calculation is based on your selected primary document (ITR or Salary Slip).
														</div>
													</div>

													<div class="compact-form-group">
														<label class="form-label">Upload Form 16 <span class="text-danger">*</span></label>
														<input type="file" class="form-control form16-supporting-upload base64file"
														       name="form16SupportingFile" accept=".pdf" data-max-size="2097152">
														<input type="hidden" name="form16SupportingFileBase64">
														<div class="form-text">Accepted file: PDF (Max: 2MB)</div>
													</div>

													<!-- Upload Status Indicator -->
													<div class="form16-upload-status mt-2" style="display: none;">
														<div class="alert alert-success d-flex align-items-center">
															<i class="ph-check-circle fs-5 me-2"></i>
															<span>Form 16 uploaded successfully</span>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>


								</div>
							</div>
						</div>

						<!-- Pensioner Employment Type Section -->
						<div class="pensioner-section" style="display: none;">
							<div class="section-card info-card mt-3">
								<div class="section-header">
									<i class="ph-user-circle-gear pe-2"></i>
									<h6 class="section-title">Pensioner </h6>
								</div>
								<div class="section-body">
									<!-- ITR Section for Pensioner -->
									<div class="itr-options-container">
										<div class="d-flex justify-content-between align-items-center mb-2">
											<h6 class="m-0">Income Tax Return (1 year)</h6>
											<span class="badge bg-secondary">Required</span>
										</div>

										<div class="itr-buttons-container">
											<button type="button" class="btn btn-outline-primary itr-fetch-btn flex-fill">
												<i class="ph-device-mobile pe-1"></i> Fetch via SMS
											</button>
											<button type="button" class="btn btn-outline-primary itr-upload-btn flex-fill">
												<i class="ph-upload-simple pe-1"></i> Upload ITR
											</button>
										</div>

										<div class="check-itr-status mt-2" style="display: none;">
											<button type="button" class="btn btn-info btn-sm">
												<i class="ph-question pe-1"></i>Check ITR Status
											</button>
										</div>
									</div>

									<div class="itr-response itrMonthlyGrossDiv mt-3">
										<div class="itrResponse"></div>
									</div>

									<div class="itr-monthly-gross-div mt-3" style="display: none;">
										<div class="field-row">
											<div class="field-group">
												<label class="field-label">Monthly Gross Income</label>
												<div class="field-value">
													<div class="input-group">
														<span class="input-group-text"><i class="ph-currency-inr fs-6"></i></span>
														<input type="text" class="form-control itr-monthly-gross " name="pensionerMonthlyGross" readonly
														       value="<%=monthlyGrossIncome%>">
													</div>
												</div>
											</div>
										</div>
									</div>
									<div class="form-divider"></div>
									<div class="form-divider"></div>
								</div>
							</div>
						</div>

						<!-- SEP/SENP Employment Type Section -->
						<div class="sepsenp-section" style="display: none;">
							<div class="section-card warning-card mt-3">
								<div class="section-header">
									<i class="ph-user-gear pe-2"></i>
									<h6 class="section-title">Self Employed </h6>
								</div>
								<div class="section-body">
									<!-- ITR Section for SEP/SENP -->
									<div class="itr-options-container">
										<div class="d-flex justify-content-between align-items-center mb-2">
											<h6 class="m-0">Income Tax Return</h6>
											<span class="badge bg-secondary">Required</span>
										</div>

										<div class="itr-buttons-container">
											<button type="button" class="btn btn-outline-primary itr-fetch-btn flex-fill">
												<i class="ph-device-mobile pe-1"></i> Fetch via SMS
											</button>
											<button type="button" class="btn btn-outline-primary itr-upload-btn flex-fill">
												<i class="ph-upload-simple pe-1"></i> Upload ITR
											</button>
										</div>

										<div class="check-itr-status mt-2" style="display: none;">
											<button type="button" class="btn btn-info btn-sm">
												<i class="ph-question pe-1"></i>Check ITR Status
											</button>
										</div>
									</div>

									<div class="itr-response itrMonthlyGrossDiv mt-3">
										<div class="itrResponse"></div>
									</div>

									<div class="itr-monthly-gross-div mt-3" style="display: none;">
										<div class="field-row">
											<div class="field-group">
												<label class="field-label">Monthly Gross Income</label>
												<div class="field-value">
													<div class="input-group">
														<span class="input-group-text"><i class="ph-currency-inr fs-6"></i></span>
														<input type="text" class="form-control itr-monthly-gross" name="sepSenpMonthlyGross" readonly>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>

						<!-- Agriculturist Employment Type Section -->
						<div class="agriculturist-section" style="display: none;">
							<div class="section-card danger-card mt-3">
								<div class="section-header">
									<i class="ph-tree pe-2"></i>
									<h6 class="section-title">Agriculturist </h6>
								</div>
								<div class="section-body">


									<!-- ITR Section for Agriculturist -->
									<div class="itr-options-container">
										<div class="d-flex justify-content-between align-items-center mb-2">
											<h6 class="m-0">Income Tax Return (1 year)</h6>
											<span class="badge bg-secondary">Required</span>
										</div>

										<div class="itr-buttons-container">
											<button type="button" class="btn btn-outline-primary itr-fetch-btn flex-fill">
												<i class="ph-device-mobile pe-1"></i> Fetch via SMS
											</button>
											<button type="button" class="btn btn-outline-primary itr-upload-btn flex-fill">
												<i class="ph-upload-simple pe-1"></i> Upload ITR
											</button>
										</div>

										<div class="check-itr-status mt-2" style="display: none;">
											<button type="button" class="btn btn-info btn-sm">
												<i class="ph-question pe-1"></i>Check ITR Status
											</button>
										</div>
									</div>

									<div class="itr-response itrMonthlyGrossDiv mt-3">
										<div class="itrResponse"></div>
									</div>

									<div class="itr-monthly-gross-div mt-3" style="display: none;">
										<div class="field-row">
											<div class="field-group">
												<label class="field-label">Monthly Gross Income</label>
												<div class="field-value">
													<div class="input-group">
														<span class="input-group-text"><i class="ph-currency-inr fs-6"></i></span>
														<input type="text" class="form-control itr-monthly-gross" name="agriculturistMonthlyGross" readonly>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div><!--resident income section-->
				</div>
			</div>
		</div> <!--income div ends here -->


		<div class="surrogatediv" name="surrogatediv" style="<%= "SURROGATE".equals(hidProgramCode) ? "" : "display: none" %>">
			<div class="section-card warning-card">
				<div class="section-header">
					<i class="ph-chart-bar pe-2"></i>
					<h3 class="section-title">Surrogate program</h3>
				</div>
				<div class="section-body">
					<div class="hidden surrogate-section" id="surrogate-section">
						<!-- EDIT MODE -->
						<div id="surrogate-edit-mode" class="surrogate-edit-mode">
							<h5>Surrogate Edit Mode</h5>
							<div class="coverage-info">
								Months covered: <span id="months-covered" class="months-covered">0</span> / 12
								<span id="coverage-status" class="coverage-incomplete coverage-status">(incomplete)</span>
							</div>
							<!-- Container that holds our statements -->
							<div id="bank-statement-container" class="bank-statement-container"></div>
							<!-- Buttons in the edit mode -->
							<div class="mt-3">
								<!-- ADDED FOR MANUAL ADD -->
								<button type="button" class="addStatementBtn btn btn-success" id="addStatementBtn">Add Another Statement</button>
								<button type="button" class="btn btn-primary ml-3 review-selections-btn" id="review-selections-btn" disabled>Proceed</button>
								<button type="button" class="btn btn-warning ml-3 resetSurrogateBtnEdit" id="resetSurrogateBtnEdit">Reset Surrogate</button>
							</div>
						</div>

						<!-- REVIEW MODE -->
						<div class="slide-section surrogate-review-mode" id="surrogate-review-mode">
							<h5>Surrogate Review Mode</h5>
							<p class="text-muted">
								Review each row. If all correct, upload statements or go back to edit.
							</p>
							<div class="table-responsive">
								<table class="table table-bordered table-sm">
									<thead class="thead-light">
										<tr>
											<th>#</th>
											<th>Start Month/Year</th>
											<th>End Month/Year</th>
											<th>Bank Code</th>
											<th>Action</th>
										</tr>
									</thead>
									<tbody id="surrogate-summary-body" class="surrogate-summary-body"></tbody>
								</table>
							</div>
							<div class="d-flex justify-content-between mt-3">
								<button type="button" class="btn btn-secondary backToEditBtn" id="backToEditBtn"> Back: Edit Selections</button>
								<div>
									<button type="button" class="btn btn-warning resetSurrogateBtnReview" id="resetSurrogateBtnReview">Reset Surrogate</button>
								</div>
							</div>
						</div>
						<div class="surrogate-bsa-status mt-3 bsaABBDiv" data-response-type="SURROGATE-1">
							<div class="bsaResponse"></div>
						</div>
						<div class="surrogate-bsa-status mt-3 bsaABBDiv" data-response-type="SURROGATE-2">
							<div class="bsaResponse"></div>
						</div>
						<div class="surrogate-bsa-status mt-3 bsaABBDiv" data-response-type="SURROGATE-3">
							<div class="bsaResponse"></div>
						</div>
					</div>
					<div class="section-card mt-3">
						<div class="section-header">
							<i class="ph-calculator pe-2"></i>
							<h6 class="section-title">Average Bank Balance (ABB)</h6>
						</div>
						<div class="section-body">
							<div class="field-row">
								<div class="field-group">
									<label class="field-label">Average Bank Balance (ABB)</label>
									<div class="field-value">
										<div class="input-group">
											<span class="input-group-text"><i class="ph-currency-inr fs-5"></i></span>
											<input type="text" class="form-control abb-amount" name="abbAmount" readonly>
										</div>
										<small class="text-muted">This is calculated based on the processed bank statements</small>
										<a href="#" class="btn-view-modern btn-example btn-calc-info">
											<i class="fas fa-chart-line p-1"></i>
											View ABB Calculation Details</a>
										</a>
									</div>
								</div>
							</div>
						</div>
					</div>

				</div>
			</div>
		</div> <!--Surrogate ends here -->

		<!-- FD Program Section -->
		<div class="fddiv" name="fddiv" style="<%= "LOANFD".equals(hidProgramCode) ? "" : "display: none" %>">
			<div class="section-card primary-card">
				<div class="section-header">
					<i class="ph-vault pe-2"></i>
					<h6 class="section-title">Loan Against FD Program</h6>
				</div>
				<div class="section-body">

					<!-- Info Alert -->
					<div class="alert alert-info d-flex align-items-start mb-3">
						<i class="ph-info fs-4 me-2"></i>
						<div>
							<strong>Note:</strong> This program is available only for existing customers.
							Fixed Deposit details will be fetched from Finacle based on the customer's CIF ID.
						</div>
					</div>

					<!-- Fetch FD Details Button -->
					<div class="row mb-4">
						<div class="col-lg-12 text-center">
							<button type="button" class="btn btn-primary fd-account-validate">
								<i class="ph-vault me-2"></i>Fetch FD Details
							</button>
							<div class="mt-2">
                        <span class="badge bg-secondary">
                            <i class="ph-info-circle me-1"></i>
                            This will fetch all FD account details of the customer from Finacle
                        </span>
							</div>
						</div>
					</div>
					<div class="missing-cif-alert-section" style="display: none;">
						<div class="alert alert-warning alert-dismissible fade show" role="alert">
							<div class="d-flex align-items-start">
								<i class="ph-warning fs-3 me-3"></i>
								<div class="flex-grow-1">
									<h6 class="alert-heading mb-2">
										<i class="ph-users me-2"></i>Co-Applicants Required
									</h6>
									<p class="mb-2">
										The following CIF IDs are associated with the FD accounts but are not added as applicants/co-applicants in this loan application.
										Please add them as co-applicants to proceed.
									</p>
									<div class="missing-cif-list">
										<!-- Will be populated dynamically -->
									</div>
									<hr>
									<small class="text-muted">
										<i class="ph-info-circle me-1"></i>
										Joint FD accounts require all holders to be part of the loan application.
									</small>
								</div>
							</div>
							<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
						</div>
					</div>

					<!-- FD Details Table Container -->
					<div class="fdDetailsDiv" style="<%= !vehicleLoanFDList.isEmpty() ? "" : "display: none" %>">

						<!-- FD Accounts Table -->
						<div class="table-responsive">
							<div class="fdResponse m-2">
								<%
									if (vehicleLoanFDList != null && !vehicleLoanFDList.isEmpty()) {
										String tableHtml = "";
										totalavailBalance = BigDecimal.ZERO;

										tableHtml += "<table class='table table-bordered table-hover'>";
										tableHtml += "<thead class='table-light'>";
										tableHtml += "<tr>";
										tableHtml += "<th>FD A/C</th>";
										tableHtml += "<th>Account Status</th>";
										tableHtml += "<th>Account Type</th>";
										tableHtml += "<th>Account Open Date</th>";
										tableHtml += "<th>Maturity Date</th>";
										tableHtml += "<th>Deposit Amount (₹)</th>";
										tableHtml += "<th>Deposit Available (₹)</th>";
										tableHtml += "<th>FSLD Adj (₹)</th>";
										tableHtml += "<th>Available Balance (₹)</th>";
										tableHtml += "<th>Eligible</th>";
										tableHtml += "<th>Action</th>";
										tableHtml += "</tr>";
										tableHtml += "</thead>";
										tableHtml += "<tbody>";

										for (VehicleLoanFD vehicleLoanFDItem : vehicleLoanFDList) {
											if (vehicleLoanFDItem.isEligible()) {
												String accountOpenDate = formatter.format(vehicleLoanFDItem.getAccountOpenDate());
												String maturityDate = formatter.format(vehicleLoanFDItem.getMaturityDate());
												String rowClass = vehicleLoanFDItem.isEligible() ? "" : "text-muted";

												tableHtml += "<tr class='" + rowClass + "'>";
												tableHtml += "<td><strong>" + vehicleLoanFDItem.getFdaccnum() + "</strong></td>";
												tableHtml += "<td>" + vehicleLoanFDItem.getFdStatus() + "</td>";
												tableHtml += "<td>" + vehicleLoanFDItem.getSingleJoint() + "</td>";
												tableHtml += "<td>" + accountOpenDate + "</td>";
												tableHtml += "<td>" + maturityDate + "</td>";
												tableHtml += "<td class='text-end'>" + vehicleLoanFDItem.getDepositAmount() + "</td>";
												tableHtml += "<td class='text-end'>" + vehicleLoanFDItem.getFdBalAmount() + "</td>";
												tableHtml += "<td class='text-end'>" + vehicleLoanFDItem.getFsldAdjAmount() + "</td>";
												tableHtml += "<td class='text-end'><strong>" + vehicleLoanFDItem.getAvailbalance() + "</strong></td>";

												if (vehicleLoanFDItem.isEligible()) {
													tableHtml += "<td><span class='badge bg-success'>Yes</span></td>";
													tableHtml += "<td><button type='button' class='btn btn-danger btn-sm delete-fd-btn' data-ino='" + vehicleLoanFDItem.getIno() + "'>";
													tableHtml += "<i class='ph-trash me-1'></i>Delete</button></td>";
												} else {
													tableHtml += "<td><span class='badge bg-secondary'>No</span></td>";
													tableHtml += "<td>-</td>";
												}

												tableHtml += "</tr>";

												if (vehicleLoanFDItem.isEligible()) {
													totalavailBalance = totalavailBalance.add(vehicleLoanFDItem.getAvailbalance());
												}
											}
										}

										tableHtml += "</tbody>";
										tableHtml += "</table>";
										out.println(tableHtml);
									}
								%>
							</div>
						</div>

						<!-- Total Available Balance -->
						<div class="row m-2 totfdrow">
							<div class="col-lg-12">
								<div class="card bg-light-success">
									<div class="card-body">
										<div class="d-flex justify-content-between align-items-center">
											<div>
												<h6 class="mb-1">Total Available Balance</h6>
												<small class="text-muted">Sum of all eligible FD accounts</small>
											</div>
											<div class="text-end">
												<h4 class="mb-0 text-success">
													<span class="totalavailBalance-display"><%=totalavailBalance%></span>
												</h4>
												<input type="hidden" name="totalavailBalance"
												       class="totalavailBalance" value="<%=totalavailBalance%>">
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>

						<!-- FD Summary Cards -->
						<div class="row mt-3">
							<div class="col-md-4">
								<div class="card">
									<div class="card-body text-center">
										<i class="ph-bank fs-1 text-primary mb-2"></i>
										<h6 class="mb-1">Total FD Accounts</h6>
										<h4 class="mb-0 fd-count"><%=vehicleLoanFDList.size()%>
										</h4>
									</div>
								</div>
							</div>
							<div class="col-md-4">
								<div class="card">
									<div class="card-body text-center">
										<i class="ph-check-circle fs-1 text-success mb-2"></i>
										<h6 class="mb-1">Eligible Accounts</h6>
										<h4 class="mb-0 fd-eligible-count">
											<%=vehicleLoanFDList.stream().filter(VehicleLoanFD::isEligible).count()%>
										</h4>
									</div>
								</div>
							</div>
							<div class="col-md-4">
								<div class="card">
									<div class="card-body text-center">
										<i class="ph-currency-circle-dollar fs-1 text-info mb-2"></i>
										<h6 class="mb-1">Loan Eligibility</h6>
										<h4 class="mb-0 fd-loan-eligibility"><%=totalavailBalance%>
										</h4>
									</div>
								</div>
							</div>
						</div>

					</div>

					<!-- No FD Message -->
					<div class="no-fd-message" style="<%= vehicleLoanFDList.isEmpty() ? "" : "display: none" %>">
						<div class="alert alert-warning d-flex align-items-center">
							<i class="ph-warning-circle fs-4 me-2"></i>
							<div>
								No FD accounts found. Click "Fetch FD Details" to retrieve the customer's FD accounts.
							</div>
						</div>
					</div>

				</div>
			</div>
		</div><!-- FD Closes here -->
		<div class="imputeddiv" name="imputeddiv" style="<%= "IMPUTED".equals(hidProgramCode) ? "" : "display: none" %>">
			<div class="section-card primary-card">
				<div class="section-header">
					<i class="ph-chart-line pe-2"></i>
					<h6 class="section-title">Imputed Income Program</h6>
				</div>
				<div class="section-body">

					<!-- Info Alert -->
					<div class="alert alert-info d-flex align-items-start mb-4">
						<i class="ph-info fs-4 me-2"></i>
						<div>
							<strong>About Imputed Income:</strong>
							This program calculates the applicant's income capacity based on CIBIL bureau data
							and scorecard analysis. The imputed income is algorithmically derived from credit behavior patterns.
						</div>
					</div>

					<!-- Calculate Button -->
					<div class="row mb-4">
						<div class="col-lg-12 text-center">
							<button type="button" class="btn btn-primary btn-lg calculate-imputed-income">
								<i class="ph-calculator me-2"></i>Calculate Imputed Income
							</button>
							<div class="mt-2">
                        <span class="badge bg-secondary">
                            <i class="ph-warning-circle me-1"></i>
                            Based on CIBIL data and Scorecard analysis
                        </span>
							</div>
						</div>
					</div>

					<!-- Loading Indicator -->
					<div class="imputed-loading-section" style="display: none;">
						<div class="text-center py-5">
							<div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;">
								<span class="visually-hidden">Loading...</span>
							</div>
							<p class="mt-3 text-muted">Analyzing CIBIL data and calculating imputed income...</p>
							<small class="text-muted">This may take a few moments</small>
						</div>
					</div>

					<!-- Imputed Income Result Section -->
					<div class="imputed-result-section" style="display: none;">

						<!-- Main Result Card -->
						<div class="row">
							<div class="col-lg-12">
								<div class="card imputed-result-card">
									<div class="card-body">
										<div class="row align-items-center">
											<div class="col-md-6">
												<div class="imputed-result-label">
													<i class="ph-currency-circle-dollar fs-3 text-success me-2"></i>
													<div>
														<h6 class="mb-1">Calculated Imputed Income</h6>
														<small class="text-muted">Monthly Income Capacity</small>
													</div>
												</div>
											</div>
											<div class="col-md-6 text-end">
												<h2 class="mb-0 text-success imputed-income-display">₹ 0.00</h2>
												<input type="hidden" name="imputedIncome" class="imputed-income-value" value="">
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>

						<!-- Analysis Details -->
						<div class="row mt-4">
							<div class="col-md-6">
								<div class="card analysis-detail-card">
									<div class="card-body text-center">
										<i class="ph-bank fs-2 text-primary mb-2"></i>
										<h6 class="mb-1">CIBIL Score</h6>
										<h4 class="mb-0 cibil-score-display">-</h4>
										<small class="text-muted cibil-score-band">-</small>
									</div>
								</div>
							</div>
							<div class="col-md-6">
								<div class="card analysis-detail-card">
									<div class="card-body text-center">
										<i class="ph-chart-bar fs-2 text-info mb-2"></i>
										<h6 class="mb-1">Scorecard Rating</h6>
										<h4 class="mb-0 scorecard-rating-display">-</h4>
										<small class="text-muted scorecard-grade">-</small>
									</div>
								</div>
							</div>

						</div>
					</div>

					<!-- No Data Message -->
					<div class="no-imputed-message" style="display: block;">
						<div class="alert alert-warning d-flex align-items-center">
							<i class="ph-warning-circle fs-4 me-2"></i>
							<div>
								Click "Calculate Imputed Income" to analyze CIBIL data and compute income capacity.
							</div>
						</div>
					</div>

				</div>
			</div>
		</div> <!--imputed closed here -->

		<!-- Non-FOIR Program Section -->
		<div class="nonfoirdiv" name="nonfoirdiv" style="<%= "NONFOIR".equals(hidProgramCode) ? "" : "display: none" %>">
			<div class="section-card primary-card">
				<div class="section-header">
					<i class="ph-chart-line-up pe-2"></i>
					<h6 class="section-title">Non-FOIR Program</h6>
				</div>
				<div class="section-body">

					<!-- Info Alert -->
					<div class="alert alert-info d-flex align-items-start mb-4">
						<i class="ph-info fs-4 me-2"></i>
						<div>
							<strong>About Non-FOIR:</strong>
							This program analyzes the customer's bank statement to assess repayment capacity
							without requiring detailed income documentation. Upload the latest 6-month bank statement for analysis.
						</div>
					</div>

					<!-- Bank Statement Section -->
					<div class="section-card info-card mt-3 bank-statement-section">
						<div class="section-header">
							<i class="ph-bank pe-2"></i>
							<h6 class="section-title">Bank Statement Analysis</h6>
						</div>
						<div class="section-body">

							<!-- Alert Info -->
							<div class="alert-card alert-info mb-3">
								<i class="ph-info pe-2"></i>
								<span>Upload the latest 6-month bank statement for analysis</span>
							</div>

							<!-- Date and Bank Selection Row -->
							<div class="row">
								<!-- Start Date -->
								<div class="col-md-4">
									<div class="compact-form-group">
										<label class="form-label">Start Date <span class="text-danger">*</span></label>
										<div class="input-with-icon">
											<i class="ph-calendar input-icon"></i>
											<input type="text"
											       class="form-control bank-start-date nonfoir-start-date"
											       name="nonfoirBankStartDate"
											       placeholder="Select Start Month"
											       readonly
											       value="<%=startDate%>">
										</div>
										<small class="text-muted">Start of 6-month period</small>
									</div>
								</div>

								<!-- End Date -->
								<div class="col-md-4">
									<div class="compact-form-group">
										<label class="form-label">End Date <span class="text-danger">*</span></label>
										<div class="input-with-icon">
											<i class="ph-calendar input-icon"></i>
											<input type="text"
											       class="form-control bank-end-date nonfoir-end-date"
											       name="nonfoirBankEndDate"
											       placeholder="Select End Month"
											       readonly
											       value="<%=endDate%>">
										</div>
										<small class="text-muted">End of 6-month period</small>
									</div>
								</div>

								<!-- Bank Selection -->
								<div class="col-md-4">
									<div class="compact-form-group">
										<label class="form-label">Bank <span class="text-danger">*</span></label>
										<select class="form-control bank-name nonfoir-bank-name" name="nonfoirBankName">
											<option value="">Select Bank</option>
											<option value="52">South Indian Bank, India</option>
											<%
												List<bsaBankDetails> bankDetails = (List<bsaBankDetails>) request.getAttribute("bankDetails");
												if (bankDetails != null) {
													bsaBankDetails southIndianBank = null;
													// Remove South Indian Bank from list to avoid duplicate
													for (int i = 0; i < bankDetails.size(); i++) {
														bsaBankDetails bank = bankDetails.get(i);
														if ("South Indian Bank, India".equals(bank.getName())) {
															southIndianBank = bank;
															bankDetails.remove(i);
															break;
														}
													}

													// Display remaining banks
													for (bsaBankDetails bank : bankDetails) {
														out.print("<option value=\"" + bank.getInstitutionId() + "\"   " +
																(bank.getInstitutionId().equals(bankId) ? "selected" : "") +
																"  >" + bank.getName() + "</option>");
													}
												}
											%>
										</select>
										<small class="text-muted">Select your bank</small>
									</div>
								</div>
							</div>

							<!-- Statement Period Validation Message -->
							<div class="statement-period-validation mt-2" style="display: none;">
								<div class="alert alert-warning d-flex align-items-center">
									<i class="ph-warning-circle me-2"></i>
									<small class="validation-message"></small>
								</div>
							</div>

							<!-- Proceed Button -->
							<div class="text-end mt-3">
								<button type="button" class="btn btn-primary bank-statement-go nonfoir-proceed-btn">
									<i class="ph-arrow-right pe-1"></i> Proceed
								</button>
							</div>

							<!-- BSA Status Display -->
							<div class="bsa-status mt-3"></div>

							<!-- BSA Analysis Result -->
							<div class="itr-bsa-status mt-3 bsaABBDiv nonfoir-bsa-result" data-response-type="BSA-NONFOIR">
								<!-- API response will be displayed here -->
							</div>

						</div>
					</div>

				</div>
			</div>
		</div><!--nonfoir program completes here-->


		<div class="text-end">

			<%--			<button class="btn btn-yellow my-1 me-2 edit-button"><i class="ph-note-pencil  ms-2"></i>Edit</button>--%>
			<button class="btn btn-primary save-button save-button-program">Save<i class="ph-paper-plane-tilt ms-2"></i></button>

		</div>


	</form>
	<div class="modal fade iframe-bsa-modal" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-xl">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">Bank statement analyser</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				</div>
				<div class="modal-body" style="min-height: 500px;">
					<div class="loading-indicator text-center" style="padding: 50px;">
						<div class="spinner-border text-primary" role="status">
							<span class="visually-hidden">Loading...</span>
						</div>
						<p class="mt-2">Loading external content...</p>
					</div>
					<iframe class="itr-iframe" style="display: none; width: 100%; height: 500px; border: none;"></iframe>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade iframe-modal" tabindex="-1" role="dialog">
		<div class="modal-dialog modal-xl">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">External Content</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				</div>
				<div class="modal-body" style="min-height: 500px;">
					<div class="loading-indicator text-center" style="padding: 50px;">
						<div class="spinner-border text-primary" role="status">
							<span class="visually-hidden">Loading...</span>
						</div>
						<p class="mt-2">Loading external content...</p>
					</div>
					<iframe class="itr-iframe" style="display: none; width: 100%; height: 500px; border: none;"></iframe>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade abbCalculationModal" id="abbCalculationModal" tabindex="-1" aria-labelledby="abbCalculationModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl">
			<div class="modal-content">
				<!-- Content will be populated by JavaScript -->
			</div>
		</div>
	</div>

</div>