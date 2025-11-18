"use strict";
var IbankLOSDashboard = function() {
    var table;
    var datatable;
    var initDatatable = function () {
        datatable = $('#pending_allot').DataTable({
            "info": false,
            'order': [],
            'pageLength': 10,
        });
    }
    var initDaterangepicker = () => {
        var start = moment().subtract(29, "days");
        var end = moment();
        var input = $("#branch_maker_queue_daterangepicker");

        function cb(start, end) {
            input.html(start.format("MMMM D, YYYY") + " - " + end.format("MMMM D, YYYY"));
            datatable.draw();
        }

        input.daterangepicker({
            startDate: start,
            endDate: end,
            ranges: {
                "Today": [moment(), moment()],
                "Yesterday": [moment().subtract(1, "days"), moment().subtract(1, "days")],
                "Last 7 Days": [moment().subtract(6, "days"), moment()],
                "Last 30 Days": [moment().subtract(29, "days"), moment()],
                "This Month": [moment().startOf("month"), moment().endOf("month")],
                "Last Month": [moment().subtract(1, "month").startOf("month"), moment().subtract(1, "month").endOf("month")]
            }
        }, cb);

        cb(start, end);
        $.fn.dataTable.ext.search.push(
        function(settings, data, dataIndex) {
            var selectedStart = moment(start, "MMMM D, YYYY");
            var selectedEnd = moment(end, "MMMM D, YYYY");
            var rowDate = moment(data[1], "MMMM D, YYYY"); // Assumes date is in the second column (index 1)

            return rowDate.isBetween(selectedStart, selectedEnd, null, '[]');
        }
    );

    }
    var handleSearchDatatable = () => {
        const filterSearch = document.querySelector('[data-kt-ecommerce-order-filter="search_pending"]');
        filterSearch.addEventListener('keyup', function (e) {
            datatable.search(e.target.value).draw();
        });
    }
    return {
        init: function () {
            table = document.querySelector('#branch-maker-queue-details-table');

            if (!table) {
                return;
            }

            initDatatable();
            handleSearchDatatable();
        }
    };
    }();
document.addEventListener('DOMContentLoaded', function() {
    IbankLOSDashboard.init();
});