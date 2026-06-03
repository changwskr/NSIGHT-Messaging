package com.nh.nsight.messaging.junmun.ac.junmunac;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class JunmunPageController {

    @GetMapping({ "/junmun", "/junmun/" })
    public String root() {
        return "redirect:/junmun/manage";
    }

    @GetMapping("/junmun/manage")
    public String manage() {
        return "junmun/manage";
    }
}
