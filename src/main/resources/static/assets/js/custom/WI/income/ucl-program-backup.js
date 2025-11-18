let statementsData = [];
const MAX_STMTS = 3;
let skipAutoPopulate = false;
let ahlloanbody;
let employment_type;
let resident_type;
$(document).ready(function () {
    employment_type = $('.det').closest('.tab-pane').find('.employmentdetails').find('.selemptype')[1].value;
    resident_type = $('.det').closest('.tab-pane').find('.generaldetails').find('.residentialStatus:checked').val();
    console.log("------------------------" + employment_type);
    console.log("------------------------" + resident_type);
    initializeProgramDetails();
    initBankStatementDatepickers();
    $('.program-itr form').each(function () {
        var form = $(this);
    })
    // Income check change handler
    $('#loanbody').on('change', '.incomeCheck', function (e) {
        e.preventDefault();
        e.stopPropagation();
        var selected = $(this).val();
        var detElement = $(this);
        var programCodeparent = $(this).closest('.section-body').find('.program');
        var programCodeDropdown = $(this).closest('.det').find('.programCode');
        var dataCode = $(this).closest('form.det').data('code') || ''
        var applicantType = "";
        if (dataCode && dataCode.length > 0) {
            var parts = dataCode.split('-');
            if (parts.length > 0) {
                applicantType = parts[0]; // This should be "A", "C", or "G"
            }
        }
        if (applicantType === "G") {
            detElement.closest('.det').find('input.incomeCheck[value="Y"]').prop('disabled', true);
            detElement.closest('.det').find('.incomeCheck').val("N").prop('checked', true);
            selected = "N";
            // if (selected === 'Y') {
            //     detElement.closest('.det').find('.incomeCheck').val("N").prop('checked', true).trigger('change');
            // }
        }
        employment_type = $(this).closest('.det').closest('.tab-pane').find('.employmentdetails').find('.selemptype').val();
        $(this).closest('.det').find('input[name="empl_type"]').val(employment_type);

        if (selected === 'Y') {
            programCodeparent.show();
            var currentSelection = programCodeDropdown.val();
            if (programCodeDropdown.find('option[value="NONE"]').length > 0) {
                programCodeDropdown.find('option[value="NONE"]').remove();
                if (currentSelection === "NONE") {
                    programCodeDropdown.val("");
                }
            }
            detElement.closest('.det').find('.programCode').val("");
            var currentProgram = detElement.closest('.det').find('.programCode').val();
            if (currentProgram && currentProgram !== 'NONE') {
                validateProgramConstraints(detElement, currentProgram, selected, function (isValid) {
                    if (!isValid) {
                        detElement.closest('.det').find('input[name="incomeCheck"][value="N"]').prop('checked', true).trigger('change');
                    }
                });
            }
                    $(this).closest('.det').find('.no-income-section').hide();
        } else {
            programCodeparent.hide();
            programCodeDropdown.val("");
            $(this).closest('.det').find('.incomediv').hide();
            $(this).closest('.det').find('.surrogatediv').hide();
            $(this).closest('.det').find('.rtrdiv').hide();
            $(this).closest('.det').find('.liquiddiv').hide();
            $(this).closest('.det').find('.no-income-section').show();
            $(this).closest('.det').find('.nonfoirdiv').hide();
            $(this).closest('.det').find('.imputeddiv').hide();
            $(this).closest('.det').find('.fddiv').hide();
        }
    });

    $('.iframe-modal').on('hidden.bs.modal', function () {
        var modal = $(this);
        var triggerElement = modal.data('triggerElement');
        var statementType = modal.data('statementType');
        var statementIndex = modal.data('statementIndex');
        var modalTitle = modal.find('.modal-title').text();
        console.log(modalTitle + "---AHL-PROGRAM---" + statementType);
        if (modalTitle === 'ITR File Upload' && statementType === undefined) {
            statementType = "ITR-UPLOAD";
        }

        if (statementType.includes('SURROGATE')) {
            // Mark statement as uploaded
            statementsData[statementIndex].uploaded = true;
            $(triggerElement).closest('.det').find(`#upload-status-${statementIndex}`)
                .html('<span class="text-success">Uploaded</span>');
            $(triggerElement).closest('.det').find(`.review-upload-btn[data-idx="${statementIndex}"]`).prop('disabled', true);
            //$(`#upload-status-${statementIndex}`).html(`<span class="text-success">Uploaded</span>`);
            // Check if all statements are uploaded
            var allUploaded = statementsData.every(s => s.uploaded);
            triggerElement.closest('.det').find('.review-selections-btn').prop('disabled', !allUploaded);
        } else if (statementType.includes('BSA-ITR')) {
            console.log("BSA-ITR dont take action from here . Handled in bsa-functions.js");
        } else {
            // Existing close modal logic
            closemodal(modalTitle, triggerElement);
        }
    });
    // Program code change handler
    $('#loanbody').on('change', '.programCode', function (e) {
        e.preventDefault();
        e.stopPropagation();
        var selected = $(this).val();
        var detElement = $(this);
        var incomeConsidered = detElement.closest('.det').find('input[name="incomeCheck"]:checked').val();
        validateProgramConstraints(detElement, selected, incomeConsidered, function (isProgramValid) {
            if (isProgramValid) {
                validateProgramSelection(detElement, selected, function (isValid) {
                    if (isValid) {
                        var currentProgram = detElement.closest('.det').find('input[name="hidProgramCode"]').val();
                        employment_type = $(this).closest('.det').closest('.tab-pane').find('.employmentdetails').find('.selemptype').val();
                        $(this).closest('.det').find('input[name="empl_type"]').val(employment_type);
                        console.log("==========employment type program code change ===========" + employment_type);
                        // If changing from one saved program to another, confirm with user using SweetAlert
                        if (currentProgram && currentProgram !== selected && currentProgram !== "") {
                            const dropdown = $(this); // Save reference to the dropdown

                            // Format program names for display
                            const currentProgramName = getProgramDisplayName(currentProgram);
                            const selectedProgramName = getProgramDisplayName(selected);

                            Swal.fire({
                                title: 'Change Program?',
                                html: `<div class="text-center">
                     <p>You are about to change the program from:</p>
                     <p class="font-weight-bold text-primary">${currentProgramName}</p>
                     <p>to:</p>
                     <p class="font-weight-bold text-success">${selectedProgramName}</p>
                     <div class="alert alert-warning mt-3">
                       <i class="ph-warning-circle pe-2"></i>
                       <span>This will reset all current program data.</span>
                     </div>
                   </div>`,
                                icon: 'warning',
                                showCancelButton: true,
                                confirmButtonColor: '#3085d6',
                                cancelButtonColor: '#d33',
                                confirmButtonText: 'Yes, change program',
                                cancelButtonText: 'Cancel',
                                buttonsStyling: false,
                                customClass: {
                                    confirmButton: 'btn btn-primary',
                                    cancelButton: 'btn btn-secondary me-3'
                                },
                                allowOutsideClick: false
                            }).then((result) => {
                                if (result.isConfirmed) {
                                    // User confirmed, continue with program change
                                    proceedWithProgramChange(detElement, selected);
                                } else {
                                    // User cancelled, revert dropdown to previous value
                                    dropdown.val(currentProgram);
                                    dropdown.trigger('change.select2'); // If using select2
                                }
                            });
                            // Return early to prevent the normal handler flow until user decides
                            return;
                        }
                        // If there's no program change or no previous program, proceed normally
                        proceedWithProgramChange(detElement, selected);
                        // Fetch applicant details from other form sections
                        const kycdetailsElement = detElement.closest('.det').parent().closest('.tab-pane').find('.kycdetails');
                        const basicdetailsElement = detElement.closest('.det').parent().closest('.tab-pane').find('.basicdetails');
                        const generaldetailsElement = detElement.closest('.det').parent().closest('.tab-pane').find('.generaldetails');

                        var entryPan = kycdetailsElement.find('.pan').val();
                        var entryPanDob = kycdetailsElement.find('.pandob').val();
                        var residentialStatus = generaldetailsElement.find('.residentialStatus:checked').val();
                        const basic_mob = basicdetailsElement.find('.basic_mob').val();
                        const basic_mobcode = basicdetailsElement.find('.basic_mobcode').val();
                        let cust_mob = basic_mobcode + basic_mob;

                        // Set hidden fields
                        $(this).closest('.det').find('input[name^="residentialStatus"]').val(residentialStatus);
                        $(this).closest('.det').find('input[name^="incomePAN"]').val(entryPan);
                        $(this).closest('.det').find('input[name^="incomeDOB"]').val(entryPanDob);
                        $(this).closest('.det').find('input[name^="incomeMOB"]').val(cust_mob);

                        // If program code is selected, fetch program details
                        if (selected !== null && selected.length > 0) {
                            fetchProgramDetails(detElement);
                        }

                        // Show/hide sections based on program selection
                        $(this).closest('.det').find('.incomediv').hide();
                        $(this).closest('.det').find('.surrogatediv').hide();
                        $(this).closest('.det').find('.liquiddiv').hide();
                        $(this).closest('.det').find('.nonfoirdiv').hide();
                        $(this).closest('.det').find('.imputeddiv').hide();
                        $(this).closest('.det').find('.fddiv').hide();

                        //$(this).closest('.det').find('.rtrdiv').hide();

                        if (selected === 'INCOME') {
                            if (resident_type === 'N') {
                                $(this).closest('.det').find('.non-resident-income-section').show();
                                $(this).closest('.det').find('.resident-income-sections').hide();
                                $(this).closest('.det').find('.salaried-section').hide();
                                $(this).closest('.det').find('.pensioner-section').hide();
                                $(this).closest('.det').find('.sepsenp-section').hide();
                                $(this).closest('.det').find('.agriculturist-section').hide();
                                $(this).closest('.det').find('.resident-status-text').text('Non-Resident');
                                $(this).closest('.det').find('.resident-check-section .alert')
                                    .removeClass('alert-info')
                                    .addClass('alert-warning');
                            } else {
                                $(this).closest('.det').find('.incomediv').show();
                                $(this).closest('.det').find('.salaried-section').hide();
                                $(this).closest('.det').find('.pensioner-section').hide();
                                $(this).closest('.det').find('.sepsenp-section').hide();
                                $(this).closest('.det').find('.agriculturist-section').hide();

                                // Show the section specific to selected employment type
                                if (employment_type === 'SALARIED') {
                                    $(this).closest('.det').find('.salaried-section').show();
                                } else if (employment_type === 'PENSIONER') {
                                    $(this).closest('.det').find('.pensioner-section').show();
                                } else if (employment_type === 'SEP' || employment_type === 'SENP') {
                                    $(this).closest('.det').find('.sepsenp-section').show();
                                } else if (employment_type === 'AGRICULTURIST') {
                                    $(this).closest('.det').find('.agriculturist-section').show();
                                }
                                checkForITREntries(detElement);
                            }
                        } else if (selected === 'SURROGATE') {
                            $(this).closest('.det').find('.surrogatediv').show();
                            $(this).closest('.det').find('.surrogate-section').removeClass('hidden');
                            resetSurrogate(detElement);
                        } else if (selected === 'NONFOIR') {
                            handleNonFOIRSelection($(this).closest('.det').find('.nonfoirdiv'));
                        } else if (selected === 'IMPUTED') {
                            handleImputedIncomeSelection($(this).closest('.det').find('.imputeddiv'));
                        } else if (selected === 'LOANFD') {
                             $(this).closest('.det').find('.fddiv').show();
                            handleFDProgramSelection($(this).closest('.det').find('.fddiv'));
                        }
                    }
                });
            }
        });
    });

    $('#loanbody').on('change', '.doc-type-selection', function (e) {
        e.preventDefault();
        e.stopPropagation();
        var selected = $(this).val();
        var detElement = $(this);

        // Hide all document sections first
        $(this).closest('.salaried-section').find('.itr-section').hide();
        $(this).closest('.salaried-section').find('.payslip-section').hide();

        // Show the section specific to selected document type
        if (selected === 'ITR') {
            $(this).closest('.salaried-section').find('.itr-section').show();
        } else if (selected === 'PAYSLIP') {
            $(this).closest('.salaried-section').find('.payslip-section').show();
            // Initialize with one row if none exist
            if ($(this).closest('.salaried-section').find('.payslip-table-body tr').length === 0) {
                addPayslipRow($(this));
            }
        }
    });
    $('#loanbody').on('change', '.has-form16-check', function (e) {
        var hasForm16 = $(this).val();
        var detElement = $(this).closest('.det');
        var form16Container = detElement.find('.form16-upload-container');

        console.log("Has Form 16: " + hasForm16);

        if (hasForm16 === 'Y') {
            form16Container.slideDown(300);
            detElement.find('.form16-supporting-upload').prop('required', true);

            Swal.fire({
                icon: 'info',
                title: 'Form 16 Upload',
                html: '<div class="text-start">' +
                    '<p>Please upload your Form 16 as supporting documentation.</p>' +
                    '<p class="mb-0"><strong>Note:</strong> Form 16 will be used for verification. ' +
                    'Your income calculation is still based on ITR or Salary Slip.</p>' +
                    '</div>',
                confirmButtonText: 'Understood'
            });
        } else {
            form16Container.slideUp(300);
            detElement.find('.form16-supporting-upload').prop('required', false);
            detElement.find('.form16-supporting-upload').val('');
            detElement.find('.form16-financial-year').val('');
            detElement.find('.form16-employer-name').val('');
            detElement.find('.form16-upload-status').hide();
        }
    });
     $('#loanbody').on('input', '.monthly-salary-nr, .total-remittance', function() {
            calculateRemittances($(this).closest('.det'));
        });

    $('#loanbody').on('change', '.rtr-topup-loan', function (e) {
        e.preventDefault();
        var selected = $(this).val();

        if (selected === 'Y') {
            $(this).closest('.section-body').find('.rtr-topup-details').show();
        } else {
            $(this).closest('.section-body').find('.rtr-topup-details').hide();
            // Clear values when No is selected
            $(this).closest('.section-body').find('.rtr-topup-details input').val('');
        }
    });
    // Add payslip row button handler
    $('#loanbody').on('click', '.add-payslip-row', function (e) {
        e.preventDefault();
        addPayslipRow($(this));
    });

    // Delete payslip row button handler
    $('#loanbody').on('click', '.delete-payslip-row', function (e) {
        e.preventDefault();
        deletePayslipRow($(this));
    });

    // Calculate total income when payslip amount changes
    $('#loanbody').on('input', '.payslip-amount', function (e) {
        calculateTotalIncome($(this));
    });
    $('#loanbody').on('input', '.add-backs-obligations', function () {
        calculateFinalAMI($(this));
    });


});
function handleFDProgramSelection(detElement) {
    console.log("========== FD Program Selected ==========");

    // Validate that customer is existing
    var customerId = detElement.closest('.tab-pane').find('.generaldetails').find('.custID').val();

    if (!customerId || customerId === "") {
        Swal.fire({
            icon: 'warning',
            title: 'Existing Customer Required',
            text: 'FD Program is available only for existing customers.',
            confirmButtonText: 'OK'
        });

        // Reset program selection
        detElement.find('.programCode').val('').trigger('change');
        return false;
    }

    // Show FD div
    $(this).closest('.det').find('.fddiv')
    detElement.find('.fddiv').show();

    // Auto-fetch FD details if customer ID exists
    console.log("Auto-fetching FD details for customer: " + customerId);

    // Small delay to allow UI to render
    setTimeout(function() {
        detElement.find('.fd-account-validate').trigger('click');
    }, 300);
}
/**
 * NEW FUNCTION: Fetch FD Details Button Click Handler
 */
$('#loanbody').on('click', '.fd-account-validate', function () {
    console.log("========== Fetching FD Details ==========");
    fetchFDDetails($(this));
});
function fetchFDDetails(triggerElement) {
    var detElement = triggerElement.closest('.det');
    var tabPane = detElement.closest('.tab-pane');
    var generalDetails = tabPane.find('.generaldetails');
var customerId = triggerElement.closest('.tab-pane').find('.generaldetails').find('.custID').val();
    // Get customer details
   // var customerId = generalDetails.find('.custID').val();
    var cifId = customerId;//generalDetails.find('.cifId').val();
    var sibCustomer = generalDetails.find('.sibCustomer').val();
    var residentialStatus = generalDetails.find('.residentialStatus:checked').val();
    var applicantId = generalDetails.find('.appid').val();
    var slno = detElement.find('.slno').val();
    var wiNum = detElement.find('.wiNum').val();

    // Validate customer ID
    if (!customerId || customerId === "") {
        Swal.fire({
            icon: 'error',
            title: 'Customer ID Required',
            text: 'Only existing customers can choose FD program.',
            confirmButtonText: 'OK'
        });
        return false;
    }

    // Prepare request payload
    var jsonBody = {
        customerId: customerId,
        cifId: cifId,
        sibCustomer: sibCustomer,
        residentialStatus: residentialStatus,
        slno: slno,
        wiNum: wiNum,
        applicantId: applicantId
    };

    console.log("Fetching FD details for:", jsonBody);

    // Show loading
    showLoader();

    // AJAX call to fetch FD details
    $.ajax({
        url: "api/getFDAccountDetailsbycifV2",
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(jsonBody),
        success: function (response) {
            console.log("FD Details fetched successfully:", response);

            // Update the FD accounts table
            updateFDAccountsTable(response, triggerElement);

            // Enable save button
            var programBtn = detElement.find('.save-button-program');
            programBtn.prop("disabled", false);

            hideLoader();

            // Show success message
            Swal.fire({
                icon: 'success',
                title: 'FD Details Retrieved',
                text: 'FD account details have been fetched successfully.',
                timer: 2000,
                showConfirmButton: false
            });
        },
        error: function (xhr, status, error) {
            hideLoader();

            console.error("Error retrieving FD Details:", xhr.responseText, status, error);

            Swal.fire({
                icon: 'error',
                title: 'FD Details Fetch Failed',
                text: xhr.responseText || 'Failed to retrieve FD details. Please try again.',
                confirmButtonText: 'OK'
            });
        }
    });
}
function updateFDAccountsTable(fdAccountResponse, triggerElement) {
    var detElement = triggerElement.closest('.det');
    var fdAccounts = fdAccountResponse.fdAccounts;
    var missingCifIds = fdAccountResponse.missingCifIds || [];
    var totalavailBalance = 0;
    var eligibleCount = 0;

    // Build table HTML
    var tableHtml = '<table class="table table-bordered table-hover">';
    tableHtml += '<thead class="table-light">';
    tableHtml += '<tr>';
    tableHtml += '<th>FD A/C</th>';
    tableHtml += '<th>Account Status</th>';
    tableHtml += '<th>Account Type</th>';
    tableHtml += '<th>CIF IDs</th>';
    tableHtml += '<th>Account Open Date</th>';
    tableHtml += '<th>Maturity Date</th>';
    tableHtml += '<th>Deposit Amount (₹)</th>';
    tableHtml += '<th>Deposit Available (₹)</th>';
    tableHtml += '<th>FSLD Adj (₹)</th>';
    tableHtml += '<th>Available Balance (₹)</th>';
    tableHtml += '<th>Eligible</th>';
    tableHtml += '<th>Action</th>';
    tableHtml += '</tr>';
    tableHtml += '</thead>';
    tableHtml += '<tbody>';

    fdAccounts.forEach(function (accountResponse) {
        if (accountResponse.eligible) {
            var account = accountResponse.vehicleLoanFD;
            var rowClass = accountResponse.eligible ? '' : 'text-muted';
            var cifIds = account.cifid || '';

            tableHtml += '<tr class="' + rowClass + '">';
            tableHtml += '<td><strong>' + account.fdaccnum + '</strong></td>';
            tableHtml += '<td>' + account.fdStatus + '</td>';
            tableHtml += '<td>' + account.singleJoint + '</td>';
            tableHtml += '<td>';
            if (cifIds) {
                var cifArray = cifIds.split(',');
                cifArray.forEach(function(cif) {
                    var trimmedCif = cif.trim();
                    // Highlight missing CIF IDs in red
                    var badgeClass = missingCifIds.includes(trimmedCif) ? 'bg-danger' : 'bg-info';
                    tableHtml += '<span class="badge ' + badgeClass + ' me-1 mb-1">' + trimmedCif + '</span>';
                });
            }
            tableHtml += '</td>';
            tableHtml += '<td>' + formatDate(account.accountOpenDate) + '</td>';
            tableHtml += '<td>' + formatDate(account.maturityDate) + '</td>';
            tableHtml += '<td class="text-end">' + formatCurrency(account.depositAmount) + '</td>';
            tableHtml += '<td class="text-end">' + formatCurrency(account.fdBalAmount) + '</td>';
            tableHtml += '<td class="text-end">' + formatCurrency(account.fsldAdjAmount) + '</td>';
            tableHtml += '<td class="text-end"><strong>' + formatCurrency(account.availbalance) + '</strong></td>';

            if (accountResponse.eligible) {
                tableHtml += '<td><span class="badge bg-success">Yes</span></td>';
                tableHtml += '<td><button type="button" class="btn btn-danger btn-sm delete-fd-btn" data-ino="' + account.ino + '">';
                tableHtml += '<i class="ph-trash me-1"></i>Delete</button></td>';
                eligibleCount++;
            } else {
                tableHtml += '<td><span class="badge bg-secondary">No</span></td>';
                tableHtml += '<td>-</td>';
            }

            tableHtml += '</tr>';

            if (accountResponse.eligible) {
                totalavailBalance += account.availbalance;
            }
        }
    });

    tableHtml += '</tbody>';
    tableHtml += '</table>';

    // Update table
    detElement.find('.fdResponse').empty().html(tableHtml);

    // Show FD details div
    detElement.find('.fdDetailsDiv').show();
    detElement.find('.no-fd-message').hide();

    // Update total available balance
    detElement.find('.totalavailBalance').val(totalavailBalance);
    detElement.find('.totalavailBalance-display').text(formatCurrency(totalavailBalance));

    // Update summary cards
    detElement.find('.fd-count').text(fdAccounts.length);
    detElement.find('.fd-eligible-count').text(eligibleCount);
    detElement.find('.fd-loan-eligibility').text(formatCurrency(totalavailBalance));
    if (missingCifIds && missingCifIds.length > 0) {
        displayMissingCifIds(detElement, missingCifIds);
    } else {
        // Hide missing CIF alert if no missing IDs
        detElement.find('.missing-cif-alert-section').hide();
    }

    // Check for warnings
    if (fdAccounts.length > 0 && !fdAccounts[0].eligible) {
        Swal.fire({
            icon: 'warning',
            title: 'Some FD Accounts Ineligible',
            text: 'Some FD accounts may be ineligible for the loan application.',
            confirmButtonText: 'OK'
        });
    }

    // Check for missing CIF IDs
    if (fdAccountResponse.missingCifIds && fdAccountResponse.missingCifIds.length > 0) {
        Swal.fire({
            icon: 'info',
            title: 'Co-applicants Required',
            html: 'The following CIF IDs need to be added as co-applicants:<br><strong>' +
                  fdAccountResponse.missingCifIds.join(", ") + '</strong>',
            confirmButtonText: 'OK'
        });
    }

    console.log("FD table updated. Total Balance: ₹" + totalavailBalance);
}
function displayMissingCifIds(detElement, missingCifIds) {
    console.log("Displaying missing CIF IDs:", missingCifIds);

    // Build missing CIF IDs HTML
    var missingCifHtml = '';

    missingCifIds.forEach(function(cifId, index) {
        missingCifHtml += '<div class="missing-cif-item">';
        missingCifHtml += '<i class="ph-warning-circle text-warning me-2 fs-5"></i>';
        missingCifHtml += '<span class="cif-id">' + cifId + '</span>';
        missingCifHtml += '</div>';
    });

    // Update the missing CIF list
    detElement.find('.missing-cif-list').html(missingCifHtml);

    // Show the alert section with animation
    detElement.find('.missing-cif-alert-section').slideDown(400);

    // Also show a SweetAlert notification
    Swal.fire({
        icon: 'warning',
        title: 'Co-Applicants Required',
        html: '<div class="text-start">' +
              '<p>The following CIF IDs need to be added as co-applicants:</p>' +
              '<div class="mt-3 mb-3">' +
              missingCifIds.map(cif =>
                  '<div class="badge bg-warning text-dark me-2 mb-2 p-2" style="font-size: 14px;">' +
                  '<i class="ph-user-circle me-1"></i>' + cif +
                  '</div>'
              ).join('') +
              '</div>' +
              '<p class="mb-0"><small class="text-muted">Joint FD accounts require all holders to be part of the loan application.</small></p>' +
              '</div>',
        confirmButtonText: 'Understood',
        confirmButtonColor: '#ffc107',
        showClass: {
            popup: 'animate__animated animate__fadeInDown'
        }
    });
}
function handleNonFOIRSelection(detElement) {
    console.log("========== Non-FOIR Program Selected ==========");

    // Show Non-FOIR div
    detElement.show();

    // Initialize date pickers for 6-month period
    initializeNonFOIRDatePickers(detElement);

    console.log("Non-FOIR section displayed. Ready for statement upload.");
}
function initializeNonFOIRDatePickers(detElement) {
    var startDateElement = detElement.find('.nonfoir-start-date')[0];
    var endDateElement = detElement.find('.nonfoir-end-date')[0];

    if (!startDateElement || !endDateElement) return;

    var currentDate = new Date();
    var currentYear = currentDate.getFullYear();
    var currentMonth = currentDate.getMonth();

    // Calculate 6 months ago
    var sixMonthsAgo = new Date(currentYear, currentMonth - 6, 1);

    // Start Date Picker
    var startDatePicker = new Datepicker(startDateElement, {
        container: '.content-inner',
        buttonClass: 'btn',
        format: 'yyyy-mm',
        pickLevel: 1,
        maxDate: new Date(currentYear, currentMonth, 0), // Last month
        minDate: new Date(currentYear - 2, 0, 1), // 2 years ago
        autohide: true
    });

    // End Date Picker
    var endDatePicker = new Datepicker(endDateElement, {
        container: '.content-inner',
        buttonClass: 'btn',
        format: 'yyyy-mm',
        pickLevel: 1,
        maxDate: new Date(currentYear, currentMonth, 0), // Last month
        minDate: sixMonthsAgo,
        autohide: true
    });

    // Set default values (last 6 months)
    var defaultStartDate = moment().subtract(6, 'months').format('YYYY-MM');
    var defaultEndDate = moment().subtract(1, 'months').format('YYYY-MM');

    detElement.find('.nonfoir-start-date').val(defaultStartDate);
    detElement.find('.nonfoir-end-date').val(defaultEndDate);

    console.log("Date pickers initialized for 6-month period:", defaultStartDate, "to", defaultEndDate);
}
function handleImputedIncomeSelection(detElement) {
    console.log("========== Imputed Income Program Selected ==========");

    // Show imputed income div
    detElement.show();

    // Reset the section to initial state
    detElement.find('.imputed-result-section').hide();
    detElement.find('.no-imputed-message').show();
    detElement.find('.imputed-loading-section').hide();

    console.log("Imputed Income section displayed. Ready for calculation.");
}
$('#loanbody').on('click', '.calculate-imputed-income, .recalculate-imputed', function () {
    console.log("========== Calculating Imputed Income ==========");
    calculateImputedIncome($(this));
});
function calculateImputedIncome(triggerElement) {
    var detElement = triggerElement.closest('.det');
    var tabPane = detElement.closest('.tab-pane');
    var generalDetails = tabPane.find('.generaldetails');
    var kycDetails = tabPane.find('.kycdetails');

    // Get applicant details
    var applicantId = generalDetails.find('.appid').val();
    var wiNum = detElement.find('.wiNum').val();
    var panNo = kycDetails.find('.pan').val();
    var customerId = generalDetails.find('.custID').val();
    var cifId = generalDetails.find('.cifId').val();

    // Validate required fields
    if (!panNo || panNo === "") {
        Swal.fire({
            icon: 'error',
            title: 'PAN Required',
            text: 'PAN number is required for imputed income calculation.',
            confirmButtonText: 'OK'
        });
        return false;
    }

    // Prepare request payload
    var jsonBody = {
        applicantId: applicantId,
        wiNum: wiNum,
        panNo: panNo,
        customerId: customerId,
        cifId: cifId
    };

    console.log("Calculating imputed income for:", jsonBody);

    // Show loading section
    detElement.find('.no-imputed-message').hide();
    detElement.find('.imputed-result-section').hide();
    detElement.find('.imputed-loading-section').show();

    // AJAX call to calculate imputed income
    $.ajax({
        url: "api/calculateImputedIncome",  // DUMMY CONTROLLER URL
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(jsonBody),
        success: function (response) {
            console.log("Imputed income calculated successfully:", response);

            // Hide loading
            detElement.find('.imputed-loading-section').hide();
             var dummyResponse = generateDummyImputedIncomeResponse();

            // Update UI with calculated values
            updateImputedIncomeDisplay(dummyResponse, detElement);

            // Show result section
            detElement.find('.imputed-result-section').show();

            // Show success message
            Swal.fire({
                icon: 'success',
                title: 'Calculation Complete',
                text: 'Imputed income has been calculated successfully.',
                timer: 2000,
                showConfirmButton: false
            });
        },
        error: function (xhr, status, error) {
             detElement.find('.imputed-loading-section').hide();
             var dummyResponse = generateDummyImputedIncomeResponse();
              updateImputedIncomeDisplay(dummyResponse, detElement);
               detElement.find('.imputed-result-section').show();
            // Hide loading
            // detElement.find('.imputed-loading-section').hide();
            // detElement.find('.no-imputed-message').show();
            //
            // console.error("Error calculating imputed income:", xhr.responseText, status, error);
            //
            // Swal.fire({
            //     icon: 'error',
            //     title: 'Calculation Failed',
            //     text: xhr.responseText || 'Failed to calculate imputed income. Please try again.',
            //     confirmButtonText: 'OK'
            // });
        }
    });
}
function generateDummyImputedIncomeResponse() {
    // Generate random values for demo
    var baseIncome = Math.floor(Math.random() * 30000) + 20000; // 20k-50k
    var cibilScore = Math.floor(Math.random() * 200) + 650; // 650-850
    var scorecardRating = Math.floor(Math.random() * 30) + 70; // 70-100

    var cibilAdjustment = Math.floor(baseIncome * 0.15);
    var scorecardFactor = Math.floor(baseIncome * 0.10);
    var riskAdjustment = Math.floor(baseIncome * -0.05);

    var imputedIncome = baseIncome + cibilAdjustment + scorecardFactor + riskAdjustment;

    return {
        imputedIncome: imputedIncome,
        cibilScore: cibilScore,
        scorecardRating: scorecardRating,
        confidenceLevel: Math.floor(Math.random() * 20) + 80, // 80-100%
        breakdown: {
            baseIncome: baseIncome,
            cibilAdjustment: cibilAdjustment,
            scorecardFactor: scorecardFactor,
            riskAdjustment: riskAdjustment
        },
        calculationDate: new Date().toISOString(),
        status: 'SUCCESS'
    };
}
function updateImputedIncomeDisplay(response, detElement) {
    // Main imputed income value
    var imputedIncome = response.imputedIncome || 0;
    detElement.find('.imputed-income-value').val(imputedIncome);
    detElement.find('.imputed-income-display').text('₹ ' + formatCurrencyINR(imputedIncome));
    detElement.find('.final-imputed-display').text('₹ ' + formatCurrencyINR(imputedIncome));

    // CIBIL Score
    var cibilScore = response.cibilScore || 0;
    var cibilBand = getCibilScoreBand(cibilScore);
    detElement.find('.cibil-score-display').text(cibilScore);
    detElement.find('.cibil-score-band').text(cibilBand);

    // Scorecard Rating
    var scorecardRating = response.scorecardRating || 0;
    var scorecardGrade = getScorecardGrade(scorecardRating);
    detElement.find('.scorecard-rating-display').text(scorecardRating);
    detElement.find('.scorecard-grade').text(scorecardGrade);
}
function getCibilScoreBand(score) {
    if (score >= 750) return 'Excellent';
    if (score >= 700) return 'Good';
    if (score >= 650) return 'Fair';
    if (score >= 600) return 'Average';
    return 'Poor';
}
function getScorecardGrade(rating) {
    if (rating >= 90) return 'Grade A+';
    if (rating >= 80) return 'Grade A';
    if (rating >= 70) return 'Grade B+';
    if (rating >= 60) return 'Grade B';
    if (rating >= 50) return 'Grade C';
    return 'Grade D';
}


function formatCurrencyINR(amount) {
    if (!amount || isNaN(amount)) return '0.00';
    return parseFloat(amount).toLocaleString('en-IN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

function getMissingCifIds(detElement) {
    var missingCifIds = [];

    detElement.find('.missing-cif-item .cif-id').each(function() {
        missingCifIds.push($(this).text().trim());
    });

    return missingCifIds;
}


function hasMissingCifIds(detElement) {
    return detElement.find('.missing-cif-alert-section').is(':visible');
}


function validateFDProgram(detElement) {
    var errors = [];
    var warnings = [];

    // Check if customer ID exists
    var customerId = detElement.closest('.tab-pane').find('.generaldetails').find('.custID').val();
    if (!customerId || customerId === "") {
        errors.push("Customer ID is required for FD program");
    }

    // Check if FD details are fetched
    var fdDetailsVisible = detElement.find('.fdDetailsDiv').is(':visible');
    if (!fdDetailsVisible) {
        errors.push("Please fetch FD details before saving");
    }

    // Check if there are eligible FD accounts
    var totalBalance = parseFloat(detElement.find('.totalavailBalance').val()) || 0;
    if (totalBalance <= 0) {
        errors.push("No eligible FD accounts found. Total available balance must be greater than zero.");
    }

    // NEW: Check for missing CIF IDs
    if (hasMissingCifIds(detElement)) {
        var missingCifIds = getMissingCifIds(detElement);
        warnings.push("The following CIF IDs need to be added as co-applicants: " + missingCifIds.join(", "));
    }

    return {
        isValid: errors.length === 0,
        errors: errors,
        warnings: warnings,
        hasMissingCifIds: hasMissingCifIds(detElement),
        missingCifIds: getMissingCifIds(detElement)
    };
}

function calculateRemittances(detElement) {
            const monthlySalary = parseFloat(detElement.find('.monthly-salary-nr').val()) || 0;
            let totalRemittanceSum = 0;
            let netRemittanceSum = 0;
            let validMonthCount = 0;

            // Process each remittance row
            detElement.find('.remittance-row').each(function() {
                const row = $(this);
                const totalRemittance = parseFloat(row.find('.total-remittance').val()) || 0;

                // Calculate Bulk Remittance
                let bulkRemittance = 0;
                if (totalRemittance > monthlySalary) {
                    bulkRemittance = totalRemittance - monthlySalary;
                }

                // Calculate Net Remittance
                const netRemittance = totalRemittance - bulkRemittance;

                // Update the row
                row.find('.bulk-remittance').val(bulkRemittance.toFixed(2));
                row.find('.net-remittance').val(netRemittance.toFixed(2));

                // Add to sums for average calculation
                if (totalRemittance > 0) {
                    totalRemittanceSum += totalRemittance;
                    netRemittanceSum += netRemittance;
                    validMonthCount++;
                }
            });

            // Calculate averages (divide by 12 for all months, not just valid ones)
            const avgTotalRemittance = totalRemittanceSum / 12;
            const avgNetRemittance = netRemittanceSum / 12;

            // Update average fields
            detElement.find('.avg-total-remittance').val(avgTotalRemittance.toFixed(2));
            detElement.find('.avg-net-remittance').val(avgNetRemittance.toFixed(2));

            // Monthly Gross Income = Average of Net Remittance
            const monthlyGrossIncome = avgNetRemittance;

            // Update Monthly Gross Income display
            detElement.find('.calculated-monthly-gross-income').text(formatCurrency(monthlyGrossIncome));
            detElement.find('.monthly-gross-income-nr-hidden').val(monthlyGrossIncome.toFixed(2));

            // Highlight if all 12 months are not filled
            if (validMonthCount < 12) {
                detElement.find('.remittance-grid-header .badge')
                    .removeClass('bg-primary')
                    .addClass('bg-warning')
                    .text('Incomplete (' + validMonthCount + '/12)');
            } else {
                detElement.find('.remittance-grid-header .badge')
                    .removeClass('bg-warning')
                    .addClass('bg-success')
                    .text('Complete (12/12)');
            }
        }

        // Helper function to format currency
        function formatCurrency(amount) {
            return parseFloat(amount).toLocaleString('en-IN', {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            });
        }

        // Initial calculation if data is pre-filled
        $('.non-resident-income-section').each(function() {
            if ($(this).is(':visible')) {
                calculateRemittances($(this).closest('.det'));
            }
        });

function calculateFinalAMI(element) {
    var form = element.closest('.field-row').parent();
    var monthlyGrossField = form.find('.itr-monthly-gross, [name="itrMonthlyGross"], [name="pensionerMonthlyGross"], [name="sepSenpMonthlyGross"], [name="agriculturistMonthlyGross"], .form16-monthly-income, .avg-monthly-income');

    if (monthlyGrossField.length === 0) {
        // Try to find in previous sibling
        monthlyGrossField = element.closest('.field-row').prev().find('.itr-monthly-gross, [name="itrMonthlyGross"], [name="pensionerMonthlyGross"], [name="sepSenpMonthlyGross"], [name="agriculturistMonthlyGross"], .form16-monthly-income, .avg-monthly-income');
    }

    if (monthlyGrossField.length === 0) {
        console.error("Monthly gross income field not found");
        return;
    }

    // Get the monthly gross income value
    var monthlyGrossIncome = parseFloat(monthlyGrossField.val().replace(/,/g, '')) || 0;
    var addBacksObligations = parseFloat(element.val()) || 0;

    // Calculate final AMI
    var finalAMI = monthlyGrossIncome + addBacksObligations;

    // Find and update the final AMI field
    var finalAMIField = form.find('.final-ami');
    if (finalAMIField.length === 0) {
        // Try to find in next sibling
        finalAMIField = element.closest('.field-row').next().find('.final-ami');
    }

    if (finalAMIField.length > 0) {
        finalAMIField.val(finalAMI.toFixed(2));
    }
}

function resetSurrogate(detElement) {
    console.log("---------surrogate reset called");
    statementsData = [];
    skipAutoPopulate = false;
    // Hide the BSA data sections
    detElement.closest('.det').find('[data-response-type="SURROGATE-1"]').hide();
    detElement.closest('.det').find('[data-response-type="SURROGATE-2"]').hide();
    detElement.closest('.det').find('[data-response-type="SURROGATE-3"]').hide();
    addStatement(false, false, detElement);
    renderStatements(detElement);
    detElement.closest('.det').find('.surrogate-edit-mode').show();
    detElement.closest('.det').find('.surrogate-review-mode').hide();
    detElement.closest('.det').find('.review-selections-btn').prop('disabled', true);
    detElement.closest('.det').find('.months-covered').text('0');
    detElement.closest('.det').find('.coverage-status').removeClass('coverage-complete');
    detElement.closest('.det').find('.coverage-status').addClass('coverage-incomplete');
    detElement.closest('.det').find('.coverage-status').text('incomplete');
    // Clear the ABB amount
    detElement.closest('.det').find('.abb-amount').val('');
}

$('#loanbody').on('click', '.resetSurrogateBtnEdit, .resetSurrogateBtnReview', function (e) {
    resetSurrogate($(this));
});

function addStatement(autoStart, autoEnd, detElement) {
    console.log("----ahl-----surrogate add statement");
    // If we already have 3, do not add more
    if (statementsData.length >= MAX_STMTS) return;

    statementsData.push({
        startDate: '',
        endDate: '',
        startDisplay: '',
        endDisplay: '',
        bankCode: '',
        uploaded: false,
        autoStart: !!autoStart,
        autoEnd: !!autoEnd
    });
}

function renderStatements(detElement) {
    console.log("----ahl-----surrogate render");
    detElement.closest('.det').find('.bank-statement-container').empty();
    statementsData.forEach((stmt, idx) => {
        const rowNum = idx + 1;
        const startClass = stmt.autoStart
            ? 'form-control month-year-picker start-date-picker auto-calculated'
            : 'form-control month-year-picker start-date-picker';
        const startDisabled = stmt.autoStart
            ? 'disabled title="Auto-calculated from previous end + 1 month"'
            : '';

        const endClass = stmt.autoEnd
            ? 'form-control month-year-picker end-date-picker auto-calculated'
            : 'form-control month-year-picker end-date-picker';
        const endDisabled = stmt.autoEnd
            ? 'disabled title="Auto-calculated to complete 12 months"'
            : '';

        const rowHtml = `
        <div class="border p-3 mb-2" id="statement-${rowNum}">
          <div class="row">
            <!-- Start -->
            <div class="form-group col-md-4">
              <label>Start Month & Year:</label>
              <div class="date-picker-wrapper">
                <input
                  type="text"
                  class="${startClass}"
                  id="start-${rowNum}"
                  placeholder="Select Start"
                  readonly
                  value="${stmt.startDisplay}"
                  ${startDisabled}
                />
                <input
                  type="hidden"
                  name="bankStatements[${idx}].startDate"
                  value="${stmt.startDate}"
                />
                <i class="date-picker-icon  fas fa-calendar-alt"></i>
              </div>
            </div>

            <!-- End -->
            <div class="form-group col-md-4">
              <label>End Month & Year:</label>
              <div class="date-picker-wrapper">
                <input
                  type="text"
                  class="${endClass}"
                  id="end-${rowNum}"
                  placeholder="Select End"
                  readonly
                  value="${stmt.endDisplay}"
                  ${endDisabled}
                />
                <input
                  type="hidden"
                  name="bankStatements[${idx}].endDate"
                  value="${stmt.endDate}"
                />
                <i class="date-picker-icon fas fa-calendar-alt"></i>
              </div>
            </div>

            <!-- Bank Code -->
            <div class="form-group col-md-3">
              <label>Bank Code:</label>
              <select
                class="form-control bank-select"
                data-idx="${idx}"
              >
<option value="">Select Bank</option><option value="52">South Indian Bank, India</option><option value="3">ADCB, India</option><option value="12101">AG's Bank, India</option><option value="12110">ANZ Bank, India</option><option value="196">AU Small Finance Bank, India</option><option value="625">Abasaheb Patil Rendal Sahakari Bank Ltd, India</option><option value="612">Abhinandan Urban Co-Op Bank Ltd, India</option><option value="151">Abhyudaya Co-Op bank Ltd, India</option><option value="998">Acme Bank Ltd., India</option><option value="12579">Adarniya P.D.Patilsaheb Sahakari Bank Ltd, India</option><option value="161">Adarsh Bank, India</option><option value="12859">Adarsh Co-operative Bank Ltd, India</option><option value="12474">Adarsh Mahila Mercantile Co-Op Bank, India</option><option value="12317">Adilabad District Co-Op Central Bank Ltd, India</option><option value="12614">Aditi Urban Co-Op Credit Society, India</option><option value="12236">Adv Shamraoji Shinde Satyashodhak Sahakari Bank Ltd, India</option><option value="541">Agrasen Co-op Urban Bank Ltd, India</option><option value="12770">Agrasen Nagari Sahakari Bank Ltd, India</option><option value="12592">Agroha Co-Op Urban Bank Ltd, India</option><option value="414">Ahmedabad District Co-Op. Bank Ltd, India</option><option value="79">Ahmedabad Mercantile Co-op Bank Ltd., India</option><option value="395">Ahmednagar Mercantile Co-Op. Bank Ltd, India</option><option value="709">Ahmednagar Shahar Sahakari Bank, India</option><option value="267">Airtel Payments Bank, India</option><option value="12636">Ajantha Urban Co-Op Bank Osmanpura, India</option><option value="247">Akhand Anand Co-Operative Bank Ltd, India</option><option value="472">Akola District Central Co-Op. Bank Ltd, India</option><option value="342">Akola Janata Commercial Co-Op. Bank Ltd, India</option><option value="12704">Alappuzha District Co-Op Bank, India</option><option value="931">Alavi Co-Op Bank Ltd, India</option><option value="12245">Alibag Co-Op Urban Bank Ltd, India</option><option value="945">Almora Urban Co-Op Bank Ltd, India</option><option value="902">Almora Zila Sahkari Bank, India</option><option value="12702">Alnavar Credit Souhardha Co-Op Ltd, India</option><option value="12170">Aman Sahakari Bank Ltd, India</option><option value="516">Amarnath Co-Op. Bank Ltd, India</option><option value="850">Ambajogai Peoples Co-Op. Bank, India</option><option value="12786">Ambarnath Jaihind Co-Op Bank Ltd, India</option><option value="248">Ambernath Jai-Hind Co-Op Bank Ltd, India</option><option value="5">American Express Bank, India</option><option value="12043">Amreli Jilla Madhyastha Sahakari Bank Ltd, India</option><option value="12190">Amreli Nagarik Sahakari Bank, India</option><option value="12290">Anand Mercantile Co-Op Bank Ltd, India</option><option value="12213">Anandeshwari Nagrik Sahakari Bank, India</option><option value="12523">Anant Nagari Sahakari Bank, India</option><option value="12300">Anantapur District Co-Op Central Bank Ltd, India</option><option value="193">Andhra Pradesh Grameena Vikas Bank, India</option><option value="499">Andhra Pragathi Grameena Bank, India</option><option value="12117">Annasaheb Magar Sahakari Bank Ltd, India</option><option value="12731">Apani Sahakari Bank Ltd, India</option><option value="12744">Apex Bank - MPSCB, India</option><option value="504">Apna Sahakari Bank, India</option><option value="12278">Appasaheb Birnale Sahakari Bank Ltd, India</option><option value="468">Arihant Co-operative Bank Ltd, India</option><option value="12180">Arunachal Pradesh Rural Bank, India</option><option value="182">Arvind Sahakari Bank Ltd., India</option><option value="12623">Arya Urban Co-Op Credit Society Ltd Majalgaon, India</option><option value="905">Aryavart Bank, India</option><option value="12291">Ashok Nagari Sahakari Bank Ltd, India</option><option value="12248">Ashok Sahakari Bank Ltd, India</option><option value="924">Ashokanagar Co-Op Bank Ltd, India</option><option value="12231">Ashta Peoples Co-Op Bank Ltd, India</option><option value="767">Assam Gramin Vikash Bank Ltd, India</option><option value="521">Associate Co-Op. Bank Ltd, India</option><option value="12867">Aurangabad District Central Cooperative Bank Ltd, India</option><option value="2">Axis Bank, India</option><option value="12220">Ayshwarya Syndicate Souharda Credit Co-Op Ltd, India</option><option value="12281">Azad Urban Co-Op Bank Ltd, India</option><option value="7">BNP Paribas, India</option><option value="12297">Badagabettu Co-Op Society Ltd, India</option><option value="923">Balasinor Nagarik Sahakari Bank Ltd, India</option><option value="12234">Ballari District Central Co-Op Bank Ltd, India</option><option value="12164">Balotra Urban Co-Op Bank Ltd, India</option><option value="12293">Banaras Merchantile Co-Op Bank, India</option><option value="922">Banas Bank, India</option><option value="12171">Banda Urban Co-Op Bank, India</option><option value="156">Bandhan Bank, India</option><option value="899">Bangiya Gramin Vikash Bank, India</option><option value="840">Bank of Bahrain and Kuwait, India</option><option value="8">Bank of Baroda, India</option><option value="12717">Bank of Ceylon, India</option><option value="9">Bank of India, India</option><option value="10">Bank of Maharashtra, India</option><option value="645">Bankura District Central Co-Op Bank Ltd, India</option><option value="567">Baramati Sahakari Bank, India</option><option value="12162">Baran Nagarik Sahkari Bank Ltd, India</option><option value="73">Barclays Bank, India</option><option value="12992">Barmer Cen Co-op Bank Ltd, India</option><option value="895">Baroda Gujarat Gramin Bank, India</option><option value="12318">Baroda Rajasthan Kshetriya Gramin Bank, India</option><option value="12016">Baroda Uttar Pradesh Gramin Bank, India</option><option value="387">Bassein Catholic Co-Op. Bank Ltd, India</option><option value="12041">Beawar Urban Co-Op Bank Ltd, India</option><option value="774">Belagavi Shree Basaveshwar Co-Op Bank Ltd, India</option><option value="886">Betul Nagrik Sahakari Bank, India</option><option value="12011">Bhadohi Urban Co-Op Bank, India</option><option value="789">Bhadradri Co-Op Urban Bank, India</option><option value="694">Bhagini Nivedita Sahakari Bank Ltd, India</option><option value="531">Bhagyodaya Co-Op. Bank Ltd, India</option><option value="12037">Bhandara District Central Co-Op Bank Ltd, India</option><option value="641">Bharati Sahakari Bank Ltd, India</option><option value="12314">Bharatpur Central Co-Op Bank Ltd, India</option><option value="12270">Bhausaheb Birajdar Nagari Sahakari Bank, India</option><option value="12017">Bhavani Sahakari Bank Ltd, India</option><option value="12212">Bhavani Urban Co-Op Bank Ltd, India</option><option value="12708">Bhel Employees Co-Op Bank Ltd, India</option><option value="12188">Bhilai Nagarik Sahakari Bank Maryadit, India</option><option value="12517">Bhilwara Urban Co-Op Bank Ltd, India</option><option value="12244">Bhingar Urban Co-Op Bank Ltd, India</option><option value="839">Bhuj Commercial Co-Op Bank Ltd, India</option><option value="646">Bicholim Urban Co-Op Bank Ltd, India</option><option value="536">Bihar Gramin Bank Ltd, India</option><option value="12125">Birdev Sahakari Bank Ltd, India</option><option value="183">Bombay Mercantile Co-Operative Bank Ltd., India</option><option value="12217">Brahmadeodada Mane Sahakari Bank Ltd, India</option><option value="695">Buldana Urban Co-Op Credit Society, India</option><option value="12206">Business Co-Op Bank Ltd, India</option><option value="12586">CTBC Bank, India</option><option value="777">Calicut City Service Co-Op Bank, India</option><option value="11">Canara Bank, India</option><option value="12830">Canara Credit Co-op Society Ltd, India</option><option value="118">Capital Local Area Bank, India</option><option value="222">Capital Small Finance Bank, India</option><option value="12147">Cardamom Merchants Co-Op Bank Ltd, India</option><option value="111">Catholic Syrian Bank, India</option><option value="12">Central Bank of India, India</option><option value="12785">Central Madhya Pradesh Gramin Bank, India</option><option value="896">Chaitanya Godavari Grameena Bank, India</option><option value="12487">Chamoli District Co-Op Bank, India</option><option value="12289">Chandrabhaga Urban Multistate Co-Op Credit Society, India</option><option value="12229">Chartered Sahakari Bank Niyamitha, India</option><option value="12833">Chatrapati Multistate Co.Op.Credit Society Ltd, India</option><option value="626">Chhattisgarh Rajya Gramin Bank, India</option><option value="12137">Chhattisgarh State Co-Op Bank Ltd, India</option><option value="894">Chikhali Urban Bank, India</option><option value="12596">Chittorgarh Urban Co-Op Bank Ltd, India</option><option value="12216">Chopda Peoples Co-Op Bank Ltd, India</option><option value="14">Citibank, India</option><option value="385">Citizen Credit Co-Op. Bank Ltd, India</option><option value="413">Citizens Co-Op. Bank Ltd, India</option><option value="57">City Union Bank, India</option><option value="782">Coastal Bank, India</option><option value="12198">Col R D Nikam Sainik Sahakari Bank Ltd, India</option><option value="12161">Colour Merchants' Co-Op Bank Ltd, India</option><option value="12607">Contai Co-Op Bank Ltd, India</option><option value="15">Corporation Bank, India</option><option value="83">Cosmos Co-op. Bank Ltd., India</option><option value="12880">Credit Agricole, India</option><option value="12516">Cuddapah District Central Co-Op Bank Ltd, India</option><option value="12053">D Dhanashri Multistate Co-Op Credit Society Ltd, India</option><option value="12251">D Y Patil Sahakari Bank Ltd, India</option><option value="16">DBS Bank, India</option><option value="18">DCB, India</option><option value="596">DMK Jaoli Bank, India</option><option value="12906">Daiwajna Credit Co-Operative Society Ltd, India</option><option value="904">Dakshin Bihar Gramin Bank, India</option><option value="12265">Dapoli Urban Bank, India</option><option value="12639">Darjeeling District Central Co-Op Bank, India</option><option value="939">Darussalam Co-Op Urban Bank Ltd, India</option><option value="12372">Daund Urban Co-Op Bank Ltd, India</option><option value="12429">Dausa Urban Co-Op Bank Ltd, India</option><option value="12260">Davangere Urban Co-Op Bank Ltd, India</option><option value="120">Deccan Merchants Co-op Bank Ltd, India</option><option value="542">Deendayal Nagari Sahakari Bank Ltd, India</option><option value="249">Deepak Sahakari Bank Ltd, India</option><option value="12525">Delhi Nagrik Sehkari Bank Ltd, India</option><option value="17">Dena Bank, India</option><option value="635">Deogiri Nagari Sahakari Bank Ltd, India</option><option value="49">Deutsche Bank, India</option><option value="12034">Development Co-Op Bank Ltd, India</option><option value="915">Devi Gayatri Co-Op Urban Bank Ltd, India</option><option value="71">Dhanalakshmi Bank Ltd., India</option><option value="12195">Dhanera Mercantile Co-Op Bank Ltd, India</option><option value="13016">Dharmapuri District Central Co-Operative Bank Ltd, India</option><option value="837">Dharmavir Sambhaji Urban Co-Op Bank Ltd, India</option><option value="12574">Dhinoj Nagrik Co-Op Bank Ltd, India</option><option value="12191">Dhule Vikas Sahakari Bank Ltd, India</option><option value="917">District Co-Op Central Bank Ltd Bidar, India</option><option value="12733">Dnyandeep Co-operative Credit Society Ltd, India</option><option value="914">Dnyanradha Multi State Co-Op Credit Society Ltd, India</option><option value="12566">Doha Bank, India</option><option value="192">Dombivli Nagari Sahakari Bank, India</option><option value="816">Dr Jaiprakash Mundada Urban Co Op Bank Ltd, India</option><option value="200">Dr. Annasaheb Chougule Urban Co-Op. Bank Ltd., India</option><option value="12040">Dr. Babasaheb Ambedkar Urban Co-Op Bank Ltd, India</option><option value="12737">Durg DCCB, India</option><option value="933">Durgapur Steel Peoples Co-Op Bank Ltd, India</option><option value="589">ESAF Small Finance Bank, India</option><option value="12544">Eastern &amp; North-East Frontier Railway Co-Op Bank Ltd, India</option><option value="224">Equitas Bank, India</option><option value="12192">Excellent Co-Op Bank, India</option><option value="12994">Fab Tech Multistate Co Op Society Solapur, India</option><option value="19">Federal Bank, India</option><option value="152">Fin Growth Co-Op bank Ltd., India</option><option value="592">Fincare Small Finance Bank, India</option><option value="388">Fino Payment Bank , India</option><option value="173">GP Parsik Bank, India</option><option value="400">GS Mahanagar Co-Op. Bank Ltd, India</option><option value="417">Gadhinglaj Urban Co-Op. Bank Ltd, India</option><option value="543">Gandhibag Sahakari Bank Ltd, India</option><option value="12102">Gandhinagar Nagrik Co-Op Bank, India</option><option value="12044">Gautam Sahakari Bank Ltd, India</option><option value="823">Goa State Co-Op Bank Ltd, India</option><option value="12722">Godavari Laxmi Co-Op Bank Ltd, India</option><option value="865">Godavari Urban Co-Op Bank, India</option><option value="12443">Gondal Nagrik Sahakari Bank Ltd, India</option><option value="12834">Goreshwar Gramin Multi State Co-op credit Society Ltd, India</option><option value="265">Greater Bombay Co-op Bank Ltd, India</option><option value="12522">Guardian Bank Ltd, India</option><option value="764">Guardian Souharda Sahakari Bank, India</option><option value="186">Gujarat Ambuja Co-Op Bank Ltd., India</option><option value="959">Gujarat Mercantile Co-Op Bank, India</option><option value="530">Gujarat State Co-Op. Bank Ltd, India</option><option value="788">Gulshan Mercantile Urban Co-Op Bank, India</option><option value="12257">Guntur District Co-Op Central Bank Ltd, India</option><option value="925">Gurgaon Central Co-Op Bank Ltd, India</option><option value="844">HCBL Co-operative Bank Ltd., India</option><option value="20">HDFC Bank, India</option><option value="554">HP State Co-Op Bank Ltd, India</option><option value="21">HSBC, India</option><option value="12502">Hadagali Urban Co-Op Bank, India</option><option value="12540">Hanamasagar Urban Co-Op Bank Ltd, India</option><option value="637">Hasti Co-Op Bank Ltd, India</option><option value="957">Haveli Sahakari Bank Ltd, India</option><option value="12900">Himachal Pradesh Gramin Bank, India</option><option value="508">Himatnagar Nagarik Sahakari Bank Ltd, India</option><option value="260">Hindusthan Co-Op Bank Ltd, India</option><option value="798">Hutatma Sahakari Bank Ltd, India</option><option value="22">ICICI Bank, India</option><option value="23">IDBI, India</option><option value="140">IDFC Bank(old), India</option><option value="12031">IMC Bank Ltd, India</option><option value="469">Imphal Urban Co-operative Bank Ltd, India</option><option value="12705">Indapur Urban Co Op Bank Ltd, India</option><option value="807">India Post Payments Bank, India</option><option value="24">Indian Bank, India</option><option value="25">Indian Overseas Bank, India</option><option value="858">Indore Cloth Market Co-Op Bank Ltd, India</option><option value="642">Indore Paraspar Sahakari Bank Ltd, India</option><option value="12402">Indore Premier Co-Op Bank Ltd, India</option><option value="879">Indraprastha Sehkari Bank Ltd, India</option><option value="638">Indrayani Co-Op Bank, India</option><option value="26">IndusInd Bank, India</option><option value="12262">Integral Urban Co-Op Bank Ltd, India</option><option value="692">Irinjalakuda Town Co-Op Bank Ltd, India</option><option value="12888">Ithithanam Service Co-Operative Bank Ltd, India</option><option value="27">J&amp;K Bank, India</option><option value="12865">J&amp;K Grameen Bank, India</option><option value="12712">JSKB Khargone, India</option><option value="781">Jai Bhavani Sahakari Bank, India</option><option value="479">Jai Tuljabhavani Urban Co-Op Bank, India</option><option value="12408">Jaihind Urban Co-Op Bank Ltd, India</option><option value="793">Jain Co-Op Bank Ltd, India</option><option value="115">Jalgaon Janata Bank, India</option><option value="509">Jalgaon Peoples Co-Op Bank Ltd, India</option><option value="812">Jalna Merchants Co-Op Bank Ltd, India</option><option value="250">Jalore Nagrik Sah Bank Ltd, India</option><option value="705">Jamia Co-Op Bank Ltd, India</option><option value="900">Jamnagar District Co-Op Bank Ltd, India</option><option value="706">Jamshedpur Urban Co-Op Bank Ltd, India</option><option value="538">Jana Small Finance Bank Ltd, India</option><option value="12126">Janalaxmi Co-Op Bank, India</option><option value="251">Janaseva Sahakari Bank Ltd, India</option><option value="158">Janata Sahakari Bank, India</option><option value="394">Janatha Seva Co-Op. Bank Ltd, India</option><option value="12594">Jandhan Nagari Sahkari Patsanstha Ltd Kolhapur, India</option><option value="13017">Janhit Nagri Sahkari Patsanstha M.Parli Vaijanath, India</option><option value="12205">Jankalyan Co-Op Bank Ltd, India</option><option value="12624">Jankalyan Nagari Sahakari Patsanstha Maryadit, Karad, India</option><option value="466">Jankalyan Sahakari Bank, India</option><option value="951">Jankalyan Urban Co-Op Bank, India</option><option value="938">Jansewa Urban Co-Op Bank Ltd, India</option><option value="252">JayPrakash Narayan Nagari Sahakari Bank Ltd, India</option><option value="12381">Jaysingpur Udgaon Sahakari Bank Ltd, India</option><option value="651">Jharkhand Rajya Gramin Bank, India</option><option value="12928">Jharneshwar Nagrik Sahakari Bank Ltd, India</option><option value="644">Jijamata Mahila Sahakari Bank, India</option><option value="12720">Jila Sahakari Kendriya Bank Maryadit, India</option><option value="12757">Jio Payments Bank Limited, India</option><option value="12036">Jivaji Sahkari Bank Ltd, India</option><option value="708">Jivan Commercial Co-Op Bank Ltd, India</option><option value="12903">Jodhpur Central Co-Op Bank Ltd, India</option><option value="12768">Jodhpur Nagrik Sahakari Bank Ltd, India</option><option value="12209">Junagadh Commercial Co-Op Bank Ltd, India</option><option value="12222">Jyoti Kranti Co-Op Credit Society Ltd, India</option><option value="754">KEB Hana Bank, India</option><option value="12791">Kai.Vyankatrao Patil Kawle Bigar Sheti Sah.Pat.Mydt.Sindhi, India</option><option value="958">Kakinada Co-Op Central Bank Ltd, India</option><option value="412">Kallappanna Awade Ichalkaranji Janata Sahakari Bank, India</option><option value="827">Kalol Nagarik Sahakari Bank Ltd, India</option><option value="343">Kalyan Janata Sahakari Bank Ltd, India</option><option value="803">Kamala Co-Op Bank Ltd, India</option><option value="225">Kangra Co-op Bank Ltd, India</option><option value="693">Kankaria Maninagar Nagrik Sahakari Bank Ltd, India</option><option value="12294">Kannur District Co-Op Bank, India</option><option value="12738">Karad Merchant Sahakari Credit Sanstha Maryadit Karad, India</option><option value="12848">Karavali Credit Co-operative Society Limited, India</option><option value="12013">Karimnagar District Co-Op Central Bank Ltd, India</option><option value="12845">Karmaveer Bhaurao Patil Nagari Sahakari Patsanstha Maryadit, Sangli, India</option><option value="647">Karnala Nagari Sahakari Bank Ltd, India</option><option value="51">Karnataka Bank, India</option><option value="768">Karnataka Central Co-Op Bank Ltd, India</option><option value="890">Karnataka Gramin Bank, India</option><option value="12503">Karnataka Mahila Sahakara Bank, India</option><option value="533">Karnataka Vikas Grameena Bank Ltd, India</option><option value="50">Karur Vysya Bank, India</option><option value="911">Kashi Gramin Bank, India</option><option value="164">Kashipur Urban Co-Op Bank Ltd., India</option><option value="501">Kaveri Grameena Bank, India</option><option value="12961">Keonjhar District Central Co-Op Bank, India</option><option value="539">Kerala Gramin Bank Ltd, India</option><option value="12200">Kerala State Co-Op Bank, India</option><option value="825">Keshav Sehkari Bank Ltd, India</option><option value="227">Khamgaon Urbank Co-op Bank Ltd, India</option><option value="12169">Khammam District Co-Op Central Bank, India</option><option value="756">Khardah Co-Op Bank Ltd, India</option><option value="12196">Kheralu Nagrik Sahakari Bank Ltd, India</option><option value="12499">Kholeshwar Multistate Co-Op Credit Society Ltd, India</option><option value="12208">Kohinoor Sahakari Bank Ltd, India</option><option value="12235">Koilkuntla Co-Op Bank Ltd, India</option><option value="513">Kokan Mercantile Co-Op. Bank Ltd, India</option><option value="578">Kolhapur District Central Co-Operative Bank Ltd, India</option><option value="607">Kolhapur Mahila Sahakari Bank Ltd, India</option><option value="613">Kolhapur Urban Co-Op Bank, India</option><option value="12713">Koliyoor Service Co-operative Bank Ltd, India</option><option value="859">Konark Urban Co-Op Bank Ltd, India</option><option value="639">Kopargaon Peoples Co-Op Bank, India</option><option value="28">Kotak Mahindra Bank, India</option><option value="12842">Kottakkal Co-op Urban Bank, India</option><option value="12823">Koyana Sahakari Bank Ltd, Karad, India</option><option value="12271">Kozhikode District Co-Op Bank, India</option><option value="893">Krishna Bhima Samruddhi Bank, India</option><option value="916">Krishna District Co-Op Central Bank Ltd, India</option><option value="800">Krishna Grameena Bank, India</option><option value="12258">Krishna Mercantile Co-Op Bank Ltd, India</option><option value="12146">Krishna Sahakari Bank Ltd, India</option><option value="12320">Kuberamitra Nidhi Ltd, India</option><option value="399">Kukarwada Nagrik Sahakari Bank, India</option><option value="12028">Kumbhi Kasari Sahakari Bank, India</option><option value="165">Kurmanchal Bank, India</option><option value="12475">Kurnool District Central Co-Op Bank Ltd, India</option><option value="12913">Kurukshetra Central Co-operative Bank, India</option><option value="12618">Kusumbh Kalyan Nidhi Ltd, India</option><option value="12428">Kutch Co-Op Bank Ltd, India</option><option value="12223">Kuttiady Co-Op Urban Bank, India</option><option value="12637">Lakhimpur Urban Co-Op Bank Ltd, India</option><option value="77">Lakshmi Vilas Bank, India</option><option value="12140">Lala Urban Co-Op Bank Ltd, India</option><option value="503">Langpi Dehangi Rural Bank, India</option><option value="12897">Latur District Central Co Op Bank Ltd (LDCC), India</option><option value="12232">Latur Multistate Co-Op Credit Society Ltd, India</option><option value="476">Latur Urban Co-Op. Bank Ltd, India</option><option value="934">Laxmi Urban Co-op Bank, India</option><option value="12015">Liluah Co-Op Bank, India</option><option value="510">Lokmangal Co-Op Bank Ltd, India</option><option value="966">Lokmangal Nagari Sahakari Patsanstha Maryadit, India</option><option value="12613">Loknete Dattaji Patil Sahakari Bank Ltd, India</option><option value="12211">Lokvikas Nagari Sahakari Bank, India</option><option value="942">Lonavala Sahakari Bank, India</option><option value="12173">M D Pawar Peoples Co-Op Bank Ltd, India</option><option value="460">M S Co-Op. Bank Ltd, India</option><option value="12489">MBabaji Date Mahila Sahakari Bank Ltd, India</option><option value="12917">MUFG Bank, India</option><option value="771">Madheshwari Urban Development Co-Op Bank, India</option><option value="847">Madhya Pradesh Gramin Bank, India</option><option value="936">Madhyanchal Gramin Bank, India</option><option value="12576">Madurai District Central Co-Op Bank Ltd, India</option><option value="12508">Mahabubnagar District Co-Op Central Bank, India</option><option value="12645">Mahalakshmi Co-Op Bank Ltd, India</option><option value="797">Mahanagar Nagrik Sahakari Bank, India</option><option value="12877">Mahananda Multistate Urban Co-Operative Credit Society Ltd, Majalgaon, India</option><option value="175">Maharashtra Gramin Bank, India</option><option value="891">Maharashtra Nagari Sahakari Bank, India</option><option value="12911">Maharashtra Urban Credit Co-Op Society Ltd, India</option><option value="12225">Mahatma Fule Urban Co Op Bank, India</option><option value="710">Mahaveer Co-Op Urban Bank Ltd, India</option><option value="174">Mahesh Bank, India</option><option value="620">Mahesh Sahakari Bank Ltd, India</option><option value="577">Mahesh Urban Co-Operative Bank Ltd, India</option><option value="12498">Maheshwar Multistate Co-Op Credit Soc Ltd, India</option><option value="12273">Mahila Sahakari Bank, India</option><option value="12249">Mahila Urban Co-Op Bank Ltd, India</option><option value="261">Mahila Vikash Co-Op Bank Ltd, India</option><option value="12635">Mahoba Urban Co-Op Bank Ltd, India</option><option value="12019">Makarpura Industrial Estate Co-Op Bank Ltd, India</option><option value="12904">Malappuram Service Co-Operative Bank, India</option><option value="12612">Malda District Central Co-Op Bank Ltd, India</option><option value="12008">Malviya Urban Co-Op Bank Ltd, India</option><option value="12991">Mamasaheb Pawar Satyavijay Co-Op Bank Ltd, India</option><option value="12292">Mandvi Mercantile Co-op Bank Ltd, India</option><option value="12219">Mandya District Co-Op Central Bank Ltd, India</option><option value="792">Mangal Co-Op Bank Ltd, India</option><option value="12302">Mangala Credit Co-Op Society Ltd, India</option><option value="12955">Mangalore Catholic Co-Operative Bank Ltd, India</option><option value="384">Maninagar Co-Op. Bank Ltd, India</option><option value="819">Manipal Co Op Bank, India</option><option value="12914">Manipur Rural Bank, India</option><option value="12298">Manipur Womens Co-Op Bank Ltd, India</option><option value="12261">Manjeri Co-Op Urban Bank Ltd, India</option><option value="12925">Manora Urban Nidhi Limited, India</option><option value="529">Manorama Co-Op. Bank Ltd, India</option><option value="12762">Mansing Co-Op Bank Ltd, India</option><option value="12050">Manvi Pattana Souharda Sahakari Bank Niyamita, India</option><option value="12240">Manwath Urban Co-Op Bank, India</option><option value="12998">Maratha Co-Op Bank Ltd, India</option><option value="12606">Marathwada Urban Co-Op Credit Society Ltd, India</option><option value="12514">Markandey Nagari Sahakari Bank Ltd, India</option><option value="12571">Marketyard Commercial Co-Op Bank Ltd, India</option><option value="12764">Mathura District Co-operative Bank, India</option><option value="12570">Mauli Multistate Co-Op Credit Society Ltd, India</option><option value="927">Medak District Co-Op Central Bank Ltd, India</option><option value="12692">Meghalaya Co-Op Apex Bank Ltd, India</option><option value="972">Meghalaya Rural Bank, India</option><option value="12227">Mehsana District Central Co-Op Bank Ltd, India</option><option value="12471">Merchants Co-Op Bank Ltd, India</option><option value="818">Merchants Liberal Co-Op Bank Ltd, India</option><option value="12194">Mizoram Rural Bank, India</option><option value="12918">Mizuho Bank, India</option><option value="657">Model Co-Op Bank Ltd, India</option><option value="12187">Model Co-Op Urban Bank Ltd, India</option><option value="12583">Motiram Agrawal Jalna Merchants Co-Op Bank Ltd, India</option><option value="12510">Mudgal Urban Co-Op Bank Limited, India</option><option value="910">Mugberia Central Co-Op Bank Ltd, India</option><option value="801">Mumbai District Central Co-Op Bank Ltd, India</option><option value="12821">Murshidabad District Central Credit Cooperative Bank Ltd, India</option><option value="12580">Mysore &amp; Chamarajanagar DCC Bank Ltd, India</option><option value="813">Mysore Merchants Co-Op Bank Ltd, India</option><option value="139">NKGSB Bank, India</option><option value="12476">NSDL Payments Bank, India</option><option value="12363">Nadia District Central Co-Op Bank, India</option><option value="12142">Nagar Sahakari Bank, India</option><option value="392">Nagar Urban Co-Op. Bank Ltd, India</option><option value="868">Nagarik Sahakari Bank Ltd, India</option><option value="605">Nagarik Samabay Bank Ltd, India</option><option value="12741">Nagaur Urban Co-Operative Bank Ltd, India</option><option value="843">Nagpur Mahanagar Palika Karmachari Sahakari Bank Ltd, India</option><option value="147">Nagpur Nagarik Sahakari Bank, India</option><option value="166">Nainital Bank, India</option><option value="12954">Nakshtra Urban Co Oprative Credit Society Limited, India</option><option value="12605">Nalgonda DCC Bank, India</option><option value="12515">Nandani Sahakari Bank Ltd, India</option><option value="555">Nanded Merchants Co-Op Bank Ltd, India</option><option value="12595">Narayani Mahila Nagari Sahkari Patsanstha Ltd Kolhapur, India</option><option value="463">Naroda Nagrik Co-Op. Bank Ltd, India</option><option value="12239">Nashik Jilha Mahila Sahakari Bank Ltd, India</option><option value="12843">Nath Multistate Co-Op Credit Society Ltd, India</option><option value="623">National Bank, India</option><option value="12246">National Mercantile Co-Op Bank Ltd, India</option><option value="898">National Urban Co-Op Bank Ltd, India</option><option value="12139">Navabharat Co-Op Urban Bank Ltd, India</option><option value="253">Navapur Mercantile Co-Op Bank Ltd, India</option><option value="12172">Navi Mumbai Co-Op Bank Ltd, India</option><option value="572">Navjeevan Co-Op Bank Ltd, India</option><option value="197">Navnirman Co-op Bank Ltd, India</option><option value="458">Navsarjan Industrial Co-Op. Bank Ltd, India</option><option value="199">Nawanagar Co-op Bank Ltd, India</option><option value="474">Neelkanth Co-Op. Bank Ltd, India</option><option value="12287">Nellore District Co-Op Central Bank Ltd, India</option><option value="12609">Ner Urban Co-Op Credit Society Ltd, India</option><option value="178">New India Co-Op Bank Ltd., India</option><option value="459">Nidhi Co-Op. Bank Ltd, India</option><option value="12052">Nilambur Co-Op Urban Bank, India</option><option value="897">Nirmal Urban Co-Op Bank Ltd, India</option><option value="12896">Nirman Multistate Co-op Cre Soc, India</option><option value="12507">Nishigandha Sahakari Bank Ltd, India</option><option value="12406">Noble Co-Op Bank Ltd, India</option><option value="477">Noida Commercial Co-Op. Bank Ltd, India</option><option value="775">North East Small Finance Bank Ltd, India</option><option value="376">Nutan Nagarik Sahakari Bank Ltd., India</option><option value="12312">Odisha Gramya Bank, India</option><option value="12709">Odisha State Co-Op Bank, India</option><option value="502">Omkar Nagreeya Sahakari Bank, India</option><option value="254">Omprakash Deora Peoples Co-Op Bank Ltd, India</option><option value="29">Oriental Bank of Commerce, India</option><option value="12698">Osmanabad District Central Co-Op Bank Ltd, India</option><option value="266">Osmanabad Janata Sahakari Bank Ltd, India</option><option value="811">Ottapalam Co-Op Urban Bank Ltd, India</option><option value="855">Padmavathi Co-Op Urban Bank, India</option><option value="12730">Padra Nagar Nagrik Sahakari Bank Ltd, India</option><option value="12512">Palakkad District Co-Op Bank Ltd, India</option><option value="973">Pali Urban Co-Op Bank, India</option><option value="772">Palus Sahakari Bank Ltd, India</option><option value="12866">Panangad Cooperative Urban Society, India</option><option value="194">Pandharpur Urban Co-op Bank Ltd, India</option><option value="456">Panipat Urban Co-Op. Bank Ltd, India</option><option value="12275">Panjabrao Deshmuk Urban Co-Op Bank, India</option><option value="12048">Panvel Co-Op Urban Bank Ltd, India</option><option value="12486">Parner Taluka Sainik Sahakari Bank Ltd, India</option><option value="177">Parshwanath Co-Op. Bank Ltd., India</option><option value="885">Paschim Banga Gramin Bank, India</option><option value="12421">Patan Co-Op Bank Ltd, India</option><option value="462">Patan Nagarik Sahakari Bank Ltd, India</option><option value="597">Pavana Sahakari Bank Ltd, India</option><option value="12864">Pavanraje Loksamruddhi Multistate Co-Op. Cre. Society. Ltd, India</option><option value="268">Paytm Payments Bank, India</option><option value="255">Peoples Co-Op Bank Ltd, India</option><option value="946">Pimpalgaon Merchants Co-Op Bank Ltd, India</option><option value="505">Pimpri Chinchwad Sahakari Bank Maryadit Pimpri, India</option><option value="659">Pochampally Bank, India</option><option value="12046">Poornawadi Nagrik Sahakari Bank Ltd, India</option><option value="12953">Porbandar Com. Co-Op. Bank Ltd, India</option><option value="598">Post Office Saving Bank, India</option><option value="576">Pragathi Co Operative Bank Ltd, India</option><option value="884">Pragathi Krishna Gramin Bank, India</option><option value="12303">Pragati Co-Op Bank Ltd, India</option><option value="480">Pragati Sahakari Bank, India</option><option value="396">Prathama Bank , India</option><option value="12035">Prathama U.P. Gramin Bank, India</option><option value="12407">Prathamika Krushi Pattina Sahakara Sangha Niyamitha, India</option><option value="949">Pravara Sahakari Bank Ltd, India</option><option value="525">Prerana Co-Op. Bank Ltd, India</option><option value="947">Prerna Nagri Sahkari Bank, India</option><option value="520">Prime Co-Op. Bank Ltd, India</option><option value="937">Priyadarshani Nagari Sahakari Bank, India</option><option value="704">Priyadarshini Urban Co-Op Bank Ltd, India</option><option value="12296">Proddatur Co-Op Town Bank Ltd, India</option><option value="12993">Progressive Co Operative Bank, India</option><option value="475">Progressive Merc. Co-Op. Bank Ltd, India</option><option value="12253">Progressive Urban Co-Op Bank, India</option><option value="587">Puduppadi Service Co-Operative Bank Ltd, India</option><option value="908">Pune Cantonment Sahakari Bank Ltd, India</option><option value="599">Pune District Central Co-Operative Bank Ltd, India</option><option value="12027">Pune Merchants Co-Op Bank, India</option><option value="12473">Pune Municipal Corporation Servants Co-Op Urban Bank Ltd, India</option><option value="189">Pune Peoples Co-Op Bank, India</option><option value="12748">Pune Sahakari Bank Ltd, India</option><option value="324">Pune Urban Co-Op Bank Ltd, India</option><option value="78">Punjab &amp; Maharashtra Co-Op Bank, India</option><option value="12127">Punjab Gramin Bank, India</option><option value="30">Punjab National Bank, India</option><option value="12144">Punjab State Co-Op Bank Ltd, India</option><option value="31">Punjab and Sind Bank, India</option><option value="471">Purvanchal Bank Ltd, India</option><option value="257">Pusad Urban Co-Op Bank Ltd, India</option><option value="13015">Quilon Co-Operative Urban Bank Ltd, India</option><option value="81">RBL (Ratnakar) Bank, India</option><option value="48">RBS (ABN AMRO), India</option><option value="557">Rabo Bank, India</option><option value="553">Raigad District Central Co-Op Bank Ltd, India</option><option value="12444">Raiganj Central Co-Op Bank Ltd, India</option><option value="12269">Rajadhani Co-Op Urban Bank Ltd, India</option><option value="12277">Rajajinagar Co-op Bank Ltd, India</option><option value="585">Rajarambapu Sahakari Bank Ltd, India</option><option value="12892">Rajarshi Shahu Government Servants Co Op Bank Ltd, India</option><option value="12534">Rajarshi Shahu Multistate Co-Op Credit Society Ltd, India</option><option value="573">Rajarshi Shahu Sahakari Bank Ltd, India</option><option value="390">Rajasthan Marudhara Gramin Bank, India</option><option value="12879">Rajdhani Nagar Sahkari Bank Ltd, India</option><option value="707">Rajgurunagar Sahakari Bank Ltd, India</option><option value="517">Rajkot Commercial Co-Op. Bank Ltd, India</option><option value="608">Rajkot Nagarik Sahakari Bank Ltd, India</option><option value="195">Rajkot Peoples Co-Op Bank Ltd., India</option><option value="12165">Rajlaxmi Urban Co-Op Bank, India</option><option value="12182">Rajputana Mahila Urban Co-Op Bank, India</option><option value="12562">Ramanagaram Urban Co-Op Bank Ltd, India</option><option value="12366">Rameshwar Co-Op Bank Ltd, India</option><option value="527">Ramgarhia Co-Op. Bank Ltd, India</option><option value="888">Ramrajya Sahakari Bank Ltd, India</option><option value="12179">Ratanchand Shah Sahakari Bank, India</option><option value="12274">Ratnagiri District Central Co-Op Bank Ltd, India</option><option value="12989">Ravi Commercial Urban Co-Operative Bank Ltd, India</option><option value="12619">Reserve Bank Employees Co-Op Bank Ltd, India</option><option value="12626">Rudreshwar Urban Co-Op Credit Society Ltd, India</option><option value="12167">Rupamata Multi State Co-Op Credit Society Ltd, India</option><option value="12267">Rythara Seva Sahakara Bank, India</option><option value="12047">SBM Bank, India</option><option value="727">SBTA Bank Ltd, India</option><option value="467">SUCO Bank, India</option><option value="12590">Sadguru Nagrik Sahakari Bank Maryadit, India</option><option value="537">Sadhana Sahakari Bank Ltd, India</option><option value="12794">Sahayog Multistate Credit Co-operative Society Ltd - Gondiya, India</option><option value="600">Sahebrao Deshmukh Co-Operative Bank Ltd, India</option><option value="12193">Sahyadri Sahakari Bank Ltd, India</option><option value="728">Saibaba Nagari Sahakari Bank Ltd, India</option><option value="948">Samarth Sahakari Bank Ltd, India</option><option value="12105">Samastipur District Central Co-operative Bank Ltd, India</option><option value="535">Samata Sahkari Bank Ltd, India</option><option value="883">Sampada Sahakari Bank Ltd, India</option><option value="954">Samruddhi Co-Op Bank Ltd, India</option><option value="769">Sangli District Central Co-Op Bank Ltd, India</option><option value="12143">Sangli Sahakari Bank Ltd, India</option><option value="455">Sangli Urban Co-Op. Bank Ltd, India</option><option value="523">Sangola Urban Co-Op. Bank Ltd, India</option><option value="12221">Sankheda Nagarik Sahakari Bank Ltd, India</option><option value="528">Sanmati Sahakari Bank Ltd, India</option><option value="12215">Sanmitra Sahakari Bank Ltd, India</option><option value="12916">Sant Kabir Multistate Co-Op Credit Society, India</option><option value="369">Sant Sopankaka Sahakari Bank Ltd, India</option><option value="12256">Saptagiri Grameena Bank, India</option><option value="344">Sarangpur Co-Op. Bank Ltd, India</option><option value="185">Saraspur Nagarik Co-Op Bank Ltd., India</option><option value="72">Saraswat Bank, India</option><option value="591">Sardar Bhiladwala Pardi People's Co-Operative Bank Ltd, India</option><option value="943">Sardar Singh Nagrik Sahakari Bank, India</option><option value="878">Sardar Vallabhbhai Sahakari Bank Ltd, India</option><option value="12364">Sardarganj Mercantile Co-Op Bank Ltd, India</option><option value="12202">Sarjeraodada Naik Shirala Sahakari Bank Ltd, India</option><option value="810">Sarva Haryana Gramin Bank, India</option><option value="540">Sarva UP Gramin Bank Ltd, India</option><option value="778">Sarvodaya Co-Op Bank, India</option><option value="580">Sarvodaya Commercial Co-Operative Bank Ltd, India</option><option value="12524">Sarvodaya Nagarik Sahakari Bank Ltd, India</option><option value="393">Sarvodaya Sahakari Bank, India</option><option value="12033">Satara District Central Co-Op Bank, India</option><option value="386">Saurashtra Gramin Bank, India</option><option value="12622">Savanur Urban Co-Op Bank Ltd, India</option><option value="12255">Sawantwadi Urban Co-Op Bank Ltd, India</option><option value="12285">Shahada Peoples Co-Op Bank, India</option><option value="606">Shalini Sahkari Bank Ltd, India</option><option value="76">Shamrao Vittal Co-Operative Bank Ltd., India</option><option value="854">Shankar Nagari Sahakari Bank, India</option><option value="919">Sharad Sahakari Bank Ltd, India</option><option value="532">Sharada Sahkari Bank Ltd, India</option><option value="12129">Shihori Nagrik Sahkari Bank Ltd, India</option><option value="828">Shikshak Sahakari Bank Ltd, India</option><option value="12286">Shikshan Maharshi Dnyandev Mohekar Multistate Credit Soc. Ltd, India</option><option value="12309">Shimoga Arecanut Mandy Merchants Co-Op Bank Ltd, India</option><option value="711">Shinhan Bank, India</option><option value="506">Shirpur People Co-Op Bank Ltd, India</option><option value="12148">Shiva Sahakari Bank , India</option><option value="955">Shivaji Nagari Sahakari Bank Ltd, India</option><option value="258">Shivajirao Bhosale Sahakari Bank Ltd, India</option><option value="389">Shivalik Small Finance Bank, India</option><option value="12116">Shivkrupa Sahakari Patpedhi Ltd, India</option><option value="12491">Shivsahyadri Sahakari Patpedhi Ltd, India</option><option value="12506">Shivshakti Urban Co-Op Bank Ltd, India</option><option value="12104">Shramajivi Nagari Sahakari Pat Sanstha Maryadit, India</option><option value="918">Shramik Nagrik Sahakari Bank, India</option><option value="12958">Shree Bhairavnath Multistate Co-Op Credit Society, India</option><option value="12032">Shree Bharat Co-Op Bank Ltd, India</option><option value="375">Shree Co-Op. Bank Ltd, India</option><option value="660">Shree Dharati Co-Op Bank Ltd, India</option><option value="795">Shree Ganesh Sahakari Bank Ltd, India</option><option value="349">Shree Kadi Nagarik Sahakari Bank Ltd, India</option><option value="12504">Shree Kanyaka Souharda Sahakari Niyamita, India</option><option value="873">Shree Laxmi Co-Op Bank, India</option><option value="12829">Shree Laxmi Mahila Sahakari Bank Ltd, India</option><option value="12886">Shree Mahavir Sahkari Bank Ltd, India</option><option value="524">Shree Mahesh Co-Op. Bank Ltd, India</option><option value="12908">Shree Mahuva Nagrik Sahkari Bank Ltd, India</option><option value="12591">Shree Mungsaji Maharaj Nagari Sahakari Patsanstha, India</option><option value="159">Shree Samarth Sahakari Bank, India</option><option value="12538">Shree Savarkundla Nagrik Sahakari Bank Ltd, India</option><option value="889">Shree Sharada Sahakari Bank Ltd, India</option><option value="12226">Shree Talaja Nagrik Sahakari Bank Ltd, India</option><option value="962">Shree Vardhaman Sahakari Bank Ltd, India</option><option value="12769">Shree Vasantrao Chougule Nagari Sahakari Path Sanstha. Ltd, India</option><option value="791">Shree Warana Sahakari Bank Ltd, India</option><option value="12056">Shri Adinath Co-Op Bank Ltd, India</option><option value="522">Shri Anand Co-Op. Bank Ltd, India</option><option value="960">Shri Anand Nagari Sahakari Bank Ltd, India</option><option value="12268">Shri Beereshwar Co-Op Credit Society Ltd, India</option><option value="12426">Shri Bhailalbhai Contractor Smarak Co-Op Bank Ltd, India</option><option value="12358">Shri Bharat Urban Co-Op Bank, India</option><option value="12243">Shri Chatrapati Shivaji Maharaj Sahakari Bank Niyamith, India</option><option value="259">Shri Chhatrapati Rajarshi Shahu Urban Co-Op Bank Ltd, India</option><option value="12254">Shri Gajanan Lokseva Sahakari Bank Ltd, India</option><option value="964">Shri Gajanan Maharaj Urban Co-Op Bank Ltd, India</option><option value="12264">Shri Gajanan Urban Co-Op Bank Ltd, India</option><option value="866">Shri Kanyaka Nagari Sahakari Bank Ltd, India</option><option value="817">Shri Laxmikrupa Urban Co-Op Bank Ltd, India</option><option value="632">Shri Mahalaxmi Co-Op Bank Ltd Kolhapur, India</option><option value="12369">Shri Mahavir Nagari Sahakari Pethpedhi, India</option><option value="12726">Shri Mahavir Urban Co-Op Bank, India</option><option value="12839">Shri Mahila Sewa Sahakari Bank Ltd, India</option><option value="12505">Shri Mangalnath Multistate Co-Op Credit Society Ltd, India</option><option value="726">Shri Panchganga Nagari Sahakari Bank Ltd, India</option><option value="12869">Shri Prabhulingeshwar Souharda Pattina Sahakari Sangha Niyamit, India</option><option value="932">Shri Rajkot District Co-Operative Bank, India</option><option value="857">Shri Renukamata Multistate Co-Op Urban Credit Society, India</option><option value="12511">Shri Rukmini Sahakari Bank Ltd, India</option><option value="967">Shri Sairam Urban Multi State Co-Op Credit Society Ltd, India</option><option value="12311">Shri Sant Nagebaba Multistate Co-Op Urban Credit Society Ltd, India</option><option value="909">Shri Shivayogi Murughendra Swami Urban Co-Op Bank Ltd, India</option><option value="611">Shri Shiveshwar Nagari Sahakari Bank Ltd, India</option><option value="12755">Shri Swami Samarth Sahakari Bank Ltd, India</option><option value="518">Shri Veershaiv Co-Op. Bank Ltd, India</option><option value="921">Shri Venkatesh Multistate Co-Op Credit Society Ltd, India</option><option value="579">Shri Vinayak Sahakari Bank Ltd, India</option><option value="12828">Shri Vishweshwar NSP, Almala, India</option><option value="12634">Shrikrishna Co-Op Bank Ltd, India</option><option value="12699">Shrimant Thorle Bajirao Peshve Nagari Sahakari Bank, India</option><option value="12241">Shripatraodada Sahkari Bank Ltd, India</option><option value="12178">Shriram Urban Co-Op Bank Ltd, India</option><option value="12000">Shubhlakshmi Mahila Co-Op Bank Ltd, India</option><option value="12038">Shushruti Souharda Sahakara Bank Niyamita, India</option><option value="12520">Siddaganga Urban Co-op Bank Ltd, India</option><option value="13011">Siddhasiri Souharda Sahakari Sangha Ltd, India</option><option value="640">Siddheshwar Sahakari Bank Ltd, India</option><option value="12587">Siddheshwar Urban Co Op Bank Maryadit, India</option><option value="12230">Sihor Mercantile Co-Op Bank Ltd, India</option><option value="974">Sindhudurg District Central Co-Op Bank Ltd, India</option><option value="12272">Sir M Visvesvaraya Co-Op Bank Ltd, India</option><option value="478">Smriti Nagrik Sahakari Bank, India</option><option value="12736">Snehshree Multi State Cooperative Credit Society Ltd, India</option><option value="12543">Solapur District Central Co-operative Bank Ltd, India</option><option value="391">Solapur Janata Sahakari Bank, India</option><option value="861">Solapur Siddheshwar Sahakari Bank Ltd, India</option><option value="12360">Solapur Social Urban Co-Op Bank Ltd, India</option><option value="12128">Sonali Bank Ltd, India</option><option value="12103">Soundarya Souharda Credit Co-Op Ltd, India</option><option value="12201">South Canara District Central Co-Op Bank Ltd, India</option><option value="12012">Sree Charan Souharda Co-Op Bank Ltd, India</option><option value="397">Sree Mahayogi Lakshmamma Co-Op. Bank Ltd, India</option><option value="935">Sree Narayana Guru Co-Op Bank Ltd, India</option><option value="856">Sree Subramanyeswara Co-Op Bank, India</option><option value="12295">Sree Thyagaraja Co-Op Bank Ltd, India</option><option value="604">Sreenidhi Souharda Sahakari Bank Niyamitha, India</option><option value="653">Sri Basaveshwar Sahakari Bank, India</option><option value="12492">Sri Gokarnanath Co-Op Bank Ltd, India</option><option value="12847">Sri Janani Pattina Sahakara Sangha Niyamita, India</option><option value="12870">Sri Jayachamaraja Credit Co-operative Society Ltd, India</option><option value="12259">Sri Kannikaparameshwari Co-Op Bank Ltd, India</option><option value="12876">Sri Nagendra Credit Co-Operative Society Ltd, India</option><option value="12049">Sri Satya Sai Nagrik Sahakari Bank, India</option><option value="12247">Sri Sudha Co-Op Bank Ltd, India</option><option value="12176">Sri Vasavamba Co-Op Bank, India</option><option value="12792">Sri Vidyaranya Credit Co Operative Society Ltd, India</option><option value="12238">St Milagres Credit Souhardha Co-Op Ltd, India</option><option value="32">Standard Chartered Bank, India</option><option value="12518">Standard Urban Co-Op Bank Ltd, India</option><option value="35">State Bank of India, India</option><option value="12753">State Transport Co-Op Bank, India</option><option value="481">Sterling Urban Co-Op Bank, India</option><option value="12315">Sulaimani Co-Op Bank Ltd, India</option><option value="12111">Sumerpur Mercantile Urban Co-Op Bank Ltd, India</option><option value="12884">Sundargarh District Central Co Op Bank, India</option><option value="461">Sundarlal Sawji Urban Co-Op. Bank Ltd, India</option><option value="473">Surat District Co-Op. Bank Ltd, India</option><option value="464">Surat National Co-Op. Bank Ltd, India</option><option value="244">Surat Peoples Co-op Bank Ltd, India</option><option value="507">Suryoday Small Finance Bank Ltd, India</option><option value="92">Sutex Co-op Bank Ltd., India</option><option value="13025">Suvarnakranti Multi State Urban Co-Op. Bank Credit Society Limited,P, India</option><option value="574">Suvarnayug Sahakari Bank, India</option><option value="12280">Swadhaar Urban Multipurpose Nidhi Ltd, India</option><option value="12242">Swatantra Senani Late Shripal Alase Kurundwad Co-Op Bank Ltd, India</option><option value="41">Syndicate Bank, India</option><option value="90">TJSB Sahakari Bank, India</option><option value="804">Tamil Nadu Grama Bank, India</option><option value="75">Tamilnad Mercantile Bank Ltd., India</option><option value="12500">Tamlukghatal Central Co-Op Bank Ltd, India</option><option value="500">Telangana Grameena Bank, India</option><option value="853">Telangana State Co-Op Apex Bank Ltd, India</option><option value="415">Texco Co-Op. Bank Ltd, India</option><option value="907">Textile Co-Op Bank Ltd, India</option><option value="401">Textile Traders Co-Op. Bank Ltd, India</option><option value="226">Thane Bharat Sahakari Bank Ltd, India</option><option value="12373">The A.P. Raja Rajeswari Mahila Co-Op Urban Bank Ltd, India</option><option value="658">The Abhinav Sahakari Bank Ltd, India</option><option value="569">The Ace Co-operative Bank Ltd, India</option><option value="180">The Adinath Co-Operative Bank Ltd., India</option><option value="12186">The Ahmednagar District Central Co-Op Bank Ltd, India</option><option value="877">The Ajara Urban Co-Op Bank Ltd, India</option><option value="187">The Akola Urban Co-Op Bank Ltd., India</option><option value="12850">The Amravati District Co-Operative Bank Ltd, India</option><option value="12319">The Amravati Merchant's Co-Op Bank Ltd, India</option><option value="961">The Amravati Zilla Parishad Shikshak Sahakari Bank Ltd, India</option><option value="864">The Andhra Pradesh State Co-Op Bank, India</option><option value="12482">The Annasaheb Savant Co-Op Urban Bank Mahad Ltd, India</option><option value="920">The Assam Co-Op Apex Bank Ltd, India</option><option value="12250">The Babasaheb Deshmukh Sahakari Bank Ltd, India</option><option value="691">The Bagat Urban Co-Operative Bank Ltd, India</option><option value="12588">The Bailhongal Urban Co-Op Bank Ltd, India</option><option value="12001">The Banaskantha District Central Co-Op Bank, India</option><option value="167">The Banaskantha Mercantile Co. Operative Bank Ltd, India</option><option value="796">The Bangalore City Co-Op Bank Ltd, India</option><option value="12175">The Bantra Co-Op Bank Ltd, India</option><option value="12849">The Bapuji Co-op Bank Ltd, India</option><option value="12009">The Bapunagar Mahila Co-Op Bank, India</option><option value="649">The Bardoli Nagarik Sahakari Bank Ltd, India</option><option value="851">The Baroda Central Co-Op Bank Ltd, India</option><option value="12207">The Baroda City Co-Op Bank Ltd, India</option><option value="806">The Bavla Nagrik Sahkari Bank Ltd, India</option><option value="12988">The Becharaji Nagarik Sahakari Bank Ltd, India</option><option value="12608">The Bengaluru DCC Bank Ltd, India</option><option value="12424">The Bhabhar Vibhag Nagrik Sahakari Bank, India</option><option value="595">The Bhagyalashmi Mahila Sahakari Bank Ltd, India</option><option value="12403">The Bhandara Urban Co-Op Bank Ltd, India</option><option value="122">The Bharat Co-Operative Bank, India</option><option value="12840">The Bharuch District Central Co-operative Bank Limited, India</option><option value="12825">The Bhatkal Urban Co-operative Bank Ltd, India</option><option value="809">The Bhavana Rishi Co-Op Urban Bank Ltd, India</option><option value="12166">The Bhavnagar District Co-Op Bank Ltd, India</option><option value="12563">The Bhiwani Central Co-Op Bank Ltd, India</option><option value="168">The Bhuj Mercantile Coop.Bank Ltd., India</option><option value="12930">The Bijnor Urban Cooperative Bank Ltd, India</option><option value="12397">The Buldana District Central Co-Op Bank Ltd, India</option><option value="633">The Burdwan Central Co-Op Bank Ltd, India</option><option value="12442">The Catholic Co-Op Urban Bank Ltd, India</option><option value="609">The Chanasma Nagrik Sahakari Bank Ltd, India</option><option value="912">The Chandigarh State Co-Op Bank Ltd, India</option><option value="12887">The Chandrapur District Central Co-Op Bank Ltd, India</option><option value="12316">The Chandwad Merchant's  Co-Op Bank Ltd, India</option><option value="794">The Chembur Nagrik Sahkari Bank, India</option><option value="12874">The Chikhli Urban Co-Op Bank, India</option><option value="12827">The Chiplun Urban Co-operative Bank Ltd, India</option><option value="882">The Chitnavispura Sahakari Bank Ltd, India</option><option value="12310">The Chittoor District Co-Op Central Bank Ltd, India</option><option value="12210">The Co-Op Bank of Mehsana Ltd, India</option><option value="162">The Co-Operative Bank of Rajkot, India</option><option value="12018">The Coimbatore District Central Co-Op Bank Ltd, India</option><option value="12005">The Commercial Co-Op Bank Ltd, India</option><option value="12920">The Comptrollers Office Co-operative Bank, India</option><option value="12740">The Cuddalore District Central Cooperative Bank Ltd, India</option><option value="12759">The Dahanu Road Janata Co-op Bank Ltd, India</option><option value="963">The Dahod Mercantile Co-Op Bank Ltd, India</option><option value="558">The Dahod Urban-Co-Op.Bank Ltd, India</option><option value="12832">The Daman and Diu State Co-Operative Bank Ltd, India</option><option value="863">The Deccan Co-Op Urban Bank Ltd, India</option><option value="805">The Delhi State Co-Op Bank Ltd, India</option><option value="12734">The Dharampeth Mahila Multi State Co-Op Society, India</option><option value="12379">The Dharmavaram Co-Op Town Bank Ltd, India</option><option value="12535">The Dhrangadhra Peoples' Co-Op Bank Ltd, India</option><option value="12952">The Digra Janta Mahila Nagari Patsansth, India</option><option value="941">The Eenadu Co-Op Urban Bank Ltd, India</option><option value="12983">The Engandiyur Farmers Service Co-Operative Bank Ltd, India</option><option value="12493">The Faridabad Central Cooperative Bank Ltd, India</option><option value="13010">The Faridkot Central Cooperative Bank Ltd, India</option><option value="188">The Financial Co-Op Bank Ltd., India</option><option value="12378">The Gadchiroli District Central Co-Op Bank Ltd, India</option><option value="627">The Gandevi People's Co-Op Bank Ltd, India</option><option value="12893">The Gandhi Co-Operative Urban Bank Limited, India</option><option value="12497">The Gandhi Gunj Co-Op Bank Ltd, India</option><option value="12304">The Gandhidham Co-Op Bank, India</option><option value="169">The Gandhidham Mercantile Co-operative Bank Ltd., India</option><option value="12174">The Gandhinagar Urban Co-Op Bank, India</option><option value="12282">The Ganga Mercantile Urban Co-Op Bank Ltd, India</option><option value="12564">The Gauhati Co-Op Urban Bank Ltd, India</option><option value="170">The Gayatri Co-Operative Urban Bank Ltd., India</option><option value="790">The Goa Urban Co-Op Bank Ltd, India</option><option value="12875">The Godhara City Co-Operative Bank Ltd, India</option><option value="12616">The Godhra Urban Co-Op Bank, India</option><option value="12577">The Gokak Urban Co-op Credit Bank Ltd, India</option><option value="12750">The Gondia District Central Co-Operative Bank, India</option><option value="12565">The Grain Merchants Co-Op Bank Ltd, India</option><option value="12367">The Guntur Co-Op Urban Bank Ltd, India</option><option value="12747">The Halol Urban Co Op Bank Ltd, India</option><option value="12703">The Hanumantha Nagar Co-Op Bank Ltd, India</option><option value="12014">The Harij Nagrik Sahakari Bank Ltd, India</option><option value="13013">The Haryana State Cooperative Apex Bank Ltd, India</option><option value="952">The Hotel Industrialists Co-Op Bank Ltd, India</option><option value="903">The Hubli Urban Co-Op Bank Ltd, India</option><option value="869">The Hyderabad District Co-Op Central Bank Ltd, India</option><option value="12214">The Ichalkaranji Merchants Co-Op Bank Ltd, India</option><option value="654">The Income-Tax Department Co-Op Bank Ltd, India</option><option value="12863">The Industrial Co-operative Bank Ltd, India</option><option value="624">The Jain Sahakari Bank Ltd, India</option><option value="12409">The Jaipur Central Co-Op Bank Ltd, India</option><option value="12984">The Jaleswar Co-op large Sized Agricultural Credit Society Ltd, India</option><option value="12776">The Jalgaon Dist. Central Co-Op Bank Ltd, India</option><option value="12224">The Jalna Peoples Co-Op Bank Ltd, India</option><option value="12400">The Jamnagar People's Co-Op Bank, India</option><option value="12488">The Jampeta Co-Op Urban Bank Ltd, India</option><option value="846">The Janata Co-Op Bank Ltd, India</option><option value="860">The Janatha Co-Op Bank, India</option><option value="12422">The Jhalawar Nagrik Sahakari Bank Ltd, India</option><option value="913">The Jharkhand State Co-Op Bank Ltd, India</option><option value="12573">The Jijau Commercial Co-Op Bank Ltd, India</option><option value="12519">The Junagadh Jilla Sahakari Bank Ltd, India</option><option value="621">The Kaira District Central Co-Op Bank Ltd, India</option><option value="12585">The Kakatiya Co-Op Urban Bank Ltd, India</option><option value="154">The Kalupur Commercial Co.op. Bank Ltd., India</option><option value="12578">The Kalwan Merchant Co-Op Bank, India</option><option value="586">The Kanakamahalakshmi Co-Operative Bank Ltd, India</option><option value="13012">The Kanara Dcc Bank Ltd, India</option><option value="12885">The Kapurthala Central Cooperative Bank Ltd, India</option><option value="176">The Karad Urban Co-op Bank, India</option><option value="12584">The Karan Urban Co-Op Bank Ltd, India</option><option value="12752">The Karnal Central Co-Operative Bank Ltd, India</option><option value="568">The Karnataka State Co-Operative Apex Bank Ltd, India</option><option value="724">The Karnavati Co-Op Bank Ltd, India</option><option value="12881">The Kendrapara Credit Cooperative Society Ltd Odisha, India</option><option value="12398">The Kerala State Financial Enterprises Ltd, India</option><option value="622">The Khattri Co-Op Urban Bank Ltd, India</option><option value="12184">The Kodinar Nagarik Sahakari Bank Ltd, India</option><option value="12581">The Kodinar Taluka Co-Op Banking Union Ltd, India</option><option value="12617">The Kolar and Chikballapura District Co-Op Central Bank Ltd, India</option><option value="12905">The Koylanchal Urban Co-Op Bank Ltd, India</option><option value="12168">The Kranthi Co-Op Urban Bank, India</option><option value="725">The Kunbi Sahakari Bank Ltd, India</option><option value="703">The Kurla Nagrik Sahakari Bank Ltd, India</option><option value="12758">The Lasalgaon Merchants Co-op Bank Ltd, India</option><option value="12494">The Lunawada Peoples Co-Op Bank Ltd, India</option><option value="12252">The Maharaja Co-Op Urban Bank Ltd, India</option><option value="971">The Maharashtra State Co-Op Bank Ltd, India</option><option value="814">The Malad Sahakari Bank Ltd, India</option><option value="783">The Malkapur Urban Co-Op Bank Ltd, India</option><option value="12739">The Malleswaram Co - Op Bank Ltd, India</option><option value="12871">The Malpur Nagrik Sahkari Bank Ltd, India</option><option value="12425">The Mansa Nagrik Sahakari Bank Ltd, India</option><option value="12929">The Meenachil East Urban Co-operative Bank, India</option><option value="12589">The Mehkar Urban Co-Op Bank Ltd, India</option><option value="928">The Mehsana Nagrik Sahakari Bank, India</option><option value="91">The Mehsana Urban Co-op Bank Ltd., India</option><option value="12533">The Merchants Souharda Sahakara Bank, India</option><option value="12361">The Modasa Nagarik Sahakari Bank Ltd, India</option><option value="12002">The Moga Central Co-Op Bank Ltd, India</option><option value="755">The Mogaveera Co-Op Bank, India</option><option value="12263">The Moti Urban Co-Op Bank Ltd, India</option><option value="12715">The Mulgund Urban Souharda Co-Op Bank Ltd, India</option><option value="575">The Municipal Co-Op Bank Ltd, India</option><option value="824">The Muslim Co-Op Bank Ltd, India</option><option value="12844">The Mysore Silk Cloth Merchants Co-op Bank Ltd, India</option><option value="12362">The Nabadwip Co-Operative Credit Bank Ltd, India</option><option value="953">The Nandura Urban Co Op Bank Ltd, India</option><option value="12042">The Nandurbar Merchant's Co-Op Bank Ltd, India</option><option value="12710">The Nashik District Central Co-Op Bank Ltd, India</option><option value="650">The Nashik Road Deolali Vyapari Sahakari Bank Ltd, India</option><option value="581">The Nasik Merchants Co-Operative Bank Ltd, India</option><option value="926">The National Co-Op Bank Ltd, India</option><option value="867">The Naval Dockyard Co-Op Bank Ltd, India</option><option value="12775">The Nawanshahr Central Cooperative Bank Ltd, India</option><option value="12951">The Nehru Nagar Co-Operative Bank Ltd, India</option><option value="12163">The New Urban Co-Op-Bank Ltd, India</option><option value="12754">The Niphad Urban Co-Op Bank Ltd, India</option><option value="12501">The Nizamabad District Co-Op Central Bank, India</option><option value="12284">The Ojhar Merchant Co-Op Bank, India</option><option value="13009">The Pachora Peoples Co-Operative Bank Limited, India</option><option value="12530">The Panchmahal District Co-Op Bank Ltd, India</option><option value="610">The Panchsheel Mercantile Co-Op Bank Ltd, India</option><option value="12611">The Pandharpur Merchants Co-Op Bank, India</option><option value="12509">The Panipat Central Co-Op Bank Ltd , India</option><option value="12496">The Parbhani District Central Co-Op Bank Ltd, India</option><option value="12604">The Patiala Central Co-Op Bank Ltd, India</option><option value="12790">The Pioneer Urban Co-Operative Bank Ltd, India</option><option value="838">The Pratap Co-Op Bank Ltd, India</option><option value="12760">The Pudukkottai District Central Co Operative Bank Ltd, India</option><option value="12541">The Quepem Urban Multipurpose Co-Op Society Ltd, India</option><option value="930">The Raddi Sahakara Bank Niyamitha, India</option><option value="12430">The Railway Employees Co-Op Bank Ltd, India</option><option value="12724">The Raipur Urban Mercantile Bank, India</option><option value="12010">The Rajasthan Urban Co-Op Bank Ltd, India</option><option value="969">The Rander Peoples Co-Operative Bank, India</option><option value="12368">The Ranuj Nagrik Sahkari Bank Ltd, India</option><option value="12915">The Rewari Central Co-operative Bank Ltd, India</option><option value="849">The SSK Co-Op Bank Ltd, India</option><option value="190">The Sabarkantha District Central Co-Op Bank Ltd., India</option><option value="12396">The Salem District Central Co-Op Bank, India</option><option value="845">The Sangamner Merchants Co-operative Bank Ltd, India</option><option value="12638">The Sangrur Central Co-Op Bank Ltd, India</option><option value="12228">The Santrampur Urban Co-Op Bank Ltd, India</option><option value="12561">The Satana Merchants Co-Op Bank Ltd, India</option><option value="181">The Satara Sahakari Bank Ltd., India</option><option value="12108">The Saurashtra Co-Op Bank Ltd, India</option><option value="12995">The Secunderabad Co Operative Urban Bank Ltd, India</option><option value="12051">The Secunderabad Mercantile Co-Op Urban Bank, India</option><option value="179">The Seva Vikas Co-Operative Bank Ltd., India</option><option value="12237">The Sevalia Urban Co-Op Bank Ltd, India</option><option value="12824">The Shillong Co-operative Urban Bank Ltd, India</option><option value="12960">The Sind Co-Operative Urban Bank Ltd, India</option><option value="12100">The Sirsi Urban Sahakari Bank Ltd, India</option><option value="12288">The Social Co-Op Bank Ltd, India</option><option value="12183">The Sonepat Urban Co-Op Bank Ltd, India</option><option value="12539">The Srivilliputhur Co-Op Urban Bank Ltd, India</option><option value="565">The Surat Mercantile Co-Op. Bank Ltd, India</option><option value="12575">The Swarnabharathi Sahakara Bank Niyamitha, India</option><option value="12926">The Talod Nagrik Sahakari Bank Ltd, India</option><option value="12723">The Tamilnadu Circle Postal Co-Op Bank Ltd, India</option><option value="594">The Tamilnadu State Apex Co-Operative Bank, India</option><option value="887">The Tasgaon Urban Co-Op Bank, India</option><option value="752">The Thane District Central Co-Op Bank, India</option><option value="12380">The Tiruchirapalli District Central Co-Op Bank Ltd, India</option><option value="12603">The Town Co-Op Bank Ltd, India</option><option value="12513">The Tumkur District Co-Op Central Bank Ltd, India</option><option value="582">The Udaipur Mahila Samridhi Urban Co-Operative Bank Ltd, India</option><option value="757">The Udaipur Mahila Urban Co-Op Bank Ltd, India</option><option value="758">The Udaipur Urban Co-Op Bank Ltd, India</option><option value="12831">The Udupi Taluk Agricultural Produce Co-operative Society, India</option><option value="901">The Umreth Urban Co-Op Bank Ltd, India</option><option value="12751">The Unava Nagrik Sahakari Bank Ltd, India</option><option value="643">The Union Co-Op Bank Ltd, India</option><option value="584">The Urban Co-Operative Bank Ltd, India</option><option value="852">The Vaish Co-Op New Bank Ltd, India</option><option value="12835">The Vaish Cooperative Adarsh Bank Ltd, India</option><option value="12107">The Vallabh Vidyanagar Commercial Co-Op Bank Ltd, India</option><option value="12039">The Vani Merchants Co-Op Bank Ltd, India</option><option value="12962">The Vepar Udhyog Vikas Sahakari Bank Limited, India</option><option value="12150">The Veraval Peoples Co-Op Bank, India</option><option value="892">The Visakhapatnam Co-Op Bank Ltd, India</option><option value="588">The Vyankateshwara Sahakari Bank Ltd, India</option><option value="929">The Washim Urban Co-Op Bank Ltd, India</option><option value="12445">The West Bengal State Co-Op Bank Ltd, India</option><option value="12313">The Yadagiri Lakshmi Narasimha Swamy Co-Op Urban Bank Ltd, India</option><option value="808">The Yashwant Co-Op Ltd, India</option><option value="12177">The Yavatmal District Central Co-Op Bank, India</option><option value="881">The Yavatmal Urban Co-Op Bank Ltd, India</option><option value="821">Thrissur District Citizens Co-Op Society Ltd, India</option><option value="12593">Tirumalla Tirupati Multistate Co-Op Credit Society Ltd, India</option><option value="12106">Tirupati Urban Co-Op Bank, India</option><option value="12003">Tripura Gramin Bank, India</option><option value="12045">Tripura State Co-Op Bank, India</option><option value="12138">Trivandrum District Co-Op Bank, India</option><option value="968">Tuljabhavani Urban Multi-state Co-Op Credit Society Ltd, India</option><option value="512">Tumkur Grain Merchants Credit Co-Op. Bank Ltd, India</option><option value="416">Tumkur Merchants Credit Co-Op. Bank Ltd, India</option><option value="820">Tumkur Veerashaiva Co-Op Bank Ltd, India</option><option value="42">UCO Bank, India</option><option value="12189">Udaygiri Multi State Co-Op Credit Society Ltd, India</option><option value="944">Udham Singh Nagar District Co-Op Bank Ltd, India</option><option value="12006">Udyam Vikas Sahakari Bank, India</option><option value="191">Ujjivan Bank, India</option><option value="514">Uma Co-Op. Bank Ltd, India</option><option value="766">Umiya Urban Co-Op Bank, India</option><option value="43">Union Bank of India, India</option><option value="470">United Co-Op. Bank Ltd, India</option><option value="12299">United Mercantile Co-Op Bank, India</option><option value="12728">Unity Small Finance Bank, India</option><option value="826">Unjha Nagarik Sahakari Bank Ltd, India</option><option value="763">Utkal Grameen Bank, India</option><option value="465">Utkarsh Small Finance Bank, India</option><option value="906">Uttar Bihar Gramin Bank, India</option><option value="341">Uttarakhand Gramin Bank, India</option><option value="12109">Uttarakhand State Co-Op Bank, India</option><option value="12136">Uttarbanga Kshetriya Gramin Bank, India</option><option value="12446">Uttarkashi Zila Sahkari Bank Ltd, India</option><option value="12185">VSV Co-Op Bank Ltd, India</option><option value="950">Vadnagar Nagrik Sahkari Bank Ltd, India</option><option value="262">Vaidyanath Urban Co-op Bank Ltd, India</option><option value="454">Vaijapur Merchants Co-Op. Bank Ltd, India</option><option value="163">Vaishya Nagari Sahakari Bank, India</option><option value="880">Vaishya Sahakari Bank Ltd, India</option><option value="12567">Valmiki Urban Co-Op Bank Ltd, India</option><option value="12572">Valsad District Central Co-Op Bank Ltd, India</option><option value="157">Varachha Co-op Bank Ltd., India</option><option value="802">Vardhaman Mahila Co-Op Urban Bank Ltd, India</option><option value="956">Vardhaman Nagari Sahakari Patsanstha Maryadit, India</option><option value="590">Vasai Janata Sahakari Bank Ltd, India</option><option value="302">Vasai Vikas Sahakari Bank Ltd, India</option><option value="12301">Vasundhara Mahila Nagari Sahakari Bank, India</option><option value="418">Veerashaiva Sahakari Bank Ltd, India</option><option value="511">Veraval Mercantile Co-Op Bank Ltd, India</option><option value="398">Vidarbha Merchants Urban Co-Op. Bank Ltd, India</option><option value="940">Vidharbha Konkan Gramin Bank, India</option><option value="534">Vidya Sahkari Bank Ltd, India</option><option value="12181">Vidyanand Co-Op Bank, India</option><option value="12852">Vidyasagar Central Co Op Bank, India</option><option value="12912">Vighnaharta Multi State Co-Op. Credit Society, India</option><option value="12846">Vijay (MRN) Souhard Credit Sahakari Ltd, India</option><option value="12891">Vijay Balaji Urban Nidhi Limited, India</option><option value="519">Vijay Co-Op. Bank Ltd, India</option><option value="12004">Vijay Commercial Co-Op Bank Ltd, India</option><option value="45">Vijaya Bank, India</option><option value="12423">Vijaypur Sahakari Bank, India</option><option value="566">Vikas Sahakari Bank Ltd, India</option><option value="648">Vikas Souharda Co-Op Bank Ltd, India</option><option value="171">Vikramaditya Nagrik Sahakari Bank, India</option><option value="12154">Vilas Bank, India</option><option value="12602">Vipra Vividhoddesha Souhardha Sahakari Niyamitha, India</option><option value="12145">Viramgam Mercantile Co-Op Bank Ltd, India</option><option value="12721">Vishwas Co-Op Bank Ltd, India</option><option value="457">Vishweshwar Sahakari Bank Ltd, India</option><option value="263">Vita Merchants Co-Op Bank Ltd, India</option><option value="564">Vyapari Sahakari Bank Maryadit, India</option><option value="636">Vyaparik Audyogik Sahkari Bank, India</option><option value="12153">Vyavsayik Sahakari Bank Ltd, India</option><option value="198">Vysya Co-op Bank Ltd, India</option><option value="160">Wai Urban Co-Operative Bank, India</option><option value="12007">Wana Nagrik Sahkari Bank Ltd, India</option><option value="12377">Warangal Urban Co-Op Bank Ltd, India</option><option value="544">Wardha Nagari Sahakari Adhikosh Bank Ltd, India</option><option value="12763">Wardha Zilla Parishad Employees (Urban) Co-op Bank Ltd, India</option><option value="770">Wardhaman Urban Co-Op bank Ltd, India</option><option value="12841">Woori Bank, India</option><option value="12625">Yash Multistate Rural Co-Op Credit Society Ltd, India</option><option value="12542">Yash Urban Co-Op Credit Society Ltd, India</option><option value="46">Yes Bank, India</option><option value="184">Yeshwant Urban Co-Op Bank Ltd., India</option><option value="822">Zila Sahkari Bank Ltd, India</option><option value="172">Zoroastrian Bank, India</option>

              </select>
            </div>

            <!-- Remove Button -->
            
          </div>
        </div>
      `;
        detElement.closest('.det').find('.bank-statement-container').append(rowHtml);
    });

    // Initialize pickers for each row
    statementsData.forEach((stmt, idx) => {
        initPicker(idx, detElement);
        // Set the Bank Code in dropdown
        //$(`.bank-select[data-idx="${idx}"]`).val(stmt.bankCode);
        detElement.closest('.det').find(`.bank-select[data-idx="${idx}"]`).val(stmt.bankCode);
    });

    // If only 1 row => cannot remove
    if (statementsData.length === 1) {

        detElement.closest('.det').find('.remove-statement').prop('disabled', true);
    } else {
        detElement.closest('.det').find('.remove-statement').prop('disabled', false);
    }

    // If we already have 3 statements, hide or disable the add button
    if (statementsData.length >= MAX_STMTS) {
        //
        detElement.closest('.det').find('.addStatementBtn').prop('disabled', true);
    } else {
        detElement.closest('.det').find('.addStatementBtn').prop('disabled', false);
    }

    updateCoverageUI(detElement);
}

// Remove statement
$('#loanbody').on('click', '.remove-statement', function (e) {
    const idx = parseInt($(this).data('idx'));
    // must remove in reverse order
    if (idx !== statementsData.length - 1) {
        alert("Remove statements from last to first to avoid gaps.");
        return;
    }
    statementsData.pop();
    renderStatements($(this));
});

// Bank code change
$('#loanbody').on('change', '.bank-select', function (e) {
    const idx = parseInt($(this).data('idx'));
    statementsData[idx].bankCode = $(this).val() || '';
    updateCoverageUI($(this));
});

// ADDED FOR MANUAL ADD

$('#loanbody').on('click', '.addStatementBtn', function (e) {
    addStatement(false, false, $(this));
    renderStatements($(this));
});

/************************************************************
 * 3. Datepicker & Auto-Calculation
 ************************************************************/
function initPicker(idx, detElement) {
    const rowNum = idx + 1;
    const $start = detElement.closest('.det').find(`.start-date-picker[id="start-${rowNum}"]`);
    const $end = detElement.closest('.det').find(`.end-date-picker[id="end-${rowNum}"]`);

    // const $start = $(`#start-${rowNum}`);
    // const $end = $(`#end-${rowNum}`);

    if (!$start.data('datepicker')) {
        $start.datepicker({
            format: "MM yyyy",
            startView: "months",
            minViewMode: "months",
            autoclose: true,
            clearBtn: true
        }).on('changeDate', function (e) {
            if (e.date) {
                const newVal = formatYyyyMm(e.date);
                statementsData[idx].startDate = newVal;
                statementsData[idx].startDisplay = formatDisplay(e.date);

                // If the end date is older than new start => reset the end
                if (
                    statementsData[idx].endDate &&
                    compareYyyyMm(statementsData[idx].endDate, newVal) < 0
                ) {
                    statementsData[idx].endDate = '';
                    statementsData[idx].endDisplay = '';
                }
            } else {
                statementsData[idx].startDate = '';
                statementsData[idx].startDisplay = '';
            }
            renderStatements(detElement);
        });
    }

    if (!$end.data('datepicker')) {
        $end.datepicker({
            format: "MM yyyy",
            startView: "months",
            minViewMode: "months",
            autoclose: true,
            clearBtn: true
        }).on('changeDate', function (e) {
            if (e.date) {
                const newVal = formatYyyyMm(e.date);
                statementsData[idx].endDate = newVal;
                statementsData[idx].endDisplay = formatDisplay(e.date);
                if (idx === 1 && statementsData.length > 2) {
                    statementsData = statementsData.slice(0, 2);
                }

                // If we have a start date, see how many months are covered
                if (statementsData[idx].startDate) {
                    const monthsSpanned = monthDiffInclusive(
                        statementsData[idx].startDate,
                        statementsData[idx].endDate
                    );
                    // If a single row covers >=12 months, disable auto-add
                    if (monthsSpanned >= 12) {
                        skipAutoPopulate = true;
                    } else {
                        skipAutoPopulate = false;
                        if (statementsData.length > 1 && idx === 0) {
                            statementsData = [statementsData[0]]; // Keep only first row
                            renderStatements(detElement);
                        }
                    }
                }

                // Only do auto-add logic if skipAutoPopulate is FALSE
                if (!skipAutoPopulate) {
                    const coverageAfterThis = calcCoverageSoFar();
                    // If coverage is 12 after just one row => do nothing
                    if (coverageAfterThis === 12 && idx === 0) {
                        // do nothing (but user may still manually add if desired)
                    }
                    // If coverage is < 12 but we are at first row => add second row
                    else if (coverageAfterThis < 12 && idx === 0) {
                        addStatement(true, false, detElement);
                        setNextStartDate(statementsData[0].endDate, 1);
                    }
                    // If coverage is 12 after second row => do nothing
                    else if (coverageAfterThis === 12 && idx === 1) {
                        // do nothing
                    }
                    // If coverage < 12 after second row => add third row
                    else if (coverageAfterThis < 12 && idx === 1) {
                        addStatement(true, true, detElement);
                        setNextStartDate(statementsData[1].endDate, 2);
                        fillThirdEndDate(2, coverageAfterThis);
                    }
                }
            } else {
                statementsData[idx].endDate = '';
                statementsData[idx].endDisplay = '';
            }
            renderStatements(detElement);
        });
    }
}

// Format a Date object as YYYY-MM
function formatYyyyMm(dateObj) {
    const y = dateObj.getFullYear();
    let m = dateObj.getMonth() + 1;
    if (m < 10) m = '0' + m;
    return `${y}-${m}`;
}

// User-friendly format
function formatDisplay(dateObj) {
    const y = dateObj.getFullYear();
    const m = dateObj.getMonth(); // 0-based
    const months = [
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    ];
    return `${months[m]} ${y}`;
}

// Compare YYYY-MM => negative if a < b
function compareYyyyMm(a, b) {
    const [ay, am] = a.split('-').map(Number);
    const [by, bm] = b.split('-').map(Number);
    if (ay === by) return (am - bm);
    return (ay - by);
}

// Return inclusive month difference between YYYY-MM strings
function monthDiffInclusive(start, end) {
    // e.g. "2023-01" to "2023-01" => 1 month
    //      "2023-01" to "2023-02" => 2 months
    const [sy, sm] = start.split('-').map(Number);
    const [ey, em] = end.split('-').map(Number);
    return (ey - sy) * 12 + (em - sm) + 1;
}

// Auto-set the next row's start date = current row's end + 1 month
function setNextStartDate(currentEnd, nextIdx) {
    if (!currentEnd) return;
    const [yy, mm] = currentEnd.split('-').map(Number);
    let nextY = yy, nextM = mm + 1;
    if (nextM > 12) {
        nextM = 1;
        nextY++;
    }
    const nextMStr = nextM < 10 ? '0' + nextM : '' + nextM;
    statementsData[nextIdx].startDate = `${nextY}-${nextMStr}`;
    statementsData[nextIdx].startDisplay =
        formatMonthName(nextM - 1) + ' ' + nextY;
    statementsData[nextIdx].autoStart = true;
}

// For the third row, auto-fill End Date to exactly reach 12 months
function fillThirdEndDate(thirdIdx, coverageSoFar) {
    const missing = 12 - coverageSoFar;
    if (missing <= 0) return;

    const startVal = statementsData[thirdIdx].startDate;
    if (!startVal) return;
    const [sy, sm] = startVal.split('-').map(Number);

    // e.g. if coverageSoFar=8 => missing=4 => end is start + (4 - 1) months
    let endY = sy, endM = sm + missing - 1;
    while (endM > 12) {
        endM -= 12;
        endY++;
    }
    const endMStr = endM < 10 ? '0' + endM : '' + endM;
    statementsData[thirdIdx].endDate = `${endY}-${endMStr}`;
    statementsData[thirdIdx].endDisplay =
        formatMonthName(endM - 1) + '' + endY;
    statementsData[thirdIdx].autoEnd = true;
}

function formatMonthName(idx) {
    const arr = [
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    ];
    return arr[idx];
}

// Coverage after all currently valid statements
function calcCoverageSoFar() {
    const covered = new Set();
    statementsData.forEach(s => {
        if (!s.startDate || !s.endDate) return;
        const [sy, sm] = s.startDate.split('-').map(Number);
        const [ey, em] = s.endDate.split('-').map(Number);
        let cur = new Date(sy, sm - 1, 1);
        const end = new Date(ey, em - 1, 1);
        while (cur <= end) {
            const yy = cur.getFullYear();
            let mm = cur.getMonth() + 1;
            if (mm < 10) mm = '0' + mm;
            covered.add(`${yy}-${mm}`);
            cur.setMonth(cur.getMonth() + 1);
        }
    });
    return covered.size;
}

/************************************************************
 * 4. Coverage UI + "Review Selections" Button
 ************************************************************/
function updateCoverageUI(detElement) {
    const total = calcCoverageSoFar();
    // detElement.closest('.det').find('.months-covered')
    detElement.closest('.det').find('.months-covered').text(total);
    if (total === 12) {
        detElement.closest('.det').find('.coverage-status').removeClass('coverage-incomplete');
        detElement.closest('.det').find('.coverage-status').addClass('coverage-complete');
        detElement.closest('.det').find('.coverage-status').text('(complete)');
        // $('#coverage-status')
        //   .removeClass('coverage-incomplete')
        //   .addClass('coverage-complete')
        //   .text('(complete)');
    } else {
        detElement.closest('.det').find('.coverage-status').removeClass('coverage-complete');
        detElement.closest('.det').find('.coverage-status').addClass('coverage-incomplete');
        detElement.closest('.det').find('.coverage-status').text('(incomplete)');
        // $('#coverage-status')
        //   .removeClass('coverage-complete')
        //   .addClass('coverage-incomplete')
        //   .text('(incomplete)');
    }

    // All bank codes chosen?
    let nonEmptyBank = statementsData.every(s => s.bankCode);
    // Exactly 12 months coverage?
    const coverageOk = (total === 12);
    // Must be 1..3 statements
    let canReview = coverageOk && nonEmptyBank &&
        (statementsData.length >= 1 && statementsData.length <= 3);
    // $('#review-selections-btn').prop('disabled', !canReview);
    detElement.closest('.det').find('.review-selections-btn').prop('disabled', !canReview);
}

// "Review Selections" => Show summary
$('#loanbody').on('click', '.review-selections-btn', function (e) {

    $(this).closest('.det').find('.surrogate-edit-mode').slideUp(400, function () {
        buildSummary($(this));
        $(this).closest('.det').find('.surrogate-review-mode').slideDown(400);
    });
});

function buildSummary(detElement) {
    detElement.closest('.det').find('.surrogate-summary-body').empty();
    statementsData.forEach((stmt, idx) => {
        const rowNo = idx + 1;
        const startLbl = (stmt.startDisplay || '').replace('Selected: ', '');
        const endLbl = (stmt.endDisplay || '').replace('Selected: ', '');
        const bankLbl = stmt.bankCode || '';
        const status = stmt.uploaded
            ? `<span class="text-success">Uploaded</span>`
            : `<span class="text-muted">Pending</span>`;

        const rowHtml = `
      <tr>
        <td>${rowNo}</td>
        <td>${startLbl}</td>
        <td>${endLbl}</td>
        <td>${bankLbl}</td>
        <td>
          <button
            type="button"
            class="btn btn-sm btn-info review-upload-btn"
            data-idx="${idx}"
          >
            Upload
          </button>
          <span class="ml-2" id="upload-status-${idx}">
            ${status}
          </span>
        </td>
      </tr>
      `;
        detElement.closest('.det').find('.surrogate-summary-body').append(rowHtml);
    });
}

// Back to Edit
$('#loanbody').on('click', '.backToEditBtn', function (e) {
    $(this).closest('.det').find('.surrogate-review-mode').slideUp(400, function () {
        $(this).closest('.det').find('.surrogate-edit-mode').slideDown(400);
    });
});

/************************************************************
 * 5. Upload in Review Mode
 ************************************************************/
$('#loanbody').on('click', '.review-upload-btn', function (e) {
    e.preventDefault();
    handleSurrogateStatementUpload($(this));
});


// Final Submit
$('#finalSubmitBtn').on('click', function () {
    // check if all are uploaded
    const allUploaded = statementsData.every(s => s.uploaded);
    if (!allUploaded) {
        alert('Not all statements are uploaded. Please upload first.');
        return;
    }
    alert('All statements uploaded! Submitting final data...');
    // TODO: actual form submission or next step
});

/************************************************************
 * payslip functions
 ************************************************************/
// Function to add a new payslip row
function addPayslipRow(triggerElement) {
    var tableBody = triggerElement.closest('.salaried-section').find('.payslip-table-body');
    var rowCount = tableBody.find('tr').length;

    // Get current date info for defaults
    var currentDate = new Date();
    var currentMonth = currentDate.getMonth(); // 0-based
    var currentYear = currentDate.getFullYear();
    var prevYear = currentYear - 1;
    var nextYear = currentYear + 1;

    // Limit to maximum 3 rows (3 months)
    if (rowCount >= 3) {
        alertmsg("Maximum 3 months of payslips can be added.");
        return;
    }

    // Create a new row with month selector, year selector, file upload, and amount input
    var rowHtml = `
        <tr>
            <td>
                <select class="form-control payslip-month">
                    <option value="">Select Month</option>
                    <option value="1">January</option>
                    <option value="2">February</option>
                    <option value="3">March</option>
                    <option value="4">April</option>
                    <option value="5">May</option>
                    <option value="6">June</option>
                    <option value="7">July</option>
                    <option value="8">August</option>
                    <option value="9">September</option>
                    <option value="10">October</option>
                    <option value="11">November</option>
                    <option value="12">December</option>
                </select>
            </td>
            <td>
                <select class="form-control payslip-year">
                    <option value="">Select Year</option>
                    <option value="${prevYear}">${prevYear}</option>
                    <option value="${currentYear}" selected>${currentYear}</option>
                    <option value="${nextYear}">${nextYear}</option>
                </select>
            </td>
            <td>
                <input type="file" class="form-control payslip-file base64file" accept=".pdf,.jpg,.jpeg,.png" data-max-size="2097152">
            </td>
            <td>
                <input type="number" class="form-control payslip-amount" placeholder="Income Amount">
            </td>
            <td>
                <button type="button" class="btn btn-danger btn-sm delete-payslip-row">
                    <i class="ph-trash"></i>
                </button>
            </td>
        </tr>
    `;

    tableBody.append(rowHtml);

    // Initialize select2 for the new dropdown if it's being used in your project
    if ($.fn.select2) {
        tableBody.find('.payslip-month').last().select2({
            templateResult: formatState,
            templateSelection: formatState
        });

        tableBody.find('.payslip-year').last().select2({
            templateResult: formatState,
            templateSelection: formatState
        });
    }
}

// Function to delete a payslip row
function deletePayslipRow(triggerElement) {
    var tableBody = triggerElement.closest('.payslip-table-body');
    var rowCount = tableBody.find('tr').length;

    if (rowCount <= 1) {
        alertmsg("Cannot delete the only row. At least one payslip is required.");
        return;
    }

    triggerElement.closest('tr').remove();
    calculateTotalIncome(tableBody);
}

// Function to calculate total income from payslips
function calculateTotalIncome(triggerElement) {
    var tableBody = (triggerElement.is('tbody')) ?
        triggerElement :
        triggerElement.closest('.salaried-section').find('.payslip-table-body');
    var total = 0;

    tableBody.find('.payslip-amount').each(function () {
        var amount = parseFloat($(this).val()) || 0;
        total += amount;
    });

    // Update average monthly income (based on number of payslips)
    var rowCount = tableBody.find('tr').length;
    var avgMonthly = rowCount > 0 ? (total / rowCount).toFixed(2) : 0;

    triggerElement.closest('.salaried-section').find('.total-income').val(total.toFixed(2));
    triggerElement.closest('.salaried-section').find('.avg-monthly-income').val(avgMonthly);
}


function initBankStatementDatepickers() {
    // Define date picker selectors specifically for bank statements
    const datePickerSelectors = [
        '.bank-start-date', '.bank-end-date',
        '.rtr-debit-start-date', '.rtr-debit-end-date',
        '.rtr-loan-start-date', '.rtr-loan-end-date',
        '.rtr-topup-debit-start-date', '.rtr-topup-debit-end-date',
        '.rtr-topup-loan-start-date', '.rtr-topup-loan-end-date'
    ];

    // Create a unified selector string
    const selectorString = datePickerSelectors.join(', ');

    // Initialize for existing elements
    $(selectorString).each(function() {
        if (!$(this).data('datepicker')) {
            $(this).datepicker({
                format: "yyyy-mm",
                startView: "months",
                minViewMode: "months",
                autoclose: true
            });
        }
    });

    // Set up a MutationObserver to detect when new elements are added to the DOM
    const observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            if (mutation.addedNodes.length) {
                // Check if any of the added nodes or their children match our selectors
                $(mutation.addedNodes).find(selectorString).add(
                    $(mutation.addedNodes).filter(selectorString)
                ).each(function() {
                    if (!$(this).data('datepicker')) {
                        $(this).datepicker({
                            format: "yyyy-mm",
                            startView: "months",
                            minViewMode: "months",
                            autoclose: true
                        });
                    }
                });
            }
        });
    });

    // Start observing the document body for changes
    observer.observe(document.body, { childList: true, subtree: true });

    // For elements added via jQuery that might not trigger the MutationObserver
    $('#loanbody').on('focus', selectorString, function() {
        if (!$(this).data('datepicker')) {
            $(this).datepicker({
                format: "yyyy-mm",
                startView: "months",
                minViewMode: "months",
                autoclose: true
            });
            $(this).datepicker('show');
        }
    });
}

//###START
function fetchProgramDetails(triggerElement) {
    var applicantId = triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val();
    var wiNum = $('#winum').val();
    var slno = $('#slno').val();
    console.log("Fetching program details with: applicantId=", applicantId, "wiNum=", wiNum, "slno=", slno);
    if (!applicantId || !slno || !wiNum) {
        console.warn("Validation failed: applicantId, slno, or wiNum is empty. Aborting AJAX call.");
        return; // Exit the function early if validation fails
    }
    showLoader();
    $.ajax({
        url: 'fetch/get-program-details',
        type: 'POST',
        contentType: 'application/json',
        async: false,
        data: JSON.stringify({
            applicantId: applicantId,
            slno: slno,
            wiNum: wiNum
        }),
        success: function (response) {
            if (response.status === 'S') {
                if (response.programDetails === null) {
                    console.log('No program found for this applicant');
                } else {
                    console.log('Program details fetched successfully');
                    updateFormWithProgramDetails(response.programDetails, triggerElement);
                }
            } else {
                console.error('Failed to fetch program details:', response.message);
                triggerElement.closest('.det').find('.programCode').val('').trigger('change');
            }
            hideLoader();
        },
        error: function (xhr, status, error) {
            console.error('Error fetching program details:', error);
            hideLoader();
            triggerElement.closest('.det').find('.programCode').val('').trigger('change');
        }
    });
}

function updateFormWithProgramDetails(programDetails, triggerElement) {
    console.log("Updating form with program details for: " + programDetails.loanProgram);
    let savedProgram = programDetails.loanProgram;
    let selectedProgram = triggerElement.closest('.det').closest('.tab-pane').find('.programCode').val();
    var switchProgram = "";
    if (!selectedProgram) {
        selectedProgram = "NONE";
        //resetProgramFields(triggerElement);
    }
    console.log("saved progarm is " + savedProgram);
    console.log("selected progarm is " + selectedProgram);
    if (savedProgram && selectedProgram && savedProgram !== selectedProgram) {
        console.log("Program switching detected: " + savedProgram + " -> " + selectedProgram);

        return;
    }


    // Set income considered radio button
    if (programDetails.incomeConsidered) {
        triggerElement.closest('.det').find('input.incomeCheck[value="' + programDetails.incomeConsidered + '"]')
            .prop('checked', true)
            .trigger('change');
        triggerElement.closest('.det').closest('.tab-pane').find('.programCode').val(selectedProgram);// since selected can be empty once above radio button event is triggered.
        var programCodeDropdown = triggerElement.closest('.det').closest('.tab-pane').find('.programCode');
        if (programCodeDropdown.find('option[value="NONE"]').length > 0) {
            programCodeDropdown.find('option[value="NONE"]').remove();
            if (selectedProgram === "NONE") {
                programCodeDropdown.val("");
                console.log("herererere" + programDetails.propertyOwner);
                if (programDetails.propertyOwner != null) {
                    triggerElement.closest('.det').closest('.tab-pane').find('.no-income-section input[name="propertyOwner"][value="' + programDetails.propertyOwner + '"]').prop('checked', true);
                }
            }
        }
    }
    switch (selectedProgram) {
        case 'INCOME':
            resetProgramFields(triggerElement);
            updateIncomeDetails(programDetails, triggerElement);
            break;
        case 'SURROGATE':
            resetProgramFields(triggerElement);
            triggerElement.closest('.det').find('.surrogate-section').removeClass('hidden');
            if (programDetails) {
                updateSurrogateDetails(programDetails, triggerElement);
            }
            break;
        case 'LIQUIDINCOME':
            resetProgramFields(triggerElement);
            updateLIQUIDINCOME(programDetails, triggerElement);
            break;
    }
    //triggerElement.closest('.det').find('.programCode').val(selectedProgram).trigger('change');
    //$('#programCode').val(selectedProgram);
}

function resetProgramFields(triggerElement) {

    var form = triggerElement.closest('.det');
    var selectedProgram = form.find('.programCode').val();
    console.log("Resetting program fields ============" + selectedProgram);
    form.find('input[name="propertyOwner"]').prop('checked', false);
    // Reset program-specific fields
    switch (selectedProgram) {
        case 'INCOME':
            form.find('.doc-type-selection').prop('checked', false);
            form.find('.salaried-section, .pensioner-section, .sepsenp-section, .agriculturist-section').hide();
            form.find('.itr-section, .form16-section, .payslip-section').hide();
            form.find('.non-resident-income-section').hide();
            form.find('.resident-income-sections').hide();
            form.find('.resident-status-text').text('');
            form.find('.resident-check-section .alert')
                .removeClass('alert-warning')
                .addClass('alert-info');

            form.find('.payslip-table-body').empty();

            // Clear all monthly gross income fields for all employment types
            form.find('.itr-monthly-gross').val('');
            form.find('input[name="itrMonthlyGross"]').val('');
            form.find('input[name="pensionerMonthlyGross"]').val('');
            form.find('input[name="sepSenpMonthlyGross"]').val('');
            form.find('input[name="agriculturistMonthlyGross"]').val('');

            form.find('.total-income, .avg-monthly-income').val('');
            form.find('input[name="financialDiscrepancy"], input[name="pensionerFinancialDiscrepancy"]').prop('checked', false);

            // Clear and reset bank statement input fields
            form.find('.bank-start-date, .bank-end-date').val('');
            form.find('.bank-name').val('');

            // Show all input sections that might have been hidden
            form.find('.bank-statement-section .row').show();
            form.find('.bank-statement-section .alert-card').show();
            form.find('.bank-statement-section .btn-primary').show();
            // Clear and hide response sections
            form.find('.bsaResponse').empty();
            form.find('.bsa-status').empty();
            form.find('[data-response-type="BSA-ITR"]').empty();

            // Clear ITR response but keep container visible
            form.find('.itrResponse').empty();
            form.find('.itrMonthlyGrossDiv').hide();
            form.find('.itr-monthly-gross-div').hide();
            // Enable input fields that might have been disabled
            form.find('.bank-start-date, .bank-end-date, .bank-name').prop('disabled', false);
            form.find('.add-backs-obligations').val('');
            form.find('.final-ami').val('');
            break;

        case 'SURROGATE':
            // Reset Surrogate program fields
            resetSurrogate(triggerElement);
            break;
        case 'LIQUIDINCOME':
            // Reset Liquid Income program fields
            form.find('.liquid-monthly-income').val('');
            // Reset file inputs - browser security prevents directly clearing file inputs via JS
            // Instead, we'll clone and replace them
            form.find('.liquid-ca-cert-upd, .liquid-business-proof-upd, .liquid-pd-doc-upd').each(function () {
                const fileInput = $(this);
                const parent = fileInput.parent();
                const newInput = fileInput.clone(true).val('');
                fileInput.remove();
                parent.append(newInput);
            });
            form.find('.file-uploaded-indicator').remove();
            form.find('input[name="liquidUploadStatus"]').val('');
            break;
    }

    // After resetting, show a success message
    // if (selectedProgram) {
    //     Swal.fire({
    //         position: 'top-end',
    //         icon: 'success',
    //         title: 'Program changed successfully',
    //         text: 'Form has been reset for the new program',
    //         showConfirmButton: false,
    //         timer: 1500
    //     });
    // }
}

function updateIncomeDetails(programDetails, triggerElement) {
    console.log("Updating Income program details");
    var currentResidentType = triggerElement.closest('.det')
        .closest('.tab-pane')
        .find('.generaldetails')
        .find('.residentialStatus:checked').val();
    var form = triggerElement.closest('.det');
    if (currentResidentType === 'N') {
        form.find('.non-resident-income-section').show();
        form.find('.resident-income-sections').hide();
        form.find('.salaried-section').hide();
        form.find('.pensioner-section').hide();
        form.find('.sepsenp-section').hide();
        form.find('.agriculturist-section').hide();
        form.find('.resident-status-text').text('Non-Resident');
        form.find('.resident-check-section .alert')
            .removeClass('alert-info')
            .addClass('alert-warning');
    } else {
        form.find('.non-resident-income-section').hide();
        form.find('.resident-income-sections').show();


        if (programDetails.ITRBSAData) {
            if (programDetails.ITRBSAData.startDate) {
                form.find('.bank-start-date').val(programDetails.ITRBSAData.startDate).prop('disabled', true);
            }

            if (programDetails.ITRBSAData.endDate) {
                form.find('.bank-end-date').val(programDetails.ITRBSAData.endDate).prop('disabled', true);
            }

            if (programDetails.ITRBSAData.bank) {
                form.find('.bank-name').val(programDetails.ITRBSAData.bank).prop('disabled', true);
            }
        }
        // Set employment type
        var employmentType = "";
        if (programDetails.employmentType) {
            employmentType = programDetails.employmentType;
        } else {
            employmentType = employment_type;
        }
        form.find('input[name="empl_type"]').val(employmentType);

        // Show employment type specific section
        form.find('.salaried-section, .pensioner-section, .sepsenp-section, .agriculturist-section').hide();

        if (employmentType === 'SALARIED') {
            form.find('.salaried-section').show();
        } else if (employmentType === 'PENSIONER') {
            form.find('.pensioner-section').show();
        } else if (employmentType === 'SEP' || employmentType === 'SENP') {
            form.find('.sepsenp-section').show();
        } else if (employmentType === 'AGRICULTURIST') {
            form.find('.agriculturist-section').show();
        }

        // Set document type and show relevant section
        if (programDetails.docType) {
            form.find('input[name="docTypeSelection"][value="' + programDetails.docType + '"]').prop('checked', true);

            form.find('.itr-section, .form16-section, .payslip-section').hide();

            if (programDetails.docType === 'ITR') {
                form.find('.itr-section').show();
                // Handle ITR-specific data if needed
                if (programDetails.itrDetails) {
                    processITRDataForDisplay(programDetails.itrDetails, form);
                }
            } else if (programDetails.docType === 'FORM16') {
                form.find('.form16-section').show();
                form.find('.form16-upd').closest('.compact-form-group').find('.text-success').remove();
                form.find('.form16-upd').closest('.compact-form-group').append('<span class="text-success ms-2">✓ File uploaded</span>');
                if (programDetails.monthlyGrossIncome != null) {
                    form.find('.form16-monthly-income').val(programDetails.monthlyGrossIncome);
                }
                // Handle Form16-specific data if needed
            } else if (programDetails.docType === 'PAYSLIP') {
                form.find('.payslip-section').show();

                // Clear existing payslip rows
                form.find('.payslip-table-body').empty();

                // Add payslip rows from data
                if (programDetails.payslipDetails && programDetails.payslipDetails.length > 0) {
                    programDetails.payslipDetails.forEach(function (payslip) {
                        addPayslipRowWithData(form, payslip);
                    });

                    // Calculate total income
                    calculateTotalIncome(form.find('.payslip-table-body'));
                } else {
                    // Add an empty row if no data
                    addPayslipRow(form);
                }
            }
        } else {
            if (programDetails.itrDetails) {
                processITRDataForDisplay(programDetails.itrDetails, form);
            } else {
                checkForITREntries(triggerElement);
            }
        }

        // Set monthly gross income if available
        if (programDetails.monthlyGrossIncome != null) {
            //if (programDetails.docType === 'ITR') {
            if (employmentType === 'SALARIED') {
                form.find('input[name="itrMonthlyGross"]').val(programDetails.monthlyGrossIncome);
            } else if (employmentType === 'PENSIONER') {
                form.find('input[name="pensionerMonthlyGross"]').val(programDetails.monthlyGrossIncome);
            } else if (employmentType === 'AGRICULTURIST') {
                form.find('input[name="agriculturistMonthlyGross"]').val(programDetails.monthlyGrossIncome);
            } else if (employmentType === 'SEP' || employmentType === 'SENP') {
                form.find('input[name="sepSenpMonthlyGross"]').val(programDetails.monthlyGrossIncome);
            } else {
                form.find('.itr-monthly-gross').val(programDetails.monthlyGrossIncome);
            }
            // }
        }
        console.log("-------------------------" + programDetails.addBacksObligations);
        if (programDetails.addBacksObligations != null) {
            form.find('.add-backs-obligations').val(programDetails.addBacksObligations);
            calculateFinalAMI(form.find('.add-backs-obligations'));
        } else if (programDetails.finalEligibilityAmi != null && programDetails.incomeMonthlyGross != null) {
            // If we have finalEligibilityAmi but no addBacksObligations, calculate the difference
            var monthlyGross = parseFloat(programDetails.incomeMonthlyGross);
            var finalAmi = parseFloat(programDetails.finalEligibilityAmi);
            var addBacks = finalAmi - monthlyGross;

            form.find('.add-backs-obligations').val(addBacks.toFixed(2));
            form.find('.final-ami').val(finalAmi.toFixed(2));
        }

        if (programDetails.ITRBSAData && programDetails.ITRBSAProcessed === 'Y') {
            populateITRBSASummary(programDetails.ITRBSAData, form);
        }
    }

}

function processITRDataForDisplay(itrDetails, form) {
    console.log("Processing ITR data for display", itrDetails);

    // The ITR data is stored as a string in itrDetails.itrResponse
    // We need to parse it first
    let itrData;
    try {
        if (itrDetails.itrResponse) {
            itrData = JSON.parse(itrDetails.itrResponse);
        } else {
            console.warn("No ITR response data found");
            return;
        }
    } catch (e) {
        console.error("Error parsing ITR response:", e);
        return;
    }

    console.log("Parsed ITR data:", itrData);

    // Extract the actual data from the parsed JSON
    let itrDataArray = [];
    let summaryData = {};
    let isAmber = false;
    let amberReason = "";

    // Handle the various possible response structures
    if (itrData.itrData) {
        // This structure matches your sample data
        itrDataArray = itrData.itrData.itrData || [];
        summaryData = itrData.itrData.summary || {};
        isAmber = itrData.itrData.isAmber || false;
        amberReason = itrData.itrData.amberReason || "";
    }

    // Get monthly income from summary and set it to the input field
    let monthlyIncome = summaryData.monthlyIncome || "";
    form.find('.itr-monthly-gross').val(monthlyIncome);

    // Build HTML content to display ITR data
    let tableHtml = '<div class="itr-summary card mb-3">';
    tableHtml += '<div class="card-header bg-light"><strong>ITR Summary</strong></div>';
    tableHtml += '<div class="card-body">';

    // Display summary data
    if (Object.keys(summaryData).length > 0) {
        tableHtml += '<div class="row">';
        tableHtml += '<div class="col-md-6"><strong>Monthly Income:</strong> ₹' + monthlyIncome + '</div>';
        tableHtml += '<div class="col-md-6"><strong>Total Annual Income:</strong> ₹' + (summaryData.totalAnnualIncome || "N/A") + '</div>';
        tableHtml += '</div>';

        tableHtml += '<div class="row mt-2">';
        tableHtml += '<div class="col-md-6"><strong>PAN:</strong> ' + (summaryData.primaryPAN || "N/A") + '</div>';
        tableHtml += '<div class="col-md-6"><strong>Name:</strong> ' + (summaryData.primaryName || "N/A") + '</div>';
        tableHtml += '</div>';

        tableHtml += '<div class="row mt-2">';
        tableHtml += '<div class="col-md-6"><strong>Fiscal Years:</strong> ' + (summaryData.yearsConsidered || "N/A") + '</div>';
        tableHtml += '<div class="col-md-6"><strong>Calculation Type:</strong> ' + (summaryData.incomeCalculationType || "N/A") + '</div>';
        tableHtml += '</div>';
    }

    // Display amber status if present
    if (isAmber) {
        tableHtml += '<div class="row mt-3">';
        tableHtml += '<div class="col-12 alert alert-warning">';
        tableHtml += '<i class="fas fa-exclamation-triangle mr-2"></i> <strong>Note:</strong> ' + amberReason;
        tableHtml += '</div>';
        tableHtml += '</div>';
    }

    tableHtml += '</div>'; // End of card-body
    tableHtml += '</div>'; // End of summary card

    // Create the ITR details table
    tableHtml += '<table class="table table-border-dashed">';
    tableHtml += '<thead><tr><th>FY</th><th>Form No</th><th>Total Income</th><th>Filing Date</th><th>Acknowledgement</th></tr></thead>';
    tableHtml += '<tbody>';

    if (itrDataArray && itrDataArray.length > 0) {
        itrDataArray.forEach(function (itr) {
            tableHtml += '<tr>';
            tableHtml += '<td>' + (itr.fy || "N/A") + '</td>';
            tableHtml += '<td>' + (itr.formNo || "N/A") + '</td>';

            // Get total income from the correct structure
            let totalIncome = "N/A";
            if (itr.totalIncome) {
                totalIncome = itr.totalIncome;
            } else if (itr.incomeDetails && itr.incomeDetails.totalIncome) {
                totalIncome = itr.incomeDetails.totalIncome;
            }
            tableHtml += '<td>₹' + totalIncome + '</td>';

            tableHtml += '<td>' + (itr.dateOfFiling || "N/A") + '</td>';
            tableHtml += '<td>' + (itr.acknowledgementNo || "N/A") + '</td>';
            tableHtml += '</tr>';
        });
    } else {
        tableHtml += '<tr><td colspan="5" class="text-center">No ITR data available</td></tr>';
    }

    tableHtml += '</tbody>';
    tableHtml += '</table>';

    // Update the response container
    form.find('.itrResponse').html(tableHtml);
    form.find('.itrMonthlyGrossDiv').show();
    form.find('.itr-monthly-gross-div').show();

    // Enable the save button
    form.find('.save-button-program').prop("disabled", false);
}


function addPayslipRowWithData(triggerElement, payslipData) {
    var tableBody = triggerElement.find('.salaried-section').find('.payslip-table-body');

    // Get current date info for reference
    var currentDate = new Date();
    var currentYear = currentDate.getFullYear();
    var prevYear = currentYear - 1;
    var nextYear = currentYear + 1;

    var fileUploaded = payslipData.fileUploaded || (payslipData.fileRef && payslipData.fileRef.length > 0);

    // Create a new row with the payslip data
    var rowHtml = `
        <tr data-payslip-id="${payslipData.payslipId || ''}">
            <td>
                <select class="form-control payslip-month">
                    <option value="">Select Month</option>
                    <option value="1" ${payslipData.payslipMonth === "1" ? 'selected' : ''}>January</option>
                    <option value="2" ${payslipData.payslipMonth === "2" ? 'selected' : ''}>February</option>
                    <option value="3" ${payslipData.payslipMonth === "3" ? 'selected' : ''}>March</option>
                    <option value="4" ${payslipData.payslipMonth === "4" ? 'selected' : ''}>April</option>
                    <option value="5" ${payslipData.payslipMonth === "5" ? 'selected' : ''}>May</option>
                    <option value="6" ${payslipData.payslipMonth === "6" ? 'selected' : ''}>June</option>
                    <option value="7" ${payslipData.payslipMonth === "7" ? 'selected' : ''}>July</option>
                    <option value="8" ${payslipData.payslipMonth === "8" ? 'selected' : ''}>August</option>
                    <option value="9" ${payslipData.payslipMonth === "9" ? 'selected' : ''}>September</option>
                    <option value="10" ${payslipData.payslipMonth === "10" ? 'selected' : ''}>October</option>
                    <option value="11" ${payslipData.payslipMonth === "11" ? 'selected' : ''}>November</option>
                    <option value="12" ${payslipData.payslipMonth === "12" ? 'selected' : ''}>December</option>
                </select>
            </td>
            <td>
                <select class="form-control payslip-year">
                    <option value="">Select Year</option>
                    <option value="${prevYear}" ${payslipData.payslipYear == prevYear ? 'selected' : ''}>${prevYear}</option>
                    <option value="${currentYear}" ${payslipData.payslipYear == currentYear ? 'selected' : ''}>${currentYear}</option>
                    <option value="${nextYear}" ${payslipData.payslipYear == nextYear ? 'selected' : ''}>${nextYear}</option>
                </select>
            </td>
            <td>
                <div class="file-upload-wrapper">
                    <input type="file" class="form-control payslip-file base64file" accept=".pdf,.jpg,.jpeg,.png" data-max-size="2097152">
                    ${fileUploaded ? '<span class="text-success ms-2 file-uploaded-indicator">✓ File uploaded</span>' : ''}
                    <input type="hidden" class="payslip-file-status" value="${fileUploaded ? 'uploaded' : ''}">
                </div>
            </td>
            <td>
                <input type="number" class="form-control payslip-amount" placeholder="Income Amount" value="${payslipData.payslipAmount || ''}">
            </td>
            <td>
                <button type="button" class="btn btn-danger btn-sm delete-payslip-row">
                    <i class="ph-trash"></i>
                </button>
            </td>
        </tr>
    `;

    tableBody.append(rowHtml);

    // Initialize select2 for the new dropdown if it's being used in your project
    if ($.fn.select2) {
        tableBody.find('.payslip-month').last().select2({
            templateResult: formatState,
            templateSelection: formatState
        });

        tableBody.find('.payslip-year').last().select2({
            templateResult: formatState,
            templateSelection: formatState
        });
    }
}


/************************************************************
 * 3. Program saving to DB
 ************************************************************/
function incomesave(form, key, callback) {
    var det = $(this).closest('.det');
    var applicantId = det.find('input[name="applicantId"]').val();
    var wiNum = det.find('input[name="wiNum"]').val();
    var programCode = det.find('.programCode').val();
    var incomeConsidered = det.find('input[name="incomeCheck"]:checked').val();

    // Get employment type and resident type
    var employment_type = det.closest('.tab-pane').find('.employmentdetails').find('.selemptype').val();
    var resident_type = det.closest('.tab-pane').find('.generaldetails').find('.residentialStatus:checked').val();

    console.log("Saving INCOME program data for NRI applicant");
    console.log("Employment Type: " + employment_type);
    console.log("Resident Type: " + resident_type);
    console.log("Program Code: " + programCode);
    var incomeCheck = form.find('input[name="incomeCheck"]:checked').val();
    if (!incomeCheck) {
        alertmsg("Please select whether income should be considered.");
        callback(false);
        return;
    }

    // If income is not considered, save immediately
    if (incomeCheck === 'N') {
        // Set program as "NONE" for consistency
        form.find('.programCode').val('NONE');
        // return;
    }

    // If income is considered, validate program selection
    var programCode = form.find('.programCode').val();
    if (incomeCheck === "N") {
        programCode = "NONE";
    }
    if (!programCode && incomeCheck !== 'N') {
        alertmsg("Please select a program.");
        form.find('.programCode').addClass('is-invalid');
        callback(false);
        return;
    }

    // Validate based on selected program
    var isValid = false;
    switch (programCode) {
        case 'INCOME':
            isValid = validateIncomeProgram(form);
            break;
        case 'SURROGATE':
            isValid = validateSurrogateProgram(form);
            break;
        case 'LOANFD':
            isValid = validateLoanFDProgram(form);
            break;
        case 'NONFOIR':
            isValid = validateNonFoirProgram(form);
            break;
        case 'IMPUTED':
            isValid = validateImputedProgram(form);
            break;
        case 'NONE':
            // No program to validate
            isValid = true;
            break;
        default:
            alertmsg("Unknown program selected. Please refresh and try again.");
            isValid = false;
    }

    if (!isValid) {
        console.log("Program validation failed for program: " + programCode);
        callback(false);
        return;
    }
    console.log("All validations passed for program: " + programCode);
    var allFiles = [];
    var promises = [];
    var fileType = null;
    var fileTypes = [];
    // Use program-specific functions to process files
    if (programCode === 'INCOME') {
        processIncomeFiles(form, allFiles, promises);
    } else if (programCode === 'SURROGATE') {
        processSurrogateFiles(form, allFiles, promises);
    }
    var programCodeSelected = form.find('select[name="programCode"]').val();
    var itrFlgSelected = form.find('input[name="itravailable"]:checked').val();
    console.log("programCodeSelected - " + programCodeSelected);
    console.log("itrFlgSelected - " + itrFlgSelected);

    Promise.all(promises).then(() => {
        console.log('All files processed:', allFiles);
        var applicantId = form.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val();
        var wiNum = $('#winum').val();
        var slno = $('#slno').val();
        var formDataArray = form.serializeArray();
        if (programCodeSelected === "INCOME" && form.find('input[name="docTypeSelection"]:checked').val() === 'PAYSLIP') {
            form.find('.payslip-table-body tr').each(function (index) {
                var row = $(this);
                var month = row.find('.payslip-month').val();
                var year = row.find('.payslip-year').val();
                var amount = row.find('.payslip-amount').val();
                var payslipId = row.data('payslip-id') || '';

                // Only add complete rows
                if (month && year && amount) {
                    formDataArray.push({name: "payslip-id" + index, value: payslipId});
                    formDataArray.push({name: "payslip-month" + index, value: month});
                    formDataArray.push({name: "payslip-year" + index, value: year});
                    formDataArray.push({name: "payslip-amount" + index, value: amount});
                    formDataArray.push({name: "payslip-uploaded" + index, value: row.find('.payslip-file-status').val() === 'uploaded' ? 'Y' : 'N'});
                }
            });
        }
        var addBacksField = form.find('.add-backs-obligations:visible');
        if (addBacksField.length > 0) {
            formDataArray.push({name: "addBacksObligationsValue", value: addBacksField.val() || "0"});
            formDataArray.push({name: "finalEligibilityAMI", value: form.find('.final-ami:visible').val() || "0"});
        }

        var data = formDataArray.map(function (item) {
            return {key: item.name, value: item.value}; // Transform to key-value pair objects
        });
        var jsonBody = {
            id: key,
            slno: slno,
            winum: wiNum,
            appid: applicantId,
            data: data,
            reqtype: form.attr('data-code'),
            DOC_ARRAY: allFiles
        };

        $.ajax({
            url: 'api/save-data',
            type: 'POST',
            async: false,
            contentType: 'application/json', // Set content type to JSON
            data: JSON.stringify(jsonBody), // Convert data object to JSON string
            success: function (response) {
                if (response.status === 'S') {
                    form.find('.appid').val(response.appid);
                    savedProgram = form.find('select[name="programCode"]').val();
                    savedmonthlyorabb = form.find('input[name="monthlyorabb"]:checked').val();
                    let payslipData = {};
                    if (response.message) {
                        try {
                            payslipData = JSON.parse(response.message);
                        } catch (e) {
                            console.error("Failed to parse response message as JSON", e);
                        }
                    }
                    // Mark all payslip files as uploaded
                    if (programCode === "INCOME" && form.find('input[name="docTypeSelection"]:checked').val() === 'PAYSLIP') {
                        form.find('.payslip-table-body tr').each(function (index) {
                            var row = $(this);

                            // Update payslip IDs if available in the response
                            if (payslipData.payslipIds && payslipData.payslipIds[index]) {
                                row.attr('data-payslip-id', payslipData.payslipIds[index]);
                            }

                            // Add file uploaded indicator if file was uploaded
                            if (!row.find('.file-uploaded-indicator').length) {
                                var fileInput = row.find('.payslip-file');
                                if (fileInput[0].files.length > 0) {
                                    row.find('.file-upload-wrapper').append('<span class="text-success ms-2 file-uploaded-indicator">✓ File uploaded</span>');
                                    row.find('.payslip-file-status').val('uploaded');
                                }
                            }
                        });
                    }
                    notyalt('Income Details Saved !!');
                    fetchProgramDetails(form);
                    disableFormInputs(form);
                    hideLoader();
                    //silentRefresh(form);
                    $('#property-accordion').find('.fp-accordion-header').trigger('click');
                    callback(true);
                } else {
                    alertmsg(response.msg);
                    callback(false);
                }
            },
            error: function (xhr, status, error) {
                alertmsg('Error during Saving data');
                callback(false);
            }
        });
    }).catch(error => {
        console.error('Error processing files:', error);
        callback(false);
    });
}

// Function to process INCOME program files
function processIncomeFiles(form, allFiles, promises) {
    var docType = form.find('input[name="docTypeSelection"]:checked').val();
    var employmentType = form.find('input[name="empl_type"]').val();

    // Process the files based on document type and employment type
    form.find('.base64file').each(function () {
        var input = $(this);
        var files = input[0].files;

        // Skip if no files selected AND there's an uploaded status
        if ((!files || files.length === 0) && input.closest('.file-upload-wrapper').find('.file-uploaded-indicator').length > 0) {
            return; // File already uploaded
        }

        // Skip if no files selected
        if (!files || files.length === 0) {
            return;
        }

        // Check if this file is relevant for INCOME program
        var isRelevant = false;

        // For SALARIED
        if (employmentType === 'SALARIED') {
            if (docType === 'FORM16' && input.hasClass('form16-upd')) {
                isRelevant = true;
            } else if (docType === 'PAYSLIP' && input.hasClass('payslip-file')) {
                isRelevant = true;
            } else if (docType === 'ITR' && input.hasClass('itr-upload')) {
                isRelevant = true;
            }
        }
        // For PENSIONER
        else if (employmentType === 'PENSIONER') {
            if (input.hasClass('pension-cert-upd') || input.hasClass('itr-upload')) {
                isRelevant = true;
            }
        }
        // For other employment types
        else if ((employmentType === 'SEP' || employmentType === 'SENP' || employmentType === 'AGRICULTURIST') &&
            input.hasClass('itr-upload')) {
            isRelevant = true;
        }

        // Skip if not relevant
        if (!isRelevant) {
            return;
        }

        var maxSize = input.data('max-size') || 2097152;

        Array.from(files).forEach(file => {
            if (file.size > maxSize) {
                alertmsg('File ' + file.name + " is too large. Maximum size is 2MB");
                promises.push(Promise.reject('File too large'));
                return;
            }

            var promise = new Promise((resolve, reject) => {
                var reader = new FileReader();
                reader.onload = function (e) {
                    var fileType = "";

                    // Determine file type based on input class
                    if (input.hasClass('form16-upd')) {
                        fileType = 'FORM16';
                    } else if (input.hasClass('payslip-file')) {
                        var row = input.closest('tr');
                        var month = row.find('.payslip-month').val();
                        var year = row.find('.payslip-year').val();
                        var payslipId = row.data('payslip-id') || '';

                        if (month && year) {
                            var monthName = getMonthName(month);
                            fileType = 'PAYSLIP_' + monthName + '_' + year + (payslipId ? '_' + payslipId : '');
                        } else {
                            fileType = 'PAYSLIP_UNKNOWN';
                        }
                    } else if (input.hasClass('itr-upload')) {
                        fileType = 'ITR_DOCUMENT';
                    } else if (input.hasClass('pension-cert-upd')) {
                        fileType = 'PENSION_CERTIFICATE';
                    }

                    console.log("Processing INCOME file:", file.name, "Type:", fileType);
                    var fileData = {
                        DOC_EXT: file.name.split('.').pop(),
                        DOC_NAME: fileType,
                        DOC_BASE64: e.target.result.split(',')[1],
                        reqtype: form.attr('data-code')
                    };
                    allFiles.push(fileData);
                    resolve();
                };

                reader.onerror = function () {
                    reject('Failed to read file: ' + file.name);
                };
                reader.readAsDataURL(file);
            });
            promises.push(promise);
        });
    });
}


// Function to process LIQUIDINCOME program files
function processLiquidIncomeFiles(form, allFiles, promises) {
    form.find('.base64file').each(function () {
        var input = $(this);
        var files = input[0].files;
        if (!files || files.length === 0) {
            return; // Skip if no files selected
        }

// Check if this file is relevant for LIQUIDINCOME program
        if (!input.hasClass('liquid-ca-cert-upd') &&
            !input.hasClass('liquid-business-proof-upd') &&
            !input.hasClass('liquid-pd-doc-upd')) {
            return; // Skip non-liquid program files
        }

        var maxSize = input.data('max-size') || 2097152;

        Array.from(files).forEach(file => {
            if (file.size > maxSize) {
                alertmsg('File ' + file.name + " is too large. Maximum size is 2MB");
                promises.push(Promise.reject('File too large'));
                return;
            }

            var promise = new Promise((resolve, reject) => {
                var reader = new FileReader();
                reader.onload = function (e) {
                    var fileType = "";

                    if (input.hasClass('liquid-ca-cert-upd')) {
                        fileType = 'CA_Certificate';
                    } else if (input.hasClass('liquid-business-proof-upd')) {
                        fileType = 'Business_Proof';
                    } else if (input.hasClass('liquid-pd-doc-upd')) {
                        fileType = 'Personal_Discussion_Document';
                    }

                    console.log("Processing LIQUIDINCOME file:", file.name, "Type:", fileType);
                    var fileData = {
                        DOC_EXT: file.name.split('.').pop(),
                        DOC_NAME: fileType,
                        DOC_BASE64: e.target.result.split(',')[1],
                        reqtype: form.attr('data-code')
                    };
                    allFiles.push(fileData);
                    resolve();
                };

                reader.onerror = function () {
                    reject('Failed to read file: ' + file.name);
                };
                reader.readAsDataURL(file);
            });
            promises.push(promise);
        });
    });
}



function processSurrogateFiles(form, allFiles, promises) {
// Implement SURROGATE-specific file processing
}


function updateLIQUIDINCOME(programDetails, detElement) {
    console.log("UPDATE THE LIQUID INCOME PROGRAM");

    var form = detElement.closest('.det');

    if (programDetails.propertyOwner) {
        detElement.closest('.det').find('input[name="propertyOwner"][value="' + programDetails.propertyOwner + '"]').prop('checked', true);
    }
    if (programDetails.liquidMonthlyIncome) {
        detElement.closest('.det').find('.liquid-monthly-income').val(programDetails.liquidMonthlyIncome);
    }
    if (programDetails.liquidUploadStatus) {
        detElement.closest('.det').find('.liquid-ca-cert-upd').closest('.compact-form-group')
            .append('<span class="text-success ms-2">✓ File uploaded</span>');
    }

    if (programDetails.liquidUploadStatus) {
        detElement.closest('.det').find('.liquid-business-proof-upd').closest('.compact-form-group')
            .append('<span class="text-success ms-2">✓ File uploaded</span>');
    }

    if (programDetails.liquidUploadStatus) {
        //detElement.closest('.det').find('.liquid-pd-doc-upd').closest('.compact-form-group')
        //  .append('<span class="text-success ms-2">✓ File uploaded</span>');
        addFileUploadIndicators(form, true);
    }
}

function initializeProgramDetails() {
    $('.program-itr form').each(function () {
        var form = $(this);
        var incomeConsidered = form.find('input[name="hidincomeConsidered"]').val();
        var programCode = form.find('input[name="hidProgramCode"]').val();
        var liquidMonthlyIncome = form.find('input[name="hidliquidMonthlyIncome"]').val();
        var liquidUploadStatus = form.find('input[name="hidliquidUploadStatus"]').val();
        console.log("initializeProgramDetails=====");
        fetchProgramDetails(form);

        // console.log("Initializing form with values:", {
        //     incomeConsidered: incomeConsidered,
        //     programCode: programCode,
        //     liquidMonthlyIncome: liquidMonthlyIncome,
        //     liquidUploadStatus: liquidUploadStatus
        // });
        // if (incomeConsidered) {
        //     form.find('input.incomeCheck[value="' + incomeConsidered + '"]')
        //         .prop('checked', true)
        //         .trigger('change');
        //     //empl_type
        // }
        // Set program dropdown and trigger its change event to show the appropriate section

    });
}

function addFileUploadIndicators(form, isUploaded) {
    // Only add indicators if they don't already exist
    if (isUploaded) {
        // Add hidden field to indicate files are already uploaded
        if (form.find('input[name="liquidUploadStatus"]').length === 0) {
            form.append('<input type="hidden" name="liquidUploadStatus" value="uploaded">');
        } else {
            form.find('input[name="liquidUploadStatus"]').val("uploaded");
        }

        // Add visual indicators for each file input if they don't already exist
        if (form.find('.liquid-ca-cert-upd').closest('.compact-form-group').find('.file-uploaded-indicator').length === 0) {
            form.find('.liquid-ca-cert-upd').closest('.compact-form-group')
                .append('<span class="text-success ms-2 file-uploaded-indicator">✓ File uploaded</span>');
        }

        if (form.find('.liquid-business-proof-upd').closest('.compact-form-group').find('.file-uploaded-indicator').length === 0) {
            form.find('.liquid-business-proof-upd').closest('.compact-form-group')
                .append('<span class="text-success ms-2 file-uploaded-indicator">✓ File uploaded</span>');
        }

        if (form.find('.liquid-pd-doc-upd').closest('.compact-form-group').find('.file-uploaded-indicator').length === 0) {
            form.find('.liquid-pd-doc-upd').closest('.compact-form-group')
                .append('<span class="text-success ms-2 file-uploaded-indicator">✓ File uploaded</span>');
        }
    }
}



function initializeProcessedBSASection(form, statementType, programDetails) {
    var section, inputSection, summarySection, alertSection;
    var startDate, endDate, bank, accountNo, accountType, avgBankBalance;
    var emireportData;
    var sectionTitle, sectionSuccessDesc;
    var bankNameDescr;


    // Select the appropriate sections based on statement type
    switch (statementType) {
        case 'SURROGATE-1':
            section = form.find('[data-response-type="SURROGATE-1"]').parent().next();
            break;
    }

    if (section && inputSection && summarySection) {
        // Hide the input fields and proceed button
        inputSection.hide();
        alertSection.hide();
        section.find('.btn-primary').hide();

        // Add a summary card and reset button
        summarySection.html(`
            <div class="card mb-3">
                <div class="card-header bg-success text-white">
                    <div class="d-flex justify-content-between align-items-center">
                        <h6 class="mb-0">${sectionTitle}</h6>
                        <button type="button" class="btn btn-sm btn-light reset-bsa-btn" data-statement-type="${statementType}">
                            Reset <i class="ph-arrow-counter-clockwise ms-1"></i>
                        </button>
                    </div>
                </div>
                <div class="card-body">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle mr-2"></i> ${sectionSuccessDesc}
                    </div>
                    <hr class="my-2">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-2">
                                <strong>Period:</strong> ${startDate} to ${endDate}
                            </div>
                            <div>
                                <strong>Bank:</strong> ${bankNameDescr}
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-2">
                                <strong>Account:</strong> ${accountNo}
                            </div>
                            <div>
                                <strong>ABB:</strong> ${avgBankBalance}
                            </div>
                        </div>
                    </div>
                    <div class="mt-2 small text-muted">
                        Click "Reset" to clear this statement and upload a new one.
                    </div>
                </div>
            </div>
        `);
    }
}

function getProgramDisplayName(programCode) {
    const programNames = {
        'INCOME': 'Income Program',
        'SURROGATE': 'Surrogate Program',
        'NONFOIR': 'Non - Foir Program',
        'IMPUTED': 'Imputed Income Program',
        'LOANFD': 'Loan FD Program',
        'NONE': 'No Program'
    };

    return programNames[programCode] || programCode;
}

function proceedWithProgramChange(detElement, selected) {
        _performProgramChange(detElement, selected);
}

// Extracted the program change logic to its own function
function _performProgramChange(detElement, selected) {
    console.log("_performProgramChange is performed ----" + selected);
    // Fetch applicant details from other form sections
    const kycdetailsElement = detElement.closest('.det').parent().closest('.tab-pane').find('.kycdetails');
    const basicdetailsElement = detElement.closest('.det').parent().closest('.tab-pane').find('.basicdetails');
    const generaldetailsElement = detElement.closest('.det').parent().closest('.tab-pane').find('.generaldetails');
    employment_type = detElement.closest('.det').closest('.tab-pane').find('.employmentdetails').find('.selemptype').val();

    var entryPan = kycdetailsElement.find('.pan').val();
    var entryPanDob = kycdetailsElement.find('.pandob').val();
    var residentialStatus = generaldetailsElement.find('.residentialStatus:checked').val();
    const basic_mob = basicdetailsElement.find('.basic_mob').val();
    const basic_mobcode = basicdetailsElement.find('.basic_mobcode').val();
    let cust_mob = basic_mobcode + basic_mob;

    // Set hidden fields
    detElement.closest('.det').find('input[name^="residentialStatus"]').val(residentialStatus);
    detElement.closest('.det').find('input[name^="incomePAN"]').val(entryPan);
    detElement.closest('.det').find('input[name^="incomeDOB"]').val(entryPanDob);
    detElement.closest('.det').find('input[name^="incomeMOB"]').val(cust_mob);
    detElement.closest('.det').find('input[name^="empl_type"]').val(employment_type);

    // Reset all program data before showing the new program section
    resetProgramFields(detElement);

    // If program code is selected, fetch program details
    if (selected != null && selected.length > 0) {
        fetchProgramDetails(detElement);
    }

    // Show/hide sections based on program selection
    detElement.closest('.det').find('.incomediv').hide();
    detElement.closest('.det').find('.surrogatediv').hide();
    detElement.closest('.det').find('.nonfoirdiv').hide();
    detElement.closest('.det').find('.imputeddiv').hide();
    detElement.closest('.det').find('.fddiv').hide();

    if (selected === 'INCOME') {
        if (resident_type === 'N') {
            detElement.closest('.det').find('.incomediv').show();
            detElement.find('.non-resident-income-section').show();
            detElement.find('.resident-income-sections').hide();
            detElement.find('.salaried-section').hide();
            detElement.find('.pensioner-section').hide();
            detElement.find('.sepsenp-section').hide();
            detElement.find('.agriculturist-section').hide();
            detElement.find('.resident-status-text').text('Non-Resident');
            detElement.find('.resident-check-section .alert')
                .removeClass('alert-info')
                .addClass('alert-warning');
        } else {
            detElement.find('.non-resident-income-section').hide();
            detElement.find('.resident-income-sections').show();
            detElement.closest('.det').find('.incomediv').show();
            detElement.closest('.det').find('.salaried-section').hide();
            detElement.closest('.det').find('.pensioner-section').hide();
            detElement.closest('.det').find('.sepsenp-section').hide();
            detElement.closest('.det').find('.agriculturist-section').hide();


            // Show the section specific to selected employment type
            if (employment_type === 'SALARIED') {
                detElement.closest('.det').find('.salaried-section').show();
            } else if (employment_type === 'PENSIONER') {
                detElement.closest('.det').find('.pensioner-section').show();
            } else if (employment_type === 'SEP' || employment_type === 'SENP') {
                detElement.closest('.det').find('.sepsenp-section').show();
            } else if (employment_type === 'AGRICULTURIST') {
                detElement.closest('.det').find('.agriculturist-section').show();
            }
            checkForITREntries(detElement);
        }
    } else if (selected === 'SURROGATE') {
        detElement.closest('.det').find('.surrogatediv').show();
        detElement.closest('.det').find('.surrogate-section').removeClass('hidden');
        resetSurrogate(detElement);
    } else if (selected === 'NONFOIR') {
         handleNonFOIRSelection(detElement.closest('.det').find('.nonfoirdiv'));
    } else if (selected === 'IMPUTED') {
        handleImputedIncomeSelection(detElement.closest('.det').find('.imputeddiv'));
    } else if (selected === 'LOANFD') {
        detElement.closest('.det').find('.fddiv').show();
        handleFDProgramSelection(detElement.closest('.det').find('.fddiv'));
    }
}
function getMonthName(monthNum) {
    var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    return months[parseInt(monthNum) - 1];
}





function toggleDetailsView(detElement) {
    var $container = detElement.closest('.details-container');
    var $button = detElement.closest('.rtr-loan-emi-view');

    if ($container.is(':visible')) {
        $container.slideUp();
        $button.text('View detailed EMI transactions');
    } else {
        $container.slideDown();
        $button.text('Hide detailed EMI transactions');
    }
}

// Helper functions
function formatMonth(dateStr) {
    if (!dateStr) return '';
    var parts = dateStr.split('-');
    if (parts.length !== 2) return dateStr;

    var year = parts[0];
    var month = parts[1];

    var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    var monthName = months[parseInt(month) - 1];

    return monthName + ' ' + year;
}

function formatDate(dateStr) {
    if (!dateStr) return '';

    // Check if it's already in display format
    if (dateStr.includes('/')) return dateStr;

    var date = new Date(dateStr);
    if (isNaN(date.getTime())) return dateStr;

    return date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear();
}

function formatAmount(amount) {
    if (amount === null || amount === undefined) return '0.00';
    return parseFloat(amount).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function updateSurrogateDetails(programDetails, detElement) {
    console.log("UPDATE THE SURROGATE PROGRAM");
    var form = detElement.closest('.det');

    if (programDetails.propertyOwner) {
        form.find('.surrogatediv input[name="propertyOwner"][value="' + programDetails.propertyOwner + '"]').prop('checked', true);
    }

    // Initialize statementsData array with data from the programDetails
    statementsData = [];

    // Add first statement if it exists
    if (programDetails.surrogate1StartDate && programDetails.surrogate1EndDate) {
        statementsData.push({
            startDate: programDetails.surrogate1StartDate,
            endDate: programDetails.surrogate1EndDate,
            startDisplay: formatDisplayFromYYYYMM(programDetails.surrogate1StartDate),
            endDisplay: formatDisplayFromYYYYMM(programDetails.surrogate1EndDate),
            bankCode: programDetails.surrogate1BSAData ? programDetails.surrogate1BSAData.bank : '',
            uploaded: programDetails.surrogate1BSAProcessed === 'Y',
            autoStart: false,
            autoEnd: false
        });
    }

    // Add second statement if it exists
    if (programDetails.surrogate2StartDate && programDetails.surrogate2EndDate) {
        statementsData.push({
            startDate: programDetails.surrogate2StartDate,
            endDate: programDetails.surrogate2EndDate,
            startDisplay: formatDisplayFromYYYYMM(programDetails.surrogate2StartDate),
            endDisplay: formatDisplayFromYYYYMM(programDetails.surrogate2EndDate),
            bankCode: programDetails.surrogate2BSAData ? programDetails.surrogate2BSAData.bank : '',
            uploaded: programDetails.surrogate2BSAProcessed === 'Y',
            autoStart: false,
            autoEnd: false
        });
    }

    // Add third statement if it exists
    if (programDetails.surrogate3StartDate && programDetails.surrogate3EndDate) {
        statementsData.push({
            startDate: programDetails.surrogate3StartDate,
            endDate: programDetails.surrogate3EndDate,
            startDisplay: formatDisplayFromYYYYMM(programDetails.surrogate3StartDate),
            endDisplay: formatDisplayFromYYYYMM(programDetails.surrogate3EndDate),
            bankCode: programDetails.surrogate3BSAData ? programDetails.surrogate3BSAData.bank : '',
            uploaded: programDetails.surrogate3BSAProcessed === 'Y',
            autoStart: false,
            autoEnd: false
        });
    }

    // Set skipAutoPopulate to true since we're loading existing data
    skipAutoPopulate = true;

    // Render the statements in the UI
    renderStatements(detElement);

    // If all statements are uploaded, switch to review mode
    var allUploaded = statementsData.every(s => s.uploaded);
    if (allUploaded && statementsData.length > 0) {
        detElement.closest('.det').find('.surrogate-edit-mode').hide();
        buildSummary(detElement);
        detElement.closest('.det').find('.surrogate-review-mode').show();

        // Display BSA data for each statement
        displaySurrogateBSAData(programDetails, detElement);
    } else {
        detElement.closest('.det').find('.surrogate-edit-mode').show();
        detElement.closest('.det').find('.surrogate-review-mode').hide();
    }

    // Update the coverage UI
    updateCoverageUI(detElement);
}

// Helper function to format YYYY-MM to display format
function formatDisplayFromYYYYMM(yyyymm) {
    if (!yyyymm) return '';
    var parts = yyyymm.split('-');
    var year = parts[0];
    var month = parseInt(parts[1]) - 1; // 0-based month

    var months = [
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    ];

    return months[month] + ' ' + year;
}

// Function to populate ITR BSA summary
function populateITRBSASummary(bsaData, form) {
    console.log("populate is called summary bsa itr");
    var section, inputSection, summarySection, alertSection;
    section = form.find('[data-response-type="BSA-ITR"]').closest('.section-body');
    inputSection = section.find('.row').first();
    alertSection = section.find('.alert-card').first();
    summarySection = form.find('[data-response-type="BSA-ITR"]');
    inputSection.hide();
    alertSection.hide();
    section.find('.btn-primary').hide();

    var bsaSection = form.find('[data-response-type="BSA-ITR"]');

    if (!summarySection.length) {
        console.warn("BSA-ITR section not found in the form");
        return;
    }

// Create BSA summary card
    var summaryHtml = `
<div class="card mb-3">
<div class="card-header bg-success text-white">
<div class="d-flex justify-content-between align-items-center">
<h6 class="mb-0">Bank Statement Analysis Processed</h6>
<button type="button" class="btn btn-sm btn-light reset-bsa-btn" data-statement-type="BSA-ITR">
Reset <i class="ph-arrow-counter-clockwise ms-1"></i>
</button>
</div>
</div>
<div class="card-body">
<div class="alert alert-success">
<i class="fas fa-check-circle mr-2"></i> Bank statement has been successfully processed.
</div>
<hr class="my-2">
<div class="row">
<div class="col-md-6">
<div class="mb-2">
<strong>Period:</strong> ${bsaData.startDate} to ${bsaData.endDate}
</div>
<div>
<strong>Bank:</strong> ${bsaData.bankNameDesc}
</div>
</div>
<div class="col-md-6">
<div class="mb-2">
<strong>Account:</strong> ${bsaData.accountNo}
</div>
<div>
<strong>Account Type:</strong> ${bsaData.accountType}
</div>
<div>
<strong>ABB:</strong> ${formatAmount(bsaData.avgBankBalance)}
</div>
</div>
</div>
<div class="mt-2 small text-muted">
Click "Reset" to clear this statement and upload a new one.
</div>
</div>
</div>
`;

// Populate the BSA section
    summarySection.html(summaryHtml);
    summarySection.show();

// Show the BSA section's parent container if hidden
    var sectionContainer = bsaSection.closest('.section-body');
    if (sectionContainer.length && !sectionContainer.is(':visible')) {
        sectionContainer.show();
    }

// Hide the upload button or form since data is already processed
    var uploadButton = form.find('.itr-section').find('.bsa-upload-btn');
    if (uploadButton.length) {
        uploadButton.hide();
    }
}

// Function to display BSA data for surrogate statements
function displaySurrogateBSAData(programDetails, detElement) {
    console.log("======================bsa summary view");
    // Display BSA data for first statement
    if (programDetails.surrogate1BSAData) {
        var bsaData1 = programDetails.surrogate1BSAData;
        detElement.closest('.det').find('[data-response-type="SURROGATE-1"]').html(`
            <div class="card mb-3">
                <div class="card-header bg-success text-white">
                    <h6 class="mb-0">Statement 1 Processed</h6>
                </div>
                <div class="card-body">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle mr-2"></i> Bank statement has been successfully processed.
                    </div>
                    <hr class="my-2">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-2">
                                <strong>Period:</strong> ${bsaData1.startDate} to ${bsaData1.endDate}
                            </div>
                            <div>
                                <strong>Bank:</strong> ${bsaData1.bankNameDesc}
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-2">
                                <strong>Account:</strong> ${bsaData1.accountNo}
                            </div>
                            
                        </div>
                    </div>
                </div>
            </div>
        `);
        detElement.closest('.det').find('[data-response-type="SURROGATE-1"]').show();

        if (programDetails.surrogate1BSAData.monthlyData) {
            var reportDataJson = programDetails.surrogate1BSAData.monthlyData;
            surrogateMonthlyDetails(reportDataJson, detElement, "SURROGATE-1");
        }


    }

    // Display BSA data for second statement
    if (programDetails.surrogate2BSAData) {
        var bsaData2 = programDetails.surrogate2BSAData;
        detElement.closest('.det').find('[data-response-type="SURROGATE-2"]').html(`
            <div class="card mb-3">
                <div class="card-header bg-success text-white">
                    <h6 class="mb-0">Statement 2 Processed</h6>
                </div>
                <div class="card-body">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle mr-2"></i> Bank statement has been successfully processed.
                    </div>
                    <hr class="my-2">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-2">
                                <strong>Period:</strong> ${bsaData2.startDate} to ${bsaData2.endDate}
                            </div>
                            <div>
                                <strong>Bank:</strong> ${bsaData2.bankNameDesc}
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-2">
                                <strong>Account:</strong> ${bsaData2.accountNo}
                            </div>
                            
                        </div>
                    </div>
                </div>
            </div>
        `);
        detElement.closest('.det').find('[data-response-type="SURROGATE-2"]').show();
        if (programDetails.surrogate2BSAData.monthlyData) {
            var reportDataJson = programDetails.surrogate2BSAData.monthlyData;
            surrogateMonthlyDetails(reportDataJson, detElement, "SURROGATE-2");
        }
    }

    // Display BSA data for third statement
    if (programDetails.surrogate3BSAData) {
        var bsaData3 = programDetails.surrogate3BSAData;
        detElement.closest('.det').find('[data-response-type="SURROGATE-3"]').html(`
            <div class="card mb-3">
                <div class="card-header bg-success text-white">
                    <h6 class="mb-0">Statement 3 Processed</h6>
                </div>
                <div class="card-body">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle mr-2"></i> Bank statement has been successfully processed.
                    </div>
                    <hr class="my-2">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-2">
                                <strong>Period:</strong> ${bsaData3.startDate} to ${bsaData3.endDate}
                            </div>
                            <div>
                                <strong>Bank:</strong> ${bsaData3.bankNameDesc}
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-2">
                                <strong>Account:</strong> ${bsaData3.accountNo}
                            </div>
                           
                        </div>
                    </div>
                </div>
            </div>
        `);
        detElement.closest('.det').find('[data-response-type="SURROGATE-3"]').show();
        if (programDetails.surrogate3BSAData.monthlyData) {
            var reportDataJson = programDetails.surrogate3BSAData.monthlyData;
            surrogateMonthlyDetails(reportDataJson, detElement, "SURROGATE-3");
        }
    }

    // Show total ABB
    if (programDetails.abbAmount) {
        detElement.closest('.det').find('.abb-amount').val(programDetails.abbAmount.toFixed(2));
    }
}

function validateProgramSelection(triggerElement, selectedProgram, callback) {
    var applicantType = getApplicantTypeFromDataCode(triggerElement);
    var incomeConsidered = triggerElement.find('input.incomeCheck:checked').val();
    if (applicantType === "G") {
        if (incomeConsidered !== "N") {
            alertmsg("Guarantors can only select 'Income considered = No'");
            return false;
        }
    }
    // If selected program is not RTR, proceed without validation
    if (selectedProgram !== 'RTR') {
        if (typeof callback === 'function') {
            callback(true);
        }
        return;
    }
}

function validateProgramConstraints(triggerElement, selectedProgram, incomeConsidered, callback) {
    // If income is not considered, no validation needed
    if (incomeConsidered !== 'Y') {
        if (typeof callback === 'function') {
            callback(true);
        }
        return;
    }

    var applicantId = triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val();
    var wiNum = $('#winum').val();
    if(!selectedProgram) {
        selectedProgram="NONE";
    }

    // Show loader
    showLoader();

    // Call the backend validation endpoint
    $.ajax({
        url: 'fetch/validate-program-selection',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            wiNum: wiNum,
            applicantId: applicantId,
            incomeConsidered: incomeConsidered,
            programCode: selectedProgram
        }),
        success: function (response) {
            hideLoader();

            if (response.status === 'S') {
                // Validation successful
                if (typeof callback === 'function') {
                    callback(true);
                }
            } else {
                // Validation failed
                Swal.fire({
                    title: 'Program Selection Validation',
                    html: response.message,
                    icon: 'warning',
                    confirmButtonText: 'OK',
                    customClass: {
                        confirmButton: 'btn btn-danger'
                    },
                    buttonsStyling: false
                }).then(function () {
                    // Reset dropdown to previous value or required program if available
                    // if (response.requiredProgram) {
                    //     triggerElement.val(response.requiredProgram);
                    // } else {
                    //     triggerElement.val(triggerElement.closest('.det').find('input[name="hidProgramCode"]').val() || 'NONE');
                    // }
                    triggerElement.val("");
                    triggerElement.trigger('change.select2'); // If using select2

                    if (typeof callback === 'function') {
                        callback(false);
                    }
                });
            }
        },
        error: function (xhr, status, error) {
            hideLoader();
            console.error('Error validating program constraints:', error);

            Swal.fire({
                title: 'Validation Error',
                text: 'An error occurred while validating program constraints. Please try again.',
                icon: 'error',
                confirmButtonText: 'OK'
            });

            if (typeof callback === 'function') {
                callback(false);
            }
        }
    });
}

function surrogateMonthlyDetails(response, triggerElement, statementType) {
    console.log("======================bsa monthly view");
    // Parse the JSON data if it's a string
    let data;
    try {
        if (typeof response === 'string') {
            data = JSON.parse(response);
        } else {
            data = response;
        }
    } catch (error) {
        console.error("Error parsing JSON data:", error);
        return;
    }

    // Check if data and required properties exist
    if (!data || !data.monthlyDetails) {
        console.error("Invalid data structure - missing monthlyDetails");
        return;
    }

    // Build the table HTML
    let tableHtml = `
        <div class="bsaResponse mt-3">
            <table class="table table-bordered table-striped">
                <thead>
                    <tr>
                        <th colspan="12">Account Information</th>
                    </tr>
                    <tr>
                        <td>Name</td>
                        <td colspan="2"><b>${data.customerInfo ? data.customerInfo.name : 'N/A'}</b></td>
                        <td>Account No</td>
                        <td>${data.accountInfo ? data.accountInfo.accountNo : 'N/A'}</td>
                        <td colspan="2">Account Type</td>
                        <td colspan="3"><b>${data.accountInfo ? data.accountInfo.accountType : 'N/A'}</b></td>
                    </tr>
                    <tr>
                        <th colspan="12">Monthly Details</th>
                    </tr>
                    <tr>
                        <th>Month</th>
                        <th>Total Credits</th>
                        <th>Total Debits</th>
                        <th>Balance Min</th>
                        <th>Balance Max</th>
                        <th>Balance Avg</th>
                        <th>Calculated ABB</th>
                        <th>Included in ABB</th>
                        <th>Penal Charges</th>
                        <th>Outward Bounces</th>
                        <th>Total Salary</th>
                        <th>Inward Bounces</th>
                    </tr>`;

    // Add rows for each month
    data.monthlyDetails.forEach(month => {
        tableHtml += `
            <tr>
            <tr ${month.isTrimmed ? '' : 'style="background-color: #ffcccc;"'}>
                <td>${month.month}</td>
                <td>${month.totalCredits}</td>
                <td>${month.totalDebits}</td>
                <td>${month.balanceMin}</td>
                <td>${month.balanceMax}</td>
                <td>${month.balanceAvg}</td>
                <td><b>${typeof month.calculatedABB === 'number' ? month.calculatedABB.toFixed(2) : month.calculatedABB}</b></td>
                <td>${month.penalCharges}</td>
                <td>${month.outwBounces}</td>
                <td>${month.totalSalary}</td>
                <td>${month.inwBounces}</td>
            </tr>`;
    });

    // Close the table and add ABB summary
    tableHtml += `
        </table>
    </div>`;

    // Find the correct container and append the table
    let container = triggerElement.closest('.det').find('[data-response-type="' + statementType + '"]');

    // Check if container exists
    if (container.length === 0) {
        console.error("Target container not found for response type:", statementType);
        return;
    }

    // Find the bsaResponse element or create it if it doesn't exist
    let bsaResponseElement = container.find('.bsaResponse');
    if (bsaResponseElement.length === 0) {
        // Append the table directly to the container
        container.append(tableHtml);
    } else {
        // Replace existing content
        bsaResponseElement.html(tableHtml);
    }

    // Update the ABB value if available
    if (data.bsaData && data.bsaData.averageBankBalance !== undefined) {
        let abbAmount = data.bsaData.averageBankBalance;
        triggerElement.closest('.det').find('.bsaABBDiv').show();
        triggerElement.closest('.det').find('input[name^="bsaABB"]').val(abbAmount.toFixed(2));
    }
}

function checkForITREntries(detElement) {
    console.log("ITR check started");
    var jsonBody = {
        wiNum: $('#winum').val(),
        applicantId: detElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val()
    };
    $.ajax({
        url: "fetch/hasITREntries",
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(jsonBody),
        success: function (hasEntries) {
            if (hasEntries) {
                console.log("ITR count check---" + hasEntries);
                $('.check-itr-status').show();
                HLITRHandler.checkLatestCompletedITRTransactionStatus(detElement);
            } else {
                $('.check-itr-status').hide();
            }
        }, error: function (xhr, status, error) {
            console.error("Error checking for entries:", status, error);
        }
    });
}

function getApplicantTypeFromDataCode(element) {
    var form = element.closest('form.det');
    var dataCode = form.data('code') || '';

    // Extract the first character of the data-code
    // Format is like "A-5", "C-5", "G-5"
    if (dataCode && dataCode.length > 0) {
        // Split by '-' and get the first part
        var parts = dataCode.split('-');
        if (parts.length > 0) {
            return parts[0]; // This should be "A", "C", or "G"
        }
    }
    return '';
}

/*
==============================================
BSA ABB calaculation display
==============================================
*/
$('#loanbody').on('click', '.btn-calc-info', function (e) {
    e.preventDefault();
    showABBCalculationModal($(this));
});
function showABBCalculationModal(triggerElement) {
    var applicantId = triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val();
    var wiNum = $('#winum').val();

    // Get the specific modal for this applicant
    var modalElement = triggerElement.closest('.det').closest('.tab-pane').find('.abbCalculationModal');

    $.ajax({
        url: `fetch/abb-calculation-details/${applicantId}/${wiNum}`,
        type: 'GET',
        dataType: 'json',
        beforeSend: function() {
            // Show professional loading spinner
            modalElement.find('.modal-body').html(`
                <div class="d-flex justify-content-center align-items-center" style="min-height: 350px;">
                    <div class="text-center">
                        <div class="professional-spinner">
                            <div class="spinner-ring"></div>
                        </div>
                        <div class="mt-4">
                            <h5 style="color: #c1272e; font-weight: 600; margin-bottom: 8px;">Analyzing Bank Statements</h5>
                            <p style="color: #6c757d; font-size: 14px; margin: 0;">Processing calculation details...</p>
                        </div>
                    </div>
                </div>
            `);
            modalElement.modal('show');
        },
        success: function(response) {
            if (response.success) {
                populateProfessionalABBModal(response, modalElement);
            } else {
                showProfessionalError(response.message, modalElement);
            }
        },
        error: function(xhr, status, error) {
            showProfessionalError('Error loading calculation details. Please try again.', modalElement);
        }
    });
}

function populateProfessionalABBModal(data, modalElement) {
    const headerStats = generateProfessionalHeader(data);
    const tableContent = generateProfessionalTable(data);
    const summaryCards = generateProfessionalSummary(data);

    const modalContent = `
        <!-- Professional Modal Header -->
        <div class="professional-modal-header">
        <div class="btnclosediv">
        <button type="button" class="btn-close-professional" data-bs-dismiss="modal" aria-label="Close">
                <i class="fas fa-times"></i>
            </button>
</div>
            <div class="header-content">
                <div class="header-main">
                    <h5 class="modal-title">
                        <i class="fas fa-chart-line me-2"></i>Average Bank Balance Analysis
                    </h5>
                    <p class="header-subtitle">Comprehensive calculation breakdown</p>
                </div>
                ${headerStats}
            </div>
            
        </div>
        
        <!-- Professional Modal Body -->
        <div class="professional-modal-body">
            ${tableContent}
            ${summaryCards}
        </div>
        
        <!-- Professional Modal Footer -->
        <div class="professional-modal-footer">
            <button type="button" class="btn-close-analysis" data-bs-dismiss="modal">
                <i class="fas fa-check me-2"></i>Close Analysis
            </button>
        </div>
    `;

    modalElement.find('.modal-content').html(modalContent);
    addProfessionalStyling();
}

function generateProfessionalHeader(data) {
    const totalMonths = data.totalMonths || 0;
    const finalABB = parseFloat(data.finalABB || 0);
    const finalABBFormatted = formatCurrencyCompact(finalABB);

    return `
        <div class="header-stats-professional">
            <div class="stat-card-pro">
                <div class="stat-number">${3}</div>
                <div class="stat-label">Bank Accounts</div>
            </div>
            <div class="stat-card-pro">
                <div class="stat-number">${totalMonths}</div>
                <div class="stat-label">Months Analyzed</div>
            </div>
            <div class="stat-card-pro stat-highlight">
                <div class="stat-number">${finalABBFormatted}</div>
                <div class="stat-label">Final ABB</div>
            </div>
        </div>
    `;
}

function generateProfessionalTable(data) {
    const tableRows = data.calculationBreakdown.map((monthData, index) => {
        const isExcluded = !monthData.included;
        const rowClass = isExcluded ? 'excluded-row-pro' : 'included-row-pro';

        const rankBadge = isExcluded ?
            `<span class="rank-badge-excluded">${monthData.rank}</span>` :
            `<span class="rank-badge-included">${monthData.rank}</span>`;

        const statusBadge = isExcluded ?
            `<span class="status-badge-excluded">
                <i class="fas fa-times-circle me-1"></i>Excluded
             </span>` :
            `<span class="status-badge-included">
                <i class="fas fa-check-circle me-1"></i>Included
             </span>`;

        return `
            <tr class="${rowClass}">
                <td class="month-cell-pro">
                    <div class="month-wrapper">
                        <strong>${monthData.month}</strong>
                    </div>
                </td>
                <td class="amount-cell-pro">${formatCurrency(monthData.surrogate1)}</td>
                <td class="amount-cell-pro">${formatCurrency(monthData.surrogate2)}</td>
                <td class="amount-cell-pro">${formatCurrency(monthData.surrogate3)}</td>
                <td class="combined-total-pro">${formatCurrency(monthData.combinedTotal)}</td>
                <td class="text-center">${rankBadge}</td>
                <td class="text-center">${statusBadge}</td>
            </tr>
        `;
    }).join('');

    const bank1Name = data.bankNames['SURROGATE-1'] || 'Bank 1';
    const bank2Name = data.bankNames['SURROGATE-2'] || 'Bank 2';
    const bank3Name = data.bankNames['SURROGATE-3'] || 'Bank 3';

    return `
        <div class="professional-table-container">
            <div class="table-header-pro">
                <h6><i class="fas fa-table me-2"></i>Monthly Balance Breakdown</h6>
                <p>Detailed analysis of combined monthly balances across all bank accounts</p>
            </div>
            <div class="table-wrapper-pro">
                <table class="table-professional">
                    <thead>
                        <tr>
                            <th><i class="fas fa-calendar me-2"></i>Month</th>
                            <th><i class="fas fa-university me-2"></i>${bank1Name}</th>
                            <th><i class="fas fa-university me-2"></i>${bank2Name}</th>
                            <th><i class="fas fa-university me-2"></i>${bank3Name}</th>
                            <th><i class="fas fa-calculator me-2"></i>Combined Total</th>
                            <th><i class="fas fa-sort-numeric-up me-2"></i>Rank</th>
                            <th><i class="fas fa-filter me-2"></i>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${tableRows}
                    </tbody>
                </table>
            </div>
        </div>
    `;
}

function generateProfessionalSummary(data) {
    const excludedMonths = data.totalMonths - data.includedMonths;

    return `
        <div class="summary-section-pro">
            <div class="row">
                <div class="col-12">
                    <div class="summary-card-pro">
                        <div class="card-header-pro">
                            <h6><i class="fas fa-chart-bar me-2"></i>Calculation Summary</h6>
                        </div>
                        <div class="card-body-pro-compact">
                            <div class="summary-stats-compact">
                                <div class="stat-compact">
                                    <i class="fas fa-calendar text-primary me-2"></i>
                                    <span class="stat-label-compact">Total:</span>
                                    <span class="stat-value-compact">${data.totalMonths}</span>
                                </div>
                                <div class="stat-compact">
                                    <i class="fas fa-check text-success me-2"></i>
                                    <span class="stat-label-compact">Included:</span>
                                    <span class="stat-value-compact">${data.includedMonths}</span>
                                </div>
                                <div class="stat-compact">
                                    <i class="fas fa-times text-danger me-2"></i>
                                    <span class="stat-label-compact">Excluded:</span>
                                    <span class="stat-value-compact">${excludedMonths}</span>
                                </div>
                                <div class="stat-compact">
                                    <i class="fas fa-plus text-info me-2"></i>
                                    <span class="stat-label-compact">Sum:</span>
                                    <span class="stat-value-compact">${formatCurrency(data.sumIncludedMonths)}</span>
                                </div>
                            </div>
                            <div class="final-result-compact">
                                <div class="result-icon-compact">
                                    <i class="fas fa-trophy"></i>
                                </div>
                                <div class="result-content-compact">
                                    <span class="result-label-compact">Final ABB:</span>
                                    <span class="result-value-compact">${formatCurrency(data.finalABB)}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

function addProfessionalStyling() {
    if (document.getElementById('professional-abb-styles')) return;

    const style = document.createElement('style');
    style.id = 'professional-abb-styles';
    style.textContent = `
        /* Professional Modal Styling */
        .abbCalculationModal .modal-content {
            border: none;
            border-radius: 12px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
            overflow: hidden;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .professional-modal-header {
            background: linear-gradient(135deg, #c1272e 0%, #f12e30 100%);
            color: white;
            padding: 20px 32px;
            position: relative;
        }
        
        .header-content {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            flex-wrap: wrap;
            gap: 20px;
        }
        
        .header-main .modal-title {
            font-size: 20px;
            font-weight: 600;
            margin: 0 0 6px 0;
            color: white;
        }
        
        .header-subtitle {
            font-size: 13px;
            opacity: 0.9;
            margin: 0;
            font-weight: 400;
        }
        
        .header-stats-professional {
            display: flex;
            gap: 16px;
            flex-wrap: wrap;
        }
        
        .stat-card-pro {
            background: rgba(255, 255, 255, 0.15);
            backdrop-filter: blur(10px);
            border-radius: 8px;
            padding: 12px 16px;
            text-align: center;
            min-width: 90px;
            border: 1px solid rgba(255, 255, 255, 0.2);
        }
        
        .stat-card-pro.stat-highlight {
            background: rgba(255, 255, 255, 0.25);
            border: 1px solid rgba(255, 255, 255, 0.4);
        }
        
        .stat-number {
            font-size: 18px;
            font-weight: 700;
            line-height: 1;
            margin-bottom: 4px;
        }
        
        .stat-label {
            font-size: 11px;
            opacity: 0.9;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .btn-close-professional {
        background: rgba(255, 255, 255, 0);
  border: 1px solid rgba(255, 255, 255, 0);
            
            color: white;
            border-radius: 6px;
            width: 32px;
            height: 32px;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: all 0.3s ease;
        }
        .btnclosediv {
                display: flex;
                justify-content: end;
                margin-bottom: 8px;
        }
        
        .btn-close-professional:hover {
            background: rgba(255, 255, 255, 0.3);
            transform: scale(1.1);
        }
        
        .professional-modal-body {
            padding: 32px;
            background: #ffffff;
        }
        
        .professional-table-container {
            background: #ffffff;
            border-radius: 12px;
            border: 1px solid #e9ecef;
            margin-bottom: 32px;
            overflow: hidden;
        }
        
        .table-header-pro {
            background: #f8f9fa;
            padding: 20px 24px;
            border-bottom: 1px solid #e9ecef;
        }
        
        .table-header-pro h6 {
            font-size: 16px;
            font-weight: 600;
            color: #2d3748;
            margin: 0 0 4px 0;
        }
        
        .table-header-pro p {
            font-size: 13px;
            color: #6c757d;
            margin: 0;
        }
        
        .table-wrapper-pro {
            overflow-x: auto;
        }
        
        .table-professional {
            width: 100%;
            margin: 0;
            font-size: 13px;
        }
        
        .table-professional thead th {
            background: #2d3748;
            color: white;
            font-weight: 600;
            font-size: 12px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            padding: 16px 12px;
            border: none;
            white-space: nowrap;
        }
        
        .table-professional tbody td {
            padding: 14px 12px;
            border-bottom: 1px solid #e9ecef;
            vertical-align: middle;
            font-weight: 500;
        }
        
        .included-row-pro {
            background: #ffffff;
            transition: all 0.3s ease;
        }
        
        .included-row-pro:hover {
            background: #f8f9fa;
        }
        
        .excluded-row-pro {
            background: #ffffff;
            position: relative;
        }
        
        .excluded-row-pro::before {
            display: none;
        }
        
        .excluded-row-pro:hover {
            background: #f8f9fa;
        }
        
        .month-cell-pro {
            font-weight: 600;
        }
        
        .month-wrapper {
            background: #f8f9fa;
            padding: 6px 12px;
            border-radius: 6px;
            display: inline-block;
            min-width: 70px;
            text-align: center;
        }
        
        .amount-cell-pro {
            font-family: 'Consolas', 'Monaco', monospace;
            text-align: right;
            color: #2d3748;
        }
        
        .combined-total-pro {
            font-family: 'Consolas', 'Monaco', monospace;
            text-align: right;
            font-weight: 700;
            color: #c1272e;
            background: rgba(193, 39, 46, 0.05);
            border-radius: 6px;
        }
        
        .rank-badge-included {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 28px;
            height: 28px;
            border-radius: 50%;
            background: #28a745;
            color: white;
            font-weight: 600;
            font-size: 12px;
        }
        
        .rank-badge-excluded {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 28px;
            height: 28px;
            border-radius: 50%;
            background: linear-gradient(135deg, #c1272e 0%, #f12e30 100%);
            color: white;
            font-weight: 600;
            font-size: 12px;
        }
        
        .status-badge-included {
            background: #d4edda;
            color: #155724;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 11px;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
        }
        
        .status-badge-excluded {
            background: linear-gradient(135deg, rgba(193, 39, 46, 0.1) 0%, rgba(241, 46, 48, 0.1) 100%);
            color: #c1272e;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 11px;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
            border: 1px solid rgba(193, 39, 46, 0.2);
        }
        
        .summary-section-pro {
            margin-top: 24px;
        }
        
        .summary-card-pro {
            background: #ffffff;
            border: 1px solid #e9ecef;
            border-radius: 12px;
            overflow: hidden;
        }
        
        .card-header-pro {
            background: #f8f9fa;
            padding: 18px 24px;
            border-bottom: 1px solid #e9ecef;
        }
        
        .card-header-pro h6 {
            font-size: 16px;
            font-weight: 600;
            color: #2d3748;
            margin: 0;
        }
        
        .card-body-pro-compact {
            padding: 16px 24px;
        }
        
        .summary-stats-compact {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 16px;
            margin-bottom: 16px;
            padding-bottom: 16px;
            border-bottom: 1px solid #e9ecef;
        }
        
        .stat-compact {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 13px;
        }
        
        .stat-label-compact {
            color: #6c757d;
            font-weight: 500;
        }
        
        .stat-value-compact {
            font-weight: 600;
            color: #2d3748;
        }
        
        .final-result-compact {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 12px;
            background: linear-gradient(135deg, rgba(193, 39, 46, 0.05) 0%, rgba(241, 46, 48, 0.05) 100%);
            border: 1px solid rgba(193, 39, 46, 0.2);
            border-radius: 8px;
            padding: 16px;
        }
        
        .result-icon-compact {
            width: 32px;
            height: 32px;
            background: linear-gradient(135deg, #c1272e 0%, #f12e30 100%);
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 16px;
        }
        
        .result-content-compact {
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        .result-label-compact {
            font-size: 14px;
            color: #6c757d;
            font-weight: 500;
        }
        
        .result-value-compact {
            font-size: 18px;
            font-weight: 700;
            color: #c1272e;
        }
        
        .methodology-card-pro {
            background: #ffffff;
            border: 1px solid #e9ecef;
            border-radius: 12px;
            height: 100%;
        }
        
        .method-header-pro {
            background: linear-gradient(135deg, #c1272e 0%, #f12e30 100%);
            color: white;
            padding: 18px 20px;
        }
        
        .method-header-pro h6 {
            font-size: 14px;
            font-weight: 600;
            margin: 0;
        }
        
        .method-body-pro {
            padding: 20px;
        }
        
        .method-title {
            font-size: 14px;
            font-weight: 600;
            color: #2d3748;
            margin-bottom: 16px;
        }
        
        .method-step {
            display: flex;
            align-items: flex-start;
            gap: 12px;
            margin-bottom: 12px;
        }
        
        .step-number {
            width: 24px;
            height: 24px;
            background: linear-gradient(135deg, #c1272e 0%, #f12e30 100%);
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 11px;
            font-weight: 600;
            flex-shrink: 0;
        }
        
        .step-text {
            font-size: 12px;
            color: #2d3748;
            line-height: 1.4;
        }
        
        .method-note {
            background: #f8f9fa;
            padding: 12px;
            border-radius: 6px;
            font-size: 11px;
            color: #6c757d;
            margin-top: 16px;
        }
        
        .professional-modal-footer {
            background: #f8f9fa;
            padding: 20px 32px;
            border-top: 1px solid #e9ecef;
            display: flex;
            justify-content: flex-end;
        }
        
        .btn-close-analysis {
            background: linear-gradient(135deg, #c1272e 0%, #f12e30 100%);
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 8px;
            font-size: 13px;
            font-weight: 600;
            transition: all 0.6s cubic-bezier(0.23, 1, 0.320, 1);
            box-shadow: 0 0 1.6em rgba(255, 33, 33, 0.3), 0 0 1.6em hsla(191.2, 98.2%, 56.1%, 0);
        }
        
        .btn-close-analysis:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(193, 39, 46, 0.4);
        }
        
        .professional-spinner {
            position: relative;
            width: 48px;
            height: 48px;
            margin: 0 auto;
        }
        
        .spinner-ring {
            width: 48px;
            height: 48px;
            border: 4px solid #f3f4f6;
            border-top: 4px solid #c1272e;
            border-radius: 50%;
            animation: spin 1s linear infinite;
        }
        
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        
        /* Responsive Design */
        @media (max-width: 768px) {
            .professional-modal-body {
                padding: 20px;
            }
            
            .header-content {
                flex-direction: column;
                gap: 16px;
            }
            
            .summary-grid {
                grid-template-columns: 1fr;
            }
            
            .final-result-pro {
                flex-direction: column;
                text-align: center;
                gap: 12px;
            }
        }
    `;
    document.head.appendChild(style);
}

function formatCurrency(amount) {
    if (!amount || isNaN(amount)) return '₹0.00';
    return `${parseFloat(amount).toLocaleString('en-IN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    })}`;
}

function formatCurrencyCompact(amount) {
    if (!amount || isNaN(amount)) return '₹0';
    const value = parseFloat(amount);
    if (value >= 10000000) {
        return `₹${(value / 10000000).toFixed(1)}Cr`;
    } else if (value >= 100000) {
        return `₹${(value / 100000).toFixed(1)}L`;
    } else if (value >= 1000) {
        return `₹${(value / 1000).toFixed(1)}K`;
    } else {
        return `₹${value.toFixed(0)}`;
    }
}

function showProfessionalError(message, modalElement) {
    modalElement.find('.modal-body').html(`
        <div class="text-center py-5">
            <div class="mb-4">
                <div style="width: 64px; height: 64px; background: linear-gradient(135deg, #c1272e 0%, #f12e30 100%); border-radius: 50%; display: flex; align-items: center; justify-content: center; margin: 0 auto;">
                    <i class="fas fa-exclamation-triangle text-white" style="font-size: 24px;"></i>
                </div>
            </div>
            <h5 style="color: #c1272e; font-weight: 600; margin-bottom: 12px;">Error Loading Analysis</h5>
            <p style="color: #6c757d; margin-bottom: 24px;">${message}</p>
            <button type="button" class="btn-close-analysis" onclick="location.reload()" style="display: inline-flex; align-items: center; gap: 8px;">
                <i class="fas fa-refresh"></i>Retry Analysis
            </button>
        </div>
    `);
}
