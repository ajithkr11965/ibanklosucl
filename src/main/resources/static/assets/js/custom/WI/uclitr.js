/**
 * HL ITR Integration JavaScript Module
 * Handles ITR functionalities for Home Loan Application
 */
const HLITRHandler = (function () {
    // Private variables
    let currentPAN = '';
    let currentDOB = '';
    let currentMobile = '';
    let currentApplicantId = '';
    let currentWiNum = '';
    let currentSlNo = '';
    let savedITRMode = '';

    /**
     * Initialize event handlers
     */
    function init() {
        // Bind event handlers
        // $('#loanbody').on('click', '.itr-fetch-btn', function () {
        //     const itrMode = $(this).data('itrmode');
        //     handleITRRequest($(this), itrMode);
        // });
        //
        // $('#loanbody').on('click', '.itr-upload-btn', function () {
        //     const itrMode = $(this).data('itrmode');
        //     handleITRRequest($(this), itrMode);
        // });
        //
        $('#loanbody').on('click', '.check-itr-status', function () {
            checkLatestCompletedITRTransactionStatus($(this));
        });

        // Modal closing handler
        $('.iframe-modal').on('hidden.bs.modal', function () {
            const modal = $(this);
            const triggerElement = modal.data('triggerElement');
            const modalTitle = modal.find('.modal-title').text();
            closeModal(modalTitle, triggerElement);
        });
    }
    async function handleITRRequest(triggerElement, mode) {
        console.log("ITR INVOKED WITH MODE : " + mode);
        showLoader();
        try {
            const hasActiveProcess = await checkActiveITRProcess(triggerElement);
            if (hasActiveProcess) {
                hideLoader();
                const shouldReset = await showConfirmationDialog();
                if (!shouldReset) {
                    return;
                }
                showLoader();
                await resetActiveITRProcess(triggerElement);
                triggerElement.closest('.det').find('.itrResponse').html('');
                triggerElement.closest('.det').find('input[name^="itrMonthlyGross"]').val("");
            }

            const response = await fetchITR(triggerElement, mode);

            if (response != null) {
                const respObj = JSON.parse(response);
                if (respObj.status === 'success' || respObj.Response) {
                    updateUIAfterITRRequest(respObj);
                } else {
                    showFeedback('Error processing ITR request', 'error');
                }
            }
            hideLoader();
        } catch (error) {
            hideLoader();
            showFeedback('An error occurred while processing your request. Please try again.', 'error');
            console.error('Error in handleITRRequest:', error);
        }
    }

    async function checkActiveITRProcess(triggerElement) {
        const generaldetailsElement = triggerElement.closest('.det').parent().closest('.tab-pane').find('.generaldetails');
        const applicantId = generaldetailsElement.find('.appid').val();
        const wiNum = $('#winum').val();

        try {
            const response = await $.ajax({
                url: 'process/check-active-itr',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({applicantId, wiNum})
            });
            return response.hasActiveProcess;
        } catch (error) {
            console.error('Error checking active ITR process:', error);
            throw error;
        }
    }

    async function resetActiveITRProcess(triggerElement) {
        const generaldetailsElement = triggerElement.closest('.det').parent().closest('.tab-pane').find('.generaldetails');
        const applicantId = generaldetailsElement.find('.appid').val();
        const wiNum = $('#winum').val();


        try {
            await $.ajax({
                url: 'process/reset-active-itr',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({applicantId, wiNum})
            });
            const employmentType = triggerElement.closest('.det').find('.empl_type').val();
            console.log("Resetting ITR UI for employment type:", employmentType);
            if (employmentType && employmentType.toLowerCase() === 'agriculturist') {
                // Clear agriculturist-specific fields
                triggerElement.closest('.det').find('.agriculturist-section .itrResponse').html('');
                triggerElement.closest('.det').find('input[name="agriculturistMonthlyGross"]').val("");
                triggerElement.closest('.det').find('.agriculturist-section .itr-monthly-gross-div').hide();
            } else if (employmentType && (employmentType.toLowerCase().includes('self') || employmentType.toLowerCase() === 'sepsenp')) {
                // Clear SEPSENP-specific fields
                triggerElement.closest('.det').find('.sepsenp-section .itrResponse').html('');
                triggerElement.closest('.det').find('input[name="sepSenpMonthlyGross"]').val("");
                triggerElement.closest('.det').find('.sepsenp-section .itr-monthly-gross-div').hide();
            } else if (employmentType && employmentType.toLowerCase() === 'pensioner') {
                // Clear pensioner-specific fields
                triggerElement.closest('.det').find('.pensioner-section .itrResponse').html('');
                triggerElement.closest('.det').find('input[name^="pensionerMonthlyGross"]').val("");
                triggerElement.closest('.det').find('.pensioner-section .itr-monthly-gross-div').hide();
            } else if (employmentType && employmentType.toLowerCase() === 'salaried') {
                // Clear salaried-specific fields
                triggerElement.closest('.det').find('.salaried-section .itrResponse').html('');
                triggerElement.closest('.det').find('input[name="itrMonthlyGross"]').val("");
                triggerElement.closest('.det').find('.salaried-section .itr-monthly-gross-div').hide();
            } else {
                // If employment type isn't set or doesn't match specific cases, clear all possible fields
                triggerElement.closest('.det').find('.itrResponse').html('');
                triggerElement.closest('.det').find('input[name^="itrMonthlyGross"]').val("");
                triggerElement.closest('.det').find('input[name="agriculturistMonthlyGross"]').val("");
                triggerElement.closest('.det').find('input[name="sepSenpMonthlyGross"]').val("");
                triggerElement.closest('.det').find('input[name^="pensionerMonthlyGross"]').val("");
                triggerElement.closest('.det').find('.itr-monthly-gross-div').hide();
            }

            // Hide status checking elements
            triggerElement.closest('.det').find('.check-itr-status').hide();

            // Disable the program save button until new ITR data is processed
            triggerElement.closest('.det').find('.save-button-program').prop("disabled", true);

            console.log("ITR UI elements reset completed");


        } catch (error) {
            console.error('Error resetting active ITR process:', error);
            throw error;
        }
    }
    async function showConfirmationDialog() {
        try {
            const result = await confirmmsg("An active ITR process already exists. Do you want to reset this and create a new one?");
            return result;
        } catch (error) {
            console.error("Error in confirmation dialog:", error);
            return false;
        }
    }
    function calculateFinalAMIITR(element) {
    var form = element.closest('.field-row').parent();
    var monthlyGrossField = element.find('.itr-monthly-gross, [name="itrMonthlyGross"], [name="pensionerMonthlyGross"], [name="sepSenpMonthlyGross"], [name="agriculturistMonthlyGross"], .form16-monthly-income, .avg-monthly-income');
    var addBacksObligationsField = element.find('[name="addBacksObligations"]');

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
    var addBacksObligations = parseFloat(addBacksObligationsField.val()) || 0;

    // Calculate final AMI
    var finalAMI = monthlyGrossIncome + addBacksObligations;

    // Find and update the final AMI field
    var finalAMIField = element.find('.final-ami');
    if (finalAMIField.length === 0) {
        // Try to find in next sibling
        finalAMIField = element.closest('.field-row').next().find('.final-ami');
    }

    if (finalAMIField.length > 0) {
        finalAMIField.val(finalAMI.toFixed(2));
    }
}

    function fetchITR(triggerElement, itrMode) {
        var itrPan = triggerElement.closest('.det').parent().closest('.tab-pane').find('.kycdetails').find('.pan').val();
        var itrDOB = triggerElement.closest('.det').parent().closest('.tab-pane').find('.kycdetails').find('.pandob').val();
        var itrSMSMobileNo = triggerElement.closest('.det').parent().closest('.tab-pane').find('.basicdetails').find('.basic_mob').val();
        var currentTab = triggerElement.closest('.det').parent();

        // Save values for future reference
        currentPAN = itrPan;
        currentDOB = itrDOB;
        currentMobile = itrSMSMobileNo;
        currentApplicantId = triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val();
        currentWiNum = $('#winum').val();
        currentSlNo = $('#slno').val();
        savedITRMode = itrMode;

        if (itrMode === "upload") {
            return fetchITRUpload(triggerElement);
        } else {
            var jsonBody = {
                itrPan: itrPan,
                itrDOB: itrDOB,
                itrSMSMobileNo: itrSMSMobileNo,
                itrMode: "sms",
                wiNum: currentWiNum,
                applicantId: currentApplicantId,
                slno: currentSlNo
            };

            return new Promise((resolve, reject) => {
                $.ajax({
                    url: "process/fetchITR",
                    type: "POST",
                    contentType: 'application/json',
                    data: JSON.stringify(jsonBody),
                    success: function (response) {
                        console.log("ITR SMS success response", response);
                        if (itrMode === "sms") {
                            alertmsg("An email has been sent to the customer with a link to the Perfios portal for ITR retrieval. Please ensure the customer completes the process within 48 hours.");
                        }
                        resolve(response);
                    },
                    error: function (xhr, status, error) {
                        var errorMessage = "Error fetching ITR details.";
                        try {
                            var errorObj = JSON.parse(xhr.responseText);
                            errorMessage = errorObj.message || errorMessage;
                        } catch (e) {
                        }

                        alertmsg(errorMessage);
                        reject(error);
                    }
                });
                $('.check-itr-status').show();
            });
        }
    }

    function fetchITRUpload(triggerElement) {
        var itrPan = triggerElement.closest('.det').parent().closest('.tab-pane').find('.kycdetails').find('.pan').val();
        var itrDOB = triggerElement.closest('.det').parent().closest('.tab-pane').find('.kycdetails').find('.pandob').val();
        var itrSMSMobileNo = triggerElement.closest('.det').parent().closest('.tab-pane').find('.basicdetails').find('.basic_mob').val();
        var currentTab = triggerElement.closest('.det').parent();
        currentPAN = itrPan;
        currentDOB = itrDOB;
        currentMobile = itrSMSMobileNo;
// TODO: finacial year arriving to be fixed. currently hardcoded as 2025
        var currentYear = new Date().getFullYear(); //hardcode for now. fix later
        var currentMonth = new Date().getMonth();
        if (currentMonth < 3) {
            currentYear--;
        }
        // var lastTwoYears = [
        //     currentYear + "-" + (currentYear + 1).toString().slice(-2),
        //     currentYear - 1 + "-" + (currentYear).toString().slice(-2)
        // ];
        var lastTwoYears = [
            currentYear + "-" + (currentYear + 1).toString().slice(-2),      // 2025-26
            currentYear - 1 + "-" + (currentYear).toString().slice(-2),      // 2024-25
            currentYear - 2 + "-" + (currentYear - 1).toString().slice(-2),  // 2023-24
            currentYear - 3 + "-" + (currentYear - 2).toString().slice(-2)   // 2022-23
        ];


        var jsonBody = {
            itrPan: itrPan,
            itrDOB: itrDOB,
            itrSMSMobileNo: itrSMSMobileNo,
            itrMode: "upload",
            wiNum: currentWiNum,
            applicantId: currentApplicantId,
            slno: currentSlNo,
            itrYearsList: lastTwoYears,
            form26asYearsList: lastTwoYears,
            form16YearsList: lastTwoYears
        };

        return new Promise((resolve, reject) => {
            $.ajax({
                url: "process/fetchITRUpload",
                type: "POST",
                contentType: 'application/json',
                data: JSON.stringify(jsonBody),
                success: function (response) {
                    var data = JSON.parse(response);
                    if (!data.Response || !data.Response.Body || !data.Response.Body.message || !data.Response.Body.message.url) {
                        reject(new Error("Unexpected response structure"));
                        return;
                    }
                    var url = data.Response.Body.message.url;
                    var modal = currentTab.find('.iframe-modal');
                    var modalTitle = modal.find('.modal-title');
                    var iframe = modal.find('.itr-iframe');
                    var loadingIndicator = modal.find('.loading-indicator');

                    modalTitle.text("ITR File Upload");

                    iframe.on('load', function () {
                        loadingIndicator.hide();
                        iframe.show();
                    });
                    iframe.attr('src', url);
                    modal.data('triggerElement', triggerElement);
                    modal.modal('show');
                    resolve(response);
                },
                error: function (xhr, status, error) {
                    var errorMessage = "Error fetching ITR upload details.";
                    try {
                        var errorObj = JSON.parse(xhr.responseText);
                        errorMessage = errorObj.message || errorMessage;
                    } catch (e) {
                    }

                    alertmsg(errorMessage);
                    reject(error);
                }
            });
        });
    }

    function checkLatestCompletedITRTransactionStatus(triggerElement) {
        var wiNum = $('#winum').val();
        var applicantId = triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val();
        var jsonBody = {
            wiNum: wiNum,
            applicantId: applicantId
        };
        showLoader();

        $.ajax({
            url: "process/getLatestCompletedITRTransactionId",
            type: "POST",
            contentType: 'application/json',
            data: JSON.stringify(jsonBody),
            success: function (response) {
                try {
                    var data = typeof response === 'string' ? JSON.parse(response) : response;
                    console.log("ITR Response is ", data);

                    // First, explicitly check for the error status case
                    if (data.status === "error") {
                        const errorMsg = data.message || "No completed ITR found";
                        notyaltInfo(errorMsg);
                        checkITRStatus(triggerElement);
                        hideLoader();
                        return; // Important: return to prevent further processing
                    }

                    // Check for nested error response in itrData
                    if (data.itrData && data.itrData.status === "error") {
                        const errorMsg = data.itrData.message || "No completed ITR found";
                        notyaltInfo(errorMsg);
                        checkITRStatus(triggerElement);
                        hideLoader();
                        return; // Important: return to prevent further processing
                    }

                    // Check for pending transaction response
                    if (data.Response && data.Response.Body && data.Response.Body.message) {
                        if (data.Response.Body.message.code === "TransactionIdNotCompleted") {
                            notyaltInfo("ITR transaction is still processing. Please check again later.");
                            checkITRStatus(triggerElement);
                            hideLoader();
                            return; // Important: return to prevent further processing
                        }
                    }

                    // Check for explicit failure indicators
                    if (data.success === false || (data.itrData && data.itrData.success === false)) {
                        const errorMsg = data.message || (data.itrData && data.itrData.message) || "ITR validation failed";
                        alertmsg(errorMsg);
                        hideLoader();
                        return; // Important: return to prevent further processing
                    }

                    // Only if we've ruled out all error cases, process the data
                    // Check if we have actual ITR data to process
                    if (data.itrData || data.itrData) {
                        processITRData(data, triggerElement);
                        hideLoader();
                        return;
                    }

                    // Default case if nothing else matched
                    notyaltInfo("Unable to determine ITR status. Please try again later.");
                    checkITRStatus(triggerElement);
                    hideLoader();
                } catch (e) {
                    console.error("Error parsing response:", e);
                    notyaltInfo("Error processing ITR data");
                    hideLoader();
                }
            },
            error: function (xhr, status, error) {
                try {
                    const errorResponse = JSON.parse(xhr.responseText);
                    if (errorResponse.success === false) {
                        alertmsg(errorResponse.message || "ITR validation failed");
                    } else {
                        alertmsg("Error retrieving the latest completed transaction ID. Please try again later.");
                    }
                } catch (e) {
                    alertmsg("Error retrieving the latest completed transaction ID. Please try again later.");
                }
                console.error("Error retrieving the latest completed transaction ID:", status, error);
                hideLoader();
            }
        });
    }

    function showFeedback(message, type = 'info') {
        switch (type) {
            case 'success':
                notyalt(message);
                break;
            case 'error':
                alertmsg(message);
                break;
            case 'warning':
                notyaltWarning(message);
                break;
            default:
                notyaltInfo(message);
        }
    }

    async function checkITRStatus(detElement) {
        const generaldetailsElement = detElement.closest('.det').parent().closest('.tab-pane').find('.generaldetails');
        const applicantId = generaldetailsElement.find('.appid').val();
        const wiNum = $('#winum').val();

        showLoader();
        try {
            const response = await $.ajax({
                url: 'process/itr-status',
                type: 'GET',
                data: {applicantId, wiNum}
            });
            updateUIWithITRStatus(detElement, response);
            hideLoader();
            return response;
        } catch (error) {
            console.error('Error checking ITR status:', error);
            updateUIWithITRStatus(detElement, null); // Show "No active ITR request found" if there's an error
            hideLoader();
            throw error;
        }
    }
    function updateUIWithITRStatus(detElement, status) {
        let statusHtml = '<div class="itr-status-container">';

        if (status) {
            const createdDate = new Date(status.timestamp);
            statusHtml += `
                <div class="status-item">
                    <span class="status-label">Mode:</span>
                    <span class="status-value">${status.itrMode || 'N/A'}</span>
                </div>
                <div class="status-item">
                    <span class="status-label">Created On:</span>
                    <span class="status-value">${createdDate.toLocaleString()}</span>
                </div>
            `;

            if (status.updated) {
                const updatedDate = new Date(status.updated);
                statusHtml += `
                    <div class="status-item">
                        <span class="status-label">Updated On:</span>
                        <span class="status-value">${updatedDate.toLocaleString()}</span>
                    </div>
                `;
            }

            statusHtml += '<div class="status-message">';

            if (status.perfiosStatus) {
                statusHtml += `
                    <p><strong>Status:</strong> ${status.perfiosStatus}</p>
                    <p><strong>Perfios ID:</strong> ${status.perfiosTransactionId || 'Pending'}</p>
                `;
            }

            if (status.message) {
                statusHtml += `<p><strong>Updates:</strong> ${status.message}</p>`;
            }

            if (!status.perfiosStatus && !status.message) {
                let statusMessage = status.itrMode === "upload"
                    ? "Upload Link Generated"
                    : "Link Generated and Sent to Customer";

                statusHtml += `
                    <p><strong>Status:</strong> ${statusMessage}</p>
                    <p><strong>Reference ID:</strong> ${status.generateLinkId || status.clientTxnId || 'N/A'}</p>
                `;

                if (status.itrMode === "upload" && !status.perfiosStatus) {
                    statusHtml += `<p><strong>URL:</strong> ${status.url || 'N/A'}</p>`;
                }

                if (status.itrMode === "sms") {
                    const expiryDate = new Date(createdDate.getTime() + 48 * 60 * 60 * 1000);
                    statusHtml += `<p><strong>Expiry:</strong> <span class="expiry-time">${expiryDate.toLocaleString()}</span></p>`;
                }
            }

            statusHtml += '</div>';

            if (status.delFlg === 'Y') {
                statusHtml += `
                    <div class="status-message" style="border-left-color: #dc3545;">
                        <strong>Note:</strong> This ITR request has been marked as deleted.
                    </div>
                `;
            }
        } else {
            statusHtml += `
                <div class="status-message">
                    <p>No active ITR request found.</p>
                </div>
            `;
        }

        // statusHtml += `
        //     <button class="btn btn-info check-itr-status mt-2">Check Status</button>
        // </div>`;
        statusHtml += `</div>`;

        // Update UI with the generated HTML
        detElement.closest('.det').find('.itrResponse').html(statusHtml);
        detElement.closest('.det').find('.itrMonthlyGrossDiv').show();
    }


    function processITRData(data, triggerElement) {
        console.log("Processing ITR data:", data);
        const employmentType = triggerElement.closest('.det').find('.empl_type').val();

        // Check if the response indicates a failure
        if (!data.success === false) {
            showFeedback(data.message || "ITR validation failed", "error");
            return;
        }

        // Handle different response structures
        let itrDataArray = [];
        let summaryData = {};
        let isAmber = false;
        let amberReason = "";

        // Handle the various possible response structures
        if (data.itrData && data.itrData.itrData) {
            // Original response structure
            itrDataArray = data.itrData.itrData;
            summaryData = data.itrData.summary || {};
        } else if (data.itrData) {
            // New structure (sample-1 format where itrData is nested under itrData)
            itrDataArray = data.itrData.itrData || [];
            summaryData = data.itrData.summary || {};
            isAmber = data.itrData.isAmber || false;
            amberReason = data.itrData.amberReason || "";
        } else {
            // New structure (sample-2 format where itrData is a direct property)
            itrDataArray = data.itrData || [];
            summaryData = data.summary || {};
            isAmber = data.isAmber || false;
            amberReason = data.amberReason || "";
        }

        // Get monthly income from summary if available
        let monthlyIncome = summaryData.monthlyIncome || "";

        // Build the response table HTML
        let tableHtml = '<div class="itr-summary card mb-3">';
        tableHtml += '<div class="card-header bg-light"><strong>ITR Summary</strong></div>';
        tableHtml += '<div class="card-body">';

        // Display summary data if available
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
        tableHtml += '<thead><tr><th>FY</th><th>Form No</th><th>Total Income</th><th>Gross Income</th><th>Filing Date</th></tr></thead>';
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
                tableHtml += '<td>' + totalIncome + '</td>';

                // Get gross income from the correct structure
                let grossIncome = "N/A";
                if (itr.grossTotalIncome) {
                    grossIncome = itr.grossTotalIncome;
                } else if (itr.incomeDetails && itr.incomeDetails.grossTotalIncome) {
                    grossIncome = itr.incomeDetails.grossTotalIncome || "N/A";
                }
                tableHtml += '<td>' + grossIncome + '</td>';

                tableHtml += '<td>' + (itr.dateOfFiling || "N/A") + '</td>';
                tableHtml += '</tr>';
            });
        } else {
            tableHtml += '<tr><td colspan="5" class="text-center">No ITR data available</td></tr>';
        }

        tableHtml += '</tbody>';
        tableHtml += '</table>';
        let section;
        switch (employmentType.toLowerCase()) {
            case 'agriculturist':
                section = triggerElement.closest('.det').find('.agriculturist-section');
                break;
            case 'salaried':
                section = triggerElement.closest('.det').find('.salaried-section');
                break;
            case 'pensioner':
                section = triggerElement.closest('.det').find('.pensioner-section');
                break;
            case 'sep':
                section = triggerElement.closest('.det').find('.sepsenp-section');
                break;
            case 'senp':
                section = triggerElement.closest('.det').find('.sepsenp-section');
                break;
            default:
                // Default to using the general section or parent container
                section = triggerElement.closest('.det');
        }
        section.find('.itrResponse').html(tableHtml);
        section.find('.itrMonthlyGrossDiv').show();
        section.find('.itr-monthly-gross-div').show();
        let inputName = 'itrMonthlyGross';
        if (employmentType.toLowerCase() === 'agriculturist') {
            inputName = 'agriculturistMonthlyGross';
        } else if (employmentType.toLowerCase() === 'pensioner') {
            inputName = 'pensionerMonthlyGross';
        } else if (employmentType.toLowerCase() === 'sep'||employmentType.toLowerCase() === 'senp') {
            inputName = 'sepSenpMonthlyGross';
        }

        section.find(`input[name="${inputName}"]`).val(monthlyIncome);
        calculateFinalAMIITR(section);
        var programBtn = triggerElement.closest('.det').find('.save-button-program');
        programBtn.prop("disabled", false);
    }

    function updateUIAfterITRRequest(response) {
        $('.check-itr-status').show();
        $('.btn-itr').prop('disabled', true);
    }

    function showFeedback(message, type = 'info') {
        switch (type) {
            case 'success':
                notyalt(message);
                break;
            case 'error':
                alertmsg(message);
                break;
            case 'warning':
                notyaltWarning(message);
                break;
            default:
                notyaltInfo(message);
        }
    }

    function closeModal(modalTitle, triggerElement) {
        console.log("Close modal called", modalTitle);
        showLoader();
        if (modalTitle === "ITR File Upload") {
            console.log("ITR Check called");
            checkLatestCompletedITRTransactionStatus(triggerElement);
        }
        hideLoader();
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

    // Public API
    return {
        init: init,
        handleITRRequest: handleITRRequest,
        checkLatestCompletedITRTransactionStatus: checkLatestCompletedITRTransactionStatus
    };
})();

// Initialize the module when the document is ready
$(document).ready(function () {
    HLITRHandler.init();

    // Add specific event handlers for the income JSP page
    $('#loanbody').on('click', '.itr-fetch-btn', function () {
        console.log("ITR fetch button clicked");
        const triggerElement = $(this);
        showMandatoryYearsBeforeAction(triggerElement, "sms");
       // HLITRHandler.handleITRRequest($(this), "sms");
    });

    $('#loanbody').on('click', '.itr-upload-btn', function () {
        console.log("ITR upload button clicked");
        const triggerElement = $(this);
         showMandatoryYearsBeforeAction(triggerElement, "upload");
        //HLITRHandler.handleITRRequest($(this), "upload");
    });

    // When the ITR section is shown, check if there are existing ITR entries
    $('#loanbody').on('change', '.itravailable', function (e) {
        e.preventDefault();
        e.stopPropagation();
        var selected = $(this).val();
        var itrparent = $(this).closest('.col-lg-7').parent().next('.row');
        var itrcode = itrparent.find('.getitr');
        itrcode.val('');
        var programBtn = $(this).closest('.det').find('.save-button-program');
        if (selected === 'Y') {
            itrparent.show();
            var detElement = $(this);
            showLoader();
            hideLoader();
            $(this).closest('.det').find('.itrbutton').show();
            $(this).closest('.det').find('.salaryupd').hide();
            programBtn.prop("disabled", true);
        } else {
            itrparent.hide();
            $(this).closest('.det').find('.itrbutton').hide();
            $(this).closest('.det').find('.salaryupd').show();
            programBtn.prop("disabled", false);
        }
    });



    // Function to check for existing ITR entries

    async function showMandatoryYearsBeforeAction(triggerElement, mode) {
    try {
        // First get the mandatory years info
        const info = await getMandatoryYearsInfoSync(triggerElement);

        if (info && info.success) {
            // Show the info in a modal or alert with proceed option
            const shouldProceed = await showITRRequirementsModal(info, mode);

            if (shouldProceed) {
                // User confirmed, proceed with ITR action
                HLITRHandler.handleITRRequest(triggerElement, mode);
            }
        } else {
            // If we can't get requirements, ask user if they want to proceed anyway
            const proceedAnyway = await confirmmsg("Unable to load ITR requirements. Do you want to proceed anyway?");
            if (proceedAnyway) {
                HLITRHandler.handleITRRequest(triggerElement, mode);
            }
        }
    } catch (error) {
        console.error("Error getting ITR requirements:", error);
        const proceedAnyway = await confirmmsg("Error loading ITR requirements. Do you want to proceed anyway?");
        if (proceedAnyway) {
            HLITRHandler.handleITRRequest(triggerElement, mode);
        }
    }
}
function getMandatoryYearsInfoSync(triggerElement) {
    const employmentType = triggerElement.closest('.det').find('.empl_type').val();
    const totalExperience = triggerElement.closest('.det').find('.total_exp').val();
    const applicantId = triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val();
    const wiNum = $('#winum').val();

    if (!employmentType) {
        showFeedback('Please select employment type first', 'warning');
        return Promise.resolve(null);
    }

    return new Promise((resolve, reject) => {
        $.ajax({
            url: "fetch/getMandatoryYears",
            type: "POST",
            contentType: 'application/json',
            data: JSON.stringify({
                employmentType: employmentType,
                totalExperience: totalExperience,
                applicantId: applicantId,
                wiNum: wiNum
            }),
            success: function (response) {
                resolve(response);
            },
            error: function (xhr, status, error) {
                reject(error);
            }
        });
    });
}

function showITRRequirementsModal(info, mode) {
    return new Promise((resolve) => {
        let actionIcon = mode === 'sms' ? 'fa-envelope' : 'fa-upload';
        let actionText = mode === 'sms' ? 'Email Link' : 'Upload Files';
        let actionColor = mode === 'sms' ? 'success' : 'primary';

        let modalHtml = `
            <div class="modal fade" id="itrRequirementsModal" tabindex="-1" role="dialog" aria-labelledby="itrRequirementsModalLabel" aria-hidden="true" data-backdrop="static">
                <div class="modal-dialog modal-lg modal-dialog-centered" role="document">
                    <div class="modal-content shadow-lg border-0">
                        <!-- Header -->
                        <div class="modal-header bg-gradient-primary text-white border-0" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                            <div class="d-flex align-items-center">
                                <div class="modal-icon-wrapper mr-3">
                                    <i class="fas fa-file-invoice-dollar fa-2x px-1"></i>
                                </div>
                                <div>
                                    <h4 class="modal-title mb-0" id="itrRequirementsModalLabel">ITR Filing Requirements</h4>
                                    <small class="text-light opacity-75">Please review before proceeding</small>
                                </div>
                            </div>
                   
                        </div>
                        
                        <!-- Body -->
                        <div class="modal-body p-0">
                            ${generateEnhancedRequirementsContent(info, mode)}
                        </div>
                        
                        <!-- Footer -->
                        <div class="modal-footer bg-light border-0 justify-content-between">
                            <button type="button" class="btn btn-outline-secondary btn-lg px-4" data-dismiss="modal" id="cancelITRModal">
                                <i class="fas fa-times mr-2"></i>Cancel
                            </button>
                            <button type="button" class="btn btn-${actionColor} btn-lg px-4 shadow-sm" id="proceedWithITR">
                                <i class="fas ${actionIcon} mr-2"></i>Proceed with ${actionText}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;

        // Remove existing modal if any
        $('#itrRequirementsModal').remove();

        // Add new modal to DOM
        $('body').append(modalHtml);
        $('#itrRequirementsModal').modal('show');

        // Initialize and show modal


        // Handle proceed button click
        $('#proceedWithITR').on('click', function() {
            $(this).prop('disabled', true).html('<i class="fas fa-spinner fa-spin mr-2"></i>Processing...');
            $('#itrRequirementsModal').modal('hide');
            setTimeout(() => resolve(true), 300);
        });

        // Handle cancel button click
        $('#cancelITRModal').on('click', function() {
            $('#itrRequirementsModal').modal('hide');
            setTimeout(() => resolve(false), 300);
        });

        // Handle modal close events
        $('#itrRequirementsModal').on('hidden.bs.modal', function(e) {
            // Only resolve false if it wasn't already resolved by proceed button
            if (!$('#proceedWithITR').prop('disabled')) {
                resolve(false);
            }
            $(this).remove();
        });

        // Prevent backdrop click from closing
        // $('#itrRequirementsModal').on('click', function(e) {
        //     if (e.target === this) {
        //         e.stopPropagation();
        //         // Optional: Add a subtle shake animation to indicate modal can't be closed by clicking outside
        //         $('#itrRequirementsModal').find('.modal-content').addClass('animate__animated animate__headShake');
        //         setTimeout(() => {
        //             modal.find('.modal-content').removeClass('animate__animated animate__headShake');
        //         }, 1000);
        //     }
        // });
    });
}

function generateEnhancedRequirementsContent(info, mode) {
    let content = `
        <!-- Alert Section -->
        <div class="px-4 pt-4">
            <div class="alert alert-info border-0 shadow-sm" style="background: linear-gradient(135deg, #e3f2fd 0%, #f1f8e9 100%); border-left: 4px solid #2196f3 !important;">
                <div class="d-flex align-items-center">
                    <i class="fas fa-info-circle text-primary fa-lg mr-3"></i>
                    <div>
                        <h6 class="mb-1 text-primary font-weight-bold px-1">Important Notice</h6>
                        <p class="mb-0 small text-muted px-1">Ensure applicant have filed ITR for the required financial years before proceeding.</p>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Main Content -->
        <div class="px-4 pb-2">
            <div class="row">
                <!-- Employment Details Card -->
                <div class="col-md-6 mb-3">
                    <div class="card border-0 shadow-sm h-100" style="border-left: 4px solid #4caf50 !important;">
                        <div class="card-header bg-light border-0 py-3">
                            <div class="d-flex align-items-center">
                                <div class="rounded-circle bg-success text-white d-flex align-items-center justify-content-center mr-3" style="width: 40px; height: 40px;">
                                    <i class="fas fa-user-tie px-1"></i>
                                </div>
                                <div>
                                    <h6 class="mb-0 font-weight-bold text-dark">Employment Details</h6>
                                    <small class="text-muted">Applicant employment classification</small>
                                </div>
                            </div>
                        </div>
                        <div class="card-body py-3">
                            <div class="mb-3">
                                <label class="small text-muted font-weight-bold">Employment Type</label>
                                <div class="d-flex align-items-center">
                                    <span class="badge badge-success px-3 py-2 font-weight-normal">${info.employmentType}</span>
    `;

    if (info.originalEmploymentType !== info.employmentType) {
        content += `<small class="text-muted ml-2">(Originally: ${info.originalEmploymentType})</small>`;
    }

    content += `
                                </div>
                            </div>
                            <div>
                                <label class="small text-muted font-weight-bold">Years Required</label>
                                <div>
                                    <span class="badge badge-primary px-3 py-2 font-size-sm">
                                        <i class="fas fa-calendar-alt mr-1"></i>${info.yearsRequired} Year(s)
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Required Years Card -->
                <div class="col-md-6 mb-3">
                    <div class="card border-0 shadow-sm h-100" style="border-left: 4px solid #2196f3 !important;">
                        <div class="card-header bg-light border-0 py-3">
                            <div class="d-flex align-items-center">
                                <div class="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center mr-3" style="width: 40px; height: 40px;">
                                    <i class="fas fa-calendar-check px-1"></i>
                                </div>
                                <div>
                                    <h6 class="mb-0 font-weight-bold text-dark">Required Financial Years</h6>
                                    <small class="text-muted">ITR must be filed for these years</small>
                                </div>
                            </div>
                        </div>
                        <div class="card-body py-3">
                            <div class="mb-3">
                                <label class="small text-muted font-weight-bold">Primary Years</label>
                                <div class="d-flex flex-wrap">
                                    <span class="badge badge-primary px-3 py-2 mr-2 mb-1">${info.requiredFY1}</span>
    `;

    if (info.requiredFY2 && info.requiredFY2.trim() !== '') {
        content += `<span class="badge badge-primary px-3 py-2 mb-1">${info.requiredFY2}</span>`;
    }

    content += `
                                </div>
                            </div>
    `;

    if (info.checkAlternate && info.alternateFY1 && info.alternateFY1.trim() !== '') {
        content += `
                            <div>
                                <label class="small text-muted font-weight-bold">Alternate Years (if primary not available)</label>
                                <div class="d-flex flex-wrap">
                                    <span class="badge badge-secondary px-3 py-2 mr-2 mb-1">${info.alternateFY1}</span>
        `;

        if (info.alternateFY2 && info.alternateFY2.trim() !== '') {
            content += `<span class="badge badge-secondary px-3 py-2 mb-1">${info.alternateFY2}</span>`;
        }

        content += `
                                </div>
                            </div>
        `;
    }

    content += `
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Detailed Requirements -->
        <div class="px-4 pb-3">
            <div class="card border-0 bg-light">
                <div class="card-body py-3">
                    <div class="d-flex align-items-start">
                        <i class="fas fa-exclamation-circle text-warning fa-lg mr-3 m-1"></i>
                        <div class="flex-grow-1">
                            <h6 class="font-weight-bold text-dark mb-2">Requirements Summary</h6>
                            <p class="mb-0 text-dark">${info.message}</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;

    // Special alerts
    if (info.isAmberByDefault) {
        content += `
            <div class="px-4 pb-3">
                <div class="alert alert-warning border-0 shadow-sm" style="border-left: 4px solid #ff9800 !important;">
                    <div class="d-flex align-items-center">
                        <i class="fas fa-exclamation-triangle text-warning fa-lg mr-3"></i>
                        <div>
                            <h6 class="mb-1 font-weight-bold text-warning">Manual Review Required</h6>
                            <p class="mb-0 small">This employment type requires additional manual review during processing.</p>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    // if (info.minMonthlyIncome) {
    //     content += `
    //         <div class="px-4 pb-3">
    //             <div class="alert alert-success border-0 shadow-sm" style="border-left: 4px solid #4caf50 !important;">
    //                 <div class="d-flex align-items-center">
    //                     <i class="fas fa-rupee-sign text-success fa-lg mr-3"></i>
    //                     <div>
    //                         <h6 class="mb-1 font-weight-bold text-success">Minimum Income Requirement</h6>
    //                         <p class="mb-0 small">Monthly income should be at least <strong>₹${parseFloat(info.minMonthlyIncome).toLocaleString('en-IN')}</strong></p>
    //                     </div>
    //                 </div>
    //             </div>
    //         </div>
    //     `;
    // }

    // Process information
    content += `
        <div class="px-4 pb-4">
            <div class="card border-0" style="background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); border-left: 4px solid ${mode === 'sms' ? '#28a745' : '#007bff'} !important;">
                <div class="card-body py-3">
                    <div class="d-flex align-items-center">
                        <div class="rounded-circle text-white d-flex align-items-center justify-content-center mr-3" style="width: 45px; height: 45px; background: ${mode === 'sms' ? '#28a745' : '#007bff'};">
                            <i class="fas ${mode === 'sms' ? 'fa-envelope' : 'fa-upload'} fa-lg"></i>
                        </div>
                        <div class="flex-grow-1">
                            <h6 class="mb-1 font-weight-bold text-dark">
                                ${mode === 'sms' ? 'Email Link Process' : 'File Upload Process'}
                            </h6>
                            <p class="mb-0 small text-muted">
                                ${mode === 'sms' 
                                    ? 'A secure link will be sent to the customer\'s registered email address. They will have 48 hours to complete the ITR submission online.' 
                                    : 'You will be redirected to a secure portal where you can directly upload ITR files and supporting documents.'}
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;

    return content;
}


});