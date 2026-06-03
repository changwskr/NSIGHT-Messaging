package com.nh.nsight.messaging.xpilotstyleguide.ac.userac;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class XpilotStyleGuidePageController {

    private static final String AC = "XpilotStyleGuidePageController";

    @GetMapping({"/xpilotstyleguide", "/xpilotstyleguide/"})
    public String root() {
        System.out.println("★★★★★ [" + AC + "] root START");
        System.out.println("★★★★★ [" + AC + "] root END redirect=/xpilotstyleguide/users");
        return "redirect:/xpilotstyleguide/users";
    }

    @GetMapping("/xpilotstyleguide/users")
    public String users() {
        System.out.println("★★★★★ [" + AC + "] users START");
        System.out.println("★★★★★ [" + AC + "] users END view=xpilotstyleguide/manage");
        return "xpilotstyleguide/manage";
    }
}
