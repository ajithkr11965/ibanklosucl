$(document).ready(function () {
    $('#fcvstatus').prop("disabled",true);
    $('#cpvstatus').prop("disabled",true);
    $('#cfrstatus').prop("disabled",true);
    $('#fileUploadFcv').prop("disabled",true);
    $('#fileUploadCfr').prop("disabled",true);
    $('#fileUploadCpv').prop("disabled",true);
    if ($('#lockflg').val() === 'Y') {
        $('.save-button').remove();
        $('.racescorelistSave').remove();
        $('#kt_button_runDKScore').remove();
        $('#addcoapp').remove();
        $('#addguarantor').remove();
        $('.FcvSave').remove();
        $('.FcvEdit').remove();
        $('.uploadfcv').remove();
        $('.uploadcpv').remove();
        $("#consent").prop("disabled", true);
        $("#crtremarks").prop('disabled', false);
        $('.crtsubmit').remove();
        $('#fcvstatus').prop("disabled",true);
        $('#cpvstatus').prop("disabled",true);
        $('#cfrstatus').prop("disabled",true);
        $('#fileUploadFcv').prop("disabled",true);
        $('#fileUploadCfr').prop("disabled",true);
        $('#fileUploadCpv').prop("disabled",true);
        alertmsg('This workitem is locked by PPC ' + $('#lockuser').val());
    }
    if ($('#bureauBlock').val() === 'Y') {
        hideLoader();
        alertmsg('Bureau is taken 45 days earlier.');
        $('.save-button').remove();
        $('.racescorelistSave').remove();
        $('#kt_button_runDKScore').remove();
        $('#addcoapp').remove();
        $('#addguarantor').remove();
        $('.FcvSave').remove();
        $('.FcvEdit').remove();
        $('.uploadfcv').remove();
        $('.uploadcpv').remove();
        $("#consent").prop("disabled", true);
        $("#crtremarks").prop('disabled', false);
        $('#CrtApprove').remove();
        $('#fcvstatus').prop("disabled",true);
        $('#cpvstatus').prop("disabled",true);
        $('#cfrstatus').prop("disabled",true);
        $('#fileUploadFcv').prop("disabled",true);
        $('#fileUploadCfr').prop("disabled",true);
        $('#fileUploadCpv').prop("disabled",true);

    }
    $('.details').on('click', function (e) {
        e.preventDefault();
        let datacode = $('form.det[data-code=' + getapp() + '-' + $(this).attr('data-code').slice(-1) + ']')
        $('.details').removeClass('active');
        $(this).addClass('active');
        showForm();

    });
    if($("#fcvstatus").val()!="NA"){
        $(".uploadfcv ").show();
    }else{
        $(".uploadfcv ").hide();
    }
    if($("#cpvstatus").val()!="NA"){
        $(".uploadcpv ").show();
    }else{
        $(".uploadcpv ").hide();
    }
    $('#fcvstatus').on('change', function (e) {
        if($("#fcvstatus").val()!="NA"){
            $(".uploadfcv ").show();
        }else{
            $(".uploadfcv ").hide();
        }
    });
    $('#cpvstatus').on('change', function (e) {
        if($("#cpvstatus").val()!="NA"){
            $(".uploadcpv ").show();
        }else{
            $(".uploadcpv ").hide();
        }
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
        e.preventDefault();
        e.stopPropagation();
        var here = $(this);
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
    });

    $('#loanbody').on('click', '.findedup-button', function (e) {
        e.preventDefault();
        e.stopPropagation();
        var here = $(this);
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
                                    <td>${item.dob || ''}</td>
                                </tr>`;
                        });
                        here.closest('.det').find('.fincount').val(data.length);
                        here.closest('.det').find('.finflag').val('true');
                        here.closest('.det').find('.findedupetable').find('tbody').html(rows);
                        hideLoader();
                    } else if (response.status === 'Y') {
                        hideLoader();
                        here.closest('.det').find('.finflag').val('true');
                        here.closest('.det').find('.findedupetable').find('tbody').html('<tr  class="bg-light-whitetr"><td colspan="8"><b>' + response.msg + '</b></td></tr>');
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
        var lockflg =$('#lockflg').val();
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
                        hideLoader();
                        // If update succeeds, process the loan
                        Swal.fire({
                            title: 'Success',
                            text: 'Details validated successfully.',
                            icon: 'success',
                            confirmButtonText: 'OK'
                        }).then((result) => {
                            if (result.isConfirmed) {
                                var form=$('<form action="wichecker2" method="post">' +
                                    '<input type="hidden" name="action" value="BC">'+
                                    '<input type="hidden" name="slno" value="'+vehicleLoanMasterId+'">' +
                                    '<input type="hidden" name="winum" value="'+winum+'">'+
                                    '<input type="hidden" name="lockflg" value="'+lockflg+'">'
                                );
                                $('body').append(form);
                                form.submit();
                            }
                        });
                    },
                    error: function (xhr) {
                        displayStructuredError("Error updating recommended amount: " + xhr.responseText);
                    }
                });
            },
            error: function (xhr) {
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
    $('#checker_forward').on('click', function(e) {
        e.preventDefault();
        handleAction('forward');
    });

    $('#checker_reject').on('click', function(e) {
        e.preventDefault();
        handleAction('reject');
    });

    $('#checker_sendback').on('click', function(e) {
        e.preventDefault();
        handleAction('sendback');
    });

    $('.fcvcpvcfr').on('click', '.FcvEdit', function (e) {
        $("#FcvSave").prop("disabled", false);
        $("#FcvEdit").prop("disabled", true);
        $('#fcvstatus').prop("disabled",false);
        $('#cpvstatus').prop("disabled",false);
        $('#cfrstatus').prop("disabled",false);
        $('#fileUploadFcv').prop("disabled",false);
        $('#fileUploadCfr').prop("disabled",false);
        $('#fileUploadCpv').prop("disabled",false);
        $('.btn-file').removeClass("disabled");
    });

    $('.fcvcpvcfr').on('click', '.FcvSave', function (e) {
        $("#FcvSave").prop("disabled", true);
        $("#FcvEdit").prop("disabled", false);
        $('#fcvstatus').prop("disabled",true);
        $('#cpvstatus').prop("disabled",true);
        $('#cfrstatus').prop("disabled",true);
        $('#fileUploadFcv').prop("disabled",true);
        $('#fileUploadCpv').prop("disabled",true);
        $('#fileUploadCfr').prop("disabled",true);
        $('.btn-file').addClass("disabled");
        if($('#fcvstatus').val()!="" && $('#cpvstatus').val()!="" && $('#cfrstatus').val()!=""){
            fcvsave();
        }
        else{
            alertmsg('Kindly select values for all required field before proceeding!');
        }


    });

    $('.crtsubmit').on('click', function (e) {
        showLoader();
        var remarks = $("#crtremarks").val().trim();
        var totalwarns = $("#totalwarnings").val();
        var flg = 0;
        if(remarks=="") {
            hideLoader();
            alertmsg('Kindly Enter Remarks');
        }else{
            var queue="",rejflg="N",status="";
            var vehicleLoanMasterId = $('#slno').val();
            var winum = $('#winum').val();
            var action="";

            if($(this).attr('id')==="CrtApprove"){
                let isValid = true;
                $('#materialListTable tbody tr').each(function () {
                    const materiallistID = $(this).find('.materiallistID').val();
                    const materiallistDesc = $(this).find('.materiallistDesc').val();
                    const materiallistCondition = $(this).find('.materiallistCondition').val();
                    const materiallistComDate = $(this).find('.materiallistComDate').val();

                    if (!materiallistID || !materiallistDesc || !materiallistCondition ) {
                        isValid = false;
                        return false; // break out of .each loop
                    }
                });

                if (!isValid) {
                    hideLoader();
                    alertmsg("All Material terms and Conditions fields must be filled in every row.");
                }else {
                    if (totalwarns > 0) {
                        hideLoader();
                        alertmsg('CBS value and user entered value mismatch. Kindly check warnings section!');
                    } else {
                        var declaration = $("#consent");
                        if (declaration.is(':checked')) {
                            queue = "XX";
                            status = "CRT Approved";
                            action = "approve";
                            flg = 1;
                        } else {
                            hideLoader();
                            alertmsg('You must check the declaraion checkbox under Warnings section to continue!');
                        }
                    }
                }


            }else if($(this).attr('id')==="CrtSendBack"){
                queue="BS";
                status="CRT Send Back";
                action="sendback";
                flg = 1;
            }else if($(this).attr('id')==="CrtReject"){
                queue="";
                rejflg="Y";
                status="CRT Reject";
                action="reject";
                flg = 1;
            }
            if(flg==1){
                $.ajax({
                    url: 'api/checker/validateandsavecrt',
                    type: 'POST',
                    data: {
                        wiNum: winum,
                        slno: vehicleLoanMasterId,
                        remarks: remarks,
                        action: action
                    },
                    success: function (updateResponse) {
                        hideLoader();


                        var responsemsg= updateResponse.split("~");
                        if(responsemsg[0]=="success") {

                            var message="";

                            if(action=="reject"){
                                 message="Work item is Successfully Rejected";
                            }else if(action=="sendback"){
                                 message="Work item is Successfully Send Back to Branch";
                            }else{
                                if(responsemsg[1]=="RA")
                                    message=" CRT Work item is Successfully Submitted to RBCPC maker";
                                else
                                    message=" CRT Work item is Successfully Submitted to Branch Documentation queue";
                            }
                            // If update succeeds, process the loan
                            Swal.fire({
                                title: 'Success',
                                text: message,
                                icon: 'success',
                                confirmButtonText: 'OK'
                            }).then((result) => {
                                if (result.isConfirmed) {
                                    window.location.href = 'crtlist';
                                }
                            });
                        }else{
                            Swal.fire({
                                title: 'Error',
                                text: updateResponse,
                                icon: 'error',
                                confirmButtonText: 'OK'
                            }).then((result) => {
                                
                            });
                        }
                    },
                    error: function (xhr) {
                        hideLoader();
                        displayStructuredError("Error saving crt: " + xhr.responseText);
                    }
                });
            }
        }
    });
    $("#remarkHist").on('click',function (e) {
        e.preventDefault();
        e.stopPropagation();
        alertmsgframe();
    });
    function alertmsgframe() {
        $('#alert_modal .modal-header').removeClass('bg-danger').addClass('bg-success');
        $('#alert_modal .modal-header').find('.modal-title').text('Remarks');
        $('#alert_modal .modal-body').html('<iframe id="modalIframe" src="remarks?slno='+$('#slno').val()+'" width="100%" height="400" frameborder="0"></iframe>');
        $('#alert_modal').modal('show');
    }

    $('#backbtn').on('click', function (e) {
        e.preventDefault();
        $('#slnobk').val($('#slno').val());
        var search = $('#slnobk').val();
        var queue = $('#queue').val();
        if (queue == 'BS' && !search) {
            $('#redirecturl').val('bslist');
        } else if (queue == 'CA'&& !search) {
            $('#redirecturl').val('calist');
        } else if (queue == 'BD'&& !search) {
            $('#redirecturl').val('boglist');
        } else if (queue == 'BM'&& !search) {
            $('#redirecturl').val('bmlist');
        } else if (queue == 'RM'&& !search) {
            $('#redirecturl').val('cpcmakerlist');
        } else if (queue == 'WA'&& !search) {
            $('#redirecturl').val('waiverlist');
        }  else if (queue == 'ACOPN'&& !search) {
            $('#redirecturl').val('bogacctopn');
        } else if (queue == 'CS'&& !search) {
            $('#redirecturl').val('crtlist');
        } else {
            $('#redirecturl').val('dashboard');
        }
        $('#backform').submit();
    });

});
function fcvsave() {

    let valid = true;
    let form = $('#fcvCpvCfrForm')[0];
    let formData = new FormData(form);
    $('input[type="file"]').each(function () {
        const fileInput = $(this);
        const files = fileInput[0].files;
        const fileName = files.length ? files[0].name : '';
        const fileExtension = fileName.split('.').pop().toLowerCase();
        const allowedExtensions = ['pdf', 'jpg'];

        if (files.length > 0 && !allowedExtensions.includes(fileExtension)) {
            alertmsg('Only PDF and JPG files are allowed.');
            valid = false;
            return false; // break out of each loop
        }
    });

    var slno= $('#slno').val();
    var winum= $('#winum').val();

    formData.append('fcvStatus', $('#fcvstatus').val());
    formData.append('cpvStatus', $('#cpvstatus').val());
    formData.append('cfrFound', $('#cfrstatus').val());
    formData.append('wiNum', winum);
    formData.append('slno', slno);

    var fileInputFcv = $('#fileUploadFcv')[0];
    if (fileInputFcv.files.length > 0) {
        formData.append('fileUploadFcv', fileInputFcv.files[0]);
    }
    var fileInputCpv = $('#fileUploadCpv')[0];
    if (fileInputCpv.files.length > 0) {
        formData.append('fileUploadCpv', fileInputCpv.files[0]);
    }

    var fileInputCfr = $('#fileUploadCfr')[0];
    if (fileInputCfr.files.length > 0) {
        formData.append('fileInputCfr', fileInputCfr.files[0]);
    }

    $.ajax({
        url: 'api/SaveFCV-data',
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false, // Convert data object to JSON string
        success: function (response) {
            console.log('FCV details submitted successfully:', response);
            if(response=="success"){
                alertmsg("FCV CPV CFR Details are saved successfully !");
            }else{
                alertmsg(response);
            }

            if(response=="success"){
                $("#fcvcpvlabel").removeClass("btn-light-primary");
                $("#fcvcpvlabel").removeClass("btn-active-light-warning");
                $("#fcvcpvlabel").addClass("btn-active-light-success btn-light-success");
                 // disableFormInputs(form);
                $.ajax({
                    dataType : "json",
                    url: 'api/FetchCRTAmberData',
                    type: 'POST',
                    async: false,
                    data: {
                        winum: $('#winum').val(),
                        slno: parseInt($('#slno').val())
                    },
                    success: function (response) {

                        var colorflg=response.color;
                        console.log(colorflg);
                        var racemessage=response.racemessage;
                        var fcvmessage=response.fcvmessage;
                        var cpvmessage=response.cpvmessage;
                        var cfrmessage=response.cfrmessage;

                        var htmlContent = '';
                        if (colorflg === "green") {
                            htmlContent = `
                                <div class="alert alert-dismissible bg-light-success d-flex flex-center flex-column py-10 px-10 px-lg-20 mb-10">
                                    <i class="ki-duotone ki-information-5 fs-5tx text-danger mb-5"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
                                    <div class="text-center">
                                        <h1 class="fw-bold mb-5"> Check Result: Green</h1>
                                    </div>
                                </div>

                            `;
                        } else if (colorflg === "amber") {
                            htmlContent = `
                                <div class="alert alert-dismissible bg-light-warning d-flex flex-center flex-column py-10 px-10 px-lg-20 mb-10">
                                    <i class="ki-duotone ki-information-5 fs-5tx text-danger mb-5"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
                                    <div class="text-center">
                                        <h1 class="fw-bold mb-5"> Check Result: Amber</h1>
                                    </div>
                                </div>
                            `;
                        } else if (colorflg === "red") {
                            htmlContent = `
                                <div class="alert alert-dismissible bg-light-danger d-flex flex-center flex-column py-10 px-10 px-lg-20 mb-10">
                                    <i class="ki-duotone ki-information-5 fs-5tx text-danger mb-5"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
                                    <div class="text-center">
                                        <h1 class="fw-bold mb-5"> Check Result: Red</h1>
                                    </div>
                                </div>
                            `;
                        }
                        $('.brestatus').empty();
                        console.log(htmlContent);
                        // Append the generated HTML to the parent div with class "brestatus"

                        $('.brestatus').append(htmlContent);

                    },
                    error: function (xhr, status, error) {
                        console.log('Amber data fetch failed', error);
                    }
                });
            }

        },
        error: function (xhr, status, error) {
            console.log('FCV/CPV details submission failed:', error);
        }
    });







}
function handleAction(action) {
    if (!$('#deactivate').is(':checked')) {
        Swal.fire({
            title: 'Error!',
            text: 'Please confirm that you have run Dedup for all applicants/co-applicants/guarantors.',
            icon: 'error',
            confirmButtonText: 'OK'
        });
        return;
    }

    processAction(action);
}


function processAction(action) {
    let vehicleLoanMasterId = $('#slno').val();
    let wiNum = $('#winum').val();
    let lockflg = $('#lockflg').val();
    let remarks = $('textarea[aria-label="Checker Remarks"]').val();

    $.ajax({
        url: `api/checker/${action}`,
        type: 'POST',
        data: {
            slno: vehicleLoanMasterId,
            winum: wiNum,
            lockflg: lockflg,
            remarks: remarks
        },
        success: function(response) {
            Swal.fire({
                title: 'Success!',
                text: `Application ${action}ed successfully.`,
                icon: 'success',
                confirmButtonText: 'OK'
            }).then((result) => {
                if (result.isConfirmed) {
                    window.location.href = 'your-redirect-url';
                }
            });
        },
        error: function(xhr) {
            if (xhr.status === 400 && Array.isArray(xhr.responseJSON)) {
                // Display detailed error messages
                let errorHtml = '<ul>';
                xhr.responseJSON.forEach(function(error) {
                    errorHtml += `<li>${error}</li>`;
                });
                errorHtml += '</ul>';

                Swal.fire({
                    title: 'Validation Errors',
                    html: errorHtml,
                    icon: 'error',
                    confirmButtonText: 'OK'
                });
            } else {
                Swal.fire({
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
    var swalInit = swal.mixin({
        buttonsStyling: false,
        confirmButtonClass: 'btn bg-danger text-white',
        cancelButtonClass: 'btn btn-light'
    });
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
