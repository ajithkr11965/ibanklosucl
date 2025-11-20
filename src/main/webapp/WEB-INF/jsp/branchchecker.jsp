
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.dto.ResponseDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="com.sib.ibanklosucl.model.VLEmployment" %>
<%@ page import="com.sib.ibanklosucl.model.integrations.VLHunterDetails" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="los" uri="http://www.siblos.com/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en" dir="ltr" data-bs-theme="light">
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
    <link href="assets/css/custom/wichecker.css" rel="stylesheet" type="text/css"/>
    <link href="assets/plugins/global/plugins.bundle.prefixed.css" rel="stylesheet" type="text/css">
		<link href="assets/css/style.bundle.prefixed.css" rel="stylesheet" type="text/css">
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
            String currentQueue = vehicleLoanMaster.getQueue();
            String sol_desc = (String) request.getAttribute("sol_desc");
            String roname = (String) request.getAttribute("roname");
            String bureauBlock = (String) request.getAttribute("bureauBlock");
            List<VehicleLoanApplicant> applicantList=Collections.emptyList();
            if(vehicleLoanMaster.getApplicants()!=null)
                applicantList = vehicleLoanMaster.getApplicants();
            int coApplicantCount = 1;
            String bpmerr="",appurl="";
            ResponseDTO  bpmResp= (ResponseDTO) request.getAttribute("bpm");
            if(bpmResp.getStatus().equals("S"))
                appurl=bpmResp.getMsg();
            else
                bpmerr=bpmResp.getMsg();

            String channel=vehicleLoanMaster.getChannel();
             Boolean hunterCheckPerformed = (Boolean) request.getAttribute("hunterCheckPerformed");
            Boolean hunterMatchFound = (Boolean) request.getAttribute("hunterMatchFound");
            List<VLHunterDetails> allHunterDetails = (List<VLHunterDetails>) request.getAttribute("allHunterDetails");

        %>

        <div class="navbar-collapse collapse order-2 order-lg-1" >
								<span class="navbar-text d-none d-lg-inline-flex align-items-lg=center me-3">
									<i class="ph-receipt me-2"></i>
									Work Item No : <b><%=vehicleLoanMaster.getWiNum()%></b>
                                    <%
                                        if(request.getAttribute("makerCheckerSame") != null && request.getAttribute("makerCheckerSame").toString().equals("Y")){
                                    %>
                                    <input type="hidden" name="makerCheckerSame" id="makerCheckerSame" value="Y"/>
                                    <%
                                        }

                                        String queue = vehicleLoanMaster.getQueue();

                                    %>
                                  <input type="hidden" name="slno" id="slno" value="<%=vehicleLoanMaster.getSlno()%>">
                                  <input type="hidden" name="winum" id="winum" value="<%=vehicleLoanMaster.getWiNum()%>">

                                  <input type="hidden" name="lockflg" id="lockflg" value="<%=request.getAttribute("lockflg")%>">
                                  <input type="hidden" name="lockuser" id="lockuser" value="<%=request.getAttribute("lockuser")%>">
                                  <input type="hidden" id="bpmerror" value="<%=bpmerr%>"/>
                                    <input type="hidden" id="queue" value="<%=queue%>"/>
                                    <input type="hidden" name="currentQueue" id="currentQueue" value="<%=currentQueue%>">
                                    <input type="hidden" name="bureauBlock" id="bureauBlock" value="<%=bureauBlock%>">

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
									Branch : <b> <%=vehicleLoanMaster.getHomeSol()%>( <%=sol_desc%>)( <%=roname%>)</b>
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
                    <%--          <li class="nav-item" >--%>
                    <%--            <a href="#" class="nav-link details"   data-code="7"">--%>
                    <%--              <i class="ph-bank"></i>--%>
                    <%--              <span>Financial Details</span>--%>
                    <%--            </a>--%>
                    <%--          </li>--%>
                    <%--          <li class="nav-item" >--%>
                    <%--            <a href="#" class="nav-link details"   data-code="8">--%>
                    <%--              <i class="ph-car"></i>--%>
                    <%--              <span>Vehicle Details</span>--%>
                    <%--            </a>--%>
                    <%--          </li>--%>
                    <%--          <li class="nav-item" >--%>
                    <%--            <a href="#" class="nav-link details"   data-code="9">--%>
                    <%--              <i class="ph-lock"></i>--%>
                    <%--              <span>Insurance Details</span>--%>
                    <%--            </a>--%>
                    <%--          </li>--%>
                    <%--          <li class="nav-item" >--%>
                    <%--            <a href="#vehList" class="nav-link details"   data-code="10">--%>
                    <%--              <i class="ph-crown"></i>--%>
                    <%--              <span>Vehicle Details</span>--%>
                    <%--            </a>--%>
                    <%--          </li>--%>
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

                        </div>
                        <ul class="nav nav-tabs nav-tabs-underline nav-justified mb-0" role="tablist" id="loanapp">
                            <%
                                for (VehicleLoanApplicant applicant : applicantList) {
                                    if ("A".equals(applicant.getApplicantType())) {
                            %>
                            <li class="nav-item" role="presentation"><a href="#tab-A" class="nav-link apptype alert show alert-primary" data-bs-toggle="tab" aria-selected="false" role="tab" tabindex="-1" data-app="A"><i class="ph-user text-white fw-semibold"></i>APPLICANT</a></li>
                            <%
                            } else if ("C".equals(applicant.getApplicantType())) {
                            %>
                            <li class="nav-item" role="presentation"><a href="#tab-C-<%=coApplicantCount%>" class="nav-link apptype alert show alert-primary" data-bs-toggle="tab" aria-selected="false" role="tab" tabindex="-1" data-app="C-<%=coApplicantCount%>"<i class="ph-user-circle-plus text-black"></i><span data-applicationtype="C">Co-Applicant-<%=coApplicantCount%></span>
                            </a>
                            </li>
                            <%
                                coApplicantCount++;
                            }
                            else if ("G".equals(applicant.getApplicantType())) {
                            %>
                            <li class="nav-item" role="presentation"><a href="#tab-G-1" class="nav-link apptype alert show alert-primary " data-bs-toggle="tab" aria-selected="false" role="tab" tabindex="-1" data-app="G-1"><i class="ph-bank text-dark fw-semibold"></i><span data-applicationtype="G">GUARANTOR</span>

                            </a></li>
                            <%

                                    }
                                }
                                if(applicantList.size()==0){
                            %>
                            <li class="nav-item" role="presentation"><a href="#tab-CA" class="nav-link apptype alert show alert-primary" data-bs-toggle="tab" aria-selected="false" role="tab" tabindex="-1" data-app="A"><i class="ph-user text-dark fw-semibold"></i>APPLICANT</a></li>
                            <%
                                }

                            %>

                        </ul>


                        <div class="tab-content card-body" id="loanbody">


                            <%
                                request.setAttribute("hunterCheckPerformed",hunterCheckPerformed);
                                request.setAttribute("hunterMatchFound",hunterMatchFound);
                                request.setAttribute("allHunterDetails",allHunterDetails);
                                 request.setAttribute("bureauBlock",bureauBlock);

                                coApplicantCount=1;
                                for (VehicleLoanApplicant applicant : applicantList) {
                                    request.setAttribute("general",applicant);
                                    if ("A".equals(applicant.getApplicantType())) {
                            %>
                            <div class="tab-pane fade active show" id="tab-A" role="tabpanel">
                                <%
                                    request.setAttribute("apptype","A");
                                %>
                                <jsp:include page="checker/generaldetails_bc.jsp">
                                    <jsp:param name="apponly" value="Y"/>
                                </jsp:include>
                                <jsp:include page="checker/kycdetails_bc.jsp" />
                                <jsp:include page="checker/basicdetails_bc.jsp" />
                                <jsp:include page="checker/employmentdetails_bc.jsp" />
                                <jsp:include page="checker/Incomedetails_bc.jsp" />
                                <jsp:include page="checker/creditcheck_bc.jsp" />
                            </div>
                            <%
                            }
                            else if ("C".equals(applicant.getApplicantType())) {
                                request.setAttribute("apptype","C-"+coApplicantCount);
                                request.setAttribute("appurl","");
                            %>
                            <div class="tab-pane fade" role="tabpanel"  id="tab-C-<%=coApplicantCount%>">
                                <jsp:include page="checker/generaldetails_bc.jsp">
                                    <jsp:param name="coapponly" value="Y"/>
                                </jsp:include>
                                <jsp:include page="checker/kycdetails_bc.jsp" />
                                <jsp:include page="checker/basicdetails_bc.jsp" />
                                <jsp:include page="checker/employmentdetails_bc.jsp" />
                                <jsp:include page="checker/Incomedetails_bc.jsp" />
                                <jsp:include page="checker/creditcheck_bc.jsp" />
                            </div>
                            <%
                                coApplicantCount++;
                            }
                            else if ("G".equals(applicant.getApplicantType())) {
                                request.setAttribute("apptype","G-1");
                                request.setAttribute("appurl","");
                            %>
                            <div class="tab-pane fade" role="tabpanel"  id="tab-G-1">
                                <jsp:include page="checker/generaldetails_bc.jsp">
                                    <jsp:param name="coapponly" value="Y"/>
                                </jsp:include>
                                <jsp:include page="checker/kycdetails_bc.jsp" />
                                <jsp:include page="checker/basicdetails_bc.jsp" />
                                <jsp:include page="checker/employmentdetails_bc.jsp" />
                                <jsp:include page="checker/Incomedetails_bc.jsp" />
                                <jsp:include page="checker/creditcheck_bc.jsp" />
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
                                <jsp:include page="checker/generaldetails_bc.jsp">
                                    <jsp:param name="apponly" value="Y"/>
                                </jsp:include>
                                <jsp:include page="checker/kycdetails_bc.jsp" />
                                <jsp:include page="checker/basicdetails_bc.jsp" />
                                <jsp:include page="checker/employmentdetails_bc.jsp" />
                                <jsp:include page="checker/Incomedetails_bc.jsp" />
                                <jsp:include page="checker/creditcheck_bc.jsp" />
                            </div>
                            <%
                                }
                            %>


                        </div>
                    </div>

                    <div class="card">
                        <div class="card-body">
                        <div class="card-group-vertical">
                            <div class="card border shadow-none">
                                <div class="card-header">
                                    <h6 class="mb-0">
                                        <a data-bs-toggle="collapse" class="d-flex align-items-center text-body collapsed" href="#vehDetailsContent" aria-expanded="false" id="vehDetailslink" style="color: #958207 !important;">
                                            <img src="assets/images/vehicle.png" style="width: 3em;" />
                                            Vehicle Details
                                            <i class="ph-caret-down collapsible-indicator ms-auto"></i>
                                        </a>
                                    </h6>
                                </div>

                                <div id="vehDetailsContent"  class="collapse" style="">
                                    <div class="card-body">
                                        <jsp:include page="checker/vehicledetails_bc.jsp" />
                                    </div>
                                </div>
                            </div>

                            <div class="card border shadow-none">
                                <div class="card-header">
                                    <h6 class="mb-0">
                                        <a class="d-flex align-items-center text-body collapsed" data-bs-toggle="collapse" href="#loanDetailsContent" aria-expanded="false" id="loanDetailslink" style="color: #4c83d9  !important;">
                                            <img src="assets/images/loan.png" style="width: 3em;" /> Loan Details
                                            <i class="ph-caret-down collapsible-indicator ms-auto"></i>
                                        </a>
                                    </h6>
                                </div>
                                <!-- style="background: rgb(249, 251, 255);" -->
                                <div id="loanDetailsContent"class="collapse" style="">
                                    <div class="card-body">
                                        <jsp:include page="checker/loandetails_bc.jsp" />
                                    </div>
                                </div>
                            </div>

                            <div class="card border shadow-none">
                                <div class="card-header">
                                    <h6 class="mb-0">
                                        <a class="d-flex align-items-center text-body collapsed" data-bs-toggle="collapse" href="#eligibilityDetailsContent" aria-expanded="false" id="ebityDetailslink" style="color: #46720d !important;">
                                            <img src="assets/images/finance.png" style="width: 3em;" />
                                            Eligibility Details
                                            <i class="ph-caret-down collapsible-indicator ms-auto"></i>
                                        </a>
                                    </h6>
                                </div>
                                <!--  style="background: rgb(249, 253, 246);" -->
                                <div id="eligibilityDetailsContent"  class="collapse show" style="">
                                    <div class="card-body">
                                        <jsp:include page="checker/eligibilitydetails_bc.jsp" />
                                    </div>
                                </div>
                            </div>
                        </div>
                        </div>
                        <div id="kt_account_settings_deactivate" class="p-4 m-4 show">
                            <!--begin::Card body-->
                            <div class="p-9">
                                <!--begin::Form input row-->
                                <div class="input-group mt-2 mt-2">
                                    <a href="#" id="remarkHist" class="badge badge-light-danger fs-base">
																				History
																			</a>
                                    <span class="input-group-text">Remarks</span>
                                    <textarea class="form-control" aria-label="Checker Remarks" name="checker_remarks" id="checker_remarks"></textarea>
                                </div>
                                <!--end::Form input row-->
                            </div>
                            <!--end::Card body-->
                            <!--begin::Card footer-->
                            <div class=" d-flex justify-content-lg-center py-6 px-9 mt-3">
                                <%if(bureauBlock.equals("N")){%>
                                <button id="checker_init" type="submit" class="btn btn-success fw-semibold ms-4 checker-sub-init">Save & Proceed</button>
                                <%}%>
                                <button id="checker_sendback" type="submit" class="btn btn-warning fw-semibold ms-4">Sendback</button>
                                <button id="checker_reject" type="submit" class="btn btn-danger fw-semibold ms-4">Reject</button>
                            </div>
                            <!--end::Card footer-->
                        </div>
                        <!--end::Content-->

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
<script src="assets/js/custom/WI/uploadwiChecker.js"></script>
<script src="assets/js/custom/checker/checkermaster.js"></script>
<!--Custom Scripts-->

<los:modal/>
<los:footer/>
</body>
</html>

