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

    jQuery.validator.addMethod("notEqualToGroup", function (value, element, options) {
      // get all the elements passed here with the same class
      var elems = $(element).parents('form').find(options[0]);
      // the value of the current element
      var valueToCompare = value;
      // count
      var matchesFound = 0;
      // loop each element and compare its value with the current value
      // and increase the count every time we find one
      jQuery.each(elems, function () {
        var thisVal = $(this).val();
        if (thisVal == valueToCompare) {
          matchesFound++;
        }
      });
      // count should be either 0 or 1 max
      if (this.optional(element) || matchesFound <= 1) {
        //elems.removeClass('error');
        return true;
      } else {
        //elems.addClass('error');
      }
    }, "Please enter a Unique Value.");

    if($('#errflag').val()=='true'){
      blockerMsg('Something went wrong');
      //$('#backbtn').click();
    }


    if($('#checker').val()=='1'){

      $('.emptable-addbtn').hide();
      $('#delbtn').hide();
      $('#acctlabelSaveBtn').hide();

      $('#acctlabelForm select').prop("disabled",true);
      $('#acctlabelForm input').attr('readonly', 'readonly');
    }

    $("#acctlabelForm").validate({
      rules: {
        acctLabel: {
          required:true, notEqualToGroup: ['.acctlabel'],
        }
      },
      messages: {

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


    // Handle save button click
    $("#acctlabelSaveBtn").on('click', function (e) {
      e.preventDefault();
      //if($("#acctlabelForm").valid()) {

      showLoader();
      var jsonArray = [];
      var errorflg = 0;
      $('#tbl tbody tr').each(function () {
        if ($(this).is(":visible")) {
          var selectValue = $(this).find('.select-box').val();
          var textValue = $(this).find('.text-box').val();
          if (selectValue == '' || textValue == '') {
            alertmsg("Please enter all fields");
            errorflg = "1";
            hideLoader();
            return;
          }

          if (selectValue === 'LOS_SOURCE') {
            var hasOtherSelects = false;

            $('#tbl tbody tr').each(function () {
              if ($(this).is(':visible')) {
                var otherSelectValue = $(this).find('.select-box').val();
                if (otherSelectValue !== 'LOS_SOURCE' && otherSelectValue !== '') {
                  hasOtherSelects = true;
                  return false; // break loop
                }
              }
            });

            if (!hasOtherSelects) {
              alertmsg("Please enter all fields");
              errorflg = "1";
              hideLoader();
              return;
            }
          }
          var rowObject = {
            "acctLabel": selectValue,
            "labelText": textValue
          };
          jsonArray.push(rowObject);
        }
      });
      var dto = {
        slno: $('#slno').val(),
        wiNum: $('#winum').val(),
        acctLabels: jsonArray
      };

      if (errorflg == '0') {

        var button = document.querySelector("#acctlabelSaveBtn");
        button.setAttribute("data-kt-indicator", "on");
        $('#acctlabelSaveBtn').attr("disabled",true);
      $.ajax({
        url: 'api/acctlabelsave', // Update with your API endpoint
        type: 'POST',
        data: JSON.stringify(dto),
        contentType: 'application/json',
        //async: false,
        success: function (response) {
          hideLoader();
          if (response.status === 'S') {
            var msg = 'Record Saved Successfully';
            notyalt(msg);
            var totalrows=+$('#tbl tbody tr:visible').length;
            $('#dbcount').val(totalrows);
          } else {
            alertmsg('Failed: ' + response.msg);
          }
          button.removeAttribute("data-kt-indicator");
          $('#acctlabelSaveBtn').attr("disabled",false);
        },
        error: function (xhr, status, error) {
          hideLoader();
          var err_data = xhr.responseJSON;
          if (err_data.msg) {
            alertmsgvert(err_data.msg);
          } else {
            alertmsg('An error occurred: ' + error);
          }
          button.removeAttribute("data-kt-indicator");
          $('#acctlabelSaveBtn').attr("disabled",false);
        }

      });
    }

      //}
    });
  });

  $(document).ready(function () {
    // Custom validation method to ensure distinct select values
    jQuery.validator.addMethod("notEqualToGroup", function (value, element, options) {
      var elems = $(element).closest('table').find(options[0]); // Adjusted for proper context
      var matchesFound = 0;

      // Loop through each element and compare its value with the current value
      elems.each(function () {
        if ($(this).val() === value && value !== "") {
          matchesFound++;
        }
      });

      // Validation is successful if there's only one match (the current element)
      return matchesFound === 1;
    }, "Please select a unique value.");


    // Example binding for a button that triggers row cloning
    $("#cloneRowButton").on("click", function() {
      cloneLastRow();
    });
  });


  function cloneLastRow() {
    var tableBody = $("#tbl").find("tbody");
    var trLast = tableBody.find("tr:last");
    var trforCloning = $('#forCloning');
    var trNew = trforCloning.clone();


    // Clear input fields in the cloned row
    trNew.find("input").val("");
    trNew.find("select").val(""); // Clear the select boxes in the cloned row
    trNew.removeAttr("style");
    // Append the cloned row to the table
    trLast.after(trNew);

    // Reapply validation to all select boxes with the 'acctlabel' class
    tableBody.find("select.acctlabel").each(function () {
      $(this).rules("add", {
        required: true,
        //notEqualToGroup: ['.acctlabel']
      });
    });
      tableBody.find("input.text-box").each(function () {
        $(this).rules("add", {
          required: true,
        });

    });
      $(this).valid(); // Trigger validation to show errors immediately
  }
  function cloneLastRow2() {
    var tableBody = $("#tbl").find("tbody");
    var trLast = tableBody.find("tr:last");
    var trNew = trLast.clone();

    // Clear input fields in the cloned row
    trNew.find("input").val("");

    // Apply the validation rule to the select boxes with the class 'acctlabel'
    trNew.find("select.acctlabel").each(function() {
      $(this).rules('add', {
        required: true,
        notEqualToGroup: ['.acctlabel']
      });
    });

    // Append the cloned row to the table
    trLast.after(trNew);

    // Revalidate all select elements to check for duplicates after appending
    tableBody.find('select.acctlabel').each(function() {
      $(this).valid();
    });
  }

  function cloneLastRow1(){

    var tableBody = $('#tbl').find("tbody");
    var trLast = tableBody.find("tr:last");
    var trNew = trLast.clone();
    trNew.find("input").val("");
    trNew.find(".text-box").rules('add', {
      required: true
    });
    trNew.find(".select-box").rules('add', {
      required: true, notEqualToGroup: ['.acctlabel']
    });
    trLast.after(trNew);
    //$("#tbl tbody").append(trNew);
    //var $clone=$('#tbl tbody tr:last').clone();
  }

  function delrowacclabel(elem){
    var totalrows=+$('#tbl tbody tr:visible').length;
    var dbcount= +$('#dbcount').val();
    if(totalrows==2 ){//if it is a fresh entry, prevent deletion if only a single row exists
      alertmsg('Cannot delete the last row');
    }else {
      $(elem).closest('tr').remove();
    }
  }


</script>

<div class="flex-stack border rounded  px-7 py-3 mb-2">
  <div class="w-100">
    <div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="acctLabelLink" data-bs-target="#acctlabelContent">
      <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
             for="acctlabelContent">
        <i class="ki-duotone ki-ranking fs-3x me-4">
          <span class="path1"></span>
          <span class="path2"></span>
        </i>
        <span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-4">Account labels</span>
                    <span class="text-muted fw-semibold fs-7">
                     Add Account labels(optional step)
                    </span>
                </span>
      </label>
    </div>
    <%
      VehicleLoanMaster master= (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
      EligibilityDetails eligibilityDetails= (EligibilityDetails) request.getAttribute("eligibilityDetails");
      VehicleLoanDetails loanDetails= (VehicleLoanDetails) request.getAttribute("loanDetails");

      String checker="0",readonly="";
      VehicleLoanSanMod vehicleLoanSanMod=null;
      if(request.getAttribute("checker")!=null) {
        checker = request.getAttribute("checker").toString();
      }
      List<VehicleLoanAcctLabels> vehicleLoanAcctLabels = master.getVehicleLoanAcctLabels();
      int count=0;
      if(vehicleLoanAcctLabels!=null){
        count=vehicleLoanAcctLabels.size();
      }

      if("1".equals(checker)){

      }
      if(master.getAccNumber()!=null){
        readonly="disabled";
        checker="1";
      }

    %>
    <input type="hidden" name="dbcount" id="dbcount" value="<%=count%>"/>
    <div id="acctlabelContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">

      <div class="row">
        <div class="col-sm-12">
          <!--begin::Repeater-->
          <div id="kt_docs_repeater_basic">

            <form id="acctlabelForm" name="acctlabelForm" method="POST">
              <div class="card">
                <div id="msgspan" class="alert alert-danger <%=readonly.equals("disabled")?"":"hide"%>">
                  <strong>Note!</strong> Editing is not allowed since account is already opened
                </div>

                <div id="emptable-sal-div-id" class="table-responsive border-white border-opacity-15 mb-3 emptable-sal-div">
                  <table class="table table-bordered " id="tbl" >
                    <thead>
                    <tr>
                      <th>Account label</th>
                      <th>Label value</th>
                      <th></th>
                    </tr>
                    </thead>

                    <tbody class="emptable-sal-pen-body" name="emptable-sal-pen-body">
                    <tr>
                      <td><input readonly type="text"  class="form-control select-box "  value="LOS_SOURCE"></td>
                      <td><input readonly type="text"  class="form-control text-box "  value="POWER DRIVE"></td>
                      <td></td>
                    </tr>
                    <tr style="display: none;" id="forCloning">
                      <td>
                        <select  name="acctlabel" class="form-control acctlabel select-box" onchange="AccountLabelChange(this.value,this)" >
                          <option value="">Please select</option>
                          <%
                            List<Misrct> titlesz = (List<Misrct>) request.getAttribute("acctlabels");
                            for (Misrct title : titlesz) {

                              out.println("<option value=\"" + title.getCodevalue()+ "\" "+(title.getCodevalue().equals("dummy") ? "selected" : "" )+" >" + title.getCodedesc() + "</option>");
                            }
                          %>
                        </select>
                      </td>
                      <td><input type="text" name="labeltext"  class="form-control text-box text-uppercase"  value=""></td>
                      <td>
                        <button type="button" onClick="delrowacclabel(this)" class="btn btn-flat-danger border-transparent btn-sm emptable-addbtn btn-file" id="delbtn">
                          <i class="ph-trash"></i>
                        </button>
                      </td>
                    </tr>
                    <%
                      int rownum=0;
                      if(vehicleLoanAcctLabels!=null && vehicleLoanAcctLabels.size()>0){
                        for(VehicleLoanAcctLabels vehicleLoanAcctLabels1:vehicleLoanAcctLabels){
                          if("LOS_SOURCE".equalsIgnoreCase(vehicleLoanAcctLabels1.getAcctLabel()))
                          {
                            continue;
                          }
                          rownum++;
                    %>
                    <tr>
                      <td>
                        <select  name="acctlabel" class="form-control acctlabel select-box" required onchange="AccountLabelChange(this.value,this)">
                          <option value="">Please select</option>
                          <%
                            List<Misrct> titles = (List<Misrct>) request.getAttribute("acctlabels");
                            for (Misrct title : titles) {

                              out.println("<option value=\"" + title.getCodevalue()+ "\" "+(title.getCodevalue().equals(vehicleLoanAcctLabels1.getAcctLabel()) ? "selected" : "" )+" >" + title.getCodedesc() + "</option>");
                            }
                          %>
                        </select>
                      </td>
                      <td>
                        <%if("11".equalsIgnoreCase(vehicleLoanAcctLabels1.getAcctLabel())){%>
                        <select  name="labeltext" class="form-control text-box text-uppercase" required >
                        <option value="PERSONAL" <%= "PERSONAL".equals(vehicleLoanAcctLabels1.getLabeltext()) ? "selected" : "" %>>PERSONAL</option>
                        <option value="BUSINESS" <%= "BUSINESS".equals(vehicleLoanAcctLabels1.getLabeltext()) ? "selected" : "" %>>BUSINESS</option>
                        </select>

                        <%}else {%>
                        <input type="text" name="labeltext"  class="form-control text-box text-uppercase" required value="<%=vehicleLoanAcctLabels1.getLabeltext()%>">
                        <%}%>
                      </td>
                      <td>
                        <button type="button" onClick="delrowacclabel(this)" class="btn btn-flat-danger border-transparent btn-sm emptable-addbtn btn-file" id="delbtn">
                          <i class="ph-trash"></i>
                        </button>
                      </td>
                    </tr>
                    <%
                      }
                      } else {
                      if(true){//for( VLEmploymentemp vlEmployment : vlEmploymentempList) {
                        if(true){//if(vlEmployment.getDelFlg().equals("N")){
                    %>
                    <tr>
                      <td>
                        <select  name="acctlabel" class="form-control acctlabel select-box" required onchange="AccountLabelChange(this.value,this)">
                          <option value="">Please select</option>
                          <%
                            List<Misrct> titles = (List<Misrct>) request.getAttribute("acctlabels");
                            for (Misrct title : titles) {

                              out.println("<option value=\"" + title.getCodevalue()+ "\" "+(title.getCodevalue().equals("dummy") ? "selected" : "" )+" >" + title.getCodedesc() + "</option>");
                            }
                          %>
                        </select>
                      </td>
                      <td><input type="text" name="labeltext" class="form-control text-box text-uppercase" required value=""></td>
                      <td>
                        <button type="button" onClick="delrowacclabel(this)" class="btn btn-flat-danger border-transparent btn-sm emptable-addbtn btn-file" id="delbtn">
                          <i class="ph-trash"></i>
                        </button>
                      </td>
                    </tr>
                    <%
                        }
                      }
                    }%>

                    </tbody>
                  </table>

                  <div  class="card-footer ">
                    <div class="row">
                      <div class="col-lg-10">
<%--                        <span class="badge bg-secondary bg-opacity-10 text-secondary mt-2">Note : Latest work experience should be entered on the top</span>--%>
                      </div>
                      <div class="col-lg-2" style="justify-content: end;display: flex;">
                        <button type="button" onclick="cloneLastRow()" class="btn btn-flat-primary btn-sm emptable-addbtn btn-file" name="emptable-addbtn">
                          <i class="ph-plus-circle  me-2"></i>
                          Add
                        </button>
                      </div>
                    </div>
                  </div>

                </div>

              </div>

            </form>
            <div class="text-end pt-5">


<%--              <button type="button" id="acctlabelSaveBtn" class="btn btn-sm btn-primary" <%=readonly%> data-kt-stepper-action="next">Save--%>
<%--                <i class="ki-duotone ki-double-right ">--%>
<%--                  <i class="path1"></i>--%>
<%--                  <i class="path2"></i>--%>
<%--                </i>--%>
<%--              </button>--%>
              <button  id="acctlabelSaveBtn" type="button" class="btn btn-sm btn-success" <%=readonly%>>
                <span class="indicator-label">Save</span>
                <span class="indicator-progress">
                    Please wait... <span class="spinner-border spinner-border-sm align-middle ms-2"></span>
                </span>
              </button>
            </div>

          </div>
          <!--end::Repeater-->
        </div>
      </div>
    </div>
  </div>
</div>

<script>
  function AccountLabelChange(value, el) {
    const row = el.closest('tr');
    const targetCell = row.cells[1]; // 2nd <td> in the row
    const currentElement = targetCell.querySelector('[name="labeltext"]');

    if (value === '11') {

      if (currentElement && currentElement.tagName.toLowerCase() === 'select') return;

      const select = document.createElement('select');
      select.name = 'labeltext';
      select.className = 'form-control text-box text-uppercase';

      select.innerHTML = `
      <option value="PERSONAL">PERSONAL</option>
      <option value="BUSINESS">BUSINESS</option>
      `;

      targetCell.replaceChild(select, currentElement);
    } else {

      if (currentElement && currentElement.tagName.toLowerCase() === 'input') return;

      const input = document.createElement('input');
      input.type = 'text';
      input.name = 'labeltext';
      input.className = 'form-control text-box text-uppercase';
      input.value = '';

      targetCell.replaceChild(input, currentElement);
    }
  }
</script>


