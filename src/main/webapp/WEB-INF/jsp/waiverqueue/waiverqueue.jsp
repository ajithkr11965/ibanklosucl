<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.dto.ResponseDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="com.sib.ibanklosucl.model.VLEmployment" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanSubqueueTask" %>
<%@ page import="java.util.Optional" %>
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
    <title>SIB-LOS DOC -Queue</title>

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
            String currentQueue = vehicleLoanMaster.getQueue();
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

            String channel=vehicleLoanMaster.getChannel();
            String roname= (String) request.getAttribute("roname");

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
                                    <input type="hidden" id="queue" value="WAIVE"/>
                                    <input type="hidden" name="currentQueue" id="currentQueue" value="<%=currentQueue%>">

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
									Branch : <b> <%=userdt.getJoinedSol()%>( <%=userdt.getBrName()%> <%=roname%>)</b>
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
                            <span>General Details </span>
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
                                <jsp:include page="generaldetails_wa.jsp">
                                    <jsp:param name="apponly" value="Y"/>
                                </jsp:include>
                                <jsp:include page="kycdetails_wa.jsp" />
                                <jsp:include page="basicdetails_wa.jsp" />
                                <jsp:include page="employmentdetails_wa.jsp" />
                                <jsp:include page="Incomedetails_wa.jsp" />
                                <jsp:include page="../docqueue/CreditCheck_bd.jsp" />
                            </div>
                            <%
                            }
                            else if ("C".equals(applicant.getApplicantType())) {
                                request.setAttribute("apptype","C-"+coApplicantCount);
                                request.setAttribute("appurl","");
                            %>
                            <div class="tab-pane fade" role="tabpanel"  id="tab-C-<%=coApplicantCount%>">
                               <jsp:include page="generaldetails_wa.jsp">
                                    <jsp:param name="coapponly" value="Y"/>
                                </jsp:include>
                                <jsp:include page="kycdetails_wa.jsp" />
                                <jsp:include page="basicdetails_wa.jsp" />
                                <jsp:include page="employmentdetails_wa.jsp" />
                                <jsp:include page="Incomedetails_wa.jsp" />
                                <jsp:include page="CreditCheck_wa.jsp" />
                            </div>
                            <%
                                coApplicantCount++;
                            }
                            else if ("G".equals(applicant.getApplicantType())) {
                                request.setAttribute("apptype","G-1");
                                request.setAttribute("appurl","");
                            %>
                            <div class="tab-pane fade" role="tabpanel"  id="tab-G-1">
                                <jsp:include page="generaldetails_wa.jsp">
                                    <jsp:param name="coapponly" value="Y"/>
                                </jsp:include>
                                <jsp:include page="kycdetails_wa.jsp" />
                                <jsp:include page="basicdetails_wa.jsp" />
                                <jsp:include page="employmentdetails_wa.jsp" />
                                <jsp:include page="Incomedetails_wa.jsp" />
                                <jsp:include page="CreditCheck_wa.jsp" />
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
                                <jsp:include page="generaldetails_wa.jsp">
                                    <jsp:param name="apponly" value="Y"/>
                                </jsp:include>
                                <jsp:include page="kycdetails_wa.jsp" />
                                <jsp:include page="basicdetails_wa.jsp" />
                                <jsp:include page="employmentdetails_wa.jsp" />
                                <jsp:include page="Incomedetails_wa.jsp" />
                                <jsp:include page="CreditCheck_wa.jsp" />
                            </div>
                            <%
                                }
                            %>


                        </div>
                    </div>


                    <div id="vehList" class="w-100 kt">
                        <form class="z-index-0" action="#">
                            <div class="card-body">
                                <div class="row">
                                    <div class=" border rounded ">
                                        <div class="1"  style="background: #fff !important;">
                                            <!-- User menu -->

                                            <!-- /user menu -->

                                            <div class="rounded border p-2 mb-3">
                                                <!-- Navigation -->

                                                <div class="accordion accordion-icon-collapse" id="vl_bd_int" data-state="2">
                                                    <%--                                            <div class="sidebar-section" id="bottomCard" data-state="0">--%>
                                                    <%request.setAttribute("checker","N");%>
                                                        <jsp:include page="vehicledetails_wa.jsp"/>
                                                        <jsp:include page="loandetails_wa.jsp"/>
                                                        <jsp:include page="eligibilitydetails_wa.jsp"/>
                                                        <jsp:include page="racescoredetails_wa.jsp"/>
                                                        <jsp:include page="validationdetails_wa.jsp"/>
                                                        <jsp:include page="hunterdetails_wa.jsp"/>
                                                        <jsp:include page="blacklistdetails_wa.jsp"/>
                                                        <jsp:include page="deviationdetails_wa.jsp"/>
                                                        <jsp:include page="sanction_wa.jsp"/>
                                                        <jsp:include page="repayment_wa.jsp"/>
                                                        <jsp:include page="roiwaiver_wa.jsp"/>
                                                        <jsp:include page="processingfee_wa.jsp"/>
                                                        <jsp:include page="decision_wa.jsp"/>





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
<style>
    .accordion-header {
        width: 100% !important;
    }
    .accordion-header label{
        width: 100% !important;
    }
    .checkbox-wrapper-16 *,
    .checkbox-wrapper-16 *:after,
    .checkbox-wrapper-16 *:before {
        box-sizing: border-box;
    }
.hide{
    display:none;
}
    .checkbox-wrapper-16 .checkbox-input {
        clip: rect(0 0 0 0);
        -webkit-clip-path: inset(100%);
        clip-path: inset(100%);
        height: 1px;
        overflow: hidden;
        position: absolute;
        white-space: nowrap;
        width: 1px;
    }

    .checkbox-wrapper-16 .checkbox-input:checked + .checkbox-tile {
        border-color: #2260ff;
        box-shadow: 0 5px 10px rgba(0, 0, 0, 0.1);
        color: #2260ff;
    }

    .checkbox-wrapper-16 .checkbox-input:checked + .checkbox-tile:before {
        transform: scale(1);
        opacity: 1;
        background-color: #2260ff;
        border-color: #2260ff;
    }

    .checkbox-wrapper-16 .checkbox-input:checked + .checkbox-tile .checkbox-icon,
    .checkbox-wrapper-16 .checkbox-input:checked + .checkbox-tile .checkbox-label {
        color: #2260ff;
    }

    .checkbox-wrapper-16 .checkbox-input:focus + .checkbox-tile {
        border-color: #2260ff;
        box-shadow: 0 5px 10px rgba(0, 0, 0, 0.1), 0 0 0 4px #b5c9fc;
    }

    .checkbox-wrapper-16 .checkbox-input:focus + .checkbox-tile:before {
        transform: scale(1);
        opacity: 1;
    }

    .checkbox-wrapper-16 .checkbox-tile {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        width: 7rem;
        min-height: 3rem;
        border-radius: 0.5rem;
        border: 2px solid #b5bfd9;
        background-color: #fff;
        box-shadow: 0 5px 10px rgba(0, 0, 0, 0.1);
        transition: 0.15s ease;
        cursor: pointer;
        position: relative;
    }

    .checkbox-wrapper-16 .checkbox-tile:before {
        content: "";
        position: absolute;
        display: block;
        width: 1.25rem;
        height: 1.25rem;
        border: 2px solid #b5bfd9;
        background-color: #fff;
        border-radius: 50%;
        top: 0.25rem;
        left: 0.25rem;
        opacity: 0;
        transform: scale(0);
        transition: 0.25s ease;
        background-image: url("data:image/svg+xml,%3Csvg xmlns='://www.w3.org/2000/svg' width='192' height='192' fill='%23FFFFFF' viewBox='0 0 256 256'%3E%3Crect width='256' height='256' fill='none'%3E%3C/rect%3E%3Cpolyline points='216 72.005 104 184 48 128.005' fill='none' stroke='%23FFFFFF' stroke-linecap='round' stroke-linejoin='round' stroke-width='32'%3E%3C/polyline%3E%3C/svg%3E");
        background-size: 12px;
        background-repeat: no-repeat;
        background-position: 50% 50%;
    }

    .checkbox-wrapper-16 .checkbox-tile:hover {
        border-color: #2260ff;
    }

    .checkbox-wrapper-16 .checkbox-tile:hover:before {
        transform: scale(1);
        opacity: 1;
    }

    .checkbox-wrapper-16 .checkbox-icon {
        transition: 0.375s ease;
        color: #494949;
    }

    .checkbox-wrapper-16 .checkbox-icon svg {
        width: 3rem;
        height: 3rem;
    }

    .checkbox-wrapper-16 .checkbox-label {
        color: #707070;
        transition: 0.375s ease;
        text-align: center;
    }

    /* Hide the default checkbox */
    .container input {
        position: absolute;
        opacity: 0;
        cursor: pointer;
        height: 0;
        width: 0;
    }

</style>
<script>
    $('.accordion-header').on('click',function(e){
            $('.accordion-header').removeClass('show');
        if(!$(this).hasClass('collapsed')) {
            $(this).addClass('show');
        }
    });
    function scrolltoId(divId) {
    var $element = $('#' + divId);

    if ($element.length) {
        var offset = $element.offset().top;
        var additionalOffset = 0; // Adjust this value based on your needs, e.g., height of a fixed header

        $('html, body').animate({
            scrollTop: offset - additionalOffset
        }, 600, function () {
        });
    }
}
</script>
<script src="assets/js/vendor/forms/selects/select2.min.js"></script>
<script src="assets/demo/pages/form_select2.js"></script>
<!--Custom Scripts-->
<script src="assets/js/custom/WI/uploadwiChecker.js"></script>
<script src="assets/js/custom/checker/checkermaster.js"></script>
<%--<script src="assets/js/custom/WI/Wicreate.js"></script>--%>
<!--Custom Scripts-->
<%--<script src="assets/plugins/global/plugins.bundle.js"></script>--%>
<los:modal/>
<los:footer/>
    <div id="legal_pdf_modal" class="modal fade" tabindex="-1"  aria-modal="true"  data-bs-backdrop="static"  role="dialog">
        <div class="modal-dialog modal-xl  modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header bg-success  text-white border-0 ">
                    <h5 class="modal-title"><div class="d-flex justify-content-center"><span class="kt "><i class="ki-duotone text-white ki-finance-calculator fs-1"><span class="path1"></span><span class="path2"></span></i></span>Digitally Signed Document</div></h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" id="legalpdfBase64" value="">
                    <iframe id="legalpdfViewer" style="width: 100%; height: 650px;" frameborder="0"></iframe>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-link" data-bs-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>
    </div>





<div id="pdf_modal" class="modal fade" tabindex="-1"  aria-modal="true"  data-bs-keyboard="false" data-bs-backdrop="static"  role="dialog">
    <div class="modal-dialog modal-xl  modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-success  text-white border-0 ">
                <h5 class="modal-title"><div class="d-flex justify-content-center"><span class="kt "><i class="ki-duotone text-white ki-finance-calculator fs-1"><span class="path1"></span><span class="path2"></span></i></span>Sanction Letter</div></h5>

                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="pdfBase64" value="<%=request.getAttribute("sancLetter").toString()%>">
                <iframe id="pdfViewer" style="width: 100%; height: 650px;" frameborder="0"></iframe>


                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="assets/js/custom/performance-logging.js"></script>
</body>
</html>

