<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.model.EligibilityDetails" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="javax.persistence.Column" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanDetails" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanSubqueueTask" %>
<%@ page import="com.sib.ibanklosucl.model.doc.VehicleLoanRoiWaiver" %>
<%@ page import="java.util.Optional" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script>
    function isReq(){
        return $('[name="roiRequired"]:checked').val()=='Y';

    }
  $(document).ready(function () {



      // Initialize form validation
      $("#roiDetailsForm").validate({
          rules: {
              'roiRequired':{
                  required:true
              },
              roisancAmt: {
                  required:function (){
                      return isReq();
                  },
                  twoDecimalPlaces:true
              },
              roiSancRoi: {
                  required:function (){
                      return isReq();
                  },
                  twoDecimalPlaces:true
              },
              roiSancTenor: {
                  required:function (){
                      return isReq();
                  },
                  twoDecimalPlaces:true
              },
              roiSancemi: {
                  required:function (){
                      return isReq();
                  },
                  twoDecimalPlaces:true
              },
              roiebr: {
                  required:function (){
                      return isReq();
                  },
                  twoDecimalPlaces:true
              },
              roioperationalCost: {
                  required:function (){
                      return isReq();
                  },
                  twoDecimalPlaces:true
              },
              roicrp: {
                  required:function (){
                      return isReq();
                  },
                  twoDecimalPlaces:true
              },
              roispread: {
                  required:function (){
                      return isReq();
                  },
                  twoDecimalPlaces:true
              },
              roibaseSpread: {
                  required:function (){
                      return isReq();
                  },
                  twoDecimalPlaces:true
              },
              revisedRoi: {
                  required:function (){
                      return isReq();
                  },
                  numberOnly:true
              },

              revisedEmi: {
                  required:function (){
                      return isReq();
                  },
                  numberOnly:true
              },
              roidecision: {
                  required:function (){
                      return isReq();
                  }
              },
              roiwaiverRemarks: {
                  required:function (){
                      return isReq();
                  },
                  maxlength:150
              }
          },
          messages: {

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
      $.validator.addMethod("regex", function(value, element, regexpr) {
          return regexpr.test(value);
      }, "Invalid format");
      jQuery.validator.addMethod("numberOnly", function(value, element) {
          return this.optional(element) || /^\d+(\.\d{1,2})?$/.test(value);
      }, "Please enter a positive valid number .");
      jQuery.validator.addMethod("twoDecimalPlaces", function(value, element) {
          return this.optional(element) || /^-?\d+(\.\d{1,2})?$/.test(value);
      }, "Please enter a valid number with up to two decimal places.");


      // Handle save button click
      $("#roisaveBtn").on('click', function (e) {
          e.preventDefault();
          if($("#roiDetailsForm").valid()) {
              var msg = 'Are you sure you don’t want to opt for the waiver? Proceeding will incur the mentioned ROI.'
              if ($('[name="roiRequired"]:checked').val() == 'Y')
                  msg = 'Are you sure you want to proceed with the ROI waiver?'
              confirmmsg(msg)
                  .then(function (result) {
                      if (result) {
                          showLoader();
                          var dto = {
                              waiverType: "ROI",
                              roidto: {
                                  sancAmt: $("#roisancAmt").val(),
                                  sancRoi: $("#roiSancRoi").val(),
                                  sanctenor: $("#roiSancTenor").val(),
                                  sancemi: $("#roiSancemi").val(),
                                  ebr: $("#roiebr").val(),
                                  operationalCost: $("#roioperationalCost").val(),
                                  crp: $("#roicrp").val(),
                                  spread: $("#roispread").val(),
                                  baseSpread: $("#roibaseSpread").val(),
                                  revisedRoi: $("#revisedRoi").val(),
                                  revisedEmi: $("#revisedEmi").val(),
                                  decision: $("#roidecision").val(),
                                  stp: $("#roiSTP").val(),
                                  roiType: $("#roiRoitype").val(),
                                  roiwaiveRequired: $('[name="roiRequired"]:checked').val(),
                                  slno: $('#slno').val(),
                                  wiNum: $('#winum').val(),
                                  initialRoi:$('#initailROI').val(),
                                  roiwaiverRemarks: $('#roiwaiverRemarks').val()
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
                  });
          }
      });
      $("#roiRecallBtn").on('click', function (e) {
          e.preventDefault();
          confirmmsg("Are you sure you want to recall the ROI Waiver SubTask?")
              .then(function (result) {
                  if (result) {
                      showLoader();
                      var dto = {
                          waiverType: "ROI",
                          roidto: {
                              roiwaiveRequired: "RECALL",
                              slno: $('#slno').val(),
                              wiNum: $('#winum').val()
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
                                  $('input:radio[name="roiRequired"]').filter('[value="N"]').prop('checked', true);
                                  $('.roiwaiveRequired').addClass('hide');
                                  notyalt('WI Recalled Successfully');

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
              });
      });


  $('#roibaseSpread').on('change', function(e) {
      e.preventDefault();
      e.stopPropagation();
      var spread=$(this);
      if(spread.val().length>=0) {
          showLoader();
          $.ajax({
              url: 'doc/getRoi',
              type: 'POST',
              data:  JSON.stringify({
                  ebr: $("#roiebr").val(),
                  operationalCost: $("#roioperationalCost").val(),
                  crp: $("#roicrp").val(),
                  spread: $("#roispread").val(),
                  baseSpread: $("#roibaseSpread").val(),
                  sanctenor: $("#roiSancTenor").val(),
                  sancAmt: $("#roisancAmt").val()
              }),
              contentType: 'application/json',
              success: function (response) {
                  if (response.status === 'S') {
                     $("#revisedRoi").val(response.revisedRoi)
                      $("#revisedEmi").val(response.revisedEmi)
                  } else {
                      alertmsg(response.msg);
                  }
                  hideLoader();
              },
              error: function (xhr, status, error) {
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

      $("#hisviewBtn").on('click', function (e) {
          var slno = $('#slno').val();
          var waiverType = "ROI";
          e.preventDefault();
          $.ajax({
              url: 'doc/waiver-history',
              type: 'POST',
              data: {slno: slno, waiverType: waiverType},
              success: function (response) {
                  var tableHeader = $('#roiwaiverHistoryTableHeader');
                  var tableBody = $('#roiwaiverHistoryTableBody');
                  tableHeader.empty();
                  tableBody.empty();

                  var headerRow = $('<tr>');
                  if (waiverType === 'ROI') {
                      headerRow.append('<th>Decision</th><th>Requested Spread</th><th>Sanctioned Spread</th><th>Last Modified</th><th>Modified By</th><th>Status</th>');
                  } else if (waiverType === 'CHARGE') {
                      headerRow.append('<th>Decision</th><th>Fee Code</th><th>Fee Name</th><th>Original Fee</th><th>Recommended Fee</th><th>Sanctioned Fee</th><th>Final Fee</th><th>Waiver Flag</th><th>Remarks</th><th>Sanction Remarks</th><th>Last Modified</th><th>Modified By</th>');
                  }
                  tableHeader.append(headerRow);

                  response.forEach(function (waiver, index) {
                      var row = $('<tr onclick="toggleExpand(this)">');
                      if (waiverType === 'ROI') {
                          row.append($('<td>').text(waiver.decision));
                          row.append($('<td>').text(waiver.requestedSpread + '%'));
                          row.append($('<td>').text(waiver.sanctionedSpread + '%'));
                          row.append($('<td>').text(new Date(waiver.lastModDate).toLocaleString()));
                          row.append($('<td>').text(waiver.lastModUser));
                          row.append($('<td>').text(waiver.taskStatus));
                      } else if (waiverType === 'CHARGE') {
                          row.append($('<td>').text(waiver.decision));
                          row.append($('<td>').text(waiver.feeCode));
                          row.append($('<td>').text(waiver.feeName));
                          row.append($('<td>').text('₹' + waiver.feeValue.toFixed(2)));
                          row.append($('<td>').text('₹' + waiver.feeValueRec.toFixed(2)));
                          row.append($('<td>').text('₹' + waiver.feeSancValue.toFixed(2)));
                          row.append($('<td>').text('₹' + waiver.finalFee.toFixed(2)));
                          row.append($('<td>').text(waiver.waiverFlag));
                          row.append($('<td>').text(waiver.remarks));
                          row.append($('<td>').text(waiver.sanctionRemarks));
                          row.append($('<td>').text(new Date(waiver.lastModDate).toLocaleString()));
                          row.append($('<td>').text(waiver.lastModUser));
                      }
                      tableBody.append(row);

                      // Create an expandable row with icons and parallel layout
                      var expandableRow = $('<tr class="expandable-content">');
                      var expandableCell = $('<td colspan="12">');
                      var cardBody = $('<div class="card card-body">');

                      var detailsRow = $('<div class="row">');
                      detailsRow.append('<div class="col-md-4"><p><i class="fas fa-user-tie details-icon"></i><strong>Decision:</strong> ' + waiver.decision + '</p><p><i class="fas fa-calendar-alt details-icon"></i><strong>Modified Date:</strong> ' + new Date(waiver.lastModDate).toLocaleString() + '</p></div>');
                      detailsRow.append('<div class="col-md-4"><p><i class="fas fa-user-tie details-icon"></i><strong>Decision:</strong> ' + waiver.decision + '</p><p><i class="fas fa-calendar-alt details-icon"></i><strong>Sanctioned User:</strong> ' + waiver.completedUser + '</p></div>');
                      detailsRow.append('<div class="col-md-4"><p><i class="fas fa-percentage details-icon"></i><strong>Requested Spread:</strong> ' + waiver.requestedSpread + '%</p><p><i class="fas fa-percentage details-icon"></i><strong>Sanctioned Spread:</strong> ' + waiver.sanctionedSpread + '%</p></div>');
                      detailsRow.append('<div class="col-md-4"><p><i class="fas fa-money-bill-wave details-icon"></i><strong>Revised ROI:</strong> ' + waiver.revisedRoi + '%</p><p><i class="fas fa-money-bill-wave details-icon"></i><strong>Revised EMI:</strong> ₹' + waiver.revisedEmi.toFixed(2) + '</p></div>');

                      cardBody.append(detailsRow);
                      cardBody.append('<div class="remarks"><strong>Branch Remarks:</strong> ' + waiver.remarks + '</div>');
                      cardBody.append('<div class="remarks"><strong>Sanction Remarks:</strong> ' + waiver.sanctionRemarks + '</div>');

                      expandableCell.append(cardBody);
                      expandableRow.append(expandableCell);
                      tableBody.append(expandableRow);
                  });

                  $('#roiwaiverHistoryModalLabel').text(waiverType + ' Waiver History');
                  $('#roiwaiverHistoryModal').modal('show');
              },
              error: function (xhr, status, error) {
                  console.error('Error fetching waiver history:', error);
                  alert('Failed to fetch waiver history. Please try again.');
              }
          });

      });


      $('input[type=radio][name=roiRequired]').change(function() {
          if (this.value == 'Y') {
              $('.roiwaiveRequired').removeClass('hide');
          }
          else if (this.value == 'N') {
              $('.roiwaiveRequired').addClass('hide');
          }
      });
  });


</script>

<div class="flex-stack border rounded sancApprove hide px-7 py-3 mb-2">
  <div class="w-100">
    <div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="roiLink" data-bs-target="#roiContent">
      <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
             for="roiContent">
        <i class="ki-duotone ki-ranking fs-3x me-4">
          <span class="path1"></span>
          <span class="path2"></span>
        </i>
        <span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-4">ROI Waiver</span>
                    <span class="text-muted fw-semibold fs-7">
                     Send WI for ROI Waiver
                    </span>
                </span>
      </label>
    </div>
      <%
          VehicleLoanMaster master= (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
          EligibilityDetails eligibilityDetails= (EligibilityDetails) request.getAttribute("eligibilityDetails");
          VehicleLoanDetails loanDetails= (VehicleLoanDetails) request.getAttribute("loanDetails");
          String latestROIWaiver = (String) request.getAttribute("initalROI");
          String roiSancRoi="",roiSanctenor="",roiSancemi="",initailROI="";
          String revisedRoi="",revisedEmi="",roidecision="",roiRemarks="";
          BigDecimal operationalCost=BigDecimal.ZERO,crp=BigDecimal.ZERO,spread=BigDecimal.ZERO,ebr=BigDecimal.ZERO,baseSpread=BigDecimal.ZERO,roisancAmt=BigDecimal.ZERO;
          boolean isFixed=false,isStp=false;
          switch (master.getStp()){
              case "STP":
                  isStp=true;
                  break;
              case "NONSTP":
                  isStp=false;
                  break;
              default:
                  throw new RuntimeException("STP Not Found");
          }
          roiSancRoi=eligibilityDetails.getSancCardRate().toString();
          roiSanctenor= String.valueOf(eligibilityDetails.getSancTenor());
          roiSancemi=eligibilityDetails.getSancEmi().toString();
          ebr=eligibilityDetails.getEbr();
          operationalCost=eligibilityDetails.getOpCost();
          spread=eligibilityDetails.getSpread();
          crp=eligibilityDetails.getCrp();
          roisancAmt=eligibilityDetails.getSancAmountRecommended();

          switch (loanDetails.getRoiType()){
              case "FIXED":
                  isFixed=true;
                  baseSpread=spread.add(operationalCost).add(crp);
                  break;
              case "FLOATING":
                  baseSpread=spread;
                  isFixed=false;
                  break;
              default:
                  throw new RuntimeException("ROI Type Not Found");
          }
          List<Map<String, Object>> roiLevels = (List<Map<String, Object>>) request.getAttribute("roiLevels");



          List<VehicleLoanSubqueueTask> subqueueTask= (List<VehicleLoanSubqueueTask>) request.getAttribute("subQueueData");
          if(latestROIWaiver==null) {
              latestROIWaiver="";
          }
          initailROI = !latestROIWaiver.equals("")?latestROIWaiver:roiSancRoi;
          if(subqueueTask!=null){
              Optional<VehicleLoanSubqueueTask> roi_=subqueueTask.stream().filter(t->t.getTaskType().equalsIgnoreCase("ROI_WAIVER")).findFirst();
              if(roi_.isPresent()){
                  VehicleLoanRoiWaiver roi=roi_.get().getRoiWaiver();
                  if(roi!=null) {
                      // spread=roi.getSpread();
                      baseSpread = roi.getBaseSpread();
                      revisedEmi = roi.getRevisedEmi().toString();
                      revisedRoi = roi.getRevisedRoi().toString();
                      roidecision = roi.getDecision();
                      roiRemarks = roi.getRoiwaiverRemarks();
                  }
              }
          }

      %>

    <div id="roiContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">

      <div class="row">
        <div class="col-sm-12">
          <!--begin::Repeater-->
          <div id="kt_docs_repeater_basic">

              <form id="roiDetailsForm" name="roiDetailsForm" method="POST">
                  <input type="hidden" class="form-control" id="initailROI" name="initailROI" value="<%=initailROI%>" readonly/>

                  <div class="checkbox-wrapper-16 mb-3 mt-2 text-center">
                      <h5 class="mb-2 text-success"> Whether any ROI Modification required?</h5>
                      <div >
                      <label class="checkbox-wrapper">
                          <input class="checkbox-input"  id="Enable" name="roiRequired" type="radio" value="Y" <%=master.getRoiRequested()!=null && master.getRoiRequested()?"checked":""%> >
                          <span class="checkbox-tile">
                                                  <span class="checkbox-icon">
                                                  </span>
                                                  <span class="checkbox-label">Yes</span>
                                                </span>
                      </label>
                      <label class="checkbox-wrapper pl-2">
                          <input class="checkbox-input" id="Disable" name="roiRequired" type="radio" value="N"   <%=master.getRoiRequested()!=null && !master.getRoiRequested()?"checked":""%>>
                          <span class="checkbox-tile">
                                                  <span class="checkbox-icon">
                                                  </span>
                                                  <span class="checkbox-label">No</span>
                                                </span>
                      </label>
                      </div>
                  </div>
                  <div class="roiwaiveRequired <%=master.getRoiRequested()!=null && master.getRoiRequested()?"":"hide"%>">
                      <div class="mb-3">
                          <label for="roisancAmt" class="form-label">Sanctioned Amount by <%=isStp?"BRANCH":"DO"%>:</label>
                          <input type="text" class="form-control" id="roisancAmt" name="roisancAmt"  required value="<%=roisancAmt%>" readonly />
                      </div>
                      <div class="mb-3">
                          <label for="roisancAmt" class="form-label">Sanctioned ROI by <%=isStp?"BRANCH":"DO"%>:</label>
                          <input type="text" class="form-control" id="roiSancRoi" name="roiSancRoi"  required value="<%=roiSancRoi%>" readonly />
                      </div>
                      <div class="mb-3">
                          <label for="roiSancTenor" class="form-label">Sanctioned Tenor:</label>
                          <input type="text" class="form-control" id="roiSancTenor" name="roiSancTenor"  required value="<%=roiSanctenor%>" readonly />
                          <input type="hidden" class="form-control" id="roiRoitype" name="roiRoitype"  required value="<%=loanDetails.getRoiType()%>"  />
                          <input type="hidden" class="form-control" id="roiSTP" name="roiSTP"  required value="<%=master.getStp()%>"  />
                      </div>
                      <div class="mb-3">
                          <label for="roiSancemi" class="form-label">Sanctioned EMI:</label>
                          <input type="text" class="form-control" id="roiSancemi" name="roiSancemi"  required value="<%=roiSancemi%>" readonly />
                      </div>
                      <div class="col-sm-12 mb-3">
                          <table class="table table-sm table-bordered table-hover">
                              <thead>
                              <tr>
                                  <th>EBR</th>
                                  <%if(!isFixed){%>
                                  <th>Operational Cost</th>
                                  <th>CRP</th>
                                  <th>Spread(editable)</th>
                                  <%}else{%>
                                  <th>Base Spread (editable)</th>
                                  <%}%>
                              </tr>
                              </thead>
                              <tbody>
                              <tr>

                                  <td>  <input type="text" class="form-control" id="roiebr" name="roiebr"  required value="<%=ebr.toString()%>" readonly /></td>
                                  <%if(!isFixed){%>
                                  <td>  <input type="text" class="form-control" id="roioperationalCost" name="roioperationalCost"  required value="<%=operationalCost.toString()%>" readonly /> </td>
                                  <td>  <input type="text" class="form-control" id="roicrp" name="roicrp"  required value="<%=crp.toString()%>" readonly /> </td>
                                  <td>  <input type="hidden" class="form-control" id="roispread" name="roispread"  required value="<%=spread.toString()%>"  />
                                      <input type="text" class="form-control" id="roibaseSpread" name="roibaseSpread"  required value="<%=baseSpread.toString()%>"  />
                                  </td>

                                  <%}else{%>
                                  <td>
                                      <input type="hidden" class="form-control" id="roioperationalCost" name="roioperationalCost"  required value="<%=operationalCost.toString()%>" />
                                      <input type="hidden" class="form-control" id="roicrp" name="roicrp"  required value="<%=crp.toString()%>" />
                                      <input type="hidden" class="form-control" id="roispread" name="roispread"  required value="<%=spread.toString()%>" />
                                      <input type="text" class="form-control" id="roibaseSpread" name="roibaseSpread"  required value="<%=baseSpread.toString()%>"  />
                                  </td>
                                  <%}%>
                              </tr>
                              </tbody>
                            </table>
                         </div>


                      <div class="mb-3">
                          <label for="revisedRoi" class="form-label">Revised ROI:</label>
                          <input type="text" class="form-control" id="revisedRoi" name="revisedRoi" readonly required value="<%=revisedRoi%>"  />
                      </div>
                      <div class="mb-3">
                          <label for="revisedEmi" class="form-label">Revised EMI:</label>
                          <input type="text" class="form-control" id="revisedEmi" name="revisedEmi" readonly required value="<%=revisedEmi%>"  />
                      </div>
                      <div class="mb-3">
                          <label for="roidecision" class="form-label">Decision:</label>
                          <select type="text"  id="roidecision" name="roidecision"  required value="<%=roidecision%>"  class="form-select" required>
                              <option value="" selected>Select Decision</option>
                              <%

                                      for (Map<String, Object> level : roiLevels) {
                                          String level_val = (String) level.get("LEVEL_NAME");
                                          String level_desc = (String) level.get("DISPLAY_NAME");
                              %>
                              <option value="<%= level_val %>" <%= level_val.equals(roidecision) ? "selected" : "" %>><%= level_desc %>
                              </option>
                              <%
                                      }

                              %>
                          </select>
                      </div>
                      <div class="mb-3">
                          <label for="roiwaiverRemarks" class="form-label">Remarks:
                              <a href="#" id="hisviewBtn" class="btn btn-lg btn-flex btn-link btn-color-danger">
                                  History
                                  <i class="ki-duotone ki-arrow-right ms-2 fs-3"><span class="path1"></span><span class="path2"></span></i> </a>
                          </label>
                          <textarea type="text"  id="roiwaiverRemarks" name="roiwaiverRemarks"   class="form-control" required><%=roiRemarks%></textarea>
                      </div>
                  </div>

              </form>
              <div class="text-end pt-5">
                  <button type="button" id="roiRecallBtn" class="btn btn-sm btn-danger" data-kt-stepper-action="next">
                      <i class="ki-duotone ki-double-left ">
                          <i class="path1"></i>
                          <i class="path2"></i>
                      </i>
                      Recall
                  </button>

                  <button type="button" id="roisaveBtn" class="btn btn-sm btn-primary" data-kt-stepper-action="next">Save
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

<div class="modal fade" id="roiwaiverHistoryModal" tabindex="-1" role="dialog" aria-labelledby="roiwaiverHistoryModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-xl" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="roiwaiverHistoryModalLabel">Waiver History</h5>
                <div class="btn btn-icon btn-sm btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="ki-duotone ki-cross fs-1"><span class="path1"></span><span class="path2"></span></i>
                </div>
            </div>
            <div class="modal-body">
                <table class="table table-striped table-responsive">
                    <thead id="roiwaiverHistoryTableHeader">

                    </thead>
                    <tbody id="roiwaiverHistoryTableBody">
                    <!-- Data will be populated here -->
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
