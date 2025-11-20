<%@ page import="com.sib.ibanklosucl.model.VehicleLoanVehicle" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%
  VehicleLoanVehicle details = (VehicleLoanVehicle) request.getAttribute("vehicleDetails");
  VehicleLoanMaster vlmasdetails = (VehicleLoanMaster)request.getAttribute("vehicleLoanMaster");
  String chanel = "";
  if(vlmasdetails!=null)
    chanel = vlmasdetails.getChannel();
  if(chanel==null)
    chanel="";
  if (details == null) {
%>
<h2>No vehicle details available</h2>
<%
} else {
  String dealerState = details.getDealerState() != null ? details.getDealerState() : "Not Available";
  String dealerCityId = details.getDealerCityId() != null ? details.getDealerCityId() : "Not Available";
  String dealerCityName = details.getDealerCityName() != null ? details.getDealerCityName() : "Not Available";
  String dealerName = details.getDealerName() != null ? details.getDealerName() : "Not Available";
  String dealerNameRemarks = details.getDealerNameRemarks() != null ? details.getDealerNameRemarks() : "-";
  String dstCode = details.getDstCode() != null ? details.getDstCode() : "Not Available";
  String dsaCode = details.getDsaCode() != null ? details.getDsaCode() : "Not Available";
  String dealerCode = details.getDealerCode() != null ? details.getDealerCode() : "-";
  String dealerSubCode = details.getDealerSubCode() != null ? details.getDealerSubCode() : "-";
  String makeId = details.getMakeId() != null ? details.getMakeId() : "Not Available";
  String makeName = details.getMakeName() != null ? details.getMakeName() : "Not Available";
  String modelId = details.getModelId() != null ? details.getModelId() : "Not Available";
  String modelName = details.getModelName() != null ? details.getModelName() : "Not Available";
  String variantId = details.getVariantId() != null ? details.getVariantId() : "Not Available";
  String variantName = details.getVariantName() != null ? details.getVariantName() : "Not Available";
  String exshowroomPrice = details.getExshowroomPrice() != null ? details.getExshowroomPrice() : "-";
  String insurancePrice = details.getInsurancePrice() != null ? details.getInsurancePrice() : "-";
  String rtoPrice = details.getRtoPrice() != null ? details.getRtoPrice() : "-";
  String extendedWarranty = details.getExtendedWarranty() != null ? details.getExtendedWarranty() : "-";
  String otherPrice = details.getOtherPrice() != null ? details.getOtherPrice() : "-";
  String dealerBank = details.getDealerBank() != null ? details.getDealerBank() : "";
  String dealerIfsc = details.getDealerIfsc() != null ? details.getDealerIfsc() : "";
  String dealerAccount = details.getDealerAccount() != null ? details.getDealerAccount() : "";


  String onroadPrice = details.getOnroadPrice() != null ? details.getOnroadPrice() : "-";
  Boolean customInsurance = details.getCustomInsurance() != null && details.getCustomInsurance();
  String customInsuranceAmount = details.getCustomInsuranceAmount() != null ? details.getCustomInsuranceAmount() : "-";
  String customInsuranceRemarks = details.getCustomInsuranceRemarks() != null ? details.getCustomInsuranceRemarks() : "-";
  String discountPrice = details.getDiscountPrice() != null ? details.getDiscountPrice() : "-";
  String totalInvoicePrice = details.getTotalInvoicePrice() != null ? details.getTotalInvoicePrice() : "-";
  String color = details.getColour() != null ? details.getColour() : "-";
  String invoiceDate = details.getInvoiceDate() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(details.getInvoiceDate()) : "-";
  String autodealerSourced = details.getAutodealerSourced() != null ? details.getAutodealerSourced().toString() : "-";
%>

<script src="assets/js/custom/WI/vehicledetails.js"></script>
<script>

  $('#dealer_code').prop('disabled', true);
  $('#dealer_code').prop('readonly', true);
</script>


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
      <ul class="nav-group-sub collapse p-3 show" id="">
        <!-- Dealer Section -->
        <li class="nav-item" id="dealer-section">
          <div class="row">
            <div class="col-lg-6">
              <div class="mb-3 mt-3">
                <div class="row">
                  <div class="col-md-12">
                    <div class="fw-semibold">Dealer Name</div>
                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                      <select id="dealer_name" class="form-control select" disabled>
                        <option value="<%= dealerName %>"><%= dealerName %></option>
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
                    <div class="fw-semibold">Dealer Sub Code</div>
                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                      <input value="<%= dealerSubCode %>" name="dealerSub">
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </li>

        <!-- Dealer Code Section -->
        <li class="nav-item" id="dealer-code-section">
          <div class="row">
            <div class="col-lg-6">
              <div class="mb-3 mt-3">
                <div class="row">
                  <div class="col-md-12">
                    <div class="fw-semibold">Dealer Code</div>
                    <div class="col-lg-12 form-control-feedback form-control-feedback-start" >
                      <input type="text" id="dealer_code" class="form-control" readonly value="<%= dealerCode %>" disabled>
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
                      <input type="text" id="dealer_name_remarks" disabled class="form-control" value="<%= dealerNameRemarks %>" placeholder="Enter remarks">
                    </div>
                  </div>
                </div>
              </div>
            </div>

          </div>
        </li>

        <!-- State and City Dropdowns -->
        <li class="nav-item">
          <div class="row">

            <div class="col-lg-6">
              <div class="mb-3 mt-3">
                <div class="row">
                  <div class="col-md-12">
                    <div class="fw-semibold">City</div>
                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                      <select id="city" class="form-control select" disabled>
                        <option value="<%= dealerCityId %>"><%= dealerCityName %></option>
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
                      <select id="state" class="form-control select" disabled>
                        <option value="<%= dealerState %>"><%= dealerState %></option>
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
                      <select data-placeholder="Select Manufacturer" id="make" class="form-control select" disabled>
                        <option value="<%= makeId %>"><%= makeName %></option>
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
                    <div class="fw-semibold">Dealer Account</div>
                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                      <select data-placeholder="Select Manufacturer" id="dealerAccount" class="form-control select" disabled>
                        <option value="<%= dealerAccount %>|<%= dealerIfsc %>|<%= dealerBank %>">ACCOUNT : <%= dealerAccount %> (IFSC : <%= dealerIfsc %> -  BANK :<%= dealerBank %>)</option>
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
                      <select data-placeholder="Select Model" id="model" class="form-control select" disabled>
                        <option value="<%= modelId %>"><%= modelName %></option>
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
                      <select data-placeholder="Select Variant" id="variant" class="form-control select" disabled>
                        <option value="<%= variantId %>"><%= variantName %></option>
                      </select>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </li>

        <!-- Variant Details Table -->



        <!-- DST and DSA Section -->
        <li class="nav-item" id="dst-section">
          <div class="row">
            <div class="col-lg-6">
              <div class="mb-3 mt-3">
                <div class="row">
                  <div class="col-md-12">
                    <div class="fw-semibold">DST Code</div>
                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                      <select id="dst_code" class="form-control select" disabled>
                        <option value="<%= dstCode %>"><%= dstCode %></option>
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
                      <select id="dsa_code" class="form-control select" disabled>
                        <option value="<%= dsaCode %>"><%= dsaCode %></option>
                      </select>
                    </div>
                  </div>


                </div>
              </div>
            </div>
          </div>
        </li>


        <li class="nav-item" id="Autodealer">
          <div class="row">
            <div class="col-lg-6">
              <div class="mb-3 mt-3">
                <div class="row">
                  <div class="col-md-12">
                    <div class="fw-semibold">Sourced by Auto dealer & pay out to be issued</div>
                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                      <input type="text" id="autodealerSourced" class="form-control" <%if(autodealerSourced.equals("Y")){%> value="YES" <%}else if(autodealerSourced.equals("N")){%> value="NO" <%}else{%> value="NA" <%}%> readonly disabled>
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
                      <input type="text" id="channel" class="form-control" value="<%=chanel%>" readonly disabled>
                      <%--                    <select id="channel" class="form-control select">--%>
                      <%--                      <option value="">Select</option>--%>
                      <%--                      <option value="BRANCH MARKETING" <%if(chanel.equals("BRANCH MARKETING")){%>selected<%}%>>BRANCH MARKETING</option>--%>
                      <%--                      <option value="AUTO DEALER"  <%if(chanel.equals("AUTO DEALER")){%>selected<%}%>>AUTO DEALER</option>--%>
                      <%--                      <option value="MARKET DSA"  <%if(chanel.equals("MARKET DSA")){%>selected<%}%>>MARKET DSA</option>--%>
                      <%--                      <option value="ADVISOR"  <%if(chanel.equals("ADVISOR")){%>selected<%}%>>ADVISOR</option>--%>
                      <%--                      <option value="AUTODEALER ASSISTED BY BRANCH"  <%if(chanel.equals("AUTODEALER ASSISTED BY BRANCH")){%>selected<%}%>>AUTODEALER ASSISTED BY BRANCH</option>--%>
                      <%--                      <option value="DST SELF SOURCED"  <%if(chanel.equals("DST SELF SOURCED")){%>selected<%}%>>DST SELF SOURCED</option>--%>
                      <%--                      <option value="DST PORTAL"  <%if(chanel.equals("DST PORTAL")){%>selected<%}%>>DST PORTAL</option>--%>
                      <%--                      <option value="DEALER PORTAL"  <%if(chanel.equals("DEALER PORTAL")){%>selected<%}%>>DEALER PORTAL</option>--%>
                      <%--                      <option value="MARKET DSA PORTAL"  <%if(chanel.equals("MARKET DSA PORTAL")){%>selected<%}%>>MARKET DSA PORTAL</option>--%>
                      <%--                    </select>--%>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </li>



        <li class="nav-item">
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
                    <%--                  <th>Other</th>--%>
                    <th>On-Road Price</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr>
                    <td><input type="number" step="0.01" disabled name="ex_showroom" id="ex_showroom" class="form-control" value="<%= exshowroomPrice %>" readonly></td>
                    <td><input type="number" step="0.01" disabled name="insurance" id="insurance" class="form-control" value="<%= insurancePrice %>" readonly></td>
                    <td><input type="number" step="0.01" disabled name="rto" id="rto" class="form-control" value="<%= rtoPrice %>" readonly></td>
                    <td><input type="number" step="0.01" disabled name="warranty" id="warranty" class="form-control" value="<%= extendedWarranty %>" readonly></td>
                    <%--                  <td><input type="hidden" step="0.01" disabled name="other" id="other" class="form-control" value="<%= otherPrice %>" readonly></td>--%>
                    <td><input type="number" step="0.01" disabled name="onroad_price" id="onroad_price" class="form-control" value="<%= onroadPrice %>" readonly></td>
                  </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <div class="form-check form-switch form-check-reverse text-start mt-2 col-3 mb-2" id="custom_insurance">
            <input type="checkbox" disabled class="form-check-input" id="custom_insurance_checkbox" <%= customInsurance ? "checked" : "" %>>
            <label class="form-check-label" for="custom_insurance_checkbox">Add custom Insurance amount</label>
          </div>

          <div class="row custom-insurance-inputs" id="custom-insurance-inputs" style="<%= customInsurance ? "" : "display: none;" %>">
            <div class="row">
              <div class="col-lg-6">
                <div class="mb-3 mt-3">
                  <div class="row">
                    <div class="col-md-12">
                      <div class="fw-semibold">Insurance Amount</div>
                      <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                        <input type="number" id="custom_insurance_amount" disabled class="form-control mb-2" value="<%= customInsuranceAmount %>" placeholder="Enter custom insurance amount">
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
                        <input type="text" id="custom_insurance_remarks" class="form-control" value="<%= customInsuranceRemarks %>" placeholder="Enter Company Name">
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

        </li>

        <!-- Discount and Invoice Price -->
        <li class="nav-item">
          <div class="row">
            <div class="col-lg-6">
              <div class="mb-3 mt-3">
                <div class="row">
                  <div class="col-md-12">
                    <div class="fw-semibold">Discount</div>
                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                      <input type="number" step="0.01" disabled name="discount" id="discount" class="form-control" value="<%= discountPrice %>" readonly>
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
                      <input type="number" step="0.01" disabled id="total-invoice-price" class="form-control" value="<%= totalInvoicePrice %>" readonly>
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
                    <div class="fw-semibold">Color</div>
                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                      <input type="text" id="color" disabled class="form-control" value="<%= color %>" placeholder="Enter color">
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
                      <input type="date" id="invoice-date" disabled class="form-control" value="<%= invoiceDate %>">
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </li>
      </ul>

    </div>
  </div>
</div>
<%
  }
%>

