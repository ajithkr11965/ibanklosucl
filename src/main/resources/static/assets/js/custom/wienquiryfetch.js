$(document).ready(function () {

    $('#remarks').hide();
    $('.backbtn').on('click', function (e) {
        $('#backbtn').click();
    });
    $('.crtcasubmit').on('click', function (e) {
        showLoader();
        var remarks =  $("#remarks").val().trim();
        var totalwarns = 0;// $("#totalwarnings").val();
        var flg = 0;
        if(totalwarns>0){
            hideLoader();
            alertmsg('CBS value and user entered value mismatch. Kindly check warnings section!');
        }else if(remarks=="") {
            hideLoader();
            alertmsg('Kindly Enter Remarks');
        }else{
            var queue="",rejflg="N",status="";
            var vehicleLoanMasterId = $('#slno').val();
            var winum = $('#winum').val();
            var decision = "";
            var action="";
            if($(this).attr('id')==="caapprovebtn"){
                decision="A";
                var declaration = "";// $("#");
                if(1==1){//if(+$('#totalwarnings').val()>0 && $("#consent").is(':checked')){
                    queue="XX";
                    status="CRT Approved";
                    action="approve";
                    flg = 1;
                }else{
                    hideLoader();
                    alertmsg('Kindly accept the declaration to continue!');
                }
            }else if($(this).attr('id')==="casendbackbtn"){
                decision="S";
                queue="BS";
                status="CRT Send Back";
                action="sendback";
                flg = 1;
            }else if($(this).attr('id')==="carejectbtn"){
                decision="R";
                queue="";
                rejflg="Y";
                status="CRT Reject";
                action="reject";
                flg = 1;
            }
            if(flg==1){
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


                        //var responsemsg= updateResponse.split("~");
                        var responsemsg= "abc";
                        if (updateResponse.status === 'S') {//if(responsemsg[0]=="success") {

                            // If update succeeds, process the loan
                            blockerMsg(updateResponse.msg);
                            /*
                            Swal.fire({
                                title: 'Success',
                                text: 'CRT Successfully submitted',
                                icon: 'success',
                                confirmButtonText: 'OK'
                            }).then((result) => {
                                if (result.isConfirmed) {
                                    $('#backbtn').click();
                                }
                            });*/
                        }else{
                            alertmsg(updateResponse.msg);
                            /*Swal.fire({
                                title: 'Error',
                                text: updateResponse,
                                icon: 'error',
                                confirmButtonText: 'OK'
                            }).then((result) => {

                            });*/
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