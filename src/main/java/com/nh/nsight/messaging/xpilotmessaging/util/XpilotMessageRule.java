package com.nh.nsight.messaging.xpilotmessaging.util;

import com.nh.nsight.messaging.common.error.BusinessException;
import com.nh.nsight.messaging.common.error.ErrorCode;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageCreateCDTO;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageUpdateCDTO;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class XpilotMessageRule {

    private static final Pattern MESSAGE_CODE_PATTERN = Pattern.compile("^[A-Z][A-Z0-9_-]{2,49}$");
    private static final Set<String> MESSAGE_TYPES = Set.of("INFO", "WARN", "ERROR", "CONFIRM", "NOTICE");
    private static final Set<String> CHANNEL_CODES = Set.of("WEBTOPSUITE", "BI_PORTAL", "ALL", "MOBILE", "ADMIN");

    public void validateCreate(MessageCreateCDTO request) {
        validateFields(request.getMessageCode(), request.getMessageType(), request.getChannelCode(),
                request.getUseYn(), request.getDisplayStartAt(), request.getDisplayEndAt());
    }

    public void validateUpdate(MessageUpdateCDTO request) {
        validateFields(request.getMessageCode(), request.getMessageType(), request.getChannelCode(),
                request.getUseYn(), request.getDisplayStartAt(), request.getDisplayEndAt());
    }

    private void validateFields(String messageCode, String messageType, String channelCode, String useYn,
            LocalDateTime displayStartAt, LocalDateTime displayEndAt) {
        if (!StringUtils.hasText(messageCode) || !MESSAGE_CODE_PATTERN.matcher(messageCode).matches()) {
            throw new BusinessException(ErrorCode.VAL_INVALID_REQUEST,
                    "messageCode는 영문 대문자, 숫자, _, - 조합으로 3~50자여야 합니다.");
        }
        if (!MESSAGE_TYPES.contains(messageType)) {
            throw new BusinessException(ErrorCode.VAL_INVALID_REQUEST,
                    "messageType은 INFO, WARN, ERROR, CONFIRM, NOTICE 중 하나여야 합니다.");
        }
        if (!CHANNEL_CODES.contains(channelCode)) {
            throw new BusinessException(ErrorCode.VAL_INVALID_REQUEST,
                    "channelCode는 WEBTOPSUITE, BI_PORTAL, ALL, MOBILE, ADMIN 중 하나여야 합니다.");
        }
        if (!Set.of("Y", "N").contains(useYn)) {
            throw new BusinessException(ErrorCode.VAL_INVALID_REQUEST, "useYn은 Y 또는 N이어야 합니다.");
        }
        if (displayStartAt != null && displayEndAt != null && displayEndAt.isBefore(displayStartAt)) {
            throw new BusinessException(ErrorCode.VAL_INVALID_REQUEST,
                    "displayEndAt은 displayStartAt 이후여야 합니다.");
        }
    }
}
