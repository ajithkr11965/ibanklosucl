<%--
  Created by IntelliJ IDEA.
  User: SIBL18202
  Date: 13-06-2024
  Time: 13:58
  To change this template use File | Settings | File Templates.
--%>
<%
  Boolean checker = request.getAttribute("checker") != null ? request.getAttribute("checker").toString().equals("Y") : false;
%>
<script src="assets/js/custom/WI/eligibilityDetails.js"></script>
<ul class="nav nav-sidebar pb-3" data-nav-type="collapsible" id="ebityDetails">
  <li class="nav-item nav-item-submenu">
    <a href="#" class="nav-link vehicleTab" id="ebityDetailslink">
      <i class="ph-plugs"></i>
      Eligibility Details
    </a>

    <ul class="nav-group-sub collapse p-3" id="eligibilityDetailsContent">
      <!-- Eligibility Details -->
      <li class="nav-item" id="checkEligibilityItems">
        <div class="row">
          <div class="col-lg-4">
            <div class="mb-3 mt-3">
              <div class="row">
                <div class="col-md-12">
                  <div class="fw-semibold">Vehicle Amount</div>
                  <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                    <input type="number" id="vehicle-eligible-amount" class="form-control" readonly>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-lg-4" id="ltv-percent-group" style="display: none;">
            <div class="mb-3 mt-3">
              <div class="row">
                <div class="col-md-12">
                  <div class="fw-semibold">LTV Percent</div>
                  <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                    <input type="number" id="ltv-percent" class="form-control" placeholder="Enter LTV Percent" max="100.00" step="0.01">
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
                    <input type="number" id="ltv-amount" class="form-control" readonly>
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
                    <input type="number" id="requested-loan-amount" class="form-control" readonly>
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
                    <input type="number" id="eligible-tenor" class="form-control" readonly>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-lg-4">
            <div class="text-center mt-3" <%= checker ? "style='display:none'" : "" %>>
              <button type="button" class="btn btn-primary check-eligibility-button">Check Eligibility</button>
            </div>
          </div>
        </div>
      </li>

      <div id="proceedSection">
        <!-- Program Based Eligibility, Eligible Loan Amount, and Card Rate -->
        <li class="nav-item">
          <div class="row">
            <div class="col-lg-6">
              <div class="mb-3 mt-3">
                <div class="row">
                  <div class="col-md-12">
                    <div class="fw-semibold">Program Based Eligibility</div>
                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                      <input type="number" id="program-eligible-amount" class="form-control" readonly>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="col-lg-6">
              <div class="mb-3 mt-3">
                <div class="row">
                  <div class="col-md-12">
                    <div class="fw-semibold">Eligible Loan Amount</div>
                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                      <input type="number" min="100000" id="eligible-loan-amount" class="form-control" readonly>
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
                      <input type="number" id="card-rate" class="form-control" readonly>
                    </div>
                  </div>
                  <div class="col-md-4">
                    <div class="fw-semibold">EMI</div>
                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                      <input type="number" id="emi" class="form-control" readonly>
                    </div>
                  </div>
                  <div class="col-md-4">
                    <div class="text-center mt-3" <%= checker ? "style='display:none'" : "" %>>
                      <button type="button" class="btn btn-primary proceed-button">Proceed</button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </li>
      </div>

    </ul>
  </li>
</ul>
