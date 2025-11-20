
<script src="assets/plugins/custom/formrepeater/formrepeater.bundle.js"></script>
<script>
  $(document).ready(function () {

    $("#bd_decision").validate({
      rules: {
        bddecision:{
          required:true
        },
        bdremarks:{
          required:true
        }
      },
      messages: {
      },
      ignore: 'input[type=hidden], .select2-search__field', // ignore hidden fields
      highlight: function(element, errorClass) {
        $(element).removeClass('is-invalid');
        $(element).removeClass('is-valid');
        $(element).removeClass('validation-invalid-label');

      },
      unhighlight: function(element, errorClass) {
        $(element).removeClass('validation-invalid-label');
        $(element).removeClass('is-invalid');
        $(element).removeClass('is-valid');
      },
      /* success: function(label) {
           label.addClass('validation-valid-label').text('Success.'); // remove to hide Success message
       },*/

      wrapper: 'span',
      errorPlacement: function (error, element) {
        element.removeClass('is-valid');
        element.addClass('is-invalid');
        error.css({'padding-left': '23px', 'margin-right': '20px', 'padding-bottom': '2px', 'color': 'red', 'font-size': 'small'});
        error.addClass("validation-invalid-label")
        error.insertAfter(element);
      }

    });

    $("#remarkHist").on('click',function (e) {
      e.preventDefault();
      e.stopPropagation();
      alertmsgframe();
    });
    $("#bdsave").on('click',function (e){
      e.preventDefault();

      var SendInfo= {
        slno : $('#slno').val(),
        winum:$('#winum').val(),
        bdremarks:$('#bdremarks').val(),
        bddecision:$('#bddecision').val()
      }
        if($("#bd_decision").valid()){
          showLoader();
          $.ajax({
            url: 'apicpc/rbcCheckerSave',
            type: 'POST',
            data: JSON.stringify(SendInfo),
            contentType: 'application/json',
            success: function(response) {
              hideLoader();
              if (response.status === 'S') {
                confirmmsg('Record Saved Successfully ' + $('#winum').val()).then(function (confirmed) {
                  $('#backform').attr('action','releaselock');
                  $('#backform').find('#slnobk').val($('#slno').val())
                  $('#backform').find('#redirecturl').val('rbcpcchecker')
                  $('#backform').submit();
                });
              } else {
                alertmsg('Failed: ' + response.msg);
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
    })



  });
  function openRemarksPage() {
    window.open('remarks?slno='+$('#slno').val(), '_blank','location=yes,height=800,width=800');
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

<div class="flex-stack border rounded hide decAccord px-7 py-3 mb-2">
  <div class="w-100">
    <div class="accordion-header d-flex collapsed  " data-bs-toggle="collapse" id="decisionDetailslink" data-bs-target="#decisionDetailsContent">
      <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
             for="decisionDetailsContent">
        <i class="ki-duotone ki-element-9 fs-3x me-4">
          <span class="path1"></span>
          <span class="path2"></span>
        </i>
        <span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-4">Decision Details</span>
                    <span class="text-muted fw-semibold fs-7">
                     Enter Decision details.
                    </span>
                </span>
      </label>
    </div>
    <div id="decisionDetailsContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">

      <div class="row">
        <div class="col-sm-12">
          <!--begin::Repeater-->
          <div id="kt_docs_repeater_basic">
            <form id="bd_decision" name="bd_decision" class="form" method="POST">
              <!-- Labels row -->
              <div class="mb-10 pt-5 fv-row fv-plugins-icon-container">
                <!--begin::Label-->
                <label class="required form-label mb-3">Select  Decision</label>
                <!--end::Label-->
                <!--begin::Input-->
                <select name="bddecision" id="bddecision" class="form-select form-select-solid decision" required>
                  <option value="" selected>select</option>
                  <option value="" >Send Back</option>
                  <option value="" >Reject</option>
                  
                </select>
                <!--end::Input-->
                <div class="fv-plugins-message-container invalid-feedback"></div>
              </div>
              <div class="mb-10 pt-5 fv-row fv-plugins-icon-container">
                <!--begin::Label-->
                <label class="required form-label mb-3">Remarks</label>
                <a href="#" id="remarkHist" class="badge badge-light-danger fs-base">
                 History
                </a>
                <!--end::Label-->
                <!--begin::Input-->
                <textarea class="form-control" aria-label="With textarea" id="bdremarks" name="bdremarks" maxlength="500"></textarea>
                <!--end::Input-->
                <div class="fv-plugins-message-container invalid-feedback"></div>
              </div>
            </form>
              <div class="text-end pt-5">
                <button type="button" id="bdsave" class="btn btn-lg btn-primary" data-kt-stepper-action="next">Save
                  <i class="ki-duotone ki-double-right ">
                    <i class="path1"></i>
                    <i class="path2"></i>
                  </i></button>
              </div>


          </div>
          <!--end::Repeater-->
        </div>
      </div>
    </div>
  </div>
</div>
