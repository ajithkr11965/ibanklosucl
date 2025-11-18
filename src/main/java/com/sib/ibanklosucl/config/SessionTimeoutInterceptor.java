package com.sib.ibanklosucl.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class SessionTimeoutInterceptor implements HandlerInterceptor {
    @Autowired
    private final UserSessionData userSessionData;

    public SessionTimeoutInterceptor(UserSessionData userSessionData) {
        this.userSessionData = userSessionData;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (userSessionData.getEmployee()==null && !request.getRequestURI().contains("login") && !request.getRequestURI().contains("breapi")  && !request.getRequestURI().contains("assets") && !request.getRequestURI().contains("DSA") && !request.getRequestURI().contains("metricsapi") && !request.getRequestURI().contains("sso") && !request.getRequestURI().contains("test")  ) {
           // System.out.println(request.getRequestURI()+"-----ttt-----------");
            response.sendRedirect(request.getContextPath() + "/login?sessionExpired=true");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // Optional: Add additional handling after the request is processed
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Optional: Add any cleanup code if needed
    }
}
