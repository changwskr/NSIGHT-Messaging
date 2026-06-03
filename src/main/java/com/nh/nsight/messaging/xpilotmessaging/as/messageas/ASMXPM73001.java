package com.nh.nsight.messaging.xpilotmessaging.as.messageas;

import com.nh.nsight.messaging.common.context.RequestContext;
import com.nh.nsight.messaging.common.error.BusinessException;
import com.nh.nsight.messaging.common.error.ErrorCode;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageCDTO;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageCDtoConverter;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageUpdateCDTO;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.DCMessage;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.dto.MessageDDTO;
import com.nh.nsight.messaging.xpilotmessaging.util.XpilotMessageRule;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ASMXPM73001 {

    private final DCMessage dcMessage;
    private final XpilotMessageRule messageRule;

    public ASMXPM73001(DCMessage dcMessage, XpilotMessageRule messageRule) {
        this.dcMessage = dcMessage;
        this.messageRule = messageRule;
    }

    @Transactional(timeout = 5)
    public MessageCDTO update(Long messageId, MessageUpdateCDTO request) {
        messageRule.validateUpdate(request);
        MessageDDTO existing = dcMessage.getMessage(messageId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.BIZ_NO_DATA, "messageId=" + messageId);
        }
        if (!existing.getMessageCode().equals(request.getMessageCode())) {
            MessageDDTO duplicate = dcMessage.findByCode(request.getMessageCode());
            if (duplicate != null) {
                throw new BusinessException(ErrorCode.BIZ_DUPLICATE_MESSAGE_CODE,
                        "messageCode=" + request.getMessageCode());
            }
        }
        MessageDDTO ddto = MessageCDtoConverter.toDDto(request);
        ddto.setMessageId(messageId);
        ddto.setCreatedBy(existing.getCreatedBy());
        ddto.setCreatedAt(existing.getCreatedAt());
        ddto.setUpdatedBy(RequestContext.get().userId());
        return MessageCDtoConverter.toCDto(dcMessage.updateMessage(messageId, ddto));
    }
}
