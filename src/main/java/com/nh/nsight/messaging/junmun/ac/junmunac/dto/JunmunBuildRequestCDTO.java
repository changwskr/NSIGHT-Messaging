package com.nh.nsight.messaging.junmun.ac.junmunac.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class JunmunBuildRequestCDTO {

    private Map<String, String> fieldValues = new LinkedHashMap<>();
    private Map<String, Object> control;
    private Map<String, Object> security;
    private Map<String, Object> error;

    public Map<String, String> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(Map<String, String> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public Map<String, Object> getControl() {
        return control;
    }

    public void setControl(Map<String, Object> control) {
        this.control = control;
    }

    public Map<String, Object> getSecurity() {
        return security;
    }

    public void setSecurity(Map<String, Object> security) {
        this.security = security;
    }

    public Map<String, Object> getError() {
        return error;
    }

    public void setError(Map<String, Object> error) {
        this.error = error;
    }
}
