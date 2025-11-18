$(document).ready(function () {
    $("#consent").prop( "disabled", false );
    $('.crtcasubmit').on('click', function (e) {

        var remarks =  $("#remarks").val().trim();
        var totalwarns_highmedium = +$("#totalwarnings").val();
        var counter=+$("#counter").val();
        var flg = 0;
        if(remarks=="") {

            alertmsg('Kindly Enter Remarks');
        }else{
            var vehicleLoanMasterId = $('#slno').val();
            var winum = $('#winum').val();
            var decision = "";
            if($(this).attr('id')==="caapprovebtn"){
                decision="A";
                /*
                if(totalwarns>0){//ie high or medium points exist which are not corrected in cbs

                    alertmsg('CBS value and user entered value mismatch. Kindly check warnings section!');
                }else if(counter>0){//ie at least 1 row exists in warnings table
                    if($("#consent").is(':checked')){
                        flg = 1;
                    }else{

                        alertmsg('Kindly accept the declaration to continue!');
                    }
                }
                */

                if(totalwarns_highmedium>0){//ie high or medium points exist which are not corrected in cbs
                    alertmsg('High/Medium level warning exist, please rectify the same in order to proceed.!');
                }else{
                    if(counter>0){//overall total warnings
                        if($("#consent").is(':checked')){
                            flg = 1;
                            saveform(winum, vehicleLoanMasterId, remarks, decision);
                        }else{
                            alertmsg('Kindly accept the declaration in Warnings section to continue!');
                        }
                    }else{
                        flg = 1;
                        saveform(winum, vehicleLoanMasterId, remarks, decision);
                    }
                }
            }else if($(this).attr('id')==="casendbackbtn"){
                var msg= "Kindly note that, this WI will have to be sanctioned again if sent back to branch. Are you sure?";
                confirmmsg(msg).then(function (confirmed) {
                    if (confirmed) {
                        decision = "S";
                        flg = 1;
                        saveform(winum, vehicleLoanMasterId, remarks, decision);
                    }
                });
            }else if($(this).attr('id')==="carejectbtn"){
                var msg= "This WI is already sanctioned. Are you sure to permanently reject this?";
                confirmmsg(msg).then(function (confirmed) {
                    if (confirmed) {
                        decision = "R";
                        flg = 1;
                        saveform(winum, vehicleLoanMasterId, remarks, decision);
                    }
                });
            }
        }
    });

/*
    $('#caapprovebtn').on('click', function (e) {
        e.preventDefault();
        $('#cadecision').val('A');
        $('#wicrtamberform').submit();
    });
    $('#carejectbtn').on('click', function (e) {
        e.preventDefault();
        $('#cadecision').val('R');
        $('#wicrtamberform').submit();
    });
    $('#casendbackbtn').on('click', function (e) {
        e.preventDefault();
        $('#cadecision').val('S');
        $('#wicrtamberform').submit();
    });*/
    if ($('#lockflg').val() === 'Y') {
        $('.crtamberbtn').remove();
        alertmsg('This workitem is locked by PPC ' + $('#lockuser').val());
    }

    $('.backbtn').on('click', function (e) {
        $('#backbtn').click();
    });
});
function blockerMsg(Msg) {
    swalInit.fire({
        html:
            '<div class="d-inline-flex p-2 mb-3 mt-1">' +
            '<img src="assets/images/siblogo.png" class="h-48px" alt="logo">' +
            '</div>' +
            '<h5 class="card-title">' + Msg + '</h5>' +
            '</div>',
        showCloseButton: true
    }).then((result)=>{
        if(result.isDismissed || result.isConfirmed){
            $('#backbtn').click();
        }
    });
}

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

function saveform(winum, vehicleLoanMasterId, remarks, decision){

    showLoader();
    $.ajax({
        url: 'api/wicrtambersave',
        type: 'POST',
        data: {
            winum: winum,
            slno: vehicleLoanMasterId,
            remarks: remarks,
            declaration: "Y",
            decision:decision
        },
        success: function (updateResponse) {
            hideLoader();
            if (updateResponse.status === 'S') {

                // If update succeeds, process the loan
                blockerMsg(updateResponse.msg);
            }else{
                alertmsg(updateResponse.msg);
            }
        },
        error: function (xhr) {
            hideLoader();
            alertmsg("Something went wrong:"+xhr.msg);
        }
    });
}