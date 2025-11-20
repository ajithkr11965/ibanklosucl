<%--
  Created by IntelliJ IDEA.
  User: SIBL12134
  Date: 30-08-2024
  Time: 10:18
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
  //   $("#disbursebtn").on('click', function (e) {
  //     var button = document.querySelector("#disbursebtn");//$('#disbursebtn');//
  //     button.setAttribute("data-kt-indicator", "on");//.attr("disabled",true);
  //     var slno= $('#slno').val();
  //     var wiNum= $('#winum').val();
  //     $.ajax({
  //       url: 'api/disbursement', // Update with your API endpoint
  //       type: 'POST',
  //       data: {
  //         winum: wiNum,
  //         slno: slno
  //       },
  //       //async:false,
  //       success: function (response) {
  //         hideLoader();
  //         if (response.status === 'S') {
  //           var msg='Disbursement request is submitted successfully';
  //           notyalt(msg);
  //           $('#disbursebtn').hide();
  //         } else {
  //           alertmsg('Failed: ' + response.msg);
  //         }
  //         button.removeAttribute("data-kt-indicator");//.attr("disabled",false);
  //       },
  //       error: function (xhr, status, error) {
  //         hideLoader();
  //         var err_data = xhr.responseJSON;
  //         if (err_data.msg!='') {
  //           alertmsgvert(err_data.msg);
  //         } else {
  //           alertmsg('An error occurred: ' + error);
  //         }
  //         button.removeAttribute("data-kt-indicator");//.attr("disabled",false);
  //       }
  //     });
  //   });
  //
  //   $("#statenqbtn").on('click', function (e) {
  //     var button = document.querySelector("#statenqbtn");//$("#statenqbtn");//
  //     button.setAttribute("data-kt-indicator", "on");//.attr("disabled",true);
  //     var slno= $('#slno').val();
  //     var wiNum= $('#winum').val();
  //     $.ajax({
  //       url: 'api/disbursementenq', // Update with your API endpoint
  //       type: 'POST',
  //       data: {
  //         winum: wiNum,
  //         slno: slno
  //       },
  //       async:false,
  //       success: function (response) {
  //         hideLoader();
  //         if (response.status === 'S') {
  //           var msg='The loan is disbursed successfully, tran id:'+response.msg;
  //           notyalt(msg);
  //         }else if (response.status === 'P') {
  //           notyalt('Request is still pending : '+response.msg);
  //         } else {
  //           alertmsg('Failed: ' + response.msg);
  //         }
  //         button.removeAttribute("data-kt-indicator");//.attr("disabled",false);
  //       },
  //       error: function (xhr, status, error) {
  //         hideLoader();
  //         var err_data = xhr.responseJSON;
  //         if (err_data.msg!='') {
  //           alertmsgvert(err_data.msg);
  //         } else {
  //           alertmsg('An error occurred: ' + error);//.attr("disabled",false);
  //         }
  //         button.removeAttribute("data-kt-indicator");
  //       }
  //     });
  //   });
  // });
</script>

<div class="flex-stack border rounded  px-7 py-3 mb-2">
  <div class="w-100">
    <div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="disblink" data-bs-target="#disbursecontent">
      <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
             for="disbursecontent">
        <i class="ki-duotone ki-ranking fs-3x me-4">
          <span class="path1"></span>
          <span class="path2"></span>
        </i>
        <span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-4">Disbursement</span>
                    <span class="text-muted fw-semibold fs-7">

                    </span>
                </span>
      </label>
    </div>
    <%

    %>

    <div id="disbursecontent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">

      <div class="row">
        <div class="col-sm-12">
          <!--begin::Repeater-->
          <div id="kt_docs_repeater_basic">

            <form id="disbForm" name="disbForm" method="POST">
              <div class="card">


                <div id="" class="table-responsive border-white border-opacity-15 mb-3 ">


                  <div  class="card-footer ">
                    <%
                      VehicleLoanAccount vehicleLoanAccount = (VehicleLoanAccount)request.getAttribute("vehicleLoanAccount");
                      String disbursebtn="", statusenqbtn="";
                      if(vehicleLoanAccount!=null){
                        if("INITIATED".equals(vehicleLoanAccount.getDisbflag())){
                          disbursebtn="disabled";
                          statusenqbtn="";
                        }else if("SUCCESS".equals(vehicleLoanAccount.getDisbflag())){
                          disbursebtn="disabled";
                          statusenqbtn="disabled";
                        }else{
                          disbursebtn="";
                          statusenqbtn="hide";
                        }
                      }
                      VehicleLoanMaster master= (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
                      String loanAcctNo=master.getAccNumber();

                      if(loanAcctNo==null){
                        loanAcctNo="Not opened";
                      }
                    %>
                    <div class="row">
                    <div class="col-lg-12 disbdivaccno">
                      Loan Account Number : <%=loanAcctNo%> .
                    </div>

                  </div>
<%--                    <div class="mb-1 mt-1 ">--%>
<%--                      <button  type="button" class="btn btn-sm btn-light-primary <%=disbursebtn%>"  id="disbursebtn" >--%>
<%--                        <span class="indicator-label">Disburse loan</span>--%>
<%--                        <span class="indicator-progress">Please wait... <span class="spinner-border spinner-border-sm align-middle ms-2"></span></span>--%>
<%--                      </button>--%>
<%--                    </div>--%>
<%--                    <div class="mb-1 mt-1 ">--%>
<%--                      <button  type="button" class="btn btn-sm btn-light-primary <%=statusenqbtn%>"id="statenqbtn">--%>
<%--                        <span class="indicator-label">Check status</span>--%>
<%--                        <span class="indicator-progress">Please wait... <span class="spinner-border spinner-border-sm align-middle ms-2"></span></span>--%>
<%--                      </button>--%>

<%--                    </div>--%>
                    <!--
                    <div class="row">
                      <div class="col-lg-1">
                      </div>
                      <div class="col-lg-2" style="justify-content: end;display: flex;">
                        <button type="button" class="btn btn-success btn-sm  btn-file <=disbursebtn%>>" name="" id="disbursebtn">
                          Click here to disburse
                        </button>
                      </div>
                    </div>
                    <div class="row">
                      <div class="col-lg-1">
                      </div>
                      <div class="col-lg-2" style="justify-content: end;display: flex;">
                        <button type="button" class="btn btn-success btn-sm  btn-file <=disbursebtn%>>" name="" id="statenqbtn">
                                Click here to check disbursement status
                        </button>
                      </div>
                    </div>
                    -->
                    <%

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
