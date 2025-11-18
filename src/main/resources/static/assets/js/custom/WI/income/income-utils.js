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