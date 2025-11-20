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

function handleNonFOIRSelection(detElement) {
    console.log("========== Non-FOIR Program Selected ==========");

    // Show Non-FOIR div
    detElement.show();

    // Initialize date pickers for 6-month period
    initializeNonFOIRDatePickers(detElement);

    console.log("Non-FOIR section displayed. Ready for statement upload.");
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
function initializeProgramDetails() {
    $('.program-itr form').each(function () {
        var form = $(this);
        var incomeConsidered = form.find('input[name="hidincomeConsidered"]').val();
        var programCode = form.find('input[name="hidProgramCode"]').val();
        var liquidMonthlyIncome = form.find('input[name="hidliquidMonthlyIncome"]').val();
        var liquidUploadStatus = form.find('input[name="hidliquidUploadStatus"]').val();
        console.log("initializeProgramDetails=====");
        fetchProgramDetails(form);
    });
}
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
        case 'LOANFD':
            // Reset FD Program fields
            form.find('.fd-accounts-table tbody').empty();
            form.find('.fd-selected-accounts').empty();
            form.find('.fd-total-amount').text('0.00');
            form.find('.fd-account-validate').prop('disabled', false);

            // Clear any FD account selection checkboxes
            form.find('input[name="fdAccountSelect"]').prop('checked', false);

            // Reset FD summary displays if they exist
            form.find('.fd-summary-section').hide();
            form.find('.fd-result-section').hide();
            form.find('.fd-no-accounts-message').show();
            break;
        case 'NONFOIR':
            // Reset Non-FOIR Program fields
            form.find('.nonfoir-start-date').val('');
            form.find('.nonfoir-end-date').val('');
            form.find('.nonfoir-upload-section').show();
            form.find('.nonfoir-result-section').hide();
            form.find('.nonfoir-processing-section').hide();

            // Clear uploaded file indicators
            form.find('.nonfoir-file-status').empty();
            form.find('input[name="nonfoirUploadStatus"]').val('');

            // Reset any Non-FOIR response data
            form.find('.nonfoir-response-data').empty();
            form.find('.nonfoir-monthly-income').val('');
            break;

        case 'IMPUTED':
            // Reset Imputed Income Program fields
            form.find('.imputed-result-section').hide();
            form.find('.no-imputed-message').show();
            form.find('.imputed-loading-section').hide();

            // Clear imputed income calculated values
            form.find('.imputed-monthly-income').val('');
            form.find('.imputed-annual-income').val('');
            form.find('.imputed-calculation-details').empty();

            // Reset calculate button state
            form.find('.calculate-imputed-income').prop('disabled', false);
            form.find('.recalculate-imputed').prop('disabled', false);

            // Clear any imputed income response data
            form.find('.imputed-response-data').empty();
            break;

        case '60/40':
            // Reset 60/40 Program fields
            // Clear file upload input
            form.find('.doc-6040-upload').val('');

            // Remove validation classes
            form.find('.doc-6040-upload').removeClass('is-invalid is-valid');

            // Hide and clear status displays
            form.find('.doc-6040-status').hide();
            form.find('.doc-6040-filename').text('');
            form.find('.doc-6040-filesize').text('');

            // Hide both alert sections (will be shown appropriately when section is displayed)
            form.find('.doc-6040-uploaded-alert').hide();
            form.find('.doc-6040-info-alert').hide();

            console.log('60/40 program fields reset');
            break;
    }
}