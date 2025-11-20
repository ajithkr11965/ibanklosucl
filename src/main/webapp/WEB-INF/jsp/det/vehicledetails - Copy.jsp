<%--
  Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 10-05-2024
  Time: 11:28
  To change this template use File | Settings | File Templates.
--%>
<%
  Boolean checker = request.getAttribute("checker")!=null? request.getAttribute("checker").toString().equals("Y"):false;
%>
<script src="assets/js/vendor/forms/selects/select2.min.js"></script>
<script src="assets/demo/pages/form_select2.js"></script>
<script src="assets/js/custom/WI/vehicledetails.js"></script>
<form id="vehicleDetailsForm">
<ul class="nav nav-sidebar" id="vehDetails" data-nav-type="collapsible">
  <li class="nav-item nav-item-submenu">
    <a href="#" class="nav-link vehicleTab" id="vehDetailslink">
      <i class="ph-car"></i>
      Vehicle Details
    </a>

    <ul class="nav-group-sub collapse p-3" id="vehDetailsContent">
      <!-- State and City Dropdowns -->
      <li class="nav-item">
        <div class="row">
          <div class="col-lg-6">
            <div class="mb-3 mt-3">
              <div class="row">
                <div class="col-md-12">
                  <div class="fw-semibold">State</div>
                  <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                    <select id="state" class="form-control select">
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
                    <select id="city" class="form-control select">
                      <option value="" selected disabled>Select a state first</option>
                      <!-- Options will be populated via AJAX -->
                    </select>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </li>

      <li class="nav-item" id="dealer-section">
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
      </li>

      <li class="nav-item" id="dst-section">
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
      </li>

      <li class="nav-item" id="dealer-code-section">
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
      </li>


      <!-- Make Dropdown -->
      <li class="nav-item" id="make-section">
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
      </li>

      <!-- Model Dropdown -->
      <li class="nav-item" id="model-section">
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
      </li>

      <!-- Variant Details Table -->
      <li class="nav-item">
        <div class="row">
          <div class="col-lg-12">
            <div class="mt-3">
              <table class="table table-bordered" id="variant-details">
                <p><small>Amount in Rs</small></p>
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
                <tbody id="variant-details-table">
                <tr>
                  <td><input type="number" step="0.01" name="ex_showroom" id="ex_showroom" class="form-control" ></td>
                  <td><input type="number" step="0.01" name="insurance" id="insurance" class="form-control" ></td>
                  <td><input type="number" step="0.01" name="rto" id="rto" class="form-control" ></td>
                  <td><input type="number" step="0.01" name="warranty" id="warranty" class="form-control" ></td>
                  <td><input type="number" step="0.01" name="other" id="other" class="form-control" ></td>
                  <td><input type="number" step="0.01" name="onroad_price" id="onroad_price" class="form-control" ></td>
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

      </li>

      <li class="nav-item">
        <div class="row">
          <div class="col-lg-4">
            <div class="mb-3 mt-3">
              <div class="row">
                <div class="col-md-12">
                  <div class="fw-semibold">Discount</div>
                  <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                    <input type="number" step="0.01" name="discount" id="discount" class="form-control">
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-lg-4">
            <div class="mb-3 mt-3">
              <div class="row">
                <div class="col-md-12">
                  <div class="fw-semibold">Total Invoice Price</div>
                  <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                    <input type="number" step="0.01" id="total-invoice-price" class="form-control">
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-lg-4">
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
        </div>

      </li>
      <!-- Color and Invoice Date -->
      <li class="nav-item">
        <div class="row">
          <div class="col-lg-6">
            <div class="mb-3 mt-3">
              <div class="row">
                <div class="col-md-12">
                  <div class="fw-semibold">Invoice Number</div>
                  <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                    <input type="text" id="invoice-no" class="form-control" placeholder="Enter Invoice Number">
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
      </li>
      <div class="text-end">
        <input type="hidden" id="ino" value="">
        <input type="hidden" id="winum" value="WI123456789">
        <input type="hidden" id="slno" value="1">
        <input type="hidden" name="appid" data-code="A-1" value="1001">
        <input type="hidden" id="collapseDisable" value="<%=checker?1:0%>">
        <%if(!checker){%>

        <button type="button"  id="vehDetailsEdit" class="btn btn-yellow my-1 me-2 "><i class="ph-note-pencil ms-2"></i>Edit</button>
        <button type="button"  id="vehDetailsSave" class="btn btn-primary ">Save<i class="ph-paper-plane-tilt ms-2"></i></button>
        <%}%>
      </div>
    </ul>

  </li>


</ul>
</form>
