let page_mode;
let loanbody;
let savedProgram;
let savedmonthlyorabb;


const wiNum = $('#winum').val();
$(document).ready(function () {

    const applicantId = $('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val();



    let fromDatepicker;
        let toDatepicker;

    const body = document.body;
    page_mode = body.getAttribute('data-page-mode');
    loanbody = $('#loanbody');
    const generaldetailsEle = loanbody.closest('.det').parent().closest('.tab-pane').find('.generaldetails');
    const appType = generaldetailsEle.find('.appType').val();
            function initDatepicker(formElement, isFromDate) {
            const element = formElement;
            const currentDate = new Date();
            const currentYear = currentDate.getFullYear();

            return new Datepicker(element, {
                container: '.content-inner',
                buttonClass: 'btn',
                format: 'yyyy-mm',
                pickLevel: 1, // Month picker
                maxDate: new Date(currentYear, currentDate.getMonth(), 31),
                minDate: new Date(currentYear - 10, 0, 1),  // Allow dates from 10 years ago
                autohide: true, // Hide datepicker after selection
                onSelect: function (dateString) {
                    const formattedDate = moment(dateString).format('YYYY-MM');
                    element.value = formattedDate;

                    if (isFromDate) {
                        // Update minDate of toDatepicker
                        if (toDatepicker) {
                            toDatepicker.setOptions({minDate: new Date(formattedDate)});
                        }
                    } else {
                        // Update maxDate of fromDatepicker
                        if (fromDatepicker) {
                            fromDatepicker.setOptions({maxDate: new Date(formattedDate)});
                        }
                    }
                }
            });
        }

        function updateTotalBalance($fdResponse) {
        let totalBalance = 0;
        let fdcount = 0;
        $fdResponse.find('tbody tr').each(function () {
            fdcount = fdcount + 1;
            let availableBalance = parseFloat($(this).find('td').eq(8).text());
            let eligibleTxt = $(this).find('td').eq(9).text();
            if (!isNaN(availableBalance) && eligibleTxt === "Yes") {
                totalBalance += availableBalance;
            }
        });
        // Update the total available balance field in the current section
        $fdResponse.closest('.fdDetailsDiv').find('.totalavailBalance').val(totalBalance);
    }

    $('.fdResponse').each(function () {
        updateTotalBalance($(this));
    });
    $('#loanbody').on('click', '.delete-fd-btn', function () {
        var ino = $(this).data('ino');
        var row = $(this).closest('tr');
        let $fdResponse = $(this).closest('.fdResponse');
        showLoader();


        $.ajax({
            url: 'api/deleteFDAccount',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ino: ino}),
            success: function (response) {

                row.remove();
                updateTotalBalance($fdResponse);
                hideLoader();
                refreshFDDetailsForAllApplicants();
            },
            error: function (xhr, status, error) {
                console.error('Error deleting FD account:', error);
                alertmsg("Error deleting FD account" + xhr.responseText);
            }
        });
    });


    //alert(appType);

    function fetchKycDetails(detElement) {
        console.log("started applicat details fetching");
        const winum = $('#winum').val();

        const generaldetailsElement = detElement.closest('.det').parent().closest('.tab-pane').find('.generaldetails');

        const appid = generaldetailsElement.find('.appid').val();
        const apptype = generaldetailsElement.find('.appType').val();

        const slno = $('#slno').val();
        console.log("fetch PAN kyc details for - slno" + slno + "--winum" + winum + "--appid" + appid);
        $.ajax({
            url: `api/applicant-details`,
            method: 'GET',
            data: {wiNum: winum, applicantId: appid, slno: slno},
            dataType: 'json',
            success: function (data) {
                const applicantType = data.applicantType;
                if (applicantType === "G") {
                    var guarntorIncheck = detElement.closest('.det').find('.incomeCheck:checked').val();
                    detElement.closest('.det').find('input[name^="incomeCheck"][value="N"]').prop('checked', true);
                    detElement.closest('.det').find('input[name^="incomeCheck"][value="Y"]').prop('disabled', true);
                    detElement.closest('.det').find('.incomeCheck').prop('readonly', true);
                    detElement.closest('.col-lg-7').parent().next('.row').hide();
                    detElement.closest('.det').find('.programCode').hide();
                    detElement.closest('.det').find('.program').hide();
                    detElement.closest('.det').find('.incomediv').hide();
                    detElement.closest('.det').find('.fddiv').hide();
                    detElement.closest('.det').find('.surrogatediv').hide();
                    detElement.closest('.det').find('._70_30div').hide();
                }
                //panNo = "SAMPLEITR1";
                //detElement.closest('.det').find('input[name^="incomePAN"]').val(data.panNo || '');
                // detElement.closest('.det').find('input[name^="incomePAN"]').val('CPYPK5063G');
                // detElement.closest('.det').find('input[name^="incomeDOB"]').val(data.panDob || '');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error('Error fetching KYC details:', textStatus, errorThrown);
                alertmsg('Failed to fetch KYC details. Please ensure KYC Details and Basic Details are complete.');
            }
        });
    }

    $('#loanbody').on('focus', 'input.fromDate', function (e) {
        if (!fromDatepicker) {
            const fromdateBasicElement = this.closest('.surrogatediv')
            const fromdateElement = fromdateBasicElement.querySelector('input.fromDate');
            fromDatepicker = initDatepicker(fromdateElement, true);
        }
        fromDatepicker.show();
    });
    $('#loanbody').on('focus', 'input.toDate', function (e) {
        if (!toDatepicker) {
            const todateBasicElement = this.closest('.surrogatediv');
            const todateElement = todateBasicElement.querySelector('input.toDate');
            toDatepicker = initDatepicker(todateElement, false);
        }
        toDatepicker.show();
    });

    $('#loanbody').on('change', '.incomeCheck', function (e) {
        e.preventDefault();
        e.stopPropagation();
        const kycdetailsElement = $(this).closest('.det').parent().closest('.tab-pane').find('.kycdetails');
        const basicdetailsElement = $(this).closest('.det').parent().closest('.tab-pane').find('.basicdetails');
        const generaldetailsElement = $(this).closest('.det').parent().closest('.tab-pane').find('.generaldetails');
        var entryPan = kycdetailsElement.find('.pan').val();
        var entryPanDob = kycdetailsElement.find('.pandob').val();
        var residentialStatus = generaldetailsElement.find('.residentialStatus:checked').val();
        const basic_mob = basicdetailsElement.find('.basic_mob').val();
        const basic_mobcode = basicdetailsElement.find('.basic_mobcode').val();
        let cust_mob = basic_mobcode + basic_mob;
        $(this).closest('.det').find('input[name^="residentialStatus"]').val(residentialStatus);
        $(this).closest('.det').find('input[name^="incomePAN"]').val(entryPan);
        $(this).closest('.det').find('input[name^="incomeDOB"]').val(entryPanDob);
        $(this).closest('.det').find('input[name^="incomeMOB"]').val(cust_mob);

        var selected = $(this).val();
        var programCodeparent = $(this).closest('.col-lg-7').parent().next('.row');
        var programCode = programCodeparent.find('.programCode').val();
        var programBtn = $(this).closest('.det').find('.save-button-program');
        fetchKycDetails($(this));
        //programCode.val('');
        if (selected === 'Y') {
             resetProgramFields($(this));
            programCodeparent.show();
            if (programCode === 'INCOME') {
                $(this).closest('.det').find('.incomediv').show();
                programBtn.prop("disabled", true);
            } else if (programCode === 'SURROGATE') {
                $(this).closest('.det').find('.surrogatediv').show();
                programBtn.prop("disabled", true);
            } else if (programCode === '70/30') {
                $(this).closest('.det').find('._70_30div').show();
                programBtn.prop("disabled", false);
            } else if (programCode === 'LOANFD') {
                refreshFDDetailsForAllApplicants($(this));
                $(this).closest('.det').find('.fddiv').show();
                programBtn.prop("disabled", true);
            }

        } else {
            resetProgramFields($(this));
            programCodeparent.hide();
            programBtn.prop("disabled", false);
            $(this).closest('.det').find('.program').hide();
            $(this).closest('.det').find('.incomediv').hide();
            $(this).closest('.det').find('.fddiv').hide();
            $(this).closest('.det').find('.surrogatediv').hide();
            $(this).closest('.det').find('._70_30div').hide();
        }
    });

    $('#loanbody').on('change', '.programCode', function (e) {
        e.preventDefault();
        e.stopPropagation();
        var selected = $(this).val();
        var detElement = $(this);
        const kycdetailsElement = detElement.closest('.det').parent().closest('.tab-pane').find('.kycdetails');
        const basicdetailsElement = detElement.closest('.det').parent().closest('.tab-pane').find('.basicdetails');
        const generaldetailsElement = detElement.closest('.det').parent().closest('.tab-pane').find('.generaldetails');
        var entryPan = kycdetailsElement.find('.pan').val();
        var entryPanDob = kycdetailsElement.find('.pandob').val();
        var residentialStatus = generaldetailsElement.find('.residentialStatus:checked').val();
        const basic_mob = basicdetailsElement.find('.basic_mob').val();
        const basic_mobcode = basicdetailsElement.find('.basic_mobcode').val();
        let cust_mob = basic_mobcode + basic_mob;
        $(this).closest('.det').find('input[name^="residentialStatus"]').val(residentialStatus);
        $(this).closest('.det').find('input[name^="incomePAN"]').val(entryPan);
        $(this).closest('.det').find('input[name^="incomeDOB"]').val(entryPanDob);
        $(this).closest('.det').find('input[name^="incomeMOB"]').val(cust_mob);
        if(selected.length>0) {
        fetchProgramDetails(detElement);
        }
        if (selected === 'INCOME') {
            $(this).closest('.det').find('.incomediv').show();
            if (residentialStatus === 'R') {
                $(this).closest('.det').find('.residentstatus').show();
                $(this).closest('.det').find('.nristatus').hide();
            } else if (residentialStatus === 'N') {
                populateMonths(detElement);
                $(this).closest('.det').find('.residentstatus').hide();
                $(this).closest('.det').find('.nristatus').show();
                // addRowMonthly('.salarytable_body');
            }
            $(this).closest('.det').find('.surrogatediv').hide();
            $(this).closest('.det').find('.fddiv').hide();
            $(this).closest('.det').find('._70_30div').hide();


        } else if (selected === 'SURROGATE') {
            //$('.surrogatediv').show();
            $(this).closest('.det').find('.surrogatediv').show();
            var detElement = $(this);
            checkForBSAEntries(detElement);
            $(this).closest('.det').find('.incomediv').hide();
            $(this).closest('.det').find('.fddiv').hide();
            $(this).closest('.det').find('._70_30div').hide();
        } else if (selected === '70/30') {
            $(this).closest('.det').find(
                '.incomediv').hide();
            $(this).closest('.det').find('.fddiv').hide();
            $(this).closest('.det').find('.surrogatediv').hide();
            $(this).closest('.det').find('._70_30div').show();

        } else if (selected === 'LOANFD') {
            $(this).closest('.det').find('.incomediv').hide();
            $(this).closest('.det').find('.fddiv').show();
            $(this).closest('.det').find('.surrogatediv').hide();
            $(this).closest('.det').find('._70_30div').hide();
        } else {
            $(this).closest('.det').find('.incomediv').hide();
            $(this).closest('.det').find('.fddiv').hide();
            $(this).closest('.det').find('.surrogatediv').hide();
            $(this).closest('.det').find('._70_30div').hide();
        }
    });

    $('#loanbody').on('change', '.itravailable', function (e) {
        e.preventDefault();
        e.stopPropagation();
        var selected = $(this).val();
        var itrparent = $(this).closest('.col-lg-7').parent().next('.row');
        var itrcode = itrparent.find('.getitr');
        itrcode.val('');
        var programBtn = $(this).closest('.det').find('.save-button-program');
        if (selected === 'Y') {
            itrparent.show();
            var detElement = $(this);
            showLoader();
            checkForITREntries(detElement);
            hideLoader();
            $(this).closest('.det').find('.itrbutton').show();
            $(this).closest('.det').find('.salaryupd').hide();
            programBtn.prop("disabled", true);
        } else {
            itrparent.hide();
            $(this).closest('.det').find('.itrbutton').hide();
            $(this).closest('.det').find('.salaryupd').show();
            programBtn.prop("disabled", false);
        }

        //check if ITR Initiates exist for this wiNum and applicantId
    });


    $('#loanbody').on('change', '.form16available', function (e) {
        e.preventDefault();
        e.stopPropagation();
        var selected = $(this).val();
        var form16parent = $(this).closest('.col-lg-7').parent().next('.row');
        var form16code = form16parent.find('.form16_div');
        form16code.val('');
        if (selected === 'Y') {
            $(this).closest('.det').find('.form16_div').show();
        } else {
            $(this).closest('.det').find('.form16_div').hide();
        }

        //check if ITR Initiates exist for this wiNum and applicantId
    });

    $('#loanbody').on('change', '.monthlyorabb', function (e) {
        e.preventDefault();
        //e.stopPropagation();
         console.log("====savedProgram----"+savedProgram+"====savedmonthlyorabb----"+savedmonthlyorabb);
        var selected = $(this).val();
        if (selected === 'MonthSalary') {
            $(this).closest('.det').find('.monthsalary').show();
            populateMonths($(this));
            $(this).closest('.det').find('.abb_div').hide();
            $(this).closest('.det').find('.abb').val("");
            if (savedmonthlyorabb !== "MonthSalary") {
                $(this).closest('.det').find('.MonthSalary').val("");
                $(this).closest('.det').find('.bulk_remittance').val("");
                $(this).closest('.det').find('.net_remittance').val("");
                $(this).closest('.det').find('.total_remittance').val("");
                $(this).closest('.det').find('.Avgtotal_remittance').val("");
                $(this).closest('.det').find('.Avgbulk_remittance').val("");
                $(this).closest('.det').find('.Avgnet_remittance').val("");
            }
        } else {
            $(this).closest('.det').find('.monthsalary').hide();
            $(this).closest('.det').find('.abb_div').show();
             $(this).closest('.det').find('.MonthSalary').val("");
                $(this).closest('.det').find('.bulk_remittance').val("");
                $(this).closest('.det').find('.net_remittance').val("");
                $(this).closest('.det').find('.total_remittance').val("");
                $(this).closest('.det').find('.Avgtotal_remittance').val("");
                $(this).closest('.det').find('.Avgbulk_remittance').val("");
                $(this).closest('.det').find('.Avgnet_remittance').val("");
            if (savedmonthlyorabb !== "ABB") {
                $(this).closest('.det').find('.abb').val("");
            }
        }
    });


    $('#loanbody').on('keydown', '.total_remittance', function (e) {
        if (e.which == 9) { // tab key
            e.preventDefault();
            var nextRow = $(this).closest('tr').next('tr');
            if (nextRow.length > 0) {
                nextRow.find('.total_remittance').focus();
            } else {
                // If it's the last row, move focus to the first row
                $('.total_remittance:first').focus();
            }
        }
    });

    // $('#loanbody').on('click','.edit-button', function(e) {
    //     e.preventDefault();
    //     e.stopPropagation();
    //     enableFormInputs($(this).closest('.det'))
    // });

    $('#loanbody').on('click', '.fetch-itr', function () {
        console.log("fetch itr");
        var itrMode = $(this).data('itrmode');
        handleITRRequest($(this), itrMode);
    });
    $('#loanbody').on('click', '.fd-account-validate', function () {
        console.log("fetch FD Details");
        fetchFDDetails($(this));
    });


    function checkForBSAEntries(detElement) {
        console.log("BSA check started");
        var jsonBody = {
            wiNum: $('#winum').val(),
            applicantId: detElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val()
        };
        $.ajax({
            url: "api/hasBSAEntries",
            type: "POST",
            contentType: 'application/json',
            data: JSON.stringify(jsonBody),
            success: function (hasEntries) {
                if (hasEntries) {
                    console.log("BSA count check---" + hasEntries);
                    $('.check-bsa-status').show();
                    checkLatestCompletedBSATransactionStatus(detElement);
                } else {
                    $('.check-bsa-status').hide();

                }
            }, error: function (xhr, status, error) {
                console.error("Error checking for entries:", status, error);
            }
        });
    }

    function checkForITREntries(detElement) {
        console.log("ITR check started");
        var currentPan = detElement.closest('.det').find('input[name^="incomePAN"]').val();
        var savedPan = detElement.closest('.det').find('input[name^="savedPan"]').val();
        console.log("savedPan==="+savedPan+"==currentPan:"+currentPan);
            var jsonBody = {
                wiNum: $('#winum').val(),
                applicantId: detElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val()
            };
            $.ajax({
                url: "api/hasITREntries",
                type: "POST",
                contentType: 'application/json',
                data: JSON.stringify(jsonBody),
                success: function (hasEntries) {
                    if (hasEntries) {
                        console.log("ITR count check---" + hasEntries);
                        $('.check-itr-status').show();
                        checkLatestCompletedITRTransactionStatus(detElement);
                    } else {
                        $('.check-itr-status').hide();
                    }
                }, error: function (xhr, status, error) {
                    console.error("Error checking for entries:", status, error);
                }
            });

    }


    $('#loanbody').on('click', '.YOUgo-button', function () {
        var triggerElement = $(this);
        var fromDate = triggerElement.closest('.surrogatediv').find('input[name="fromDate"]').val();
        var toDate = triggerElement.closest('.surrogatediv').find('input[name="toDate"]').val();
        var bankName = triggerElement.closest('.surrogatediv').find('select[name="bankName"]').val();
        var surrogateDiv = triggerElement.closest('.surrogatediv');
        var fromDateInput = surrogateDiv.find('input[name="fromDate"]');
        var toDateInput = surrogateDiv.find('input[name="toDate"]');
        var bankNameSelect = surrogateDiv.find('select[name="bankName"]');


        var isValid = true;
        surrogateDiv.find('.error-field').removeClass('error-field');

        // Check each field and highlight if empty
        if (!fromDateInput.val()) {
            fromDateInput.addClass('error-field');
            isValid = false;
        }
        if (!toDateInput.val()) {
            toDateInput.addClass('error-field');
            isValid = false;
        }
        if (!bankNameSelect.val()) {
            bankNameSelect.addClass('error-field');
            isValid = false;
        }

        // If any field is empty, show alert and return
        if (!isValid) {
            alertmsg('Please fill in all required fields: From Date, To Date, and Bank Name.');
            return;
        }
        var fromDateStr = new Date(fromDateInput.val());
        var toDateStr = new Date(toDateInput.val());
        var currentDate = new Date();

        if (fromDateStr.getFullYear() === currentDate.getFullYear() &&
            fromDateStr.getMonth() === currentDate.getMonth() &&
            toDateStr.getFullYear() === currentDate.getFullYear() &&
            toDateStr.getMonth() === currentDate.getMonth()) {
            alertmsg('Invalid Date Range: The "From" and "To" dates cannot both be in the current month. Please select a date range that spans at least two different months.');
            fromDateInput.addClass('error-field');
            toDateInput.addClass('error-field');
            return;
        }
        if (toDateStr < fromDateStr) {
            alertmsg('Invalid Date Range: The "To" date must be after the "From" date. Please adjust your selection.');
            fromDateInput.addClass('error-field');
            toDateInput.addClass('error-field');
            return;
        }


    var monthsBetween = monthDiff(fromDateStr, toDateStr);

        console.log("surrogate-"+monthsBetween);
        if (monthsBetween != 12) {
            alertmsg('Invalid Date Range: The selected date range exceeds 12 months. Please adjust your selection.');
            fromDateInput.addClass('error-field');
            toDateInput.addClass('error-field');
            return;
        }



        var jsonBody = {
            "txnId": $('#winum').val() + "_" + triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
            "institutionId": bankName,
            "yearMonthFrom": fromDate,
            "yearMonthTo": toDate,
            wiNum: $('#winum').val(),
            applicantId: triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
            slno: $('#slno').val()
        }
        $.ajax({
            url: "api/fetchBSA",
            type: "POST",
            contentType: 'application/json',
            data: JSON.stringify(jsonBody),
            success: function (response) {
                console.log("in success response")
                var data = JSON.parse(response);
                if (data.Response && data.Response.Body && data.Response.Body.message && data.Response.Body.message.Error) {
                    var errorMessage = data.Response.Body.message.Error.message;
                    alertmsg('Error: ' + errorMessage);
                    return;
                }

                var url = data.Response.Body.message.Success.url;
                console.log(url);
                var currentTab = triggerElement.closest('.det').parent();
                var modal = currentTab.find('.iframe-modal');
                var modalTitle = modal.find('.modal-title');
                var iframe = modal.find('.itr-iframe');
                var loadingIndicator = modal.find('.loading-indicator');

                modalTitle.text("Bank Statement upload");

                iframe.on('load', function () {
                    loadingIndicator.hide();
                    iframe.show();
                });
                //var url = "https://demos.perfios.com//KuberaVault/insights/gl/d1cf9474-bb39-4918-9cf5-5d6587199e6d";

                iframe.attr('src', url);
                modal.data('triggerElement', triggerElement);
                modal.modal('show');
            },
            error: function (xhr, status, error) {
                var currentTab = triggerElement.closest('.det').parent();
                var modal = currentTab.find('.iframe-modal');
                var iframe = modal.find('.itr-iframe');
                modal.data('triggerElement', triggerElement);
                // var loadingIndicator = modal.find('.loading-indicator');
                // Handle the error scenario
                // Display an error message to the user
                iframe.html("Error fetching ITR details.");
                modal.modal('show');
            }
        });

    });

    //----
    $('#loanbody').on('click', '.go-button', async function () {
        var triggerElement = $(this);

        try {
            showLoader();

            const hasActiveProcess = await checkActiveBSAProcess(triggerElement);
            if (hasActiveProcess) {
                hideLoader();
                const shouldReset = await showConfirmationBSADialog();
                if (!shouldReset) {
                    return;
                }
                showLoader();
                await resetActiveBSAProcess(triggerElement);
                triggerElement.closest('.det').find('.bsaResponse').html('');
                triggerElement.closest('.det').find('input[name^="bsaABB"]').val("");
            }

            const url = await fetchBSA(triggerElement);

            if (url) {
                updateUIAfterBSARequest(url, triggerElement);
            } else {
                showFeedback('Error processing BSA request: No URL returned', 'error');
            }
            hideLoader();
        } catch (error) {
            hideLoader();
            showFeedback('An error occurred while processing your request: ' + error.message, 'error');
        }
    });

    async function fetchBSA(triggerElement) {
        console.log('Trigger element:', triggerElement);

        var surrogateDiv = triggerElement.closest('.surrogatediv');
        if (!surrogateDiv.length) {
            surrogateDiv = triggerElement.closest('.det').find('.surrogatediv');
        }
        console.log('Surrogate div:', surrogateDiv);

        if (!surrogateDiv.length) {
            console.error('Could not find .surrogatediv');
            throw new Error('Could not find surrogate div');
        }

        var fromDateInput = surrogateDiv.find('input[name="fromDate"]');
        var toDateInput = surrogateDiv.find('input[name="toDate"]');
        var bankNameSelect = surrogateDiv.find('select[name="bankName"]');

        console.log('From date input:', fromDateInput);
        console.log('To date input:', toDateInput);
        console.log('Bank name select:', bankNameSelect);

        if (!fromDateInput.length) fromDateInput = surrogateDiv.find('.fromDate');
        if (!toDateInput.length) toDateInput = surrogateDiv.find('.toDate');
        if (!bankNameSelect.length) bankNameSelect = surrogateDiv.find('.bankName');

        console.log('From date input (after alternative):', fromDateInput);
        console.log('To date input (after alternative):', toDateInput);
        console.log('Bank name select (after alternative):', bankNameSelect);

        if (!fromDateInput.length || !toDateInput.length || !bankNameSelect.length) {
            console.error('Could not find all required inputs');
            throw new Error('Could not find all required inputs');
        }

        var isValid = true;
        surrogateDiv.find('.error-field').removeClass('error-field');

        if (!fromDateInput.val()) {
            fromDateInput.addClass('error-field');
            isValid = false;
        }
        if (!toDateInput.val()) {
            toDateInput.addClass('error-field');
            isValid = false;
        }
        if (!bankNameSelect.val()) {
            bankNameSelect.addClass('error-field');
            isValid = false;
        }

        if (!isValid) {
            throw new Error('Please fill in all required fields: From Date, To Date, and Bank Name.');
        }

        var fromDateStr = new Date(fromDateInput.val());
        var toDateStr = new Date(toDateInput.val());
        var currentDate = new Date();

        if (fromDateStr.getFullYear() === currentDate.getFullYear() &&
            fromDateStr.getMonth() === currentDate.getMonth() &&
            toDateStr.getFullYear() === currentDate.getFullYear() &&
            toDateStr.getMonth() === currentDate.getMonth()) {
            throw new Error('Invalid Date Range: The "From" and "To" dates cannot both be in the current month. Please select a date range that spans at least two different months.');
        }

        if (toDateStr < fromDateStr) {
            throw new Error('Invalid Date Range: The "To" date must be after the "From" date. Please adjust your selection.');
        }

        var monthsBetween = monthDiff(fromDateStr, toDateStr);
        if (monthsBetween != 12) {
            throw new Error('Invalid Date Range: The selected date range must be exactly 12 months. Please adjust your selection.');
        }

        var jsonBody = {
            "txnId": $('#winum').val() + "_" + triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
            "institutionId": bankNameSelect.val(),
            "yearMonthFrom": fromDateInput.val(),
            "yearMonthTo": toDateInput.val(),
            wiNum: $('#winum').val(),
            applicantId: triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
            slno: $('#slno').val()
        };

        try {
            const response = await $.ajax({
                url: "api/fetchBSA",
                type: "POST",
                contentType: 'application/json',
                data: JSON.stringify(jsonBody)
            });

            const data = JSON.parse(response);
            if (data.Response && data.Response.Body && data.Response.Body.message && data.Response.Body.message.Error) {
                throw new Error(data.Response.Body.message.Error.message);
            }

            if (data.Response && data.Response.Body && data.Response.Body.message && data.Response.Body.message.Success && data.Response.Body.message.Success.url) {
                return data.Response.Body.message.Success.url;
            } else {
                throw new Error('No URL found in the response');
            }
        } catch (error) {
            console.error('Error fetching BSA:', error);
            throw error;
        }
    }


    async function checkActiveBSAProcess(triggerElement) {
        const generaldetailsElement = triggerElement.closest('.det').parent().closest('.tab-pane').find('.generaldetails');
        const applicantId = generaldetailsElement.find('.appid').val();

        try {
            const response = await $.ajax({
                url: 'api/check-active-bsa',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({applicantId, wiNum})
            });
            return response.hasActiveProcess;
        } catch (error) {
            console.error('Error checking active BSA process:', error);
            throw error;
        }
    }


    async function resetActiveBSAProcess(triggerElement) {
        const generaldetailsElement = triggerElement.closest('.det').parent().closest('.tab-pane').find('.generaldetails');
        const applicantId = generaldetailsElement.find('.appid').val();

        try {
            await $.ajax({
                url: 'api/reset-active-bsa',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({applicantId, wiNum})
            });
        } catch (error) {
            console.error('Error resetting active BSA process:', error);
            throw error;
        }
    }

    function updateUIAfterBSARequest(url, triggerElement) {
        $('.check-bsa-status').show();

        var currentTab = triggerElement.closest('.det').parent();
        var modal = currentTab.find('.iframe-modal');
        var modalTitle = modal.find('.modal-title');
        var iframe = modal.find('.itr-iframe');
        var loadingIndicator = modal.find('.loading-indicator');

        modalTitle.text("Bank Statement upload");

        iframe.on('load', function () {
            loadingIndicator.hide();
            iframe.show();
        });

        iframe.attr('src', url);
        modal.data('triggerElement', triggerElement);
        modal.modal('show');
    }

    //----


    $('.MonthSalary').on('change', function (event) {
        const totalremittance = $(event.target).closest('.nristatus').find('input.total_remittance');
        calculateRemittance(totalremittance[0]);
    });


});

$('#loanbody').on('click', '.upload-itr', function () {
    console.log("fetch itr----" + $(this).data('itrmode'));
    var itrMode = $(this).data('itrmode');
   // fetchITR($(this), itrMode);
    handleITRRequest($(this), itrMode);
});
$('#loanbody').on('click', '.check-itr-status', function () {
    console.log("check-itr-status----");
    checkLatestCompletedITRTransactionStatus($(this));
});
$('#loanbody').on('click', '.check-bsa-status', function () {
    console.log("check-bsa-status----");
    checkLatestCompletedBSATransactionStatus($(this));
});
$('.iframe-modal').on('hidden.bs.modal', function () {
    var modal = $(this);
    var triggerElement = modal.data('triggerElement');
    var modalTitle = modal.find('.modal-title').text();
    closemodal(modalTitle, triggerElement);
});
function monthDiff(d1, d2) {
        var months;
        months = (d2.getFullYear() - d1.getFullYear()) * 12;
        months -= d1.getMonth();
        months += d2.getMonth();
        return months <= 0 ? 0 : months + 1; // Add 1 to include both start and end months
    }
function closemodal(modalTitle, triggerElement) {
    console.log("close called");
    showLoader();
    if (modalTitle === "Bank Statement upload") {
        console.log("BSA Check called");
        checkLatestCompletedBSATransactionStatus(triggerElement);
    } else if (modalTitle === "ITR File Upload") {
        console.log("ITR Check called");
        checkLatestCompletedITRTransactionStatus(triggerElement);
    }
    hideLoader();
}

function safeStringify(obj) {
    const cache = new Set();
    return JSON.stringify(obj, (key, value) => {
        if (typeof value === 'object' && value !== null) {
            if (cache.has(value)) {
                return;
            }
            cache.add(value);
        }
        return value;
    });
}


function addRowIncome(triggerElement) {
    const closetdiv = triggerElement.closest('.salarytable')
    const table = closetdiv.querySelector('.salarytable_body')
    var rowCount = table.rows.length;
    //alert(rowCount);
    if (rowCount <= 13) {
        const details = getIncomeDetails(triggerElement);
        var row = table.insertRow(rowCount);
        var colCount = table.rows[0].cells.length;
        //alert(colCount);

        for (var i = 0; i < colCount; i++) {
            var newcell = row.insertCell(i);
            // newcell.innerHTML = table.rows[0].cells[i].innerHTML;
            if (i == 0) {
                var select = document.createElement('select');
                select.name = 'sal_month' + rowCount + i;
                select.id = 'sal_month' + rowCount + i;
                select.className = 'form-control select sal_month';

                var options = [
                    {value: '', text: 'Select Month'},
                    {value: '1', text: 'January'},
                    {value: '2', text: 'February'},
                    {value: '3', text: 'March'},
                    {value: '4', text: 'April'},
                    {value: '5', text: 'May'},
                    {value: '6', text: 'June'},
                    {value: '7', text: 'July'},
                    {value: '8', text: 'August'},
                    {value: '9', text: 'September'},
                    {value: '10', text: 'October'},
                    {value: '11', text: 'November'},
                    {value: '12', text: 'December'},
                ];
                for (var j = 0; j < options.length; j++) {
                    var option = document.createElement('option');
                    option.value = options[j].value;
                    option.text = options[j].text;

                    select.appendChild(option);
                }

                newcell.appendChild(select);
                                if (details.programCode === 'INCOME' &&
                    details.residentialStatus === 'R' &&
                    details.incomeCheck &&
                    !details.itravailable) {
                    $(select).rules('add', { required: true, messages: { required: "Please select a month" } });
                }
                $('.sal_month').select2({
                    templateResult: formatState,
                    templateSelection: formatState
                });
            } else if (i == 1) {
                var input = document.createElement('input');
                input.type = 'file';
                input.className = 'form-control salaryfile base64file';
                input.name = 'salaryfile' + rowCount;
                input.accept = '.pdf,.jpg,.jpeg,.png';
                input.setAttribute('data-max-size', '2097152');
                newcell.appendChild(input);

                // Add validation to the new file input
                               if (details.programCode === 'INCOME' &&
                    details.residentialStatus === 'R' &&
                    details.incomeCheck &&
                    !details.itravailable) {

                    $(input).rules('add', {
                        required: true,
                        messages: {
                            required: "Please upload a salary slip"
                        }
                    });
                }


            } else if (i == 2) {
                var button = document.createElement('button');
                button.type = 'button';
                button.className = 'btn btn-danger btn-file ibtnDel';
                button.innerHTML = '<i class="ph-archive"></i>';
                button.onclick = function () {
                    deleteRowIncome(this);
                };
                newcell.appendChild(button);
            } else {
                //  var newcell = row.insertCell(i);
                newcell.innerHTML = table.rows[0].cells[i].innerHTML;
                //alert(newcell.childNodes[0].type);
                switch (newcell.childNodes[0].type) {
                    case "text":
                        newcell.childNodes[0].value = "";
                        newcell.childNodes[0].name = newcell.childNodes[0].name
                            + rowCount + i;
                        newcell.childNodes[0].id = newcell.childNodes[0].id + rowCount
                            + i;
                        newcell.childNodes[0].readOnly = false;
                        newcell.childNodes[0].style.backgroundColor = "#FFF";
                        break;
                    case "textarea":
                        newcell.childNodes[0].value = "";
                        newcell.childNodes[0].name = newcell.childNodes[0].name
                            + rowCount + i;
                        newcell.childNodes[0].id = newcell.childNodes[0].id + rowCount
                            + i;
                        newcell.childNodes[0].readOnly = false;
                        newcell.childNodes[0].style.backgroundColor = "#fff";
                        break;
                    // case "select-one":
                    //     newcell.childNodes[0].value = "";
                    //     newcell.childNodes[0].name = newcell.childNodes[0].name
                    //         + rowCount + i;
                    //     newcell.childNodes[0].id = newcell.childNodes[0].id + rowCount
                    //         + i;
                    //     break;
                    case "checkbox":
                        newcell.childNodes[0].checked = false;
                        break;
                }

                $('#sal_month' + rowCount + i).rules('add', {
                    required: true
                });
            }
        }
    } else {
        alertmsg("Maximum number of rows exceeded.");
    }

}

function deleteRowIncome(triggerElement) {
    try {
        const closetdiv = triggerElement.closest('.salarytable')
        const table = closetdiv.querySelector('.salarytable_body')
        var rowCount = table.rows.length;
        // rowCount = rowCount - 1;
        var curRow = triggerElement.parentNode.parentNode.rowIndex;
        curRow = curRow - 1;
        if (rowCount <= 1) {
            alertmsg("Cannot delete all the rows.");
            $('html, body').animate({
                scrollTop: $("#" + triggerElement).offset().top
            }, 2000);
        } else {
            if (confirm("Do you want to delete the row ? ")) {
                table.deleteRow(curRow);
                //$('.gettotalnew').trigger('input');
            }
        }
    } catch (e) {
        //alert(e);
    }
}


function formatState(state) {
    if (!state.id) {
        return state.text;
    }
    var $state = $('<span>' + state.text + '</span>');
    return $state;
}

function fetchITR(triggerElement, itrMode) { //ITR SMS LINK
    var itrPan = triggerElement.closest('.det').find('input[name^="incomePAN"]').val();
    var itrDOB = triggerElement.closest('.det').find('input[name^="incomeDOB"]').val();
    var itrSMSMobileNo = triggerElement.closest('.det').find('input[name^="incomeMOB"]').val();
    var currentTab = triggerElement.closest('.det').parent();


    console.log("in fetch itr" + itrMode);
    if (itrMode === "upload") {
        fetchITRUpload(triggerElement);
    } else {
        //itrPan="SAMPLEITR1";
        var jsonBody = {
            itrPan: itrPan,
            itrDOB: itrDOB,
            itrSMSMobileNo: itrSMSMobileNo,
            itrMode: "sms",
            wiNum: $('#winum').val(),
            applicantId: triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
            slno: $('#slno').val()

        };
        // Make an AJAX request to fetch ITR details
        return new Promise((resolve,reject)=>
        {
            $.ajax({
                url: "api/fetchITR",
                type: "POST",
                contentType: 'application/json',
                data: JSON.stringify(jsonBody),
                success: function (response) {
                    console.log("in success response")
                    var data = JSON.parse(response);

                    if (itrMode === "upload") {
                        var url = data.Response.Body.message.url;
                        console.log(url);
                        var modal = currentTab.find('.iframe-modal');
                        var iframe = modal.find('.itr-iframe');
                        var loadingIndicator = modal.find('.loading-indicator');
                        iframe.on('load', function () {
                            loadingIndicator.hide();
                            iframe.show();
                        });
                        iframe.attr('src', url);
                        modal.data('triggerElement', triggerElement);
                        modal.modal('show');
                    } else if (itrMode === "sms") {
                        alertmsg("An email has been sent to the customer with a link to the Perfios portal for ITR retrieval.Please ensure the customer completes the process within 48 hours.");
                        //currentTab.find('.btn-itr').prop('disabled', true);
                    }
                    resolve(response);
                },
                error: function (xhr, status, error) {
                    var currentTab = triggerElement.closest('.det').parent();
                    var modal = currentTab.find('.iframe-modal');
                    var iframe = modal.find('.itr-iframe');
                    modal.data('triggerElement', triggerElement);
                    // var loadingIndicator = modal.find('.loading-indicator');
                    // Handle the error scenario
                    // Display an error message to the user
                    iframe.html("Error fetching ITR details.");
                    modal.modal('show');
                    reject(error);
                }
            });
            $('.check-itr-status').show();
        });
    }
}

function fetchITRUpload(triggerElement) { //itr upload LINK
    var itrPan = triggerElement.closest('.det').find('input[name^="incomePAN"]').val();
    var itrDOB = triggerElement.closest('.det').find('input[name^="incomeDOB"]').val();
    var itrSMSMobileNo = triggerElement.closest('.det').find('input[name^="incomeMOB"]').val();

    var currentYear = new Date().getFullYear()-1;
    var currentMonth = new Date().getMonth();
    if(currentMonth<3) {
        currentYear--;
    }
    var lastTwoYears = [
        currentYear + 1  + "-" + (currentYear+1).toString().slice(-2),
        currentYear + "-" + (currentYear).toString().slice(-2)
    ];

    var jsonBody = {
        itrPan: itrPan,
        itrDOB: itrDOB,
        itrSMSMobileNo: itrSMSMobileNo,
        itrMode: "upload",
        wiNum: $('#winum').val(),
        applicantId: triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
        slno: $('#slno').val(),
        itrYearsList: lastTwoYears,
        form26asYearsList: lastTwoYears,
        form16YearsList: lastTwoYears
    };
return new Promise((resolve,reject)=> {
    $.ajax({
        url: "api/fetchITRUpload",
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(jsonBody),
        success: function (response) {
            var data = JSON.parse(response);
            if (!data.Response || !data.Response.Body.message.url) { throw new Error("Unexpected response structure"); }
            var url = data.Response.Body.message.url;
            var currentTab = triggerElement.closest('.det').parent();
            var modal = currentTab.find('.iframe-modal');
            var iframe = modal.find('.itr-iframe');
            var loadingIndicator = modal.find('.loading-indicator');

            iframe.on('load', function () {
                loadingIndicator.hide();
                iframe.show();
            });
            iframe.attr('src', url);
            modal.data('triggerElement', triggerElement);
            modal.modal('show');
            resolve(response);
        },
        error: function (xhr, status, error) {
            var currentTab = triggerElement.closest('.det').parent();
            var modal = currentTab.find('.iframe-modal');
            var iframe = modal.find('.itr-iframe');
            modal.data('triggerElement', triggerElement);
            iframe.html("Error fetching ITR upload details.");
            modal.modal('show');
            reject(error);
        }
    });
});
}


function checkLatestCompletedITRTransactionStatus(triggerElement) {
    var wiNum = $('#winum').val();
    var applicantId = triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val()
    var jsonBody = {
        wiNum: wiNum,
        applicantId: applicantId
    };
    showLoader();

    $.ajax({
        url: "api/getLatestCompletedITRTransactionId",
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(jsonBody),
        success: function (response) {
            var data = JSON.parse(response);
            console.log("ITR Response is " + data);
            if (data.itrData && data.itrData.itrData) {
               if (data.itrData.itrData.length > 0 && data.itrData.itrData[0].message) {
                    alertmsg("ITR document processing not done due to unsupported ITR type.");
                    triggerElement.closest('.det').find('.itrResponse').html(data.itrData.itrData[0].message);
                    triggerElement.closest('.det').find('.itrMonthlyGrossDiv').show();
                    hideLoader();
                } else {
                    // If the response already contains ITR data, process it directly
                    showLoader();
                    processITRData(data, triggerElement);
                    hideLoader();
                }
            } else if (data.latestCompletedTransactionId && data.latestCompletedTransactionId !== "") {
                console.log("LATEST ITR IS ---" + data.latestCompletedTransactionId);
                showLoader();
                fetchITRReport(data.latestCompletedTransactionId, applicantId, wiNum, triggerElement);
                hideLoader();
            } else {
                notyaltInfo("ITR Processing not completed");
                checkITRStatus(triggerElement);
            }
        },
        error: function (xhr, status, error) {
            alertmsg("Error retrieving the latest completed transaction ID. Please try again later.");
            console.error("Error retrieving the latest completed transaction ID:", status, error);
        }
    });
}
 async function checkITRStatus(detElement) {
        const generaldetailsElement = detElement.closest('.det').parent().closest('.tab-pane').find('.generaldetails');

        const applicantId = generaldetailsElement.find('.appid').val();
        showLoader();
        try {
            const response = await $.ajax({
                url: 'api/itr-status',
                type: 'GET',
                data: {applicantId, wiNum}
            });
            updateUIWithITRStatus(detElement,response);
            hideLoader();
            return response;
        } catch (error) {
            console.error('Error checking ITR status:', error);
            updateUIWithITRStatus(detElement,null); // Show "No active ITR request found" if there's an error
            hideLoader();
            throw error;
        }
    }

function updateUIWithITRStatus(detElement, status) {
    let statusHtml = '<div class="itr-status-container">';

    if (status) {
        const createdDate = new Date(status.timestamp);
        statusHtml += `
            <div class="status-item">
                <span class="status-label">Mode:</span>
                <span class="status-value">${status.itrMode || 'N/A'}</span>
            </div>
            <div class="status-item">
                <span class="status-label">Created On:</span>
                <span class="status-value">${createdDate.toLocaleString()}</span>
            </div>
        `;

        if (status.updated) {
            const updatedDate = new Date(status.updated);
            statusHtml += `
                <div class="status-item">
                    <span class="status-label">Updated On:</span>
                    <span class="status-value">${updatedDate.toLocaleString()}</span>
                </div>
            `;
        }

        statusHtml += '<div class="status-message">';

        if (status.perfiosStatus) {
            statusHtml += `
                <p><strong>Status:</strong> ${status.perfiosStatus}</p>
                <p><strong>Perfios ID:</strong> ${status.perfiosTransactionId}</p>
            `;
        }

        if (status.perfiosMessage) {
            statusHtml += `<p><strong>Updates:</strong> ${status.perfiosMessage}</p>`;
        }

        if (!status.perfiosStatus && !status.perfiosMessage) {
            let statusMessage = status.itrMode === "upload"
                ? "Upload Link Generated"
                : "Link Generated and Sent to Customer";

            statusHtml += `
                <p><strong>Status:</strong> ${statusMessage}</p>
                <p><strong>Reference ID:</strong> ${status.generateLinkId}</p>
            `;

            if (status.itrMode === "upload" && !status.perfiosStatus) {
                statusHtml += `<p><strong>URL:</strong> ${status.url}</p>`;
            }

            if (status.itrMode === "sms") {
                const expiryDate = new Date(createdDate.getTime() + 48 * 60 * 60 * 1000);
                statusHtml += `<p><strong>Expiry:</strong> <span class="expiry-time">${expiryDate.toLocaleString()}</span></p>`;
            }
        }

        statusHtml += '</div>';

        if (status.delFlg === 'Y') {
            statusHtml += `
                <div class="status-message" style="border-left-color: #dc3545;">
                    <strong>Note:</strong> This ITR request has been marked as deleted.
                </div>
            `;
        }
    } else {
        statusHtml += `
            <div class="status-message">
                <p>No active ITR request found.</p>
            </div>
        `;
    }

    statusHtml += `
        <button class="btn check-itr-status">Check Status</button>
    </div>`;

    // Update UI with the generated HTML
    detElement.closest('.det').find('.itrResponse').html(statusHtml);
    detElement.closest('.det').find('.itrMonthlyGrossDiv').show();

    // Update button states
    if (status && status.perfiosTransactionId) {
        $('.check-itr-status').show();
    } else if (status && status.generateLinkId) {
        $('.check-itr-status').show();
    } else {
        $('.check-itr-status').show();
    }
}




function checkLatestCompletedBSATransactionStatus(triggerElement) {
    var applicantId = triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val();
    var wiNum = $('#winum').val();
    var jsonBody = {
        wiNum: wiNum,
        applicantId: applicantId
    };
    showLoader();
    $.ajax({
        url: "api/getLatestCompletedBSATransactionId",
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(jsonBody),
        success: function (latestCompletedTransactionId) {

            if (latestCompletedTransactionId !== null || latestCompletedTransactionId !== "") {
                console.log("LATEST BSA IS ---" + latestCompletedTransactionId);
                showLoader();
                fetchBSAReport(latestCompletedTransactionId, applicantId, wiNum, triggerElement);
                hideLoader();
            } else {
                 notyaltInfo("BSA Processing not completed");
            }
        },
        error: function (xhr, status, error) {
              hideLoader();
            alertmsg("Error retrieving the latest completed transaction ID. Please try again later.");
            console.error("Error retrieving the latest completed transaction ID:", status, error);
        }
    });
      hideLoader();
}
async function checkBSAStatus(detElement) {
    const generaldetailsElement = detElement.closest('.det').parent().closest('.tab-pane').find('.generaldetails');
    const applicantId = generaldetailsElement.find('.appid').val();
    showLoader();
    try {
        const response = await $.ajax({
            url: 'api/bsa-status',
            type: 'GET',
            data: {applicantId, wiNum}
        });
        updateUIWithBSAStatus(detElement, response);
        hideLoader();
        return response;
    } catch (error) {
        console.error('Error checking BSA status:', error);
        updateUIWithBSAStatus(detElement, null); // Show "No active BSA request found" if there's an error
        hideLoader();
        throw error;
    }
}
function updateUIWithBSAStatus(detElement, status) {
    let statusHtml = '<div class="itr-status-container">';

    if (status) {
        const createdDate = new Date(status.timestamp);
        statusHtml += `
            <div class="status-item">
                <span class="status-label">Mode:</span>
                <span class="status-value">Upload</span>
            </div>
            <div class="status-item">
                <span class="status-label">Created On:</span>
                <span class="status-value">${createdDate.toLocaleString()}</span>
            </div>
        `;

        if (status.updated) {
            const updatedDate = new Date(status.updated);
            statusHtml += `
                <div class="status-item">
                    <span class="status-label">Updated On:</span>
                    <span class="status-value">${updatedDate.toLocaleString()}</span>
                </div>
            `;
        }

        statusHtml += '<div class="status-message">';

        if (status.perfiosStatus) {
            statusHtml += `
                <p><strong>Status:</strong> ${status.perfiosStatus}</p>
                <p><strong>Perfios ID:</strong> ${status.perfiosTransactionId}</p>
            `;
        }

        if (status.perfiosMessage) {
            statusHtml += `<p><strong>Updates:</strong> ${status.perfiosMessage}</p>`;
        }


        statusHtml += '</div>';

        if (status.delFlg === 'Y') {
            statusHtml += `
                <div class="status-message" style="border-left-color: #dc3545;">
                    <strong>Note:</strong> This BSA request has been marked as deleted.
                </div>
            `;
        }
    } else {
        statusHtml += `
            <div class="status-message">
                <p>No active BSA request found.</p>
            </div>
        `;
    }

    statusHtml += `
        <button class="btn check-bsa-status">Check Status</button>
    </div>`;

    // Update UI with the generated HTML
    detElement.closest('.det').find('.bsaResponse').html(statusHtml);
    detElement.closest('.det').find('.bsaABBDiv').show();
}
function fetchITRReport(perfiosTransactionId, applicantId, wiNum, triggerElement) {
    var jsonBody = {
        perfiosTransactionId: perfiosTransactionId,
        applicantId: applicantId,
        wiNum: wiNum
    };
    const itrResponseElement = triggerElement.closest('.det').find('.incomediv')[0];
    //const loadingOverlay = showLoadingAnimation(itrResponseElement);
    $.ajax({
        url: "api/fetchITRReport",
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(jsonBody),
        success: function (response) {
            var data = JSON.parse(response);
            processITRData(data, triggerElement);
        },
        error: function (xhr, status, error) {
            //hideLoadingAnimation(loadingOverlay);
            alertmsg("Error fetching report. Please try again later.");
            console.error("Error fetching report:", status, error);
        }
    });
}

function processITRData(data, triggerElement) {
    var itrData = data.itrData.itrData;
    var monthlyGrossIncome = data.itrData.monthlyGrossIncome;
    var tableHtml = '<table class="table table-border-dashed">';
    tableHtml += '<thead><tr><th>FY</th><th>Gross Total Income</th><th>Total Income</th><th>PAN</th><th>Name</th><th>Form No</th></tr></thead>';
    tableHtml += '<tbody>';
    itrData.forEach(function (itr) {
        tableHtml += '<tr>';
        tableHtml += '<td>' + itr.fy + '</td>';
        tableHtml += '<td>' + itr.grossTotalIncome + '</td>';
        tableHtml += '<td>' + itr.totalIncome + '</td>';
        tableHtml += '<td>' + itr.pan + '</td>';
        tableHtml += '<td>' + itr.name + '</td>';
        tableHtml += '<td>' + itr.formNo + '</td>';
        tableHtml += '</tr>';
    });
    tableHtml += '</tbody>';
    tableHtml += '</table>';

    // if (loadingOverlay) {
    //     hideLoadingAnimation(loadingOverlay);
    // }

    console.log(data);
    triggerElement.closest('.det').find('.itrResponse').html(tableHtml);
    triggerElement.closest('.det').find('.itrMonthlyGrossDiv').show();

    triggerElement.closest('.det').find('input[name^="itrMonthlyGross"]').val(monthlyGrossIncome);
    var programBtn = triggerElement.closest('.det').find('.save-button-program');
    programBtn.prop("disabled", false);

    if (data.status === "failure") {
        alertmsg(data.message);
    }
}
function fetchBSAReport(perfiosTransactionId, applicantId, wiNum, triggerElement) {
    var jsonBody = {
        perfiosTransactionId: perfiosTransactionId,
        applicantId: applicantId,
        wiNum: wiNum
    };
    showLoader();
    const bsaResponseElement = triggerElement.closest('.det').find('.incomediv')[0];
    const loadingOverlay = showLoadingAnimation(bsaResponseElement);
    $.ajax({
        url: "api/fetchBSAReport",
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(jsonBody),
        success: function (response) {
            var data = JSON.parse(response);
            if(data.bsaData && data.bsaData.averageBankBalance!==undefined) {
                var abbAmount = data.bsaData.averageBankBalance;
                console.log(data);
                triggerElement.closest('.det').find('.bsaResponse').html(`
                <table class="table table-bordered">
                    <tr>
                        <th colspan="13">Account Information</th>
                    </tr>
                    <tr>
                        <td>Name</td>
                        <td colspan="2"><b>${data.customerInfo.name}</b></td>
                        <td>Account No</td>
                        <td>${data.accountInfo.accountNo}</td>
                        <td colspan="2">Account Type</td>
                        <td colspan="3"><b>${data.accountInfo.accountType}</b></td>
                    </tr>
                    <tr>
                        <th colspan="13">Monthly Details</th>
                    </tr>
                    <tr>
                        <th>Month</th>
                        <th>Total Credits</th>
                        <th>Total Debits</th>
                        <th>Balance Min</th>
                        <th>Balance Max</th>
                        <th>Balance Avg</th>
                        <th>Calculated ABB</th>
                        <th>Included in ABB</th>
                        <th>Penal Charges</th>
                        <th>Outward Bounces</th>
                        <th>Total Salary</th>
                        <th>Inward Bounces</th>
                    </tr>
                    ${data.monthlyDetails.map(month => `
                        <tr ${month.isTrimmed ? '' : 'style="background-color: #ffcccc;"'}>
                            <td>${month.month}</td>
                            <td>${month.totalCredits}</td>
                            <td>${month.totalDebits}</td>
                            <td>${month.balanceMin}</td>
                            <td>${month.balanceMax}</td>
                            <td>${month.balanceAvg}</td>
                            <td><b>${month.calculatedABB.toFixed(2)}</b></td>
                            <td>${month.isTrimmed ? 'Yes' : 'No'}</td>
                            <td>${month.penalCharges}</td>
                            <td>${month.outwBounces}</td>
                            <td>${month.totalSalary}</td>
                            <td>${month.inwBounces}</td>
                        </tr>
                    `).join('')}
                </table>
                <div class="mt-3 ml-1">
                    <h4>ABB Calculation Summary</h4>
                    <p>Final Average Bank Balance: <b>₹${abbAmount.toFixed(2)}</b></p>
                    <p>Calculation Method: Average of monthly ABBs (excluding highest and lowest)</p>
                </div>
            `);
                hideLoadingAnimation(loadingOverlay);
                triggerElement.closest('.det').find('.bsaABBDiv').show();
                triggerElement.closest('.det').find('input[name^="bsaABB"]').val(abbAmount.toFixed(2));
                var programBtn = triggerElement.closest('.det').find('.save-button-program');
                programBtn.prop("disabled", false);
            } else {
                checkBSAStatus(triggerElement);
            }
            hideLoader();
        },
        error: function (xhr, status, error) {
             hideLoader();
            hideLoadingAnimation(loadingOverlay);
            alertmsg("Error fetching report. Please try again later.");
            console.error("Error fetching report:", status, error);
        }
    });
}

function fetchBSAReportold(perfiosTransactionId, applicantId, wiNum, triggerElement) {
    var jsonBody = {
        perfiosTransactionId: perfiosTransactionId,
        applicantId: applicantId,
        wiNum: wiNum
    };
    const bsaResponseElement = triggerElement.closest('.det').find('.incomediv')[0];
    const loadingOverlay = showLoadingAnimation(bsaResponseElement);
    $.ajax({
        url: "api/fetchBSAReport",
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(jsonBody),
        success: function (response) {
            var data = JSON.parse(response);
            var abbAmount = data.bsaData.averageBankBalance;
            console.log(data);
            triggerElement.closest('.det').find('.bsaResponse').html(`
                <table class="table table-border">
                    <tr>
                        <th colspan="12">Account Information</th>
                    </tr>
                     <tr>
                        <td >Name</td>
                        <td colspan="2"><b>${data.customerInfo.name}</b></td>
                        <td>Account No</td>
                        <td>${data.accountInfo.accountNo}</td>
                        <td colspan="2">Account Type</td>
                        <td colspan="3"><b>${data.accountInfo.accountType}</b></td>
                    </tr>
                
                    <tr>
                        <th colspan="12">Monthly Details</th>
                    </tr>
                    <tr>
                        <th>Month</th>
                        <th>Total Credits</th>
                        <th>Total Debits</th>
                        <th>Balance Min</th>
                        <th>Balance Max</th>
                        <th>Balance Avg</th>
                        <th>Penal Charges</th>
                        <th>Outward Bounces</th>
                        <th>Total Salary</th>
                        <th>Inward Bounces</th>
                    </tr>
                    ${data.monthlyDetails.map(month => `
                        <tr>
                            <td>${month.month}</td>
                            <td>${month.totalCredits}</td>
                            <td>${month.totalDebits}</td>
                            <td>${month.balanceMin}</td>
                            <td>${month.balanceMax}</td>
                            <td>${month.balanceAvg}</td>
                            <td>${month.penalCharges}</td>
                            <td>${month.outwBounces}</td>
                            <td>${month.totalSalary}</td>
                            <td>${month.inwBounces}</td>
                        </tr>
                    `).join('')}
                </table>
            `);
            hideLoadingAnimation(loadingOverlay);
            triggerElement.closest('.det').find('.bsaABBDiv').show();
            triggerElement.closest('.det').find('input[name^="bsaABB"]').val(abbAmount);
            var programBtn = triggerElement.closest('.det').find('.save-button-program');
            programBtn.prop("disabled", false);
        },
        error: function (xhr, status, error) {
            hideLoadingAnimation(loadingOverlay);
            alertmsg("Error fetching report. Please try again later.");
            console.error("Error fetching report:", status, error);
        }
    });
}


function incomesave(form, key, callback) {
    var allFiles = [];
    var promises = [];
    var fileType = null;
    var fileTypes = [];
    var programCodeSelected = form.find('select[name="programCode"]').val();
    var residentialStatusSelected = form.find('input[name="residentialStatus"]').val();
    var itrFlgSelected = form.find('input[name="itravailable"]:checked').val();
    console.log("programCodeSelected - " + programCodeSelected);
    console.log("residentialStatusSelected - " + residentialStatusSelected);
    console.log("itrFlgSelected - " + itrFlgSelected);
    form.find('.base64file').each(function () {
        var input = $(this);
        var files = input[0].files;
        var maxSize = input.data('max-size') || 2097152;

        Array.from(files).forEach(file => {

            if (file.size > maxSize) {
                alertmsg('File ' + file.name + " is too large. Maximum size is 2MB");
                promises.push(Promise.reject('File too large'));
                return;
            }
            var promise = new Promise((resolve, reject) => {
                var reader = new FileReader();
                reader.onload = function (e) {
                    if (input.hasClass('salaryfile') && programCodeSelected === "INCOME" && residentialStatusSelected === "R" && itrFlgSelected !== "Y") {
                        var row = input.closest('tr');
                        var month = row.find('.sal_month').val();
                        fileType = 'SALARY_FILE_' + getMonthName(month);
                        console.log("Salary file condition met. fileType:", fileType);
                    } else if (input.hasClass('doc70_30_upd') && programCodeSelected === "70/30") {
                        fileType = '70_30';
                        console.log("70/30 file condition met. fileType:", fileType);
                    } else if (input.hasClass('form16_upd')) {
                        fileType = 'FORM16';
                        console.log("form16 file condition met. fileType:", fileType);
                    }
                    console.log("File read successfully:", file.name);
                    var fileData = {
                        DOC_EXT: file.name.split('.').pop(),
                        DOC_NAME: fileType,
                        DOC_BASE64: e.target.result.split(',')[1],
                        reqtype: form.attr('data-code')
                    };
                    console.log("File data prepared:", fileData.DOC_NAME, fileData.DOC_EXT);
                    allFiles.push(fileData);
                    resolve();
                };
                console.log(allFiles);
                reader.onerror = function () {
                    reject('Failed to read file: ' + file.name);
                };
                reader.readAsDataURL(file);
            });
            promises.push(promise);
        });

    });
    var vehicleLoanProgramSalaryList = [];
    form.find('.salarytable_body tr').each(function () {
        var row = $(this);
        var salMonth = row.find('.sal_month').val();
        var salaryFile = row.find('.salaryfile')[0].files[0];
        var vehicleLoanProgramSalary = {
            salMonth: salMonth,
            salaryFile: salaryFile
        };
        vehicleLoanProgramSalaryList.push(vehicleLoanProgramSalary);
    });
    Promise.all(promises).then(() => {
        console.log('All files processed:', allFiles);
        var applicantId = form.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val();
        var wiNum = $('#winum').val();
        var slno = $('#slno').val();
        var formDataArray = form.serializeArray();

        // Determine which sections are relevant based on selected options
        var incomeCheck = form.find('input[name="incomeCheck"]:checked').val();
        var programCode = form.find('select[name="programCode"]').val();
        var residentialStatus = form.find('input[name="residentialStatus"]').val();
        var itrFlg = form.find('input[name="itravailable"]:checked').val();
        var monthlyorabb = form.find('input[name="monthlyorabb"]:checked').val();
        var data = formDataArray.map(function (item) {
            return {key: item.name, value: item.value}; // Transform to key-value pair objects
        });
        var jsonBody = {
            id: key,
            slno: slno,
            winum: wiNum,
            appid: applicantId,
            data: data,
            reqtype: form.attr('data-code'),
            DOC_ARRAY: allFiles,
            vehicleLoanProgramSalaryList: vehicleLoanProgramSalaryList
        };
        if (form.find('.programCode').val() === 'LOANFD' && form.find('.incomeCheck:checked').val() === "Y") {
            var totalavailBalance = form.find('.totalavailBalance').val();
            var fdRowCount = form.find('.fdResponse tbody tr').length;
            if (!totalavailBalance || totalavailBalance === '0' || fdRowCount === 0) {
                alertmsg('Enter at least one FD account Detail.');
                callback(false);
                return;
            }
        }
        if (form.find('.programCode').val() === '70/30' && form.find('.incomeCheck:checked').val() === "Y") {
            var doc70_30_upd = form.find('.doc70_30_upd').val();
            if (!doc70_30_upd || doc70_30_upd === '') {
                alertmsg('Upload the supporting dcument for 70/30 program.');
                callback(false);
                return;
            }
            var fileExtension = doc70_30_upd.split('.').pop().toLowerCase();
            if (fileExtension !== 'pdf') {
                alertmsg('The supporting document must be a PDF file.');
                callback(false);
                return;
            }

        }
        if (form.find('.programCode').val() === 'SURROGATE' && form.find('.incomeCheck:checked').val() === "Y") {
            var abbamount = form.find('.abb-amount').val();
            if (!abbamount || abbamount === '') {
                alertmsg('BSA details processing not completed. ABB amount details pending');
                callback(false);
                return;
            }
        }
        if (form.find('.programCode').val() === 'INCOME' && form.find('.incomeCheck:checked').val() === "Y") {
            var residentialStatus = form.find('input[name="residentialStatus"]').val();
            if (form.find('.itravailable').val() === 'N' && residentialStatus === "R") {

            }
            if (residentialStatus === "N") {
                if (form.find('input[name="monthlyorabb"]:checked').val() === "MonthSalary") {
                    if (form.find('input[name="MonthSalary"]').val() === "0" || form.find('input[name="MonthSalary"]').val() === "") {
                        alertmsg('Monthly remittance details not entered');
                        callback(false);
                        return;
                    } else {
                        if (form.find('input[name="Avgtotal_remittance"]').val() === "0" || form.find('input[name="Avgtotal_remittance"]').val() === "") {
                            alertmsg('Monthly remittance details not entered');
                            callback(false);
                            return;
                        }

                    }
                } else if (form.find('input[name="monthlyorabb"]:checked').val() === "ABB") {
                    if (form.find('input[name="abb"]').val() === "0" || form.find('input[name="abb"]').val() === "") {
                        alertmsg('ABB as per overseas Statement details not entered');
                        callback(false);
                        return;
                    }
                }

            }
            // alert("checker");
            //      var abbamount = form.find('.abb-amount').val();
            //       if (!abbamount || abbamount === '') {
            //         alertmsg('BSA details processing not completed. ABB amount details pending');
            //         callback(false);
            //         return;
            //     }
        }

        $.ajax({
            url: 'api/save-data',
            type: 'POST',
            async: false,
            contentType: 'application/json', // Set content type to JSON
            data: JSON.stringify(jsonBody), // Convert data object to JSON string
            success: function (response) {
                if (response.status === 'S') {
                    form.find('.appid').val(response.appid);
                    savedProgram = form.find('select[name="programCode"]').val();
                    savedmonthlyorabb = form.find('input[name="monthlyorabb"]:checked').val();
                    disableFormInputs(form);
                    notyalt('Income Details Saved !!')
                    silentRefresh(form);
                    callback(true);
                } else {
                    alertmsg('Error during Saving data');
                    callback(false);
                }
            },
            error: function (xhr, status, error) {
                alertmsg('Error during Saving data');
                callback(false);
            }
        });
    }).catch(error => {
        console.error('Error processing files:', error);
        callback(false);
    });
}


function fetchProgramDetails(triggerElement) {
    var applicantId = triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val();
    var wiNum = $('#winum').val();
    var slno = $('#slno').val();
    showLoader();
    $.ajax({
        url: 'api/fetch-program-details',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            applicantId: applicantId,
            slno: slno,
            wiNum: wiNum
        }),
        success: function (response) {
            if (response.status === 'S') {
                if (response.programDetails === null) {
                    console.log('No program found for this applicant');
                } else {
                    console.log('Program details fetched successfully');
                    updateFormWithProgramDetails(response.programDetails, triggerElement);
                }
            } else {
                console.error('Failed to fetch program details:', response.message);
                //alertmsg('Failed to fetch program details: ' + response.message);
                // Revert the select box to the previous value or empty
                $('#programCode').val('').trigger('change');
            }
            hideLoader();
        },
        error: function (xhr, status, error) {
            console.error('Error fetching program details:', error);
            // alertmsg('Error fetching program details: ' + error);
            // Revert the select box to the previous value or empty
            hideLoader();
            $('#programCode').val('').trigger('change');
        }
    });
}

function updateFormWithProgramDetails(programDetails, triggerElement) {
    console.log("program details as per DB for the applicant  ======" + programDetails.loanProgram);
    let savedProgram = programDetails.loanProgram;
    let selectedProgram = triggerElement.closest('.det').closest('.tab-pane').find('.programCode').val();
    var switchProgram = "";
    console.log("saved progarm is " + savedProgram);
    console.log("selected progarm is " + selectedProgram);
    // if(savedProgram===null ||savedProgram==="") {
    //     switchProgram=selecteddProgram;
    // } else {
    //     if(savedProgram===selecteddProgram) {
    //         switchProgram=selecteddProgram;
    //     } else {
    //
    //     }
    // }

    switch (selectedProgram) {
        case 'INCOME':
            resetProgramFields(triggerElement);
            updateIncomeDetails(programDetails, triggerElement);
            break;
        case 'SURROGATE':
            resetProgramFields(triggerElement);
            updateSurrogateDetails(programDetails, triggerElement);
            break;
        case '70/30':
            resetProgramFields(triggerElement);
            update7030Details(programDetails, triggerElement);
            break;
        case 'LOANFD':
            resetProgramFields(triggerElement);
            updateFDDetails(programDetails, triggerElement);
            break;
    }
    $('#programCode').val(selectedProgram);
}

function resetProgramFields(triggerElement) {
    console.log("clearing the form data");
    triggerElement.closest('.det').closest('.tab-pane').find('.incomediv').hide();
    triggerElement.closest('.det').closest('.tab-pane').find('.surrogatediv').hide();
    triggerElement.closest('.det').closest('.tab-pane').find('.bsaABBDiv').hide();
    triggerElement.closest('.det').find('.fromDate').val("");
    triggerElement.closest('.det').find('.toDate').val("");
    triggerElement.closest('.det').find('.bankName').val("");
    triggerElement.closest('.det').find('.abb-amount').val("");
    triggerElement.closest('.det').find('.itr-monthly-gross').val("");
    triggerElement.closest('.det').find('input[name^="form16available"]').prop('checked', false);
    triggerElement.closest('.det').closest('.tab-pane').find('.form16_div').hide();
    triggerElement.closest('.det').find('input[name^="itravailable"]').prop('checked', false);
    triggerElement.closest('.det').closest('.tab-pane').find('.itrbutton').hide();
    triggerElement.closest('.det').closest('.tab-pane').find('.salaryupd').hide();
    triggerElement.closest('.det').find('input[name^="monthlyorabb"]').prop('checked', false);
    triggerElement.closest('.det').closest('.tab-pane').find('.MonthSalary').val("");
    triggerElement.closest('.det').closest('.tab-pane').find('.abb').val("");
    triggerElement.closest('.det').closest('.tab-pane').find('.monthsalary').hide();
    triggerElement.closest('.det').closest('.tab-pane').find('.abb_div').hide();
    triggerElement.closest('.det').closest('.tab-pane').find('._70_30div').hide();
    triggerElement.closest('.det').find('.doc70_30_upd').val('');
    triggerElement.closest('.det').find('.itrResponse').empty();
    triggerElement.closest('.det').find('.itrMonthlyGross').val("");//itrMonthlyGross
    triggerElement.closest('.det').find('.fdResponse').empty();
    triggerElement.closest('.det').find('.AvgIncome').val("");
    triggerElement.closest('.det').find('.totalavailBalance').val("");
    triggerElement.closest('.det').find('.salarytable_body').empty();
    var emptyRow = `<tr> <td> <select name="sal_month" class="form-control select sal_month"> <option value="" selected>Select Month</option> <option value="1">January</option> <option value="2">February</option> <option value="3">March</option> <option value="4">April</option> <option value="5">May</option> <option value="6">June</option> <option value="7">July</option> <option value="8">August</option> <option value="9">September</option> <option value="10">October</option> <option value="11">November</option> <option value="12">December</option> </select> </td> <td><input type="file" class="form-control salaryfile base64file" name="salaryfile"></td> <td> <button type="button" class="btn btn-danger btn-file ibtnDel" onClick="deleteRowIncome(this)"> <i class="ph-archive"></i> </button> </td> </tr>`;
    triggerElement.closest('.det').find('.salarytable_body').html(emptyRow);
    var monthSalarytableBody = triggerElement.closest('.det').find('.MonthSalarytable_body');
    monthSalarytableBody.empty();
    for (var i = 0; i < 12; i++) {
        var newRow = ` <tr> <td><input type="text" class="form-control MonthSalary_mon" name="MonthSalary_mon${i}" id="MonthSalary_mon${i}" readonly/></td> <td><input type="text" class="form-control total_remittance" name="total_remittance${i}" id="total_remittance${i}" onchange="calculateRemittance(this)"/></td> <td><input type="text" class="form-control bulk_remittance" name="bulk_remittance${i}" id="bulk_remittance${i}" readonly/></td> <td><input type="text" class="form-control net_remittance" name="net_remittance${i}" id="net_remittance${i}" readonly/></td> </tr> `;
        monthSalarytableBody.append(newRow);
    }
    triggerElement.closest('.det').find('#Avgtotal_remittance, #Avgbulk_remittance, #Avgnet_remittance').val('');
    populateMonths(triggerElement);
}

function updateIncomeDetails(programDetails, triggerElement) {

    triggerElement.closest('.det').closest('.tab-pane').find('.incomediv').show();

    if (programDetails.form16Flg) {
        triggerElement.closest('.det').find('input[name^="form16available"][value="' + programDetails.form16Flg + '"]').prop('checked', true).trigger('change');
    }
    if (programDetails.itrFlg) {
        $('input[name="itravailable"][value="' + programDetails.itrFlg + '"]').prop('checked', true).trigger('change');
    }
    if (programDetails.residentialStatus) {
        if (programDetails.residentialStatus === 'R') {
            if (programDetails.vehicleLoanITRList && programDetails.vehicleLoanITRList.length > 0) {
                var itrList = programDetails.vehicleLoanITRList;
                if (itrList && itrList.length > 0) {
                    var tableHtml = "<table class='table table-border-dashed'>";
                    tableHtml += "<thead><tr><th>FY</th><th>Gross Total Income</th><th>Total Income</th><th>PAN</th><th>Name</th><th>Form No</th></tr></thead>";
                    tableHtml += "<tbody>";

                    var monthlyGrossIncome = 0;

                    itrList.forEach(function (itr) {
                        if (itr.delFlg === 'N' && itr.fetchResponse) {
                            var responseMap = JSON.parse(itr.fetchResponse);
                            var itrData = responseMap.itrData;
                            if (itrData && itrData.itrData) {
                                itrData.itrData.forEach(function (item) {
                                    tableHtml += "<tr>";
                                    tableHtml += "<td>" + item.fy + "</td>";
                                    tableHtml += "<td>" + item.grossTotalIncome + "</td>";
                                    tableHtml += "<td>" + item.totalIncome + "</td>";
                                    tableHtml += "<td>" + item.pan + "</td>";
                                    tableHtml += "<td>" + item.name + "</td>";
                                    tableHtml += "<td>" + item.formNo + "</td>";
                                    tableHtml += "</tr>";
                                });
                            }
                            if (itrData.monthlyGrossIncome) {
                                monthlyGrossIncome = itrData.monthlyGrossIncome;
                            }
                        }

                    });
                    tableHtml += "</tbody></table>";
                    $('.itrMonthlyGrossDiv').show();
                    triggerElement.closest('.det').find('.itrMonthlyGrossDiv .itrResponse').html(tableHtml);
                    triggerElement.closest('.det').find('.itrMonthlyGrossDiv #itrMonthlyGross').val(monthlyGrossIncome);
                } else {
                    triggerElement.closest('.det').find('.itrMonthlyGrossDiv').hide();
                }
            }
            if (programDetails.vehicleLoanProgramSalaryList && programDetails.vehicleLoanProgramSalaryList.length > 0) {
                var salaryList = programDetails.vehicleLoanProgramSalaryList;
                if (salaryList && salaryList.length > 0) {
                    var tableHtml = "";
                    salaryList.forEach(function (salary, index) {
                        tableHtml += "<tr>";
                        tableHtml += "<td><select name='sal_month' class='form-control sal_month'>";
                        tableHtml += "<option value='" + salary.salMonth + "' selected>" + getMonthName(salary.salMonth) + "</option>";
                        tableHtml += "</select></td>";
                        tableHtml += "<td><input type='file' class='form-control salaryfile base64file' name='salaryfile" + salary.salMonth + "' disabled>";
                        tableHtml += "<span class='text-success'>File uploaded</span></td>";
                        tableHtml += "<td><button type='button' class='btn btn-danger btn-file ibtnDel' data-ino='" + salary.ino + "' onClick='deleteRowIncome(this)'><i class='ph-archive'></i></button></td>";
                        tableHtml += "</tr>";
                    });
                    triggerElement.closest('.det').find('.salarytable_body').html(tableHtml);
                    triggerElement.closest('.det').find('.salaryupd').show();
                    triggerElement.closest('.det').find('input[name^="AvgIncome"]').val(programDetails.avgSal);

                }

            }
        } else if (programDetails.residentialStatus === "N") {
            if (programDetails.doctype) {
                var radioValue = "";
                if (programDetails.doctype === 'OVERSEASABB') {
                    radioValue = "ABB";
                } else if (programDetails.doctype === 'MONTHLY') {
                    radioValue = "MonthSalary";
                } else {
                    radioValue = "";
                }
                triggerElement.closest('.det').find('input[name="monthlyorabb"][value="' + radioValue + '"]').prop('checked', true).trigger('change');
            } else {
                triggerElement.closest('.det').find('input[name="monthlyorabb"]').prop('checked', false);
            }
            if (programDetails.abb) {
                triggerElement.closest('.det').find('#abb').val(programDetails.abb);
            }
            if (programDetails.MonthSalary) {
                triggerElement.closest('.det').find('.MonthSalary').val(programDetails.MonthSalary);
                if (programDetails.vehicleLoanProgramNRIList && programDetails.vehicleLoanProgramNRIList.length > 0) {
                    var tableHtml = "";
                    programDetails.vehicleLoanProgramNRIList.forEach(function (nri, index) {
                        tableHtml += "<tr>";
                        tableHtml += "<td><input type='text' class='form-control MonthSalary_mon' name='MonthSalary_mon" + index + "' value='" + formatDate(nri.remitMonth + "-" + nri.remitYear) + "' readonly /></td>";
                        tableHtml += "<td><input type='text' class='form-control total_remittance' name='total_remittance" + index + "' value='" + (nri.totRemittance || '') + "' onchange='calculateRemittance(this)' /></td>";
                        tableHtml += "<td><input type='text' class='form-control bulk_remittance' name='bulk_remittance" + index + "' value='" + (nri.bulkRemittance || '') + "' readonly /></td>";
                        tableHtml += "<td><input type='text' class='form-control net_remittance' name='net_remittance" + index + "' value='" + (nri.netRemittance || '') + "' readonly /></td>";
                        tableHtml += "</tr>";
                    });
                    $('.MonthSalarytable_body').html(tableHtml);

                    // Update average remittances
                    updateAverageRemittances(programDetails.vehicleLoanProgramNRIList);
                }
            }
        }
    }

}

function getMonthName(monthVal) {
    const monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
    return monthNames[monthVal - 1];
}

function formatCurrency(amount) {
    if (amount === null || amount === undefined) return '';
    return parseFloat(amount).toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
}

function updateSurrogateDetails(programDetails, triggerElement) {
    triggerElement.closest('.det').closest('.tab-pane').find('.surrogatediv').show();

    const bsaList = programDetails.vehicleLoanBSAList;
    if (bsaList && bsaList.length > 0) {
        var hidStartDate = triggerElement.closest('.det').closest('.tab-pane').find('.hidStartDate').val();
        var hidEndDate = triggerElement.closest('.det').closest('.tab-pane').find('.hidEndDate').val();
        var hidInstituition = triggerElement.closest('.det').closest('.tab-pane').find('.hidInstituition').val();
        console.log("====hidStartDate:" + hidStartDate + "---hidEndDate" + hidEndDate + "==hidInstituition" + hidInstituition);
        triggerElement.closest('.det').closest('.tab-pane').find('.fromDate').val(hidStartDate);
        triggerElement.closest('.det').closest('.tab-pane').find('.toDate').val(hidEndDate);
        triggerElement.closest('.det').closest('.tab-pane').find('.bankName').val(hidInstituition);
        triggerElement.closest('.det').closest('.tab-pane').find('.surrogatediv').show();

        if (!bsaList && bsaList.length === 0) {
            console.error('No BSA data found');
            return;
        }
        const bsaEntry = bsaList[0];
        const fetchResponse = JSON.parse(bsaEntry.fetchResponse);
        document.querySelector('.bsaResponse').innerHTML = `
        <table class='table table-border-dashed'>
            <tr><th colspan='12'>Account Information</th></tr>
            <tr>
                <td>Name</td><td colspan='2'><b>${fetchResponse.customerInfo.name}</b></td>
                <td>Account No</td><td>${fetchResponse.accountInfo.accountNo}</td>
                <td colspan='2'>Account Type</td><td colspan='3'><b>${fetchResponse.accountInfo.accountType}</b></td>
            </tr>
            <tr><th colspan='12'>Monthly Details</th></tr>
            <tr>
                <th>Month</th>
                <th>Total Credits</th>
                <th>Total Debits</th>
                <th>Balance Min</th>
                <th>Balance Max</th>
                <th>Balance Avg</th>
                <th>Penal Charges</th>
                <th>Outward Bounces</th>
                <th>Total Salary</th>
                <th>Inward Bounces</th>
            </tr>
            ${fetchResponse.monthlyDetails.map(month => `
                <tr>
                    <td>${month.month}</td>
                    <td>${month.totalCredits}</td>
                    <td>${month.totalDebits}</td>
                    <td>${month.balanceMin}</td>
                    <td>${month.balanceMax}</td>
                    <td>${month.balanceAvg}</td>
                    <td>${month.penalCharges}</td>
                    <td>${month.outwBounces}</td>
                    <td>${month.totalSalary}</td>
                    <td>${month.inwBounces}</td>
                </tr>
            `).join('')}
        </table>
    `;
        triggerElement.closest('.det').find('.bsaABBDiv').show();
        triggerElement.closest('.det').find('input[name^="bsaABB"]').val(programDetails.abb.toFixed(2));
    } else {
        console.log('No BSA data found - disbale save button');
        var programBtn = triggerElement.closest('.det').find('.save-button-program');
            programBtn.prop("disabled", true);
        //
    }
}

function update7030Details(programDetails, triggerElement) {

    triggerElement.closest('.det').closest('.tab-pane').find('._70_30div').show();
}

function updateFDDetails(programDetails, triggerElement) {

    var vehicleLoanFDList = "";
    if(programDetails.vehicleLoanFDList) {
         vehicleLoanFDList = programDetails.vehicleLoanFDList.fdAccounts;
    }

    if (vehicleLoanFDList && vehicleLoanFDList.length > 0) {
        var fdList = programDetails.vehicleLoanFDList.fdAccounts;
        if (fdList && fdList.length > 0) {
            var totalavailBalance = 0;
            var tableHtml = '<table class="table table-xs table-border-dashed">' +
                '<tr><th>FD A/C</th><th>Account Status</th><th>Account Type</th><th>Account Open Date</th><th>Deposit Amount Available</th><th>Deposit Amount</th><th>Deposit Amount Available</th><th>FSLD Adj</th><th>Available Balance</th><th>Eligible</th><th>Action</th></tr>';
            fdList.forEach(function (accountResponse) {
                if (accountResponse.eligible) {
                    var account = accountResponse.vehicleLoanFD;
                    var rowClass = accountResponse.eligible ? '' : 'class="text-muted"';
                    tableHtml += '<tr ' + rowClass + '>';
                    tableHtml += '<td>' + account.fdaccnum + '</td>';
                    tableHtml += '<td>' + account.fdStatus + '</td>';
                    tableHtml += '<td>' + account.singleJoint + '</td>';
                    tableHtml += '<td>' + formatDate(account.accountOpenDate) + '</td>';
                    tableHtml += '<td>' + formatDate(account.maturityDate) + '</td>';
                    tableHtml += '<td>' + account.depositAmount + '</td>';
                    tableHtml += '<td>' + account.fdBalAmount + '</td>';
                    tableHtml += '<td>' + account.fsldAdjAmount + '</td>';
                    tableHtml += '<td>' + account.availbalance + '</td>';
                    tableHtml += '<td>' + (accountResponse.eligible ? 'Yes' : 'No') + '</td>';
                    if (accountResponse.eligible) {
                        tableHtml += '<td><button type="button" class="btn btn-danger btn-file delete-btn" data-ino="' + account.ino + '">Delete</button></td>';
                    } else {
                        tableHtml += '<td></td>';
                    }

                    tableHtml += '</tr>';
                    if (accountResponse.eligible) {
                        totalavailBalance += account.availbalance;
                    }
                }
            });
            tableHtml += '</table>';
            triggerElement.closest('.det').find('.fdResponse').empty().html(tableHtml);
            triggerElement.closest('.det').find('.fdDetailsDiv').show();
            triggerElement.closest('.det').find('input[name^="fd_account_number"]').val('');
            triggerElement.closest('.det').find('input[name^="totalavailBalance"]').val(totalavailBalance);

            if (fdList.length > 0 && !fdList[0].eligible) {
                alertmsg("Some FD accounts may be ineligible for the loan application.");
            }
            if (programDetails.vehicleLoanFDList.missingCifIds && programDetails.vehicleLoanFDList.missingCifIds.length > 0) {
                alertmsg("The following CIF IDs need to be added as co-applicants to the loan: " + programDetails.vehicleLoanFDList.missingCifIds.join(", "));
            }
        } else {
            triggerElement.closest('.det').find('.fdDetailsDiv').hide();
        }
    } else {
        console.log("=====null data " + programDetails.vehicleLoanFDList);
    }
}

function updateAverageRemittances(nriList) {
    var totalRemittanceSum = 0;
    var bulkRemittanceSum = 0;
    var netRemittanceSum = 0;
    var count = 0;

    nriList.forEach(function (nri) {
        if (nri.totRemittance) {
            totalRemittanceSum += parseFloat(nri.totRemittance);
            count++;
        }
        if (nri.bulkRemittance) {
            bulkRemittanceSum += parseFloat(nri.bulkRemittance);
        }
        if (nri.netRemittance) {
            netRemittanceSum += parseFloat(nri.netRemittance);
        }
    });

    var avgTotalRemittance = count > 0 ? (totalRemittanceSum / count).toFixed(2) : '0.00';
    var avgBulkRemittance = count > 0 ? (bulkRemittanceSum / count).toFixed(2) : '0.00';
    var avgNetRemittance = count > 0 ? (netRemittanceSum / count).toFixed(2) : '0.00';

    $('#Avgtotal_remittance').val(avgTotalRemittance);
    $('#Avgbulk_remittance').val(avgBulkRemittance);
    $('#Avgnet_remittance').val(avgNetRemittance);
}

function fetchFDDetails(triggerElement) {
    var customerId = triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.custID').val();
    if (customerId == null || customerId === "") {
        alertmsg("Only existing cutomer can choose FD program");
        return false;
    } else {
        var jsonBody = {
            customerId: customerId,
            cifId: triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.custID').val(),
            sibCustomer: triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.sibCustomer').val(),
            residentialStatus: triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.residentialStatus').val(),
            slno: $('#slno').val(),
            wiNum: $('#winum').val(),
            applicantId: triggerElement.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val()
        };
        showLoader();
        $.ajax({
            url: "api/getFDAccountDetailsbycifV2",
            type: "POST",
            contentType: 'application/json',
            data: JSON.stringify(jsonBody),
            success: function (response) {
                // Update the table with the fetched FD accounts
                updateFDAccountsTable(response, triggerElement);
                hideLoader();
                 var programBtn = triggerElement.closest('.det').find('.save-button-program');
            programBtn.prop("disabled", false);

            },
            error: function (xhr, status, error) {
                hideLoader();
                alertmsg("FD Details Fetching-" + xhr.responseText);
                console.error("Error retrieving the FD Details:" + xhr.responseText, status, error);
            }
        });
    }
}

function updateFDAccountsTable(fdAccountResponse, triggerElement) {
    var fdAccounts = fdAccountResponse.fdAccounts;
    var totalavailBalance = 0;
    var tableHtml = '<table class="table table-xs table-border-dashed">' +
        '<tr><th>FD A/C</th><th>Account Status</th><th>Account Type</th><th>Account Open Date</th><th>Maturity Date</th><th>Deposit Amount</th><th>Deposit Amount Available</th><th>FSLD Adj</th><th>Available Balance</th><th>Eligible</th><th>Action</th></tr>';

    fdAccounts.forEach(function (accountResponse) {
        if (accountResponse.eligible) {
            var account = accountResponse.vehicleLoanFD;
            var rowClass = accountResponse.eligible ? '' : 'class="text-muted"';
            tableHtml += '<tr ' + rowClass + '>';
            tableHtml += '<td>' + account.fdaccnum + '</td>';
            tableHtml += '<td>' + account.fdStatus + '</td>';
            tableHtml += '<td>' + account.singleJoint + '</td>';
            tableHtml += '<td>' + formatDate(account.accountOpenDate) + '</td>';
            tableHtml += '<td>' + formatDate(account.maturityDate) + '</td>';
            tableHtml += '<td>' + account.depositAmount + '</td>';
            tableHtml += '<td>' + account.fdBalAmount + '</td>';
            tableHtml += '<td>' + account.fsldAdjAmount + '</td>';
            tableHtml += '<td>' + account.availbalance + '</td>';
            tableHtml += '<td>' + (accountResponse.eligible ? 'Yes' : 'No') + '</td>';
            if (accountResponse.eligible) {
                tableHtml += '<td><button type="button" class="btn btn-danger btn-file delete-btn" data-ino="' + account.ino + '">Delete</button></td>';
            } else {
                tableHtml += '<td></td>';
            }

            tableHtml += '</tr>';
            if (accountResponse.eligible) {
                totalavailBalance += account.availbalance;
            }
        }
    });

    tableHtml += '</table>';
    triggerElement.closest('.det').find('.fdResponse').empty().html(tableHtml);
    triggerElement.closest('.det').find('.fdDetailsDiv').show();
    triggerElement.closest('.det').find('input[name^="fd_account_number"]').val('');
    triggerElement.closest('.det').find('input[name^="totalavailBalance"]').val(totalavailBalance);

    if (fdAccounts.length > 0 && !fdAccounts[0].eligible) {
        alertmsg("Some FD accounts may be ineligible for the loan application.");
    }
    if (fdAccountResponse.missingCifIds && fdAccountResponse.missingCifIds.length > 0) {
        alertmsg("The following CIF IDs need to be added as co-applicants to the loan: " + fdAccountResponse.missingCifIds.join(", "));
    }
}

// function refreshFDDetailsForAllApplicants(triggerElement) {
//     triggerElement.closest('.det').each(function () {
//         var triggerElement = $(this).find('.fetchFDDetails');
//         fetchFDDetails(triggerElement);
//     });
// }
function refreshFDDetailsForAllApplicants() {
    $('#loanbody .tab-pane').each(function () {
        var detElement = $(this).closest('.program-itr');
        var cifId = $(this).find('.generaldetails').find('.custID').val();

        console.log("del cif" + cifId);
        $('#loanbody .tab-pane .program-itr').each(function () {
            if (cifId != null && cifId.length == "9") {
                detElement.find('.fd-account-validate').trigger('click');
            }
        });
    });
}

function formatDate(dateString) {
    var date = new Date(dateString);
    var day = date.getDate().toString().padStart(2, '0');
    var month = (date.getMonth() + 1).toString().padStart(2, '0');
    var year = date.getFullYear();
    return day + '-' + month + '-' + year;
}


function calculateRemittance(triggerElement) {
    const monthlySalary = parseFloat(triggerElement.closest('.nristatus').querySelector('.MonthSalary').value) || 0;
    if (triggerElement.closest('.MonthSalarytable')) {
        var table = triggerElement.closest('.MonthSalarytable');
        const tableBody = table.querySelector('.MonthSalarytable_body');
        const rows = tableBody.rows;
        let totalbulkRemittance = 0;
        let totalnetRemittance = 0;
        let totalallRemittance = 0;
        for (let i = 0; i < rows.length; i++) {
            let row = rows[i];
            let totalRemittanceInput = row.cells[1].querySelector('input');
            let totalRemittance = parseFloat(totalRemittanceInput.value) || 0;
            let bulkRemittanceCell = row.cells[2].querySelector('input');
            if (totalRemittance <= monthlySalary) {
                bulkRemittanceCell.value = '0.00';
            } else {
                bulkRemittanceCell.value = (totalRemittance - monthlySalary).toFixed(2);
            }
            let netRemittanceCell = row.cells[3].querySelector('input');
            netRemittanceCell.value = (totalRemittance - parseFloat(bulkRemittanceCell.value) || 0).toFixed(2);
            totalbulkRemittance += parseFloat(bulkRemittanceCell.value) || 0;
            totalnetRemittance += parseFloat(netRemittanceCell.value) || 0;
            totalallRemittance += totalRemittance;
        }
        let avgbulkRemittance = (totalbulkRemittance / 12).toFixed(2);
        let avgnetRemittance = (totalnetRemittance / 12).toFixed(2);
        let avgtotalRemittance = (totalallRemittance / 12).toFixed(2);
        triggerElement.closest('.nristatus').querySelector('.Avgnet_remittance').value = avgnetRemittance;
        triggerElement.closest('.nristatus').querySelector('.Avgtotal_remittance').value = avgtotalRemittance;
        triggerElement.closest('.nristatus').querySelector('.Avgbulk_remittance').value = avgbulkRemittance;
    }
}

function populateMonths(Element) {
    var triggerElement = Element.closest('.det').find('input.MonthSalary_mon');
    if (triggerElement.length > 0 && triggerElement[0].closest('.MonthSalarytable')) {
        var table = triggerElement[0].closest('.MonthSalarytable');
        const tableBody = table.querySelector('.MonthSalarytable_body');
        const currentDate = new Date();
        const monthName = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
        const monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
        const rows = tableBody.rows;
        for (let i = 0; i < rows.length; i++) {
            const date = new Date(currentDate.getFullYear(), currentDate.getMonth() - i - 1, 1);
            const optionText = `${monthNames[date.getMonth()]}-${date.getFullYear()}`;
            let row = rows[i];
            let MonthSalary_mon = row.cells[0].querySelector('input');
            MonthSalary_mon.value = optionText;
        }
    }

}

window.addEventListener('message', function (event) {
    if (event.data === 'submissionComplete') {
        closeModalAndShowMessage();
    }
});

function closeModalAndShowMessage() {
    modal.style.display = "none";
}

function showLoadingAnimation(element) {
    // Create overlay
    const overlay = document.createElement('div');
    overlay.classList.add('loading-overlay');
    overlay.style.position = 'absolute';
    overlay.style.top = '0';
    overlay.style.left = '0';
    overlay.style.width = '100%';
    overlay.style.height = '100%';
    overlay.style.backgroundColor = 'rgba(255, 255, 255, 0.8)';
    overlay.style.display = 'flex';
    overlay.style.justifyContent = 'center';
    overlay.style.alignItems = 'center';
    overlay.style.zIndex = '1000';

    // Create spinner
    const spinner = document.createElement('div');
    spinner.classList.add('spinner-border', 'text-primary');
    spinner.setAttribute('role', 'status');

    // Create loading text
    const loadingText = document.createElement('span');
    loadingText.textContent = 'Loading...';
    loadingText.style.marginLeft = '10px';

    // Append spinner and text to overlay
    overlay.appendChild(spinner);
    overlay.appendChild(loadingText);

    // Add overlay to the element
    element.style.position = 'relative';
    element.appendChild(overlay);

    return overlay;
}

function hideLoadingAnimation(overlay) {
    if (overlay && overlay.parentNode) {
        overlay.parentNode.removeChild(overlay);
    }
}

const AccountFormatter = {
    format(input) {
        const cleaned = input.replace(/\D/g, '');
        if (cleaned.length === 16) return cleaned;

        const parts = input.split('.');
        if (parts.length !== 3) return input;

        const [part1, part2, part3] = parts;
        const formatted = [
            part1.padStart(4, '0'),
            part2.padStart(3, '0'),
            part3.padStart(9, '0')
        ].join('');

        return formatted.length === 16 ? formatted : input;
    },

    attachToInput(inputElement) {
        $(inputElement).on('input', function () {
            const formatted = AccountFormatter.format(this.value);
            if (formatted !== this.value) {
                this.value = formatted;
                $(this).trigger('change');
            }
        });

        $(inputElement).on('blur', function () {
            if (this.value.length === 16) {
                // Trigger account name fetch or other actions
                getAccountName(this.value);
            }
        });
    }
};

function silentRefresh(form) {
    var applicantId = form.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val();
    var programCode = form.find('select[name="programCode"]').val();
    var postData = {
        action: 'SF5',
        slno: $('#slno').val(),
    };
    if (programCode !== "SURROGATE") {
        form.closest('.det').closest('.bsaResponse').html('');
    }


    // Perform the refresh
    // $.ajax({
    //     url: window.location.href,
    //     type: 'POST',
    //     data: postData,
    //     success: function(data) {
    //          hideLoader();
    //     },
    //     error: function(xhr, status, error) {
    //         console.error('Silent refresh failed:', error);
    //          notyalt('Failed to refresh data. Please try again.');
    //     }
    // });
}

function getIncomeDetails(element) {
    const incDetails = $(element).closest('.det').closest('.tab-pane').find('.Incomedetails');
    const genDetails = $(element).closest('.det').parent().closest('.tab-pane').find('.generaldetails');
    return {
        incomeCheck: incDetails.find('.incomeCheck:checked').val() === "Y",
        form16available: incDetails.find('.form16available:checked').val() === "Y",
        itravailable: incDetails.find('.itravailable:checked').val() === "Y",
        programCode: incDetails.find('.programCode').val(),
        residentialStatus: genDetails.find('.residentialStatus:checked').val(),
        totalavailBalance: incDetails.find('.totalavailBalance').val(),
        monthlyorabb: incDetails.find('.monthlyorabb:checked').val()
    };
}

// Helper function to check incomeCheck status
function isIncomeCheckChecked(element) {
    return getIncomeDetails(element).incomeCheck;
}

async function handleITRRequest(triggerElement,mode) {
        showLoader();
        try {
            const hasActiveProcess = await checkActiveITRProcess(triggerElement);
            if (hasActiveProcess) {
                hideLoader();
                const shouldReset = await showConfirmationDialog();
                if (!shouldReset) {
                    return;
                }
                showLoader();
                await resetActiveITRProcess(triggerElement);
                triggerElement.closest('.det').find('.itrResponse').html('');
                triggerElement.closest('.det').find('input[name^="itrMonthlyGross"]').val("");
            }

            const response = await fetchITR(triggerElement, mode);

            if(response!=null) {
                var respObj = JSON.parse(response);
                if (respObj.status === 'success') {
                   // showFeedback('ITR request processed successfully', 'success');
                    updateUIAfterITRRequest(respObj);
                } else {
                    showFeedback('Error processing ITR request', 'error');
                }
            }
             hideLoader();
        } catch (error) {
            hideLoader();
            showFeedback('An error occurred while processing your request. Please try again.', 'error');
        }
    }
    function showFeedback2(message, type) {
        alert(message); // Replace with a more sophisticated feedback mechanism if needed
    }
    function showFeedback(message, type = 'info', duration = 5000) {
    const feedbackTypes = {
        success: { icon: 'fa-check-circle', class: 'alert-success' },
        error: { icon: 'fa-exclamation-circle', class: 'alert-danger' },
        warning: { icon: 'fa-exclamation-triangle', class: 'alert-warning' },
        info: { icon: 'fa-info-circle', class: 'alert-info' }
    };

    const feedbackType = feedbackTypes[type] || feedbackTypes.info;
    const feedbackElement = $(`
        <div class="alert ${feedbackType.class} alert-dismissible fade show" role="alert">
            <i class="fas ${feedbackType.icon} me-2"></i>
            <strong>${type.charAt(0).toUpperCase() + type.slice(1)}:</strong> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `);
    $('body').append(feedbackElement);
    feedbackElement.css({
        position: 'fixed',
        top: '20px',
        left: '50%',
        transform: 'translateX(-50%)',
        zIndex: 9999,
        minWidth: '300px',
        maxWidth: '80%'
    });
    setTimeout(() => {
        feedbackElement.alert('close');
    }, duration);

    feedbackElement.on('closed.bs.alert', function () {
        $(this).remove();
    });
}
    function updateUIAfterITRRequest(response) {
        $('.check-itr-status').show();
        $('.btn-itr').prop('disabled', true);

        // if (response.url) {
        //     showFeedback(`ITR link sent.`, 'success');
        // } else {
        //     showFeedback('ITR document uploaded successfully', 'success');
        // }

    }
    async function checkActiveITRProcess(triggerElement) {
                const generaldetailsElement = triggerElement.closest('.det').parent().closest('.tab-pane').find('.generaldetails');
        const applicantId = generaldetailsElement.find('.appid').val();

        try {
            const response = await $.ajax({
                url: 'api/check-active-itr',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ applicantId, wiNum })
            });
            return response.hasActiveProcess;
        } catch (error) {
            console.error('Error checking active ITR process:', error);
            throw error;
        }
    }

    async function resetActiveITRProcess(detElement) {
                const generaldetailsElement = detElement.closest('.det').parent().closest('.tab-pane').find('.generaldetails');

        const applicantId = generaldetailsElement.find('.appid').val();
        try {
            await $.ajax({
                url: 'api/reset-active-itr',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ applicantId, wiNum })
            });
        } catch (error) {
            console.error('Error resetting active ITR process:', error);
            throw error;
        }
    }

    async function showConfirmationDialog() {
        try {
            const result = await confirmmsg("An active ITR process already exists. Do you want to reset this and create a new one?");
            return result;
        } catch (error) {
            console.error("Error in confirmation dialog:", error);
            return false;
        }
    }


     async function showConfirmationBSADialog() {
        try {
            const result = await confirmmsg("An active Bank Statement analysis process already exists. Do you want to reset this and create a new one?");
            return result;
        } catch (error) {
            console.error("Error in confirmation dialog:", error);
            return false;
        }
    }