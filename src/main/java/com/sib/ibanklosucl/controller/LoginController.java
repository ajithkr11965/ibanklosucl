package com.sib.ibanklosucl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.config.menuaccess.RequiresMenuAccess;
import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.dashboard.*;
import com.sib.ibanklosucl.dto.mssf.MSSFCustomerDTO;
import com.sib.ibanklosucl.dto.mssf.MssfCustomerDetailsDTO;
import com.sib.ibanklosucl.exception.AuthenticationServiceException;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.model.menuaccess.DashboardData;
import com.sib.ibanklosucl.model.menuaccess.NewsAlert;
import com.sib.ibanklosucl.model.mssf.MSSFCustomerData;
import com.sib.ibanklosucl.model.notification.Notification;
import com.sib.ibanklosucl.model.user.EmployeeNotification;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.UserSelectRepository;
import com.sib.ibanklosucl.repository.ValidationRepository;
import com.sib.ibanklosucl.service.*;
import com.sib.ibanklosucl.service.dashboard.DashboardService;
import com.sib.ibanklosucl.service.dashboard.NewsAlertService;
import com.sib.ibanklosucl.service.dashboard.NewsFeedService;
import com.sib.ibanklosucl.service.dashboard.notification.NotificationService;
import com.sib.ibanklosucl.service.mssf.MSSFService;
import com.sib.ibanklosucl.service.mssf.PDFGeneratorService;
import com.sib.ibanklosucl.service.user.CongratsService;
import com.sib.ibanklosucl.service.user.UserService;
import com.sib.ibanklosucl.service.vlsr.*;
import com.sib.ibanklosucl.utilies.AESUtil;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.Constants;
import com.sib.ibanklosucl.utilies.UserSessionData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class LoginController {
    @Value("${app.dev-mode:true}")
    private boolean devMode;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserSessionData usd;

    @Autowired
    private final VehicleLoanLockService vehicleLoanLockService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final VLWiCreateService vlWiCreateService;
    @Autowired
    private VehicleLoanMasterService vlservice;

    @Autowired
    private NewsFeedService newsFeedService;
    @Autowired
    private NewsAlertService newsAlertService;


    @Autowired
    private VLFileUploadService vlFileUploadService;

    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private ValidationRepository validationRepository;


    @Autowired
    private VLEmploymentService vlemploymentService;

    @Autowired
    private VLEmploymentempService vlEmploymentempService;

    @Autowired
    private VehicleLoanAllotmentService vlallotmentservice;

    @Autowired
    private UserSelectRepository UserSelectRepositoryallot;
    @Autowired
    private RemarksHistService remarkshistservice;
    @Autowired
    private MenuAccessService menuAccessService;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private VehicleLoanTatService vehicleLoanTatService;
    @Autowired
    private VehicleLoanAmberService vehicleLoanAmberService;
    @Autowired
    private VehicleLoanWaiverService loanWaiverService;
    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private CongratsService congratsService;
    @Autowired
    private MSSFService mssfService;
    @Autowired
    private AESUtil aesUtil;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private PDFGeneratorService pdfGeneratorService;
    @Autowired
    private Environment env;
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");


    @Value("${sso.token.secret}")
    private String tokenSecret;
    @Value("${centralAuthUrl}")
    private String centralAuthUrl;

    public LoginController(VehicleLoanLockService vehicleLoanLockService, UserService userService, VLWiCreateService vlWiCreateService) {

        this.vehicleLoanLockService = vehicleLoanLockService;
        this.userService = userService;
        this.vlWiCreateService = vlWiCreateService;
    }

    @RequestMapping("wicreate")
    public String handleRequest(@RequestParam String action, @RequestParam(required = false) String slno, HttpServletRequest request, Model model) {
        if (usd.getEmployee() == null)
            return "login";


        log.info("wicreate enter ppc:-{} action {} slno:-{}", usd.getPPCNo(), action, slno);
        action = usd.getTrans_slno() == null && slno.isBlank() ? "Add" : "Modify";
        slno = usd.getTrans_slno() != null ? usd.getTrans_slno() : slno;
        String datamod = "INIT";

        String ppc = usd.getEmployee().getPpcno();
        switch (action) {
            case "Add":
                datamod = "INIT";
                model = vlWiCreateService.addEntry(request, model);
                model.addAttribute("datamod", datamod);
                log.info("wicreate EXIT ppc:-{} action {} slno:-{}", usd.getPPCNo(), action, slno);
                return "wicreate";
            case "Modify":
                datamod = "MODIFY";
                VehicleLoanTat vlTat = vehicleLoanTatService.getTatBySlno(Long.valueOf(slno));
                String currentQueue = "";
                if (vlTat != null) {
                    currentQueue = vlTat.getQueue();
                }
                if (currentQueue.equals("BS") && vehicleLoanTatService.makerCheckerAreSame(Long.valueOf(slno), ppc)) {
                    model.addAttribute("makerCheckerSame", "Y");
                }
                model = vlWiCreateService.modifyEntry(slno, request, model);
                model.addAttribute("datamod", datamod);
                log.info("wicreate EXIT ppc:-{} action {} slno:-{}", usd.getPPCNo(), action, slno);
                return "wicreate";
            case "SF5":
                datamod = "MODFIY";
                vlTat = vehicleLoanTatService.getTatBySlno(Long.valueOf(slno));
                currentQueue = "";
                if (vlTat != null) {
                    currentQueue = vlTat.getQueue();
                }
                if (currentQueue.equals("BS") && vehicleLoanTatService.makerCheckerAreSame(Long.valueOf(slno), ppc)) {
                    model.addAttribute("makerCheckerSame", "Y");
                }
                model = vlWiCreateService.modifyEntry(slno, request, model);

                model.addAttribute("datamod", datamod);
                log.info("wicreate EXIT ppc:-{} action {} slno:-{}", usd.getPPCNo(), action, slno);
                return "wicreate";

            default:
                model.addAttribute("message", Constants.Messages.SOMETHING_ERROR);
                log.info("wicreate EXIT ppc:-{} action {} slno:-{}", usd.getPPCNo(), action, slno);
                return "error";
        }

    }

    @PostMapping("/wichecker")
    public String CheckerRequest(@RequestParam String action, @RequestParam(required = false) String slno, HttpServletRequest request, Model model) {

        slno = usd.getTrans_slno() != null ? usd.getTrans_slno() : slno;
        String ppc = usd.getEmployee().getPpcno();
        if (vehicleLoanTatService.makerCheckerAreSame(Long.valueOf(slno), ppc)) {
            model.addAttribute("error", Constants.Messages.MAKER_CHECKER_MISMATCH);
            return "error";
        }
        switch (action) {
            case "BC":
                model = vlWiCreateService.fetchcheckerDetails(slno, request, model);
                return "branchchecker";
            case "CS":
                model = vlWiCreateService.fetchCRTcheckerDetails(slno, request, model);
                return "crtchecker";
            case "AQ":
                model = vlWiCreateService.fetchcheckerDetails(slno, request, model);
                return "allotmentView";
            default:
                model.addAttribute("message", Constants.Messages.SOMETHING_ERROR);
                return "error";
        }
    }

    @GetMapping("/wicheckerget")
    public String CheckerFetchRequest(@RequestParam String action, @RequestParam(required = false) String slno, HttpServletRequest request, Model model) {
        slno = usd.getTrans_slno() != null ? usd.getTrans_slno() : slno;
        action = "BC";
        switch (action) {
            case "BC":
                model = vlWiCreateService.fetchcheckerDetails(slno, request, model);
                return "branchchecker";
            default:
                model.addAttribute("message", Constants.Messages.SOMETHING_ERROR);
                return "error";
        }
    }


    @RequestMapping("/wichecker2")
    public String CheckerRequest2(@RequestParam(required = false) String action, @RequestParam(required = false) String slno, HttpServletRequest request, Model model) {
        log.info("Received request for /wichecker2 with action: {}, slno: {}, winum: {}, lockflg: {}",
                action, slno);
        try {
            slno = usd.getTrans_slno() != null ? usd.getTrans_slno() : slno;
            if ("BC".equals(action)) {
                log.info("Fetching checker details for slno: {}", slno);
                model = vlWiCreateService.fetchcheckerDetails(slno, request, model);
                return "branchchecker2";
            } else {
                log.warn("Invalid action received: {}", action);
                model.addAttribute("message", "Invalid action: " + action);
                return "error";
            }
        } catch (Exception e) {
            log.error("An error occurred in CheckerRequest2", e);
            model.addAttribute("message", "An error occurred: " + e.getMessage());
            return "error";
        }
    }

    @RequestMapping("/wirmmodify")
    public String handleRMRequest(@RequestParam(required = false) String slno, HttpServletRequest request, Model model) {
        if (usd.getEmployee() == null)
            return "login";
        slno = usd.getTrans_slno() != null ? usd.getTrans_slno() : slno;
        model = vlWiCreateService.modifyRMEntry(slno, request, model);
        return "wirmmodify";
    }

    @RequestMapping("/insOut")
    public String insOpt(@RequestParam(required = false) String slno, HttpServletRequest request, Model model) {
        if (usd.getEmployee() == null)
            return "login";
        slno = usd.getTrans_slno() != null ? usd.getTrans_slno() : slno;
        model = vlWiCreateService.insOptOut(slno, request, model);
        return "ins/insOut";
    }

    @RequestMapping("/wicpcchecker")
    public String handleRCRequest(@RequestParam(required = false) String slno, HttpServletRequest request, Model model) {
        slno = usd.getTrans_slno() != null ? usd.getTrans_slno() : slno;
        model = vlWiCreateService.modifyRCEntry(slno, request, model);
        return "wicpcchecker";
    }

    @RequestMapping("/docqueue")
    public String docQueue(@RequestParam(required = false) String slno, HttpServletRequest request, Model model) {
        if (usd.getEmployee() == null)
            return "login";
        slno = usd.getTrans_slno() != null ? usd.getTrans_slno() : slno;
        model = vlWiCreateService.modifyBDEntry(slno, request, model);
        return "docqueue/docqueue";
    }

    @RequestMapping("/waiverqueue")
    public String waiverQueue(@RequestParam(required = false) String slno, HttpServletRequest request, Model model) {
        if (usd.getEmployee() == null)
            return "login";
        slno = usd.getTrans_slno() != null ? usd.getTrans_slno() : slno;
        model = vlWiCreateService.modifyWaiverEntry(slno, request, model);
        //model = vlWiCreateService.modifyBDEntry(slno, request, model);
        return "waiverqueue/waiverqueue";
    }

    @RequestMapping("/wilist")
    public String wilist(Model model) {
        usd.setTrans_slno(null);
        //System.out.println(usd.getPPCNo());
        return "wilist";
    }

    @RequestMapping({"/login", "/"})
    public String login(@RequestParam(value = "sessionExpired", required = false) String sessionExpired, @RequestParam(required = false) String expired, @RequestParam(required = false) String error, Model model, HttpServletRequest request) {
        String str = "";
        if (sessionExpired != null) {
            model.addAttribute("error", "Your session has expired. Please log in again.");
            str = "?sessionExpired=true";
        }
        if (expired != null) {
            model.addAttribute("error", "Your session was expired because you logged in from another device.");
            str = "?expired=true";
        }
        if (error != null) {
            //    model.addAttribute("error", "Your session was expired because you logged in from another device.");
            str = "?error=" + error;
        }
        usd.clear();
        if (Arrays.asList(env.getActiveProfiles()).contains("dev")) {
            return "login";
        }
        return "redirect:" + centralAuthUrl + "login" + str;
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @RequestMapping("/markAsRead")
    public String congrats() {
        congratsService.markNotificationAsSeen(usd.getPPCNo());
        return "redirect:/dashboard";
    }

    @GetMapping("/profilek")
    public String profilek() {
        return "profilek";
    }

    @RequestMapping("/loading")
    public String Loading() {
        return "vllogin";
    }

    @GetMapping("/sample")
    public String redirectUrl() {
        return "redirect";
    }

    @RequiresMenuAccess(menuIds = {"BM"})
    @GetMapping("/bmlist")
    public String branchEntryQueue(Model model) {
        List<VehicleLoanMasterDTO> entryQueue = vlservice.getEntryQueue(usd.getSolid());
        model.addAttribute("entryQueue", entryQueue);
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        usd.setTrans_slno(null);//Reset wi
        return "bmqueue";
        //return "bmlist";
    }

    @RequiresMenuAccess(menuIds = {"BM"})
    @GetMapping("/bmlist1")
    public String branchEntryQueue1(Model model) {
        List<VehicleLoanMasterDTO> entryQueue = vlservice.getEntryQueue("0364");
        model.addAttribute("entryQueue", entryQueue);
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        return "bmlist";
    }

    @RequiresMenuAccess(menuIds = {"PUBLIC"})
    @GetMapping("/dashboard")
    public String dashbaord(Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        String usersolid = usd.getSolid();
        usd.setTrans_slno(null);
        String employeeType = "SIB";

        List<Message> messages = newsFeedService.fetchMessages(usersolid);
        // List<Message> finacleMessages = newsFeedService.fetchFinacleMessages();
        List<MenuList> accessibleMenus = menuAccessService.getAccessibleMenus();
        Collections.sort(accessibleMenus, Comparator.comparingLong(MenuList::getOrderid));

        model.addAttribute("accessibleMenus", accessibleMenus);
        model.addAttribute("messages", messages);
//        model.addAttribute("finacleMessages", finacleMessages);
        model.addAttribute("userName", userName);
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());

        QueueCountDTO queueCountDTO = vlservice.getStatusCountsBySolId(usd.getSolid());
        model.addAttribute("queueCountDTO", queueCountDTO);

        log.info("Office Type:" + usd.getEmployee().getOffType());

        DashboardData dashboardData = dashboardService.prepareDashboardData();
        model.addAttribute("dashboardData", dashboardData);


        if (usd.getEmployee().getOffType().equalsIgnoreCase("BRANCH")) {
            // Add the alert message to the model

            EmployeeNotification employeeNotification = congratsService.sendDisbursementNotification(usd.getSolid(), usd.getPPCNo());
            if (employeeNotification != null) {
                model.addAttribute("message", employeeNotification.getMessage());
                return "congrats";
            }
            return "dashboard_branch";
        } else {
            List<NewsAlert> newsAlerts = newsAlertService.getActiveNewsAlerts();
            model.addAttribute("newsAlerts", newsAlerts);
            return "dashboard";
        }
    }


    @GetMapping("/notifications")
    public List<Notification> getUserNotifications(@RequestParam String ppc,
                                                   @RequestParam(required = false) String category,
                                                   @RequestParam(required = false) String priority) {
        return notificationService.getFilteredNotifications(ppc, category, priority);
    }


    @GetMapping("/notifications/paginated")
    public ResponseEntity<Page<Notification>> getNotificationsWithPagination(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "solId") String solId,
            @RequestParam(value = "ppc") String ppc) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Notification> notifications = notificationService.getUnreadNotifications(solId, ppc, pageable);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Page.empty());
        }
    }


    @PostMapping("/notifications/markAsRead")
    public ResponseEntity<Boolean> markNotificationsAsRead(@RequestParam String solId, @RequestParam String ppc) {
        try {
            notificationService.markNotificationsAsRead(solId, ppc);
            return ResponseEntity.ok(true);  // Return true if successful
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);  // Return false in case of error
        }
    }


    // Fetch unread notification count by solId and ppc
    @GetMapping("/notifications/count")
    public @ResponseBody int getNotificationCount(@RequestParam String ppc) {
        String solId = usd.getSolid();
        return (int) notificationService.getUnreadNotificationCount(solId, ppc);
    }


    @PostMapping("/loginauth")
    public String loginauth(@RequestParam("userName") String username,
                            @RequestParam("password") String password,
                            HttpSession session, HttpServletRequest request, Model model) {
        try {
            if (username.toUpperCase().contains("SIBL")) {
                username = username.toUpperCase().replace("SIBL", "");
            }
            log.info("Login started for user: {}", username);
            UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(username, password);
            boolean authenticated = customUserDetailsService.authenticate(username, password, model, false);
            if (authenticated) {
                return loginValidations(model, session, request);
            } else {
                log.warn("Authentication failed for user: {}", username);
                return "login";
            }
        } catch (AuthenticationServiceException e) {
            log.error("Authentication error for user: {}", username, e);
            //   model.addAttribute("error", "Authentication failed. Please try again.");
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @PostMapping("/sso/user/{token}")
    @ResponseBody
    public ResponseDTO updateToken(@PathVariable String token) throws Exception {
        log.info("Token Received {}", token);
        return tokenService.parseToken(token);
    }

    @GetMapping("/sso/validate")
    public String validateSsoToken(
            @RequestParam String token,
            @RequestParam(required = false) String refreshToken,
            HttpServletRequest request,
            HttpSession session,
            Model model) {
        try {
            String appId = "VL";
            String remoteXIP = CommonUtils.getClientIp(request);
            log.info("Validating SSO token for application {}", appId);
            securityLogger.info("SSO_TOKEN_VALIDATION_ATTEMPT|APP_ID={}|IP={}|TOKEN_PREFIX={}",
                    appId, remoteXIP, token.substring(0, Math.min(10, token.length())));

            // Validate token
            Claims claims = Jwts.parser()
                    .setSigningKey(tokenSecret)
                    .parseClaimsJws(token)
                    .getBody();
            // Verify token is for this application
            String username = claims.getSubject();
            String decryptedJson = aesUtil.decrypt(claims.get("userData", String.class));
            Map<String, String> userParsed = mapper.readValue(decryptedJson, Map.class);
            String targetApp = userParsed.get("targetApp");
            String ppcno = userParsed.get("ppcno");
            securityLogger.info("SSO_TOKEN_VALIDATION_SUCCESS|PPC={}|IP={}|TARGET_APP={}|USERNAME={}",
                    ppcno, remoteXIP, targetApp, username);
            if (!targetApp.equals(appId)) {
                securityLogger.error("SSO_TOKEN_APP_MISMATCH|PPC={}|IP={}|EXPECTED_APP={}|RECEIVED_APP={}|SEVERITY=HIGH",
                        ppcno, remoteXIP, appId, targetApp);
                log.warn("Token targetApp mismatch:  ppcno {} expected {}, got {}", ppcno, appId, targetApp);
                return "redirect:/login?error=invalid_token";
            }
            String remoteIP = userParsed.get("remoteIP");
            String existIP = CommonUtils.getClientIp(request);

            if (!existIP.equals(remoteIP) && !devMode) {
                securityLogger.error("SSO_TOKEN_IP_MISMATCH|PPC={}|TOKEN_IP={}|ACTUAL_IP={}|DEV_MODE={}|SEVERITY=HIGH",
                        ppcno, remoteIP, remoteIP, devMode);
                log.warn("Token IP mismatch: ppcno {} , expected {}, got {}", ppcno, remoteIP, existIP);
                return "redirect:/login?error=invalid_ip";
            }
            if (!ppcno.equals(username)) {
                securityLogger.info("SSO_AUTHENTICATION_SUCCESS|PPC={}|IP={}|SESSION_ID={}",
                        ppcno, remoteXIP, session.getId());
                log.warn("Username  mismatch: ppcno {} , expected {}, got {}", ppcno, username, ppcno);
                return "redirect:/login?error=invalid_user";
            }
            log.info("Token validated for user {} (PPC: {})", username, ppcno);
            // Store tokens in session
            usd.setSsoToken(token);
            boolean authenticated = customUserDetailsService.authenticate(username, "", model, true);
            if (authenticated) {
                securityLogger.info("SSO_AUTHENTICATION_SUCCESS|PPC={}|IP={}|SESSION_ID={}",
                        ppcno, remoteXIP, session.getId());
                return loginValidations(model, session, request);
            } else {
                securityLogger.warn("SSO_AUTHENTICATION_FAILED|PPC={}|IP={}|REASON=USER_SERVICE_FAILED",
                        ppcno, remoteXIP);
                log.warn("Authentication failed for user: {}", username);
                return "login";
            }
        } catch (ExpiredJwtException e) {
            // SECURITY LOG: Expired token
            securityLogger.warn("SSO_TOKEN_EXPIRED|IP={}|ERROR={}",
                    CommonUtils.getClientIp(request), e.getMessage());
            return "redirect:/login?error=token_expired";

        } catch (SignatureException e) {
            // SECURITY LOG: Token tampering attempt (CRITICAL)
            securityLogger.error("SSO_TOKEN_SIGNATURE_INVALID|IP={}|ERROR={}|SEVERITY=CRITICAL",
                    CommonUtils.getClientIp(request), e.getMessage());
            return "redirect:/login?error=invalid_token";
        } catch (Exception e) {
            securityLogger.error("SSO_TOKEN_VALIDATION_ERROR|IP={}|ERROR_TYPE={}|ERROR_MSG={}",
                    CommonUtils.getClientIp(request), e.getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
            log.error("Token validation failed", e);
            return "redirect:/login?error=invalid_token";
        }
    }

    public String loginValidations(Model model, HttpSession session, HttpServletRequest request) {
        String remoteIP = CommonUtils.getClientIp(request);
        auditLogger.info("USER_SESSION_CLEANUP|SESSION_ID={}|IP={}", session.getId(), remoteIP);
        usd.clear();
        log.info("Cleared previous session data");
        Employee employeeData = (Employee) model.getAttribute("employeeData");
        auditLogger.info("USER_LOGIN|PPC={}|EMPLOYEE_ID={}|IP={}|SESSION_ID={}|SOL_ID={}|BRANCH={}",
                employeeData.getPpcno(), employeeData.getPpcno(), remoteIP,
                session.getId(), employeeData.getPpcAvailSol(), employeeData.getBrName());
        session.setAttribute("employeeData", employeeData);
        usd.setEmployee(employeeData);
        usd.setRemoteIP(CommonUtils.getClientIp(request));
        log.info("Set new employee data for PPC: {}", employeeData.getPpcno());
        boolean isSuperUser = validationRepository.isSuperUSer(remoteIP, employeeData.getPpcno());
        usd.getEmployee().setSuperUser(isSuperUser);
        auditLogger.info("USER_PRIVILEGE_ASSIGNED|PPC={}|IS_SUPER_USER={}|IP={}",
                employeeData.getPpcno(), isSuperUser, remoteIP);
        if (model.containsAttribute("reportees")) {
            session.setAttribute("reportees", model.getAttribute("reportees"));
        }
        if (model.containsAttribute("clusterSols")) {
            session.setAttribute("clusterSols", model.getAttribute("clusterSols"));
        }
        if (model.containsAttribute("saleSols")) {
            session.setAttribute("saleSols", model.getAttribute("saleSols"));
        }
        if (model.containsAttribute("userRoles")) {
            session.setAttribute("userRoles", model.getAttribute("userRoles"));
        }
        if (model.containsAttribute("menuList")) {
            List<MenuList> menuList = (List<MenuList>) model.getAttribute("menuList");
            auditLogger.info("USER_MENU_ACCESS_ASSIGNED|PPC={}|MENU_COUNT={}|MENUS={}",
                    employeeData.getPpcno(), menuList.size(),
                    menuList.stream().map(MenuList::getMenuDesc).collect(Collectors.joining(",")));
            session.setAttribute("menuList", menuList);
            if (model.getAttribute("menuList") != null) {
                usd.setMenuList((List<MenuList>) model.getAttribute("menuList"));
            } else {
                usd.setMenuList(new ArrayList<>());
            }
        }

        String userName = (String) model.getAttribute("username");
        usd.setUserName(userName);
        //Releasing ALL Lock FOR the PPC
        auditLogger.info("VEHICLE_LOCKS_RELEASED|PPC={}|ACTION=RELEASE_ALL_LOCKS", employeeData.getPpcno());
        vehicleLoanLockService.ReleaseAllLock(usd.getPPCNo());
        com.sib.ibanklosucl.model.user.User user = userService.login(usd.getPPCNo(), usd.getRemoteIP(), usd.getSolid());
        auditLogger.info("USER_SERVICE_LOGIN|PPC={}|USER_SESSION_ID={}|SOL_ID={}|IP={}",
                employeeData.getPpcno(), user.getSessionId(), employeeData.getPpcAvailSol(), remoteIP);
        tokenService.updateAllToken(user.getSessionId(), usd.getPPCNo());
        auditLogger.info("USER_TOKENS_UPDATED|PPC={}|SESSION_ID={}|ACTION=UPDATE_ALL_TOKENS",
                employeeData.getPpcno(), user.getSessionId());
        session.setAttribute("userSessionPPC", usd.getPPCNo());
        session.setAttribute("userSessionID", user.getSessionId());
        usd.getEmployee().setLhUser(fetchRepository.isLMPPC(usd.getPPCNo()));
        usd.getEmployee().setRahUser(fetchRepository.isRAHPPC(usd.getPPCNo()));
        log.info("Login completed successfully for PPC: {}", usd.getPPCNo());
        auditLogger.info("USER_LOGIN_COMPLETED|PPC={}|SESSION_ID={}|REDIRECT_TO=loading",
                employeeData.getPpcno(), session.getId());
        return "redirect:/loading";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String remoteIP = CommonUtils.getClientIp(request);
        log.info("Processing logout request");
        // Get refresh token for revocation
        String refreshToken = null;
        HttpSession session = request.getSession(false);
        String sessionId = session != null ? session.getId() : "UNKNOWN";
        String ppcno = usd.getPPCNo();
        auditLogger.info("USER_LOGOUT_INITIATED|PPC={}|SESSION_ID={}|IP={}", ppcno, sessionId, remoteIP);
        securityLogger.info("USER_SESSION_INVALIDATED|PPC={}|SESSION_ID={}|IP={}", ppcno, sessionId, remoteIP);
        if (session != null) {
            auditLogger.info("USER_SESSION_CLEANUP|PPC={}|SESSION_ID={}|ACTION=INVALIDATE", ppcno, sessionId);
//            refreshToken = (String) session.getAttribute("refreshToken");
            session.invalidate();
        }
        // Clear security context
        SecurityContextHolder.clearContext();
        auditLogger.info("USER_LOGOUT_COMPLETED|PPC={}|IP={}|REDIRECT_TO=central_logout", ppcno, remoteIP);
        // Redirect to central logout with refresh token if available
        if (refreshToken != null) {
            return "redirect:" + centralAuthUrl + "centralLogout?refreshToken=" + refreshToken;
        } else {
            return "redirect:" + centralAuthUrl + "centralLogout";
        }
    }

    @GetMapping("/allotment")
    @RequiresMenuAccess(menuIds = {"ALLOT"})
    public String allotmentList(Model model) {
        //to find rec ie to be  alloted --- pending for allotment
        List<AllotmentDTO> allotmentList = vlallotmentservice.getAllotment("RA");
        model.addAttribute("allotmentList", allotmentList);

        //to find rec ie already alloted
        List<AllotmentDTO> allotmentListAlloted = vlallotmentservice.getAllotment("RM");
        model.addAttribute("allotmentListAlloted", allotmentListAlloted);

        List<UserSelectDTO> userdetailsallot = UserSelectRepositoryallot.getUserList();
        model.addAttribute("userdetailsallot", userdetailsallot);
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        return "allotmentList";
    }


    @GetMapping("/remarks")
    public String remarksPage(@RequestParam("slno") String slno, Model model) {
        List<RemarksHistDTO> remarksList = remarkshistservice.getRemarksList(slno);
        model.addAttribute("remarksList", remarksList);
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        List<UserSelectDTO> userdetailsallot = UserSelectRepositoryallot.getUserList();
        model.addAttribute("userdetailsallot", userdetailsallot);
        return "RemarksHist";
    }


    @GetMapping("/bslist")
    @RequiresMenuAccess(menuIds = {"BS"})
    public String brSendbackList(Model model) {
        List<Map<String, String>> entryQueue = vlservice.getBsQueue(usd.getSolid());
        model.addAttribute("bsQueue", entryQueue);
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        usd.setTrans_slno(null);//Reset wi
        return "bslist";
    }

    @GetMapping("/bclist")
    @RequiresMenuAccess(menuIds = {"BC"})
    public String branchCheckerQueue(Model model) {
        List<VehicleLoanMasterDTO> entryQueue = vlservice.getBCQueue(usd.getSolid());
        model.addAttribute("bcQueue", entryQueue);
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        usd.setTrans_slno(null);//Reset wi
        return "bcqueue";
        //return "bmlist";
    }

    @GetMapping("/rbcpcchecker")
    @RequiresMenuAccess(menuIds = {"RBCC"})
    public String rbcpcChecker(Model model) {
        List<VehicleLoanRBCPCDTO> entryQueue = vlservice.getRBCQueue(usd.getPPCNo());
        model.addAttribute("rbcQueue", entryQueue);
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        usd.setTrans_slno(null);//Reset wi
        return "rbcpcchecker";
    }

    @GetMapping("/doclist")
    @RequiresMenuAccess(menuIds = {"BD"})
    public String docqueue(Model model) {
        List<VehicleLoanMasterDTO> bdQueue = vlservice.getDOCQueue(usd.getSolid());
        model.addAttribute("bdQueue", bdQueue);
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        usd.setTrans_slno(null);//Reset wi
        return "bdqueue";
    }

    @GetMapping("/waiverlist")
    @RequiresMenuAccess(menuIds = {"WAIVE"})
    public String waiverChecker(Model model) {
        List<WaiverSubtaskDTO> waiverQueue = vlservice.findByWaiverSubtask(usd.getPPCNo());
        model.addAttribute("waiverQueue", waiverQueue);
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        usd.setTrans_slno(null);//Reset wi
        return "waiverchecker";
    }

    @RequiresMenuAccess(menuIds = {"CRTC"})
    @GetMapping("/crtlist")
    public String CRTQueue(Model model) {
        List<VehicleLoanMasterDTO> entryQueue = vlservice.getCRTQueue(usd.getSolid());
        model.addAttribute("crtQueue", entryQueue);
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        usd.setTrans_slno(null);//Reset wi
        return "crtqueue";
        //return "bmlist";
    }

    @GetMapping("/cpcmakerlist")
    @RequiresMenuAccess(menuIds = {"RBCM"})
    public String cpcMakerQueue(Model model) {
        List<VehicleLoanMasterDTO> cpcQueue = vlservice.getRMQueue();
        model.addAttribute("rmQueue", cpcQueue);
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        usd.setTrans_slno(null);//Reset wi
        return "rmqueue";
    }

    @RequiresMenuAccess(menuIds = {"CA"})
    @GetMapping("/calist")
    public String crtAmberList(Model model) {
        List<VehicleLoanMasterDTO> entryQueue = vlservice.getCRTAmberQueue();
        model.addAttribute("crtQueue", entryQueue);
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        usd.setTrans_slno(null);//Reset wi
        return "crtamberqueue";
    }

    @RequiresMenuAccess(menuIds = {"INSOPTOUT"})
    @GetMapping("/insOptOut")
    public String insOptOut(Model model) {
        List<VehicleLoanMasterDTO> entryQueue = vlservice.getInsData(usd.getSolid());
        model.addAttribute("insQueue", entryQueue);
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        usd.setTrans_slno(null);
        return "ins/insOptOut";
    }


    @PostMapping("/wicrtamber")
    public String handleCARequest(@RequestParam @Validated String slno, HttpServletRequest request, Model model) {
        if (usd.getEmployee() == null)
            return "login";
        slno = usd.getTrans_slno() != null ? usd.getTrans_slno() : slno;
        model = vlWiCreateService.modifyCRTAmber(slno, request, model);
        return "wicrtamber";
    }

    @PostMapping("/wienquiryfetch")
    public String wiEnquiryFetch(@RequestParam @Validated String slno, HttpServletRequest request, Model model) {
        if (usd.getEmployee() == null)
            return "login";
        slno = usd.getTrans_slno() != null ? usd.getTrans_slno() : slno;
        model = vlWiCreateService.modifyCRTAmber(slno, request, model);
        return "wienquiryfetch";
    }

    @RequestMapping("/releaselock")
    public String ReleaseLock(@RequestParam("slnobk") String slno, @RequestParam("redirecturl") String redirecturl) {
        vehicleLoanLockService.ReleaseLock(Long.valueOf(slno), usd.getPPCNo());
        loanWaiverService.releaseSubQueueLocks(Long.valueOf(slno), usd.getPPCNo());
        return "redirect:/" + redirecturl;
    }

    @RequestMapping("/releaseMainAndSublock")
    public String ReleaseMainAndSubLock(@RequestParam("slnobk") String slno, @RequestParam("winum") String winum, @RequestParam("redirecturl") String redirecturl) {
        vehicleLoanLockService.ReleaseLock(Long.valueOf(slno), usd.getPPCNo());

        return "redirect:/" + redirecturl;
    }

    @PostMapping("/bmdocupload")
    public String DocListForward(@RequestParam Long slno, Model model) {
        VehicleLoanMaster vehicleLoanMaster = vlservice.findAppBySlno(slno);

        if (!"BM".equalsIgnoreCase(vehicleLoanMaster.getQueue()) && !"BS".equalsIgnoreCase(vehicleLoanMaster.getQueue())) {
            return "dashboard";
        }
        List<VLFileUpload> vlFileUpload = vlFileUploadService.findFileBySlno(slno);
        model.addAttribute("vlmaster", vehicleLoanMaster);
        model.addAttribute("fileUploads", vlFileUpload);
        model.addAttribute("userdata", usd.getEmployee());
        model.addAttribute("docmas", fetchRepository.getDocMas(String.valueOf(slno)));
        model.addAttribute("roname", fetchRepository.getROName(vehicleLoanMaster.getSolId()));
        return "bmdocupload";
    }


    @GetMapping("/employmentDetails")
    public String getEmploymentDetails(@RequestParam("slno") Long slno, Model model) {
        VLEmployment employment = vlemploymentService.getEmploymentDetails(slno);
        model.addAttribute("employment", employment);
        if (employment != null) {
            List<VLEmploymentemp> employmentEmpList = vlEmploymentempService.findByEmploymentInoAndDelFlg(employment.getApplicantId(), employment.getWiNum(), "N");
            model.addAttribute("employmentEmpList", employmentEmpList);
        }

        return "employmentDetails";
    }


    @RequestMapping("/showmsg")
    public String ShowMessage(Model model) {
        return "success";
    }

    @GetMapping("/get-deviation-details")
    public String getDeviationDetails(@RequestParam String wiNum, @RequestParam Long slno, Model model) {
        VehicleLoanMaster vehicleLoanMaster = vlservice.findAppBySlno(slno);
        // Fetch the updated deviation details
        List<Map<String, Object>> checkerLevels = vehicleLoanAmberService.getAllActiveDeviationLevel();
        model.addAttribute("checkerLevels", checkerLevels);
        List<VehicleLoanAmber> vehicleLoanAmberList = vehicleLoanAmberService.getAmberDeviationsByWiNumAndSlno(wiNum, slno);
        model.addAttribute("vehicleLoanMaster", vehicleLoanMaster);
        model.addAttribute("vehicleLoanAmberList", vehicleLoanAmberList);

        // Return only the deviation details content
        return "rmmaker/deviationdetails_rm";
    }

    @GetMapping("/recallwi")
    public String recallWIList(Model model) {
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        usd.setTrans_slno(null);//Reset wi
        return "recallwi";
    }

    @RequiresMenuAccess(menuIds = {"WIENQUIRY"})
    @GetMapping("/wienquiry")
    public String wiEnquiry(Model model) {
        model.addAttribute("employee", usd.getEmployee());
        usd.setTrans_slno(null);//Reset wi
        return "wienquiry";
    }

    @RequiresMenuAccess(menuIds = {"BOGQUEUE"})
    @GetMapping("/boglist")
    public String bogList(Model model) {
        List<VehicleLoanMasterDTO> bogQueue = vlservice.getBOGQueue();
        model.addAttribute("bogQueue", bogQueue);
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        model.addAttribute("vlservice", vlservice);
        usd.setTrans_slno(null);//Reset wi
        return "boglist";
    }


    @PostMapping("/wibog")
    public String wiBOG(@RequestParam @Validated String slno, HttpServletRequest request, Model model) {
        if (usd.getEmployee() == null)
            return "login";
        slno = usd.getTrans_slno() != null ? usd.getTrans_slno() : slno;
        //model = vlWiCreateService.modifyCRTAmber(slno, request, model);
        model = vlWiCreateService.fetchWiBog(slno, request, model);
        return "wibog";
    }

    @RequiresMenuAccess(menuIds = {"ACOPN"})
    @GetMapping("/bogacctopn")
    public String bogAcctOpen(Model model) {
        List<VehicleLoanMasterDTO> bogAssetQueue = vlservice.getAcctOpenQueue();
        model.addAttribute("bogAssetQueue", bogAssetQueue);
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        usd.setTrans_slno(null);//Reset wi
        return "bogacctopn";
    }

    @PostMapping("/wibogassets")
    public String wibogassets(@RequestParam @Validated String slno, HttpServletRequest request, Model model) {
        if (usd.getEmployee() == null)
            return "login";
        slno = usd.getTrans_slno() != null ? usd.getTrans_slno() : slno;
        model = vlWiCreateService.fetchBOGAssetDetails(slno, request, model);
        return "wibogassets";
    }

    @GetMapping("/wisearch")
    public String wisearch(@RequestParam @Validated String winum, HttpServletRequest request, Model model) {

        if (usd.getEmployee() == null)
            return "login";
        String decoded_winum = new String(Base64.getDecoder().decode(winum));
        VehicleLoanMaster vehicleLoanMaster = vlservice.SearchByWiNum(decoded_winum);
        model.addAttribute("employee", usd.getEmployee());
        if (vehicleLoanMaster == null) {
            request.setAttribute("erStatus", "noEntry");
            return "notFound";
        } else if (vehicleLoanMaster.getCustName() == null) {
            request.setAttribute("erStatus", "NoCustName");
            return "notFound";
        } else {
            String slno = String.valueOf(vehicleLoanMaster.getSlno());

            model = vlWiCreateService.wiSearch(slno, request, model);
            return "wiSearch";
        }
    }

    @RequiresMenuAccess(menuIds = {"SM"})
    @GetMapping("/sanmodlist")
    public String sanmodlist(Model model) {
        List<VehicleLoanMasterDTO> bogQueue = vlservice.getSanModQueue();
        model.addAttribute("bogQueue", bogQueue);
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        model.addAttribute("vlservice", vlservice);
        usd.setTrans_slno(null);//Reset wi
        return "sanmodlist";
    }

    @GetMapping("/switch")
    public String contextSwitch(Model model) {
        if (usd.getEmployee().isSuperUser()) {
            model.addAttribute("ppcno", usd.getPPCNo());
            model.addAttribute("ppcName", usd.getEmployee().getPpcName());
            model.addAttribute("solid", usd.getSolid());
            return "switch";
        } else {
            model.addAttribute("error", "You are not authorized to use this page");
            return "error";
        }
    }

    @PostMapping("/switchMaker")
    public String contextSwitchMaker(@RequestParam @Validated String searchBox, Model model, HttpSession session, HttpServletRequest request) {
        if (usd.getEmployee().isSuperUser()) {
            model = customUserDetailsService.UserService(searchBox, model, session, request);
            return "redirect:/loading";
        } else {
            model.addAttribute("error", "You are not authorized to use this page");
            return "error";
        }
    }


    @PostMapping("/wism")
    public String wiSm(@RequestParam @Validated String slno, HttpServletRequest request, Model model) {
        if (usd.getEmployee() == null)
            return "login";
        slno = usd.getTrans_slno() != null ? usd.getTrans_slno() : slno;
        model = vlWiCreateService.fetchWiSm(slno, request, model);
        return "wism";
    }

    @RequestMapping("/hunterqueue")
    public String hunterQueue(@RequestParam(required = false) String slno, HttpServletRequest request, Model model) {
        if (usd.getEmployee() == null)
            return "login";
        slno = usd.getTrans_slno() != null ? usd.getTrans_slno() : slno;
        model = vlWiCreateService.fetchcheckerDetails(slno, request, model);
        //model = vlWiCreateService.modifyBDEntry(slno, request, model);
        return "hunterqueue";
    }

    @GetMapping("/hunterlist")
    @RequiresMenuAccess(menuIds = {"HUNTER"})
    public String hunterCheckerQueue(Model model) {
        List<VehicleLoanMasterDTO> entryQueue = vlservice.getHunterQueue();
        model.addAttribute("bcQueue", entryQueue);
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        usd.setTrans_slno(null);//Reset wi
        return "hunterlist";
        //return "bmlist";
    }


    @GetMapping("/customupload")
    public String CustomUpload(Model model) {
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        return "customupload";
    }

    @GetMapping("/mssfqueue")
    public String mssfQueue(Model model) {
        List<MSSFCustomerDTO> mssfQueue = mssfService.getMSSFQueue(usd.getSolid());
        model.addAttribute("mssfQueue", mssfQueue);
        model.addAttribute("employee", usd.getEmployee());
        model.addAttribute("menuList", usd.getMenuList());
        return "mssfqueue";
    }

    @PostMapping("/mssfdetail")
    public String mssfDetails(@RequestParam("refNo") String refNo, Model model) {
        try {
            MSSFCustomerData mssfData = mssfService.getMSSFDetails(refNo);
            MssfCustomerDetailsDTO customerDetails = mssfService.getMssfDetails(refNo);
            model.addAttribute("mssfDetailData", customerDetails);
            model.addAttribute("mssfData", mssfData);
            model.addAttribute("employee", usd.getEmployee());
            return "mssfdetail";
        } catch (Exception e) {
            log.error("Error processing MSSF details", e);
            return "redirect:/mssfqueue?error=" + e.getMessage();
        }
    }

    @GetMapping("/generate/{refNo}")
    public ResponseEntity<byte[]> generatePDF(@PathVariable String refNo) {
        try {
            MSSFCustomerData customerData = mssfService.getMSSFDetails(refNo);
            byte[] pdfBytes = pdfGeneratorService.generateMSSFLeadPDF(customerData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("MSSF_Lead_" + refNo + ".pdf").build());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error processing MSSF details", e);
        }
        return null;
    }

    @PostMapping("/addmssfEntry")
    @ResponseBody
    public Map<String, String> addEntry(HttpServletRequest request, Model model) {
        Map<String, String> response = new HashMap<>();
        try {
            model = vlWiCreateService.addEntry(request, model);

            // Extract slno and winum from model attributes
            String slno = (String) model.getAttribute("slno");
            String winum = (String) model.getAttribute("winum");

            response.put("status", "success");
            response.put("slno", slno);
            response.put("winum", winum);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return response;
    }


}
