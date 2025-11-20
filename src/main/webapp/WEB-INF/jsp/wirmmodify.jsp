<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.dto.ResponseDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="com.sib.ibanklosucl.model.VLEmployment" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="los" uri="http://www.siblos.com/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en" dir="ltr">
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<meta name="_csrf" content="${_csrf.token}"/>
		<meta name="_csrf_header" content="${_csrf.headerName}"/>
		<title>SIB-LOS RM -Entry</title>

		<!-- Global stylesheets -->
		<link href="assets/fonts/inter/inter.css" rel="stylesheet" type="text/css">
		<link href="assets/icons/phosphor/styles.min.css" rel="stylesheet" type="text/css">
		<link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
		<link href="assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
		<link href="assets/css/custom.css" rel="stylesheet" type="text/css"/>
		<link href="assets/css/custom/wirmmodify.css" rel="stylesheet" type="text/css"/>
		<link href="assets/css/style.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
		<link href="assets/plugins/global/plugins.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
		<!-- /global stylesheets -->

		<!-- Core JS files -->

		<script src="assets/js/bootstrap/bootstrap.bundle.min.js"></script>
		<script src="assets/js/jquery/jquery.min.js"></script>
		<script src="assets/js/vendor/forms/validation/validate.min.js"></script>
		<script src="assets/js/vendor/split.min.js"></script>
		<!-- /core JS files -->
		<script src="assets/demo/pages/components_tooltips.js"></script>
		<!-- Theme JS files -->
		<script src="assets/js/app.js"></script>
		<script src="assets/js/vendor/uploaders/fileinput/fileinput.min.js"></script>
		<script src="assets/js/vendor/uploaders/fileinput/plugins/sortable.min.js"></script>
		<script src="assets/js/vendor/notifications/sweet_alert.min.js"></script>
		<script src="assets/js/vendor/notifications/noty.min.js"></script>
		<script src="assets/js/vendor/pickers/datepicker.min.js"></script>
		<script src="assets/demo/pages/picker_date.js"></script>
		<%--    <script src="assets/js/custom/progress.js"></script>--%>

		<!-- /theme JS files -->

		<script>

            // Modify the selector based on decision value


            function alertmsgframe() {
                $('#alert_modal .modal-header').removeClass('bg-danger').addClass('bg-success');
                $('#alert_modal .modal-header').find('.modal-title').text('Remarks');
                $('#alert_modal .modal-body').html('<iframe id="modalIframe" src="remarks?slno=' + $('#slno').val() + '" width="100%" height="400" frameborder="0"></iframe>');
                $('#alert_modal').modal('show');
            }
             function formatPPC(ppc) {
            if (!ppc.id) {
                return ppc.text;
            }

            if (ppc.children) {
                // This is a group header
                return $('<strong>' + ppc.text + '</strong>');
            } else {
                // This is a PPC entry
                return $('<span>' + ppc.text + ' (' + ppc.id + ')</span>');
            }
        }

        function formatPPCSelection(ppc) {
            if (!ppc.id) {
                return ppc.text;
            }
            return ppc.text + (ppc.children ? '' : ' (' + ppc.id + ')');
        }

        // Optional: Add change event handler if you need to do something when selection changes
        $('#ppcSelect').on('change', function() {
            const selectedPPC = $(this).select2('data')[0];
            if (selectedPPC && !selectedPPC.children) {
                console.log('Selected PPC:', selectedPPC.id, selectedPPC.text);
                // Add your handling code here
            }
        });

            $(document).ready(function () {
                console.log("Document ready, setting up event handlers");
                const ppcData = ${ppcData};
                $('#ppcSelect').select2({
                data: ppcData,
                placeholder: 'Select a PPC',
                allowClear: true,
                templateResult: formatPPC,
                templateSelection: formatPPCSelection
            });
                $('#ppcSelectContainer').hide();
                $('#decision_panel').on('change', function() {
    var selectedLevel = $(this).val();
        const decisionValue = $(this).val();
    var ppcSelectContainer = $('#ppcSelectContainer');
    var ppcSelect = $('#ppcSelect');

    // Clear current selection
    ppcSelect.val(null).trigger('change');

    // Hide PPC selection for empty, SB, or HU decisions
    if (!selectedLevel || selectedLevel === 'SB' || selectedLevel === 'HU') {
        ppcSelectContainer.hide();
        return;
    }

    // Show container and load PPCs for selected level
    ppcSelectContainer.show();

    // Fetch PPCs for selected level
    $.ajax({
        url: 'apicpc/ppcs/' + selectedLevel,
        type: 'GET',
        beforeSend: function() {
            ppcSelect.prop('disabled', true);
        },
        success: function(response) {
            // Clear existing options
            ppcSelect.empty();

            // Add default empty option
            ppcSelect.append(new Option('Select PPC', '', true, true));

            // Find the matching level object and get its children
            response.forEach(function(levelObj) {
                if (levelObj.id === selectedLevel && levelObj.children) {
                    // Add PPC options from children array
                    levelObj.children.forEach(function(ppc) {
                        ppcSelect.append(new Option(ppc.text, ppc.id, false, false));
                    });
                }
            });

            // Initialize or refresh Select2
            ppcSelect.trigger('change');
        },
        error: function(xhr, status, error) {
            console.error('Error loading PPCs:', error);
            alertmsg('Error loading PPCs for selected level');
        },
        complete: function() {
            ppcSelect.prop('disabled', false);
        }
    });
    if (decisionValue === 'SB' || decisionValue === 'HU') {
        $('#pre_cond_rmks, #ppcSelect').each(function() {
            $(this).removeClass('is-invalid');
            if ($(this).is('textarea')) {
                $(this).closest('.input-group').next('.error-message').remove();
            } else {
                $(this).siblings('.error-message').remove();
            }
        });
    }

});

                if ($('#bureauBlock')) {
                    if ($('#bureauBlock').val() === 'Y') {
                        alertmsg("Bureau has been fetched before 45 days,Kindly send back the WI")
                    }
                }
                $("#remarkHist").on('click', function (e) {
                    e.preventDefault();
                    e.stopPropagation();
                    alertmsgframe();
                });
                 $('.maxlength-textarea').maxlength({
                    alwaysShow: true
                });
                $('#vl_rm_int').on('click', '.runhunterCheck', function (e) {

                    var vhidbreVal = $('#vhidbreVal').val();
                    console.log("in bre got" + vhidbreVal);
                    var applicantHunterData = $(this).closest("tr").find(".applicantHunterData");
                    var applicantHunterData = $(this).closest("tr").find(".applicantHunterData");
                    var applicantId = applicantHunterData.data("applicantid");
                    var button = document.querySelector("#kt_button_hunt_" + applicantId);
                    button.setAttribute("data-kt-indicator", "on");
                   // button.setAttribute("disabled", true);
                    var wiNum = $('#winum').val();
                    var slno = $('#slno').val();
                    var $row = $(this).closest("tr");
                    $.ajax({
                        url: 'api/checker/experian-hunter',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify({
                            applicantId: applicantId.toString(),
                            wiNum: wiNum,
                            slno: slno
                        }),
                        success: function (response) {
                            console.log('Experian Hunter API call successful:', response);
                            if (response.errorMessage) {
                                alertmsg(response.errorMessage);
                            } else if (response.matchfound) {
                                alertmsg("Hunter match found. Forward the workitem to Hunter Queue.");
                            }
                            updateHunterResults($row, response);
                            button.removeAttribute("data-kt-indicator");
                           // button.setAttribute("disabled", false);
                            // Handle the response as needed
                        },
                        error: function (xhr, status, error) {
                            console.error('Error calling Experian Hunter API:', error);
                            button.removeAttribute("data-kt-indicator");
                            //button.setAttribute("disabled", false);
                            // Handle the error as needed
                        }
                    });

                    function updateHunterResults($row, response) {
                        var currentDate = new Date().toLocaleString();
                        $row.find('.runDate').text(currentDate);
                        $row.find('.score').text(response.score);
                        $row.find('.decision').text(response.decision);
                        $row.find('.status').text(response.errorMessage ? 'Error' : (response.status || ''));
                        $row.find('.runhunterCheck').text('Re-run Hunter');
                    }
                });
                $('#vl_rm_int').on('click', '.runblacklistCheck', function (e) {
                    console.log("blacklist check initiated");
                    var applicantData = $(this).closest("tr").find(".applicantData");
                    var applicantId = applicantData.data("applicantid");
                    var button = document.querySelector("#kt_button_" + applicantId);
                    button.setAttribute("data-kt-indicator", "on");
                    button.setAttribute("disabled", true);
                    event.preventDefault();
                    var formData = {
                        "request": {
                            "DOB": applicantData.data("dob"),
                            "Pan": applicantData.data("pan"),
                            "Passport": applicantData.data("passport")
                        },
                        "mock": false,
                        "apiName": "blacklistExactMatch",
                        "workItemNumber": $('#winum').val(),
                        "origin": applicantData.data("applicantid"),
                        "slno": $('#slno').val()
                    };

                    $.ajax({
                        type: "POST",
                        url: "api/checker/checkBlacklist",
                        contentType: "application/json",
                        data: JSON.stringify(formData),
                        success: function (response) {
                            var rowCells = applicantData.closest("tr").find("td");
                            if (response.blacklisted) {
                                rowCells.eq(7).text(new Date().toLocaleDateString());
                                rowCells.eq(8).html('<span class="badge bg-danger">Blacklisted</span>');
                            } else {
                                rowCells.eq(7).text(new Date().toLocaleDateString());
                                rowCells.eq(8).html('<span class="badge bg-success">Not Blacklisted</span>');
                            }
                            button.removeAttribute("data-kt-indicator");
                            validateAllApplicants();
                            button.setAttribute("disabled", false);
                        },
                        error: function (xhr, status, error) {
                            $("#result").text("Error occurred: " + error);
                            button.removeAttribute("data-kt-indicator");
                            button.setAttribute("disabled", false);
                        }
                    });
                });

                function validateAllApplicants() {
                    var allNotBlacklisted = true;
                    $('#blacklisttable tbody tr').each(function () {
                        var status = $(this).find('td').eq(8).text();
                        if (status !== 'Not Blacklisted') {
                            allNotBlacklisted = false;
                            return false;
                        }
                    });

                    if (allNotBlacklisted) {
                        $('.blacklistSave').prop('disabled', false);
                    } else {
                        $('.blacklistSave').prop('disabled', true);
                    }
                }

                $('#kt_button_runDKScore').on('click', function () {
                    var winum = $('#winum').val();
                    var slno = $('#slno').val();
                    var $button = $(this);
                    var $table = $('#dkScoreTable');
                    var button = document.querySelector("#kt_button_runDKScore");
                    button.setAttribute("data-kt-indicator", "on");
                    button.setAttribute("disabled", true);

                    //$button.prop('disabled', true).html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Running...');

                    $.ajax({
                        url: 'api/checker/dk-score',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify({
                            workItemNumber: winum,
                            slno: slno
                        }),
                        success: function (response) {
                            console.log('DK Score API call successful:', response);
                            updateDKScoreTable(response.dkScoreItems);
                            button.removeAttribute("data-kt-indicator");
                            button.setAttribute("disabled", false);
                            updateAccordionStyle('dkScoreDetails', true);
                            $('#dkScoreTableContainer').show();
                            $('#additionalDetails').show();
                        },
                        error: function (xhr, status, error) {
                            button.removeAttribute("data-kt-indicator");
                            button.setAttribute("disabled", false);
                            console.error('Error calling DK Score API:', error);
                            alert('Error running DK Score check. Please try again.');
                        },
                        complete: function () {
                            button.removeAttribute("data-kt-indicator");
                            button.setAttribute("disabled", false);
                        }
                    });
                });
                $('#vl_rm_int').on('click', '.hunterlistSave', function (e) {
                    console.log("blacklist save initiated");
                    var winum = $('#winum').val();
                    var slno = $('#slno').val();
                    var formData = {
                        "slno": slno,
                        "wiNum": winum,
                        "identifier": "HUNTER",
                        "updValue": "Y"
                    };
                    $.ajax({
                        type: "POST",
                        url: "api/checker/updateBlacklistOption",
                        contentType: "application/json",
                        data: JSON.stringify(formData),
                        success: function (response) {
                            if (response.msg === "SUCCESS") {
                                $('#huntertable').collapse('hide');
                                $('#blacklisttable').collapse('show');
                                alertmsg("Hunter check details updated");
                                updateAccordionStyle('hunterlistDetails', true);
                            } else {
                                alertmsg("Hunter check details update failed - " + response.msg);
                            }
                        },
                        error: function (xhr, status, error) {
                            alert("Error occurred: " + error);
                        }
                    });
                });
                $('#vl_rm_int').on('click', '.blacklistSave', function (e) {
                    console.log("blacklist save initiated");
                    var winum = $('#winum').val();
                    var slno = $('#slno').val();
                    var formData = {
                        "slno": slno,
                        "wiNum": winum,
                        "identifier": "BLACKLIST",
                        "updValue": "Y"
                    };
                    $.ajax({
                        type: "POST",
                        url: "api/checker/updateBlacklistOption",
                        contentType: "application/json",
                        data: JSON.stringify(formData),
                        success: function (response) {
                            if (response.msg === "SUCCESS") {
                                $('#blacklisttable').collapse('hide');
                                $('#bretable').collapse('show');
                                alertmsg("Blacklist check details updated");
                                updateAccordionStyle('blacklistDetails', true);
                            } else {
                                alertmsg("Blacklist check details update failed - " + response.msg);
                            }
                        },
                        error: function (xhr, status, error) {
                            alert("Error occurred: " + error);
                        }
                    });
                });

                function validateAndSubmitForm() {
                    console.log("Validating form before submission");
                    var $submitButton = $('#maker_forward');
                    var $form = $('#rbcpcMakerForm');
                    var remarks = $('#rm_remarks').val();
                    var predisbcond = $('#pre_cond_rmks').val();
                    var decision = $('#decision_panel').val();
                    var winum = $('#winum').val();
                    var slno = $('#slno').val();

                    // Disable the submit button
                    $submitButton.prop('disabled', true);
                    console.log("Submit button disabled");
                    if (decision === "SB" || decision == "HU") {
                        submitForm(winum, slno, remarks, predisbcond, decision);
                    } else {
                    // First, perform the server-side validation
                    $.ajax({
                        url: 'apicpc/api/validate-rm-maker',
                        type: 'POST',
                        data: {
                            winum: winum,
                            slno: slno,
                            decision: decision
                        },
                        success: function (validationResponse) {
                            if (validationResponse.valid) {
                                // If validation passes, proceed with form submission
                                submitForm(winum, slno, remarks, predisbcond, decision);
                            } else {
                                // If validation fails, show errors and re-enable submit button
                                console.error('Validation failed:', validationResponse.errors);
                                displayStructuredError(validationResponse.errors);
                                $submitButton.prop('disabled', false);
                            }
                        },
                        error: function (xhr, status, error) {
                            console.error('Validation AJAX Error:', error);
                            alert('An error occurred during validation. Please try again.');
                            $submitButton.prop('disabled', false);
                        }
                    });
                }
                }

                function submitForm(winum, slno, remarks, predisbcond, decision) {
                    console.log("Submitting form");

                    $.ajax({
                        url: 'apicpc/rbcpc-maker-submit',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify({
                            winum: winum,
                            slno: slno,
                            remarks: remarks,
                            preDisbursementCondition: predisbcond,
                            decision: decision,
	                        rbcpcCheckerUser:$('#ppcSelect').val()
                        }),
                        success: function (response) {
                            console.log("AJAX success. Response:", response);
                            if (response.error) {
                                console.error('Error from server:', response.error);
                                displayStructuredError('Error: ' + response.error);
                                $('#maker_forward').prop('disabled', false);
                            } else {
                                console.log('Success:', response.message);
                                alertmsg('Success: ' + response.message);
                                hideLoader();
                                Swal.fire({
                                    title: 'Success',
                                    text: 'Details submitted successfully.',
                                    icon: 'success',
                                    confirmButtonText: 'OK'
                                }).then((result) => {
                                    if (result.isConfirmed) {
                                        window.location.href = "cpcmakerlist";
                                        // var form = $('<form action="cpcmakerlist" method="post">' +
                                        //     '<input type="hidden" name="action" value="RM">' +
                                        //     '<input type="hidden" name="slno" value="' + slno + '">' +
                                        //     '<input type="hidden" name="winum" value="' + winum + '">' +
                                        //     '<input type="hidden" name="lockflg" value="' + $('#lockflg').val() + '">'
                                        // );
                                        // $('body').append(form);
                                        // form.submit();
                                    }
                                });
                            }
                        },
                        error: function (xhr, status, error) {
                            console.error('AJAX Error:', error);
                            console.error('Status:', status);
                            console.error('Response:', xhr.responseText);
                            displayStructuredError('An error occurred. Please try again.');
                            $('#maker_forward').prop('disabled', false);
                        },
                        complete: function () {
                            console.log("AJAX request completed");
                        }
                    });
                }

                $('#maker_forward').click(function (event) {
                    console.log("Submit button clicked");
                    event.preventDefault();
                    if (validateForm()) {
                        var decision = $('#decision_panel').val();
                        var remarks = $('#rm_remarks').val();
                        var predisbcond = $('#pre_cond_rmks').val();
                        var winum = $('#winum').val();
                        var slno = $('#slno').val();
                        console.log("Form validation passed, proceeding with submission");
                        if (decision === "SB") {
                            console.log("Decision is SB, bypassing validation and submitting form directly");
                            submitForm(winum, slno, remarks, predisbcond, decision);
                        } else {
                            validateAndSubmitForm();
                        }
                    } else {
                        console.log("Form validation failed");
                    }
                });

                function validateForm() {
                    console.log("Starting form validation");
                    var isValid = true;
                    const decisionValue = $('#decision_panel').val();
	                let notSelector = '#rm_remarks';
				    if (decisionValue && (decisionValue === 'SB' || decisionValue === 'HU')) {
				        notSelector += ', #pre_cond_rmks, #ppcSelect';
				    }


                    // Validate remarks field (mandatory at all times)
                    var remarksField = $('#rm_remarks');
                    console.log("Validating remarks field. Value:", remarksField.val());
                    if (remarksField.val().trim() === '') {
                        console.log("Remarks field is empty");
                        isValid = false;
                        remarksField.addClass('is-invalid');
                        if (!remarksField.siblings('.error-message').length) {
                            $('<div class="error-message text-danger">Remarks are required</div>').insertAfter(remarksField);
                        }
                    } else {
                        console.log("Remarks field is valid");
                        remarksField.removeClass('is-invalid');
                        remarksField.siblings('.error-message').remove();
                    }

					//validate materialist
					let ismcValid = true;
					var newdecision = $('#decision_panel').val();
					if(newdecision !== 'SB') {
						$('#materialListTable tbody tr').each(function () {
							const materiallistID = $(this).find('.materiallistID').val();
							const materiallistDesc = $(this).find('.materiallistDesc').val();
							const materiallistCondition = $(this).find('.materiallistCondition').val();
							const materiallistComDate = $(this).find('.materiallistComDate').val();
							const conditionField = $(this).find('.materiallistCondition');

							if (!materiallistID || !materiallistDesc || !materiallistCondition) {
								ismcValid = false;
								conditionField.addClass('is-invalid');
								return false; // break out of .each loop
							}else{
								conditionField.removeClass('is-invalid');
							}
						});
						if (!ismcValid) {
							isValid = false;
							let accordion=new bootstrap.Collapse(document.getElementById('materialDetailsContent'),{
								show:true,
								toggle:false
							});
							accordion.show();
							alertmsg("All Material terms and Conditions fields must be filled in every row.");
						}
					}else{
						$('.materiallistCondition').removeClass('is-invalid');
						let accordion=new bootstrap.Collapse(document.getElementById('materialDetailsContent'),{
							toggle:false
						});
						accordion.hide();
					}


                    // Validate precondition field (mandatory unless decision is "Sendback")
                    var decision = $('#decision_panel').val();
                    var preConditionField = $('#pre_cond_rmks');
                    console.log("Validating precondition field. Decision:", decision);
                    if(preConditionField.val()!=undefined) {
                        if ((decision !== 'SB' || decision !== 'HU') && preConditionField.val().trim() === '') {
                            console.log("Precondition field is required and empty");
                            isValid = false;
                            preConditionField.addClass('is-invalid');
                            if (!preConditionField.siblings('.error-message').length) {
                                $('<div class="error-message text-danger">Precondition is required</div>').insertAfter(preConditionField);
                            }
                        } else {
                            console.log("Precondition field is valid or not required");
                            preConditionField.removeClass('is-invalid');
                            preConditionField.siblings('.error-message').remove();
                        }
                    }


                    // Validate all other fields
                    $('#rbcpcMakerForm input, #rbcpcMakerForm textarea, #rbcpcMakerForm select').not(':disabled').not(notSelector).each(function () {
                        console.log("Validating field:", this.name, "Value:", $(this).val());
                        if ($(this).val().trim() === '') {
                            console.log("Field is empty:", this.name);
                            isValid = false;
                            $(this).addClass('is-invalid');
                            var errorMessage = 'This field is required';
                            if ($(this).is('textarea')) {
                                var inputGroup = $(this).closest('.input-group');
                                if (!inputGroup.next('.error-message').length) {
                                    $('<div class="error-message text-danger mt-2">' + errorMessage + '</div>').insertAfter(inputGroup);
                                }
                            } else {
                                if (!$(this).siblings('.error-message').length) {
                                    $('<div class="error-message text-danger">' + errorMessage + '</div>').insertAfter(this);
                                }
                            }
                        } else {
                            console.log("Field is valid:", this.name);
                            $(this).removeClass('is-invalid');
                            if ($(this).is('textarea')) {
                                $(this).closest('.input-group').next('.error-message').remove();
                            } else {
                                $(this).siblings('.error-message').remove();
                            }
                        }
                    });

                    // $('#rbcpcMakerForm input, #rbcpcMakerForm textarea, #rbcpcMakerForm select').not(':disabled').not('#rm_remarks, #pre_cond_rmks').each(function () {
                    //     console.log("Validating field:", this.name, "Value:", $(this).val());
                    //     if ($(this).val().trim() === '') {
                    //         console.log("Field is empty:", this.name);
                    //         isValid = false;
                    //         $(this).addClass('is-invalid');
                    //         var errorMessage = 'This field is required';
                    //         if ($(this).is('textarea')) {
                    //             var inputGroup = $(this).closest('.input-group');
                    //             if (!inputGroup.next('.error-message').length) {
                    //                 $('<div class="error-message text-danger mt-2">' + errorMessage + '</div>').insertAfter(inputGroup);
                    //             }
                    //         } else {
                    //             if (!$(this).siblings('.error-message').length) {
                    //                 $('<div class="error-message text-danger">' + errorMessage + '</div>').insertAfter(this);
                    //             }
                    //         }
                    //     } else {
                    //         console.log("Field is valid:", this.name);
                    //         $(this).removeClass('is-invalid');
                    //         if ($(this).is('textarea')) {
                    //             $(this).closest('.input-group').next('.error-message').remove();
                    //         } else {
                    //             $(this).siblings('.error-message').remove();
                    //         }
                    //     }
                    // });

                    console.log("Form validation result:", isValid ? "Valid" : "Invalid");
                    if (!isValid) {
                        alertmsg('Please fill in all required fields.');
                    }

                    return isValid;
                }

                // Function to show alert messages
                function alertmsg(message) {
                    Swal.fire({
                        text: message,
                        icon: 'info',
                        confirmButtonText: 'OK'
                    });
                }

                // Function to hide loader (if implemented)
                function hideLoader() {
                    // Implement hide loader logic here
                    console.log("Hiding loader");
                }

                function displayStructuredError(structuredErrors) {
                    let errorHtml = '';
                    for (let category in structuredErrors) {
                        errorHtml += "<strong>" + category + "</strong><ul>";
                        structuredErrors[category].forEach(error => {
                            errorHtml += "<li>" + error + "</li>";
                        });
                        errorHtml += '</ul>';
                    }

                    swalInit.fire({
                        title: 'RBCPC Maker Validations',
                        html: errorHtml,
                        icon: 'error',
                        confirmButtonText: 'OK',
                        confirmButtonColor: '#e74c3c',
                        customClass: {
                            container: 'custom-swal-container',
                            popup: 'custom-swal-popup',
                            content: 'custom-swal-content'
                        }
                    });
                }

                function updateAccordionStyle(accordionId, isCompleted) {
                    const accordion = document.querySelector("#" + accordionId);
                    if (accordion) {
                        const label = accordion.querySelector('label');
                        if (label) {
                            if (isCompleted) {
                                label.classList.remove('btn-active-light-primary');
                                label.classList.add('btn-active-light-success', 'show');
                            } else {
                                label.classList.remove('btn-active-light-success', 'show');
                                label.classList.add('btn-active-light-primary');
                            }
                        }
                    }
                }

                function formatDate(dateString) {
                    if (!dateString) return 'N/A';
                    var date = new Date(dateString);
                    return date.toLocaleString('en-IN', {
                        day: '2-digit',
                        month: '2-digit',
                        year: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit',
                        second: '2-digit',
                        hour12: false
                    }).replace(/,/, '');
                }

                function updateDKScoreTable(dkScoreItems) {
                    var tableHtml = `
        <table id="dkScoreTable" class="table table-sm align-middle table-row-dashed table-row-gray-400 fs-8 gy-3">
            <thead>
                <tr class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">
                    <th>Applicant Type</th>
                    <th>Applicant Name</th>
                    <th>Run Date</th>
                    <th>Bureau Score</th>
                    <th>Race Score</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
    `;

                    dkScoreItems.forEach(function (item) {
                        var status = item.status === 'SUCCESS' ? 'Success' : 'Failed';
                        var statusClass = item.status === 'SUCCESS' ? 'badge-light-success' : 'badge-light-danger';
                        console.log(item);
    if(item.color==="red") {
        status="RACE Score failed";
        statusClass='badge-light-danger';
        alertmsg("RACE Score not in allowed range");
    }

                        tableHtml += '<tr class="table-row-gray-400">';
                        tableHtml += '<td>' + (item.applicantType !== null && item.applicantType !== undefined && item.applicantType !== '' ? item.applicantType : 'N/A') + '</td>';
                        tableHtml += '<td class="text-gray-800 text-hover-primary fs-7 fw-bold">' + (item.applicantName !== null && item.applicantName !== undefined && item.applicantName !== '' ? item.applicantName : 'N/A') + '</td>';
                        tableHtml += '<td>' + (item.runDate !== null && item.runDate !== undefined && item.runDate !== '' ? formatDate(item.runDate) : 'N/A') + '</td>';
                        tableHtml += '<td>' + (item.score !== null && item.score !== undefined && item.score !== '' ? item.score : 'N/A') + '</td>';
                        tableHtml += '<td>' + (item.score !== null && item.score !== undefined && item.score !== '' ? item.raceScore : 'N/A') + '</td>';
                        tableHtml += '<td><span class="badge ' + statusClass + '">' + status + '</span></td>';
                        tableHtml += '</tr>';
                    });


                    tableHtml += '</tbody></table>';
                    $('#dkresponsedata').html(tableHtml);
                    //  $('#kt_button_runDKScore').hide(); // Hide the button after successful fetch
                }


                function getStatusBadge(color,score) {
                    if (color ==="green") {
                        return '<span class="badge bg-success">score</span>';
                    } else if (color ==="amber") {
                        return '<span class="badge bg-warning">score</span>';
                    } else {
                        return '<span class="badge bg-danger">score</span>';
                    }
                }
            });

            // Add this line at the end of your script to check if it's loaded
            console.log("Form validation and submission script loaded successfully");


		</script>
	</head>

	<body>
		<%--<los:header/>--%>
		<los:loader/>


		<!-- Page header -->
		<los:pageheader/>
		<!-- /page header -->

		<div class="navbar navbar-expand-lg shadow rounded py-1 mb-3">
			<div class="container-fluid">


				<%
					Employee userdt = (Employee) request.getAttribute("userdata");
					VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
					List<Map<String, Object>> checkerLevels = (List<Map<String, Object>>) request.getAttribute("checkerLevels");
					String preDisbCond = "";
					String currentQueue = vehicleLoanMaster.getQueue();
					List<VehicleLoanApplicant> applicantList = Collections.emptyList();
					if (vehicleLoanMaster.getApplicants() != null)
						applicantList = vehicleLoanMaster.getApplicants();
					preDisbCond = vehicleLoanMaster.getPreDisbCondition() != null ? vehicleLoanMaster.getPreDisbCondition() : "";
					int coApplicantCount = 1;
					boolean modify = false;
					if (applicantList.size() > 0) {
						modify = true;
					}
					long coappsize = applicantList.stream().filter(d -> "C".equals(d.getApplicantType())).count();
					String currentTab = vehicleLoanMaster.getCurrentTab() == null ? "A-1" : vehicleLoanMaster.getCurrentTab();

					String bpmerr = "", appurl = "";
					ResponseDTO bpmResp = (ResponseDTO) request.getAttribute("bpm");
					if (bpmResp.getStatus().equals("S"))
						appurl = bpmResp.getMsg();
					else
						bpmerr = bpmResp.getMsg();
                    String sol_desc = (String) request.getAttribute("sol_desc");
					String roname = (String) request.getAttribute("roname");
                    String queue = vehicleLoanMaster.getQueue();
					Boolean hunterCheckPerformed = (Boolean) request.getAttribute("hunterCheckPerformed");
					Boolean hunterMatchFound = (Boolean) request.getAttribute("hunterMatchFound");
					Boolean hunterReviewCompleted = (Boolean) request.getAttribute("hunterReviewCompleted");
                    boolean allowFurtherProcessing = !hunterMatchFound || (hunterMatchFound && hunterReviewCompleted);
                    String bureauBlock = (String) request.getAttribute("bureauBlock");

				%>

				<div class="navbar-collapse collapse order-2 order-lg-1">
								<span class="navbar-text d-none d-lg-inline-flex align-items-lg=center me-3">
									<i class="ph-receipt me-2"></i>
									Work Item No : <b><%=vehicleLoanMaster.getWiNum()%></b>
                                    <input type="hidden" name="slno" id="slno" value="<%=vehicleLoanMaster.getSlno()%>">
                                    <input type="hidden" name="winum" id="winum" value="<%=vehicleLoanMaster.getWiNum()%>">
                                    <input type="hidden" name="modify" id="modify" value="<%=modify%>">
                                    <input type="hidden" name="lockflg" id="lockflg" value="<%=request.getAttribute("lockflg")%>">
									<input type="hidden" name="lockuser" id="lockuser" value="<%=request.getAttribute("lockuser")%>">
									<input type="hidden" id="isCompleted" value="<%=isCompleted(applicantList)%>"/>
									<input type="hidden" name="modifycnt" id="modifycnt" value="<%=coappsize%>">
                                    <input type="hidden" id="bpmerror" value="<%=bpmerr%>"/>
									<input type="hidden" name="currentQueue" id="currentQueue" value="<%=currentQueue%>">
									<input type="hidden" id="currentTab" value="<%=currentTab%>"/>
									<input type="hidden" id="queue" value="<%=queue%>"/>
									<input type="hidden" name="bureauBlock" id="bureauBlock" value="<%=bureauBlock%>">
<%--                                  <input type="hidden" id="currentTab" value="<%=vehicleLoanMaster.getCurrentTab()%>"/>--%>
                                    <input type="hidden" id="currentTab" value="<%=vehicleLoanMaster.getCurrentTab()==null ? "A-1" : vehicleLoanMaster.getCurrentTab()%>"/>

<%--                                  <input type="hidden" id="currentTab" value="A-6"/>--%>
								</span>
					<%--            <button type="button" id="checkButton">Check Save Buttons</button>--%>
				</div>



			</div>
			<div class="container-fluid">

				<div class="navbar-collapse collapse order-2 order-lg-1">
								<span class="navbar-text d-none d-lg-inline-flex align-items-lg=center me-3">
									<i class="ph-house me-2"></i>
									Branch : <b><%=vehicleLoanMaster.getSolId()%>( <%=sol_desc%> <%=roname%>)</b>
								</span>
				</div>

			</div>
			<button id="parentToggle" type="button" class="btn btn-primary ">
        <i class="ph-file-doc"></i>DOCLIST
    </button>
		</div>


		<!-- Page content -->
		<div class="page-content pt-0">

			<!-- Main sidebar -->
			<div class="sidebar sidebar-main sidebar-expand-lg align-self-start">

				<!-- Sidebar content -->
				<div class="sidebar-content">

					<!-- Sidebar header -->
					<div class="sidebar-section">
						<div class="sidebar-section-body d-flex justify-content-center">
							<h5 class="sidebar-resize-hide flex-grow-1 my-auto">Details</h5>

							<div>
								<button type="button"
								        class="btn btn-light btn-icon btn-sm rounded-pill border-transparent sidebar-control sidebar-main-resize d-none d-lg-inline-flex">
									<i class="ph-arrows-left-right"></i>
								</button>
								<button type="button" class="btn btn-light btn-icon btn-sm rounded-pill border-transparent sidebar-mobile-main-toggle d-lg-none">
									<i class="ph-x"></i>
								</button>


							</div>
						</div>
					</div>
					<!-- /sidebar header -->


					<!-- Main navigation -->
					<div class="sidebar-section">
						<ul class="nav nav-sidebar" data-nav-type="accordion">
							<li class="nav-item">
								<a href="#" class="nav-link active details" data-code="1">
									<i class="ph-identification-card"></i>
									<span>General Details</span>
								</a>
							</li>
							<li class="nav-item">
								<a href="#" class="nav-link details" data-code="2">
									<i class="ph-identification-card"></i>
									<span>KYC Details</span>
								</a>
							</li>
							<li class="nav-item">
								<a href="#" class="nav-link details" data-code="3">
									<i class="ph-user-circle"></i>
									<span>Basic Details</span>
								</a>
							</li>
							<li class="nav-item">
								<a href="#" class="nav-link details" data-code="4">
									<i class="ph-activity"></i>
									<span>Employment Details</span>
								</a>
							</li>
							<li class="nav-item">
								<a href="#" class="nav-link details" data-code="5">
									<i class="ph-money"></i>
									<span>Program Details</span>
								</a>
							</li>
							<li class="nav-item">
								<a href="#" class="nav-link details" data-code="6">
									<i class="ph-credit-card"></i>
									<span>Credit check</span>
								</a>
							</li>
							<!-- /layout -->

						</ul>
					</div>
					<!-- /main navigation -->

				</div>
				<!-- /sidebar content -->
				<article title="Car Inspection  3D Icon" class="container_EOFSc"
				         style="display: block;border-radius: 12px;background-color: #FAFAFC;position: relative;height: 22.125rem;background-color: #F5F6FA !important;">
					<picture class="pict">
						<source type="image/webp" srcset="assets/images/car-loan.png?f=webp 1x, assets/images/car-loan.png?f=webp 2x">
						<img alt="Car Inspection  3D Icon" loading="lazy" src="assets/images/car-loan.png?f=webp"
						     srcset="assets/images/car-loan.png?f=webp 1x, assets/images/car-loan.png?f=webp 2x" style="width: 19em;">
					</picture>
				</article>
			</div>
			<!-- /main sidebar -->

			<!-- Main content -->
			<div class="content-wrapper">

				<!-- Content area -->
				<div class="content">

					<!-- Main charts -->
					<div class="d-flex flex-row min-vh-100">
						<div id="appList" class="w-100">

							<div class="card">
								<div class="card-header d-flex align-items-center">
									<h5 class="mb-0">Loan Application</h5>
<%--									<div class="ms-auto">--%>
<%--										<button type="button" id="addcoapp" class="btn btn-outline-primary ms-auto">--%>
<%--											Co-Applicant--%>
<%--											<i class="fas fa-user-plus ms-2"></i>--%>
<%--										</button>--%>

<%--										<%if (!applicantList.stream().anyMatch(a -> "G".equals(a.getApplicantType()))) {%>--%>
<%--										<button type="button" id="addguarantor" class="me-2 btn btn-outline-danger ms-auto">--%>
<%--											GUARANTOR--%>
<%--											<i class="fas fa-user-plus ms-2"></i>--%>
<%--										</button>--%>
<%--										<%}%>--%>
<%--									</div>--%>

								</div>
								<ul class="nav nav-tabs nav-justified mb-0" role="tablist" id="loanapp">
									<%
										for (VehicleLoanApplicant applicant : applicantList) {
											if ("A".equals(applicant.getApplicantType())) {
									%>
									<li class="nav-item" role="presentation"><a href="#tab-A" class="nav-link apptype active  alert alert-primary " data-bs-toggle="tab"
									                                            aria-selected="false" role="tab" tabindex="-1" data-app="A"><i class="ph-user fw-semibold"></i>APPLICANT</a>
									</li>
									<%
									} else if ("C".equals(applicant.getApplicantType())) {
									%>
									<li class="nav-item" role="presentation"><a href="#tab-C-<%=coApplicantCount%>" class="nav-link apptype   alert alert-primary  "
									                                            data-bs-toggle="tab" aria-selected="false" role="tab" tabindex="-1"
									                                            data-app="C-<%=coApplicantCount%>"><i class="ph-user-circle-plus "></i><span
											data-applicationtype="C">Co-Applicant-<%=coApplicantCount%></span>
<%--										<button type="button" class="btn-close closeapplicant pe-5" data-closecode="C-<%=coApplicantCount%>"></button>--%>
									</a>
									</li>
									<%
										coApplicantCount++;
									} else if ("G".equals(applicant.getApplicantType())) {
									%>
									<li class="nav-item" role="presentation"><a href="#tab-G-1" class="nav-link apptype   alert alert-primary   " data-bs-toggle="tab"
									                                            aria-selected="false" role="tab" tabindex="-1" data-app="G-1"><i
											class="ph-bank   fw-semibold"></i><span data-applicationtype="G">GUARANTOR</span>
<%--										<button type="button" class="btn-close closeapplicant pe-5" data-closecode="G-1"></button>--%>
									</a></li>
									<%

											}
										}
										if (applicantList.size() == 0) {
									%>
									<li class="nav-item" role="presentation"><a href="#tab-CA" class="nav-link apptype active  alert alert-primary " data-bs-toggle="tab"
									                                            aria-selected="false" role="tab" tabindex="-1" data-app="A"><i class="ph-user fw-semibold"></i>APPLICANT</a>
									</li>
									<%
										}

									%>
								</ul>


								<div class="tab-content card-body" id="loanbody">
									<div class="tab-pane fade" id="tab-clone" role="tabpanel">
										<%
											request.setAttribute("apptype", "T");
										%>
										<jsp:include page="rmmaker/generaldetails_rm.jsp">
											<jsp:param name="coapponly" value="Y"/>
										</jsp:include>
										<jsp:include page="rmmaker/kycdetails_rm.jsp"/>
										<jsp:include page="rmmaker/basicdetails_rm.jsp"/>
										<jsp:include page="det/employmentdetails.jsp"/>
<%--										<jsp:include page="rmmaker/Incomedetails_rm.jsp"/>--%>
										<jsp:include page="det/Incomedetails.jsp" />
										<jsp:include page="rmmaker/CreditCheck_rm.jsp"/>
										<%--                <jsp:include page="det/FinancialDetails.jsp" />--%>
										<%--                <jsp:include page="det/vehicledetails_allot.jsp" />--%>
										<%--                <jsp:include page="det/insurancedetails.jsp" />--%>
										<%--                <jsp:include page="det/loandetails_allot.jsp" />--%>
									</div>

									<%
										coApplicantCount = 1;
										for (VehicleLoanApplicant applicant : applicantList) {
											request.setAttribute("general", applicant);
											if ("A".equals(applicant.getApplicantType())) {
									%>
									<div class="tab-pane fade active show" id="tab-A" role="tabpanel">
										<%
											request.setAttribute("apptype", "A");
											request.setAttribute("appurl", appurl);
										%>
										<jsp:include page="rmmaker/generaldetails_rm.jsp">
											<jsp:param name="apponly" value="Y"/>
										</jsp:include>
										<jsp:include page="rmmaker/kycdetails_rm.jsp"/>
										<jsp:include page="rmmaker/basicdetails_rm.jsp"/>
										<jsp:include page="det/employmentdetails.jsp"/>
<%--										<jsp:include page="rmmaker/Incomedetails_rm.jsp"/>--%>
										<jsp:include page="det/Incomedetails.jsp" />
										<jsp:include page="rmmaker/CreditCheck_rm.jsp"/>
									</div>
									<%
									} else if ("C".equals(applicant.getApplicantType())) {
										request.setAttribute("apptype", "C-" + coApplicantCount);
										request.setAttribute("appurl", "");
									%>
									<div class="tab-pane fade" role="tabpanel" id="tab-C-<%=coApplicantCount%>">
										<jsp:include page="rmmaker/generaldetails_rm.jsp">
											<jsp:param name="coapponly" value="Y"/>
										</jsp:include>
										<jsp:include page="rmmaker/kycdetails_rm.jsp"/>
										<jsp:include page="rmmaker/basicdetails_rm.jsp"/>
										<jsp:include page="det/employmentdetails.jsp"/>
<%--										<jsp:include page="rmmaker/Incomedetails_rm.jsp"/>--%>
										<jsp:include page="det/Incomedetails.jsp" />
										<jsp:include page="rmmaker/CreditCheck_rm.jsp"/>
									</div>
									<%
										coApplicantCount++;
									} else if ("G".equals(applicant.getApplicantType())) {
										request.setAttribute("apptype", "G-1");
										request.setAttribute("appurl", "");
									%>
									<div class="tab-pane fade" role="tabpanel" id="tab-G-1">
										<jsp:include page="rmmaker/generaldetails_rm.jsp">
											<jsp:param name="coapponly" value="Y"/>
										</jsp:include>
										<jsp:include page="rmmaker/kycdetails_rm.jsp"/>
										<jsp:include page="rmmaker/basicdetails_rm.jsp"/>
										<jsp:include page="det/employmentdetails.jsp"/>
<%--										<jsp:include page="rmmaker/Incomedetails_rm.jsp"/>--%>
										<jsp:include page="det/Incomedetails.jsp" />
										<jsp:include page="rmmaker/CreditCheck_rm.jsp"/>
									</div>
									<%
											}

										}
										if (applicantList.size() == 0) {
									%>
									<div class="tab-pane fade active show" id="tab-A" role="tabpanel">
										<%
											request.setAttribute("apptype", "A");
											request.setAttribute("init", "Y");
										%>
										<jsp:include page="rmmaker/generaldetails_rm.jsp">
											<jsp:param name="apponly" value="Y"/>
										</jsp:include>
										<jsp:include page="rmmaker/kycdetails_rm.jsp"/>
										<jsp:include page="rmmaker/basicdetails_rm.jsp"/>
										<jsp:include page="det/employmentdetails.jsp"/>
<%--										<jsp:include page="rmmaker/Incomedetails_rm.jsp"/>--%>
										<jsp:include page="det/Incomedetails.jsp" />
										<jsp:include page="rmmaker/CreditCheck_rm.jsp"/>
									</div>
									<%
										}
									%>


								</div>
							</div>
							<div id="vehList" class="w-100 kt">
								<form class="z-index-0" action="#">
									<div class="card-body">
										<div class="row">
											<div class="1 w-100 border rounded mb-lg-4 p-0">
												<div class="1" style="background: #fff !important;">
													<!-- User menu -->

													<!-- /user menu -->

													<div class="rounded border p-2 mb-3">
														<!-- Navigation -->

														<div class="accordion accordion-icon-collapse" id="vl_rm_int" data-state="2">
															<%--                                            <div class="sidebar-section" id="bottomCard" data-state="0">--%>
															<%request.setAttribute("checker", "N");%>
															<jsp:include page="rmmaker/vehicledetails_rm.jsp"/>
															<jsp:include page="rmmaker/loandetails_rm.jsp"/>
															<jsp:include page="rmmaker/eligibilitydetails_rm.jsp"/>
															<jsp:include page="rmmaker/racescoredetails_rm.jsp"/>
															<jsp:include page="rmmaker/validationdetails_rm.jsp"/>
															<jsp:include page="rmmaker/hunterdetails_rm.jsp"/>
																<% if (allowFurtherProcessing) { %>
															<div class="hideonhunter">
															<jsp:include page="rmmaker/blacklistdetails_rm.jsp"/>
															<jsp:include page="rmmaker/warning_condition_details_rm.jsp"/>
															<div id="deviation_parent">
																<jsp:include page="rmmaker/deviationdetails_rm.jsp"/>
															</div>
															</div>
															<%}%>

																<jsp:include page="rmmaker/materialtc_rm.jsp"/>

														</div>
														<div class="btcard">
															<form method="post" name="rbcpcMakerForm" id="rbcpcMakerForm" action="#">


																<!--begin::Content-->
																<div id="kt_account_settings_deactivate" class="collapse show">
																	<!--begin::Card body-->
																	<div class="p-9">
																		<% if (allowFurtherProcessing) { %>
																		<!--begin::Form input row-->
																		<div class="input-group mt-3 mb-3 hideonhunter2">
																			<span class="input-group-text">Pre Disbursment Conditions</span>
																			<textarea class="form-control " rows="4" aria-label="Pre Disbursment Conditions" maxlength="8000" name="pre_cond_rmks"
																			          id="pre_cond_rmks"><%=preDisbCond%></textarea>
																		</div>
																		<%}%>

																		<div class="input-group mt-3 mb-3"><a href="#" id="remarkHist" class="badge badge-light-danger fs-base">
																			History
																		</a>
																			<span class="input-group-text">Remarks</span>

																			<textarea class="form-control maxlength-textarea" rows="4" aria-label="Remarks" name="rm_remarks" id="rm_remarks" maxlength="4000"></textarea>
																		</div>

																		<div class="row">
																			<div class="col-sm-12 mt-3">
																				<label for="decision_panel" class="required form-label">Decision</label>
																				<select name="decision_panel" id="decision_panel" class="form-select" required>
																					<option value="" selected>select</option>

																					<%
																						if(bureauBlock.equals("N")){
																						 if (allowFurtherProcessing) {
																						if (checkerLevels != null) {
																							for (Map<String, Object> level : checkerLevels) {
																								String level_val = (String) level.get("LEVEL_NAME");
																								String level_desc = (String) level.get("DISPLAY_NAME");
																					%>
																					<option value="<%= level_val %>"><%= level_desc %>
																					</option>
																					<%
																							}
																						}
																						%>
																					<option value="SB">Branch Sendback</option>
																					<%
																						 } else {
																					%>
                                                                                             <option value="HU">Forward to hunter</option>
																					<option value="SB">Branch Sendback</option>
																							<%
																						 }
																						} else {
																					%>
																					<option value="SB">Branch Sendback</option>
																					<%}%>
																				</select></div>
																		</div>
																		<div class="row" id="ppcSelectContainer" style="display: none;">
    <div class="col-sm-12 mt-3">
        <label for="ppcSelect" class="required form-label">Select PPC</label>
        <select id="ppcSelect" name="ppcSelect" class="form-select">
            <option></option>
        </select>
    </div>
</div>

<%--																		 <div class="container">--%>
<%--        <select id="ppcSelect" class="form-select" style="width: 100%">--%>
<%--            <option></option>--%>
<%--        </select>--%>
<%--    </div>--%>
																		<!--end::Form input row-->
																	</div>
																	<!--end::Card body-->
																	<!--begin::Card footer-->
																	<div class=" d-flex justify-content-lg-center py-6 px-9">
																		<button id="maker_forward" type="submit" class="btn btn-success fw-semibold ms-4">Submit</button>
																		<button id="maker_reject" type="submit" class="btn btn-danger fw-semibold ms-4">Cancel</button>
																	</div>
																	<!--end::Card footer-->
																</div>
																<!--end::Content-->
															</form>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>
								</form>
							</div>


						</div>

						<div id="docList">

						</div>
					</div>

					<!-- /main charts -->


				</div>
				<!-- /content area -->

			</div>
			<!-- /main content -->

		</div>
		<!-- /page content -->

		<div class="position-absolute top-50 end-100  visible">
			<button id="toggleList" type="button" class="btn btn-primary position-fixed top-50 end-0 translate-middle-y border-right-0">
				<i class="ph-file-doc"></i>
			</button>
		</div>
		<script src="assets/js/vendor/forms/selects/select2.min.js"></script>
		<script src="assets/demo/pages/form_select2.js"></script>
		<!--Custom Scripts-->
		<script src="assets/js/custom/WI/kycdetails.js"></script>
		<script src="assets/js/custom/WI/basicdetails.js"></script>
		<script src="assets/js/custom/WI/generaldetails.js"></script>
		<script src="assets/js/custom/WI/employmentdetails.js"></script>
		<script src="assets/js/custom/WI/Incomedetails.js"></script>
		<script src="assets/js/custom/WI/creditCheck.js"></script>
		<script src="assets/js/custom/WI/uploadwi.js"></script>
		<script src="assets/js/custom/WI/Wicreate.js"></script>
			<script src="assets/js/vendor/forms/inputs/maxlength.min.js"></script>
		<%--<script src="assets/js/custom/WI/programdetails.js"></script>--%>
		<!--Custom Scripts-->
		<%--<script src="assets/plugins/global/plugins.bundle.js"></script>--%>
		<los:modal/>
		<los:footer/>
		<script src="assets/js/custom/performance-logging.js"></script>
	</body>
	<%!
		private boolean isCompleted(List<VehicleLoanApplicant> applicantList) {
			return applicantList.size() > 0 && applicantList.stream()
					.filter(t -> "N".equalsIgnoreCase(t.getDelFlg()))
					.allMatch(t -> isValid(t.getKycComplete()) &&
							isValid(t.getBasicComplete()) &&
							isValid(t.getCreditComplete()) &&
							isValid(t.getGenComplete()) &&
							isValid(t.getIncomeComplete()) &&
							isValid(t.getEmploymentComplete()));
		}

		private boolean isValid(String str) {
			return str != null && "Y".equalsIgnoreCase(str);
		}

	%>
</html>

