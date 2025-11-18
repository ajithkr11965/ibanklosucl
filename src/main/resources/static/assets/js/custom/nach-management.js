/**
 * NACH Management System
 * Handles all NACH-related operations including mandate requests, status checks, and cancellations
 */

// Constants and Configurations
const NACH = {
    API_ENDPOINTS: {
        SEND_REQUEST: 'api/sendNachRequest',
        CHECK_STATUS: 'api/checkMandateStatus',
        CANCEL_MANDATE: 'api/cancelMandate',
        INSTALMENT_DATES: 'api/instalment-dates',
        RESEND_NACH: 'api/resendNachDetails',
        MANDATE_EXISTS: 'api/nachMandateExists'
    },

    STATUS_CLASSES: {
        SUCCESS: 'bg-light-success',
        WARNING: 'bg-light-warning',
        DANGER: 'bg-light-danger',
        INFO: 'bg-light-info'
    },

    TEXT_CLASSES: {
        SUCCESS: 'text-success',
        WARNING: 'text-warning',
        DANGER: 'text-danger',
        INFO: 'text-info'
    }
};

// Button State Manager
const ButtonManager = {
    buttons: {
        request: '#kt_button_nach',
        checkStatus: '#kt_button_check_status',
        cancel: '#kt_button_cancel_mandate',
        resend: '#kt_button_resend_nach',
        manual: '#kt_button_manual_upload'
    },

    states: {
        'Authorized': {
            request: false,
            checkStatus: true,
            cancel: false,
            resend: false,
            manual: false
        },
        'Authorization Request Rejected': {
            request: true,
            checkStatus: true,
            cancel: false,
            resend: false,
            manual: true
        },
        'Rejected By NPCI': {
            request: true,
            checkStatus: true,
            cancel: false,
            resend: false,
            manual: true,
        },
        'Progressed by User': {
            request: false,
            checkStatus: true,
            cancel: true,
            resend: false,
            manual: false
        },
        'Transaction Open Status': {
            request: false,
            checkStatus: true,
            cancel: true,
            resend: false,
            manual: false
        },
        'Initiated': {
            request: false,
            checkStatus: true,
            cancel: false,
            resend: true,
            manual: false
        },
        'Manual Authorized': {
        request: false,
        checkStatus: false,
        cancel: false,
        resend: false,
        manual: false
    }

    },

    updateStates(status) {
        const state = this.states[status] || {
            request: true,
            checkStatus: true,
            cancel: false,
            resend: false,
            manual: false
        };

        Object.entries(state).forEach(([button, enabled]) => {
            const $button = $(this.buttons[button]);
            if ($button.length) {
                if (enabled) {
                    $button.removeClass('d-none').prop('disabled', false);
                } else {
                    $button.addClass('d-none').prop('disabled', true);
                }
            }
        });
    },

    setLoading(buttonId, isLoading) {
        const $button = $(buttonId);
        if (isLoading) {
            $button.addClass('loading').prop('disabled', true);
        } else {
            // Only enable the button if it should be enabled based on current state
            const currentStatus = $('#currentMandateStatus').val();
            const buttonKey = Object.entries(this.buttons)
                .find(([_, value]) => value === buttonId)?.[0];
            const shouldBeEnabled = this.states[currentStatus]?.[buttonKey] ?? false;

            $button.removeClass('loading')
                .prop('disabled', !shouldBeEnabled);
        }
    }
};

const ModeManager = {
    init() {
    $('input[name="mandateMode"]').on('change', (e) => {
        const mode = e.target.value;
        console.log("Mode status -"+mode);
        const currentStatus = $('#currentMandateStatus').val();
        console.log("Mode manager console log is "+currentStatus);
        if (currentStatus !== 'Authorized') {
            this.updateUIForMode(mode);
            if (mode === 'MANUAL') {
                $('#kt_button_manual_upload').removeClass('d-none').prop('disabled', false);
            }
        }
    });
}
,

    updateUIForMode(mode) {
    const $digitalButtons = $('#kt_button_nach, #kt_button_check_status, #kt_button_resend_nach');
    const $manualButtons = $('#kt_button_manual_upload');
    const currentStatus = $('#currentMandateStatus').val();

    if (mode === 'DIGITAL') {
        $digitalButtons.removeClass('d-none');
        $manualButtons.addClass('d-none');
    } else if (mode === 'MANUAL') {
        $digitalButtons.addClass('d-none');
        $manualButtons.removeClass('d-none').prop('disabled', false);
    }

}

    ,

    getCurrentMode() {
        return $('input[name="mandateMode"]:checked').val() || 'DIGITAL';
    }
};


// Status Display Manager
const StatusManager = {
    getStatusClasses(status) {
        const statusMap = {
            'Authorized': {
                bg: NACH.STATUS_CLASSES.SUCCESS,
                text: NACH.TEXT_CLASSES.SUCCESS
            },
            'Manual Authorized': {
                bg: NACH.STATUS_CLASSES.SUCCESS,
                text: NACH.TEXT_CLASSES.SUCCESS
            },
            'Authorization Request Rejected': {
                bg: NACH.STATUS_CLASSES.DANGER,
                text: NACH.TEXT_CLASSES.DANGER
            },
            'Rejected By NPCI': {
                bg: NACH.STATUS_CLASSES.DANGER,
                text: NACH.TEXT_CLASSES.DANGER
            },
            'Progressed by User': {
                bg: NACH.STATUS_CLASSES.WARNING,
                text: NACH.TEXT_CLASSES.WARNING
            },
            'Transaction Open Status': {
                bg: NACH.STATUS_CLASSES.WARNING,
                text: NACH.TEXT_CLASSES.WARNING
            },
            'Initiated': {
                bg: NACH.STATUS_CLASSES.WARNING,
                text: NACH.TEXT_CLASSES.WARNING
            }
        };

        return statusMap[status] || {
            bg: NACH.STATUS_CLASSES.INFO,
            text: NACH.TEXT_CLASSES.INFO
        };
    },

    updateStatusDisplay(status) {
        const statusClasses = this.getStatusClasses(status);
        const statusHtml = `
            <div class="d-flex align-items-center ${statusClasses.bg} rounded p-5 mb-7">
                <i class="ki-duotone ki-abstract-26 ${statusClasses.text} fs-1 me-5">
                    <span class="path1"></span>
                    <span class="path2"></span>
                </i>
                <div class="flex-grow-1 me-2">
                    <a href="#" class="fw-bold text-gray-800 text-hover-primary fs-6">${status}</a>
                </div>
            </div>
        `;
        $('#mandateStatusRow td').html(statusHtml);
    }
};

// API Service Handler
const APIService = {
    async makeRequest(endpoint, data, method = 'POST') {
        try {
            const response = await $.ajax({
                url: endpoint,
                type: method,
                contentType: 'application/json',
                data: data ? JSON.stringify(data) : undefined,
                dataType: 'json'
            });
            return {success: true, data: response};
        } catch (error) {
            console.error(`API Error (${endpoint}):`, error);
            return {
                success: false,
                error: error.responseJSON?.message || error.statusText || 'Unknown error occurred'
            };
        }
    }
};

// NACH Operations Handler
const NACHOperations = {
    async sendNachRequest() {
        // Show stylish confirmation dialog
        const mode = ModeManager.getCurrentMode();
        if (mode === 'MANUAL') {
            await this.handleManualNach();
            return;
        }

        const result = await Swal.fire({
            title: 'Send NACH Mandate Request?',
            html: `
            <div class="text-start">
                <div class="d-flex align-items-center mb-3">
                    <i class="ki-duotone ki-message-text-2 fs-2 text-info me-2">
                        <span class="path1"></span>
                        <span class="path2"></span>
                        <span class="path3"></span>
                    </i>
                    <span>NACH mandate request will be sent to:</span>
                </div>
                <div class="bg-light rounded p-3 mb-3">
                    <div>
                        <span class="text-muted">Customer Email:</span>
                        <span class="fw-bold" id="customerEmail">${$('#emailId').val() || 'Customer Email'}</span>
                    </div>
                </div>

                <div class="alert alert-warning d-flex mt-3">
                    <i class="ki-duotone ki-timer fs-2 text-warning me-2">
                        <span class="path1"></span>
                        <span class="path2"></span>
                    </i>
                    <div>
                        <div class="fw-bold">Important Notes:</div>
                        <ul class="my-2 ps-3">
                            <li>The NACH mandate link has an expiry time</li>
                            <li>Customer should complete the process in one session</li>
                            <li>If interrupted, the link may become invalid</li>
                        </ul>
                    </div>
                </div>

                <div class="alert alert-info d-flex mt-3">
                    <i class="ki-duotone ki-information-5 fs-2 me-2">
                        <span class="path1"></span>
                        <span class="path2"></span>
                        <span class="path3"></span>
                    </i>
                    <div>
                        <div class="fw-bold">What to do if link expires?</div>
                        <div class="text-muted mt-1">
                            Use the <strong>'Cancel Mandate'</strong> button to invalidate the current request and initiate a new one.
                        </div>
                    </div>
                </div>
            </div>
        `,
            icon: 'info',
            showCancelButton: true,
            confirmButtonText: 'Yes, send request',
            cancelButtonText: 'No, cancel',
            customClass: {
                confirmButton: 'btn btn-primary',
                cancelButton: 'btn btn-active-light-primary'
            },
            buttonsStyling: false,
            reverseButtons: true
        });

        if (!result.isConfirmed) {
            return;
        }

        // Show processing state
        Swal.fire({
            title: 'Sending',
            html: `
            <div class="d-flex align-items-center justify-content-center">
                <i class="ki-duotone ki-message-sending fs-2x text-primary me-2">
                    <span class="path1"></span>
                    <span class="path2"></span>
                </i>
                <span>Sending NACH mandate request...</span>
            </div>
        `,
            allowOutsideClick: false,
            showConfirmButton: false,
            willOpen: () => {
                Swal.showLoading();
            }
        });
        ButtonManager.setLoading(ButtonManager.buttons.request, true);
        const slno = $('#slno').val();

        try {
            const response = await $.ajax({
                url: NACH.API_ENDPOINTS.SEND_REQUEST,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({slno}),
                dataType: 'json'
            });

            if (response.message === "Active mandate request is present") {
                await Swal.fire({
                    title: 'Active Request Exists',
                    html: `
                    <div class="text-start">
                        <div class="alert alert-warning d-flex align-items-center">
                            <i class="ki-duotone ki-information-5 fs-2 me-2">
                                <span class="path1"></span>
                                <span class="path2"></span>
                                <span class="path3"></span>
                            </i>
                            <span>An active NACH mandate request already exists.</span>
                        </div>
                        <div class="alert alert-info d-flex mt-3">
                            <i class="ki-duotone ki-notify fs-2 me-2">
                                <span class="path1"></span>
                                <span class="path2"></span>
                            </i>
                            <span>Click <strong>'Check Status'</strong> button to view the current status.</span>
                        </div>
                    </div>
                `,
                    icon: 'warning',
                    confirmButtonText: 'OK',
                    customClass: {
                        confirmButton: 'btn btn-primary'
                    },
                    buttonsStyling: false
                });
                ButtonManager.updateStates('Initiated');
                await checkMandateStatus();
            } else if (response.Response?.Status?.Code === "200" ||
                response.Response?.Status?.Code === "Initiated") {
                // Show success message
                await Swal.fire({
                    title: 'Success!',
                    html: `
        <div class="text-start">
            <div class="d-flex align-items-center mb-3">
                <i class="ki-duotone ki-check-circle fs-2x text-success me-2">
                    <span class="path1"></span>
                    <span class="path2"></span>
                </i>
                <span class="text-success fw-bold">NACH mandate request sent successfully</span>
            </div>
            <div class="bg-light rounded p-3 mb-3">
                <span class="text-muted">Sent to:</span>
                <span class="fw-bold">${$('#emailId').val() || 'Customer Email'}</span>
            </div>
            <div class="alert alert-info d-flex mt-3">
                <i class="ki-duotone ki-notification-bing fs-2 me-2">
                    <span class="path1"></span>
                    <span class="path2"></span>
                </i>
                <div>
                    <div class="fw-bold">Next Steps:</div>
                    <ul class="my-2 ps-3">
                        <li>Inform customer to check their email for NACH mandate link</li>
                        <li>Advise to complete the process without interruption</li>
                        <li>Click <strong>'Check Status'</strong> button to monitor the latest status</li>
                    </ul>
                </div>
            </div>
            <div class="alert alert-warning d-flex mt-3">
                <i class="ki-duotone ki-eye fs-2 me-2">
                    <span class="path1"></span>
                    <span class="path2"></span>
                    <span class="path3"></span>
                </i>
                <div>
                    <div class="fw-bold">Important:</div>
                    <div class="text-muted">
                        Please click <strong>'Check Status'</strong> after a few minutes to get the latest NACH status.
                    </div>
                </div>
            </div>
            <div class="alert alert-white d-flex mt-3">
                <i class="ki-duotone ki-message-text-2 fs-2 text-primary me-2">
                    <span class="path1"></span>
                    <span class="path2"></span>
                    <span class="path3"></span>
                </i>
                <div>
                    <div class="fw-bold text-primary">Need to resend the email?</div>
                    <div class="text-muted mt-1">
                        You can use the <strong>'Resend NACH Details'</strong> button once the status shows as <strong>'Initiated'</strong>.<br>
                        When using this option:
                        <ul class="mt-2">
                            <li>Both customer and <strong>albgenach@sib.co.in</strong> will receive the email</li>
                            <li>Forward from <strong>albgenach@sib.co.in</strong> if customer still doesn't receive it</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    `,
                    icon: 'success',
                    confirmButtonText: 'OK',
                    customClass: {
                        confirmButton: 'btn btn-primary',
                        htmlContainer: 'text-start' // Ensures all content is left-aligned
                    },
                    buttonsStyling: false,
                    width: '600px' // Makes the modal slightly wider to accommodate the content
                });

                ButtonManager.updateStates('Initiated');
            } else {
                console.error('Error in sendNachRequest:', error);
                const errorDetails = error.responseJSON||{};

                throw new Error(response.Response?.Status?.Desc || 'Unknown error occurred');
            }
        } catch (error) {
            console.error('Error in sendNachRequest:', error);
            await Swal.fire({
                title: 'Error',
                html: `
                <div class="text-start">
                    <div class="d-flex align-items-center mb-3">
                        <i class="ki-duotone ki-cross-circle fs-2x text-danger me-2">
                            <span class="path1"></span>
                            <span class="path2"></span>
                        </i>
                        <span class="text-danger fw-bold">Failed to send NACH mandate request</span>
                    </div>
                    <div class="text-muted small mt-2">
                        ${error.responseJSON?.message || error.message || 'An unexpected error occurred'}
                    </div>
                </div>
            `,
                icon: 'error',
                confirmButtonText: 'OK',
                customClass: {
                    confirmButton: 'btn btn-primary'
                },
                buttonsStyling: false
            });
        } finally {
            ButtonManager.setLoading(ButtonManager.buttons.request, false);
        }
    },


    async checkMandateStatus() {
        ButtonManager.setLoading(ButtonManager.buttons.checkStatus, true);
        const slno = $('#slno').val();

        try {
            const response = await APIService.makeRequest(NACH.API_ENDPOINTS.CHECK_STATUS, {slno});

            if (response.success && response.data.Response?.Body?.statusCheck?.message) {
                const message = response.data.Response.Body.statusCheck.message;
                const nachStatus = message.requestStatus;
                if (nachStatus !== 'Authorized') {
                    $('#manualMode').prop('disabled', false);
                }


                // Update status display
                StatusManager.updateStatusDisplay(nachStatus);

                // Store current status
                $('#currentMandateStatus').val(nachStatus);

                // Update button states
                ButtonManager.updateStates(nachStatus);

                // Get status-specific styling
                const statusStyle = getStatusStyle(nachStatus);

                // Show styled status information
                await Swal.fire({
                    title: 'NACH Mandate Status',
                    html: `
        <div class="text-start">
            <!-- Status Header -->
            <div class="d-flex align-items-center mb-3">
                <i class="ki-duotone ${statusStyle.icon} fs-2x ${statusStyle.textColor} me-2">
                    <span class="path1"></span>
                    <span class="path2"></span>
                </i>
                <div>
                    <div class="fs-3 fw-bold ${statusStyle.textColor}">
                        ${nachStatus}
                    </div>
                    <div class="text-muted fs-7">
                        ${statusStyle.description}
                    </div>
                </div>
            </div>

            <!-- Combined Details in Two Columns -->
            <div class="bg-light rounded p-3">
                <div class="row g-3">
                    <!-- Left Column -->
                    <div class="col-6">
                        <div class="mb-2">
                            <span class="text-muted fs-7">Mandate ID:</span>
                            <div class="fw-bold">${message.mndtId || 'N/A'}</div>
                        </div>
                        <div class="mb-2">
                            <span class="text-muted fs-7">Reference Number:</span>
                            <div class="fw-bold">${message.referenceNumber || 'N/A'}</div>
                        </div>
                        <div>
                            <span class="text-muted fs-7">Collection Amount:</span>
                            <div class="fw-bold">₹ ${message.colltnAmt || 'N/A'}</div>
                        </div>
                    </div>
                    <!-- Right Column -->
                    <div class="col-6">
                        <div class="mb-2">
                            <span class="text-muted fs-7">Collection Type:</span>
                            <div class="fw-bold">${message.amountTp || 'N/A'}</div>
                        </div>
                        <div class="mb-2">
                            <span class="text-muted fs-7">First Collection:</span>
                            <div class="fw-bold">${message.frstColltnDt || 'N/A'}</div>
                        </div>
                        <div>
                            <span class="text-muted fs-7">Final Collection:</span>
                            <div class="fw-bold">${message.fnlColltnDt || 'N/A'}</div>
                        </div>
                    </div>
                </div>
            </div>

            ${getStatusSpecificContent(nachStatus, message)}
        </div>
    `,
                    icon: statusStyle.icon === 'ki-check' ? 'success' :
                        statusStyle.icon === 'ki-cross' ? 'error' : 'info',
                    confirmButtonText: 'OK',
                    customClass: {
                        confirmButton: 'btn btn-primary'
                    },
                    buttonsStyling: false,
                    width: '550px' // Reduced width
                });

            } else {
                throw new Error('Invalid response format');
            }
        } catch (error) {
            console.error('Error checking mandate status:', error);
            await Swal.fire({
                title: 'Error',
                html: `
                <div class="text-start">
                    <div class="d-flex align-items-center mb-3">
                        <i class="ki-duotone ki-cross-circle fs-2x text-danger me-2">
                            <span class="path1"></span>
                            <span class="path2"></span>
                        </i>
                        <span class="text-danger fw-bold">Failed to fetch mandate status</span>
                    </div>
                    <div class="text-muted small mt-2">
                        ${error.responseJSON?.message || error.message || 'An unexpected error occurred'}
                    </div>
                </div>
            `,
                icon: 'error',
                confirmButtonText: 'OK',
                customClass: {
                    confirmButton: 'btn btn-primary'
                },
                buttonsStyling: false
            });
        } finally {
            ButtonManager.setLoading(ButtonManager.buttons.checkStatus, false);
        }
    },

    async cancelMandate() {
        // Show stylish confirmation dialog
        const result = await Swal.fire({
            title: 'Cancel NACH Mandate?',
            html: `
            <div class="text-start">
                <div class="d-flex align-items-center mb-3">
                    <i class="ki-duotone ki-information-5 fs-2 text-warning me-2">
                        <span class="path1"></span>
                        <span class="path2"></span>
                        <span class="path3"></span>
                    </i>
                    <span>Are you sure you want to cancel this NACH mandate?</span>
                </div>
                <div class="text-muted small">
                    Note: This action will:
                    <ul class="my-2">
                        <li>Cancel the current mandate request</li>
                        <li>Initiate a new mandate request automatically</li>
                    </ul>
                </div>
            </div>
        `,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Yes, cancel mandate',
            cancelButtonText: 'No, keep mandate',
            customClass: {
                confirmButton: 'btn btn-danger',
                cancelButton: 'btn btn-active-light-primary'
            },
            buttonsStyling: false,
            reverseButtons: true
        });

        if (!result.isConfirmed) {
            return;
        }

        // Show processing state
        Swal.fire({
            title: 'Processing',
            html: 'Cancelling mandate request...',
            allowOutsideClick: false,
            showConfirmButton: false,
            willOpen: () => {
                Swal.showLoading();
            }
        });
        ButtonManager.setLoading(ButtonManager.buttons.cancel, true);

        const slno = $('#slno').val();

        try {
            const response = await APIService.makeRequest(NACH.API_ENDPOINTS.CANCEL_MANDATE, {slno});

            if (response.success) {
                const forceInitiate = response.data.Response?.Body?.forceIntiate;
                if (forceInitiate) {
                    if (forceInitiate.status === "200") {
                        // Show success message
                        await Swal.fire({
                            title: 'Success!',
                            html: `
                            <div class="text-start">
                                <div class="d-flex align-items-center mb-3">
                                    <i class="ki-duotone ki-check-circle fs-2x text-success me-2">
                                        <span class="path1"></span>
                                        <span class="path2"></span>
                                    </i>
                                    <span class="text-success fw-bold">Previous mandate cancelled successfully</span>
                                </div>
                                <div class="d-flex align-items-center mb-3">
                                    <i class="ki-duotone ki-notification-bing fs-2x text-info me-2">
                                        <span class="path1"></span>
                                        <span class="path2"></span>
                                    </i>
                                    <span class="text-info">New mandate request initiated</span>
                                </div>
                                <div class="text-muted small mt-3">
                                    <strong>Reference:</strong> ${forceInitiate.reference}<br>
                                    <strong>Additional Info:</strong> ${forceInitiate.moreInfo || 'N/A'}
                                </div>
                            </div>
                        `,
                            icon: 'success',
                            confirmButtonText: 'OK',
                            customClass: {
                                confirmButton: 'btn btn-primary'
                            },
                            buttonsStyling: false
                        });

                        // Update the status and buttons
                        await this.checkMandateStatus();
                        var nachStatus = $('#currentMandateStatus').val();
                        ButtonManager.updateStates(nachStatus);
                    } else {
                        // Show error message
                        await Swal.fire({
                            title: 'Cancellation Failed',
                            html: `
                            <div class="text-start">
                                <div class="text-danger mb-3">Unable to process the cancellation request</div>
                                <div class="text-muted small">
                                    <p>Status: ${forceInitiate.status}</p>
                                    <p>Message: ${forceInitiate.message || 'No additional information available'}</p>
                                </div>
                            </div>
                        `,
                            icon: 'error',
                            confirmButtonText: 'OK',
                            customClass: {
                                confirmButton: 'btn btn-primary'
                            },
                            buttonsStyling: false
                        });
                    }
                } else {
                    await Swal.fire({
                        title: 'Status Unclear',
                        html: `
                        <div class="text-start">
                            <div class="text-warning mb-3">
                                <strong>Unable to process the response</strong>
                            </div>
                            <div class="text-muted">
                                Please check the mandate status to confirm the current state.
                            </div>
                        </div>
                    `,
                        icon: 'warning',
                        confirmButtonText: 'Check Status',
                        showCancelButton: true,
                        cancelButtonText: 'Close',
                        customClass: {
                            confirmButton: 'btn btn-primary',
                            cancelButton: 'btn btn-light'
                        },
                        buttonsStyling: false
                    });
                }
            } else {
                throw new Error(response.error || 'Failed to cancel mandate');
            }
        } catch (error) {
            console.error('Error in cancelMandate:', error);
            await Swal.fire({
                title: 'Error',
                html: `
                <div class="text-start">
                    <div class="text-danger mb-3">
                        <strong>Error cancelling mandate</strong>
                    </div>
                    <div class="text-muted">
                        ${error.message || 'An unexpected error occurred'}
                    </div>
                </div>
            `,
                icon: 'error',
                confirmButtonText: 'OK',
                customClass: {
                    confirmButton: 'btn btn-primary'
                },
                buttonsStyling: false
            });
        } finally {
            ButtonManager.setLoading(ButtonManager.buttons.cancel, false);
        }
    },
    async resendNachDetails() {
        // Show stylish confirmation dialog
        const result = await Swal.fire({
            title: 'Resend NACH Details?',
            html: `
            <div class="text-start">
                <div class="d-flex align-items-center mb-3">
                    <i class="ki-duotone ki-message-text-2 fs-2 text-info me-2">
                        <span class="path1"></span>
                        <span class="path2"></span>
                        <span class="path3"></span>
                    </i>
                    <span>NACH details will be sent to:</span>
                </div>
                <div class="bg-light rounded p-3 mb-3">
                    <div class="mb-2">
                        <span class="text-muted">To:</span>
                        <span class="fw-bold" id="customerEmail">${$('#emailId').val() || 'Customer Email'}</span>
                    </div>
                    <div>
                        <span class="text-muted">CC:</span>
                        <span class="fw-bold">albgenach@sib.co.in</span>
                    </div>
                </div>
                <div class="text-muted small">
                    The email will include:
                    <ul class="my-2">
                        <li>NACH mandate details</li>
                        <li>Link to complete the mandate</li>
                    </ul>
                </div>
                <div class="alert alert-info d-flex align-items-center mt-3 mb-0">
                    <i class="ki-duotone ki-information-5 fs-2 me-2">
                        <span class="path1"></span>
                        <span class="path2"></span>
                        <span class="path3"></span>
                    </i>
                    <div class="text-muted small">
                        Note: If customer doesn't receive the email, you can forward it from <strong>albgenach@sib.co.in</strong>
                    </div>
                </div>
            </div>
        `,
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Yes, resend details',
            cancelButtonText: 'No, cancel',
            customClass: {
                confirmButton: 'btn btn-primary',
                cancelButton: 'btn btn-active-light-primary'
            },
            buttonsStyling: false,
            reverseButtons: true
        });

        if (!result.isConfirmed) {
            return;
        }

        // Show processing state
        Swal.fire({
            title: 'Sending',
            html: `
            <div class="d-flex align-items-center justify-content-center">
                <i class="ki-duotone ki-message-sending fs-2x text-primary me-2">
                    <span class="path1"></span>
                    <span class="path2"></span>
                </i>
                <span>Sending NACH details...</span>
            </div>
        `,
            allowOutsideClick: false,
            showConfirmButton: false,
            willOpen: () => {
                Swal.showLoading();
            }
        });

        ButtonManager.setLoading(ButtonManager.buttons.resend, true);
        const slno = $('#slno').val();

        try {
            const response = await $.ajax({
                url: NACH.API_ENDPOINTS.RESEND_NACH,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({slno}),
                dataType: 'json'
            });

            if (response.status === 'success') {
                // Show success message
                await Swal.fire({
                    title: 'Sent Successfully!',
                    html: `
                    <div class="text-start">
                        <div class="d-flex align-items-center mb-3">
                            <i class="ki-duotone ki-check-circle fs-2x text-success me-2">
                                <span class="path1"></span>
                                <span class="path2"></span>
                            </i>
                            <span class="text-success fw-bold">NACH details have been sent successfully</span>
                        </div>
                        <div class="bg-light rounded p-3 mb-3">
                            <div class="mb-2">
                                <span class="text-muted">Sent to:</span>
                                <span class="fw-bold">${$('#emailId').val() || 'Customer Email'}</span>
                            </div>
                            <div>
                                <span class="text-muted">CC:</span>
                                <span class="fw-bold">albgenach@sib.co.in</span>
                            </div>
                        </div>
                        <div class="text-muted small">
                            <i class="ki-duotone ki-information-5 text-info me-1">
                                <span class="path1"></span>
                                <span class="path2"></span>
                                <span class="path3"></span>
                            </i>
                            Please inform the customer to check their email and complete the mandate process.
                        </div>
                    </div>
                `,
                    icon: 'success',
                    confirmButtonText: 'OK',
                    customClass: {
                        confirmButton: 'btn btn-primary'
                    },
                    buttonsStyling: false
                });
            } else {
                throw new Error(response.message || 'Failed to send NACH details');
            }
        } catch (error) {
            console.error('Error in resendNachDetails:', error);
            await Swal.fire({
                title: 'Error',
                html: `
                <div class="text-start">
                    <div class="d-flex align-items-center mb-3">
                        <i class="ki-duotone ki-cross-circle fs-2x text-danger me-2">
                            <span class="path1"></span>
                            <span class="path2"></span>
                        </i>
                        <span class="text-danger ">Failed to send NACH details</span>
                    </div>
                    <div class="fw-bold small mt-2">
                        ${error.message || error.statusText || 'An unexpected error occurred'}
                    </div>
                    
                </div>
            `,
                icon: 'error',
                confirmButtonText: 'OK',
                customClass: {
                    confirmButton: 'btn btn-primary'
                },
                buttonsStyling: false
            });
        } finally {
            ButtonManager.setLoading(ButtonManager.buttons.resend, false);
        }
    },
    async fetchInstalmentDates() {
        const slno = $('#slno').val();

        const response = await APIService.makeRequest(NACH.API_ENDPOINTS.INSTALMENT_DATES, {slno});

        if (response.success) {
            const {data} = response;
            $('#inststartdate').html(data.instalmentStartDate);
            $('#instenddate').html(data.instalmentEndDate);

            let doubleEmi = 0;
            if (data.emi) {
                const emiValue = parseFloat(data.emi);
                if (!isNaN(emiValue)) {
                    doubleEmi = (emiValue * 2).toFixed(2);
                }
            }

            $('#nachemi').html(doubleEmi);
            $('#nachtenor').html(data.tenor);
            $('#hidac_open_flg').val(data.ac_open_flg);

            if (data.ac_open_flg === "") {
                alertmsg("Account Opening is not complete!");
            }

            ButtonManager.updateStates($('#currentMandateStatus').val());
        } else {
            alertmsg('Error fetching instalment dates: ' + response.error);
        }
    },
    async handleManualNach() {
        console.log('Manual NACH button clicked'); // Debug log 1
    const slno = $('#slno').val();
    console.log('SLNO value:', slno); // Debug log 2

    try {
        console.log('Making API call to createManualNach'); // Debug log 3
        const response = await $.ajax({
            url: 'api/createManualNach',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                slno,
                mode: 'MANUAL'
            })
        });
        console.log('API Response:', response); // Debug log 4

        if (response.success) {
            $('#currentMandateStatus').val('Manual_Saved');
            await Swal.fire({
                title: 'Manual NACH Completed',
                html: `
                    <div class="text-start">
                        <div class="alert alert-info d-flex align-items-center">
                            <i class="ki-duotone ki-information-5 fs-2 me-2">
                                <span class="path1"></span>
                                <span class="path2"></span>
                                <span class="path3"></span>
                            </i>
                            <div>
                                Manual NACH mandate has been initiated. Please follow these steps:
                                <ol class="mt-2">
                                    <li>Download and print the NACH mandate form</li>
                                    <li>Get it signed by the customer</li>
                                    <li>Submit the form to the respective department</li>
                                </ol>
                            </div>
                        </div>
                    </div>
                `,
                icon: 'success',
                confirmButtonText: 'OK',
                customClass: {
                    confirmButton: 'btn btn-primary'
                }
            });

            // Update button states after manual NACH
            ButtonManager.updateStates('Manual Authorized');
            StatusManager.updateStatusDisplay('Manual Authorized');
            $('#currentMandateStatus').val('Manual Authorized');
        }
    } catch (error) {
        console.error('Error creating manual NACH:', error);
        await Swal.fire({
            title: 'Error',
            text: 'Failed to create manual NACH mandate',
            icon: 'error',
            confirmButtonText: 'OK'
        });
    }
}

};

function getStatusStyle(status) {
    const styles = {
        'Authorized': {
            icon: 'ki-check',
            textColor: 'text-success',
            description: 'NACH mandate has been successfully authorized'
        },
        'Manual Authorized': {
            icon: 'ki-check',
            textColor: 'text-success',
            description: 'NACH mandate has been manually authorized'
        },
        'Authorization Request Rejected': {
            icon: 'ki-cross',
            textColor: 'text-danger',
            description: 'The NACH mandate request was rejected'
        },
        'Rejected By NPCI': {
            icon: 'ki-cross',
            textColor: 'text-danger',
            description: 'The mandate was rejected by NPCI'
        },
        'Progressed by User': {
            icon: 'ki-notification-bing',
            textColor: 'text-warning',
            description: 'Customer has initiated the mandate process'
        },
        'Transaction Open Status': {
            icon: 'ki-notification-bing',
            textColor: 'text-warning',
            description: 'Customer has initiated the mandate process,not completed'
        },
        'Initiated': {
            icon: 'ki-abstract-26',
            textColor: 'text-primary',
            description: 'NACH mandate request has been initiated'
        }
    };

    return styles[status] || {
        icon: 'ki-information',
        textColor: 'text-info',
        description: 'Current status of the NACH mandate'
    };
}

// Helper function to get status-specific content
function getStatusSpecificContent(status, message) {
    switch (status) {
        case 'Authorization Request Rejected':
        case 'Rejected By NPCI':
            return `
                <div class="alert alert-danger d-flex mt-3 py-2">
                    <i class="ki-duotone ki-information-5 fs-2 me-2">
                        <span class="path1"></span>
                        <span class="path2"></span>
                        <span class="path3"></span>
                    </i>
                    <div>
                        <div class="fw-bold">Rejection Details:</div>
                        <div class="mt-1">
                            <strong>Reason:</strong> ${message.reasonDesc || 'N/A'}<br>
                            <strong>Action:</strong> Use 'Send NACH Request' to initiate new mandate
                        </div>
                    </div>
                </div>
            `;
        case 'Initiated':
            return `
                <div class="alert alert-info d-flex mt-3 py-2">
                    <i class="ki-duotone ki-notification-bing fs-2 me-2">
                        <span class="path1"></span>
                        <span class="path2"></span>
                    </i>
                    <div>
                        <div class="fw-bold mb-1">Action Required:</div>
                        • Check email delivery status<br>
                        • Use 'Resend NACH Details' if needed<br>
                        • Monitor for customer's action
                    </div>
                </div>
            `;
        case 'Progressed by User':
            return `
                <div class="alert alert-warning d-flex mt-3 py-2">
                    <i class="ki-duotone ki-timer fs-2 me-2">
                        <span class="path1"></span>
                        <span class="path2"></span>
                    </i>
                    <div>
                        <span class="fw-bold">Session in Progress</span>
                        <div class="mt-1">For interrupted sessions:</div>
                        • Confirm customer is not actively using the link<br>
                        • Use 'Cancel Mandate' to reset and auto-initiate new request
                        <div class="bg-light-warning rounded p-1 mt-2 small">
                            <i class="ki-duotone ki-warning-2 text-warning me-1">
                                <span class="path1"></span>
                                <span class="path2"></span>
                            </i>
                            Do not cancel during active customer sessions
                        </div>
                    </div>
                </div>
            `;
        case 'Authorized':
            return `
                <div class="alert alert-success d-flex mt-3 py-2">
                    <i class="ki-duotone ki-check-circle fs-2 me-2">
                        <span class="path1"></span>
                        <span class="path2"></span>
                    </i>
                    <div>
                        <span class="fw-bold">Successfully Completed</span>
                        <div class="mt-1">NACH mandate is now active</div>
                    </div>
                </div>
            `;
        default:
            return '';
    }
}


// Initialize everything when document is ready
$(document).ready(function () {
    ModeManager.init();
    const initialMode = ModeManager.getCurrentMode();
    console.log("Initial mode for the nach mandate is - "+initialMode);
    const $manualButton = $('#kt_button_manual_upload');
    console.log('Manual button exists:', $manualButton.length > 0);
    console.log('Manual button disabled:', $manualButton.prop('disabled'));
    console.log('Manual button classes:', $manualButton.attr('class'));

    // Setup event listeners
    $('#nachtableDetails').on('click', NACHOperations.fetchInstalmentDates);
    $('#kt_button_nach').on('click', () => NACHOperations.sendNachRequest());
    $('#kt_button_check_status').on('click', () => NACHOperations.checkMandateStatus());
    $('#kt_button_cancel_mandate').on('click', () => NACHOperations.cancelMandate());
    $('#kt_button_resend_nach').on('click', () => NACHOperations.resendNachDetails());
    $('#kt_button_manual_upload').on('click', () => NACHOperations.handleManualNach());

    // Initialize tooltips
    $('[data-bs-toggle="tooltip"]').tooltip();

    // Initial setup
    const currentStatus = $('#currentMandateStatus').val();
    if (currentStatus) {
        if (currentStatus !== 'Authorized') {
            $('#manualMode').prop('disabled', false);
        }
        ButtonManager.updateStates(currentStatus);
        StatusManager.updateStatusDisplay(currentStatus);
    }

    if (initialMode === 'MANUAL') {
        $('input[name="mandateMode"]').prop('disabled', true);
    }

});
