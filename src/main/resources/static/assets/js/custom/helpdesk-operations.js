console.log("Helpdesk scr loading");
var repaymentModal;
var experianModal;
let experianModalInstance;
document.addEventListener('DOMContentLoaded', function() {
    console.log("DOMContentLoaded fired");
    initializeEventListeners();
    initializeBootstrapComponents();
    initializePagination();
    initializePaginationData();
});

function initializeRepaymentModal() {
    const modalElement = document.getElementById('repaymentModal');
    if (!modalElement) {
        console.log("Modal element not found");
        return;
    }
    repaymentModal = new bootstrap.Modal(modalElement);
}
function initializeExperianModal() {
    const modalElement = document.getElementById('experianModal');
    if (!modalElement) {
        console.error("Modal element not found");
        return;
    }
    // Initialize the modal instance only once
    experianModalInstance = new bootstrap.Modal(modalElement);
    // Initialize the form and its event listener only once
    initializeExperianForm();
}

function populateRepaymentForm() { // Get the values from existing fields if available
    const bankName = document.getElementById('bankName');
    const accountNumber = document.getElementById('accountNumber');
    const ifscCode = document.getElementById('ifscCode');
    const borrowerName = document.getElementById('borrowerName');
    const remarks = document.getElementById('remarks'); // Reset form
     const form = document.getElementById('repaymentForm');
    if (form) form.reset(); // Set values if they exist in the data attributes
    if (bankName && bankName.dataset.value) {
        bankName.value = bankName.dataset.value;
    }
    if (accountNumber && accountNumber.dataset.value) {
        accountNumber.value = accountNumber.dataset.value;
    }
    if (ifscCode && ifscCode.dataset.value) {
        ifscCode.value = ifscCode.dataset.value;
    }
    if (borrowerName && borrowerName.dataset.value) {
        borrowerName.value = borrowerName.dataset.value;
    }
}

function showRepaymentModal() {
console.log("Entered repayment modal");
    const modalElement = document.getElementById('repaymentModal');
    if (!modalElement) {
        console.error('Modal element not found');
        return;
    }

    if (!repaymentModal) {
        initializeRepaymentModal();

    }
    populateRepaymentForm();

    repaymentModal.show();
}


function showExperianModal() {
    console.log("Entered showExperianModal");

    // Get the current work item from your main page's search input
    const currentWiNum = document.getElementById('wiNum').value;
    const expWorkItemInput = document.getElementById('expworkItem');

    // If the main wiNum field is empty, log an error and exit
    if (!currentWiNum) {
        console.error("Main wiNum field is empty. Cannot populate modal.");
        return;
    }

    // Update the modal's work item field before showing
    expWorkItemInput.value = currentWiNum;
    console.log("Populating modal with wiNum: " + currentWiNum);

   // if (!experianModalInstance) {
        // Fallback for initialization if it hasn't been done yet
        initializeExperianModal();
    //}

    experianModalInstance.show();
}


function initializeExperianForm() {
    console.log("initializeExperianForm called for the first time");
    const saveButton = document.getElementById('saveExperianBtn');
    if (saveButton) {
        // Add event listener once, no need to remove/add again
        saveButton.addEventListener('click', handleExperianSave);
    } else {
        console.error("Save button not found");
    }
}



async function handleExperianSave(e) {
    e.preventDefault();
    console.log("Entered saving section");

    const formWorkItem = document.getElementById('expworkItem').value;
    const remarks = document.getElementById('expremarks').value;
    const panNumber = document.getElementById('panNumber').value;

    console.log("Remarks: " + remarks);
    console.log("WorkItem from modal: " + formWorkItem);

    if (!formWorkItem || !remarks || !panNumber) {
        showError('Please fill out all required fields.');
        return;
    }

    showLoading('Saving...');

    try {
        const response = await fetch('helpdesk/reset-experian', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
                workItem: formWorkItem,
                remarks: remarks,
                panNumber: panNumber
            })
        });


const data = await response.json();
hideLoading();

if (data.status === 'Success') {
    Swal.fire({
        title: 'Success',
        text: data.msg,
        icon: 'success'
    });
    experianModalInstance.hide();
       document.getElementById("experianForm").reset();
} else if (data.status === 'error') { // Handle the specific error status
    // Display the error message from the response
    Swal.fire({
        title: 'Error',
        text: data.message, // Use data.message for the error text
        icon: 'error'
    });
   }
    }catch (error) {
          hideLoading();
          showError(error.message);
      }


}


function initializeRepaymentForm() {
    console.log("initializeRepaymentForm called");
    const form = document.getElementById('repaymentForm');
    const saveButton = document.getElementById('saveRepaymentBtn');

    if (!form || !saveButton) {
        console.log("Form or save button not found");
        return;
    }

    // Remove any existing event listeners
    saveButton.removeEventListener('click', handleRepaymentSave);

    // Add click event listener to save button
    saveButton.addEventListener('click', handleRepaymentSave);
}

async function handleRepaymentSave(e) {
    e.preventDefault();

    if (!validateRepaymentForm()) return;

    const remarks = document.getElementById('remarks').value;
    if (!remarks || remarks.trim().length < 10) {
        showError('Please enter remarks with at least 10 characters');
        return;
    }

    showLoading('Saving...');

    try {
        const requestData = {
            repaymentDTO: {
                bankName: document.getElementById('bankName').value,
                accountNumber: document.getElementById('accountNumber').value,
                ifscCode: document.getElementById('ifscCode').value,
                borrowerName: document.getElementById('borrowerName').value,
                wiNum: document.getElementById('wiNum').value,
                 slno: document.getElementById('workslno').value
            },
            remarks: remarks
        };

        const response = await fetch('helpdesk/updateRepaymentDetails', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(requestData)
        });

        const data = await response.json();
        hideLoading();

        if (data.status === 'S') {
            Swal.fire({
                title: 'Success',
                text: data.msg,
                icon: 'success'
            });
            repaymentModal.hide();
        } else {
            throw new Error(data.msg);
        }
    } catch (error) {
        hideLoading();
        showError(error.message);
    }
}


function loadApiEndpoints() {
    const select = document.getElementById('apiEndpoint');
    select.disabled = true;
    select.innerHTML = '<option value="">Loading...</option>';

    fetch('api/endpoints')
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            if (!Array.isArray(data)) {
                throw new Error('Invalid response format');
            }

            select.innerHTML = '<option value="">Select an API endpoint</option>';
            data.forEach(endpoint => {
                const option = document.createElement('option');
                option.value = endpoint.API;
                option.textContent = endpoint.API_NAME;
                select.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Failed to load endpoints:', error);
            select.innerHTML = '<option value="">Error loading endpoints</option>';
        })
        .finally(() => {
            select.disabled = false;
        });
}





function initializePagination() {
    if (totalPages > 1) {
        setupPagination();
        const pageSizeSelect = document.getElementById('pageSize');
        if (pageSizeSelect) {
            pageSizeSelect.value = currentSize;
        }
    }
}
function validateRepaymentForm() {
    const accountNumber = document.getElementById('accountNumber').value;
    const ifscCode = document.getElementById('ifscCode').value;
    const borrowerName = document.getElementById('borrowerName').value;

    // Validates account number format (9-18 alphanumeric chars)
    if (!/^[a-zA-Z0-9\s]{9,18}$/.test(accountNumber)) {
        const accountNumber = "0193054000000214";
        const regex = /^[a-zA-Z0-9\s]{9,18}$/;
        console.log(regex.test(accountNumber));

        showError('Invalid account number format');
        return false;
    }

    // Validates IFSC code format (4 letters + 0 + 6 alphanumeric)
    if (!/^[A-Z]{4}0[A-Z0-9]{6}$/.test(ifscCode)) {
        showError('Invalid IFSC code format');
        return false;
    }

    // Validates borrower name (letters and spaces only, max 50 chars)
    if (!/^[a-zA-Z\s]{1,50}$/.test(borrowerName)) {
        showError('Invalid borrower name format');
        return false;
    }

    return true;
}

// function initializeRepaymentForm() {
//     console.log("initializeRepaymentForm called");
//     const form = document.getElementById('repaymentForm');
//     if (!form) return;
//
//     form.addEventListener('submit', async function(e) {
//         console.log("Form submit event fired");
//         e.preventDefault();
//         if (!validateRepaymentForm()) return;
//         showLoading('Saving...');
//
//         try {
//             const response = await fetch('doc/updateRepaymentDetails', {
//                 method: 'POST',
//                 headers: {'Content-Type': 'application/json'},
//                 body: JSON.stringify({
//                     bankName: document.getElementById('bankName').value,
//                     accountNumber: document.getElementById('accountNumber').value,
//                     ifscCode: document.getElementById('ifscCode').value,
//                     borrowerName: document.getElementById('borrowerName').value,
//                     slno: document.getElementById('slno')?.value,
//                     wiNum: document.getElementById('wiNum')?.value
//                 })
//             });
//
//             const data = await response.json();
//             hideLoading();
//
//             if (data.status === 'S') {
//                 Swal.fire({
//                     title: 'Success',
//                     text: data.msg,
//                     icon: 'success'
//                 });
//                 repaymentModal.hide();
//             } else {
//                 throw new Error(data.msg);
//             }
//         } catch (error) {
//             hideLoading();
//             showError(error.message);
//         }
//     });
// }
// function initializeRepaymentFormold() {
//     console.log("initializeRepaymentForm called");
//     const form = document.getElementById('repaymentForm');
//     if (!form) return;
//
//     form.addEventListener('submit', async function(e) {
//         console.log("Form submit event fired");
//         e.preventDefault();
//         if (!validateRepaymentForm()) return;
//         showLoading('Saving...');
//
//         try {
//             const response = await fetch('doc/updateRepaymentDetails', {
//                 method: 'POST',
//                 headers: {'Content-Type': 'application/json'},
//                 body: JSON.stringify({
//                     bankName: document.getElementById('bankName').value,
//                     accountNumber: document.getElementById('accountNumber').value,
//                     ifscCode: document.getElementById('ifscCode').value,
//                     borrowerName: document.getElementById('borrowerName').value,
//                     slno: document.getElementById('slno')?.value,
//                     wiNum: document.getElementById('wiNum')?.value
//                 })
//             });
//
//             const data = await response.json();
//             hideLoading();
//
//             if (data.status === 'S') {
//                 Swal.fire({
//                     title: 'Success',
//                     text: data.msg,
//                     icon: 'success'
//                 });
//                 document.getElementById('repaymentSection').classList.add('d-none');
//             } else {
//                 throw new Error(data.msg);
//             }
//         } catch (error) {
//             hideLoading();
//             showError(error.message);
//         }
//     });
// }

function initializePaginationData() {
    const pageData = document.getElementById('pageData');
    if (pageData) {
        window.currentWiNum = pageData.dataset.wiNum;
        window.currentPage = parseInt(pageData.dataset.currentPage);
        window.currentSize = parseInt(pageData.dataset.pageSize);
        window.totalPages = parseInt(pageData.dataset.totalPages);
        window.totalElements = parseInt(pageData.dataset.totalElements);

        console.log("Pagination data initialized:", {
            wiNum: window.currentWiNum,
            page: window.currentPage,
            size: window.currentSize,
            totalPages: window.totalPages
        });
    }
}


function formatInput(input) {
    let value = input.value;
    const formattedPattern = /^VLR_\d{9}$/;
    if (formattedPattern.test(value)) return;
    let numericPart = value.replace(/\D/g, '');
    if (numericPart.length > 0) {
        value = 'VLR_' + numericPart.padStart(9, '0');
    }
    input.value = value;
}

function initializeEventListeners() {
    const searchForm = document.getElementById('searchForm');
    if (searchForm) {
        searchForm.addEventListener('submit', function(e) {
            e.preventDefault();
            searchWorkItem();
        });
    }

    const pageSizeSelect = document.getElementById('pageSize');
    if (pageSizeSelect) {
        pageSizeSelect.addEventListener('change', (e) => changePageSize(e.target.value));
    }
}

function initializeBootstrapComponents() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

function setupPagination() {
    updatePaginationControls();
    updatePageInfo();
}

function changePage(page) {
    window.location.href = 'helpdesk/search?wiNum=' + currentWiNum + '&page=' + page + '&size=' + currentSize;
}


function changePageSize(size) {
    window.location.href = 'helpdesk/search?wiNum=' + currentWiNum + '&page=1&size=' + size;
}


function updatePaginationControls() {
    const pagination = document.getElementById('pagination');
    if (!pagination) return;

    let html = '<li class="page-item ' + (currentPage === 1 ? 'disabled' : '') + '">' +
        '<a class="page-link" href="javascript:void(0)" onclick="changePage(' + (currentPage - 1) + ')">Previous</a>' +
        '</li>';

    for (let i = 1; i <= totalPages; i++) {
        if (i === 1 || i === totalPages || Math.abs(i - currentPage) <= 2) {
            html += '<li class="page-item ' + (i === currentPage ? 'active' : '') + '">' +
                '<a class="page-link" href="javascript:void(0)" onclick="changePage(' + i + ')">' + i + '</a>' +
                '</li>';
        } else if (Math.abs(i - currentPage) === 3) {
            html += '<li class="page-item disabled"><span class="page-link">...</span></li>';
        }
    }

    html += '<li class="page-item ' + (currentPage === totalPages ? 'disabled' : '') + '">' +
        '<a class="page-link" href="javascript:void(0)" onclick="changePage(' + (currentPage + 1) + ')">Next</a>' +
        '</li>';

    pagination.innerHTML = html;
}


function updatePageInfo() {
    const start = ((currentPage - 1) * currentSize) + 1;
    const end = Math.min(currentPage * currentSize, totalElements);
    const pageInfo = document.getElementById('pageInfo');
    if (pageInfo) {
        pageInfo.textContent = `Showing `+start+` to `+end+` of `+totalElements+` entries`;
    }
}

async function searchWorkItem() {
    formatInput(document.getElementById('wiNum'));
    const wiNum = document.getElementById('wiNum').value;
    if (!wiNum) {
        Swal.fire({
            title: 'Error',
            text: 'Please enter a work item number',
            icon: 'error',
            confirmButtonColor: '#3085d6'
        });
        return;
    }

    showLoading('Searching work item...');

    try {
        const response = await fetch(`helpdesk/search?wiNum=`+wiNum+``);
        const htmlContent = await response.text();

        hideLoading();

        if (!response.ok) {
            throw new Error('Failed to fetch work item details');
        }

        const detailsContainer = document.getElementById('workItemDetails');
        if (detailsContainer) {
            detailsContainer.innerHTML = htmlContent;
            detailsContainer.classList.remove('d-none');
            initializeBootstrapComponents();
            setupPagination();
        }

    } catch (error) {
        hideLoading();
        showError(error.message || 'Failed to fetch work item details');
    }
}

async function performAction(action) {
    const wiNum = document.getElementById('wiNum').value;
    const slno = document.getElementById('workslno').value;
    let confirmConfig = {
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, proceed',
        cancelButtonText: 'No, cancel',
        reverseButtons: true
    };

    switch(action) {
        case 'reset-documentation':
            confirmConfig = {
                ...confirmConfig,
                title: 'Reset Documentation',
                html: `
                   <div class="text-start">
                       <p class="mb-3">This action will:</p>
                       <ul class="list-unstyled ms-3 mb-3">
                           <li>• Reset the documentation mode</li>
                           <li>• Remove all legality invitees</li>
                       </ul>
                       <div class="alert alert-warning">
                           <i class="bi bi-exclamation-triangle-fill me-2"></i>
                           <strong>Warning:</strong> If document signing is already completed, 
                           resetting will require re-signing and may incur additional stamp charges.
                       </div>
                   </div>`,
                icon: 'warning',
                confirmButtonText: 'Yes, Reset Documentation'
            };
            break;
                  case 'reset-experian':
                  showExperianModal();
                   return;
        case 'release-lock':
            confirmConfig = {
                ...confirmConfig,
                title: 'Release Lock',
                text: 'This will release the main workitem lock. Are you sure?',
                icon: 'warning',
                confirmButtonText: 'Yes, Release Lock'
            };
            break;
        case 'release-child-locks':
            confirmConfig = {
                ...confirmConfig,
                title: 'Release Child Locks',
                text: 'This will release all child workitem locks. Are you sure?',
                icon: 'warning',
                confirmButtonText: 'Yes, Release Child Locks'
            };
            break;
         case 'show-repayment':
         showRepaymentModal();

            return;
        case 'manage-acopn':
            // Step 1: Show a dropdown (or radio) to pick the sub-action
            const { value: subAction } = await Swal.fire({
                title: 'Manage Account Opening Queue',
                html: `
                    <div class="text-start">
                        <p class="mb-3">This action will:</p>
                        <ul class="list-unstyled ms-3 mb-3">
                            <li>• Manage the Workitem from Account Opening Queue</li>
                            <li>• Only Rejection / Revoke / Mark PD is allowed</li>
                        </ul>
                        <div class="alert alert-warning">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>
                            <strong>Warning:</strong> If account opening has already completed, this feature is not allowed.
                        </div>
                        <hr>
                        <label for="queueAction" class="form-label fw-bold">Choose Action:</label>
                        <select id="queueAction" class="form-select">
                            <option value="REJ">Reject</option>
                            <option value="REV">Revoke</option>
                            <option value="PD">Mark PD</option>
                        </select>
                    </div>
                `,
                icon: 'warning',
                showCancelButton: true,
                preConfirm: () => {
                    // Return the value from the <select> in the modal
                    const el = document.getElementById('queueAction');
                    return el ? el.value : null;
                }
            });

            if (!subAction) return; // user cancelled

            // Step 2: Ask for remarks
            const remarksResult = await Swal.fire({
                title: 'Enter Remarks',
                input: 'textarea',
                inputLabel: 'Please provide remarks for this action',
                inputPlaceholder: 'Type your remarks here...',
                inputAttributes: {
                    'aria-label': 'Remarks',
                    'maxlength': '500'
                },
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                inputValidator: (value) => {
                    if (!value) return 'You need to enter remarks!';
                    if (value.length < 10) return 'Remarks should be at least 10 characters long!';
                    return null;
                }
            });

            if (!remarksResult.isConfirmed) return;
            const remarks = remarksResult.value;

            try {
                showLoading('Processing request...');

                // Step 3: Send POST request to /manage-acopn
                const response = await fetch('helpdesk/manage-acopn', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams({
                        wiNum: wiNum,
                        remarks: remarks,
                        queueAction: subAction,  // "REJ", "REV", or "PD"
                        userId: document.getElementById('currentUserId').value
                    })
                });

                const responseData = await response.json();
                hideLoading();

                if (!response.ok) {
                    throw new Error(responseData.message || 'Operation failed');
                }

                await Swal.fire({
                    title: 'Success',
                    text: responseData.message || 'Operation completed successfully',
                    icon: 'success',
                    confirmButtonColor: '#3085d6'
                });

                // Refresh the page or re-fetch details
                await searchWorkItem();

            } catch (error) {
                hideLoading();
                showError(error.message || 'An unexpected error occurred');
            }
            return;

    }

    try {
        const result = await Swal.fire(confirmConfig);
        if (!result.isConfirmed) return;

        const remarksResult = await Swal.fire({
            title: 'Enter Remarks',
            input: 'textarea',
            inputLabel: 'Please provide detailed remarks for this action',
            inputPlaceholder: 'Type your remarks here...',
            inputAttributes: {
                'aria-label': 'Remarks',
                'maxlength': '500'
            },
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            inputValidator: (value) => {
                if (!value) return 'You need to enter remarks!';
                if (value.length < 10) return 'Remarks should be at least 10 characters long!';
                return null;
            }
        });

        if (!remarksResult.isConfirmed) return;

        showLoading('Processing request...');

        const response = await fetch(`helpdesk/`+action, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
                wiNum: wiNum,
                remarks: remarksResult.value,
                userId: document.getElementById('currentUserId').value
            })
        });

        const responseData = await response.json();

        hideLoading();

        if (!response.ok) {
            throw new Error(responseData.message || 'Operation failed');
        }

        await Swal.fire({
            title: 'Success',
            text: responseData.message || 'Operation completed successfully',
            icon: 'success',
            confirmButtonColor: '#3085d6'
        });

        await searchWorkItem();

    } catch (error) {
        hideLoading();
        showError(error.message || 'An unexpected error occurred');
    }
}

function showLoading(message = 'Loading...') {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) {
        loadingOverlay.style.display = 'flex';
        loadingOverlay.style.justifyContent = 'center';
    }
}

function hideLoading() {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) {
        loadingOverlay.style.display = 'none';
    }
}

function showError(message) {
    Swal.fire({
        title: 'Error',
        text: message,
        icon: 'error',
        confirmButtonColor: '#3085d6'
    });
}

function formatDateTime(dateString) {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleString('en-IN', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
    });
}

window.addEventListener('unhandledrejection', function(event) {
    console.error('Unhandled promise rejection:', event.reason);
    hideLoading();
    showError('An unexpected error occurred. Please try again.');
});
