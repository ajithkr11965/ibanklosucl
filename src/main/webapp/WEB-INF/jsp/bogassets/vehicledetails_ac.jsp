<%@ page import="com.sib.ibanklosucl.model.VehicleLoanVehicle" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %><%--
  Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 10-05-2024
  Time: 11:28
  To change this template use File | Settings | File Templates.
--%>
<%
    String chanel = "";
    VehicleLoanMaster vlmas = (VehicleLoanMaster)request.getAttribute("vehicleLoanMaster");
    if(vlmas!=null)
        chanel = vlmas.getChannel();
    if(chanel==null)
        chanel="";
    Boolean checker = request.getAttribute("checker") != null ? request.getAttribute("checker").toString().equals("Y") : false;
    VehicleLoanVehicle details = (VehicleLoanVehicle) request.getAttribute("vehicleDetails");
    String dealerState = details.getDealerState() != null ? details.getDealerState().toString() : "-";
    String dealerName = details.getDealerName() != null ? details.getDealerName().toString() : "-";
    String dealerCode = details.getDealerCode() != null ? details.getDealerCode().toString() : "-";
    String dealerSubCode = details.getDealerSubCode() != null ? details.getDealerSubCode().toString() : "-";
    String dealerNameRmks = details.getDealerNameRemarks() != null ? details.getDealerSubCode().toString() : "-";
    String dealerCity = details.getDealerCityId() != null ? details.getDealerCityId().toString() : "-";
    String dstCode = details.getDstCode() != null ? details.getDstCode().toString() : "-";
    String dsaCode = details.getDsaCode() != null ? details.getDsaCode().toString() : "-";
    String dealerMake = details.getMakeName() != null ? details.getMakeName().toString() : "-";
    String dealerModel = details.getModelName() != null ? details.getModelName().toString() : "-";
    String dealerVariant = details.getVariantName() != null ? details.getVariantName().toString() : "-";
    String exShowroom = details.getExshowroomPrice() != null ? details.getExshowroomPrice().toString() : "-";
    String insurance = details.getInsurancePrice() != null ? details.getInsurancePrice().toString() : "-";
    String rto = details.getRtoPrice() != null ? details.getRtoPrice().toString() : "-";
    String other = details.getOtherPrice() != null ? details.getOtherPrice().toString() : "-";
    String warranty = details.getExtendedWarranty() != null ? details.getExtendedWarranty().toString() : "-";
    String onroadprice = details.getOnroadPrice() != null ? details.getOnroadPrice().toString() : "-";
    String custom_insurance = details.getCustomInsuranceAmount() != null ? details.getCustomInsuranceAmount().toString() : "-";
    String insurance_company = details.getCustomInsuranceRemarks() != null ? details.getCustomInsuranceRemarks().toString() : "-";
    String discount = details.getDiscountPrice() != null ? details.getDiscountPrice().toString() : "-";
    String total_invoice_price = details.getTotalInvoicePrice() != null ? details.getTotalInvoicePrice().toString() : "-";
    String colour = details.getColour() != null ? details.getColour().toString() : "-";
    String invoice_date = details.getInvoiceDate() != null ? details.getInvoiceDate().toString() : "-";
    String autodealerSourced = details.getAutodealerSourced() != null ? details.getAutodealerSourced().toString() : "-";
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
                                    <input type="text" id="state" class="form-control" value="<%= dealerState %>" readonly disabled>
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
                                    <input type="text" id="city" class="form-control" value="<%= dealerCity %>" readonly disabled>
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
                                    <input type="text" id="dealer_name" class="form-control" value="<%= dealerName %>" readonly disabled>
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
                                    <input type="text" id="dealer_name_remarks" class="form-control" value="<%= dealerNameRmks %>" readonly disabled>
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
                                    <input type="text" id="dst_code" class="form-control" value="<%= dstCode %>" readonly disabled>
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
                                    <input type="text" id="dsa_code" class="form-control" value="<%= dsaCode %>" readonly disabled>
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
                                    <input type="text" id="dealer_code" class="form-control" value="<%= dealerCode %>" readonly disabled>
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
                                    <input type="text" id="dealer_sub_code" class="form-control" value="<%= dealerSubCode %>" readonly disabled>
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
                                    <input type="text" id="make" class="form-control" value="<%= dealerMake %>" readonly disabled>
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
                                    <input type="text" id="model" class="form-control" value="<%=dealerModel  %>" readonly disabled>
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
                                    <input type="text" id="variant" class="form-control" value="<%=dealerVariant  %>" readonly disabled>
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
<%--                                    <select id="channel" class="form-control select">--%>
<%--                                        <option value="">Select</option>--%>
<%--                                        <option value="BRANCH MARKETING" <%if(chanel.equals("BRANCH MARKETING")){%>selected<%}%>>BRANCH MARKETING</option>--%>
<%--                                        <option value="AUTO DEALER"  <%if(chanel.equals("AUTO DEALER")){%>selected<%}%>>AUTO DEALER</option>--%>
<%--                                        <option value="MARKET DSA"  <%if(chanel.equals("MARKET DSA")){%>selected<%}%>>MARKET DSA</option>--%>
<%--                                        <option value="ADVISOR"  <%if(chanel.equals("ADVISOR")){%>selected<%}%>>ADVISOR</option>--%>
<%--                                        <option value="AUTODEALER ASSISTED BY BRANCH"  <%if(chanel.equals("AUTODEALER ASSISTED BY BRANCH")){%>selected<%}%>>AUTODEALER ASSISTED BY BRANCH</option>--%>
<%--                                        <option value="DST SELF SOURCED"  <%if(chanel.equals("DST SELF SOURCED")){%>selected<%}%>>DST SELF SOURCED</option>--%>
<%--                                        <option value="DST PORTAL"  <%if(chanel.equals("DST PORTAL")){%>selected<%}%>>DST PORTAL</option>--%>
<%--                                        <option value="DEALER PORTAL"  <%if(chanel.equals("DEALER PORTAL")){%>selected<%}%>>DEALER PORTAL</option>--%>
<%--                                        <option value="MARKET DSA PORTAL"  <%if(chanel.equals("MARKET DSA PORTAL")){%>selected<%}%>>MARKET DSA PORTAL</option>--%>
<%--                                    </select>--%>
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
                                <td><input type="number" step="0.01" name="ex_showroom" id="ex_showroom" class="form-control" readonly value="<%=exShowroom  %>"></td>
                                <td><input type="number" step="0.01" name="insurance" id="insurance" class="form-control" readonly value="<%=insurance  %>"></td>
                                <td><input type="number" step="0.01" name="rto" id="rto" class="form-control" readonly value="<%=rto  %>"></td>
                                <td><input type="number" step="0.01" name="warranty" id="warranty" class="form-control" readonly value="<%=warranty  %>"></td>
                                <td><input type="number" step="0.01" name="other" id="other" class="form-control" readonly value="<%=other  %>"></td>
                                <td><input type="number" step="0.01" name="onroad_price" id="onroad_price" class="form-control" readonly value="<%=onroadprice  %>"></td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <div class="row custom-insurance-inputs" id="custom-insurance-inputs">
                <div class="row">
                    <div class="col-lg-6">
                        <div class="mb-3 mt-3">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="fw-semibold">Insurance Amount</div>
                                    <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                                        <input type="number" id="custom_insurance_amount" class="form-control mb-2" placeholder="Enter custom insurance amount" readonly value="<%=custom_insurance  %>">
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
                                        <input type="text" id="custom_insurance_remarks" class="form-control" placeholder="Enter Company Name" value="<%=insurance_company  %>" readonly disabled>
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
                                    <input type="number" step="0.01" name="discount" id="discount" class="form-control" value="<%=discount  %>" readonly disabled>
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
                                    <input type="number" step="0.01" id="total-invoice-price" class="form-control" value="<%=total_invoice_price  %>" readonly disabled>
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
                                    <input type="text" id="color" class="form-control" placeholder="Enter color" value="<%=colour  %>" readonly disabled>
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
                                    <input type="date" id="invoice-date" class="form-control" value="<%=invoice_date  %>" readonly disabled>
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
