<%@ page import="com.sib.ibanklosucl.dto.mssf.MssfCustomerDetailsDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ taglib prefix="los" uri="http://www.siblos.com/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
	<head>
		<title>MSSF Processing</title>
		<meta name="_csrf" content="">
		<meta name="_csrf_header" content="">

		<link href="assets/css/sweetalert2.min.css" rel="stylesheet">

		<link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
		<link href="assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
		<link href="assets/css/custom.css" rel="stylesheet" type="text/css"/>
		<link href="assets/css/custom/wirmmodify.css" rel="stylesheet" type="text/css"/>
		<link href="assets/css/style.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
		<link href="assets/plugins/global/plugins.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
		<script src="assets/js/bootstrap/bootstrap.bundle.min.js"></script>
		<script src="assets/js/jquery/jquery.min.js"></script>
		<script src="assets/js/vendor/forms/validation/validate.min.js"></script>
		<script src="assets/js/vendor/split.min.js"></script>
		<!-- /core JS files -->
		<script src="assets/demo/pages/components_tooltips.js"></script>
		<script src="assets/js/bootstrap/bootstrap.bundle.min.js"></script>
		<script src="assets/js/vendor/forms/selects/select2.min.js"></script>
		<script src="assets/demo/pages/form_select2.js"></script>
		<!-- Theme JS files -->
		<script src="assets/js/app.js"></script>
		<script src="assets/js/vendor/uploaders/fileinput/fileinput.min.js"></script>
		<script src="assets/js/vendor/uploaders/fileinput/plugins/sortable.min.js"></script>
		<script src="assets/js/vendor/notifications/sweet_alert.min.js"></script>
		<script src="assets/js/vendor/notifications/noty.min.js"></script>
		<style>
            .required::after {
                content: " *";
                color: red;
            }

            .readonly-section {
                background-color: #f8f9fa;
                padding: 10px;
                border-radius: 4px;
                border: 1px solid #dee2e6;
            }

            .non-editable {
                background-color: #f8f9fa !important;
                cursor: not-allowed;
            }

            .info-banner {
                background-color: #e7f3fe;
                border-left: 4px solid #1a73e8;
                padding: 12px;
                margin-bottom: 20px;
            }

            .sticky-footer {
                position: fixed;
                bottom: 0;
                width: 100%;
                background: white;
                border-top: 1px solid #dee2e6;
                padding: 15px;
                box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
                z-index: 1000;
            }

            .content-wrapper {
                margin-bottom: 100px;
            }

            .form-label {
                font-weight: 500;
                margin-bottom: 0.3rem;
            }

            .card {
                box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
            }

            .card-header {
                background-color: white;
                border-bottom: 1px solid rgba(0, 0, 0, 0.1);
            }

            .is-invalid {
                border-color: #dc3545;
                padding-right: calc(1.5em + 0.75rem);
                background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 12 12' width='12' height='12' fill='none' stroke='%23dc3545'%3e%3ccircle cx='6' cy='6' r='4.5'/%3e%3cpath stroke-linejoin='round' d='M5.8 3.6h.4L6 6.5z'/%3e%3ccircle cx='6' cy='8.2' r='.6' fill='%23dc3545' stroke='none'/%3e%3c/svg%3e");
                background-repeat: no-repeat;
                background-position: right calc(0.375em + 0.1875rem) center;
                background-size: calc(0.75em + 0.375rem) calc(0.75em + 0.375rem);
            }

            .spinner-overlay {
                backdrop-filter: blur(3px);
            }

            .alert {
                border-radius: 4px;
                margin-bottom: 1rem;
            }
		</style>
	</head>
	<%
		MssfCustomerDetailsDTO mssfData = (MssfCustomerDetailsDTO) request.getAttribute("mssfDetailData");
		Employee employee = new Employee();
		out.print("mssf dob " + mssfData.getPdDob());
		if (request.getAttribute("employee") != null) {
			employee = (Employee) request.getAttribute("employee");
		}
		out.print(employee.getPpcAvailSol());
	%>
	<body class="bg-light" id="loanbody">
		<form id="backToQueueForm" action="mssfqueue" method="GET">
		</form>

		<los:pageheader/>
		<div class="content-wrapper">
			<div class="container-fluid p-3">
				<!-- Header -->
				<div class="card mb-3">
					<div class="card-header">
						<h5 class="mb-0">MSSF Application Processing</h5>
					</div>
				</div>
				<div class="info-banner mb-3">
					<div class="alert alert-info border-0 mb-3">
						<div class="d-flex align-items-start">
							<i class="bi bi-telephone fs-4 text-primary me-3 mt-1"></i>
							<div>
								<h6 class="text-primary mb-2">Important: Customer Verification Required</h6>
								<div class="mb-2">
									Please contact the customer on <strong><%=mssfData.getPdMobile()%>
								</strong> to:
								</div>
								<ul class="mb-2">
									<li>Verify and confirm all personal details</li>
									<li>Confirm preferred branch for loan processing</li>
									<li>Explain next steps in the loan process</li>
								</ul>
								<div class="border-top pt-2 mt-2">
									<strong class="text-primary">Note:</strong>
									<span class="text-muted">After verification, loan details will be available for modification in branch maker queue</span>
								</div>
							</div>
						</div>
					</div>

					<!-- Loan Details Section -->

				</div>


				<!-- Fixed Details Panel -->
				<div class="card mb-3">
					<div class="card-header">
						<h6 class="mb-0">Application Details</h6>
					</div>
					<div class="card-body">
						<div class="row">
							<!-- Read-only Fields -->
							<div class="col-md-3 mb-3">
								<label class="form-label text-muted">Reference Number</label>
								<div class="readonly-section"><%=mssfData.getRefNo()%> <input type="hidden" name="refNo" id="refNo" value="<%=mssfData.getRefNo()%>"></div>
							</div>
							<div class="col-md-3 mb-3">
								<label class="form-label text-muted">Dealer Code</label>
								<div class="readonly-section"><%=mssfData.getDlrCode()%>
								</div>
							</div>

							<!-- Basic Details -->
							<div class="col-md-3 mb-3">
								<label class="form-label required">Salutation</label>
								<select class="form-select" name="salutation" id="salutation">
									<option value="">Select</option>
									<option value="Mr" <%="Mr".equals(mssfData.getPdSalutation()) ? "selected" : ""%>>Mr</option>
									<option value="Ms" <%="Ms".equals(mssfData.getPdSalutation()) ? "selected" : ""%>>Ms</option>
									<option value="Mrs" <%="Mrs".equals(mssfData.getPdSalutation()) ? "selected" : ""%>>Mrs</option>
									<option value="Dr" <%="Dr".equals(mssfData.getPdSalutation()) ? "selected" : ""%>>Dr</option>
								</select>
							</div>

							<div class="col-md-3 mb-3">
								<label class="form-label required">First Name</label>
								<input type="text" class="form-control" name="firstName"
								       value="<%=mssfData.getPdFirstName() != null ? mssfData.getPdFirstName() : ""%>">
							</div>

							<div class="col-md-3 mb-3">
								<label class="form-label">Middle Name</label>
								<input type="text" class="form-control" name="middleName"
								       value="<%=mssfData.getPdMiddleName() != null ? mssfData.getPdMiddleName() : ""%>">
							</div>

							<div class="col-md-3 mb-3">
								<label class="form-label required">Last Name</label>
								<input type="text" class="form-control" name="lastName"
								       value="<%=mssfData.getPdLastName() != null ? mssfData.getPdLastName() : ""%>">
							</div>

							<!-- Personal Information -->
							<%--        <div class="col-md-3 mb-3">--%>
							<%--            <label class="form-label required">Gender</label>--%>
							<%--            <select class="form-select" name="gender" id="gender">--%>
							<%--                <option value="">Select</option>--%>
							<%--                <option value="Male" <%="Male".equals(mssfData.getPdGender()) ? "selected" : ""%>>Male</option>--%>
							<%--                <option value="Female" <%="Female".equals(mssfData.getPdGender()) ? "selected" : ""%>>Female</option>--%>
							<%--                <option value="Transgender" <%="Transgender".equals(mssfData.getPdGender()) ? "selected" : ""%>>Others</option>--%>
							<%--            </select>--%>
							<%--        </div>--%>

							<%--        <div class="col-md-3 mb-3">--%>
							<%--            <label class="form-label required">Date of Birth</label>--%>
							<%--            <input type="date" class="form-control" name="dob"--%>
							<%--                   value="<%=mssfData.getPdDob() != null ? mssfData.getPdDob() : ""%>">--%>
							<%--        </div>--%>

							<%--        <div class="col-md-3 mb-3">--%>
							<%--            <label class="form-label">Marital Status</label>--%>
							<%--            <select class="form-select" name="maritalStatus" id="maritalStatus">--%>
							<%--                <option value="">Select</option>--%>
							<%--                <option value="Single" <%="Single".equals(mssfData.getPdMaritalStatus()) ? "selected" : ""%>>Single</option>--%>
							<%--                <option value="Married" <%="Married".equals(mssfData.getPdMaritalStatus()) ? "selected" : ""%>>Married</option>--%>
							<%--                <option value="Divorcee" <%="Divorcee".equals(mssfData.getPdMaritalStatus()) ? "selected" : ""%>>Divorcee</option>--%>
							<%--            </select>--%>
							<%--        </div>--%>

							<!-- Contact Details -->
							<div class="col-md-3 mb-3">
								<label class="form-label required">Mobile</label>
								<input type="text" class="form-control" name="mobile"
								       value="<%=mssfData.getPdMobile() != null ? mssfData.getPdMobile() : ""%>">
							</div>

							<div class="col-md-3 mb-3">
								<label class="form-label required">Email</label>
								<input type="email" class="form-control" name="email"
								       value="<%=mssfData.getPdEmail() != null ? mssfData.getPdEmail() : ""%>">
							</div>
							<div class="col-md-3 mb-3">
								<label class="form-label required">Branch</label>
								<select class="form-control form-select branchId" id="branchId" name="branchId">
									<option value="">Select Branch</option>
								</select>
								<div class="invalid-feedback">Please select a branch</div>
							</div>
							<div class="col-md-3 mb-3">
								<label class="form-label required">Canvassing Person</label>
								<select class="form-control form-select canvassed_ppc canvassingPerson" id="canvassingPerson" name="canvassingPerson">
									<option value="">Select Person</option>

								</select>
								<div class="invalid-feedback">Please select a canvassing person</div>
							</div>
							<div class="col-md-3 mb-3">
								<label class="form-label required">RSM</label>
								<select class="form-control form-select rsm_ppc" id="rsm_ppc" name="rsm_ppc">
									<option value="">Select Person</option>

								</select>
								<div class="invalid-feedback">Please select RSM</div>
							</div>


						</div>
					</div>
				</div>
				<div class="card mb-3">
					<div class="card-header">
						<h6 class="mb-0">Customer Details</h6>
					</div>
					<div class="card-body">
						<div class="row">
							<div class="col-md-6 mb-3">
								<label class="form-label required">Residential Status of the Customer</label>
								<div class="form-check">
									<input class="form-check-input" type="radio" name="residentialStatus" id="resident" value="R">
									<label class="form-check-label" for="resident">Resident</label>
								</div>
								<div class="form-check">
									<input class="form-check-input" type="radio" name="residentialStatus" id="nri" value="N">
									<label class="form-check-label" for="nri">NRI</label>
								</div>
							</div>
							<div class="col-md-6 mb-3">
								<label class="form-label required">Whether the customer is an existing customer</label>
								<div class="form-check">
									<input class="form-check-input" type="radio" name="existingCustomer" id="existingYes" value="Y" disabled>
									<label class="form-check-label" for="existingYes">Yes</label>
								</div>
								<div class="form-check">
									<input class="form-check-input" type="radio" name="existingCustomer" id="existingNo" value="N" disabled>
									<label class="form-check-label" for="existingNo">No</label>
								</div>
							</div>
							<div class="col-md-12 mb-3" id="customerIDField" style="display: none;">
								<label class="form-label required">Customer ID</label>
								<input type="text" class="form-control custID" name="customerID" id="customerID">
								<div id="custuserName" class="text-success pt-2 userName"></div>
								<div class="invalid-feedback">Customer ID is required</div>
							</div>
						</div>
					</div>
				</div>

				<!-- Vehicle & Loan Details Panel -->
				<div class="card mb-3">
					<div class="card-header">
						<h6 class="mb-0">Loan & Income Details <span class="text-muted small">(View Only)</span></h6>
					</div>
					<div class="card-body">
						<div class="info-banner">
							<i class="bi bi-info-circle"></i>
							Loan details can be modified after work item creation in the branch maker queue.
						</div>

						<div class="row">
							<div class="col-md-4 mb-3">
								<label class="form-label">Loan Amount</label>
								<input type="text" class="form-control non-editable" value="<%=mssfData.getLaLoanAmt() != null ? mssfData.getLaLoanAmt() : ""%>" readonly>
							</div>
							<div class="col-md-4 mb-3">
								<label class="form-label">Tenure (Months)</label>
								<input type="text" class="form-control non-editable" value="<%=mssfData.getLaTenure() != null ? mssfData.getLaTenure() : ""%>" readonly>
							</div>
							<div class="col-md-4 mb-3">
								<label class="form-label">Rate of Interest (%)</label>
								<input type="text" class="form-control non-editable" value="<%=mssfData.getLaRoi() != null ? mssfData.getLaRoi() : ""%>" readonly>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<!-- Fixed Footer with Actions -->
		<div class="sticky-footer">
			<div class="container-fluid">
				<div class="row align-items-center">
					<div class="col-md-6 text-end">
						<button type="button" class="btn btn-light me-2" id="backToQueueBtn">
							<i class="bi bi-arrow-left me-1"></i>Back to Queue
						</button>


						<button type="button" class="btn btn-primary" id="createWorkItemBtn">
							<i class="bi bi-check-circle me-1"></i>Create Work Item
						</button>
					</div>
				</div>
			</div>
		</div>

		<!-- Confirmation Modal -->
		<div class="modal fade" id="confirmModal" tabindex="-1">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title">Confirm Work Item Creation</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
					</div>
					<div class="modal-body">
						<div class="alert alert-info">
							<p><strong>Please confirm the following:</strong></p>
							<ul class="mb-0">
								<li>Work item will be created in <strong><span id="selectedBranch">Branch Name</span></strong></li>
								<li>Canvassing Person: <strong><span id="selectedPerson">Person Name</span></strong></li>
								<li>All changes made to personal details will be saved</li>
								<li><span id="docMessage">Selected documents will be pulled from MSSF</span></li>
							</ul>
						</div>
						<div class="invalid-feedback-modal mt-2 d-none text-danger">
							<small>Please correct the errors before proceeding.</small>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
						<button type="button" class="btn btn-primary" id="confirmCreate">Confirm & Create</button>
					</div>
				</div>
			</div>
		</div>

		<script>
            const PROCESSING_MESSAGES = {
                FETCH: 'Fetching documents from MSSF...',
                STORE: 'Storing documents...',
                ACK: 'Acknowledging documents...',
                CREATE: 'Creating work item...'
            };
            const LoaderManager = {
                currentLoader: null,
                progressBar: null,

                show(message, showProgress = false) {
                    const loaderHTML = `
            <div class="position-fixed top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center" style="background: rgba(0,0,0,0.5); z-index: 9999;">
                <div class="bg-white p-4 rounded shadow-lg text-center" style="max-width: 400px;">
                    <div class="spinner-border text-primary mb-3"></div>
                    <h5 class="mb-3">${message}</h5>
                    ${showProgress ? '<div class="progress mb-3"><div class="progress-bar progress-bar-striped progress-bar-animated" style="width: 0%"></div></div>' : ''}
                    <div id="loader-status" class="text-muted small"></div>
                </div>
            </div>
        `;

                    // Remove existing loader if any
                    this.hide();

                    // Create new loader
                    const loaderElement = document.createElement('div');
                    loaderElement.innerHTML = loaderHTML;
                    document.body.appendChild(loaderElement);

                    this.currentLoader = loaderElement;
                    this.progressBar = showProgress ? loaderElement.querySelector('.progress-bar') : null;
                },

                updateStatus(status, progress = null) {
                    if (this.currentLoader) {
                        const statusElement = this.currentLoader.querySelector('#loader-status');
                        if (statusElement) statusElement.textContent = status;

                        if (this.progressBar && progress !== null) {
                            this.progressBar.style.width = `${progress}%`;
                        }
                    }
                },

                hide() {
                    if (this.currentLoader) {
                        this.currentLoader.remove();
                        this.currentLoader = null;
                        this.progressBar = null;
                    }
                }
            };

            // Button state management
            const ButtonManager = {
                disableButtons() {
                    const buttons = ['createWorkItemBtn', 'backToQueueBtn'];
                    buttons.forEach(id => {
                        const btn = document.getElementById(id);
                        if (btn) {
                            btn.disabled = true;
                            btn.classList.add('disabled');
                        }
                    });
                },

                enableButtons() {
                    const buttons = ['createWorkItemBtn', 'backToQueueBtn'];
                    buttons.forEach(id => {
                        const btn = document.getElementById(id);
                        if (btn) {
                            btn.disabled = false;
                            btn.classList.remove('disabled');
                        }
                    });
                }
            };
            let documentsProcessed = "";

            // Main Process Handler
            document.getElementById('createWorkItemBtn').addEventListener('click', async function () {
                this.disabled = true;
                this.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Processing...';
                try {
                    if (!validateForm()) {
                        showValidationErrors();
                        return;
                    }
                    // First process documents
                    const documentsProcessed = await handleDocumentProcessing($('#refNo').val());
                    //const documentResult  = await handleDocumentProcessing(129029496);
                    console.log('Document processing result:', documentsProcessed);
                    if (!documentsProcessed) return;
                    //documentsProcessed = documentResult;
                    console.log('Stored documents:', documentsProcessed);
                    // Then show modal
                    showModal();
                } finally {
                    // Re-enable only if needed
                    this.disabled = false;
                    this.innerHTML = '<i class="bi bi-check-circle me-1"></i>Create Work Item';
                }

            });

            document.getElementById('backToQueueBtn').addEventListener('click', function (e) {
                e.preventDefault();

                Swal.fire({
                    title: 'Confirm Navigation',
                    text: 'Are you sure you want to go back?',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonText: 'Yes, go back',
                    cancelButtonText: 'No, stay here'
                }).then((result) => {
                    if (result.isConfirmed) {
                        document.getElementById('backToQueueForm').submit();
                    }
                });
            });


            // Document Processing
            async function handleDocumentProcessing(refNo) {
                mssfShowLoader('Processing Documents');
                updateProcessingStatus(PROCESSING_MESSAGES.FETCH);

                try {
                    const response = await fetch('api/process-documents/' + refNo, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                        }
                    });

                    const result = await response.json();
                    if (!response.ok) throw new Error(result.message || 'Document processing failed');

                    hideLoader();
                    return result;

                } catch (error) {
                    hideLoader();
                    showErrorModal('Document Processing Failed', error.message);
                    return false;
                }
            }


            // Helper Functions
            function getCustomerName() {
                var firstName = $('input[name="firstName"]').val() || '';
                var middleName = $('input[name="middleName"]').val() || '';
                var lastName = $('input[name="lastName"]').val() || '';
                return $.trim(firstName + ' ' + middleName + ' ' + lastName);
            }

            async function createWorkItem(data) {
                const formData = new URLSearchParams();
                Object.keys(data).forEach(key => {
                    formData.append(key, data[key]);
                });

                const response = await fetch('addmssfEntry', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                    },
                    body: formData.toString()
                });

                const result = await response.json();
                if (!result.slno || !result.winum) {
                    throw new Error('Work item creation failed');
                }
                return result;
            }

            function mssfShowLoader(message) {
                Swal.fire({
                    title: message,
                    html: '<div class="progress" style="height: 4px;"><div class="progress-bar progress-bar-indeterminate"></div></div><div id="processingStatus" class="mt-3">Please wait...</div>',
                    allowOutsideClick: false,
                    showConfirmButton: false
                });
            }

            function hideLoader() {
                Swal.close();
            }

            function updateProcessingStatus(message) {
                const statusElement = document.getElementById('processingStatus');
                if (statusElement) {
                    statusElement.textContent = message;
                }
            }

            function showErrorModal(title, message) {
                Swal.fire({
                    title: title,
                    html: '<div class="alert alert-danger"><p>' + message + '</p><hr><p class="mb-0">Please try again or contact support.</p></div>',
                    icon: 'error',
                    showCancelButton: true,
                    confirmButtonText: 'Retry',
                    cancelButtonText: 'Back to Queue'
                }).then((result) => {
                    if (!result.isConfirmed) {
                        document.getElementById('backToQueueForm').submit();
                    }
                });
            }


            function showSuccessAndRedirect(winum) {
                Swal.fire({
                    title: 'Success',
                    html: '<div class="alert alert-success"><p>Work Item Number: ' + winum + '</p><p class="mb-0">Redirecting to queue...</p></div>',
                    icon: 'success',
                    timer: 2000,
                    showConfirmButton: false
                }).then(() => {
                    document.getElementById('backToQueueForm').submit();
                });
            }

            function validateForm() {
                var isValid = true;
                var requiredFields = {
                    'branchId': 'Branch',
                    'canvassingPerson': 'Canvassing Person',
                    'firstName': 'First Name',
                    'lastName': 'Last Name',
                    'mobile': 'Mobile Number',
                    'email': 'Email'
                };

                Object.keys(requiredFields).forEach(function (field) {
                    var element = document.querySelector('[name="' + field + '"]');
                    if (!element.value.trim()) {
                        element.classList.add('is-invalid');
                        isValid = false;
                    } else {
                        element.classList.remove('is-invalid');
                    }
                });

                var mobile = document.querySelector('[name="mobile"]').value;
                if (!/^\d{10}$/.test(mobile)) {
                    document.querySelector('[name="mobile"]').classList.add('is-invalid');
                    isValid = false;
                }

                return isValid;
            }

            function showValidationErrors() {
                var errors = [];
                var requiredFields = {
                    'branchId': 'Branch',
                    'canvassingPerson': 'Canvassing Person',
                    'firstName': 'First Name',
                    'lastName': 'Last Name',
                    'mobile': 'Mobile Number',
                    'email': 'Email'
                };

                Object.keys(requiredFields).forEach(function (field) {
                    var element = document.querySelector('[name="' + field + '"]');
                    if (!element.value.trim()) {
                        errors.push('<li>' + requiredFields[field] + ' is required</li>');
                    }
                });

                var mobile = document.querySelector('[name="mobile"]').value;
                if (!/^\d{10}$/.test(mobile)) {
                    errors.push('<li>Mobile number must be 10 digits</li>');
                }

                Swal.fire({
                    title: 'Validation Error',
                    html: '<div class="alert alert-warning"><h6 class="alert-heading">Please check the following:</h6><ul class="mb-0">' + errors.join('') + '</ul></div>',
                    icon: 'warning',
                    confirmButtonText: 'Ok'
                });
            }

            // Initialize Form
            document.addEventListener('DOMContentLoaded', function () {
                initializeFormHandlers();
                initializeSelect2();
                setupNavigationHandlers();
            });

            function setupNavigationHandlers() {
                const backBtn = document.getElementById('backToQueueBtn');
                const createBtn = document.getElementById('createWorkItemBtn');

                if (backBtn) {
                    backBtn.addEventListener('click', handleBackNavigation);
                }

                // if (createBtn) {
                //     createBtn.addEventListener('click', createWorkItem);
                // }
            }

            function handleBackNavigation(e) {
                e.preventDefault();

                if (2 == 1) {
                    Swal.fire({
                        title: 'Unsaved Changes',
                        html: `
                <div class="alert alert-warning">
                    <p>You have unsaved changes. Are you sure you want to leave?</p>
                </div>
            `,
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonText: 'Yes, leave page',
                        cancelButtonText: 'Stay on page'
                    }).then((result) => {
                        if (result.isConfirmed) {
                            LoaderManager.show('Returning to queue...', true);
                            document.getElementById('backToQueueForm').submit();
                        }
                    });
                } else {
                    LoaderManager.show('Returning to queue...', true);
                    document.getElementById('backToQueueForm').submit();
                }
            }

            function initializeFormHandlers() {
                document.querySelectorAll('.form-control, .form-select').forEach(function (element) {
                    element.addEventListener('change', function () {
                        this.classList.remove('is-invalid');
                    });
                });

                $('#customerID').on('change', handleCustomerIdChange);

                document.getElementsByName('residentialStatus').forEach(function (radio) {
                    radio.addEventListener('change', handleResidentialStatusChange);
                });

                document.getElementsByName('existingCustomer').forEach(function (radio) {
                    radio.addEventListener('change', handleExistingCustomerChange);
                });
            }

            function initializeSelect2() {
                $('#branchId').select2({
                    placeholder: "Enter a Sol (ID or Name)",
                    minimumInputLength: 4,
                    ajax: {
                        url: function (params) {
                            return 'api/sol-mssffetch/' + params.term;
                        },
                        dataType: 'json',
                        delay: 250,
                        processResults: function (data) {
                            return {
                                results: data.map(function (item) {
                                    return {
                                        id: item.codevalue,
                                        text: item.codedesc
                                    };
                                })
                            };
                        },
                        cache: true
                    }
                });

                $('#canvassingPerson').select2({
                    placeholder: "Enter a PPC",
                    minimumInputLength: 4,
                    ajax: {
                        url: function (params) {
                            return 'api/ppc-fetch/' + params.term;
                        },
                        dataType: 'json',
                        delay: 250,
                        processResults: function (data) {
                            return {
                                results: data.map(function (item) {
                                    return {
                                        id: item.codevalue,
                                        text: item.codedesc
                                    };
                                })
                            };
                        },
                        cache: true
                    }
                });

                $('#rsm_ppc').select2({
                    placeholder: "Enter a RSM PPC",
                    minimumInputLength: 4,
                    ajax: {
                        url: function (params) {
                            return 'api/ppc-rsmfetch/' + params.term;
                        },
                        dataType: 'json',
                        delay: 250,
                        processResults: function (data) {
                            return {
                                results: data.map(function (item) {
                                    return {
                                        id: item.codevalue,
                                        text: item.codedesc
                                    };
                                })
                            };
                        },
                        cache: true
                    }
                });
            }

            async function handleCustomerIdChange() {
                var custID = $(this).val();
                if (custID.length >= 9) {
                    mssfShowLoader();
                    try {
                        const response = await fetch('api/getCustName', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded',
                                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                            },
                            body: 'custId=' + custID
                        });

                        const data = await response.json();
                        if (data.status === 'S') {
                            $('#custuserName').html('<i class="ph ph-user"></i>' + data.msg);
                        } else {
                            throw new Error('Invalid CustID');
                        }
                    } catch (error) {
                        $(this).val('');
                        $('#custuserName').html('');
                        showErrorAlert('Please Enter Valid CustID');
                    } finally {
                        hideLoader();
                    }
                }
            }

            function handleResidentialStatusChange() {
                var isNRI = document.getElementById('nri').checked;
                var existingCustomerOptions = document.getElementsByName('existingCustomer');
                var customerIDField = document.getElementById('customerIDField');

                existingCustomerOptions.forEach(function (option) {
                    option.checked = false;
                    option.disabled = isNRI && option.value === 'N';
                });

                if (isNRI) {
                    document.getElementById('existingYes').checked = true;
                    customerIDField.style.display = 'block';
                } else {
                    customerIDField.style.display = 'none';
                }
            }

            function handleExistingCustomerChange() {
                var isExisting = document.getElementById('existingYes').checked;
                document.getElementById('customerIDField').style.display = isExisting ? 'block' : 'none';
            }

            // Error and Alert Functions
            function showErrorAlert(message) {
                Swal.fire({
                    title: 'Error',
                    html: '<div class="alert alert-danger">' + message + '</div>',
                    icon: 'error',
                    confirmButtonText: 'OK'
                });
            }

            function showSuccessAlert(message) {
                Swal.fire({
                    title: 'Success',
                    html: '<div class="alert alert-success">' + message + '</div>',
                    icon: 'success',
                    timer: 2000,
                    timerProgressBar: true,
                    showConfirmButton: false
                });
            }

            // Modal Functions
            function showModal() {
                var branch = document.querySelector('select[name="branchId"] option:checked').text;
                var person = document.querySelector('select[name="canvassingPerson"] option:checked').text;

                document.getElementById('selectedBranch').textContent = branch;
                document.getElementById('selectedPerson').textContent = person;

                $('#confirmModal').modal('show');
            }

            // Form Reset Functions
            function resetCustomerFields() {
                $('#customerID').val('');
                $('#custuserName').html('');
                document.getElementById('customerIDField').style.display = 'none';
            }

            function resetValidation() {
                document.querySelectorAll('.is-invalid').forEach(function (element) {
                    element.classList.remove('is-invalid');
                });
                document.querySelectorAll('.invalid-feedback').forEach(function (element) {
                    element.style.display = 'none';
                });
            }

            // CSRF Token Functions
            function getCSRFToken() {
                return document.querySelector('meta[name="_csrf"]').content;
            }

            function getCSRFHeader() {
                return document.querySelector('meta[name="_csrf_header"]').content;
            }

            // Error Response Handler
            function handleErrorResponse(error) {
                console.error('Error:', error);
                var errorMessage = 'An unexpected error occurred. Please try again.';

                if (error.message) {
                    errorMessage = error.message;
                } else if (error.responseJSON && error.responseJSON.message) {
                    errorMessage = error.responseJSON.message;
                }

                showErrorAlert(errorMessage);
            }

            // Add event listener for modal close
            $('#confirmModal').on('hidden.bs.modal', function () {
                document.getElementById('confirmCreate').disabled = false;
            });

            // Add event listener for back button
            document.getElementById('backToQueueBtn').addEventListener('click', function (e) {
                e.preventDefault();

                Swal.fire({
                    title: 'Confirm Navigation',
                    text: 'Are you sure you want to go back?',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonText: 'Yes, go back',
                    cancelButtonText: 'No, stay here'
                }).then((result) => {
                    if (result.isConfirmed) {
                        document.getElementById('backToQueueForm').submit();
                    }
                });
            });


            // Add error boundary
            window.onerror = function (message, source, lineno, colno, error) {
                console.error('Global error:', error);
                showErrorAlert('An unexpected error occurred. Please try again or contact support.');
                return false;
            };


            // Modal Functions
            function showModal() {
                var branch = $('#branchId option:selected').text();
                var person = $('#canvassingPerson option:selected').text();

                $('#selectedBranch').text(branch);
                $('#selectedPerson').text(person);
                $('#confirmModal').modal('show');
            }

            // Back Button Confirmation

            // Modal Event Handlers
            $('#confirmModal').on('hidden.bs.modal', function () {
                $('#confirmCreate').prop('disabled', false);
            });

            // Global Error Handler
            window.onerror = function (message, source, lineno, colno, error) {
                console.error('Global error:', {message, source, lineno, colno, error});
                showErrorAlert('An unexpected error occurred. Please try again or contact support.');
                return false;
            };
            document.getElementById('confirmCreate').addEventListener('click', async function () {
                // Prevent double submission
                if (this.disabled) return;

                this.disabled = true;
                $(this).html('<span class="spinner-border spinner-border-sm me-2"></span>Processing...');
                mssfShowLoader('Creating Work Item');

                try {
                    const workitemData = {
                        refNo: $('#refNo').val(),
                        customerName: getCustomerName(),
                        residentialStatus: $('input[name="residentialStatus"]:checked').val(),
                        existingCustomer: $('input[name="existingCustomer"]:checked').val(),
                        customerID: $('#customerID').val(),
                        branchId: $('#branchId').val(),
                        canvassingPerson: $('#canvassingPerson').val(),
                        rsmPpc: $('#rsm_ppc').val(),
                        fromMSSF: 'Y'
                    };

                    const response = await createWorkItem(workitemData);
                    $('#confirmModal').modal('hide');

                    if (response.winum) {
                        // Prepare formData for the general details API
                        const formData = {
                            loanType: 'VL',
                            tabType: 'GENERAL',
                            body: {
                                slno: response.slno,
                                winum: response.winum,
                                appid: '',
                                reqtype: 'A',
                                currenttab: 'A-1',
                                data: [
                                    {
                                        key: 'residentialStatus',
                                        value: $('input[name="residentialStatus"]:checked').val()
                                    },
                                    {
                                        key: 'custID',
                                        value: $('#customerID').val()
                                    },
                                    {
                                        key: 'sibCustomer',
                                        value: $('input[name="existingCustomer"]:checked').val() === 'Yes' ? 'Y' : 'N'
                                    },
                                    {
                                        key: 'canvassed_ppc',
                                        value: $('#canvassingPerson').val()
                                    },
                                    {
                                        key: 'rsm_ppc',
                                        value: $('#rsm_ppc').val()
                                    }
                                ],
                                DOC_ARRAY: documentsProcessed.documents
                            },
                            reqip: ''
                        };
                        console.log('FormData being sent:', formData);
                        await saveGeneralDetails(formData, response.winum);
                    } else {
                        throw new Error('Work item creation failed');
                    }

                } catch (error) {
                    handleError(error);
                } finally {
                    this.disabled = false;
                    $(this).html('Confirm & Create');
                }
            });

            // Helper function to save general details
            async function saveGeneralDetails(formData, winum) {
                try {
                    const response = await $.ajax({
                        url: 'api/saveGeneralDetails',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(formData)
                    });

                    if (response.status === 'S') {
                        await showSuccessAndRedirect(winum);
                    } else {
                        throw new Error(response.message || 'Error saving details');
                    }
                } catch (error) {
                    hideLoader();
                    showErrorAlert('Error saving general details: ' + error.message);
                    throw error;
                }
            }

            // Helper function to handle errors
            function handleError(error) {
                console.error('Work item creation error:', error);
                hideLoader();
                showErrorAlert('Failed to create work item: ' + error.message);
            }


		</script>
		<los:modal/>
		<los:footer/>
	</body>
</html>