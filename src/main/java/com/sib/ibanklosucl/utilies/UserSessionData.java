package com.sib.ibanklosucl.utilies;

import com.sib.ibanklosucl.dto.Employee;
import com.sib.ibanklosucl.dto.MenuList;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION,proxyMode = ScopedProxyMode.TARGET_CLASS)
@Data
public class UserSessionData {
    private String winumber;
    private String trans_slno;
    private String userName;
    private String uidaitxn;
//    private String aadhar_pdf;

    private String trace_id;
    private String remoteIP;
    private String ssoToken;


    private Employee employee;
    private List<MenuList> menuList;

    public String getSolid() {
        return this.getEmployee().getPpcAvailSol();
    }

    public String getPPCNo() {
        return this.getEmployee().getPpcno();
    }

    private Map<String, Map<String, String>> appData = new HashMap<>();


    // Method to store data in appData map
    public void putData(String key1, String key2, String value) {
        if (key1 == null || key2 == null) {
            throw new IllegalArgumentException("Keys cannot be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        appData.computeIfAbsent(key1, k -> new HashMap<>()).put(key2, value);
    }

    // Method to retrieve data from sessionData map
    public String getData(String key1, String key2) {
        if (key1 == null || key2 == null) {
            throw new IllegalArgumentException("Keys cannot be null");
        }
        Map<String, String> innerMap = appData.get(key1);
        return (innerMap != null) ? innerMap.get(key2) : null;
    }
     public void clear() {
        this.winumber = null;
        this.trans_slno = null;
        this.uidaitxn = null;
        this.trace_id = null;
        this.remoteIP = null;
        this.employee = null;
        this.menuList = null;
        this.userName = null;
        this.ssoToken = null;
        this.appData.clear();
    }


}
