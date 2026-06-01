package com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.repository;

import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.XpmMessage;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.dto.MessageSearchDDTO;

import java.util.List;
import java.util.Optional;

public interface MessageRepository {

    void insert(XpmMessage message);

    Optional<XpmMessage> findById(Long messageId);

    Optional<XpmMessage> findByCode(String messageCode);

    List<XpmMessage> findMessages(MessageSearchDDTO condition);

    long countMessages(MessageSearchDDTO condition);

    int update(XpmMessage message);

    int deleteById(Long messageId);
}
