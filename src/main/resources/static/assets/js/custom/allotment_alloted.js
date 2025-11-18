"use strict";

var IbankLOSDashboard = function() {
    var pendingTable;
    var alreadyAllotedTable;

    var initPendingTable = function() {
        pendingTable = $('#pending_allot').DataTable({
            "info": false,
            'order': [],
            'pageLength': 10,
            'destroy': true, // Add this line to ensure proper reinitialization
        });

        const filterPendingSearch = document.querySelector('[data-kt-ecommerce-order-filter="search_pending"]');
        filterPendingSearch.addEventListener('keyup', function(e) {
            pendingTable.search(e.target.value).draw();
        });
    }

    var initAlreadyAllotedTable = function() {
        alreadyAllotedTable = $('#already_alloted').DataTable({
            "info": false,
            'order': [],
            'pageLength': 10,
            'destroy': true, // Add this line to ensure proper reinitialization
        });

        const filterAllotedSearch = document.querySelector('[data-kt-ecommerce-order-filter="search_alloted"]');
        filterAllotedSearch.addEventListener('keyup', function(e) {
            alreadyAllotedTable.search(e.target.value).draw();
        });
    }

    var initDaterangepicker = () => {
        var start = moment().subtract(29, "days");
        var end = moment();
        var input = $("#branch_maker_queue_daterangepicker");

        function cb(start, end) {
            input.html(start.format("MMMM D, YYYY") + " - " + end.format("MMMM D, YYYY"));
            pendingTable.draw();
            alreadyAllotedTable.draw();
        }

        input.daterangepicker({
            startDate: start,
            endDate: end,
            ranges: {
                "Today": [moment(), moment()],
                "Yesterday": [moment().subtract(1, "days"), moment().subtract(1, "days")],
                "Last  Days": [moment().subtract(6, "days"), moment()],
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

    return {
        init: function() {
            initPendingTable();
            if (!pendingTable) {
                console.log("table not found");
                return;
            }
            initAlreadyAllotedTable();
            if (!alreadyAllotedTable) {
                console.log("alreadyAllotedTable not found");
                return;
            }
            // initDaterangepicker();
        }
    };
}();

document.addEventListener('DOMContentLoaded', function() {
    IbankLOSDashboard.init();
});
