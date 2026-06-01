package com.nh.nsight.messaging.xpilottransactionmgr.ac.transactionac;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.xpilottransactionmgr.as.transactionas.ASMXPT75001;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/xpilottransactionmgr/message-logs")
public class ACMXPT75001 {

    private static final String AC = "ACMXPT75001";

    private final ASMXPT75001 asmxpt75001;

    public ACMXPT75001(ASMXPT75001 asmxpt75001) {
        this.asmxpt75001 = asmxpt75001;
    }

    @GetMapping("/storage-location")
    public StandardResponse<Map<String, String>> storageLocation() {
        System.out.println("★★★★★★★ [" + AC + "] storageLocation START");
        StandardResponse<Map<String, String>> result = StandardResponse.success(
                "XPT-MSG-LOG-LOC-001", "xpilotMessageLogStorageLocation", asmxpt75001.storageInfo());
        System.out.println("★★★★★★★ [" + AC + "] storageLocation END");
        return result;
    }
}
