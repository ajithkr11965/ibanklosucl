$(document).ready(function() {

    $('#loanbody').on('click','.cbsfetch', function(e) {
        e.preventDefault();
        e.stopPropagation();
        showLoader();
        fetchCbsData($(this));
    });

    function fetchCbsData(tab){
        var form=tab.closest('.det');
        var custID=form.closest('.tab-pane').find('.generaldetails').find('.custID').val();
        if(custID.length<=0){
          alertmsg('Kindly Enter Customer Id in general Details');
          hideLoader();
        }
        else {
            var jsonBody = {
                slno: $('#slno').val(),
                winum: $('#winum').val(),
                appid: form.closest('.tab-pane').find('.generaldetails').find('.appid').val(),
                custID: custID
            };
            $.ajax({
                url: 'api/fetch-cbs',
                type: 'POST',
             //   async: false,
                contentType: 'application/json', // Set content type to JSON
                data: JSON.stringify(jsonBody), // Convert data object to JSON string
                success: function (response) {
                    if (response.status === 'S') {
                        if (response.msg.length > 0 && response.msg!='null') {
                            var data = JSON.parse(response.msg);
                            // Set each field individually
                            var $basic = form.closest('.tab-pane').find('.basicdetails');
                            var $kyc = form.closest('.tab-pane').find('.kycdetails');
                            // $basic.find('.basic_name').val(data.customerName || '');
                            $basic.find('.basic_gender').val(data.gender || '');
                            $basic.find('.basic_ftname').val(data.fathersName || '');
                            $basic.find('.basic_mtname').val(data.mothersName || '');
                            $basic.find('.basic_ms').val(data.maritalStatus || '');
                            $basic.find('.basic_saltutation').val(data.custTitle || '');
                            $basic.find('.permanentAddress1').val(data.permanentAddress1 || '').prop('disabled',true);
                            $basic.find('.permanentAddress2').val(data.permanentAddress2 || '').prop('disabled',true);
                            $basic.find('.permanentAddress3').val(data.permanentAddress3 || '').prop('disabled',true);
                            var permanentCityCode=data.permanentCityCode || '';
                            var permanentCity=data.permanentCity || '';
                            var permanentStateCode=data.permanentStateCode || '';
                            var permanentState=data.permanentState || '';
                            var permanentCountry=data.permanentCountry || '';
                            var permanentCountryCode=data.permanentCountryCode || '';
                            $basic.find('.permanentCity').html('<option value="' +permanentCityCode  + '">' + permanentCity + '</option>').prop('disabled', true);
                            $basic.find('.permanentState').html('<option value="' + permanentStateCode  + '">' + permanentState + '</option>').prop('disabled', true);
                            $basic.find('.permanentCountry').html('<option value="' + permanentCountryCode  + '">' +permanentCountry + '</option>').prop('disabled', true);
                            $basic.find('.permanentPin').val(data.permanentPin || '').prop('disabled',true);

                            $basic.find('.presentAddress1').val(data.communicationAddress1 || '');
                            $basic.find('.presentAddress2').val(data.communicationAddress2 || '');
                            $basic.find('.presentAddress3').val(data.communicationAddress3 || '');
                            $basic.find('.permanentCity').addClass('noneditable')
                            $basic.find('.permanentState').addClass('noneditable')
                            $basic.find('.permanentCountry').addClass('noneditable')
                            $basic.find('.permanentPin').addClass('noneditable')
                            $basic.find('.permanentAddress1').addClass('noneditable')
                            $basic.find('.permanentAddress2').addClass('noneditable')
                            $basic.find('.permanentAddress3').addClass('noneditable')




                            var presentCityCode=data.communicationCityCode || '';
                            var presentCity=data.communicationCity || '';
                            var communicationStateCode=data.communicationStateCode || '';
                            var communicationState=data.communicationState || '';
                            var communicationCountryCode=data.communicationCountryCode || '';
                            var communicationCountry=data.communicationCountry || '';
                            $basic.find('.presentCity').html('<option value="' +presentCityCode + '">' + presentCity+ '</option>');
                            $basic.find('.presentState').html('<option value="' + communicationStateCode + '">' + communicationState  + '</option>');
                            $basic.find('.presentCountry').html('<option value="' + communicationCountryCode+ '">' +communicationCountry + '</option>');
                            $basic.find('.presentPin').val(data.communicationPin || '');

                            $kyc.find('.pan').val(data.pan || '');
                            $kyc.find('.pandob').val(data.custDob || '');
                            $kyc.find('.aadhaarRefNo').val(data.aadhaarRefNo || '');
                            $basic.find('.passport').val(data.passport || '');
                            $basic.find('.basic_mob').val(data.cellPhone || '');
                            $basic.find('.basic_email').val(data.commEmail || '');

                            $basic.find('.losflag').val('false');
                            $basic.find('.finflag').val('false');
                        }
                        hideLoader();
                    } else {
                        alertmsg(response.msg);
                        hideLoader();
                    }
                },
                error: function (xhr, status, error) {
                    alertmsg(error);
                    hideLoader();
                }
            });
        }
    }

    function handlePanDetails(context, action, value) {
        const pan = $(context).closest('.det').find('.pan');
        const pandob = $(context).closest('.det').find('.pandob');
        const panname = $(context).closest('.det').find('.panname');
        const ocr_pan = $(context).closest('.det').find('.ocr_pan');
        const ocr_pandob = $(context).closest('.det').find('.ocr_pandob');
        const ocr_panname = $(context).closest('.det').find('.ocr_panname');
        const panvalidated = $(context).closest('.det').find('.pan-validated');
        const panfup = $(context).closest('.det').find('.panfup');
        const appid= $(context).closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid');
        switch (action) {
            case 'setValue':
                pan.val(value.pan);
                pandob.val(value.dob);
                panname.val(value.name);
                break;
            case 'setValueOcr':
                ocr_pan.val(value.pan);
                ocr_pandob.val(value.dob);
                ocr_panname.val(value.name);
                break;
            case 'clearValue':
                pan.val('');
                pandob.val('');
                panname.val('');
                ocr_pan.val('');
                ocr_pandob.val('');
                ocr_panname.val('');
                break;
            case 'setReadonly':
                pan.prop('readonly', true);
                pandob.prop('readonly', true);
                panname.prop('readonly', true);
                break;
            case 'removeReadonly':
                pan.prop('readonly', false);
                pandob.prop('readonly', false);
                panname.prop('readonly', false);
                break;
            case 'isValid':
                pan.addClass('is-valid');
                pandob.addClass('is-valid');
                panname.addClass('is-valid');
                panvalidated.val('true');
                panfup.val('true');
                break;
            case 'remisValid':
                pan.removeClass('is-valid');
                pandob.removeClass('is-valid');
                panname.removeClass('is-valid');
                panvalidated.val('false');
             //   panfup.val('false');
                break;
            case 'getValue':
                return {
                    panDoc: {
                        pan: pan.val(),
                        dob: pandob.val(),
                        name: panname.val()
                    },
                    appid:appid.val(),
                    winum: $('#winum').val()
                };
            case 'valid':
                return pan.val().length==10 && pandob.val().length>0 && panname.val().length>0;
            default:
                console.error('Invalid action');
        }
    }


    $('#loanbody').on('change','.panfile', function(e) {
      //  handlePanDetails(this, 'clearValue');
        handlePanDetails(this,'removeReadonly');
        handlePanDetails(this, 'remisValid');
    });
    $('#loanbody').on('click','.pan-validate', function(e) {
        e.preventDefault();
        e.stopPropagation();
        handlePanDetails(bthis, 'remisValid');
        var bthis=$(this);
        var fileInput = $(this).closest('.det').find('.panfile')[0];
        var filealreadyuploaded=$(this).closest('.det').find('.panfup').val();
        if ((fileInput.files.length > 0 || filealreadyuploaded=='true') &&   handlePanDetails(bthis, 'valid')) {
            showLoader()
            $.ajax({
                url: 'api/pan-validate',
                type: 'POST',
                data: JSON.stringify(handlePanDetails(this, 'getValue')),
                contentType: 'application/json',
                success: function (response) {
                    hideLoader();
                    if (response.status === 'S') {
                        handlePanDetails(bthis, 'setReadonly');
                        handlePanDetails(bthis, 'isValid');

                    } else {
                        alertmsg(response.msg);
                        handlePanDetails(bthis, 'clearValue');
                        handlePanDetails(bthis, 'remisValid');
                    }
                },
                error: function (error) {
                    alertmsg("Error Occured while Validating PAN !!!");
                    handlePanDetails(bthis, 'clearValue');
                    handlePanDetails(bthis, 'remisValid');
                    hideLoader();
                }
            });
        }
        else {
            alertmsg('Kindly fill the input parameters and  attach file');
        }
    });
    $('#loanbody').on('click','.panocr', function(e) {
        e.preventDefault();
        e.stopPropagation();
        var bodythis = $(this);
        handlePanDetails(bodythis,'remisValid');
        var fileMap=AppidMap.get(getAppid(bodythis));
            if (fileMap && fileMap.has('panfile')){
                var panfiledt=fileMap.get('panfile');
                showLoader();
                var docMeta={
                    documentType: "PAN",
                    applicationType: "LOS",
                    workItemNo: $('#winum').val()
                }
                var data = {
                    winum:$('#winum').val(),
                    appid:handlePanDetails(bodythis, 'getValue').appid,
                    fileRequest: {
                        fileType: panfiledt.docName,
                        base64:  panfiledt.base64String,
                        documentMeta: docMeta
                    }
                }
                $.ajax({
                    url: 'api/losocr', // Replace with your server endpoint
                    type: 'POST',
                    data: JSON.stringify(data),
                    contentType: 'application/json',
                    success: function(response) {
                        log(response);
                        if(response.status==='SUCCESS'){
                            handlePanDetails(bodythis,'setValue',response.pan);
                            handlePanDetails(bodythis,'setValueOcr',response.pan);
                        }
                        else{
                            handlePanDetails(bodythis,'clearValue');
                            alertmsg(response.errorMessage);
                        }
                        hideLoader();
                    },
                    error: function(error) {
                        alertmsg("OCR Data fetch Failed Kindly Enter manually!!");
                        handlePanDetails(bodythis,'clearValue');
                        hideLoader();
                    }
                });
            }else {
            alertmsg('File not attached');
        }
    });
    // $('#loanbody').on('click','.panocr', function(e) {
    //     e.preventDefault();
    //     e.stopPropagation();
    //     var fileInput = $(this).closest('.det').find('.panfile')[0];
    //     var bodythis = $(this);
    //     handlePanDetails(bodythis,'remisValid');
    //     if (fileInput.files.length > 0) {
    //         var file = fileInput.files[0];
    //         var reader = new FileReader();
    //         reader.onload = function(event) {
    //             showLoader();
    //             var base64String = event.target.result.split(',')[1]; // Get the base64 string
    //             var docMeta={
    //                 documentType: "PAN",
    //                 applicationType: "LOS",
    //                 workItemNo: $('#winum').val()
    //             }
    //             var data = {
    //                 winum:$('#winum').val(),
    //                 appid:handlePanDetails(bodythis, 'getValue').appid,
    //                 fileRequest: {
    //                     fileType: "PAN",
    //                     base64: base64String,
    //                     documentMeta: docMeta
    //                 }
    //             }
    //             $.ajax({
    //                 url: 'api/losocr', // Replace with your server endpoint
    //                 type: 'POST',
    //                 data: JSON.stringify(data),
    //                 contentType: 'application/json',
    //                 success: function(response) {
    //                     log(response);
    //                     if(response.status==='SUCCESS'){
    //                         handlePanDetails(bodythis,'setValue',response.pan);
    //                         handlePanDetails(bodythis,'setValueOcr',response.pan);
    //                     }
    //                     else{
    //                         handlePanDetails(bodythis,'clearValue');
    //                         alertmsg(response.errorMessage);
    //                     }
    //                     hideLoader();
    //                 },
    //                 error: function(error) {
    //                     alertmsg("Error Parsing FIle!!");
    //                     handlePanDetails(bodythis,'clearValue');
    //                     hideLoader();
    //                 }
    //             });
    //         };
    //         reader.readAsDataURL(file); // Convert file to base64 string
    //     } else {
    //         alertmsg('File not attached');
    //     }
    //
    // });
//PASSPORT
    function handlePassportDetails(context, action, value) {
        const passport = $(context).closest('.det').find('.passport');
        const passportname = $(context).closest('.det').find('.passportname');
        const passportexp = $(context).closest('.det').find('.passportexp');
        const ocr_passport = $(context).closest('.det').find('.ocr_passport');
        const ocr_passportexp = $(context).closest('.det').find('.ocr_passportexp');
        const passportvalidated = $(context).closest('.det').find('.passport-validated');
        switch (action) {
            case 'setValue':
                passport.val(value.passportNumber);
                passportexp.val(value.doe);
                passportname.val(value.givenName);
                break;
            case 'setOcrValue':
                ocr_passportexp.val(value.doe);
                ocr_passport.val(value.passportNumber);
                break;
            case 'clearValue':
                passport.val('');
                passportexp.val('');
                ocr_passportexp.val('');
                ocr_passport.val('');
                passportname.val('');
                break;
            case 'setReadonly':
                passport.prop('readonly', true);
                passportexp.prop('readonly', true);
                passportname.prop('readonly', true);
                break;
            case 'removeReadonly':
                passport.prop('readonly', false);
                passportexp.prop('readonly', false);
                passportname.prop('readonly', false);
                break;
            case 'isValid':
                passport.addClass('is-valid');
                passportexp.addClass('is-valid');
                passportname.addClass('is-valid');
                passportvalidated.val('true');
                break;
            case 'remisValid':
                passport.removeClass('is-valid');
                passportexp.removeClass('is-valid');
                passportname.removeClass('is-valid');
                passportvalidated.val('false');
                break;
            case 'getValue':
                return {
                    passport: passport.val(),
                    passportname: passportname.val(),
                    passportexp: passportexp.val()
                };
            case 'valid':
                return passport.val().length>0 && passportexp.val().length>0 && passportname.val().length>0;
            default:
                console.error('Invalid action');
        }
    }


    $('#loanbody').on('change','.passportfile', function(e) {
     //   handlePassportDetails(this, 'clearValue');
        handlePassportDetails(this,'removeReadonly');
        handlePassportDetails(this, 'remisValid');
    });
    $('#loanbody').on('click','.passportocr', function(e) {
        e.preventDefault();
        e.stopPropagation();
        var bodythis = $(this);
        handlePassportDetails(bodythis,'remisValid');
        var fileMap=AppidMap.get(getAppid(bodythis));
        if (fileMap && fileMap.has('passportfile')){
            var filedt=fileMap.get('passportfile');
                showLoader();
                var docMeta={
                    documentType: "PASSPORT",
                    applicationType: "LOS",
                    workItemNo: $('#winum').val()
                }
                var data = {
                    winum:$('#winum').val(),
                    appid:handlePanDetails(bodythis, 'getValue').appid,
                    fileRequest: {
                        fileType: filedt.docName,
                        base64:  filedt.base64String,
                        documentMeta: docMeta
                    }
                }
                $.ajax({
                    url: 'api/losocr', // Replace with your server endpoint
                    type: 'POST',
                    data: JSON.stringify(data),
                    contentType: 'application/json',
                    success: function(response) {
                        log(response);
                        if(response.status==='SUCCESS'){
                            handlePassportDetails(bodythis,'setValue',response.passport);
                            handlePassportDetails(bodythis,'setOcrValue',response.passport);
                        }
                        else{
                            handlePassportDetails(bodythis,'clearValue');
                            alertmsg(response.errorMessage);
                        }
                        hideLoader();
                    },
                    error: function(error) {
                        alertmsg("OCR Data fetch Failed Kindly Enter manually!!");
                        handlePassportDetails(bodythis,'clearValue');
                        hideLoader();
                    }
                });

        } else {
            alertmsg('File not attached');
        }

    });
    // $('#loanbody').on('click','.passportocr', function(e) {
    //     e.preventDefault();
    //     e.stopPropagation();
    //     var fileInput = $(this).closest('.det').find('.passportfile')[0];
    //     var bodythis = $(this);
    //     handlePassportDetails(bodythis,'remisValid');
    //     if (fileInput.files.length > 0) {
    //         var file = fileInput.files[0];
    //         var reader = new FileReader();
    //         reader.onload = function(event) {
    //             showLoader();
    //             var base64String = event.target.result.split(',')[1]; // Get the base64 string
    //             var docMeta={
    //                 documentType: "PASSPORT",
    //                 applicationType: "LOS",
    //                 workItemNo: $('#winum').val()
    //             }
    //             var data = {
    //                 winum:$('#winum').val(),
    //                 appid:handlePanDetails(bodythis, 'getValue').appid,
    //                 fileRequest: {
    //                     fileType: "PASSPORT",
    //                     base64: base64String,
    //                     documentMeta: docMeta
    //                 }
    //             }
    //             $.ajax({
    //                 url: 'api/losocr', // Replace with your server endpoint
    //                 type: 'POST',
    //                 data: JSON.stringify(data),
    //                 contentType: 'application/json',
    //                 success: function(response) {
    //                     log(response);
    //                     if(response.status==='SUCCESS'){
    //                         handlePassportDetails(bodythis,'setValue',response.passport);
    //                         handlePassportDetails(bodythis,'setOcrValue',response.passport);
    //                     }
    //                     else{
    //                         handlePassportDetails(bodythis,'clearValue');
    //                         alertmsg(response.errorMessage);
    //                     }
    //                     hideLoader();
    //                 },
    //                 error: function(error) {
    //                     alertmsg("Error Parsing FIle!!");
    //                     handlePassportDetails(bodythis,'clearValue');
    //                     hideLoader();
    //                 }
    //             });
    //         };
    //         reader.readAsDataURL(file); // Convert file to base64 string
    //     } else {
    //         alertmsg('File not attached');
    //     }
    //
    // });


//AADHAAR
    function handleuidDetails(context, action, value) {
        const uid = $(context).closest('.det').find('.uid');
       // const uiddob = $(context).closest('.det').find('.uiddob');
        const pandob = $(context).closest('.det').find('.pandob');
        const uidname = $(context).closest('.det').find('.uidname');
        const uidvalidated = $(context).closest('.det').find('.uid-validated');
        const uidfile = $(context).closest('.det').find('.uidfile');
        const uidotp = $(context).closest('.det').find('.uidotpval');
        const uidfup = $(context).closest('.det').find('.uidfup');
        const appid= $(context).closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid');
        switch (action) {
            case 'clearFile' :
                uidfile.val('');
                uidfile.fileinput('clear');
                break;
            case 'setValue':
                uid.val(value.aadhaar);
           //     uiddob.val(value.dob);
                uidname.val(value.name);
                break;
            case 'clearValue':
                uid.val('');
           //     uiddob.val('');
                uidname.val('');
                uidotp.val('');
                break;
            case 'setReadonly':
                uid.prop('readonly', true);
             //   uiddob.prop('readonly', true);
                uidname.prop('readonly', true);
                break;
            case 'removeReadonly':
                uid.prop('readonly', false);
          //      uiddob.prop('readonly', false);
                uidname.prop('readonly', false);
                break;
            case 'isValid':
                uid.addClass('is-valid');
            //    uiddob.addClass('is-valid');
                uidname.addClass('is-valid');
                uidvalidated.val('true');
                uidfup.val('true');
                break;
            case 'isValidotp':
                uidotp.addClass('is-valid');
                uid.addClass('is-valid');
                uidvalidated.val('true');
                break;
            case 'remisValid':
                uid.removeClass('is-valid');
              //  uiddob.removeClass('is-valid');
                uidname.removeClass('is-valid');
                uidotp.removeClass('is-valid');
                uidvalidated.val('false');
                uidfup.val('false');
                break;
            case 'getValue':
                return {
                    uidDoc: {
                        uid: uid.val(),
                        yob: pandob.val().substring(0,4),
                        name: uidname.val(),
                        otp: uidotp.val()
                    },
                    appid : appid.val()
                };
            case 'valid':
                return uid.val().length==12  && uidname.val().length>0;
            case 'otpvalid':
                return uid.val().length>0 ;
            case 'otpvalid2':
                return uid.val().length>0 && uidotp.val().length>0;
            default:
                console.error('Invalid action');
        }
    }

    $('#loanbody').on('change','.uidmode', function(e) {
        handleuidDetails(this, 'clearValue');
        handleuidDetails(this,'removeReadonly');
        handleuidDetails(this, 'remisValid');
        handleuidDetails(this, 'clearFile');
        $(this).closest('.det').find('.otppage').addClass('hide');
        console.log(  $(this).closest('.det').find('.uidmanual'))
        if($(this).val()==='M'){
            $(this).closest('.det').find('.uidmanual').removeClass('hide');
            $(this).closest('.det').find('.uidotp').addClass('hide');
        }
        else{
            $(this).closest('.det').find('.uidmanual').addClass('hide');
            $(this).closest('.det').find('.uidotp').removeClass('hide');
        }
    });




   // $('#loanbody .uidmode[value="M"]').trigger('change');

    // $('#loanbody .kycdetails').each(function() {
    //     var $form = $(this);
    //     var $checkedRadio = $form.find('.uidmode:checked');
    //     if ($checkedRadio.length) {
    //         $checkedRadio.trigger('change');
    //     }
    // });


    $('#loanbody').on('change','.uidfile', function(e) {
      //  handleuidDetails(this, 'clearValue');
        handleuidDetails(this,'removeReadonly');
        handleuidDetails(this, 'remisValid');
    });

    $('#loanbody').on('click','.uid-otp-validate', function(e) {
        e.preventDefault();
        e.stopPropagation();
        handleuidDetails(bthis, 'remisValid');
        var bthis=$(this);
        var docMeta={
            documentType: "AADHAAR",
            applicationType: "LOS",
            workItemNo: $('#winum').val()
        }
        if (handleuidDetails(bthis, 'otpvalid2')) {
            showLoader()
            var data=handleuidDetails(bthis, 'getValue');
            data.fileRequest={
                fileType: "AADHAAR",
                documentMeta: docMeta
            };
            $.ajax({
                url: 'api/uid-otp-validate',
                type: 'POST',
                data: JSON.stringify(data),
                contentType: 'application/json',
                success: function (response) {
                    hideLoader();
                    if (response.status === 'S') {
                        handleuidDetails(bthis, 'setReadonly');
                        handleuidDetails(bthis, 'isValidotp');

                    } else {
                        alertmsg(response.msg);
                        handleuidDetails(bthis, 'clearValue');
                    }
                },
                error: function (error) {
                    alertmsg("Error Validating uid !!!");
                    handleuidDetails(bthis, 'clearValue');
                    hideLoader();
                }
            });
        }
        else {
            alertmsg('Kindly enter the otp');
        }
    });
    $('#loanbody').on('click','.uid-otp', function(e) {
        e.preventDefault();
        e.stopPropagation();
        handleuidDetails(bthis, 'remisValid');
        var bthis=$(this);
        if (handleuidDetails(bthis, 'otpvalid')) {
            showLoader()
            $.ajax({
                url: 'api/uid-otp-sent',
                type: 'POST',
                data: JSON.stringify(handleuidDetails(this, 'getValue')),
                contentType: 'application/json',
                success: function (response) {
                    hideLoader();
                    if (response.status === 'S') {
                        bthis.closest('.det').find('.otppage').removeClass('hide');
                        handleuidDetails(bthis, 'setReadonly');
                        handleuidDetails(bthis, 'isValid');
                        alertmsg(response.msg);
                    } else {
                        alertmsg(response.msg);
                        handleuidDetails(bthis, 'clearValue');
                    }
                },
                error: function (error) {
                    alertmsg("Error Senting Otp !!!");
                    handleuidDetails(bthis, 'clearValue');
                    hideLoader();
                }
            });
        }
        else {
            alertmsg('Kindly Enter Aadhar Number');
        }
    });
    $('#loanbody').on('click','.uidocr', function(e) {
        e.preventDefault();
        e.stopPropagation();
        const panvalidated = $(this).closest('.det').find('.pan-validated').val();
        if(panvalidated !=='true'){
            alertmsg('Kindly validate PAN !!')
        }
        else {
            var bthis = $(this);
            var filealreadyuploaded = $(this).closest('.det').find('.uidfup').val();
            handleuidDetails(bthis, 'remisValid');
            var validated = true;
            if (bthis.hasClass('uid-validate')) {
                validated = handleuidDetails(bthis, 'valid');
            }
            var fileMap=AppidMap.get(getAppid(bthis));
            if (fileMap && fileMap.has('uidfile') && validated){
                var filedt=fileMap.get('uidfile');
                let mask = true;
                    showLoader();
                    var dt = handleuidDetails(bthis, 'getValue');
                    var docMeta = {
                        documentType: "AADHAAR",
                        applicationType: "LOS",
                        workItemNo: $('#winum').val()
                    }
                if (!bthis.hasClass('uid-validate')) {
                   mask=false;
                }
                    dt.fileRequest = {
                        fileType: filedt.docName,
                        base64:  filedt.base64String,
                        documentMeta: docMeta,
                        maskOnly:mask
                    }

                    $.ajax({
                        url: 'api/losocr', // Replace with your server endpoint
                        type: 'POST',
                        data: JSON.stringify(dt),
                        contentType: 'application/json',
                        success: function (response) {
                            log(response);
                            if (response.status === 'SUCCESS') {
                                if (!bthis.hasClass('uid-validate')) {
                                    handleuidDetails(bthis, 'setValue', response.uid);
                                }
                                if (bthis.hasClass('uid-validate') ) {
                                    $.ajax({
                                        url: 'api/uid-demo-validate',
                                        type: 'POST',
                                        data: JSON.stringify(handleuidDetails(bthis, 'getValue')),
                                        contentType: 'application/json',
                                        success: function (response) {
                                            hideLoader();
                                            if (response.status === 'S') {
                                                handleuidDetails(bthis, 'setReadonly');
                                                handleuidDetails(bthis, 'isValid');
                                            } else {
                                                alertmsg(response.msg);
                                                handleuidDetails(bthis, 'clearValue');
                                            }
                                        },
                                        error: function (error) {
                                            alertmsg("Error Validating uid !!!");
                                            handleuidDetails(bthis, 'clearValue');
                                            hideLoader();
                                        }
                                    });
                                } else {
                                    hideLoader();
                                }
                            } else {
                                handleuidDetails(bthis, 'clearValue');
                                alertmsg(response.errorMessage);
                                hideLoader();
                            }
                        },
                        error: function (error) {
                            if (bthis.hasClass('uid-validate')) {
                                alertmsg("Aadhaar Masking Failed!!");
                            }
                            else{
                                alertmsg("OCR Data fetch Failed Kindly Enter manually!!");
                            }
                            handleuidDetails(bthis, 'clearValue');
                            hideLoader();
                        }
                    });
            } else if (validated && filealreadyuploaded === 'true') {
                $.ajax({
                    url: 'api/uid-demo-validate',
                    type: 'POST',
                    data: JSON.stringify(handleuidDetails(bthis, 'getValue')),
                    contentType: 'application/json',
                    success: function (response) {
                        hideLoader();
                        if (response.status === 'S') {
                            handleuidDetails(bthis, 'setReadonly');
                            handleuidDetails(bthis, 'isValid');
                        } else {
                            alertmsg(response.msg);
                            handleuidDetails(bthis, 'clearValue');
                        }
                    },
                    error: function (error) {
                        alertmsg("Error Occured  While Validating Aadhaar !!!");
                        handleuidDetails(bthis, 'clearValue');
                        hideLoader();
                    }
                });
            } else {
                alertmsg('Kindly fill the Aadhaar details and attach file');
            }
        }

    });


});
//     $('#loanbody').on('click','.uidocr', function(e) {
//         e.preventDefault();
//         e.stopPropagation();
//         const panvalidated = $(this).closest('.det').find('.pan-validated').val();
//         if(panvalidated !=='true'){
//             alertmsg('Kindly validate PAN !!')
//         }
//         else {
//             var fileInput = $(this).closest('.det').find('.uidfile')[0];
//             var bthis = $(this);
//             var filealreadyuploaded = $(this).closest('.det').find('.uidfup').val();
//             handleuidDetails(bthis, 'remisValid');
//             var validated = true;
//             if (bthis.hasClass('uid-validate')) {
//                 validated = handleuidDetails(bthis, 'valid');
//             }
//             if (fileInput.files.length > 0 && validated) {
//                 var file = fileInput.files[0];
//                 var reader = new FileReader();
//                 reader.onload = function (event) {
//                     showLoader();
//                     var dt = handleuidDetails(bthis, 'getValue');
//                     var base64String = event.target.result.split(',')[1]; // Get the base64 string
//                     var docMeta = {
//                         documentType: "AADHAAR",
//                         applicationType: "LOS",
//                         workItemNo: $('#winum').val()
//                     }
//                     dt.fileRequest = {
//                         fileType: "AADHAAR",
//                         base64: base64String,
//                         documentMeta: docMeta
//                     }
//
//                     $.ajax({
//                         url: 'api/losocr', // Replace with your server endpoint
//                         type: 'POST',
//                         data: JSON.stringify(dt),
//                         contentType: 'application/json',
//                         success: function (response) {
//                             log(response);
//                             if (response.status === 'SUCCESS') {
//                                 if (!bthis.hasClass('uid-validate')) {
//                                     handleuidDetails(bthis, 'setValue', response.uid);
//                                 }
//                                 if (bthis.hasClass('uid-validate')) {
//                                     $.ajax({
//                                         url: 'api/uid-demo-validate',
//                                         type: 'POST',
//                                         data: JSON.stringify(handleuidDetails(bthis, 'getValue')),
//                                         contentType: 'application/json',
//                                         success: function (response) {
//                                             hideLoader();
//                                             if (response.status === 'S') {
//                                                 handleuidDetails(bthis, 'setReadonly');
//                                                 handleuidDetails(bthis, 'isValid');
//                                             } else {
//                                                 alertmsg(response.msg);
//                                                 handleuidDetails(bthis, 'clearValue');
//                                             }
//                                         },
//                                         error: function (error) {
//                                             alertmsg("Error Validating uid !!!");
//                                             handleuidDetails(bthis, 'clearValue');
//                                             hideLoader();
//                                         }
//                                     });
//                                 } else {
//                                     hideLoader();
//                                 }
//                             } else {
//                                 handleuidDetails(bthis, 'clearValue');
//                                 alertmsg(response.errorMessage);
//                                 hideLoader();
//                             }
//                         },
//                         error: function (error) {
//                             alertmsg("Error Parsing FIle!!");
//                             handleuidDetails(bthis, 'clearValue');
//                             hideLoader();
//                         }
//                     });
//                 };
//                 reader.readAsDataURL(file); // Convert file to base64 string
//             } else if (validated && filealreadyuploaded === 'true') {
//                 $.ajax({
//                     url: 'api/uid-demo-validate',
//                     type: 'POST',
//                     data: JSON.stringify(handleuidDetails(bthis, 'getValue')),
//                     contentType: 'application/json',
//                     success: function (response) {
//                         hideLoader();
//                         if (response.status === 'S') {
//                             handleuidDetails(bthis, 'setReadonly');
//                             handleuidDetails(bthis, 'isValid');
//                         } else {
//                             alertmsg(response.msg);
//                             handleuidDetails(bthis, 'clearValue');
//                         }
//                     },
//                     error: function (error) {
//                         alertmsg("Error Validating uid !!!");
//                         handleuidDetails(bthis, 'clearValue');
//                         hideLoader();
//                     }
//                 });
//             } else {
//                 alertmsg('Kindly fill the details and attach file');
//             }
//         }
//
//     });
//
//
// });

function kycsavefn(form,key,callback) {
    if(form.find('.uid-validated').val()==='false' && form.find('.uid').val().length>0){
        hideLoader();
        alertmsg('Kindly Validate Aadhaar');
        return false;
    }
    else    if(form.find('.pan-validated').val()==='false'){
        hideLoader();
        alertmsg('Kindly Validate PAN');
        return false;
    }
    else {
        // Array to store the result
        const allFiles = [];
// Iterating over the fileMap and adding the desired properties to the array
        var fileMap=AppidMap.get(getAppid(form));
        fileMap.forEach((value, key) => {
            if(value.docName!=='AADHAAR'){
                allFiles.push({
                    DOC_EXT: value.fileExtension,
                    DOC_BASE64: value.base64String,
                    DOC_NAME: value.docName
                });
            }
        });
        var photo=fileMap.get("photofile");
        if(!photo || !photo.base64String){
            alertmsg("Kindly Upload CUSTOMER  PHOTO");
            hideLoader();
            return ;
        }
        if(photo.fileExtension.toLowerCase()!='jpg' && photo.fileExtension.toLowerCase()!='jpeg' && photo.fileExtension.toLowerCase()!='png' ){
            alertmsg("Please upload jpg/jpeg/png for Customer Photo");
            hideLoader();
            return ;
        }
        var consent=fileMap.get("consentfile");
        if(!consent  || !consent.base64String){
            alertmsg("Kindly Upload CONSENT FORM");
            hideLoader();
            return ;
        }
        var originalfile=fileMap.get("originalfile");
        if(!originalfile  || !originalfile.base64String){
            if(form.find('.cif_mode').val()==='M') {
                alertmsg("Kindly Upload Original Seen & Verified FORM");
                hideLoader();
                return;
            }
        }
        var custsig=fileMap.get("custsig");
        if(!custsig  || !custsig.base64String){
            if(form.closest('.det').closest('.tab-pane').find('.generaldetails').find('.sibCustomer:checked').val()==='N') {
                alertmsg("Customer Signature is Mandatory for Non Sib Customers");
                hideLoader();
                return;
            }
        }

        var formDataArray = form.serializeArray();
        var data = formDataArray.map(function(item) {
            return {key: item.name, value: item.value};
        });
        var jsonBody = {
            id: key,
            slno: $('#slno').val(),
            winum: $('#winum').val(),
            appid:form.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
            data: data,
            reqtype: form.attr('data-code'),
            DOC_ARRAY: allFiles
        };

        $.ajax({
            url: 'api/save-data',
            type: 'POST',
         //   async: false,
            contentType: 'application/json', // Set content type to JSON
            data: JSON.stringify(jsonBody), // Convert data object to JSON string
            success: function (response) {
                console.log('KYC details submitted successfully:', response);
                if(response.status==='S' || response.status==='W'){
                    var data = JSON.parse(response.msg);
                    if(response.status==='W')
                        alertmsg(data.warn);
                    else
                        notyalt('KYC Details Saved !!')
                    form.closest('.det').closest('.tab-pane').find('.basicdetails').find('.basic_name').val(data.name);
                    form.closest('.det').closest('.tab-pane').find('.basicdetails').find('.basic_age').val(data.age);
                    form.closest('.det').closest('.tab-pane').find('.basicdetails').find('.losflag').val('false');
                    form.closest('.det').closest('.tab-pane').find('.basicdetails').find('.finflag').val('false');
                    form.closest('.det').closest('.tab-pane').find('.basicdetails').find('.basic_dob').val(form.find('.pandob').val());
                    disableFormInputs(form);
                    scrolltoId('appList');
                    callback(true);
                }
                else{
                    alertmsg(response.msg);
                    callback(false);
                }
            },
            error: function (xhr, status, error) {
                console.log('KYC details submission failed:', error);
                callback(false);
            }
        });

    }
}
// function kycsavefn(form,key,callback) {
//     if(form.find('.uid-validated').val()==='false' && form.find('.uid').val().length>0){
//         hideLoader();
//         alertmsg('Kindly Validate Aadhaar');
//         return false;
//     }
//     else    if(form.find('.pan-validated').val()==='false'){
//         hideLoader();
//         alertmsg('Kindly Validate PAN');
//         return false;
//     }
//     else {
//         var allFiles = [];
//         var promises = [];
//
//         // Process only inputs with the class 'base64file'
//         form.find('.base64file').each(function() {
//             var input = $(this);
//             var files = input[0].files;
//
//             var fileType = null;
//             if(input.hasClass('uidfile'))
//                 fileType='AADHAAR' ;
//             else if(input.hasClass('panfile'))
//                 fileType= 'PAN' ;
//             else if(input.hasClass('passportfile'))
//                 fileType=  'PASSPORT' ;
//             else if(input.hasClass('visafile'))
//                 fileType=  'VISA_OCI' ;
//
//
//             Array.from(files).forEach(file => {
//                 var promise = new Promise((resolve, reject) => {
//                     var reader = new FileReader();
//                     reader.onload = function(e) {
//                         var fileData = {
//                             DOC_EXT: file.name.split('.').pop(),
//                             DOC_NAME: fileType,
//                             DOC_BASE64: e.target.result.split(',')[1] // Extract Base64 content
//                         };
//                         allFiles.push(fileData);
//                         resolve();
//                     };
//                     reader.onerror = function() {
//                         reject('Failed to read file: ' + file.name);
//                     };
//                     reader.readAsDataURL(file);
//                 });
//                 promises.push(promise);
//             });
//         });
//
//         // Wait for all file reading to finish
//         Promise.all(promises).then(() => {
//             console.log('All files processed:', allFiles);
//
//             var formDataArray = form.serializeArray();
//             var data = formDataArray.map(function(item) {
//                 return {key: item.name, value: item.value};
//             });
//             var jsonBody = {
//                 id: key,
//                 slno: $('#slno').val(),
//                 winum: $('#winum').val(),
//                 appid:form.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
//                 data: data,
//                 reqtype: form.attr('data-code'),
//                 DOC_ARRAY: allFiles
//             };
//
//                  $.ajax({
//                     url: 'api/save-data',
//                     type: 'POST',
//                     async: false,
//                     contentType: 'application/json', // Set content type to JSON
//                     data: JSON.stringify(jsonBody), // Convert data object to JSON string
//                     success: function (response) {
//                         console.log('KYC details submitted successfully:', response);
//                         if(response.status==='S' || response.status==='W'){
//                             var data = JSON.parse(response.msg);
//                          if(response.status==='W')
//                              alertmsg(data.warn);
//                          else
//                              notyalt('KYC Details Saved !!')
//                          form.closest('.det').closest('.tab-pane').find('.basicdetails').find('.basic_name').val(data.name);
//                          form.closest('.det').closest('.tab-pane').find('.basicdetails').find('.losflag').val('false');
//                          form.closest('.det').closest('.tab-pane').find('.basicdetails').find('.finflag').val('false');
//                          form.closest('.det').closest('.tab-pane').find('.basicdetails').find('.basic_dob').val(form.find('.pandob').val());
//                             disableFormInputs(form);
//                             callback(true);
//                         }
//                         else{
//                             alertmsg(response.msg);
//                             callback(false);
//                         }
//                     },
//                     error: function (xhr, status, error) {
//                         console.log('KYC details submission failed:', error);
//                         callback(false);
//                     }
//                 });
//         }).catch(error => {
//             console.error('Error processing files:', error);
//             callback(false);
//         });
//
//     }
//
//
//
//
// }


function getAppid(th){
    return th.closest('.tab-pane').find('.generaldetails').find('.appid').val()
}