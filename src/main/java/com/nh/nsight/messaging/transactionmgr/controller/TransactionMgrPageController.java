package com.nh.nsight.messaging.transactionmgr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TransactionMgrPageController {

    @GetMapping("/transactionmgr")
    public String transactionMgr() {
        return "transactionmgr/manage";
    }
}
