package com.nh.nsight.messaging.home.interceptor;

import com.nh.nsight.messaging.home.service.HomeAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class HomeAuthInterceptor implements HandlerInterceptor {

    private final HomeAuthService homeAuthService;

    public HomeAuthInterceptor(HomeAuthService homeAuthService) {
        this.homeAuthService = homeAuthService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String uri = request.getRequestURI();
        if (!isProtectedPage(uri)) {
            return true;
        }
        HttpSession session = request.getSession(false);
        if (homeAuthService.isAuthenticated(session)) {
            return true;
        }
        String redirect = URLEncoder.encode(uri, StandardCharsets.UTF_8);
        response.sendRedirect("/home/login?redirect=" + redirect);
        return false;
    }

    private boolean isProtectedPage(String uri) {
        if ("/home".equals(uri)) {
            return true;
        }
        return uri.startsWith("/messages")
                || uri.startsWith("/files")
                || uri.startsWith("/transactionmgr")
                || uri.startsWith("/tracedump");
    }
}
