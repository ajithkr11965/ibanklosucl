<%--
  Created by IntelliJ IDEA.
  User: SIBL12134
  Date: 02-07-2024
  Time: 18:12
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
<html lang="en" dir="ltr" data-bs-theme="light">
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<meta name="_csrf" content="${_csrf.token}"/>
		<meta name="_csrf_header" content="${_csrf.headerName}"/>
		<title>SIB-LOS</title>

		<!-- Global stylesheets -->
		<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700">
		<link href="assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css">
		<link href="assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css">
		<link href="assets/plugins/global/plugins.bundle.prefixed.css" rel="stylesheet" type="text/css">
		<link href="assets/css/style.bundle.prefixed.css" rel="stylesheet" type="text/css">
		<link href="assets/icons/phosphor/styles.min.css" rel="stylesheet" type="text/css">
		<link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
		<link href="assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
		<link href="assets/css/custom.css" rel="stylesheet" type="text/css"/>
		<link href="assets/css/custom/wicreate.css" rel="stylesheet" type="text/css"/>

		<link href="assets/css/custom/wichecker.css" rel="stylesheet" type="text/css"/>


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
	<%
		Employee userdt = (Employee) request.getAttribute("userdata");
		VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
		String sol_desc = (String) request.getAttribute("sol_desc");
		String roname = (String) request.getAttribute("roname");
		List<VehicleLoanApplicant> applicantList = Collections.emptyList();
		if (vehicleLoanMaster.getApplicants() != null)
			applicantList = vehicleLoanMaster.getApplicants();
		request.setAttribute("applicantList", applicantList);
		int coApplicantCount = 1;
        String bpmerr="",appurl="";
         ResponseDTO  bpmResp= (ResponseDTO) request.getAttribute("bpm");
            if(bpmResp.getStatus().equals("S"))
                appurl=bpmResp.getMsg();
            else
                bpmerr=bpmResp.getMsg();
            Boolean hunterCheckPerformed = (Boolean) request.getAttribute("hunterCheckPerformed");
            Boolean hunterMatchFound = (Boolean) request.getAttribute("hunterMatchFound");
            String bureauBlock = (String) request.getAttribute("bureauBlock");
            //out.print(hunterCheckPerformed);
          //  out.print(hunterMatchFound);


	%>
	<body id="kt_body" class="header-tablet-and-mobile-fixed aside-enabled" style="background-color: #ffffff">
		<%--<los:header/>--%>
		<los:loader/>

		<div class="d-flex flex-column flex-root">
			<div class="page d-flex flex-row flex-column-fluid">

			</div>
		</div>
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
				<div class=" m-auto me-0">
					<a type="button" href="wicheckerget?slno=<%=vehicleLoanMaster.getSlno()%>&action=BC" class="btn btn-outline-warning btn-labeled btn-labeled-start btn-sm">
                                        <span class="btn-labeled-icon bg-warning text-white">
                                            <i class="ph-arrow-circle-left  ph-sm"></i>
                                        </span>Back</a>
				</div>
			</div>
		</div>
		<!-- /page header -->

		<div class="navbar navbar-expand-lg shadow rounded py-1 mb-3">
			<div class="container-fluid">

				<div class="navbar-collapse collapse order-2 order-lg-1">
								<span class="navbar-text d-none d-lg-inline-flex align-items-lg=center me-3">
									<i class="ph-receipt me-2"></i>
									Registration No : <b><%=vehicleLoanMaster.getWiNum()%></b>
                                  <input type="hidden" name="slno" id="slno" value="<%=vehicleLoanMaster.getSlno()%>">
                                  <input type="hidden" name="winum" id="winum" value="<%=vehicleLoanMaster.getWiNum()%>">
                                  <input type="hidden" name="lockflg" id="lockflg" value="<%=request.getAttribute("lockflg")%>">

                                  <input type="hidden" id="currentTab" value="A-3"/>
								</span>
				</div>


			</div>
			<div class="container-fluid">

				<div class="navbar-collapse collapse order-2 order-lg-1">
								<span class="navbar-text d-none d-lg-inline-flex align-items-lg=center me-3">
									<i class="ph-house me-2"></i>
									Branch : <b><%=vehicleLoanMaster.getSolId()%>( <%=sol_desc%> <%=roname%>)</b>
									<input type="hidden" id="bpmerror" value="<%=bpmerr%>"/>
								</span>
				</div>

			</div>
			 <button id="parentToggle" type="button" class="btn btn-primary ">
        <i class="ph-file-doc"></i>DOCLIST
    </button>
		</div>


		<!-- Page content -->
		<div class="page-content pt-0">
			<!-- Main content -->
			<div class="kt content-wrapper">
				<!-- Content area -->
				<div class="content">
					<!-- Main charts -->
					<div class="d-flex flex-row min-vh-100">
						<div id="appList" class="post d-flex flex-column-fluid">
							<div class="content d-flex flex-column flex-column-fluid">
								<div class="toolbar d-flex flex-stack mb-2 mb-lg-5" id="kt_toolbar">
									<!--begin::Container-->
									<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack flex-wrap">
										<div class="page-title d-flex flex-column me-5 py-2">
											<h1 class="d-flex flex-column text-dark fw-bold fs-3 mb-0">Vehicle Loan Checker</h1>
											<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 pt-1">
												<li class="breadcrumb-item text-muted">
													<a href="dashboard" class="text-muted text-hover-primary">Home</a>
												</li>
												<li class="breadcrumb-item">
													<span class="bullet bg-gray-200 w-5px h-2px"></span>
												</li>
												<li class="breadcrumb-item text-muted">Branch Checker Queue</li>
												<!--end::Item-->
												<!--begin::Item-->
												<li class="breadcrumb-item">
													<span class="bullet bg-gray-200 w-5px h-2px"></span>
												</li>
												<!--end::Item-->
												<!--begin::Item-->
												<li class="breadcrumb-item text-muted"><%=vehicleLoanMaster.getWiNum()%>
												</li>
												<!--end::Item-->
												<!--begin::Item-->
												<li class="breadcrumb-item">
													<span class="bullet bg-gray-200 w-5px h-2px"></span>
												</li>
												<!--end::Item-->
												<!--begin::Item-->
												<li class="breadcrumb-item text-dark">Decision Panel</li>
												<!--end::Item-->
											</ul>
											<!--end::Breadcrumb-->
										</div>
										<!--end::Page title-->
										<!--begin::Actions-->

										<!--end::Actions-->
									</div>
									<!--end::Container-->
								</div>
								<div id="vehList" class="container-xxl">
									<form class="z-index-0" action="#">
										<div id="loancheckerbody">
											<div class="card card-flush shadow-sm">
												<div class="card-header">
													<h3 class="card-title">Checker Decision Panel</h3>
												</div>
												<div class="card-body py-5">
													<!-- User menu -->

													<!-- /user menu -->
													<!-- Navigation -->
													<div class="rounded border p-2 mb-3">
														<div class="accordion accordion-icon-collapse timeline" id="vl_checker_int" data-state="2">
															<%--                      <div class="sidebar-section1" id="bottomCard" data-state="2">--%>
															<%request.setAttribute("checker", "Y");%>
															<jsp:include page="checker/hunterdetails_bc.jsp"/>

															<jsp:include page="checker/blacklistdetails_bc.jsp"/>
															<jsp:include page="checker/racescoredetails_bc.jsp"/>
															<jsp:include page="checker/bredetails_bc.jsp"/>
														</div>
														<div class="btcard">

															<!--begin::Content-->
															<div id="kt_account_settings_deactivate" class="collapse show">
																	<!--begin::Card body-->
																	<div class="p-9">
																		<!--begin::Form input row-->
																		<div class="form-check form-check-solid fv-row">
																			<input name="deactivate" class="form-check-input" type="checkbox" value="" id="deactivate"/>
																			<label class="form-check-label fw-semibold ps-2 fs-6" for="deactivate">I hereby confirm that I have run
																				Dedup for all applicants/co-applicants/guarantors</label>
																		</div>
																		<div class="input-group mt-2 mt-2">
																			<a href="#" id="remarkHist" class="badge badge-light-danger fs-base">
																				History
																			</a>
																			<span class="input-group-text">Remarks</span>
																			<textarea class="form-control" aria-label="Checker Remarks" maxlength="4000" name="checker_remarks" id="checker_remarks"></textarea>
																		</div>
																		<!--end::Form input row-->
																	</div>
																	<!--end::Card body-->
																	<!--begin::Card footer-->
																	<div class=" d-flex justify-content-lg-center py-6 px-9">
																		<%if(bureauBlock.equals("N")){%>
																		<button id="checker_forward" type="submit" class="btn btn-success fw-semibold ms-4">Forward</button>
																		<%}%>
																		<button id="checker_sendback" type="submit" class="btn btn-warning fw-semibold ms-4">Sendback</button>
																		<button id="checker_reject" type="submit" class="btn btn-danger fw-semibold ms-4">Reject</button>
																	</div>
																	<!--end::Card footer-->
															</div>
															<!--end::Content-->
														</div>
													</div>
													<div class="sidebar-section" id="bottomCard" data-state="2">

													</div>
												</div>
											</div>

										</div>

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
			<button id="toggleList" type="button" class="btn btn-primary position-fixed top-50 end-0 translate-middle-y border-right-0">
				<i class="ph-file-doc"></i>
			</button>
		</div>

		<!--Custom Scripts-->
		<script src="assets/js/custom/WI/uploadwi.js"></script>
		<script src="assets/js/custom/checker/checkermaster.js"></script>
		<script src="assets/js/custom/checker/integrations.js"></script>
		<!--Custom Scripts-->

<los:modal/>
		<los:footer/>
	</body>
</
>

