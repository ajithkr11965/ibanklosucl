<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.sib.ibanklosucl.model.*" %>
<%@ page import="java.util.Optional" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="assets/plugins/custom/formrepeater/formrepeater.bundle.js"></script>
<script src="assets/js/vendor/forms/inputs/maxlength.min.js"></script>
<style>
  .select2-selection{
  height: 45px !important;
}
.select2-selection__arrow{
  display: none !important;
}
</style>
<script>
  $(document).ready(function () {
    $('#ppcSelect').select2({
        placeholder: 'Select PPC',
        width: '100%',
        templateResult: formatPPC,
        templateSelection: formatPPCSelection
    });

    // Ensure PPC selection is hidden initially
    $('#ppcSelectContainer').hide();
    $('#rbcdecision').on('change', function() {
      console.log("decision invoked");
        var selectedLevel = $(this).val();
        var ppcSelectContainer = $('#ppcSelectContainer');
        var ppcSelect = $('#ppcSelect');

        // Clear current selection
        ppcSelect.val(null).trigger('change');

        // Hide PPC selection for empty, SB, or HU decisions
        if (!selectedLevel || selectedLevel === 'NIL' || selectedLevel === 'SANC' || selectedLevel === 'RM') {
            ppcSelectContainer.hide();
            return;
        }

        // Show container and load PPCs for selected level
        ppcSelectContainer.show();

        // Fetch PPCs for selected level
        $.ajax({
        url: 'apicpc/ppcs/' + selectedLevel,
        type: 'GET',
        beforeSend: function() {
            ppcSelect.prop('disabled', true);
        },
        success: function(response) {
            // Clear existing options
            ppcSelect.empty();

            // Add default empty option
            ppcSelect.append(new Option('Select PPC', '', true, true));

            // Find the matching level object and get its children
            response.forEach(function(levelObj) {
                if (levelObj.id === selectedLevel && levelObj.children) {
                    // Add PPC options from children array
                    levelObj.children.forEach(function(ppc) {
                        ppcSelect.append(new Option(ppc.text, ppc.id, false, false));
                    });
                }
            });

            // Initialize or refresh Select2
            ppcSelect.trigger('change');
        },
        error: function(xhr, status, error) {
            console.error('Error loading PPCs:', error);
            alertmsg('Error loading PPCs for selected level');
        },
        complete: function() {
            ppcSelect.prop('disabled', false);
        }
    });
    });

    $("#rc_decision").validate({
      rules: {
        rbcdecision:{
          required:true
        },
        rbccremarks:{
          required:true
        },
        ppcSelect: {
            required: function() {
                var decision = $('#rbcdecision').val();
                return decision && decision !== 'NIL' && decision !== 'SANC' && decision !== 'RM';
            }
        }
      },
      messages: {
         ppcSelect: {
            required: "Please select a PPC"
        }
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
    $("#rcsave").on('click',function (e){
      e.preventDefault();
      var selectedPPC = $('#ppcSelect').val();

      var SendInfo= {
        slno : $('#slno').val(),
        winum:$('#winum').val(),
        rbccremarks:$('#rbccremarks').val(),
        rbcdecision:$('#rbcdecision').val(),
        rbcpcCheckerUser: selectedPPC
      }
        if($("#rc_decision").valid()){
          showLoader();
          $.ajax({
            url: 'apicpc/rbcCheckerSave',
            type: 'POST',
            data: JSON.stringify(SendInfo),
            contentType: 'application/json',
            success: function(response) {
              hideLoader();
              if (response.status === 'S') {
                confirmmsg_lat('Record Saved Successfully ' + $('#winum').val()).then(function (confirmed) {
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

$('.maxlength-textarea').maxlength({
                    alwaysShow: true
                });

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
  function formatPPC(ppc) {
    if (!ppc.id) {
        return ppc.text;
    }
    return $('<span>' + ppc.text + ' (' + ppc.id + ')</span>');
}

function formatPPCSelection(ppc) {
    if (!ppc.id) {
        return ppc.text;
    }
    return ppc.text + ' (' + ppc.id + ')';
}

</script>
<%
  VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
  String preDisbCond = vehicleLoanMaster.getPreDisbCondition() != null ? vehicleLoanMaster.getPreDisbCondition() : "";
  List<Misrct> decisionParam= (List<Misrct>) request.getAttribute("decisionParam");
%>
<div class="flex-stack border rounded px-7 py-3 mb-2">
  <div class="w-100">
    <div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="decisionDetailslink" data-bs-target="#decisionDetailsContent">
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
            <form id="rc_decision" name="rc_decision" class="form" method="POST">
              <!-- Labels row -->

              <div class="input-group mt-3 mb-3">
                <span class="input-group-text">Pre Disbursment Conditions</span>
                <textarea class="form-control" rows="4" aria-label="Pre Disbursment Conditions" name="pre_cond_rmks"
                          id="pre_cond_rmks" readonly><%=preDisbCond%></textarea>
              </div>
              <div class="mb-10 pt-5 fv-row fv-plugins-icon-container">
                <!--begin::Label-->
                <label class="required form-label mb-3">Select  Decision</label>
                <!--end::Label-->
                <!--begin::Input-->
                <select name="rbcdecision" id="rbcdecision" class="form-select form-select-solid decision" required>
                  <option value="" selected>select</option>
                  <%
                    String bureauBlock = (String) request.getAttribute("bureauBlock");
                    if(bureauBlock.equals("Y")){
                      decisionParam=decisionParam.stream().filter(t->"RM".equalsIgnoreCase(t.getCodevalue())).collect(Collectors.toList());
                    }

                    for (Misrct dec : decisionParam) { %>
                  <option value="<%=dec.getCodevalue()%>" ><%=dec.getCodedesc()%></option>
                  <% } %>
                </select>
                <!--end::Input-->
                <div class="fv-plugins-message-container invalid-feedback"></div>
              </div>
              <div class="row" id="ppcSelectContainer" style="display: none;">
                <div class="col-sm-12 mt-3">
                    <label for="ppcSelect" class="required form-label">Select PPC</label>
                    <select id="ppcSelect" name="ppcSelect" class="form-select-solid decision">
                        <option></option>
                    </select>
                </div>
            </div>
              <div class="mb-10 pt-5 fv-row fv-plugins-icon-container">
                <!--begin::Label-->
                <label class="required form-label mb-3">Remarks</label>
                <a href="#" id="remarkHist" class="badge badge-light-danger fs-base">
                 History
                </a>
                <!--end::Label-->
                <!--begin::Input-->
                <textarea class="form-control maxlength-textarea" aria-label="With textarea" id="rbccremarks" name="rbccremarks" maxlength="4000"></textarea>
                <!--end::Input-->
                <div class="fv-plugins-message-container invalid-feedback"></div>
              </div>
            </form>
              <div class="text-end pt-5">
                <button type="button" id="rcsave" class="btn btn-lg btn-primary" data-kt-stepper-action="next">Submit
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
