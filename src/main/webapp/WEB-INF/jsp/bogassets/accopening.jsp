<%--
  Created by IntelliJ IDEA.
  User: SIBL12134
  Date: 28-08-2024
  Time: 16:45
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
  $(document).ready(function () {
    $("#accopenbtn").on('click', function (e) {
       var slno= $('#slno').val();
       var wiNum= $('#winum').val();
        if(false){//if(!$("#check1").is(':checked') || !$("#check2").is(':checked')){
            alertmsg('Please tick both checkboxes to continue');
        }
        else{
            var button = document.querySelector("#accopenbtn");
            button.setAttribute("data-kt-indicator", "on");//.attr("disabled",true);
            $('#accopenbtn').attr("disabled",true);

            $.ajax({
                url: 'api/accopening', // Update with your API endpoint
                type: 'POST',
                data: {
                    winum: wiNum,
                    slno: slno
                },
                //async:false,
                success: function (response) {
                    hideLoader();
                    if (response.status === 'S') {
                        var accountno=response.msg;
                        var msg='Loan Account '+accountno+' Opened Successfuly';
                        $('#loanAccountNo_neft').val(accountno);
                        $('.disbdivaccno').text('Loan account number : '+accountno);
                        $('.accspan').text('Loan account number : '+accountno);
                        notyalt(msg);
                        $("#accopenbtn").hide();
                        $('.class111').hide();
                    } else {
                        alertmsg('Failed: ' + response.msg);
                        button.removeAttribute("data-kt-indicator");//.attr("disabled",false);
                        $('#accopenbtn').attr("disabled",false);
                    }
                },
                error: function (xhr, status, error) {

                    hideLoader();
                    var err_data = xhr.responseJSON;
                    if (err_data.msg!='') {
                        alertmsgvert(err_data.msg);
                    } else {
                        alertmsg('An error occurred: ' + error);
                    }
                    button.removeAttribute("data-kt-indicator");//.attr("disabled",false);
                    $('#accopenbtn').attr("disabled",false);
                }
            });
        }



    });
  });
</script>

<div class="flex-stack border rounded  px-7 py-3 mb-2">
  <div class="w-100">
    <div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="accopeninglink" data-bs-target="#accopencontent">
      <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
             for="accopencontent">
        <i class="ki-duotone ki-ranking fs-3x me-4">
          <span class="path1"></span>
          <span class="path2"></span>
        </i>
        <span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-4">Account Opening</span>
                    <span class="text-muted fw-semibold fs-7">

                    </span>
                </span>
      </label>
    </div>
    <%

    %>

    <div id="accopencontent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">

      <div class="row">
        <div class="col-sm-12">
          <!--begin::Repeater-->
          <div id="kt_docs_repeater_basic">

            <form id="acctlabelForm" name="acctlabelForm" method="POST">
              <div class="card">


                <div id="" class="table-responsive border-white border-opacity-15 mb-3 ">


                  <div  class="card-footer ">
                      <%
                          VehicleLoanMaster master= (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
                          String loanAcctNo=master.getAccNumber();
                          if(loanAcctNo!=null){
                      %>
                      <div class="row">
                          <div class="col-lg-12">
                              Loan Account Number : <%=loanAcctNo%>.
                          </div>

                      </div>
                      <%
                          }else{
                      %>
                      <div class="row">

                      <div class="col-lg-12 class111 hide">

                          <div class="form-check">
                              <input class="form-check-input" type="checkbox" id="check1" name="check1" value="Y" >
                              <label class="form-check-label">All loan documents are in order</label>
                          </div>
                          <div class="form-check">
                              <input class="form-check-input" type="checkbox" id="check2" name="check2" value="Y" >
                              <label class="form-check-label">All sanction terms and conditions are complied</label>
                          </div>
                      </div>
                  </div>
                      <div class="row">
                          <div class="col-lg-1">
                          </div>
                          <div class="col-lg-2" style="justify-content: end;display: flex;">
                              <button  id="accopenbtn" type="button" class="btn btn-sm btn-success">
                                  <span class="indicator-label">Open Loan Account</span>
                                  <span class="indicator-progress">
                                    Please wait... <span class="spinner-border spinner-border-sm align-middle ms-2"></span>
                                </span>
                              </button>
<%--                              <button type="button" class="btn btn-success btn-sm  btn-file" name="" id="accopenbtn2">--%>
<%--                                  &lt;%&ndash;                          <i class="ph-plus-circle  me-2"></i>&ndash;%&gt;--%>
<%--                                  Open Loan Account--%>
<%--                              </button>--%>
                          </div>
                          <div class="col-lg-8" style="display: flex;">
                              <span class="text-success fw-bold accspan"></span>
                          </div>
                      </div>
                      <%
                          }
                      %>
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
