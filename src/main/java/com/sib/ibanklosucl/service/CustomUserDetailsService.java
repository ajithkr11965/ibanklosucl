package com.sib.ibanklosucl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.exception.AuthenticationServiceException;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.ValidationRepository;
import com.sib.ibanklosucl.service.user.UserService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private RestTemplate restTemplate;


    @Value("${ldap.authentication.url}")
    private String ldapAuthenticationUrl;

    @Value("${userrole.endpoint.url}")
    private String userServiceEndpint;

    @Value("${app.dev-mode:true}")
    private boolean devMode;

    @Autowired
    private UserSessionData usd;
    @Autowired
    private ValidationRepository validationRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private FetchRepository fetchRepository;


    @Override
//    @CircuitBreaker(name = "ldapAuthService", fallbackMethod = "fallbackLoadUserByUsername")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            String password = getCurrentPassword();
            // Call the LDAP authentication microservice
            ResponseEntity<AuthenticationResponse> response = restTemplate.postForEntity(
                    ldapAuthenticationUrl,
                    new AuthenticationRequest(username, password),
                    AuthenticationResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                AuthenticationResponse authResponse = response.getBody();
                if (authResponse.isSuccess()) {
                    // Authentication successful, extract user details from the response
                    User user = authResponse.getUser();
                    List<GrantedAuthority> authorities = getUserAuthorities(user);
                    // Create and return a UserDetails object
                    return new org.springframework.security.core.userdetails.User(
                            user.getUserName(),
                            "",
                            authorities
                    );
                } else {
                    throw new BadCredentialsException(authResponse.getMessage());
                }
            } else {
                throw new AuthenticationServiceException("Authentication service returned an error: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new AuthenticationServiceException("Failed to communicate with the authentication service", e);
        }
    }

    //    @CircuitBreaker(name = "ldapAuthService", fallbackMethod = "fallbackAuthenticate")
    public boolean authenticate(String username, String password, Model model,Boolean isSSoLogin) {
        String originalPPC = username; // Store original PPC for comparison
        String currentThread = Thread.currentThread().getName();
        log.info("[Auth-Start][Thread: {}] Starting authentication for user: {}, Current USD PPC: {}",
                currentThread, username, originalPPC);
        try {
            if (username.matches("\\d+")) {
                username = "SIBL" + username;
            }
            log.info("[Auth-LDAP][Thread: {}] Calling LDAP for user: {}", currentThread, username);
            String hardcodedResponse = "{\"success\":true,\"user\":{\"mobile\":\"+SSO\",\"officeCode\":\"SSO\",\"mail\":\"SSO@sib.bank.in\",\"officeName\":\"SSO DEPARTMENT\",\"ppcno\":\"" + username + "\",\"designation\":\"SSO\",\"description\":\"SSO\",\"ipPhone\":\"SSO\",\"userName\":\"SSO LOGIN USER\"},\"message\":\"Authenticated Successfully\"}";
            String hardcodedVendorResponse = "{\"success\":true,\"user\":{\"mobile\":null,\"officeCode\":null,\"mail\":\"SHYJAMOLNS98@GMAIL.COM\",\"officeName\":null,\"ppcno\":\"DAUD809403\",\"designation\":null,\"description\":null,\"ipPhone\":null,\"userName\":\"DAUD809403_SHYJAMOL N S\",\"reviewAuthority\":null,\"reports\":null},\"message\":\"Authenticated Successfully\"}";

            AuthenticationResponse authResponse;
            if (devMode || isSSoLogin) {
                authResponse = new ObjectMapper().readValue(hardcodedResponse, AuthenticationResponse.class);
            } else {
                ResponseEntity<AuthenticationResponse> response = restTemplate.postForEntity(ldapAuthenticationUrl, new AuthenticationRequest(username, password), AuthenticationResponse.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    authResponse = response.getBody();
                } else {
                    model.addAttribute("error", "Authentication service returned an error");
                    throw new AuthenticationServiceException("Authentication service returned an error: " + response.getStatusCode());
                }

            }
            if (authResponse.isSuccess()) {

                String userType = "";
                User user = authResponse.getUser();
                log.info("[Auth-LDAP-Success][Thread: {}] LDAP Success for user: {}, PPC: {}",
                        currentThread, username, user.getPpcno());
                List<GrantedAuthority> authorities = getUserAuthorities(user);
                if (username.toUpperCase().contains("SIBL")) {
                    userType = "SIBL";
                }
                log.info("[Auth-Roles][Thread: {}] Fetching roles for PPC: {}",
                        currentThread, user.getPpcno());
                ResponseEntity<EmployeeResponseDTO> rolesResponse = restTemplate.postForEntity(
                        userServiceEndpint,
                        new RoleRequest(user.getPpcno(), "VL", userType),
                        // new RoleRequest("8063"),
                        EmployeeResponseDTO.class
                );
                if (rolesResponse.getStatusCode().is2xxSuccessful()) {
                    EmployeeResponseDTO responseDTO = rolesResponse.getBody();
                    if (responseDTO == null) {
                        throw new AuthenticationServiceException("Empty response received from user service");
                    }
                    Optional<Employee> employeeData = responseDTO.getEmployee();
                    if (employeeData.isPresent()) {
                        Employee employee = employeeData.get();
                        String newPPC = employee.getPpcno();

                        log.info("[Auth-Employee][Thread: {}] Processing employee data:", currentThread);
                        log.info("  - New Employee PPC: {}", newPPC);
                        log.info("  - Original USD PPC: {}", originalPPC);
                        if (!employeeData.get().isPpcAvailStatus()) {
                            log.warn("[Auth-Denied][Thread: {}] Access denied for PPC: {}",
                                    currentThread, newPPC);
                            throw new AuthenticationServiceException("You are Not Authorized to Access the Application!!");
                        }
                        model.addAttribute("employeeData", employeeData.get());
                        log.info("[Auth-Model][Thread: {}] Set employeeData in model for PPC: {}",
                                currentThread, newPPC);
                    } else {
                        log.error("[Auth-Error][Thread: {}] Empty employee details for PPC: {}",
                                    currentThread, user.getPpcno());
                        model.addAttribute("error", "Employee details are empty");
                        throw new AuthenticationServiceException("Employee details are empty");
                    }
                    List<Reportee> reportees = responseDTO.getReportees();  // This will never be null now
                    List<String> clusterSols = responseDTO.getClusterSols();
                    List<String> saleSols = responseDTO.getSaleSols();
                    List<String> userRoles = responseDTO.getRoles();
                    List<MenuList> menuList = responseDTO.getMenuList();


                   // if (!reportees.isEmpty()) {
                        model.addAttribute("reportees", reportees);
                   // }
                    //if (!clusterSols.isEmpty()) {
                        model.addAttribute("clusterSols", clusterSols);
                   // }
                   // if (!saleSols.isEmpty()) {
                        model.addAttribute("saleSols", saleSols);
                    //}
                  //  if (!userRoles.isEmpty()) {
                        model.addAttribute("userRoles", userRoles);
                   // }
                  //  if (!menuList.isEmpty()) {
                        model.addAttribute("menuList", menuList);
                        model.addAttribute("username", username);
                   // }
                    //usd.setMenuList(menuList);
                    List<String> roleList = new ArrayList<>(rolesResponse.getBody().getRoles());
                    updateAuthoritiesWithRoles(authorities, userRoles);
                    // Check for potential session mixing

                    log.info("[Auth-Success][Thread: {}] Authentication completed:", currentThread);
                    log.info("  - Model Employee PPC: {}",
                            ((Employee) model.getAttribute("employeeData")).getPpcno());

                } else {
                    log.error("[Auth-Error][Thread: {}] Failed to fetch employee data. Status: {}",
                                currentThread, rolesResponse.getStatusCode());
                    model.addAttribute("error", "Failed to fetch user roles");
                    throw new AuthenticationServiceException("Failed to fetch user roles");
                }

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                return authResponse.isSuccess();
            } else {
                log.warn("[Auth-Failed][Thread: {}] Authentication failed for user: {}",
                            currentThread, username);
                model.addAttribute("error", authResponse.getMessage());
                throw new BadCredentialsException(authResponse.getMessage());
            }

        } catch (RestClientException e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to communicate with the authentication service");
            throw new AuthenticationServiceException("Failed to communicate with the authentication service", e);
        } catch (BadCredentialsException exbad) {
            model.addAttribute("error", exbad.getMessage());
            throw new AuthenticationServiceException(exbad.getMessage(), exbad);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Model UserService(String ppcno, Model model, HttpSession session, HttpServletRequest request) {
        ResponseEntity<EmployeeResponseDTO> rolesResponse = restTemplate.postForEntity(
                userServiceEndpint,
                new RoleRequest(ppcno, "VL", "SIBL"),
                // new RoleRequest("8063"),
                EmployeeResponseDTO.class
        );
        if (rolesResponse.getStatusCode().is2xxSuccessful()) {
            Optional<Employee> employeeData = rolesResponse.getBody().getEmployee();
            List<Reportee> reportees = rolesResponse.getBody().getReportees();
            List<String> clusterSols = new ArrayList<>(rolesResponse.getBody().getClusterSols());
            List<String> saleSols = new ArrayList<>(rolesResponse.getBody().getSaleSols());
            List<String> userRoles = new ArrayList<>(rolesResponse.getBody().getRoles());
            List<MenuList> menuList = new ArrayList<>(rolesResponse.getBody().getMenuList());
            if (employeeData.isPresent()) {
                if (!employeeData.get().isPpcAvailStatus()) {
                    throw new AuthenticationServiceException("You are Not Authorized to Access the Application!!");
                }
                model.addAttribute("employeeData", employeeData.get());
            } else {
                model.addAttribute("error", "Employee details are empty");
                throw new AuthenticationServiceException("Employee details are empty");
            }
            if (!reportees.isEmpty()) {
                model.addAttribute("reportees", reportees);
            }
            if (!clusterSols.isEmpty()) {
                model.addAttribute("clusterSols", clusterSols);
            }
            if (!saleSols.isEmpty()) {
                model.addAttribute("saleSols", saleSols);
            }
            if (!userRoles.isEmpty()) {
                model.addAttribute("userRoles", userRoles);
            }
            usd.setMenuList(menuList);
            List<String> roleList = new ArrayList<>(rolesResponse.getBody().getRoles());
            session.setAttribute("employeeData", model.getAttribute("employeeData"));
            usd.setEmployee((Employee) model.getAttribute("employeeData"));
            usd.setRemoteIP(CommonUtils.getClientIp(request));

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
            usd.getEmployee().setSuperUser(validationRepository.isSuperUSer(usd.getRemoteIP()));
            com.sib.ibanklosucl.model.user.User user = userService.login(usd.getPPCNo(), usd.getRemoteIP(), usd.getSolid());
            tokenService.updateAllToken(user.getSessionId(),usd.getPPCNo());
            session.setAttribute("userSessionPPC", usd.getPPCNo());
            session.setAttribute("userSessionID", user.getSessionId());
            usd.getEmployee().setLhUser(fetchRepository.isLMPPC(usd.getPPCNo()));
            usd.getEmployee().setRahUser(fetchRepository.isRAHPPC(usd.getPPCNo()));

        } else {
            model.addAttribute("error", "Failed to fetch user roles");
            throw new AuthenticationServiceException("Failed to fetch user roles");
        }
        return model;
    }


    private void updateAuthoritiesWithRoles(List<GrantedAuthority> authorities, List<String> roles) {
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
    }

    public boolean fallbackAuthenticate(String username, String password, Model model, Throwable throwable) {
        //  throw new AuthenticationServiceException("Authentication service is currently unavailable. Please try again later.");
        throw new AuthenticationServiceException(throwable.getMessage());

    }

    private List<GrantedAuthority> getUserAuthorities(User user) {
        // Extract user roles or authorities from the user object
        // and return them as a list of GrantedAuthority objects
        List<GrantedAuthority> authorities = new ArrayList<>();
        // Add user-specific roles or authorities here
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authorities;
    }

    private String getCurrentPassword() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return (String) authentication.getCredentials();
        }
        throw new IllegalStateException("Current authentication is not a UsernamePasswordAuthenticationToken");
    }

    public UserDetails fallbackLoadUserByUsername(String username, Throwable throwable) {
        // Fallback logic when the circuit breaker is open
        //throw new AuthenticationServiceException("Authentication service is currently unavailable. Please try again later.");
        throw new AuthenticationServiceException(throwable.getMessage());
    }
}

