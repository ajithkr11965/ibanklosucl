package com.sib.ibanklosucl.config;

import com.sib.ibanklosucl.config.secure.CachedBodyHttpServletRequest;
import com.sib.ibanklosucl.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private final UserService userService;

    private static final Pattern[] SQL_INJECTION_PATTERNS = {
            Pattern.compile("exec\\s", Pattern.CASE_INSENSITIVE), // EXEC keyword
            Pattern.compile("union\\s+select\\s", Pattern.CASE_INSENSITIVE), // UNION SELECT keyword
            Pattern.compile("insert\\s+into\\s", Pattern.CASE_INSENSITIVE), // INSERT INTO keyword
            Pattern.compile("select\\s+.*from\\s", Pattern.CASE_INSENSITIVE),// SELECT * FROM keyword
            Pattern.compile("drop\\s+table", Pattern.CASE_INSENSITIVE), // DROP TABLE keyword
            Pattern.compile("declare\\s+.*=", Pattern.CASE_INSENSITIVE), // DECLARE variable assignment
            Pattern.compile("1\\s*=\\s*1", Pattern.CASE_INSENSITIVE), // Tautological condition
            Pattern.compile("<"), // Less-than symbol
            Pattern.compile(">") // Greater-than symbol
    };



    private static final String X_XSS_PROTECTION_HEADER = "X-XSS-Protection";
    private static final String X_FRAME_OPTIONS_HEADER = "X-Frame-Options";
    private static final String X_CONTENT_TYPE_OPTIONS_HEADER = "X-Content-Type-Options";
    private static final String CONTENT_SECURITY_POLICY_HEADER = "Content-Security-Policy";



    @Value("${security.filter.excluded-paths:/assets/,/metricsapi/,/test/,/DSA/,/breapi/,/login,/sso/}")
    private String[] excludedPaths;


    public SecurityFilter( UserService userService) {
        this.userService = userService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return Arrays.stream(excludedPaths)
                .anyMatch(pattern -> path.contains(pattern.replace("**", ".*")));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

       // System.out.println(request.getRequestURI()+"----------------");
        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userSessionID") == null) {
        //   System.out.println(request.getRequestURI()+"-----ssss-----------");
            response.sendRedirect(request.getContextPath() + "/login?sessionExpired=true");
            return;
        }

        String userSessionID = (String) session.getAttribute("userSessionID");
        String userSessionPPC = (String) session.getAttribute("userSessionPPC");

        if (!userService.isValidSession(userSessionPPC, userSessionID)) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login?expired=true");
            return;
        }



        if (containsSqlInjection(request)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Suspicious Input detected");
            return;
        }

        // Check JSON body for SQL injection if it's a JSON request
        if (isJsonRequest(request)) {
            try {
                HttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);
                filterChain.doFilter(wrappedRequest, response);
                return;
            } catch (IOException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Suspicious Input detected");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }


    private boolean containsSqlInjection(HttpServletRequest request) {
        // Check request parameters for SQL injection
        boolean paramsContainSqlInjection = request.getParameterMap().values().stream()
                .flatMap(Arrays::stream)
                .anyMatch(this::containsSqlInjectionPattern);

        return paramsContainSqlInjection;
    }

    private boolean containsSqlInjectionPattern(String value) {
        return Arrays.stream(SQL_INJECTION_PATTERNS)
                .anyMatch(pattern -> pattern.matcher(value).find());
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().contains("application/json");
    }

}
