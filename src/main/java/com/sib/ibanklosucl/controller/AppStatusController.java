package com.sib.ibanklosucl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
public class AppStatusController {
    @Autowired
    private Environment environment;
    @GetMapping("/status")
    public String getApplicationStatus() {
        String[] activeProfiles = environment.getActiveProfiles();
        if(activeProfiles.length>0) {
            return "Application is running with active profiles: " + Arrays.toString(activeProfiles);
        } else {
            return  "Application is running no profiles set";
        }
    }

}
