package com.nh.nsight.messaging.zpilotfwk.order.ac.dto;

import com.nh.nsight.messaging.zpilotfwk.order.dc.dto.SpOrder7101BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonCommonDTO;

public class SpOrder7101REQCDTO {

    private EPlatonCommonDTO common;
    private SpOrder7101BIZDDTO bizData;

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
