package com.nh.nsight.messaging.zpilotfwk.tcf;

import com.nh.nsight.messaging.zpilotfwk.tcf.support.Constants;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.EPlatonDtoHelper;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.IDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.TPSVCINFODTO;

/**
 * 
 * Application-Business layer 전송 객체.
 * 
 */

public class EPlatonEvent {

    private String action;

    private EPlatonCommonDTO common;

    private EPlatonErrDTO err;

    private TPSVCINFODTO tpmsvc;

    private IDTO request;

    private IDTO response;

    public EPlatonEvent() {

        this.action = "xxxxxxxx";

        this.common = new EPlatonCommonDTO();

        this.tpmsvc = new TPSVCINFODTO();

        this.err = EPlatonErrDTO.from(this.tpmsvc);

    }

    public EPlatonCommonDTO getCommon() {

        return common;

    }

    public void setCommon(EPlatonCommonDTO common) {

        this.common = common;

    }

    /** TPSVCINFODTO와 동기화된 에러 DTO (읽기 시 tpmsvc 기준 반영) */
    public EPlatonErrDTO getErr() {
        err = EPlatonErrDTO.from(tpmsvc);
        return err;
    }

    public void setErr(EPlatonErrDTO err) {

        this.err = err != null ? err : new EPlatonErrDTO();

        this.err.applyTo(tpmsvc);

    }

    public TPSVCINFODTO getTPSVCINFODTO() {

        return tpmsvc;

    }

    public void setTPSVCINFO(TPSVCINFODTO tpmsvc) {

        this.tpmsvc = tpmsvc;

        this.err = EPlatonErrDTO.from(this.tpmsvc);

    }

    public void setAction(String action) {

        this.action = action;

    }

    public String getAction() {

        return action;

    }

    public void setRequest(IDTO request) {

        this.request = request;

    }

    public IDTO getRequest() {

        return request;

    }

    public void setResponse(IDTO response) {

        this.response = response;

    }

    public IDTO getResponse() {

        return response;

    }

    public <T extends IDTO> T getRequestAs(Class<T> type) {

        return EPlatonDtoHelper.cast(request, type);

    }

    public <T extends IDTO> T getResponseAs(Class<T> type) {

        return EPlatonDtoHelper.cast(response, type);

    }

    @Override

    public String toString() {

        return "{" + getClass().getName() + "@" + this.hashCode() + Constants.LINE_SEPARATOR

                + "(action=" + this.action + ")" + Constants.LINE_SEPARATOR

                + "(common=" + this.common + ")" + Constants.LINE_SEPARATOR

                + "(err=" + this.getErr() + ")" + Constants.LINE_SEPARATOR

                + "(request=" + this.request + ")" + Constants.LINE_SEPARATOR

                + "(response=" + this.response + ")}";

    }

}
