<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %><%--
  Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 10-05-2024
  Time: 11:28
  To change this template use File | Settings | File Templates.
--%>
<%
  Boolean checker = request.getAttribute("checker")!=null? request.getAttribute("checker").toString().equals("Y"):false;
  String fileclass="file-input*";
  if(request.getAttribute("init")!=null)
    fileclass="file-input";
  String chanel = "";
  VehicleLoanMaster vlmas = (VehicleLoanMaster)request.getAttribute("vehicleLoanMaster");
  if(vlmas!=null)
    chanel = vlmas.getChannel();
  if(chanel==null)
    chanel="";

%>
<script src="assets/js/vendor/forms/selects/select2.min.js"></script>
<script src="assets/demo/pages/form_select2.js"></script>
<script src="assets/js/custom/WI/vehicledetails.js"></script>
<form id="vehicleDetailsForm">
<%--<ul class="nav nav-sidebar" id="vehDetails" data-nav-type="collapsible">--%>
<%--  <li class="nav-item nav-item-submenu">--%>
<%--    <a href="#" class="nav-link vehicleTab" id="vehDetailslink">--%>
<%--      <i class="ph-car"></i>--%>
<%--      Vehicle Details--%>
<%--    </a>--%>

    <ul class="nav-group-sub  p-3" id="vehDetails"  >
      <!-- State and City Dropdowns -->


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
                  <div class="fw-semibold">Dealer Code</div>
                  <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                    <input type="text" id="dealer_code"  readonly class="form-control" placeholder="Select Dealer Name">
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
                  <div class="fw-semibold">Dealer Sub Code</div>
                  <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                    <select id="dealer_sub_code" class="form-control select">
                      <option value="" selected disabled>Select  dealer Name first</option>
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
                    <input type="text" id="dealer_name_remarks" class="form-control " placeholder="Enter remarks">
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </li>

      <li class="nav-item">
        <div class="row">
          <div class="col-lg-6">
            <div class="mb-3 mt-3">
              <div class="row">
                <div class="col-md-12">
                  <div class="fw-semibold">City</div>
                  <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                    <select id="city" class="form-control select">
                      <option value="" selected disabled>Select Dealer Sub Code first</option>
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
                  <div class="fw-semibold">State</div>
                  <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                    <input type="text" id="state"  readonly class="form-control" placeholder="Select City">
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

      <!-- Make Dropdown -->
      <li class="nav-item" id="acct-section">
        <div class="row">
          <div class="col-lg-12">
            <div class="mt-3">
              <div class="row">
                <div class="row col-md-12">
                  <div class="fw-semibold">Dealer Account Details</div>
                  <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                    <select data-placeholder="Select Manufacturer" id="dealerAccount" class="form-control select" disabled>
                      <option value="">Please Select Account</option>
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


      <li class="nav-item" id="autodealer">
        <div class="row">

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
                    <span id="chennel_err"></span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-lg-6">
            <div class="mb-3 mt-3">
              <div class="row">
                <div class="col-md-12">
                  <div class="fw-semibold">Sourced by Auto dealer & pay out to be issued</div>
                  <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                    <select id="autodealerSourced" class="form-control select">
                      <option value="">Select</option>
                      <option value="Y">Yes</option>
                      <option value="N">No</option>
                    </select>
                    <span id="payout_err"></span>
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
                      <option value="" selected>Select a dealer first</option>
                      <!-- Options will be populated via AJAX -->
                    </select>
                    <span id="dst_err"></span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-lg-6">
            <div class="mb-3 mt-3">
              <div class="row">
                <div class="col-md-12">
                  <div class="fw-semibold">DSA SUB CODE</div>
                  <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                    <select id="dsa_sub_code" class="form-control select">
                      <option value="" selected>Select a dealer first</option>
                      <!-- Options will be populated via AJAX -->
                    </select>
                    <span id="dsa_err"></span>
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
<%--                  <th>Other</th>--%>
                  <th>On-Road Price</th>
                </tr>
                </thead>
                <tbody id="variant-details-table">
                <tr>
                  <td><input type="text"  name="ex_showroom" id="ex_showroom" class="form-control text-only" ></td>
                  <td><input type="text"  name="insurance" id="insurance" class="form-control text-only" ></td>
                  <td><input type="text"  name="rto" id="rto" class="form-control text-only" ></td>
                  <td><input type="text"  name="warranty" id="warranty" class="form-control text-only" ></td>
<%--                  <td></td>--%>
                  <td><input type="hidden"  name="other" id="other" class="form-control text-only" >
                    <input type="text"  name="onroad_price" id="onroad_price" class="form-control text-only" >
                  </td>
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
                      <input type="text" id="custom_insurance_amount" class="form-control mb-2 text-only" placeholder="Enter custom insurance amount">
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
                    <input type="text"  name="discount" id="discount" class="form-control text-only">
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
                    <input type="number" readonly id="total-invoice-price" class="form-control">
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
                    <input type="date" id="invoice-date" class="form-control" max="<%=new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date())%>">
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </li>
      <li class="nav-item">
        <div class="row">
          <div class="col-lg-6">
            <div class="border-bottom pb-2 mb-2">
              <span class="fw-bold">Invoice Document </span> <code>(Mandatory)</code>
            </div>
            <input type="file" class="file-input base64file invoiceDoc" name='invoiceDoc' id="invoiceDoc"  data-filedesc="UPLOAD Invoice Document" data-show-upload="false" data-show-remove="false" data-filebase64="" data-filebase64ext="">
            <input type="hidden" name="fileup" id="fileup" class="fileup" value="">
            <input type="hidden" name="fileupext"  id="fileupext" class="fileupext" value="">
            </div>
            <span id="fileuploadedspan" class="text-success"></span>
        </div>
      </li>

      <div class="text-end">
        <input type="hidden" id="ino" value="">
        <input type="hidden" id="collapseDisable" value="<%=checker?1:0%>">
        <%if(!checker){%>

        <button type="button"  id="vehDetailsEdit" class="btn btn-yellow my-1 me-2 comon"><i class="ph-note-pencil ms-2"></i>Edit</button>
        <button type="button"  id="vehDetailsSave" class="btn btn-primary comon">Save<i class="ph-paper-plane-tilt ms-2"></i></button>
        <%}%>
      </div>
    </ul>

<%--  </li>--%>


<%--</ul>--%>
</form>
