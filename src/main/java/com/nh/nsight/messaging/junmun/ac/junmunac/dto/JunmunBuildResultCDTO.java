package com.nh.nsight.messaging.junmun.ac.junmunac.dto;

import java.util.List;

public class JunmunBuildResultCDTO {

    private String messageCode;
    private String envelopeJson;
    private boolean valid;
    private List<String> validationErrors;

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getEnvelopeJson() {
        return envelopeJson;
    }

    public void setEnvelopeJson(String envelopeJson) {
        this.envelopeJson = envelopeJson;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
