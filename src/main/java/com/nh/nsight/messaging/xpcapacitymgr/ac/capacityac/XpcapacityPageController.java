package com.nh.nsight.messaging.xpcapacitymgr.ac.capacityac;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class XpcapacityPageController {

    private static final String AC = "XpcapacityPageController";

    @GetMapping({ "/xpcapacitymgr", "/xpcapacitymgr/" })
    public String root() {
        System.out.println("★★★★★ [" + AC + "] root redirect plan");
        return "redirect:/xpcapacitymgr/plan";
    }

    @GetMapping("/xpcapacitymgr/plan")
    public String plan() {
        System.out.println("★★★★★ [" + AC + "] plan view");
        return "xpcapacitymgr/plan";
    }
}
