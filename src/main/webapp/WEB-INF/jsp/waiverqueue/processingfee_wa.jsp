<%@ page import="com.sib.ibanklosucl.dto.doc.WaiverDto" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.model.EligibilityDetails" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanDetails" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="org.springframework.security.core.parameters.P" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanSubqueueTask" %>
<%@ page import="com.sib.ibanklosucl.model.doc.VehicleLoanChargeWaiver" %>
<%@ page import="java.util.Optional" %>
<%@ page import="com.sib.ibanklosucl.dto.subqueue.LockStatusDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.WaiverAccessDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<style>
	.kt .modal-xl {
		--bs-modal-width: 1300px !important;
	}
</style>
<script>
    $(document).ready(function () {
        $(':input[readonly]').css({'background-color': '#f6f6f6'});

        function isReqFee() {
            return $('[name="feewaiveRequired"]:checked').val() == 'Y';

        }
        $("#hisChgviewBtn").on('click', function (e) {
    var slno = $('#slno').val();
    var waiverType = "CHARGE"; // Assuming you want to fetch Charge Fee Waiver history
    e.preventDefault();
    $.ajax({
        url: 'doc/waiver-history',
        type: 'POST',
        data: {slno: slno, waiverType: waiverType},
        success: function (response) {
            var tableHeader = $('#chgwaiverHistoryTableHeader');
            var tableBody = $('#chgwaiverHistoryTableBody');
            tableHeader.empty();
            tableBody.empty();

            var headerRow = $('<tr>');
            if (waiverType === 'ROI') {
                headerRow.append('<th>Decision</th><th>Requested Spread</th><th>Sanctioned Spread</th><th>Revised ROI</th><th>Revised EMI</th><th>Last Modified</th><th>Modified By</th>');
            } else if (waiverType === 'CHARGE') {
                headerRow.append('<th>Decision</th><th>Last Modified</th><th>Modified By</th>');
            }
            tableHeader.append(headerRow);

            // Group waivers by taskId
            var groupedWaivers = {};
            response.forEach(function (waiver) {
                if (!groupedWaivers[waiver.taskId]) {
                    groupedWaivers[waiver.taskId] = [];
                }
                groupedWaivers[waiver.taskId].push(waiver);
            });

            // Iterate over each group (taskId)
            for (var taskId in groupedWaivers) {
                if (groupedWaivers.hasOwnProperty(taskId)) {
                    var waivers = groupedWaivers[taskId];
                    var mainWaiver = waivers[0]; // Assuming the first entry represents the main details

                    var row = $('<tr onclick="toggleExpand(this)">');
                    if (waiverType === 'ROI') {
                        row.append($('<td>').text(mainWaiver.decision));
                        row.append($('<td>').text(mainWaiver.requestedSpread + '%'));
                        row.append($('<td>').text(mainWaiver.sanctionedSpread + '%'));
                        row.append($('<td>').text(mainWaiver.revisedRoi + '%'));
                        row.append($('<td>').text('₹' + mainWaiver.revisedEmi.toFixed(2)));
                        row.append($('<td>').text(new Date(mainWaiver.lastModDate).toLocaleString()));
                        row.append($('<td>').text(mainWaiver.lastModUser));
                    } else if (waiverType === 'CHARGE') {
                        row.append($('<td>').text(mainWaiver.decision));
                        row.append($('<td>').text(new Date(mainWaiver.lastModDate).toLocaleString()));
                        row.append($('<td>').text(mainWaiver.lastModUser));
                    }
                    tableBody.append(row);

                    // Create an expandable row with detailed information for all waivers under the same taskId
                    var expandableRow = $('<tr class="expandable-content">');
                    var expandableCell = $('<td colspan="12">');
                    var cardBody = $('<div class="card card-body">');

                    waivers.forEach(function (waiver) {
                        var detailsRow = $('<div class="row">');
                        if (waiverType === 'ROI') {
                            detailsRow.append('<div class="col-md-4"><p><i class="fas fa-user-tie details-icon"></i><strong>Decision:</strong> ' + waiver.decision + '</p><p><i class="fas fa-calendar-alt details-icon"></i><strong>Modified Date:</strong> ' + new Date(waiver.lastModDate).toLocaleString() + '</p></div>');
                            detailsRow.append('<div class="col-md-4"><p><i class="fas fa-percentage details-icon"></i><strong>Requested Spread:</strong> ' + waiver.requestedSpread + '%</p><p><i class="fas fa-percentage details-icon"></i><strong>Sanctioned Spread:</strong> ' + waiver.sanctionedSpread + '%</p></div>');
                            detailsRow.append('<div class="col-md-4"><p><i class="fas fa-money-bill-wave details-icon"></i><strong>Revised ROI:</strong> ' + waiver.revisedRoi + '%</p><p><i class="fas fa-money-bill-wave details-icon"></i><strong>Revised EMI:</strong> ₹' + waiver.revisedEmi.toFixed(2) + '</p></div>');
                        } else if (waiverType === 'CHARGE') {
                            // detailsRow.append('<div class="col-sm-4"><p><i class="fas fa-file-alt details-icon"></i><strong>Fee Name:</strong> ' + waiver.feeName + '</p></div>');
							// detailsRow.append('<div class="col-sm-2"><p><i class="fas fa-comment-alt details-icon"></i><strong>Original Fee:</strong> ₹' + waiver.feeValue.toFixed(2) + '</p></div>');
                            // detailsRow.append('<div class="col-sm-2"><p><i class="fas fa-comment-alt details-icon"></i><strong>Recommended Fee:</strong> ₹' + waiver.feeValueRec.toFixed(2) + '</p></div>');
                            // detailsRow.append('<div class="col-sm-2"><p><i class="fas fa-comment-alt details-icon"></i><strong>Sanctioned Fee:</strong> ₹' + waiver.feeSancValue.toFixed(2) + '</p></div>');
                            // detailsRow.append('<div class="col-md-3"><p><i class="fas fa-comment-alt details-icon"></i><strong>Remarks:</strong> ' + waiver.remarks + '</p></div>');
                            // detailsRow.append('<div class="col-md-4"><p><i class="fas fa-comment-alt details-icon"></i><strong>Sanction Remarks:</strong> ' + waiver.sanctionRemarks + '</p></div>');

							detailsRow.append('<div class="col-sm-2"><div class="d-flex justify-content-start flex-column"><span class="text-muted fw-semibold text-muted d-block fs-9">Fee Name</span><div href="#" class="text-dark fw-bold fs-9">'+  waiver.feeName +'</div> </div></div>');
							detailsRow.append('<div class="col-sm-2"><div class="d-flex justify-content-start flex-column"><span class="text-muted fw-semibold text-muted d-block fs-9">Original Fee</span><div href="#" class="text-dark fw-bold fs-9">'+  waiver.feeValue.toFixed(2) +'</div> </div></div>');
							detailsRow.append('<div class="col-sm-2"><div class="d-flex justify-content-start flex-column"><span class="text-muted fw-semibold text-muted d-block fs-9">Recommended Fee</span><div href="#" class="text-dark fw-bold fs-9">'+  waiver.feeValueRec.toFixed(2) +'</div> </div></div>');
							detailsRow.append('<div class="col-sm-2"><div class="d-flex justify-content-start flex-column"><span class="text-muted fw-semibold text-muted d-block fs-9">Sanctioned Fee</span><div href="#" class="text-dark fw-bold fs-9">'+ waiver.feeSancValue.toFixed(2) +'</div> </div></div>');
							detailsRow.append('<div class="col-sm-2 "><div class="d-flex justify-content-start flex-column"><span class="text-muted fw-semibold text-muted d-block fs-9">Remarks</span><div href="#" class="text-dark fw-bold fs-9">'+  waiver.remarks +'</div> </div></div>');
							detailsRow.append('<div class="col-sm-2 "><div class="d-flex justify-content-start flex-column"><span class="text-muted fw-semibold text-muted d-block fs-9">Sanction Remarks</span><div href="#" class="text-dark fw-bold fs-9">'+  waiver.sanctionRemarks +'</div> </div></div>');
							detailsRow.append('<div class="separator separator-dashed separator-content border-gray my-3"><i class="ki-duotone ki-chart-pie-4 fs-9 text-success me-2"> <span class="path1"></span><span class="path2"></span>	<span class="path3"></span><span class="path4"></span></i></div>');

						}
                        cardBody.append(detailsRow);
                    });

                    expandableCell.append(cardBody);
                    expandableRow.append(expandableCell);
                    tableBody.append(expandableRow);
                }
            }

            $('#chgwaiverHistoryModalLabel').text(waiverType + ' Waiver History');
            $('#chgwaiverHistoryModal').modal('show');
        },
        error: function (xhr, status, error) {
            console.error('Error fetching waiver history:', error);
            alert('Failed to fetch waiver history. Please try again.');
        }
    });
});

        $("#hisChgviewBtns").on('click', function (e) {
    var slno = $('#slno').val();
    var waiverType = "CHARGE"; // Change this to "CHARGE" if you want to fetch Charge Fee Waiver history
    e.preventDefault();
    $.ajax({
        url: 'doc/waiver-history',
        type: 'POST',
        data: {slno: slno, waiverType: waiverType},
        success: function (response) {
            var tableHeader = $('#chgwaiverHistoryTableHeader');
            var tableBody = $('#chgwaiverHistoryTableBody');
            tableHeader.empty();
            tableBody.empty();

            var headerRow = $('<tr>');
            if (waiverType === 'ROI') {
                headerRow.append('<th>Decision</th><th>Requested Spread</th><th>Sanctioned Spread</th><th>Revised ROI</th><th>Revised EMI</th><th>Last Modified</th><th>Modified By</th>');
            } else if (waiverType === 'CHARGE') {
                headerRow.append('<th>Decision</th><th>Last Modified</th><th>Modified By</th>');
            }
            tableHeader.append(headerRow);

            response.forEach(function (waiver, index) {
                var row = $('<tr onclick="toggleExpand(this)">');
                if (waiverType === 'ROI') {
                    row.append($('<td>').text(waiver.decision));
                    row.append($('<td>').text(waiver.requestedSpread + '%'));
                    row.append($('<td>').text(waiver.sanctionedSpread + '%'));
                    row.append($('<td>').text(waiver.revisedRoi + '%'));
                    row.append($('<td>').text('₹' + waiver.revisedEmi.toFixed(2)));
                    row.append($('<td>').text(new Date(waiver.lastModDate).toLocaleString()));
                    row.append($('<td>').text(waiver.lastModUser));
                } else if (waiverType === 'CHARGE') {
                    row.append($('<td>').text(waiver.decision));
                    row.append($('<td>').text(new Date(waiver.lastModDate).toLocaleString()));
                    row.append($('<td>').text(waiver.lastModUser));
                }
                tableBody.append(row);

                // Create an expandable row with detailed information
                var expandableRow = $('<tr class="expandable-content">');
                var expandableCell = $('<td colspan="12">');
                var cardBody = $('<div class="card card-body">');

                var detailsRow = $('<div class="row">');
                if (waiverType === 'ROI') {
                    detailsRow.append('<div class="col-md-4"><p><i class="fas fa-user-tie details-icon"></i><strong>Decision:</strong> ' + waiver.decision + '</p><p><i class="fas fa-calendar-alt details-icon"></i><strong>Modified Date:</strong> ' + new Date(waiver.lastModDate).toLocaleString() + '</p></div>');
                    detailsRow.append('<div class="col-md-4"><p><i class="fas fa-percentage details-icon"></i><strong>Requested Spread:</strong> ' + waiver.requestedSpread + '%</p><p><i class="fas fa-percentage details-icon"></i><strong>Sanctioned Spread:</strong> ' + waiver.sanctionedSpread + '%</p></div>');
                    detailsRow.append('<div class="col-md-4"><p><i class="fas fa-money-bill-wave details-icon"></i><strong>Revised ROI:</strong> ' + waiver.revisedRoi + '%</p><p><i class="fas fa-money-bill-wave details-icon"></i><strong>Revised EMI:</strong> ₹' + waiver.revisedEmi.toFixed(2) + '</p></div>');
                } else if (waiverType === 'CHARGE') {
                    detailsRow.append('<div class="col-md-4"><p><i class="fas fa-file-alt details-icon"></i><strong>Fee Code:</strong> ' + waiver.feeCode + '</p><p><i class="fas fa-file-alt details-icon"></i><strong>Fee Name:</strong> ' + waiver.feeName + '</p></div>');
                    detailsRow.append('<div class="col-md-4"><p><i class="fas fa-comment-alt details-icon"></i><strong>Remarks:</strong> ' + waiver.remarks + '</p></div>');
                    detailsRow.append('<div class="col-md-4"><p><i class="fas fa-comment-alt details-icon"></i><strong>Sanction Remarks:</strong> ' + waiver.sanctionRemarks + '</p></div>');
                }

                cardBody.append(detailsRow);
                expandableCell.append(cardBody);
                expandableRow.append(expandableCell);
                tableBody.append(expandableRow);
            });

            $('#chgwaiverHistoryModalLabel').text(waiverType + ' Waiver History');
            $('#chgwaiverHistoryModal').modal('show');
        },
        error: function (xhr, status, error) {
            console.error('Error fetching waiver history:', error);
            alert('Failed to fetch waiver history. Please try again.');
        }
    });
});
        $("#hisChgviewBtn3").on('click', function (e) {
            var slno = $('#slno').val();
            var waiverType = "CHARGE";
            e.preventDefault();
            $.ajax({
                url: 'doc/waiver-history',
                type: 'POST',
                data: {slno: slno, waiverType: waiverType},
                success: function (response) {
                    var tableBody = $('#chgwaiverHistoryTableBody');
                    tableBody.empty();

                    var headerRow = $('<tr>');
                    if (waiverType === 'ROI') {
                        headerRow.append('<th>Decision</th><th>Requested Spread</th><th>Sanctioned Spread</th><th>Revised ROI</th><th>Revised EMI</th><th>Remarks</th><th>Sanction Remarks</th><th>Last Modified</th><th>Modified By</th>');
                    } else if (waiverType === 'CHARGE') {
                        headerRow.append('<th>Decision</th><th>Fee Code</th><th>Fee Name</th><th>Original Fee</th><th>Recommended Fee</th><th>Sanctioned Fee</th><th>Final Fee</th><th>Waiver Flag</th><th>Remarks</th><th>Sanction Remarks</th><th>Last Modified</th><th>Modified By</th>');
                    }
                    tableBody.append(headerRow);

                    response.forEach(function (waiver) {
                        var row = $('<tr>');
                        if (waiverType === 'ROI') {
                            row.append($('<td>').text(waiver.decision));
                            row.append($('<td>').text(waiver.requestedSpread + '%'));
                            row.append($('<td>').text(waiver.sanctionedSpread + '%'));
                            row.append($('<td>').text(waiver.revisedRoi + '%'));
                            row.append($('<td>').text('₹' + waiver.revisedEmi.toFixed(2)));
                            row.append($('<td>').text(waiver.remarks));
                            row.append($('<td>').text(waiver.sanctionRemarks));
                            row.append($('<td>').text(new Date(waiver.lastModDate).toLocaleString()));
                            row.append($('<td>').text(waiver.lastModUser));
                        } else if (waiverType === 'CHARGE') {
                            row.append($('<td>').text(waiver.decision));
                            row.append($('<td>').text(waiver.feeCode));
                            row.append($('<td>').text(waiver.feeName));
                            row.append($('<td>').text('₹' + (waiver.feeValue != null ? waiver.feeValue.toFixed(2) : '0.00')));
							row.append($('<td>').text('₹' + (waiver.feeValueRec != null ? waiver.feeValueRec.toFixed(2) : '0.00')));
							row.append($('<td>').text('₹' + (waiver.feeSancValue != null ? waiver.feeSancValue.toFixed(2) : '0.00')));
	                        row.append($('<td>').text('₹' + (waiver.finalFee != null ? waiver.finalFee.toFixed(2) : '0.00')));
                            row.append($('<td>').text(waiver.waiverFlag));
                            row.append($('<td>').text(waiver.remarks));
                            row.append($('<td>').text(waiver.sanctionRemarks));
                            row.append($('<td>').text(new Date(waiver.lastModDate).toLocaleString()));
                            row.append($('<td>').text(waiver.lastModUser));
                        }
                        tableBody.append(row);
                    });

                    $('#chgwaiverHistoryModalLabel').text(waiverType + ' Waiver History');
                    $('#chgwaiverHistoryModal').modal('show');
                },
                error: function (xhr, status, error) {
                    console.error('Error fetching waiver history:', error);
                    alert('Failed to fetch waiver history. Please try again.');
                }
            });
        });

        $("#feeDetailsForm").validate({
            rules: {
                'feeValueSanc': {
                    required: function (element) {
                        console.log('Element:', element);
                        console.log('Data waiver:', $(element).data('waiver'));
                        console.log('Attr waiver:', $(element).attr('data-waiver'));
                        console.log('Hidden waiver flag:', $(element).closest('tr').find('input[name="feeWaiverFlag"]').val());
                        const waiverFromAttr = $(element).attr('data-waiver') === 'Y';
                        const waiverFromHidden = $(element).closest('tr').find('input[name="feeWaiverFlag"]').val() === 'Y';
                        console.log('Waiver from attr:', waiverFromAttr);
                        console.log('Waiver from hidden:', waiverFromHidden);
                        const isRejected = $("#feedecision").val()==="REJECT";
                         console.log('Decision is rejected:', isRejected);
                        return waiverFromHidden && isRejected; // or use waiverFromAttr based on what logs show
                    },
                    number: true,
                    min: 0
                },
                feeWaiverSancRemarks: {
                    required: true,
                    maxlength: 150
                },
                feedecision: {
                    required: true
                }
            },
            messages: {
                feeValueSanc: {
	                required: "The sanctioned amount is mandatory for waiver items.",
                    number: "Please enter a valid number.",
                    min: "The recommended amount should be greater than or equal to 0."
                } ,
                feeWaiverSancRemarks: {
                    required: "Remarks are mandatory when rejecting the request",
                    maxlength: "Remarks cannot exceed 150 characters"
                }
            },
            ignore: 'input[type=hidden], .select2-search__field',
            highlight: function (element, errorClass) {
                $(element).removeClass('is-invalid').removeClass('is-valid').removeClass('validation-invalid-label');
            },
            unhighlight: function (element, errorClass) {
                $(element).removeClass('validation-invalid-label').removeClass('is-invalid').removeClass('is-valid');
            },
            errorPlacement: function (error, element) {
                element.removeClass('is-valid').addClass('is-invalid');
                error.css({
                    'padding-left': '23px',
                    'margin-right': '20px',
                    'padding-bottom': '2px',
                    'color': 'red',
                    'font-size': 'small'
                });
                error.addClass("validation-invalid-label");
                console.log(element);
                if (element.hasClass('checkbox-input')) {
                    element.parent().parent().parent().append(error);
                } else {
                    error.insertAfter(element);
                }
            }
        });

        // function isValid() {
		//
        //     let isValid = true;
        //     if (isReqFee()) {
        //         // Loop through each recommended field
        //         $("input[name='feeValueSanc']").each(function () {
        //             let recommendedValue = parseFloat($(this).val());
        //             let amountValue = parseFloat($(this).closest('tr').find("input[name='feeValue']").val());
		//
        //             // Check if the recommended value is a valid number, greater than or equal to 0, and not greater than the amount
        //             if (isNaN(recommendedValue) || recommendedValue < 0 || recommendedValue > amountValue) {
        //                 isValid = false;
		//
        //                 // Display an error message (can be customized)
        //                 $(this).next('.error-message').text("Recommended amount must be between 0 and the specified amount.");
        //             } else {
        //                 // Clear any previous error message
        //                 $(this).next('.error-message').text("");
        //             }
        //         });
        //     }
        //     return isValid;
        // }
        function isValid() {
            let isValid = true;
            if (isReqFee()) {
                // Loop through each sanctioned field
                $("input[name='feeValueSanc']").each(function () {
                    // Only validate if this is a waiver item
                    if ($(this).data('waiver') === 'Y') {
                        let sanctionedValue = parseFloat($(this).val());
                        let amountValue = parseFloat($(this).closest('tr').find("input[name='feeValue']").val());

                        if (isNaN(sanctionedValue) || sanctionedValue < 0 || sanctionedValue > amountValue) {
                            isValid = false;
                            $(this).next('.error-message').text("Sanctioned amount must be between 0 and the specified amount.");
                        } else {
                            $(this).next('.error-message').text("");
                        }
                    }
                });
            }
            return isValid;
        }




        // Handle save button click
        $("#feesaveBtn").on('click', function (e) {
            e.preventDefault();
             var showMsg="";
             var decision= $("#feedecision").val();
            if (isValid() && $("#feeDetailsForm").valid()) {
                showLoader();
                // Initialize an array to hold all fee objects
                let dataList = [];
                // Loop through each row in your table or list
                $("#feeDetailsForm table tbody tr").each(function () {
                    let data = {
                        feeCode: $(this).find(".feeCode").val(),
                        feeName: $(this).find(".feeName").val(),
                        feeValue: $(this).find(".feeValue").val(),
                        feeValueSanc: $(this).find(".feeValueSanc").val(),
                        feeWaiverFlag: $(this).find(".feeWaiverFlag").val()
                    };
                    dataList.push(data); // Add each row's data to the list
                });
                var dto = {
                    wiSlno: $('#slno').val(),
                    waiverType: "CHARGE",
                    processFeeWaiverDto: {
                        decision: $("#feedecision").val(),
                        feewaiveRequired: $('[name="feewaiveRequired"]:checked').val(),
                        slno: $('#slno').val(),
                        wiNum: $('#winum').val(),
                        feeWaiverSancRemarks: $('#feeWaiverSancRemarks').val(),
                        feeData: dataList
                    }
                };
                $.ajax({
                    url: 'doc/updateWaiverChecker', // Update with your API endpoint
                    type: 'POST',
                    data: JSON.stringify(dto),
                    contentType: 'application/json',
                    success: function (response) {
                        hideLoader();
                        if(decision.startsWith("RI")) {
                            showMsg="Charge Waiver request forwarded";
                        } else if (decision==="SANCTION") {
                            showMsg="Charge Waiver request sanctioned";
                        } else if (decision==="REJECT") {
							showMsg="Charge Waiver request rejected";
                        }
                        if (response.status === 'S') {
	                        $('#feesaveBtn').hide();
                            notyalt(showMsg);
                            updateCheckerAccordionStyle("processFeeLink",true);
                            checkFeeWaiverLocksAndRedirect(dto.processFeeWaiverDto.wiNum);
                        } else {
                            alertmsg('Failed: ' + response.msg);
                        }
                    },
                    error: function (xhr, status, error) {
                        hideLoader();
                        var err_data = xhr.responseJSON;
                        if (err_data.msg) {
                            alertmsgvert(err_data.msg);
                        } else {
                            alertmsg('An error occurred: ' + error);
                        }
                    }
                });
            }
        });


        $('input[type=radio][name=feewaiveRequired]').change(function () {
            if (this.value == 'Y') {
                $('.feewaiveRequired').removeClass('hide');
            } else if (this.value == 'N') {
                $('.feewaiveRequired').addClass('hide');
            }
        });

        function checkFeeWaiverLocksAndRedirect(wiNum) {
            var emp_ppcno = $('#emp_ppcno').val();
            $.ajax({
                url: 'doc/checkWaiverLocks', // Endpoint to check the waiver locks
                type: 'GET',
                data: {wiNum: wiNum},
                success: function (lockStatus) {
                    // Check if the current user has locks on both ROI_WAIVER and CHARGE_WAIVER
                    if (lockStatus.roiLockedBy === emp_ppcno || lockStatus.chargeLockedBy === emp_ppcno) {
                        if (lockStatus.roiLocked || lockStatus.chargeLocked) {
                            notyalt('Record Saved Successfully');
                        } else {
                            window.location.href = 'waiverlist'; // Redirect to the waiver list
                        }
                    } else {
                        window.location.href = 'waiverlist'; // Redirect to the waiver list
                    }
                },
                error: function (xhr, status, error) {
                    alertmsg('Error checking waiver locks: ' + error);
                }
            });
        }


    });
    function toggleExpand(row) {
            var nextRow = row.nextElementSibling;
            if (nextRow && nextRow.classList.contains('expandable-content')) {
                nextRow.style.display = nextRow.style.display === 'table-row' ? 'none' : 'table-row';
            }
        }


</script>

<div class="flex-stack border rounded sancApprove hide px-7 py-3 mb-2">
	<div class="w-100">
		<div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="processFeeLink" data-bs-target="#processFeeContent">
			<label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
			       for="processFeeContent">
				<i class="ki-duotone ki-element-9 fs-3x me-4">
					<span class="path1"></span>
					<span class="path2"></span>
				</i>
				<span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-4">Processing Fee Waiver</span>
                    <span class="text-muted fw-semibold fs-7">
                     Send WI for Processing Fee Waiver
                    </span>
                </span>
			</label>
		</div>
		<div id="processFeeContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">
			<%
				Employee userdt = (Employee) request.getAttribute("userdata");
				VehicleLoanMaster master = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
				LockStatusDTO lockStatus = (LockStatusDTO) request.getAttribute("lockStatus");
				WaiverAccessDTO userAccess = (WaiverAccessDTO) request.getAttribute("userAccess");
				VehicleLoanSubqueueTask latestTask = (VehicleLoanSubqueueTask) request.getAttribute("latestTask");

				boolean chgWaiverLocked = false;
				EligibilityDetails eligibilityDetails = (EligibilityDetails) request.getAttribute("eligibilityDetails");
				List<WaiverDto.ProcessFeeWaiverDto> feeMast = (List<WaiverDto.ProcessFeeWaiverDto>) request.getAttribute("processFeeMast");
				String Processfee;
				String hideClass = master.getChargeWaiverRequested() != null && master.getChargeWaiverRequested() ? "" : "hide";
				String taskStatus = "", enableClass = "", taskDecision = "", feeWaiverSancRemarks = "";
				List<VehicleLoanSubqueueTask> subqueueTask = (List<VehicleLoanSubqueueTask>) request.getAttribute("subQueueData");
				if (subqueueTask != null) {
					Optional<VehicleLoanSubqueueTask> chg_ = subqueueTask.stream().filter(t -> t.getTaskType().equalsIgnoreCase("CHARGE_WAIVER")).findFirst();
					if (chg_.isPresent()) {
						VehicleLoanSubqueueTask task = chg_.get();
						taskStatus = task.getStatus();
						taskDecision = task.getDecision();
//					} else {
//                        if(latestTask!=null && latestTask.getTaskType().equals("CHARGE_WAIVER")) {
//                            taskStatus = latestTask.getStatus();
//                            taskDecision = latestTask.getDecision();
//                        }
//
//                        System.out.println("chg is not present"+taskStatus+"---"+taskDecision+"-----"+latestTask.getTaskType());
//					}
//					} else {
//						System.out.println("subqueue is empty");
//					}
						if (!"COMPLETED".equals(taskStatus)) {
							enableClass = "";
						} else {
							enableClass = "disabled";
						}
						if (lockStatus != null && lockStatus.isAnyLocked()) {
							if (userAccess.isHasChargeAccess() && lockStatus.isChargeLocked()) {
								if (!lockStatus.getChargeLockedBy().equals(userdt.getPpcno())) {
									enableClass = "disabled";
									chgWaiverLocked = true;
								}
							} else if (!userAccess.isHasChargeAccess()) {
								enableClass = "disabled";
							}
						} else if (!userAccess.isHasChargeAccess()) {
							enableClass = "disabled";
						}

			%>
			<div class="row">
				<div class="col-sm-12">
					<!--begin::Repeater-->
					<div id="kt_docs_repeater_basic">
						<form id="feeDetailsForm" name="feeDetailsForm" method="POST">
							<input type="hidden" name="emp_ppcno" id="emp_ppcno" value="<%=userdt.getPpcno()%>"/>
							<div class="">
								<%--                      --%>
								<div class="col-sm-12 mb-3">
									<table class="table table-sm table-bordered table-hover">
										<thead>
											<tr>
												<th>Description<%=userAccess.isHasChargeAccess()%>
												</th>
												<th>Amount</th>
												<th class="feewaiveRequired <%=hideClass%>">Recommended</th>
												<th class=" feewaiveRequired <%=hideClass%>">Sanctioned</th>
											</tr>
										</thead>
										<tbody>
											<%

												List<Map<String, Object>> feeLevels = (List<Map<String, Object>>) request.getAttribute("feeLevels");
												String feedecision = "", feeRemarks = "";
												for (WaiverDto.ProcessFeeWaiverDto fee : feeMast) {
													feeRemarks = fee.getFeewaiverRemarks() == null ? "" : fee.getFeewaiverRemarks();
													feedecision = fee.getDecision() == null ? "" : fee.getDecision();

													Processfee = "0";
                                                    Processfee=fee.getFeeValue();



													String feeValueRec = fee.getFeeWaive() == null ? "" : fee.getFeeWaive();
													String feeSancValueRec = fee.getFeeSancValue() == null ? "" : fee.getFeeSancValue();
													if (fee.getWaiver().equals("N")) {
														feeValueRec = Processfee.toString();
													}

											%>
											<tr>
												<td>
													<input type="hidden" class="form-control feeCode" id="feeCode" name="feeCode" required value="<%=fee.getChargeCode()%>"
													       readonly/>
													<input type="hidden" class="form-control feeWaiverFlag" id="feeWaiverFlag" name="feeWaiverFlag" required
													       value="<%=fee.getWaiver()%>" readonly/>
													<input type="hidden" class="form-control feeName" id="feeName" name="feeName" required value="<%=fee.getChargeName()%>"
													       readonly/>
													<%=fee.getChargeName()%>
												</td>
												<td><input type="hidden" class="form-control feeValue" id="feeValue" name="feeValue" <%=enableClass%> required
												           value="<%=Processfee%>" readonly/>
													<%=Processfee%>
												</td>

												<td class="feewaiveRequired <%=hideClass%>">
													<input type="text" class="form-control feeValueRec" id="feeValueRec" <%=enableClass%> name="feeValueRec" required
													       value="<%=feeValueRec%>"
													       readonly/>
													<div class="error-message" style="color: red;"></div>
												</td>
												<td class=" feewaiveRequired <%=hideClass%>">
													<input type="text" class="form-control feeValueSanc" data-waiver="<%=fee.getWaiver()%>" <%=enableClass%> id="feeValueSanc" name="feeValueSanc" required
													       value="<%=feeSancValueRec%>" <%=fee.getWaiver().equals("N") ? "readonly" : ""%> />
												</td>
											</tr>
											<%
												}
											%>

										</tbody>
									</table>
								</div>
								<div class="mb-3 feewaiveRequired <%=hideClass%>">
									<label for="feedecision" class="form-label">Decision:</label>
									<select type="text" id="feedecision" name="feedecision" required value="<%=feedecision%>" <%=enableClass%> class="form-select" required>
										<option value="" selected>Select Decision</option>
										<%

											for (Map<String, Object> level : feeLevels) {
												String level_val = (String) level.get("LEVEL_NAME");
												String level_desc = (String) level.get("DISPLAY_NAME");
										%>
										<option value="<%= level_val %>" <%= level_val.equals(taskDecision) ? "selected" : "" %>><%= level_desc %>
										</option>
										<%
											}

										%>
										<option value="SANCTION" <%= "SANCTION".equals(taskDecision) ? "selected" : "" %>>Sanctioned
										<option value="REJECT" <%= "REJECT".equals(taskDecision) ? "selected" : "" %>>Rejected
									</select>
								</div>
								<div class="mb-3 feewaiveRequired">
									<label for="feewaiverRemarks" class="form-label">Remarks:</label>
									<textarea type="text" id="feewaiverRemarks" name="feewaiverRemarks" disabled class="form-control" required><%=feeRemarks%></textarea>
								</div>
								<div class="mb-3 feewaiveRequired">
									<label for="feewaiverRemarks" class="form-label">Remarks:</label>
									<textarea type="text" <%=enableClass%> id="feeWaiverSancRemarks" name="feeWaiverSancRemarks" class="form-control"
									          required><%=feeWaiverSancRemarks%></textarea>
								</div>
							</div>
						</form>
						<div class="text-end pt-5">
							<%if (!"COMPLETED".equals(taskStatus) && !chgWaiverLocked && userAccess.isHasChargeAccess()) {%>
							<button type="button" id="feesaveBtn" class="btn btn-sm btn-primary" data-kt-stepper-action="next">Save
								<i class="ki-duotone ki-double-right ">
									<i class="path1"></i>
									<i class="path2"></i>
								</i></button>
							<%} else if (chgWaiverLocked) {%>
							<div class="col-lg-10">
								<span class="badge bg-warning mt-2">Fee waiver in progress . Locked by - <%=lockStatus.getChargeLockedBy()%></span>
							</div>
							<%} else if ("COMPLETED".equals(taskStatus)) {%>
							<div class="col-lg-10">
								<span class="badge bg-success mt-2">Fee waiver sanctioned</span>
							</div>
							<%} else if (!userAccess.isHasChargeAccess()) {%>
							<div class="col-lg-10">
								<span class="badge bg-warning mt-2">Fee waiver privileges are not present for the user</span>
							</div>
							<%}%>


						</div>

					</div>
					<!--end::Repeater-->
				</div>
				 <a href="#" id="hisChgviewBtn" class="btn btn-lg btn-flex btn-link btn-color-warning">
                    Waiver History
                    <i class="ki-duotone ki-arrow-right ms-2 fs-3"><span class="path1"></span><span class="path2"></span></i>				</a>
			</div>
			<%} else {%>
			<div class="col-sm-12 mb-2 justify-content-center">
            <div class="bg-light bg-opacity-50 rounded-3 p-10 mx-md-5 h-md-100">

                <div class="d-flex flex-center w-60px h-60px rounded-3 bg-light-warning bg-opacity-90 mb-10">
                    <i class="ki-duotone ki-abstract-25 text-warning fs-3x"><span class="path1"></span><span class="path2"></span></i>                </div>

                <h4 class="mb-5">No Pending Fee waiver request found</h4>
                <a href="#" class="btn btn-lg btn-flex btn-link btn-color-warning">
                    Charge waiver History
                    <i class="ki-duotone ki-arrow-right ms-2 fs-3"><span class="path1"></span><span class="path2"></span></i>				</a>
            </div>
        </div>
			<%}}%>
		</div>
	</div>
</div>
 <div class="modal fade" id="chgwaiverHistoryModal" tabindex="-1" role="dialog" aria-labelledby="chgwaiverHistoryModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-xl" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="chgwaiverHistoryModalLabel">Waiver History</h5>
                 <div class="btn btn-icon btn-sm btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="ki-duotone ki-cross fs-1"><span class="path1"></span><span class="path2"></span></i>
                </div>
            </div>
            <div class="modal-body">
                <table class="table table-striped table-responsive">
                    <thead id="chgwaiverHistoryTableHeader">

                    </thead>
                    <tbody id="chgwaiverHistoryTableBody">
                        <!-- Data will be populated here -->
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
