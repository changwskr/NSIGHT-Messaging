package com.nh.nsight.messaging.message.dto;

public class MessageSearchCondition {
    private String messageType;
    private String channelCode;
    private String useYn;

    public MessageSearchCondition() {
    }

    public MessageSearchCondition(String messageType, String channelCode, String useYn) {
        this.messageType = messageType;
        this.channelCode = channelCode;
        this.useYn = useYn;
    }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    public String getChannelCode() { return channelCode; }
    public void setChannelCode(String channelCode) { this.channelCode = channelCode; }
    public String getUseYn() { return useYn; }
    public void setUseYn(String useYn) { this.useYn = useYn; }
}
