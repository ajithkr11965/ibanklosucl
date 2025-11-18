function basicinit(){
    $('.basic_city').select2({
        placeholder: "Search for a City",
        minimumInputLength: 3, // Requires at least one character for the search
        ajax: {
            url: function (params) {
                // Append the search term as a path variable
                return 'api/get-city/' + params.term;
            },
            dataType: 'json', // Data type of the API response
            delay: 250, // Wait 250ms after typing stops to send the request
            processResults: function (data) {
                // Transform the top-level data array to fit Select2's requirements
                return {
                    results: data.map(item => ({
                        id: item.codevalue, // Adjusted for your data format where `codevalue` is the id
                        text: item.codedesc // Adjusted for your data format where `codedesc` is the text
                    }))
                };
            },
            cache: true
        }
    });
    // Fixed width. Single select
    $('.basic_country').select2({
        placeholder: "Search for a Country",
        minimumInputLength: 3, // Requires at least one character for the search
        ajax: {
            url: function (params) {
                // Append the search term as a path variable
                return 'api/get-country/' + params.term;
            },
            dataType: 'json', // Data type of the API response
            delay: 250, // Wait 250ms after typing stops to send the request
            processResults: function (data) {
                // Transform the top-level data array to fit Select2's requirements
                return {
                    results: data.map(item => ({
                        id: item.codevalue, // Adjusted for your data format where `codevalue` is the id
                        text: item.codedesc // Adjusted for your data format where `codedesc` is the text
                    }))
                };
            },
            cache: true
        }
    });
    $('.basic_state').select2({
        placeholder: "Search for a State",
        minimumInputLength: 3, // Requires at least one character for the search
        ajax: {
            url: function (params) {
                // Append the search term as a path variable
                return 'api/get-state/' + params.term;
            },
            dataType: 'json', // Data type of the API response
            delay: 250, // Wait 250ms after typing stops to send the request
            processResults: function (data) {
                // Transform the top-level data array to fit Select2's requirements
                return {
                    results: data.map(item => ({
                        id: item.codevalue, // Adjusted for your data format where `codevalue` is the id
                        text: item.codedesc // Adjusted for your data format where `codedesc` is the text
                    }))
                };
            },
            cache: true
        }
    });
}
$(document).ready(function() {
    const body = document.body;
    // Fixed width. Single select
    basicinit();

    $('#loanbody').on('change','.basic_mob ,.basic_email ,.basic_mobcode', function(e) {
        var mobileNumber = $(this).val();
        var regex = /^[6-9]\d{9}$/;
        if($(this).hasClass('basic_mob')){
            if($(this).closest('.det').closest('.tab-pane').find('.basicdetails').find('.basic_mobcode').val()=='91'){
                if (!regex.test(mobileNumber)) {
                    alertmsg('Kindly Enter 10 digit Mobile Number');
                    $(this).val('');
                }
            }
        }
        if($(this).hasClass('basic_mobcode')){
            $(this).closest('.det').closest('.tab-pane').find('.basicdetails').find('.basic_mob').val('');
        }
        if($(this).hasClass('basic_email')){
            var email=$(this).closest('.det').closest('.tab-pane').find('.basicdetails').find('.basic_email');
            if(!/^.+@.+\..+$/.test(email.val())){
                alertmsg('Kindly Enter Valid Email ID');
                email.val('');
            }
        }
        $(this).closest('.det').closest('.tab-pane').find('.basicdetails').find('.losflag').val('false');
        $(this).closest('.det').closest('.tab-pane').find('.basicdetails').find('.finflag').val('false');
    });
    $('#loanbody').on('change','.pincode', function(e) {
        var $thisT = $(this); // Get the current value of the input/select

        // Check if the length of the value is at least 6 characters
        if ( $thisT.val() >= 6) {
            $.ajax({
                url: 'api/pincode/' +  $thisT.val(), // Append the value as a path variable in the API call
                type: 'GET', // Method of the AJAX call
                dataType: 'json', // Expected data type of the response
                success: function(data) {
                    if (data && data.pincode) { // Check if data is valid and has a pincode
                        const $details = $thisT.closest('.det');
                        if($thisT.hasClass('permanentPin')) {
                            $details.find('.permanentCity').html('<option value="' + data.finacleCityCode + '">' + data.finacleCity + '</option>');
                            $details.find('.permanentState').html('<option value="' + data.finacleStateCode + '">' + data.finacleState + '</option>');
                            $details.find('.permanentCountry').html('<option value="IN">INDIA</option>');
                        }
                        else{
                            $details.find('.presentCity').html('<option value="' + data.finacleCityCode + '">' + data.finacleCity + '</option>');
                            $details.find('.presentState').html('<option value="' + data.finacleStateCode + '">' + data.finacleState + '</option>');
                            $details.find('.presentCountry').html('<option value="IN">INDIA</option>');
                        }
                        // Call any initialization or follow-up functions, if necessary
                        basicinit(); // Assuming this is a function you've defined to reinitialize components or perform further setup
                    } else {
                        console.log("Data is invalid or missing crucial information.");
                    }

                },
                error: function(xhr, status, error) {
                    alertmsg('Error: ' + xhr.responseText);

                }
            });
        }
    });

    // $('#loanbody').on('change','.sameAsPermanent', function(e) {
    //     e.preventDefault();
    //     e.stopPropagation();
    //     if(this.checked) {
    //         $(this).closest('.det').find('input[name^="comm_proof_flg"]').val('N');
    //         $(this).closest('.det').find('input[name^="sameAsper_flg"]').val('Y');
    //         $(this).closest('.det').find('input[name^="present"]').each(function() {
    //             var name = $(this).attr('name');
    //             var correspondingPermanentName = name.replace('present', 'permanent');
    //             $(this).val($(this).closest('.det').find('input[name="' + correspondingPermanentName + '"]').val());
    //             $(this).prop('readonly', true);
    //         });
    //         $(this).closest('.det').find('select[name^="present"]').each(function() {
    //             var $this = $(this);
    //             var name = $this.attr('name');
    //             var correspondingPermanentName = name.replace('present', 'permanent');
    //             var $correspondingPermanentSelect = $this.closest('.det').find('select[name="' + correspondingPermanentName + '"]');
    //             var clonedOption = $correspondingPermanentSelect.find('option:selected').clone();
    //             $this.empty().append(clonedOption).prop('disabled', true);
    //
    //         });
    //     } else {
    //         $(this).closest('.det').find('input[name^="sameAsper_flg"]').val('N');
    //         $(this).closest('.det').find('input[name^="comm_proof_flg"]').val('N');
    //         $(this).closest('.det').find('input[name^="present"]').val('');
    //         $(this).closest('.det').find('input[name^="present"]').prop('readonly', false);
    //         $(this).closest('.det').find('input[name^="present"]').prop('disabled', false);
    //         $(this).closest('.det').find('select[name^="present"]').val('');
    //     }
    // });


    $('#loanbody').on('change','.permanentCheck', function(e) {
        e.preventDefault();
        e.stopPropagation();
        var $closestDet = $(this).closest('.det');
        if($closestDet.find('.sameAsPermanent').is(':checked'))
            $closestDet.find('.sameAsPermanent').prop('checked', false).trigger('change');
    });

    $('#loanbody').on('change','.sameAsPermanent', function(e) {
        e.preventDefault();
        e.stopPropagation();
        var $closestDet = $(this).closest('.det');
        if(this.checked) {
            $closestDet.find('input[name^="comm_proof_flg"]').val('N');
            $closestDet.find('input[name^="sameAsper_flg"]').val('Y');
            $closestDet.find('input[name^="present"]').each(function() {
                var name = $(this).attr('name');
                var correspondingPermanentName = name.replace('present', 'permanent');
                $(this).val($closestDet.find('input[name="' + correspondingPermanentName + '"]').val());
                $(this).prop('readonly', true);
            });
            $closestDet.find('select[name^="present"]').each(function() {
                var $this = $(this);
                var name = $this.attr('name');
                var correspondingPermanentName = name.replace('present', 'permanent');
                var $correspondingPermanentSelect = $closestDet.find('select[name="' + correspondingPermanentName + '"]');
                var clonedOption="";
                if(correspondingPermanentName==='permanentResidenceType'){
                     clonedOption = $correspondingPermanentSelect.find('option').clone();
                    $this.empty().append(clonedOption);
                    $this.val($correspondingPermanentSelect.find('option:selected').val()).trigger('change').prop('disabled', true);
                }
                else{
                     clonedOption = $correspondingPermanentSelect.find('option:selected').clone();
                    $this.empty().append(clonedOption).prop('disabled', true);
                }

                basicinit();
            });
        } else {
            $closestDet.find('input[name^="sameAsper_flg"]').val('N');
            $closestDet.find('input[name^="comm_proof_flg"]').val('N');
            $closestDet.find('input[name^="present"]').val('');
            $closestDet.find('input[name^="present"]').prop('readonly', false);
            $closestDet.find('input[name^="present"]').prop('disabled', false);
            $closestDet.find('select[name^="present"]').each(function() {
            $(this).prop('disabled', false).val('') // Enable and reinitialize Select2
                basicinit();
            });
        }
    });


    $('#loanbody').on('click','.losdedup-button', function(e) {
        e.preventDefault();
        e.stopPropagation();
        var here=$(this);
        var mob=here.closest('.det').find('.basic_mob').val();
        var email=here.closest('.det').find('.basic_email').val();
        if(mob.length<=1 || email.length<=1 ){
            alertmsg('Kindly Enter Mobile Number And Email');
        }
        else {
            showLoader();
            var data = {
                winum: $('#winum').val(),
                slno: $('#slno').val(),
                appid: here.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
                mobno: mob,
                email: email
            }
            $.ajax({
                url: 'api/los-dedupe',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: function (response) {
                    if (response.status === 'S') {
                        let rows = '';
                        var data = JSON.parse(response.msg);
                        data.forEach(item => {
                            rows += `<tr class="bg-light-whitetr">
                                    <td>${item.wi_name || ''}</td>
                                    <td>${item.custName || ''}</td>
                                    <td>${item.loanType || ''}</td>
                                    <td>${item.wiStatus || ''}</td>
                                    <td>${item.AppType || ''}</td>
                                    <td>${item.rejectReason || ''}</td>
                                    <td>${item.doRemarks || ''}</td>
                                    <td>${item.DOB || ''}</td>
                                    <td>${item.aadhaar || ''}</td>
                                    <td>${item.panNo || ''}</td>
                                    <td>${item.voterID || ''}</td>
                                    <td>${item.passportNo || ''}</td>
                                    <td>${item.driveLic || ''}</td>
                                    <td>${item.gstNo || ''}</td>
                                    <td>${item.CorpID || ''}</td>
                                </tr>`;
                        });
                        here.closest('.det').find('.losdedupetable').find('tbody').html(rows);
                        here.closest('.det').find('.loscount').val(data.length);
                        here.closest('.det').find('.losflag').val('true');
                        hideLoader();
                    } else if (response.status === 'Y') {
                        hideLoader();
                        here.closest('.det').find('.losdedupetable').find('tbody').html('<tr  class="bg-light-whitetr"><td colspan="15"><b>' + response.msg + '</b></td></tr>');
                        here.closest('.det').find('.losflag').val('true');
                    } else {
                        alertmsg(response.msg);
                        hideLoader();
                    }
                },
                error: function (xhr) {
                    hideLoader();
                    alertmsg('Error: ' + xhr.responseText);
                }
            });
        }
    });

    $('#loanbody').on('click','.findedup-button', function(e) {
        e.preventDefault();
        e.stopPropagation();
        var here=$(this);
        var mob=here.closest('.det').find('.basic_mob').val();
        var email=here.closest('.det').find('.basic_email').val();
        if(mob.length<=1 || email.length<=1 ){
            alertmsg('Kindly Enter Mobile Number And Email');
        }
        else {
            showLoader();
            var data = {
                winum: $('#winum').val(),
                slno: $('#slno').val(),
                appid: here.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
                mobno: mob,
                email: email
            }
            $.ajax({
                url: 'api/fin-dedupe',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: function (response) {
                    if (response.status === 'S') {
                        let rows = '';
                        var data = JSON.parse(response.msg);
                        data.forEach(item => {
                            rows += `<tr class="bg-light-whitetr">
                                    <td><input type="radio" name="dedupcustid" class="form-check-input dedupcustid" value="${item.customerid}" checked/></td>
                                    <td>${item.customerid}</td>
                                    <td>${item.name || ''}</td>
                                    <td>${item.emailid || ''}</td>
                                    <td>${item.mobilephone || ''}</td>
                                    <td>${item.voterid || ''}</td>
                                    <td>${item.aadhar_ref_no || ''}</td>
                                    <td>${item.pan || ''}</td>
                                    <td>${item.dob ? item.dob.substring(0, 10) : ''}</td>
                                    <td>${item.tds_customerid || ''}</td>
                                </tr>`;
                        });
                        here.closest('.det').find('.fincount').val(data.length);
                        here.closest('.det').find('.finflag').val('true');
                        here.closest('.det').find('.findedupetable').find('tbody').html(rows);
                        hideLoader();
                    } else if (response.status === 'Y') {
                        hideLoader();
                        here.closest('.det').find('.finflag').val('true');
                        here.closest('.det').find('.findedupetable').find('tbody').html('<tr  class="bg-light-whitetr"><td colspan="10"><b>' + response.msg + '</b></td></tr>');
                    } else {
                        alertmsg(response.msg);
                        hideLoader();
                    }
                },
                error: function (xhr) {
                    hideLoader();
                    alertmsg('Error: ' + xhr.responseText);
                }
            });
        }
    });

});


function fileToBase64(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result.split(',')[1]);
        reader.onerror = error => reject(error);
    });
}
function uploadFile(form,key) {
    const fileval=form.find('.comm_proof')[0].value==="";

    var formDataArray = form.serializeArray(); // Serialize form data to array
    var data = formDataArray.map(function (item) {
        return {key: item.name, value: item.value}; // Transform to key-value pair objects
    });
    if(form.find('.comm_proof_flg').val()==='N' && (!form.find('.sameAsPermanent').is(':checked') || form.find('.permanentResidenceType').val()==='R2')  && fileval){
        alertmsg("please upload communication address proof");
        hideLoader();
    }
    else if(form.find('.losflag').val()==='false'){
        alertmsg("Kindly Click on LOS Dedup Button!!");
        hideLoader();
    }
    else if(form.find('.finflag').val()==='false'){
        alertmsg("Kindly Click on Finacle Dedup Button!!");
        hideLoader();
    }
    else if(!fileval) {
        const file= form.find('.comm_proof')[0].files[0];


        return fileToBase64(file).then(base64 => {
            var jsonBody = {
                id: key,
                slno: $('#slno').val(),
                winum: $('#winum').val(),
                appid: form.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
                data: data,
                reqtype: form.attr('data-code'),
                DOC_ARRAY: [{
                    DOC_EXT: file.name.split('.').pop(),
                    DOC_NAME: "COM_ADR_PROOF",
                    DOC_BASE64: base64
                }]
            };
            return $.ajax({
                url: 'api/save-data',
                type: 'POST',
           //     async: false,
                contentType: 'application/json', // Set content type to JSON
                data: JSON.stringify(jsonBody)
            });
        });
    }
    else{
        var jsonBody = {
            id: key,
            slno: $('#slno').val(),
            winum: $('#winum').val(),
            appid: form.closest('.det').closest('.tab-pane').find('.generaldetails').find('.appid').val(),
            data: data,
            reqtype: form.attr('data-code'),
            DOC_ARRAY:[]
        };
        return $.ajax({
            url: 'api/save-data',
            type: 'POST',
        //    async: false,
            contentType: 'application/json', // Set content type to JSON
            data: JSON.stringify(jsonBody)
        });
    }
}

function basicsave(form,key,callback) {
    uploadFile(form,key)
        .then(response => {
            if(response.status==='S' || response.status==='W')
            {
                form.find('.appid').val(response.appid);
                disableFormInputs(form);
                notyalt('Basic Details Saved !!')
                if(response.status==='W')
                    alertmsg(response.msg);
                scrolltoId('appList');
                callback(true);
            }
            else{
                alertmsg(response.msg);
                callback(false);
            }
        })
        .catch(error => {
            alertmsg('Error during Saving data');
            callback(false);
        });
}


