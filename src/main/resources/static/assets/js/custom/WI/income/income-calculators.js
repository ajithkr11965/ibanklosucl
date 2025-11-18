function calculateRemittances(detElement) {
    const monthlySalary = parseFloat(detElement.find('.monthly-salary-nr, [name="MonthSalary"]').val()) || 0;
    let totalRemittanceSum = 0;
    let netRemittanceSum = 0;
    let validMonthCount = 0;
    // Process each remittance row
    detElement.find('.remittance-row').each(function () {
        const row = $(this);
        const totalRemittance = parseFloat(row.find('.total-remittance').val()) || 0;
        // Calculate Bulk Remittance
        let bulkRemittance = 0;
        if (totalRemittance > monthlySalary) {
            bulkRemittance = totalRemittance - monthlySalary;
        }
        // Calculate Net Remittance
        const netRemittance = totalRemittance - bulkRemittance;
        // Update the row
        row.find('.bulk-remittance').val(bulkRemittance.toFixed(2));
        row.find('.net-remittance').val(netRemittance.toFixed(2));
        // Add to sums for average calculation
        if (totalRemittance > 0) {
            totalRemittanceSum += totalRemittance;
            netRemittanceSum += netRemittance;
            validMonthCount++;
        }
    });
    // Calculate averages (divide by 12 for all months, not just valid ones)
    const avgTotalRemittance = totalRemittanceSum / 12;
    const avgNetRemittance = netRemittanceSum / 12;
    // Update average fields (using both class and name selectors for compatibility)
    detElement.find('.avg-total-remittance, [name="Avgtotal_remittance"]').val(avgTotalRemittance.toFixed(2));
    detElement.find('.avg-net-remittance, [name="Avgnet_remittance"]').val(avgNetRemittance.toFixed(2));
    // Monthly Gross Income = Average of Net Remittance
    const monthlyGrossIncome = avgNetRemittance;
    // Update Monthly Gross Income display
    detElement.find('.calculated-monthly-gross-income').text(formatCurrency(monthlyGrossIncome));
    detElement.find('.monthly-gross-income-nr-hidden').val(monthlyGrossIncome.toFixed(2));
    // Highlight if all 12 months are not filled
    if (validMonthCount < 12) {
        detElement.find('.remittance-grid-header .badge')
            .removeClass('bg-primary')
            .addClass('bg-warning')
            .text('Incomplete (' + validMonthCount + '/12)');
    } else {
        detElement.find('.remittance-grid-header .badge')
            .removeClass('bg-warning')
            .addClass('bg-success')
            .text('Complete (12/12)');
    }
}

function calculateFinalAMI(element) {
    var form = element.closest('.field-row').parent();
    var monthlyGrossField = form.find('.itr-monthly-gross, [name="itrMonthlyGross"], [name="pensionerMonthlyGross"], [name="sepSenpMonthlyGross"], [name="agriculturistMonthlyGross"], .form16-monthly-income, .avg-monthly-income');

    if (monthlyGrossField.length === 0) {
        // Try to find in previous sibling
        monthlyGrossField = element.closest('.field-row').prev().find('.itr-monthly-gross, [name="itrMonthlyGross"], [name="pensionerMonthlyGross"], [name="sepSenpMonthlyGross"], [name="agriculturistMonthlyGross"], .form16-monthly-income, .avg-monthly-income');
    }

    if (monthlyGrossField.length === 0) {
        console.error("Monthly gross income field not found");
        return;
    }

    // Get the monthly gross income value
    var monthlyGrossIncome = parseFloat(monthlyGrossField.val().replace(/,/g, '')) || 0;
    var addBacksObligations = parseFloat(element.val()) || 0;

    // Calculate final AMI
    var finalAMI = monthlyGrossIncome + addBacksObligations;

    // Find and update the final AMI field
    var finalAMIField = form.find('.final-ami');
    if (finalAMIField.length === 0) {
        // Try to find in next sibling
        finalAMIField = element.closest('.field-row').next().find('.final-ami');
    }

    if (finalAMIField.length > 0) {
        finalAMIField.val(finalAMI.toFixed(2));
    }
}

function calculateTotalIncome(triggerElement) {
    var tableBody = (triggerElement.is('tbody')) ?
        triggerElement :
        triggerElement.closest('.salaried-section').find('.payslip-table-body');
    var total = 0;

    tableBody.find('.payslip-amount').each(function () {
        var amount = parseFloat($(this).val()) || 0;
        total += amount;
    });

    // Update average monthly income (based on number of payslips)
    var rowCount = tableBody.find('tr').length;
    var avgMonthly = rowCount > 0 ? (total / rowCount).toFixed(2) : 0;

    triggerElement.closest('.salaried-section').find('.total-income').val(total.toFixed(2));
    triggerElement.closest('.salaried-section').find('.avg-monthly-income').val(avgMonthly);
}
function calculateImputedIncome(triggerElement) {
    var detElement = triggerElement.closest('.det');
    var tabPane = detElement.closest('.tab-pane');
    var generalDetails = tabPane.find('.generaldetails');
    var kycDetails = tabPane.find('.kycdetails');

    // Get applicant details
    var applicantId = generalDetails.find('.appid').val();
    var wiNum = detElement.find('.wiNum').val();
    var panNo = kycDetails.find('.pan').val();
    var customerId = generalDetails.find('.custID').val();
    var cifId = generalDetails.find('.cifId').val();

    // Validate required fields
    if (!panNo || panNo === "") {
        Swal.fire({
            icon: 'error',
            title: 'PAN Required',
            text: 'PAN number is required for imputed income calculation.',
            confirmButtonText: 'OK'
        });
        return false;
    }

    // Prepare request payload
    var jsonBody = {
        applicantId: applicantId,
        wiNum: wiNum,
        panNo: panNo,
        customerId: customerId,
        cifId: cifId
    };

    console.log("Calculating imputed income for:", jsonBody);

    // Show loading section
    detElement.find('.no-imputed-message').hide();
    detElement.find('.imputed-result-section').hide();
    detElement.find('.imputed-loading-section').show();

    // AJAX call to calculate imputed income
    $.ajax({
        url: "api/calculateImputedIncome",  // DUMMY CONTROLLER URL
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(jsonBody),
        success: function (response) {
            console.log("Imputed income calculated successfully:", response);

            // Hide loading
            detElement.find('.imputed-loading-section').hide();
             var dummyResponse = generateDummyImputedIncomeResponse();

            // Update UI with calculated values
            updateImputedIncomeDisplay(dummyResponse, detElement);

            // Show result section
            detElement.find('.imputed-result-section').show();

            // Show success message
            Swal.fire({
                icon: 'success',
                title: 'Calculation Complete',
                text: 'Imputed income has been calculated successfully.',
                timer: 2000,
                showConfirmButton: false
            });
        },
        error: function (xhr, status, error) {
             detElement.find('.imputed-loading-section').hide();
             var dummyResponse = generateDummyImputedIncomeResponse();
              updateImputedIncomeDisplay(dummyResponse, detElement);
               detElement.find('.imputed-result-section').show();
            // Hide loading
            // detElement.find('.imputed-loading-section').hide();
            // detElement.find('.no-imputed-message').show();
            //
            // console.error("Error calculating imputed income:", xhr.responseText, status, error);
            //
            // Swal.fire({
            //     icon: 'error',
            //     title: 'Calculation Failed',
            //     text: xhr.responseText || 'Failed to calculate imputed income. Please try again.',
            //     confirmButtonText: 'OK'
            // });
        }
    });
}
function generateDummyImputedIncomeResponse() {
    // Generate random values for demo
    var baseIncome = Math.floor(Math.random() * 30000) + 20000; // 20k-50k
    var cibilScore = Math.floor(Math.random() * 200) + 650; // 650-850
    var scorecardRating = Math.floor(Math.random() * 30) + 70; // 70-100

    var cibilAdjustment = Math.floor(baseIncome * 0.15);
    var scorecardFactor = Math.floor(baseIncome * 0.10);
    var riskAdjustment = Math.floor(baseIncome * -0.05);

    var imputedIncome = baseIncome + cibilAdjustment + scorecardFactor + riskAdjustment;

    return {
        imputedIncome: imputedIncome,
        cibilScore: cibilScore,
        scorecardRating: scorecardRating,
        confidenceLevel: Math.floor(Math.random() * 20) + 80, // 80-100%
        breakdown: {
            baseIncome: baseIncome,
            cibilAdjustment: cibilAdjustment,
            scorecardFactor: scorecardFactor,
            riskAdjustment: riskAdjustment
        },
        calculationDate: new Date().toISOString(),
        status: 'SUCCESS'
    };
}
// Coverage after all currently valid statements
function calcCoverageSoFar() {
    const covered = new Set();
    statementsData.forEach(s => {
        if (!s.startDate || !s.endDate) return;
        const [sy, sm] = s.startDate.split('-').map(Number);
        const [ey, em] = s.endDate.split('-').map(Number);
        let cur = new Date(sy, sm - 1, 1);
        const end = new Date(ey, em - 1, 1);
        while (cur <= end) {
            const yy = cur.getFullYear();
            let mm = cur.getMonth() + 1;
            if (mm < 10) mm = '0' + mm;
            covered.add(`${yy}-${mm}`);
            cur.setMonth(cur.getMonth() + 1);
        }
    });
    return covered.size;
}
// Return inclusive month difference between YYYY-MM strings

function monthDiffInclusive(start, end) {
    // e.g. "2023-01" to "2023-01" => 1 month
    //      "2023-01" to "2023-02" => 2 months
    const [sy, sm] = start.split('-').map(Number);
    const [ey, em] = end.split('-').map(Number);
    return (ey - sy) * 12 + (em - sm) + 1;
}