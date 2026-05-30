package com.nh.nsight.messaging.home.controller;

import com.nh.nsight.messaging.home.service.HomeAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/home")
public class HomeLoginController {

    private final HomeAuthService homeAuthService;

    public HomeLoginController(HomeAuthService homeAuthService) {
        this.homeAuthService = homeAuthService;
    }

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String redirect,
            @RequestParam(required = false) String error,
            HttpSession session,
            Model model
    ) {
        if (homeAuthService.isAuthenticated(session)) {
            return "redirect:" + homeAuthService.safeRedirect(redirect);
        }
        model.addAttribute("redirect", homeAuthService.safeRedirect(redirect));
        model.addAttribute("loginError", "invalid".equals(error));
        return "home/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String loginId,
            @RequestParam String loginPassword,
            @RequestParam(required = false) String redirect,
            HttpSession session
    ) {
        if (!homeAuthService.authenticate(loginId, loginPassword)) {
            String target = homeAuthService.safeRedirect(redirect);
            String encoded = URLEncoder.encode(target, StandardCharsets.UTF_8);
            return "redirect:/home/login?error=invalid&redirect=" + encoded;
        }
        homeAuthService.login(session, loginId);
        return "redirect:" + homeAuthService.safeRedirect(redirect);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        homeAuthService.logout(session);
        return "redirect:/home/login";
    }
}
