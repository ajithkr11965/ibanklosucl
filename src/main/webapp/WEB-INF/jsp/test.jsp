<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.dto.ResponseDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="com.sib.ibanklosucl.model.VLEmployment" %>
<%@ page import="com.sib.ibanklosucl.dto.VLDocMas" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %><%--
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

<body>
<%--<los:header/>--%>
<los:loader/>


<!-- Page header -->
<div class="page-header">
  <div class="page-header-content d-lg-flex">
    <div class="d-flex">
      <div class="d-flex d-lg-none me-2">
        <button type="button" class="navbar-toggler sidebar-mobile-main-toggle rounded">
          <i class="ph-list"></i>
        </button>
      </div>
      <h4 class="page-title mb-0">
        Home - <span class="fw-normal">PowerDrive</span>
      </h4>

      <a href="#page_header" class="btn btn-light align-self-center collapsed d-lg-none border-transparent rounded-pill p-0 ms-auto" data-bs-toggle="collapse">
        <i class="ph-caret-down collapsible-indicator ph-sm m-1"></i>
      </a>
    </div>
    <div  class=" m-auto me-0">
      <a type="button" href="dashboard" class="btn btn-outline-warning btn-labeled btn-labeled-start btn-sm">
                                        <span class="btn-labeled-icon bg-warning text-white">
                                            <i class="ph-arrow-circle-left  ph-sm"></i>
                                        </span>Back</a>
    </div>
  </div>
</div>
<!-- /page header -->

<div class="navbar navbar-expand-lg shadow rounded py-1 mb-3">
  <div class="container-fluid">


    <%
      Employee userdt= (Employee) request.getAttribute("userdata");
      VehicleLoanMaster master= (VehicleLoanMaster) request.getAttribute("vlmaster");
      List<VLDocMas> docMas= (List<VLDocMas>) request.getAttribute("docmas");

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
									Branch : <b><%=userdt.getJoinedSol()%></b>
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
              <h5 class="mb-0">Loan Application Documment Upload</h5>
            </div>

            <div class="tab-content card-body" id="loanbody">
              <form id="fileUploadForm" method="post" enctype="multipart/form-data">
                <input type="hidden" name="slno" value="<%=master.getSlno()%>">
                <input type="hidden" name="winum" value="<%=master.getWiNum()%>">

                <%
                  List<VehicleLoanApplicant> vehicleLoanApplicants=master.getApplicants();
                  VehicleLoanApplicant applicant=vehicleLoanApplicants.stream().filter(t-> "A".equals(t.getApplicantType())).toList().get(0);
                  VehicleLoanApplicant guarantor=vehicleLoanApplicants.stream().filter(t-> "G".equals(t.getApplicantType())).toList().get(0);
                  List<VehicleLoanApplicant> coapp=vehicleLoanApplicants.stream().filter(t-> "C".equals(t.getApplicantType())).toList();

                  List<VLDocMas> appmas=docMas.stream().filter(t->"Y".equals(t.getApplicant())).toList();

                  if(appmas.size()>0){
                %>
                <div id="applicant">
                  <h3>APPLICANT</h3>
                  <%
                    for(VLDocMas app:appmas){
                  %>
                  <label><%=app.getLabelname()%></label>
                  <input type="file" name="applicantFiles" data-mandatory="<%=app.getMandatory()%>" data-label="<%=app.getLabelname()%>">
                  <input type="hidden" name="applicantFileCodes" value="<%=app.getLabelcode()%>">
                  <input type="hidden" name="applicantFileNames" value="<%=app.getFilename()%>">
                  <input type="hidden" name="applicantType" value="A-1">
                  <%
                    }
                  %>
                </div>
                <%
                  }
                  List<VLDocMas> coappmas=docMas.stream().filter(t->"Y".equals(t.getCoapplicant())).toList();
                  if(coapp!=null && coappmas.size()>0){
                    for(VehicleLoanApplicant vehicleLoanApplicant:coapp){
                %>

                <div id="coApplicant">
                  <h3><%=StringUtils.capitalize(vehicleLoanApplicant.getBpmFolderName())%></h3>
                  <%
                    int i=0;
                    for(VLDocMas app:appmas){
                      i++;
                  %>
                  <label><%=app.getLabelname()%></label>
                  <input type="file" name="coApplicantFiles" data-mandatory="<%=app.getMandatory()%>" data-label="<%=app.getLabelname()%>">
                  <input type="hidden" name="coApplicantFileCodes" value="<%=app.getLabelcode()%>">
                  <input type="hidden" name="coApplicantFileNames" value="<%=app.getFilename()%>">
                  <input type="hidden" name="coApplicantType" value="C-<%=i%>">
                  <%
                      }
                    }
                  %>
                </div>
                <%
                  }
                %>


                <%

                  List<VLDocMas> gntmas=docMas.stream().filter(t->"Y".equals(t.getGurantor())).toList();
                  if(guarantor!=null && gntmas.size()>0){
                %>

                <div id="guarantor">
                  <h3>GUARANTOR</h3>
                  <%
                    for(VLDocMas app:gntmas){
                  %>
                  <label><%=app.getLabelname()%></label>
                  <input type="file" name="guarantorFiles"   data-mandatory="<%=app.getMandatory()%>" data-label="<%=app.getLabelname()%>">
                  <input type="hidden" name="guarantorFileCodes" value="<%=app.getLabelcode()%>">
                  <input type="hidden" name="guarantorFileNames" value="<%=app.getFilename()%>">
                  <input type="hidden" name="guarantorType" value="G-1">
                  <%
                    }

                  %>
                </div>
                <%
                  }
                %>

                <%

                  List<VLDocMas> genric=docMas.stream().filter(t->"Y".equals(t.getGeneric())).toList();
                  if(genric.size()>0){
                %>

                <div id="common">
                  <h3>COMMON</h3>
                  <%
                    for(VLDocMas app:genric){
                  %>
                  <label><%=app.getLabelname()%></label>
                  <input type="file" name="commonFiles" data-mandatory="<%=app.getMandatory()%>" data-label="<%=app.getLabelname()%>">
                  <input type="hidden" name="commonFileCodes" value="<%=app.getLabelcode()%>">
                  <input type="hidden" name="commonFileNames" value="<%=app.getFilename()%>">
                  <input type="hidden" name="commonType" value="P">
                  <%
                    }

                  %>
                </div>
                <%}%>


                <button id="upload" type="submit" value="Upload">Upload</button>

                <%--  <input id="docsubmit" type="submit" value="Submit">--%>
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


<los:footer/>
</body>
</html>

