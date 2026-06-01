package com.nh.nsight.messaging.xpilotmessaging.ac.messageac;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.message.dto.MessageResponse;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageCDtoConverter;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageCreateCDTO;
import com.nh.nsight.messaging.xpilotmessaging.as.messageas.ASMXPM71001;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/xpilotmessaging/messages")
public class ACMXPM71001 {

    private static final String AC = "ACMXPM71001";

    private final ASMXPM71001 asmxpm71001;

    public ACMXPM71001(ASMXPM71001 asmxpm71001) {
        this.asmxpm71001 = asmxpm71001;
    }

    @PostMapping
    public StandardResponse<MessageResponse> createMessage(@Valid @RequestBody MessageCreateCDTO request) {
        System.out.println("-------7 [" + AC + "] createMessage START messageCode=" + request.getMessageCode());
        MessageResponse response = MessageCDtoConverter.toResponse(asmxpm71001.create(request));
        StandardResponse<MessageResponse> result = StandardResponse.success("XPM-CREATE-001", "xpilotMessageCreate",
                response);
        System.out.println("-------8 [" + AC + "] createMessage END messageId=" + response.messageId());
        return result;
    }
}
