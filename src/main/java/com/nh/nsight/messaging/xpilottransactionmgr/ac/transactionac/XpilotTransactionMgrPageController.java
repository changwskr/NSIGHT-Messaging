package com.nh.nsight.messaging.xpilottransactionmgr.ac.transactionac;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class XpilotTransactionMgrPageController {

    @GetMapping({"/xpilottransactionmgr", "/xpilottransactionmgr/"})
    public String manage() {
        return "xpilottransactionmgr/manage";
    }
}
