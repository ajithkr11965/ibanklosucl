$(document).ready(function() {
    let ino=0;





    $('#loanbody').on('change','.selemptype', function(e) {
        e.preventDefault();
        e.stopPropagation();

        var selectedEmptype=$(this).val();
        var parentRow=$(this).closest('.row');

        if(selectedEmptype=='SALARIED' || selectedEmptype=='PENSIONER')
        {
            parentRow.next().find('.emp-sal-pen').show();
            parentRow.next().find('.emp-oth').hide();
            //Salaried case
            if(selectedEmptype=='SALARIED')
            {
                parentRow.next().find('.emp-sal-ret').show();
            }
            else if(selectedEmptype=='PENSIONER')
            {
                parentRow.next().find('.emp-sal-ret').hide();
                $(this).closest('.det').find('[name="emp-retage"]').val('');
            }
            $('.empworkexp-sal').trigger('change');
        }
        else if(selectedEmptype=='SEP' || selectedEmptype=='SENP' || selectedEmptype=='AGRICULTURIST' )
        {
            parentRow.next().find('.emp-sal-pen').hide();
            parentRow.next().find('.emp-oth').show();
            $('.empworkexp-oth').trigger('change');
            $(this).closest('.det').find('[name="emp-retage"]').val('');
        }
        else if(selectedEmptype=='NONE'  || selectedEmptype=='' )
        {
            parentRow.next().find('.emp-sal-pen').hide();
            parentRow.next().find('.emp-oth').hide();
            $('.total_experience_emp').val(0);
            $(this).closest('.det').find('[name="emp-retage"]').val('');
        }

    });

    $('#loanbody').on('change','.emptable-sal-pen-empcurent', function(e) {
        if ($(this).is(':checked')) {
            $('.emptable-sal-pen-empcurent').not(this).prop('checked', false);
        }
    });

    $('#loanbody').on('change','.empworkexp-sal', function(e) {
        let sum=0;
        $(this).closest('.det').find('.empworkexp-sal').each(function () {
            let value = parseFloat($(this).val() || 0);
            sum=sum+value;
        });

        $(this).closest('.det').find('.empworkexp-sal-tot').text(sum);
        $(this).closest('.det').find('.total_experience_emp').val(0);
        $(this).closest('.det').find('.total_experience_emp').val(sum);


    });

    $('#loanbody').on('change','.empworkexp-oth', function(e) {
        let sum=0;
        $(this).closest('.det').find('.empworkexp-oth').each(function () {
            let value = parseFloat($(this).val() || 0);
            sum=sum+value;
        });

        $(this).closest('.det').find('.empworkexp-oth-tot').text(sum);
        $(this).closest('.det').find('.total_experience_emp').val(0);
        $(this).closest('.det').find('.total_experience_emp').val(sum);

    });




    // to trigger the on change on page load
     $('.selemptype').trigger('change');


});


function addRowEmp(button,tablechk) {
    // var table=$(this).closest('.emptable-div').find('.emptable-sal-pen');
    // console.dir("table"+table);

    var div_identify="";
    var table_identify="";


    // for the cases salries/pension use this div and table section
    if(tablechk=='SALARIED')
    {
        div_identify="emptable-sal-div";
        table_identify="emptable-sal-pen-body";
    }
    else if(tablechk=='OTH')
    {
        div_identify="emptable-oth-div";
        table_identify="emptable-oth-body";
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
        if (tablechk == 'OTH' && ( i <=2) ) {
            var select = document.createElement('select');
            var rowCountPlusI = rowCount + i;

            select.className = 'form-control select select2-initialisation-emp';

            if (i == 0) {
                select.name = 'empothtype';
                select.id = 'empothtype' + rowCountPlusI;
                select.onchange=function(){
                    updateEmpothcode(this)
                };

                // Append the select element to the DOM first
                newcell.appendChild(select);

                // Capture the current select element
                (function(select) {
                    // Populate only the i==0 select element
                    $.ajax({
                        url: 'api/getOctDetails',
                        method: 'GET',
                        success: function(data) {
                            // Add "Please Select" option at the top
                            select.appendChild(new Option("Please Select", ""));

                            // Add options from the fetched data
                            data.forEach(function(octdetail) {
                                select.appendChild(new Option(octdetail.octDesc, octdetail.octValue+"_"+octdetail.octDesc));
                            });

                            //Reinitialize select2 after populating options
                            $(select).select2({
                                templateResult: formatState,
                                templateSelection: formatState
                            });
                        },
                        error: function(err) {
                            console.error("Failed to fetch options:", err);
                        }
                    });
                })(select); // Immediately invoke the function with the select element
             }
            else if (i == 1) {
                newcell.innerHTML = table.rows[0].cells[i].innerHTML;
                // alert(newcell.childNodes[0].type);
                switch (newcell.childNodes[0].type) {
                    case "text":
                        newcell.childNodes[0].name = newcell.childNodes[0].name;
                        newcell.childNodes[0].id = newcell.childNodes[0].id + rowCount+ i;
                        newcell.childNodes[0].readOnly = true;
                        break;
                }
            }
                else if (i == 2) {
                // Similar handling for i==2
                select.name = 'empothprof';
                select.id = 'empothprof' + rowCountPlusI;

                // Manually set the options for i==2
                var options = [
                    { value: '', text: 'Please Select' },
                    { value: 'ARCH', text: 'Architect' },
                    { value: 'CA', text: 'Chartered Accountant' },
                    { value: 'CS', text: 'Company Secretary' },
                    { value: 'COSTACT', text: 'Cost Accountant' },
                    { value: 'DR', text: 'Doctor' },
                    { value: 'ENG', text: 'Engineer' },
                    { value: 'LAW', text: 'Lawyer' },
                    { value: 'OTH', text: 'Others' }
                ];
                options.forEach(function(option) {
                    select.appendChild(new Option(option.text, option.value));
                });

                // Append the select element to the DOM first
                newcell.appendChild(select);

                // Reinitialize select2 after adding options
                $(select).select2({
                    templateResult: formatState,
                    templateSelection: formatState
                });
            }
        }



        else
        {
            newcell.innerHTML = table.rows[0].cells[i].innerHTML;
            // alert(newcell.childNodes[0].type);
            switch (newcell.childNodes[0].type) {
                case "text":

                    newcell.childNodes[0].name = newcell.childNodes[0].name;
                    newcell.childNodes[0].id = newcell.childNodes[0].id + rowCount
                        + i;
                    newcell.childNodes[0].readOnly = false;
                   // newcell.childNodes[0].value = "";
                    newcell.childNodes[0].setAttribute('value', ''); // Change this line
                    //newcell.childNodes[0].className='form-control';
                    //newcell.childNodes[0].placeholder='Enter Data';
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

        //
        // if(tablechk=='SALARIED')
        // {
        //     $(this).closest('.det').find('[name="empname"]').rules('add', {
        //         required: true
        //     });
        //     $('#empaddress' + rowCount + i).rules('add', {
        //         required: true
        //     });
        //     $('#empworkexp' + rowCount + i).rules('add', {
        //         required: true
        //     });
        // }
        // else if(tablechk=='OTH')
        // {
        //     $('#empothtype'+rowCount + i).rules('add', {
        //         required: true
        //     });
        //     $('#empothcode' + rowCount + i).rules('add', {
        //         required: true
        //     });
        //     $('#empothprof' + rowCount + i).rules('add', {
        //         required: true
        //     });
        //     $('empothname').rules('add', {
        //         required: true
        //     });
        //     $('#empothwrk' + rowCount + i).rules('add', {
        //         required: true
        //     });
        // }

        // Add validation rules
        // if (tablechk == 'SALARIED') {
        //     $('#empname' + rowCount + i).rules('add', {
        //         required: true,
        //         messages: { required: "Name is required" }
        //     });
        //     $('#empaddress' + rowCount + i).rules('add', {
        //         required: true,
        //         messages: { required: "Address is required" }
        //     });
        //     $('#empworkexp' + rowCount + i).rules('add', {
        //         required: true,
        //         messages: { required: "Work experience is required" }
        //     });
        // } else if (tablechk == 'OTH') {
        //     $('#empothtype' + rowCount + i).rules('add', {
        //         required: true,
        //         messages: { required: "Type is required" }
        //     });
        //     $('#empothcode' + rowCount + i).rules('add', {
        //         required: true,
        //         messages: { required: "Code is required" }
        //     });
        //     $('#empothprof' + rowCount + i).rules('add', {
        //         required: true,
        //         messages: { required: "Profession is required" }
        //     });
        //     // $('#empothname' + rowCount + i).rules('add', {
        //     //     required: true,
        //     //     messages: { required: "Name is required" }
        //     // });
        //     $('#empothwrk' + rowCount + i).rules('add', {
        //         required: true,
        //         messages: { required: "Work details are required" }
        //     });
        // }

    }

    $('.employmentdetails').validate().form();

}


function deleteRowEmp(element,tablechk) {
    try {

        var div_identify="";
        var table_identify="";


        // for the cases salries/pension use this div and table section
        if(tablechk=='SALARIED')
        {
            div_identify="emptable-sal-div";
            table_identify="emptable-sal-pen-body";
           // $('.empworkexp-sal').trigger('change');
        }
        else if(tablechk=='OTH')
        {
            div_identify="emptable-oth-div";
            table_identify="emptable-oth-body";
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
                //$('.gettotalnew').trigger('input');
                if(tablechk=='SALARIED')
                {
                    $('.empworkexp-sal').trigger('change');
                }
                else if(tablechk=='OTH')
                {
                    $('.empworkexp-oth').trigger('change');
                }

            }
        }
    } catch (e) {
        //alert(e);
    }
}

function formatState(state)
{
    if(!state.id)
    {
        return state.text;
    }
    var $state=$('<span>'+state.text+'</span>');
    return $state;
}


//save

    function employementsave(form,key,callback) {

        var error_msg="";
        var flg=0;
        var flg_check=0;

        var vlemploymentemp=[];

        if (!form.valid()) {
            alert('Please correct the errors in the form before saving.');
            return; // Stop the function if the form is not valid
        }



        var employment_type=form.find('[name="selemptype"]').val();
        if(employment_type=='SALARIED' || employment_type=='PENSIONER')
        {
            form.find('.emptable-sal-pen-body').find('tr').each(function(){
                var row=$(this);
                var emp={};
                emp.ino= row.find('[name="hid_ino_emp"]').val();
                emp.employerName=row.find('[name="empname"]').val();
                emp.employerAddress=row.find('[name="empaddress"]').val();
                emp.workExperience=row.find('[name="empworkexp"]').val();
                emp.currentEmployer=row.find('[name="empcurent"]').is(':checked');
                console.log(row.find('[name="empcurent"]').is(':checked'));
                vlemploymentemp.push(emp);

                if(row.find('[name="empname"]').val() == null || row.find('[name="empname"]').val() == '')
                {
                    error_msg +="Employment Name Cannot be Empty <br>";
                    flg=1;
                }
                else if(row.find('[name="empname"]').val().length > 1000)
                {
                    error_msg +="Employment Name must not exceed 1000 characters<br>";
                    flg=1;
                }

                if(row.find('[name="empaddress"]').val() == null || row.find('[name="empaddress"]').val() == '')
                {
                    error_msg +="Employment Address Cannot be Empty <br>";
                    flg=1;
                }
                else if(row.find('[name="empaddress"]').val().length > 1000)
                {
                    error_msg +="Employment Name must not exceed 1000 characters<br>";
                    flg=1;
                }
                if(row.find('[name="empworkexp"]').val() == null || row.find('[name="empworkexp"]').val() == '')
                {
                    error_msg +="Work Experience Cannot be Empty <br>";
                    flg=1;
                }
               else if(!/^\d+$/.test(row.find('[name="empworkexp"]').val()))
                {
                    error_msg +="Work Experience--Only numbers are allowed <br>";
                    flg=1;
                }

                if(row.find('[name="empcurent"]').is(':checked') && employment_type=='SALARIED')
                {
                    flg_check=1;
                }




            });

            if(flg_check !=1  && employment_type=='SALARIED')
            {
                error_msg +="Select Current employer Status <br>";
                flg=1;
            }
        }
        else if(employment_type=='SEP' || employment_type=='SENP' || employment_type=='AGRICULTURIST' )
        {
            form.find('.emptable-oth-body').find('tr').each(function(){
                var row=$(this);
                var emp={};
                emp.ino= row.find('[name="hid_ino_emp"]').val();
                emp.occupationType=row.find('[name="empothtype"]').val().split("_")[1];
                emp.occupationCode=row.find('[name="empothcode"]').val();
                emp.profession=row.find('[name="empothprof"]').val();
                emp.employerName=row.find('[name="empothname"]').val();
                emp.businessExperience=row.find('[name="empothwrk"]').val();
                vlemploymentemp.push(emp);



                if(row.find('[name="empothtype"]').val() == null || row.find('[name="empothtype"]').val() == '')
                {
                    error_msg +="Occupation Type Cannot be Empty <br>";
                    flg=1;
                }



                if(row.find('[name="empothprof"]').val() == null || row.find('[name="empothprof"]').val() == '')
                {
                    error_msg +="Occupation Proffesssion Cannot be Empty <br>";
                    flg=1;
                }

                if(row.find('[name="empothname"]').val() == null || row.find('[name="empothname"]').val() == '')
                {
                    error_msg +="Employer Name Cannot be Empty <br>";
                    flg=1;
                }
                else if(row.find('[name="empothname"]').val().length > 1000)
                {
                    error_msg +="Employer Name must not exceed 1000 characters<br>";
                    flg=1;
                }

                if(row.find('[name="empothwrk"]').val() == null || row.find('[name="empothwrk"]').val() == '')
                {
                    error_msg +="Business experience Cannot be Empty <br>";
                    flg=1;
                }
                else if(!/^\d+$/.test(row.find('[name="empothwrk"]').val()))
                {
                    error_msg +="Business experience--Only numbers are allowed <br>";
                    flg=1;
                }

            });
        }

        console.log("selected empttype: "+ form.find('[name="selemptype"]').val());
        console.log(" retage: "+ form.find('[name="emp-retage"]').val());
        console.log(" total_experience_emp: "+form.find('[name="total_experience_emp"]').val());


        console.log(JSON.stringify(vlemploymentemp));
        var emp=[
            {"key":"employment_type","value":form.find('[name="selemptype"]').val()},
            {"key":"retirement_age","value":form.find('[name="emp-retage"]').val()},
            {"key":"total_experience","value":form.find('[name="total_experience_emp"]').val()},
            {"key":"vlemploymentemp","value":JSON.stringify(vlemploymentemp)}
        ];

        var jsonBody = {
            id: key,
            slno: $('#slno').val(),
            winum: $('#winum').val(),
            appid: form.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
            data: emp,
            reqtype:form.attr('data-code'),
        };

        if(flg==0)
        {
            $.ajax({
                url: 'api/save-data',
                type: 'POST',
         //       async: false,
                contentType: 'application/json', // Set content type to JSON
                data: JSON.stringify(jsonBody), // Convert data object to JSON string
                success: function (response) {
                    if (response.status === 'S') {
                        form.find('.appid').val(response.appid);
                        disableFormInputs(form);
                        notyalt('Employment Details Saved !!')
                        if (response.msg.length > 0 && response.msg != 'null') {
                            var data = JSON.parse(response.msg);

                        }
                        callback(true);
                    } else {
                        alertmsg(response.msg);
                        callback(false);
                    }
                },
                error: function (xhr, status, error) {
                    alertmsg('Error during Saving data');
                    callback(false);
                }
            });
        }
        else if(flg==1)
        {
            alertmsg(error_msg);
            callback(false);
        }
    }

function updateEmpothcode(selectElement)
{
    var closestTr = $(selectElement).closest('tr');
    var Empothcode = closestTr.find('[name="empothcode"]');
    Empothcode.val($(selectElement).val().split("_")[0]);
}



