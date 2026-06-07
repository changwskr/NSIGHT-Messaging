package com.nh.nsight.messaging.zpilotfwk.comrc.ac.dto;

import com.nh.nsight.messaging.zpilotfwk.comrc.dc.dto.SpComrc7201BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonCommonDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonErrDTO;

public class SpComrc7201RESCDTO {

    private EPlatonErrDTO err;
    private EPlatonCommonDTO common;
    private SpComrc7201BIZDDTO bizData;

    public EPlatonErrDTO getErr() {
        return err;
    }

    public void setErr(EPlatonErrDTO err) {
        this.err = err;
    }

    public EPlatonCommonDTO getCommon() {
        return common;
    }

    public void setCommon(EPlatonCommonDTO common) {
        this.common = common;
    }

    public SpComrc7201BIZDDTO getBizData() {
        return bizData;
    }

    public void setBizData(SpComrc7201BIZDDTO bizData) {
        this.bizData = bizData;
    }
}
