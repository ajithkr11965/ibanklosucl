$(document).ready(function() {
    $('.canvassed_ppc').select2({
        placeholder: "Enter a PPC",
        minimumInputLength: 4, // Requires at least one character for the search
        ajax: {
            url: function (params) {
                // Append the search term as a path variable
                return 'api/ppc-fetch/' + params.term;
            },
            dataType: 'json', // Data type of the API response
            delay: 250, // Wait 250ms after typing stops to send the request
            processResults: function (data) {
                // Transform the top-level data array to fit Select2's requirements
                return {
                    results: data.map(item => ({
                        id: item.codevalue, // Adjusted for your data format where `codevalue` is the id
                        text: item.codedesc // Adjusted for your data format where `codedesc` is the text
                    }))
                };
            },
            cache: true
        }
    });
    $('.rsm_ppc').select2({
        placeholder: "Enter a RSM PPC",
        minimumInputLength: 4, // Requires at least one character for the search
        ajax: {
            url: function (params) {
                // Append the search term as a path variable
                return 'api/ppc-rsmfetch/' + params.term;
            },
            dataType: 'json', // Data type of the API response
            delay: 250, // Wait 250ms after typing stops to send the request
            processResults: function (data) {
                // Transform the top-level data array to fit Select2's requirements
                return {
                    results: data.map(item => ({
                        id: item.codevalue, // Adjusted for your data format where `codevalue` is the id
                        text: item.codedesc // Adjusted for your data format where `codedesc` is the text
                    }))
                };
            },
            cache: true
        }
    });
    $('.rsm_sol').select2({
        placeholder: "Enter a SOL ID",
        minimumInputLength: 4, // Requires at least one character for the search
        ajax: {
            url: function (params) {
                // Append the search term as a path variable
                return 'api/sol-rsmfetch/' + params.term;
            },
            dataType: 'json', // Data type of the API response
            delay: 250, // Wait 250ms after typing stops to send the request
            processResults: function (data) {
                // Transform the top-level data array to fit Select2's requirements
                return {
                    results: data.map(item => ({
                        id: item.codevalue, // Adjusted for your data format where `codevalue` is the id
                        text: item.codedesc // Adjusted for your data format where `codedesc` is the text
                    }))
                };
            },
            cache: true
        }
    });
    $('.lh_sol').select2({
        placeholder: "Enter a SOL ID",
        minimumInputLength: 4, // Requires at least one character for the search
        ajax: {
            url: function (params) {
                // Append the search term as a path variable
                return 'api/sol-lhfetch/' + params.term;
            },
            dataType: 'json', // Data type of the API response
            delay: 250, // Wait 250ms after typing stops to send the request
            processResults: function (data) {
                // Transform the top-level data array to fit Select2's requirements
                return {
                    results: data.map(item => ({
                        id: item.codevalue, // Adjusted for your data format where `codevalue` is the id
                        text: item.codedesc // Adjusted for your data format where `codedesc` is the text
                    }))
                };
            },
            cache: true
        }
    });

    $('.rah_sol').select2({
        placeholder: "Enter a SOL ID",
        minimumInputLength: 4, // Requires at least one character for the search
        ajax: {
            url: function (params) {
                // Append the search term as a path variable
                return 'api/sol-rahfetch/' + params.term;
            },
            dataType: 'json', // Data type of the API response
            delay: 250, // Wait 250ms after typing stops to send the request
            processResults: function (data) {
                // Transform the top-level data array to fit Select2's requirements
                return {
                    results: data.map(item => ({
                        id: item.codevalue, // Adjusted for your data format where `codevalue` is the id
                        text: item.codedesc // Adjusted for your data format where `codedesc` is the text
                    }))
                };
            },
            cache: true
        }
    });


    $('#loanbody').on('change','.sibCustomer', function(e) {
        e.preventDefault();
        e.stopPropagation();
        var selected= $(this).val();
        var selectedres=  $(this).closest('.det').find('.residentialStatus:checked').val();
        var custid=$(this).closest('.det').find('.custID');
        var custidparent=$(this).closest('.det').find('.custidparent');
        custid.val('');
        if(selected === 'Y' || selectedres==='N'){
            custidparent.show();
            $(this).closest('.det').closest('.tab-pane').find('.kycdetails').find('.cifmode').addClass('hide');
            $(this).closest('.det').closest('.tab-pane').find('.kycdetails').find('.custSig').addClass('hide');
            $(this).closest('.det').closest('.tab-pane').find('.kycdetails').find('.cifmode').removeClass('d-flex');
       //     $(this).closest('.det').closest('.tab-pane').find('.kycdetails').find('.custSig').removeClass('d-flex');
        }
        else{
            $(this).closest('.det').closest('.tab-pane').find('.kycdetails').find('.cifmode').removeClass('hide');
            $(this).closest('.det').closest('.tab-pane').find('.kycdetails').find('.custSig').removeClass('hide');
            $(this).closest('.det').closest('.tab-pane').find('.kycdetails').find('.cifmode').addClass('d-flex');
          //  $(this).closest('.det').closest('.tab-pane').find('.kycdetails').find('.custSig').addClass('d-flex');
            custidparent.hide();
        }
    });
    $('#loanbody').on('change','.custID', function(e) {
        e.preventDefault();
        e.stopPropagation();
        var custID=$(this);
        if(custID.val().length>=9) {
            showLoader();
            $.ajax({
                url: 'api/getCustName',
                type: 'POST',
                data: {custId: custID.val()},
                success: function (response) {
                    if (response.status === 'S') {
                        custID.closest('.form-check-horizontal').find('.userName') .html('<i class="ph ph-user"></i>'+response.msg);
                    } else {
                        alertmsg('Please Enter Valid CustID');
                        custID.val('');
                        custID.closest('.form-check-horizontal').find('.userName').html('');
                    }
                    hideLoader();
                },
                error: function (xhr, status, error) {
                    custID.val('');
                    alertmsg('Please Enter Valid CustID!');
                    hideLoader();
                    custID.closest('.form-check-horizontal').find('.userName') .html('');
                }
            });
        }
    });
    $('#loanbody').on('change','.residentialStatus', function(e) {
        e.preventDefault();
        e.stopPropagation();
        var selected= $(this).val();
        var selectedres=  $(this).closest('.det').find('.sibCustomer:checked').val();
        var custidparent=$(this).closest('.det').find('.custidparent');
        var custid=custidparent.find('.custID');
        custid.val('');
        if(selected === 'N'){
            custidparent.show();
            $(this).closest('.det').find('input[name="sibCustomer"][value="N"]').prop('checked',false);
            $(this).closest('.det').find('input[name="sibCustomer"][value="N"]').prop('disabled',true);
            $(this).closest('.det').find('input[name="sibCustomer"][value="Y"]').prop('checked',true);
            $(this).closest('.det').closest('.tab-pane').find('.kycdetails').find('.cifmode').addClass('hide');
            $(this).closest('.det').closest('.tab-pane').find('.kycdetails').find('.custSig').addClass('hide');
            $(this).closest('.det').closest('.tab-pane').find('.kycdetails').find('.cifmode').removeClass('d-flex');
        }
        else{
            $(this).closest('.det').find('input[name="sibCustomer"][value="N"]').prop('disabled',false);
            if(selectedres==='N') {
                custidparent.hide();
                $(this).closest('.det').closest('.tab-pane').find('.kycdetails').find('.cifmode').removeClass('hide');
                $(this).closest('.det').closest('.tab-pane').find('.kycdetails').find('.custSig').removeClass('hide');
                $(this).closest('.det').closest('.tab-pane').find('.kycdetails').find('.cifmode').addClass('d-flex');
            }
        }
    });
    $('#loanbody').on('change','.residentialStatus', function(e) {
        e.preventDefault();
        e.stopPropagation();
        var selected= $(this).val();
        if(selected === 'N'){
        $(this).closest('.det').closest('.tab-pane').find('.kycdetails').find('.nresdiv').removeClass('hide');
            $(this).closest('.det').closest('.tab-pane').find('.kycdetails').find('.resdiv').addClass('hide');
        }
        else{
            $(this).closest('.det').closest('.tab-pane').find('.kycdetails').find('.resdiv').removeClass('hide');
            $(this).closest('.det').closest('.tab-pane').find('.kycdetails').find('.nresdiv').addClass('hide');

        }
    });


    $('#loanbody').on('click','.edit-button', function(e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).closest('.det').find('.valid-lib').val('false');
        enableFormInputs($(this).closest('.det'))
    });

});

function gensavefn(form,key,callback) {
    var formDataArray = form.serializeArray(); // Serialize form data to array
    var data = formDataArray.map(function(item) {
        return {key: item.name, value: item.value}; // Transform to key-value pair objects
    });
    var jsonBody = {
        id: key,
        slno: $('#slno').val(),
        winum: $('#winum').val(),
        appid: form.find('.appid').val(),
        data: data,
        reqtype:form.attr('data-code'),
    };
    $.ajax({
        url: 'api/save-data',
        type: 'POST',
       // async:false,
        contentType: 'application/json', // Set content type to JSON
        data: JSON.stringify(jsonBody), // Convert data object to JSON string
        success: function (response) {
            if(response.status==='S')
            {
                form.find('.appid').val(response.appid);
                disableFormInputs(form);
                notyalt('General Details Saved !!');

                var data_ = JSON.parse(response.msg);
                if(data_.warn) {
                  alertmsg(data_.warn);
                }
                if(data_.custChanged=='Y') {
                    resetProgramFields(form.closest('.tab-pane').find('.Incomedetails').find('.programCode'));
                }
                if(data_.cifView) {
                    var data =JSON.parse(data_.cifView) ;
                    if(data && data!='null') {
                        // Set each field individually
                        var $basic = form.closest('.tab-pane').find('.basicdetails');
                        var $kyc = form.closest('.tab-pane').find('.kycdetails');
                        // $basic.find('.basic_name').val(data.customerName || '');
                        $basic.find('.basic_gender').val(data.gender || '');
                        $basic.find('.basic_ftname').val(data.fathersName || '');
                        $basic.find('.basic_mtname').val(data.mothersName || '');
                        $basic.find('.basic_ms').val(data.maritalStatus || '');
                        $basic.find('.basic_saltutation').val(data.custTitle || '');
                        $basic.find('.permanentAddress1').val(data.permanentAddress1 || '').prop('disabled', true);
                        $basic.find('.permanentAddress2').val(data.permanentAddress2 || '').prop('disabled', true);
                        $basic.find('.permanentAddress3').val(data.permanentAddress3 || '').prop('disabled', true);
                        var permanentCityCode=data.permanentCityCode || '';
                        var permanentCity=data.permanentCity || '';
                        var permanentStateCode=data.permanentStateCode || '';
                        var permanentState=data.permanentState || '';
                        var permanentCountry=data.permanentCountry || '';
                        var permanentCountryCode=data.permanentCountryCode || '';
                        $basic.find('.permanentCity').html('<option value="' +permanentCityCode  + '">' + permanentCity + '</option>').prop('disabled', true);
                        $basic.find('.permanentState').html('<option value="' + permanentStateCode  + '">' + permanentState + '</option>').prop('disabled', true);
                        $basic.find('.permanentCountry').html('<option value="' + permanentCountryCode  + '">' +permanentCountry + '</option>').prop('disabled', true);
                        $basic.find('.permanentPin').val(data.permanentPin || '').prop('disabled', true);

                        $basic.find('.presentAddress1').val(data.communicationAddress1 || '');
                        $basic.find('.presentAddress2').val(data.communicationAddress2 || '');
                        $basic.find('.presentAddress3').val(data.communicationAddress3 || '');
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
                        $kyc.find('.pan-validated').val(false);
                        $kyc.find('.uid-validated').val(false);
                        $kyc.find('.aadhaarRefNo').val(data.aadhaarRefNo || '');
                        $basic.find('.passport').val(data.passport || '');
                        $basic.find('.basic_mob').val(data.cellPhone || '');
                        $basic.find('.basic_email').val(data.commEmail || '');
                        $basic.find('.permanentCity').addClass('noneditable')
                        $basic.find('.permanentState').addClass('noneditable')
                        $basic.find('.permanentCountry').addClass('noneditable')
                        $basic.find('.permanentPin').addClass('noneditable')
                        $basic.find('.permanentAddress1').addClass('noneditable')
                        $basic.find('.permanentAddress2').addClass('noneditable')
                        $basic.find('.permanentAddress3').addClass('noneditable')
                    }
                }
                else if( form.find('.sibCustomer:checked').val()==='N'){
                    $basic.find('.permanentCity').removeClass('noneditable').prop('disabled', false);
                    $basic.find('.permanentState').removeClass('noneditable').prop('disabled', false);
                    $basic.find('.permanentCountry').removeClass('noneditable').prop('disabled', false);
                    $basic.find('.permanentPin').removeClass('noneditable').prop('disabled', false);
                    $basic.find('.permanentAddress1').removeClass('noneditable').prop('disabled', false);
                    $basic.find('.permanentAddress2').removeClass('noneditable').prop('disabled', false);
                    $basic.find('.permanentAddress3').removeClass('noneditable').prop('disabled', false);
                }


                hideLoader();
                callback(true);
            }
            else{
                alertmsg(response.msg);
                hideLoader();
                callback(false);
            }

        },
        error: function (xhr, status, error) {
            alertmsg('Error during Saving data');
            hideLoader();
            callback(false);
        }
    });
}