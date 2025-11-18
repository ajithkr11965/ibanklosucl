var swalInit = swal.mixin({
    buttonsStyling: false,
    confirmButtonClass: 'btn bg-danger text-white',
    cancelButtonClass: 'btn btn-light'
});

function alertmsgframe() {
    $('#alert_modal .modal-header').removeClass('bg-danger').addClass('bg-success');
    $('#alert_modal .modal-header').find('.modal-title').text('Remarks');
    $('#alert_modal .modal-body').html('<iframe id="modalIframe" src="remarks?slno=' + $('#slno').val() + '" width="100%" height="400" frameborder="0"></iframe>');
    $('#alert_modal').modal('show');
}

$(document).ready(function () {

    if($('#bureauBlock')){
        if($('#bureauBlock').val()==='Y'){
            alertmsg("Bureau has been fetched before 45 days,Kindly send back the WI")
        }
    }

    $("#remarkHist").on('click', function (e) {
        e.preventDefault();
        e.stopPropagation();
        alertmsgframe();
    });
    $('#backbtn').on('click', function (e) {
        e.preventDefault();
        $('#slnobk').val($('#slno').val());
        if ($('#queue').val() === 'BC')
            $('#redirecturl').val('bclist');
        else if ($('#queue').val() === 'BD')
            $('#redirecturl').val('doclist');
        else if ($('#queue').val().includes('RC'))
            $('#redirecturl').val('rbcpcchecker');
        else if ($('#queue').val().includes('WAIVE'))
            $('#redirecturl').val('waiverlist');
        else
            $('#redirecturl').val('dashboard');
        $('#backform').submit();
    });

    if ($('#lockflg').val() === 'Y') {
        $('button').not('#backbtn').remove();
        $('.save-button').remove();
        $('.edit-button').remove();
        $('.comon').remove(); // remove loan,vehicle,eligible tab save button
        $('.closeapplicant').remove(); // to remove close option in Co Applicant
        $('#addcoapp').remove();
        $('#addguarantor').remove();
        alertmsg('This workitem is locked by PPC ' + $('#lockuser').val());
    }
    $('.no-browse').hide();
    $('.details').on('click', function (e) {
        e.preventDefault();
        let datacode = $('form.det[data-code=' + getapp() + '-' + $(this).attr('data-code').slice(-1) + ']')
        $('.details').removeClass('active');
        $(this).addClass('active');
        showForm();

    });

    var $docList = $('#docList');
    $docList.hide();
    var bpmerror = $('#bpmerror').val();
    if (bpmerror.length > 0) {
        alertmsg(bpmerror);
    } else {
        $('#toggleList , #parentToggle').on('click', function () {
            var isVisible = $docList.is(':visible');
            $('#appList').removeClass('w-100');
            var id = $(this).attr('id');
            var sendUrl;
            if (id === 'toggleList')
                sendUrl = 'api/co-app-bpm';
            else
                sendUrl = 'api/getParentBpm';
            if (!isVisible) {
                var jsonBody = {
                    slno: $('#slno').val(),
                    winum: $('#winum').val(),
                    reqtype: 'A-1'
                };
                $.ajax({
                    url: sendUrl,
                    type: 'POST',
                    data: JSON.stringify(jsonBody),
                    async: false,
                    contentType: 'application/json',
                    success: function (response) {
                        if (response.status === 'S') {
                            $('#docList').html('');
                            $('#docList').append('<iframe frameborder="0" class="w-100 h-100 border-0"  src="' + response.msg + '"></iframe>')
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
                        } else {
                            alertmsg(response.msg);
                        }
                    },
                    error: function (error) {
                        alertmsg("Error bpM !!!");
                        hideLoader();
                        coApplicantCount--;
                    }
                });
            } else {
                $docList.hide(); // Hide the document list using jQuery
                $('.gutter').remove(); // Remove gutter using jQuery
                $docList.removeData('initialized'); // Reset initialization flag using jQuery
                $('#appList').addClass('w-100'); // Add class using jQuery
            }
        });
    }

    $('#loanapp').on('click', '.apptype', function (e) {
        e.preventDefault();
        $('.apptype').removeClass('show active alert-primary');
        $(this).addClass('show active  alert-primary');
        showForm();
    });
    disableFormInputs($('#loanbody'));


    var currentTab = $('#currentTab').val();
    $('.apptype').removeClass('show active alert-primary');
    $('.tab-pane').removeClass('show active');
    $('#tab-' + parseCode(currentTab, 'app')).addClass('show active');
    $('a.apptype[data-app="' + parseCode(currentTab, 'app') + '"]').addClass('show active  alert-primary');
    $('.details').removeClass('active');
    $('.details[data-code="' + parseCode(currentTab, 'det') + '"]').addClass('active');
    $('form.det[data-code="' + currentTab + '"]').show();

    FileUpload.init();


    $('#loanbody').on('click', '.losdedup-button', function (e) {
        console.log("losdedup event for the applicant" + $('#winum').val());
        e.preventDefault();
        e.stopPropagation();

        var here = $(this);
        //here.attr("data-kt-indicator", "on");
        var mob = here.closest('.det').find('.basic_mob').val();
        var email = here.closest('.det').find('.basic_email').val();
        if (mob.length <= 1 || email.length <= 1) {
            alertmsg('Kindly Enter Mobile Number And Email');
        } else {
            showLoader();
            var data = {
                winum: $('#winum').val(),
                slno: $('#slno').val(),
                appid: here.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
                mobno: mob,
                email: email
            }
            $.ajax({
                url: 'api/los-dedupe',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: function (response) {
                    if (response.status === 'S') {
                        let rows = '';
                        var data = JSON.parse(response.msg);
                        data.forEach(item => {
                            rows += `<tr class="bg-light-whitetr">
                                    <td>${item.wi_name || ''}</td>
                                    <td>${item.custName || ''}</td>
                                    <td>${item.loanType || ''}</td>
                                    <td>${item.wiStatus || ''}</td>
                                    <td>${item.AppType || ''}</td>
                                    <td>${item.rejectReason || ''}</td>
                                    <td>${item.doRemarks || ''}</td>
                                    <td>${item.DOB || ''}</td>
                                    <td>${item.aadhaar || ''}</td>
                                    <td>${item.panNo || ''}</td>
                                    <td>${item.voterID || ''}</td>
                                    <td>${item.passportNo || ''}</td>
                                    <td>${item.driveLic || ''}</td>
                                    <td>${item.gstNo || ''}</td>
                                    <td>${item.CorpID || ''}</td>
                                </tr>`;
                        });
                        here.closest('.det').find('.losdedupetable').find('tbody').html(rows);
                        here.closest('.det').find('.loscount').val(data.length);
                        here.closest('.det').find('.losflag').val('true');
                        hideLoader();
                    } else if (response.status === 'Y') {
                        hideLoader();
                        here.closest('.det').find('.losdedupetable').find('tbody').html('<tr  class="bg-light-whitetr"><td colspan="15"><b>' + response.msg + '</b></td></tr>');
                        here.closest('.det').find('.losflag').val('true');
                    } else {
                        alertmsg(response.msg);
                        hideLoader();
                    }
                },
                error: function (xhr) {
                    hideLoader();
                    alertmsg('Error: ' + xhr.responseText);
                }
            });
        }
        here.removeAttr("data-kt-indicator");
    });

    $('#loanbody').on('click', '.findedup-button', function (e) {
        e.preventDefault();
        e.stopPropagation();
        var here = $(this);
        var mob = here.closest('.det').find('.basic_mob').val();
        var email = here.closest('.det').find('.basic_email').val();
        if (mob.length <= 1 || email.length <= 1) {
            alertmsg('Kindly Enter Mobile Number And Email');
            hideLoader();
        } else {
            showLoader();
            var data = {
                winum: $('#winum').val(),
                slno: $('#slno').val(),
                appid: here.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
                mobno: mob,
                email: email
            }
            $.ajax({
                url: 'api/fin-dedupe',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: function (response) {
                    if (response.status === 'S') {
                        let rows = '';
                        var data = JSON.parse(response.msg);
                        data.forEach(item => {
                            rows += `<tr class="bg-light-whitetr">
                                    <td><input type="radio" name="dedupcustid" class="form-check-input dedupcustid" value="${item.customerid}" checked/></td>
                                    <td>${item.customerid}</td>
                                    <td>${item.name || ''}</td>
                                    <td>${item.emailid || ''}</td>
                                    <td>${item.mobilephone || ''}</td>
                                    <td>${item.voterid || ''}</td>
                                    <td>${item.aadhar_ref_no || ''}</td>
                                    <td>${item.pan || ''}</td>
                                    <td>${item.dob ? item.dob.substring(0, 10) : ''}</td>
                                    <td>${item.tds_customerid || ''}</td>
                                </tr>`;
                        });
                        here.closest('.det').find('.fincount').val(data.length);
                        here.closest('.det').find('.finflag').val('true');
                        here.closest('.det').find('.findedupetable').find('tbody').html(rows);
                        hideLoader();
                    } else if (response.status === 'Y') {
                        hideLoader();
                        here.closest('.det').find('.finflag').val('true');
                        here.closest('.det').find('.findedupetable').find('tbody').html('<tr  class="bg-light-whitetr"><td colspan="10"><b>' + response.msg + '</b></td></tr>');
                    } else {
                        alertmsg(response.msg);
                        hideLoader();
                    }
                },
                error: function (xhr) {
                    hideLoader();
                    alertmsg('Error: ' + xhr.responseText);
                }
            });
        }
    });


    $('.checker-sub-init').on('click', function (e) {
        console.log("checker init called");
        e.preventDefault();
        var vehicleLoanMasterId = $('#slno').val();
        var winum = $('#winum').val();
        var recommendedAmount = $('#loan-amount-recommend').val();
        var lockflg = $('#lockflg').val();
        if (!vehicleLoanMasterId || !winum || !recommendedAmount) {
            swalInit.fire({
                title: 'Error',
                text: 'Missing required fields. Please ensure all fields are filled.',
                icon: 'error',
                confirmButtonText: 'OK'
            });
            return;
        }
        showLoader();
        $.ajax({
            url: 'api/checker/validate',
            type: 'POST',
            data: {vehicleLoanMasterId: vehicleLoanMasterId},
            success: function (response) {
                // If validation succeeds, update the recommended amount
                $.ajax({
                    url: 'api/checker/update-recommended-amount',
                    type: 'POST',
                    data: {
                        wiNum: winum,
                        slno: vehicleLoanMasterId,
                        recommendedAmount: recommendedAmount
                    },
                    success: function (updateResponse) {

                        // If update succeeds, process the loan
                        Swal.fire({
                            title: 'Success',
                            text: 'Details validated successfully.',
                            icon: 'success',
                            confirmButtonText: 'OK'
                        }).then((result) => {
                            if (result.isConfirmed) {
                                showLoader();
                                var form = $('<form action="wichecker2" method="post">' +
                                    '<input type="hidden" name="action" value="BC">' +
                                    '<input type="hidden" name="slno" value="' + vehicleLoanMasterId + '">' +
                                    '<input type="hidden" name="winum" value="' + winum + '">' +
                                    '<input type="hidden" name="lockflg" value="' + lockflg + '">'
                                );
                                $('body').append(form);
                                form.submit();
                                hideLoader();
                            }
                        });
                    },
                    error: function (xhr) {
                        hideLoader();
                        displayStructuredError("Error updating recommended amount: " + xhr.responseText);
                    }
                });
            },
            error: function (xhr) {
                hideLoader();
                displayStructuredError(xhr.responseText);
            }
        });

        // $.ajax({
        //     url: 'api/checker/process',
        //     type: 'POST',
        //     data: { vehicleLoanMasterId: slno },
        //     success: function(response) {
        //         alert('Vehicle loan processed successfully.');
        //     },
        //     error: function(xhr) {
        //          displayStructuredError(xhr.responseText);
        //     }
        // });
    });
    $('#checker_forward').on('click', function (e) {
        e.preventDefault();
        toggleActionButtons(true);
        handleAction('forward');
    });

    $('#checker_reject').on('click', function (e) {
        e.preventDefault();
        let remarks = $('#checker_remarks').val().trim();
        if (remarks === '') {
            alertmsg('Please provide remarks before proceeding.');
            return false;
        } else {
            toggleActionButtons(true);
            processAction('reject');
        }
    });

    $('#checker_sendback').on('click', function (e) {
        e.preventDefault();

        let remarks = $('#checker_remarks').val().trim();
        if (remarks === '') {
            alertmsg('Please provide remarks before proceeding.');
            return false;
        } else {
            toggleActionButtons(true);
            processAction('sendback');
        }
    });
    $('#hunter_forward').on('click', function (e) {
        e.preventDefault();

        let remarks = $('#checker_remarks').val().trim();
        if (remarks === '') {
            alertmsg('Please provide remarks before proceeding.');
            return false;
        } else {
            toggleActionButtons(true);
            processAction('hunter-forward');
        }
    });

});

function handleAction(action) {
    let errors = validateCheckerAction();
    if (errors.length > 0) {
        swalInit.fire({
            title: 'Error!',
            text: errors.join('\n'),
            icon: 'error',
            confirmButtonText: 'OK'
        });
        toggleActionButtons(false);
        return;
    }

    processAction(action);
}

function processAction(action) {
    let vehicleLoanMasterId = $('#slno').val();
    let wiNum = $('#winum').val();
    let lockflg = $('#lockflg').val();
    let remarks = $('#checker_remarks').val().trim();
    let decision;
    if (action === "forward") {
        decision = "FW";
    } else if (action === "reject") {
        decision = "BCREJ";
    } else if (action === "sendback") {
        decision = "SB";
    }  else if (action === "hunter-forward") {
        decision = "HU";
    }
    showLoader();
    $.ajax({
        url: `api/checker/checker-submit`,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            slno: vehicleLoanMasterId,
            winum: wiNum,
            lockflg: lockflg,
            remarks: remarks,
            decision: decision
        }),

        success: function (response) {
            hideLoader();
            swalInit.fire({
                title: 'Success!',
                text: `Application ${action}ed successfully.`,
                icon: 'success',
                confirmButtonText: 'OK'
            }).then((result) => {
                if (result.isConfirmed) {
                    showLoader();
                    window.location.href = 'bclist';
                    hideLoader();
                }
            });
        },
        error: function (xhr) {
            toggleActionButtons(false);
            if (xhr.status === 400 && Array.isArray(xhr.responseJSON)) {
                // Display detailed error messages
                let errorHtml = '<ul>';
                xhr.responseJSON.forEach(function (error) {
                    errorHtml += `<li>${error}</li>`;
                });
                errorHtml += '</ul>';
                hideLoader();

                swalInit.fire({
                    title: 'Validation Errors',
                    html: errorHtml,
                    icon: 'error',
                    confirmButtonText: 'OK'
                });
            } else {
                swalInit.fire({
                    title: 'Error!',
                    text: `Failed to ${action} application. Please try again.`,
                    icon: 'error',
                    confirmButtonText: 'OK'
                });
            }
        }
    });
}

function displayStructuredError(errorMessage) {

    swalInit.fire({
        title: 'Validation Failed',
        html: errorMessage,
        icon: 'error',
        confirmButtonText: 'OK',
        confirmButtonColor: '#e74c3c',
        customClass: {
            container: 'custom-swal-container',
            popup: 'custom-swal-popup',
            content: 'custom-swal-content'
        }
    });
}

function disableFormInputs(form) {
    $(form).find('input[type="checkbox"],input[type="text"],input[type="date"],input[type="number"], input[type="radio"], input[type="file"], select , .btn-file ,.save-button').prop('disabled', true);
    $(form).find('.btn-file').addClass('disabled');
}

function showForm() {
    $('form.det').hide();
    $('form.det[data-code=' + getapp() + '-' + getcode() + ']').show();
}

function getcode() {
    return $('.details.active').attr('data-code').slice(-1);
}

function getapp() {
    return $('.apptype.active').data('app');
}

function parseCode(code, inputType) {
    let parts = code.split('-');
    if (inputType === 'app') {
        if (parts.length === 2) {
            return parts[0];
        } else if (parts.length === 3) {
            return parts[0] + '-' + parts[1];
        }
    } else if (inputType === 'det') {
        return parts[parts.length - 1];
    }
    return null; // or an appropriate default value
}

function validateCheckerAction() {
    let errors = [];

    let isChecked = $('#deactivate').is(':checked');
    let remarks = $('#checker_remarks').val().trim();

   if (!isChecked && remarks === '') {
        errors.push('Please confirm that you have run Dedup for all applicants/co-applicants/guarantors.');
    }

    if (remarks === '') {
        errors.push('Please provide remarks before proceeding.');
    }

    return errors;
}

function disableAccordian() {
    var state = $('#bottomCard').data('state');
    if (state === 0) {
        $('#ebityDetailslink, #loanDetailslink').css({
            'pointer-events': 'none',
            'opacity': '0.5',
            'cursor': 'not-allowed'
        });
        $('#ebityDetails, #loanDetails').css({'cursor': 'not-allowed'});
    } else if (state === 1) {
        $('#loanDetailslink').css({
            'pointer-events': 'auto',
            'opacity': '1',
            'cursor': 'auto'
        });
        $('#loanDetails').css({'cursor': 'auto'});
        $('#ebityDetailslink').css({
            'pointer-events': 'none',
            'opacity': '0.5',
            'cursor': 'not-allowed'
        });
        $('#ebityDetails').css({'cursor': 'not-allowed'});
    } else if (state === 2) {
        $('#loanDetailslink, #ebityDetailslink').css({
            'pointer-events': 'auto',
            'opacity': '1',
            'cursor': 'auto'
        });
        $('#loanDetails').css({'cursor': 'auto'});
        $('#ebityDetails').css({'cursor': 'auto'});
    }
}

function notyalt(msg) {
    // new Noty({
    //     layout: 'bottomRight',
    //     text: msg,
    //     type: 'success'
    // }).show();
    swalInit.fire({
        title: 'Success!',
        text: msg,
        icon: 'success',
        confirmButtonClass: 'btn btn-success',
        showCloseButton: true
    });

}

var swalInit = swal.mixin({
    buttonsStyling: false,
    confirmButtonClass: 'btn bg-danger text-white',
    cancelButtonClass: 'btn btn-light'
});

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


// Check the value after DOM content is loaded
if ($('#collapseDisable').val() === '1') {
    // Set a timeout to simulate the first click after 1 second
    setTimeout(function () {
        $('#vehDetailslink').click();

        // Simulate the second click after a small delay to ensure the first click action is completed
        setTimeout(function () {
            $('#loanDetailslink').click();

            // Simulate the third click after another small delay
            setTimeout(function () {
                $('#ebityDetailslink').click();
            }, 150); // Adjust the delay as needed
        }, 350); // Adjust the delay as needed
    }, 1000); // 1 second delay
}

// function showLoader() {
//     $('.hideloader').show();
// }
//
// function hideLoader() {
//     $('.hideloader').hide();
// }
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
function confirmmsg_lat(msg) {
    return swalInit.fire({
        title: msg,
        icon: "warning",
        showCancelButton: false,
        confirmButtonText: "Ok",
    }).then(function (result) {
        return result.isConfirmed;
    });
}
function updateCheckerAccordionStyle(accordionId, isCompleted) {
        const accordion = document.querySelector(`#${accordionId}`);
        if (accordion) {
            const label = accordion.querySelector('label');
            if (label) {
                if (isCompleted) {
                    label.classList.remove('btn-active-light-primary');
                    label.classList.add('btn-active-light-success', 'show');
                } else {
                    label.classList.remove('btn-active-light-success', 'show');
                    label.classList.add('btn-active-light-primary');
                }
            }
        }
    }
function toggleActionButtons(disable) {
  $('#checker_forward, #checker_reject, #checker_sendback').prop('disabled', disable);
}
