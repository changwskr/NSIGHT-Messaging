package com.nh.nsight.messaging.xpilot.ac.pilotac;

import com.nh.nsight.messaging.home.service.HomeAuthService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class XpilotPageController {

    private static final String AC = "XpilotPageController";

    private final HomeAuthService homeAuthService;

    public XpilotPageController(HomeAuthService homeAuthService) {
        this.homeAuthService = homeAuthService;
    }

    @GetMapping("/xpilot")
    public String xpilotRoot() {
        System.out.println("[" + AC + "] xpilotRoot START");
        System.out.println("[" + AC + "] xpilotRoot END");
        return "redirect:/xpilot/pilot-001";
    }

    @GetMapping("/xpilot/pilot-001")
    public String pilot001(HttpSession session, Model model) {
        System.out.println("[" + AC + "] pilot001 START");
        String view = pilotScreen(session, model, "pilot001", "pilot-001");
        System.out.println("[" + AC + "] pilot001 END");
        return view;
    }

    @GetMapping("/xpilot/pilot-002")
    public String pilot002(HttpSession session, Model model) {
        System.out.println("[" + AC + "] pilot002 START");
        String view = pilotScreen(session, model, "pilot002", "pilot-002");
        System.out.println("[" + AC + "] pilot002 END");
        return view;
    }

    private String pilotScreen(HttpSession session, Model model, String activeNav, String viewName) {
        System.out.println("[" + AC + "] pilotScreen START view=" + viewName);
        model.addAttribute("loginUserId", homeAuthService.currentUserId(session));
        model.addAttribute("activeNav", activeNav);
        String view = "xpilot/" + viewName;
        System.out.println("[" + AC + "] pilotScreen END view=" + viewName);
        return view;
    }
}
