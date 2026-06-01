package com.nh.nsight.messaging.xpilotmessaging.dc.messagedc;

import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.dto.MessageDDTO;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.dto.MessageSearchDDTO;

import java.util.List;

public interface IDCMessage {

    void createMessage(MessageDDTO messageDDTO);

    MessageDDTO getMessage(Long messageId);

    List<MessageDDTO> searchMessages(MessageSearchDDTO condition);

    long countMessages(MessageSearchDDTO condition);

    MessageDDTO updateMessage(Long messageId, MessageDDTO messageDDTO);

    void deleteMessage(Long messageId);
}
