<%--
  Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 10-05-2024
  Time: 11:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanDetails" %><%
    VehicleLoanDetails details = (VehicleLoanDetails) request.getAttribute("loanDetails");
    if (details == null) {
%>
<h2>No loan details available</h2>
<%
} else {
    String vehicleAmount = details.getVehicleAmt() != null ? details.getVehicleAmt().toString() : "-";
    String loanAmount = details.getLoanAmt() != null ? details.getLoanAmt().toString() : "-";
    String tenor = String.valueOf(details.getTenor());
    String roiType = details.getRoiType() != null ? details.getRoiType() : "-";
    String foirType = details.getFoirType() != null ? details.getFoirType() : "-";
%>

<ul class="nav-group-sub p-3">

    <div class="row">
        <div class="col-lg-6">
            <div class="mb-2 mt-2">
                <div class="row">
                    <div class="col-md-12">
                        <div class="fw-semibold">Vehicle Amount</div>
                        <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                            <input type="number" id="vehicle-amount" class="form-control" value="<%= vehicleAmount %>" readonly disabled>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-lg-6">
            <div class="mb-2 mt-2">
                <div class="row">
                    <div class="col-md-12">
                        <div class="fw-semibold">Requested Loan Amount</div>
                        <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                            <input type="number" id="loan-amount" class="form-control" value="<%= loanAmount %>" placeholder="Enter loan amount" disabled>
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
                        <div class="fw-semibold">Tenor in Months</div>
                        <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                            <input type="number" id="tenor" class="form-control" value="<%= tenor %>" placeholder="Enter tenor in months" disabled>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-lg-3">
            <div class="mb-3 mt-3">
                <div class="row">
                    <div class="col-md-12">
                        <div class="fw-semibold">ROI Type</div>
                        <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="roiType" id="roi-fixed"  value="fixed" disabled <%= "fixed".equalsIgnoreCase(roiType) ? "checked" : "" %>>
                                <label class="form-check-label" for="roi-fixed">Fixed</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="roiType" id="roi-floating" value="floating" disabled <%= "floating".equalsIgnoreCase(roiType) ? "checked" : "" %>>
                                <label class="form-check-label" for="roi-floating">Floating</label>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-lg-3">
            <div class="mb-3 mt-3">
                <div class="row">
                    <div class="col-md-12">
                        <div class="fw-semibold">FOIR Type</div>
                        <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                            <div class="form-check mb-2">
                                <input class="form-check-input" disabled type="radio" name="foirType" id="foir-foir" value="foir" <%= "Y".equalsIgnoreCase(foirType) ? "checked" : "" %>>
                                <label class="form-check-label" for="foir-foir">FOIR</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" disabled type="radio" name="foirType" id="foir-non-foir" value="non-foir" <%= "N".equalsIgnoreCase(foirType) ? "checked" : "" %>>
                                <label class="form-check-label" for="foir-non-foir">Non FOIR</label>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>



</ul>




<%
    }
%>
