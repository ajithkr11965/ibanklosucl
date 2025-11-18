package com.sib.ibanklosucl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.dto.Employee;
import com.sib.ibanklosucl.dto.ResponseDTO;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.service.user.UserService;
import com.sib.ibanklosucl.utilies.AESUtil;
import com.sib.ibanklosucl.utilies.UserSessionData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    @Value("${sso.token.secret}")
    private String tokenSecret;

    @Value("${sso.token.expiry}")
    private long tokenExpiry;


    private final UserSessionData usd;
    private final RestTemplate restTemplate;
    private final UserService userService;
    private final AESUtil aesUtil;
    private final ObjectMapper mapper;
    private final FetchRepository fetchRepository;

    @Value("${centralAuthUrl}")
    private String centralAuthUrl;
    @Value("${ahlUrl}")
    private String ahlUrl;

    public String getAppUrlById(String appId) {
       return centralAuthUrl;
    }
    public String generateToken(String username, String targetApp) throws Exception {

        // Create JWT token with user details
        Employee employee=usd.getEmployee();

        // Convert to JSON and encrypt
        Map<String,String> userData=new HashMap<>();
        userData.put("ppcno",username);
        userData.put("userName", employee.getPpcName());
        userData.put("targetApp", targetApp);
        userData.put("remoteIP", usd.getRemoteIP());
        String jsonString = mapper.writeValueAsString(userData);
        String encryptedJson = aesUtil.encrypt(jsonString);;
        return Jwts.builder()
                .setSubject(username)
                .claim("userData", encryptedJson)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiry))
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                .compact();
    }
    public void updateAllToken(String sessionID,String ppcno){
        try {
            Map<String, String> userData = new HashMap<>();
            userData.put("ppcno",ppcno);
            userData.put("sessionID", sessionID);
            String jsonString = mapper.writeValueAsString(userData);
            String encryptedJson = aesUtil.encrypt(jsonString);
            ;
            String token = Jwts.builder()
                    .setSubject(ppcno)
                    .claim("userData", encryptedJson)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 50000))
                    .signWith(SignatureAlgorithm.HS512, tokenSecret)
                    .compact();


            String ssol = "sso/user/" + token;
            // Optional headers (not required if none needed)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> entity = new HttpEntity<>(headers); // No body

            Map<String,String> urlMap=new HashMap<>();
            urlMap.put("AHL",ahlUrl);
            urlMap.put("LOS",centralAuthUrl);
            ResponseEntity<String> response=null;
            List<Map<String,String>> appData=fetchRepository.getAppData();
            // AHL
            for(Map<String,String> map:appData) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if ("Y".equals(value)) {
                        String url = urlMap.get(key) + ssol;
                        log.info("Inside Token UpdATE {} , {}", sessionID, url);
                        response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                        log.info("response {} , {}", response, response.getBody());
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    public ResponseDTO parseToken(String token) throws Exception {
        // Validate token
        Claims claims = Jwts.parser()
                .setSigningKey(tokenSecret)
                .parseClaimsJws(token)
                .getBody();
        // Verify token is for this application
        String username = claims.getSubject();
        String decryptedJson = aesUtil.decrypt( claims.get("userData", String.class));
        Map<String,String> userParsed = mapper.readValue(decryptedJson, Map.class);
        String ip = userParsed.get("remoteIP");
        String solID = userParsed.get("solID");
        String ppcno = userParsed.get("ppcno");
        String sessionID = userParsed.get("sessionID");
        if (!username.equals(ppcno)) {
            log.warn("Token ppc mismatch: expected {}, got {}",ppcno, username);
            return new ResponseDTO("F","Inavlid PPC");
        }
        userService.losPlatform(ppcno,ip,solID,sessionID);
        return new ResponseDTO("S","Session Updated");
    }

}
