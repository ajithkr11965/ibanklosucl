<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %><%--
  Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 10-05-2024
  Time: 11:28
  To change this template use File | Settings | File Templates.
--%>
<%

	Boolean checker = request.getAttribute("checker") != null ? request.getAttribute("checker").toString().equals("Y") : false;
	String chanel = "";
	VehicleLoanMaster vlmas = (VehicleLoanMaster)request.getAttribute("vehicleLoanMaster");
	if(vlmas!=null)
		chanel = vlmas.getChannel();
	if(chanel==null)
		chanel="";
%>

<script src="assets/js/custom/WI/vehicledetails.js"></script>


<div class="d-flex flex-stack border rounded px-7 py-3 mb-2">
	<div class="w-100">
		<div class="accordion-header d-flex collapsed" data-bs-toggle="collapse" id="vehDetailslink" data-bs-target="#vehDetailsContent">
			<label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2" for="vehDetailsContent">
				<i class="ki-duotone ki-car-3 fs-3x me-4">
					<span class="path1"></span>
					<span class="path2"></span>
					<span class="path3"></span>
				</i>
				<span class="d-block fw-semibold text-start">
                        <span class="text-gray-900 fw-bold d-block fs-4">

      Vehicle Details
                        </span>
                        <span class="text-muted fw-semibold fs-7 show">
                          Check the PAN,DOB,AADHAAR are in the Blacklist group
                        </span>
                    </span>
			</label>
		</div>

		<div id="vehDetailsContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">
			<!-- State and City Dropdowns -->
			<div class="row">
				<div class="col-lg-6">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="fw-semibold">State</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<select id="state" data-control="select2" class="form-select select">
										<!-- Options will be populated via AJAX -->
									</select>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-6">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="fw-semibold">City</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<select id="city" class="form-select select">
										<option value="" selected disabled>Select a state first</option>
										<!-- Options will be populated via AJAX -->
									</select>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-lg-6">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="fw-semibold">Dealer Name</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<select id="dealer_name" class="form-control select">
										<!-- Options will be populated via AJAX -->
									</select>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-6">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="fw-semibold">Dealer Name Remarks</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="text" id="dealer_name_remarks" class="form-control" placeholder="Enter remarks">
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-lg-6">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="fw-semibold">DST Code</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<select id="dst_code" class="form-control select">
										<option value="" selected disabled>Select a dealer first</option>
										<!-- Options will be populated via AJAX -->
									</select>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-6">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="fw-semibold">DSA Code</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<select id="dsa_code" class="form-control select">
										<option value="" selected disabled>Select a dealer first</option>
										<!-- Options will be populated via AJAX -->
									</select>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-lg-6">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="fw-semibold">Dealer Code</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="text" id="dealer_code" class="form-control">
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-6">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="fw-semibold">Dealer Sub Code</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<select id="dealer_sub_code" class="form-control select">
										<option value="" selected disabled>Select a dealer first</option>
									</select>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>


			<!-- Make Dropdown -->
			<div class="row">
				<div class="col-lg-12">
					<div class="mt-3">
						<div class="row">
							<div class="row col-md-12">
								<div class="fw-semibold">Make</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<select data-placeholder="Select Manufacturer" id="make" class="form-control select">
										<option></option>
										<!-- Options will be populated via AJAX -->
									</select>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>

			<!-- Model Dropdown -->
			<div class="row">
				<div class="col-lg-6">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="row col-md-12">
								<div class="fw-semibold">Model</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<select data-placeholder="Select Model" id="model" class="form-control select">
										<option></option>
										<!-- Options will be populated via AJAX -->
									</select>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-6">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="row col-md-12">
								<div class="fw-semibold">Variant</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<select data-placeholder="Select Variant" id="variant" class="form-control select">
										<option></option>
										<!-- Options will be populated via AJAX -->
									</select>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>

			<!-- Variant Details Table -->

			<div class="row">
				<div class="col-lg-6">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="row col-md-12">
								<div class="fw-semibold">Sourced by Auto dealer & pay out to be issued</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<select id="autodealerSourced" class="form-control select">
										<option value="">Select</option>
										<option value="Y">Yes</option>
										<option value="N">No</option>
									</select>
								</div>
							</div>
						</div>
					</div>
				</div>

				<div class="col-lg-6">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="fw-semibold">Channel</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<select id="channel" class="form-control select">
										<option value="">Select</option>
										<option value="BRANCH MARKETING" <%if(chanel.equals("BRANCH MARKETING")){%>selected<%}%>>BRANCH MARKETING</option>
										<option value="AUTO DEALER"  <%if(chanel.equals("AUTO DEALER")){%>selected<%}%>>AUTO DEALER</option>
										<option value="MARKET DSA"  <%if(chanel.equals("MARKET DSA")){%>selected<%}%>>MARKET DSA</option>
										<option value="ADVISOR"  <%if(chanel.equals("ADVISOR")){%>selected<%}%>>ADVISOR</option>
										<option value="AUTODEALER ASSISTED BY BRANCH"  <%if(chanel.equals("AUTODEALER ASSISTED BY BRANCH")){%>selected<%}%>>AUTODEALER ASSISTED BY BRANCH</option>
										<option value="DST SELF SOURCED"  <%if(chanel.equals("DST SELF SOURCED")){%>selected<%}%>>DST SELF SOURCED</option>
										<option value="DST PORTAL"  <%if(chanel.equals("DST PORTAL")){%>selected<%}%>>DST PORTAL</option>
										<option value="DEALER PORTAL"  <%if(chanel.equals("DEALER PORTAL")){%>selected<%}%>>DEALER PORTAL</option>
										<option value="MARKET DSA PORTAL"  <%if(chanel.equals("MARKET DSA PORTAL")){%>selected<%}%>>MARKET DSA PORTAL</option>
									</select>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-lg-12">
					<div class="mt-3">
						<table class="table table-bordered" id="variant-details">
							<thead>
								<tr>
									<th>Ex-Showroom</th>
									<th>Insurance</th>
									<th>RTO</th>
									<th>Extended Warranty</th>
									<th>Other</th>
									<th>On-Road Price</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td><input type="number" step="0.01" name="ex_showroom" id="ex_showroom" class="form-control" readonly></td>
									<td><input type="number" step="0.01" name="insurance" id="insurance" class="form-control" readonly></td>
									<td><input type="number" step="0.01" name="rto" id="rto" class="form-control" readonly></td>
									<td><input type="number" step="0.01" name="warranty" id="warranty" class="form-control" readonly></td>
									<td><input type="number" step="0.01" name="other" id="other" class="form-control" readonly></td>
									<td><input type="number" step="0.01" name="onroad_price" id="onroad_price" class="form-control" readonly></td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
			<div class="form-check form-switch form-check-reverse text-start mt-2 col-3 mb-2" id="custom_insurance">
				<input type="checkbox" class="form-check-input" id="custom_insurance_checkbox">
				<label class="form-check-label" for="custom_insurance_checkbox">Add custom Insurance amount</label>
			</div>
			<div class="row custom-insurance-inputs" id="custom-insurance-inputs">
				<div class="row">
					<div class="col-lg-6">
						<div class="mb-3 mt-3">
							<div class="row">
								<div class="col-md-12">
									<div class="fw-semibold">Insurance Amount</div>
									<div class="col-lg-12 form-control-feedback form-control-feedback-start">
										<input type="number" id="custom_insurance_amount" class="form-control mb-2" placeholder="Enter custom insurance amount">
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-lg-6">
						<div class="mb-3 mt-3">
							<div class="row">
								<div class="col-md-12">
									<div class="fw-semibold">Insurance Company</div>
									<div class="col-lg-12 form-control-feedback form-control-feedback-start">
										<input type="text" id="custom_insurance_remarks" class="form-control" placeholder="Enter Company Name">
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-lg-6">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="fw-semibold">Discount</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="number" step="0.01" name="discount" id="discount" class="form-control" readonly>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-6">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="fw-semibold">Total Invoice Price</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="number" step="0.01" id="total-invoice-price" class="form-control" readonly>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>

			<!-- Color and Invoice Date -->
			<div class="row">
				<div class="col-lg-6">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="fw-semibold">Color</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="text" id="color" class="form-control" placeholder="Enter color">
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-6">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="fw-semibold">Invoice Date</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="date" id="invoice-date" class="form-control">
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="text-end">

				<input type="hidden" id="collapseDisable" value="<%=checker?1:0%>">
				<%if (!checker) {%>

<%--				<button type="button" id="vehDetailsEdit" class="btn btn-yellow my-1 me-2 "><i class="ph-note-pencil ms-2"></i>Edit</button>--%>
<%--				<button type="button" id="vehDetailsSave" class="btn btn-primary ">Save<i class="ph-paper-plane-tilt ms-2"></i></button>--%>
				<%}%>
			</div>

		</div>
	</div>
</div>
