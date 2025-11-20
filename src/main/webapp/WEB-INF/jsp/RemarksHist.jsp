<%--
  Created by IntelliJ IDEA.
  User: SIBL11965
  Date: 11-06-2024
  Time: 13:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.*" %>
<!DOCTYPE html>
<html lang="en" data-bs-theme="light">
	<head>
		<meta charset="UTF-8">
		<title>Entry Queue</title>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<meta name="_csrf" content="">
		<meta name="_csrf_header" content="">
		<!-- Global stylesheets -->
<%--		<link href="assets/fonts/inter/inter.css" rel="stylesheet" type="text/css">--%>
<%--		--%>
		<link href="assets/icons/phosphor/styles.min.css" rel="stylesheet" type="text/css">
		<link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
		<link href="assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
<%--		<link href="assets/css/custom/dashboard.css" rel="stylesheet" type="text/css">--%>
		<!-- /global stylesheets -->
		<!-- Core JS files -->
		<link href="assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css"/>
		<link href="assets/css/style.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
		<link href="assets/plugins/global/plugins.bundle.prefixed.css" rel="stylesheet" type="text/css"/>

		<script src="assets/js/vendor/notifications/sweet_alert.min.js"></script>
		<script src="assets/js/custom/allotment/allotmentList.js"></script>
		<style>

			.asidecheck {
				position: fixed;
				top: 105px;
				bottom: 30px;
				left: 30px;
				z-index: 100;
				overflow: hidden;
				width: 300px;
				border-radius: 1.75rem;
			}

			.table-shadow
			{
				box-shadow: rgba(0, 0, 0, 0.05) 0px 6px 24px 0px, rgba(17, 17, 17, 0.05) 0px 0px 0px 1px !important;
			}
			.bodycolor
			{
				background-color: white;
			/*	background-image : url("assets/images/page-bg.jpg") !important;*/
			}

			.kt .timeline .timeline-line {
				border-left-color: #B2B6C8 !important;
			}

		</style>
	</head>
	<body class="bodycolor" >
<%--	<jsp:include page="header.jsp"/>--%>

		<div class="page-content">
			<div class="content-wrapper">
				<div class="content-inner">
					<div class="content">
						<div class="kt">
							<div class="card">
								<!--begin::Card body-->
								<div class="card-body">
									<!--begin::Tab Content-->
									<div class="tab-content">
										<!--begin::Tab panel-->
										<div id="kt_activity_today" class="card-body p-0 tab-pane fade show active" role="tabpanel" aria-labelledby="kt_activity_today_tab">
											<!--begin::Timeline-->
											<%
												List<RemarksHistDTO> remarksList = (List<RemarksHistDTO>) request.getAttribute("remarksList");
												if(!remarksList.isEmpty())
												{
											%>
											<div class="timeline">
												<!--begin::Timeline item-->

												<%

													//out.println(remarksList);
													int count = 0;
													String queue="",reg_or_br="",user_class="",remarks_class="",latest="";
													for (RemarksHistDTO remarks : remarksList)
													{
														count++;
														queue="";
														reg_or_br="";
														if(count==1)
														{
															user_class=" border-success border-success-300 ";
															remarks_class=" border-success border-success-300 bg-light-success ";
															latest="<span class='badge badge-light-success ms-2'>Latest Queue</span>";
														}
														else
														{
															user_class=" border-gray border-gray-500 ";
															remarks_class="border-gray border-gray-500";
															latest="";
														}


												%>


												<div class="timeline-item">
													<!--begin::Timeline line-->
													<div class="timeline-line w-40px"></div>
													<!--end::Timeline line-->
													<!--begin::Timeline icon-->
													<div class="timeline-icon symbol symbol-circle symbol-40px me-4">
														<div class="symbol-label bg-light">
															<i class="ki-duotone ki-disconnect fs-2 text-gray-500">
																<span class="path1"></span>
																<span class="path2"></span>
																<span class="path3"></span>
																<span class="path4"></span>
																<span class="path5"></span>
															</i>
														</div>
													</div>
													<!--end::Timeline icon-->
													<!--begin::Timeline content-->
													<div class="timeline-content mb-10 mt-n1">
														<div class="pe-3 mb-5">
															<%
																if(remarks.getFromQueue().startsWith("BC"))queue="Branch Checker";
																if(remarks.getFromQueue().startsWith("BD"))queue="Branch Documentation";
																if(remarks.getFromQueue().startsWith("BG"))queue="BOG Queue";
																if(remarks.getFromQueue().startsWith("BM"))queue="Branch Maker";
																if(remarks.getFromQueue().startsWith("BS"))queue="Send Back";
																if(remarks.getFromQueue().startsWith("CA"))queue="CRT Amber queue";
																if(remarks.getFromQueue().startsWith("CS"))queue="CRT Queue";
																if(remarks.getFromQueue().startsWith("DP"))queue="Deviation Pending";
																if(remarks.getFromQueue().startsWith("RA"))queue="RBCPC Allotment";
																if(remarks.getFromQueue().startsWith("RC"))queue="RBCPC Checker";
																if(remarks.getFromQueue().startsWith("RM"))queue="RBCPC Maker";
															%>
															<div class="fs-5 fw-semibold mb-2"><%=queue%><%=latest%></div>
															<div class="d-flex align-items-center mt-1 fs-6">
																<div class="text-muted me-2 fs-7">Added at <%=remarks.getCmdate()%></div>
															</div>
														</div>
														<div class="overflow-auto pb-5">
															<div class="d-flex align-items-center border border-dashed <%=user_class%>  rounded min-w-750px px-7 py-0 mb-5">
																<div data-bs-toggle="tooltip" aria-label="Brian Cox" data-bs-original-title="Brian Cox" data-kt-initialized="1" class="symbol symbol-45px symbol-circle">
																	<img alt="Pic" src="https://infobank.sib.co.in:8443/UPLOAD_FILES/IMAGE/<%=remarks.getCmuser()%>.xjpg">
																</div>
																<div class="d-flex flex-column ms-3 mt-4">
																	<div class="d-flex align-items-center mb-2">
																		<a href="#" class="text-gray-900 text-hover-primary fs-7 fw-bold me-1"><%=remarks.getPpcName()%></a>

																	</div>
																	<div class="d-flex flex-wrap fw-semibold fs-7 mb-4 pe-2">
																		<a href="#" class="d-flex align-items-center text-gray-400 text-hover-primary me-5 mb-2">
																			<i class="ki-duotone ki-profile-circle fs-4 me-1">
																				<span class="path1"></span>
																				<span class="path2"></span>
																				<span class="path3"></span>
																			</i>SIBL<%=remarks.getCmuser()%></a>
																		<a href="#" class="d-flex align-items-center text-gray-400 text-hover-primary me-5 mb-2">
																			<i class="ki-duotone ki-geolocation fs-4 me-1">
																				<span class="path1"></span>
																				<span class="path2"></span>
																				<%
																				if(remarks.getBrName() !=null )
																				{
																					reg_or_br=remarks.getBrName();
																				}
																				else if(remarks.getRegName() !=null)
																				{
																					reg_or_br=remarks.getRegName();
																				}
																				%>
																			</i><%=reg_or_br%></a>
																		<a href="#" class="d-flex align-items-center text-gray-400 text-hover-primary mb-2">
																			<i class="ki-duotone ki-sms fs-4 me-1">
																				<span class="path1"></span>
																				<span class="path2"></span>
																			</i><%=remarks.getSolId()%></a>
																		<%-- Add assigned user information if present for RBCPC queues --%>
    <% if(remarks.getAssignUser() != null &&
          (remarks.getFromQueue().startsWith("RA") ||
           remarks.getFromQueue().startsWith("RM") ||
           remarks.getFromQueue().startsWith("RC"))) { %>
        <a href="#" class="d-flex align-items-center text-gray-400 text-hover-primary mb-2 ms-5">
            <i class="ki-duotone ki-user-edit fs-4 me-1">
                <span class="path1"></span>
                <span class="path2"></span>
            </i>
            <div class="d-flex align-items-center">
                <%
                String assignedLabel = "";
                String badgeClass = "";
                if(remarks.getFromQueue().startsWith("RA")) {
                    assignedLabel = "Dealing Officer";
                    badgeClass = "badge-light-success";
                } else if(remarks.getFromQueue().startsWith("RM")) {
                    assignedLabel = "Checker User";
                    badgeClass = "badge-light-info";
                 } else if(remarks.getFromQueue().startsWith("RC")) {
                    assignedLabel = "Checker User Forwarded To";
                    badgeClass = "badge-light-info";

                }
                %>
                <span class="badge <%=badgeClass%> me-2"><%=assignedLabel%></span>
                <span class="fw-semibold text-gray-600">SIBL<%=remarks.getAssignUser()%></span>
            </div>
        </a>
    <% } %>


																	</div>

																</div>

																<div class="min-w-125px py-3 px-4 me-6 mt-2 ms-3">
																	<!--begin::Number-->
																	<div class="d-flex align-items-center">
																		<i class="ki-duotone ki-arrow-up fs-3 text-success me-2">
																			<span class="path1"></span>
																			<span class="path2"></span>
																		</i>
																		<%
																			if(remarks.getToQueue().equals("BC"))queue="Branch Checker";
																			if(remarks.getToQueue().equals("BD"))queue="Branch Documentation";
																			if(remarks.getToQueue().equals("BG"))queue="BOG Queue";
																			if(remarks.getToQueue().equals("BM"))queue="Branch Maker";
																			if(remarks.getToQueue().equals("BS"))queue="Send Back";
																			if(remarks.getToQueue().equals("CA"))queue="CRT Amber queue";
																			if(remarks.getToQueue().equals("CS"))queue="CRT Queue";
																			if(remarks.getToQueue().equals("DP"))queue="Deviation Pending";
																			if(remarks.getToQueue().equals("RA"))queue="RBCPC Allotment";
																			if(remarks.getToQueue().startsWith("RC"))queue="RBCPC Checker";
																			if(remarks.getToQueue().equals("RM"))queue="RBCPC Maker";
																		%>
																		<div data-kt-countup="true" data-kt-countup-value="4500" data-kt-countup-prefix="$" data-kt-initialized="1" class="fs-7 fw-bold counted"><%=queue%></div>
																	</div>
																	<!--end::Number-->
																	<!--begin::Label-->
																	<div class="fw-semibold fs-7 text-gray-400">Next Queue</div>
																	<!--end::Label-->
																</div>
															</div>
															<!--end::Record-->
															<!--begin::Record-->
															<%
																if(count==1)
																{
															%>
																<div class="d-flex align-items-center rounded py-2 px-5 bg-light-success"><i class="ki-duotone ki-notepad-bookmark fs-2x text-success me-5"><span class="path1"></span><span class="path2"></span><span class="path3"></span>
																	<span class="path4"></span>
																	<span class="path5"></span>
																	<span class="path6"></span></i>    <!--begin::Description-->
																	<div class="text-gray-700 fw-bold fs-7" style="white-space: pre-line;margin-top: -25px;">
																		<%=remarks.getRemarks()%>
																	</div>    <!--end::Description-->
																</div>
															<%
																}
																else {
															%>


																		<div class="d-flex align-items-center rounded py-2 px-5 bg-light-gray border border-dashed border-gray border-gray-500"><i class="ki-duotone ki-notepad-bookmark fs-2x text-primary me-5"><span class="path1"></span><span class="path2"></span><span class="path3"></span>
																			<span class="path4"></span>
																			<span class="path5"></span>
																			<span class="path6"></span></i>    <!--begin::Description-->
																			<div class="text-gray-700 fw-bold fs-7" style="white-space: pre-line;margin-top: -25px;">
																				<%=remarks.getRemarks()%>
																			</div>
																		</div>

															<%
																}
															%>





														</div>
														<!--end::Timeline details-->
													</div>
													<!--end::Timeline content-->
												</div>

												<%
													}
												%>
												<!--end::Timeline item-->
												<!--begin::Timeline item-->




											</div>

											<%
												}
												else
												{
													%>
											<div class="card card-docs flex-row-fluid mb-2" id="kt_docs_content_card">
												<!--begin::Card Body-->
												<div class="card-body fs-6 py-15 px-10 py-lg-15 px-lg-15 text-gray-700">


													<div class="row g-0" style="justify-content: center;display: flex;" align="center">
														<div class="col-md-12 mb-10">
															<div class="bg-light bg-opacity-50 rounded-3 p-1 mx-md-5 h-md-0">

																<div class="d-flex flex-center w-60px h-60px rounded-3 bg-light-success bg-opacity-90 mb-1">
																	<i class="ki-duotone ki-design text-success fs-3x"><span class="path1"></span><span class="path2"></span></i>                </div>


															</div>
														</div>
													</div>


													<!--begin::Section-->
													<div class="px-md-11 pt-md-1 pb-md-1">
														<!--begin::Block-->
														<div class="text-center mb-20">
															<h1 class="fs-2tx fw-bold mb-8">

															<span class="d-inline-block position-relative ms-2">
																<span class="d-inline-block mb-2">Oops            </span>
																<span class="d-inline-block position-absolute h-8px bottom-0 end-0 start-0 bg-success translate rounded"></span>
															</span>
																, No Remarks have been found
															</h1>


														</div>
														<!--end::Block-->

														<!--begin::Row-->

														<!--end::Row-->
													</div>
												</div>
												<!--end::Card Body-->
											</div>


													<%
												}
											%>
											<!--end::Timeline-->
										</div>
									</div>
									<!--end::Tab Content-->
								</div>
								<!--end::Card body-->
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>


		<script src="assets/demo/demo_configurator.js"></script>
		<script src="assets/js/bootstrap/bootstrap.bundle.min.js"></script>
		<!-- /core JS files -->
		<!-- Theme JS files -->
		<script src="assets/js/vendor/visualization/d3/d3.min.js"></script>
		<script src="assets/js/vendor/visualization/d3/d3_tooltip.js"></script>
		<script src="assets/js/jquery/jquery.min.js"></script>

		<script src="assets/js/scripts.bundle.js"></script>
		<script src="assets/plugins/global/plugins.bundle.js"></script>
		<script src="assets/plugins/custom/datatables/datatables.bundle.js"></script>
		<!--end::Vendors Javascript-->
		<!--begin::Custom Javascript(used for this page only)-->
		<script src="assets/js/custom/dashboard.js"></script>
		<script src="assets/js/vendor/forms/selects/select2.min.js"></script>
		<script src="assets/demo/pages/form_select2.js"></script>

		<script src="assets/js/app.js"></script>
		<script>
            $(document).ready(function () {
                $('.progress-info').hover(function () {
                    $(this).find('.applicant-progress').toggle();
                });
                // $('.modify-btn').click(function () {
                //     var slno = $(this).data('slno');
                //     $('#slnoInput').val(slno);
                //     $('#wibmlist').submit();
                // });
			$('#branch-maker-queue-details-table').on('click','.modify-btn',function (e) {
				e.preventDefault();
				e.stopPropagation();
                    var slno = $(this).data('slno');
                    $('#slnoInput').val(slno);
                    $('#wibmlist').submit();
                });

				$('.select2-initialisation-allot').select2({
					templateResult: formatState,
					templateSelection: formatState

				});
            });

			function formatState(state)
			{
				if(!state.id)
				{
					return state.text;
				}
				var $state=$('<span>'+state.text+'</span>');
				return $state;
			}
		</script>
	</body>

</html>