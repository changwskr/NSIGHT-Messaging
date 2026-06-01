package com.nh.nsight.messaging.xpilotmessaging.ac.messageac;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.xpilotmessaging.as.messageas.ASMXPM74001;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/xpilotmessaging/messages")
public class ACMXPM74001 {

    private static final String AC = "ACMXPM74001";

    private final ASMXPM74001 asmxpm74001;

    public ACMXPM74001(ASMXPM74001 asmxpm74001) {
        this.asmxpm74001 = asmxpm74001;
    }

    @DeleteMapping("/{messageId}")
    public StandardResponse<Void> deleteMessage(@PathVariable Long messageId) {
        System.out.println("-------7 [" + AC + "] deleteMessage START messageId=" + messageId);
        asmxpm74001.delete(messageId);
        StandardResponse<Void> result = StandardResponse.success("XPM-DELETE-001", "xpilotMessageDelete", null);
        System.out.println("------8[" + AC + "] deleteMessage END messageId=" + messageId);
        return result;
    }
}
