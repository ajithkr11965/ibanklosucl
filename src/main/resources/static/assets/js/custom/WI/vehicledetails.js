$(document).ready(function () {
    initializeChannelValidation();
    var currentinsuranceamount=0;
    //populateExistingValues();
    //updateTotalAmount();
    // disableFields();
    //
    // $('#dealer_name').select2({
    //     templateResult: formatState,
    //     templateSelection: formatState
    // });

    var today = new Date().toISOString().split('T')[0];

    // Set the max attribute of the input[type=date] to today's date
    //$('#invoice-date').attr('max', today);
    // Check the input value when it loses focus
    $('#invoice-date').on('change', function() {
        var selectedDate = $(this).val();
        if (selectedDate > today) {
            alertmsg("The selected date cannot be in the future. Please choose a valid date.");
            $(this).val('');  // Clear the invalid date
        }
    });



    $('#vehDetailslink').click(function (e) {
        e.preventDefault();
        var applicantId = $('[data-code="A-1"]').find('[name="appid"]').val();
        if (applicantId) {
            $('#vehDetailsContent').collapse('show');
            populateExistingValues();
            // updateTotalAmount();
        } else {
            alertmsg("Kindly Save Genaral Details of Applicant");
            setTimeout(() => {
                $('#vehDetailsContent').collapse('hide');
            }, 1000); // Defer the collapse to ensure it happens after any UI updates

        }
        //
    });


    function populateExistingValues() {
        var winum = $('#winum').val();
        var slno = $('#slno').val();
        var applicantId = $('[data-code="A-1"]').find('[name="appid"]').val();
        if (applicantId) {
            $.ajax({
                url: 'api/fetchExisting',
                method: 'GET',
                async: false,
                data: {wiNum: winum, slno: slno, applicantId: applicantId},
                success: function (data, textStatus, jqXHR) {
                    addFlag = false;
                    if (data.ino) {
                        $('#ino').val(data.ino); // Store ino for updating the existing entry
                        populateDealerNamesCodes(data.dealerName, data.dealerCode);
                        populateDealerSubCodes(data.dealerCode, data.dealerName, data.dstCode, data.dsaCode, data.dealerSubCode);
                        $('#dealer_name_remarks').val(data.dealerNameRemarks);
                        $('#state').val(data.dealerState);
                        populateCity(data.dealerName, data.dealerCode, data.dealerSubCode, data.dealerState, data.dealerCityId);
                        $('#make-section').show();
                        populateMakeCity(data.dealerCityId, data.dealerSubCode, data.dealerCode, data.dealerName, data.makeId)
                        $('#model-section').show();
                        populateModel(data.makeId, data.modelId,data.dealerCityId, data.dealerSubCode, data.dealerCode,data.dealerAccount);
                        $('#variant-section').show();
                        populateVariant(data.modelId, data.variantId);
                        $('#variant').val(data.variantName).prop('disabled', true);
                        $('#ex_showroom').val(data.exshowroomPrice);
                        if (data.insurancePrice != null) originalInsurance = data.insurancePrice;
                        $('#insurance').val(originalInsurance);
                        if (data.rtoPrice != null) originalRto = data.rtoPrice;
                        $('#rto').val(originalRto);
                        $('#other').val(data.otherPrice);
                        if (data.discountPrice != null) originalDiscount = data.discountPrice;
                        $('#discount').val(originalDiscount);
                        $('#onroad_price').val(data.onroadPrice);
                        $('#invoice-no').val(data.invoiceNo);
                        $('#color').val(data.colour);
                        if (data.extendedWarranty != null) originalWarranty = data.extendedWarranty;
                        $('#warranty').val(data.extendedWarranty);
                        console.log(data.invoiceDate);
                        $('#invoice-date').val(data.invoiceDate ? data.invoiceDate.substring(0, 10) : '');
                        $('#invoiceDoc').attr('data-filebase64', data.invoiceDoc);
                        $('#invoiceDoc').attr('data-filebase64ext', data.invoiceExt);
                        $('#fileuploadedspan').text("Please View the uploaded invoice in DOCLIST option");
                        $('#fileup').val(data.invoiceDoc);
                        $('#fileupext').val(data.invoiceExt);
                        $('#autodealerSourced').val(data.autodealerSourced).trigger("change");
                        if (data.customInsurance) {
                            $('#custom_insurance_checkbox').prop('checked', true);
                            $('#custom_insurance_amount').val(data.customInsuranceAmount);
                            $('#custom_insurance_remarks').val(data.customInsuranceRemarks);
                            $('#custom-insurance-inputs').show();
                            $('#custom-insurance').show();
                            $('#custom-insurance-inputs').addClass('d-flex');
                            $('#insurance').prop("readonly",true);
                        }
                        originalValues = {
                            exshowroomPrice: data.exshowroomPrice,
                            insurancePrice: data.insurancePrice,
                            rtoPrice: data.rtoPrice,
                            otherPrice: data.otherPrice,
                            extendedWarranty: data.extendedWarranty,
                            onroadPrice: data.onroadPrice
                        };
                        updateTotalAmount();
                        disableFields();
                    } else {
                        //populateState();
                        // populateMakes();
                        populateAllDealerNames();
                        enableFields();
                        $('#vehDetailsEdit').prop('disabled', true);
                        $('#vehDetailsSave').prop('disabled', false);
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (jqXHR.status === 400) {
                        alertmsg("Error" + jqXHR.status);
                        $('#vehDetailsContent').collapse('toggle');
                        $('#vehDetailslink').prop('disabled', true);
                    }
                }
            });
        }
    }

    var addFlag = true;
    var originalValues = {};
    var originalDiscount = 0;
    var originalInsurance = 0;
    var originalExshowroom = 0;
    var stockExshowroom = 0;
    var stockInsurance = 0;
    var originalRto = 0;
    var stockRto = 0;
    var originalWarranty = 0;
    var stockWarranty = 0;
    var originalOther = 0;
    var stockOther = 0;
    var originalOnroad = 0;
    var stockOnroad = 0;
    var variantId;
    var cityId = $('#city').val();
    $('#dealer_name').change(function () {
        var dealerName = $(this).val();
        populateDealerNames(dealerName);
    });
    $('#dealer_sub_code').change(function () {
        var dealerSubCode = $(this).val();
        var dealerName = $('#dealer_name').val();
        var dealerCode = $('#dealer_code').val();
        populateSubCodesCity(dealerSubCode, dealerCode, dealerName);
    });
    $('#city').change(function () {
        var cityCode = $(this).val();
        var dealerSubCode = $('#dealer_sub_code').val();
        var dealerName = $('#dealer_name').val();
        var dealerCode = $('#dealer_code').val();
        populateMakeSubCodesCity(cityCode, dealerSubCode, dealerCode, dealerName);
    });


    function populateCity(dealerName, dealerCode, dealerSubCode, dealerState, CityId) {
        //if (stateCode) {
        $.ajax({
            url: 'api/dealerSubCodes/' + dealerSubCode + '/' + dealerCode + '/' + dealerName,
            method: 'GET',
            success: function (data) {

                var cityDropdown = $('#city');
                cityDropdown.empty();
                cityDropdown.append('<option value="" selected disabled>Select a city</option>');
                $.each(data, function (key, value) {
                    var selected = (CityId && CityId === value.city_id) ? 'selected' : '';
                    cityDropdown.append('<option ' + selected + '  value="' + value.city_id + '">' + value.city_name + ' </option>');
                });
                cityDropdown.select2('destroy').select2({
                    templateResult: formatState,
                    templateSelection: formatState
                });
            },error: function (error) {
                alertmsg('Error.Please try again');
            }
        });
        // } else {
        //      $('#city').empty().prop('disabled', true);
        //  }
    }

    function populateMakeCity(cityId, dealerSubCode, dealerCode, dealerName, makeId) {
        $.ajax({
            url: 'api/makesByDealer/' + dealerSubCode + '/' + dealerCode + '/' + dealerName + '/' + cityId,
            method: 'GET',
            success: function (data) {
                var makeDropdown = $('#make');
                makeDropdown.empty();
                makeDropdown.append('<option value="" selected disabled>Select a make</option>');
                $.each(data, function (key, value) {
                    var selected = (makeId && makeId === value.make_id) ? 'selected' : '';
                    makeDropdown.append('<option  ' + selected + ' value="' + value.make_id + '">' + value.make_name + '</option>');
                });
                makeDropdown.select2('destroy').select2({
                    templateResult: formatState,
                    templateSelection: formatState
                });


            },error: function (error) {
                alertmsg('Error.Please try again');
            }
        });
    }

    function populateAllDealerNames() {
        $.ajax({
            url: 'api/dealers',
            method: 'GET',
            success: function (data) {
                var dealerDropdown = $('#dealer_name');
                dealerDropdown.empty();
                dealerDropdown.append('<option value="" selected disabled>Select a dealer</option>');
                $.each(data, function (key, value) {
                    dealerDropdown.append('<option  value="' + value.dealer_name + '">' + value.dealer_name + ' </option>');
                });
                dealerDropdown.select2('destroy').select2({
                    templateResult: formatState,
                    templateSelection: formatState
                });
               //  $('#dealer_code').val(dealerCode);

            },error: function (error) {
                alertmsg('Error.Please try again');
            }
        });
    }

    function populateDealerNamesCodes(dealerName, dealerCode) {
        $.ajax({
            url: 'api/dealers',
            method: 'GET',
            success: function (data) {
                var dealerDropdown = $('#dealer_name');
                dealerDropdown.empty();
                if (!dealerName) {
                    dealerDropdown.append('<option value="" selected disabled>Select a dealer</option>');
                } else
                    dealerDropdown.append('<option value=""  disabled>Select a dealer</option>');
                $.each(data, function (key, value) {
                    var selected = (dealerName && dealerName === value.dealer_name) ? 'selected' : '';
                    dealerDropdown.append('<option ' + selected + ' value="' + value.dealer_name + '">' + value.dealer_name + ' </option>');
                });
                // dealerDropdown.select2('destroy').select2({
                //     templateResult: formatState,
                //     templateSelection: formatState
                // });
                $('#dealer_code').val(dealerCode);

            },error: function (error) {
                alertmsg('Error.Please try again');
            }
        });
    }

    function populateSubCodesCity(dealerSubCode, dealerCode, dealerName) {
        $.ajax({
            url: 'api/dealerSubCodes/' + dealerSubCode + '/' + dealerCode + '/' + dealerName,
            method: 'GET',
            success: function (data) {
                //$('#state').val();
                var cityDropdown = $('#city');
                cityDropdown.empty();
                cityDropdown.append('<option value="" selected disabled>Select a city</option>');
                $.each(data, function (key, value) {
                    cityDropdown.append('<option  value="' + value.city_id + '">' + value.city_name + ' </option>');
                });
                cityDropdown.select2('destroy').select2({
                    templateResult: formatState,
                    templateSelection: formatState
                });
            },error: function (error) {
                alertmsg('Error.Please try again');
            }
        });
    }

    function populateMakeSubCodesCity(cityId, dealerSubCode, dealerCode, dealerName) {
        $.ajax({
            url: 'api/makesByDealer/' + dealerSubCode + '/' + dealerCode + '/' + dealerName + '/' + cityId,
            method: 'GET',
            success: function (data) {
                var makeDropdown = $('#make');
                makeDropdown.empty();
                makeDropdown.append('<option value="" selected disabled>Select a make</option>');
                $.each(data, function (key, value) {
                    makeDropdown.append('<option  value="' + value.make_id + '">' + value.make_name + '</option>');
                    $('#state').val(value.state_name);
                });

                makeDropdown.select2('destroy').select2({
                    templateResult: formatState,
                    templateSelection: formatState
                });


            },error: function (error) {
                alertmsg('Error.Please try again');
            }
        });
    }

    function populateDealerNames(dealerName, dstCode, dsaCode, dealerSubCode) {
        $.ajax({
            url: 'api/dealerCodes/' + dealerName,
            method: 'GET',
            success: function (data) {

                $('#dealer_code').val(data.dealerCode);
                if (dealerSubCode)
                    populateDropdown('#dealer_sub_code', data.dealerSubCodes, dealerSubCode);
                else
                    populateDropdown('#dealer_sub_code', data.dealerSubCodes);

                if (dstCode)
                    populateDropdown('#dst_code', data.dstCodes, dstCode);
                else
                    populateDropdown('#dst_code', data.dstCodes);
                if (dsaCode)
                    populateDropdown('#dsa_sub_code', data.dsaCodes, dsaCode);
                else
                    populateDropdown('#dsa_sub_code', data.dsaCodes);

            },error: function (error) {
                alertmsg('Error.Please try again');
            }
        });
    }


    function populateDealerSubCodes(dealerCode, dealerName, dstCode, dsaCode, dealerSubCode) {
        $.ajax({
            url: 'api/dealerNames/' + dealerName + '/' + dealerCode,
            method: 'GET',
            success: function (data) {

                if (dealerSubCode)
                    populateDropdown('#dealer_sub_code', data.dealerSubCodes, dealerSubCode);
                else
                    populateDropdown('#dealer_sub_code', data.dealerSubCodes);

                if (dstCode)
                    populateDropdown('#dst_code', data.dstCodes, dstCode);
                else
                    populateDropdown('#dst_code', data.dstCodes);
                if (dsaCode)
                    populateDropdown('#dsa_sub_code', data.dsaCodes, dsaCode);
                else
                    populateDropdown('#dsa_sub_code', data.dsaCodes);
            },error: function (error) {
                alertmsg('Error.Please try again');
            }
        });
    }

    function populateDropdown(selector, data, itemselected) {
        var dropdown = $(selector);
        dropdown.empty();
        if (!itemselected) {
            dropdown.append('<option value="" selected' + (((selector !== '#dst_code') && (selector !== '#dsa_sub_code')) ? ' disabled' : '') + '>Select an option</option>');
        } else {
            dropdown.append('<option value=""' + (((selector !== '#dst_code') && (selector !== '#dsa_sub_code')) ? ' disabled' : '') + '>Select an option</option>');
        }

        $.each(data, function (key, value) {
            var selected = (itemselected && itemselected === value.ID) ? 'selected' : '';
            dropdown.append('<option ' + selected + ' value="' + value.ID + '">' + (((selector == '#dst_code') || (selector == '#dsa_sub_code')) ?value.NAME+' ('+value.ID+')':value.NAME )+ ' </option>');
        });
        dropdown.select2('destroy').select2({
            templateResult: formatState,
            templateSelection: formatState
        });
    }

    $('#make').change(function () {
        var makeId = $(this).val();
        populateModel(makeId,'',$('#city').val(),$('#dealer_sub_code').val(),$('#dealer_code').val());
    });

    $('#model').change(function () {
        var modelId = $(this).val();
        populateVariant(modelId);
    });

    $('#variant').change(function () {
        var variantId = $(this).val();
        cityId = $('#city').val();
        populatePrice(variantId, cityId);
    });

    function populateMakes(makeId) {
        $.ajax({
            url: 'api/makes',
            method: 'GET',
            success: function (data) {
                var makeDropdown = $('#make');

                makeDropdown.empty();
                if (!makeId) {
                    makeDropdown.append('<option value="" selected disabled>Select a make</option>');
                } else
                    makeDropdown.append('<option value="" disabled>Select a make</option>');
                $.each(data, function (key, value) {
                    var selected = (makeId && makeId === value.make_id) ? 'selected' : '';
                    makeDropdown.append('<option ' + selected + ' value="' + value.make_id + '">' + value.make_name + '</option>');
                });
                makeDropdown.select2('destroy').select2({
                    templateResult: formatState,
                    templateSelection: formatState
                });
            },error: function (error) {
                alertmsg('Error.Please try again');
            }
        });
    }

    function populateModel(makeId, modelId,dealerCityId, dealerSubCode,dealerCode,dealerAccount) {
        if (makeId) {
            $('#model-section').show();
            $.ajax({
                url: 'api/models/'+makeId+'/'+dealerCode+'/'+dealerSubCode+'/'+dealerCityId,
                method: 'GET',
                success: function (data) {
                    var modelMap=data.modelMap;
                    var ifscMap	=data.ifscMap;
                    var modelDropdown = $('#model');
                    var dmodelDropdown = $('#dealerAccount');
                    dmodelDropdown.empty();
                    dmodelDropdown.append('<option value="">Please Select Account</option>');
                    modelDropdown.empty();
                    modelDropdown.append('<option value="">Please Select Model</option>');

                    if (!modelId) {
                        modelDropdown.append('<option value="" selected disabled>Select a model</option>');
                    } else modelDropdown.append('<option value="" disabled>Select a model</option>');
                    $.each(modelMap, function (key, value) {
                        var selected = (modelId && modelId === value.model_id) ? 'selected' : '';
                        modelDropdown.append('<option ' + selected + ' value="' + value.model_id + '">' + value.model_name + '</option>');
                    });
                    if(ifscMap) {
                        $.each(ifscMap, function (key, value) {
                            var selected = (dealerAccount && dealerAccount === value.FORACID) ? 'selected' : '';
                            dmodelDropdown.append('<option ' + selected + ' value="' + value.VALUE+'">ACCOUNT : ' + value.FORACID + '  ( IFSC : '+value.IFSC+' - BANK : '+value.BANK_NAME+')</option>');
                        });
                    }
                    else {
                        alertmsg("Dealer Account Not Mapped For selected Model!");
                    }
                    modelDropdown.select2('destroy').select2({
                        templateResult: formatState,
                        templateSelection: formatState
                    });
                    dmodelDropdown.select2('destroy').select2({
                        templateResult: formatState,
                        templateSelection: formatState
                    });
                },error: function (error) {
                    alertmsg('Error.Please try again');
                }
            });
        } else {
            $('#model-section').hide();
            $('#variant-section').hide();
        }
    }

    function populateVariant(modelId, variantId) {
        if (modelId) {
            $('#variant-section').show();
            $.ajax({
                url: 'api/variants/' + modelId,
                method: 'GET',
                success: function (data) {
                    var variantDropdown = $('#variant');
                    variantDropdown.empty();
                    if (!variantId) {
                        variantDropdown.append('<option value="" selected disabled>Select a variant</option>');
                    } else
                        variantDropdown.append('<option value="" disabled>Select a variant</option>');
                    $.each(data, function (key, value) {
                        var selected = (variantId && variantId === value.variant_id) ? 'selected' : '';
                        variantDropdown.append('<option ' + selected + ' value="' + value.variant_id + '">' + value.variant_name + ' - ' + value.fuel_type + ' - ' + value.transmission + '</option>');
                    });
                    variantDropdown.select2('destroy').select2({
                        templateResult: formatState,
                        templateSelection: formatState
                    });
                },error: function (error) {
                    alertmsg('Error.Please try again');
                }
            });
        } else {
            $('#variant-section').hide();
        }
    }

    function populatePrice(variantId, cityId) {
        if (variantId && cityId) {
            $('#variant-details').show();
            $.ajax({
                url: 'api/prices/' + variantId + '/' + cityId,
                method: 'GET',
                async: false,
                success: function (data) {
                    stockExshowroom = data.ex_showroom;
                    stockInsurance = data.insurance;
                    stockRto = data.rto;
                    stockWarranty = data.extended_warranty;
                    stockOther = data.other;
                    stockOnroad = data.onroad_price;

                    // if (originalRto != 0) stockRto = originalRto;
                    // if (originalInsurance != 0) stockInsurance = originalInsurance;
                    // if (originalExshowroom != 0) stockExshowroom = originalExshowroom;
                    // if (originalWarranty != 0) stockWarranty = originalWarranty;
                    // if (originalOther != 0) stockOther = originalOther;
                    // if (originalOnroad != 0) stockOnroad = originalOnroad;


                        $('#ex_showroom').val(stockExshowroom);
                        $('#insurance').val(stockInsurance);
                        $('#rto').val(stockRto);
                        $('#other').val(stockOther);
                        $('#warranty').val(stockWarranty);
                        $('#onroad_price').val(stockOnroad);
                        $('#discount').val(originalDiscount);
                        $('#custom_insurance').show();



                    originalValues = {
                        extendedWarranty: data.extendedWarranty,
                        exshowroomPrice: data.ex_showroom,
                        insurancePrice: data.insurance,
                        rtoPrice: data.rto,
                        otherPrice: data.other,
                        onroadPrice: data.onroad_price
                    };

                    updateTotalAmount();
                },error: function (error) {
                    alertmsg('Error.Please try again');
                }
            });
        } else {
            $('#variant-details').hide();
        }
    }


    function disableFields() {
        $('#vehDetails input, #vehDetails select').prop('disabled', true);
        $('#city').attr('disabled', true);
        $('#model').attr('disabled', true);
        $('#variant').attr('disabled', true);
        $('#vehDetailsSave').prop('disabled', true);
        $('#vehDetailsEdit').prop('disabled', false);
    }

    function enableFields() {
        $('#vehDetails input, #vehDetails select').prop('disabled', false);
        $('#city').prop('disabled', false);
        $('#model').prop('disabled', false);
        $('#variant').prop('disabled', false);
        $('#vehDetailsSave').prop('disabled', false);
        $('#vehDetailsEdit').prop('disabled', true);
    }

    function updateTotalAmount() {
        var exShowroom = parseFloat($('#ex_showroom').val()) || 0;
        var rto = parseFloat($('#rto').val()) || 0;
        var warranty = parseFloat($('#warranty').val()) || 0;
        var other =  0;
        //var other = parseFloat($('#other').val()) || 0;
        var discountPrice = parseFloat($('#discount').val()) || 0;
        if ($('#custom_insurance_checkbox').is(':checked')) {
            var customInsuranceAmount = parseFloat($('#custom_insurance_amount').val()) || 0;
            $('#insurance').val(customInsuranceAmount);
        }else{
            $('#insurance').val(originalValues.insurancePrice);
        }
        var insurance = parseFloat($('#insurance').val()) || 0;
        var onroadPrice = exShowroom + insurance + rto + other + warranty;
        $('#onroad_price').val(onroadPrice);
        $('#total-invoice-price').val(onroadPrice - discountPrice);
    }
    function updateTotalAmountNew() {
        var exShowroom = parseFloat($('#ex_showroom').val()) || 0;
        var rto = parseFloat($('#rto').val()) || 0;
        var warranty = parseFloat($('#warranty').val()) || 0;
        var other =  0;
        //var other = parseFloat($('#other').val()) || 0;
        var discountPrice = parseFloat($('#discount').val()) || 0;
        var insurance = parseFloat($('#insurance').val()) || 0;
        var onroadPrice = exShowroom + insurance + rto + other + warranty;
        $('#onroad_price').val(onroadPrice);
        $('#total-invoice-price').val(onroadPrice - discountPrice);
    }

    $('#custom_insurance_checkbox').change(function () {
        if (this.checked) {
            $('.custom-insurance-inputs').show().addClass('d-flex');
            $('#insurance').prop("readonly",true);
        } else {
            $('.custom-insurance-inputs').hide().removeClass('d-flex');
            $('#custom_insurance_amount').val('');
            $('#custom_insurance_remarks').val('');
            $('#insurance').prop("readonly",false);
            updateTotalAmount();
        }
    });

    $('#custom_insurance_amount').change(function () {
        updateTotalAmount();
    });

    $('#discount').change(function () {
        updateTotalAmount();
    });

    $('#variant-details').on('input change', 'input', function () {
        updateTotalAmountNew();
    });

    $('#vehDetailsEdit').click(function () {
        enableFields();
        $('#bottomCard').data('state', 0).attr('state', 0);
        //  disableAccordian();
    });

    function fileToBase64(file) {
        console.log("1");
        return new Promise((resolve, reject) => {
            console.log("2");
            const reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = () => resolve(reader.result.split(',')[1]);
            reader.onerror = error => reject(error);
        });
    }

    $('#vehDetailsSave').click(function () {
        var data
        if (validateVehicleDetailsForm() && validateChannelAndCodes() ) {

            const element = $('#dsa_err');

            element.empty();
            var fileInput = document.getElementById('invoiceDoc');
            var file = fileInput.files[0];
            console.log(file);
            var filealreadyuploaded = $('#fileup').val();
            console.log($('#fileup').val());
            console.log(file);
            // Check if file is selected
            if (file || (filealreadyuploaded!="")) {
                console.log(filealreadyuploaded);
                var base64file = "";
                var extfile = "";
                if(file){
                    return fileToBase64(file).then(base64 => {
                        base64file = base64;
                        extfile = file.name.split('.').pop();
                        console.log(base64file);
                        console.log(extfile);

                        data= {
                            ino: $('#ino').val(),
                            wiNum: $('#winum').val(),
                            slno: $('#slno').val(),
                            applicantId: $('[data-code="A-1"]').find('[name="appid"]').val(),
                            req_ip_addr: '',
                            dealerState: $('#state').val(),
                            dealerCityId: $('#city').val(),
                            dealerCityName: $('#city option:selected').text(),
                            dealerName: $('#dealer_name').val(),
                            dealerNameRemarks: $('#dealer_name_remarks').val(),
                            dstCode: $('#dst_code').val(),
                            dsaCode: $('#dsa_sub_code').val(),
                            dealerCode: $('#dealer_code').val(),
                            dealerSubCode: $('#dealer_sub_code').val(),
                            autodealerSourced:$('#autodealerSourced').val(),
                            makeId: $('#make').val(),
                            makeName: $('#make option:selected').text(),
                            modelId: $('#model').val(),
                            modelName: $('#model option:selected').text(),
                            variantId: $('#variant').val(),
                            variantName: $('#variant option:selected').text(),
                            exshowroomPrice: $('#ex_showroom').val(),
                            rtoPrice: $('#rto').val(),
                            insurancePrice: $('#custom_insurance_checkbox').is(':checked') ? $('#custom_insurance_amount').val() : $('#insurance').val(),
                            otherPrice: $('#other').val(),
                            onroadPrice: $('#onroad_price').val(),
                            extendedWarranty: $('#warranty').val(),
                            discountPrice: $('#discount').val(),
                            totalInvoicePrice: $('#total-invoice-price').val(),
                            invoiceNo: $('#invoice-no').val(),
                            invoiceDate: $('#invoice-date').val(),
                            colour: $('#color').val(),
                            cmuser: '',
                            cmdate: new Date(),
                            delFlg: 'N',
                            homeSol: '',
                            customInsurance: $('#custom_insurance_checkbox').is(':checked'),
                            customInsuranceAmount: $('#custom_insurance_amount').val(),
                            customInsuranceRemarks: $('#custom_insurance_remarks').val(),
                            dealerAccount: $('#dealerAccount').val(),
                            invoiceDoc: base64file, // Add base64 string to the data
                            invoiceExt: extfile  // Add file extension to the data
                        };
                        console.log("data: " + data);
                        $.ajax({
                            url: 'api/vehicleDetails',
                            method: 'POST',
                            contentType: 'application/json',
                            data: JSON.stringify(data),
                            async: false,
                            success: function (response) {

                                $.ajax({
                                    url: 'api/updateChannel',
                                    method: 'POST',
                                    async: false,
                                    data: {channel: $('#channel').val(), winum: $('#winum').val()},
                                    success: function (data) {
                                        console.log("inside success: " + data);
                                        $('#ino').val(response.ino);
                                        notyalt('Vehicle loan details saved successfully');
                                        $('#vehDetailsSave').prop('disabled', true);
                                        $('#vehDetailsEdit').prop('disabled', false);
                                        disableFields();
                                        // let state = 2;
                                        // $('#bottomCard').data('state', state).attr('state', state);
                                        // disableAccordian();
                                        $('#vehDetailsContent').collapse('hide');
                                        $('#loanDetailslink').trigger('click');
                                        $('#loanDetailsContent').collapse('show');
                                    },
                                    error: function (error) {
                                        alertmsg('Error saving vehicle loan details');
                                    }
                                });
                            },
                            error: function (error) {
                                alertmsg('Error saving vehicle loan details');
                            }
                        });

                        if ($('#custom_insurance_checkbox').is(':checked')) {
                            var revisionData = {
                                wiNum: $('#winum').val(),
                                slno: $('#slno').val(),
                                applicantId: $('[data-code="A-1"]').find('[name="appid"]').val(),
                                cmuser: '18202',
                                cmdate: new Date(),
                                exshowroomPrice: originalValues.exshowroomPrice,
                                insurancePrice: originalValues.insurancePrice,
                                rtoPrice: originalValues.rtoPrice,
                                otherPrice: originalValues.otherPrice,
                                onroadPrice: originalValues.onroadPrice,
                                remarks: $('#custom_insurance_remarks').val()
                            };

                            $.ajax({
                                url: 'api/updatePriceRevision',
                                method: 'POST',
                                contentType: 'application/json',
                                data: JSON.stringify(revisionData),
                                success: function (response) {
                                    console.log('Price revision details saved successfully');
                                },
                                error: function (error) {
                                    console.log('Error saving price revision details');
                                }
                            });
                        }

                    });



                }else{
                    base64file = $('#fileup').val();
                    extfile = $('#fileupext').val();
                    data= {
                        ino: $('#ino').val(),
                        wiNum: $('#winum').val(),
                        slno: $('#slno').val(),
                        applicantId: $('[data-code="A-1"]').find('[name="appid"]').val(),
                        req_ip_addr: '',
                        dealerState: $('#state').val(),
                        dealerCityId: $('#city').val(),
                        dealerCityName: $('#city option:selected').text(),
                        dealerName: $('#dealer_name').val(),
                        dealerNameRemarks: $('#dealer_name_remarks').val(),
                        dstCode: $('#dst_code').val(),
                        dsaCode: $('#dsa_sub_code').val(),
                        dealerCode: $('#dealer_code').val(),
                        dealerSubCode: $('#dealer_sub_code').val(),
                        autodealerSourced: $('#autodealerSourced').val(),
                        makeId: $('#make').val(),
                        makeName: $('#make option:selected').text(),
                        modelId: $('#model').val(),
                        modelName: $('#model option:selected').text(),
                        variantId: $('#variant').val(),
                        variantName: $('#variant option:selected').text(),
                        exshowroomPrice: $('#ex_showroom').val(),
                        rtoPrice: $('#rto').val(),
                        insurancePrice: $('#custom_insurance_checkbox').is(':checked') ? $('#custom_insurance_amount').val() : $('#insurance').val(),
                        otherPrice: $('#other').val(),
                        onroadPrice: $('#onroad_price').val(),
                        extendedWarranty: $('#warranty').val(),
                        discountPrice: $('#discount').val(),
                        totalInvoicePrice: $('#total-invoice-price').val(),
                        invoiceNo: $('#invoice-no').val(),
                        invoiceDate: $('#invoice-date').val(),
                        colour: $('#color').val(),
                        dealerAccount: $('#dealerAccount').val(),
                        cmuser: '',
                        cmdate: new Date(),
                        delFlg: 'N',
                        homeSol: '',
                        customInsurance: $('#custom_insurance_checkbox').is(':checked'),
                        customInsuranceAmount: $('#custom_insurance_amount').val(),
                        customInsuranceRemarks: $('#custom_insurance_remarks').val(),
                        invoiceDoc: base64file, // Add base64 string to the data
                        invoiceExt: extfile  // Add file extension to the data
                    };
                    console.log("data: " + data);
                    $.ajax({
                        url: 'api/vehicleDetails',
                        method: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(data),
                        async: false,
                        success: function (response) {
                            console.log("vehicle updation success. next step updating channel");
                            $.ajax({
                                url: 'api/updateChannel',
                                method: 'POST',
                                async: false,
                                data: {channel: $('#channel').val(), winum: $('#winum').val()},
                                success: function (data) {
                                    console.log("inside success: " + data);
                                    $('#ino').val(response.ino);
                                    notyalt('Vehicle loan details saved successfully');
                                    $('#vehDetailsSave').prop('disabled', true);
                                    $('#vehDetailsEdit').prop('disabled', false);
                                    disableFields();
                                    // let state = 2;
                                    // $('#bottomCard').data('state', state).attr('state', state);
                                    // disableAccordian();
                                    $('#vehDetailsContent').collapse('hide');
                                    $('#loanDetailslink').trigger('click');
                                    $('#loanDetailsContent').collapse('show');
                                },
                                error: function (error) {
                                    alertmsg('Error saving vehicle loan details');
                                }
                            });
                        },
                        error: function (error) {
                            alertmsg('Error saving vehicle loan details');
                        }
                    });

                    if ($('#custom_insurance_checkbox').is(':checked')) {
                        var revisionData = {
                            wiNum: $('#winum').val(),
                            slno: $('#slno').val(),
                            applicantId: $('[data-code="A-1"]').find('[name="appid"]').val(),
                            cmuser: '18202',
                            cmdate: new Date(),
                            exshowroomPrice: originalValues.exshowroomPrice,
                            insurancePrice: originalValues.insurancePrice,
                            rtoPrice: originalValues.rtoPrice,
                            otherPrice: originalValues.otherPrice,
                            onroadPrice: originalValues.onroadPrice,
                            remarks: $('#custom_insurance_remarks').val()
                        };

                        $.ajax({
                            url: 'api/updatePriceRevision',
                            method: 'POST',
                            contentType: 'application/json',
                            data: JSON.stringify(revisionData),
                            success: function (response) {
                                console.log('Price revision details saved successfully');
                            },
                            error: function (error) {
                                console.log('Error saving price revision details');
                            }
                        });
                    }
                }
            } else {
                //alert('Please upload an invoice document.');
                alertmsg("Please upload an invoice document.");
            }
        }
    });

    function formatState(opt) {
        if (!opt.id) {
            return opt.text.toUpperCase();
        }

        var optimage = $(opt.element).attr('data-image');

        if (!optimage) {
            return opt.text.toUpperCase();
        } else {
            var $opt = $(
                '<span><img class="rounded-pill" src="' + optimage + '" width="25px" height="25px" /> ' + opt.text.toUpperCase() + '</span>'
            );
            return $opt;
        }
    }

    $('.select').select2({
        templateResult: formatState,
        templateSelection: formatState
    });

    function validateVehicleDetailsForm() {
        let isValid = true;
        const formFields = {
            'ex_showroom': {
                value: $('#ex_showroom').val().trim(),
                required: true,
                number: true,
                errorMessage: 'Please enter a valid ex-showroom price.',
                isSelect2: false
            },
            'insurance': {
                value: $('#insurance').val().trim(),
                required: true,
                number: true,
                errorMessage: 'Please enter a valid insurance amount.',
                isSelect2: false
            },
            'rto': {
                value: $('#rto').val().trim(),
                required: true,
                number: true,
                errorMessage: 'Please enter a valid RTO price.',
                isSelect2: false
            },
            'warranty': {
                value: $('#warranty').val().trim(),
                required: true,
                number: true,
                errorMessage: 'Please enter a valid warranty amount.',
                isSelect2: false
            },
            'other': {
                value: $('#other').val().trim(),
                number: true,
                errorMessage: 'Please enter a valid amount.',
                isSelect2: false
            },
            'onroad_price': {
                value: $('#onroad_price').val().trim(),
                required: true,
                number: true,
                errorMessage: 'Please enter a valid on-road price.',
                isSelect2: false
            },
            'total-invoice-price': {
                value: $('#total-invoice-price').val().trim(),
                required: true,
                number: true,
                errorMessage: 'Please enter a valid total invoice price.',
                isSelect2: false
            },
            'invoice-no': {
                value: $('#invoice-no').val().trim(),
                required: true,
                errorMessage: 'Please enter the invoice number.',
                isSelect2: false
            },
            'invoice-date': {
                value: $('#invoice-date').val().trim(),
                required: true,
                date: true,
                errorMessage: 'Please enter a valid invoice date.',
                isSelect2: false
            },
            'dealer_name': {
                value: $('#dealer_name').val(),
                required: true,
                errorMessage: 'Please select Dealer Name',
                isSelect2: true
            },
            'model': {
                value: $('#model').val(),
                required: true,
                errorMessage: 'Please select Model',
                isSelect2: true
            },
            'dealerAccount': {
                value: $('#dealerAccount').val(),
                required: true,
                errorMessage: 'Please select dealer Account',
                isSelect2: true
            },
            'variant': {
                value: $('#variant').val(),
                required: true,
                errorMessage: 'Please select Variant',
                isSelect2: true
            },
            'make': {
                value: $('#make').val(),
                required: true,
                errorMessage: 'Please select Make',
                isSelect2: true
            },
            'dealer_sub_code': {
                value: $('#dealer_sub_code').val(),
                required: true,
                errorMessage: 'Please select Dealer Sub Code',
                isSelect2: true
            },
            'dealer_code': {
                value: $('#dealer_code').val(),
                required: true,
                errorMessage: 'Please Enter Dealer Code',
                isSelect2: false
            },
            'city': {
                value: $('#city').val(),
                required: true,
                errorMessage: 'Please select City',
                isSelect2: true
            },
            'state': {
                value: $('#state').val(),
                required: true,
                errorMessage: 'Please Enter State',
                isSelect2: false
            },
            'custom_insurance_amount': {
                value: $('#custom_insurance_amount').val(),
                required: $('#custom_insurance_checkbox').is(':checked') ? true : false,
                errorMessage: 'Please Enter Custom insurance amount',
                isSelect2: false
            },
            'custom_insurance_remarks': {
                value: $('#custom_insurance_remarks').val(),
                required: $('#custom_insurance_checkbox').is(':checked') ? true : false,
                errorMessage: 'Please Enter Company Name',
                isSelect2: false
            },
            'discount': {
                value: $('#discount').val().trim(),
                required: false,
                number: true,
                errorMessage: 'Please enter a valid discount amount.',
                isSelect2: false
            },
            'channel': {
                value: $('#channel').val(),
                required: true,
                errorMessage: 'Please select the channel',
                isSelect2: true
            },
            'autodealerSourced': {
                value: $('#autodealerSourced').val(),
                required: true,
                errorMessage: 'Please select if Autodealer sourced or not',
                isSelect2: true
            }
        };

        $.each(formFields, function (fieldName, field) {
            const element = $('#' + fieldName);
            const errorContainer = field.isSelect2 ? element.next('.select2-container') : element;

            // Remove previous error message if any
            errorContainer.next('.error-message').remove();
            let fieldValid = true;
            if (field.required && !field.value) {
                fieldValid = false;
            }
            if (field.number && field.value && isNaN(Number(field.value))) {
                fieldValid = false;
            }
            if (field.date && field.value && !/^\d{4}[-/]\d{1,2}[-/]\d{1,2}$/.test(field.value)) {
                fieldValid = false;
            }
            if (!fieldValid) {
                if (field.isSelect2) {
                    errorContainer.addClass('is-invalid');
                } else {
                    element.addClass('is-invalid');
                }
                element.removeClass('is-valid');
                isValid = false;
                // Append error message
                $('<span class="error-message text-danger">' + field.errorMessage + '</span>').insertAfter(errorContainer);
            } else {
                if (field.isSelect2) {
                    errorContainer.removeClass('is-invalid');
                    errorContainer.addClass('is-valid');
                } else {
                    element.removeClass('is-invalid');
                    element.addClass('is-valid');
                }
            }
        });

        // Validate the channel and code relationships
        const channelValid = validateChannelAndCodes();
        if (!channelValid) {
            isValid = false;
        }

        return isValid;
    }

    // $('#vehicleDetailsForm').on('input', 'input[type="text"]', function () {
    //     this.value = this.value.replace(/[^0-9.]/g, ''); // Allow numbers and decimal points only
    // });

    $('.text-only').on('input', 'input[type="text"]', function () {
        this.value = this.value.replace(/[^0-9.]/g, ''); // Allow numbers and decimal points only
    });

    function validateDropdown(element, errorMessage, showErrors = false) {
        var $element = $(element);
        var $errorLabel = $element.next('.select2-container').next('.error-label');

        var isValid = $element.find('option:selected').index() > 0;

        if (!isValid && showErrors) {
            if (!$errorLabel.length) {
                $errorLabel = $('<label class="error-label" style="color: red;"></label>');
                $errorLabel.insertAfter($element.next('.select2-container'));
            }
            $errorLabel.text(errorMessage);
        } else {
            if ($errorLabel.length) {
                $errorLabel.remove();
            }
        }
        return isValid;
    }

    function validateCustomInsurance(showErrors = false) {
        var isValid = true;
        var $checkbox = $('#custom_insurance_checkbox');
        var $amount = $('#custom_insurance_amount');
        var $remarks = $('#custom_insurance_remarks');

        function setErrorFor(element, message) {
            if (showErrors) {
                var $errorLabel = element.next('.error-label');
                if (!$errorLabel.length) {
                    $errorLabel = $('<label class="error-label" style="color: red;"></label>');
                    $errorLabel.insertAfter(element);
                }
                $errorLabel.text(message);
            }
        }

        function clearErrorFor(element) {
            var $errorLabel = element.next('.error-label');
            if ($errorLabel.length) {
                $errorLabel.remove();
            }
        }

        if ($checkbox.is(':checked')) {
            if (!$amount.val().trim()) {
                setErrorFor($amount, "Please enter a custom insurance amount");
                isValid = false;
            } else {
                clearErrorFor($amount);
            }

            if (!$remarks.val().trim()) {
                setErrorFor($remarks, "Please enter insurance company name");
                isValid = false;
            } else {
                clearErrorFor($remarks);
            }
        } else {
            clearErrorFor($amount);
            clearErrorFor($remarks);
        }

        return isValid;
    }

    function populateState(stateName) {
        $.ajax({
            url: 'api/states',
            method: 'GET',
            success: function (data) {
                var stateDropdown = $('#state');
                stateDropdown.empty();
                if (!stateName) {
                    stateDropdown.append('<option value="" selected disabled>Select a state</option>');
                } else
                    stateDropdown.append('<option value="" disabled>Select a state</option>');
                $.each(data, function (key, value) {
                    var selected = (stateName && stateName === value.state_name) ? 'selected' : '';
                    stateDropdown.append('<option ' + selected + ' value="' + value.state_name + '">' + value.state_name + ' </option>');
                });
                stateDropdown.select2('destroy').select2({
                    templateResult: formatState,
                    templateSelection: formatState
                });
            }
        });
    }


    // Add this validation function to your vehicle.js file
    function validateChannelAndCodes() {
        const channel = $('#channel').val();
        const autodealerSourced = $('#autodealerSourced').val();
        const dstCode = $('#dst_code').val();
        const dsaSubCode = $('#dsa_sub_code').val();
        let isValid = true;
        let errorMessage = "";

        // Clear previous error messages
        $('#dsa_err').empty();
        $('#dst_err').empty();
        $('#chennel_err').empty();
        $('#payout_err').empty();

        // Validation rules based on Channel selection
        switch(channel) {
            case "BRANCH MARKETING":
                // DST Code is optional, No dealer payout
                if (autodealerSourced === "Y") {
                    isValid = false;
                    errorMessage = "Payout Error: For Branch Marketing channel, Payout should not be given.";
                }
                break;

            case "AUTO DEALER":
                // DST Code is optional, Dealer payout is Yes
                if (autodealerSourced === "N") {
                    isValid = false;
                    errorMessage = "Payout Error: For Auto Dealer channel, Payout should be given.";
                }
                break;

            case "MARKET DSA":
                // DST Code should be No (empty), No dealer payout, DSA Sub Code required
                if (dstCode) {
                    isValid = false;
                    errorMessage = "DST Code Error: For Market DSA channel, DST Code should not be selected.";
                }
                if (autodealerSourced === "Y") {
                    isValid = false;
                    errorMessage = "Payout Error: For Market DSA channel, Payout should not be given.";
                }
                // if (!dsaSubCode) {
                //     isValid = false;
                //     errorMessage = "DSA SUB CODE Error: For Market DSA channel, DSA Sub Code is required.";
                // }
                break;

            case "ADVISOR":
                // DST Code should be No (empty), No dealer payout
                if (dstCode) {
                    isValid = false;
                    errorMessage = "DST Code Error: For Advisor channel, DST Code should not be selected.";
                }
                if (autodealerSourced === "Y") {
                    isValid = false;
                    errorMessage = "Payout Error: For Advisor channel, Payout should not be given.";
                }
                break;

            case "AUTODEALER ASSISTED BY BRANCH":
                // DST Code should be No (empty), Dealer payout is Yes
                if (dstCode) {
                    isValid = false;
                    errorMessage = "DST Code Error: For Auto Dealer Assisted by Branch channel, DST Code should not be selected.";
                }
                if (autodealerSourced === "N") {
                    isValid = false;
                    errorMessage = "Payout Error: For Auto Dealer Assisted by Branch channel, Payout should be given.";
                }
                break;

            case "DST SELF SOURCED":
                // DST Code should be Yes (value selected), No dealer payout
                if (!dstCode) {
                    isValid = false;
                    errorMessage = "DST Code Error: For DST Self Sourced channel, DST Code must be selected.";
                }
                if (autodealerSourced === "Y") {
                    isValid = false;
                    errorMessage = "Payout Error: For DST Self Sourced channel, Payout should not be given.";
                }
                break;

            case "DST PORTAL":
                // DST Code should be Yes (value selected), Optional dealer payout
                if (!dstCode) {
                    isValid = false;
                    errorMessage = "DST Code Error: For DST Portal channel, DST Code must be selected.";
                }
                break;

            case "DEALER PORTAL":
                // DST Code should be No (empty), Dealer payout is Yes
                if (dstCode) {
                    isValid = false;
                    errorMessage = "DST Code Error:For Dealer Portal channel, DST Code should not be selected.";
                }
                if (autodealerSourced === "N") {
                    isValid = false;
                    errorMessage = "Payout Error: For Dealer Portal channel, Payout should be given.";
                }
                break;

            case "MARKET DSA PORTAL":
                // DST Code should be No (empty), No dealer payout
                if (dstCode) {
                    isValid = false;
                    errorMessage = "DST Code Error: For Market DSA Portal channel, DST Code should not be selected.";
                }
                if (autodealerSourced === "Y") {
                    isValid = false;
                    errorMessage = "Payout Error: For Market DSA Portal channel, Payout should not be given.";
                }
                // if (!dsaSubCode) {
                //     isValid = false;
                //     errorMessage = "DSA SUB CODE Error: For Market DSA Portal channel, DSA Sub Code is required.";
                // }
                break;

            default:
                if (!channel) {
                    isValid = false;
                    errorMessage = "Channel Error: Please select a channel.";
                }
                break;
        }

        // Check mutual exclusivity between DST Code and DSA Sub Code
        if (dstCode && dsaSubCode) {
            isValid = false;
            errorMessage = "DSA SUB CODE Error: Both DST Code and DSA Sub Code cannot be selected simultaneously.";
        }

        // Display error message if validation fails
        if (!isValid) {




            if (errorMessage.includes("DST Code Error:")) {
                $('#dst_code').next('.select2-container').addClass('is-invalid');
                $('#dst_err').html('<span class="error-message text-danger">' + errorMessage + '</span>');
            }
            if (errorMessage.includes("DSA SUB CODE Error:")) {
                $('#dsa_sub_code').next('.select2-container').addClass('is-invalid');
                $('#dsa_err').html('<span class="error-message text-danger">' + errorMessage + '</span>');
            }
            if (errorMessage.includes("Payout Error: ")) {
                $('#autodealerSourced').next('.select2-container').addClass('is-invalid');
                $('#payout_err').html('<span class="error-message text-danger">' + errorMessage + '</span>');
            }
            if (errorMessage.includes("Channel Error:")) {
                $('#channel').next('.select2-container').addClass('is-invalid');
                $('#channel_err').html('<span class="error-message text-danger">' + errorMessage + '</span>');
            }
        } else {
            $('#dst_code, #dsa_sub_code, #autodealerSourced, #channel').next('.select2-container').removeClass('is-invalid').addClass('is-valid');
        }

        return isValid;
    }

    function highlightRequiredFields(channel) {
        // Reset all styling first
        $('#dst_code, #dsa_sub_code').next('.select2-container')
            .removeClass('required-field select2-container--focus');

        // Add visual indicator for required fields based on channel
        switch(channel) {
            case "MARKET DSA":
            case "MARKET DSA PORTAL":
                // DSA Sub Code is required
                $('#dsa_sub_code').next('.select2-container').addClass('required-field');
                break;

            case "DST SELF SOURCED":
            case "DST PORTAL":
                // DST Code is required
                $('#dst_code').next('.select2-container').addClass('required-field');
                break;
        }
    }

    // Add this function to initialize validation state on page load
    function initializeChannelValidation() {
        // Get current channel value
        const channel = $('#channel').val();

        // If channel is already selected, apply its rules
        if (channel) {
            // Apply the channel-specific rules
            switch(channel) {
                case "BRANCH MARKETING":
                    // DST Code optional, No dealer payout
                    $('#autodealerSourced').val('N').trigger('change');
                    break;

                case "AUTO DEALER":
                    // DST Code optional, Dealer payout is Yes
                    $('#autodealerSourced').val('Y').trigger('change');
                    break;

                case "MARKET DSA":
                case "ADVISOR":
                case "MARKET DSA PORTAL":
                    // DST Code should be empty, No dealer payout
                    $('#autodealerSourced').val('N').trigger('change');
                    // Clear DST Code if selected
                    if ($('#dst_code').val()) {
                        $('#dst_code').val('').trigger('change');
                    }
                    break;

                case "AUTODEALER ASSISTED BY BRANCH":
                case "DEALER PORTAL":
                    // DST Code should be empty, Dealer payout is Yes
                    $('#autodealerSourced').val('Y').trigger('change');
                    // Clear DST Code if selected
                    if ($('#dst_code').val()) {
                        $('#dst_code').val('').trigger('change');
                    }
                    break;

                case "DST SELF SOURCED":
                case "DST PORTAL":
                    // DST Code required, No dealer payout for DST Self Sourced
                    if (channel === "DST SELF SOURCED") {
                        $('#autodealerSourced').val('N').trigger('change');
                    }
                    // Clear DSA Sub Code if selected
                    if ($('#dsa_sub_code').val()) {
                        $('#dsa_sub_code').val('').trigger('change');
                    }
                    break;
            }

            // Highlight required fields
            highlightRequiredFields(channel);
        }
    }

    $('#channel').change(function() {
        const channel = $(this).val();

        // Reset fields
        $('#dst_code, #dsa_sub_code').val('').trigger('change');
        $('#dsa_err').empty();
        $('#dst_err').empty();
        $('#chennel_err').empty();
        $('#payout_err').empty();
        $('#dst_code, #dsa_sub_code').next('.select2-container').removeClass('is-invalid is-valid');

        // Apply rules based on channel
        switch(channel) {
            case "BRANCH MARKETING":
                // DST Code is optional, No dealer payout
                $('#autodealerSourced').val('N').trigger('change');
                // Both DST and DSA are optional
                break;

            case "AUTO DEALER":
                // DST Code is optional, Dealer payout is Yes
                $('#autodealerSourced').val('Y').trigger('change');
                // Both DST and DSA are optional
                break;

            case "MARKET DSA":
                // DST Code should be empty, No dealer payout, DSA Sub Code required
                $('#autodealerSourced').val('N').trigger('change');
                $('#dst_code').val('').trigger('change');
                // Focus on DSA Sub Code field
                setTimeout(function() {
                    $('#dsa_sub_code').select2('open');
                }, 100);
                break;

            case "ADVISOR":
                // DST Code should be empty, No dealer payout
                $('#autodealerSourced').val('N').trigger('change');
                $('#dst_code').val('').trigger('change');
                break;

            case "AUTODEALER ASSISTED BY BRANCH":
                // DST Code should be empty, Dealer payout is Yes
                $('#autodealerSourced').val('Y').trigger('change');
                $('#dst_code').val('').trigger('change');
                break;

            case "DST SELF SOURCED":
                // DST Code must be selected, No dealer payout
                $('#autodealerSourced').val('N').trigger('change');
                $('#dsa_sub_code').val('').trigger('change');
                // Focus on DST Code field
                setTimeout(function() {
                    $('#dst_code').select2('open');
                }, 100);
                break;

            case "DST PORTAL":
                // DST Code must be selected, dealer payout optional
                $('#dsa_sub_code').val('').trigger('change');
                // Focus on DST Code field
                setTimeout(function() {
                    $('#dst_code').select2('open');
                }, 100);
                break;

            case "DEALER PORTAL":
                // DST Code should be empty, Dealer payout is Yes
                $('#autodealerSourced').val('Y').trigger('change');
                $('#dst_code').val('').trigger('change');
                break;

            case "MARKET DSA PORTAL":
                // DST Code should be empty, No dealer payout, DSA Sub Code required
                $('#autodealerSourced').val('N').trigger('change');
                $('#dst_code').val('').trigger('change');
                // Focus on DSA Sub Code field
                setTimeout(function() {
                    $('#dsa_sub_code').select2('open');
                }, 100);
                break;
        }

        // Highlight required fields based on channel
        highlightRequiredFields(channel);
    });

    $('#dst_code').on('change', function() {
        const dstCode = $(this).val();
        if (dstCode) {
            // If DST Code is selected, clear DSA Sub Code
            $('#dsa_sub_code').val('').trigger('change');
        }
        // Clear error message
        $('#dsa_err').empty();
        $('#dst_err').empty();
        $('#chennel_err').empty();
        $('#payout_err').empty();
    });

    $('#dsa_sub_code').on('change', function() {
        const dsaSubCode = $(this).val();
        if (dsaSubCode) {
            // If DSA Sub Code is selected, clear DST Code
            $('#dst_code').val('').trigger('change');
        }
        // Clear error message
     //   $('#dsa_err').empty();
   //     $('#dst_err').empty();
    //    $('#chennel_err').empty();
   //     $('#payout_err').empty();
    });

    $('head').append(`
        <style>
            .required-field {
                border: 1px solid #ffc107 !important;
                box-shadow: 0 0 0 0.2rem rgba(255, 193, 7, 0.25) !important;
            }
            .select2-container.is-invalid {
                border: 1px solid #dc3545 !important;
                border-radius: 0.25rem;
            }
            .select2-container.is-valid {
                border: 1px solid #28a745 !important;
                border-radius: 0.25rem;
            }
        </style>
    `);
});
