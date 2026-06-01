package com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.dto;

import java.time.LocalDateTime;

public class MessageDDTO {

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
