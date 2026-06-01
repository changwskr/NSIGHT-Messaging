package com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class MessageCreateCDTO {

    @NotBlank @Size(max = 50)
    private String messageCode;
    @NotBlank @Size(max = 100)
    private String messageName;
    @NotBlank @Size(max = 20)
    private String messageType;
    @NotBlank @Size(max = 30)
    private String channelCode;
    @NotBlank @Size(max = 10)
    private String locale;
    @NotBlank @Size(max = 4000)
    private String messageContent;
    private LocalDateTime displayStartAt;
    private LocalDateTime displayEndAt;
    @NotBlank @Size(max = 1)
    private String useYn;

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
}
