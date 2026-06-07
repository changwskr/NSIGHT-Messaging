package com.nh.nsight.messaging.zpilotfwk.tcf;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.TPSVCINFODTO;

/**
 * EPlatonEvent 에러 정보 DTO.
 */
public class EPlatonErrDTO {

    private String errcode = "IZZ000";
    private String errorMessage = "";

    public static EPlatonErrDTO of(String errcode, String errorMessage) {
        EPlatonErrDTO dto = new EPlatonErrDTO();
        dto.setErrcode(errcode);
        dto.setErrorMessage(errorMessage);
        return dto;
    }

    public static EPlatonErrDTO from(TPSVCINFODTO tpmsvc) {
        if (tpmsvc == null) {
            return new EPlatonErrDTO();
        }
        return of(tpmsvc.getErrorcode(), tpmsvc.getError_message());
    }

    public void applyTo(TPSVCINFODTO tpmsvc) {
        if (tpmsvc == null) {
            return;
        }
        tpmsvc.setErrorcode(errcode);
        tpmsvc.setError_message(errorMessage);
    }

    @JsonProperty("errorcode")
    @JsonAlias("errcode")
    public String getErrcode() {
        return errcode;
    }

    @JsonProperty("errorcode")
    @JsonAlias("errcode")
    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
