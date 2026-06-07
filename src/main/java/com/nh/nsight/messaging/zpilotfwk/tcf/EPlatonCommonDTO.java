package com.nh.nsight.messaging.zpilotfwk.tcf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 공통 DTO.
 */
public class EPlatonCommonDTO {

    private String terminalID;
    private String terminalType;
    private String xmlSeq;
    private String bankCode;
    private String branchCode;
    private String glPostBranchCode;
    private String channelType;
    private String userID;
    private String eventNo;
    private String nation;
    private String regionCode;
    private String timeZone;
    private int fxRateCount;
    private String reqName;
    private String systemDate;
    private String businessDate;
    private String systemInTime;
    private String systemOutTime;
    private String transactionNo;
    private String baseCurrency;
    private String multiPL;
    private int userLevel;
    private String IPAddress;

    private String systemName;
    private String operationName;
    private String tpfq;
    private String txTimer;
    private String hostseq;
    private String orgseq;

    public EPlatonCommonDTO() {
        terminalID = "*";
        terminalType = "*";
        xmlSeq = "*";
        bankCode = "*";
        branchCode = "*";
        glPostBranchCode = "*";
        channelType = "*";
        userID = "*";
        eventNo = "*";
        nation = "*";
        regionCode = "*";
        timeZone = "*";
        fxRateCount = 0;
        reqName = "*";
        systemDate = "*";
        businessDate = "*";
        systemInTime = "*";
        systemOutTime = "*";
        transactionNo = "*";
        baseCurrency = "*";
        multiPL = "*";
        userLevel = 0;
        IPAddress = "*";
        systemName = "*";
        operationName = "*";
        tpfq = "*";
        txTimer = "*";
        hostseq = "*";
        orgseq = "*";
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public String getBusinessDate() {
        return businessDate;
    }

    public String getChannelType() {
        return channelType;
    }

    public String getEventNo() {
        return eventNo;
    }

    public int getFxRateCount() {
        return fxRateCount;
    }

    public String getGlPostBranchCode() {
        return glPostBranchCode;
    }

    public String getNation() {
        return nation;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public String getReqName() {
        return reqName;
    }

    public String getTerminalID() {
        return terminalID;
    }

    public String getTerminalType() {
        return terminalType;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getSystemDate() {
        return systemDate;
    }

    public String getSystemInTime() {
        return systemInTime;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public String getSystemOutTime() {
        return systemOutTime;
    }

    public String getUserID() {
        return userID;
    }

    public String getXmlSeq() {
        return xmlSeq;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public void setEventNo(String eventNo) {
        this.eventNo = eventNo;
    }

    public void setFxRateCount(int fxRateCount) {
        this.fxRateCount = fxRateCount;
    }

    public void setGlPostBranchCode(String glPostBranchCode) {
        this.glPostBranchCode = glPostBranchCode;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public void setReqName(String reqName) {
        this.reqName = reqName;
    }

    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public void setSystemDate(String systemDate) {
        this.systemDate = systemDate;
    }

    public void setSystemInTime(String systemInTime) {
        this.systemInTime = systemInTime;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public void setSystemOutTime(String systemOutTime) {
        this.systemOutTime = systemOutTime;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setXmlSeq(String xmlSeq) {
        this.xmlSeq = xmlSeq;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public String getMultiPL() {
        return multiPL;
    }

    public void setMultiPL(String multiPL) {
        this.multiPL = multiPL;
    }

    @JsonIgnore
    public String getIPAddress() {
        return IPAddress;
    }

    @JsonIgnore
    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    @JsonProperty("ipAddress")
    public String getIpAddress() {
        return IPAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.IPAddress = ipAddress;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getTpfq() {
        return tpfq;
    }

    public void setTpfq(String tpfq) {
        this.tpfq = tpfq;
    }

    public String getTxTimer() {
        return txTimer;
    }

    public void setTxTimer(String txTimer) {
        this.txTimer = txTimer;
    }

    public String getHostseq() {
        return hostseq;
    }

    public void setHostseq(String hostseq) {
        this.hostseq = hostseq;
    }

    public String getOrgseq() {
        return orgseq;
    }

    public void setOrgseq(String orgseq) {
        this.orgseq = orgseq;
    }

    /** SP_COMMON 테스트/REST 기본 common 값 */
    public static EPlatonCommonDTO sample() {
        EPlatonCommonDTO dto = new EPlatonCommonDTO();
        dto.setTerminalID("TERM001");
        dto.setTerminalType("WEB");
        dto.setXmlSeq("1");
        dto.setBankCode("11");
        dto.setBranchCode("0001");
        dto.setGlPostBranchCode("0001");
        dto.setChannelType("ONL");
        dto.setUserID("TESTER");
        dto.setEventNo("SP_COMMON7001");
        dto.setNation("KR");
        dto.setRegionCode("01");
        dto.setTimeZone("Asia/Seoul");
        dto.setFxRateCount(0);
        dto.setReqName("SP_COMMON_TEST");
        dto.setSystemDate("");
        dto.setBusinessDate("");
        dto.setSystemInTime("");
        dto.setSystemOutTime("*");
        dto.setTransactionNo("*");
        dto.setBaseCurrency("KRW");
        dto.setMultiPL("N");
        dto.setUserLevel(0);
        dto.setIPAddress("127.0.0.1");
        dto.setSystemName("WEB");
        dto.setOperationName("AC_SP_COMMON.execute");
        dto.setTpfq("200");
        dto.setTxTimer("60");
        dto.setHostseq("HOST-0001");
        dto.setOrgseq("ORG-0001");
        return dto;
    }

    /** source 필드를 target에 복사 */
    public static void copyTo(EPlatonCommonDTO target, EPlatonCommonDTO source) {
        if (target == null || source == null) {
            return;
        }
        target.setTerminalID(source.getTerminalID());
        target.setTerminalType(source.getTerminalType());
        target.setXmlSeq(source.getXmlSeq());
        target.setBankCode(source.getBankCode());
        target.setBranchCode(source.getBranchCode());
        target.setGlPostBranchCode(source.getGlPostBranchCode());
        target.setChannelType(source.getChannelType());
        target.setUserID(source.getUserID());
        target.setEventNo(source.getEventNo());
        target.setNation(source.getNation());
        target.setRegionCode(source.getRegionCode());
        target.setTimeZone(source.getTimeZone());
        target.setFxRateCount(source.getFxRateCount());
        target.setReqName(source.getReqName());
        target.setSystemDate(source.getSystemDate());
        target.setBusinessDate(source.getBusinessDate());
        target.setSystemInTime(source.getSystemInTime());
        target.setSystemOutTime(source.getSystemOutTime());
        target.setTransactionNo(source.getTransactionNo());
        target.setBaseCurrency(source.getBaseCurrency());
        target.setMultiPL(source.getMultiPL());
        target.setUserLevel(source.getUserLevel());
        target.setIPAddress(source.getIPAddress());
        target.setSystemName(source.getSystemName());
        target.setOperationName(source.getOperationName());
        target.setTpfq(source.getTpfq());
        target.setTxTimer(source.getTxTimer());
        target.setHostseq(source.getHostseq());
        target.setOrgseq(source.getOrgseq());
    }
}
