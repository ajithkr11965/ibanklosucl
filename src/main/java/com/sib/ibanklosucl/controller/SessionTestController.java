package com.sib.ibanklosucl.controller;

import com.sib.ibanklosucl.dto.Employee;
import com.sib.ibanklosucl.service.CustomUserDetailsService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.CountDownLatch;

@RestController
@RequestMapping("/test")
@Slf4j
public class SessionTestController {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserSessionData usd;

    @GetMapping("/simulate-session-mix1")
public String simulateSessionMixing1(HttpServletRequest request, HttpSession session) {
    log.info("Starting session mixing simulation");
    CountDownLatch latch = new CountDownLatch(2);

    // Shared session and USD instance
    ServletRequestAttributes originalAttributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    Thread thread1 = new Thread(() -> {
        try {
            // Simulating Employee 1 Login
            simulateLogin1("SIBL11965", "password123", "192.168.1.101", originalAttributes, session,request);
        } finally {
            latch.countDown();
        }
    });

    Thread thread2 = new Thread(() -> {
        try {
            // Simulating Employee 2 Login
            simulateLogin1("SIBL11945", "password456", "192.168.1.102", originalAttributes, session,request);
        } finally {
            latch.countDown();
        }
    });

    try {
        thread1.start();
        Thread.sleep(50); // Artificial delay to increase race condition likelihood
        thread2.start();

        latch.await();

        log.info("Final Session State Check:");
        if (usd.getEmployee() != null) {
            log.info("Final session belongs to: {}", usd.getEmployee().getPpcno());
        } else {
            log.info("Final session is null!");
        }

        return "Simulation completed - Check logs for results";

    } catch (InterruptedException e) {
        log.error("Simulation interrupted", e);
        return "Simulation failed: " + e.getMessage();
    }
}



private void simulateLogin1(String username, String password, String remoteIP,
                           ServletRequestAttributes originalAttributes, HttpSession session,HttpServletRequest request) {
    try {
        // Set request attributes for the current thread
        RequestContextHolder.setRequestAttributes(originalAttributes, true);

        Model model = new ExtendedModelMap();
        log.info("Login process started for: {}", username);
         //session.invalidate();
           // session=request.getSession(true);

        // Call the authenticate method
        boolean authenticated = userDetailsService.authenticate(username, password, model,false);
        if (authenticated) {
            // Mimic setting values in usd, similar to loginauth()
            Employee employee = (Employee) model.getAttribute("employeeData");
            log.info("Employee details fetching completed: {}", employee != null ? employee.getPpcno() : "null");
            usd.setEmployee(employee);
            usd.setRemoteIP(remoteIP);

            // Simulate setting session attributes
            session.setAttribute("employeeData", employee);
            session.setAttribute("remoteIP", remoteIP);

            log.info("Session State after login - PPC: {}", usd.getEmployee().getPpcno());
        } else {
            log.warn("Authentication failed for: {}", username);
        }
    } catch (Exception e) {
        log.error("Error during login for: {}", username, e);
    } finally {
        // Reset request attributes for the thread
        RequestContextHolder.resetRequestAttributes();
    }
}

@GetMapping("/simulate-session-mix")
    public String simulateSessionMixing(HttpServletRequest request, HttpSession session) {
        log.info("Starting session mixing simulation");
        CountDownLatch latch = new CountDownLatch(2);

        // Capture the current request attributes for thread binding
        ServletRequestAttributes originalAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        // Thread 1: Simulate Employee 1 Login
        Thread thread1 = new Thread(() -> {
            try {
                RequestContextHolder.setRequestAttributes(originalAttributes);
                simulateLogin("SIBL11965", "password123", "192.168.1.101", session, request);
            } finally {
                RequestContextHolder.resetRequestAttributes();
                latch.countDown();
            }
        });

        // Thread 2: Simulate Employee 2 Login
        Thread thread2 = new Thread(() -> {
            try {
                RequestContextHolder.setRequestAttributes(originalAttributes);
                simulateLogin("SIBL11945", "password456", "192.168.1.102", session, request);
            } finally {
                RequestContextHolder.resetRequestAttributes();
                latch.countDown();
            }
        });

        try {
            thread1.start();
            Thread.sleep(50); // Artificial delay to create race condition
            thread2.start();

            latch.await();

            log.info("Final Session State Check:");
            if (usd.getEmployee() != null) {
                log.info("Final session belongs to: {}", usd.getEmployee().getPpcno());
            } else {
                log.info("Final session is null!");
            }

            return "Simulation completed - Check logs for results";

        } catch (InterruptedException e) {
            log.error("Simulation interrupted", e);
            return "Simulation failed: " + e.getMessage();
        }
    }

    private void simulateLogin(String username, String password, String remoteIP, HttpSession session, HttpServletRequest request) {
        try {
            Model model = new ExtendedModelMap();
            log.info("Login process started for: {}", username);

            // Authenticate the user
            boolean authenticated = userDetailsService.authenticate(username, password, model,false);
            if (authenticated) {
                Employee employee = (Employee) model.getAttribute("employeeData");
                log.info("Employee details fetched: {}", employee != null ? employee.getPpcno() : "null");

                // Manually set attributes in the session
                session.setAttribute("employeeData", employee);
                session.setAttribute("remoteIP", remoteIP);

                // Update UserSessionData (mocking session-scoped behavior)
                usd.setEmployee(employee);
                usd.setRemoteIP(remoteIP);

                log.info("Session State after login - PPC: {}", usd.getEmployee() != null ? usd.getEmployee().getPpcno() : "null");
            } else {
                log.warn("Authentication failed for: {}", username);
            }
        } catch (Exception e) {
            log.error("Error during login for: {}", username, e);
        }
    }




}
