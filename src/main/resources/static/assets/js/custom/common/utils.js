function makeAjaxCall(e) {
    // Perform AJAX call
    $.ajax({
        url: 'your_api_endpoint',
        method: 'GET',
        success: function (response) {
            // Check response and decide whether to permit moving to the next step
            if (response.allowed) {
                e.goNext(); // Proceed to the next step
            } else {
                // Show error message or take appropriate action
                alertmsg('You are not permitted to proceed!');
            }
        },
        error: function () {
            // Handle error
            alertmsg('Error occurred while fetching data!');
            e.goNext();
        }
    });
}
function UidValidate(e) {
    // Perform AJAX call
    $.ajax({
        url: 'your_api_endpoint',
        method: 'GET',
        success: function (response) {
            // Check response and decide whether to permit moving to the next step
            if (response.allowed) {
                e.goNext(); // Proceed to the next step
            } else {
                // Show error message or take appropriate action
                alertmsg('You are not permitted to proceed!');
            }
        },
        error: function () {
            // Handle error
            alertmsg('Error occurred while fetching data!');
            e.goNext();
        }
    });
}


function alertmsg(msg){
    Swal.fire({
        text: msg,
        icon: "error",
        buttonsStyling: !1,
        confirmButtonText: "Ok!",
        customClass: {
            confirmButton: "btn btn-light"
        }
    }).then((function() {
        KTUtil.scrollTop()
    }))
}


function otp(){
    $('.pin-input').each(function () {
        $(this).attr('maxlength', 1); // Ensures a single digit per input
        $(this).on('input', function (e) {
            var value = $(this).val();
            // Remove any non-numeric characters
            var numericValue = value.replace(/[^\d]/g, '');
            $(this).val(numericValue); // Set the sanitized numeric value
            // Auto-focus logic
            if (numericValue) {
                $(this).next('.pin-input').focus(); // Move to next field if current field has value
            } else {
                $(this).prev('.pin-input').focus(); // Move to previous field if current field is cleared
            }
        });
    });
}