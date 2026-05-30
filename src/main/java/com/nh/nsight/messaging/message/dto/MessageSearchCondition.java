package com.nh.nsight.messaging.message.dto;

public class MessageSearchCondition {
    private String messageType;
    private String channelCode;
    private String useYn;
    private Integer pageNo;
    private Integer pageSize;

    public MessageSearchCondition() {
    }

    public MessageSearchCondition(String messageType, String channelCode, String useYn) {
        this(messageType, channelCode, useYn, 1, 3);
    }

    public MessageSearchCondition(String messageType, String channelCode, String useYn, Integer pageNo, Integer pageSize) {
        this.messageType = messageType;
        this.channelCode = channelCode;
        this.useYn = useYn;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public int getOffset() {
        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 3 : pageSize;
        return (safePageNo - 1) * safePageSize;
    }

    public int getSafePageNo() {
        return pageNo == null || pageNo < 1 ? 1 : pageNo;
    }

    public int getSafePageSize() {
        return pageSize == null || pageSize < 1 ? 3 : pageSize;
    }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    public String getChannelCode() { return channelCode; }
    public void setChannelCode(String channelCode) { this.channelCode = channelCode; }
    public String getUseYn() { return useYn; }
    public void setUseYn(String useYn) { this.useYn = useYn; }
    public Integer getPageNo() { return pageNo; }
    public void setPageNo(Integer pageNo) { this.pageNo = pageNo; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
