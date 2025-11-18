$(document).ready(function () {
    const wiNum = $('#winum').val();
    const slNo = $('#slno').val();
    const currentQueue = $('#currentQueue').val();
    let program, specificEmploymentType, annualIncome, bankBalance, creditScore;
    let bypass = bypassCheck();
    let allowEligibility = true;
    let ltvType = "none";
    let eligibilityIno;
    let originalROI;
    let originalRecAmt;
    let initialValues = {};
    let tempMainROI;
    let tempMainRecAmt;


    if (currentQueue === "RM") {
        $(".proceed-button-rm").prop("disabled", true);
    }
    setupEventListeners();

    function setupEventListeners() {
        $('#proceedSection').hide();
        $('#reset-button').click(handleReset);
        $('#ltv-percent').change(handleLTVPercentChange);
        $('#ebityDetailslink').click(handleEligibilityDetailsLinkClick);
        $('.check-eligibility-button').click(checkEligibility);
        $('.proceed-button').click(submitForm);
        $('#cancel-button').click(handleCancelClick);
        $('#edit-amount-icon').click(handleEditAmount);
        $('#edit-roi-icon').click(handleEditROI);
        $('#fetch-emi-button').click(handleFetchEMIClick);
        $('#confirm-button').click(handleConfirmClick);
        $('#proceed-eligibility').click(handleProceedClick);
        $('#kt_modal_1').on('show.bs.modal', populateModal);
        $('#rm-recommend-loan-amount').prop('readonly', true);
        $('#rm-recommended-roi').prop('readonly', true);
    }

    function handleCancelClick() {
        console.log(tempMainRecAmt + "-----tempMainROI" + tempMainROI)
        // Revert the main form fields to the initial values
        $('#rm-recommended-roi').val(tempMainROI);
        $('#rm-recommend-loan-amount').val(tempMainRecAmt);

        // Hide the modal
        $('#kt_modal_1').modal('hide');
    }


    function handleLTVPercentChange() {
        const ltvPercent = parseFloat($(this).val());
        updateLTVAmount(ltvPercent);
    }

    function updateLTVAmount(ltvPercent) {
        if (!isNaN(ltvPercent)) {
            const loanAmount = parseFloat($('#vehicle-eligible-amount').val());
            const ltvAmount = (ltvPercent / 100) * loanAmount;
            $('#ltv-amount').val(ltvAmount.toFixed(2));
        }
    }

    function handleEligibilityDetailsLinkClick(e) {
        showLoader();
        e.preventDefault();
        fetchLoanDetails()
            .then(response => {
                hideLoader();
                $('#eligibilityDetailsContent').collapse('show');
                $('#requested-loan-amount').val(response.requestLoanAmount);
                $('#vehicle-eligible-amount').val(response.vlamount);
                $('#eligible-tenor').val(response.tenor);
                $('#add-ltv-amount').val(response.addltvAmount);
                if (response.ltvmanual) {
                    $('#ltv-percent-group').show();
                    if (response.ltvpercentage) {
                        $('#ltv-percent').val(response.ltvpercentage);
                        $('#ltv-amount').val(response.ltvAmount);
                    }
                    ltvType="custom";
                } else {
                    ltvType="none";
                    $('#ltv-percent-group').hide();
                    $('#ltv-percent').val(response.ltvpercentage);
                    $('#ltv-amount').val(response.ltvAmount);
                }
                if (currentQueue === 'RM' || currentQueue.startsWith('RC')) {
                    checkExistingEligibilityData();
                    storeInitialValues();
                }

            })
    }

    function clearOldValues() {
        $('#loan-amount').val('');
        $('#ltv-amount').val('');
        $('#tenor').val('');
        $('#ltv-percent').val('');
        $('#ltv-percent-group').hide();
        ltvType = "none";
    }

    function bypassCheck() {
        return ['VLR_000000104', 'VLR_000000229'].includes(wiNum);
    }

    function fetchLoanDetails() {
        return ajaxCall('GET', `api/getLoanEligibilityDetails/${wiNum}/${slNo}`);
    }

    function handleEmploymentDetails(details) {
        if (containsCustomLTVEmploymentType(details)) {
            $('#ltv-percent-group').show();
            ltvType = "CUSTOM";
        } else {
            $('#ltv-percent-group').hide();
            ltvType = "none";
        }
    }

    function containsCustomLTVEmploymentType(types) {
        const customTypes = ['PENSIONER', 'AGRICULTURIST', 'NONE'];
        return types.some(type => customTypes.includes(type));
    }

    function ajaxCall(method, url, data = {}) {
        return $.ajax({
            url: url,
            method: method,
            cache: false,
            data: data,
            contentType: 'application/json',
            dataType: 'json'
        }).fail(xhr => {
            var err_data = xhr.responseJSON;
            if (err_data.errorMessage) {
                alertmsgvert(err_data.errorMessage);
            } else {
                throw new Error(xhr.responseJSON.message || "Unknown error occurred");
            }
            if (url.indexOf('getLoanEligibilityDetails') != -1) {
                setTimeout(() => {
                    $('#eligibilityDetailsContent').collapse('hide');
                }, 1000); // Defer the collapse to ensure it happens after any UI updates
            }
            hideLoader();
        });
    }

    function handleAjaxError(xhr) {
        console.error("AJAX Error:", xhr);
        if (xhr.responseJSON) {
            hideLoader();
            var err_data = xhr.responseJSON;
            if (err_data.errorMessage) {
                errormsg(xhr.responseJSON.errorMessage).then(function (confirmed) {
                    $('#eligibilityDetailsContent').collapse('hide');
                });
            } else if (err_data.msg) {
                alertmsgvert(err_data.msg);
            }
        } else if (xhr)
            alertmsg(xhr.message);
        else
            alertmsg("Unable to process your request");
    }

    function checkEligibility() {

        if (checkSaveButtons()) {
            showLoader();
            const tenor = $('#tenor').val();
            const interestType = $('input[name="roiType"]:checked').val() === 'fixed' ? 'FIXED' : 'FLOATING';
            const channelId = 'BRANCH MARKETING';

            const checkEligibilityRequest = {
                wiNum: wiNum,
                slno: slNo,
                tenor: tenor,
                cardRate: $('#card-rate').val(),
                loanAmt: $('#requested-loan-amount').val(),
                ltvAmt: $('#ltv-amount').val(),
                applicantId: $('[data-code="A-1"]').find('[name="appid"]').val(),
                vehicleAmt: $('#vehicle-eligible-amount').val(),
                ltvType: ltvType,
                ltvPer: parseFloat($('#ltv-percent').val())
            };
            if (eligibilityIno) {
                checkEligibilityRequest.ino = eligibilityIno;
            }
            $.ajax({
                type: 'POST',
                url: 'api/checkProgramEligibility',
                data: JSON.stringify(checkEligibilityRequest),
                contentType: 'application/json',
                dataType: 'json',
                //     async: false,
                success: function (response) {
                    hideLoader();
                    eligibilityresp = response.response;
                    if (eligibilityresp.eligibility === "ERROR") {
                        alertmsg(eligibilityresp.ErrorMessage);
                    } else {
                        // button.removeAttribute("data-kt-indicator");
                        // button.removeAttribute("disabled");
                        $('#proceedSection').show();
                        $(".proceed-button-rm").prop("disabled", false);
                        $('#program-eligible-amount').val(eligibilityresp.eligibility);
                        $('#card-rate').val(eligibilityresp.cardRate);
                        $('#eligible-loan-amount').val(eligibilityresp.principal);
                        $('#dealter-loan-amount').val(eligibilityresp.dealerSum);
                        $('#ins-loan-amount').val(eligibilityresp.insSum);
                        $('#emi').val(eligibilityresp.emi);
                        $('#rm-recommend-loan-amount').val(eligibilityresp.principal);
                        $('#rm-recommended-roi').val(eligibilityresp.cardRate);
                        $('#ltv-percentage').val(eligibilityresp.finalLTV);
                        //
                        if (eligibilityresp.loanProgram !== "") {
                            console.log(" before handle icons for "+eligibilityresp.loanProgram);
                            updateFieldsBasedOnProgram(eligibilityresp.loanProgram)
                            console.log("after handle handle icons for "+eligibilityresp.loanProgram);
                        }
                        originalRecAmt = $('#rm-recommend-loan-amount').val();
                        originalROI = $('#rm-recommended-roi').val();

                    }
                },
                error: handleAjaxError
            });
        }
        // button.removeAttribute("data-kt-indicator");
        //                  button.removeAttribute("disabled");
    }
    function modalCheckEligibility() {

        if (checkSaveButtons()) {
            showLoader();
            const tenor = $('#tenor').val();
            const interestType = $('input[name="roiType"]:checked').val() === 'fixed' ? 'FIXED' : 'FLOATING';
            const channelId = 'BRANCH MARKETING';

            const checkEligibilityRequest = {
                wiNum: wiNum,
                slno: slNo,
                tenor: tenor,
                cardRate: $('#rm-recommended-roi').val(),
                loanAmt: $('#requested-loan-amount').val(),
                ltvAmt: $('#ltv-amount').val(),
                applicantId: $('[data-code="A-1"]').find('[name="appid"]').val(),
                vehicleAmt: $('#vehicle-eligible-amount').val(),
                ltvType: ltvType,
                ltvPer: parseFloat($('#ltv-percent').val())
            };
            if (eligibilityIno) {
                checkEligibilityRequest.ino = eligibilityIno;
            }
            $.ajax({
                type: 'POST',
                url: 'api/checkProgramEligibility',
                data: JSON.stringify(checkEligibilityRequest),
                contentType: 'application/json',
                dataType: 'json',
                //     async: false,
                success: function (response) {
                    hideLoader();
                    eligibilityresp = response.response;
                    if (eligibilityresp.eligibility === "ERROR") {
                        alertmsg(eligibilityresp.ErrorMessage);
                    } else {
                        console.log("========"+eligibilityresp.eligibility);
                        $('#modal-program-eligibility').val(eligibilityresp.eligibility);
                        $('#modal-recommended-amount').val(eligibilityresp.principal);
                    }
                },
                error: handleAjaxError
            });
        }
        // button.removeAttribute("data-kt-indicator");
        //                  button.removeAttribute("disabled");
    }

    function updateFieldsBasedOnProgram(eligloanProgram) {
        if (eligloanProgram === "INCOME" || eligloanProgram === "SURROGATE") {
            $('#rm-recommend-loan-amount').prop('readonly', true);
            $('#rm-recommended-roi').prop('readonly', true);
            $('#edit-amount-icon').hide();
            $('#edit-roi-icon').show();
        } else if (eligloanProgram === "60/40" || eligloanProgram === "LOANFD") {
            $('#rm-recommend-loan-amount').prop('readonly', true);
            $('#rm-recommended-roi').prop('readonly', true);
            $('#edit-amount-icon').show();
            $('#edit-roi-icon').hide();
        }
    }

    function handleEditAmount() {
        var loanProgram = eligibilityresp.loanProgram;
        if (loanProgram === "60/40" || loanProgram === "LOANFD") {
            $('#rm-recommend-loan-amount').prop('readonly', false).focus();
        }
    }

    function handleEditROI() {
        const loanProgram = eligibilityresp.loanProgram;
        if (loanProgram === "INCOME" || loanProgram === "SURROGATE") {
            $('#rm-recommended-roi').prop('readonly', false).focus();
        }
    }


    function proceedWithEligibility() {
        return new Promise((resolve, reject) => {
            const data = {
                wiNum: $('#winum').val(),
                slno: $('#slno').val(),
                applicantId: $('[data-code="A-1"]').find('[name="appid"]').val(),
                vehicleAmt: $('#vehicle-amount').val(),
                ltvAmt: $('#ltv-amount').val(),
                loanAmt: $('#requested-loan-amount').val()
            };
            if (eligibilityIno) {
                data.ino = eligibilityIno;
            }
            ajaxCall('POST', 'api/saveEligibilityDetails', JSON.stringify(data))
                .then(response => {
                    if (response.ino) {
                        $('#ino').val(response.ino);
                        let state = $('#bottomCard').data('state');
                        state += 1;
                        $('#bottomCard').data('state', state).attr('state', state);
                        resolve();
                    } else {
                        reject();
                    }
                })
                .catch(reject);
        });
    }


    function getEligibleLoanAmount() {
        let a = parseFloat($('#ltv-amount').val());
        let b = parseFloat($('#requested-loan-amount').val());
        let c = parseFloat($('#program-eligible-amount').val());
        return Math.min(a, b, c);
    }

    function toggleSection(sectionToShow, ...sectionsToHide) {
        $(sectionToShow).collapse('show');
        sectionsToHide.forEach(section => $(section).collapse('hide'));
    }

    function populateEligibilityFields(response = {}) {
        if (response) {
            $('#requested-loan-amount').val(response.requestedLoanAmount);
            $('#ltv-amount').val(response.ltvAmount);
            $('#vehicle-eligible-amount').val(response.vehicleAmount);
            $('#eligible-tenor').val(response.tenor);
            if (response.ltvType === "CUSTOM") {
                ltvType = "CUSTOM";
                $('#ltv-percent-group').show();
            }
            $('#ltv-percent').val(response.ltvPercent);
        } else {
            const onroadPrice = $('#onroad_price').val();
            const loanAmount = $('#loan-amount').val();
            const eligibleTenor = $('#tenor').val();
            $('#vehicle-amount').val(onroadPrice);
            $('#requested-loan-amount').val(loanAmount);
            $('#eligible-tenor').val(eligibleTenor);
        }
    }

    function populateFromLoanEligibilityDetails(response) {
        program = response.loanProgram;
        specificEmploymentType = response.employmentType;  // Assuming employmentDetails is an array
        annualIncome = response.annualIncomeAndBankBalance.annualIncome;
        bankBalance = response.annualIncomeAndBankBalance.bankBalance;
        creditScore = response.creditScore;
        if (response.creditComplete || bypass) {
            handleEmploymentDetails(specificEmploymentType);
            if ($('#onroad_price').val() < $('#loan-amount').val()) {
                alertmsg("Loan amount must be less than Vehicle Amount");
                $('#bottomCard').data('state', 2).attr('state', 2);
                disableAccordion();
            } else {
                if ($('#collapseDisable').val() === '1') {
                    $('#eligibilityDetailsContent').collapse('toggle');
                } else {
                    toggleSection('#eligibilityDetailsContent', '#vehDetailsContent', '#loanDetailsContent');
                }
                populateEligibilityFields(response);
                populateVehicleAmount();

                if (!bypass) allowEligibility = checkSaveButtons();
                if (!allowEligibility) {
                    $('#eligibilityDetailsContent').collapse('toggle');
                }
                if ($('#collapseDisable').val() === '1') {
                    setTimeout(checkEligibility, 150);
                }
                $('#proceedSection').hide();
            }
        } else {
            alertmsg("Please complete the credit check.");
            $('#bottomCard').data('state', 2).attr('state', 2);
            disableAccordion();
        }
    }


    function submitForm() {
        if (checkSaveButtons()) {
            const slno = $("#slno").val();
            if (slno !== "" && !isNaN(slno)) {
                if (currentQueue == "RM") {
                    checkEligibilityFlag().then(response => {
                        if (response.eligibilityFlag === 'Y') {
                            proceedWithEligibility();
                        } else {
                            console.error('Please run the eligibility check before proceeding.:', response.eligibilityFlag);
                        }
                    }).catch(error => {
                        console.error('Error checking eligibility flag:', error);
                        alertmsg("An error occurred while checking eligibility. Please try again.");
                    });
                } else {
                    confirmmsg("Do you want to proceed to upload ?").then(function (result) {
                        if (result) {
                            proceedWithEligibility()
                                .then(() => {
                                    const form = $('<form>', {
                                        action: 'bmdocupload',
                                        method: 'post'
                                    }).append($('<input>', {
                                        type: 'hidden',
                                        name: 'slno',
                                        value: slno
                                    }));
                                    $('body').append(form);
                                    form.submit();
                                    $('#bmdocupload').submit();
                                })
                                .catch((xhr) => {
                                    console.log(xhr)
                                    var err_data = xhr.responseJSON;
                                    if (err_data.errorMessage) {
                                        alertmsgvert(err_data.errorMessage);
                                    } else {
                                        alertmsg("Unable to Proceed!!");
                                    }
                                });
                        }
                    });
                }
            } else {
                alertmsg("Error: SL No must be a valid number");
            }
        }
    }


    function populateVehicleAmount() {
        const onroadPrice = $('#total-invoice-price').val(); // Assuming onroad_price is available in the variant details table
        $('#vehicle-eligible-amount').val(onroadPrice);
    }

    function populateModal() {
        modalCheckEligibility();
        var loanProgram = eligibilityresp.loanProgram;
        if (tempMainROI == "") {
            tempMainROI = $('#rm-recommended-roi').val();
        }
        if (tempMainRecAmt == "") {
            tempMainRecAmt = $('#rm-recommend-loan-amount').val();
        }


        $('#modal-ltv-amount').val($('#ltv-amount').val());
        $('#add-modal-ltv-amount').val($('#add-ltv-amount').val());
        $('#modal-requested-loan-amount').val($('#requested-loan-amount').val());
        //$('#modal-program-eligibility').val($('#program-eligible-amount').val());
        $('#modal-recommended-roi').val($('#rm-recommended-roi').val());
        $('#modal-recommended-roi').prop('readonly', true);
        $('#modal-recommended-amount').val($('#rm-recommend-loan-amount').val());
        $('#modal-card-rate').val($('#card-rate').val());
        if (loanProgram === "60/40" || loanProgram === "LOANFD") {
            $('#roimodalrow').hide();
            $('#amtmodalrow').removeClass("col-md-6");
            $('#amtmodalrow').addClass("col-md-12");
        }
        $('#modal-emi').val('');
    }

    function storeInitialValues() {
        initialValues = {
            vehicleEligibleAmount: $('#vehicle-eligible-amount').val(),
            requestedLoanAmount: $('#requested-loan-amount').val(),
            ltvAmount: $('#ltv-amount').val(),
            ltvPercent: $('#ltv-percent').val(),
            eligibleTenor: $('#eligible-tenor').val(),
            programEligibleAmount: $('#program-eligible-amount').val(),
            cardRate: $('#card-rate').val(),
            eligibleLoanAmount: $('#eligible-loan-amount').val(),
            emi: $('#emi').val(),
            rmRecommendLoanAmount: $('#rm-recommend-loan-amount').val(),
            rmRecommendedRoi: $('#rm-recommended-roi').val()
        };


    }

    function handleFetchEMIClick() {
        return new Promise((resolve, reject) => {
            const loanProgram = eligibilityresp.loanProgram;
            const recommendedAmount = parseFloat($('#modal-recommended-amount').val());
            const ltvAmount = parseFloat($('#modal-ltv-amount').val());
            const addltvAmount = parseFloat($('#add-modal-ltv-amount').val());
            const programEligibility = parseFloat($('#modal-program-eligibility').val());
            const requestedAmount = parseFloat($('#modal-requested-loan-amount').val());
            const recommendedROI = parseFloat($('#modal-recommended-roi').val());


            // Add the checks here
            if (recommendedAmount > Math.min(ltvAmount+(1*addltvAmount), programEligibility, requestedAmount)) {
                alertmsg("Recommended amount should be less than the minimum of LTV, program eligibility, and requested amount.");
                reject("Invalid recommended amount");
                return;
            }
            if (recommendedROI <= 0) {
                alertmsg("Recommended ROI must be greater than 0.");
                reject("Invalid ROI");
                return;
            }

            let cardRate;
            if (loanProgram === "INCOME" || loanProgram === "SURROGATE") {
                cardRate = recommendedROI;
            } else {
                cardRate = parseFloat($('#card-rate').val());
            }
            if (recommendedROI > cardRate) {
                alertmsg("Recommended ROI cannot exceed the card rate.");
                reject("Invalid ROI");
                return;
            }


            const checkEligibilityRequest = {
                wiNum: wiNum,
                slno: slNo,
                tenor: $('#eligible-tenor').val(),
                cardRate: cardRate,
                loanAmt: recommendedAmount,
                ltvAmt: ltvAmount,
                applicantId: $('[data-code="A-1"]').find('[name="appid"]').val(),
                vehicleAmt: $('#vehicle-eligible-amount').val(),
                ltvType: ltvType,
                ltvPer: parseFloat($('#ltv-percent').val())
            };

            $.ajax({
                type: 'POST',
                url: 'api/checkProgramEligibility',
                data: JSON.stringify(checkEligibilityRequest),
                contentType: 'application/json',
                dataType: 'json',
                success: function (response) {
                    if (response.response.eligibility === "ERROR") {
                        alertmsg(response.response.ErrorMessage);
                        reject(response.response.ErrorMessage);
                    } else {
                        $('#modal-emi').val(response.response.emi);
                        $('#modal-dealAmt').val(response.response.dealerSum);
                        $('#modal-insAmt').val(response.response.insSum);
                        // updateReferenceValues();
                        resolve();
                    }
                },
                error: function (xhr, status, error) {
                    handleAjaxError(xhr);
                    reject(error);
                }
            });
        });
    }


    function handleConfirmClick() {
        const loanProgram = eligibilityresp.loanProgram;
        const recommendedAmount = parseFloat($('#modal-recommended-amount').val());
        const ltvAmount = parseFloat($('#modal-ltv-amount').val());
        const addltvAmount = parseFloat($('#add-modal-ltv-amount').val());
        const programEligibility = parseFloat($('#modal-program-eligibility').val());
        const requestedAmount = parseFloat($('#modal-requested-loan-amount').val());
        const recommendedROI = parseFloat($('#modal-recommended-roi').val());
        const cardRate = parseFloat($('#modal-card-rate').val());
        tempMainROI = recommendedROI;
        tempMainRecAmt = requestedAmount;

        if (recommendedAmount > Math.min(ltvAmount+(1*addltvAmount), programEligibility, requestedAmount)) {
            alertmsg("Recommended amount should be less than the minimum of LTV, program eligibility, and requested amount.");
            return;
        }
        if (recommendedROI <= 0) {
            alertmsg("Recommended ROI must be greater than 0.");
            return;
        }
        if (recommendedROI > cardRate) {
            alertmsg("Recommended ROI cannot exceed the card rate.");
            return;
        }

        // Check if #fetch-emi-button was clicked
        if ($('#modal-emi').val() === '') {
            alertmsg("Please fetch the EMI before confirming.");
            return;
        }

        // Check if ROI or amount has changed
        console.log("recommendedROI---:" + recommendedROI);
        console.log("originalRecAmt----:" + originalRecAmt);
        console.log("cardRate----:" + cardRate);
        console.log("recommendedAmount----:" + recommendedAmount);
        console.log("originalROI----:" + originalROI);
        if (recommendedROI !== originalROI || recommendedAmount !== originalRecAmt) {
            confirmmsg("The ROI or recommended amount has changed. Do you confirm the recalculated the EMI?")
                .then(function (result) {
                    if (result) {
                        handleFetchEMIClick().then(() => {
                            // Update reference values after recalculation
                            updateReferenceValues();
                            updateMainForm();
                        }).catch((error) => {
                            alertmsg("Error recalculating EMI: " + error);
                        });
                    } else {
                        // Reset values
                        $('#modal-recommended-roi').val(cardRate);
                        $('#modal-recommended-amount').val(originalRecAmt);
                        $('#modal-emi').val('');
                        $('#modal-dealAmt').val('');
                        $('#modal-insAmt').val('');
                        alertmsg("Values have been reset. Please fetch the EMI again.");
                    }
                });
        } else {
            updateMainForm();
        }
    }

    function updateReferenceValues() {
        originalROI = parseFloat($('#modal-recommended-roi').val());
        originalRecAmt = parseFloat($('#modal-recommended-amount').val());
    }


    function handleReset() {
        confirmmsg("Are you sure you want to reset all values to their initial state?")
            .then(function (result) {
                if (result) {
                    // Reset all fields to their initial values
                  //  $('#vehicle-eligible-amount').val(initialValues.vehicleEligibleAmount);
                    //$('#requested-loan-amount').val(initialValues.requestedLoanAmount);
                    //$('#ltv-amount').val(initialValues.ltvAmount);
                    //$('#ltv-percent').val(initialValues.ltvPercent);
                    //$('#eligible-tenor').val(initialValues.eligibleTenor);
                    $('#program-eligible-amount').val(initialValues.programEligibleAmount);
                    $('#card-rate').val(initialValues.cardRate);
                    $('#eligible-loan-amount').val(initialValues.eligibleLoanAmount);
                    $('#emi').val(initialValues.emi);
                    $('#rm-recommend-loan-amount').val(initialValues.rmRecommendLoanAmount);
                    $('#rm-recommended-roi').val(initialValues.rmRecommendedRoi);

                    // Reset global variables
                    originalROI = initialValues.rmRecommendedRoi;
                    originalRecAmt = initialValues.rmRecommendLoanAmount;
                    tempMainROI = "";
                    tempMainRecAmt = "";

                    // Reset eligibilityresp if needed
                    // You may need to adjust this part based on your specific requirements
                    eligibilityresp = null;

                    // Hide sections that should be hidden initially
                    $('#proceedSection').hide();

                    // Reset readonly and editable states
                    $('#rm-recommend-loan-amount').prop('readonly', true);
                    $('#rm-recommended-roi').prop('readonly', true);
                    $('#edit-amount-icon').hide();
                    $('#edit-roi-icon').hide();

                    alertmsg("All values have been reset to their initial state.");
                }
            });
    }


    function updateMainForm() {
        $('#program-eligible-amount').val($('#modal-program-eligibility').val());
        $('#eligible-loan-amount').val($('#modal-recommended-amount').val());
        $('#emi').val($('#modal-emi').val());
        $('#dealter-loan-amount').val($('#modal-dealAmt').val());
        $('#ins-loan-amount').val($('#modal-insAmt').val());
        $('#rm-recommended-roi').val(originalROI);
        $('#rm-recommend-loan-amount').val(originalRecAmt);
        updateFieldsBasedOnProgram(eligibilityresp.loanProgram);
        //originalROI = $('#rm-recommended-roi').val();
        //originalRecAmt = $('#rm-recommend-loan-amount').val();
        $('#kt_modal_1').modal('hide');
        alertmsg("Values updated. Click 'Proceed' to save and continue.");
    }


    function handleProceedClick() {
        checkEligibilityFlag().then(response => {
            if (response.eligibilityFlag === 'Y') {
                const loanProgram = eligibilityresp.loanProgram;
                const recommendedAmount = parseFloat($('#rm-recommend-loan-amount').val());
                const recommendedROI = parseFloat($('#rm-recommended-roi').val());
                const emi = parseFloat($('#emi').val());


                if (isNaN(recommendedAmount) || isNaN(recommendedROI) || isNaN(emi)) {
                    alertmsg("Please ensure all values are filled correctly.");
                    return;
                }

                if (recommendedROI <= 0) {
                    alertmsg("Recommended ROI must be greater than 0.");
                    return;
                }

                if (loanProgram === "INCOME" || loanProgram === "SURROGATE") {
                    console.log("recommendedROI=" + recommendedROI + "----originalROI=" + originalROI);
                    if (recommendedROI !== parseFloat(originalROI)) {
                        confirmmsg("The ROI has been changed. Do you want to calculate the new EMI based on this ROI?")
                            .then(function (result) {
                                if (result) {
                                    alertmsg("Please click the Calculate button to update the EMI.");
                                } else {
                                    $('#rm-recommended-roi').val(originalROI);
                                    alertmsg("ROI has been reset to the original value");
                                }
                            });
                        return;
                    }
                }

                if (loanProgram === "60/40" || loanProgram === "LOANFD") {
                    console.log("recommendedAmount=" + recommendedAmount + "----originalRecAmt=" + originalRecAmt);
                    if (recommendedAmount !== parseFloat(originalRecAmt)) {
                        confirmmsg("The Recommended Amount has been changed. Do you want to calculate the new EMI based on this ROI?")
                            .then(function (result) {
                                if (result) {
                                    alertmsg("Please click the Calculate button to update the EMI.");
                                } else {
                                    $('#rm-recommended-roi').val(originalROI);
                                    alertmsg("ROI has been reset to the original value");
                                }
                            });
                        return;
                    }
                }

                // Prepare data to be sent to the server
                const dataToSave = {
                    slno: slNo,
                    wiNum: wiNum,
                    loanAmountRecommendedCPC: recommendedAmount,
                    roiRecommendedCPC: recommendedROI,
                    emi: emi,
                    vehicleAmt: $('#vehicle-amount').val(),
                    ltvAmt: $('#ltv-amount').val(),
                    loanAmt: $('#requested-loan-amount').val(),
                    ltvPer: parseFloat($('#ltv-percent').val())
                };

                // AJAX call to save data
                $.ajax({
                    type: 'POST',
                    url: 'api/saveEligibilityRecommendation',
                    data: JSON.stringify(dataToSave),
                    contentType: 'application/json',
                    dataType: 'json',
                    success: function (response) {
                        if (response.status === "SUCCESS") {
                            alertmsg("Recommendation saved successfully.");
                        } else {
                            alertmsg("Error saving recommendation: " + response.message);
                            updateAccordionStyle('eligibilityDetailsContent', true);
                        }
                    },
                    error: function (xhr, status, error) {
                        if (xhr.responseJSON && xhr.responseJSON.msg) {
                            alertmsg(xhr.responseJSON.msg);
                        } else {
                            alertmsg("Error saving recommendation: " + error);
                        }

                    }
                });
            } else {
                $('#proceedSection').hide();
                alertmsg("Please run the eligibility check before proceeding.");
            }

        }).catch(error => {
        console.error('Error checking eligibility flag:', error);
        alertmsg("An error occurred while checking eligibility. Please try again.");
    });
    }
    function checkEligibilityFlag() {
    return $.ajax({
        url: 'api/checkEligibilityFlag',
        method: 'POST',
        data: JSON.stringify({ slno: slNo }),
        contentType: 'application/json',
        dataType: 'json'
    });
}


    function populateEligibilityFieldsCPC(data) {
        $('#rm-recommend-loan-amount').val(data.loanAmountRecommendedCPC);
        $('#rm-recommended-roi').val(data.roiRecommendedCPC);
        $('#emi').val(data.emi);
        $('#program-eligible-amount').val(data.programEligibleAmt);
        $('#eligible-loan-amount').val(data.eligibleLoanAmt);
        $('#card-rate').val(data.cardRate);
    }

    function updateAccordionStyle(accordionId, isCompleted) {
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

    function checkExistingEligibilityData() {
        var target = document.querySelector("#eligibilityDetailsContent");
        // var blockUI = new KTBlockUI(target);
        // blockUI.block();
        const tenor = $('#tenor').val();
        const interestType = $('input[name="roiType"]:checked').val() === 'fixed' ? 'FIXED' : 'FLOATING';
        const channelId = 'BRANCH MARKETING';

        const checkEligibilityRequest = {
            wiNum: wiNum,
            slno: slNo,
            tenor: tenor,
            cardRate: $('#card-rate').val(),
            loanAmt: $('#requested-loan-amount').val(),
            ltvAmt: $('#ltv-amount').val(),
            applicantId: $('[data-code="A-1"]').find('[name="appid"]').val(),
            vehicleAmt: $('#vehicle-eligible-amount').val(),
            ltvType: ltvType,
            ltvPer: parseFloat($('#ltv-percent').val())
        };
        if (eligibilityIno) {
            checkEligibilityRequest.ino = eligibilityIno;
        }
        $.ajax({
            type: 'POST',
            url: 'api/checkProgramEligibility',
            data: JSON.stringify(checkEligibilityRequest),
            contentType: 'application/json',
            dataType: 'json',
            async: false,
            success: function (response) {
                eligibilityresp = response.response;
                if (eligibilityresp.eligibility === "ERROR") {
                    alertmsg(eligibilityresp.ErrorMessage);
                } else {
                    //      $('#proceedSection').show();
                    $('#program-eligible-amount').val(eligibilityresp.eligibility);
                    $('#card-rate').val(eligibilityresp.cardRate);
                    $('#eligible-loan-amount').val(eligibilityresp.principal);
                    $('#emi').val(eligibilityresp.emi);
                    $('#rm-recommend-loan-amount').val(eligibilityresp.recCPCAmt);
                    $('#rm-recommended-roi').val(eligibilityresp.recCPCROI);
                    $('#dealter-loan-amount').val(eligibilityresp.dealerSum);
                    $('#ins-loan-amount').val(eligibilityresp.insSum);
                    originalROI = eligibilityresp.recCPCROI;
                    originalRecAmt = eligibilityresp.recCPCAmt;
                    tempMainROI = originalROI;
                    tempMainRecAmt = originalRecAmt;
                    console.log(tempMainRecAmt + "-----tempMainROI" + tempMainROI)
                    if (eligibilityresp.loanProgram !== "") {
                        updateFieldsBasedOnProgram(eligibilityresp.loanProgram)
                    }


                }
                // if (blockUI.isBlocked()) {
                //     blockUI.release();
                // }
            },
            error: handleAjaxError
        });
        // if (blockUI.isBlocked()) {
        //     blockUI.release();
        // }


    }

});
