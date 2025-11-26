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
            $(this).closest('.det').find('.sixtyfortydiv').hide();
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
                        $(this).closest('.det').find('.sixtyfortydiv').hide();

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
                        } else if (selected === '60/40') {
                            $(this).closest('.det').find('.sixtyfortydiv').show();
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
    $('#loanbody').on('input', '.monthly-salary-nr, .total-remittance', function () {
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
    // Calculate total income when gross salary amount changes
    $('#loanbody').on('input', '.payslip-gross-amount', function (e) {
        calculateTotalIncome($(this));
    });
    $('#loanbody').on('input', '.add-backs-obligations', function () {
        calculateFinalAMI($(this));
    });
    $('#loanbody').on('click', '.fd-account-validate', function () {
        console.log("========== Fetching FD Details ==========");
        fetchFDDetails($(this));
    });

    // Delete FD account button handler
    $('#loanbody').on('click', '.delete-fd-btn', function (e) {
        e.preventDefault();
        console.log("========== Delete FD Account ==========");

        var ino = $(this).data('ino');
        var row = $(this).closest('tr');
        var $fdResponse = $(this).closest('.fdResponse');
        var detElement = $(this).closest('.det');

        // Confirm deletion
        Swal.fire({
            title: 'Delete FD Account?',
            text: 'Are you sure you want to remove this FD account from the program?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Yes, delete it!',
            cancelButtonText: 'Cancel'
        }).then((result) => {
            if (result.isConfirmed) {
                showLoader();

                $.ajax({
                    url: 'api/deleteFDAccount',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({ino: ino}),
                    success: function (response) {
                        console.log("FD account deleted successfully");

                        // Remove row from table
                        row.remove();

                        // Recalculate total balance based on remaining visible rows
                        updateTotalBalance($fdResponse);

                        // Update row count
                        var remainingRows = $fdResponse.find('tbody tr').length;
                        detElement.find('.fd-count').text(remainingRows);

                        hideLoader();

                        Swal.fire({
                            icon: 'success',
                            title: 'Deleted!',
                            text: 'FD account has been removed. Eligibility recalculated.',
                            timer: 2000,
                            showConfirmButton: false
                        });
                    },
                    error: function (xhr, status, error) {
                        console.error('Error deleting FD account:', error);
                        hideLoader();

                        Swal.fire({
                            icon: 'error',
                            title: 'Delete Failed',
                            text: xhr.responseText || 'Failed to delete FD account. Please try again.',
                            confirmButtonText: 'OK'
                        });
                    }
                });
            }
        });
    });

    $('#loanbody').on('click', '.calculate-imputed-income, .recalculate-imputed', function () {
        console.log("========== Calculating Imputed Income ==========");
        calculateImputedIncome($(this));
    });
    $('#loanbody').on('click', '.resetSurrogateBtnEdit, .resetSurrogateBtnReview', function (e) {
        resetSurrogate($(this));
    });
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

    $('#loanbody').on('change', '.bank-select', function (e) {
        const idx = parseInt($(this).data('idx'));
        statementsData[idx].bankCode = $(this).val() || '';
        updateCoverageUI($(this));
    });

    $('#loanbody').on('click', '.addStatementBtn', function (e) {
        addStatement(false, false, $(this));
        renderStatements($(this));
    });
    $('#loanbody').on('click', '.review-selections-btn', function (e) {

        $(this).closest('.det').find('.surrogate-edit-mode').slideUp(400, function () {
            buildSummary($(this));
            $(this).closest('.det').find('.surrogate-review-mode').slideDown(400);
        });
    });
    $('#loanbody').on('click', '.backToEditBtn', function (e) {
        $(this).closest('.det').find('.surrogate-review-mode').slideUp(400, function () {
            $(this).closest('.det').find('.surrogate-edit-mode').slideDown(400);
        });
    });

    $('#loanbody').on('click', '.review-upload-btn', function (e) {
        e.preventDefault();
        handleSurrogateStatementUpload($(this));
    });
    $('#loanbody').on('click', '.btn-calc-info', function (e) {
        e.preventDefault();
        showABBCalculationModal($(this));
    });


});