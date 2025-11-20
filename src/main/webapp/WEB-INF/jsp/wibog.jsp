<%--
  Created by IntelliJ IDEA.
  User: SIBL12134
  Date: 13-08-2024
  Time: 11:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.dto.ResponseDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="com.sib.ibanklosucl.model.VLEmployment" %>
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
  <title>SIB-LOS BOG</title>

  <!-- Global stylesheets -->
  <link href="assets/fonts/inter/inter.css" rel="stylesheet" type="text/css">
  <link href="assets/icons/phosphor/styles.min.css" rel="stylesheet" type="text/css">
  <link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
  <link href="assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
  <link href="assets/css/custom.css" rel="stylesheet" type="text/css" />
  <link href="assets/css/custom/wicreate.css" rel="stylesheet" type="text/css" />
  <link href="assets/css/custom/wirmmodify.css" rel="stylesheet" type="text/css" />
  <link href="assets/css/style.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
  <link href="assets/plugins/global/plugins.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
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
  <script src="assets/js/vendor/uploaders/fileinput/fileinput.min.js"></script>
  <script src="assets/js/vendor/uploaders/fileinput/plugins/sortable.min.js"></script>
  <script src="assets/js/vendor/notifications/sweet_alert.min.js"></script>
  <script src="assets/js/vendor/notifications/noty.min.js"></script>
  <!-- /theme JS files -->

</head>

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
      VehicleLoanMaster vehicleLoanMaster=(VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
      List<VehicleLoanApplicant> applicantList=Collections.emptyList();
      if(vehicleLoanMaster.getApplicants()!=null)
        applicantList = vehicleLoanMaster.getApplicants();
      int coApplicantCount = 1;
      boolean modify=false;
      if(applicantList.size()>0){
        modify=true;
      }
      long coappsize=applicantList.stream().filter(d-> "C".equals(d.getApplicantType())).count();

      String bpmerr="",appurl="";
      ResponseDTO  bpmResp= (ResponseDTO) request.getAttribute("bpm");
      if(bpmResp.getStatus().equals("S"))
        appurl=bpmResp.getMsg();
      else
        bpmerr=bpmResp.getMsg();


      String sol_desc = (String) request.getAttribute("sol_desc");
      String roname = (String) request.getAttribute("roname");

    %>

    <div class="navbar-collapse collapse order-2 order-lg-1" >
								<span class="navbar-text d-none d-lg-inline-flex align-items-lg=center me-3">
									<i class="ph-receipt me-2"></i>
									Work Item No : <b><%=vehicleLoanMaster.getWiNum()%></b>
                                  <input type="hidden" name="slno" id="slno" value="<%=vehicleLoanMaster.getSlno()%>">
                                  <input type="hidden" name="winum" id="winum" value="<%=vehicleLoanMaster.getWiNum()%>">
                                  <input type="hidden" name="modify" id="modify" value="<%=modify%>">

                                  <input type="hidden" name="lockflg" id="lockflg" value="<%=request.getAttribute("lockflg")%>">
                                  <input type="hidden" name="lockuser" id="lockuser" value="<%=request.getAttribute("lockuser")%>">
                                  <input type="hidden" name="modifycnt" id="modifycnt" value="<%=coappsize%>">
                                  <input type="hidden" id="bpmerror" value="<%=bpmerr%>"/>
                                  <input type="hidden" id="queue" value="<%=vehicleLoanMaster.getQueue()%>"/>

<%--                                  <input type="hidden" id="currentTab" value="<%=vehicleLoanMaster.getCurrentTab()%>"/>--%>


                                  <input type="hidden" id="currentTab" value="A-1"/>

<%--                                  <input type="hidden" id="currentTab" value="A-6"/>--%>
								</span>
      <%--            <button type="button" id="checkButton">Check Save Buttons</button>--%>
    </div>



  </div>
  <div class="container-fluid">

    <div class="navbar-collapse collapse order-2 order-lg-1" >
								<span class="navbar-text d-none d-lg-inline-flex align-items-lg=center me-3">
									<i class="ph-house me-2"></i>
									Branch : <b><%=vehicleLoanMaster.getSolId()%>(<%=sol_desc%> <%=roname%>)</b>
								</span>
    </div>

  </div>

  <button id="parentToggle" type="button" class="btn btn-primary ">
    <i class="ph-file-doc"></i>DOCLIST
  </button>
</div>


<!-- Page content -->
<div class="page-content pt-0">

  <!-- Main sidebar -->
  <div class="sidebar sidebar-main sidebar-expand-lg align-self-start">

    <!-- Sidebar content -->
    <div class="sidebar-content">

      <!-- Sidebar header -->
      <div class="sidebar-section">
        <div class="sidebar-section-body d-flex justify-content-center">
          <h5 class="sidebar-resize-hide flex-grow-1 my-auto">Details</h5>

          <div>
            <button type="button" class="btn btn-light btn-icon btn-sm rounded-pill border-transparent sidebar-control sidebar-main-resize d-none d-lg-inline-flex">
              <i class="ph-arrows-left-right"></i>
            </button>
            <button type="button" class="btn btn-light btn-icon btn-sm rounded-pill border-transparent sidebar-mobile-main-toggle d-lg-none">
              <i class="ph-x"></i>
            </button>


          </div>
        </div>
      </div>
      <!-- /sidebar header -->


      <!-- Main navigation -->
      <div class="sidebar-section">
        <ul class="nav nav-sidebar" data-nav-type="accordion">
          <li class="nav-item">
            <a href="#" class="nav-link active details" data-code="1">
              <i class="ph-identification-card"></i>
              <span>General Details</span>
            </a>
          </li>
          <li class="nav-item">
            <a href="#" class="nav-link details" data-code="2">
              <i class="ph-identification-card"></i>
              <span>KYC Details</span>
            </a>
          </li>
          <li class="nav-item">
            <a href="#" class="nav-link details" data-code="3">
              <i class="ph-user-circle"></i>
              <span>Basic Details</span>
            </a>
          </li>
          <li class="nav-item">
            <a href="#" class="nav-link details"   data-code="4">
              <i class="ph-activity"></i>
              <span>Employment Details</span>
            </a>
          </li>
          <li class="nav-item" >
            <a href="#" class="nav-link details"   data-code="5">
              <i class="ph-money"></i>
              <span>Program Details</span>
            </a>
          </li>
          <li class="nav-item" >
            <a href="#" class="nav-link details"   data-code="6">
              <i class="ph-credit-card"></i>
              <span>Credit check</span>
            </a>
          </li>
          <!-- /layout -->

        </ul>
      </div>
      <!-- /main navigation -->

    </div>
    <!-- /sidebar content -->
    <article title="Car Inspection  3D Icon" class="container_EOFSc" style="display: block;border-radius: 12px;background-color: #FAFAFC;position: relative;height: 22.125rem;background-color: #F5F6FA !important;">
      <picture class="pict">
        <source type="image/webp" srcset="assets/images/car-loan.png?f=webp 1x, assets/images/car-loan.png?f=webp 2x">
        <img alt="Car Inspection  3D Icon" loading="lazy" src="assets/images/car-loan.png?f=webp" srcset="assets/images/car-loan.png?f=webp 1x, assets/images/car-loan.png?f=webp 2x" style="width: 19em;">
      </picture>
    </article>
  </div>
  <!-- /main sidebar -->

  <!-- Main content -->
  <div class="content-wrapper">

    <!-- Content area -->
    <div class="content">

      <!-- Main charts -->
      <div class="d-flex flex-row min-vh-100">
        <div id="appList" class="w-100">

          <div class="card">
            <div class="card-header d-flex align-items-center">
              <h5 class="mb-0">Loan Application</h5>
              <div class="ms-auto">
                <%--                <button type="button" id="addcoapp" class="btn btn-outline-primary ms-auto">--%>
                <%--                  Co-Applicant--%>
                <%--                  <i class="fas fa-user-plus ms-2"></i>--%>
                <%--                </button>--%>

                <%if(!applicantList.stream().anyMatch(a -> "G".equals(a.getApplicantType()))){%>
                <%--                <button type="button" id="addguarantor" class="me-2 btn btn-outline-danger ms-auto">--%>
                <%--                  GUARANTOR--%>
                <%--                  <i class="fas fa-user-plus ms-2"></i>--%>
                <%--                </button>--%>
                <%}%>
              </div>

            </div>
            <ul class="nav nav-tabs nav-justified mb-0" role="tablist" id="loanapp">
              <%
                for (VehicleLoanApplicant applicant : applicantList) {
                  if ("A".equals(applicant.getApplicantType())) {
              %>
              <li class="nav-item" role="presentation"><a href="#tab-A" class="nav-link apptype active  alert alert-primary " data-bs-toggle="tab" aria-selected="false" role="tab" tabindex="-1" data-app="A"><i class="ph-user fw-semibold"></i>APPLICANT</a></li>
              <%
              } else if ("C".equals(applicant.getApplicantType())) {
              %>
              <li class="nav-item" role="presentation"><a href="#tab-C-<%=coApplicantCount%>" class="nav-link apptype   alert alert-primary  " data-bs-toggle="tab" aria-selected="false" role="tab" tabindex="-1" data-app="C-<%=coApplicantCount%>"><i class="ph-user-circle-plus "></i><span data-applicationtype="C">Co-Applicant-<%=coApplicantCount%></span>
<%--                <button type="button" class="btn-close closeapplicant pe-5" data-closecode="C-<%=coApplicantCount%>"></button>--%>
              </a>
              </li>
              <%
                coApplicantCount++;
              }
              else if ("G".equals(applicant.getApplicantType())) {
              %>
              <li class="nav-item" role="presentation"><a href="#tab-G-1" class="nav-link apptype   alert alert-primary   " data-bs-toggle="tab" aria-selected="false" role="tab" tabindex="-1" data-app="G-1"><i class="ph-bank   fw-semibold"></i><span data-applicationtype="G">GUARANTOR</span>
<%--                <button type="button" class="btn-close closeapplicant pe-5" data-closecode="G-1"></button>--%>
              </a></li>
              <%

                  }
                }
                if(applicantList.size()==0){
              %>
              <li class="nav-item" role="presentation"><a href="#tab-CA" class="nav-link apptype active  alert alert-primary " data-bs-toggle="tab" aria-selected="false" role="tab" tabindex="-1" data-app="A"><i class="ph-user fw-semibold"></i>APPLICANT</a></li>
              <%
                }

              %>
            </ul>


            <div class="tab-content card-body" id="loanbody">


              <%
                coApplicantCount=1;
                for (VehicleLoanApplicant applicant : applicantList) {
                  request.setAttribute("general",applicant);
                  if ("A".equals(applicant.getApplicantType())) {
              %>
              <div class="tab-pane fade active show" id="tab-A" role="tabpanel">
                <%
                  request.setAttribute("apptype","A");
                  request.setAttribute("appurl",appurl);
                %>
                <jsp:include page="rcchecker/generaldetails_rc.jsp">
                  <jsp:param name="apponly" value="Y"/>
                </jsp:include>
                <jsp:include page="rcchecker/kycdetails_rc.jsp" />
                <jsp:include page="rcchecker/basicdetails_rc.jsp" />
                <jsp:include page="rcchecker/employmentdetails_rc.jsp" />
                <jsp:include page="rcchecker/Incomedetails_rc.jsp" />
                <jsp:include page="rcchecker/CreditCheck_rc.jsp" />
              </div>
              <%
              }
              else if ("C".equals(applicant.getApplicantType())) {
                request.setAttribute("apptype","C-"+coApplicantCount);
                request.setAttribute("appurl","");
              %>
              <div class="tab-pane fade" role="tabpanel"  id="tab-C-<%=coApplicantCount%>">
                <jsp:include page="rcchecker/generaldetails_rc.jsp">
                  <jsp:param name="coapponly" value="Y"/>
                </jsp:include>
                <jsp:include page="rcchecker/kycdetails_rc.jsp" />
                <jsp:include page="rcchecker/basicdetails_rc.jsp" />
                <jsp:include page="rcchecker/employmentdetails_rc.jsp" />
                <jsp:include page="rcchecker/Incomedetails_rc.jsp" />
                <jsp:include page="rcchecker/CreditCheck_rc.jsp" />
              </div>
              <%
                coApplicantCount++;
              }
              else if ("G".equals(applicant.getApplicantType())) {
                request.setAttribute("apptype","G-1");
                request.setAttribute("appurl","");
              %>
              <div class="tab-pane fade" role="tabpanel"  id="tab-G-1">
                <jsp:include page="rcchecker/generaldetails_rc.jsp">
                  <jsp:param name="coapponly" value="Y"/>
                </jsp:include>
                <jsp:include page="rcchecker/kycdetails_rc.jsp" />
                <jsp:include page="rcchecker/basicdetails_rc.jsp" />
                <jsp:include page="rcchecker/employmentdetails_rc.jsp" />
                <jsp:include page="rcchecker/Incomedetails_rc.jsp" />
                <jsp:include page="rcchecker/CreditCheck_rc.jsp" />
              </div>
              <%
                  }

                }
                if(applicantList.size()==0){
              %>
              <div class="tab-pane fade active show" id="tab-A" role="tabpanel">
                <%
                  request.setAttribute("apptype","A");
                  request.setAttribute("init","Y");
                %>
                <jsp:include page="rcchecker/generaldetails_rc.jsp">
                  <jsp:param name="apponly" value="Y"/>
                </jsp:include>
                <jsp:include page="rcchecker/kycdetails_rc.jsp" />
                <jsp:include page="rcchecker/basicdetails_rc.jsp" />
                <jsp:include page="rcchecker/employmentdetails_rc.jsp" />
                <jsp:include page="rcchecker/Incomedetails_rc.jsp" />
                <jsp:include page="rcchecker/CreditCheck_rc.jsp" />
              </div>
              <%
                }
              %>


            </div>
          </div>
          <div class="w-100 kt">
            <form id="wicrtamberform" class="z-index-0"  action="wibogsave">
              <input type="hidden" id="cadecision" name="cadecision"/>
              <div class="card-body">
                <div class="row">
                  <div class="1 w-100 border rounded mb-lg-4 p-0">
                    <div class="1"  style="background: #fff !important;">
                      <!-- User menu -->

                      <!-- /user menu -->

                      <div class="rounded border p-2 mb-3">
                        <!-- Navigation -->

                        <div class="accordion accordion-icon-collapse" id="vl_rc_int" data-state="2">
                          <%--                                            <div class="sidebar-section" id="bottomCard" data-state="0">--%>
                          <%request.setAttribute("checker","N");%>

                          <jsp:include page="rcchecker/vehicledetails_rc.jsp" />
                            <jsp:include page="rcchecker/loandetails_rc.jsp" />
                          <jsp:include page="rcchecker/eligibilitydetails_rc.jsp" />
                          <jsp:include page="rcchecker/racescoredetails_rc.jsp" />
                          <jsp:include page="rcchecker/validationdetails_rc.jsp" />
                          <jsp:include page="rcchecker/hunterdetails_rc.jsp" />
                          <jsp:include page="rcchecker/blacklistdetails_rc.jsp" />
                          <jsp:include page="rcchecker/deviationdetails_rc.jsp" />
                          <jsp:include page="crtchecker/vehicleloanwarning.jsp"/>
                            <jsp:include page="docqueue/bogcifdedup.jsp"/>
                            <jsp:include page="checker/cifcreation.jsp"/>
                          <jsp:include page="rcchecker/remarks_ca.jsp" />
                          <%--                                                <jsp:include page="rcchecker/bredetails_rc.jsp" />--%>


                        </div>
                        <div class="text-center">
                          <button type="button" id="bogfinalsubmit" class="btn btn-sm btn-success  ms-1 me-1 bogfinalsubmit">Submit<i class="ph-paper-plane-tilt ms-2"></i></button>
                          <button type="button"  class="btn btn-sm btn-primary ms-1 me-1" id="backbtnbog">Back </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </form>
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
<script src="assets/js/vendor/forms/selects/select2.min.js"></script>
<script src="assets/demo/pages/form_select2.js"></script>
<!--Custom Scripts-->
<script src="assets/js/custom/WI/kycdetails.js"></script>
<script src="assets/js/custom/WI/basicdetails.js"></script>
<script src="assets/js/custom/WI/generaldetails.js"></script>
<script src="assets/js/custom/WI/employmentdetails.js"></script>
<script src="assets/js/custom/WI/Incomedetails.js"></script>
<script src="assets/js/custom/WI/creditCheck.js"></script>
<script src="assets/js/custom/WI/uploadwi.js"></script>
<script src="assets/js/custom/WI/Wicreate.js"></script>
<script src="assets/js/custom/checker/wibog.js"></script>
<%--<script src="assets/js/custom/checker/wicrtamber.js"></script>--%>
<%--<script src="assets/js/custom/WI/programdetails.js"></script>--%>
<!--Custom Scripts-->
<%--<script src="assets/plugins/global/plugins.bundle.js"></script>--%>
<los:modal/>
<los:footer/>
</body>
</html>
