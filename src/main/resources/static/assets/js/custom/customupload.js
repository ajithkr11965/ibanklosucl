$(document).ready(function () {

    function handleFileUpload(event) {
        event.preventDefault(); // Prevent default form submission

        const fileInput = document.getElementById("fileInput").files[0];

        if (!fileInput) {
            alertmsg("Please select a file.");
            hideLoader();
            return;
        }

        if ($('#winum').val().length<=0) {
            alertmsg("Please enter the WI number.");
            hideLoader();
            return;
        }
        if ($('#foldername').val().length<=0) {
            alertmsg("Please select the Folder Name.");
            hideLoader();
            return;
        }

        // Check if the file size is greater than 5MB
        const fileSizeInMB = fileInput.size / (1024 * 1024);
        if (fileSizeInMB > 5) {
            hideLoader();
            alertmsg("File size exceeds 5MB limit. Please select a smaller file.");
            return;
        }

        const fileExtension =  fileInput.name.split('.').pop();
        // Remove the file extension and sanitize the file name (replace non-alphanumeric characters with '_')
        const fileNameWithoutExtension = fileInput.name.replace(/\.[^/.]+$/, ''); // Removes the file extension
        const sanitizedFileName = fileNameWithoutExtension.replace(/[^a-zA-Z0-9]/g, '_'); // Sanitizes file name


        // Read file as Base64 and handle via Promise
        readFileAsBase64(fileInput)
            .then(base64File => sendFileToBackend(base64File, sanitizedFileName, fileExtension))
            .then(response => {
                alertmsg(response.msg)
                }
            )
            .catch(error => alertmsg("Error uploading file: " + error));
    }

    // Function to read the file as Base64, wrapped in a Promise
    function readFileAsBase64(file) {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();

            // Resolve the promise on successful file read
            reader.onload = function(event) {
                const base64File = event.target.result.split(',')[1]; // Extract base64 part
                resolve(base64File);
            };

            // Reject the promise on error
            reader.onerror = function() {
                reject("Error reading file.");
            };

            reader.readAsDataURL(file); // Start reading the file as DataURL
        });
    }

    // Function to send the file as base64 to the backend via jQuery AJAX
    function sendFileToBackend(base64File, fileName, fileExtension) {
        return new Promise((resolve, reject) => {
            $.ajax({
                url: 'api/customuploadsave', // Adjust your backend URL
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    docRequest: {
                        fileName: fileName,
                        fileExtension: fileExtension,
                        base64Data: base64File,
                        winum: $('#winum').val(),
                        foldername: $('#foldername').val(),
                        remarks: $('#remarks').val()
                    }
                }),
                success: function(response) {
                    hideLoader();
                    resolve(response); // Resolve the promise on success
                },
                error: function(xhr, status, error) {
                    hideLoader();
                    reject("Failed to upload the file: " + error); // Reject the promise on error
                }
            });
        });
    }
    $('#custdocbtn').click(function (event) {
        confirmmsg("The file will get uploaded to selected folder & It cannot be replaced or deleted Once Uploaded. Do you want to proceed?").then(function (confirmed) {
            if (confirmed) {
                showLoader();
                handleFileUpload(event);
            }
        });
    });


    // $('#custdocbtn').click(function () {
    //     confirmmsg("The file will get uploaded to selected folder. Do you want to proceed?").then(function (confirmed) {
    //         if (confirmed) {
    //             showLoader();
    //             var jsonBody = {
    //                 winum: $('#winum').val(),
    //                 foldername: $('#foldername').val(),
    //                 filename: $('#filename').val(),
    //                 remarks: $('#remarks').val()
    //             };
    //             $.ajax({
    //                 url: 'api/customuploadsave',
    //                 type: 'POST',
    //                 //data: JSON.stringify(jsonBody),
    //                 data: {
    //                     winum: $('#winum').val(),
    //                     foldername: $('#foldername').val(),
    //                     filename: $('#filename').val(),
    //                     remarks: $('#remarks').val()
    //                 },
    //                 async: false,
    //                 //contentType: 'application/json',
    //                 success: function (response) {
    //                     hideLoader();
    //                     alertmsg(response.msg);
    //                     $('#winum').val('');
    //                     $('#remarks').val('');
    //                     $('#foldername').val('');
    //                     $('#filename').val('');
    //                 },
    //                 error: function (error) {
    //                     hideLoader();
    //                     alertmsg("Error, please check the WI number");
    //                 }
    //             });
    //         }
    //     });
    // });

    function formatInput(value) {

        // Check if the input is already in the correct format 'VLR_' followed by exactly 9 digits
        const formattedPattern = /^VLR_\d{9}$/;
        if (formattedPattern.test(value)) {
            return value ; // Do nothing if already formatted
        }

        // Remove any non-numeric characters and get the numeric part
        let numericPart = value.replace(/\D/g, '');

        // If there is a number, format it with 'VLR_' prefix and pad it to 9 digits
        if (numericPart.length > 0) {
            value = 'VLR_' + numericPart.padStart(9, '0');
        }

        // Set the formatted value back to the input field
        return  value;
    }

    $('#winum').change(function () {
        showLoader();
        $(this).val(formatInput($(this).val()));
        var select = $('#foldername');
        select.empty();
        var jsonBody = {
            winum: $('#winum').val()

        };
        $.ajax({
            url: 'api/wifolders',
            type: 'GET',
            //data: JSON.stringify(jsonBody),
            data: {
                winum: $('#winum').val()
            },
            async: false,
            //contentType: 'application/json',
            success: function (response) {
                hideLoader();

                if (response.status == 'S') {
                    var jsonResponse = JSON.parse(response.msg);
                    console.log(jsonResponse);
                    select.empty();
                    select.append($('<option>', {
                        value: '',
                        text: '--Please select folder--'
                    }));
                    select.append($('<option>', {
                        value: 'NA',
                        text: 'Parent Folder'
                    }));
                    jsonResponse.forEach(function (item) {
                        select.append($('<option>', {
                            value: item.folderName,
                            text: item.appName + ' (' + item.folderName + ')'
                        }));
                    });

                } else {
                    alertmsg(response.msg);
                    $('#winum').val('');
                }

            },
            error: function (error) {
                hideLoader();
                alertmsg("Error, please check the WI number");
            }
        });

    });
});



var swalInit = swal.mixin({
    buttonsStyling: false,
    confirmButtonClass: 'btn bg-danger text-white',
    cancelButtonClass: 'btn btn-light'
});
function confirmmsg(msg) {
    return swalInit.fire({
        title: msg,
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Yes",
        cancelButtonText: "No",
        button: "close!"
    }).then(function (result) {
        return result.isConfirmed;
    });
}
function alertmsg(Msg) {
    swalInit.fire({
        html:
            '<div class="d-inline-flex p-2 mb-3 mt-1">' +
            '<img src="assets/images/siblogo.png" class="h-48px" alt="logo">' +
            '</div>' +
            '<h5 class="card-title">' + Msg + '</h5>' +
            '</div>',
        showCloseButton: true
    });
}
