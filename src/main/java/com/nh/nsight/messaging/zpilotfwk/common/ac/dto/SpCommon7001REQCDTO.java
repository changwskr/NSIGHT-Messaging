package com.nh.nsight.messaging.zpilotfwk.common.ac.dto;

import com.nh.nsight.messaging.zpilotfwk.common.dc.dto.SpCommon7001BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonCommonDTO;

/**
 * AC → TCF execute API 요청 DTO.
 */
public class SpCommon7001REQCDTO {

    private EPlatonCommonDTO common;
    private SpCommon7001BIZDDTO bizData;

    public EPlatonCommonDTO getCommon() {
        return common;
    }

    public void setCommon(EPlatonCommonDTO common) {
        this.common = common;
    }

    public SpCommon7001BIZDDTO getBizData() {
        return bizData;
    }

    public void setBizData(SpCommon7001BIZDDTO bizData) {
        this.bizData = bizData;
    }
}
