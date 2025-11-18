package com.sib.ibanklosucl.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request , Model model) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
         Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        String errorMessage = throwable!=null?throwable.getMessage():"";
        model.addAttribute("error",errorMessage);
        return "error";

    }
}
