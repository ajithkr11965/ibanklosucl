// Validation Logger for detecting and logging validation failures to the console
// This enhances the existing validation functions

// Helper function to log validation errors to console
function logValidationError(programType, fieldName, fieldValue, errorMessage) {
    console.log(
        "[VALIDATION ERROR] " +
        "PROGRAM: " + programType + " | " +
        "FIELD: " + fieldName + " | " +
        "VALUE: " + (fieldValue || "empty") + " | " +
        "ERROR: " + errorMessage
    );
}

function validateIncomeProgram(form) {
    var isValid = true;
    var validationErrors = [];

    console.log("=== VALIDATING INCOME PROGRAM ===");

    // Clear previous error indicators
    form.find('.is-invalid').removeClass('is-invalid');
    var residentialStatus = form.closest('.tab-pane').find('.generaldetails').find('.residentialStatus:checked').val()

    // Get the employment type
    var employmentType = form.find('input[name="empl_type"]').val();

    if (!employmentType) {
        alertmsg("Employment type not specified. Please complete Employment Details first.");
        logValidationError("INCOME", "empl_type", "", "Employment type not specified");
        isValid = false;
        console.log("=== VALIDATION FAILED: Employment type missing ===");
        return isValid;
    }

    console.log("Validating for employment type: " + employmentType);
    if (residentialStatus === 'N') {
        // Non-Resident - Monthly remittance validation (common for all employment types)
        console.log("Validating NON-RESIDENT remittance fields");

        // Validate remittance table rows
        var remittanceRows = form.find('.remittance-table-body tr');

        if (remittanceRows.length === 0) {
            alertmsg("Monthly remittance details are required for Non-Resident applicants.");
            logValidationError("INCOME", "remittance-rows", "0", "No remittance data found");
            isValid = false;
        } else {
            var totalRemittanceSum = 0;
            var netRemittanceSum = 0;
            var validMonthCount = 0;

            remittanceRows.each(function () {
                var row = $(this);
                var totalRemittance = row.find('.total-remittance').val();
                var bulkRemittance = row.find('.bulk-remittance').val() || '0';
                var netRemittance = row.find('.net-remittance').val();

                // Validate total remittance
                if (!totalRemittance || isNaN(parseFloat(totalRemittance))) {
                    row.find('.total-remittance').addClass('is-invalid');
                    logValidationError("INCOME", "total-remittance", totalRemittance || "", "Total remittance invalid for a month");
                    isValid = false;
                } else {
                    totalRemittanceSum += parseFloat(totalRemittance);
                    validMonthCount++;
                }

                // Validate bulk remittance (optional but must be valid if provided)
                if (bulkRemittance && isNaN(parseFloat(bulkRemittance))) {
                    row.find('.bulk-remittance').addClass('is-invalid');
                    logValidationError("INCOME", "bulk-remittance", bulkRemittance, "Bulk remittance invalid");
                    isValid = false;
                }

                // Validate net remittance calculation
                var calculatedNet = parseFloat(totalRemittance || 0) - parseFloat(bulkRemittance || 0);
                if (netRemittance && parseFloat(netRemittance) !== calculatedNet) {
                    row.find('.net-remittance').addClass('is-invalid');
                    logValidationError("INCOME", "net-remittance", netRemittance, "Net remittance calculation incorrect");
                    isValid = false;
                } else if (netRemittance) {
                    netRemittanceSum += parseFloat(netRemittance);
                }
            });

            // Validate averages
            if (validMonthCount < 12) {
                alertmsg("All 12 months of remittance data must be provided.");
                logValidationError("INCOME", "remittance-months", validMonthCount, "Less than 12 months of remittance data");
                isValid = false;
            }

            var avgTotalRemittance = validMonthCount > 0 ? totalRemittanceSum / validMonthCount : 0;
            var avgNetRemittance = validMonthCount > 0 ? netRemittanceSum / validMonthCount : 0;

            if (avgTotalRemittance <= 0) {
                form.find('.avg-total-remittance').addClass('is-invalid');
                alertmsg("Average total remittance must be greater than zero.");
                logValidationError("INCOME", "avg-total-remittance", avgTotalRemittance, "Average total remittance is zero or negative");
                isValid = false;
            }

            if (avgNetRemittance <= 0) {
                form.find('.avg-net-remittance').addClass('is-invalid');
                alertmsg("Average net remittance must be greater than zero.");
                logValidationError("INCOME", "avg-net-remittance", avgNetRemittance, "Average net remittance is zero or negative");
                isValid = false;
            }

            console.log("Average Total Remittance: " + avgTotalRemittance);
            console.log("Average Net Remittance: " + avgNetRemittance);
        }
    } else if (residentialStatus === 'R') {
        // Employment type specific validations
        if (employmentType === 'SALARIED') {
            console.log("Validating SALARIED specific fields");

            // Validate document type selection
            var docTypeSelection = form.find('input[name="docTypeSelection"]:checked').val();

            if (!docTypeSelection) {
                form.find('.doc-selector').addClass('is-invalid');
                logValidationError("INCOME", "docTypeSelection", "", "Document type not selected for SALARIED employment");
                isValid = false;
            } else {
                console.log("Document type selected: " + docTypeSelection);

                // Document type specific validations
                if (docTypeSelection === 'ITR') {
                    // Validate ITR
                    var itrMonthlyGross = form.find('.itr-monthly-gross').val();
                    var itrResponse = form.find('.itr-response').html();

                    if (!itrMonthlyGross || itrMonthlyGross === '0') {
                        form.find('.itr-monthly-gross').addClass('is-invalid');
                        alertmsg("ITR document has not been processed correctly. Monthly Gross Income is missing.");
                        logValidationError("INCOME", "itr-monthly-gross", itrMonthlyGross || "", "ITR monthly gross income missing or zero");
                        isValid = false;
                    }

                    if (!itrResponse || itrResponse.trim() === '') {
                        alertmsg("ITR document has not been uploaded or processed. Please upload and process the ITR document.");
                        logValidationError("INCOME", "itr-response", "", "ITR document not uploaded/processed");
                        isValid = false;
                    }
                } else if (docTypeSelection === 'FORM16') {
                    // Validate Form 16
                    var form16File = form.find('.form16-upd').val();
                    var hasForm16File = form16File || form.find('.form16-upd').closest('.compact-form-group').find('.text-success').length > 0;

                    if (!hasForm16File) {
                        form.find('.form16-upd').addClass('is-invalid');
                        alertmsg("Please upload Form 16 document.");
                        logValidationError("INCOME", "form16-upd", "", "Form 16 document not uploaded");
                        isValid = false;
                    }
                } else if (docTypeSelection === 'PAYSLIP') {
                    var payslipRows = form.find('.payslip-table-body tr');
                    // Check if at least one payslip row exists
                    if (payslipRows.length === 0) {
                        alertmsg("At least one payslip is required.");
                        isValid = false;
                    }
                    // Validate each payslip row
                    payslipRows.each(function () {
                        var row = $(this);
                        var month = row.find('.payslip-month').val();
                        var year = row.find('.payslip-year').val();
                        var amount = row.find('.payslip-amount').val();
                        var fileInput = row.find('.payslip-file');
                        var fileStatus = row.find('.payslip-file-status').val();
                        var hasFile = fileInput[0].files.length > 0 || fileStatus === 'uploaded';
                        // Check required fields
                        if (!month) {
                            row.find('.payslip-month').addClass('is-invalid');
                            isValid = false;
                        }
                        if (!year) {
                            row.find('.payslip-year').addClass('is-invalid');
                            isValid = false;
                        }
                        if (!amount || parseFloat(amount) <= 0) {
                            row.find('.payslip-amount').addClass('is-invalid');
                            isValid = false;
                        }
                        // Only require file upload for new payslip entries (no payslip-id)
                        if (!hasFile && !row.data('payslip-id')) {
                            fileInput.addClass('is-invalid');
                            isValid = false;
                        }
                    });
                    // Check total income
                    var totalIncome = parseFloat(form.find('.total-income').val());
                    if (isNaN(totalIncome) || totalIncome <= 0) {
                        form.find('.total-income').addClass('is-invalid');
                        isValid = false;
                    }
                }
            }
        } else if (employmentType === 'PENSIONER') {
            console.log("Validating PENSIONER specific fields");

            // Validate ITR for Pensioner
            var pensionerItrResponse = form.find('.pensioner-section .itr-response').html();

            if (!pensionerItrResponse || pensionerItrResponse.trim() === '') {
                alertmsg("ITR document has not been uploaded or processed for Pensioner. Please upload and process the ITR document.");
                logValidationError("INCOME", "pensioner-itr-response", "", "Pensioner ITR document not uploaded/processed");
                isValid = false;
            }

        } else if (employmentType === 'SEP' || employmentType === 'SENP') {
            console.log("Validating SEPSENP specific fields");

            // Validate ITR for Self-Employed
            var sepSenpItrResponse = form.find('.sepsenp-section .itr-response').html();

            if (!sepSenpItrResponse || sepSenpItrResponse.trim() === '') {
                alertmsg("ITR document has not been uploaded or processed for Self-Employed. Please upload and process the ITR document.");
                logValidationError("INCOME", "sepsenp-itr-response", "", "Self-Employed ITR document not uploaded/processed");
                isValid = false;
            }

            // Validate monthly gross income
            var sepSenpMonthlyGross = form.find('input[name="sepSenpMonthlyGross"]').val();

            if (!sepSenpMonthlyGross || parseFloat(sepSenpMonthlyGross) <= 0) {
                form.find('input[name="sepSenpMonthlyGross"]').addClass('is-invalid');
                alertmsg("Monthly Gross Income is missing or invalid for Self-Employed applicant.");
                logValidationError("INCOME", "sepSenpMonthlyGross", sepSenpMonthlyGross || "", "Self-Employed monthly gross income missing or invalid");
                isValid = false;
            }
        } else if (employmentType === 'AGRICULTURIST') {
            console.log("Validating AGRICULTURIST specific fields");

            // Validate ITR for Agriculturist
            var agriculturistItrResponse = form.find('.agriculturist-section .itr-response').html();

            if (!agriculturistItrResponse || agriculturistItrResponse.trim() === '') {
                alertmsg("ITR document has not been uploaded or processed for Agriculturist. Please upload and process the ITR document.");
                logValidationError("INCOME", "agriculturist-itr-response", "", "Agriculturist ITR document not uploaded/processed");
                isValid = false;
            }

            // Validate monthly gross income
            var agriculturistMonthlyGross = form.find('input[name="agriculturistMonthlyGross"]').val();

            if (!agriculturistMonthlyGross || parseFloat(agriculturistMonthlyGross) <= 0) {
                form.find('input[name="agriculturistMonthlyGross"]').addClass('is-invalid');
                alertmsg("Monthly Gross Income is missing or invalid for Agriculturist applicant.");
                logValidationError("INCOME", "agriculturistMonthlyGross", agriculturistMonthlyGross || "", "Agriculturist monthly gross income missing or invalid");
                isValid = false;
            }
        }
    } // Income Resident validation ends here

    var finalAMI = form.find('.final-ami:visible').val();
    if (finalAMI === undefined || finalAMI === null || finalAMI === '') {
        // If monthly gross income and add backs are valid but final AMI is missing, try to calculate it
        var monthlyGrossIncome = null;
        // Find the appropriate monthly gross income field based on employment type and document type
        if(residentialStatus==="R") {
            if (employmentType === 'SALARIED') {
                var docTypeSelection = form.find('input[name="docTypeSelection"]:checked').val();
                if (docTypeSelection === 'ITR') {
                    monthlyGrossIncome = parseFloat(form.find('.itr-monthly-gross').val() || '0');
                } else if (docTypeSelection === 'PAYSLIP') {
                    monthlyGrossIncome = parseFloat(form.find('.avg-monthly-income').val() || '0');
                }
            } else if (employmentType === 'PENSIONER') {
                monthlyGrossIncome = parseFloat(form.find('input[name="pensionerMonthlyGross"]').val() || '0');
            } else if (employmentType === 'SEP' || employmentType === 'SENP') {
                monthlyGrossIncome = parseFloat(form.find('input[name="sepSenpMonthlyGross"]').val() || '0');
            } else if (employmentType === 'AGRICULTURIST') {
                monthlyGrossIncome = parseFloat(form.find('input[name="agriculturistMonthlyGross"]').val() || '0');
            }
        } else if (residentialStatus==="N") {
            monthlyGrossIncome = avgNetRemittance;
        }

        if (monthlyGrossIncome !== null && !isNaN(monthlyGrossIncome)) {
            // Calculate final AMI
            var calculatedFinalAMI = monthlyGrossIncome ;
            form.find('.final-ami:visible').val(calculatedFinalAMI.toFixed(2));
            console.log("Final AMI calculated automatically:", calculatedFinalAMI.toFixed(2));
        } else {
            form.find('.final-ami:visible').addClass('is-invalid');
            logValidationError("INCOME", "final-ami", "", "Final Monthly Income for Eligibility is missing");
            isValid = false;
        }
    }

    if (!isValid) {
        alertmsg("Please fill in all required fields before saving.");
        console.log("=== INCOME PROGRAM VALIDATION FAILED ===");
    } else {
        console.log("=== INCOME PROGRAM VALIDATION PASSED ===");
    }

    return isValid;
}

// Validation for Surrogate Program
function validateSurrogateProgram(form) {
    var isValid = true;

    console.log("=== VALIDATING SURROGATE PROGRAM ===");

    // Clear previous error indicators
    form.find('.is-invalid').removeClass('is-invalid');

    // Determine which mode we're in (edit or review)
    var isEditMode = form.find('.surrogate-edit-mode').is(':visible');
    var isReviewMode = form.find('.surrogate-review-mode').is(':visible');

    console.log("Surrogate validation mode: " + (isEditMode ? "Edit" : (isReviewMode ? "Review" : "Unknown")));

    // Validate if bank statements cover full 12 months
    var monthsCovered = parseInt(form.find('.months-covered').text());

    if (monthsCovered !== 12) {
        alertmsg("Bank statements must cover exactly 12 months. Currently covering: " + monthsCovered + " months.");
        logValidationError("SURROGATE", "months-covered", monthsCovered, "Bank statements must cover exactly 12 months");
        isValid = false;
    }

    // Check if any bank statements have been added
    var statementCount = form.find('.bank-statement-container').children().length;

    if (statementCount === 0) {
        alertmsg("Please add at least one bank statement.");
        logValidationError("SURROGATE", "bank-statement-count", "0", "No bank statements added");
        isValid = false;
    }

    // Validate statements data from the global statementsData array
    if (typeof statementsData !== 'undefined' && statementsData.length > 0) {
        // Validate each statement has a bank selected
        for (var i = 0; i < statementsData.length; i++) {
            var statement = statementsData[i];
            if (!statement.bankCode) {
                var bankSelect = form.find(`.bank-select[data-idx="${i}"]`);
                bankSelect.addClass('is-invalid');
                logValidationError("SURROGATE", `bank-code-${i + 1}`, "", `Bank not selected for statement ${i + 1}`);
                isValid = false;
            }

            if (!statement.startDate) {
                logValidationError("SURROGATE", `start-date-${i + 1}`, "", `Start date not selected for statement ${i + 1}`);
                isValid = false;
            }

            if (!statement.endDate) {
                logValidationError("SURROGATE", `end-date-${i + 1}`, "", `End date not selected for statement ${i + 1}`);
                isValid = false;
            }

            // Check if statement is uploaded (required in review mode)
            if (isReviewMode && !statement.uploaded) {
                logValidationError("SURROGATE", `statement-${i + 1}-uploaded`, "false", `Statement ${i + 1} is not uploaded`);
                isValid = false;
            }
        }
    }

    // In review mode, make sure all statements are processed
    if (isReviewMode) {
        // Check if BSA status is available for each uploaded statement
        var allUploaded = statementsData.every(s => s.uploaded);
        if (!allUploaded) {
            alertmsg("All statements must be uploaded before proceeding.");
            logValidationError("SURROGATE", "all-statements-uploaded", "false", "Not all statements are uploaded");
            isValid = false;
        }

        // Validate BSA processed status for each surrogate statement
        // Look for presence of BSA responses
        if (statementsData.length >= 1) {
            var bsaResponse1 = form.find('.surrogate-bsa-status[data-response-type="SURROGATE-1"] .bsaResponse').html();
            if (!bsaResponse1 || bsaResponse1.trim() === '') {
                logValidationError("SURROGATE", "surrogate-bsa-status-1", "", "First surrogate statement not processed");
                isValid = false;
            }
        }

        if (statementsData.length >= 2) {
            var bsaResponse2 = form.find('.surrogate-bsa-status[data-response-type="SURROGATE-2"] .bsaResponse').html();
            if (!bsaResponse2 || bsaResponse2.trim() === '') {
                logValidationError("SURROGATE", "surrogate-bsa-status-2", "", "Second surrogate statement not processed");
                isValid = false;
            }
        }

        if (statementsData.length >= 3) {
            var bsaResponse3 = form.find('.surrogate-bsa-status[data-response-type="SURROGATE-3"] .bsaResponse').html();
            if (!bsaResponse3 || bsaResponse3.trim() === '') {
                logValidationError("SURROGATE", "surrogate-bsa-status-3", "", "Third surrogate statement not processed");
                isValid = false;
            }
        }
    }

    // Validate ABB amount (required in both modes)
    var abbAmount = form.find('.abb-amount').val();

    if (!abbAmount || parseFloat(abbAmount) <= 0) {
        form.find('.abb-amount').addClass('is-invalid');
        logValidationError("SURROGATE", "abb-amount", abbAmount || "", "Average Bank Balance amount missing or invalid");
        isValid = false;
    }

    // Validate property owner question
    var propertyOwner = form.find('input[name="propertyOwner"]:checked').val();

    if (!propertyOwner) {
        form.find('input[name="propertyOwner"]').closest('.form-check-input').addClass('is-invalid');
        logValidationError("SURROGATE", "propertyOwner", "", "Property owner question not answered");
        isValid = false;
    }

    if (!isValid) {
        alertmsg("Please fill in all required fields before saving.");
        console.log("=== SURROGATE PROGRAM VALIDATION FAILED ===");
    } else {
        console.log("=== SURROGATE PROGRAM VALIDATION PASSED ===");
    }

    return isValid;
}

function validateNonFoirProgram(form) {
    var isValid = true;

    console.log("=== VALIDATING NONFOIR PROGRAM ===");

    // Clear previous error indicators
    form.find('.is-invalid').removeClass('is-invalid');

    // Validate Start Date
    var startDate = form.find('.nonfoir-start-date').val() ||
        form.find('input[name="nonfoirBankStartDate"]').val();

    if (!startDate || startDate.trim() === '') {
        form.find('.nonfoir-start-date, input[name="nonfoirBankStartDate"]').addClass('is-invalid');
        alertmsg("Bank statement start date is required.");
        logValidationError("NONFOIR", "start-date", "", "Start date is empty");
        isValid = false;
    } else {
        console.log("Start Date: " + startDate);
    }

    // Validate End Date
    var endDate = form.find('.nonfoir-end-date').val() ||
        form.find('input[name="nonfoirBankEndDate"]').val();

    if (!endDate || endDate.trim() === '') {
        form.find('.nonfoir-end-date, input[name="nonfoirBankEndDate"]').addClass('is-invalid');
        alertmsg("Bank statement end date is required.");
        logValidationError("NONFOIR", "end-date", "", "End date is empty");
        isValid = false;
    } else {
        console.log("End Date: " + endDate);
    }

    // Validate Date Range (must be exactly 6 months)
    if (startDate && endDate) {
        try {
            // Parse dates
            var start = new Date(startDate);
            var end = new Date(endDate);

            // Check if dates are valid
            if (isNaN(start.getTime()) || isNaN(end.getTime())) {
                alertmsg("Invalid date format. Please select valid dates.");
                logValidationError("NONFOIR", "date-format", startDate + " to " + endDate, "Invalid date format");
                isValid = false;
            } else {
                // Check if end date is after start date
                if (end <= start) {
                    form.find('.nonfoir-start-date, .nonfoir-end-date').addClass('is-invalid');
                    alertmsg("End date must be after start date.");
                    logValidationError("NONFOIR", "date-order", startDate + " to " + endDate, "End date not after start date");
                    isValid = false;
                }

                // Calculate month difference
                var monthsDiff = (end.getFullYear() - start.getFullYear()) * 12 +
                    (end.getMonth() - start.getMonth());

                console.log("Month difference: " + monthsDiff);

                // Check if exactly 6 months
                if (monthsDiff !== 6) {
                    form.find('.nonfoir-start-date, .nonfoir-end-date').addClass('is-invalid');
                    alertmsg("Bank statement period must be exactly 6 months. Currently: " + monthsDiff + " months.");
                    logValidationError("NONFOIR", "date-range", monthsDiff + " months", "Date range is not exactly 6 months");
                    isValid = false;
                } else {
                    console.log("Date range validation passed: 6 months");
                }
            }
        } catch (e) {
            console.error("Error parsing dates:", e);
            alertmsg("Error validating dates. Please check the date format.");
            logValidationError("NONFOIR", "date-parsing-error", e.message, "Date parsing failed");
            isValid = false;
        }
    }

    // Validate Bank Selection
    var bankName = form.find('.nonfoir-bank-name').val() ||
        form.find('select[name="nonfoirBankName"]').val();

    if (!bankName || bankName.trim() === '' || bankName === '0') {
        form.find('.nonfoir-bank-name, select[name="nonfoirBankName"]').addClass('is-invalid');
        alertmsg("Please select a bank.");
        logValidationError("NONFOIR", "bank-name", "", "Bank not selected");
        isValid = false;
    } else {
        console.log("Bank selected: " + bankName);
    }

    // Validate Bank Statement Upload Status
    var uploadStatus = form.find('.nonfoir-upload-status').val() ||
        form.find('input[name="nonfoirUploadStatus"]').val();

    var hasUploadedFile = form.find('.nonfoir-file-upload')[0] &&
        form.find('.nonfoir-file-upload')[0].files.length > 0;

    var isUploaded = uploadStatus === 'uploaded' || hasUploadedFile;

    if (!isUploaded) {
        alertmsg("Please upload the bank statement.");
        logValidationError("NONFOIR", "upload-status", uploadStatus || "not uploaded", "Bank statement not uploaded");
        isValid = false;
    } else {
        console.log("Bank statement upload status: uploaded");
    }

    // Validate BSA-NONFOIR Processing
    var bsaResponse = form.find('.nonfoir-bsa-result .bsaResponse').html() ||
        form.find('[data-response-type="BSA-NONFOIR"] .bsaResponse').html();

    if (!bsaResponse || bsaResponse.trim() === '') {
        alertmsg("Bank statement has not been processed by BSA. Please process the bank statement.");
        logValidationError("NONFOIR", "bsa-nonfoir-response", "", "BSA-NONFOIR not processed");
        isValid = false;
    } else {
        console.log("BSA-NONFOIR processing validated");
    }

    if (!isValid) {
        alertmsg("Please fill in all required fields before saving.");
        console.log("=== NONFOIR PROGRAM VALIDATION FAILED ===");
    } else {
        console.log("=== NONFOIR PROGRAM VALIDATION PASSED ===");
    }

    return isValid;
}

// Validation for LOANFD (FD) Program
function validateLoanFDProgram(form) {
    var isValid = true;

    console.log("=== VALIDATING LOANFD (FD) PROGRAM ===");

    // Clear previous error indicators
    form.find('.is-invalid').removeClass('is-invalid');

    // Check if at least one FD entry exists in the table
    var fdRows = form.find('.fd-table-body tr');

    if (fdRows.length === 0) {
        alertmsg("At least one Fixed Deposit (FD) entry is required.");
        logValidationError("LOANFD", "fd-table-rows", "0", "No FD entries found");
        isValid = false;
        console.log("=== LOANFD PROGRAM VALIDATION FAILED: No FD entries ===");
        return isValid;
    }

    console.log("Number of FD entries: " + fdRows.length);

    // Get SIB customer flag
    var sibCustomer = form.find('.sibCustomer').val();
    var isSibCustomer = (sibCustomer === 'Y' || sibCustomer === 'true' || sibCustomer === true);
    console.log("Is SIB Customer: " + isSibCustomer);

    var totalFDAmount = 0;

    // Validate each FD row
    fdRows.each(function (index) {
        var row = $(this);
        var rowNumber = index + 1;

        console.log("Validating FD row " + rowNumber);

        // Validate FD Account Number
        var fdAccountNumber = row.find('.fd-account-number').val();
        if (!fdAccountNumber || fdAccountNumber.trim() === '') {
            row.find('.fd-account-number').addClass('is-invalid');
            alertmsg("FD Account Number is required for FD entry " + rowNumber);
            logValidationError("LOANFD", "fd-account-number-" + rowNumber, "", "FD Account Number missing");
            isValid = false;
        }

        // Validate FD Amount
        var fdAmount = row.find('.fd-amount').val();
        if (!fdAmount || fdAmount.trim() === '') {
            row.find('.fd-amount').addClass('is-invalid');
            alertmsg("FD Amount is required for FD entry " + rowNumber);
            logValidationError("LOANFD", "fd-amount-" + rowNumber, "", "FD Amount missing");
            isValid = false;
        } else if (isNaN(parseFloat(fdAmount)) || parseFloat(fdAmount) <= 0) {
            row.find('.fd-amount').addClass('is-invalid');
            alertmsg("FD Amount must be a valid number greater than zero for FD entry " + rowNumber);
            logValidationError("LOANFD", "fd-amount-" + rowNumber, fdAmount, "FD Amount invalid or zero");
            isValid = false;
        } else {
            totalFDAmount += parseFloat(fdAmount);
            console.log("FD row " + rowNumber + " amount: " + fdAmount);
        }

        // Validate Maturity Date
        var maturityDate = row.find('.fd-maturity-date').val();
        if (!maturityDate || maturityDate.trim() === '') {
            row.find('.fd-maturity-date').addClass('is-invalid');
            alertmsg("Maturity Date is required for FD entry " + rowNumber);
            logValidationError("LOANFD", "fd-maturity-date-" + rowNumber, "", "Maturity Date missing");
            isValid = false;
        } else {
            // Basic date format validation
            var datePattern = /^\d{4}-\d{2}-\d{2}$/;
            if (!datePattern.test(maturityDate)) {
                row.find('.fd-maturity-date').addClass('is-invalid');
                alertmsg("Maturity Date format is invalid for FD entry " + rowNumber + ". Expected format: YYYY-MM-DD");
                logValidationError("LOANFD", "fd-maturity-date-" + rowNumber, maturityDate, "Invalid date format");
                isValid = false;
            }
        }

        // Validate Customer Name
        var customerName = row.find('.fd-customer-name').val();
        if (!customerName || customerName.trim() === '') {
            row.find('.fd-customer-name').addClass('is-invalid');
            alertmsg("Customer Name is required for FD entry " + rowNumber);
            logValidationError("LOANFD", "fd-customer-name-" + rowNumber, "", "Customer Name missing");
            isValid = false;
        }

        // Validate CIF ID (required only if SIB customer)
        if (isSibCustomer) {
            var cifId = row.find('.fd-cif-id').val();
            if (!cifId || cifId.trim() === '') {
                row.find('.fd-cif-id').addClass('is-invalid');
                alertmsg("CIF ID is required for SIB customer in FD entry " + rowNumber);
                logValidationError("LOANFD", "fd-cif-id-" + rowNumber, "", "CIF ID missing for SIB customer");
                isValid = false;
            }
        }
    });

    // Validate Total Available Balance
    console.log("Total FD Amount calculated: " + totalFDAmount);

    var totalAvailableBalance = form.find('.total-available-balance').val() ||
        form.find('.total-fd-amount').val() ||
        form.find('.hidTotalFDAmount').val();

    if (totalFDAmount <= 0) {
        alertmsg("Total FD amount must be greater than zero.");
        logValidationError("LOANFD", "total-fd-amount", totalFDAmount, "Total FD amount is zero or negative");
        isValid = false;
    }

    // Optional: Validate that displayed total matches calculated total
    if (totalAvailableBalance) {
        var displayedTotal = parseFloat(totalAvailableBalance);
        if (Math.abs(displayedTotal - totalFDAmount) > 0.01) { // Allow small floating point differences
            console.warn("Warning: Displayed total (" + displayedTotal + ") differs from calculated total (" + totalFDAmount + ")");
            logValidationError("LOANFD", "total-mismatch", displayedTotal + " vs " + totalFDAmount, "Total mismatch");
        }
    }

    if (!isValid) {
        alertmsg("Please fill in all required fields before saving.");
        console.log("=== LOANFD PROGRAM VALIDATION FAILED ===");
    } else {
        console.log("=== LOANFD PROGRAM VALIDATION PASSED ===");
    }

    return isValid;
}

function validateImputedProgram(form) {
    var isValid = true;

    console.log("=== VALIDATING IMPUTED PROGRAM ===");

    // Clear previous error indicators
    form.find('.is-invalid').removeClass('is-invalid');

    // Validate Imputed Income Amount
    var imputedIncome = form.find('.imputed-income-amount').val() ||
        form.find('.imputed-income').val() ||
        form.find('input[name="imputedIncome"]').val();

    if (!imputedIncome || imputedIncome.trim() === '') {
        form.find('.imputed-income-amount, .imputed-income, input[name="imputedIncome"]').addClass('is-invalid');
        alertmsg("Imputed income amount is required.");
        logValidationError("IMPUTED", "imputed-income", "", "Imputed income amount is empty");
        isValid = false;
    } else if (isNaN(parseFloat(imputedIncome))) {
        form.find('.imputed-income-amount, .imputed-income, input[name="imputedIncome"]').addClass('is-invalid');
        alertmsg("Imputed income amount must be a valid number.");
        logValidationError("IMPUTED", "imputed-income", imputedIncome, "Imputed income amount is not a valid number");
        isValid = false;
    } else if (parseFloat(imputedIncome) <= 0) {
        form.find('.imputed-income-amount, .imputed-income, input[name="imputedIncome"]').addClass('is-invalid');
        alertmsg("Imputed income amount must be greater than zero.");
        logValidationError("IMPUTED", "imputed-income", imputedIncome, "Imputed income amount is zero or negative");
        isValid = false;
    } else {
        console.log("Imputed Income Amount validated: " + imputedIncome);
    }

    if (!isValid) {
        alertmsg("Please fill in all required fields before saving.");
        console.log("=== IMPUTED PROGRAM VALIDATION FAILED ===");
    } else {
        console.log("=== IMPUTED PROGRAM VALIDATION PASSED ===");
    }

    return isValid;
}
