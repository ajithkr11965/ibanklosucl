$(document).ready(function () {
    $('#loancheckerbody').on('click', '.runhunterCheck', function (e) {

        var vhidbreVal = $('#vhidbreVal').val();
        console.log("in bre got" + vhidbreVal);
        var applicantHunterData = $(this).closest("tr").find(".applicantHunterData");
        var applicantHunterData = $(this).closest("tr").find(".applicantHunterData");
        var applicantId = applicantHunterData.data("applicantid");
        var button = document.querySelector("#kt_button_hunt_" + applicantId);
        button.setAttribute("data-kt-indicator", "on");
        button.setAttribute("disabled", true);
        var wiNum = $('#winum').val();
        var slno = $('#slno').val();
        var $row = $(this).closest("tr");
        $.ajax({
            url: 'api/checker/experian-hunter',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                applicantId: applicantId.toString(),
                wiNum: wiNum,
                slno: slno
            }),
            success: function (response) {
                console.log('Experian Hunter API call successful:', response);
                updateHunterResults($row, response);
                button.removeAttribute("data-kt-indicator");
                button.setAttribute("disabled", false);
                // Handle the response as needed
            },
            error: function (xhr, status, error) {
                console.error('Error calling Experian Hunter API:', error);
                button.removeAttribute("data-kt-indicator");
                button.setAttribute("disabled", false);
                // Handle the error as needed
            }
        });

        function updateHunterResults($row, response) {
            var currentDate = new Date().toLocaleString();
            $row.find('.runDate').text(currentDate);
            $row.find('.score').text(response.score);
            $row.find('.decision').text(response.decision);
            $row.find('.status').text(response.status);
            $row.find('.runhunterCheck').text('Re-run Hunter');
        }
    });
    $('#loancheckerbody').on('click', '.runblacklistCheck', function (e) {
        console.log("blacklist check initiated");
        var applicantData = $(this).closest("tr").find(".applicantData");
        var applicantId = applicantData.data("applicantid");
        var button = document.querySelector("#kt_button_" + applicantId);
        button.setAttribute("data-kt-indicator", "on");
        button.setAttribute("disabled", true);
        event.preventDefault();
        var formData = {
            "request": {
                "DOB": applicantData.data("dob"),
                "Pan": applicantData.data("pan"),
                "Passport": applicantData.data("passport")
            },
            "mock": false,
            "apiName": "blacklistExactMatch",
            "workItemNumber": $('#winum').val(),
            "origin": applicantData.data("applicantid"),
            "slno": $('#slno').val()
        };

        $.ajax({
            type: "POST",
            url: "api/checker/checkBlacklist",
            contentType: "application/json",
            data: JSON.stringify(formData),
            success: function (response) {
                var rowCells = applicantData.closest("tr").find("td");
                if (response.blacklisted) {
                    rowCells.eq(7).text(new Date().toLocaleDateString());
                    rowCells.eq(8).html('<span class="badge bg-danger">Blacklisted</span>');
                } else {
                    rowCells.eq(7).text(new Date().toLocaleDateString());
                    rowCells.eq(8).html('<span class="badge bg-success">Not Blacklisted</span>');
                }
                button.removeAttribute("data-kt-indicator");
                validateAllApplicants();
                button.setAttribute("disabled", false);
            },
            error: function (xhr, status, error) {
                $("#result").text("Error occurred: " + error);
                button.removeAttribute("data-kt-indicator");
                button.setAttribute("disabled", false);
            }
        });
    });

    $('#loancheckerbody').on('click', '.hunterlistSave', function (e) {
        console.log("blacklist save initiated");
        var winum = $('#winum').val();
        var slno = $('#slno').val();
        var formData = {
            "slno": slno,
            "wiNum": winum,
            "identifier": "HUNTER",
            "updValue": "Y"
        };
        $.ajax({
            type: "POST",
            url: "api/checker/updateBlacklistOption",
            contentType: "application/json",
            data: JSON.stringify(formData),
            success: function (response) {
                if (response.msg === "SUCCESS") {
                    $('#huntertable').collapse('hide');
                    $('#blacklisttable').collapse('show');
                    alertmsg("Hunter check details updated");
                    updateAccordionStyle('hunterlistDetails', true);
                } else {
                    alertmsg("Hunter check details update failed - " + response.msg);
                }
            },
            error: function (xhr, status, error) {
                alert("Error occurred: " + error);
            }
        });
    });
    $('#loancheckerbody').on('click', '.blacklistSave', function (e) {
        console.log("blacklist save initiated");
        var winum = $('#winum').val();
        var slno = $('#slno').val();
        var formData = {
            "slno": slno,
            "wiNum": winum,
            "identifier": "BLACKLIST",
            "updValue": "Y"
        };
        $.ajax({
            type: "POST",
            url: "api/checker/updateBlacklistOption",
            contentType: "application/json",
            data: JSON.stringify(formData),
            success: function (response) {
                if (response.msg === "SUCCESS") {
                    $('#blacklisttable').collapse('hide');
                    $('#bretable').collapse('show');
                    alertmsg("Blacklist check details updated");
                    updateAccordionStyle('blacklistDetails', true);
                } else {
                    alertmsg("Blacklist check details update failed - " + response.msg);
                }
            },
            error: function (xhr, status, error) {
                alert("Error occurred: " + error);
            }
        });
    });
    $('#kt_button_runDKScore').on('click', function () {
        var winum = $('#winum').val();
        var slno = $('#slno').val();
        var $button = $(this);
        var $table = $('#dkScoreTable');
        var button = document.querySelector("#kt_button_runDKScore");
        button.setAttribute("data-kt-indicator", "on");
        //button.setAttribute("disabled", true);

        //$button.prop('disabled', true).html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Running...');

        $.ajax({
            url: 'api/checker/dk-score',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                workItemNumber: winum,
                slno: slno
            }),
            success: function (response) {
                console.log('DK Score API call successful:', response);
                updateDKScoreTable(response.dkScoreItems);
                button.removeAttribute("data-kt-indicator");
                button.removeAttribute("disabled");
                updateAccordionStyle('dkScoreDetails', true);
                $('#dkScoreTableContainer').show();
                $('#additionalDetails').show();
                if($('#kt_button_runDKScore').hasClass("crt")){
                    fetchamberdata(winum,slno);
                }
            },
            error: function (xhr, status, error) {
                button.removeAttribute("data-kt-indicator");
                 button.removeAttribute("disabled");
                console.error('Error calling DK Score API:', error);
                alert('Error running DK Score check. Please try again.');
            },
            complete: function () {
                button.removeAttribute("data-kt-indicator");
                button.removeAttribute("disabled");
            }
        });
    });

    function formatDate(dateString) {
        if (!dateString) return 'N/A';
        var date = new Date(dateString);
        return date.toLocaleString('en-IN', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false
        }).replace(/,/, '');
    }

             function formatDate(dateString) {
                      if (!dateString) return 'N/A';
                      var date = new Date(dateString);
                      return date.toLocaleString('en-IN', {
                          day: '2-digit',
                          month: '2-digit',
                          year: 'numeric',
                          hour: '2-digit',
                          minute: '2-digit',
                          second: '2-digit',
                          hour12: false
                      }).replace(/,/, '');
                  }
    function fetchamberdata(winum,slno){
        $.ajax({
            dataType: "json",
            url: 'api/FetchCRTAmberData',
            type: 'POST',
            async: false,
            data: {
                winum: winum,
                slno: slno
            },
            success: function (response) {
                var colorflg = response.color;
                console.log(colorflg);
                var racemessage = response.racemessage;
                var fcvmessage = response.fcvmessage;
                var cpvmessage = response.cpvmessage;
                var cfrmessage = response.cfrmessage;

                var htmlContent = '';
                if (colorflg === "green") {
                    htmlContent = `
                                <div class="alert alert-dismissible bg-light-success d-flex flex-center flex-column py-10 px-10 px-lg-20 mb-10">
                                    <i class="ki-duotone ki-information-5 fs-5tx text-danger mb-5"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
                                    <div class="text-center">
                                        <h1 class="fw-bold mb-5"> Check Result: Green</h1>
                                    </div>
                                </div>

                            `;
                } else if (colorflg === "amber") {
                    htmlContent = `
                                <div class="alert alert-dismissible bg-light-warning d-flex flex-center flex-column py-10 px-10 px-lg-20 mb-10">
                                    <i class="ki-duotone ki-information-5 fs-5tx text-danger mb-5"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
                                    <div class="text-center">
                                        <h1 class="fw-bold mb-5"> Check Result: Amber</h1>
                                    </div>
                                </div>
                            `;
                } else if (colorflg === "red") {
                    htmlContent = `
                                <div class="alert alert-dismissible bg-light-danger d-flex flex-center flex-column py-10 px-10 px-lg-20 mb-10">
                                    <i class="ki-duotone ki-information-5 fs-5tx text-danger mb-5"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
                                    <div class="text-center">
                                        <h1 class="fw-bold mb-5"> Check Result: Red</h1>
                                    </div>
                                </div>
                            `;
                }
                $('.brestatus').empty();
                console.log(htmlContent);
                // Append the generated HTML to the parent div with class "brestatus"

                $('.brestatus').append(htmlContent);

            },
            error: function (xhr, status, error) {
                console.log('Amber data fetch failed', error);
            }
        });
    }

                  function updateDKScoreTable(dkScoreItems) {
                      var tableHtml = `
        <table id="dkScoreTable" class="table table-sm align-middle table-row-dashed table-row-gray-400 fs-8 gy-3">
            <thead>
                <tr class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">
                    <th>Applicant Type</th>
                    <th>Applicant Name</th>
                    <th>Run Date</th>
                    <th>Bureau Score</th>
                    <th>Race Score</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
    `;

                      dkScoreItems.forEach(function (item) {
    var status = item.status === 'SUCCESS' ? 'Success' : 'Failed';
    var statusClass = item.status === 'SUCCESS' ? 'badge-light-success' : 'badge-light-danger';
    console.log(item);
    if(item.color==="red") {
        status="RACE Score failed";
        statusClass='badge-light-danger';
        alertmsg("RACE Score not in allowed range");
    }

    tableHtml += '<tr class="table-row-gray-400">';
    tableHtml += '<td>' + (item.applicantType !== null && item.applicantType !== undefined && item.applicantType !== '' ? item.applicantType : 'N/A') + '</td>';
    tableHtml += '<td class="text-gray-800 text-hover-primary fs-7 fw-bold">' + (item.applicantName !== null && item.applicantName !== undefined && item.applicantName !== '' ? item.applicantName : 'N/A') + '</td>';
    tableHtml += '<td>' + (item.runDate !== null && item.runDate !== undefined && item.runDate !== '' ? formatDate(item.runDate) : 'N/A') + '</td>';
    tableHtml += '<td>' + (item.score !== null && item.score !== undefined && item.score !== '' ? item.score : 'N/A') + '</td>';
    tableHtml += '<td>' + (item.score !== null && item.score !== undefined && item.score !== '' ? item.raceScore : 'N/A') + '</td>';
    tableHtml += '<td><span class="badge ' + statusClass + '">' + status + '</span></td>';
    tableHtml += '</tr>';
});


                      tableHtml += '</tbody></table>';
                      $('#dkresponsedata').html(tableHtml);
                    //  $('#kt_button_runDKScore').hide(); // Hide the button after successful fetch
                  }



        function getStatusBadge(color,score) {
                    if (color ==="green") {
                        return '<span class="badge bg-success">"+score+"</span>';
                    } else if (color ==="amber") {
                        return '<span class="badge bg-warning">"+score+"</span>';
                    } else {
                        return '<span class="badge bg-danger">"+score+"</span>';
                    }
                }


    $('#loancheckerbody').on('click', '.breFetch', function (e) {
        console.log("BRE check initiated");
        var button = document.querySelector("#kt_button_breDetails");
        button.setAttribute("data-kt-indicator", "on");
        var winum = $('#winum').val();
        var slno = $('#slno').val();
        var formData = {
            "slno": slno,
            "wiNum": winum
        };
        $.ajax({
            url: "api/checker/fetchBRE",
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(formData),
            success: function (response) {
                $('#breDetailView').show();
                $('#bre-response').empty();
                console.log(response);

                if (response.status === "ERROR") {
                    displayErrorMessage(response.message);
                    return;
                }

                if (response.status === "failure" || response.eligibilityFlag !== "green") {
                    displayFailureResponse(response);
                } else {
                    displaySuccessResponse(response);
                }

                updateAccordionStyle('brelistDetails', true);
                button.removeAttribute("data-kt-indicator");
            },
            error: function (xhr, status, error) {
                console.log("Error fetching BRE details: " + error);
                displayErrorMessage("An error occurred while fetching BRE details. Please try again later.");
                button.removeAttribute("data-kt-indicator");
            }
        });
    });

    function displayErrorMessage(message) {
        $('#bre-response').html(`
        <div class="alert alert-danger d-flex flex-column py-10 px-10 px-lg-20 mb-10">
            <div class="text-center">
                <h1 class="fw-bold mb-5">Error Occurred</h1>
            </div>
            <div class="separator separator-dashed border-danger opacity-25 mb-5"></div>
            <div class="mb-9 text-gray-900">
                <p>${message}</p>
            </div>
        </div>
    `);
    }

    function displayFailureResponse(response) {
        const eligibilityData = response.eligibilityData;
        let html = `
        <div class="bg-light bg-opacity-50 rounded-3 p-10 mx-md-5 h-md-100">
            <div class="d-flex flex-center w-60px h-60px rounded-3 bg-light-danger bg-opacity-90 mb-10">
                <i class="ki-duotone ki-abstract-25 text-danger fs-3x"><span class="path1"></span><span class="path2"></span></i>
            </div>
            <h2 class="mb-2"><span><p class="btn btn-lg btn-flex btn-link btn-color-danger">ELIGIBILITY FAILED</p></span></h2>
    `;

        for (let key in eligibilityData) {
            if (key !== "status") {
                let item = eligibilityData[key];
        const itemClass = item.color === 'green' ? 'success' : (item.color === 'amber' ? 'warning' : 'danger');
                html += `
                <div class="card mb-5">
                    <div class="card-header">
                        <h3 class="card-title">${item.eliCode}: ${item.eliDesc}</h3>
                        <div class="card-toolbar">
                            <span class="badge badge-${itemClass}">${item.color.toUpperCase()}</span>
                        </div>
                    </div>
                    <div class="card-body">
                        ${item.eliSub.map(sub => `
                            <div class="mb-3">
                                <p>${sub.Desc}</p>
                                <p><strong>Applicant(s):</strong> ${Array.isArray(sub.applicantName) ? sub.applicantName.join(', ') : sub.applicantName}</p>
                            </div>
                        `).join('')}
                    </div>
                </div>
            `;
            }
        }

        html += '</div>';
        $('#bre-response').html(html);
    }

    function displaySuccessResponse(response) {
        if (response.status !== "SUCCESS") {
            displayErrorMessage("Unexpected response status: " + response.status);
            return;
        }

        const eligibilityClass = response.eligibilityFlag === 'green' ? 'success' : (response.eligibilityFlag === 'amber' ? 'warning' : 'danger')
        const breClass = response.breFlag === 'green' ? 'success' : (response.breFlag === 'amber' ? 'warning' : 'danger');

        let html = `
<div class="bg-light bg-opacity-50 rounded-3 p-10 mx-md-5 h-md-100">
                <div class="d-flex flex-center w-60px h-60px rounded-3 bg-light-${breClass} bg-opacity-90 mb-10">
                    <i class="ki-duotone ki-abstract-25 text-${breClass} fs-3x"><span class="path1"></span><span class="path2"></span></i></div>

                <h2 class="mb-2"><span><p class="btn btn-lg btn-flex btn-link btn-color-${breClass}">${response.breFlag.toUpperCase()}</p></span></h2>
    `;

        for (let key in response.breData) {
            let item = response.breData[key];
            const itemClass = item.color === 'green' ? 'success' : (item.color === 'amber' ? 'warning' : 'danger');
            html += `
            <div class="card mb-5">
                <div class="card-header">
                    <h3 class="card-title">${item.breCode}: ${item.breDesc}</h3>
                    <div class="card-toolbar">
                        <span class="badge badge-${itemClass}">${item.color.toUpperCase()}</span>
                    </div>
                </div>
                <div class="card-body">
        `;

            if (item.breSub && Array.isArray(item.breSub)) {
                item.breSub.forEach(sub => {
                    html += `
                    <div class="mb-3">
                        <p><strong>Applicant:</strong> ${sub.applicantName}</p>
                        <p><strong>Current Value:</strong> ${sub.currentValue}</p>
                        <p><strong>Allowed Range/Value:</strong> ${sub.masterValue}</p>
                    </div>
                `;
                });
            } else if (item.generic === "Y") {
                html += `<p>This is a generic rule.</p>`;
            } else {
                html += '<p>No additional details available.</p>';
            }

            html += `
                </div>
            </div> 
        `;
        }
        html += `
                </div>`;
        console.log(html);
        $('#bre-response').html(html);
    }

    function getApplicantType(type) {
        const types = {
            'A': 'Primary Applicant',
            'C': 'Co-Applicant'
            // Add more types if needed
        };
        return types[type] || 'Applicant';
    }


    function validateAllApplicants() {
        var allNotBlacklisted = true;
        $('#blacklisttable tbody tr').each(function () {
            var status = $(this).find('td').eq(8).text();
            if (status !== 'Not Blacklisted') {
                allNotBlacklisted = false;
                return false;
            }
        });

        if (allNotBlacklisted) {
            $('.blacklistSave').prop('disabled', false);
        } else {
            $('.blacklistSave').prop('disabled', true);
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


    $('#loancheckerbody').on('click', '.runracescoreCheck', function (e) {
        var applicantid = $(this).attr("data-applicantId");
        var wiNum = $('#winum').val();
        var slno = $('#slno').val();
        var $row = $(this).closest("tr");
        console.log('Score:', $row.find('.score'));
        $.ajax({
            url: 'api/checker/runRaceScore',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                applicantId: applicantid.toString(),
                wiNum: wiNum,
                slno: slno
            }),
            success: function (response) {
                console.log('RaceScore API call successful:', response);
                updateRaceScoreResults($row, response);
                // Handle the response as needed
            },
            error: function (xhr, status, error) {
                console.error('Error calling RaceScore API:', error);
                // Handle the error as needed
            }
        });
    });

    function updateRaceScoreResults($row, response) {
        var currentDate = new Date().toLocaleString();
        var jsonobj = JSON.parse(response);
        console.log(response);
        console.log(jsonobj.score);
        console.log(jsonobj.color);
        $row.find('.score').text(jsonobj.score);
        $row.find('.color').text(jsonobj.color);
        $row.find('.runracescoreCheck').text('Re-run Race Score');
    }

    function checkTableColors() {
        var alertColor = 'green';
        var hasAmber = false;
        var hasEmpty = false;
        $('#racescrtbl tbody tr').each(function() {
            var colorTd = $(this).find('td.color').text().trim().toLowerCase();
            if (colorTd === '') {
                hasEmpty = true;
            }
        });
        var labelclass="btn-light-success";
        if (hasEmpty) {
            labelclass="btn-active-light-primary btn-light-primary";
        } else {
            labelclass="btn-active-light-success btn-light-success";
        }
        $("#racescrorelabel").removeClass("btn-active-light-primary");
        $("#racescrorelabel").removeClass("btn-light-primary");
        $("#racescrorelabel").addClass(labelclass);
    }


    $('#loancheckerbody').on('click', '#racescoreSave', function (e) {
        var wiNum = $('#winum').val();
        var slno = $('#slno').val();
        $.ajax({
            url: 'api/checker/saveRaceScoreAmber',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                wiNum: wiNum,
                slno: slno
            }),
            success: function (response) {
                checkTableColors();
                console.log('RaceScore API call successful:', response);
                alertmsg("Race Score Details successfully saved");
                // Handle the response as needed
                $.ajax({
                    dataType: "json",
                    url: 'api/FetchCRTAmberData',
                    type: 'POST',
                    async: false,
                    data: {
                        winum: wiNum,
                        slno: slno
                    },
                    success: function (response) {
                        var colorflg = response.color;
                        console.log(colorflg);
                        var racemessage = response.racemessage;
                        var fcvmessage = response.fcvmessage;
                        var cpvmessage = response.cpvmessage;
                        var cfrmessage = response.cfrmessage;

                        var htmlContent = '';
                        if (colorflg === "green") {
                            htmlContent = `
                                <div class="alert alert-dismissible bg-light-success d-flex flex-center flex-column py-10 px-10 px-lg-20 mb-10">
                                    <i class="ki-duotone ki-information-5 fs-5tx text-danger mb-5"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
                                    <div class="text-center">
                                        <h1 class="fw-bold mb-5"> Check Result: Green</h1>
                                    </div>
                                </div>

                            `;
                        } else if (colorflg === "amber") {
                            htmlContent = `
                                <div class="alert alert-dismissible bg-light-warning d-flex flex-center flex-column py-10 px-10 px-lg-20 mb-10">
                                    <i class="ki-duotone ki-information-5 fs-5tx text-danger mb-5"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
                                    <div class="text-center">
                                        <h1 class="fw-bold mb-5"> Check Result: Amber</h1>
                                    </div>
                                </div>
                            `;
                        } else if (colorflg === "red") {
                            htmlContent = `
                                <div class="alert alert-dismissible bg-light-danger d-flex flex-center flex-column py-10 px-10 px-lg-20 mb-10">
                                    <i class="ki-duotone ki-information-5 fs-5tx text-danger mb-5"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
                                    <div class="text-center">
                                        <h1 class="fw-bold mb-5"> Check Result: Red</h1>
                                    </div>
                                </div>
                            `;
                        }
                        $('.brestatus').empty();
                        console.log(htmlContent);
                        // Append the generated HTML to the parent div with class "brestatus"

                        $('.brestatus').append(htmlContent);

                    },
                    error: function (xhr, status, error) {
                        console.log('Amber data fetch failed', error);
                    }
                });
            },
            error: function (xhr, status, error) {
                console.error('Error calling RaceScore API:', error);
                alertmsg("Error during race score details save");
                // Handle the error as needed
            }
        });
    });


});