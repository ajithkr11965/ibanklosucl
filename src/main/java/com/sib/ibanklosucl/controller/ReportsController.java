package com.sib.ibanklosucl.controller;

import com.sib.ibanklosucl.config.menuaccess.RequiresMenuAccess;
import com.sib.ibanklosucl.service.reports.ReportService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
//@RequestMapping("/rpt")
public class ReportsController {
    @Autowired
    private UserSessionData usd;
    @Autowired
    private ReportService reportService;
    @GetMapping("/rptlist")
    @RequiresMenuAccess(menuIds = {"RPT"})
    public String ReportList(Model model) {
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        return "rpt/rptlist";
    }

    @GetMapping("/rpt1")
    @RequiresMenuAccess(menuIds = {"RPT1"})
    public String getPpcCheckerLevelDetails(Model model) {
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        model.addAttribute("reportname", "RBCPC Checker Levels Report");
        model.addAttribute("report", reportService.getPpcCheckerLevelDetails("N"));
        return "rpt/report1";
    }
    @GetMapping("/rpt2")
    @RequiresMenuAccess(menuIds = {"RPT2"})
    public String getRoiWaiverLevelDetails(Model model) {
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        model.addAttribute("reportname", "ROI Waiver Levels Report");
        model.addAttribute("report", reportService.getRoiWaiverLevelDetails("N"));
        return "rpt/report1";
    }
    @GetMapping("/rpt3")
    @RequiresMenuAccess(menuIds = {"RPT3"})
    public String getChargeWaiverLevelDetails(Model model) {
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        model.addAttribute("reportname", "Charge Waiver Levels Report");
        model.addAttribute("report", reportService.getChargeWaiverLevelDetails("N"));
        return "rpt/report1";
    }


    @GetMapping("/rpt4")
    @RequiresMenuAccess(menuIds = {"RPT4"})
    public String DetailReport(Model model) {
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        model.addAttribute("detailreport", reportService.getDetailReport(String.valueOf(usd.getSolid())));
        return "detailreport";
    }

    @GetMapping("/rpt5")
    @RequiresMenuAccess(menuIds = {"RPT5"})
    public String ConsolReport(Model model) {
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        model.addAttribute("consolreport", reportService.getConsolReport(String.valueOf(usd.getSolid())));
        return "consolreport";
    }
    @PostMapping("/rpt6")
    @RequiresMenuAccess(menuIds = {"RPT6"})
    public String dkexperianFailReport( @RequestParam  String fromdt,@RequestParam  String todt,Model model) {
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        model.addAttribute("report", reportService.dkexperianFailReport(fromdt,todt));
        return "rpt/dkexperianFailReport";
    }



    @GetMapping("/rpt7")
    @RequiresMenuAccess(menuIds = {"RPT7"})
    public String WaiverReportInput(Model model) {
        return "rpt/waiverDate";
    }

    @PostMapping("/waiverReport7")
    @RequiresMenuAccess(menuIds = {"RPT7"})
    public String WaiverReport( @RequestParam  String fromdt1,@RequestParam  String todt1,@RequestParam  String fromdt2,@RequestParam  String todt2,Model model) {
        if(fromdt1!=null && !fromdt1.isBlank())
        model.addAttribute("report", reportService.WaiverReport(fromdt1,todt1,"R"));
        else if(fromdt2!=null && !fromdt2.isBlank())
        model.addAttribute("report", reportService.WaiverReport(fromdt2,todt2,"P"));
        return "rpt/waiverReport";
    }

    @GetMapping("/dtrpt/{reportPath}")
  //  @RequiresMenuAccess(menuIds = {"RPT8"})
    public String DateReportInput(@PathVariable String reportPath,Model model) {
        String reportName=usd.getMenuList().stream().filter(t->t.getMenuID().equalsIgnoreCase(reportPath)).toList().get(0).getMenuDesc();

        model.addAttribute("reportName",reportName);
        model.addAttribute("reportPath",reportPath);
        return "rpt/dateReport";
    }

    @PostMapping("/rpt8")
    @RequiresMenuAccess(menuIds = {"RPT8"})
    public String CurrentReport( @RequestParam  String fromdt,@RequestParam  String todt,Model model) {
        model.addAttribute("report", reportService.getCurrentReport(fromdt,todt));
        return "rpt/CurrentStatusReport";
    }
    @PostMapping("/rpt9")
    @RequiresMenuAccess(menuIds = {"RPT9"})
    public String MisReport( @RequestParam  String fromdt,@RequestParam  String todt,Model model) {
        model.addAttribute("report", reportService.getMisReport(fromdt,todt));
        return "rpt/MisReport";
    }

    @GetMapping ("/rpt10")
    @RequiresMenuAccess(menuIds = {"RPT10"})
    public String vehicleLoanCifReport(Model model) {
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        model.addAttribute("report", reportService.getVehicleLoanCifReport());
        return "rpt/vehicleLoanCifReport";
    }
    @GetMapping ("/rpt11")
    @RequiresMenuAccess(menuIds = {"RPT11"})
    public String vehicleLoanDeviationsReport(Model model) {
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        model.addAttribute("report", reportService.vehicleLoanDeviationsReport());
        return "rpt/deviationsReport";
    }
    @GetMapping("/statrpt/{reportPath}")
    //  @RequiresMenuAccess(menuIds = {"RPT8"})
    public String StatReportInput(@PathVariable String reportPath,Model model) {
        String reportName=usd.getMenuList().stream().filter(t->t.getMenuID().equalsIgnoreCase(reportPath)).toList().get(0).getMenuDesc();

        model.addAttribute("reportName",reportName);
        model.addAttribute("reportPath",reportPath);
        return "rpt/statReport";
    }

    @PostMapping("/rpt12")
    @RequiresMenuAccess(menuIds = {"RPT12"})
    public String StatReport1(@RequestParam  String wistat,Model model) {
        model.addAttribute("livereport", reportService.vehicleLoanLiveReport(wistat));
        return "rpt/LiveStatusReport";
    }

    @GetMapping("/rpt12")
    @RequiresMenuAccess(menuIds = {"RPT12"})
    public String StatReport(@RequestParam  String wistat,Model model) {
        model.addAttribute("livereport", reportService.vehicleLoanLiveReport(wistat));
        return "rpt/LiveStatusReport";
    }
    @GetMapping("/rpt13")
@RequiresMenuAccess(menuIds = {"RPT13"})
public String manualNachReport(Model model) {
    model.addAttribute("employee", usd.getEmployee());
    model.addAttribute("menuList", usd.getMenuList());
    model.addAttribute("reportname", "Manual NACH Pending Report");
    model.addAttribute("report", reportService.getManualNachReport());
    return "rpt/manualNachReport";
}

    @GetMapping("/rpt14")
    @RequiresMenuAccess(menuIds = {"RPT14"})
    public String getMSSFReport(Model model) {
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        model.addAttribute("reportname", "MSSF Customer Details Report");
        model.addAttribute("report", reportService.getMSSFReport());
        return "rpt/mssfReport";
    }
}
