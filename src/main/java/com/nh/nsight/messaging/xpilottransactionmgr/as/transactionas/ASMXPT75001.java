package com.nh.nsight.messaging.xpilottransactionmgr.as.transactionas;

import com.nh.nsight.messaging.common.log.MessageEnvelopeFileService;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ASMXPT75001 {

    private static final String AS = "ASMXPT75001";

    private final MessageEnvelopeFileService messageEnvelopeFileService;

    public ASMXPT75001(MessageEnvelopeFileService messageEnvelopeFileService) {
        this.messageEnvelopeFileService = messageEnvelopeFileService;
    }

    public Map<String, String> storageInfo() {
        System.out.println("★★★★★★★ [" + AS + "] storageInfo START");
        Map<String, String> info = messageEnvelopeFileService.storageInfo();
        System.out.println("★★★★★★★ [" + AS + "] storageInfo END");
        return info;
    }
}
