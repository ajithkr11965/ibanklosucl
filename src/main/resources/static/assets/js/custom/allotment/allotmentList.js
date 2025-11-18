


function changecategory_topending(button)
{
    $('.pending_allot').show();
    $('.already_alloted').hide();

    $('.pending_btn').hide();
    $('.alloted_btn').show();

    $('#search_pending').show();
    $('#search_alloted').hide();

    $('#already_alloted_length').hide();
    $('#already_alloted_paginate').hide();

    $('#pending_allot_length').show();
    $('#pending_allot_padginate').show();



}

function changecategory_toalloted(button)
{
    $('.pending_allot').hide();
    $('.already_alloted').show();

    $('.pending_btn').show();
    $('.alloted_btn').hide();

    $('#search_pending').hide();
    $('#search_alloted').show();

    $('#already_alloted_length').show();
    $('#already_alloted_paginate').show();

    $('#pending_allot_length').hide();
    $('#pending_allot_padginate').hide();



}

function allotmentsave(button)
{
    var row = button.closest('tr');

    var slno = row.getAttribute('data-slno');

    var wiNum = $("#wiNum_"+slno).val();

    var allotedPpc = $("#allotedPpc_"+slno).val();
    if(allotedPpc==null)
    {
        allotedPpc="";
    }

    $('#doPpc').select2({
        dropdownParent: $('#modal_default'),
        templateResult: formatState,
        templateSelection: formatState
    });




    $("#modal_default").modal('show');
    $("#doPpc").val(allotedPpc);
    $("#remarks").val('');
    $("#modalsave").off("click").click(function()
    {   var doPpc = $("#doPpc").val();
            var remarks = $("#remarks").val();
            if(doPpc == null || doPpc == "" || remarks == null || remarks == ""  )
            {
                if(doPpc == null || doPpc == "")
                {
                    alertmsg("Select the Allotment PPC");
                }
                else if(remarks == null || remarks == "")
                {
                    alertmsg("Enter Remarks");
                }
            }
            else
            {
                var data = {
                    wiNum: wiNum,
                    slno: slno,
                    doPpc: doPpc,
                    remarks: remarks
                };
                console.log(JSON.stringify(data));
                $.ajax({
                    url: 'api/saveAllotment',
                    method: 'POST',
                    data: data,
                    success: function(response) {
                        if (response.status === 'S') {
                            alertmsg("Allotment PPC Saved Successfully")
                            $("#modal_default").modal('hide');
                            // $("#modal_default").on('hidden.bs.modal', function () {
                            //     location.reload();
                            // });
                        } else {
                            alertmsg('Failed: ' + response.msg);
                        }
                    },
                    error: function(error) {
                        alertmsg('An error occurred: ' + error);
                    }
                });
            }
        });



}


var swalInit = swal.mixin({
    buttonsStyling: false,
    confirmButtonClass: 'btn bg-danger text-white',
    cancelButtonClass: 'btn btn-light'
});

function alertmsg(Msg) {
    swalInit.fire({
        html:
            '<div class="d-inline-flex p-2 mb-3 mt-1">' +
            '<img src="assets/images/siblogo.png" class="h-48px" alt="logo">' +
            '</div>' +
            '<h5 class="card-title">' + Msg + '</h5>' +
            '</div>',
        showCloseButton: true
    }).then((result)=>{
            location.reload();
    });
}



