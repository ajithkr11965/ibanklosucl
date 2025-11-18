$(document).ready(function () {
    //addCoApplicantBtn.prop('disabled', true);

    $('#backbtnbog').on('click', function (e) {
        $('#backbtn').click();
    });

    //$('#loancheckerbody').on('click', '.runblacklistCheck', function (e) {
    $('.runblacklistCheck').on('click', function (e) {
        var applicantData = $(this).closest("tr").find(".applicantData");
        var applicantId = applicantData.data("applicantid");
        var buttonId="#kt_button_" + applicantId;
        var submitbtnId="#kt_cifbutton_" + applicantId;
        var button = document.querySelector(buttonId);
        button.setAttribute("data-kt-indicator", "on");
        $(buttonId).attr('disabled',true);
        $(submitbtnId).attr('disabled',true);
        event.preventDefault();
        var formData = {
            "request": {
                "CustName": applicantData.data("custname"),
                "Gender":  applicantData.data("gender"),
                "LastName": applicantData.data("lastname"),
                "Country":"IN"
            },
            "mock": false,
            "apiName": "partialBlackList",
            "workItemNumber": $('#winum').val(),
            "origin": applicantData.data("applicantid"),
            "slno": $('#slno').val()
        };

        $.ajax({
            type: "POST",
            url: "api/checker/checkPartialBlacklist",
            contentType: "application/json",
            data: JSON.stringify(formData),
            //async:false,
            success: function (response) {
                $("#blTable tr").remove();
                $('#alert_modal_bl').modal('show');
                if(response.statusCode=='406'){
                    var tbodyRef = document.getElementById('blTable').getElementsByTagName('tbody')[0];
                    var newRow = tbodyRef.insertRow();
                    var newCell;
                    var newText='';

                    newCell = newRow.insertCell();
                    newCell.colSpan=13;
                    newText = document.createTextNode('No records found');
                    newCell.appendChild(newText);
                }else{

                    var blarray=response.body;
                    var arrayLength = blarray.length;

                    for (var i = 0; i < arrayLength; i++) {
                        var bl = blarray[i];
                        var tbodyRef = document.getElementById('blTable').getElementsByTagName('tbody')[0];

// Insert a row at the end of table
                        var newRow = tbodyRef.insertRow();

// Insert a cell at the end of the row
                        var newCell;
                        var newText='';

                        newCell = newRow.insertCell();
                        newText = document.createTextNode(i+1);
                        newCell.appendChild(newText);

                        newCell = newRow.insertCell();
                        newText = document.createTextNode(bl.AGE);
                        newCell.appendChild(newText);

                        newText='';
                        newText = document.createTextNode(bl.ALIASES);
                        newCell = newRow.insertCell();
                        newCell.appendChild(newText);

                        newText='';
                        newText = document.createTextNode(bl.COMPANIES);
                        newCell = newRow.insertCell();
                        newCell.appendChild(newText);

                        newText='';
                        newText = document.createTextNode(bl.COUNTRIES);
                        newCell = newRow.insertCell();
                        newCell.appendChild(newText);

                        newText='';
                        newText = document.createTextNode(bl.DOB);
                        newCell = newRow.insertCell();
                        newCell.appendChild(newText);

                        newText='';
                        newText = document.createTextNode(bl.FIRSTNAME);
                        newCell = newRow.insertCell();
                        newCell.appendChild(newText);

                        newText='';
                        newText = document.createTextNode(bl.FURTHERINFORMATION);
                        newCell = newRow.insertCell();
                        newCell.appendChild(newText);

                        newText='';
                        newText = document.createTextNode(bl.LASTNAME);
                        newCell = newRow.insertCell();
                        newCell.appendChild(newText);

                        newText='';
                        newText = document.createTextNode(bl.LOCATIONS);
                        newCell = newRow.insertCell();
                        newCell.appendChild(newText);

                        newText='';
                        newText = document.createTextNode(bl.PASSPORTS);
                        newCell = newRow.insertCell();
                        newCell.appendChild(newText);

                        newText='';
                        newText = document.createTextNode(bl.PLACEOFBIRTH);
                        newCell = newRow.insertCell();
                        newCell.appendChild(newText);

                        newText='';
                        newText = document.createTextNode(bl.UID);
                        newCell = newRow.insertCell();
                        newCell.appendChild(newText);

                    }
                }
                var currentdate = new Date();
                var datetime = currentdate.getDate() + "/"
                    + (currentdate.getMonth()+1)  + "/"
                    + currentdate.getFullYear() + " @ "
                    + currentdate.getHours() + ":"
                    + currentdate.getMinutes() + ":"
                    + currentdate.getSeconds();
                //alert('datetime is:'+datetime);
                $('#rundate'+applicantId).text(datetime);
                $('.blstatus'+applicantId).text('Completed');
                $('.blstatus'+applicantId).removeClass('badge-light-warning').addClass('badge-light-success');
                button.removeAttribute("data-kt-indicator");$(buttonId).attr('disabled',false);
                $(submitbtnId).attr('disabled',false);
                //validateAllApplicants();
            },
            error: function (xhr, status, error) {
                $("#result").text("Error occurred: " + error);
                button.removeAttribute("data-kt-indicator");$(buttonId).attr('disabled',false);
                $(submitbtnId).attr('disabled',false);
            }
        });
    });



    $('.submitBtn').on('click', function (e) {

        var applicantData = $(this).closest("tr").find(".applicantData");
        var applicantId = applicantData.data("applicantid");
        var buttonid="#kt_cifbutton_" + applicantId;
        var button = document.querySelector(buttonid);// $("#kt_cifbutton_" + applicantId);//
        button.setAttribute("data-kt-indicator", "on");//.attr("disabled",true);
        $(buttonid).attr('disabled',true);
        event.preventDefault();
        var decisionId = '#decision' + applicantId;
        var blStatusId = '.blstatus' + applicantId;
        var remarksId = '#remarks' + applicantId;

        var flg = 1;
        if ($(decisionId).val() === '') {
            flg = -1;
            alertmsg('Please select a decision');
            button.removeAttribute("data-kt-indicator");$(buttonid).attr('disabled',false);
        } else if ($(remarksId).val() === '') {
            flg = -1;
            alertmsg('Please enter remarks');
            button.removeAttribute("data-kt-indicator");$(buttonid).attr('disabled',false);
        } else if ($(decisionId).val() === 'APPROVE' && $(blStatusId).text() != 'Completed') {
            flg = -1;
            alertmsg('Please run Blacklist before approving');
            button.removeAttribute("data-kt-indicator");$(buttonid).attr('disabled',false);
        }

        if (flg == 1) {
            showLoader();
            var action = $(decisionId).val();
            var remarks=$(remarksId).val();

            var formData = {
                "action": action,
                "mock": false,
                "apiName": "cifCreation",
                "workItemNumber": $('#winum').val(),
                "applicantId": applicantData.data("applicantid"),
                "slno": $('#slno').val(),
                "remarks": remarks
            };

            $.ajax({
                type: "POST",
                url: "api/checker/cifCreation",
                contentType: "application/json",
                data: JSON.stringify(formData),
                //async: false,
                success: function (response) {

                    if(response.code=='200') {
                        if ($(decisionId).val() === 'APPROVE') {
                            var cifId = response.cifId;
                            alertmsg('CIF ID ' + cifId + ' created successfully');
                            var cifspan='#cifid'+applicantId;
                            $(cifspan).text(cifId);
                        }else{
                            alertmsg('The record is rejected successfully');
                        }
                        $(buttonid).hide();
                        $(decisionId).attr('disabled',true);
                        var tr_class='.tr'+applicantId;
                        $(tr_class).find('input').prop('disabled',true);
                    }else{
                        alertmsg('An error occurred:'+response.desc);
                    }

                    button.removeAttribute("data-kt-indicator");//.attr("disabled",false);
                    $(buttonid).attr('disabled',false);
                    hideLoader();
                },
                error: function (xhr, status, error) {
                    alertmsg("Error occurred: " + error);
                    button.removeAttribute("data-kt-indicator");//.attr("disabled",false);
                    $(buttonid).attr('disabled',false);
                    hideLoader();
                }
            });
        }
    });

    $('.bogfinalsubmit').on('click', function (e) {

        var vv=true;
        var msg='';
        var remarks =  $("#remarks").val().trim();
        var slno = $('#slno').val();
        var winum = $('#winum').val();
        var flg = 0;

        var ff=0;
        $('.decisionddl').each(function(i, obj) {
            if($(this).val()!='APPROVE' && $(this).val()!='REJECT'){
                ff++;
            }
        });
        if(ff!=0){
            msg='Please Approve or Send back each item under CIF creation';

        }else if(remarks=="") {
            msg='Please enter final remarks\n';

        }
        if(msg!=''){
            alertmsg(msg);
        }else{
            saveform(winum, slno, remarks);
        }
    });

});
function saveform(winum, slno, remarks){


    showLoader();
    $.ajax({
        url: 'api/wibogsave',
        type: 'POST',
        async : false,
        data: {
            winum: winum,
            slno: slno,
            remarks: remarks
        },
        success: function (response) {
            hideLoader();
            if (response.status === 'S') {
                blockerMsg('Saved successfully');
            }else{
                alertmsg(response.msg);
            }
        },
        error: function (xhr) {
            hideLoader();
            alertmsg("Something went wrong:"+xhr.msg);
        }
    });


}