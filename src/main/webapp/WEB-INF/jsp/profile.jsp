<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sib.ibanklosucl.dto.Reportee" %><%--
  Created by IntelliJ IDEA.
  User: SIBL11965
  Date: 09-05-2024
  Time: 15:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en" dir="ltr" class="custom-scrollbars">
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<meta name="_csrf" content="">
		<meta name="_csrf_header" content="">
		<title>SIB-LOS Dashboard</title>
		<!-- Global stylesheets -->
		<link href="assets/fonts/inter/inter.css" rel="stylesheet" type="text/css">
		<link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
		<link href="assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
		<link href="assets/css/CRMLead.css" rel="stylesheet" type="text/css">
		<!-- /global stylesheets -->
		<!-- Core JS files -->
		<script src="assets/demo/demo_configurator.js"></script>
		<script src="assets/js/bootstrap/bootstrap.bundle.min.js"></script>
		<!-- /core JS files -->
		<!-- Theme JS files -->
		<script src="assets/js/vendor/visualization/d3/d3.min.js"></script>
		<script src="assets/js/vendor/visualization/d3/d3_tooltip.js"></script>

		<script src="assets/js/app.js"></script>
		<script src="assets/demo/pages/dashboard.js"></script>
		<!-- /theme JS files -->
	</head>
	<%
		Employee employee = new Employee();
		List<Reportee> reporteeList = null;
        List<String> saleSols=null;
        List<String>clusterSols=null;
        List<String> userRoles =null;
		if (request.getSession().getAttribute("employeeData") != null) {
			employee = (Employee) session.getAttribute("employeeData");
		}
		if (request.getSession().getAttribute("reportees") != null) {
			reporteeList = (List<Reportee>) session.getAttribute("reportees");
		}
        if (request.getSession().getAttribute("saleSols") != null) {
			saleSols = (List<String>) session.getAttribute("saleSols");
		}
        if (request.getSession().getAttribute("reportees") != null) {
			clusterSols = (List<String>) session.getAttribute("clusterSols");
		}
         if (request.getSession().getAttribute("userRoles") != null) {
			userRoles = (List<String>) session.getAttribute("userRoles");
		}
	%>
	<body>
		<div class="page-content">

			<!-- Main sidebar -->

			<!-- /main sidebar -->


			<!-- Main content -->
			<div class="content-wrapper">

				<!-- Inner content -->
				<div class="content-inner">


					<!-- Content area -->
					<div class="content">

						<!-- Inner container -->
						<div class="d-lg-flex align-items-lg-start">

							<!-- Left sidebar component -->
							<div class="sidebar sidebar-component sidebar-expand-lg bg-transparent shadow-none me-lg-3">

								<!-- Sidebar content -->
								<div class="sidebar-content">

									<!-- Navigation -->
									<div class="card">
										<div class="sidebar-section-body text-center">
											<div class="card-img-actions d-inline-block mb-3">
												<img class="img-fluid rounded-circle" src="<%=employee.getImageUrl()%>" width="150" height="150" alt="">
												<div class="card-img-actions-overlay card-img rounded-circle">
													<a href="#" class="btn btn-outline-white btn-icon rounded-pill">
														<i class="ph-pencil"></i>
													</a>
												</div>
											</div>

											<h6 class="mb-0"><%=employee.getPpcName()%>
											</h6>
											<span class="text-muted"><%=employee.getRoleName()%></span>
										</div>

										<ul class="nav nav-sidebar" role="tablist">
											<li class="nav-item" role="presentation">
												<a href="#profile" class="nav-link active" data-bs-toggle="tab" aria-selected="true" role="tab">
													<i class="ph-user me-2"></i>
													My profile
												</a>
											</li>


											<li class="nav-item-divider"></li>
											<li class="nav-item" role="presentation">
												<a href="logout" class="nav-link"  aria-selected="false" tabindex="-1">
													<i class="ph-sign-out me-2"></i>
													Logout
												</a>
											</li>
										</ul>
									</div>
									<!-- /navigation -->


									<!-- Online users -->
									<div class="card">
										<div class="sidebar-section-header d-flex border-bottom">
											<span class="fw-semibold">Reportees</span>
											<div class="ms-auto">
												<span class="badge bg-success rounded-pill"><%=reporteeList!=null?reporteeList.size():"0"%></span>
											</div>
										</div>
										<%if(reporteeList!=null) {%>
										<div class="sidebar-section-body">
											<%
												for (Reportee reportee : reporteeList) {
											%>
											<div class="hstack gap-3 mb-3">
												<img src="<%=reportee.getImageUrl()%>" class="rounded-circle" width="40" height="40" alt="">

												<div class="flex-fill">
													<a href="#" class="fw-semibold"><%=reportee.getPpcName()%>
													</a>
													<div class="fs-sm text-muted"><%=reportee.getRoleName()%></div>
														<div class="fs-sm text-muted"><%=reportee.getBrName()%>- <%=reportee.getSolId()%>
													</div>
												</div>

												<div class="bg-success border-success rounded-pill p-1"></div>
											</div>
											<%}%>
										</div><%}%>
									</div>
									<!-- /online users -->
								</div>
								<!-- /sidebar content -->

							</div>
							<!-- /left sidebar component -->


							<!-- Right content -->
							<div class="tab-content flex-fill">
								<div class="tab-pane fade active show" id="profile" role="tabpanel">
									<!-- Profile info -->
									<div class="card">
										<div class="card-header">
											<h5 class="mb-0">Profile information</h5>
										</div>

										<div class="">
											<div class="employee-card">
												<div class="card-body">
													<div class="row">
														<div class="col-md-9">
															<div class="row">
																<div class="col-md-6">
																	<p class="card-text"><i class="fas fa-id-badge icon m-1"></i><strong>PPC No:</strong> <%=employee.getPpcno()%>
																	</p>
																	<p class="card-text"><i
																			class="fas fa-briefcase icon m-1"></i><strong>Designation:</strong> <%=employee.getDesigDesc()%>
																	</p>
																	<p class="card-text"><i class="fas fa-signal icon m-1"></i><strong>Scale:</strong> <%=employee.getScaleDesc()%>
																	</p>
																</div>
																<div class="col-md-6">
																	<p class="card-text"><i class="fas fa-building icon m-1"></i><strong>Branch:</strong> <%=employee.getBrName()%>
																		- <%=employee.getJoinedSol()%>
																	</p>
																	<p class="card-text"><i class="fas fa-globe icon m-1"></i><strong>Region:</strong> <%=employee.getRegName()%>
																	</p>
																	<p class="card-text"><i class="fas fa-house-user icon m-1"></i><strong>Office
																		Type:</strong> <%=employee.getOffType()%>
																	</p>
																	<p class="card-text"><i class="fas fa-house-user icon m-1"></i><strong>Business Unit:</strong> <%=employee.getBusunitName()%>
																	</p>
																</div>
															</div>
															<div class="row">
																<div class="col-md-6">
																	<p class="card-text"><i class="fas fa-check-circle icon m-1"></i><strong>Status:</strong> <span
																			class="badge bg-success"><%=employee.getEmployeeStatusDesc()%></span></p>
																	<p class="card-text"><i class="fas fa-envelope icon m-1"></i><strong>Email:</strong> <%=employee.getEmailid()%>
																	</p>
																</div>
																<div class="col-md-6">
																	<p class="card-text"><i class="fas fa-phone-alt icon m-1"></i><strong>Mobile
																		No:</strong> <%=employee.getMobno()%>
																	</p>
																	<p class="card-text"><i class="fas fa-phone icon m-1"></i><strong>IP Phone:</strong> <%=employee.getIpphone()%>
																	</p>
																</div>
															</div>
															<div class="row">
																<div class="col-md-6">
																	<p class="card-text"><i class="fas fa-user-tag icon m-1"></i><strong>Job Role:</strong> <%=employee.getRoleName()%>
																	</p>
																</div>
															</div>
															<div class="row">
              <div class="col-md-12">
                <h6 class="info-title"><i class="fas fa-user icon m-1"></i> Roles - <%=userRoles%></h6>
              </div>
            </div>
															<div class="row">
              <div class="col-md-12">
                <h6 class="info-title"><i class="fas fa-bank icon m-1"></i> RSM Branches - <%=saleSols%></h6>
              </div>
            </div>

														</div>
													</div>
												</div>
											</div>
										</div>

									</div>
									<!-- /profile info -->


									<!-- Account settings -->
<%--									<div class="card">--%>
<%--										<div class="card-header">--%>
<%--											<h5 class="mb-0">Account settings</h5>--%>
<%--										</div>--%>

<%--										<div class="card-body">--%>
<%--											<form action="#">--%>
<%--												<div class="row">--%>
<%--													<div class="col-lg-6">--%>
<%--														<div class="mb-3">--%>
<%--															<label class="form-label">Username</label>--%>
<%--															<input type="text" value="Vicky" readonly="" class="form-control">--%>
<%--														</div>--%>
<%--													</div>--%>

<%--													<div class="col-lg-6">--%>
<%--														<div class="mb-3">--%>
<%--															<label class="form-label">Current password</label>--%>
<%--															<input type="password" value="password" readonly="" class="form-control">--%>
<%--														</div>--%>
<%--													</div>--%>
<%--												</div>--%>

<%--												<div class="row">--%>
<%--													<div class="col-lg-6">--%>
<%--														<div class="mb-3">--%>
<%--															<label class="form-label">New password</label>--%>
<%--															<input type="password" placeholder="Enter new password" class="form-control">--%>
<%--														</div>--%>
<%--													</div>--%>

<%--													<div class="col-lg-6">--%>
<%--														<div class="mb-3">--%>
<%--															<label class="form-label">Repeat password</label>--%>
<%--															<input type="password" placeholder="Repeat new password" class="form-control">--%>
<%--														</div>--%>
<%--													</div>--%>
<%--												</div>--%>

<%--												<div class="row">--%>
<%--													<div class="col-lg-6">--%>
<%--														<div class="mb-3">--%>
<%--															<label class="form-label">Profile visibility</label>--%>

<%--															<label class="form-check mb-2">--%>
<%--																<input type="radio" name="visibility" class="form-check-input" checked="">--%>
<%--																<span class="form-check-label">Visible to everyone</span>--%>
<%--															</label>--%>

<%--															<label class="form-check mb-2">--%>
<%--																<input type="radio" name="visibility" class="form-check-input">--%>
<%--																<span class="form-check-label">Visible to friends only</span>--%>
<%--															</label>--%>

<%--															<label class="form-check mb-2">--%>
<%--																<input type="radio" name="visibility" class="form-check-input">--%>
<%--																<span class="form-check-label">Visible to my connections only</span>--%>
<%--															</label>--%>

<%--															<label class="form-check">--%>
<%--																<input type="radio" name="visibility" class="form-check-input">--%>
<%--																<span class="form-check-label">Visible to my colleagues only</span>--%>
<%--															</label>--%>
<%--														</div>--%>
<%--													</div>--%>

<%--													<div class="col-lg-6">--%>
<%--														<div class="mb-3">--%>
<%--															<label class="form-label">Notifications</label>--%>

<%--															<label class="form-check mb-2">--%>
<%--																<input type="checkbox" class="form-check-input" checked="">--%>
<%--																<span class="form-check-label">Password expiration notification</span>--%>
<%--															</label>--%>

<%--															<label class="form-check mb-2">--%>
<%--																<input type="checkbox" class="form-check-input" checked="">--%>
<%--																<span class="form-check-label">New message notification</span>--%>
<%--															</label>--%>

<%--															<label class="form-check mb-2">--%>
<%--																<input type="checkbox" class="form-check-input" checked="">--%>
<%--																<span class="form-check-label">New task notification</span>--%>
<%--															</label>--%>

<%--															<label class="form-check">--%>
<%--																<input type="checkbox" class="form-check-input">--%>
<%--																<span class="form-check-label">New contact request notification</span>--%>
<%--															</label>--%>
<%--														</div>--%>
<%--													</div>--%>
<%--												</div>--%>
<%--											</form>--%>
<%--										</div>--%>
<%--									</div>--%>
									<!-- /account settings -->

								</div>

								<div class="tab-pane fade" id="schedule" role="tabpanel">

									<!-- Available hours -->
									<div class="card">
										<div class="card-header">
											<h5 class="mb-0">Available hours</h5>
										</div>

										<div class="card-body">
											<div class="chart-container">
											</div>
										</div>
									</div>
									<!-- /available hours -->


									<!-- Schedule -->
									<div class="card">
										<div class="card-header">
											<h5 class="mb-0">My schedule</h5>
										</div>

										<div class="card-body">
										</div>
									</div>
									<!-- /schedule -->

								</div>


							</div>
							<!-- /right content -->

						</div>
						<!-- /inner container -->

					</div>
					<!-- /content area -->


					<!-- Footer -->
					<div class="navbar navbar-sm navbar-footer border-top">
						<div class="container-fluid">
							<span>© 2022 <a href="#">Infobank</a></span>
						</div>
					</div>
					<!-- /footer -->

				</div>
				<!-- /inner content -->

<%--				<div class="btn-to-top btn-to-top-visible">--%>
<%--					<button class="btn btn-secondary btn-icon rounded-pill" type="button"><i class="ph-arrow-up"></i></button>--%>
<%--				</div>--%>
			</div>
			<!-- /main content -->


		</div>
	</body>
</html>
