package com.nh.nsight.messaging.zpilotfwk.common.ac.dto;

import com.nh.nsight.messaging.zpilotfwk.common.dc.dto.SpCommon7001BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonCommonDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonErrDTO;

/**
 * TCF execute API 결과 DTO.
 * <p>
 * {@link SpCommon7001REQCDTO}와 대칭 구조: err + common + bizData
 * </p>
 */
public class SpCommon7001RESCDTO {

    private EPlatonErrDTO err;
    private EPlatonCommonDTO common;
    private SpCommon7001BIZDDTO bizData;

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

    public SpCommon7001BIZDDTO getBizData() {
        return bizData;
    }

    public void setBizData(SpCommon7001BIZDDTO bizData) {
        this.bizData = bizData;
    }
}
