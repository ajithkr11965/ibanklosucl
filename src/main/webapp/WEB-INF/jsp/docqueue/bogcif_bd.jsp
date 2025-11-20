<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanSubqueueTask" %>
<%@ page import="java.util.Optional" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script>
  $(document).ready(function () {
    $("#cifDetailsForm").validate({
      rules: {
        'cifMode':{
          required:true
        }
      },
      messages: {

      },
      ignore: 'input[type=hidden], .select2-search__field',
      highlight: function(element, errorClass) {
        $(element).removeClass('is-invalid').removeClass('is-valid').removeClass('validation-invalid-label');
      },
      unhighlight: function(element, errorClass) {
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
        if(element.hasClass('checkbox-input')){
          element.parent().parent().parent().append(error);
        }
        else {
          error.insertAfter(element);
        }
      }
    });

    // Handle save button click
    $("#cifsaveBtn").on('click', function (e) {
      e.preventDefault();
      if($("#cifDetailsForm").valid()) {
        var msg = 'Are you sure you want to proceed with CIF Creation(Digitally)'
        if ($('[name="cifMode"]:checked').val() == 'M')
          msg = 'Are you sure you want to proceed with CIF Creation(Manually)'
        confirmmsg(msg)
                .then(function (result) {
                  if (result) {
                    showLoader();
                    var dto = {
                      waiverType: "CIF",
                      cifCreationDto: {
                        cifwaiveRequired: "Y",
                        cifMode: $('[name="cifMode"]:checked').val(),
                        slno: $('#slno').val(),
                        wiNum: $('#winum').val(),
                        cifwaiverRemarks: $('#cifwaiverRemarks').val()
                      }
                    };
                    $.ajax({
                      url: 'doc/updateWaiver', // Update with your API endpoint
                      type: 'POST',
                      data: JSON.stringify(dto),
                      contentType: 'application/json',
                      success: function (response) {
                        hideLoader();
                        if (response.status === 'S') {
                          notyalt('Record Saved Successfully');
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
    $("#cifRecallBtn").on('click', function (e) {
      e.preventDefault();
      confirmmsg("Are you sure you want to recall the CIF Creation SubTask?")
              .then(function (result) {
                if (result) {
                  showLoader();
                  var dto = {
                    waiverType: "CIF",
                    cifCreationDto: {
                      cifwaiveRequired: "RECALL",
                      slno: $('#slno').val(),
                      wiNum: $('#winum').val(),
                      cifMode: $('[name="cifMode"]:checked').val(),
                    }
                  };
                  $.ajax({
                    url: 'doc/updateWaiver', // Update with your API endpoint
                    type: 'POST',
                    data: JSON.stringify(dto),
                    contentType: 'application/json',
                    success: function (response) {
                      hideLoader();
                      if (response.status === 'S') {
                        notyalt('WI Recalled Successfully');
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
    });

  });


</script>

<div class="flex-stack border rounded  hide sancApprove px-7 py-3 mb-2">
  <div class="w-100">
    <div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="cifLink" data-bs-target="#cifContent">
      <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
             for="cifContent">
        <i class="ki-duotone ki-badge fs-3x me-4">
          <span class="path1"></span>
          <span class="path2"></span>
        </i>
        <span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-4">CIF ID Creation</span>
                    <span class="text-muted fw-semibold fs-7">
                     Send WI for CIF ID Creation
                    </span>
                </span>
      </label>
    </div>
    <%

      String cifRemarks="";
      String cifMode="";

      List<VehicleLoanSubqueueTask> subqueueTask= (List<VehicleLoanSubqueueTask>) request.getAttribute("subQueueData");
      if(subqueueTask!=null){
        Optional<VehicleLoanSubqueueTask> cif_=subqueueTask.stream().filter(t->t.getTaskType().equalsIgnoreCase("CIF_CREATION") || t.getTaskType().equalsIgnoreCase("VKYC")  ).findFirst();
        if(cif_.isPresent()) {
          cifRemarks=cif_.get().getRemarks();
          cifMode=cif_.get().getTaskType().equalsIgnoreCase("VKYC")?"V":"M";
        }
      }


      VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
      List<VehicleLoanApplicant> vehicleLoanApplicants = new ArrayList<>();
      if (vehicleLoanMaster != null) {
        vehicleLoanApplicants = vehicleLoanMaster.getApplicants().stream().filter(t->t.getSibCustomer().equalsIgnoreCase("N") && t.getDelFlg().equalsIgnoreCase("N")).toList();
      }
    %>
    <div id="cifContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">
      <form id="cifDetailsForm" name="cifDetailsForm" method="POST">
      <div class="row">
        <div class="col-sm-12">
          <!--begin::Repeater-->
          <div id="kt_docs_repeater_basic">
            <div class="p-5 text-center">

              <div class="checkbox-wrapper-16 mb-3 mt-2 text-center">
                <h5 class="mb-2 text-success">CIF Creation Mode</h5>
                <div >
                  <label class="checkbox-wrapper">
                    <input class="checkbox-input"  id="Enable" name="cifMode" type="radio" value="M" <%=cifMode.equals("M")?"checked":""%> >
                    <span class="checkbox-tile">
                                                  <span class="checkbox-icon">
                                                  </span>
                                                  <span class="checkbox-label">Manual</span>
                                                </span>
                  </label>
                  <label class="checkbox-wrapper pl-2">
                    <input class="checkbox-input" id="Disable" name="cifMode" type="radio" value="V"   <%=cifMode.equals("V")?"checked":""%>>
                    <span class="checkbox-tile">
                                                  <span class="checkbox-icon">
                                                  </span>
                                                  <span class="checkbox-label">VKYC</span>
                                                </span>
                  </label>
                </div>
              </div>
                <div class="col-sm-12 pt-4">
                  <table class="table table-sm table-bordered table-hover">
                    <thead>
                    <tr>
                      <th>Type</th>
                      <th>Name</th>
                      <th>Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%

                      for (VehicleLoanApplicant applicant : vehicleLoanApplicants) {
                        String applicantType="";
                        if ("A".equals(applicant.getApplicantType())) {
                          applicantType = "Applicant";
                        } else if ("C".equals(applicant.getApplicantType())) {
                          applicantType = "Co-Applicant";
                        } else if ("G".equals(applicant.getApplicantType())) {
                          applicantType = "Guarantor";
                        }

                    %>
                    <tr>

                      <td><%=applicantType%>
                      </td>
                      <td><%=applicant.getBasicapplicants().getApplicantName()%>
                      </td>
                      <td>PENDING
                      </td>
                    </tr>
                    <%
                        }
                    %>
                    </tbody>
                  </table>
                </div>
                <div class="mb-3">
                  <label for="cifwaiverRemarks" class="form-label">Remarks:</label>
                  <textarea type="text"  id="cifwaiverRemarks" name="cifwaiverRemarks"   class="form-control" required><%=cifRemarks%></textarea>
                </div>
                <div class="text-end pt-5">
                  <button type="button" id="cifRecallBtn" class="btn btn-sm btn-danger" data-kt-stepper-action="next">
                    <i class="ki-duotone ki-double-left ">
                      <i class="path1"></i>
                      <i class="path2"></i>
                    </i>
                    Recall
                  </button>

                  <button type="button" id="cifsaveBtn" class="btn btn-sm btn-primary" data-kt-stepper-action="next">Save
                    <i class="ki-duotone ki-double-right ">
                      <i class="path1"></i>
                      <i class="path2"></i>
                    </i></button>
                </div>

            </div>


          </div>
          <!--end::Repeater-->
        </div>
      </div>
      </form>
    </div>
  </div>
</div>
