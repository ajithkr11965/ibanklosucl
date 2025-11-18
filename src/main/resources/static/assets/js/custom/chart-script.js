$(document).ready(function () {

    setInterval(function(){
        location.reload();
    },45000);
    // Parse initial data from hidden input
    let initialData = {};
    try {
        initialData = JSON.parse($('#initialData').val());
    } catch (e) {
        console.error('Failed to parse initial data:', e);
        initialData = null;
    }

    $('#backbtn').on('click',function(){
        window.location.href='dashboard';
    });

    // Initialize variables
    let monthlyStatsChart, queueProcessingChart, loanStatusChart, loanPerformanceChart;
    let drillDownChart;

    // Start the initialization process
    initializeDashboard(initialData);

    // Function to initialize the dashboard
    function initializeDashboard(initialData) {
        initializePlugins();
        initializeEventHandlers();
        populateQueueOptions();

        // If initial data is available, use it to render charts and KPIs
        if (initialData) {
            updateKPIs(initialData.kpis);
            updateCharts(initialData.chartData);
        }
    }

    // Function to initialize third-party plugins
    function initializePlugins() {
        // Initialize Flatpickr for Date Range
        const initialStartDate = '2020-01-01';
        const initialEndDate = new Date().toISOString().split('T')[0];

        flatpickr("#date-range", {
            mode: "range",
            dateFormat: "Y-m-d",
            altInput: true,
            altFormat: "F j, Y",
            defaultDate: [initialStartDate, initialEndDate],
            onChange: function() {
                applyFilters();
            }
        });

        // Initialize noUiSlider for Loan Amount Range
        initializeLoanAmountSlider();
    }

    // Function to initialize the loan amount slider
    function initializeLoanAmountSlider() {
        let loanSlider = document.getElementById('loan-amount-slider');
        noUiSlider.create(loanSlider, {
            start: [1000, 1000000],
            connect: true,
            tooltips: [wNumb({ decimals: 0, thousand: ',' }), wNumb({ decimals: 0, thousand: ',' })],
            range: { 'min': 0, 'max': 2000000 }
        });

        loanSlider.noUiSlider.on('update', function (values) {
            $('#min-loan-amount').text('₹' + values[0]);
            $('#max-loan-amount').text('₹' + values[1]);
        });

        // Apply filters when loan amount slider value changes
        loanSlider.noUiSlider.on('change', function () {
            applyFilters();
        });
    }

    // Function to initialize event handlers
    function initializeEventHandlers() {
        // Aggregation option change handler
        $('input[name="aggregation"]').change(function () {
            // Remove 'active' class from all buttons
            $('input[name="aggregation"]').parent().removeClass('active');
            // Add 'active' class to the parent label of the checked input
            $(this).parent().addClass('active');
            applyFilters();
        });

        // Queue checkbox change handler
        $('#queue-checkboxes').on('change', 'input[type="checkbox"]', function () {
            applyFilters();
        });

        // Reset Filters button click event
        $('#reset-filters').click(function () {
            resetFilters();
        });

        // Export Data button click event
        $('#export-data').click(function () {
            exportData();
        });

        // Error message close button handler
        $('#error-message .close').click(function () {
            hideError();
        });
    }

    // Function to populate queue options
    function populateQueueOptions() {
        $.ajax({
            url: 'api/queue-options',
            method: 'GET',
            success: function (queues) {
                const queueContainer = $('#queue-checkboxes');
                queueContainer.empty();
                queues.forEach(function (queue) {
                    const codeValue = queue.CODEVALUE;
                    const codeDesc = queue.CODEDESC;
                    queueContainer.append(`
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" value="${codeValue}" id="queue-${codeValue}" checked>
                            <label class="form-check-label" for="queue-${codeValue}">${codeDesc}</label>
                        </div>
                    `);
                });
            },
            error: function () {
                showError('Failed to load queues.');
            }
        });
    }

    // Function to apply filters
    function applyFilters() {
        const dateRange = $('#date-range').val();
        const loanAmountRange = document.getElementById('loan-amount-slider').noUiSlider.get().map(Number); // Convert to numbers
        const aggregation = $('input[name="aggregation"]:checked').val();
        const queues = $('#queue-checkboxes input:checked').map(function () {
            return this.value;
        }).get();

        if (!validateFilters(dateRange, queues)) {
            return;
        }

        const filters = {
            dateRange: dateRange,
            loanAmountRange: loanAmountRange,
            aggregation: aggregation,
            queues: queues
        };

        // Show loading spinner
        $('#loading-spinner').show();

        $.ajax({
            url: 'api/filtered-data',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(filters),
            success: function (data) {
                updateKPIs(data.kpis);
                updateCharts(data.chartData);
            },
            error: function () {
                showError('Failed to apply filters.');
            },
            complete: function () {
                $('#loading-spinner').hide();
            }
        });
    }

    // Function to reset filters
    function resetFilters() {
        $('#filterForm')[0].reset();
        document.getElementById('loan-amount-slider').noUiSlider.reset();
        // Check all queue checkboxes
        $('#queue-checkboxes input[type="checkbox"]').prop('checked', true);
        // Reset aggregation to default (e.g., 'monthly')
        $('input[name="aggregation"]').prop('checked', false).parent().removeClass('active');
       // $('input[name="aggregation"][value="monthly"]').prop('checked', true).parent().addClass('active');
        initializeDashboard(initialData);
        //applyFilters();
    }

    // Function to export data
    function exportData() {
        const dateRange = $('#date-range').val();
        const loanAmountRange = document.getElementById('loan-amount-slider').noUiSlider.get().map(Number); // Convert to numbers
        const aggregation = $('input[name="aggregation"]:checked').val();
        const queues = $('#queue-checkboxes input:checked').map(function () {
            return this.value;
        }).get();

        const filters = {
            dateRange: dateRange,
            loanAmountRange: loanAmountRange,
            aggregation: aggregation,
            queues: queues
        };

        // Show loading spinner
        $('#loading-spinner').show();

        $.ajax({
            url: 'api/export-data',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(filters),
            xhrFields: {
                responseType: 'blob'
            },
            success: function (data, status, xhr) {
                const disposition = xhr.getResponseHeader('Content-Disposition');
                let filename = 'exported_data.csv';
                if (disposition && disposition.indexOf('filename=') !== -1) {
                    const matches = /filename="?([^"]+)"?/.exec(disposition);
                    if (matches != null && matches[1]) filename = matches[1];
                }
                const blob = new Blob([data], { type: 'text/csv;charset=utf-8;' });
                if (navigator.msSaveBlob) {
                    navigator.msSaveBlob(blob, filename);
                } else {
                    const link = document.createElement('a');
                    const url = URL.createObjectURL(blob);
                    link.href = url;
                    link.setAttribute('download', filename);
                    document.body.appendChild(link);
                    link.click();
                    document.body.removeChild(link);
                }
            },
            error: function () {
                showError('Failed to export data.');
            },
            complete: function () {
                $('#loading-spinner').hide();
            }
        });
    }

    // Function to update KPIs
    function updateKPIs(kpis) {
        if (!kpis) {
            showError('No KPI data available.');
            return;
        }
        $('#total-loans').text(kpis.totalLoans || 0);
        $('#total-loan-amount').text('₹' + ((kpis.totalLoanAmount || 0).toFixed(2)));
        $('#avg-loan-amount').text('₹' + ((kpis.avgLoanAmount || 0).toFixed(2)));
        $('#avg-tat').text(((kpis.avgTAT || 0).toFixed(2)));

        // Update new KPI cards
        $('#sanctioned-loans').text(kpis.sanctionedLoans || 0);
        $('#disbursed-loans').text(kpis.disbursedLoans || 0);
    }

    // Function to initialize or update charts
    function updateCharts(chartData) {
        if (!chartData) {
            showError('No chart data available.');
            return;
        }

        // Update Monthly Stats Chart
        if (chartData.monthlyStats) {
            if (!monthlyStatsChart) {
                monthlyStatsChart = new ApexCharts(document.querySelector("#monthly-stats-chart"), getMonthlyStatsOptions(chartData.monthlyStats));
                monthlyStatsChart.render();
            } else {
                monthlyStatsChart.updateOptions(getMonthlyStatsOptions(chartData.monthlyStats));
            }
        }

        // Update Queue Processing Chart
        if (chartData.queueProcessing) {
            if (!queueProcessingChart) {
                queueProcessingChart = new ApexCharts(document.querySelector("#queue-processing-chart"), getQueueProcessingOptions(chartData.queueProcessing));
                queueProcessingChart.render();
            } else {
                queueProcessingChart.updateOptions(getQueueProcessingOptions(chartData.queueProcessing));
            }
        }

        // Update Loan Status Chart
        if (chartData.loanStatus) {
            if (!loanStatusChart) {
                loanStatusChart = new ApexCharts(document.querySelector("#loan-status-chart"), getLoanStatusOptions(chartData.loanStatus));
                loanStatusChart.render();
            } else {
                loanStatusChart.updateOptions(getLoanStatusOptions(chartData.loanStatus));
            }
        }

        // Update Loan Performance Chart
        if (chartData.loanPerformance) {
            if (!loanPerformanceChart) {
                loanPerformanceChart = new ApexCharts(document.querySelector("#loan-performance-chart"), getLoanPerformanceOptions(chartData.loanPerformance));
                loanPerformanceChart.render();
            } else {
                loanPerformanceChart.updateOptions(getLoanPerformanceOptions(chartData.loanPerformance));
            }
        }
    }

    // Chart Options Functions
    function getMonthlyStatsOptions(data) {
        return {
            chart: {
                type: 'bar',
                height: 350,
                stacked: false,
                toolbar: { show: true },
                events: {
                    dataPointSelection: function(event, chartContext, config) {
                        onChartDataPointClick('monthlyStats', config);
                    }
                }
            },
            colors: ['#241c80', '#17a2b8', '#28a745', '#ffc107', '#dc3545'],
            series: Array.isArray(data.series) ? data.series.map(item => ({
                name: item.name,
                data: item.data
            })) : [],
            xaxis: { categories: Array.isArray(data.categories) ? data.categories : [] },
            yaxis: { title: { text: 'Values' } },
            tooltip: { y: { formatter: val => val } },
            noData: { text: 'No data available' }
        };
    }

    function getQueueProcessingOptions(data) {
        return {
            chart: {
                type: 'bar',
                height: 350,
                toolbar: { show: true },
                events: {
                    dataPointSelection: function(event, chartContext, config) {
                        onChartDataPointClick('queueProcessing', config);
                    }
                }
            },
            colors: ['#241c80'],
            series: Array.isArray(data.series) ? data.series.map(item => ({
                name: item.name,
                data: item.data
            })) : [],
            xaxis: { categories: Array.isArray(data.categories) ? data.categories : [] },
            yaxis: { title: { text: 'Processing Time (Hours)' } },
            tooltip: { y: { formatter: val => val + ' hours' } },
            noData: { text: 'No data available' }
        };
    }

    function getLoanStatusOptions(data) {
        // Clean labels: replace nulls with 'Unknown'
        const labels = Array.isArray(data.labels)
            ? data.labels.map(label => (label == null ? 'Unknown' : label))
            : [];

        // Ensure that series is an array of numbers
        const series = (data && data.series && data.series[0] && Array.isArray(data.series[0].data))
            ? data.series[0].data
            : [];

        // Ensure labels and series have the same length
        if (series.length !== labels.length) {
            console.error('Series and labels length mismatch in loanStatus data.');
            console.log('Series length:', series.length);
            console.log('Labels length:', labels.length);
        }

        return {
            chart: {
                type: 'pie',
                height: 350,
                events: {
                    dataPointSelection: function(event, chartContext, config) {
                        onChartDataPointClick('loanStatus', config);
                    }
                }
            },
            colors: ['#241c80', '#17a2b8', '#28a745', '#ffc107', '#dc3545'],
            series: series,
            labels: labels,
            tooltip: { y: { formatter: val => val } },
            legend: { position: 'bottom' },
            noData: { text: 'No data available' }
        };
    }

    function getLoanPerformanceOptions(data) {
        return {
            chart: {
                type: 'line',
                height: 350,
                toolbar: { show: true },
                events: {
                    dataPointSelection: function(event, chartContext, config) {
                        onChartDataPointClick('loanPerformance', config);
                    }
                }
            },
            colors: ['#241c80', '#17a2b8'],
            series: Array.isArray(data.series) ? data.series.map(item => ({
                name: item.name,
                data: item.data
            })) : [],
            xaxis: { categories: Array.isArray(data.categories) ? data.categories : [], title: { text: 'Month' } },
            yaxis: { title: { text: 'Amount (Million)' } },
            tooltip: { y: { formatter: val => val + 'M' } },
            legend: { position: 'top' },
            noData: { text: 'No data available' }
        };
    }

    // Function to handle data point clicks for drill-down
    function onChartDataPointClick(chartType, config) {
        const dataPointIndex = config.dataPointIndex;
        const seriesIndex = config.seriesIndex;

        let drillDownParams = {};

        switch (chartType) {
            case 'monthlyStats':
                const category = config.w.config.xaxis.categories[dataPointIndex];
                const seriesName = config.w.config.series[seriesIndex].name;
                drillDownParams = {
                    chartType: 'monthlyStats',
                    category: category,
                    seriesName: seriesName
                };
                break;
            // Add cases for other chart types if needed
            default:
                console.warn('Drill-down not implemented for chart type:', chartType);
                return;
        }

        // Show loading spinner in the modal
        $('#drill-down-chart').html('<div class="text-center"><div class="spinner-border text-primary" role="status"></div></div>');

        // Open the modal
        $('#drillDownModal').modal('show');

        // Fetch detailed data based on the selected data point
        fetchDrillDownData(drillDownParams);
    }

    // Function to fetch drill-down data from the server
    function fetchDrillDownData(params) {
        $.ajax({
            url: 'api/drill-down-data',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(params),
            success: function(data) {
                renderDrillDownChart(data);
            },
            error: function() {
                $('#drill-down-chart').html('<p class="text-danger">Failed to load data.</p>');
            }
        });
    }

    // Function to render the drill-down chart
    function renderDrillDownChart(data) {
        // If the chart already exists, destroy it
        if (drillDownChart) {
            drillDownChart.destroy();
        }

        // Define chart options based on the data
        const options = {
            chart: {
                type: 'bar', // Adjust based on your data
                height: 350,
                toolbar: { show: true }
            },
            colors: ['#241c80'],
            series: data.series,
            xaxis: { categories: data.categories },
            yaxis: { title: { text: data.yAxisTitle } },
            title: { text: data.title },
            noData: { text: 'No data available' }
        };

        // Create the chart
        drillDownChart = new ApexCharts(document.querySelector("#drill-down-chart"), options);
        drillDownChart.render();
    }

    // Function to close the drill-down modal
    window.closeDrillDownModal = function() {
        if (drillDownChart) {
            drillDownChart.destroy();
            drillDownChart = null;
        }
        $('#drillDownModal').modal('hide');
    };

    // Show Error Message with Close Button
    function showError(message) {
        $('#error-text').text(message);
        $('#error-message').show();
    }

    // Hide Error Message
    function hideError() {
        $('#error-message').hide();
    }

    // Validation Function for Filters
    function validateFilters(dateRange, queues) {
        if (!dateRange) {
            showError('Please select a date range.');
            return false;
        }
        if (queues.length === 0) {
            showError('Please select at least one queue.');
            return false;
        }
        hideError();
        return true;
    }
});