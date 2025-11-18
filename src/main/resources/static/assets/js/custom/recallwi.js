$(document).ready(function () {
    $('#recallbtn').click(function () {
        confirmmsg("This WI will get recalled back to RBCPC maker queue. Do you want to proceed?").then(function (confirmed) {
            if (confirmed) {
                showLoader();
                var jsonBody = {
                    winum: $('#winum').val(),
                    remarks : $('#remarks').val()
                };
                $.ajax({
                    url: 'api/recallwisave',
                    type: 'POST',
                    //data: JSON.stringify(jsonBody),
                    data: {
                        winum: $('#winum').val(),
                        remarks : $('#remarks').val()
                    },
                    async: false,
                    //contentType: 'application/json',
                    success: function (response) {
                        hideLoader();
                        alertmsg(response.msg);
                        $('#winum').val('');
                        $('#remarks').val('');
                    },
                    error: function (error) {
                        hideLoader();
                        alertmsg("Error, please check the WI number");
                    }
                });
            }
        });
    });


    $('#wienqbtn').click(function () {

        var winum = $('#winum').val();
        if (winum == '') {
            alertmsg("WI number is required");
        }else{
        showLoader();
        var jsonBody = {
            winum: winum,
            remarks: $('#remarks').val()
        };
        $.ajax({
            url: 'api/wienquiryfetch',
            type: 'GET',
            //data: JSON.stringify(jsonBody),
            data: {
                winum: $('#winum').val()
            },
            async: false,
            //contentType: 'application/json',
            success: function (response) {
                hideLoader();
                if(response.status=='S'){
                    var slno=response.msg;
                    if(slno =='' || slno == undefined){
                        alertmsg("Something went wrong");
                    }else{
                        alertmsg("success,submit form->"+response.msg+",slno="+slno);
                        $('#slno').val(slno);
                        $('#wienquiryfrm').submit();
                    }

                }else{
                    alertmsg(response.msg);
                    $('#winum').val('');
                }

            },
            error: function (error) {
                hideLoader();
                alertmsg("Error, please check the WI number");
            }
        });
    }

    });

});

var swalInit = swal.mixin({
    buttonsStyling: false,
    confirmButtonClass: 'btn bg-danger text-white',
    cancelButtonClass: 'btn btn-light'
});
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
