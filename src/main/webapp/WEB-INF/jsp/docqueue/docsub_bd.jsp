<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanSubqueueTask" %>
<%@ page import="java.util.Optional" %>
<%@ page import="com.sib.ibanklosucl.model.doc.LegalityInvitees" %>
<%@ page import="com.sib.ibanklosucl.service.doc.ManDoc" %>
<%@ page import="com.sib.ibanklosucl.model.doc.ManDocData" %>
<%@ page import="com.sib.ibanklosucl.model.Misrct" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script>
  $(document).ready(function () {
    $("#docDetailsForm").validate({
      rules: {
        'docMode': {
          required: true
        }
      },
      messages: {},
      ignore: 'input[type=hidden], .select2-search__field',
      highlight: function (element, errorClass) {
        $(element).removeClass('is-invalid').removeClass('is-valid').removeClass('validation-invalid-label');
      },
      unhighlight: function (element, errorClass) {
        $(element).removeClass('validation-invalid-label').removeClass('is-invalid').removeClass('is-valid');
      },
      errorPlacement: function (error, element) {
        element.removeClass('is-valid').addClass('is-invalid');
        error.css({
          'padding-left': '23px',
          'margin-right': '20px',
          'padding-bottom': '2px',
          'color': 'red',
          'font-size': 'small'
        });
        error.addClass("validation-invalid-label");
        //   log(element);
        if (element.hasClass('checkbox-input')) {
          element.parent().parent().parent().append(error);
        } else {
          error.insertAfter(element);
        }
      }
    });

    // Handle save button click
    $("#docsaveBtn").on('click', function (e) {
      e.preventDefault();
      if ($("#docDetailsForm").valid()) {
        var selectedVal = $('[name="docMode"]:checked').val();
        var msg = 'Are you sure you want to proceed with Documentation(Digitally), Once Submitted Cannot be taken for Waivers <br/> <span class="text-danger">Before proceeding verify that the branch signatory name <span class="text-success">'+$('#brHead').val()+ '</span> matches the name on the Aadhaar card.</span>';
        if (selectedVal === 'M')
          msg = 'Are you sure you want to proceed with Documentation(Manually), Once Submitted Cannot be taken for Waivers & Document uploaded if Any will be Reset'
        confirmmsg(msg)
                .then(function (result) {
                  if (result) {
                    showLoader();
                    var dto = {
                      docMode: $('[name="docMode"]:checked').val(),
                      slNo: $('#slno').val(),
                      wiNum: $('#winum').val()
                    };
                    $.ajax({
                      url: 'doc/updateLegality', // Update with your API endpoint
                      type: 'POST',
                      data: JSON.stringify(dto),
                      contentType: 'application/json',
                      success: function (response) {
                        hideLoader();
                        if (response.status === 'S') {
                          if (selectedVal === 'D') {
                            notyalt('Invitations sent successfully.');
                            var data_ = JSON.parse(response.msg);
                            showLegal(data_);
                            $('#man-div').hide();
                            $('#digitDiv').show();
                          } else {
                            $('#man-div-html').html(response.msg);
                            $('#man-div').show();
                            $('#digitDiv').hide();
                            downloadPDF();
                          }
                        } else {
                          alertmsg('Failed: ' + response.msg);
                        }
                      },
                      error: function (xhr, status, error) {
                        hideLoader();
                        var err_data = xhr.responseJSON;
                        if (err_data.msg) {
                          alertmsgvert(err_data.msg);
                        } else {
                          alertmsg('An error occurred: ' + error);
                        }
                      }
                    });
                  }
                });
      }
    });

    $("#doclegalStatus").on('click', function (e) {
      e.preventDefault();
      if ($("#docDetailsForm").valid()) {
        msg = 'Are you sure you want to proceed with Documentation Status Fetch'
        confirmmsg(msg)
                .then(function (result) {
                  if (result) {
                    showLoader();
                    var dto = {
                      docMode: 'LS',
                      slNo: $('#slno').val(),
                      wiNum: $('#winum').val(),
                      legalDocID: $('#legalDocID').val()
                    };
                    $.ajax({
                      url: 'doc/updateLegality', // Update with your API endpoint
                      type: 'POST',
                      data: JSON.stringify(dto),
                      contentType: 'application/json',
                      success: function (response) {
                        hideLoader();
                        if (response.status === 'S') {
                          var data_ = JSON.parse(response.msg);
                          showLegal(data_);
                          if (data_.completed) {
                            notyalt('Documentation Completed Successfully.');
                            $('#docSv').show();
                            $('#docsaveBtn').remove();
                            $('#allUploadBtn').remove();
                            $('input[name=docMode]').attr("disabled", true);
                          } else {
                            notyalt('Status Fetched Successfully.');
                          }
                        } else {
                          alertmsg('Failed: ' + response.msg);
                        }
                      },
                      error: function (xhr, status, error) {
                        hideLoader();
                        var err_data = xhr.responseJSON;
                        if (err_data.msg) {
                          alertmsgvert(err_data.msg);
                        } else {
                          alertmsg('An error occurred: ' + error);
                        }
                      }
                    });
                  }
                });
      }
    });

    function isDocMan() {
      return $('[name="docMode"]:checked').val() == 'M';
    }

    $("#doc_finalsave").validate({
      rules: {
        doc_date: {
          required: function () {
            return isDocMan();
          }
        },
        modeofoper: {
          required: true
        },
        doc_remarks: {
          required: true
        },
        disbursementInst: {
          required: true,
          fileExtension: ["pdf"],
          maxFileSize: 5 * 1024 * 1024 // 5 MB in bytes
        },
        rtoform: {
          fileExtension: ["pdf"],
          maxFileSize: 5 * 1024 * 1024 // 5 MB in bytes
        },
        marginfile: {
          required: function () {
            return $('#marginreq').val() === 'Y';
          },
          fileExtension: ["pdf"],
          maxFileSize: 5 * 1024 * 1024 // 5 MB in bytes
        },
        marginreq: {
          required: true
        }
      },
      messages: {
        marginfile: {
          required: "Please upload a file",
          fileExtension: "Only PDF files are allowed"
        },
        disbursementInst: {
          required: "Please upload a file",
          fileExtension: "Only PDF files are allowed"
        }
      },
      ignore: 'input[type=hidden], .select2-search__field',
      highlight: function (element, errorClass) {
        $(element).removeClass('is-invalid').removeClass('is-valid').removeClass('validation-invalid-label');
      },
      unhighlight: function (element, errorClass) {
        $(element).removeClass('validation-invalid-label').removeClass('is-invalid').removeClass('is-valid');
      },
      errorPlacement: function (error, element) {
        element.removeClass('is-valid').addClass('is-invalid');
        error.css({
          'padding-left': '23px',
          'margin-right': '20px',
          'padding-bottom': '2px',
          'color': 'red',
          'font-size': 'small'
        });
        error.addClass("validation-invalid-label");
        // log(element);
        if (element.hasClass('checkbox-input')) {
          element.parent().parent().parent().append(error);
        } else {
          error.insertAfter(element);
        }
      }
    });
    $.validator.addMethod("fileExtension", function (value, element, param) {
      var fileExtension = value.split('.').pop().toLowerCase();
      return this.optional(element) || $.inArray(fileExtension, param) !== -1;
    }, "Please upload a file with a valid extension.");
    $.validator.addMethod("maxFileSize", function (value, element, param) {
      if (element.files.length > 0) {
        var fileSize = element.files[0].size; // size in bytes
        return fileSize <= param;
      }
      return true;
    }, "The file must be less than 5 MB.");


    $("#docfinalsaveBtn").on('click', function (e) {
      e.preventDefault();
      if ($("#doc_finalsave").valid()) {
        var msg = 'Are you sure you want to complete Documentation Queue'
        confirmmsg(msg)
                .then(function (result) {
                  if (result) {

                    // Get the files from the input fields
                    var marginfile = $('#marginfile')[0].files[0];
                    var disbursementInst = $('#disbursementInst')[0].files[0];
                    var rtoform = $('#rtoform')[0].files[0];

                    // Convert the available files to Base64 using promises
                    var promises = [];
                    var rtoformTaken=false;
                    if (disbursementInst) {
                      promises.push(convertToBase64(disbursementInst));
                    }
                    if (marginfile) {
                      promises.push(convertToBase64(marginfile));
                    }
                    if(rtoform){
                      promises.push(convertToBase64(rtoform));
                      rtoformTaken=true;
                    }

                    Promise.all(promises)
                            .then(function (base64Files) {
                              var disbursementInst = base64Files[0] || null;
                              var marginfile = base64Files.length > 1 ? base64Files[1] : null;
                              var rtoform = base64Files.length > 2 ? base64Files[2] : ((base64Files.length > 1 && rtoformTaken) ? base64Files[1] : null);
                              // Make the AJAX request with the converted Base64 data
                              showLoader();
                              var dto = {
                                waiverType: "DOCSAVE",
                                completeDto: {
                                  docDate: $('#doc_date').val(),
                                  modeofoper: $('#modeofoper').val(),
                                  slno: $('#slno').val(),
                                  wiNum: $('#winum').val(),
                                  docRemarks: $('#doc_remarks').val(),
                                  marginfile: marginfile,
                                  disbursementInst: disbursementInst,
                                  rtoform: rtoform,
                                  marginreq: $('#marginreq').val()
                                }
                              };
                              return $.ajax({
                                url: 'doc/updateWaiver',
                                type: 'POST',
                                data: JSON.stringify(dto),
                                contentType: 'application/json',
                              });
                            }).then(function (response) {
                      hideLoader();
                      if (response.status === 'S') {
                        confirmmsg_lat('Documentation Completed Successfully ' + $('#winum').val()).then(function (confirmed) {
                          $('#backform').attr('action', 'releaselock');
                          $('#backform').find('#slnobk').val($('#slno').val())
                          $('#backform').find('#redirecturl').val('doclist')
                          $('#backform').submit();
                        });
                      } else {
                        alertmsg('Failed: ' + response.msg);
                      }
                    })
                            .catch(function (xhr, status, error) {
                              hideLoader();
                              var err_data = xhr.responseJSON;
                              if (err_data.msg) {
                                alertmsgvert(err_data.msg);
                              } else {
                                alertmsg('An error occurred: ' + error);
                              }
                            });

                  }
                });
      }
    });

    $('input[type=radio][name=docMode]').change(function () {
      if (this.value == 'D') {
        // $('#digitDiv').show();
        $('#man-div').hide();
      } else if (this.value == 'M') {
        $('#digitDiv').hide();
        $('#man-div').show();
      }
    })


      $('#man-div').on('click', '.allUploadBtn', function (e) {
        e.preventDefault();
        e.stopPropagation();
        // Find the closest row (tr) of the clicked button
        var row = $(this).closest('tr');

        // Find the second column (td:eq(1)) which contains the file input
        var fileInput = row.find('td:eq(1) .fileInput')[0];

        // Check if a file has been selected
        if (!fileInput || fileInput.files.length === 0) {
          alertmsg('No file selected.');
          return;
        }

        // Get the selected file
        var file = fileInput.files[0];

        // Check if the file is a PDF
        if (file.type !== 'application/pdf') {
          alertmsg('Only PDF files are allowed.');
          return;
        }

        // Check if the file size is less than 5 MB (5 * 1024 * 1024 bytes)
        if (file.size > 5 * 1024 * 1024) {
          alertmsg('The file size must be less than 5 MB.');
          return;
        }

        // Prepare the form data
        var formData = new FormData();

        // Append the file to the FormData object
        formData.append('commonFiles', file);

        // Append hidden fields or other input elements from the second column
        row.find('td:eq(1) input[type="hidden"], td:eq(1) input[type="text"], td:eq(1) select, td:eq(1) textarea').each(function () {
          formData.append($(this).attr('name'), $(this).val());
        });
        formData.append("slno", $('#slno').val());
        formData.append("winum", $('#winum').val());

        showLoader();
        $.ajax({
          url: 'doc/manDocuploadsave',
          type: 'POST',
          data: formData,
          processData: false,
          contentType: false,
          success: function(response) {
            hideLoader();
            if (response.status === 'S') {
              var data = JSON.parse(response.msg);
              row.find('.tdstat').html('<span class="badge badge-success">Completed</span>')
              //  $('#toggleList').trigger('click');
              notyalt('Documents uploaded successfully.');
              if (data.completed==='Y') {
                $('#docSv').show();
                $('#docsaveBtn').remove();
                $('#allUploadBtn').remove();
                $('input[name=docMode]').attr("disabled", true);
              }
            } else {
              alertmsg('File upload failed: ' + response.msg);
              $('#docSv').hide();
            }
          },
          error: function(xhr, status, error) {
            hideLoader();
            if (error.responseJSON) {
              alertmsg( error.responseJSON.msg);
            } else {
              alertmsg('Error saving  details');

            }
            $('#docSv').hide();
          }
        });
      });



    $('#man-div').on('click','.allUploadBtnold',function (e){
      e.preventDefault();
      e.stopPropagation();
      let valid = true;
      var maxFileSize=$('#maxFileSize').val();

      var row = $(this).closest('tr').prev('tr');
      var fileInput = row.find('.file-input')[0];
         const fileName = fileInput.length ? fileInput[0].name : '';
        const fileExtension = fileName.split('.').pop().toLowerCase();
        const allowedExtensions = ['pdf']
      // Check if a file has been selected
      if (fileInput.files.length === 0) {
        alertmsg('Kindly attach corresponding Documents');
        return;
      }
        if (fileInput.length > 0 && !allowedExtensions.includes(fileExtension)) {
          alertmsg('Only PDF is allowed.');
          valid = false;
          return ;
        }
        if (fileInput.length > 0 && fileInput[0].size>maxFileSize) {
          alertmsg('Maximum File Size allowed is 5mb.');
          valid = false;
          return ;
        }
      // Prepare the form data
      var formData = new FormData();

      // Append the file to the FormData object
      formData.append('file', fileInput.files[0]);

      // Append hidden fields (or any other fields in the row) to the FormData object
      row.find('input[type="hidden"], input[type="text"], select, textarea').each(function() {
        formData.append($(this).attr('name'), $(this).val());
      });



      // let form = $('#docDetailsForm')[0];
      // let formData = new FormData(form);
      // $('#man-div input[type="file"]').each(function() {
      //   const fileInput = $(this);
      //   const files = fileInput[0].files;
      //   const fileName = files.length ? files[0].name : '';
      //   const fileExtension = fileName.split('.').pop().toLowerCase();
      //   const allowedExtensions = ['pdf'];
      //   // if ( files.length === 0) {
      //   //   alertmsg('Kindly attach All Documents');
      //   //   valid = false;
      //   //   return false; // break out of each loop
      //   // }
      //
      //   if (files.length > 0 && !allowedExtensions.includes(fileExtension)) {
      //     alertmsg('Only PDF is allowed.');
      //     valid = false;
      //     return false; // break out of each loop
      //   }
      //   if (files.length > 0 && files[0].size>maxFileSize) {
      //     alertmsg('Maximum File Size allowed is 5mb.');
      //     valid = false;
      //     return false; // break out of each loop
      //   }
      // });
      if (!valid) {
        return;
      }
      else{
        showLoader();
        $.ajax({
          url: 'doc/manDocuploadsave',
          type: 'POST',
          data: formData,
          processData: false,
          contentType: false,
          success: function(response) {
            hideLoader();
            if (response.status === 'S') {
            //  $('#toggleList').trigger('click');
              notyalt('Documents uploaded successfully.');
              $('#docSv').show();
              $('#docsaveBtn').remove();
              $('#allUploadBtn').remove();
              $('input[name=docMode]').attr("disabled",true);
            } else {
              alertmsg('File upload failed: ' + response.msg);
              $('#docSv').hide();
            }
          },
          error: function(xhr, status, error) {
            hideLoader();
            if (error.responseJSON) {
              alertmsg( error.responseJSON.msg);
            } else {
              alertmsg('Error saving  details');

            }
            $('#docSv').hide();
          }
        });

      }
    })

    $("#remarkHistory").on('click',function (e) {
      e.preventDefault();
      e.stopPropagation();
      alertmsgframe();
    });
    $("#digitDiv").on('click','#resendLegal',function (e) {
          e.preventDefault();
          e.stopPropagation();

          var url=$(this).attr('data-url');
      var msg = 'Are you sure you want to resent Invitations ? '
      confirmmsg(msg)
              .then(function (result) {
                if (result) {
                  showLoader();
                        var dto = {
                          waiverType: "RESEND",
                          completeDto: {
                            slno: $('#slno').val(),
                            wiNum: $('#winum').val(),
                            signUrl: url
                          }
                        };
                          $.ajax({
                            url: 'doc/updateWaiver',
                            type: 'POST',
                            data: JSON.stringify(dto),
                            contentType: 'application/json',
                            success: function (response) {
                              hideLoader();
                              if (response.status === 'S') {
                                //  $('#toggleList').trigger('click');
                                notyalt(response.msg);
                              } else {
                                alertmsg(response.msg);
                              }
                            },
                            error: function (xhr, status, error) {
                              hideLoader();
                              var err_data = xhr.responseJSON;
                              if (err_data.msg) {
                                alertmsgvert(err_data.msg);
                              } else {
                                alertmsg('An error occurred: ' + error);
                              }
                            }
                          });
                    }
              });

    });

  });
  function alertmsgframe() {
    $('#alert_modal .modal-header').removeClass('bg-danger').addClass('bg-success');
    $('#alert_modal .modal-header').find('.modal-title').text('Remarks');
    $('#alert_modal .modal-body').html('<iframe id="modalIframe" src="remarks?slno='+$('#slno').val()+'" width="100%" height="400" frameborder="0"></iframe>');
    $('#alert_modal').modal('show');
  }
  function downloadPDF() {
    var link = document.createElement('a');
    link.href = 'data:application/pdf;base64,'+$('#sancManpdf').val();
    link.download = $('#winum').val()+'.pdf';
    link.click();
 }

  function convertToBase64(file) {
    return new Promise(function(resolve, reject) {
      var reader = new FileReader();
      reader.onload = function() {
        resolve(reader.result.split(',')[1]); // Remove the "data:image/png;base64," part
      };
      reader.onerror = function(error) {
        reject(error);
      };
      reader.readAsDataURL(file);
    });
  }


function showLegal(data_){
  var data=JSON.parse(data_.inv);
  var table = '<table class="table table-sm table-bordered table-hover ">';
  table += '<thead class="thead-dark"><tr>'
          + '<th>Name</th>'
          + '<th>Email</th>'
          + '<th>Phone</th>'
          + '<th>Resend</th>'
          + '<th>Active Status</th>'
          + '<th>Expiry Date</th>'
          + '</tr></thead><tbody>';

  $.each(data, function(index, invitee) {
    table += '<tr>'
            + '<td>' + (invitee.name || '<span class="text-muted">N/A</span>') + '</td>'
            + '<td>' + (invitee.email || '<span class="text-muted">N/A</span>') + '</td>'
            + '<td>' + (invitee.phone || '<span class="text-muted">N/A</span>') + '</td>';
    if(!invitee.signed) {
      table += '<td><a href="#" data-url="' + (invitee.signUrl || '#') + '" id="resendLegal" target="_blank" class="btn btn-sm btn-info">Resend</a></td>';
    }
    else{
      table += '<td><span class="badge badge-success">Signed</span></td>';
    }
    table += '<td>' + (invitee.active
                    ? '<span class="badge badge-success">Active</span>'
                    : '<span class="badge badge-secondary">Inactive</span>')
            + '</td>'
            + '<td>' + (invitee.expiryDate
                    ?invitee.expiryDate
                    : '<span class="text-muted">N/A</span>')
            + '</td>'
            + '</tr>';
  });
// new Date(invitee.expiryDate).toLocaleString()
  table += '</tbody></table>';
  $('#invitee-table').html(table);

  var file=data_.file;
  if(file) {
    $('#inv_view').html('<button type="button" id="legalFileBtn" onclick="modalSanc(this)" class="btn btn-sm btn-light" data-kt-stepper-action="next">View<i class="ki-duotone ki-add-notepad "><i class="path1"></i> <i class="path2"></i></i></button>');
    $('#legalpdfBase64').val(file);
  }

  $('#legalDocID').val(data_.legalDocID);
  $('#digitDiv').show();
}

  function modalSanc(c){
    // Get the Base64 PDF from the hidden input
    var base64PDF = $('#legalpdfBase64').val();

    // Convert the Base64 string to a data URI
    var pdfDataUri = "data:application/pdf;base64," + base64PDF;

    // Load the PDF into the iframe
    $('#legalpdfViewer').attr('src', pdfDataUri);

    // Show the modal
    $('#legal_pdf_modal').modal('show');
  }

</script>

<div class="flex-stack border rounded  hide sancApprove px-7 py-3 mb-2">
  <div class="w-100">
    <div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="docLink" data-bs-target="#docContent">
      <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
             for="docContent">
        <i class="ki-duotone ki-document fs-3x me-4">
          <span class="path1"></span>
          <span class="path2"></span>
        </i>
        <span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-4">Documentation</span>
                    <span class="text-muted fw-semibold fs-7">
                    Complete the documentation of the WI.
                    </span>
                </span>
      </label>
    </div>
    <%

      String docRemarks="";
      String docMode="";


      List<LegalityInvitees> legalityInvitees=new ArrayList<>();
      List<ManDocData> manDoc=new ArrayList<>();

      String docID="";
      VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
      docMode=vehicleLoanMaster.getDocMode()==null?"":vehicleLoanMaster.getDocMode();
      if("D".equalsIgnoreCase(docMode)){
        legalityInvitees= (List<LegalityInvitees>) request.getAttribute("legalDoc");
        if(legalityInvitees!=null)
          docID=legalityInvitees.get(0).getDocumentId();
        else
          legalityInvitees=new ArrayList<>();
      }
      if("M".equalsIgnoreCase(docMode)){
        manDoc= (List<ManDocData>) request.getAttribute("manDoc");
        if(manDoc==null)
          manDoc=new ArrayList<>();
      }
      List<VehicleLoanApplicant> vehicleLoanCoApplicants = new ArrayList<>();
      if (vehicleLoanMaster != null) {
        vehicleLoanCoApplicants = vehicleLoanMaster.getApplicants().stream().filter(t->t.getApplicantType().equalsIgnoreCase("C") && t.getDelFlg().equalsIgnoreCase("N")).toList();
      }
      String brHead=(String) request.getAttribute("brHead");
    %>

    <div id="docContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">
      <form id="docDetailsForm" name="docDetailsForm" method="POST" enctype="multipart/form-data">
        <div class="row">
          <div class="col-sm-12">
            <!--begin::Repeater-->
            <div id="kt_docs_repeater_basic">
              <div class="p-5 text-center">

                <div class="checkbox-wrapper-16 mb-3 mt-2 text-center">


                  <div class="alert alert-dismissible bg-light-danger border border-danger border-dashed d-flex flex-column flex-sm-row w-100 p-5 mb-10">
                    <i class="ki-duotone ki-information fs-2hx text-danger me-4 mb-5 mb-sm-0"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
                    <div class="d-flex flex-column pe-0 pe-sm-10">
                      <span class="fw-bold">Note : Kindly review the Sanction Letter Before Proceeding <br/> <span class="text-danger">  Before proceeding with digital processing, verify that the branch signatory name <span class="text-success">"<%=brHead%>"</span> matches the name on the Aadhaar card.</span></span>
                    </div>
                  </div>
                  <h5 class="mb-2 text-success">Documentation Mode</h5>
                  <div >
                    <label class="checkbox-wrapper">
                      <input class="checkbox-input"  id="Enable" name="docMode" type="radio" value="M" <%=docMode.equals("M")?"checked":""%> <%=!vehicleLoanMaster.isDocCompleted()?"":"disabled"%> >
                      <span class="checkbox-tile">
                                                  <span class="checkbox-icon">
                                                  </span>
                                                  <span class="checkbox-label">Manual</span>
                                                </span>
                    </label>
                    <label class="checkbox-wrapper pl-2">
                      <input class="checkbox-input" id="Disable" name="docMode" type="radio" value="D"   <%=docMode.equals("D")?"checked":""%> <%=!vehicleLoanMaster.isDocCompleted()?"":"disabled"%>>
                      <span class="checkbox-tile">
                                                  <span class="checkbox-icon">
                                                  </span>
                                                  <span class="checkbox-label">Digital</span>
                                                </span>
                    </label>
                  </div>
                </div>

                <div class="text-center pt-5 <%=!vehicleLoanMaster.isDocCompleted()?"":"hide"%>">
                  <button type="button" id="docsaveBtn" class="btn btn-sm btn-primary" data-kt-stepper-action="next">Continue
                    <i class="ki-duotone ki-double-right ">
                      <i class="path1"></i>
                      <i class="path2"></i>
                    </i></button>
                </div>

                <div class="pt-3 table-responsive <%=docMode.equals("M")?"":"hide"%>" id="man-div">

                    <input type="hidden" name="slno" value="<%=vehicleLoanMaster.getSlno()%>">
                    <input type="hidden" name="winum" value="<%=vehicleLoanMaster.getWiNum()%>">
                    <input type="hidden" id="maxFileSize" value="5242880">
                  <div id="man-div-html">
                  <%
                  if(docMode.equalsIgnoreCase("M")){
                    out.print(generateHTML(manDoc,vehicleLoanMaster.isDocCompleted()));
                  }

                  %>
                </div>

                </div>


                <div id="digitDiv" class=" <%=docMode.equals("D")?"":"hide"%>">
                <div class="pt-3 table-responsive" id="invitee-table">

                  <%
                    if(legalityInvitees.size()>0){
                  %>

                  <table class="table table-sm table-bordered table-hover ">
                    <thead class="thead-dark"><tr>
                      <th>Name</th>
                      <th>Email</th>
                      <th>Phone</th>
                      <th>Resend</th>
                      <th>Active Status</th>
                      <th>Expiry Date</th>
                    </tr></thead>
                    <tbody>
                    <%
                    }

                  for (LegalityInvitees invitee:legalityInvitees){


                    %>
                    <tr>
                      <td>
                        <%= invitee.getName() != null ? invitee.getName() : "<span class='text-muted'>N/A</span>" %>
                      </td>
                      <td>
                        <%= invitee.getEmail() != null ? invitee.getEmail() : "<span class='text-muted'>N/A</span>" %>
                      </td>
                      <td>
                        <%= invitee.getPhone() != null ? invitee.getPhone() : "<span class='text-muted'>N/A</span>" %>
                      </td>
                      <td>
                        <%
                          if (invitee.getSigned()!=null && invitee.getSigned()) {
                                    out.print(" <span class=\"badge badge-success\">Signed</span>");
                          }else if (invitee.getSignUrl() != null) {
                        %>
                        <a href="#"  data-url="<%= invitee.getSignUrl() %>" id="resendLegal" target="_blank" class="btn btn-sm btn-info">Resend</a>
                        <%
                          } else {
                            out.print("<span class='text-muted'>N/A</span>");
                          }
                        %>
                      </td>
                      <td>
                        <%
                          if (invitee.getActive() != null && invitee.getActive()) {
                        %>
                        <span class="badge badge-success">Active</span>
                        <%
                          } else {
                            out.print("<span class='badge badge-secondary'>Inactive</span>");
                          }
                        %>
                      </td>
                      <td>
                        <%= invitee.getExpiryDate() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(invitee.getExpiryDate()) : "<span class='text-muted'>N/A</span>" %>
                      </td>
                    </tr>
                      <%

                  }

                       if(legalityInvitees.size()>0){
                        %>
                    </tbody>
                    </table>



                  <%
                    }
                  %>
                </div>



              <div id="inv_btn" class="d-flex justify-content-center align-items-center <%=!vehicleLoanMaster.isDocCompleted()?"":"hide"%>">
                <div id="inv_view" class="text-center pt-5">

                </div>
                <div class="text-center pt-5 ps-4">
                  <input type="hidden" id="legalDocID" value="<%=docID%>">
                  <button type="button" id="doclegalStatus" class="btn btn-sm btn-danger" data-kt-stepper-action="next">Status
                    <i class="ki-duotone ki-save-2 ">
                      <i class="path1"></i>
                      <i class="path2"></i>
                    </i></button>
                </div>
              </div>
                </div>
              </div>

            </div>
            <!--end::Repeater-->
          </div>
        </div>
      </form>


      <div id="docSv" class="<%=vehicleLoanMaster.isDocCompleted()?"":"hide"%>">




        <form id="doc_finalsave" name="doc_finalsave" class="form" method="POST">
          <div class="mb-10 pt-5 fv-row fv-plugins-icon-container">
            <!--begin::Input-->
            <label class="required form-label mb-3">Date of Documentation(Manual)</label>
            <input class="form-control" type="date" id="doc_date" name="doc_date" maxlength="10"/>
            <!--end::Input-->

          </div>

          <!-- Labels row -->
          <div class="mb-10 pt-5 fv-row fv-plugins-icon-container">
            <!--begin::Label-->
            <label class="required form-label mb-3">Select Mode of operations of Loan Account</label>
            <!--end::Label-->
            <!--begin::Input-->
            <select name="modeofoper" id="modeofoper" class="form-select form-select-solid decision" required>
              <option value="" selected>select</option>
              <%
                List<Misrct> ModeofOper= (List<Misrct>) request.getAttribute("ModeofOper");
                if(vehicleLoanCoApplicants.size()==0){
                  ModeofOper=ModeofOper.stream().filter(t->t.getCodevalue().equalsIgnoreCase("001")).collect(Collectors.toList());
                }
                for (Misrct dec : ModeofOper) { %>
              <option value="<%=dec.getCodevalue()%>" ><%=dec.getCodedesc()%></option>
              <% } %>
            </select>
            <!--end::Input-->
            <div class="fv-plugins-message-container invalid-feedback"></div>
          </div>

          <!-- Labels row -->
          <div class="mb-10 pt-5 d-flex fv-row fv-plugins-icon-container">
            <div class="col-md-4">
              <!--begin::Label-->
              <label class="required form-label mb-3">Whether Margin Money has been paid for ?</label>
              <!--end::Label-->
              <!--begin::Input-->
              <select name="marginreq" id="marginreq" class="form-select form-select-solid decision" required>
                <option value="" selected>select</option>
                <option value="Y" >Yes</option>
                <option value="N" >No</option>
              </select>
              <!--end::Input-->
              <div class="fv-plugins-message-container invalid-feedback"></div>
            </div>
            <div class="col-md-2">
            </div>
            <div class="col-md-6">
              <!--begin::Label-->
              <label class="required form-label mb-3">Margin File Receipt (Applicable if Margin Money is Paid)</label>
              <!--end::Label-->
              <!--begin::Input-->
              <input type='file' accept="application/pdf" class='form-control' name='marginfile' id="marginfile" />
              <!--end::Input-->

            </div>

          </div>
          <div class="mb-10 pt-5 fv-row fv-plugins-icon-container">
            <div class="col-md-6">
            <!--begin::Label-->
            <label class="required form-label mb-3">Disbursement Instruction</label>
            <!--end::Label-->
            <!--begin::Input-->
            <input type='file' accept="application/pdf" class='form-control' name='disbursementInst' id="disbursementInst" />
            <!--end::Input-->
            <div class="fv-plugins-message-container invalid-feedback"></div>
            </div>
            <div class="col-md-6">
              <!--begin::Label-->
              <label class="required form-label mb-3">RTO Form</label>
              <!--end::Label-->
              <!--begin::Input-->
              <input type='file' accept="application/pdf" class='form-control' name='rtoform' id="rtoform" />
              <!--end::Input-->
              <div class="fv-plugins-message-container invalid-feedback"></div>
            </div>
          </div>

          <div class="mb-10 pt-5 fv-row fv-plugins-icon-container">
            <!--begin::Label-->
            <label class="required form-label mb-3">Remarks</label>
            <button  id="remarkHistory" class="badge badge-light-danger fs-base">
              History
            </button>
            <!--end::Label-->
            <!--begin::Input-->
            <textarea class="form-control" aria-label="With textarea" id="doc_remarks" name="doc_remarks" maxlength="500"></textarea>
            <!--end::Input-->
            <div class="fv-plugins-message-container invalid-feedback"></div>
          </div>

      <div class="text-end pt-5">
        <button type="button" id="docfinalsaveBtn" class="btn btn-sm btn-primary" data-kt-stepper-action="next">Save
          <i class="ki-duotone ki-double-right ">
            <i class="path1"></i>
            <i class="path2"></i>
          </i></button>
      </div>
        </form>
      </div>



    </div>
  </div>
</div>
<%!
  public String generateHTML(List<ManDocData> documentDTO,boolean completed) {
    StringBuilder htmlBuilder = new StringBuilder();


    // Table structure
    htmlBuilder.append("<table class='table table-sm table-bordered table-hover '>");
    htmlBuilder.append("<thead class='thead-dark'>");
    htmlBuilder.append("<tr>");
    htmlBuilder.append("<th>Description</th>");
    if(!completed) {
      htmlBuilder.append("<th>File</th>");
    }
    htmlBuilder.append("<th>Status</th>");
    htmlBuilder.append("<th>Upload</th>");
    htmlBuilder.append("<th></th>");

    htmlBuilder.append("</tr>");
    htmlBuilder.append("</thead>");
    htmlBuilder.append("<tbody>");

    // Loop through each document required and create a row with upload functionality
    for (ManDocData doc : documentDTO) {
      htmlBuilder.append("<tr>");
      htmlBuilder.append("<td>").append(doc.getDocDesc()).append("</td>");

        htmlBuilder.append("<td>");
        htmlBuilder.append("<input type='file' accept=\"application/pdf\" class='form-control fileInput' name='commonFiles' />");
        htmlBuilder.append("<input type='hidden'  name='commonFileNames' value='").append(doc.getDocDesc().replace(" ", "_")).append("' />");
        htmlBuilder.append("<input type='hidden'  name='commonFileCodes' value='").append(doc.getDocName()).append("' />");
        htmlBuilder.append("</td>");
      if(!doc.getUploadFlg()) {
        htmlBuilder.append("<td  class='tdstat'><span class=\"badge badge-secondary\">Pending</span></td>");
      }
      else{
      //  htmlBuilder.append("<td>-</td>");
        htmlBuilder.append("<td class='tdstat'><span class=\"badge badge-success\">Completed</span></td>");
      }
        htmlBuilder.append("<td><a href='#' class='btn btn-sm btn-bg-light allUploadBtn btn-icon-success btn-text-succes'><i class='ki-duotone ki-up-square fs-1'><span class='path1'></span><span class='path2'></span></i></a><td>");
      htmlBuilder.append("</tr>");
    }

    htmlBuilder.append("</tbody>");
    htmlBuilder.append("</table>");

    if(!completed) {
      // Submit button
//      htmlBuilder.append("<div>");
//      htmlBuilder.append("<a href='#' class='btn btn-sm btn-danger allUploadBtn' id='allUploadBtn' >rr</a>");
//      htmlBuilder.append("</div>");
    }


    return htmlBuilder.toString();
  }
%>