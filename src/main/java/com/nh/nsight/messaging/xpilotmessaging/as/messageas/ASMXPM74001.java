package com.nh.nsight.messaging.xpilotmessaging.as.messageas;

import com.nh.nsight.messaging.common.error.BusinessException;
import com.nh.nsight.messaging.common.error.ErrorCode;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.DCMessage;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ASMXPM74001 {

    private final DCMessage dcMessage;

    public ASMXPM74001(DCMessage dcMessage) {
        this.dcMessage = dcMessage;
    }

    @Transactional(timeout = 5)
    public void delete(Long messageId) {
        if (dcMessage.getMessage(messageId) == null) {
            throw new BusinessException(ErrorCode.BIZ_NO_DATA, "messageId=" + messageId);
        }
        dcMessage.deleteMessage(messageId);
    }
}
