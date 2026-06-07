package com.nh.nsight.messaging.zpilotfwk.comrc.ac.dto;

import com.nh.nsight.messaging.zpilotfwk.comrc.dc.dto.SpComrc7201BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonCommonDTO;

public class SpComrc7201REQCDTO {

    private EPlatonCommonDTO common;
    private SpComrc7201BIZDDTO bizData;

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
