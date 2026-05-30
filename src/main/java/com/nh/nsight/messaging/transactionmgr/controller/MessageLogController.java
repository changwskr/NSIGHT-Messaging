package com.nh.nsight.messaging.transactionmgr.controller;

import com.nh.nsight.messaging.common.log.MessageEnvelopeFileService;
import com.nh.nsight.messaging.common.response.StandardResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/message-logs")
public class MessageLogController {

    private final MessageEnvelopeFileService messageEnvelopeFileService;

    public MessageLogController(MessageEnvelopeFileService messageEnvelopeFileService) {
        this.messageEnvelopeFileService = messageEnvelopeFileService;
    }

    @GetMapping("/storage-location")
    public StandardResponse<Map<String, String>> storageLocation() {
        return StandardResponse.success("MSG-LOG-LOC-001", "messageLogStorageLocation",
                messageEnvelopeFileService.storageInfo());
    }
}
