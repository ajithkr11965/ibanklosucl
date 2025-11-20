<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.sib.ibanklosucl.model.*" %>
<%@ page import="java.util.Optional" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="assets/plugins/custom/formrepeater/formrepeater.bundle.js"></script>
<script>
    $(document).ready(function () {



		$('.approveCode').on('change',function (e){
			var selected=$(this).val();
			var approvedcmt=$(this).closest('.approved-parent').next('.approver-only');
			if(selected==="APPROVE"){
				approvedcmt.find('.approveComments').val('').attr('readonly',false);
			}
			else{
				approvedcmt.find('.approveComments').val('').attr('readonly',true);
			}
		})

        $('#decisionApprove').click(function (e) {
            console.log("saving the devations");
            e.preventDefault();
			if($("#dynamicForm").valid()) {
				submitAmberDeviations();
			}
            console.log("saving the devations completed");
        });

        function submitAmberDeviations() {

            var winum = $('#winum').val();
            var slno = $('#slno').val();
            var formData = {
                wiNum: winum,
                slno: slno,
                reqIpAddr: "", // Assuming you want to send the client's IP address
                amberData: []
            };

            // Collect data from preloaded rows
            $('#preloadedData .form-group.row').each(function () {
                var row = $(this);
                var amberData = {
                    id: row.data('amber-id'),
                    amberDesc: row.find('.devdesc').text().trim(),
					approveCode: row.find('select[name="approveCode"]').val(),
					approveComments: row.find('input[name="approveComments"]').val().trim(),
                };
                console.log("before amberdata=====" + JSON.stringify(formData));
                formData.amberData.push(amberData);
            });
            // Send AJAX request
			var isValid=true;
			formData.amberData.forEach(function(item) {
				if (item.approveCode === "APPROVE" && (!item.approveComments || item.approveComments.trim() === "")) {
					isValid=false;
				}
			});
		if(isValid) {
			$.ajax({
				url: 'apicpc/update-checker-deviations',
				type: 'POST',
				contentType: 'application/json',
				data: JSON.stringify(formData),
				success: function (response) {
					notyalt('Deviations updated successfully');
					$('#decisionDetailslink').trigger('click');
					// You can add more user feedback here, such as updating the UI or refreshing the data
				},
				error: function (xhr, status, error) {
					alert('Error updating deviations: ' + error);
					// You can add more error handling here, such as displaying specific error messages
				}
			});
		}
		else{
			alertmsg("Approver comments is mandatory when Action is Approve.");
		}
        }
    });
</script>
<%
	Employee userdt= (Employee) request.getAttribute("userdata");
	VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	List<VehicleLoanApplicant> vehicleLoanApplicants = new ArrayList<>();
	List<VehicleLoanAmber> vehicleLoanAmberList = new ArrayList<>();
	VehicleLoanBasic vehicleLoanBasic = null;
	String applicantId = "", wiNum = "", slno = "", applicantype = "", showFlg = "", accordion_style = "btn-active-light-primary";
	Boolean checker = request.getAttribute("checker") != null ? request.getAttribute("checker").toString().equals("Y") : false;
	if (vehicleLoanMaster != null) {
		vehicleLoanApplicants = vehicleLoanMaster.getApplicants();
		vehicleLoanAmberList = (List<VehicleLoanAmber>) request.getAttribute("vehicleLoanAmberList");
		//vehicleLoanAmberList=vehicleLoanAmberList.stream().filter(t->!t.getApprovingAuth().equalsIgnoreCase("NA")).collect(Collectors.toList());
		wiNum = vehicleLoanMaster.getWiNum();
		slno = vehicleLoanMaster.getSlno().toString();
	} else {
	}

	String userRole=(String) request.getAttribute("userRole");
	//System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzuserRole:"+userRole);
	int userLevel= 0;
	if(userRole.startsWith("RCL")){
		userLevel= Integer.parseInt(userRole.substring(userRole.length()-1));
	}else{
		userLevel = 99;
	}
	int allcount=0,nacount=0;
%>
<div class="flex-stack border rounded px-7 py-3 mb-2">
	<div class="w-100">
		<div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="deviationDetailslink" data-bs-target="#deviationDetailsContent">
			<label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
			       for="deviationDetailsContent">
				<i class="ki-duotone ki-element-8 fs-3x me-4">
					<span class="path1"></span>
					<span class="path2"></span>
				</i>
				<span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-4">Deviation Details</span>
                    <span class="text-muted fw-semibold fs-7">
                     Enter Deviation details.
                    </span>
                </span>
			</label>
		</div>
		<div id="deviationDetailsContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">

			<div class="row">
				<div class="col-sm-12">
					<!--begin::Repeater-->
					<div id="kt_docs_repeater_basic">
						<form id="dynamicForm" class="form">
							<!-- Labels row -->
							<div class="form-group row">
								<div class="col-sm-2">
									<label class="col-form-label">Deviation</label>
								</div>
								<div class="col-sm-1">
									<label class="col-form-label"> Range</label>
								</div>
								<div class="col-sm-1">
									<label class="col-form-label">Allotted</label>
								</div>
								<div class="col-sm-2">
									<label class="col-form-label"> Value</label>
								</div>
								<div class="col-sm-2">
									<label class="col-form-label">DO comments</label>
								</div>
								<div class="col-sm-2">
									<label class="col-form-label">Action</label>
								</div>
								<div class="col-sm-2 approver-only">
									<label class="col-form-label">Approver comments</label>
								</div>
							</div>
							<div class="separator separator-dashed border-gray-500 my-1"></div>

							<!-- Preloaded data container -->
							<div id="preloadedData">
								<%

									for (VehicleLoanAmber vehicleLoanAmber : vehicleLoanAmberList) {
                                    int currentLevel=0;
										allcount++;
                                    if(vehicleLoanAmber.getApprovingAuth()!=null) {
										if(!vehicleLoanAmber.getApprovingAuth().equalsIgnoreCase("NA"))
                                        currentLevel= Integer.parseInt(vehicleLoanAmber.getApprovingAuth().substring(vehicleLoanAmber.getApprovingAuth().length()-1));
										else
											nacount++;
                                    }

								%>
								<div class="form-group row mb-1" data-amber-id="<%=vehicleLoanAmber.getId()%>">
									<div class="col-sm-2">
										<span class="devdesc"><%= vehicleLoanAmber.getAmberDesc() %></span>
									</div>
									<div class="col-sm-1">
										<%= vehicleLoanAmber.getAmberSubList().isEmpty() ? "" : vehicleLoanAmber.getAmberSubList().get(0).getMasterValue() != null ? vehicleLoanAmber.getAmberSubList().get(0).getMasterValue() : "-" %>
									</div>
									<div class="col-sm-1">
											<%=vehicleLoanAmber.getApprovingAuth().replaceAll("RC","")%>
									</div>
									<div class="col-sm-2">
										<% for (VehicleLoanAmberSub amberSub : vehicleLoanAmber.getAmberSubList()) { %>
										<div class="parameter-value-display display-field devdesc">
											<%= amberSub.getApplicantName() != null ? amberSub.getApplicantName() : "" %>(
											<%= amberSub.getApplicantType() != null ? amberSub.getApplicantType() : "" %>)
											-:
											<%= amberSub.getCurrentValue() != null ? amberSub.getCurrentValue() : "" %>
										</div>
										<% } %>
									</div>
									<div class="col-sm-2">
										<%= vehicleLoanAmber.getDoRemarks() != null ? vehicleLoanAmber.getDoRemarks() : "" %>
									</div>
									<div class="col-sm-2 approved-parent">

										<%
											String appr=vehicleLoanAmber.getApprAuthAction()==null?"":vehicleLoanAmber.getApprAuthAction();
											String appruser=vehicleLoanAmber.getApprAuthUser()==null?"":vehicleLoanAmber.getApprAuthUser();
											if("APPROVE".equalsIgnoreCase(appr) && currentLevel!=userLevel){// && !appruser.equalsIgnoreCase(userdt.getPpcno())
											out.print("Approved");
											%>
										<input type="hidden" name="approveCode"  value=""/>
										<%
											}
											else if(vehicleLoanAmber.getApprovingAuth().equalsIgnoreCase("NA")){
													%>
													<input type="hidden" name="approveCode" value=""/>
													<%
											out.print("-");
											}
											else if(currentLevel>userLevel){
												%>
												<input type="hidden" name="approveCode" value=""/>
												<%
											out.print("-");
											}
											else{
										%>
										<select name="approveCode" class="form-select form-select-solid approveCode" >
											<option value="" selected>select</option>
											<option value="APPROVE" <%=appr.equals("APPROVE")?"selected":""%>   >Approve</option>
										</select>
										<%}%>
									</div>

									<div class="col-sm-2 approver-only">
										<%		if("APPROVE".equalsIgnoreCase(appr) && currentLevel!=userLevel){// && !appruser.equalsIgnoreCase(userdt.getPpcno())
											out.print(vehicleLoanAmber.getApprAuthRemarks());
										%>
										<input type="hidden" name="approveComments"  class="form-control approveComments" value="<%=vehicleLoanAmber.getApprAuthRemarks()%>"/>
										<%
										}
										else if(vehicleLoanAmber.getApprovingAuth().equalsIgnoreCase("NA")){
										%>
										<input type="hidden" name="approveComments"   class="form-control approveComments"  value=""/>
										<%
											out.print("-");
										}
										else if(currentLevel>userLevel){
										%>
										<input type="hidden" name="approveComments"   class="form-control approveComments"  value=""/>
										<%
											out.print("-");
										}
										else{
										%>
										<input type="text" name="approveComments"  class="form-control approveComments" readonly="<%=  (vehicleLoanAmber.getApprAuthAction()!=null && "APPROVE".equalsIgnoreCase(vehicleLoanAmber.getApprAuthAction())) ?"false":"true"%>"
											   value="<%= vehicleLoanAmber.getApprAuthRemarks() != null ? vehicleLoanAmber.getApprAuthRemarks() : "" %>">
										<%}%>

									</div>
								</div>
								<div class="separator separator-dashed border-gray-400 my-1"></div>
								<% } %>
							</div>

							<%
							if(userLevel!=99){

								if(allcount!=nacount){
							%>
							<div class="text-end pt-5">
								<button type="button" id="decisionApprove" class="btn btn-lg btn-primary" data-kt-stepper-action="next">Save
									<i class="ki-duotone ki-double-right ">
										<i class="path1"></i>
										<i class="path2"></i>
									</i></button>
							</div>

							<%
								}
							}
							%>

						</form>
					</div>
					<!--end::Repeater-->
				</div>
			</div>
		</div>
	</div>
</div>
