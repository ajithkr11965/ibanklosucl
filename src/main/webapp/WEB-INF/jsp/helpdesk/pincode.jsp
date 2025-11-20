<%--
  Created by IntelliJ IDEA.
  User: SIBL18202
  Date: 27-11-2024
  Time: 19:33
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="los" uri="http://www.siblos.com/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- Main Form Section -->
<style>
    .spinner {
        display: none;
        width: 50px;
        height: 50px;
        margin: 20px auto;
        border: 5px solid #f3f3f3;
        border-top: 5px solid #3498db;
        border-radius: 50%;
        animation: spin 1s linear infinite;
    }
    @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
    }
    .error-message {
        display: none;
        color: #dc3545;
        margin-top: 5px;
    }

    .success-message {
        display: none;
        color: #dc3545;
        margin-top: 5px;
    }
    .select2-container {
        width: 100% !important;
    }
    .select2-selection--single {
        height: 38px !important;
        padding: 5px !important;
    }
    .select2-selection__arrow {
        height: 36px !important;
    }
</style>
<div class="card mb-4">
    <div class="card-header">
        <h5 class="mb-0"><i class="bi bi-pin-angle-fill me-2"></i>Add PIN Code</h5>
    </div>
    <div class="card-body">
<div class="container mt-4">
    <form id="pincodeForm">
        <div id="select2-container">
            <div class="row g-3">
                <!-- State Dropdown -->
                <div class="col-md-6">
                    <div class="form-group">

                    <label for="stateDropdown">State</label>
                <select id="stateDropdown" class="form-control select2">
                    <option value="">Select State</option>
                <option value="AN">Andaman & Nicobar</option>
                <option value="AP">Andhra Pradesh</option>
                <option value="AR">Arunachal Pradesh</option>
                <option value="AS">Assam</option>
                <option value="BR">Bihar</option>
                <option value="CG">Chhattisgarh</option>
                <option value="CH">Chandigarh</option>
                <option value="DD">Dadra and Nagar Haveli and Daman and Diu</option>
                <option value="DL">Delhi</option>
                <option value="GA">Goa</option>
                <option value="GJ">Gujarat</option>
                <option value="HR">Haryana</option>
                <option value="HP">Himachal Pradesh</option>
                <option value="JK">Jammu and Kashmir</option>
                <option value="JH">Jharkhand</option>
                <option value="KA">Karnataka</option>
                <option value="KL">Kerala</option>
                <option value="LA">Ladakh</option>
                <option value="LD">Lakshadweep</option>
                <option value="ML">Meghalaya</option>
                <option value="MN">Manipur</option>
                <option value="MH">Maharshtra</option>
                <option value="MP">Madhya Pradesh</option>
                <option value="MZ">Mizoram</option>
                <option value="NL">Nagaland</option>
                <option value="OR">Odisha</option>
                <option value="PB">Punjab</option>
                <option value="PY">Pondicherry</option>
                <option value="RJ">Rajasthan</option>
                <option value="SK">Sikkim</option>
                <option value="TN">Tamil Nadu</option>
                <option value="TS">Telangana</option>
                <option value="TR">Tripura</option>
                <option value="UA">Uttarakhand</option>
                <option value="UP">Uttar Pradesh</option>
                <option value="WB">West Bengal</option>
                </select>
                <div class="error-message" id="stateError"></div>
                    </div>
                </div>


                <!-- City Dropdown -->
                <div class="col-md-6">
                    <div class="form-group">

                    <label for="cityDropdown">City</label>
                <select id="cityDropdown" class="form-control select2" disabled>
                    <option value="">Select State First</option>
                </select>
                <div class="error-message" id="cityError"></div>
                    </div>

                </div>


                <!-- Pincode Section -->
                <div class="col-12">
                    <div class="form-group">

                <label for="newPincode">Pincode</label>
                <input type="text" id="newPincode" class="form-control"
                       placeholder="Enter a 6-digit pincode" maxlength="6">
                <div class="error-message" id="pincodeError"></div>
                    </div>
                    <div class="success-message" id="success"></div>

                </div>
            </div>


            <!-- Pincode Details Section -->
            <div id="pincodeDetails" class="mt-4"></div>

            <!-- Buttons -->
            <div class="mt-4">
                <button type="submit" id="submitButton" class="btn btn-primary">Submit</button>
                <button type="button" id="resetButton" class="btn btn-secondary">Reset</button>
            </div>
        </div>
    </form>

    <!-- Spinner -->
    <div class="spinner"></div>
</div>
    </div></div>
<script>
    $(document).ready(function() {
        initializeSelect2();
        setupEventListeners();
    });

    function initializeSelect2() {
        try {
            $('#stateDropdown').select2({
                width: '100%',
                placeholder: 'Select State',
                allowClear: true,
                dropdownParent: $('#select2-container')
            });

            $('#cityDropdown').select2({
                width: '100%',
                placeholder: 'Select City',
                allowClear: true,
                dropdownParent: $('#select2-container')
            });

            $('#cityDropdown').prop('disabled', true);
        } catch (error) {
            console.error('Select2 initialization failed:', error);
        }
    }

    function setupEventListeners() {
        // State change event
        $('#stateDropdown').on('change', function() {
            var stateCode = $(this).val();
            $('#cityDropdown').empty().append('<option value="">Select City</option>');
            $('#pincodeDetails').empty();
            $('#newPincodeSection').hide();

            if (stateCode) {
                updateCities(stateCode);
            } else {
                $('#cityDropdown').prop('disabled', true).trigger('change');
            }
        });

        // City change event
        $('#cityDropdown').on('change', function() {
            var cityCode = $(this).val();
            if (cityCode) {
                var stateCode = $('#stateDropdown').val();
                loadPincodeDetails(stateCode, cityCode);
            }
        });

        // Pincode input validation
        $('#newPincode').on('change', function() {
            var pincode = $(this).val().replace(/[^0-9]/g, '');
            $(this).val(pincode);

            if (pincode.length === 6) {
                hideError('pincodeError');
            } else if (pincode.length > 0) {
                showError('pincodeError', 'Please enter a valid 6-digit pincode');
            }
        });

        // Form submission
        $('#pincodeForm').on('submit', function(event) {
            event.preventDefault();
            handleFormSubmit();
        });

        // Reset button
        $('#resetButton').on('click', resetForm);
    }

    function updateCities(stateCode) {
        showSpinner();
        $.ajax({
            url: 'api/pincodes/' + stateCode, // Matches the @GetMapping endpoint
            type: 'GET',
            success: function (response) {
                if (response && response.success) {
                    var cityDropdown = $('#cityDropdown');
                    cityDropdown.empty().append('<option value="">Select City</option>');

                    if (response.data && response.data.length > 0) {
                        // Populate the city dropdown with Finacle City Name and Code
                        $.each(response.data, function (index, city) {
                            cityDropdown.append('<option value="' + city.finacleCityCode + '">'
                                + city.finacleCity + '</option>');
                        });
                        cityDropdown.prop('disabled', false);
                    } else {
                        showError('cityError', 'No cities found for the selected state.');
                    }
                } else {
                    showError('cityError', response.message || 'Failed to load cities.');
                }
            },
            error: function () {
                showError('cityError', 'Error fetching cities. Please try again.');
            },
            complete: hideSpinner
        });
    }


    function loadPincodeDetails(stateCode, cityCode) {
        showSpinner();
        $.ajax({
            url: 'api/pincodes/' + stateCode + '/' + cityCode,
            type: 'GET',
            success: function(response) {
                if (response && response.success) {
                    renderPincodeDetails(response.data);
                } else {
                    showError('pincodeError', response.message || 'Failed to load pincode details');
                }
            },
            error: function() {
                showError('pincodeError', 'Error loading pincode details');
            },
            complete: hideSpinner
        });
    }

    function renderPincodeDetails(data) {
        var details = $('#pincodeDetails').empty();
        if (!data || data.length === 0) {
            details.html('<div class="alert alert-info text-center">No pincode details available</div>');
            return;
        }

        // Create a Bootstrap row container
        var row = $('<div class="row g-3"></div>');
        details.append(
            '<div class="card">' +
            '<div class="card-header text-center bg-primary text-white">Pincode Details</div>' +
            '<div class="card-body"></div>' +
            '</div>'
        );
        details.find('.card-body').append(row);

        // Populate the row with pincode details
        $.each(data, function(index, entry) {
            row.append(
                '<div class="col-md-4">' +
                '<div class="card pincode-card">' +
                '<div class="card-body">' +
                '<h5 class="card-title">' + entry.district + '</h5>' +
                '<p class="card-text"><strong>Pincode:</strong> ' + entry.pincode + '</p>' +
                '<div class="form-check">' +
                '<input class="form-check-input" type="radio" name="pincodeEntry" value="' + entry.id + '" id="pincode-' + entry.id + '">' +
                '<label class="form-check-label" for="pincode-' + entry.id + '">Select</label>' +
                '</div>' +
                '</div>' +
                '</div>' +
                '</div>'
            );
        });

        // Add click handler for the entire card
        $('.pincode-card').on('click', function(e) {
            if (!$(e.target).is(':radio')) {
                $(this).find('input[type="radio"]').prop('checked', true).trigger('change');
            }
        });
    }


    function handleFormSubmit() {
        var formData = {
            stateCode: $('#stateDropdown').val(),
            cityCode: $('#cityDropdown').val(),
            pincode: $('#newPincode').val()
        };

        if (!validateForm(formData)) {
            return;
        }

        submitData(formData);
    }

    function validateForm(formData) {
        if (!formData.stateCode) {
            showError('stateError', 'Please select a state');
            return false;
        }
        if (!formData.cityCode) {
            showError('cityError', 'Please select a city');
            return false;
        }
        if (!/^[1-9][0-9]{5}$/.test(formData.pincode)) {
            showError('pincodeError', 'Please enter a valid 6-digit pincode');
            return false;
        }
        return true;
    }

    function submitData(formData) {
        $('#submitButton').prop('disabled', true);
        showSpinner();

        $.ajax({
            url: 'api/pincode/create',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function(response) {
                if (response && response.success) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Pincode mapping created successfully!',
                        icon: 'success',
                        confirmButtonText: 'OK'
                    })
                    resetForm();
                } else {
                    showError('pincodeError', response.message || 'Failed to create pincode mapping');
                }
            },
            error: function() {
                showError('pincodeError', 'Error creating pincode mapping');
            },
            complete: function() {
                $('#submitButton').prop('disabled', false);
                hideSpinner();
            }
        });
    }

    function resetForm() {
        $('#stateDropdown').val('').trigger('change');
        $('#cityDropdown').val('').prop('disabled', true).trigger('change');
        $('#newPincode').val('');
        $('#pincodeDetails').empty();
        $('.error-message').hide();
    }

    function showSpinner() {
        $('.spinner').show();
    }

    function hideSpinner() {
        $('.spinner').hide();
    }

    function showError(elementId, message) {
        $('#' + elementId).text(message).show();
    }

    function hideError(elementId) {
        $('#' + elementId).hide();
    }

</script>
