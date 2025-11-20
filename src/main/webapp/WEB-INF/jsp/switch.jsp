<%--
  Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 07-09-2024
  Time: 15:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<!--begin::Head-->
<head>
  <title>Context Switch</title>
  <!--begin::Global Stylesheets Bundle(mandatory for all pages)-->
  <link href="assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
  <link href="assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
  <!--end::Global Stylesheets Bundle-->
  <style>
    .select2-dropdown{
      background:white !important;
    }
    .select2-container--default .select2-results__option {
      padding: 10px;
    }

    .select2-container--default .select2-results__option--highlighted[aria-selected] {
      background-color: #f0f0f0;
      color: #333;
    }

    .select2-container--default .select2-selection--single .select2-selection__rendered {
      padding-left: 0;
    }

  </style>
</head>
<!--end::Head-->
<!--begin::Body-->
<body id="kt_body" class="app-blank kt bgi-size-cover bgi-position-center bgi-no-repeat">
<!--begin::Theme mode setup on page load-->
<!--begin::Root-->
<div class="d-flex flex-column flex-root" id="kt_app_root">
  <!--begin::Page bg image-->
  <style>body { background-image: url('assets/media/auth/bg9.jpg'); } [data-bs-theme="dark"] body { background-image: url('assets/media/auth/bg9-dark.jpg'); }</style>
  <!--end::Page bg image-->
  <!--begin::Authentication - Signup Welcome Message -->
  <div class="d-flex flex-column flex-center flex-column-fluid">
    <!--begin::Content-->
    <div class="d-flex flex-column flex-center text-center p-10">
      <!--begin::Wrapper-->
      <div class="card card-flush w-lg-650px py-5">
        <div class="card-body py-15 py-lg-20">
          <!--begin::Title-->
          <h1 class="fw-bolder text-gray-900 mb-7">Context Sol & User Updation</h1>
          <!--end::Title-->

          <!--end::Counter-->
          <!--begin::Illustration-->
<%--          <div class="mb-n5">--%>
<%--            <img src="assets/media/auth/icon-positive-vote-1.svg" class="mw-100 mh-300px theme-light-show" alt="" />--%>
<%--          </div>--%>
          <!--end::Illustration-->
          <!--begin::Form-->
          <form class="w-md-1350px mb-2 mx-auto" method="POST" id="kt_form" action="switchMaker">
            <div class="fv-row text-start">
              <div class="d-flex flex-column flex-md-row justify-content-center gap-3">
                <!--end::Input=-->
                 <select class="form-select form-select-solid" data-control="select2" name="searchBox" id="searchBox"   data-placeholder="Select an option">
                   <option value="<%=request.getAttribute("ppcno")%>"><%=request.getAttribute("ppcName")%>(<%=request.getAttribute("ppcno")%>)</option>
                </select>
                <!--end::Input=-->
                <div class="fv-row text-start">
                  <!--begin::Submit-->
                  <a href="#" id="kt_submit" class="btn btn-secondary">Switch</a>

                  <!--end::Submit-->
                </div>
              </div>
            </div>

          </form>
          <!--end::Form-->

        </div>
      </div>
      <!--end::Wrapper-->
    </div>
    <!--end::Content-->
  </div>
  <!--end::Authentication - Signup Welcome Message-->
</div>
<!--end::Root-->
<!--begin::Javascript-->
<script>var hostUrl = "assets/";</script>
<!--begin::Global Javascript Bundle(mandatory for all pages)-->
<%--<script src="assets/js/bootstrap/bootstrap.bundle.min.js"></script>--%>
<%--<script src="assets/js/jquery/jquery.min.js"></script>--%>
<link href="assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css"/>
<script src="assets/plugins/global/plugins.bundle.js"></script>

<script>
  $(document).ready(function(){

    $('#kt_submit').on('click',function (e){
      e.preventDefault();
      $('#kt_form').submit();
    })

    $('#searchBox').select2({
      placeholder: 'Search for PPC/Office Name',
      minimumInputLength: 4,
      ajax: {
        url: 'api/searchppc',
        dataType: 'json',
        delay: 250,
        data: function (params) {
          return {
            searchText: params.term.toUpperCase() // search term
          };
        },
        processResults: function (data) {
          return {
            results: data.map(function(item) {
              return {
                id: item.codevalue, // The value for select (ppcno)
                text: item.codedesc, // This will be unused but needed
                name: item.name, // Name of the person
                ppc: item.ppc, // PPC
                designation: item.designation, // Designation
                office: item.office, // Office Name
                photo: 'https://infobank.sib.co.in:8443/UPLOAD_FILES/IMAGE/' + item.ppc + '.xjpg' // Assuming the photo is named as ppcno.jpg
              };
            })
          };
        },
        cache: true
      },
      templateResult: formatResult, // Custom function to format each result
      templateSelection: formatSelection // Custom function to format the selected value
    });
// Custom function to format the dropdown options
    function formatResult(item) {
      if (!item.id) {
        return item.text;
      }

      // Return the custom HTML for the dropdown item
      var $result = $(
              '<div class="row align-items-center">' +
              '<div class="col-2">' +
              '<img src="' + item.photo + '" class="img-thumbnail" style="width: 50px; height: 50px;" />' +
              '</div>' +
              '<div class="col-10">' +
              '<h6 class="mb-0">' + item.name + '</h6>' +
              '<small>PPC: ' + item.ppc + ' | ' + item.designation + ' | ' + item.office + '</small>' +
              '</div>' +
              '</div>'
      );
      return $result;
    }

// Custom function to format the selected value
    function formatSelection(item) {
      return item.text; // Show the name of the selected employee
    }

  })
</script>

<!--end::Global Javascript Bundle-->
<!--begin::Custom Javascript(used for this page only)-->
<!--end::Custom Javascript-->
<!--end::Javascript-->
</body>
<!--end::Body-->
</html>