package com.nh.nsight.messaging.zpilotfwk.tcf.support;

import java.util.ArrayList;
import java.util.List;

import com.nh.nsight.messaging.zpilotfwk.tcf.TPMSVCINFO;

public class TPSVCINFODTO extends DTO {

    private String errorcode = "IZZ000";
    private String errorMessage = "";
    private String systemName = "LOCAL";
    private String operationName = "operation";
    private String actionName = "action";
    private String hostseq = "0";
    private String orgseq = "0";
    private String txTimer = "60";
    private String tpfq = "200";
    private String trclass = "0";
    private String systemDate = "*";
    private String systemInTime = "*";
    private String systemOutTime = "*";
    private String logicLevel = "XXXX";
    private String stfIntime = "XXXXXXXX";
    private String stfOuttime = "XXXXXXXX";
    private String btfIntime = "XXXXXXXX";
    private String btfOuttime = "XXXXXXXX";
    private String etfIntime = "XXXXXXXX";
    private String etfOuttime = "XXXXXXXX";
    private final List<TPMSVCINFO> tpmsvcInfos = new ArrayList<>();

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public String getError_message() {
        return errorMessage;
    }

    public void setError_message(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSystem_name() {
        return systemName;
    }

    public void setSystem_name(String systemName) {
        this.systemName = systemName;
    }

    public String getOperation_name() {
        return operationName;
    }

    public void setOperation_name(String operationName) {
        this.operationName = operationName;
    }

    public String getAction_name() {
        return actionName;
    }

    public void setAction_name(String actionName) {
        this.actionName = actionName;
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

    public String getTx_timer() {
        return txTimer;
    }

    public void setTx_timer(String txTimer) {
        this.txTimer = txTimer;
    }

    public String getTpfq() {
        return tpfq;
    }

    public void setTpfq(String tpfq) {
        this.tpfq = tpfq;
    }

    public String getTrclass() {
        return trclass;
    }

    public void setTrclass(String trclass) {
        this.trclass = trclass;
    }

    public String getSystem_date() {
        return systemDate;
    }

    public void setSystem_date(String systemDate) {
        this.systemDate = systemDate;
    }

    public String getSystemInTime() {
        return systemInTime;
    }

    public void setSystemInTime(String systemInTime) {
        this.systemInTime = systemInTime;
    }

    public String getSystemOutTime() {
        return systemOutTime;
    }

    public void setSystemOutTime(String systemOutTime) {
        this.systemOutTime = systemOutTime;
    }

    public String getLogic_level() {
        return logicLevel;
    }

    public void setLogic_level(String logicLevel) {
        this.logicLevel = logicLevel;
    }

    public void setSTF_intime(String stfIntime) {
        this.stfIntime = stfIntime;
    }

    public void setSTF_outtime(String stfOuttime) {
        this.stfOuttime = stfOuttime;
    }

    public void setBTF_intime(String btfIntime) {
        this.btfIntime = btfIntime;
    }

    public void setBTF_outtime(String btfOuttime) {
        this.btfOuttime = btfOuttime;
    }

    public void setETF_intime(String etfIntime) {
        this.etfIntime = etfIntime;
    }

    public void setETF_outtime(String etfOuttime) {
        this.etfOuttime = etfOuttime;
    }

    public ArrayList getAllTPMSVCINFO() {
        return new ArrayList<>(tpmsvcInfos);
    }

    public void addTPMSVCINFO(TPMSVCINFO info) {
        tpmsvcInfos.add(info);
    }
}
