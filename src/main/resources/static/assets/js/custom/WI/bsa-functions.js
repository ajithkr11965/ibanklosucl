function monthDiff(startDate, endDate) {
    const start = new Date(startDate.getFullYear(), startDate.getMonth(), 1);
    const end = new Date(endDate.getFullYear(), endDate.getMonth(), 1);
    const months = (end.getFullYear() - start.getFullYear()) * 12 + (end.getMonth() - start.getMonth());
    return months + 1;
}

function showLoadingAnimation(element) {
    // Create overlay
    const overlay = document.createElement('div');
    overlay.classList.add('loading-overlay');
    overlay.style.position = 'absolute';
    overlay.style.top = '0';
    overlay.style.left = '0';
    overlay.style.width = '100%';
    overlay.style.height = '100%';
    overlay.style.backgroundColor = 'rgba(255, 255, 255, 0.8)';
    overlay.style.display = 'flex';
    overlay.style.justifyContent = 'center';
    overlay.style.alignItems = 'center';
    overlay.style.zIndex = '1000';

    // Create spinner
    const spinner = document.createElement('div');
    spinner.classList.add('spinner-border', 'text-primary');
    spinner.setAttribute('role', 'status');

    // Create loading text
    const loadingText = document.createElement('span');
    loadingText.textContent = 'Loading...';
    loadingText.style.marginLeft = '10px';

    // Append spinner and text to overlay
    overlay.appendChild(spinner);
    overlay.appendChild(loadingText);

    // Add overlay to the element
    element.style.position = 'relative';
    element.appendChild(overlay);

    return overlay;
}

function hideLoadingAnimation(overlay) {
    if (overlay && overlay.parentNode) {
        overlay.parentNode.removeChild(overlay);
    }
}

// Function to handle bank statement upload
function handleBankStatementUpload(triggerElement, statementType) {
    var startDate = triggerElement.closest('.bank-statement-section').find('.bank-start-date').val();
    var endDate = triggerElement.closest('.bank-statement-section').find('.bank-end-date').val();
    var bankName = triggerElement.closest('.bank-statement-section').find('.bank-name').val();

    // Validate inputs
    if (!startDate || !endDate || !bankName) {
        alertmsg("Please fill all required fields: Start Date, End Date and Bank Name.");
        return;
    }

    // Validate date range (12 months)
    var startDateObj = new Date(startDate);
    var endDateObj = new Date(endDate);
    var monthDifference = monthDiff(startDateObj, endDateObj);

    if (monthDifference !== 12) {
        alertmsg("Bank statement must cover exactly 12 months. Please adjust your date range.");
        return;
    }

    // Construct request body for the API
    var jsonBody = {
        "txnId": $('#winum').val() + "_" + triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
        "institutionId": bankName,
        "yearMonthFrom": startDate,
        "yearMonthTo": endDate,
        wiNum: $('#winum').val(),
        applicantId: triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
        slno: $('#slno').val(),
        "statementType": statementType
    };

    showLoader();

    // Call the API to initiate bank statement upload
    $.ajax({
        url: "process/fetchBSA",
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(jsonBody),
        success: function (response) {
            hideLoader();

            try {
                var data = JSON.parse(response);

                // Check for error in response
                if (data.Response && data.Response.Body && data.Response.Body.message && data.Response.Body.message.Error) {
                    var errorMessage = data.Response.Body.message.Error.message;
                    alertmsg('Error: ' + errorMessage);
                    return;
                }

                // Get URL from response and open in modal
                var url = data.Response.Body.message.Success.url;

                var currentTab = triggerElement.closest('.det').parent();
                var modal = currentTab.find('.iframe-modal');
                var modalTitle = modal.find('.modal-title');
                var iframe = modal.find('.itr-iframe');
                var loadingIndicator = modal.find('.loading-indicator');

                modalTitle.text("ITR program-Bank Statement Upload");

                iframe.on('load', function () {
                    loadingIndicator.hide();
                    iframe.show();
                });

                iframe.attr('src', url);
                modal.data('triggerElement', triggerElement);
                modal.data('statementType', statementType);
                modal.modal('show');

            } catch (error) {
                alertmsg("Error processing bank statement upload: " + error.message);
            }
        },
        error: function (xhr, status, error) {
            hideLoader();
            alertmsg("Error processing bank statement upload: " + error);
        }
    });
}

function handleBankStatementUploadRTR(triggerElement, statementType) {
    var startDate, endDate, bankName, statusElement;

    // Set variables based on statement type
    switch (statementType) {
        case 'RTR_DEBIT':
            startDate = triggerElement.closest('.section-body').find('.rtr-debit-start-date').val();
            endDate = triggerElement.closest('.section-body').find('.rtr-debit-end-date').val();
            bankName = triggerElement.closest('.section-body').find('.rtr-debit-bank-name').val();
            statusElement = triggerElement.closest('.section-body').find('.rtr-debit-bsa-status');
            break;
        case 'RTR_LOAN':
            startDate = triggerElement.closest('.section-body').find('.rtr-loan-start-date').val();
            endDate = triggerElement.closest('.section-body').find('.rtr-loan-end-date').val();
            bankName = triggerElement.closest('.section-body').find('.rtr-loan-bank-name').val();
            statusElement = triggerElement.closest('.section-body').find('.rtr-loan-bsa-status');
            break;
        case 'RTR_TOPUP_DEBIT':
            startDate = triggerElement.closest('.section-body').find('.rtr-topup-debit-start-date').val();
            endDate = triggerElement.closest('.section-body').find('.rtr-topup-debit-end-date').val();
            bankName = triggerElement.closest('.section-body').find('.rtr-topup-debit-bank-name').val();
            statusElement = triggerElement.closest('.section-body').find('.rtr-topup-debit-bsa-status');
            break;
        case 'RTR_TOPUP_LOAN':
            startDate = triggerElement.closest('.section-body').find('.rtr-topup-loan-start-date').val();
            endDate = triggerElement.closest('.section-body').find('.rtr-topup-loan-end-date').val();
            bankName = triggerElement.closest('.section-body').find('.rtr-topup-loan-bank-name').val();
            statusElement = triggerElement.closest('.section-body').find('.rtr-topup-loan-bsa-status');
            break;
        default:
            // For regular BSA (Income program)
            startDate = triggerElement.closest('.bank-statement-section').find('.bank-start-date').val();
            endDate = triggerElement.closest('.bank-statement-section').find('.bank-end-date').val();
            bankName = triggerElement.closest('.bank-statement-section').find('.bank-name').val();
            statusElement = triggerElement.closest('.bank-statement-section').find('.bsa-status');
    }

    // Construct request body for the API
    var jsonBody = {
        "txnId": $('#winum').val() + "_" + triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val() + "_" + statementType,
        "institutionId": bankName,
        "yearMonthFrom": startDate,
        "yearMonthTo": endDate,
        "statementType": statementType,
        wiNum: $('#winum').val(),
        applicantId: triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
        slno: $('#slno').val()
    };

    showLoader();

    // Call the API to initiate bank statement upload
    $.ajax({
        url: "process/fetchBSA",
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(jsonBody),
        success: function (response) {
            hideLoader();

            try {
                var data = JSON.parse(response);

                // Check for error in response
                if (data.Response && data.Response.Body && data.Response.Body.message && data.Response.Body.message.Error) {
                    var errorMessage = data.Response.Body.message.Error.message;
                    alertmsg('Error: ' + errorMessage);
                    return;
                }

                // Get URL from response and open in modal
                var url = data.Response.Body.message.Success.url;

                var currentTab = triggerElement.closest('.det').parent();
                var modal = currentTab.find('.iframe-modal');
                var modalTitle = modal.find('.modal-title');
                var iframe = modal.find('.itr-iframe');
                var loadingIndicator = modal.find('.loading-indicator');

                modalTitle.text("Bank Statement Upload - " + statementType);

                iframe.on('load', function () {
                    loadingIndicator.hide();
                    iframe.show();
                });

                iframe.attr('src', url);
                modal.data('triggerElement', triggerElement);
                modal.data('statementType', statementType);
                modal.data('statusElement', statusElement);
                modal.modal('show');

            } catch (error) {
                alertmsg("Error processing bank statement upload: " + error.message);
            }
        },
        error: function (xhr, status, error) {
            hideLoader();
            alertmsg("Error processing bank statement upload: " + error);
        }
    });
}

function checkLatestCompletedBSATransactionStatus(triggerElement, viewType, statementType) {
    console.log("Checking the BSA status for ---viewType---" + viewType + "--statementType---" + statementType)
    var applicantId = triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val();
    var wiNum = $('#winum').val();
    var jsonBody = {
        wiNum: wiNum,
        applicantId: applicantId,
        statementType: statementType
    };
    showLoader();
    $.ajax({
        url: "process/getLatestCompletedBSATransactionId",
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(jsonBody),
        success: function (latestCompletedTransactionId) {
            if (latestCompletedTransactionId !== null || latestCompletedTransactionId !== "") {
                console.log("LATEST BSA IS ---" + latestCompletedTransactionId);
                showLoader();
                if (viewType === "SUMMARY") {
                    fetchBSASummary(latestCompletedTransactionId, applicantId, wiNum, triggerElement, statementType);
                } else {
                    fetchBSAReport(latestCompletedTransactionId, applicantId, wiNum, triggerElement, statementType, "");
                }

                hideLoader();
            } else {
                notyaltInfo("BSA Processing not completed");
            }
        },
        error: function (xhr, status, error) {
            hideLoader();
            alertmsg("Error retrieving the latest completed transaction ID. Please try again later.");
            console.error("Error retrieving the latest completed transaction ID:", status, error);
        }
    });
    hideLoader();
}

function checkLatestCompletedBSATransactionStatusSurrogate(triggerElement, viewType, statementType, modal) {
    console.log("Checking the surrogate BSA status for ---viewType---" + viewType + "--statementType---" + statementType)
    var applicantId = triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val();
    var wiNum = $('#winum').val();
    var jsonBody = {
        wiNum: wiNum,
        applicantId: applicantId,
        statementType: statementType
    };
    showLoader();
    $.ajax({
        url: "process/getLatestCompletedBSATransactionId",
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(jsonBody),
        success: function (latestCompletedTransactionId) {
            if (latestCompletedTransactionId !== null || latestCompletedTransactionId !== "") {
                console.log("LATEST BSA IS ---" + latestCompletedTransactionId);
                showLoader();
                if (viewType === "SUMMARY") {
                    fetchBSASummary(latestCompletedTransactionId, applicantId, wiNum, triggerElement, statementType);
                } else {
                    fetchBSAReport(latestCompletedTransactionId, applicantId, wiNum, triggerElement, statementType, modal);
                }
                hideLoader();
            } else {
                notyaltInfo("BSA Processing not completed");
            }
        },
        error: function (xhr, status, error) {
            hideLoader();
            alertmsg("Error retrieving the latest completed transaction ID. Please try again later.");
            console.error("Error retrieving the latest completed transaction ID:", status, error);
        }
    });
    hideLoader();
}

function fetchBSAReport(perfiosTransactionId, applicantId, wiNum, triggerElement, statementType, modal) {
    console.log("Fetching the detailed BSA report for the surrogate program--statementType--" + statementType);
    var jsonBody = {
        perfiosTransactionId: perfiosTransactionId,
        applicantId: applicantId,
        wiNum: wiNum
    };
    showLoader();
    const bsaResponseElement = triggerElement.closest('.det').find('[data-response-type="' + statementType + '"]');
    const loadingOverlay = showLoadingAnimation(bsaResponseElement[0]);
    $.ajax({
        url: "process/fetchBSAReport",
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(jsonBody),
        success: function (response) {
            var data = JSON.parse(response);
            if (data.bsaData && data.bsaData.averageBankBalance !== undefined) {
                var abbAmount = data.bsaData.averageBankBalance;
                console.log(data);
                triggerElement.closest('.det').find('[data-response-type="' + statementType + '"]').find('.bsaResponse').html(`
                <table class="table table-bordered">
                    <tr>
                        <th colspan="13">Account Information</th>
                    </tr>
                    <tr>
                        <td>Name</td>
                        <td colspan="2"><b>${data.customerInfo.name}</b></td>
                        <td>Account No</td>
                        <td>${data.accountInfo.accountNo}</td>
                        <td colspan="2">Account Type</td>
                        <td colspan="3"><b>${data.accountInfo.accountType}</b></td>
                    </tr>
                    <tr>
                        <th colspan="13">Monthly Details</th>
                    </tr>
                    <tr>
                        <th>Month</th>
                        <th>Total Credits</th>
                        <th>Total Debits</th>
                        <th>Balance Min</th>
                        <th>Balance Max</th>
                        <th>Balance Avg</th>
                        <th>Calculated ABB</th>
                        <th>Penal Charges</th>
                        <th>Outward Bounces</th>
                        <th>Total Salary</th>
                        <th>Inward Bounces</th>
                    </tr>
                    ${data.monthlyDetails.map(month => `
                        <tr>
                            <td>${month.month}</td>
                            <td>${month.totalCredits}</td>
                            <td>${month.totalDebits}</td>
                            <td>${month.balanceMin}</td>
                            <td>${month.balanceMax}</td>
                            <td>${month.balanceAvg}</td>
                            <td><b>${month.calculatedABB.toFixed(2)}</b></td>
                            <td>${month.penalCharges}</td>
                            <td>${month.outwBounces}</td>
                            <td>${month.totalSalary}</td>
                            <td>${month.inwBounces}</td>
                        </tr>
                    `).join('')}
                </table>
            `);
                hideLoadingAnimation(loadingOverlay);
                bsaResponseElement.show();
                triggerElement.closest('.det').find('[data-response-type="' + statementType + '"]').parent().next().find('input[name^="abbAmount"]').val(abbAmount.toFixed(2));
                //triggerElement.closest('.det').find('[data-response-type="' + statementType + '"]').find('input[name^="bsaABB"]').val(abbAmount.toFixed(2));
                var programBtn = triggerElement.closest('.det').find('.save-button-program');
                programBtn.prop("disabled", false);
                var statementIndex = modal.data('statementIndex');
                var modalTitle = modal.find('.modal-title').text();
                console.log(modalTitle + "---AHL-PROGRAM---" + statementType + "--------------" + statementIndex);
                statementsData[statementIndex].uploaded = true;
                $(triggerElement).closest('.det').find(`#upload-status-${statementIndex}`).html('<span class="text-success">Uploaded</span>');
                $(triggerElement).closest('.det').find(`.review-upload-btn[data-idx="${statementIndex}"]`).prop('disabled', true);
                var allUploaded = statementsData.every(s => s.uploaded);
                triggerElement.closest('.det').find('.review-selections-btn').prop('disabled', !allUploaded);
            } else {
                alertmsg("Error fetching report. Please try again later.");
            }
            hideLoader();
        },
        error: function (xhr, status, error) {
            hideLoader();
            hideLoadingAnimation(loadingOverlay);
            alertmsg("Error fetching report. Please try again later.");
            console.error("Error fetching report:", status, error);
        }
    });
}

function fetchBSASummary(perfiosTransactionId, applicantId, wiNum, triggerElement, statementType) {
    var jsonBody = {
        perfiosTransactionId: perfiosTransactionId,
        applicantId: applicantId,
        wiNum: wiNum
    };

    showLoader();

    // Find the specific response div for this statement type
    const responseDiv = triggerElement.closest('.det').find('[data-response-type="' + statementType + '"]');
    const loadingOverlay = showLoadingAnimation(responseDiv[0]);

    $.ajax({
        url: "process/fetchBSAReport",
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(jsonBody),
        success: function (response) {
            var data = JSON.parse(response);
            if (data.customerInfo && data.accountInfo) {
                // Display just a summary view rather than detailed analysis
                responseDiv.html(`
                <div class="card mb-3">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0">Bank Statement Validation Summary</h5>
                    </div>
                    <div class="card-body">
                        <div class="row mb-3">
                            <div class="col-md-12">
                                <div class="alert alert-success">
                                    <i class="fas fa-check-circle mr-2"></i> Bank statement data has been successfully validated.
                                </div>
                            </div>
                        </div>
                        
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <div class="card">
                                    <div class="card-header bg-light">
                                        <h6 class="mb-0">Customer Information</h6>
                                    </div>
                                    <div class="card-body">
                                        <p><strong>Name:</strong> ${data.customerInfo.name}</p>
                                        <p><strong>PAN:</strong> ${data.customerInfo.pan || 'N/A'}</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="card">
                                    <div class="card-header bg-light">
                                        <h6 class="mb-0">Account Information</h6>
                                    </div>
                                    <div class="card-body">
                                        <p><strong>Account Number:</strong> ${data.accountInfo.accountNo}</p>
                                        <p><strong>Account Type:</strong> ${data.accountInfo.accountType}</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                `);

                hideLoadingAnimation(loadingOverlay);

                // If data also contains ABB information, store it for later use
                if (data.bsaData && data.bsaData.averageBankBalance !== undefined) {
                    var abbAmount = data.bsaData.averageBankBalance;
                    triggerElement.closest('.det').find('input[name^="bsaABB"]').val(abbAmount.toFixed(2));

                    // Also store month count for the summary
                    if (data.monthlyDetails) {
                        var monthCount = data.monthlyDetails.length;
                        responseDiv.append(`
                            <div class="text-muted text-center mt-2">
                                <small>${monthCount} months of data analyzed. Average Bank Balance: ₹${abbAmount.toFixed(2)}</small>
                            </div>
                        `);
                    }

                    // Enable program button
                    var programBtn = triggerElement.closest('.det').find('.save-button-program');
                    programBtn.prop("disabled", false);
                }
            } else {
                hideLoadingAnimation(loadingOverlay);
                responseDiv.html(`
                <div class="alert alert-warning">
                    <i class="fas fa-exclamation-triangle mr-2"></i> Bank statement data validation incomplete. Please try again.
                </div>
                `);
            }
            hideLoader();
        },
        error: function (xhr, status, error) {
            hideLoader();
            hideLoadingAnimation(loadingOverlay);
            alertmsg("Error validating bank statement. Please try again later.");
            console.error("Error validating bank statement:", status, error);
        }
    });
}


function closemodal(modalTitle, triggerElement) {
    console.log("close called" + modalTitle);
    showLoader();

    // Extract statement type from modal title Surrogate Statement
    let statementType = null;
    if (modalTitle.includes("RTR_DEBIT")) { // modal closed from RTR - debit bsa upload
        statementType = "RTR_DEBIT";
    } else if (modalTitle.includes("RTR_LOAN")) { // modal closed from RTR - loan bsa upload
        statementType = "RTR_LOAN";
    } else if (modalTitle.includes("RTR_TOPUP_DEBIT")) { // modal closed from RTR - topup debit bsa upload
        statementType = "RTR_TOPUP_DEBIT";
    } else if (modalTitle.includes("RTR_TOPUP_LOAN")) { // modal closed from RTR - topup loan bsa upload
        statementType = "RTR_TOPUP_LOAN";
    } else if (modalTitle.includes("Surrogate")) { // modal closed from surrogate program
        if (modalTitle.includes("Statement 1")) {
            statementType = "SURROGATE-1";
        } else if (modalTitle.includes("Statement 2")) {
            statementType = "SURROGATE-2";
        } else if (modalTitle.includes("Statement 3")) {
            statementType = "SURROGATE-3";
        }
    } else if (modalTitle.includes("ITR")) {
        statementType = "BSA-ITR";
    }

    if (modalTitle.includes("Bank Statement Upload") || modalTitle.includes("Surrogate Statement Upload")) {
        console.log("BSA validity check called for:", statementType);

        // Set the processed flag based on statement type
        if (statementType) {
            var form = triggerElement.closest('form');
            switch (statementType) {
                case 'RTR_DEBIT':
                    form.find('input[name="rtrDebitBSAProcessed"]').val('Y');
                    break;
                case 'RTR_LOAN':
                    form.find('input[name="rtrLoanBSAProcessed"]').val('Y');
                    break;
                case 'RTR_TOPUP_DEBIT':
                    form.find('input[name="rtrTopupDebitBSAProcessed"]').val('Y');
                    break;
                case 'RTR_TOPUP_LOAN':
                    form.find('input[name="rtrTopupLoanBSAProcessed"]').val('Y');
                    break;
            }
        }
        if (statementType.includes("SURROGATE")) {
            checkLatestCompletedBSATransactionStatus(triggerElement, "DETAIL", statementType);
        } else {
            checkLatestCompletedBSATransactionStatus(triggerElement, "SUMMARY", statementType);
        }


    } else if (modalTitle === "ITR File Upload") {
        console.log("ITR Check called");
        //checkLatestCompletedITRTransactionStatus(triggerElement);
    }
    hideLoader();
}

function closemodalSurrogate(modalTitle, triggerElement, modal) {
    console.log("close called" + modalTitle);
    showLoader();
    let statementType = null;
    if (modalTitle.includes("Surrogate")) { // modal closed from surrogate program
        if (modalTitle.includes("Statement 1")) {
            statementType = "SURROGATE-1";
        } else if (modalTitle.includes("Statement 2")) {
            statementType = "SURROGATE-2";
        } else if (modalTitle.includes("Statement 3")) {
            statementType = "SURROGATE-3";
        }
    }
    if (modalTitle.includes("Bank Statement Upload") || modalTitle.includes("Surrogate Statement Upload")) {
        console.log("BSA validity check called for:", statementType);
        if (statementType.includes("SURROGATE")) {
            checkLatestCompletedBSATransactionStatusSurrogate(triggerElement, "DETAIL", statementType, modal);
        }
        hideLoader();
    }
}

// Validate BSA Date Range
function validateBSADateRange(startDate, endDate) {
    // Validate date range (12 months)
    var startDateObj = new Date(startDate);
    var endDateObj = new Date(endDate);
    var monthDifference = monthDiff(startDateObj, endDateObj);

    if (monthDifference !== 12) {
        alertmsg("Bank statement must cover exactly 12 months. Please adjust your date range.");
        return false;
    }
    return true;
}

// Validate Bank Statement Upload
function validateBankStatementUpload(startDate, endDate, bankName) {
    // Validate inputs
    if (!startDate || !endDate || !bankName) {
        alertmsg("Please fill all required fields: Start Date, End Date and Bank Name.");
        return false;
    }

    // Validate date range
    return validateBSADateRange(startDate, endDate);
}

// Error Handling Utility
function handleBSAError(error, context) {
    console.error(`BSA Error in ${context}:`, error);
    hideLoader();
    alertmsg(`An error occurred during ${context}. Please try again later.`);
}

// Attach event listeners for BSA-related actions
$(document).ready(function () {

    // Bank Statement Go Button
    $('#loanbody').on('click', '.bank-statement-go', function (e) {
        e.preventDefault();
        handleBankStatementUpload($(this), "BSA-ITR");
    });

    // Handler for RTR debit statement go button

    $('#loanbody').on('click', '.rtr-debit-go', function (e) {
        e.preventDefault();
        var startDate = $(this).closest('.section-body').find('.rtr-debit-start-date').val();
        var endDate = $(this).closest('.section-body').find('.rtr-debit-end-date').val();
        var bankName = $(this).closest('.section-body').find('.rtr-debit-bank-name').val();

        // Validate inputs
        if (!startDate || !endDate || !bankName) {
            alertmsg("Please fill all required fields: Start Date, End Date and Bank Name.");
            return;
        }

        // Validate date range (12 months)
        var startDateObj = new Date(startDate);
        var endDateObj = new Date(endDate);
        var monthDifference = monthDiff(startDateObj, endDateObj);

        if (monthDifference !== 12) {
            alertmsg("Bank statement must cover exactly 12 months. Please adjust your date range.");
            return;
        }

        // Call bank statement upload function with appropriate parameters
        handleBankStatementUploadRTR($(this), 'RTR_DEBIT');
    });

// Handler for RTR loan statement go button
    $('#loanbody').on('click', '.rtr-loan-go', function (e) {
        e.preventDefault();
        var startDate = $(this).closest('.section-body').find('.rtr-loan-start-date').val();
        var endDate = $(this).closest('.section-body').find('.rtr-loan-end-date').val();
        var bankName = $(this).closest('.section-body').find('.rtr-loan-bank-name').val();

        // Validate inputs
        if (!startDate || !endDate || !bankName) {
            alertmsg("Please fill all required fields: Start Date, End Date and Bank Name.");
            return;
        }

        // Validate date range (23-25 months for loan statements)
        // This allows for March 2023 to March 2025 (25 months inclusive)
        // Or March 2023 to February 2025 (24 months inclusive)
        // Or April 2023 to March 2025 (24 months inclusive)
        const monthsDiff = calculateMonthsBetween(startDate, endDate, true);

        if (monthsDiff < 23 || monthsDiff > 25) {
            alertmsg("Loan bank statement should cover approximately 24 months (between 23-25 months). Please adjust your date range.");
            return;
        }

        // Call bank statement upload function with appropriate parameters
        handleBankStatementUploadRTR($(this), 'RTR_LOAN');
    });
// Handler for RTR top-up debit statement go button
    $('#loanbody').on('click', '.rtr-topup-debit-go', function (e) {
        e.preventDefault();
        var startDate = $(this).closest('.section-body').find('.rtr-topup-debit-start-date').val();
        var endDate = $(this).closest('.section-body').find('.rtr-topup-debit-end-date').val();
        var bankName = $(this).closest('.section-body').find('.rtr-topup-debit-bank-name').val();

        // Validate inputs
        if (!startDate || !endDate || !bankName) {
            alertmsg("Please fill all required fields: Start Date, End Date and Bank Name.");
            return;
        }

        // Validate date range (12 months)
        var startDateObj = new Date(startDate);
        var endDateObj = new Date(endDate);
        var monthDifference = monthDiff(startDateObj, endDateObj);

        if (monthDifference !== 12) {
            alertmsg("Bank statement must cover exactly 12 months. Please adjust your date range. Currently it is " + monthDifference);
            return;
        }

        // Call bank statement upload function with appropriate parameters
        handleBankStatementUploadRTR($(this), 'RTR_TOPUP_DEBIT');
    });

// Handler for RTR top-up loan statement go button
    $('#loanbody').on('click', '.rtr-topup-loan-go', function (e) {
        e.preventDefault();
        var startDate = $(this).closest('.section-body').find('.rtr-topup-loan-start-date').val();
        var endDate = $(this).closest('.section-body').find('.rtr-topup-loan-end-date').val();
        var bankName = $(this).closest('.section-body').find('.rtr-topup-loan-bank-name').val();

        // Validate inputs
        if (!startDate || !endDate || !bankName) {
            alertmsg("Please fill all required fields: Start Date, End Date and Bank Name.");
            return;
        }

        // Validate date range (23-25 months for loan statements)
        const monthsDiff = calculateMonthsBetween(startDate, endDate, true);

        if (monthsDiff < 23 || monthsDiff > 25) {
            alertmsg("Top-up loan bank statement should cover approximately 24 months (between 23-25 months). Please adjust your date range.");
            return;
        }

        // Call bank statement upload function with appropriate parameters
        handleBankStatementUploadRTR($(this), 'RTR_TOPUP_LOAN');
    });

    // Modal Close Handler
    $('.iframe-modal').on('hidden.bs.modal', function () {
        var modal = $(this);
        var triggerElement = modal.data('triggerElement');
        var modalTitle = modal.find('.modal-title').text();
        closemodal(modalTitle, triggerElement);
    });
    // Reset BSA summary -RTR section
    $('#loanbody').on('click', '.reset-bsa-btn', function (e) {
        e.preventDefault();
        var statementType = $(this).data('statement-type');
        var form = $(this).closest('form');
        var resetButton = $(this);
        Swal.fire({
            title: 'Reset Bank Statement?',
            html: `
            <div class="text-center">
                <p>Are you sure you want to reset this bank statement?</p>
                <div class="alert alert-warning mt-3">
                    <i class="ph-warning-circle pe-2"></i>
                    <span>This will require you to upload a new statement.</span>
                </div>
            </div>
        `,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, reset statement',
            cancelButtonText: 'Cancel',
            buttonsStyling: false,
            customClass: {
                confirmButton: 'btn btn-primary',
                cancelButton: 'btn btn-secondary me-3'
            },
            allowOutsideClick: false
        }).then((result) => {
            if (result.isConfirmed) {
                switch (statementType) {
                    case 'RTR_DEBIT':
                        form.find('input[name="rtrDebitBSAProcessed"]').val('N');
                        break;
                    case 'RTR_LOAN':
                        form.find('input[name="rtrLoanBSAProcessed"]').val('N');
                        break;
                    case 'RTR_TOPUP_DEBIT':
                        form.find('input[name="rtrTopupDebitBSAProcessed"]').val('N');
                        break;
                    case 'RTR_TOPUP_LOAN':
                        form.find('input[name="rtrTopupLoanBSAProcessed"]').val('N');
                        break;
                    case 'BSA-ITR':
                        // form.find('[data-response-type="BSA-ITR"]').empty();
                        // form.find('.bank-start-date').prop('disabled', false).val('');
                        // form.find('.bank-end-date').prop('disabled', false).val('');
                        // form.find('.bank-name').prop('disabled', false).val('');
                        // form.find('.bsa-status').empty();
                        // form.find('.bank-statement-go').show();
                        // form.find('.alert-card').show();
                        form.find('input[name="bsaProcessed"]').val('N');
                        break;
                }
                var section = resetButton.closest('.section-body');
                var responseDiv = section.find('[data-response-type="' + statementType + '"]');
                var inputSection = section.find('.row').first();
                responseDiv.empty();
                inputSection.show();
                section.find('.btn-primary').show();
                Swal.fire({
                    icon: 'success',
                    title: 'Statement Reset',
                    text: 'Bank statement has been reset. You can now upload a new statement.',
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true
                });
            }
        });
    });
});

function calculateMonthsBetween(startYearMonth, endYearMonth, inclusive = true) {
    // Parse YYYY-MM strings
    const startYear = parseInt(startYearMonth.split('-')[0]);
    const startMonth = parseInt(startYearMonth.split('-')[1]) - 1; // 0-based months
    const endYear = parseInt(endYearMonth.split('-')[0]);
    const endMonth = parseInt(endYearMonth.split('-')[1]) - 1; // 0-based months
    // Calculate months difference
    const monthsDiff = (endYear - startYear) * 12 + (endMonth - startMonth);
    // Return inclusive or exclusive count
    return inclusive ? monthsDiff + 1 : monthsDiff;
}

function handleSurrogateStatementUpload(triggerElement) {
    // Get the current index of the statement being uploaded
    const idx = parseInt(triggerElement.data('idx'));
    const statementData = statementsData[idx];
    const statementType = `SURROGATE-${idx + 1}`;

    // Validate inputs
    if (!statementData.startDate || !statementData.endDate || !statementData.bankCode) {
        alertmsg("Please fill in all required fields: Start Date, End Date, and Bank Name.");
        return;
    }

    // Validate date range (12 months)
    const startDateObj = new Date(statementData.startDate);
    const endDateObj = new Date(statementData.endDate);
    const monthDifference = monthDiff(startDateObj, endDateObj);

    // if (monthDifference !== 12) {
    //     alertmsg("Bank statement must cover exactly 12 months. Please adjust your date range.");
    //     return;
    // }

    // Prepare request body for the API
    const jsonBody = {
        "txnId": $('#winum').val() + "_" + triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val() + "_SURROGATE_" + idx,
        "institutionId": statementData.bankCode,
        "yearMonthFrom": statementData.startDate,
        "yearMonthTo": statementData.endDate,
        "statementType": statementType,
        wiNum: $('#winum').val(),
        applicantId: triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
        slno: $('#slno').val()
    };

    showLoader();

    // Call the API to initiate bank statement upload
    $.ajax({
        url: "process/fetchBSA",
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(jsonBody),
        success: function (response) {
            hideLoader();

            try {
                var data = JSON.parse(response);

                // Check for error in response
                if (data.Response && data.Response.Body && data.Response.Body.message && data.Response.Body.message.Error) {
                    var errorMessage = data.Response.Body.message.Error.message;
                    alertmsg('Error: ' + errorMessage);
                    return;
                }

                // Get URL from response and open in modal
                var url = data.Response.Body.message.Success.url;

                var currentTab = triggerElement.closest('.det').parent();
                var modal = currentTab.find('.iframe-bsa-modal');
                var modalTitle = modal.find('.modal-title');
                var iframe = modal.find('.itr-iframe');
                var loadingIndicator = modal.find('.loading-indicator');

                modalTitle.text("Surrogate Statement Upload - Statement " + (idx + 1));

                iframe.on('load', function () {
                    loadingIndicator.hide();
                    iframe.show();
                });

                iframe.attr('src', url);
                modal.data('triggerElement', triggerElement);
                modal.data('statementType', statementType);
                modal.data('statementIndex', idx);
                modal.modal('show');

            } catch (error) {
                alertmsg("Error processing bank statement upload: " + error.message);
            }
        },
        error: function (xhr, status, error) {
            hideLoader();
            alertmsg("Error processing bank statement upload: " + error);
        }
    });
}

$('.iframe-bsa-modal').on('hidden.bs.modal', function () {
    console.log("BSA Closure triggered");
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
        closemodalSurrogate(modalTitle, triggerElement, modal);
    } else if (statementType.includes('BSA-ITR')) {
        console.log("BSA-ITR dont take action from here . Handled in bsa-functions.js");
    } else {
        // Existing close modal logic
        closemodal(modalTitle, triggerElement);
    }
});

// function showABBCalculationModal(triggerElement) {
//     var applicantId = triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val();
//     var wiNum = $('#winum').val();
//
//     // Get the specific modal for this applicant
//     var modalElement = triggerElement.closest('.det').closest('.tab-pane').find('.abbCalculationModal');
//
//     $.ajax({
//         url: `fetch/abb-calculation-details/${applicantId}/${wiNum}`,
//         type: 'GET',
//         dataType: 'json',
//         beforeSend: function() {
//             // Show modern loading spinner in the specific modal
//             modalElement.find('.modal-body').html(`
//                 <div class="d-flex justify-content-center align-items-center" style="min-height: 300px;">
//                     <div class="text-center">
//                         <div class="spinner-border text-primary" style="width: 3rem; height: 3rem;" role="status">
//                             <span class="visually-hidden">Loading...</span>
//                         </div>
//                         <div class="mt-3">
//                             <h5 class="text-primary">Analyzing Bank Statements</h5>
//                             <p class="text-muted">Processing calculation details...</p>
//                         </div>
//                     </div>
//                 </div>
//             `);
//             modalElement.modal('show');
//         },
//         success: function(response) {
//             if (response.success) {
//                 populateModernABBCalculationModal(response, modalElement);
//             } else {
//                 showErrorMessage(response.message, modalElement);
//             }
//         },
//         error: function(xhr, status, error) {
//             showErrorMessage('Error loading calculation details. Please try again.', modalElement);
//         }
//     });
// }
// function populateModernABBCalculationModal(data) {
//     // Generate header stats
//     const headerStats = generateHeaderStats(data);
//
//     // Generate table content
//     const tableContent = generateModernTable(data);
//
//     // Generate summary cards
//     const summaryCards = generateSummaryCards(data);
//
//     const modalContent = `
//         <!-- Modern Modal Header -->
//         <div class="modal-header" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none; padding: 24px 32px; position: relative;">
//             <div style="position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: rgba(255,255,255,0.1); backdrop-filter: blur(10px);"></div>
//             <div style="position: relative; z-index: 1; width: 100%;">
//                 <h5 class="modal-title text-white mb-3" style="font-weight: 600; font-size: 1.5rem;">
//                     <i class="fas fa-chart-line me-3"></i>Average Bank Balance Calculation
//                 </h5>
//                 ${headerStats}
//             </div>
//             <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
//         </div>
//
//         <!-- Modern Modal Body -->
//         <div class="modal-body" style="padding: 32px; background: #ffffff;">
//             ${tableContent}
//             ${summaryCards}
//         </div>
//
//         <!-- Modern Modal Footer -->
//         <div class="modal-footer" style="background: #f8fafc; border: none; padding: 20px 32px;">
//             <button type="button" class="btn btn-primary" data-bs-dismiss="modal" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none; border-radius: 12px; padding: 12px 24px; font-weight: 600;">
//                 <i class="fas fa-check me-2"></i>Close Analysis
//             </button>
//         </div>
//     `;
//
//     $('#abbCalculationModal .modal-content').html(modalContent);
//
//     // Add modern styling dynamically
//     addModernStyling();
// }
// function generateHeaderStats(data) {
//     const totalMonths = data.totalMonths || 0;
//     const finalABB = parseFloat(data.finalABB || 0);
//     const finalABBFormatted = formatCurrencyCompact(finalABB);
//
//     return `
//         <div class="d-flex gap-4 flex-wrap">
//             <div style="background: rgba(255,255,255,0.2); padding: 8px 16px; border-radius: 8px; backdrop-filter: blur(10px);">
//                 <span style="font-size: 1.25rem; font-weight: 700; display: block;">3</span>
//                 <span style="font-size: 0.8rem; opacity: 0.9;">Bank Accounts</span>
//             </div>
//             <div style="background: rgba(255,255,255,0.2); padding: 8px 16px; border-radius: 8px; backdrop-filter: blur(10px);">
//                 <span style="font-size: 1.25rem; font-weight: 700; display: block;">${totalMonths}</span>
//                 <span style="font-size: 0.8rem; opacity: 0.9;">Months Analyzed</span>
//             </div>
//             <div style="background: rgba(255,255,255,0.2); padding: 8px 16px; border-radius: 8px; backdrop-filter: blur(10px);">
//                 <span style="font-size: 1.25rem; font-weight: 700; display: block;">${finalABBFormatted}</span>
//                 <span style="font-size: 0.8rem; opacity: 0.9;">Final ABB</span>
//             </div>
//         </div>
//     `;
// }
// function generateModernTable(data) {
//     const tableRows = data.calculationBreakdown.map((monthData, index) => {
//         const isExcluded = !monthData.included;
//         const rowClass = isExcluded ? 'excluded-row' : '';
//
//         // Get bank names
//         const bank1Name = data.bankNames['SURROGATE-1'] || 'Bank 1';
//         const bank2Name = data.bankNames['SURROGATE-2'] || 'Bank 2';
//         const bank3Name = data.bankNames['SURROGATE-3'] || 'Bank 3';
//
//         const rankBadgeStyle = isExcluded ?
//             'background: linear-gradient(135deg, #ff6b6b 0%, #ffa726 100%);' :
//             'background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);';
//
//         const statusBadge = isExcluded ?
//             `<span class="status-excluded"><i class="fas fa-times me-2"></i>${monthData.trimmedStatus}</span>` :
//             `<span class="status-included"><i class="fas fa-check me-2"></i>Included</span>`;
//
//         return `
//             <tr class="${rowClass}" style="transition: all 0.3s ease;">
//                 <td class="month-cell" style="background: linear-gradient(135deg, #edf2f7 0%, #e2e8f0 100%); font-weight: 700; border-radius: 8px; margin: 4px; padding: 12px !important;">
//                     <strong>${monthData.month}</strong>
//                 </td>
//                 <td class="amount-cell" style="font-family: 'Monaco', 'Menlo', monospace; font-weight: 600; text-align: right;">
//                     ${formatCurrency(monthData.surrogate1)}
//                 </td>
//                 <td class="amount-cell" style="font-family: 'Monaco', 'Menlo', monospace; font-weight: 600; text-align: right;">
//                     ${formatCurrency(monthData.surrogate2)}
//                 </td>
//                 <td class="amount-cell" style="font-family: 'Monaco', 'Menlo', monospace; font-weight: 600; text-align: right;">
//                     ${formatCurrency(monthData.surrogate3)}
//                 </td>
//                 <td class="combined-total amount-cell" style="background: linear-gradient(135deg, #e6fffa 0%, #b2f5ea 100%); border-radius: 8px; padding: 8px 12px !important; font-weight: 700; font-size: 1rem; text-align: right;">
//                     ${formatCurrency(monthData.combinedTotal)}
//                 </td>
//                 <td style="text-align: center;">
//                     <span class="rank-badge" style="display: inline-flex; align-items: center; justify-content: center; width: 32px; height: 32px; border-radius: 50%; color: white; font-weight: 700; font-size: 0.8rem; ${rankBadgeStyle}">
//                         ${monthData.rank}
//                     </span>
//                 </td>
//                 <td>${statusBadge}</td>
//             </tr>
//         `;
//     }).join('');
//
//     // Get first bank names for headers
//     const bank1Name = data.bankNames['SURROGATE-1'] || 'Bank 1';
//     const bank2Name = data.bankNames['SURROGATE-2'] || 'Bank 2';
//     const bank3Name = data.bankNames['SURROGATE-3'] || 'Bank 3';
//
//     return `
//         <div class="calculation-table" style="background: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 30px rgba(0,0,0,0.1); margin-bottom: 32px;">
//             <div style="max-height: 600px; overflow-y: auto;">
//                 <table class="table mb-0" style="font-size: 0.9rem;">
//                     <thead>
//                         <tr style="background: linear-gradient(135deg, #2d3748 0%, #4a5568 100%);">
//                             <th style="color: white; font-weight: 600; border: none; padding: 20px 16px; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">
//                                 <i class="fas fa-calendar-alt me-2"></i>Month
//                             </th>
//                             <th style="color: white; font-weight: 600; border: none; padding: 20px 16px; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">
//                                 <i class="fas fa-university me-2"></i>${bank1Name}
//                             </th>
//                             <th style="color: white; font-weight: 600; border: none; padding: 20px 16px; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">
//                                 <i class="fas fa-university me-2"></i>${bank2Name}
//                             </th>
//                             <th style="color: white; font-weight: 600; border: none; padding: 20px 16px; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">
//                                 <i class="fas fa-university me-2"></i>${bank3Name}
//                             </th>
//                             <th style="color: white; font-weight: 600; border: none; padding: 20px 16px; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">
//                                 <i class="fas fa-calculator me-2"></i>Combined Total
//                             </th>
//                             <th style="color: white; font-weight: 600; border: none; padding: 20px 16px; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">
//                                 <i class="fas fa-sort-numeric-up me-2"></i>Rank
//                             </th>
//                             <th style="color: white; font-weight: 600; border: none; padding: 20px 16px; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">
//                                 <i class="fas fa-check-circle me-2"></i>Status
//                             </th>
//                         </tr>
//                     </thead>
//                     <tbody>
//                         ${tableRows}
//                     </tbody>
//                 </table>
//             </div>
//         </div>
//     `;
// }
// function generateSummaryCards(data) {
//     const excludedMonths = data.totalMonths - data.includedMonths;
//
//     return `
//         <div class="row g-4">
//             <div class="col-lg-12">
//                 <div class="card h-100" style="border: none; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 30px rgba(0,0,0,0.1);">
//                     <div style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); padding: 20px 24px; border: none; position: relative;">
//                         <div style="position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: rgba(255,255,255,0.1); backdrop-filter: blur(10px);"></div>
//                         <h6 class="mb-0 text-white" style="position: relative; z-index: 1; font-weight: 600;">
//                             <i class="fas fa-chart-pie me-2"></i>Calculation Summary
//                         </h6>
//                     </div>
//                     <div style="padding: 24px; background: rgba(255,255,255,0.9);">
//                         <table class="table mb-0" style="background: transparent;">
//                             <tr>
//                                 <td style="border: none; padding: 12px 0; font-weight: 500;">
//                                     <i class="fas fa-calendar me-2 text-primary"></i><strong>Total Months Analyzed:</strong>
//                                 </td>
//                                 <td class="text-end" style="border: none; padding: 12px 0;">
//                                     <span class="badge bg-primary rounded-pill">${data.totalMonths}</span>
//                                 </td>
//                             </tr>
//                             <tr>
//                                 <td style="border: none; padding: 12px 0; font-weight: 500;">
//                                     <i class="fas fa-check-circle me-2 text-success"></i><strong>Months Included:</strong>
//                                 </td>
//                                 <td class="text-end" style="border: none; padding: 12px 0;">
//                                     <span class="badge bg-success rounded-pill">${data.includedMonths}</span>
//                                 </td>
//                             </tr>
//                             <tr>
//                                 <td style="border: none; padding: 12px 0; font-weight: 500;">
//                                     <i class="fas fa-times-circle me-2 text-danger"></i><strong>Months Excluded:</strong>
//                                 </td>
//                                 <td class="text-end" style="border: none; padding: 12px 0;">
//                                     <span class="badge bg-danger rounded-pill">${excludedMonths}</span>
//                                 </td>
//                             </tr>
//                             <tr>
//                                 <td style="border: none; padding: 12px 0; font-weight: 500;">
//                                     <i class="fas fa-plus me-2 text-info"></i><strong>Sum of Included Months:</strong>
//                                 </td>
//                                 <td class="text-end" style="border: none; padding: 12px 0; font-weight: 600;">
//                                     ${formatCurrency(data.sumIncludedMonths)}
//                                 </td>
//                             </tr>
//                             <tr style="background: linear-gradient(135deg, #e6fffa 0%, #b2f5ea 100%); border-radius: 8px;">
//                                 <td style="border: none; padding: 16px 12px; font-weight: 700; font-size: 1.1rem; color: #065f46;">
//                                     <i class="fas fa-trophy me-2"></i><strong>Final Average Bank Balance:</strong>
//                                 </td>
//                                 <td class="text-end" style="border: none; padding: 16px 12px; font-weight: 700; font-size: 1.1rem; color: #065f46;">
//                                     <strong>${formatCurrency(data.finalABB)}</strong>
//                                 </td>
//                             </tr>
//                         </table>
//                     </div>
//                 </div>
//             </div>
//
//
//         </div>
//     `;
// }
//
// function addModernStyling() {
//     // Add CSS for excluded rows and status badges
//     const style = document.createElement('style');
//     style.textContent = `
//         .excluded-row {
//             background: linear-gradient(135deg, #fff5f5 0%, #fed7d7 100%) !important;
//             position: relative;
//         }
//         .excluded-row::before {
//
//             position: absolute;
//             left: 0;
//             top: 0;
//             bottom: 0;
//             width: 4px;
//             background: linear-gradient(135deg, #ff6b6b 0%, #ffa726 100%);
//         }
//         .status-included {
//             display: inline-flex;
//             align-items: center;
//             padding: 6px 12px;
//             background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
//             color: #155724;
//             border-radius: 20px;
//             font-weight: 600;
//             font-size: 0.8rem;
//         }
//         .status-excluded {
//             display: inline-flex;
//             align-items: center;
//             padding: 6px 12px;
//             background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
//             color: #721c24;
//             border-radius: 20px;
//             font-weight: 600;
//             font-size: 0.8rem;
//             animation: pulse 2s infinite;
//         }
//         @keyframes pulse {
//             0% { transform: scale(1); }
//             50% { transform: scale(1.05); }
//             100% { transform: scale(1); }
//         }
//         .table tbody tr:hover {
//             background-color: #f8fafc !important;
//             transform: translateX(4px);
//         }
//     `;
//     document.head.appendChild(style);
// }
//
// function formatCurrency(amount) {
//     if (!amount || isNaN(amount)) return '₹0.00';
//     return `₹${parseFloat(amount).toLocaleString('en-IN', {
//         minimumFractionDigits: 2,
//         maximumFractionDigits: 2
//     })}`;
// }
//
// function formatCurrencyCompact(amount) {
//     if (!amount || isNaN(amount)) return '₹0';
//     const value = parseFloat(amount);
//     if (value >= 10000000) { // 1 crore
//         return `₹${(value / 10000000).toFixed(1)}Cr`;
//     } else if (value >= 100000) { // 1 lakh
//         return `₹${(value / 100000).toFixed(1)}L`;
//     } else if (value >= 1000) { // 1 thousand
//         return `₹${(value / 1000).toFixed(1)}K`;
//     } else {
//         return `₹${value.toFixed(0)}`;
//     }
// }
//
// function showErrorMessage(message) {
//     $('#abbCalculationModal .modal-body').html(`
//         <div class="text-center py-5">
//             <div class="mb-4">
//                 <i class="fas fa-exclamation-triangle text-warning" style="font-size: 4rem;"></i>
//             </div>
//             <h5 class="text-danger mb-3">Error Loading Calculation</h5>
//             <p class="text-muted">${message}</p>
//             <button type="button" class="btn btn-primary mt-3" onclick="location.reload()">
//                 <i class="fas fa-refresh me-2"></i>Retry
//             </button>
//         </div>
//     `);
// }



