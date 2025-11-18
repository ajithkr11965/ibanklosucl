$(document).ready(function () {

    var isComplete = ($('#isCompleted').val() === 'true');

    $('#backbtn').on('click', function (e) {
        e.preventDefault();
        $('#slnobk').val($('#slno').val());
        var search = $('#slnobk').val();
        var queue = $('#queue').val();
        if (queue == 'BS' && !search) {
            $('#redirecturl').val('bslist');
        } else if (queue == 'CA'&& !search) {
            $('#redirecturl').val('calist');
        } else if (queue == 'BD'&& !search) {
            $('#redirecturl').val('boglist');
        } else if (queue == 'BM'&& !search) {
            $('#redirecturl').val('bmlist');
         } else if (queue == 'RM'&& !search) {
            $('#redirecturl').val('cpcmakerlist');
        } else if (queue == 'WA'&& !search) {
            $('#redirecturl').val('waiverlist');
        }  else if (queue == 'ACOPN'&& !search) {
            $('#redirecturl').val('bogacctopn');
        } else {
            $('#redirecturl').val('dashboard');
        }
        $('#backform').submit();
    });


    if ($('#lockflg').val() === 'Y') {
        $('button').not('#backbtn').remove();
        $('.save-button').remove();
        $('.edit-button').remove();
        $('.comon').remove(); // remove loan,vehicle,eligible tab save button
        $('.closeapplicant').remove(); // to remove close option in Co Applicant
        $('#addcoapp').remove();
        $('#addguarantor').remove();
        var queue = $('#queue').val();
        if (queue == 'BD') {
            $('.cifCreationDiv').find('input, button, select, textarea').prop('disabled', true);
            $('#bogfinalsubmit').prop('disabled', true);
        }
        alertmsg('This workitem is locked by PPC ' + $('#lockuser').val());
    }
// Example usage
    var currentTab = $('#currentTab').val();
    $('.apptype').removeClass('show active alert-primary');
    $('.tab-pane').removeClass('show active');
    $('#tab-' + parseCode(currentTab, 'app')).addClass('show active');
    $('a.apptype[data-app="' + parseCode(currentTab, 'app') + '"]').addClass('show active  alert-primary');
    $('.details').removeClass('active');
    $('.details[data-code="' + parseCode(currentTab, 'det') + '"]').addClass('active');
    $('form.det[data-code="' + currentTab + '"]').show();

    $('#checkButton').click(function () {
        checkSaveButtons();
    });


    var modify = $('#modify').val();
    var modifycnt = $('#modifycnt').val();
    if (modify === 'true') {
        //Modfn(modifycnt);
    }
    Modfn(modifycnt);

    var $docList = $('#docList');
    $docList.hide();
    var bpmerror = $('#bpmerror').val();
    if (bpmerror.length > 0) {
        alertmsg(bpmerror);
    } else {
        $('#toggleList , #parentToggle').on('click', function () {
            var isVisible = $docList.is(':visible');
            $('#appList').removeClass('w-100');
            var id = $(this).attr('id');
            var sendUrl;
            if (id === 'toggleList')
                sendUrl = 'api/co-app-bpm';
            else
                sendUrl = 'api/getParentBpm';
            if (!isVisible) {
                var jsonBody = {
                    slno: $('#slno').val(),
                    winum: $('#winum').val(),
                    reqtype: getapp() + '-' + getcode()
                };
                $.ajax({
                    url: sendUrl,
                    type: 'POST',
                    data: JSON.stringify(jsonBody),
                    async: false,
                    contentType: 'application/json',
                    success: function (response) {
                        if (response.status === 'S') {
                            $('#docList').html('');
                            $('#docList').append('<iframe frameborder="0" class="w-100 h-100 border-0"  src="' + response.msg + '"></iframe>')
                            $docList.show(); // Make the document list visible using jQuery
                            // Initialize Split.js if not already initialized
                            if (!$docList.data('initialized')) {
                                Split(['#appList', '#docList'], {
                                    sizes: [50, 50], // Initial split ratio
                                    minSize: [200, 200], // Minimum size of each pane
                                    gutterSize: 10, // Width of the gutter
                                    cursor: 'col-resize', // Cursor type on gutter hover
                                    gutter: function (index, direction) {
                                        var gutter = $('<div>', {
                                            'class': 'gutter gutter-' + direction
                                        });
                                        return gutter[0]; // Return DOM element from jQuery object
                                    }
                                });
                                $docList.data('initialized', true); // Mark as initialized using jQuery
                            }
                        } else {
                            alertmsg(response.msg);
                        }
                    },
                    error: function (error) {
                        alertmsg("Error bpM !!!");
                        hideLoader();
                        coApplicantCount--;
                    }
                });
            } else {
                $docList.hide(); // Hide the document list using jQuery
                $('.gutter').remove(); // Remove gutter using jQuery
                $docList.removeData('initialized'); // Reset initialization flag using jQuery
                $('#appList').addClass('w-100'); // Add class using jQuery
            }
        });
    }


    //   cloneForm('A', '1');
    showForm();
    $('#loanapp').on('click', '.apptype', function (e) {
        console.log($(this))
        e.preventDefault();
        $('.apptype').removeClass('show active alert-primary');
        $(this).addClass('show active  alert-primary');
        showForm();
    });
    $('#appList').on('click', '.nav-item', function (e) {
        if ($('#docList').is(':visible'))
            $('#toggleList').trigger('click');
    });
    $('#loanapp').on('click', '.closeapplicant', function (e) {
        e.preventDefault();
        e.stopPropagation();
        var here = $(this);
        var msg = "Are you Sure to Delete " + here.prev("span").text();
        confirmmsg(msg).then(function (confirmed) {
            if (confirmed) {
                var code = here.data('closecode');
                var appid = $('#tab-' + code).find('.generaldetails').find('.appid').val();

                var jsonBody = {
                    slno: $('#slno').val(),
                    winum: $('#winum').val(),
                    appid: appid
                };
                $.ajax({
                    url: 'api/del-coapp',
                    type: 'POST',
                    data: JSON.stringify(jsonBody),
                    async: false,
                    contentType: 'application/json',
                    success: function (response) {
                        if (response.status === 'S') {
                            if (here.prev("span").text().indexOf('GUARANTOR') == -1) {
                                coApplicantheadCount--;
                                // coApplicantCount--;
                            }
                            $('a.apptype[data-app=' + code + ']').parent('.nav-item').remove();
                            $('#tab-' + code).remove();
                            renameheading();
                            showFormWithCode("A", "1");
                            initializeForm(coApplicantheadCount > 0);
                            $('#addguarantor').show();
                            addCoApplicantBtn.prop('disabled', false);
                            addGuarantor.prop('disabled', false);
                        } else {
                            alertmsg(response.msg);
                        }
                    },
                    error: function (error) {
                        alertmsg("Error  !!!");
                        hideLoader();
                        coApplicantCount--;
                    }
                });
            }
        });
    });

    let coApplicantCount = $('#modifycnt').val();
    let coApplicantheadCount = $('#modifycnt').val();


    $('#addguarantor').on('click', function (e) {
        e.preventDefault();
        showLoader();
        var jsonBody = {
            slno: $('#slno').val(),
            winum: $('#winum').val(),
            reqtype: 'G-1'
        };
        $.ajax({
            url: 'api/co-app-bpm',
            type: 'POST',
            data: JSON.stringify(jsonBody),
            async: false,
            contentType: 'application/json',
            success: function (response) {
                hideLoader();
                if (response.status === 'S') {
                    $('#isCompleted').val('false');
                    addCoApplicantBtn.prop('disabled', true);
                    var li = $('.apptype.active').closest('.nav-item').clone();
                    var clonedLi = $('.apptype.active').clone();
                    $('.apptype.active').removeClass('active');
                    clonedLi.attr('data-app', 'G-1');
                    clonedLi.attr('href', 'tab-G-1');
                    clonedLi.html('<i class="ph-user-circle-plus "></i><span data-applicationtype="G">GUARANTOR</span>');
                    clonedLi.addClass('active');
                    clonedLi.append('<button type="button" class="btn-close closeapplicant pe-5" data-closecode="G-1"></button>')
                    li.html(clonedLi);
                    li.appendTo("ul#loanapp");
                    cloneForm('G-1', 'G');
                    showForm();
                    $('.details[data-code="1"]').trigger('click');
                    sections = $("#tab-G-1 > .det");
                    initializeForm(true);
                    $('#addguarantor').hide();
                } else {
                    alertmsg(response.msg);
                }
            },
            error: function (error) {
                alertmsg("Error Validating uid !!!");
                hideLoader();
            }
        });
    });


    $('#addcoapp').on('click', function (e) {
        e.preventDefault();
        showLoader();

        coApplicantCount++;
        var jsonBody = {
            slno: $('#slno').val(),
            winum: $('#winum').val(),
            reqtype: 'C-' + coApplicantCount
        };
        $.ajax({
            url: 'api/co-app-bpm',
            type: 'POST',
            data: JSON.stringify(jsonBody),
            async: false,
            contentType: 'application/json',
            success: function (response) {
                hideLoader();
                if (response.status === 'S') {
                    $('#isCompleted').val('false');
                    addCoApplicantBtn.prop('disabled', true);
                    addGuarantor.prop('disabled', true);
                    coApplicantheadCount++;
                    var li = $('.apptype.active').closest('.nav-item').clone();
                    var clonedLi = $('.apptype.active').clone();
                    $('.apptype.active').removeClass('active');
                    clonedLi.attr('data-app', 'C-' + coApplicantCount);
                    clonedLi.attr('href', 'tab-C' + coApplicantCount);
                    clonedLi.html('<i class="ph-user-circle-plus "></i><span data-applicationtype="C">Co-Applicant ' + coApplicantheadCount + '</span>');
                    clonedLi.addClass('active');
                    clonedLi.append('<button type="button" class="btn-close closeapplicant pe-5" data-closecode="C-' + coApplicantCount + '"></button>')
                    li.html(clonedLi);
                    li.appendTo("ul#loanapp");
                    cloneForm('C-' + coApplicantCount, 'C');
                    showForm();
                    $('.details[data-code="1"]').trigger('click');
                    sections = $("#tab-C-" + coApplicantCount + " > .det");
                    initializeForm(coApplicantheadCount !== 0);

                } else {
                    alertmsg(response.msg);
                    coApplicantCount--;
                }
            },
            error: function (error) {
                alertmsg("Error Validating uid !!!");
                hideLoader();
                coApplicantCount--;
            }
        });

    });


    let sections = $("#tab-A > .det");
    const addCoApplicantBtn = $("#addcoapp");

    const addGuarantor = $("#addguarantor");
    if (!isComplete) {
        addGuarantor.prop('disabled', true);
        addCoApplicantBtn.prop('disabled', true);
    }

    function bool(c) {
        return c !== 'false' && Boolean(c);
    }

    //Details
    $('.details').on('click', function (e) {
        e.preventDefault();
        let prevcompleted;
        let datacode = $('form.det[data-code=' + getapp() + '-' + $(this).attr('data-code').slice(-1) + ']')
        if ($(this).attr('data-code') === '6') {
           // prevcompleted = bool(datacode.prev().find('form.det').attr('data-completed'));
            prevcompleted = bool(datacode.parent().prev().attr('data-completed'));
        } else if ($(this).attr('data-code') === '5') {
            prevcompleted = bool(datacode.parent().prev().attr('data-completed'));
        } else {
            prevcompleted = bool(datacode.prev().attr('data-completed'));
        }

        if (prevcompleted || datacode.hasClass('generaldetails')) {
            $('.details').removeClass('active');
            $(this).addClass('active');
            showForm();
        } else {
            alertmsg('Kindly Save Existing Form');
        }
    });

    // Function to initialize form - disable all but first section
    function initializeForm(c) {
        sections.attr('data-completed', false);
        if (!isComplete) {
            addCoApplicantBtn.prop('disabled', true);
        } else {
            addCoApplicantBtn.prop('disabled', c);
        }
    }

    // Function to handle save actions
    function handleSave(currentSection) {


        let nextSection = null;
        if (currentSection.attr('data-code').slice(-1) === "4") {
            nextSection = currentSection.next(".program-itr").find("form.det");
        } else if (currentSection.attr('data-code').slice(-1) === "5") {
            nextSection = currentSection.closest('.program-itr').next('form.det');
        } else {
            nextSection = currentSection.next(".det");
        }
        console.log(nextSection);
        showLoader();
        if (currentSection.valid()) {
            let fn = validators[getcode()];
            fn.save(currentSection, fn.key, function (success) {
                if (success) {
                    if (nextSection.length > 0) {
                        var code = nextSection.attr('data-code').slice(-1);
                        //  code=code[code.length -1];
                        $('.details').removeClass('active');
                        $('.details[data-code="' + code + '"]').addClass('active');
                        currentSection.hide();
                        //  fetchFormData(validators[getcode()].key,nextSection.attr('data-code'));

                        currentSection.attr('data-completed', true);
                        nextSection.show();

                        hideLoader();
                    } else {
                        currentSection.attr('data-completed', true);
                        // Last section saved, handle co-applicant logic
                        checkAllSectionsCompleted();

                        hideLoader();
                    }
                } else {
                    hideLoader();
                    console.log("Operation failed.");
                    // Handle failure case
                }
            });
        } else {
            hideLoader();
            alertmsg('Kindly fill the required fields before Submitting the form')
        }
    }

    // Check if all sections are completed
    function checkAllSectionsCompleted() {
        let allCompleted = true;
        sections.each(function () {
            if (!$(this).data('completed')) {
                allCompleted = false;
                return false;
            }
        });

        addCoApplicantBtn.prop('disabled', !allCompleted);
        addGuarantor.prop('disabled', !allCompleted);
    }

    // Setup event handlers
    $('#loanbody').on('click', '.save-button', function (e) {
        e.preventDefault();
        e.stopPropagation();
        let currentSection = $(this).closest(".det");
        handleSave(currentSection);
    });
    //  addCoApplicantBtn.prop('disabled', true);

    if ($('#makerCheckerSame').val() == 'Y') {
        blockerMsg('Maker and checker cannot be the same PPC');
        /*
        confirmmsg('Maker and checker cannot be the same PPC').then(function (confirmed) {
            $('#back_form').submit();
        });
        */

    }



});


function savefn(form, key, callback) {

    var formDataArray = form.serializeArray(); // Serialize form data to array
    var data = formDataArray.map(function (item) {
        return {key: item.name, value: item.value}; // Transform to key-value pair objects
    });
    var jsonBody = {
        id: key,
        slno: $('#slno').val(),
        data: data,
        reqtype: form.attr('data-code'),
    };

    $.ajax({
        url: 'api/save-data',
        type: 'POST',
        async: false,
        contentType: 'application/json', // Set content type to JSON
        data: JSON.stringify(jsonBody), // Convert data object to JSON string
        success: function (response) {
            console.log('KYC details submitted successfully:', response);
            callback(true);
        },
        error: function (xhr, status, error) {
            console.log('KYC details submission failed:', error);
            callback(false);
        }
    });
}


var validators = {
    '1': {
        rules: {
            'sibCustomer': {
                required: true
            },
            'residentialStatus': {
                required: true
            },
            'rsm_sol': {
                required: true
            },
            'lh_sol': {
                required: true
            },
            'rah_sol': {
                required: true
            },
            // 'rsm_ppc': {
            //     required: true
            // },
            'canvassed_ppc': {
                required: true
            },
            'relation': {
                required: true
            },
            'custID': {
                required: function (element) {
                    return $(element).closest('.row').prev().find('input.sibCustomer:checked').val() === 'Y' || $(element).closest('.row').prev().find('input.residentialStatus:checked').val() === 'N';
                },
                custPattern: /^[A-Za-z][0-9]{8}$/
            },

        },
        messages: {},
        save: gensavefn,
        key: "GEN",
        desc: "GENERAL DETAILS"
    },
    '2': {
        rules: {
            'pan': {
                required: true,
                panval: true,
                minlength: 10,
                maxlength: 10
            },
            'pandob': {
                required: true,
                minlength: 10,
                maxlength: 10
            },
            'panname': {
                required: true,
                maxlength: 50
            },
            'cif_mode': {
                required: function (element) {
                    $(element).closest('.det').closest('.tab-pane').find('.generaldetails').find('.sibCustomer:checked').val() === 'N'
                }
            },
            'panfile': {
                required: function (element) {
                    return $(element).closest('.det').find('.panfup').val() === 'false';
                }
            },
            'uid': {
                required: function (element) {
                    return $(element).closest('.det').closest('.tab-pane').find('.generaldetails').find('.residentialStatus:checked').val() === 'R';
                },
                numeric: true,
                minlength: 12,
                maxlength: 12
            },
            'uiddob': {
                required: function (element) {
                    return $(element).closest('.det').find('.uidmode:checked').val() === 'M' && $(element).closest('.det').closest('.tab-pane').find('.generaldetails').find('.residentialStatus:checked').val() === 'R';
                },
                numeric: true,
                minlength: 4,
                maxlength: 4
            },
            'uidname': {
                required: function (element) {
                    return $(element).closest('.det').find('.uidmode:checked').val() === 'M' && $(element).closest('.det').closest('.tab-pane').find('.generaldetails').find('.residentialStatus:checked').val() === 'R';
                },
                maxlength: 50
            },
            'uidfile': {
                required: function (element) {
                    return $(element).closest('.det').find('.uidfup').val() === 'false' && $(element).closest('.det').closest('.tab-pane').find('.generaldetails').find('.residentialStatus:checked').val() === 'R';
                }
            },
            'uidotpval': {
                required: function (element) {
                    return $(element).closest('.det').find('.uidmode:checked').val() === 'O';
                },
                numeric: true
            },
            'visafile': {
                required: function (element) {
                    return $(element).closest('.det').find('.visafup').val() === 'false' && $(element).closest('.det').closest('.tab-pane').find('.generaldetails').find('.residentialStatus:checked').val() === 'N';
                }
            },
            'visa': {
                required: function (element) {
                    return $(element).closest('.det').closest('.tab-pane').find('.generaldetails').find('.residentialStatus:checked').val() === 'N';
                }
            },
            'visaocimode': {
                required: function (element) {
                    return $(element).closest('.det').closest('.tab-pane').find('.generaldetails').find('.residentialStatus:checked').val() === 'N';
                }
            },
            'visa_exp': {
                required: function (element) {
                    return $(element).closest('.det').closest('.tab-pane').find('.generaldetails').find('.residentialStatus:checked').val() === 'N' && $(element).closest('.det').find('.visaocimode').val() === 'V';
                }
            },
            'passport': {
                required: function (element) {
                    return $(element).closest('.det').closest('.tab-pane').find('.generaldetails').find('.residentialStatus:checked').val() === 'N';
                }
            },
            'passportfile': {
                required: function (element) {
                    return $(element).closest('.det').find('.passportfup').val() === 'false' && $(element).closest('.det').closest('.tab-pane').find('.generaldetails').find('.residentialStatus:checked').val() === 'N';
                }
            },
            'passportexp': {
                required: function (element) {
                    return $(element).closest('.det').closest('.tab-pane').find('.generaldetails').find('.residentialStatus:checked').val() === 'N';
                },
                minlength: 10,
                maxlength: 10
            }

        },
        messages: {},
        save: kycsavefn,
        key: "KYC",
        desc: "KYC DETAILS"
    },
    '3': {
        rules: {
            'basic_name': {
                required: true,
                maxlength: 50,
                customerNameRegex: true
            },
            'basic_dob': {
                required: true,
                maxlength: 12
            },
            'basic_gender': {
                required: true
            },
            'basic_ftname': {
                // required: true,
                maxlength: 50,
                customerNameRegex: true,
                customCharsNotAllowed: true
            },
            'basic_mtname': {
                required: true,
                maxlength: 50,
                customerNameRegex: true,
                customCharsNotAllowed: true
            },
            'basic_ms': {
                required: true
            },
            'basic_edu': {
                required: true
            },
            'basic_pep': {
                required: true
            },
            'basic_mobcode': {
                required: true
            },
            'basic_mob': {
                required: true,
                numeric: true,
                // mobileLength: function (element) {
                //     return $(element).closest('.det').find('.basic_mobcode').val() === '91';
                // },
            },
            'basic_email': {
                required: true,
                email: true,
                restrictEmailDomain: true,
                customEmailPattern: true
            },
            'basic_saltutation': {
                required: true
            },
            'basic_occupation': {
                required: true
            },
            'basic_annualincome': {
                required: true
            },
            'basic_spname': {
                required: function (element) {
                    return $(element).closest('.det').find('.basic_ms').val() === 'MARID';
                },
                maxlength: 50,
                customerNameRegex: true,
                customCharsNotAllowed: true
            },
            'permanentAddress1': {
                required: true,
                maxlength: 50,
                adrRegex: true
            },
            'permanentAddress2': {
                required: true,
                maxlength: 50,
                adrRegex: true
            },
            'permanentAddress3': {
                maxlength: 50,
                adrRegex: true
            },
            'permanentCity': {
                required: true,
                maxlength: 50,
                adrRegex: true
            },
            'permanentState': {
                required: true,
                maxlength: 50
            },
            'permanentCountry': {
                required: true,
                maxlength: 50
            },
            'permanentPin': {
                required: true,
                digits: function (element) {
                    return $(element).closest('.det').closest('.tab-pane').find('.generaldetails').find('.residentialStatus:checked').val() === 'R';
                },
                alphanumeric: function (element) {
                    return $(element).closest('.det').closest('.tab-pane').find('.generaldetails').find('.residentialStatus:checked').val() === 'N';
                },
                maxlength: 50
            },
            'permanentDurationOfStay': {
                required: true,
                digits: true
            },
            'permanentResidenceType': {
                required: true
            },
            'presentAddress1': {
                required: true,
                maxlength: 50,
                adrRegex: true
            },
            'presentAddress2': {
                required: true,
                maxlength: 50,
                adrRegex: true
            },
            'presentAddress3': {
                maxlength: 50,
                adrRegex: true
            },
            'presentCity': {
                required: true,
                maxlength: 50
            },
            'presentState': {
                required: true,
                maxlength: 50
            },
            'presentCountry': {
                required: true,
                maxlength: 50
            },
            'presentPin': {
                required: true,
                digits: function (element) {
                    return $(element).closest('.det').closest('.tab-pane').find('.generaldetails').find('.residentialStatus:checked').val() === 'R';
                },
                alphanumeric: function (element) {
                    return $(element).closest('.det').closest('.tab-pane').find('.generaldetails').find('.residentialStatus:checked').val() === 'N';
                },
                maxlength: 50
            },
            'presentDurationOfStay': {
                required: true,
                digits: true
            },
            'presentdistanceFromBranch': {
                required: true,
                digits: true
            },
            'permanentdistanceFromBranch': {
                required: true,
                digits: true
            },
            'presentResidenceType': {
                required: true
            },
            'preferred_flag': {
                required: true
            },
            'current_residence_flag': {
                required: true
            },
            'basic_cpoa': {
                required: function (element) {
                    return !$(element).closest('.det').find('.sameAsPermanent').is(':checked') || $(element).closest('.det').find('.permanentResidenceType').val()==='RC2';
                }
            }

        },
        messages: {},
        save: basicsave,
        key: "BASIC",
        desc: "BASIC DETAILS"
    },
    '4': {
        rules: {
            'selemptype': {
                required: true
            },
            'emp-retage': {
                required: function (element) {
                    return $(element).closest('.det').closest('.tab-pane').find('.employmentdetails').find('.selemptype').val() === 'SALARIED';
                },
                digits: true
            },
            'empname': {
                required: function (element) {
                    return $(element).closest('.det').closest('.tab-pane').find('.employmentdetails').find('.selemptype').val() === 'SALARIED' || $(element).closest('.det').closest('.tab-pane').find('.employmentdetails').find('.selemptype').val() === 'PENSIONER';
                }
            },
        },
        messages: {},
        save: employementsave,
        key: "EMP",
        desc: "Employment Details "
    },
    '5': {
        rules: {},
        messages: {},
        save: incomesave,
        key: "PROGRAM",
        desc: "PROGRAM DETAILS"
    },
    '6': {
        rules: {},
        messages: {},
        save: creditsave,
        key: "CREDIT",
        desc: "CREDIT DETAILS"
    },
    '7': {
        rules: {},
        messages: {},
        save: savefn
    },
    '8': {
        rules: {},
        messages: {},
        save: savefn
    },
    '9': {
        rules: {},
        messages: {},
        save: savefn
    },
    '10': {
        rules: {},
        messages: {},
        save: savefn
    },
    '11': {
        rules: {},
        messages: {},
        save: savefn
    }
};
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
    });
}

function blockerMsg(Msg) {
    swalInit.fire({
        html:
            '<div class="d-inline-flex p-2 mb-3 mt-1">' +
            '<img src="assets/images/siblogo.png" class="h-48px" alt="logo">' +
            '</div>' +
            '<h5 class="card-title">' + Msg + '</h5>' +
            '</div>',
        showCloseButton: true
    }).then((result) => {
        if (result.isDismissed || result.isConfirmed) {
            $('#backbtn').click();
        }
    });
}

function toggleSection(sectionToToggle, sectionToCollapse1, sectionToCollapse2) {
    if ($(sectionToToggle).hasClass('show')) {
        $(sectionToToggle).collapse('toggle');
    } else {
        if ($(sectionToCollapse1).hasClass('show')) {
            $(sectionToCollapse1).collapse('toggle');
        }
        if ($(sectionToCollapse2).hasClass('show')) {
            $(sectionToCollapse2).collapse('toggle');
        }
        $(sectionToToggle).collapse('toggle');
    }
}

function dummy(form) {
    log(form.serialize());
    return true;
}

// function disableAccordian() {
//     var state = $('#bottomCard').data('state');
//     if (state === 0) {
//         $('#ebityDetailslink, #loanDetailslink', '#vehDetailslink').css({
//             'pointer-events': 'none',
//             'opacity': '0.5',
//             'cursor': 'not-allowed'
//         });
//     } else if (state === 1) {
//         $('#ebityDetailslink, #loanDetailslink').css({
//             'pointer-events': 'none',
//             'opacity': '0.5',
//             'cursor': 'not-allowed'
//         });
//         $('#vehDetailslink').css({
//             'pointer-events': 'auto',
//             'opacity': '1',
//             'cursor': 'pointer'
//         });
//         $('#vehDetails').css({'cursor': 'pointer'});
//         $('#ebityDetails, #loanDetails').css({'cursor': 'not-allowed'});
//     } else if (state === 2) {
//         $('#vehDetailslink').css({
//             'pointer-events': 'auto',
//             'opacity': '1',
//             'cursor': 'pointer'
//         });
//         $('#loanDetailslink').css({
//             'pointer-events': 'auto',
//             'opacity': '1',
//             'cursor': 'pointer'
//         });
//         $('#vehDetails').css({'cursor': 'pointer'});
//         $('#loanDetails').css({'cursor': 'pointer'});
//         $('#ebityDetailslink').css({
//             'pointer-events': 'none',
//             'opacity': '0.5',
//             'cursor': 'not-allowed'
//         });
//         $('#ebityDetails').css({'cursor': 'not-allowed'});
//     } else if (state === 3) {
// //        $('#loanDetailslink, #ebityDetailslink','#vehDetailslink').css({
//         $('#loanDetailslink , #ebityDetailslink ,#vehDetailslink').css({
//             'pointer-events': 'auto',
//             'opacity': '1',
//             'cursor': 'pointer'
//         });
//         $('#loanDetails').css({'cursor': 'pointer'});
//         $('#ebityDetails').css({'cursor': 'pointer'});
//     }
// }

// var originalBaseColor = '#c6d3da08';
// var originalTextColor = '#009ef7';
// var originalHoverColor = '#c6d3da08';
// var originalSelectedColor = '#2247c9';
//
// function applyColors(baseColor, hoverColor, selectedColor) {
//     $('.nav-link').css({
//         'background-color': '#c6d3da08',
//         'color': '#009ef7'
//     });
//     $('.nav-link').hover(
//         function() { $(this).css('background-color', hoverColor); },
//         function() {
//             if (!$(this).hasClass('active')) {
//                 $(this).css('background-color', '#c6d3da08');
//             }
//         }
//     );
//     $('.nav-link.active').css({
//         'background-color': selectedColor,
//         'color': 'white'
//     });
// }
//
// // Load saved colors from localStorage
// function loadSavedColors() {
//     var baseColor = localStorage.getItem('navBaseColor');
//     var hoverColor = localStorage.getItem('navHoverColor');
//     var selectedColor = localStorage.getItem('navSelectedColor');
//     if (baseColor && hoverColor && selectedColor) {
//         applyColors(baseColor, hoverColor, selectedColor);
//     } else {
//         applyColors(originalBaseColor, originalHoverColor, originalSelectedColor);
//     }
// }
//
// loadSavedColors();
//
// $('.color-button').click(function() {
//     var baseColor = $(this).data('base');
//     var hoverColor = $(this).data('hover');
//     var selectedColor = $(this).data('selected');
//
//     applyColors(baseColor, hoverColor, selectedColor);
//
//     localStorage.setItem('navBaseColor', baseColor);
//     localStorage.setItem('navHoverColor', hoverColor);
//     localStorage.setItem('navSelectedColor', selectedColor);
// });
//
// $('.reset-button').click(function() {
//     applyColors(originalBaseColor, originalHoverColor, originalSelectedColor);
//     localStorage.removeItem('navBaseColor');
//     localStorage.removeItem('navHoverColor');
//     localStorage.removeItem('navSelectedColor');
// });
//
// // Handle active state changes
// $('.nav-link').click(function(e) {
//     e.preventDefault();
//     $('.nav-link').removeClass('active').css({
//         'background-color': '#c6d3da08',
//         'color': '#009ef7'
//     });
//     $(this).addClass('active').css({
//         'background-color': localStorage.getItem('navSelectedColor') || originalSelectedColor,
//         'color': 'white'
//     });
// });


function applyValidation() {
    $(".form-details").each(function () {
        var form = $(this);
        if (form.attr('data-completed') === 'true') {
            disableFormInputs(form);
        }
        var code = form.attr('data-code').slice(-1);
        console.log(code)
        if (validators[code]) {
            form.validate({
                rules: validators[code].rules,
                messages: validators[code].messages,
                ignore: 'input[type=hidden], .select2-search__field', // ignore hidden fields
                errorClass: 'validation-invalid-label',
                successClass: 'validation-valid-label',
                validClass: 'validation-valid-label',
                highlight: function (element, errorClass) {
                    $(element).removeClass(errorClass);
                },
                unhighlight: function (element, errorClass) {
                    $(element).removeClass(errorClass);
                },
                // success: function(label) {
                //      label.addClass('validation-valid-label').text('Success.'); // remove to hide Success message
                // },
                invalidHandler: function (event, validator) {
                    console.log(validator.errorList);
                },
                // Different components require proper error label placement
                errorPlacement: function (error, element) {

                    // Input with icons and Select2
                    if (element.hasClass('select2-hidden-accessible')) {
                        error.appendTo(element.parent());
                    }

                    // Input group, form checks and custom controls
                    else if (element.parents().hasClass('form-control-feedback') || element.parents().hasClass('form-check') || element.parents().hasClass('input-group')) {
                        error.appendTo(element.parent().parent());
                    } else if (element.hasClass('file-input')) {
                        error.insertAfter(element.parent().parent());
                    }
                    // Other elements
                    else {
                        error.insertAfter(element);
                    }
                }

            });
            $.validator.addMethod('custPattern', function (value, element, param) {
                return this.optional(element) || param.test(value);
            }, 'Please enter Valid Input.');
            $.validator.addMethod("panval", function (value, element) {
                return this.optional(element) || /^[A-Za-z]{5}[0-9]{4}[A-Za-z]{1}$/.test(value);
            }, "Invalid PAN");
            $.validator.addMethod("numeric", function (value, element) {
                return /^\d*$/.test(value);
            }, "Only digits allowed");
            $.validator.addMethod("customerNameRegex", function (value, element) {
                return this.optional(element) || /^[a-zA-Z.\s'-]+$/.test(value);
            }, "Use letters, spaces, hyphens, or apostrophes only.");
            $.validator.addMethod("adrRegex", function (value, element) {
                return this.optional(element) || /^[A-Za-z0-9 &,./-]+$/.test(value);
            }, "Address must contain only letters, numbers, spaces, commas, periods, and hyphens.");
            $.validator.addMethod("customCharsNotAllowed", function (value, element) {
                // This regex matches if there are disallowed characters anywhere in the string
                var disallowedRegex = /[@#$%^&*?\\]+/;
                // This regex matches if there are consecutive spaces or any of the previously specified special characters in succession
                var consecutiveRegex = /(\s{2,}|,{2,}|\.\/{2,}|\?{2,}|;{2,}|:{2,}|‘{2,}|’{2,}|“{2,}|”{2,}|\{{2,}|\}{2,}|\[{2,}|\]{2,})/;
                return this.optional(element) || !disallowedRegex.test(value) && !consecutiveRegex.test(value);
            }, "Disallowed or consecutive special characters are not allowed.");
            $.validator.addMethod("mobileLength", function (value, element) {
                return this.optional(element) || value.length === 10;
            }, "Please enter a valid 10-digit mobile number.");
            $.validator.addMethod("restrictEmailDomain", function (value, element) {
                // Convert value to lowercase and check if it ends with @abc.co.in or @abc.co
                return this.optional(element) || !/(@sib\.co\.in|@sib\.co)$/i.test(value.toLowerCase());
            }, "Email address with @abc.co or @sib.co.in domain is not allowed.");

            $.validator.addMethod("customEmailPattern", function (value, element) {
                // Define the pattern to enforce "@" followed by any characters and then a "." and more characters
                return this.optional(element) || /^.+@.+\..+$/.test(value);
            }, "Please enter a valid email address .");
            jQuery.validator.addMethod("alphanumeric", function (value, element) {
                return this.optional(element) || /^[\w.]+$/i.test(value);
            }, "Letters, numbers, and underscores only please");
            $.validator.addMethod("atLeastOneTotalRemittance", function (value, element) {
                const details = getIncomeDetails(element);
                const monthlyOrAbb = details.monthlyorabb === 'MonthSalary';

                // Apply the at least one validation only if the specified conditions are met
                if (details.programCode === 'INCOME' &&
                    details.residentialStatus === 'N' &&
                    details.incomeCheck &&
                    monthlyOrAbb) {

                    const totalRemittanceInputs = $(element).closest('.MonthSalarytable_body').find('.total_remittance');
                    let isAnyFilled = false;

                    totalRemittanceInputs.each(function () {
                        if ($(this).val().trim() !== "") {
                            isAnyFilled = true;
                            return false;  // Break out of the loop
                        }
                    });

                    return isAnyFilled;
                }

                // If the conditions are not met, the validation passes by default
                return true;
            }, "At least one Total Remittance must be filled.");


        }
    });
}

function renameheading() {
    var i = 1;
    $('span[data-applicationtype="C"]').each(function () {
        $(this).text('Co-Applicant ' + i);
        i++;
    });
}

function cloneForm(cloneid, apptype) {
    var cloneval = $('#tab-clone').clone();
    cloneval.addClass('show active');
    cloneval.attr('id', 'tab-' + cloneid);
    cloneval.find('form').each(function () {
        var data_code = $(this).attr('data-code').slice(-1);
        if (data_code == "1")
            $(this).find('.appType').val(apptype);
        $(this).attr('data-code', cloneid + '-' + data_code)
    });
    if (cloneval.find('input[type="file"]').hasClass('file-input*')) {
        cloneval.find('input[type="file"]').removeClass('file-input*').addClass('file-input');
    }
    cloneval.find('span.select2').remove();
    cloneval.appendTo("#loanbody");
    let fn = validators[getcode()];
    // fetchFormData(fn.key,cloneid+'-1');
    applyValidation();
    FileUpload.init();
    basicinit();
    if (apptype === 'G') {
        const incomedetails = loanbody.closest('.det').parent().closest('.tab-pane').find('.program-itr');
        incomedetails.find('.incomeCheck:checked').val("N")
    }
}

function Modfn(c) {
    applyValidation();
    FileUpload.init();
}

function fetchFormData(key, code) {

    var jsonBody = {
        id: key,
        slno: $('#slno').val(),
        reqtype: code,
    };

    $.ajax({
        url: 'api/fetch-form',
        type: 'POST',
        async: false,
        contentType: 'application/json', // Set content type to JSON
        data: JSON.stringify(jsonBody), // Convert data object to JSON string
        success: function (respoonse) {
            var data = respoonse.data;
            console.log(data);
            // Assuming `response` is the JSON array
            data.forEach(function (item) {
                // `item` should contain `name` and `value` properties

                var $input = $('#loanbody').find('form.det[data-code="' + code + '"]').find('[name="' + item.key + '"]');
                log($input);
                //$('#myForm [name="' + item.name + '"]');
                if ($input.is(':radio')) {
                    // If it's a radio button, check the one that matches the value
                    $input.filter('[value="' + item.value + '"]').prop('checked', true).trigger('change');
                } else {
                    // For other input types, just set the value
                    $input.val(item.value);
                }
            });

        },
        error: function () {
            console.log('Error fetching form data');
        }
    });
}

function showForm() {
    $('form.det').hide();
    $('form.det[data-code=' + getapp() + '-' + getcode() + ']').show();
}

function showFormWithCode(app, det) {
    $('form.det').hide();
    $('#tab-' + app).addClass('show active');
    $('a.apptype[data-app=' + app + ']').addClass('active');
    $('form.det[data-code="' + app + '-' + det + '"]').show();
}

function getcode() {
    return $('.details.active').attr('data-code').slice(-1);
}

function getapp() {
    return $('.apptype.active').data('app');
}

function log(c) {
    console.log(c)
}

function disableFormInputs(form) {
    $(form).find('input[type="checkbox"],input[type="text"],input[type="date"],input[type="number"], input[type="radio"], input[type="file"], select , .btn-file ,.save-button').prop('disabled', true);
    $(form).find('.btn-file').addClass('disabled');
}

function parseCode(code, inputType) {
    let parts = code.split('-');
    if (inputType === 'app') {
        if (parts.length === 2) {
            return parts[0];
        } else if (parts.length === 3) {
            return parts[0] + '-' + parts[1];
        }
    } else if (inputType === 'det') {
        return parts[parts.length - 1];
    }
    return null; // or an appropriate default value
}

function enableFormInputs(form) {

    $(form).find('input[type="checkbox"], input[type="text"],input[type="number"],input[type="date"], input[type="radio"], input[type="file"], select , .btn-file , .save-button').prop('disabled', false);
    $(form).find('.btn-file').removeClass('disabled');
    $(form).find('.noneditable').prop('disabled', true);
    $(form).find('.nonreadable').prop('readonly', true);
    $(form).find('input[type="radio"].nonreadable:not(:checked)').prop('disabled', true);

}

function decodeAlertMsg(dataCode) {
    var parts = dataCode.split('-');
    var alertMsg = '';

    if (parts.length === 2 && parts[0] === 'A') {
        alertMsg = 'Applicant  : ' + validators[parts[1]].desc + ' not saved';
        ;
    } else if (parts.length === 3 && parts[0] === 'C') {
        alertMsg = 'Co-applicant : ' + validators[parts[2]].desc + ' not saved';
        // alertMsg = 'Co-applicant-' + parts[1] + ' : ' + validators[parts[2]].desc +' not saved';
    } else if (parts.length === 3 && parts[0] === 'G') {
        alertMsg = 'Guarantor  : ' + validators[parts[2]].desc + ' not saved';
    } else {
        alertMsg = 'Invalid code format';
    }

    return alertMsg;
}


function checkSaveButtons() {

    var allDisabled = true;
    $('form').each(function () {
        var form = $(this);
        form.find('.save-button').each(function () {
            if (!form.attr('data-code').startsWith('T')) {
                if (!$(this).prop('disabled')) {
                    allDisabled = false;
                    return false; // Exit loop early if a non-disabled button is found
                }
            }
        });

        if (!allDisabled) {
            var dataCode = form.attr('data-code');
            if (dataCode)
                alertmsg(decodeAlertMsg(dataCode));
            return allDisabled;
        }

    });
    return allDisabled;
}

// Override Noty defaults
Noty.overrideDefaults({
    theme: 'limitless',
    layout: 'topRight',
    type: 'alert',
    timeout: 2500
});


function notyalt(msg) {
    // new Noty({
    //     layout: 'bottomRight',
    //     text: msg,
    //     type: 'success'
    // }).show();
    swalInit.fire({
        title: 'Success!',
        text: msg,
        icon: 'success',
        confirmButtonClass: 'btn btn-success',
        showCloseButton: true
    });

}
function notyaltInfo(msg) {
    new Noty({
        layout: 'bottomRight',
        text: msg,
        type: 'info'
    }).show();
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

function errormsg(msg) {
    return swalInit.fire({
        title: msg,
        icon: "warning",
        showCancelButton: true,
        showConfirmButton: false,
        cancelButtonText: "Ok",
        button: "close!"
    }).then(function (result) {
        return result.isConfirmed;
    });
}

function alertmsgvert(Msg) {
    // swalInit.fire({
    //     html:
    //         '<div class="d-inline-flex p-2 mb-3 mt-1">' +
    //         '<img src="assets/images/siblogo.png" class="h-48px" alt="logo">' +
    //         '</div>' +
    //         '<h5 class="card-title">' + Msg + '</h5>' +
    //         '</div>',
    //     showCloseButton: true,
    //     grow: 'row'
    // });
    $('#alert_modal .modal-body').html(Msg);
    $('#alert_modal').modal('show');
}


function scrolltoId(divId) {
    var $element = $('#' + divId);

    if ($element.length) {
        var offset = $element.offset().top;
        var additionalOffset = 0; // Adjust this value based on your needs, e.g., height of a fixed header

        $('html, body').animate({
            scrollTop: offset - additionalOffset
        }, 600, function () {
        });
    }
}

function toggleExpand(row) {
    var nextRow = row.nextElementSibling;
    if (nextRow && nextRow.classList.contains('expandable-content')) {
        nextRow.style.display = nextRow.style.display === 'table-row' ? 'none' : 'table-row';
    }
}
function updateAccordionStyle(accordionId, isCompleted) {
        const accordion = document.querySelector(`#${accordionId}`);
        if (accordion) {
            const label = accordion.querySelector('label');
            if (label) {
                if (isCompleted) {
                    label.classList.remove('btn-active-light-primary');
                    label.classList.add('btn-active-light-success', 'show');
                } else {
                    label.classList.remove('btn-active-light-success', 'show');
                    label.classList.add('btn-active-light-primary');
                }
            }
        }
    }
function formatIndianCurrency(amount) {
            const formatter = new Intl.NumberFormat('en-IN', {
                style: 'currency',
                currency: 'INR',
                minimumFractionDigits: 0,
                maximumFractionDigits: 0
            });
            return formatter.format(amount);
        }
