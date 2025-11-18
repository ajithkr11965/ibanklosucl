package com.sib.ibanklosucl.service.user;

import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import  com.sib.ibanklosucl.model.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FetchRepository fetchRepository;

    @Transactional
    public User login(String username,String ip,String solID) {
        User user = userRepository.findByUsername(username);
        if(user==null) {
             user = new User();
        }
        user.setUsername(username);
        user.setIp(ip);
        user.setSolID(solID);
        user.setLastLoginTime(new Date());
//        String sessionID=fetchRepository.getUserSessionID(username);
//        if(sessionID==null || sessionID.contains("LOGOUT")){
//            throw new ValidationException(ValidationError.COM001,"Invalid Session Please Login Again");
//        }

        user.setSessionId(generateSessionId(username,"VL"));
        userRepository.save(user);
        return user;
    }
    @Transactional
    public User losPlatform(String username,String ip,String solID,String sessionID) {
        User user = userRepository.findByUsername(username);
        if(user==null) {
             user = new User();
        }
        user.setUsername(username);
//        user.setIp(ip);
   //     user.setSolID(solID);
        user.setSessionId(sessionID);
        userRepository.save(user);
        return user;
    }

    private String generateSessionId(String ppcno,String platform) {
        return ppcno+"_"+platform+"_"+java.util.UUID.randomUUID();
    }

//    @Transactional
//    public void logout(String userId) {
//        User user = userRepository.findByUsername(userId);
//        if (user != null) {
//            user.setSessionId(null);
//            user.setLastLogOut(new Date());
//            userRepository.save(user);
//        }
//    }

    public boolean isValidSession(String userId, String sessionId) {
        User user = userRepository.findByUsername(userId);
        return user != null && sessionId.equals(user.getSessionId());
    }

}
