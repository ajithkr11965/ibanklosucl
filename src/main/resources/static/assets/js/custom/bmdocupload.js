$(document).ready(function() {

    if($('#man_count').val()*1=='0'){
        $('#bmsave').attr('disabled',false);
    }


    $('#backbtn').on('click', function(event) {
        $('#back_form').attr('method','POST');
        $('#back_form').attr('action','wicreate');
        $('#back_form').submit();
    });
    $('#vl_owner_status').on('change', function(event) {
        var value=$(this).val();
        if(value==='N'){
            $('#vl_first_proof_file').attr('data-mandatory','Y');
            $('#mand3').removeClass('d-none');
            $('#mand4').addClass('d-none');
        }
        else{
            $('#vl_first_proof_file').attr('data-mandatory','N');
            $('#mand3').addClass('d-none');
        }
    });

    $('#bmsave').on('click', function(event) {
        event.preventDefault();
        let valid = true;
        if($('#vl_owner').val().length<=0){
            alertmsg('Please Select Vehicle Owner');
            return;
        }
        if($('#vl_owner_status').val().length<=0){
            alertmsg('Please Select Whether Vehicle Owner First Time Buyer');
            return;
        }

        $('input[type="file"]').each(function() {
            const fileInput = $(this);
            const isMandatory = fileInput.attr('data-mandatory');
            const label = fileInput.data('label');
            const files = fileInput[0].files;
            const fileName = files.length ? files[0].name : '';
            const fileExtension = fileName.split('.').pop().toLowerCase();
            const allowedExtensions = ['pdf', 'jpg'];

            if (isMandatory==='Y' && files.length === 0) {
                alertmsg('Kindly attach a file for '+label);
                valid = false;
                return false; // break out of each loop
            }

            if (files.length > 0 && !allowedExtensions.includes(fileExtension)) {
                alertmsg('Only PDF and JPG files are allowed.');
                valid = false;
                return false; // break out of each loop
            }
            if (files.length > 0 && files[0].size>maxFileSize) {
                alertmsg('Maximum File Size allowed is 5mb.');
                valid = false;
                return false; // break out of each loop
            }
        });

        if (!valid) {
            return;
        }

        var SendInfo={
            slno : $('#slno').val(),
            winum:$('#winum').val(),
            vlowner:$('#vl_owner').val(),
            vlownerstatus:$('#vl_owner_status').val(),
            remarks:$('#remarks').val()
        }
        if(SendInfo.remarks<=0){
            alertmsg('Kindly Enter Remarks');
        }
        else {
            showLoader();
            $.ajax({
                url: 'api/bmsave',
                type: 'POST',
                data: SendInfo,
                success: function (response) {
                    hideLoader();
                    if (response.status === 'S') {
                        confirmmsg('Record Saved Successfully' + $('#winum').val()).then(function (confirmed) {
                            $('#back_form').submit();
                        });
                    } else {
                        hideLoader();
                        alertmsg('Failed: ' + response.msg);
                    }
                },
                error: function (xhr, status, error) {
                    hideLoader();
                    alertmsg('An error occurred: ' + error);
                }
            });
        }

    });
    $('#upload').on('click', function(event) {
        event.preventDefault();
        let valid = true;
        var maxFileSize=$('#maxFileSize').val();
        let form = $('#fileUploadForm')[0];
        let formData = new FormData(form);
        if($('#vl_owner').val().length<=0){
            alertmsg('Please Select Vehicle Owner');
            return;
        }
        if($('#vl_owner_status').val().length<=0){
            alertmsg('Please Select Whether Vehicle Owner First Time Buyer');
            return;
        }

        $('input[type="file"]').each(function() {
            const fileInput = $(this);
            const isMandatory = fileInput.attr('data-mandatory');
            const label = fileInput.data('label');
            const files = fileInput[0].files;
            const fileName = files.length ? files[0].name : '';
            const fileExtension = fileName.split('.').pop().toLowerCase();
            const allowedExtensions = ['pdf', 'jpg'];

            if (isMandatory==='Y' && files.length === 0) {
                alertmsg('Kindly attach a file for '+label);
                valid = false;
                return false; // break out of each loop
            }

            if (files.length > 0 && !allowedExtensions.includes(fileExtension)) {
                alertmsg('Only PDF and JPG files are allowed.');
                valid = false;
                return false; // break out of each loop
            }
            if (files.length > 0 && files[0].size>maxFileSize) {
                alertmsg('Maximum File Size allowed is 5mb.');
                valid = false;
                return false; // break out of each loop
            }
        });

        if (!valid) {
            return;
        }
        else{
            showLoader();
            $.ajax({
                url: 'api/bmdocuploadsave',
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function(response) {
                    hideLoader();
                    if (response.status === 'S') {
                        $('#toggleList').trigger('click');
                        $('#bmsave').attr('disabled',false);
                        alertmsg('Files uploaded successfully.');
                    } else {
                        alertmsg('File upload failed: ' + response.msg);
                    }
                },
                error: function(xhr, status, error) {
                    hideLoader();
                    if (error.responseJSON) {
                        alertmsg( error.responseJSON.msg);
                    } else {
                        alertmsg('Error saving Data '+error);
                    }
                }
            });

        }
    });

    var $docList = $('#docList');
    $docList.hide();
    $('#toggleList').on('click', function () {
        var isVisible = $docList.is(':visible');
        $('#appList').removeClass('w-100');
        if (!isVisible) {
            var jsonBody = {
                slno: $('#slno').val(),
                winum: $('#winum').val(),
                reqtype:"A-1"
            };
            $.ajax({
                url: 'api/bpm-parent',
                type: 'POST',
                data: JSON.stringify(jsonBody),
                async:false,
                contentType: 'application/json',
                success: function (response) {
                    if(response.status==='S') {
                        $('#docList').html('');
                        $('#docList').append('<iframe frameborder="0" class="w-100 h-100 border-0"  src="'+response.msg+'"></iframe>')
                        $docList.show(); // Make the document list visible using jQuery
                        // Initialize Split.js if not already initialized
                        if (!$docList.data('initialized')) {
                            Split(['#appList', '#docList'], {
                                sizes: [50, 50], // Initial split ratio
                                minSize: [200, 200], // Minimum size of each pane
                                gutterSize: 10, // Width of the gutter
                                cursor: 'col-resize', // Cursor type on gutter hover
                                gutter: function (index, direction) {
                                    var gutter = $('<div>', {
                                        'class': 'gutter gutter-' + direction
                                    });
                                    return gutter[0]; // Return DOM element from jQuery object
                                }
                            });
                            $docList.data('initialized', true); // Mark as initialized using jQuery
                        }
                    }
                    else{
                        alertmsg(response.msg);
                    }
                },
                error: function (error) {
                    alertmsg("Error bpM !!!");
                    hideLoader();
                }
            });
        } else {
            $docList.hide(); // Hide the document list using jQuery
            $('.gutter').remove(); // Remove gutter using jQuery
            $docList.removeData('initialized'); // Reset initialization flag using jQuery
            $('#appList').addClass('w-100'); // Add class using jQuery
        }
    });
});
var swalInit = swal.mixin({
    buttonsStyling: false,
    confirmButtonClass: 'btn bg-indigo text-white',
    cancelButtonClass: 'btn btn-light'
});
function alertmsg(Msg){
    swalInit.fire({
        html:
            '<div class="d-inline-flex p-2 mb-3 mt-1">' +
            '<img src="assets/images/siblogo.png" class="h-48px" alt="logo">' +
            '</div>' +
            '<h5 class="card-title">'+Msg+'</h5>' +
            '</div>',
        showCloseButton: true
    });
}
// function showLoader()
// {
//     $('.hideloader').show();
// }
// function hideLoader(){
//     $('.hideloader').hide();
// }
function confirmmsg(msg) {
    return swalInit.fire({
        title: msg,
        icon: "warning",
        showCancelButton: true,
        showConfirmButton:false,
        cancelButtonText: "Ok",
        button: "close!"
    }).then(function (result) {
        return result.isConfirmed;
    });
}