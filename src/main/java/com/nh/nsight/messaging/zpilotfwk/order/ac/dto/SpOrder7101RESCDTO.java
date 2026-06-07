package com.nh.nsight.messaging.zpilotfwk.order.ac.dto;

import com.nh.nsight.messaging.zpilotfwk.order.dc.dto.SpOrder7101BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonCommonDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonErrDTO;

public class SpOrder7101RESCDTO {

    private EPlatonErrDTO err;
    private EPlatonCommonDTO common;
    private SpOrder7101BIZDDTO bizData;

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

    public SpOrder7101BIZDDTO getBizData() {
        return bizData;
    }

    public void setBizData(SpOrder7101BIZDDTO bizData) {
        this.bizData = bizData;
    }
}
