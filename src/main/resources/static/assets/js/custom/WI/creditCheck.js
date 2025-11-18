$(document).ready(function() {
    let ino=0;
    var button="";

    $('#loanbody').on('click','.crchk-runexp', function(e) {
        e.preventDefault();
        e.stopPropagation();
        button=$(this);
        var exptenure=$(this).closest('.det').find('.exptenure').val();
        var expLoanAmt=$(this).closest('.det').find('.expLoanAmt').val();

            if(exptenure<=0 || expLoanAmt<=0){
                alertmsg("Please Enter Valid Tenure & Amount");
                return;
            }

        var SendInfo={
            experianForm: {
                slno: $('#slno').val(),
                appid: $(this).closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
                reqtype: $(this).closest('.det').attr('data-code'),
                winum: $('#winum').val(),
                exptenure: exptenure,
                expLoanAmt: expLoanAmt
            }
        }

        showLoader();
        $.ajax({
            url: 'api/getExperianScore',
            type: 'POST',
            data: JSON.stringify(SendInfo),
            contentType: 'application/json',
            success: function (response) {
                hideLoader();
                if (response.status === 'S' || response.status === 'W' ) {
                    var data =JSON.parse(response.msg) ;
                    button.closest('.det').find('.experian_score').text(data.score);
                    button.closest('.det').find('.experian_fetchTime').text('Time - '+data.fetchTime);
                    // Parse the liabilityList which is a stringified JSON array

                    var liabilityList =data.liabilityList? JSON.parse(data.liabilityList):"";

                    // Target your table's tbody or table element
                    var tableBody =  button.closest('.det').find('.crchk-inc-lia-table tbody'); // replace '#your-table-id' with your actual table ID

                    // Clear the table body if you want to replace existing data
                    if(liabilityList) {
                        tableBody.empty();

                        // Loop through the liabilityList and append rows to the table
                        liabilityList.forEach(function (liability, index) {
                            var newRow = '<tr>' +
                                '<td><input type="text" name="liabankname" id="liabankname' + index + '" class="form-control" placeholder="Bank Name" value="' + liability.bankName + '"  readonly disabled=""></td>' +
                                '<td><input type="text" name="lianaturelimit" id="lianaturelimit' + index + '" class="form-control" placeholder="Enter Nature of Limit" value="' + liability.natureOfLimit + '" readonly disabled=""></td>' +
                                '<td><input type="text" name="lialimit" id="lialimit' + index + '" class="form-control" placeholder="Enter Limit" value="' + liability.limit + '" disabled=""></td>' +
                                '<td><input type="text" name="liaoutstanding" id="liaoutstanding' + index + '" class="form-control" placeholder="Enter Outstanding" value="' + liability.outStandingBal + '" readonly disabled=""></td>' +
                                '<td><input type="text" name="liaemi" id="liaemi' + index + '" class="form-control" placeholder="Enter EMI" value="' + Math.round(liability.emiAmount) + '" readonly disabled=""></td>' +
                                '<td><input type="text" name="liamodifiedemi" id="liamodifiedemi' + index + '" class="form-control" placeholder="Enter Modified EMI" value="' + Math.round(liability.emiAmount) + '" ></td>' +
                                '</tr>';
                            // Append the new row to the table body
                            tableBody.append(newRow);
                        });
                    }



                    var targetelement = button.closest('.det').find('[name="crchk-score"]');
                    var score = data.score;
                    // Calculate the rotation angle
                    var maxScore = 999;
                    var maxRotation = 180; // degrees for a semicircle
                    if(score<=560){
                        maxRotation=36;
                        maxScore=560;
                    }
                   else if(score<=720){
                        maxRotation=72;
                        maxScore=720;
                    }
                    else if(score<=880){
                        maxRotation=108;
                        maxScore=880;
                    }
                    else if(score<=960){
                        maxRotation=144;
                        maxScore=960;
                    }
                    else{
                        maxRotation=180;
                        maxScore=999;
                    }
                    var rotationAngle = (score / maxScore) * maxRotation;
                    // Apply the rotation transform
                  //  targetElement.css('transform', 'rotate(' + rotationAngle + 'deg) scale(0.9999999832349832, 0.9999999832349832)');
                    rotationAngle=rotationAngle-90;
                    targetelement.attr('transform','rotate('+rotationAngle+') ,scale(0.9999999832349832,0.9999999832349832)');
                    if (response.status === 'S') {
                        button.closest('.det').find('[name="crchk-experionscore"]').show();
                        button.closest('.det').find('[name="crchk-inc"]').show();
                        button.closest('.det').find('[name="crchk-inc-lia"]').show();
                    }
                    else{
                        alertmsg(data.errorReason)
                        button.text('Fetch Experian Report');
                    }
                } else {
                    hideLoader();
                    alertmsg('Failed: ' + response.msg);
                }
            },
            error: function (xhr, status, error) {
                hideLoader();
                var err_data = xhr.responseJSON;
                if (err_data.msg) {
                    alertmsg(err_data.msg);
                }
                else {
                    alertmsg('An error occurred: ' + error);
                }
            }
        });


});



});


function addRowCrchk(button,tablechk) {

    var div_identify="";
    var table_identify="";
    // for the cases salries/pension use this div and table section
    if(tablechk=='LIA')
    {
        div_identify="crchk-inc-lia-div";
        table_identify="crchk-inc-lia-table-body";
    }
    else if(tablechk=='AST')
    {
        div_identify="crchk-inc-ast-div";
        table_identify="crchk-inc-ast-table-body";
    }
    const closestDiv=button.closest('.'+div_identify);
    const table = closestDiv.querySelector('.'+table_identify);

    const rowcount=table.rows.length;
    console.log(rowcount);

    var rowCount = table.rows.length;
    var row = table.insertRow(rowCount);
    //alert($("#"+tableID).prop('rows').length);
    var colCount = table.rows[0].cells.length;
    for (var i = 0; i < colCount; i++)
    {
        var newcell = row.insertCell(i);

            newcell.innerHTML = table.rows[0].cells[i].innerHTML;
            // alert(newcell.childNodes[0].type);
            switch (newcell.childNodes[0].type) {
                case "text":
                    newcell.childNodes[0].value = "";
                    newcell.childNodes[0].name = newcell.childNodes[0].name;
                    newcell.childNodes[0].id = newcell.childNodes[0].id + rowCount
                        + i;
                    newcell.childNodes[0].readOnly = false;
                    if (newcell.childNodes[0].name === "liaemi") {
                       // newcell.childNodes[0].readOnly = true;
                    }
                    // newcell.childNodes[0].className='form-control';
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
                case "select-one":
                    newcell.childNodes[0].value = "";
                    newcell.childNodes[0].name = newcell.childNodes[0].name
                        + rowCount + i;
                    newcell.childNodes[0].id = newcell.childNodes[0].id + rowCount
                        + i;
                    break;
                case "checkbox":
                    newcell.childNodes[0].checked = false;
                    newcell.childNodes[0].name = newcell.childNodes[0].name;
                    newcell.childNodes[0].id = newcell.childNodes[0].id + rowCount
                        + i;
                    break;
            }



    }

}


function deleteRowCrchk(element,tablechk) {
    try {

        var div_identify="";
        var table_identify="";

        // for the cases salries/pension use this div and table section
        if(tablechk=='LIA')
        {
            div_identify="crchk-inc-lia-div";
            table_identify="crchk-inc-lia-table-body";
            // $('.empworkexp-sal').trigger('change');
        }
        else if(tablechk=='AST')
        {
            div_identify="crchk-inc-ast-div";
            table_identify="crchk-inc-ast-table-body";
            // $('.empworkexp-oth').trigger('change');
        }
        const closestDiv=element.closest('.'+div_identify);
        const table = closestDiv.querySelector('.'+table_identify);

        //const closestDiv=element.closest('.emptable-sal-div');
        //const table = closestDiv.querySelector('.emptable-sal-pen-body');
        var rowCount = table.rows.length;
        var curRow = element.parentNode.parentNode.rowIndex;
        curRow=curRow-1;
        //alert(curRow);
        if (rowCount <= 1) {
            alertmsg("Cannot delete all the rows.");
        }
        else {
            if (confirm("Do you want to delete the row ? ")) {
                table.deleteRow(curRow);

            }
        }
    } catch (e) {
        //alert(e);
    }
}


function creditsave(form,key,callback) {

    var vlfinliab=[];
    var vlfinast=[];
    var liabilityAsPerPayslip = form.find('[name="liabilityAsPerPayslip"]').val();


    form.find('.crchk-inc-lia-table-body').find('tr').each(function(){
        var row=$(this);
        var lia={};
        lia.ino= row.find('[name="hid_ino_emp"]').val();
        lia.bankName=row.find('[name="liabankname"]').val();
        lia.natureLim=row.find('[name="lianaturelimit"]').val();
        lia.limit=row.find('[name="lialimit"]').val();
        lia.outStanding=row.find('[name="liaoutstanding"]').val();
        lia.emi=row.find('[name="liaemi"]').val();
        lia.modifiedEmi = row.find('[name="liamodifiedemi"]').val();
        vlfinliab.push(lia);
    });

    form.find('.crchk-inc-ast-table-body').find('tr').each(function(){
        var row=$(this);
        var ast={};
        ast.ino= row.find('[name="hid_ino_emp"]').val();
        ast.assetType=row.find('[name="asttypeasset"]').val();
        ast.assetVal=row.find('[name="astvalue"]').val();
        vlfinast.push(ast);
    });



    var emp=[
        {"key":"vlfinliab","value":JSON.stringify(vlfinliab)},
        {"key":"vlfinast","value":JSON.stringify(vlfinast)},
        {"key":"liabilityAsPerPayslip","value": liabilityAsPerPayslip}
    ];

    var jsonBody = {
        id: key,
        slno: $('#slno').val(),
        winum: $('#winum').val(),
        liabilityAsPerPayslip: $('#liabilityAsPerPayslip').val(),
        appid: form.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
        data: emp,
        reqtype:form.attr('data-code'),
    };
    console.log("vlcredit" + JSON.stringify(jsonBody,null,2));
    $.ajax({
        url: 'api/save-data',
        type: 'POST',
     //   async:false,
        contentType: 'application/json', // Set content type to JSON
        data: JSON.stringify(jsonBody), // Convert data object to JSON string
        success: function (response) {
            if(response.status==='S')
            {
                form.find('.appid').val(response.appid);
                disableFormInputs(form);
                notyalt('Credit Details Saved !!')
                if(response.msg.length>0 && response.msg!='null') {
                    var data = JSON.parse(response.msg);

                }
                // setTimeout(() => {
                //     $('#vehDetailsContent').collapse('show');
                // }, 1000); // Defer the collapse to ensure it happens after any UI updates
                scrolltoId("ebityDetailslink ");

                callback(true);
            }
            else{
                alertmsg(response.msg);
                callback(false);
            }
        },
        error: function (xhr, status, error) {
            if (error.responseJSON) {
                alertmsg( error.responseJSON.msg);
            } else {
                alertmsg('Error saving loan details');
            }
            callback(false);
        }
    });
}
function validateIntegerInputs() {
    // Find all modified EMI inputs and add event listeners
    $('input[name="liamodifiedemi"], input[name="liabilityAsPerPayslip"]').each(function() {
        // Handle keydown to prevent decimal point entry
        $(this).on('keydown', function(e) {
            // Check if the pressed key is a decimal point (both regular period and numpad decimal)
            if (e.key === '.' || e.keyCode === 190 || e.keyCode === 110) {
                e.preventDefault();
                alertmsg("Decimal points are not allowed. Please enter whole numbers only.");
                return false;
            }
        });

        // Handle input event to remove any decimal points that might have been entered
        $(this).on('input', function() {
            if (this.value.includes('.')) {
                alertmsg("Decimal points are not allowed. Please enter whole numbers only.");
                this.value = this.value.replace(/\./g, ''); // Remove all decimal points
            }

            // Also remove any non-numeric characters
            var originalValue = this.value;
            var newValue = this.value.replace(/[^0-9]/g, '');

            if (originalValue !== newValue) {
                this.value = newValue;
            }
        });

        // Also handle paste events
        $(this).on('paste', function(e) {
            var pastedText = (e.originalEvent || e).clipboardData.getData('text/plain');
            if (pastedText.includes('.') || !/^\d+$/.test(pastedText)) {
                e.preventDefault();
                alertmsg("Only whole numbers are allowed. Decimal points are not permitted.");
            }
        });

        // Final check on blur (when field loses focus)
        $(this).on('blur', function() {
            if (!Number.isInteger(Number(this.value))) {
                alertmsg("Only whole numbers are allowed. Converting to integer.");
                this.value = Math.floor(this.value);
            }
        });
    });
}
function validateDecimal(input) {
    if (input.value.includes('.')) {
        alertmsg("Decimal points are not allowed. Converting to whole number.");
        input.value = Math.floor(input.value);
    }
}






