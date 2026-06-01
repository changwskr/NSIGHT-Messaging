package com.nh.nsight.messaging.xpilotmessaging.ac.messageac;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class XpilotMessagingPageController {

    private static final String AC = "XpilotMessagingPageController";

    @GetMapping({ "/xpilotmessaging", "/xpilotmessaging/" })
    public String xpilotMessagingRoot() {
        System.out.println("-----------3 [" + AC + "] xpilotMessagingRoot START");
        System.out.println("----------4 [" + AC + "] xpilotMessagingRoot END");

        return "redirect:/xpilotmessaging/messages";
    }

    @GetMapping("/xpilotmessaging/messages")
    public String messages() {
        System.out.println("----------1 [" + AC + "] messages START");
        System.out.println("----------2 [" + AC + "] messages END");
        return "xpilotmessaging/register";
    }
}
