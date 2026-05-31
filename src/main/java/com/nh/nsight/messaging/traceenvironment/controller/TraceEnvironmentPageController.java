package com.nh.nsight.messaging.traceenvironment.controller;



import com.nh.nsight.messaging.home.service.HomeAuthService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;



@Controller

public class TraceEnvironmentPageController {



    private final HomeAuthService homeAuthService;



    public TraceEnvironmentPageController(HomeAuthService homeAuthService) {

        this.homeAuthService = homeAuthService;

    }



    @GetMapping("/traceenvironment")

    public String traceEnvironmentRoot() {

        return "redirect:/traceenvironment/env-001";

    }



    @GetMapping("/traceenvironment/env-001")

    public String env001(HttpSession session, Model model) {

        return envScreen(session, model, "env001", "env-001");

    }



    @GetMapping("/traceenvironment/env-002")

    public String env002(HttpSession session, Model model) {

        return envScreen(session, model, "env002", "env-002");

    }



    @GetMapping("/traceenvironment/env-003")

    public String env003(HttpSession session, Model model) {

        return envScreen(session, model, "env003", "env-003");

    }



    @GetMapping("/traceenvironment/env-004")

    public String env004(HttpSession session, Model model) {

        return envScreen(session, model, "env004", "env-004");

    }



    @GetMapping("/traceenvironment/check")

    public String envCheck(HttpSession session, Model model) {

        return envScreen(session, model, "check", "check");

    }



    @GetMapping("/traceenvironment/rule-check")

    public String envRuleCheck(HttpSession session, Model model) {

        return envScreen(session, model, "rulecheck", "rule-check");

    }



    private String envScreen(HttpSession session, Model model, String activeNav, String viewName) {

        model.addAttribute("loginUserId", homeAuthService.currentUserId(session));

        model.addAttribute("activeNav", activeNav);

        return "traceenvironment/" + viewName;

    }

}

