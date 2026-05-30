package com.nh.nsight.messaging.home.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class HomeAuthService {

    public static final String SESSION_USER_ID = "HOME_AUTH_USER_ID";

    private static final String LOGIN_ID = "admin";
    private static final String LOGIN_PASSWORD = "1234";

    public boolean authenticate(String loginId, String loginPassword) {
        return LOGIN_ID.equals(loginId) && LOGIN_PASSWORD.equals(loginPassword);
    }

    public void login(HttpSession session, String loginId) {
        session.setAttribute(SESSION_USER_ID, loginId);
    }

    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    public boolean isAuthenticated(HttpSession session) {
        return session != null && session.getAttribute(SESSION_USER_ID) != null;
    }

    public String currentUserId(HttpSession session) {
        if (!isAuthenticated(session)) {
            return null;
        }
        return String.valueOf(session.getAttribute(SESSION_USER_ID));
    }

    public String safeRedirect(String redirect) {
        if (redirect != null && redirect.startsWith("/") && !redirect.startsWith("//")) {
            return redirect;
        }
        return "/home";
    }
}
