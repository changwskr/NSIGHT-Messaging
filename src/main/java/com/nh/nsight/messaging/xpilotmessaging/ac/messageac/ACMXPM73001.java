package com.nh.nsight.messaging.xpilotmessaging.ac.messageac;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.message.dto.MessageResponse;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageCDtoConverter;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageUpdateCDTO;
import com.nh.nsight.messaging.xpilotmessaging.as.messageas.ASMXPM73001;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/xpilotmessaging/messages")
public class ACMXPM73001 {

    private static final String AC = "ACMXPM73001";

    private final ASMXPM73001 asmxpm73001;

    public ACMXPM73001(ASMXPM73001 asmxpm73001) {
        this.asmxpm73001 = asmxpm73001;
    }

    @PutMapping("/{messageId}")
    public StandardResponse<MessageResponse> updateMessage(
            @PathVariable Long messageId,
            @Valid @RequestBody MessageUpdateCDTO request) {
        System.out.println("-------5[" + AC + "] updateMessage START messageId=" + messageId);
        MessageResponse response = MessageCDtoConverter.toResponse(asmxpm73001.update(messageId, request));
        StandardResponse<MessageResponse> result = StandardResponse.success("XPM-UPDATE-001", "xpilotMessageUpdate",
                response);
        System.out.println("------6 [" + AC + "] updateMessage END messageId=" + messageId);
        return result;
    }
}
