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