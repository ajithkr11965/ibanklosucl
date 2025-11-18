package com.sib.ibanklosucl.service;

import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

public interface VLWiCreateService {
    Model addEntry(HttpServletRequest request, Model model);
    Model modifyEntry(String slno, HttpServletRequest request, Model model);
    Model fetchcheckerDetails(String slno, HttpServletRequest request, Model model);
    Model fetchCRTcheckerDetails(String slno, HttpServletRequest request, Model model);

    Model modifyRMEntry(String slno, HttpServletRequest request, Model model);
    Model modifyRCEntry(String slno, HttpServletRequest request, Model model);
    Model modifyBDEntry(String slno, HttpServletRequest request, Model model);
    Model modifyCRTAmber(String slno, HttpServletRequest request, Model model);

    Model fetchBOGAssetDetails(String slno, HttpServletRequest request, Model model);
    Model wiSearch(String winum, HttpServletRequest request, Model model);
    Model fetchWiBog(String slno, HttpServletRequest request, Model model);
    Model insOptOut(String slno, HttpServletRequest request, Model model);


    Model modifyWaiverEntry(String slno, HttpServletRequest request, Model model);

    Model fetchWiSm(String slno, HttpServletRequest request, Model model);

}
