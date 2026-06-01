package com.nh.nsight.messaging.xpilotmessaging.as.messageas;

import com.nh.nsight.messaging.common.error.BusinessException;
import com.nh.nsight.messaging.common.error.ErrorCode;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageCDTO;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageCDtoConverter;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageSearchCDTO;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.DCMessage;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.dto.MessageDDTO;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.dto.MessageSearchDDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ASMXPM72001 {

    private final DCMessage dcMessage;

    public ASMXPM72001(DCMessage dcMessage) {
        this.dcMessage = dcMessage;
    }

    @Transactional(readOnly = true, timeout = 3)
    public MessageCDTO get(Long messageId) {
        MessageDDTO found = dcMessage.getMessage(messageId);
        if (found == null) {
            throw new BusinessException(ErrorCode.BIZ_NO_DATA, "messageId=" + messageId);
        }
        return MessageCDtoConverter.toCDto(found);
    }

    @Transactional(readOnly = true, timeout = 3)
    public List<MessageCDTO> search(MessageSearchCDTO criteria) {
        MessageSearchDDTO condition = MessageCDtoConverter.toSearchDDto(criteria);
        return MessageCDtoConverter.toCDtoList(dcMessage.searchMessages(condition));
    }

    @Transactional(readOnly = true, timeout = 3)
    public long count(MessageSearchCDTO criteria) {
        return dcMessage.countMessages(MessageCDtoConverter.toSearchDDto(criteria));
    }
}
