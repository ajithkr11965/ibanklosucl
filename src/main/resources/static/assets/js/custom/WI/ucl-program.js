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

    detElement.find('.missing-cif-item .cif-id').each(function () {
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

// Function to validate 60/40 Program
function validate6040Program(form) {
    var errors = [];

    // Check if supporting document is uploaded
    var doc6040File = form.find('.doc-6040-upload').val();
    if (!doc6040File || doc6040File === '') {
        errors.push("Please upload the supporting document for 60/40 program");
        form.find('.doc-6040-upload').addClass('is-invalid');
        return false;
    }

    // Validate file extension is PDF
    var fileExtension = doc6040File.split('.').pop().toLowerCase();
    if (fileExtension !== 'pdf') {
        alertmsg('The supporting document must be a PDF file.');
        form.find('.doc-6040-upload').addClass('is-invalid');
        return false;
    }

    form.find('.doc-6040-upload').removeClass('is-invalid');
    return errors.length === 0;
}

// Helper function to format currency
function formatCurrency(amount) {
    return parseFloat(amount).toLocaleString('en-IN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}
// Initial calculation if data is pre-filled
$('.non-resident-income-section').each(function () {
    if ($(this).is(':visible')) {
        calculateRemittances($(this).closest('.det'));
    }
});
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
    $(selectorString).each(function () {
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
    const observer = new MutationObserver(function (mutations) {
        mutations.forEach(function (mutation) {
            if (mutation.addedNodes.length) {
                // Check if any of the added nodes or their children match our selectors
                $(mutation.addedNodes).find(selectorString).add(
                    $(mutation.addedNodes).filter(selectorString)
                ).each(function () {
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
    observer.observe(document.body, {childList: true, subtree: true});

    // For elements added via jQuery that might not trigger the MutationObserver
    $('#loanbody').on('focus', selectorString, function () {
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
        case '60/40':
            isValid = validate6040Program(form);
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
    } else if (programCode === '60/40') {
        process6040Files(form, allFiles, promises);
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

        // Handle NRI remittance data transformation for INCOME program
        if (programCodeSelected === "INCOME" && resident_type === 'N') {
            console.log("Transforming NRI remittance data for backend");

            // Add monthly salary
            var monthlySalary = form.find('.monthly-salary-nr').val();
            if (monthlySalary) {
                formDataArray.push({name: "MonthSalary", value: monthlySalary});
            }

            // Add average values for VehicleLoanProgram
            var avgTotalRemittance = form.find('.avg-total-remittance').val();
            var avgBulkRemittance = form.find('.avg-bulk-remittance').val() || '0';
            var avgNetRemittance = form.find('.avg-net-remittance').val();

            if (avgTotalRemittance) {
                formDataArray.push({name: "Avgtotal_remittance", value: avgTotalRemittance});
            }
            if (avgBulkRemittance) {
                formDataArray.push({name: "Avgbulk_remittance", value: avgBulkRemittance});
            }
            if (avgNetRemittance) {
                formDataArray.push({name: "Avgnet_remittance", value: avgNetRemittance});
            }

            // Transform remittance table data for VehicleLoanProgramNRI
            // Backend expects data in specific order:
            // 1. All MonthSalary_mon fields first (to create NRI objects)
            // 2. Then all total_remittance fields
            // 3. Then all bulk_remittance fields
            // 4. Then all net_remittance fields

            var remittanceData = [];
            form.find('.remittance-row').each(function(index) {
                var row = $(this);
                var monthYear = row.find('.remittance-month-year').val(); // Format: "2024-11"
                var totalRemittance = row.find('.total-remittance').val();
                var bulkRemittance = row.find('.bulk-remittance').val() || '0';
                var netRemittance = row.find('.net-remittance').val();

                // Only include rows with data
                if (totalRemittance && parseFloat(totalRemittance) > 0) {
                    // Convert "2024-11" to "Nov-2024" format
                    var monthYearFormatted = convertToMonthYearFormat(monthYear);

                    remittanceData.push({
                        index: index,
                        monthYear: monthYearFormatted,
                        totalRemittance: totalRemittance,
                        bulkRemittance: bulkRemittance,
                        netRemittance: netRemittance
                    });
                }
            });

            // Add all MonthSalary_mon fields first
            remittanceData.forEach(function(data) {
                formDataArray.push({name: "MonthSalary_mon" + data.index, value: data.monthYear});
            });

            // Then add all total_remittance fields
            remittanceData.forEach(function(data) {
                formDataArray.push({name: "total_remittance" + data.index, value: data.totalRemittance});
            });

            // Then add all bulk_remittance fields
            remittanceData.forEach(function(data) {
                formDataArray.push({name: "bulk_remittance" + data.index, value: data.bulkRemittance});
            });

            // Then add all net_remittance fields
            remittanceData.forEach(function(data) {
                formDataArray.push({name: "net_remittance" + data.index, value: data.netRemittance});
            });

            console.log("NRI remittance data transformation complete. Total rows:", remittanceData.length);
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

// Function to process 60/40 program files
function process6040Files(form, allFiles, promises) {
    form.find('.base64file').each(function () {
        var input = $(this);
        var files = input[0].files;
        if (!files || files.length === 0) {
            return; // Skip if no files selected
        }

        // Check if this file is relevant for 60/40 program
        if (!input.hasClass('doc-6040-upload')) {
            return; // Only process 60/40 program files
        }

        Array.from(files).forEach(function (file) {
            var promise = new Promise(function (resolve, reject) {
                var reader = new FileReader();
                reader.onload = function (e) {
                    var fileType = '60_40';

                    console.log("Processing 60/40 file:", file.name, "Type:", fileType);
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


function getMonthName(monthNum) {
    var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    return months[parseInt(monthNum) - 1];
}

// Helper function to convert "2024-11" to "Nov-2024" format
function convertToMonthYearFormat(yyyyMm) {
    if (!yyyyMm) return '';
    var parts = yyyyMm.split('-');
    if (parts.length !== 2) return yyyyMm;

    var year = parts[0];
    var monthNum = parseInt(parts[1]);
    var monthName = getMonthName(monthNum);

    return monthName + '-' + year;
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
    if (!selectedProgram) {
        selectedProgram = "NONE";
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

function showABBCalculationModal(triggerElement) {
    var applicantId = triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val();
    var wiNum = $('#winum').val();

    // Get the specific modal for this applicant
    var modalElement = triggerElement.closest('.det').closest('.tab-pane').find('.abbCalculationModal');

    $.ajax({
        url: `fetch/abb-calculation-details/${applicantId}/${wiNum}`,
        type: 'GET',
        dataType: 'json',
        beforeSend: function () {
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
        success: function (response) {
            if (response.success) {
                populateProfessionalABBModal(response, modalElement);
            } else {
                showProfessionalError(response.message, modalElement);
            }
        },
        error: function (xhr, status, error) {
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
