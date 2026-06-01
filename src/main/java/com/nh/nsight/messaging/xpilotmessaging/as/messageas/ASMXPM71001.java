package com.nh.nsight.messaging.xpilotmessaging.as.messageas;

import com.nh.nsight.messaging.common.context.RequestContext;
import com.nh.nsight.messaging.common.error.BusinessException;
import com.nh.nsight.messaging.common.error.ErrorCode;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageCDTO;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageCDtoConverter;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageCreateCDTO;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.DCMessage;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.dto.MessageDDTO;
import com.nh.nsight.messaging.xpilotmessaging.zcommonutil.XpilotMessageRule;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ASMXPM71001 {

    private final DCMessage dcMessage;
    private final XpilotMessageRule messageRule;

    public ASMXPM71001(DCMessage dcMessage, XpilotMessageRule messageRule) {
        this.dcMessage = dcMessage;
        this.messageRule = messageRule;
    }

    @Transactional(timeout = 5)
    public MessageCDTO create(MessageCreateCDTO request) {
        messageRule.validateCreate(request);
        if (dcMessage.findByCode(request.getMessageCode()) != null) {
            throw new BusinessException(ErrorCode.BIZ_DUPLICATE_MESSAGE_CODE,
                    "messageCode=" + request.getMessageCode());
        }
        String userId = RequestContext.get().userId();
        MessageDDTO ddto = MessageCDtoConverter.toDDto(request);
        ddto.setCreatedBy(userId);
        ddto.setUpdatedBy(userId);
        dcMessage.createMessage(ddto);
        return MessageCDtoConverter.toCDto(dcMessage.getMessage(ddto.getMessageId()));
    }
}
