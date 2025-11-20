<%--
  Created by IntelliJ IDEA.
  User: SIBL12071
  Date: 21-08-2024
  Time: 23:02
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="java.math.BigDecimal" %>
<%@ page import="javax.persistence.Column" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sib.ibanklosucl.model.doc.VehicleLoanRoiWaiver" %>
<%@ page import="java.util.Optional" %>
<%@ page import="com.sib.ibanklosucl.dto.acopn.SanctionDetailsDTO" %>
<%@ page import="com.sib.ibanklosucl.model.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script>
  function isReq(){
    return $('[name="roiRequired"]:checked').val()=='Y';

  }
  $(document).ready(function () {

    if($('#errflag').val()=='true'){
      blockerMsg('Something went wrong');
      //$('#backbtn').click();
    }

    if($('#task_id').val()!=='' && +$('#task_id').val()>0){
      $('#roiRecallBtn').show();
    }else{
      $('#roiRecallBtn').hide();
    }

    if($('#checker').val()=='1'){

      $('#fetchEMIButton').hide();
      $('#roiRecallBtn').hide();
      $('#sanModSaveBtn').hide();
      $('#msgspan').hide();

      $('#sanModifyForm input').attr('readonly', 'readonly');
    }

    $("#sanModifyForm").validate({
      rules: {
        roiRequired:{
          required:true
        },
        rec_sanc_amt:{
          required:function () {
            return $('#roiRequired').val() == 'Y';
          },
          number:true, max : +$('#eligibleLoanAmt').val(), digits:true
        }, rec_tenor:{
          required:function () {
            return $('#roiRequired').val() == 'Y';
          },digits:true
        },rev_emi_amt:{
          required:function () {
            return $('#roiRequired').val() == 'Y';
          }, min:1
        },sanModRemarks:{
          required:function () {
            return $('#roiRequired').val() == 'Y';
          }, maxlength:500
        }
      },
      messages: {
        rec_sanc_amt:{
          max:"The value should not exceed the maximum eligibility:"+$('#eligibleLoanAmt').val()
        }
      },
      ignore: 'input[type=hidden], .select2-search__field', // ignore hidden fields
      highlight: function(element, errorClass) {
        $(element).removeClass('is-invalid');
        $(element).removeClass('is-valid');
        $(element).removeClass('validation-invalid-label');

      },
      unhighlight: function(element, errorClass) {
        $(element).removeClass('validation-invalid-label');
        $(element).removeClass('is-invalid');
        $(element).removeClass('is-valid');
      },
      /* success: function(label) {
           label.addClass('validation-valid-label').text('Success.'); // remove to hide Success message
       },*/

      wrapper: 'span',
      errorPlacement: function (error, element) {
        element.removeClass('is-valid');
        element.addClass('is-invalid');
        error.css({'padding-left': '23px', 'margin-right': '20px', 'padding-bottom': '2px', 'color': 'red', 'font-size': 'small'});
        error.addClass("validation-invalid-label")
        error.insertAfter(element);
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
    $("#fetchEMIButton").on('click', function (e) {
      e.preventDefault();
      //if($("#sanModifyForm").validate()==false){
      //if($("#sanModifyForm").valid()==false){

      /*alert($("#sanModifyForm").valid());
      if(!$("#sanModifyForm").valid()){
        return false;
      }

       */
      $("#rec_sanc_amt_error").hide();
      $("#rec_tenor_error").hide();
        $("#rev_emi_amt").hide();
      var recSancAmt = $("#rec_sanc_amt").val();
      var recTenor = $("#rec_tenor").val();
      var isValid = true;
      if(recSancAmt === "" || recSancAmt == null){
        $("#rec_sanc_amt_error").show();
        isValid = false;
      }
      if(recTenor === "" || recTenor == null){
        $("#rec_tenor_error").show();
        isValid = false;
      }
      if(isValid){
        showLoader();
        $.ajax({
          url: 'doc/getRevEmiBogAssets',
          type: 'POST',
          data:  JSON.stringify({
            revisedRoi: $("#roi").val(),
            revisedTenor: $("#rec_tenor").val(),
            revisedAmount: $("#rec_sanc_amt").val(),
            wiNum: $('#winum').val(),
            slno: $('#slno').val(),
          }),
          contentType: 'application/json',
          success: function (response) {
            if (response.status === 'S') {
              $(".revemiamt").show();
                $("#rev_emi_amt").show();
              $("#rev_emi_amt").val(response.revisedEmi)
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

    // Handle save button click
    $("#sanModSaveBtn").on('click', function (e) {
      e.preventDefault();
      if($("#sanModifyForm").valid()) {
        var msg = 'Are you sure you don’t want to opt for Sanction Modification?'
        if ($('[name="roiRequired"]:checked').val() == 'Y')
          msg = 'Are you sure you want to proceed with Sanction Modification?'
        confirmmsg(msg)
                .then(function (result) {
                  if (result) {
                    showLoader();
                    var dto = {
                      waiverType: "SAN_MOD",
                      sanModDto: {
                        slno: $('#slno').val(),
                        wiNum: $('#winum').val(),
                        sanModRequired: $('[name="roiRequired"]:checked').val(),
                        sancAmt: $("#sancAmt").val(),
                        roi: $("#roi").val(),
                        tenor: $("#tenor").val(),
                        emi: $("#emi").val(),
                        reqAmt: $("#loanAmt").val(),
                        ltvAmount: $("#ltvAmount").val(),
                        rec_sanc_amt: $("#rec_sanc_amt").val(),
                        rec_tenor: $("#rec_tenor").val(),
                        rev_emi_amt: $("#rev_emi_amt").val(),
                        sanModRemarks: $('#sanModRemarks').val(),
                        eligibleAmt:$('#eligibleLoanAmt').val()
                      }
                    };
                    $.ajax({
                      url: 'doc/createSanModRequest', // Update with your API endpoint
                      type: 'POST',
                      data: JSON.stringify(dto),
                      contentType: 'application/json',
                      async:false,
                      success: function (response) {
                        hideLoader();
                        if (response.status === 'S') {
                          var msg='Record Saved Successfully';
                          if($('[name="roiRequired"]:checked').val()=='Y'){
                            msg='Request is submitted for verification';
                          }
                          notyalt(msg);
                          $('#sanModSaveBtn').hide();
                          $('#fetchEMIButton').hide();
                          $('#roiRecallBtn').show();
                          $('#rec_sanc_amt').prop('readonly',true);
                          $('#rec_tenor').prop('readonly',true);
                          $('#sanModRemarks').prop('readonly',true);
                          $('#msgspan').show();
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
      confirmmsg("Are you sure you want to recall the Sanction Modification SubTask?")
              .then(function (result) {
                if (result) {
                  showLoader();
                  var dto = {
                    waiverType: "SAN_MOD",
                    sanModDto: {
                      slno: $('#slno').val(),
                      wiNum: $('#winum').val(),
                      sanModRequired: 'RECALL',
                      taskId:$('#task_id').val()
                    }
                  };
                  $.ajax({
                    url: 'doc/createSanModRequest', // Update with your API endpoint
                    type: 'POST',
                    data: JSON.stringify(dto),
                    contentType: 'application/json',
                    async:false,
                    success: function (response) {
                      hideLoader();
                      if (response.status === 'S') {
                        $('input:radio[name="roiRequired"]').filter('[value="N"]').prop('checked', true);
                        $('.roiwaiveRequired').addClass('hide');
                        notyalt('WI Recalled Successfully');
                        $('#backbtnbog').click();
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
/*
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
*/
    /*$('#fetchEMIButton').on(''click'', function(e) {
      //e.preventDefault();
      //e.stopPropagation();
      alert('xxxxxxxxx');
    });*/


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

<div class="flex-stack border rounded  px-7 py-3 mb-2">
  <div class="w-100">
    <div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="roiLink" data-bs-target="#roiContent">
      <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
             for="roiContent">
        <i class="ki-duotone ki-ranking fs-3x me-4">
          <span class="path1"></span>
          <span class="path2"></span>
        </i>
        <span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-4">Sanction Modifications</span>
                    <span class="text-muted fw-semibold fs-7">
                     Send WI to Credit for Sanction amount/EMI modifications.
                    </span>
                </span>
      </label>
    </div>
    <%
      VehicleLoanMaster master= (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
      EligibilityDetails eligibilityDetails= (EligibilityDetails) request.getAttribute("eligibilityDetails");
      VehicleLoanDetails loanDetails= (VehicleLoanDetails) request.getAttribute("loanDetails");
      String roiSancRoi="",roiSanctenor="",roiSancemi="";
      String revisedRoi="",revisedEmi="",roidecision="",roiRemarks="";
      BigDecimal operationalCost=BigDecimal.ZERO,crp=BigDecimal.ZERO,spread=BigDecimal.ZERO,ebr=BigDecimal.ZERO,baseSpread=BigDecimal.ZERO,roisancAmt=BigDecimal.ZERO;
      boolean isFixed=false,isStp=false, errflag=false;
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



      SanctionDetailsDTO sancDet= (SanctionDetailsDTO) request.getAttribute("sanctionDetailsFinal");
      String sancAmt="",emi="",ifsc="",roi="",tenor="",ltvpercentage="",ltvAmount="",loanAmount="", eligibleLoanAmt="0";
      String readonly="", task_id="0";
      String rec_sanc_amt="",rec_tenor="",rev_emi_amt="",sanModRemarks="", sanAmtModReqd="";
      String checker="0";
      VehicleLoanSanMod vehicleLoanSanMod=null;
      if(request.getAttribute("checker")!=null) {
        checker = request.getAttribute("checker").toString();
      }
      if(sancDet!=null){
        sancAmt= sancDet.getSancAmountRecommended();
        emi= sancDet.getSancEmi();
        roi= sancDet.getSancCardRate();
        tenor= sancDet.getSancTenor();
        ltvpercentage = sancDet.getLtvPercentage();
        ltvAmount = sancDet.getLtvAmount();
        loanAmount = sancDet.getLoanAmt();
        eligibleLoanAmt=sancDet.getEligibleLoanAmt();
      }
      List<VehicleLoanSubqueueTask> vehicleLoanSubqueueTasks=master.getSubqueueTasks().stream().filter(task->"PENDING".equals(task.getStatus()) && "SAN_MOD".equalsIgnoreCase(task.getTaskType())).toList();
      VehicleLoanSubqueueTask vehicleLoanSubqueueTask;
      if(vehicleLoanSubqueueTasks!=null && vehicleLoanSubqueueTasks.size()>0){
        vehicleLoanSubqueueTask = vehicleLoanSubqueueTasks.get(0);
        if(vehicleLoanSubqueueTask!=null){
          readonly="disabled";

          vehicleLoanSanMod=(VehicleLoanSanMod)request.getAttribute("vehicleLoanSanMod");
          if(vehicleLoanSanMod!=null){
            rec_sanc_amt=vehicleLoanSanMod.getRevisedSanAmt().toString();
            rec_tenor=vehicleLoanSanMod.getRevisedTenor().toString();
            rev_emi_amt=vehicleLoanSanMod.getRevisedEmi().toString();
            sanModRemarks=vehicleLoanSanMod.getRemarks();
            sanAmtModReqd=vehicleLoanSanMod.getSanModRequired();
            task_id=vehicleLoanSanMod.getVlsanmod().getTaskId().toString();


          }
        }
      }
      if("1".equals(checker)){
        if(vehicleLoanSanMod==null || vehicleLoanSanMod.getDecision()!=null || vehicleLoanSubqueueTasks==null || vehicleLoanSubqueueTasks.size()==0){
          errflag=true;
        }

      }




    %>

    <div id="roiContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">

      <div class="row">
        <div class="col-sm-12">
          <!--begin::Repeater-->
          <div id="kt_docs_repeater_basic">

            <form id="sanModifyForm" name="sanModifyForm" method="POST">

              <input type="hidden" id="eligibleLoanAmt" name="eligibleLoanAmt" value="<%=eligibleLoanAmt%>"/>
              <input type="hidden" id="task_id" name="task_id" value="<%=task_id%>"/>
              <input type="hidden" id="checker" name="checker" value="<%=checker%>"/>
              <input type="hidden" id="errflag" name="errflag" value="<%=errflag%>"/>
<%--              <span id="msgspan" class=" ">This record is pending verification</span>--%>
              <div id="msgspan" class="alert alert-danger <%=readonly.equals("disabled")?"":"hide"%>">
                <strong>Note!</strong> This record is pending for verification
              </div>
              <div class="checkbox-wrapper-16 mb-3 mt-2 text-center">
                <h5 class="mb-2 text-success"> Whether any Sanction Modification to be done?</h5>
                <div >
                  <label class="checkbox-wrapper">
                    <input class="checkbox-input"  id="Enable" name="roiRequired" type="radio" value="Y" <%=readonly%> <%=sanAmtModReqd.equals("Y")?"checked":""%> >
                    <span class="checkbox-tile">
                                                  <span class="checkbox-icon">
                                                  </span>
                                                  <span class="checkbox-label">Yes</span>
                                                </span>
                  </label>
                  <label class="checkbox-wrapper pl-2">
                    <input class="checkbox-input" id="Disable" name="roiRequired" type="radio" value="N"  <%=readonly%> <%=sanAmtModReqd.equals("N")?"checked":""%>>
                    <span class="checkbox-tile">
                                                  <span class="checkbox-icon">
                                                  </span>
                                                  <span class="checkbox-label">No</span>
                                                </span>
                  </label>
                </div>
              </div>
              <div class="roiwaiveRequired <%=sanAmtModReqd.equals("Y")?"":"hide"%>">
                <div class="row">
                  <div class="col-lg-6">
                    <div class="mb-2 mt-2">
                      <div class="row">
                        <div class="col-md-12">
                          <label for="sancAmt" class="form-label">Sanctioned Amount</label>
                          <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                            <input type="text" class="form-control" id="sancAmt" name="sancAmt"  required value="<%=sancAmt%>" readonly />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="col-lg-6">
                    <div class="mb-2 mt-2">
                      <div class="row">
                        <div class="col-md-12">
                          <label for="loanAmt" class="form-label">Loan Amount Requested</label>
                          <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                            <input type="text" class="form-control" id="loanAmt" name="loanAmt"  required value="<%=loanAmount%>" readonly />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>


                <div class="row">
                  <div class="col-lg-6">
                    <div class="mb-2 mt-2">
                      <div class="row">
                        <div class="col-md-12">
                          <label for="roi" class="form-label">Effective ROI</label>
                          <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                            <input type="text" class="form-control" id="roi" name="roi"  required value="<%=roi%>" readonly />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="col-lg-6">
                    <div class="mb-2 mt-2">
                      <div class="row">
                        <div class="col-md-12">
                          <label for="ltvAmount" class="form-label">LTV Amount</label>
                          <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                            <input type="text" class="form-control" id="ltvAmount" name="ltvAmount"  required value="<%=ltvAmount%>" readonly />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="row">
                  <div class="col-lg-6">
                    <div class="mb-2 mt-2">
                      <div class="row">
                        <div class="col-md-12">
                          <label for="tenor" class="form-label">Tenor</label>
                          <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                            <input type="text" class="form-control" id="tenor" name="tenor"  required value="<%=tenor%>" readonly />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="col-lg-6">
                    <div class="mb-2 mt-2">
                      <div class="row">
                        <div class="col-md-12">
                          <label for="emi" class="form-label">EMI</label>
                          <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                            <input type="text" class="form-control" id="emi" name="emi"  required value="<%=emi%>" readonly />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="row">
                  <div class="col-lg-6">
                    <div class="mb-2 mt-2">
                      <div class="row">
                        <div class="col-md-12">
                          <label for="rec_sanc_amt" class="form-label">Revised Sanction Amount</label>
                          <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                            <input type="text" class="form-control" id="rec_sanc_amt" name="rec_sanc_amt" <%=readonly%>  value="<%=rec_sanc_amt%>" />
                            <small id="rec_sanc_amt_error" class="text-danger" style="display: none;">This field is required</small>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="col-lg-6">
                    <div class="mb-2 mt-2">
                      <div class="row">
                        <div class="col-md-12">
                          <label for="rec_tenor" class="form-label">Revised Tenor</label>
                          <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                            <input type="text" class="form-control" id="rec_tenor" name="rec_tenor"  <%=readonly%>  value="<%=rec_tenor%>" />
                            <small id="rec_tenor_error" class="text-danger" style="display: none;">This field is required</small>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="row">
                  <div class="col-lg-12 d-flex justify-content-center mb-2 mt-2">
                      <button type="button" id="fetchEMIButton" <%=readonly%> class="btn btn-sm btn-info" data-kt-stepper-action="next">
                        Calculate EMI
                        <i class="ki-duotone ki-double-right ">
                          <i class="path1"></i>
                          <i class="path2"></i>
                        </i>
                      </button>
                  </div>
                </div>

                  <div class="row">
                      <div class="col-lg-6">
                          <div class="mb-2 mt-2">
                              <div class="row">

                                  <div class="col-md-12 revemiamt <%=readonly.equals("disabled")?"":"hide"%> ">
                                      <label for="rev_emi_amt" class="form-label">Revised EMI Amount</label>
                                      <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                                          <input type="text" class="form-control" id="rev_emi_amt" name="rev_emi_amt" readonly required value="<%=rev_emi_amt%>" />
                                      </div>
                                  </div>
                              </div>
                          </div>
                      </div>
                  </div>

                <div class="row">
                  <div class="col-lg-6">
                    <div class="mb-2 mt-2">
                      <div class="row">
                        <div class="col-md-12">
                          <label for="rev_emi_amt" class="form-label">Remarks</label>
                          <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                            <input type="text" class="form-control" id="sanModRemarks" name="sanModRemarks" required <%=readonly%> value="<%=sanModRemarks%>" />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
<%--              </div>--%>

            </form>
            <div class="text-end pt-5">
              <button type="button" id="roiRecallBtn" class="btn btn-sm btn-danger <%=sanAmtModReqd.isEmpty()?"hide":""%>" data-kt-stepper-action="next" >
                <i class="ki-duotone ki-double-left ">
                  <i class="path1"></i>
                  <i class="path2"></i>
                </i>
                Recall
              </button>

              <button type="button" id="sanModSaveBtn" class="btn btn-sm btn-primary" <%=readonly%> data-kt-stepper-action="next">Save
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
