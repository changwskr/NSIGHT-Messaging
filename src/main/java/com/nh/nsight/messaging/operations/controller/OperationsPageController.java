package com.nh.nsight.messaging.operations.controller;

import com.nh.nsight.messaging.home.service.HomeAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OperationsPageController {

    private final HomeAuthService homeAuthService;

    public OperationsPageController(HomeAuthService homeAuthService) {
        this.homeAuthService = homeAuthService;
    }

    @GetMapping("/operations")
    public String operations(HttpSession session, Model model) {
        model.addAttribute("loginUserId", homeAuthService.currentUserId(session));
        return "operations/index";
    }
}
