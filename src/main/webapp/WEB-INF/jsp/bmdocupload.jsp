<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.dto.ResponseDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="com.sib.ibanklosucl.model.VLEmployment" %>
<%@ page import="com.sib.ibanklosucl.dto.VLDocMas" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.sib.ibanklosucl.model.VLFileUpload" %>
<%@ page import="java.util.Optional" %>
<%@ page import="java.util.stream.Collectors" %><%--
  Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 08-05-2024
  Time: 15:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="los" uri="http://www.siblos.com/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en" dir="ltr">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="_csrf" content="${_csrf.token}"/>
  <meta name="_csrf_header" content="${_csrf.headerName}"/>
  <title>SIB-LOS</title>

  <!-- Global stylesheets -->
  <link href="assets/fonts/inter/inter.css" rel="stylesheet" type="text/css">
  <link href="assets/icons/phosphor/styles.min.css" rel="stylesheet" type="text/css">
  <link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
  <link href="assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
  <link href="assets/css/custom.css" rel="stylesheet" type="text/css" />
  <link href="assets/css/custom/wicreate.css" rel="stylesheet" type="text/css" />
  <!-- /global stylesheets -->

  <!-- Core JS files -->

  <script src="assets/js/bootstrap/bootstrap.bundle.min.js"></script>
  <script src="assets/js/jquery/jquery.min.js"></script>
  <script src="assets/js/vendor/forms/validation/validate.min.js"></script>
  <script src="assets/js/vendor/split.min.js"></script>
  <!-- /core JS files -->
  <script src="assets/demo/pages/components_tooltips.js"></script>
  <!-- Theme JS files -->
  <script src="assets/js/app.js"></script>
  <script src="assets/js/vendor/notifications/sweet_alert.min.js"></script>
  <script src="assets/js/vendor/notifications/noty.min.js"></script>
  <!-- /theme JS files -->

</head>
<style>
  label{
    font-weight: bold;
    color: #cd3232;
    /*width:10em;*/
  }
  .bgapp{
    border: 3px dotted #d0e3f9;border-radius: 15px !important;background: #f8f9fb;
  }
</style>
<body>
<%--<los:header/>--%>
<los:loader/>


<!-- Page header -->
<los:pageheader/>
<!-- /page header -->

<div class="navbar navbar-expand-lg shadow rounded py-1 mb-3">
  <div class="container-fluid">


    <%
      Employee userdt= (Employee) request.getAttribute("userdata");
      VehicleLoanMaster master= (VehicleLoanMaster) request.getAttribute("vlmaster");
      List<VLDocMas> docMas= (List<VLDocMas>) request.getAttribute("docmas");
      List<VLFileUpload> fileUploads= (List<VLFileUpload>) request.getAttribute("fileUploads");

      String roname = (String) request.getAttribute("roname");


    %>

    <div class="navbar-collapse collapse order-2 order-lg-1" >
								<span class="navbar-text d-none d-lg-inline-flex align-items-lg=center me-3">
									<i class="ph-receipt me-2"></i>
									Registration No : <b><%=master.getWiNum()%></b>
                                  <input type="hidden" name="slno" id="slno" value="<%=master.getSlno()%>">
                                  <input type="hidden" name="winum" id="winum" value="<%=master.getWiNum()%>">
								</span>
    </div>



  </div>
  <div class="container-fluid">

    <div class="navbar-collapse collapse order-2 order-lg-1" >
								<span class="navbar-text d-none d-lg-inline-flex align-items-lg=center me-3">
									<i class="ph-house me-2"></i>
									Branch : <b><%=userdt.getJoinedSol()%>( <%=userdt.getBrName()%> <%=roname%>)</b>
								</span>
    </div>

  </div>
</div>


<!-- Page content -->
<div class="page-content pt-0">
  <!-- Main content -->
  <div class="content-wrapper">

    <!-- Content area -->
    <div class="content">

      <!-- Main charts -->
      <div class="d-flex flex-row min-vh-100">
        <div id="appList" class="w-100">

          <div class="card">
            <div class="card-header d-flex align-items-center">
              <h5 class="mb-0">Loan Application Document Upload</h5>
            </div>

            <div class="tab-content card-body" id="loanbody">
              <form id="fileUploadForm" method="post" enctype="multipart/form-data">
                <input type="hidden" name="slno" value="<%=master.getSlno()%>">
                <input type="hidden" name="winum" value="<%=master.getWiNum()%>">
                <input type="hidden" id="maxFileSize" value="5242880">


                <%

                  List<VehicleLoanApplicant> vehicleLoanApplicants=master.getApplicants();
                  VehicleLoanApplicant applicant=vehicleLoanApplicants.stream().filter(t-> "A".equals(t.getApplicantType())).toList().get(0);
                  Optional<VehicleLoanApplicant> guarantorOpt=vehicleLoanApplicants.stream().filter(t-> "G".equals(t.getApplicantType())).findFirst();
                  List<VehicleLoanApplicant> coapp=vehicleLoanApplicants.stream().filter(t-> "C".equals(t.getApplicantType())).toList();
                  VehicleLoanApplicant guarantor = guarantorOpt.orElse(null);
                  VLDocMas vlother=new VLDocMas();
                  vlother.setMandatory("Y");
                  vlother.setLabelname("Any Other Documents");
                  vlother.setLabelcode("LMOD01");
                  vlother.setFilename("OTHER_DOCUMENTS");
                  vlother.setGeneric("N");
                  //vlother.setApplicant("N".equals(applicant.getSibCustomer())?"Y":"N");
                  vlother.setApplicant("N");
                  vlother.setCoapplicant("N");
                  vlother.setGurantor("N");
                  //vlother.setGurantor("N".equals(guarantorOpt.isPresent()?guarantor.getSibCustomer():"")?"Y":"N");
                  docMas.add(vlother);
                    int mand_uploaded=0;

                    String sel_appid=master.getOwnerApplicantId()==null?"":master.getOwnerApplicantId().toString();
                    String sel_masterappid=master.getFirstTimeBuyer()==null?" ":master.getFirstTimeBuyer();
                  List<VLDocMas> appmas=docMas.stream().filter(t->"Y".equals(t.getApplicant())).toList();
                  %>




                <%

                  if(appmas.size()>0){
                %>
                <div class="borders bgapp  m-5 rounded  border-dashed  ">
                  <div class="list-feed list-feed-solid ms-3 mt-3 mb-3">
                <div id="applicant">
                  <h4 class="page-title -0">
                    <u>APPLICANT</u>
                  </h4>
                  <ol type="i">
                  <%
                    for(VLDocMas app:appmas){
                      boolean mandatory=UploadCompleted(fileUploads,app.getLabelcode(),"A-1");
                      if(!mandatory){
                        mand_uploaded++;
                      }
                  %>
                  <li>
                    <div class="d-flex m-2">
                      <div> <label><%=app.getLabelname()%></label></div>
                  <input type="file"  class="form-control" name="applicantFiles" data-mandatory="<%=mandatory?"N":app.getMandatory()%>" data-label="<%=app.getLabelname()%>">
                  <input type="hidden" name="applicantFileCodes" value="<%=app.getLabelcode()%>">
                  <input type="hidden" name="applicantFileNames" value="<%=app.getFilename()%>">
                  <input type="hidden" name="applicantType" value="A-1">
                  <input type="hidden" name="applicantid" value="<%=applicant.getApplicantId()%>">
                    </div>
                    <div class="text-center <%=app.getMandatory().equals("Y")&& !mandatory?"":"d-none"%>">
                      <code class="text-danger">Above field is mandatory</code>

                    </div>
                    <div class="text-center <%=mandatory?"":"d-none"%>">
                      <label class="validation-invalid-label validation-valid-label" >File Uploaded.</label>
                    </div>
                  </li>
                  <%
                    }

                  %>
                    </ol>
                </div>
                </div>
                </div>
                <%
                  }
                  List<VLDocMas> coappmas=docMas.stream().filter(t->"Y".equals(t.getCoapplicant())).toList();
                  if(coapp!=null && coappmas.size()>0){

                    int i=0;
                    for(VehicleLoanApplicant vehicleLoanApplicant:coapp){
                      i++;

                %>
                <div class="borders bgapp rounded m-5  border-dashed  ">
                  <div class="list-feed list-feed-solid ms-3 mt-3 mb-3">
                <div id="coApplicant">
                  <h4 class="page-title -0">
                    <u><%=StringUtils.capitalize(vehicleLoanApplicant.getBpmFolderName().replaceAll("_","-"))%></u>
                  </h4>
                  <ol type="i">
                  <%
                    for(VLDocMas app:appmas){
                      boolean mandatory=UploadCompleted(fileUploads,app.getLabelcode(),"C-"+i);
                      if(!mandatory){
                        mand_uploaded++;
                      }
                  %>
                  <li>
                    <div class="d-flex m-2">
                      <div> <label><%=app.getLabelname()%></label></div>
                      <input type="file"    class="form-control" name="coApplicantFiles"  data-mandatory="<%=mandatory?"N":app.getMandatory()%>" data-label="<%=app.getLabelname()%>">
                      <input type="hidden" name="coApplicantFileCodes" value="<%=app.getLabelcode()%>">
                      <input type="hidden" name="coApplicantFileNames" value="<%=app.getFilename()%>">
                      <input type="hidden" name="coApplicantType" value="C-<%=i%>">
                      <input type="hidden" name="coApplicantid" value="<%=vehicleLoanApplicant.getApplicantId()%>">
                    </div>
                    <div class="text-center <%=app.getMandatory().equals("Y")&& !mandatory?"":"d-none"%>">
                      <code class="text-danger">Above field is mandatory</code>

                    </div>
                    <div class="text-center <%=mandatory?"":"d-none"%>">
                      <label class="validation-invalid-label validation-valid-label" >File Uploaded.</label>
                    </div>
                  </li>

                  <%
                      }
                    if("N".equals(vehicleLoanApplicant.getSibCustomer())){
                      boolean mandatory=UploadCompleted(fileUploads,vlother.getLabelcode(),"C-"+i);
                      if(!mandatory){
                        mand_uploaded++;
                      }
                  %>
                    <li>
                      <div class="d-flex m-2">
                        <div> <label><%=vlother.getLabelname()%></label></div>
                        <input type="file"  class="form-control" name="coApplicantFiles"  data-mandatory="<%=mandatory?"N":vlother.getMandatory()%>"data-label="<%=vlother.getLabelname()%>">
                        <input type="hidden" name="coApplicantFileCodes" value="<%=vlother.getLabelcode()%>">
                        <input type="hidden" name="coApplicantFileNames" value="<%=vlother.getFilename()%>">
                        <input type="hidden" name="coApplicantType"  value="C-<%=i%>">
                        <input type="hidden" name="coApplicantid" value="<%=vehicleLoanApplicant.getApplicantId()%>">
                      </div>
                      <div class="text-center <%=vlother.getMandatory().equals("Y")&& !mandatory?"":"d-none"%>">
                        <code class="text-danger">Above field is mandatory</code>

                      </div>
                      <div class="text-center <%=mandatory?"":"d-none"%>">
                        <label class="validation-invalid-label validation-valid-label" >File Uploaded.</label>
                      </div>
                    </li>
                    <%
                      }

                  %>
                </ol>
                </div>
                </div>
                </div>
                <%
                  }
                  }
                %>


                <%

                  List<VLDocMas> gntmas=docMas.stream().filter(t->"Y".equals(t.getGurantor())).toList();
                  if(guarantor!=null && gntmas.size()>0){
                %>
                <div  class="borders bgapp rounded  m-5 border-dashed  ">
                  <div class="list-feed list-feed-solid ms-3 mt-3 mb-3">
                <div id="guarantor">
                  <h4 class="page-title -0">
                    <u>GUARANTOR</u>
                  </h4>
                  <ol type="i">
                  <%
                    for(VLDocMas app:gntmas){
                      boolean mandatory=UploadCompleted(fileUploads,app.getLabelcode(),"G-1");
                      if(!mandatory){
                        mand_uploaded++;
                      }
                  %>
                    <li>
                      <div class="d-flex m-2">
                        <div> <label><%=app.getLabelname()%></label></div>
                        <input type="file"   class="form-control" name="guarantorFiles"   data-mandatory="<%=mandatory?"N":app.getMandatory()%>" data-label="<%=app.getLabelname()%>">
                        <input type="hidden" name="guarantorFileCodes" value="<%=app.getLabelcode()%>">
                        <input type="hidden" name="guarantorFileNames" value="<%=app.getFilename()%>">
                        <input type="hidden" name="guarantorType" value="G-1">
                        <input type="hidden" name="guarantorid" value="<%=guarantor.getApplicantId()%>">
                      </div>
                      <div class="text-center <%=app.getMandatory().equals("Y")&& !mandatory?"":"d-none"%>">
                        <code class="text-danger">Above field is mandatory</code>

                      </div>
                      <div class="text-center <%=mandatory?"":"d-none"%>">
                        <label class="validation-invalid-label validation-valid-label" >File Uploaded.</label>
                      </div>
                    </li>
                  <%
                    }

                  %>
                  </ol>
                </div>
                </div>
                </div>
                <%
                  }
                %>

                <%

                  List<VLDocMas> genric=docMas.stream().filter(t->"Y".equals(t.getGeneric())).toList();
                  if(genric.size()>0){
                %>
                <div  class="borders  bgapp rounded  m-5  border-dashed  ">
                  <div class="list-feed list-feed-solid ms-3 mt-3 mb-3">
                <div id="common">
                  <h4 class="page-title -0">
                    <u>COMMON</u>
                  </h4>
                  <ol type="i">
                  <%
                    for(VLDocMas app:genric){
                      boolean mandatory=UploadCompleted(fileUploads,app.getLabelcode(),"P");
                      if(!mandatory){
                        mand_uploaded++;
                      }
                  %>

                    <li>
                        <div class="d-flex m-2">
                        <div> <label><%=app.getLabelname()%></label></div>
                        <input type="file"   class="form-control" name="commonFiles" data-mandatory="<%=mandatory?"N":app.getMandatory()%>" data-label="<%=app.getLabelname()%>">
                        <input type="hidden" name="commonFileCodes" value="<%=app.getLabelcode()%>">
                        <input type="hidden" name="commonFileNames" value="<%=app.getFilename()%>">
                        <input type="hidden" name="commonType" value="P">
                        </div>
                      <div class="text-center <%=app.getMandatory().equals("Y")&& !mandatory?"":"d-none"%>">
                        <code class="text-danger">Above field is mandatory</code>

                      </div>
                      <div class="text-center <%=mandatory?"":"d-none"%>">
                        <label class="validation-invalid-label validation-valid-label" >File Uploaded.</label>
                      </div>
                    </li>
                  <%
                    }

                  %>

                  </ol>
                </div>
                </div>
                </div>
                <%}%>

                <div class="borders bgapp  m-5 rounded  border-dashed  ">
                  <div class="list-feed list-feed-solid ms-3 mt-3 mb-3">
                    <div id="general">
                      <h4 class="page-title -0">
                        <u>GENERAL   </u>
                      </h4>
                      <ol type="i">
                        <li class="pb-2">
                          <div class="d-flex m-2">
                            <div> <label>VEHICLE OWNER</label>
                            </div>
                            <select class="form-control form-select" name="vl_owner" id="vl_owner">
                              <option value=""> Please Select</option>
                              <%
                                for(VehicleLoanApplicant app : vehicleLoanApplicants.stream().filter(a-> "A".equals(a.getApplicantType()) || "C".equals(a.getApplicantType())).toList()){
                              %>
                              <option  <%=sel_appid.equals(app.getApplicantId())? "selected" :""%> value="<%=app.getApplicantId()%>"> <%=app.getApplName()%></option>
                              <%}%>
                            </select>
                          </div>
                        </li>
                        <li class="pb-2">
                          <div class="d-flex m-2">
                            <div> <label>Whether Vehicle Owner First Time Buyer</label>
                            </div>
                            <select class="form-control form-select " id="vl_owner_status" name="vl_owner_status">
                              <option value=""> Please Select</option>
                              <option <%=sel_masterappid.equals("Y")? "selected" :""%> value="Y"> Yes</option>
                              <option  <%=sel_masterappid.equals("N")? "selected" :""%>value="N"> No</option>
                            </select>
                          </div>
                        </li>
                        <li>
                          <div class="d-flex m-2">
                            <div> <label>If Above is selected as No ,Attach Proof</label></div>
                            <input type="file"   class="form-control" name="commonFiles"  id="vl_first_proof_file" data-mandatory="<%=sel_masterappid.equals("Y")?"N":"Y"%>" data-label=" Vehicle Owner's Existing Vehicles Proof">
                            <input type="hidden" name="commonFileCodes" value="LFTBP">
                            <input type="hidden" name="commonFileNames" value="FIRST_TIME_BUYER_PROOF">
                            <input type="hidden" name="commonType" value="P">
                          </div>
                          <div class="text-center ">
                            <code id="mand3" class="text-danger <%=!sel_masterappid.equals("N")?"":"d-none"%>">Above field is mandatory</code>
                          </div>
                          <div class="text-center <%=sel_masterappid.equals("N")?"":"d-none"%>">
                            <label id="mand4" class="validation-invalid-label validation-valid-label" >File Uploaded.</label>
                          </div>
                        </li>
                        <li class="pb-2">
                          <div class="d-flex m-2">
                            <div> <label>Remarks</label>
                            </div>
                            <div class="w-100 ms-5" >
                              <textarea class="form-control" required="" placeholder="Enter Remarks" id="remarks" name="remarks"></textarea>
                            </div>

                          </div>
                          <a href="#" id="remarkHist" >
                            History
                          </a>
                        </li>
                      </ol>
                    </div>
                  </div>
                </div>


                <div class="text-center">
                  <input type="hidden" id="man_count" value="<%=mand_uploaded%>"/>
                <button id="upload" class="btn btn-teal my-1 me-2"><i class="ph-upload  ms-2"></i>Upload</button>
                <button id="bmsave" class="btn btn-info my-1 me-2" disabled><i class="ph-paper-plane-tilt  ms-2"></i>Save</button>

                </div>
              </form>
              <form action="bmlist" id="back_form" name="back_form" method="GET">
                <input type="hidden" id="slno11" name="slno" value="<%=master.getSlno()%>">
                <input type="hidden" id="action" name="action" value="Modify">
              </form>

            </div>
          </div>

        </div>

        <div id="docList" >

        </div>
      </div>

      <!-- /main charts -->



    </div>
    <!-- /content area -->

  </div>
  <!-- /main content -->

</div>
<!-- /page content -->


<div class="position-absolute top-50 end-100  visible">
  <button   id="toggleList" type="button" class="btn btn-primary position-fixed top-50 end-0 translate-middle-y border-right-0" >
    <i class="ph-file-doc"></i>
  </button>
</div>

<!--Custom Scripts-->
<script src="assets/js/custom/bmdocupload.js"></script>
<!--Custom Scripts-->
<script>
  $(document).ready(function () {

    $("#remarkHist").on('click',function (e) {
      e.preventDefault();
      e.stopPropagation();
      alertmsgframe();
    });

  });
  function alertmsgframe() {
    $('#alert_modal .modal-header').removeClass('bg-danger').addClass('bg-success');
    $('#alert_modal .modal-header').find('.modal-title').text('Remarks');
    $('#alert_modal .modal-body').html('<iframe id="modalIframe" src="remarks?slno='+$('#slno').val()+'" width="100%" height="400" frameborder="0"></iframe>');
    $('#alert_modal').modal('show');
  }

</script>
<los:footer/>
<los:modal/>
</body>
</html>
<%!
  public boolean UploadCompleted( List<VLFileUpload> fileUploads,String label,String app){
    return fileUploads.stream().anyMatch(t-> label.equals(t.getFileCode()) && app.startsWith(t.getAppType()));
  }
%>
