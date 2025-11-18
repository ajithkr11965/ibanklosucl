$(document).ready(function() {
    populateExistingLoanDetails('D');
    populateVehicleAmount();
    bindEvents();
    const isRm = $('#currentQueue').val()==='RM';

    var insVal='';
    var insType='';
    var insAmt='';

    // Define all functions but do not execute them on document ready
    function populateVehicleAmount() {
        var onroadPrice = $('#total-invoice-price').val(); // Assuming onroad_price is available in the variant details table
        $('#vehicle-amount').val(onroadPrice);
    }


    function setReadonlyIfPopulated() {
        $('#loanDetails ul input,#loanDetails ul select').each(function() {
            if ($(this).val())
            {
                populateflag=true;
            }

        });
        if(populateflag){
            var state = $('#bottomCard').data('state');
            state +=1;
            $('#bottomCard').data('state',state).attr('state',state);

            $('#loanDetails ul input,#loanDetails ul select').each(function() {
                $('#vehDetailsSave').prop('disabled', true);
                $(this).prop('readonly', true);
                $(this).prop('disabled', true);
            });}
    }



    function validateLoanDetailsForm() {
        let isValid = true;

        const formFields = {
            'vehicle-amount': {
                value: $('#vehicle-amount').val().trim(),
                required: true,
                decimal: true,
                errorMessage: 'Please enter a valid vehicle amount (numbers only).'
            },
            'loan-amount': {
                value: $('#loan-amount').val().trim(),
                required: true,
                digits: true,
                errorMessage: 'Please enter a valid loan amount (numbers only).'
            },
            'insAmt': {
                ins:true,
                value: $('#insAmt').val().trim(),
                amt: true,
                errorMessage: 'Please enter a valid Premium amount (numbers only).'
            },
            'insType': {
                ins:true,
                value: $('#insType').val().trim(),
                errorMessage: 'Please Select Insurance Partner.'
            },
            tenor: {
                value: $('#tenor').val().trim(),
                required: true,
                digits: true,
                errorMessage: 'Please enter a valid tenor (numbers only).'
            }
        };

        $.each(formFields, function(fieldName, field) {
            const element = $('#' + fieldName.replace("'", ""));
            let fieldValid = true;

            // Remove previous error message if any
            element.next('.error-message').remove();
            $('input[name="foirType"]').parent().parent().find('.error-message').remove() ;
            $('input[name="roiType"]').parent().parent().find('.error-message').remove();
            $('input[name="insVal"]').parent().parent().find('.error-message').remove();

            if (field.required && !field.value) {
                fieldValid = false;
            }
            if (field.ins && !field.value && $('input[name="insVal"]:checked').val()==='Y') {
                fieldValid = false;
            }
            if (field.ins && field.amt && $('input[name="insVal"]:checked').val()==='Y'  && !/^\d+(\.\d{1,2})?$/.test(field.value)) {
                fieldValid = false;
            }

            if (field.digits && !/^\d+$/.test(field.value)) {
                fieldValid = false;
            }
            if (field.decimal && !/^\d+(\.\d{1,2})?$/.test(field.value)) {
                fieldValid = false;
            }

            if (!fieldValid) {
                element.addClass('is-invalid');
                element.removeClass('is-valid');
                isValid = false;

                // Append error message
                $('<span class="error-message text-danger">' + field.errorMessage + '</span>').insertAfter(element);
            } else {
                element.removeClass('is-invalid');
                element.addClass('is-valid');
            }
        });

        // Validate radio button group for foirType
        const foirTypeValid = $('input[name="foirType"]:checked').length > 0;
        if (!foirTypeValid) {
            $('input[name="foirType"]').each(function() {
                $(this).addClass('is-invalid').removeClass('is-valid');
            });

            // Append error message for foirType
            $('<span class="error-message text-danger">Please select a FOIR type.</span>').insertAfter($('input[name="foirType"]').last().parent());
            isValid = false;
        } else {
            $('input[name="foirType"]').each(function() {
                $(this).removeClass('is-invalid').addClass('is-valid');
            });
        }

        // Validate radio button group for roiType
        const roiType = $('input[name="roiType"]:checked').length > 0;
        if (!roiType) {
            $('input[name="roiType"]').each(function() {
                $(this).addClass('is-invalid').removeClass('is-valid');
            });

            // Append error message for roiType
            $('<span class="error-message text-danger">Please select a ROI type.</span>').insertAfter($('input[name="roiType"]').last().parent());
            isValid = false;
        } else {
            $('input[name="roiType"]').each(function() {
                $(this).removeClass('is-invalid').addClass('is-valid');
            });
        }
        const insVal = $('input[name="insVal"]:checked').length > 0;
        if (!insVal) {
            $('input[name="insVal"]').each(function() {
                $(this).addClass('is-invalid').removeClass('is-valid');
            });

            // Append error message for roiType
            $('<span class="error-message text-danger">Please check Whether Insurance protection needed.</span>').insertAfter($('input[name="insVal"]').last().parent());
            isValid = false;
        } else {

            $('input[name="insVal"]').each(function() {
                $(this).removeClass('is-invalid').addClass('is-valid');
            });
        }

        return isValid;
    }


    $('#onroad_price').change(function() {populateVehicleAmount();});
    function populateExistingLoanDetails(code) {
        var winum = $('#winum').val();
        var slno = $('#slno').val();
        var applicantId = $('[data-code="A-1"]').find('[name="appid"]').val();
        $.ajax({
            url: 'api/fetchLoanDetails',
            method: 'GET',
            data: { wiNum: winum, slno: slno, applicantId: applicantId },
            success: function(data) {
                if (data && data.vehicleAmt) {
                    $('#vehicle-amount').val(data.vehicleAmt);
                    $('#loan-amount').val(data.loanAmt);
                    if(data.tenor!='0') {
                        $('#tenor').val(data.tenor);
                        $('#insType').val(data.insType).trigger('change');
                        $('#insAmt').val(data.insAmt);
                        if(isRm){
                            insType=data.insType;
                            insVal=data.insVal;
                            insAmt=data.insAmt;
                        }
                        $('input[name="roiType"][value="' + (data.roiType === 'FIXED' ? 'fixed' : 'floating') + '"]').prop('checked', true);
                        $('input[name="foirType"][value="' + (data.foirType === 'Y' ? 'foir' : 'non-foir') + '"]').prop('checked', true);
                        $('input[name="insVal"][value="' + (data.insVal === 'Y' ? 'Y' : 'N') + '"]').prop('checked', true).trigger('change');
                        $('#loanDetailsSave').prop('disabled', true);
                        $('#roi-floating').prop('disabled', true);
                        $('#foir-foir').prop('disabled', true);
                    }
                    else
                        $('#loanDetailsEdit').trigger('click');
                }
                else if(code==='C'){
                    alertmsgvert('Kindly Complete Vehicle Details!');
                    setTimeout(() => {
                        $('#loanDetailsContent').collapse('hide');
                    }, 1000); // Defer the collapse to ensure it happens after any UI updates
                }

            },
            error: function(error) {
                alertmsg('Error fetching  loan details '+error);
                setTimeout(() => {
                    $('#loanDetailsContent').collapse('hide');
                }, 250); // Defer the collapse to ensure it happens after any UI updates
            }
        });
    }

    $('input[name="insVal"]').on('change', function() {
        let selectedValue = $('input[name="insVal"]:checked').val();
       if(selectedValue==='N'){
           $('.insVal').addClass('d-none');
           $('#insType').val('').trigger('change');
           $('#insAmt').val('');
       }
       else{
           if(isRm){
               $('#insType').val(insType).trigger('change');
               $('#insAmt').val(insAmt);
           }
           $('.insVal').removeClass('d-none')
       }

    });
    $('input[name="insVal"]:checked').trigger('change');


    $('#loanDetailsSave').click(function() {
        if (validateLoanDetailsForm()) {
            var data = {
                wiNum: $('#winum').val(),
                slno: $('#slno').val(),
                applicantId: $('[data-code="A-1"]').find('[name="appid"]').val(),
                vehicleAmt: $('#vehicle-amount').val(),
                loanAmt: $('#loan-amount').val(),
                tenor: $('#tenor').val(),
                roiType: $('input[name="roiType"]:checked').val() === 'fixed' ? 'FIXED' : 'FLOATING',
                foirType: $('input[name="foirType"]:checked').val() === 'foir' ? 'Y' : 'N',
                insVal: $('input[name="insVal"]:checked').val(),
                insType: $('#insType').val(),
                insAmt: $('#insAmt').val()
            };
            $.ajax({
                url: 'api/saveLoanDetails',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: function (response) {
                    notyalt('Loan details saved Successfully');
                    $('#roi-floating').prop('disabled', true);
                    $('#loanDetailsSave').prop('disabled', true);
                    $('#loanDetailsEdit').prop('disabled', false);
                    updateAccordionStyle('loanDetailsContent', true);
                    $('#ebityDetailslink').trigger('click');
                    $('#eligibilityDetailsContent').collapse('show');
                },
                error: function (error) {
                    if (error.responseJSON) {
                        alertmsg( error.responseJSON.errorMessage);
                    } else {
                        alertmsg('Error saving loan details');
                    }
                }
            });
        }
    });





    $('#loanDetailsEdit').click(function() {
        $('#loanDetailsSave').prop('disabled', false);
        $('#loanDetailsSave').removeAttr('disabled');
        $('#loanDetailsEdit').prop('disabled', true);
        $('#tenor').prop('readonly', false);
        $('#tenor').prop('disabled', false);
        $('#roi-floating').prop('disabled', false);
        $('#foir-foir').prop('disabled', false);
        $('#loan-amount').prop('disabled', false);
        $('#loan-amount').prop('readonly', false);
        $('#insAmt').prop('readonly', false);
        $('#insType').prop('readonly', false);
        $('#insAmt').prop('disabled', false);
        $('#insType').prop('disabled', false);
        $('input[name="roiType"]').prop('disabled', false);
        $('input[name="foirType"]').prop('disabled', false);
        $('input[name="insVal"]').prop('disabled', false);
    });
    function bindEvents() {
        $('#loanDetailsSave').prop('disabled', true);
        $('#loanDetailsEdit').removeAttr('disabled');
    }


    // Execute functions on click of Loan Details
    $('#loanDetailslink').click(function(e) {
        e.preventDefault();
        showLoader();
        document.body.offsetHeight;
        if(checkSaveButtons()) {
            populateVehicleAmount();
            populateExistingLoanDetails('C');
            bindEvents();
            hideLoader();
        }
        else
        {
            setTimeout(() => {
                $('#loanDetailsContent').collapse('hide');
                $('#eligibilityDetailsContent').collapse('hide');
            }, 1000); // Defer the collapse to ensure it happens after any UI updates

            hideLoader();
        }
    });


});
