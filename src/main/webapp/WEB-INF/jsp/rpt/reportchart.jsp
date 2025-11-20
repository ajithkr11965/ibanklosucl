<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Combined Vehicle Loan Dashboard</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/noUiSlider/15.7.0/nouislider.min.css">
  <script src="https://cdn.jsdelivr.net/npm/apexcharts"></script>
  <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/noUiSlider/15.7.0/nouislider.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/wnumb/1.2.0/wNumb.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
  <style>
    body {
      font-family: 'Poppins', sans-serif;
      background-color: #f4f7fa;
      padding-top: 20px;
    }
    .container-fluid {
      max-width: 1400px;
    }
    h1, h3, h4 {
      color: #333;
    }
    .chart-container {
      background-color: white;
      padding: 20px;
      margin-bottom: 20px;
      border-radius: 8px;
      box-shadow: 0 0 15px rgba(0,0,0,0.1);
    }
    .kpi-card {
      background-color: #fff;
      border: none;
      border-radius: 8px;
      margin-bottom: 15px;
      text-align: center;
      padding: 15px;
      box-shadow: 0 1px 3px rgba(0,0,0,0.1);
    }
    .kpi-card h5 {
      font-size: 14px;
      color: #6c757d;
    }
    .kpi-card .count-up {
      font-size: 28px;
      font-weight: bold;
    }
    #filter-container {
      background-color: #fff;
      padding: 10px 15px;
      border-radius: 8px;
      margin-bottom: 15px;
    }
    .filter-label {
      margin-right: 10px;
      font-weight: bold;
    }
    .slider-label {
      font-weight: bold;
      margin-bottom: 5px;
    }
    .slider-value {
      font-weight: bold;
    }
    .button-group {
      margin-top: 10px;
    }
    .export-button {
      margin-right: 5px;
    }
  </style>
</head>
<body>
  <div class="container-fluid">
    <h1 class="text-center mb-4">Combined Vehicle Loan Dashboard</h1>

    <!-- KPIs -->
    <div class="row mb-4">
      <div class="col-md-3">
        <div class="kpi-card">
          <h5>Total Number of Items</h5>
          <div id="total-items" class="count-up">0</div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="kpi-card">
          <h5>Total Loan Amount</h5>
          <div id="total-loan-amount" class="count-up">Rs 0</div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="kpi-card">
          <h5>Average Loan Amount</h5>
          <div id="average-loan-amount" class="count-up">Rs 0</div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="kpi-card">
          <h5>Average TAT (Minutes)</h5>
          <div id="average-tat" class="count-up">0</div>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div id="filter-container" class="mb-4">
      <div class="row align-items-center">
        <div class="col-md-2 col-sm-6 mb-2">
          <label class="filter-label">Aggregate By:</label>
          <div>
            <div class="form-check form-check-inline">
              <input class="form-check-input" type="radio" name="aggregationPeriod" id="aggregateDaily" value="daily">
              <label class="form-check-label" for="aggregateDaily">Daily</label>
            </div>
            <div class="form-check form-check-inline">
              <input class="form-check-input" type="radio" name="aggregationPeriod" id="aggregateMonthly" value="monthly" checked>
              <label class="form-check-label" for="aggregateMonthly">Monthly</label>
            </div>
            <div class="form-check form-check-inline">
              <input class="form-check-input" type="radio" name="aggregationPeriod" id="aggregateYearly" value="yearly">
              <label class="form-check-label" for="aggregateYearly">Yearly</label>
            </div>
          </div>
        </div>
        <div class="col-md-3 col-sm-6 mb-2">
          <label class="filter-label">Date Range:</label>
          <input type="text" id="date-range" class="form-control">
        </div>
        <div class="col-md-3 col-sm-6 mb-2">
          <label class="filter-label">Loan Amount:</label>
          <div id="loan-amount-slider"></div>
          <div class="d-flex justify-content-between">
            <span id="min-loan-amount" class="slider-value">$0</span>
            <span id="max-loan-amount" class="slider-value">$0</span>
          </div>
        </div>
        <div class="col-md-2 col-sm-6 mb-2">
          <label class="filter-label">Queue:</label>
          <select id="queue-filter" class="form-select" multiple></select>
        </div>
        <div class="col-md-2 col-sm-6 mb-2">
          <label class="filter-label">SOL_ID:</label>
          <select id="solid-filter" class="form-select" multiple></select>
        </div>
      </div>
      <div class="button-group text-end">
        <button id="export-data" class="btn btn-primary export-button">Export Data</button>
        <button id="export-chart" class="btn btn-secondary export-button">Download Charts</button>
      </div>
    </div>

    <div class="row">
      <!-- Chart 1: Number of Items Over Time -->
      <div class="col-md-6">
        <div class="chart-container">
          <div id="chart1"></div>
        </div>
      </div>
      <!-- Chart 2: Total Loan Amount Over Time -->
      <div class="col-md-6">
        <div class="chart-container">
          <div id="chart2"></div>
        </div>
      </div>
    </div>

    <div class="row">
      <!-- Chart 3: Average TAT by Queue -->
      <div class="col-md-6">
        <div class="chart-container">
          <div id="chart3"></div>
        </div>
      </div>
      <!-- Chart 4: TAT Distribution by Queue -->
      <div class="col-md-6">
        <div class="chart-container">
          <div id="chart4"></div>
        </div>
      </div>
    </div>

    <div class="row">
      <!-- Chart 5: TAT Trends Over Time -->
      <div class="col-md-6">
        <div class="chart-container">
          <div id="chart5"></div>
        </div>
      </div>
      <!-- Chart 6: Queue-Level TAT Comparison -->
      <div class="col-md-6">
        <div class="chart-container">
          <div id="chart6"></div>
        </div>
      </div>
    </div>

    <div class="row">
      <!-- Chart 7: Queue Handover Efficiency (Sankey Chart) -->
      <div class="col-md-6">
        <div class="chart-container">
          <div id="sankey_chart"></div>
        </div>
      </div>
      <!-- Chart 8: Loan Amount vs TAT -->
      <div class="col-md-6">
        <div class="chart-container">
          <div id="chart8"></div>
        </div>
      </div>
    </div>

    <!-- New Chart 9: TAT vs Number of Items Heatmap -->
    <div class="row">
      <div class="col-md-12">
        <div class="chart-container">
          <div id="chart9"></div>
        </div>
      </div>
    </div>

    <!-- New Chart 10: Queue Performance Comparison -->
    <div class="row">
      <div class="col-md-12">
        <div class="chart-container">
          <div id="chart10"></div>
        </div>
      </div>
    </div>

  </div>

  <!-- Data Modal -->
  <div class="modal fade" id="dataModal" tabindex="-1" aria-labelledby="dataModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-scrollable">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Detailed Data</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <table id="data-table" class="table table-striped">
            <thead>
              <tr>
                <th>QUEUE</th>
                <th>QUEUE_DATE</th>
                <th>SOL_ID</th>
                <th>QUEUE_NAME</th>
                <th>BR_NAME</th>
                <th>LOAN_AMT</th>
                <th>TAT</th>
              </tr>
            </thead>
            <tbody>
              <!-- Data populated dynamically -->
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>

  <script>
    // Declare chart variables in the global scope
    var chart1, chart2, chart3, chart4, chart5, chart6, chart8, chart9, chart10;

    $(document).ready(function() {
      // Sample Data (combined from both files)
      var originalData = [
        { QUEUE: 'BM', QUEUE_DATE: '2022-01-15 10:30:00', SOL_ID: '001', QUEUE_NAME: 'Branch Maker', BR_NAME: 'Branch A', LOAN_AMT: 500000, TAT: 133.45 },
        { QUEUE: 'BC', QUEUE_DATE: '2022-02-20 14:45:00', SOL_ID: '002', QUEUE_NAME: 'Branch Checker', BR_NAME: 'Branch B', LOAN_AMT: 1500000, TAT: 31.43 },
        { QUEUE: 'CS', QUEUE_DATE: '2022-03-05 09:15:00', SOL_ID: '003', QUEUE_NAME: 'Credit Sanctioner', BR_NAME: 'Branch C', LOAN_AMT: 250000, TAT: 211.67 },
        { QUEUE: 'BD', QUEUE_DATE: '2023-04-10 11:20:00', SOL_ID: '004', QUEUE_NAME: 'Branch Disbursal', BR_NAME: 'Branch D', LOAN_AMT: 750000, TAT: 8479.27 },
        { QUEUE: 'ACOPN', QUEUE_DATE: '2023-05-18 16:00:00', SOL_ID: '005', QUEUE_NAME: 'Account Opening', BR_NAME: 'Branch E', LOAN_AMT: 2000000, TAT: 366.93 },
        { QUEUE: 'BM', QUEUE_DATE: '2023-06-25 08:50:00', SOL_ID: '006', QUEUE_NAME: 'Branch Maker', BR_NAME: 'Branch F', LOAN_AMT: 300000, TAT: 120 },
        { QUEUE: 'BC', QUEUE_DATE: '2024-07-30 13:35:00', SOL_ID: '007', QUEUE_NAME: 'Branch Checker', BR_NAME: 'Branch G', LOAN_AMT: 1000000, TAT: 45 },
        { QUEUE: 'CS', QUEUE_DATE: '2024-08-22 15:40:00', SOL_ID: '008', QUEUE_NAME: 'Credit Sanctioner', BR_NAME: 'Branch H', LOAN_AMT: 500000, TAT: 180 },
        { QUEUE: 'BD', QUEUE_DATE: '2024-09-12 12:10:00', SOL_ID: '009', QUEUE_NAME: 'Branch Disbursal', BR_NAME: 'Branch I', LOAN_AMT: 1200000, TAT: 7200 },
        { QUEUE: 'ACOPN', QUEUE_DATE: '2024-10-05 14:25:00', SOL_ID: '010', QUEUE_NAME: 'Account Opening', BR_NAME: 'Branch J', LOAN_AMT: 800000, TAT: 400 },
        // Add more sample data as needed
      ];
      var data = originalData.slice();
      var totalItems = 0;
      var totalLoanAmount = 0;
      var averageLoanAmount = 0;
      var averageTAT = 0;
      var loanAmountRange = [0, 0];

      // Initialize Filters
      function initializeFilters() {
        var queues = [...new Set(originalData.map(item => item.QUEUE))].sort();
        var solIds = [...new Set(originalData.map(item => item.SOL_ID))].sort();

        queues.forEach(queue => {
          $('#queue-filter').append(`<option value="${queue}">${queue}</option>`);
		  });

        solIds.forEach(solId => {
          $('#solid-filter').append(`<option value="${solId}">${solId}</option>`);
        });

        // Initialize date range
        var dates = originalData.map(item => parseDate(item.QUEUE_DATE));
        var minDate = new Date(Math.min.apply(null, dates));
        var maxDate = new Date(Math.max.apply(null, dates));

        flatpickr("#date-range", {
          mode: "range",
          dateFormat: "Y-m-d",
          defaultDate: [minDate, maxDate],
          onChange: applyFilters
        });

        // Initialize loan amount slider
        var loanAmts = originalData.map(item => item.LOAN_AMT || 0);
        var minLoanAmt = Math.min.apply(null, loanAmts);
        var maxLoanAmt = Math.max.apply(null, loanAmts);

        loanAmountRange = [minLoanAmt, maxLoanAmt];

        var slider = document.getElementById('loan-amount-slider');

        noUiSlider.create(slider, {
          start: [minLoanAmt, maxLoanAmt],
          connect: true,
          range: {
            'min': minLoanAmt,
            'max': maxLoanAmt
          },
          tooltips: true,
          format: wNumb({
            decimals: 0,
            thousand: ',',
            prefix: 'Rs '
          })
        });

        $('#min-loan-amount').text('Rs ' + minLoanAmt.toLocaleString());
        $('#max-loan-amount').text('Rs ' + maxLoanAmt.toLocaleString());

        slider.noUiSlider.on('update', function(values, handle) {
          loanAmountRange = values.map(v => Number(v.replace(/[^0-9.-]+/g,"")));
          $('#min-loan-amount').text(values[0]);
          $('#max-loan-amount').text(values[1]);
        });

        slider.noUiSlider.on('change', applyFilters);

        // Trigger filters when selection changes
        $('#queue-filter, #solid-filter').on('change', applyFilters);
        $('input[name="aggregationPeriod"]').on('change', applyFilters);

        // Export Buttons
        $('#export-data').on('click', exportChartData);
        $('#export-chart').on('click', exportChartImages);
      }

      // Parse date strings into Date objects
      function parseDate(dateString) {
        return new Date(dateString.replace(' ', 'T'));
      }

      // Apply Filters
      function applyFilters() {
        var selectedQueues = $('#queue-filter').val() || [];
        var selectedSolIds = $('#solid-filter').val() || [];
        var dateRange = $('#date-range').val().split(' to ');
        var startDate = dateRange[0] ? new Date(dateRange[0]) : null;
        var endDate = dateRange[1] ? new Date(dateRange[1]) : null;
        if (endDate) {
          endDate.setHours(23,59,59,999);
        }
        var minLoanAmt = loanAmountRange[0];
        var maxLoanAmt = loanAmountRange[1];
        var aggregationPeriod = $('input[name="aggregationPeriod"]:checked').val();

        data = originalData.filter(function(entry) {
          var include = true;
          var date = parseDate(entry.QUEUE_DATE);
          var loanAmt = entry.LOAN_AMT || 0;

          if (selectedQueues.length > 0 && !selectedQueues.includes(entry.QUEUE)) {
            include = false;
          }
          if (selectedSolIds.length > 0 && !selectedSolIds.includes(entry.SOL_ID)) {
            include = false;
          }
          if (startDate && date < startDate) {
            include = false;
          }
          if (endDate && date > endDate) {
            include = false;
          }
          if (loanAmt < minLoanAmt || loanAmt > maxLoanAmt) {
            include = false;
          }
          return include;
        });

        updateDashboardMetrics();
        renderCharts();
      }

      // Update Dashboard Metrics
      function updateDashboardMetrics() {
        totalItems = data.length;
        totalLoanAmount = data.reduce((sum, entry) => sum + (entry.LOAN_AMT || 0), 0);
        averageLoanAmount = totalItems > 0 ? totalLoanAmount / totalItems : 0;
        averageTAT = data.reduce((sum, entry) => sum + entry.TAT, 0) / totalItems;

        $('#total-items').text(totalItems);
        $('#total-loan-amount').text('Rs ' + totalLoanAmount.toLocaleString());
        $('#average-loan-amount').text('Rs ' + averageLoanAmount.toFixed(2).toLocaleString());
        $('#average-tat').text(averageTAT.toFixed(2));
      }

      // Render Charts
      function renderCharts() {
        var aggregationPeriod = $('input[name="aggregationPeriod"]:checked').val();
        var aggregatedData = aggregateData(aggregationPeriod);

        renderChart1(aggregatedData);
        renderChart2(aggregatedData);
        renderChart3();
        renderChart4();
        renderChart5(aggregatedData);
        renderChart6();
        renderSankeyChart();
        renderChart8();
        renderChart9();
        renderChart10();
      }

      // Aggregate Data
      function aggregateData(period) {
        var groupedData = {};
        data.forEach(function(entry) {
          var date = parseDate(entry.QUEUE_DATE);
          var key;
          if (period === 'daily') {
            key = date.toISOString().split('T')[0];
          } else if (period === 'monthly') {
            key = date.getFullYear() + '-' + String(date.getMonth() + 1).padStart(2, '0');
          } else if (period === 'yearly') {
            key = date.getFullYear().toString();
          }
          if (!groupedData[key]) {
            groupedData[key] = {
              x: key,
              y: 0,
              totalLoanAmt: 0,
              totalTAT: 0
            };
          }
          groupedData[key].y++;
          groupedData[key].totalLoanAmt += entry.LOAN_AMT || 0;
          groupedData[key].totalTAT += entry.TAT || 0;
        });
        var result = Object.values(groupedData);
        result.sort((a, b) => new Date(a.x) - new Date(b.x));
        result.forEach(function(item) {
          if (period === 'monthly') {
            var parts = item.x.split('-');
            var year = parts[0];
            var month = parts[1];
            var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun",
                              "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
            item.x = monthNames[parseInt(month) - 1] + ' ' + year;
          }
          item.averageTAT = item.totalTAT / item.y;
        });
        return result;
      }

      // Render Chart 1: Number of Items Over Time
      function renderChart1(aggregatedData) {
        var options = {
          series: [{
            name: 'Number of Items',
            data: aggregatedData.map(item => item.y)
          }],
          chart: {
            type: 'bar',
            height: 350,
            events: {
              dataPointSelection: (event, chartContext, config) => {
                var selectedCategory = aggregatedData[config.dataPointIndex].x;
                var detailedData = getDetailedData(selectedCategory, $('input[name="aggregationPeriod"]:checked').val());
                displayDataModal(detailedData);
              }
            }
          },
          plotOptions: {
            bar: {
              horizontal: false,
              columnWidth: '55%',
              endingShape: 'rounded'
            },
          },
          dataLabels: {
            enabled: false
          },
          xaxis: {
            categories: aggregatedData.map(item => item.x),
          },
          yaxis: {
            title: {
              text: 'Number of Items'
            }
          },
          fill: {
            opacity: 1
          },
          title: {
            text: 'Number of Items Over Time',
            align: 'center'
          }
        };

        if (chart1) {
          chart1.updateOptions(options);
        } else {
          chart1 = new ApexCharts(document.querySelector("#chart1"), options);
          chart1.render();
        }
      }

      // Render Chart 2: Total Loan Amount Over Time
      function renderChart2(aggregatedData) {
        var options = {
          series: [{
            name: 'Total Loan Amount',
            data: aggregatedData.map(item => item.totalLoanAmt)
          }],
          chart: {
            type: 'line',
            height: 350,
            events: {
              dataPointSelection: (event, chartContext, config) => {
                var selectedCategory = aggregatedData[config.dataPointIndex].x;
                var detailedData = getDetailedData(selectedCategory, $('input[name="aggregationPeriod"]:checked').val());
                displayDataModal(detailedData);
              }
            }
          },
          stroke: {
            curve: 'smooth',
            width: 3,
          },
          dataLabels: {
            enabled: false
          },
          xaxis: {
            categories: aggregatedData.map(item => item.x),
          },
          yaxis: {
            title: {
              text: 'Total Loan Amount'
            },
            labels: {
              formatter: function (value) {
                return '$' + value.toFixed(0).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
              }
            }
          },
          title: {
            text: 'Total Loan Amount Over Time',
            align: 'center'
          }
        };

        if (chart2) {
          chart2.updateOptions(options);
        } else {
          chart2 = new ApexCharts(document.querySelector("#chart2"), options);
          chart2.render();
        }
      }

      // Render Chart 3: Average TAT by Queue
      function renderChart3() {
        var queueData = {};
        data.forEach(function(entry) {
          if (!queueData[entry.QUEUE]) {
            queueData[entry.QUEUE] = { total: 0, count: 0 };
          }
          queueData[entry.QUEUE].total += entry.TAT;
          queueData[entry.QUEUE].count++;
        });

        var queues = Object.keys(queueData);
        var averageTATs = queues.map(queue => queueData[queue].total / queueData[queue].count);

        var options = {
          series: [{
            name: 'Average TAT (Minutes)',
            data: averageTATs
          }],
          chart: {
            type: 'bar',
            height: 350
          },
          plotOptions: {
            bar: {
              horizontal: true,
            }
          },
          dataLabels: {
            enabled: false
          },
          xaxis: {
            categories: queues,
          },
          yaxis: {
            title: {
              text: 'Average TAT (Minutes)'
            }
          },
          title: {
            text: 'Average TAT by Queue',
            align: 'center'
          }
        };

        if (chart3) {
          chart3.updateOptions(options);
        } else {
          chart3 = new ApexCharts(document.querySelector("#chart3"), options);
          chart3.render();
        }
      }

      // Render Chart 4: TAT Distribution by Queue
      function renderChart4() {
        var tatRanges = {
          'Below 1 Hour': 0,
          '1-5 Hours': 0,
          '5-10 Hours': 0,
          '10+ Hours': 0
        };

        data.forEach(function(entry) {
          if (entry.TAT < 60) {
            tatRanges['Below 1 Hour']++;
          } else if (entry.TAT < 300) {
            tatRanges['1-5 Hours']++;
          } else if (entry.TAT < 600) {
            tatRanges['5-10 Hours']++;
          } else {
            tatRanges['10+ Hours']++;
          }
        });

        var options = {
          series: Object.values(tatRanges),
          chart: {
            type: 'pie',
            height: 350
          },
          labels: Object.keys(tatRanges),
          title: {
            text: 'TAT Distribution',
            align: 'center'
          },
          responsive: [{
            breakpoint: 480,
            options: {
              chart: {
                width: 200
              },
              legend: {
                position: 'bottom'
              }
            }
          }]
        };

        if (chart4) {
          chart4.updateOptions(options);
        } else {
          chart4 = new ApexCharts(document.querySelector("#chart4"), options);
          chart4.render();
        }
      }

      // Render Chart 5: TAT Trends Over Time
      function renderChart5(aggregatedData) {
        var options = {
          series: [{
            name: 'Average TAT',
            data: aggregatedData.map(item => item.averageTAT)
          }],
          chart: {
            height: 350,
            type: 'line',
          },
          stroke: {
            curve: 'smooth',
          },
          title: {
            text: 'TAT Trends Over Time',
            align: 'center'
          },
          xaxis: {
            categories: aggregatedData.map(item => item.x),
          },
          yaxis: {
            title: {
              text: 'Average TAT (Minutes)'
            }
          }
        };

        if (chart5) {
          chart5.updateOptions(options);
        } else {
          chart5 = new ApexCharts(document.querySelector("#chart5"), options);
          chart5.render();
        }
      }

      // Render Chart 6: Queue-Level TAT Comparison
      function renderChart6() {
        var queueData = {};
        data.forEach(function(entry) {
          if (!queueData[entry.QUEUE]) {
            queueData[entry.QUEUE] = { total: 0, count: 0 };
          }
          queueData[entry.QUEUE].total += entry.TAT;
          queueData[entry.QUEUE].count++;
        });

        var queues = Object.keys(queueData);
        var averageTATs = queues.map(queue => queueData[queue].total / queueData[queue].count);

        var options = {
          series: [{
            name: 'Average TAT (Minutes)',
            data: averageTATs
          }],
          chart: {
            height: 350,
            type: 'radar',
			},
          title: {
            text: 'Queue-Level TAT Comparison',
            align: 'center'
          },
          xaxis: {
            categories: queues,
          },
          yaxis: {
            show: false
          }
        };

        if (chart6) {
          chart6.updateOptions(options);
        } else {
          chart6 = new ApexCharts(document.querySelector("#chart6"), options);
          chart6.render();
        }
      }

      // Render Sankey Chart: Queue Handover Efficiency
      function renderSankeyChart() {
        google.charts.load('current', {'packages':['sankey']});
        google.charts.setOnLoadCallback(drawSankeyChart);

        function drawSankeyChart() {
          var queueFlow = {};
          data.forEach(function(entry, index) {
            if (index < data.length - 1) {
              var from = entry.QUEUE;
              var to = data[index + 1].QUEUE;
              var key = from + '->' + to;
              if (!queueFlow[key]) {
                queueFlow[key] = { count: 0, totalTAT: 0 };
              }
              queueFlow[key].count++;
              queueFlow[key].totalTAT += entry.TAT;
            }
          });

          var sankeyData = [['From', 'To', 'TAT']];
          Object.entries(queueFlow).forEach(([key, value]) => {
            var [from, to] = key.split('->');
            sankeyData.push([from, to, value.totalTAT / value.count]);
          });

          var data = google.visualization.arrayToDataTable(sankeyData);

          var options = {
            width: 600,
            height: 300,
            sankey: {
              node: {
                colors: ['#a6cee3', '#b2df8a', '#fb9a99', '#fdbf6f', '#cab2d6']
              },
              link: {
                colorMode: 'gradient',
                colors: ['#ffffcc', '#800026']
              }
            }
          };

          var chart = new google.visualization.Sankey(document.getElementById('sankey_chart'));
          chart.draw(data, options);
        }
      }

      // Render Chart 8: Loan Amount vs TAT Scatter Plot
      function renderChart8() {
        var options = {
          series: [{
            name: 'TAT vs Loan Amount',
            data: data.map(item => [item.LOAN_AMT, item.TAT])
          }],
          chart: {
            height: 350,
            type: 'scatter',
            zoom: {
              enabled: true,
              type: 'xy'
            }
          },
          xaxis: {
            title: {
              text: 'Loan Amount'
            },
            labels: {
              formatter: function(val) {
                return '$' + val.toFixed(0).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
              }
            }
          },
          yaxis: {
            title: {
              text: 'TAT (Minutes)'
            }
          },
          title: {
            text: 'Loan Amount vs TAT',
            align: 'center'
          }
        };

        if (chart8) {
          chart8.updateOptions(options);
        } else {
          chart8 = new ApexCharts(document.querySelector("#chart8"), options);
          chart8.render();
        }
      }

      // Render Chart 9: TAT vs Number of Items Heatmap
      function renderChart9() {
        var tatRanges = ['0-60', '61-300', '301-600', '601+'];
        var itemRanges = ['1-10', '11-50', '51-100', '101+'];
        var heatmapData = {};

        tatRanges.forEach(tatRange => {
          heatmapData[tatRange] = {};
          itemRanges.forEach(itemRange => {
            heatmapData[tatRange][itemRange] = 0;
          });
        });

        data.forEach(function(entry) {
          var tatRange = entry.TAT <= 60 ? '0-60' :
                         entry.TAT <= 300 ? '61-300' :
                         entry.TAT <= 600 ? '301-600' : '601+';
          var itemRange = entry.LOAN_AMT <= 1000000 ? '1-10' :
                          entry.LOAN_AMT <= 5000000 ? '11-50' :
                          entry.LOAN_AMT <= 10000000 ? '51-100' : '101+';
          heatmapData[tatRange][itemRange]++;
        });

        var series = tatRanges.map(tatRange => ({
          name: tatRange,
          data: itemRanges.map(itemRange => heatmapData[tatRange][itemRange])
        }));

        var options = {
          series: series,
          chart: {
            height: 350,
            type: 'heatmap',
          },
          dataLabels: {
            enabled: false
          },
          colors: ["#008FFB"],
          title: {
            text: 'TAT vs Number of Items Heatmap',
            align: 'center'
          },
          xaxis: {
            categories: itemRanges,
            title: {
              text: 'Loan Amount Range'
            }
          },
          yaxis: {
            categories: tatRanges,
            title: {
              text: 'TAT Range (Minutes)'
            }
          }
        };

        if (chart9) {
          chart9.updateOptions(options);
        } else {
          chart9 = new ApexCharts(document.querySelector("#chart9"), options);
          chart9.render();
        }
      }

      // Render Chart 10: Queue Performance Comparison
      function renderChart10() {
        var queueMetrics = {};
        data.forEach(function(entry) {
          if (!queueMetrics[entry.QUEUE]) {
            queueMetrics[entry.QUEUE] = { totalTAT: 0, totalLoanAmt: 0, count: 0 };
          }
          queueMetrics[entry.QUEUE].totalTAT += entry.TAT;
          queueMetrics[entry.QUEUE].totalLoanAmt += entry.LOAN_AMT;
          queueMetrics[entry.QUEUE].count++;
        });

        var queues = Object.keys(queueMetrics);
        var avgTAT = queues.map(queue => queueMetrics[queue].totalTAT / queueMetrics[queue].count);
        var avgLoanAmt = queues.map(queue => queueMetrics[queue].totalLoanAmt / queueMetrics[queue].count);
        var efficiency = queues.map(queue => (queueMetrics[queue].totalLoanAmt / queueMetrics[queue].totalTAT) / 1000);

        var options = {
          series: [{
            name: 'Avg TAT (minutes)',
            type: 'column',
            data: avgTAT
          }, {
            name: 'Avg Loan Amount ($)',
            type: 'line',
            data: avgLoanAmt
          }, {
            name: 'Efficiency (Loan Amt / TAT)',
            type: 'line',
            data: efficiency
          }],
          chart: {
            height: 350,
            type: 'line',
            stacked: false
          },
          dataLabels: {
            enabled: false
          },
          stroke: {
            width: [1, 1, 4]
          },
          title: {
            text: 'Queue Performance Comparison',
            align: 'center'
          },
          xaxis: {
            categories: queues,
          },
          yaxis: [
            {
              axisTicks: {
                show: true,
              },
              axisBorder: {
                show: true,
                color: '#008FFB'
              },
              labels: {
                style: {
                  colors: '#008FFB',
                }
              },
              title: {
                text: "Avg TAT (minutes)",
                style: {
                  color: '#008FFB',
                }
              },
              tooltip: {
                enabled: true
              }
            },
            {
              seriesName: 'Avg Loan Amount',
              opposite: true,
              axisTicks: {
                show: true,
              },
              axisBorder: {
                show: true,
                color: '#00E396'
              },
              labels: {
                style: {
                  colors: '#00E396',
                },
                formatter: function(val) {
                  return '$' + val.toFixed(0).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
                }
              },
              title: {
                text: "Avg Loan Amount ($)",
                style: {
                  color: '#00E396',
                }
              }
            },
            {
              opposite: true,
              seriesName: 'Efficiency',
              axisTicks: {
                show: true,
              },
              axisBorder: {
                show: true,
                color: '#775DD0'
              },
              labels: {
                style: {
                  colors: '#775DD0',
                }
              },
              title: {
                text: "Efficiency (Loan Amt / TAT)",
                style: {
                  color: '#775DD0',
                }
              }
            }
          ],
          tooltip: {
            shared: true,
            intersect: false,
            y: {
              formatter: function(val, opts) {
                var seriesIndex = opts.seriesIndex;
                if (seriesIndex === 1) {
                  return '$' + val.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
                }
                return val.toFixed(2);
              }
            }
          },
        };

        if (chart10) {
          chart10.updateOptions(options);
        } else {
          chart10 = new ApexCharts(document.querySelector("#chart10"), options);
          chart10.render();
        }
      }

      // Get detailed data based on selected category
      function getDetailedData(selectedCategory, aggregationPeriod) {
        return data.filter(function(entry) {
          var entryDate = parseDate(entry.QUEUE_DATE);
          var category = '';
          if (aggregationPeriod === 'daily') {
            category = entryDate.toISOString().split('T')[0];
          } else if (aggregationPeriod === 'monthly') {
            category = entryDate.getFullYear() + '-' + String(entryDate.getMonth() + 1).padStart(2, '0');
          } else if (aggregationPeriod === 'yearly') {
            category = entryDate.getFullYear().toString();
          }
          return category === selectedCategory;
        });
      }

      // Display detailed data in a modal
      function displayDataModal(detailedData) {
        var tbody = $('#data-table tbody');
        tbody.empty();
        detailedData.forEach(function(entry) {
          tbody.append(`
            <tr>
              <td>${entry.QUEUE}</td>
              <td>${entry.QUEUE_DATE}</td>
              <td>${entry.SOL_ID}</td>
              <td>${entry.QUEUE_NAME}</td>
              <td>${entry.BR_NAME}</td>
              <td>$${(entry.LOAN_AMT || 0).toLocaleString()}</td>
              <td>${entry.TAT.toFixed(2)}</td>
            </tr>
          `);
        });
        $('#dataModal').modal('show');
      }

      // Export chart data to CSV
      function exportChartData() {
        var csvContent = "data:text/csv;charset=utf-8,";
        csvContent += "QUEUE,QUEUE_DATE,SOL_ID,QUEUE_NAME,BR_NAME,LOAN_AMT,TAT\n";

        data.forEach(function(entry) {
          var row = [
            entry.QUEUE,
            entry.QUEUE_DATE,
            entry.SOL_ID,
            entry.QUEUE_NAME,
            entry.BR_NAME,
            entry.LOAN_AMT || 0,
            entry.TAT.toFixed(2)
          ];
          csvContent += row.join(",") + "\n";
        });

        var encodedUri = encodeURI(csvContent);
        var link = document.createElement("a");
        link.setAttribute("href", encodedUri);
        link.setAttribute("download", "dashboard_data.csv");
        document.body.appendChild(link); // Required for Firefox
        link.click();
        document.body.removeChild(link);
      }

     // Export charts as images
function exportChartImages() {
  var charts = [chart1, chart2, chart3, chart4, chart5, chart6, chart8, chart9, chart10];
  var promises = [];

  charts.forEach(function(chart, index) {
    if (chart) { // Check if chart is properly initialized
      promises.push(chart.dataURI());
    }
  });

  Promise.all(promises).then(function(dataURIs) {
    dataURIs.forEach(function(dataURI, index) {
      if (dataURI && dataURI.imgURI) { // Check if imgURI is available
        var link = document.createElement("a");
        link.href = dataURI.imgURI;
        link.download = `chart_${index + 1}.png`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      }
    });
  }).catch(function(error) {
    console.error("Error generating chart images: ", error);
  });
}

      // Initialize everything
      initializeFilters();
      applyFilters();
    });
  </script>
</body>
</html>
