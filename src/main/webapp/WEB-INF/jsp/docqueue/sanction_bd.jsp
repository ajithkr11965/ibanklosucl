
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script>
  $(document).ready(function () {
    $('#sanctionDetailsContent').collapse('show')
    $('#sanctiondetailsLink').collapse('show')
    scrollToDivAndClick("sanctiondetailsLink");
      if($('[name="Activity"]').is(':checked')){
          var proceedVal=$('[name="Activity"]:checked').val()
          if(proceedVal==='N') {
              $('.sancApprove').removeClass('hide');
              $('.decAccord').addClass('hide');
          }
          else{
              $('.sancApprove').addClass('hide');
              $('.decAccord').removeClass('hide');
              alertmsg('Please proceed with Send back/Rejection of the work item  ');
          }
      }
      else{
          $('.sancApprove').addClass('hide');
          $('.decAccord').addClass('hide');
      }

  });
  function scrollToDivAndClick(divId) {




    var $element = $('#' + divId);

    if ($element.length) {
      var offset = $element.offset().top;
      var additionalOffset = 0; // Adjust this value based on your needs, e.g., height of a fixed header

      $('html, body').animate({
        scrollTop: offset - additionalOffset
      }, 600, function() {
      });
    }
    $('#sancBtn').on('click', function(e) {
        e.preventDefault();
        showLoader();
        var dto = {
            slNo: $('#slno').val(),
            wiNum: $('#winum').val()
        };
        fetch('doc/getSancLetter', {
            method: 'POST', // Use POST method
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dto)
        }).then(response => {
            if (!response.ok) {
                // If response status is not 200, throw an error
                throw new Error(response.text());
            }
            return response.text(); // Parse the response as plain text (Base64 string)
        }).then(data => {
                // Get the Base64 PDF from the hidden input
               // var base64PDF = $('#pdfBase64').val();
                // Convert the Base64 string to a data URI
                var pdfDataUri = "data:application/pdf;base64," + data;
                // Load the PDF into the iframe
                $('#pdfViewer').attr('src', pdfDataUri);
                // Show the modal
                $('#pdf_modal').modal('show');
            hideLoader();
            })
            .catch(error => {
                alertmsgvert('Error in fetching SanctionLetter');
                hideLoader();
            });

    });
    $('#sancProceed').on('click', function(e) {
        e.preventDefault();

        if(!$('[name="Activity"]').is(':checked')){
            alertmsg('Kindly select Whether any sanction modification required?')
        }
        else{
            var proceedVal=$('[name="Activity"]:checked').val()
            $.ajax({
                url: 'doc/sancStatus', // Update with your API endpoint
                type: 'POST',
                data:{
                    slno : $('#slno').val(),
                    action : proceedVal
                },
                success: function(response) {
                    hideLoader();
                    if (response.status === 'S') {
                        $('#pdf_modal').modal('hide');
                        if(proceedVal==='N'){
                            $('.sancApprove').removeClass('hide');
                            $('.decAccord').addClass('hide');
                        }
                        else{
                            $('.sancApprove').addClass('hide');
                            $('.decAccord').removeClass('hide');
                            alertmsg('Please proceed with Send back/Rejection of the work item  ');
                        }
                        scrollToDivAndClick("sanctiondetailsLink");
                    } else {
                        alertmsg(response.msg);
                    }
                },
                error: function(xhr, status, error) {
                    hideLoader();
                    var err_data = xhr.responseJSON;
                    if(err_data.msg){
                        alertmsgvert(err_data.msg);
                    }
                    else {
                        alertmsg('An error occurred: ' + error);
                    }
                }
            });
        }
    });

  }



  function alertmsgvert(Msg) {
    $('#alert_modal .modal-header').removeClass('bg-success').addClass('bg-danger');
    $('#alert_modal .modal-header').find('.modal-title').text('Please Note!');
    $('#alert_modal .modal-body').html(Msg);
    $('#alert_modal').modal('show');
  }
  function alertmsgframe() {
    $('#alert_modal .modal-header').removeClass('bg-danger').addClass('bg-success');
    $('#alert_modal .modal-header').find('.modal-title').text('Remarks');
    $('#alert_modal .modal-body').html('<iframe id="modalIframe" src="remarks?slno='+$('#slno').val()+'" width="100%" height="400" frameborder="0"></iframe>');
    $('#alert_modal').modal('show');
  }

</script>

<div class="flex-stack border rounded px-7 py-3 mb-2">
  <div class="w-100">
    <div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="sanctiondetailsLink" data-bs-target="#sanctionDetailsContent">
      <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
             for="sanctionDetailsContent">
        <i class="ki-duotone ki-element-9 fs-3x me-4">
          <span class="path1"></span>
          <span class="path2"></span>
        </i>
        <span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-4">Sanction Details</span>
                    <span class="text-muted fw-semibold fs-7">
                     See Sanction Details
                    </span>
                </span>
      </label>
    </div>
    <div id="sanctionDetailsContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">

      <div class="row">
        <div class="col-sm-12">
          <!--begin::Repeater-->
          <div id="kt_docs_repeater_basic">

              <!-- Labels row -->
              <div class="mb-10 pt-5 fv-row fv-plugins-icon-container d-flex justify-content-center">
                <a href="#" id="sancBtn" class="btn btn-success  hover-scale"> <i class="ki-duotone ki-finance-calculator fs-1"><span class="path1"></span><span class="path2"></span></i>
                  View Sanction Letter</a>

              </div>

          </div>
          <!--end::Repeater-->
        </div>
      </div>
    </div>
  </div>
</div>
