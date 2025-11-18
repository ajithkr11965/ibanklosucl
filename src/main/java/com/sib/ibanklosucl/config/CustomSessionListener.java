package com.sib.ibanklosucl.config;

import com.sib.ibanklosucl.service.vlsr.VehicleLoanLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
@Component
public class CustomSessionListener implements HttpSessionListener {

    @Autowired
    private VehicleLoanLockService lockservice;


    @Override
    public void sessionCreated(HttpSessionEvent se) {
        //
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // Obtain the session
        HttpSession session = se.getSession();
        // Retrieve UserSessionData from the session attributes
        String userSessionPPC =(String) session.getAttribute("userSessionPPC");
        if (userSessionPPC != null) {
            lockservice.ReleaseAllLock(userSessionPPC);
        }

    }
}
