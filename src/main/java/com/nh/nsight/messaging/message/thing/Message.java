package com.nh.nsight.messaging.message.thing;

import java.time.LocalDateTime;

public class Message {
    private Long messageId;
    private String messageCode;
    private String messageName;
    private String messageType;
    private String channelCode;
    private String locale;
    private String messageContent;
    private LocalDateTime displayStartAt;
    private LocalDateTime displayEndAt;
    private String useYn;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    public static Message create(String messageCode, String messageName, String messageType, String channelCode,
                                 String locale, String messageContent, LocalDateTime displayStartAt,
                                 LocalDateTime displayEndAt, String useYn, String userId) {
        Message message = new Message();
        message.messageCode = messageCode;
        message.messageName = messageName;
        message.messageType = messageType;
        message.channelCode = channelCode;
        message.locale = locale;
        message.messageContent = messageContent;
        message.displayStartAt = displayStartAt;
        message.displayEndAt = displayEndAt;
        message.useYn = useYn == null ? "Y" : useYn;
        message.createdBy = userId;
        message.updatedBy = userId;
        return message;
    }

    public boolean isActiveNow(LocalDateTime now) {
        boolean afterStart = displayStartAt == null || !now.isBefore(displayStartAt);
        boolean beforeEnd = displayEndAt == null || !now.isAfter(displayEndAt);
        return "Y".equals(useYn) && afterStart && beforeEnd;
    }

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    public String getMessageCode() { return messageCode; }
    public void setMessageCode(String messageCode) { this.messageCode = messageCode; }
    public String getMessageName() { return messageName; }
    public void setMessageName(String messageName) { this.messageName = messageName; }
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    public String getChannelCode() { return channelCode; }
    public void setChannelCode(String channelCode) { this.channelCode = channelCode; }
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }
    public LocalDateTime getDisplayStartAt() { return displayStartAt; }
    public void setDisplayStartAt(LocalDateTime displayStartAt) { this.displayStartAt = displayStartAt; }
    public LocalDateTime getDisplayEndAt() { return displayEndAt; }
    public void setDisplayEndAt(LocalDateTime displayEndAt) { this.displayEndAt = displayEndAt; }
    public String getUseYn() { return useYn; }
    public void setUseYn(String useYn) { this.useYn = useYn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
