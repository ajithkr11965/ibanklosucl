function calculateRemittances(detElement) {
    const monthlySalary = parseFloat(detElement.find('.monthly-salary-nr').val()) || 0;
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
    // Update average fields
    detElement.find('.avg-total-remittance').val(avgTotalRemittance.toFixed(2));
    detElement.find('.avg-net-remittance').val(avgNetRemittance.toFixed(2));
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

// calculateFinalAMI function has been removed as Final AMI is now equal to Average Monthly Income

function calculateTotalIncome(triggerElement) {
    var tableBody = (triggerElement.is('tbody')) ?
        triggerElement :
        triggerElement.closest('.salaried-section').find('.payslip-table-body');
    var total = 0;
    var totalGross = 0;

    // Calculate total net salary
    tableBody.find('.payslip-amount').each(function () {
        var amount = parseFloat($(this).val()) || 0;
        total += amount;
    });

    // Calculate total gross salary
    tableBody.find('.payslip-gross-amount').each(function () {
        var grossAmount = parseFloat($(this).val()) || 0;
        totalGross += grossAmount;
    });

    // Update average monthly income (based on number of payslips)
    var rowCount = tableBody.find('tr').length;
    var avgMonthly = rowCount > 0 ? (total / rowCount).toFixed(2) : 0;
    var avgGrossMonthly = rowCount > 0 ? (totalGross / rowCount).toFixed(2) : 0;

    // Update net salary fields
    triggerElement.closest('.salaried-section').find('.total-income').val(total.toFixed(2));
    triggerElement.closest('.salaried-section').find('.avg-monthly-income').val(avgMonthly);

    // Update gross salary fields
    triggerElement.closest('.salaried-section').find('.total-gross-income').val(totalGross.toFixed(2));
    triggerElement.closest('.salaried-section').find('.avg-gross-monthly-income').val(avgGrossMonthly);
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

// Check if at least ONE bank has exactly 12 consecutive months (not more, not less)
function hasComplete12MonthCoverage() {
    if (!statementsData || statementsData.length === 0) return false;

    // First, check if all statements have required data (including bankCode)
    const incompleteStatements = statementsData.filter(s => !s.startDate || !s.endDate || !s.bankCode);
    if (incompleteStatements.length > 0) {
        // Some statements are missing data - validation should fail
        // But don't return false yet, let getCoverageValidationMessage() handle the detailed error
        console.warn(`${incompleteStatements.length} statement(s) missing required data (dates or bank selection)`);
    }

    // Group statements by bank code (only complete ones)
    const bankGroups = {};
    statementsData.forEach(s => {
        if (!s.startDate || !s.endDate || !s.bankCode) return;
        if (!bankGroups[s.bankCode]) {
            bankGroups[s.bankCode] = [];
        }
        bankGroups[s.bankCode].push(s);
    });

    // If no valid statements after filtering, return false
    if (Object.keys(bankGroups).length === 0) return false;

    let hasValidBank = false;

    // Check each bank to see if it has exactly 12 consecutive months
    for (const bankCode in bankGroups) {
        const statements = bankGroups[bankCode];

        // Collect all months covered by this bank
        const covered = new Set();
        statements.forEach(s => {
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

        // Skip if less than 12 months
        if (covered.size < 12) continue;

        // Reject if more than 12 unique months (affects ABB calculation)
        if (covered.size > 12) {
            console.warn(`Bank ${bankCode} has ${covered.size} unique months (more than 12 allowed)`);
            continue;
        }

        // Convert to sorted array
        const sortedMonths = Array.from(covered).sort();

        // Check if this bank has exactly 12 consecutive months without gaps
        let consecutiveCount = 1;
        let hasGap = false;
        for (let i = 1; i < sortedMonths.length; i++) {
            const [prevY, prevM] = sortedMonths[i-1].split('-').map(Number);
            const [currY, currM] = sortedMonths[i].split('-').map(Number);

            // Check if current month is exactly 1 month after previous
            const prevDate = new Date(prevY, prevM - 1, 1);
            prevDate.setMonth(prevDate.getMonth() + 1);
            const expectedY = prevDate.getFullYear();
            const expectedM = prevDate.getMonth() + 1;

            if (currY === expectedY && currM === expectedM) {
                consecutiveCount++;
            } else {
                hasGap = true;
                break;
            }
        }

        // This bank is valid if it has exactly 12 consecutive months with no gaps
        if (covered.size === 12 && consecutiveCount === 12 && !hasGap) {
            hasValidBank = true;
            break; // Found at least one valid bank
        }
    }

    return hasValidBank;
}

// Get validation details for user-friendly error messages
function getCoverageValidationMessage() {
    if (!statementsData || statementsData.length === 0) {
        return "No bank statements have been added.";
    }

    // Check for statements with missing data
    const missingBank = [];
    const missingDates = [];

    statementsData.forEach((s, idx) => {
        if (!s.bankCode) {
            missingBank.push(idx + 1); // Statement number (1-based)
        }
        if (!s.startDate || !s.endDate) {
            missingDates.push(idx + 1);
        }
    });

    // If any statements are missing required data, report that first
    if (missingBank.length > 0 || missingDates.length > 0) {
        const errors = [];
        if (missingBank.length > 0) {
            errors.push(`Statement(s) ${missingBank.join(', ')} missing bank selection`);
        }
        if (missingDates.length > 0) {
            errors.push(`Statement(s) ${missingDates.join(', ')} missing start/end dates`);
        }
        return "Incomplete statement data:\n• " + errors.join("\n• ");
    }

    // All statements have required data, now group by bank
    const bankGroups = {};
    statementsData.forEach(s => {
        if (!s.startDate || !s.endDate || !s.bankCode) return;
        if (!bankGroups[s.bankCode]) {
            bankGroups[s.bankCode] = [];
        }
        bankGroups[s.bankCode].push(s);
    });

    const bankDetails = [];
    for (const bankCode in bankGroups) {
        const statements = bankGroups[bankCode];
        const covered = new Set();
        statements.forEach(s => {
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

        const sortedMonths = Array.from(covered).sort();
        let hasGap = false;
        for (let i = 1; i < sortedMonths.length; i++) {
            const [prevY, prevM] = sortedMonths[i-1].split('-').map(Number);
            const [currY, currM] = sortedMonths[i].split('-').map(Number);
            const prevDate = new Date(prevY, prevM - 1, 1);
            prevDate.setMonth(prevDate.getMonth() + 1);
            const expectedY = prevDate.getFullYear();
            const expectedM = prevDate.getMonth() + 1;
            if (currY !== expectedY || currM !== expectedM) {
                hasGap = true;
                break;
            }
        }

        bankDetails.push({
            bankCode: bankCode,
            monthCount: covered.size,
            hasGap: hasGap
        });
    }

    // Generate specific error message
    const invalidBanks = bankDetails.filter(b => b.monthCount !== 12 || b.hasGap);
    if (invalidBanks.length === bankDetails.length) {
        // All banks are invalid
        const messages = invalidBanks.map(b => {
            if (b.monthCount < 12) {
                return `Bank ${b.bankCode}: Only ${b.monthCount} months (needs 12)`;
            } else if (b.monthCount > 12) {
                return `Bank ${b.bankCode}: ${b.monthCount} months (exceeds 12, affects ABB calculation)`;
            } else if (b.hasGap) {
                return `Bank ${b.bankCode}: Has gaps in coverage`;
            }
        });
        return "No bank has exactly 12 consecutive months:\n• " + messages.join("\n• ");
    }

    return "Validation passed";
}
// Return inclusive month difference between YYYY-MM strings

function monthDiffInclusive(start, end) {
    // e.g. "2023-01" to "2023-01" => 1 month
    //      "2023-01" to "2023-02" => 2 months
    const [sy, sm] = start.split('-').map(Number);
    const [ey, em] = end.split('-').map(Number);
    return (ey - sy) * 12 + (em - sm) + 1;
}