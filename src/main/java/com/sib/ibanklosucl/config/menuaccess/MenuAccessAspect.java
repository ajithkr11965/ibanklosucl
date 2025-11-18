package com.sib.ibanklosucl.config.menuaccess;

import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
public class MenuAccessAspect {

    // Specialized loggers for different purposes
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    private static final Logger log = LoggerFactory.getLogger(MenuAccessAspect.class);

    @Autowired
    private UserSessionData usd;

    @Pointcut("@annotation(requiresMenuAccess)")
    public void callAt(RequiresMenuAccess requiresMenuAccess) {}

    @Around("callAt(requiresMenuAccess)")
    public Object around(ProceedingJoinPoint joinPoint, RequiresMenuAccess requiresMenuAccess) throws Throwable {

        // Get request context for logging
        HttpServletRequest request = getCurrentRequest();
        String remoteIP = getClientIP(request);
        String sessionId = getSessionId(request);
        String userPPC = usd.getPPCNo();

        // Get method and controller information
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String controllerMethod = signature.getDeclaringType().getSimpleName() + "." + method.getName();
        String requestMapping = getRequestMapping(request);

        // Get required menu IDs
        String[] requiredMenuIds = requiresMenuAccess.menuIds();
        String requiredMenuIdsStr = Arrays.stream(requiredMenuIds).collect(Collectors.joining(","));

        // APPLICATION LOG: Menu access check initiated
        log.debug("Menu access check initiated for method: {} by PPC: {}", controllerMethod, userPPC);

        try {
            // Check if session is valid
            if (usd.getMenuList() == null) {
                // SECURITY LOG: Session expired access attempt
                securityLogger.error("MENU_ACCESS_SESSION_EXPIRED|PPC={}|IP={}|SESSION_ID={}|METHOD={}|URL={}|REQUIRED_MENUS={}|SEVERITY=HIGH",
                                   userPPC != null ? userPPC : "UNKNOWN", remoteIP, sessionId,
                                   controllerMethod, requestMapping, requiredMenuIdsStr);

                // AUDIT LOG: Failed access due to session expiry
                auditLogger.warn("MENU_ACCESS_DENIED_SESSION_EXPIRED|PPC={}|IP={}|METHOD={}|URL={}|REQUIRED_MENUS={}",
                                userPPC != null ? userPPC : "UNKNOWN", remoteIP, controllerMethod,
                                requestMapping, requiredMenuIdsStr);

                throw new RuntimeException("Session Expired !!");
            }

            // Get user's available menu IDs
            List<String> userMenuIds = usd.getMenuList().stream()
                    .map(menu -> menu.getMenuID())
                    .collect(Collectors.toList());
            String userMenuIdsStr = String.join(",", userMenuIds);

            // Check access for each required menu
            boolean hasAccess = false;
            String grantedMenu = "";

            for (String menuId : requiredMenuIds) {
                if ("PUBLIC".equalsIgnoreCase(menuId)) {
                    hasAccess = true;
                    grantedMenu = "PUBLIC";

                    // AUDIT LOG: Public resource access
                    auditLogger.info("MENU_ACCESS_PUBLIC|PPC={}|IP={}|METHOD={}|URL={}|MENU_ID={}|ACCESS_TYPE=PUBLIC",
                                    userPPC, remoteIP, controllerMethod, requestMapping, menuId);
                    break;

                } else if (usd.getMenuList().stream().anyMatch(menu -> menu.getMenuID().equals(menuId))) {
                    hasAccess = true;
                    grantedMenu = menuId;

                    // AUDIT LOG: Successful menu access
                    auditLogger.info("MENU_ACCESS_GRANTED|PPC={}|IP={}|METHOD={}|URL={}|GRANTED_MENU={}|USER_MENUS={}|ACCESS_TYPE=AUTHORIZED",
                                    userPPC, remoteIP, controllerMethod, requestMapping, menuId, userMenuIdsStr);
                    break;
                }
            }

            if (hasAccess) {
                // SECURITY LOG: Successful access
                securityLogger.info("MENU_ACCESS_SUCCESS|PPC={}|IP={}|SESSION_ID={}|METHOD={}|URL={}|GRANTED_MENU={}|REQUIRED_MENUS={}",
                                  userPPC, remoteIP, sessionId, controllerMethod, requestMapping,
                                  grantedMenu, requiredMenuIdsStr);

                // Execute the actual method
                long startTime = System.currentTimeMillis();
                Object result = joinPoint.proceed();
                long executionTime = System.currentTimeMillis() - startTime;

                // AUDIT LOG: Method execution completed
                auditLogger.info("MENU_ACCESS_METHOD_EXECUTED|PPC={}|IP={}|METHOD={}|URL={}|EXECUTION_TIME={}ms|STATUS=SUCCESS",
                                userPPC, remoteIP, controllerMethod, requestMapping, executionTime);

                return result;

            } else {
                // SECURITY LOG: Access denied - insufficient privileges
                securityLogger.warn("MENU_ACCESS_DENIED|PPC={}|IP={}|SESSION_ID={}|METHOD={}|URL={}|REQUIRED_MENUS={}|USER_MENUS={}|SEVERITY=MEDIUM",
                                  userPPC, remoteIP, sessionId, controllerMethod, requestMapping,
                                  requiredMenuIdsStr, userMenuIdsStr);

                // AUDIT LOG: Unauthorized access attempt
                auditLogger.warn("MENU_ACCESS_UNAUTHORIZED_ATTEMPT|PPC={}|IP={}|METHOD={}|URL={}|REQUIRED_MENUS={}|USER_MENUS={}|REASON=INSUFFICIENT_PRIVILEGES",
                                userPPC, remoteIP, controllerMethod, requestMapping,
                                requiredMenuIdsStr, userMenuIdsStr);

                // Check for potential privilege escalation attempt
                if (isPrivilegeEscalationAttempt(requiredMenuIds, userMenuIds)) {
                    securityLogger.error("POTENTIAL_PRIVILEGE_ESCALATION|PPC={}|IP={}|METHOD={}|URL={}|REQUIRED_MENUS={}|USER_MENUS={}|SEVERITY=CRITICAL",
                                       userPPC, remoteIP, controllerMethod, requestMapping,
                                       requiredMenuIdsStr, userMenuIdsStr);
                }

                throw new RuntimeException("Access denied");
            }

        } catch (RuntimeException e) {
            // Re-throw known exceptions
            throw e;

        } catch (Throwable e) {
            // SECURITY LOG: Unexpected error during access check
            securityLogger.error("MENU_ACCESS_ERROR|PPC={}|IP={}|METHOD={}|URL={}|ERROR_TYPE={}|ERROR_MSG={}|SEVERITY=HIGH",
                               userPPC, remoteIP, controllerMethod, requestMapping,
                               e.getClass().getSimpleName(), e.getMessage());

            // APPLICATION LOG: Detailed error for debugging
            log.error("Unexpected error during menu access check for method: {} by PPC: {}",
                     controllerMethod, userPPC, e);

            throw e;
        }
    }

    /**
     * Get current HTTP request from RequestContextHolder
     */
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest();
        } catch (Exception e) {
            log.warn("Could not retrieve current request: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get client IP address from request
     */
    private String getClientIP(HttpServletRequest request) {
        if (request == null) return "UNKNOWN";

        // Use your existing CommonUtils method if available
        try {
            return CommonUtils.getClientIp(request);
        } catch (Exception e) {
            // Fallback implementation
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }

            String xRealIP = request.getHeader("X-Real-IP");
            if (xRealIP != null && !xRealIP.isEmpty()) {
                return xRealIP;
            }

            return request.getRemoteAddr();
        }
    }

    /**
     * Get session ID from request
     */
    private String getSessionId(HttpServletRequest request) {
        if (request == null) return "UNKNOWN";

        try {
            HttpSession session = request.getSession(false);
            return session != null ? session.getId() : "NO_SESSION";
        } catch (Exception e) {
            return "ERROR_GETTING_SESSION";
        }
    }

    /**
     * Get request mapping/URL from request
     */
    private String getRequestMapping(HttpServletRequest request) {
        if (request == null) return "UNKNOWN";

        try {
            String uri = request.getRequestURI();
            String contextPath = request.getContextPath();
            if (contextPath != null && uri.startsWith(contextPath)) {
                uri = uri.substring(contextPath.length());
            }
            return request.getMethod() + " " + uri;
        } catch (Exception e) {
            return "ERROR_GETTING_URI";
        }
    }

    /**
     * Check if this looks like a privilege escalation attempt
     * This is a simple heuristic - you can enhance based on your menu hierarchy
     */
    private boolean isPrivilegeEscalationAttempt(String[] requiredMenuIds, List<String> userMenuIds) {
        // Define high-privilege menu IDs that should trigger alerts
        String[] highPrivilegeMenus = {"ADMIN", "SUPER", "CONFIG", "USER_MGMT", "REPORT_ALL", "AUDIT"};

        for (String requiredMenu : requiredMenuIds) {
            for (String highPrivMenu : highPrivilegeMenus) {
                if (requiredMenu.contains(highPrivMenu) && !userMenuIds.contains(requiredMenu)) {
                    return true;
                }
            }
        }
        return false;
    }
}
