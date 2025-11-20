<%@ page import="com.sib.ibanklosucl.dto.doc.WaiverDto" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.model.EligibilityDetails" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanDetails" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="org.springframework.security.core.parameters.P" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanSubqueueTask" %>
<%@ page import="com.sib.ibanklosucl.model.doc.VehicleLoanChargeWaiver" %>
<%@ page import="java.math.RoundingMode" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script>
  $(document).ready(function () {
      $(':input[readonly]').css({'background-color':'#f6f6f6'});
      function isReqFee(){
          return $('[name="feewaiveRequired"]:checked').val()=='Y';

      }
      $("#feeDetailsForm").validate({
          rules: {
              'feeValueRec': {
                  required:function (){
                      return isReqFee();
                  },
                  number: true,
                  min: 0
              },
              feewaiverRemarks:{
                  required:function (){
                      return isReqFee();
                  },
                  maxlength:150
              },
              feedecision:{
                  required:function (){
                      return isReqFee();
                  }
              }
          },
          messages: {
              feeValueRec: {
                  required: "The recommended amount is mandatory.",
                  number: "Please enter a valid number.",
                  min: "The recommended amount should be greater than or equal to 0."
              }
          },
          ignore: 'input[type=hidden], .select2-search__field',
          highlight: function(element, errorClass) {
              $(element).removeClass('is-invalid').removeClass('is-valid').removeClass('validation-invalid-label');
          },
          unhighlight: function(element, errorClass) {
              $(element).removeClass('validation-invalid-label').removeClass('is-invalid').removeClass('is-valid');
          },
          errorPlacement: function (error, element) {
              element.removeClass('is-valid').addClass('is-invalid');
              error.css({
                  'padding-left': '23px',
                  'margin-right': '20px',
                  'padding-bottom': '2px',
                  'color': 'red',
                  'font-size': 'small'
              });
              error.addClass("validation-invalid-label");
              log(element);
              if(element.hasClass('checkbox-input')){
                  element.parent().parent().parent().append(error);
              }
              else {
                  error.insertAfter(element);
              }
          }
      });
      function isValid() {

          let isValid = true;
          if(isReqFee()) {
              // Loop through each recommended field
              $("input[name='feeValueRec']").each(function () {
                  let recommendedValue = parseFloat($(this).val());
                  let amountValue = parseFloat($(this).closest('tr').find("input[name='feeValue']").val());

                  // Check if the recommended value is a valid number, greater than or equal to 0, and not greater than the amount
                  if (isNaN(recommendedValue) || recommendedValue < 0 || recommendedValue > amountValue) {
                      isValid = false;

                      // Display an error message (can be customized)
                      $(this).next('.error-message').text("Recommended amount must be between 0 and the specified amount.");
                  } else {
                      // Clear any previous error message
                      $(this).next('.error-message').text("");
                  }
              });
          }
          return isValid;
      }

      // Handle save button click
      $("#feesaveBtn").on('click', function (e) {
          e.preventDefault();
          if( isValid() && $("#feeDetailsForm").valid() ){
              var msg='Are you sure you don’t want to opt for the waiver? Proceeding will incur the mentioned processing fee.'
              if($('[name="feewaiveRequired"]:checked').val()=='Y')
                  msg='Are you sure you want to proceed with the processing fee waiver?'
              confirmmsg(msg)
                  .then(function (result) {
                      if (result) {
                          showLoader();
                          // Initialize an array to hold all fee objects
                          let dataList = [];

                          // Loop through each row in your table or list
                          $("#feeDetailsForm table tbody tr").each(function () {
                              let data = {
                                  feeCode: $(this).find(".feeCode").val(),
                                  feeName: $(this).find(".feeName").val(),
                                  feeValue: $(this).find(".feeValue").val(),
                                  feeValueRec: $(this).find(".feeValueRec").val(),
                                  feeWaiverFlag: $(this).find(".feeWaiverFlag").val(),
                                  frequency: $(this).find(".frequency").val()
                              };
                              dataList.push(data); // Add each row's data to the list
                          });


                          var dto = {
                              waiverType: "CHARGE",
                              processFeeWaiverDto: {
                                  decision: $("#feedecision").val(),
                                  feewaiveRequired: $('[name="feewaiveRequired"]:checked').val(),
                                  slno: $('#slno').val(),
                                  wiNum: $('#winum').val(),
                                  feewaiverRemarks: $('#feewaiverRemarks').val(),
                                  feeData: dataList
                              }
                          };
                          $.ajax({
                              url: 'doc/updateWaiver', // Update with your API endpoint
                              type: 'POST',
                              data: JSON.stringify(dto),
                              contentType: 'application/json',
                              success: function (response) {
                                  hideLoader();
                                  if (response.status === 'S') {
                                      notyalt('Record Saved Successfully');
                                  } else {
                                      alertmsg('Failed: ' + response.msg);
                                  }
                              },
                              error: function (xhr, status, error) {
                                  hideLoader();
                                  var err_data = xhr.responseJSON;
                                  if (err_data.msg) {
                                      alertmsgvert(err_data.msg);
                                  } else {
                                      alertmsg('An error occurred: ' + error);
                                  }
                              }
                          });
                      }
                  });}
      });
      $("#feeRecallBtn").on('click', function (e) {
          e.preventDefault();
          confirmmsg("Are you sure you want to recall the Fee Waiver SubTask?")
              .then(function (result) {
                  if (result) {
                              showLoader();
                              var dto = {
                                  waiverType:"CHARGE",
                                  processFeeWaiverDto: {
                                      feewaiveRequired: "RECALL",
                                      slno: $('#slno').val(),
                                      wiNum: $('#winum').val()
                                  }
                              };
                              $.ajax({
                                  url: 'doc/updateWaiver', // Update with your API endpoint
                                  type: 'POST',
                                  data: JSON.stringify(dto),
                                  contentType: 'application/json',
                                  success: function(response) {
                                      hideLoader();
                                      if (response.status === 'S') {
                                          $('input:radio[name="feewaiveRequired"]').filter('[value="N"]').prop('checked', true);
                                          $('.feewaiveRequired').addClass('hide');
                                          notyalt('WI Recalled Successfully');

                                      } else {
                                          alertmsg('Failed: ' + response.msg);
                                      }
                                  },
                                  error: function(xhr, status, error) {
                                      hideLoader();
                                      var err_data = xhr.responseJSON;
                                      if(err_data.msg){
                                          alertmsgvert(err_data.msg);
                                      }
                                      else {
                                          alertmsg('An error occurred: ' + error);
                                      }
                                  }
                              });
                          }
              });
        });

  $('input[type=radio][name=feewaiveRequired]').change(function() {
      if (this.value == 'Y') {
          $('.feewaiveRequired').removeClass('hide');
      }
      else if (this.value == 'N') {
          $('.feewaiveRequired').addClass('hide');
      }
  });

      $("#hisChgviewBtn3").on('click', function (e) {
          var slno = $('#slno').val();
          var waiverType = "CHARGE";
          e.preventDefault();
          $.ajax({
              url: 'doc/waiver-history',
              type: 'POST',
              data: {slno: slno, waiverType: waiverType},
              success: function (response) {
                  var tableBody = $('#chgwaiverHistoryTableBody');
                  tableBody.empty();

                  var headerRow = $('<tr>');
                  if (waiverType === 'ROI') {
                      headerRow.append('<th>Decision</th><th>Requested Spread</th><th>Sanctioned Spread</th><th>Revised ROI</th><th>Revised EMI</th><th>Remarks</th><th>Sanction Remarks</th><th>Last Modified</th><th>Modified By</th>');
                  } else if (waiverType === 'CHARGE') {
                      headerRow.append('<th>Decision</th><th>Fee Code</th><th>Fee Name</th><th>Original Fee</th><th>Recommended Fee</th><th>Sanctioned Fee</th><th>Final Fee</th><th>Waiver Flag</th><th>Remarks</th><th>Sanction Remarks</th><th>Last Modified</th><th>Modified By</th>');
                  }
                  tableBody.append(headerRow);

                  response.forEach(function (waiver) {
                      var row = $('<tr>');
                      if (waiverType === 'ROI') {
                          row.append($('<td>').text(waiver.decision));
                          row.append($('<td>').text(waiver.requestedSpread + '%'));
                          row.append($('<td>').text(waiver.sanctionedSpread + '%'));
                          row.append($('<td>').text(waiver.revisedRoi + '%'));
                          row.append($('<td>').text('₹' + waiver.revisedEmi.toFixed(2)));
                          row.append($('<td>').text(waiver.remarks));
                          row.append($('<td>').text(waiver.sanctionRemarks));
                          row.append($('<td>').text(new Date(waiver.lastModDate).toLocaleString()));
                          row.append($('<td>').text(waiver.lastModUser));
                      } else if (waiverType === 'CHARGE') {
                          row.append($('<td>').text(waiver.decision));
                          row.append($('<td>').text(waiver.feeCode));
                          row.append($('<td>').text(waiver.feeName));
                          row.append($('<td>').text('₹' + (waiver.feeValue != null ? waiver.feeValue.toFixed(2) : '0.00')));
                          row.append($('<td>').text('₹' + (waiver.feeValueRec != null ? waiver.feeValueRec.toFixed(2) : '0.00')));
                          row.append($('<td>').text('₹' + (waiver.feeSancValue != null ? waiver.feeSancValue.toFixed(2) : '0.00')));
                          row.append($('<td>').text('₹' + (waiver.finalFee != null ? waiver.finalFee.toFixed(2) : '0.00')));
                          row.append($('<td>').text(waiver.waiverFlag));
                          row.append($('<td>').text(waiver.remarks));
                          row.append($('<td>').text(waiver.sanctionRemarks));
                          row.append($('<td>').text(new Date(waiver.lastModDate).toLocaleString()));
                          row.append($('<td>').text(waiver.lastModUser));
                      }
                      tableBody.append(row);
                  });

                  $('#chgwaiverHistoryModalLabel').text(waiverType + ' Waiver History');
                  $('#chgwaiverHistoryModal').modal('show');
              },
              error: function (xhr, status, error) {
                  console.error('Error fetching waiver history:', error);
                  alert('Failed to fetch waiver history. Please try again.');
              }
          });
      });

  });


</script>

<div class="flex-stack border rounded sancApprove hide px-7 py-3 mb-2">
  <div class="w-100">
    <div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="processFeeLink" data-bs-target="#processFeeContent">
      <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
             for="processFeeContent">
        <i class="ki-duotone ki-abstract-42 fs-3x me-4">
          <span class="path1"></span>
          <span class="path2"></span>
        </i>
        <span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-4">Processing Fee Waiver</span>
                    <span class="text-muted fw-semibold fs-7">
                     Send WI for Processing Fee Waiver
                    </span>
                </span>
      </label>
    </div>
    <div id="processFeeContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">
    <%   VehicleLoanMaster master= (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
        EligibilityDetails eligibilityDetails= (EligibilityDetails) request.getAttribute("eligibilityDetails");
        List<WaiverDto.ProcessFeeWaiverDto> feeMast= (List<WaiverDto.ProcessFeeWaiverDto>) request.getAttribute("processFeeMast");
        BigDecimal Processfee;
        String hideClass=master.getChargeWaiverRequested()!=null && master.getChargeWaiverRequested()?"":"hide";
    %>
      <div class="row">
        <div class="col-sm-12">
          <!--begin::Repeater-->
          <div id="kt_docs_repeater_basic">
              <form id="feeDetailsForm" name="feeDetailsForm" method="POST">
                  <div class="checkbox-wrapper-16 mb-3 mt-2 text-center">
                      <h5 class="mb-2 text-success"> Whether Processing fee / other charge waiver required?</h5>
                      <div >
                          <label class="checkbox-wrapper">
                              <input class="checkbox-input"  id="Enable" name="feewaiveRequired" type="radio" value="Y" <%=master.getChargeWaiverRequested()!=null && master.getChargeWaiverRequested()?"checked":""%> >
                              <span class="checkbox-tile">
                                                  <span class="checkbox-icon">
                                                  </span>
                                                  <span class="checkbox-label">Yes</span>
                                                </span>
                          </label>
                          <label class="checkbox-wrapper pl-2">
                              <input class="checkbox-input" id="Disable" name="feewaiveRequired" type="radio" value="N"   <%=master.getChargeWaiverRequested()!=null && !master.getChargeWaiverRequested()?"checked":""%>>
                              <span class="checkbox-tile">
                                                  <span class="checkbox-icon">
                                                  </span>
                                                  <span class="checkbox-label">No</span>
                                                </span>
                          </label>
                      </div>
                  </div>



                  <div class="">
<%--                      --%>
                              <div class="col-sm-12 mb-3">
                                  <table class="table table-sm table-bordered table-hover">
                                      <thead>
                                      <tr>
                                          <th>Description</th>
                                          <th>Amount</th>
                                          <th class="feewaiveRequired <%=hideClass%>">Recommended</th>
                                          <th class=" feewaiveRequired <%=hideClass%>">Sanctioned</th>
                                      </tr>
                                      </thead>
                                      <tbody>
                                          <%

                                              List<Map<String, Object>> feeLevels = (List<Map<String, Object>>) request.getAttribute("feeLevels");
                                              String feedecision="",feeRemarks="";
                                          for(WaiverDto.ProcessFeeWaiverDto fee:feeMast){
                                              feeRemarks=fee.getFeewaiverRemarks()==null?"":fee.getFeewaiverRemarks();
                                              feedecision=fee.getDecision()==null?"":fee.getDecision();

                                              Processfee=BigDecimal.ZERO;
                                              if("Y".equalsIgnoreCase(fee.getStaticFixed())){
                                                  Processfee=fee.getValue();
                                              }
                                              if("Y".equalsIgnoreCase(fee.getPercantage())){
                                                  if("Y".equalsIgnoreCase(fee.getLoanAmountPercantage())){
                                                      Processfee=eligibilityDetails.getSancAmountRecommended().multiply(fee.getValue()).divide(new BigDecimal(100),2, RoundingMode.HALF_EVEN);
                                                  }
                                                  if("Y".equalsIgnoreCase(fee.getVehiclePricePercantage())){
                                                      Processfee=eligibilityDetails.getVehicleAmt().multiply(fee.getValue()).divide(new BigDecimal(100),2, RoundingMode.HALF_EVEN);
                                                  }
                                                  //Others TODO
                                              }
                                              if("Y".equalsIgnoreCase(fee.getMaximumLimit())){
                                               Processfee= Processfee.compareTo(fee.getMaximumValue())>0?fee.getMaximumValue():Processfee;
                                              }

                                              String feeValueRec=fee.getFeeWaive()==null?"": fee.getFeeWaive();
                                              String feeSancValueRec=fee.getFeeSancValue()==null?"-":fee.getFeeSancValue();
                                              if(fee.getWaiver().equals("N")) {
                                                  feeValueRec=Processfee.toString();
                                              }

                                              %>
                                                  <tr>
                                                          <td>
                                                              <input type="hidden" class="form-control feeCode" id="feeCode" name="feeCode"  required value="<%=fee.getChargeCode()%>" readonly />
                                                              <input type="hidden" class="form-control feeWaiverFlag" id="feeWaiverFlag" name="feeWaiverFlag"  required value="<%=fee.getWaiver()%>" readonly />
                                                              <input type="hidden" class="form-control feeName" id="feeName" name="feeName"  required value="<%=fee.getChargeName()%>" readonly />
                                                              <input type="hidden" class="form-control frequency" id="frequency" name="frequency"  required value="<%=fee.getFrequency()%>" readonly />
                                                              <%=fee.getChargeName()%>
                                                          </td>
                                                          <td> <input type="hidden" class="form-control feeValue" id="feeValue" name="feeValue"  required value="<%=Processfee%>" readonly />
                                                              <%=Processfee%>
                                                          </td>

                                                          <td class="feewaiveRequired <%=hideClass%>">
                                                              <input type="text" class="form-control feeValueRec" id="feeValueRec" name="feeValueRec"  required value="<%=feeValueRec%>" <%=fee.getWaiver().equals("N")?"readonly":""%> />
                                                              <div class="error-message" style="color: red;"></div>
                                                          </td>
                                                          <td class=" feewaiveRequired <%=hideClass%>">
                                                              <%=feeSancValueRec%>
                                                          </td>
                                                  </tr>
                                                <%
                                          }
                                          %>

                                      </tbody>
                                  </table>
                              </div>
                                  <div class="mb-3 feewaiveRequired <%=hideClass%>">
                                      <label for="feedecision" class="form-label">Decision:</label>
                                      <select type="text"  id="feedecision" name="feedecision"  required value="<%=feedecision%>"  class="form-select" required>
                                          <option value="" selected>Select Decision</option>
                                          <%

                                              for (Map<String, Object> level : feeLevels) {
                                                  String level_val = (String) level.get("LEVEL_NAME");
                                                  String level_desc = (String) level.get("DISPLAY_NAME");
                                          %>
                                          <option value="<%= level_val %>" <%= level_val.equals(feedecision) ? "selected" : "" %>><%= level_desc %>
                                          </option>
                                          <%
                                              }

                                          %>
                                      </select>
                                  </div>
                                  <div class="mb-3 feewaiveRequired <%=hideClass%>">
                                      <label for="feewaiverRemarks" class="form-label">Remarks:
                                          <a href="#" id="hisChgviewBtn3" class="btn btn-lg btn-flex btn-link btn-color-danger">
                                              Waiver History
                                              <i class="ki-duotone ki-arrow-right ms-2 fs-3"><span class="path1"></span><span class="path2"></span></i>				</a>
                                      </label>
                                      <textarea type="text"  id="feewaiverRemarks" name="feewaiverRemarks"   class="form-control" required><%=feeRemarks%></textarea>
                                  </div>
                  </div>
              </form>
              <div class="text-end pt-5">
                  <button type="button" id="feeRecallBtn" class="btn btn-sm btn-danger" data-kt-stepper-action="next">
                      <i class="ki-duotone ki-double-left ">
                          <i class="path1"></i>
                          <i class="path2"></i>
                      </i>
                      Recall
                  </button>

                  <button type="button" id="feesaveBtn" class="btn btn-sm btn-primary" data-kt-stepper-action="next">Save
                      <i class="ki-duotone ki-double-right ">
                          <i class="path1"></i>
                          <i class="path2"></i>
                      </i></button>
              </div>

          </div>
          <!--end::Repeater-->
        </div>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="chgwaiverHistoryModal" tabindex="-1" role="dialog" aria-labelledby="chgwaiverHistoryModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-xl" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="chgwaiverHistoryModalLabel">Waiver History</h5>
                <div class="btn btn-icon btn-sm btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="ki-duotone ki-cross fs-1"><span class="path1"></span><span class="path2"></span></i>
                </div>
            </div>
            <div class="modal-body">
                <table class="table table-striped table-responsive">
                    <thead id="chgwaiverHistoryTableHeader">

                    </thead>
                    <tbody id="chgwaiverHistoryTableBody">
                    <!-- Data will be populated here -->
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
