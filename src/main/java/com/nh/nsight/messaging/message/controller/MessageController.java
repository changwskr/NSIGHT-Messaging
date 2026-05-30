package com.nh.nsight.messaging.message.controller;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.message.dto.MessageCreateRequest;
import com.nh.nsight.messaging.message.dto.MessageResponse;
import com.nh.nsight.messaging.message.dto.MessageSearchCondition;
import com.nh.nsight.messaging.message.facade.MessageFacade;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {
    private final MessageFacade messageFacade;

    public MessageController(MessageFacade messageFacade) {
        this.messageFacade = messageFacade;
    }

    @PostMapping
    public StandardResponse<MessageResponse> createMessage(@Valid @RequestBody MessageCreateRequest request) {
        MessageResponse response = messageFacade.createMessage(request);
        return StandardResponse.success("MSG-CREATE-001", "messageCreate", response);
    }

    @GetMapping("/{messageId}")
    public StandardResponse<MessageResponse> getMessage(@PathVariable Long messageId) {
        MessageResponse response = messageFacade.getMessage(messageId);
        return StandardResponse.success("MSG-DETAIL-001", "messageDetail", response);
    }

    @GetMapping
    public StandardResponse<List<MessageResponse>> searchMessages(
            @RequestParam(required = false) String messageType,
            @RequestParam(required = false) String channelCode,
            @RequestParam(required = false) String useYn
    ) {
        MessageSearchCondition condition = new MessageSearchCondition(messageType, channelCode, useYn);
        List<MessageResponse> response = messageFacade.searchMessages(condition);
        long totalCount = messageFacade.countMessages(condition);
        return StandardResponse.successPage("MSG-LIST-001", "messageList", response, 1, response.size(), totalCount);
    }
}
