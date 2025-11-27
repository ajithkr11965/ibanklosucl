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
        case 'LOANFD':
            resetProgramFields(triggerElement);

            // Show FD div
            form = triggerElement.closest('.det');
            form.find('.fddiv').show();

            console.log("Loading LOANFD program details from database");

            // If FD details are saved in database, restore them
            if (programDetails.vehicleLoanFDList) {
                updateFDDetailsFromDatabase(programDetails, triggerElement);
            } else {
                console.log("No saved FD data found");
            }
            break;
            case 'NONFOIR':
            resetProgramFields(triggerElement);

            // Show Non-FOIR div
            form = triggerElement.closest('.det');
            form.find('.nonfoirdiv').show();

            // Initialize date pickers
            handleNonFOIRSelection(form.find('.nonfoirdiv'));

            // If Non-FOIR data is saved, restore it
            if (programDetails.nonFoirDetails) {
                // Restore date range
                if (programDetails.nonFoirDetails.startDate) {
                    form.find('.nonfoir-start-date').val(programDetails.nonFoirDetails.startDate);
                }
                if (programDetails.nonFoirDetails.endDate) {
                    form.find('.nonfoir-end-date').val(programDetails.nonFoirDetails.endDate);
                }

                // If statement was already uploaded and processed, show result
                if (programDetails.nonFoirDetails.uploadStatus === 'PROCESSED') {
                    form.find('.nonfoir-upload-section').hide();
                    form.find('.nonfoir-result-section').show();

                    // Restore monthly income
                    if (programDetails.nonFoirDetails.monthlyIncome) {
                        form.find('.nonfoir-monthly-income').val(programDetails.nonFoirDetails.monthlyIncome);
                    }

                    // Show file uploaded indicator
                    form.find('.nonfoir-file-status').html('<span class="text-success">✓ Statement Processed</span>');
                }
            }
            break;

        case 'IMPUTED':
            resetProgramFields(triggerElement);
            // Show Imputed Income div
            form = triggerElement.closest('.det');
            form.find('.imputeddiv').show();
            // Initialize imputed income section
            handleImputedIncomeSelection(form.find('.imputeddiv'));
            // If imputed income was already calculated, restore it
            if (programDetails.imputedDetails) {
                if (programDetails.imputedDetails.calculationStatus === 'COMPLETED') {
                    // Hide the "no imputed" message and show results
                    form.find('.no-imputed-message').hide();
                    form.find('.imputed-result-section').show();

                    // Restore calculated values
                    if (programDetails.imputedDetails.monthlyIncome) {
                        form.find('.imputed-monthly-income').val(programDetails.imputedDetails.monthlyIncome);
                    }
                    if (programDetails.imputedDetails.annualIncome) {
                        form.find('.imputed-annual-income').val(programDetails.imputedDetails.annualIncome);
                    }
                    // If there's response data, display it
                    if (programDetails.imputedDetails.responseData) {
                        updateImputedIncomeDisplay(
                            JSON.parse(programDetails.imputedDetails.responseData),
                            form
                        );
                    }
                }
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

        // Load NRI remittance details if available
        if (programDetails.nriRemittanceDetails) {
            console.log("Loading NRI remittance details from saved data");

            // Load monthly salary
            if (programDetails.nriRemittanceDetails.monthlySalary) {
                form.find('.monthly-salary-nr').val(programDetails.nriRemittanceDetails.monthlySalary);
            }

            // Load averages
            if (programDetails.nriRemittanceDetails.avgTotalRemittance) {
                form.find('.avg-total-remittance').val(programDetails.nriRemittanceDetails.avgTotalRemittance);
            }
            if (programDetails.nriRemittanceDetails.avgBulkRemittance) {
                form.find('.avg-bulk-remittance').val(programDetails.nriRemittanceDetails.avgBulkRemittance);
            }
            if (programDetails.nriRemittanceDetails.avgNetRemittance) {
                form.find('.avg-net-remittance').val(programDetails.nriRemittanceDetails.avgNetRemittance);

                // Update calculated monthly gross income
                form.find('.calculated-monthly-gross-income').text(programDetails.nriRemittanceDetails.avgNetRemittance);
                form.find('.monthly-gross-income-nr-hidden').val(programDetails.nriRemittanceDetails.avgNetRemittance);
            }

            // Load 12 months remittance data
            if (programDetails.nriRemittanceDetails.remittanceMonths &&
                programDetails.nriRemittanceDetails.remittanceMonths.length > 0) {

                programDetails.nriRemittanceDetails.remittanceMonths.forEach(function(monthData) {
                    // Find the row with matching month
                    var row = form.find('.remittance-row[data-month="' + monthData.monthYear + '"]');
                    if (row.length > 0) {
                        row.find('.total-remittance').val(monthData.totalRemittance || '');
                        row.find('.bulk-remittance').val(monthData.bulkRemittance || '');
                        row.find('.net-remittance').val(monthData.netRemittance || '');
                    }
                });
            }
        }
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
        // Support both 'docType' (camelCase) and 'doctype' (lowercase) from backend
        var documentType = programDetails.docType || programDetails.doctype;
        if (documentType) {
            form.find('input[name="docTypeSelection"][value="' + documentType + '"]').prop('checked', true);

            form.find('.itr-section, .form16-section, .payslip-section').hide();

            if (documentType === 'ITR') {
                form.find('.itr-section').show();
                // Handle ITR-specific data if needed
                if (programDetails.itrDetails) {
                    processITRDataForDisplay(programDetails.itrDetails, form);
                }
            } else if (documentType === 'FORM16') {
                form.find('.form16-section').show();
                form.find('.form16-upd').closest('.compact-form-group').find('.text-success').remove();
                form.find('.form16-upd').closest('.compact-form-group').append('<span class="text-success ms-2">✓ File uploaded</span>');
                if (programDetails.monthlyGrossIncome != null) {
                    form.find('.form16-monthly-income').val(programDetails.monthlyGrossIncome);
                }
                // Handle Form16-specific data if needed
            } else if (documentType === 'PAYSLIP') {
                form.find('.payslip-section').show();

                // Clear existing payslip rows
                form.find('.payslip-table-body').empty();

                // Add payslip rows from data
                // Support both payslipDetails and vehicleLoanProgramSalaryList from backend
                var payslipData = programDetails.payslipDetails || programDetails.vehicleLoanProgramSalaryList;
                if (payslipData && payslipData.length > 0) {
                    payslipData.forEach(function (payslip) {
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
// Helper function to get bank name from bank code
function getBankNameFromCode(bankCode) {
    // Find the bank select dropdown (any instance will have the same options)
    const bankSelect = $('.bank-select').first();

    if (bankSelect.length > 0) {
        // Find the option with matching value
        const option = bankSelect.find(`option[value="${bankCode}"]`);
        if (option.length > 0) {
            return option.text();
        }
    }

    // Fallback to bank code if name not found
    return bankCode;
}

function updateCoverageUI(detElement) {
    // Use the new per-bank validation logic
    const validation = validateBankStatementCoverage();
    const { bankValidation, hasValidBank, totalBanks } = validation;

    // For backward compatibility, still show total months covered (legacy)
    const total = calcCoverageSoFar();
    detElement.closest('.det').find('.months-covered').text(total);

    // Build detailed per-bank coverage message
    let detailedMessage = '';
    let hasErrors = false;

    if (totalBanks === 0) {
        detailedMessage = '<div class="alert alert-warning mb-2"><i class="ph-warning me-2"></i>No bank statements added yet</div>';
        hasErrors = true;
    } else {
        // Show per-bank validation results
        detailedMessage = '<div class="bank-coverage-details mt-2">';

        Object.keys(bankValidation).forEach(bankCode => {
            const bankInfo = bankValidation[bankCode];
            const bankName = getBankNameFromCode(bankCode);
            let badgeClass = 'bg-success';
            let iconClass = 'ph-check-circle';

            if (bankInfo.status === 'valid') {
                badgeClass = 'bg-success';
                iconClass = 'ph-check-circle';
            } else if (bankInfo.status === 'incomplete') {
                badgeClass = 'bg-warning';
                iconClass = 'ph-warning';
                hasErrors = true;
            } else if (bankInfo.status === 'excess') {
                badgeClass = 'bg-danger';
                iconClass = 'ph-x-circle';
                hasErrors = true;
            } else if (bankInfo.status === 'gaps') {
                badgeClass = 'bg-danger';
                iconClass = 'ph-x-circle';
                hasErrors = true;
            }

            detailedMessage += `
                <div class="d-flex align-items-center mb-2 p-2 border rounded ${bankInfo.status === 'valid' ? 'border-success bg-success-subtle' : 'border-warning bg-warning-subtle'}">
                    <span class="badge ${badgeClass} me-2"><i class="${iconClass} me-1"></i>${bankName}</span>
                    <span class="flex-grow-1">${bankInfo.message}</span>
                </div>
            `;
        });

        detailedMessage += '</div>';

        // Add overall status message
        if (hasValidBank) {
            detailedMessage = '<div class="alert alert-success mb-2"><i class="ph-check-circle me-2"></i><strong>Valid:</strong> At least one bank has exactly 12 consecutive months</div>' + detailedMessage;
        } else {
            detailedMessage = '<div class="alert alert-danger mb-2"><i class="ph-x-circle me-2"></i><strong>Invalid:</strong> No bank has exactly 12 consecutive months</div>' + detailedMessage;
        }
    }

    // Update the coverage status in the UI
    const coverageDetailsContainer = detElement.closest('.det').find('.coverage-details-container');
    if (coverageDetailsContainer.length > 0) {
        coverageDetailsContainer.html(detailedMessage);
    } else {
        // Fallback: Create the container if it doesn't exist
        const coverageStatusElement = detElement.closest('.det').find('.coverage-status');
        if (coverageStatusElement.length > 0) {
            // Insert detailed message after coverage status
            if (coverageStatusElement.next('.coverage-details-container').length === 0) {
                coverageStatusElement.after('<div class="coverage-details-container mt-2"></div>');
            }
            detElement.closest('.det').find('.coverage-details-container').html(detailedMessage);
        }
    }

    // Update main coverage status (for backward compatibility)
    if (hasValidBank) {
        detElement.closest('.det').find('.coverage-status').removeClass('coverage-incomplete');
        detElement.closest('.det').find('.coverage-status').addClass('coverage-complete');
        detElement.closest('.det').find('.coverage-status').text('(complete)');
    } else {
        detElement.closest('.det').find('.coverage-status').removeClass('coverage-complete');
        detElement.closest('.det').find('.coverage-status').addClass('coverage-incomplete');
        detElement.closest('.det').find('.coverage-status').text('(incomplete)');
    }

    // All bank codes chosen?
    let nonEmptyBank = statementsData.every(s => s.bankCode);
    // Must have at least one valid bank with exactly 12 consecutive months
    const coverageOk = hasValidBank;
    // Must be 1..3 statements
    let canReview = coverageOk && nonEmptyBank &&
        (statementsData.length >= 1 && statementsData.length <= 3);

    detElement.closest('.det').find('.review-selections-btn').prop('disabled', !canReview);
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
                <input type="number" class="form-control payslip-gross-amount" placeholder="Gross Salary">
            </td>
            <td>
                <input type="number" class="form-control payslip-amount" placeholder="Net Salary">
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
function addPayslipRowWithData(triggerElement, payslipData) {
    var tableBody = triggerElement.find('.salaried-section').find('.payslip-table-body');

    // Get current date info for reference
    var currentDate = new Date();
    var currentYear = currentDate.getFullYear();
    var prevYear = currentYear - 1;
    var nextYear = currentYear + 1;

    // Support both frontend and backend property names
    var month = payslipData.payslipMonth || payslipData.salMonth;
    var year = payslipData.payslipYear || payslipData.salYear;
    var amount = payslipData.payslipAmount || payslipData.salAmount;
    var grossAmount = payslipData.payslipGrossAmount || payslipData.salGrossAmount;
    var payslipId = payslipData.payslipId || payslipData.ino;
    var fileRef = payslipData.fileRef || payslipData.salaryDoc;

    var fileUploaded = payslipData.fileUploaded || (fileRef && fileRef.length > 0);

    // Create a new row with the payslip data
    var rowHtml = `
        <tr data-payslip-id="${payslipId || ''}">
            <td>
                <select class="form-control payslip-month">
                    <option value="">Select Month</option>
                    <option value="1" ${month == "1" || month == 1 ? 'selected' : ''}>January</option>
                    <option value="2" ${month == "2" || month == 2 ? 'selected' : ''}>February</option>
                    <option value="3" ${month == "3" || month == 3 ? 'selected' : ''}>March</option>
                    <option value="4" ${month == "4" || month == 4 ? 'selected' : ''}>April</option>
                    <option value="5" ${month == "5" || month == 5 ? 'selected' : ''}>May</option>
                    <option value="6" ${month == "6" || month == 6 ? 'selected' : ''}>June</option>
                    <option value="7" ${month == "7" || month == 7 ? 'selected' : ''}>July</option>
                    <option value="8" ${month == "8" || month == 8 ? 'selected' : ''}>August</option>
                    <option value="9" ${month == "9" || month == 9 ? 'selected' : ''}>September</option>
                    <option value="10" ${month == "10" || month == 10 ? 'selected' : ''}>October</option>
                    <option value="11" ${month == "11" || month == 11 ? 'selected' : ''}>November</option>
                    <option value="12" ${month == "12" || month == 12 ? 'selected' : ''}>December</option>
                </select>
            </td>
            <td>
                <select class="form-control payslip-year">
                    <option value="">Select Year</option>
                    <option value="${prevYear}" ${year == prevYear ? 'selected' : ''}>${prevYear}</option>
                    <option value="${currentYear}" ${year == currentYear ? 'selected' : ''}>${currentYear}</option>
                    <option value="${nextYear}" ${year == nextYear ? 'selected' : ''}>${nextYear}</option>
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
                <input type="number" class="form-control payslip-gross-amount" placeholder="Gross Salary" value="${grossAmount || ''}">
            </td>
            <td>
                <input type="number" class="form-control payslip-amount" placeholder="Net Salary" value="${amount || ''}">
            </td>
            <td>
                <button type="button" class="btn btn-danger btn-sm delete-payslip-row">
                    <i class="ph-trash"></i>
                </button>
            </td>
        </tr>
    `;

    tableBody.append(rowHtml);

    // Get the newly added row
    var newRow = tableBody.find('tr').last();

    // Initialize select2 for the new dropdown if it's being used in your project
    if ($.fn.select2) {
        var monthSelect = newRow.find('.payslip-month');
        var yearSelect = newRow.find('.payslip-year');

        monthSelect.select2({
            templateResult: formatState,
            templateSelection: formatState
        });

        yearSelect.select2({
            templateResult: formatState,
            templateSelection: formatState
        });

        // Set values after initialization using Select2's val() method
        if (month) {
            monthSelect.val(month.toString()).trigger('change');
        }
        if (year) {
            yearSelect.val(year.toString()).trigger('change');
        }
    }

    // Ensure input values are set programmatically
    if (grossAmount) {
        newRow.find('.payslip-gross-amount').val(grossAmount);
    }
    if (amount) {
        newRow.find('.payslip-amount').val(amount);
    }
}
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

/************************************************************
 * NRI Remittance Calculations
 ************************************************************/

// Function to calculate net remittance and averages for NRI applicants
function calculateRemittances(detElement) {
    console.log("Calculating remittances for NRI applicant");
    
    var totalRemittanceSum = 0;
    var bulkRemittanceSum = 0;
    var netRemittanceSum = 0;
    var validMonthCount = 0;
    
    // Iterate through all remittance rows
    detElement.find(".remittance-row").each(function() {
        var row = $(this);
        var totalRemittance = parseFloat(row.find(".total-remittance").val()) || 0;
        var bulkRemittance = parseFloat(row.find(".bulk-remittance").val()) || 0;
        
        // Calculate net remittance (Total - Bulk)
        var netRemittance = totalRemittance - bulkRemittance;
        
        // Update the net remittance field
        row.find(".net-remittance").val(netRemittance.toFixed(2));
        
        // Add to sums
        totalRemittanceSum += totalRemittance;
        bulkRemittanceSum += bulkRemittance;
        netRemittanceSum += netRemittance;
        
        // Count valid months (where total remittance is entered)
        if (totalRemittance > 0) {
            validMonthCount++;
        }
    });
    
    // Calculate averages
    var avgTotalRemittance = validMonthCount > 0 ? totalRemittanceSum / validMonthCount : 0;
    var avgBulkRemittance = validMonthCount > 0 ? bulkRemittanceSum / validMonthCount : 0;
    var avgNetRemittance = validMonthCount > 0 ? netRemittanceSum / validMonthCount : 0;
    
    // Update average fields
    detElement.find(".avg-total-remittance").val(avgTotalRemittance.toFixed(2));
    detElement.find(".avg-bulk-remittance").val(avgBulkRemittance.toFixed(2));
    detElement.find(".avg-net-remittance").val(avgNetRemittance.toFixed(2));
    
    // Update the calculated monthly gross income display
    detElement.find(".calculated-monthly-gross-income").text(avgNetRemittance.toFixed(2));
    detElement.find(".monthly-gross-income-nr-hidden").val(avgNetRemittance.toFixed(2));
    
    console.log("Remittance calculation complete:", {
        validMonths: validMonthCount,
        avgTotal: avgTotalRemittance.toFixed(2),
        avgBulk: avgBulkRemittance.toFixed(2),
        avgNet: avgNetRemittance.toFixed(2)
    });
}

// Initialize remittance calculation handlers
$(document).ready(function() {
    console.log("Initializing NRI remittance handlers");
    
    // Handle total remittance input
    $(document).on("input", ".total-remittance", function() {
        var detElement = $(this).closest(".det");
        calculateRemittances(detElement);
    });
    
    // Handle bulk remittance input
    $(document).on("input", ".bulk-remittance", function() {
        var detElement = $(this).closest(".det");
        calculateRemittances(detElement);
    });
    
    // Calculate on page load if data exists
    $(".non-resident-income-section:visible").each(function() {
        var detElement = $(this).closest(".det");
        calculateRemittances(detElement);
    });
});

// Update total balance for FD accounts
function updateTotalBalance($fdResponse) {
    let totalBalance = 0;
    let fdcount = 0;
    let eligibleCount = 0;

    $fdResponse.find('tbody tr').each(function () {
        fdcount = fdcount + 1;

        // Column 9 is Available Balance in the JavaScript-generated table
        // (Column 8 has FSLD Adj, Column 9 has Available Balance)
        let availableBalanceText = $(this).find('td').eq(9).text().trim();
        let availableBalance = parseFloat(availableBalanceText.replace(/,/g, ''));

        // Column 10 is Eligible status
        let eligibleTxt = $(this).find('td').eq(10).text().trim();

        console.log("Row " + fdcount + ": Available Balance = '" + availableBalanceText + "' (" + availableBalance + "), Eligible = '" + eligibleTxt + "'");

        if (!isNaN(availableBalance) && eligibleTxt === "Yes") {
            totalBalance += availableBalance;
            eligibleCount++;
        }
    });

    console.log("Total FDs: " + fdcount + ", Eligible: " + eligibleCount + ", Total Balance: " + totalBalance);

    // Update the total available balance field in the current section
    $fdResponse.closest('.fdDetailsDiv').find('.totalavailBalance').val(totalBalance.toFixed(2));
    $fdResponse.closest('.fdDetailsDiv').find('.totalavailBalance-display').text(formatCurrencyINR(totalBalance));

    // Update the summary cards
    $fdResponse.closest('.fdDetailsDiv').find('.fd-count').text(fdcount);
    $fdResponse.closest('.fdDetailsDiv').find('.fd-eligible-count').text(eligibleCount);
    $fdResponse.closest('.fdDetailsDiv').find('.fd-loan-eligibility').text(formatCurrencyINR(totalBalance));
}

/**
 * Refresh FD details for all applicants by re-fetching from Finacle
 *
 * WARNING: This function triggers a FULL re-fetch from Finacle which:
 * 1. Marks ALL existing FD records as deleted in DB
 * 2. Fetches fresh data from Finacle
 * 3. Creates new FD records
 *
 * DO NOT use this after delete operations as it will bring back deleted FDs!
 * The delete operation already recalculates eligibility on the backend.
 *
 * Use only when you need to sync with latest Finacle data (e.g., initial fetch, manual refresh)
 */
function refreshFDDetailsForAllApplicants() {
    console.log("========== Refreshing FD Details for All Applicants ==========");
    console.warn("WARNING: This will re-fetch all FDs from Finacle!");

    $('#loanbody .tab-pane').each(function () {
        var $tabPane = $(this);
        var cifId = $tabPane.find('.generaldetails').find('.custID').val();

        console.log("Checking applicant with CIF: " + cifId);

        // Only refresh if customer ID exists and is valid (9 digits)
        if (cifId != null && cifId.length === 9) {
            var $fdButton = $tabPane.find('.fd-account-validate');

            // Only refresh if FD program is selected
            var selectedProgram = $tabPane.find('.programCode').val();

            if (selectedProgram === 'LOANFD' && $fdButton.length > 0) {
                console.log("Refreshing FD details for CIF: " + cifId);

                // Small delay to avoid overwhelming the server
                setTimeout(function() {
                    $fdButton.trigger('click');
                }, 300);
            }
        }
    });
}

/**
 * Update FD details from database when page loads
 * This populates the FD table with previously saved FD data
 */
function updateFDDetailsFromDatabase(programDetails, detElement) {
    console.log("========== Populating FD Details from Database ==========");

    var fdData = programDetails.vehicleLoanFDList;
    var depAmt = programDetails.depAmt;

    console.log("FD Data:", fdData);
    console.log("Deposit Amount:", depAmt);

    // Check if we have FD accounts
    if (!fdData || !fdData.fdAccounts || fdData.fdAccounts.length === 0) {
        console.log("No FD accounts found in database");
        detElement.find('.fdDetailsDiv').hide();
        detElement.find('.no-fd-message').show();
        return;
    }

    var fdAccounts = fdData.fdAccounts;
    var missingCifIds = fdData.missingCifIds || [];

    console.log("Number of FD accounts: " + fdAccounts.length);
    console.log("Missing CIF IDs: " + missingCifIds);

    // Build the FD table HTML
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

    var totalavailBalance = 0;
    var eligibleCount = 0;

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
                totalavailBalance += account.availbalance;
            } else {
                tableHtml += '<td><span class="badge bg-secondary">No</span></td>';
                tableHtml += '<td>-</td>';
            }

            tableHtml += '</tr>';
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

    // Display missing CIF IDs if any
    if (missingCifIds && missingCifIds.length > 0) {
        displayMissingCifIds(detElement, missingCifIds);
    } else {
        // Hide missing CIF alert if no missing IDs
        detElement.find('.missing-cif-alert-section').hide();
    }

    console.log("=== FD Details Loaded Successfully ===");
    console.log("Total FDs: " + fdAccounts.length);
    console.log("Eligible FDs: " + eligibleCount);
    console.log("Total Available Balance: ₹" + totalavailBalance.toFixed(2));
}

