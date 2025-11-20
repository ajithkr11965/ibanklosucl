<%@ page import="com.sib.ibanklosucl.model.EligibilityDetails" %>
<%
  EligibilityDetails details = (EligibilityDetails) request.getAttribute("eligibilityDetails");
  if (details == null) {
%>
<h2>No eligibility details available</h2>
<%
} else {
  String vehicleAmount = details.getVehicleAmt() != null ? details.getVehicleAmt().toString() : "-";
  String ltvAmount = details.getLtvAmt() != null ? details.getLtvAmt().toString() : "-";
  String loanAmount = details.getLoanAmt() != null ? details.getLoanAmt().toString() : "-";
  String tenor = String.valueOf(details.getTenor());
  String programEligibleAmount = details.getProgramEligibleAmt() != null ? details.getProgramEligibleAmt().toString() : "-";
  String eligibleLoanAmount = details.getEligibleLoanAmt() != null ? details.getEligibleLoanAmt().toString() : "-";
  String cardRate = details.getCardRate() != null ? details.getCardRate().toString() : "-";
  String emi = details.getEmi() != null ? details.getEmi().toString() : "-";
  boolean showLtvPercentGroup = "CUSTOM".equalsIgnoreCase(details.getLtvType());
  String ltvPercent = details.getLtvPer() != null ? details.getLtvPer().toString() : "-";
  String loanAmountRecommended = details.getLoanAmountRecommended()!= null ? String.valueOf(details.getLoanAmountRecommended()) :details.getEligibleLoanAmt().toString();
%>

    <ul class="nav-group-sub collapse p-3 show" id="">
      <!-- Eligibility Details -->
      <li class="nav-item" id="checkEligibilityItems">
        <div class="row">
          <div class="col-lg-4">
            <div class="mb-3 mt-3">
              <div class="row">
                <div class="col-md-12">
                  <div class="fw-semibold">Vehicle Amount</div>
                  <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                    <input type="number" id="vehicle-eligible-amount" class="form-control" value="<%= vehicleAmount %>" readonly>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-lg-4" id="ltv-percent-group" style="<%= showLtvPercentGroup ? "" : "display: none;" %>">
            <div class="mb-3 mt-3">
              <div class="row">
                <div class="col-md-12">
                  <div class="fw-semibold">LTV Percent</div>
                  <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                    <input type="number" id="ltv-percent" class="form-control" value="<%= ltvPercent %>" placeholder="Enter LTV Percent" max="100" step="0.01" disabled>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-lg-4">
            <div class="mb-3 mt-3">
              <div class="row">
                <div class="col-md-12">
                  <div class="fw-semibold">LTV Amount</div>
                  <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                    <input type="number" id="ltv-amount" class="form-control" value="<%= ltvAmount %>" readonly>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-lg-4">
            <div class="mb-3 mt-3">
              <div class="row">
                <div class="col-md-12">
                  <div class="fw-semibold">Requested Loan Amount</div>
                  <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                    <input type="number" id="requested-loan-amount" class="form-control" value="<%= loanAmount %>" readonly>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-lg-4">
            <div class="mb-3 mt-3">
              <div class="row">
                <div class="col-md-12">
                  <div class="fw-semibold">Tenor</div>
                  <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                    <input type="number" id="eligible-tenor" class="form-control" value="<%= tenor %>" readonly>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </li>

      <div id="proceedSection">
        <!-- Program Based Eligibility, Eligible Loan Amount, and Card Rate -->
        <li class="nav-item">
          <div class="row">
            <div class="col-lg-4">
              <div class="mb-3 mt-3">
                <div class="row">
                  <div class="col-md-12">
                    <div class="fw-semibold">Program Based Eligibility</div>
                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                      <input type="number" id="program-eligible-amount" class="form-control" value="<%= programEligibleAmount %>" readonly>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="col-lg-4">
              <div class="mb-3 mt-3">
                <div class="row">
                  <div class="col-md-12">
                    <div class="fw-semibold">Eligible Loan Amount</div>
                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                      <input type="number" id="eligible-loan-amount" class="form-control" value="<%= eligibleLoanAmount %>" readonly>
                    </div>
                  </div>
                </div>
              </div>
            </div>
             <div class="col-lg-4">
                        <div class="mb-3 mt-3">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="fw-semibold">Recommended Loan Amount</div>
                                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                                        <input type="text" id="loan-amount-recommend" class="form-control" value="<%= loanAmountRecommended %>" readonly>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
            <div class="col-lg-12">
              <div class="mb-3 mt-3">
                <div class="row">
                  <div class="col-md-4">
                    <div class="fw-semibold">Card Rate</div>
                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                      <input type="number" id="card-rate" class="form-control" value="<%= cardRate %>" readonly>
                    </div>
                  </div>
                  <div class="col-md-4">
                    <div class="fw-semibold">EMI</div>
                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                      <input type="number" id="emi" class="form-control" value="<%= emi %>" readonly>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

        </li>
      </div>

    </ul>

<%
  }
%>
