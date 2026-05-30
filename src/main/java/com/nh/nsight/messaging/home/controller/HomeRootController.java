package com.nh.nsight.messaging.home.controller;

import com.nh.nsight.messaging.home.service.HomeAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeRootController {

    private final HomeAuthService homeAuthService;

    public HomeRootController(HomeAuthService homeAuthService) {
        this.homeAuthService = homeAuthService;
    }

    @GetMapping("/")
    public String root(HttpSession session) {
        if (homeAuthService.isAuthenticated(session)) {
            return "redirect:/home";
        }
        return "redirect:/home/login";
    }
}
