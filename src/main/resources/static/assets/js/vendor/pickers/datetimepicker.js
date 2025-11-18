$("#trandate").datetimepicker({
    useCurrent: false,
    format: "DD-MMM-YYYY",
    showTodayButton: true,
    icons: {
      next: "fa fa-chevron-right",
      previous: "fa fa-chevron-left",
      today: 'todayText',
    }
  });

  $("#trantime").datetimepicker({
     format: "hh:mm:ss",
    icons: {
      up: "fa fa-chevron-up",
      down: "fa fa-chevron-down"
    }
  });
