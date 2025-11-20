<%--
  Created by IntelliJ IDEA.
  User: SIBL12134
  Date: 31-08-2024
  Time: 13:49
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
    var cityId=$('#dealerCity_neft').val();
    var dealerSubCode=$('#dealerSubCode_neft').val();
    var dealerCode=$('#dealerCode_neft').val();
    var dealerName=$('#dealerName_neft').val();
    var makeId=$('#dealerMake_neft').val();


    // // Handle save button click
    // $(".neftbtn").on('click', function (e) {
    //   e.preventDefault();
    //   var clickedid = this.id;
    //   var button =  document.querySelector("#"+clickedid);//$("#"+clickedid);//
    //
    //   var flag=0;
    //   if($('#neftForm').valid()) {
    //
    //     flag = validateAmounts();
    //     if (flag == '1') {
    //       button.setAttribute("data-kt-indicator", "on");//.attr("disabled",true);
    //       var neftamountdealer = +$('#neftamountdealer').val();
    //       var neftAmtManuf = +$('#neftAmtManuf').val();
    //       var winum = $('#winum').val();
    //       var slno = $('#slno').val();
    //       var disbType = $('[name="disbType"]:checked').val();
    //       var add1 = $('#add1').val();
    //       var add2 = $('#add2').val();
    //       var add3 = $('#add3').val();
    //
    //
    //       if (clickedid == 'dneftbtn') {
    //         doNeft(slno, winum, "D", neftamountdealer, neftAmtManuf, "", "", "", "",disbType,"","","");
    //       } else {
    //         var manAccNum = $('#manAccNum').val();
    //         var manIfsc = $('#manIfsc').val();
    //         var accname = $('#accname').val();
    //         var manufmobile = $('#manuMob').val();
    //         doNeft(slno, winum, "M", neftamountdealer, neftAmtManuf, manAccNum, manIfsc, accname, manufmobile,disbType,add1, add2, add3);
    //       }
    //       button.removeAttribute("data-kt-indicator");//attr("disabled",false);
    //     }
    //   }
    // });


    $('input[type=radio][name=disbType]').change(function() {
      showHideNeftDivs(this.value);
    });
    var disbType=$('[name="disbType"]:checked').val();
    showHideNeftDivs(disbType);

    var neftflagmanu = $('#neftflagmanu').val();
    var neftflagdealer = $('#neftflagdealer').val();
    if(neftflagmanu=='SUCCESS' || neftflagdealer=='SUCCESS'){
      $("#neftContent :input").attr("disabled", true);
    }
  });

  function doNeft(slno,wiNum,beneficiaryType,dneftamt, mneftamt, accnum,ifsc, accname,manufmobile ,disbType, add1, add2, add3){

    $.ajax({
      url: 'api/performneft', // Update with your API endpoint
      type: 'POST',
      data: {
        winum: wiNum,
        slno: slno,
        beneficiaryType:beneficiaryType,
        dneftamt:dneftamt,
        mneftamt:mneftamt,
        accnum:accnum,
        ifsc:ifsc,
        accname:accname,
        manufmobile:manufmobile,
        disbType:disbType,
        add1:add1,
        add2:add2,
        add3:add3
      },
      //async:false,
      success: function (response) {
        hideLoader();
        if (response.status === 'S') {

          var msg='', utrtext='';
          if (ifsc.indexOf('SIBL') == 0) {//ie ifsc starts with sibl
            msg='Tran is successful, Tran ID:'+response.msg;
            utrtext='Tran ID:'+response.msg;
          }else{
            msg='Neft is Successful, utr:'+response.msg;
            utrtext='UTR No:'+response.msg;
          }


          if(beneficiaryType=='D'){
            $('#dutr').text(utrtext);
            $('.dutrtick').removeClass('hide');
            $('#neftamountdealer').attr("disabled", true);
            $('#dneftbtn').hide();
            $('#neftAmtManuf').attr("disabled", true);
          }else{
            $("#neftContent :input").attr("disabled", true);
            $('#mutr').text(utrtext);
            $('.mutrtick').removeClass('hide');
          }
          notyalt(msg);
        } else {
          alertmsg('Failed: ' + response.msg);
        }
      },
      error: function (xhr, status, error) {
        hideLoader();
        alertmsg('An error occurred: ' + error);
      }
    });
  }

  function showHideNeftDivs(disbType){
    $('.commonDiv').removeClass('hide');
    if (disbType == 'M') {
      $('.multipleDisbDiv').removeClass('hide');
    }else{
      $('.multipleDisbDiv').addClass('hide');
    }

  }

  function validateAmounts(){

    var disbursedAmount=$('#disbursedAmount').val();
    var dneftamt = +$('#neftamountdealer').val();
    var mneftamt = +$('#neftAmtManuf').val();
    var disbtype = $('[name="disbType"]:checked').val();
    var flag=1;
    if(disbtype!='S' && disbtype!='M'){
      alertmsg('Please select the type of disbursement');
      flag=0;
    }else{
      if(disbtype=='S'){
        if(dneftamt<=0){
          alertmsg('Dealer NEFT amount should be greater than 0');
          flag=0;
        }else if(disbursedAmount!=dneftamt){
          alertmsg('Dealer NEFT amount should match the disbursed amount');
          flag=0;
        }
      }
      if(disbtype=='M'){

        if(mneftamt<=0){
          alertmsg('Dealer NEFT amount should be greater than 0');
          flag=0;
        }else if(mneftamt<=0){
          alertmsg('Manufacturer NEFT amount should be greater than 0');
          flag=0;
        }else if(disbursedAmount!=(dneftamt+mneftamt)){
          alertmsg('Dealer amount + Manufacturer neft amount should match the Total disbursed amount');
          flag=0;
        }
      }
    }

    return flag;
  }
</script>

<div class="flex-stack border rounded  px-7 py-3 mb-2">
  <div class="w-100">
    <div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="neftlink" data-bs-target="#neftContent">
      <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
             for="neftContent">
        <i class="ki-duotone ki-ranking fs-3x me-4">
          <span class="path1"></span>
          <span class="path2"></span>
        </i>
        <span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-4">NEFT</span>
                    <span class="text-muted fw-semibold fs-7">
                     
                    </span>
                </span>
      </label>
    </div>
    <%
      VehicleLoanMaster master= (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
      String loanAccountNo=master.getAccNumber();
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

      VehicleLoanAccount vehicleLoanAccount = (VehicleLoanAccount) request.getAttribute("vehicleLoanAccount");
      String neftflagdealer="", fiflagdealer="";
      String neftflagmanu="", fiflagmanu="";
      String disbType="",neftamountdealer="", dutr="", mutr="", manuMob="", add1="",add2="",add3="", manIfsc="",accname="",neftAmtManuf="",manAccNum="";
      BigDecimal disbursedAmount =BigDecimal.ZERO;
      if(disbType==null){
        disbType="";
      }
      if(vehicleLoanAccount!=null){


        if(vehicleLoanAccount.getFiflag_dealer()!=null){
          fiflagdealer=vehicleLoanAccount.getFiflag_dealer();
        }
        if(vehicleLoanAccount.getFiflag_manu()!=null){
          fiflagmanu=vehicleLoanAccount.getFiflag_manu();
        }

        if(vehicleLoanAccount.getManufifsc()!=null){
          manIfsc=vehicleLoanAccount.getManufifsc();
        }
        if(vehicleLoanAccount.getManuname()!=null){
          accname=vehicleLoanAccount.getManuname();
        }
        if(vehicleLoanAccount.getNeftamountmanuf()!=null){
          neftAmtManuf=vehicleLoanAccount.getNeftamountmanuf().toString();
        }
        if(vehicleLoanAccount.getManufacc()!=null){
          manAccNum=vehicleLoanAccount.getManufacc();
        }

        if(vehicleLoanAccount.getNeftflagdealer()!=null) {
          neftflagdealer = vehicleLoanAccount.getNeftflagdealer();
        }
        if(vehicleLoanAccount.getNeftflagmanuf()!=null){
          neftflagmanu=vehicleLoanAccount.getNeftflagmanuf();
        }
        if(vehicleLoanAccount.getDisbType()!=null){
          disbType=vehicleLoanAccount.getDisbType();
        }

        disbursedAmount = vehicleLoanAccount.getDisbursedAmount();
        if(disbursedAmount==null){
          disbursedAmount=BigDecimal.ZERO;
        }
        if(vehicleLoanAccount.getNeftamountdealer()!=null) {
          neftamountdealer = vehicleLoanAccount.getNeftamountdealer().toString();
        }
        if(vehicleLoanAccount.getUtrnodealer()!=null){
          dutr="UTR No:"+vehicleLoanAccount.getUtrnodealer();
        }else if(vehicleLoanAccount.getFitranid_dealer()!=null){
          dutr="TranID:"+vehicleLoanAccount.getFitranid_dealer();
        }
        if(vehicleLoanAccount.getUtrnomanuf()!=null){
          mutr="UTR No:"+vehicleLoanAccount.getUtrnomanuf();
        }else if(vehicleLoanAccount.getFitranid_manu()!=null){
          mutr="TranID:"+vehicleLoanAccount.getFitranid_manu();
        }
        if(vehicleLoanAccount.getManumob()!=null){
          manuMob=vehicleLoanAccount.getManumob();
        }
        if(vehicleLoanAccount.getAdd1()!=null){
          add1=vehicleLoanAccount.getAdd1();
        }
        if(vehicleLoanAccount.getAdd2()!=null){
          add2=vehicleLoanAccount.getAdd2();
        }
        if(vehicleLoanAccount.getAdd3()!=null){
          add3=vehicleLoanAccount.getAdd3();
        }
      }
      String dealeraccifsc=request.getAttribute("dealeraccifsc")==null?"":(String)request.getAttribute("dealeraccifsc");
      String dealeracc="", dealerifsc="";
      if(dealeraccifsc!=null && dealeraccifsc.contains("-")){
        dealeracc = dealeraccifsc.split("-")[0];
        dealerifsc=dealeraccifsc.split("-")[1];
      }

    %>

    <input type="hidden" id="dealerCity_neft" name="dealerCity" value="<%=dealerCity%>"/>
    <input type="hidden" id="dealerSubCode_neft" name="dealerSubCode" value="<%=dealerSubCode%>"/>
    <input type="hidden" id="dealerCode_neft" name="dealerCode" value="<%=dealerCode%>"/>
    <input type="hidden" id="dealerName_neft" name="dealerName" value="<%=dealerName%>"/>
    <input type="hidden" id="dealerMake_neft" name="dealerMake" value="<%=dealerMake%>"/>
    <input type="hidden" id="neftflagdealer" name="neftflagdealer" value="<%=neftflagdealer%>"/>
    <input type="hidden" id="neftflagmanu" name="neftflagmanu" value="<%=neftflagmanu%>"/>

    <div id="neftContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">

      <div class="row">
        <div class="col-sm-12">
          <!--begin::Repeater-->
          <div id="kt_docs_repeater_basic">

            <form id="neftForm" name="neftForm" method="POST">

              <div class="alert alert-danger">
                Loan Account Number : <input type="text" class="form-control" id="loanAccountNo_neft" name="loanAccountNo_neft"  value="<%=loanAccountNo%>" readonly />
              </div>
              <div class="alert alert-danger">
                Disbursed amount : <input type="text" class="form-control" id="disbursedAmount" name="disbursedAmount"  value="<%=disbursedAmount%>" readonly />
              </div>
              <div class="checkbox-wrapper-16 mb-3 mt-2 text-center">
                <h5 class="mb-2 text-success"> Whether disbursement should be single/multiple?</h5>
                <div >
                  <label class="checkbox-wrapper">
                    <input class="checkbox-input"  id="Enable" disabled name="disbType" type="radio" value="S" <%=disbType.equals("S")?"checked":""%> >
                    <span class="checkbox-tile">
                                                  <span class="checkbox-icon">
                                                  </span>
                                                  <span class="checkbox-label">Single</span>
                                                </span>
                  </label>
                  <label class="checkbox-wrapper pl-2">
                    <input class="checkbox-input" id="Disable" disabled name="disbType" type="radio" value="M" <%=disbType.equals("M")?"checked":""%> >
                    <span class="checkbox-tile">
                                                  <span class="checkbox-icon">
                                                  </span>
                                                  <span class="checkbox-label">Multiple</span>
                                                </span>
                  </label>
                </div>
              </div>
              <div class="fs-6  ps-2 commonDiv hide" >
                <h4>Dealer details</h4>

                <div class="row">
                  <div class="col-lg-12">
                    <div class="m-3">
                      <div class="row">
                        <div class="col-sm-12">
                          <table class="table table-sm align-middle table-row-dashed table-row-gray-400 fs-8 gy-3">
                            <thead>

                            <tr>
                              <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">Dealer Name</th>
                              <td><%=dealerName%>
                              </td>
                              <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">Dealer Name Remarks</th>
                              <td><%=dealerNameRmks%>
                              </td>
                            </tr>
                            <tr>
                              <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">DST Code</th>
                              <td><%=dstCode%>
                              </td>
                              <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">DSA Code</th>
                              <td><%=dsaCode%></td>
                            </tr>
                            <tr>
                              <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">Dealer Code</th>
                              <td><%=dealerCode%></td>
                              <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">Dealer Sub Code</th>
                              <td><%=dealerSubCode%></td>
                            </tr>
                            <%if(request.getAttribute("dealeraccifsc")!=null)
                            {%>
                            <tr>
                              <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">Dealer Account Number</th>
                              <td><%=dealeracc%></td>
                              <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">IFSC</th>
                              <td><%=dealerifsc%>
                              </td>
                            </tr>
                            <%}%>
                            <tr>
                              <th class="text-start text-danger fw-bold fs-8 text-uppercase gs-0">NEFT Amount for Dealer</th>
                              <td>
                                <div class="input-group">
                                  <input type="text" readonly class="form-control " <%=neftflagdealer.equals("SUCCESS") || fiflagdealer.equals("SUCCESS")?"disabled":""%> id="neftamountdealer" name="neftamountdealer"  required number="true" min="1" value="<%=neftamountdealer%>" style="width: 50%" />
                                  <div class="input-group-append  dutrtick  <%=neftflagdealer.equals("SUCCESS") || fiflagdealer.equals("SUCCESS")?"":"hide"%> ">
                                    <span class="input-group-text bg-white">
                                      <i class="text-success bi-check-circle-fill"></i>
                                    </span>
                                  </div>
                                </div>

                              </td>
                              <th class="text-success fw-bold fs-8 text-uppercase gs-0 utrspan <%=neftflagdealer.equals("SUCCESS") || fiflagdealer.equals("SUCCESS")?"":"hide"%>"><span id="dutr"><%=dutr%></span></th>
                              <td>
                              </td>
                            </tr>

                            </thead>
                          </table>
<%--                          <div class="mb-1 mt-1 text-end">--%>
<%--                            <button id="dneftbtn" type="button" class="btn btn-sm btn-light-primary neftbtn">--%>
<%--                              <span class="indicator-label">Send NEFT to Dealer</span>--%>
<%--                              <span class="indicator-progress">Please wait... <span class="spinner-border spinner-border-sm align-middle ms-2"></span></span>--%>
<%--                            </button>--%>

<%--                          </div>--%>

                        </div>
                      </div>
                    </div>
                  </div>

                </div>

              </div>

              <div class="fs-6  ps-2 multipleDisbDiv hide" >
                <h4>Manufacturer details</h4>
                <div class="row">
                  <div class="col-lg-12">
                    <div class="m-3">
                      <div class="row">
                        <div class="col-sm-12">
                          <table class="table table-sm align-middle table-row-dashed table-row-gray-400 fs-8 gy-3">
                            <thead>

                            <tr>
                              <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">Make</th>
                              <td>
                                <%=dealerMake%>
                              </td>
                              <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">Manufacturer Mobile Number</th>
                              <td><input type="text" class="form-control" id="manuMob" name="manuMob"  minlength="10" maxlength="10" required digits="true" value="<%=manuMob%>"  /></td>
                            </tr>
                            <tr>
                              <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">Manufacturer Account Number</th>
                              <td><input type="text" class="form-control" id="manAccNum" name="manAccNum"  required maxlength="20" value="<%=manAccNum%>"  />
                              </td>
                              <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">Confirm Account Number</th>
                              <td><input type="text" class="form-control" id="confirmAcc" name="confirmAcc"  maxlength="20" required value="<%=manAccNum%>"  /></td>
                            </tr>
                            <tr>
                              <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">IFSC Code</th>
                              <td><input type="text" class="form-control" id="manIfsc" name="manIfsc"  required value="<%=manIfsc%>" oninput="this.value = this.value.toUpperCase()"  maxlength="11" />
                              </td>
                              <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">Account Name</th>
                              <td><input type="text" class="form-control" id="accname" name="accname"  required value="<%=accname%>"  maxlength="50" /></td>
                            </tr>
                            <tr>
                              <th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">Beneficiary Address</th>
                              <td colspan="3">
                                <input type="text" class="form-control" id="add1"  name="add1" placeholder="Address line 1" required value="<%=add1%>"  maxlength="35" />
                                <input type="text" class="form-control" id="add2"  name="add2" placeholder="Address line 2"  required value="<%=add2%>"  maxlength="35" />
                                <input type="text" class="form-control" id="add3"  name="add3" placeholder="Address line 3"  required value="<%=add3%>"  maxlength="35" />
                              </td>
                            </tr>
                            <tr>
                              <th class="text-start text-danger fw-bold fs-8 text-uppercase gs-0">NEFT Amount for Manufacturer</th>
                              <td>
                              <div class="input-group">
                                <input type="text" class="form-control" <%=neftflagmanu.equals("SUCCESS") || fiflagmanu.equals("SUCCESS")?"disabled":""%> id="neftAmtManuf" name="neftAmtManuf" value="<%=neftAmtManuf%>" required number="true" min="1" value="" style="width: 50%" />
                                <div class="input-group-append mutrtick <%=neftflagmanu.equals("SUCCESS") || fiflagmanu.equals("SUCCESS")?"":"hide"%>">
                                    <span class="input-group-text bg-white">
                                      <i class="text-success bi-check-circle-fill"></i>
                                    </span>
                                </div>
                              </div>
                              </td>
                              <th class="text-success fw-bold fs-8 text-uppercase gs-0 utrspan <%=neftflagmanu.equals("SUCCESS")?"":"hide"%>"><span id="mutr"><%=mutr%></span></th>
                              <td>
                              </td>

                            </tr>


                            </thead>
                          </table>
                          <div class="mb-1 mt-1 text-end">
<%--                            <button id="mneftbtn" type="button" class="btn btn-sm btn-light-primary neftbtn">--%>
<%--                              <span class="indicator-label">Send NEFT to Manufacturer</span>--%>
<%--                              <span class="indicator-progress">Please wait... <span class="spinner-border spinner-border-sm align-middle ms-2"></span></span>--%>
<%--                            </button>--%>

                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                </div>

              </div>


            </form>

          </div>
          <!--end::Repeater-->
        </div>
      </div>
    </div>
  </div>
</div>
