<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.model.doc.VehicleLoanRepayment" %>
<%@ page import="javax.persistence.Column" %>
<%@ page import="com.sib.ibanklosucl.model.Misrct" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 14-08-2024
  Time: 01:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<script>
  $(document).ready(function () {
    // Initialize form validation
    $("#bankDetailsForm").validate({
      rules: {
        bankName: {
          required: true
        },
        accountNumber: {
          required: true,
          regex: /^[a-zA-Z0-9\s]{9,18}$/,
          minlength: 9, // NACH requires at least 9 digits for account numbers
          maxlength: 18 // NACH typically handles up to 18 digits
        },
        ifscCode: {
          required: true,
          regex: /^[A-Z]{4}0[A-Z0-9]{6}$/
        },
        borrowerName: {
          required: true,
          regex: /^[a-zA-Z\s]{1,50}$/
        }
      },
      messages: {
        ifscCode: {
          required: "IFSC code is required",
          regex: "Please enter a valid IFSC code (e.g., ABCD0123456)"
        },
        borrowerName: {
          required: "Borrower name is required",
          regex: "Borrower name should contain only letters and spaces, and be up to 50 characters"
        },
        accountNumber: {
          required: "Account number is required",
          digits: "Please enter a valid account number",
          minlength: "Account number must be at least 9 digits",
          maxlength: "Account number must not exceed 18 digits"
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
        error.css({'padding-left': '23px', 'margin-right': '20px', 'padding-bottom': '2px', 'color': 'red', 'font-size': 'small'});
        error.addClass("validation-invalid-label");
        error.insertAfter(element);
      }
    });
    $.validator.addMethod("regex", function(value, element, regexpr) {
      return regexpr.test(value);
    }, "Invalid format");


    // Handle save button click
    $("#repsaveBtn").on('click', function (e) {
      e.preventDefault();
      if($("#bankDetailsForm").valid()){
        showLoader();
        var formData = {
          bankName: $('#bankName').val(),
          accountNumber: $('#accountNumber').val(),
          ifscCode: $('#ifscCode').val(),
          borrowerName: $('#borrowerName').val(),
          slno : $('#slno').val(),
          wiNum:$('#winum').val()
        };
        $.ajax({
          url: 'doc/updateRepaymentDetails', // Update with your API endpoint
          type: 'POST',
          data: JSON.stringify(formData),
          contentType: 'application/json',
          success: function(response) {
            hideLoader();
            if (response.status === 'S') {
              notyalt('Record Saved Successfully');
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

</script>
<%
  VehicleLoanRepayment vehicleLoanRepayment= (VehicleLoanRepayment) request.getAttribute("repaymentDetails");
 String bankName="",repaymentAccno="",ifsc="",borrowerName="";
 if(vehicleLoanRepayment!=null){
   bankName= vehicleLoanRepayment.getBankName();
   repaymentAccno= vehicleLoanRepayment.getAccountNumber();
   ifsc= vehicleLoanRepayment.getIfscCode();
   borrowerName= vehicleLoanRepayment.getBorrowerName();
 }
%>
<div class="flex-stack border rounded hide sancApprove px-7 py-3 mb-2">
  <div class="w-100">
    <div class="accordion-header d-flex collapsed  " data-bs-toggle="collapse" id="decisionDetailslink" data-bs-target="#repayDetailsContent">
      <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
             for="repayDetailsContent">
        <i class="ki-duotone ki-wallet  fs-3x me-4">
          <span class="path1"></span>
          <span class="path2"></span>
        </i>
        <span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-4">Repayment Details</span>
                    <span class="text-muted fw-semibold fs-7">
                     Enter Repayment details.
                    </span>
                </span>
      </label>
    </div>
    <div id="repayDetailsContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">

      <div class="row">
        <div class="col-sm-12">
          <!--begin::Repeater-->
          <div id="kt_docs_repeater_basic">
            <form id="bankDetailsForm" name="bankDetailsForm" method="POST">
              <!-- Bank Name -->
              <div class="mb-3">
                <label for="bankName" class="form-label">Bank Name</label>
                <select name="bankName" id="bankName" class="form-select" readonly disabled>
                  <option value="" selected>Select Bank</option>
                  <%
                  List<Misrct> bankList= (List<Misrct>) request.getAttribute("bankName");
                    for (Misrct bm : bankList) {
                      out.println("<option value=\"" + bm.getCodevalue() + "\" "+(bm.getCodevalue().equals(bankName) ? "selected" : "" )+" >" + bm.getCodedesc()  + "</option>");
                    }
                  %>
                </select>
              </div>

              <!-- Account Number -->
              <div class="mb-3">
                <label for="accountNumber" class="form-label">Account Number</label>
                <input type="text" disabled class="form-control" id="accountNumber" readonly name="accountNumber" required value="<%=repaymentAccno%>" />
              </div>

              <!-- IFSC Code -->
              <div class="mb-3">
                <label for="ifscCode" class="form-label">IFSC Code</label>
                <input type="text" disabled class="form-control" id="ifscCode" readonly name="ifscCode" minlength="11" maxlength="11" required value="<%=ifsc%>" />
              </div>

              <!-- Borrower Name -->
              <div class="mb-3">
                <label for="borrowerName" class="form-label">Borrower Name</label>
                <input type="text" disabled readonly class="form-control" id="borrowerName" name="borrowerName" required value="<%=borrowerName%>" />
              </div>

            </form>



          </div>
          <!--end::Repeater-->
        </div>
      </div>
    </div>
  </div>
</div>

