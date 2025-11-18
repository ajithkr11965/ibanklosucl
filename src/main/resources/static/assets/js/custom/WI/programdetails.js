$(document).ready(function () {
    function toggleLoanProgram() {
        var incomeConsideration = $(this).closest('.det').find('select[name^="incomeConsideration"]').val();
        var loanProgramSection = $(this).closest('.det').find('.loanProgramSection');

        if (incomeConsideration === "yes") {
            loanProgramSection.show();
        } else {
            loanProgramSection.hide();
            $(this).closest('.det').find('.itrSection').hide();
        }
    }

    function toggleITRSection() {
        var loanProgram = $(this).closest('.det').find('select[name^="loanProgram"]').val();
        var itrSection = $(this).closest('.det').find('.itrSection');

        if (loanProgram === "income" && itrSection.length) {
            itrSection.show();
        } else if (itrSection.length) {
            itrSection.hide();
        }
    }

    function toggleITROptions() {
        var itrAvailability = $(this).closest('.det').find('select[name^="itrAvailability"]').val();
        var itrOptionsSection = $(this).closest('.det').find('.itrOptionsSection');

        if (itrAvailability === "yes" && itrOptionsSection.length) {
            itrOptionsSection.show();
        } else if (itrOptionsSection.length) {
            itrOptionsSection.hide();
        }
    }


    function checkStatus() {
        // Make an AJAX request to check the status
        $.ajax({
            url: "checkStatus",
            type: "GET",
            success: function (response) {
                // Handle the success response
                // Display the status or update the page content
                alert("Status checked successfully.");
            },
            error: function (xhr, status, error) {
                // Handle the error scenario
                // Display an error message to the user
                alert("Error checking status.");
            }
        });
    }

    // Event listeners
    $('#loanbody').on('change', '.incomeConsideration', toggleLoanProgram);
    $('#loanbody').on('change', '.loanProgram', toggleITRSection);
    $('#loanbody').on('change', '.itrAvailability', toggleITROptions);


});

$('#loanbody').on('click', '.btn-itr', function () {
    console.log("fetch itr");
    fetchITR($(this));
});


function fetchITR(triggerElement) {
    var itrPan = triggerElement.closest('.det').find('input[name^="incomePAN"]');
    var itrDOB = triggerElement.closest('.det').find('input[name^="incomeDOB"]').val();
    var itrSMSMobileNo = triggerElement.closest('.det').find('input[name^="incomeMOB"]').val();
    var itrMode = "upload";
    var name = triggerElement.attr('name');
    console.log("in fetch itr" + name);
    var jsonBody = {
        itrPan: itrPan,
        itrDOB: itrDOB,
        itrSMSMobileNo: itrSMSMobileNo,
        itrMode: itrMode
    };
    // Make an AJAX request to fetch ITR details
    $.ajax({
        url: "api/fetchITR",
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(jsonBody),
        success: function (response) {
            console.log("in success response")
            // Parse the response JSON
            var data = JSON.parse(response);
            // Extract the URL from the response
            var url = data.Response.Body.message.url;
            console.log(url);


            var currentTab = triggerElement.closest('.det').parent();
            var modal = currentTab.find('.iframe-modal');
            var iframe = modal.find('.itr-iframe');
            var loadingIndicator = modal.find('.loading-indicator');

            iframe.on('load', function () {
                loadingIndicator.hide();
                iframe.show();
            });

            iframe.attr('src', url); // Set the fetched URL here

            // Show the modal
            modal.modal('show');


            // document.getElementsByName("urlContainer").empty().append(iframe);
        },
        error: function (xhr, status, error) {
            var currentTab = triggerElement.closest('.det').parent();
            var modal = currentTab.find('.iframe-modal');
            var iframe = modal.find('.itr-iframe');
            // var loadingIndicator = modal.find('.loading-indicator');
            // Handle the error scenario
            // Display an error message to the user
            iframe.html("Error fetching ITR details.");
            modal.modal('show');
        }
    });
}

