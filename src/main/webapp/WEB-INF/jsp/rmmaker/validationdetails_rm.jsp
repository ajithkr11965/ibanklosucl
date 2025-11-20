<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanFcvCpvCfr" %><%--
  Created by IntelliJ IDEA.
  User: SIBL11965
  Date: 25-07-2024
  Time: 10:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
	VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
	VehicleLoanFcvCpvCfr fcvCpvCfr = (VehicleLoanFcvCpvCfr) request.getAttribute("fcvCpvCfr");
	String previousQueue = (String) request.getAttribute("previousQueue");
    String hidfcvStatus="";
    String hidcpvStatus="";
    String hidcfrStatus="";
    String showFlg = "", accordion_style = "btn-active-light-primary";
		    boolean hidcpvFile=false;
            boolean hidfcvFile=false;
    if(fcvCpvCfr!=null) {
        hidfcvStatus=fcvCpvCfr.getFcvStatus();
        hidcpvStatus=fcvCpvCfr.getCpvStatus();
        hidcfrStatus=fcvCpvCfr.getCfrStatus();
        hidfcvFile=fcvCpvCfr.getFcvFileUploaded();
        hidcpvFile=fcvCpvCfr.getCpvFileUploaded();
        showFlg = "show";
       accordion_style = "btn-active-light-success";

    }

%>
<script>
    $(document).ready(function () {
        const wiNum = $('#winum').val();
        const slNo = $('#slno').val();
        var hidfcvStatus = $('#hidfcvStatus').val();
        var hidcpvStatus = $('#hidcpvStatus').val();

        function updateFileUploadVisibility(status, fileSection) {
            if (status !== 'NA') {
                $(fileSection).show();
            } else {
                $(fileSection).hide();
            }
        }
        updateFileUploadVisibility(hidfcvStatus, '#fileUploadFcvSection');
        updateFileUploadVisibility(hidcpvStatus, '#fileUploadCpvSection');
        $('#fcvStatus').change(function () {
        updateFileUploadVisibility($(this).val(), '#fileUploadFcvSection');
        if ($(this).val() === 'Negative') {
            alert('Work Item will be rejected. Recommend for rejection in DO remarks and send back to branch.');
        }
    });

    $('#cpvStatus').change(function () {
        updateFileUploadVisibility($(this).val(), '#fileUploadCpvSection');
        if ($(this).val() === 'Negative') {
            alert('Work Item will be rejected. Recommend for rejection in DO remarks and send back to branch.');
        }
    });



        // if ("PASS" === hidfcvStatus) {
        //     $('#fileUploadFcvSection').show();
        // }
        // if ("PASS" === hidcpvStatus) {
        //     $('#fileUploadCpvSection').show();
        // }
		//
        // $('#fcvStatus').change(function () {
        //     if ($(this).val() === 'PASS') {
        //         $('#fileUploadFcvSection').show();
        //     } else {
        //         $('#fileUploadFcvSection').hide();
        //     }
		//
        //     if ($(this).val() === 'Negative') {
        //         alert('Work Item will be rejected. Recommend for rejection in DO remarks and send back to branch.');
        //     }
        // });
        // $('#cpvStatus').change(function () {
        //     if ($(this).val() === 'PASS') {
        //         $('#fileUploadCpvSection').show();
        //     } else {
        //         $('#fileUploadCpvSection').hide();
        //     }
		//
        //     if ($(this).val() === 'Negative') {
        //         alert('Work Item will be rejected. Recommend for rejection in DO remarks and send back to branch.');
        //     }
        // });
        if ('${fcvCpvCfr.fcvStatus}' === 'PASS') {
            $('#fileUploadFcvSection').show();
        }
        if ('${fcvCpvCfr.cpvStatus}' === 'PASS') {
            $('#fileUploadCpvSection').show();
        }

        // $('#cfrFound').change(function () {
        //     if ($(this).val() === 'Yes') {
        //         console.log('Add deviation: CFR match is found');
        //     }
        // });

        if ('<%= previousQueue %>' === 'CS') {
            $('#cfrFound').val('Yes').trigger('change');
        }
        $('#kt_button_fcvcpvcfr').click(function (e) {
            e.preventDefault();

            $('.is-invalid').removeClass('is-invalid');

            let isValid = true;
            if ($('#fcvStatus').val() === '') {
                $('#fcvStatus').addClass('is-invalid');
                isValid = false;
            }
            if ($('#cpvStatus').val() === '') {
                $('#cpvStatus').addClass('is-invalid');
                isValid = false;
            }


            if ($('#cfrFound').val() === '') {
                $('#cfrFound').addClass('is-invalid');
                isValid = false;
            }

            function validateFileUpload(statusSelector, fileSelector, fileSection) {
                const status = $(statusSelector).val();
                if (status !== 'NA' &&
                    $(fileSelector).val() === '' &&
                    !$(fileSection + ' .text-success').length) {
                    $(fileSelector).addClass('is-invalid');
                    return false;
                }
                return true;
            }

            isValid = validateFileUpload('#fcvStatus', '#fileUploadFcv', '#fileUploadFcvSection') && isValid;
            isValid = validateFileUpload('#cpvStatus', '#fileUploadCpv', '#fileUploadCpvSection') && isValid;

            if (isValid) {
                saveFcvCpvCfrDetails();
            } else {
                console.log('Form is invalid. Please check the fields.');
                alertmsg('Please fill in all required fields and upload necessary files.');
            }
        });



        // $('#kt_button_fcvcpvcfr').click(function (e) {
        //     e.preventDefault();
		//
		//
        //     $('.is-invalid').removeClass('is-invalid');
		//
        //     let isValid = true;
		//

		//
        //     // if ($('#fcvStatus').val() === 'PASS' && $('#fileUploadFcv').val() === '' && hidfcvStatus === '') {
        //     //     $('#fileUploadFcv').addClass('is-invalid');
        //     //     isValid = false;
        //     // }
        //     // if ($('#cpvStatus').val() === 'PASS' && $('#fileUploadCpv').val() === '' && hidcpvStatus === '') {
        //     //     $('#fileUploadCpv').addClass('is-invalid');
        //     //     isValid = false;
        //     // }
        //     if (($('#fcvStatus').val() === 'PASS' || hidfcvStatus === 'PASS') &&
        //         $('#fileUploadFcv').val() === '' &&
        //         !$('#fileUploadFcvSection .text-success').length) {
        //         $('#fileUploadFcv').addClass('is-invalid');
        //         isValid = false;
        //     }
        //     if (($('#cpvStatus').val() === 'PASS' || hidcpvStatus === 'PASS') &&
        //         $('#fileUploadCpv').val() === '' &&
        //         !$('#fileUploadCpvSection .text-success').length) {
        //         $('#fileUploadCpv').addClass('is-invalid');
        //         isValid = false;
        //     }
		//
		//
        //     if (isValid) {
        //         saveFcvCpvCfrDetails();
		//
        //     } else {
        //         console.log('Form is invalid. Please check the fields.');
        //         alertmsg('Please fill in all required fields and upload necessary files.');
        //     }
        // });

        function saveFcvCpvCfrDetails() {
            let valid = true;
            let form = $('#fcvCpvCfrForm')[0];
            let formData = new FormData(form);
            $('input[type="file"]').each(function () {
                const fileInput = $(this);
                const isMandatory = fileInput.attr('data-mandatory');
                const label = fileInput.data('label');
                const files = fileInput[0].files;
                const fileName = files.length ? files[0].name : '';
                const fileExtension = fileName.split('.').pop().toLowerCase();
                const allowedExtensions = ['pdf', 'jpg'];

                if (isMandatory === 'Y' && files.length === 0) {
                    alertmsg('Kindly attach a file for ' + label);
                    valid = false;
                    return false; // break out of each loop
                }

                if (files.length > 0 && !allowedExtensions.includes(fileExtension)) {
                    alertmsg('Only PDF and JPG files are allowed.');
                    valid = false;
                    return false; // break out of each loop
                }
            });
            // formData.append('fcvStatus', $('#fcvStatus').val());
            // formData.append('cpvStatus', $('#cpvStatus').val());
            // formData.append('cfrFound', $('#cfrFound').val());
            formData.append('wiNum', wiNum);
            formData.append('slno', slNo);

            // Append file if it exists
            var fileInputFcv = $('#fileUploadFcv')[0];
            if (fileInputFcv.files.length > 0) {
                formData.append('fileUploadFcv', fileInputFcv.files[0]);
            }
            var fileInputCpv = $('#fileUploadCpv')[0];
            if (fileInputCpv.files.length > 0) {
                formData.append('fileUploadCpv', fileInputCpv.files[0]);
            }
            var fileUploadCfr = $('#fileUploadCfr')[0];
            if (fileUploadCfr.files.length > 0) {
                formData.append('fileUploadCfr', fileUploadCfr.files[0]);
            }
             showLoader();

            $.ajax({
                url: 'apicpc/fcv-cpv-cfr-save',
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function (response) {
                    console.log('Details saved successfully:', response);
                     hideLoader();
                    alertmsg('FCV/CPV/CFR details saved successfully.');
                    updateDeviationDetails();
                    updateAccordionStyle('validationDetailslink', true);

                },
                error: function (xhr, status, error) {
                    console.error('Error saving details:', error);
                     hideLoader();
                    alertmsg('Error saving FCV/CPV/CFR details: ' + xhr.responseText);
                }
            });
             hideLoader();
        }

        function updateDeviationDetails() {
            $.ajax({
                url: 'get-deviation-details',
                type: 'GET',
                data: {wiNum: $('#winum').val(), slno: $('#slno').val()},
                success: function (response) {
                    // Update the deviation details content
                    $('#deviation_parent').html('');
                    $('#deviation_parent').html(response);

                    // Re-initialize any necessary JavaScript for the updated content
                    //initializeDeviationDetails();
                },
                error: function (xhr, status, error) {
                    console.error('Error fetching deviation details:', error);
                }
            });
        }

    });
</script>
<div class="d-flex flex-stack border rounded px-7 py-3 mb-2">
	<div class="">
		<div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="validationDetailslink" data-bs-target="#validationDetailsContent">
			<label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2 <%=accordion_style%> <%=showFlg%>"
			       for="validationDetailsContent">
				<i class="ki-duotone ki-question fs-3x me-4">
					<span class="path1"></span>
					<span class="path2"></span>
					<span class="path3"></span>
				</i>
				<span class="d-block fw-semibold text-start">
                        <span class="text-gray-900 fw-bold d-block fs-4">FCV/CPC/CFR Details</span>
                        <span class="text-muted fw-semibold fs-7">
                         Enter FCV/CPC/CFR Details details.
                        </span>
                    </span>
			</label>
		</div>
		<div id="validationDetailsContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">
			<form id="fcvCpvCfrForm" name="fcvCpvCfrForm" enctype="multipart/form-data">
				<input type="hidden" name="hidfcvStatus" id="hidfcvStatus" value="<%=hidfcvStatus!=null ?hidfcvStatus:""%>">
				<input type="hidden" name="hidcpvStatus" id="hidcpvStatus" value="<%=hidcpvStatus!=null?hidcpvStatus:""%>">
				<div class="row mb-3">
					<div class="col-md-4">
						<label for="fcvStatus" class="form-label">FCV Status:</label>
						<select id="fcvStatus" name="fcvStatus" class="form-select" required>
							<option value="">Select</option>
							<option value="PASS" <%= fcvCpvCfr != null && "PASS".equals(hidfcvStatus) ? "selected" : "" %>>PASS</option>
							<option value="NA" <%= fcvCpvCfr != null && "NA".equals(hidfcvStatus) ? "selected" : "" %>>NA</option>
							<option value="Negative" <%= fcvCpvCfr != null && "Negative".equals(hidfcvStatus) ? "selected" : "" %>>Negative</option>
							<option value="Refer to credit" <%= fcvCpvCfr != null && "Refer to credit".equals(hidfcvStatus) ? "selected" : "" %>>Refer to credit
							</option>

						</select>
					</div>
					<div class="col-md-4">
						<label for="cpvStatus" class="form-label">CPV Status:</label>
						<select id="cpvStatus" name="cpvStatus" class="form-select" required>
							<option value="">Select</option>
							<option value="PASS" <%= fcvCpvCfr != null && "PASS".equals(hidcpvStatus) ? "selected" : "" %>>PASS</option>
							<option value="NA" <%= fcvCpvCfr != null && "NA".equals(hidcpvStatus) ? "selected" : "" %>>NA</option>
							<option value="Negative" <%= fcvCpvCfr != null && "Negative".equals(hidcpvStatus) ? "selected" : "" %>>Negative</option>
							<option value="Refer to credit" <%= fcvCpvCfr != null && "Refer to credit".equals(hidcpvStatus) ? "selected" : "" %>>Refer to credit
							</option>

						</select>
					</div>
					<div class="col-md-4">
						<label for="cfrFound" class="form-label">Whether CFR Found:</label>
						<select id="cfrFound" name="cfrFound" class="form-select" required>
							<option value="">Select</option>
							<option value="Yes" <%= fcvCpvCfr != null && "Yes".equals(hidcfrStatus) ? "selected" : "" %>>Yes</option>
							<option value="No" <%= fcvCpvCfr != null && "No".equals(hidcfrStatus) ? "selected" : "" %>>No</option>

						</select>
					</div>
				</div>
				<div id="fileUploadFcvSection" class="row mb-3" style="display: none;">
					<div class="col-md-6">
						<label for="fileUploadFcv" class="form-label">Upload FCV File:</label>
						<input type="file" id="fileUploadFcv" name="fileUploadFcv" class="form-control" required>
						<% if (fcvCpvCfr != null && fcvCpvCfr.getFcvFileUploaded() != null && fcvCpvCfr.getFcvFileUploaded()) { %>
						<span class="text-success">File uploaded</span>
						<% } %>
					</div>
				</div>
				<div id="fileUploadCpvSection" class="row mb-3" style="display: none;">
					<div class="col-md-6">
						<label for="fileUploadCpv" class="form-label">Upload CPV File:</label>
						<input type="file" id="fileUploadCpv" name="fileUploadCpv" class="form-control" required>
						<% if (fcvCpvCfr != null && fcvCpvCfr.getCpvFileUploaded() != null && fcvCpvCfr.getCpvFileUploaded()) { %>
						<span class="text-success">File uploaded</span>
						<% } %>


					</div>
				</div>
				<div id="fileUploadCfrSection" class="row mb-3" >
					<div class="col-md-6">
						<label for="fileUploadCfr" class="form-label">Upload CFR File:</label>
						<input type="file" id="fileUploadCfr" name="fileUploadCfr" class="form-control">
						<% if (fcvCpvCfr != null && fcvCpvCfr.getCfrFileUploaded()!= null && fcvCpvCfr.getCfrFileUploaded()) { %>
						<span class="text-success">File uploaded</span>
						<% } %>
					</div>
				</div>
				<div class="row">
					<div class="kt col-md-12">
<%--						<button type="button" id="fcvcpvcfr" class="btn btn-primary fcvcpvcfr">Save</button>--%>
						<button id="kt_button_fcvcpvcfr" type="button"  class="btn btn-sm btn-light-primary fcvcpvcfrSave">
																<span class="indicator-label">Save</span>
																<span class="indicator-progress">
        Please wait... <span class="spinner-border spinner-border-sm align-middle ms-2"></span>
    </span>

															</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>
