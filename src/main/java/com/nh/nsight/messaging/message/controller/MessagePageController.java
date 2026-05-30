package com.nh.nsight.messaging.message.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MessagePageController {

    @GetMapping({"/messaging", "/messaging/"})
    public String messagingAlias() {
        return "redirect:/home";
    }

    @GetMapping("/messages")
    public String messages() {
        return "messages/register";
    }
}
