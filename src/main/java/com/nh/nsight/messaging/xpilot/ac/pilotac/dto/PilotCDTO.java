package com.nh.nsight.messaging.xpilot.ac.pilotac.dto;

/**
 * Pilot CDTO — AC·AS 경계(문자열 필드).
 */
public class PilotCDTO {

    private String pilotId;
    private String pilotName;
    private String targetModule;
    private String sourceStructure;
    private String targetStructure;
    private String status;
    private String envRunId;
    private String note;
    private String createdDate;
    private String updatedDate;

    public String getPilotId() {
        return pilotId;
    }

    public void setPilotId(String pilotId) {
        this.pilotId = pilotId;
    }

    public String getPilotName() {
        return pilotName;
    }

    public void setPilotName(String pilotName) {
        this.pilotName = pilotName;
    }

    public String getTargetModule() {
        return targetModule;
    }

    public void setTargetModule(String targetModule) {
        this.targetModule = targetModule;
    }

    public String getSourceStructure() {
        return sourceStructure;
    }

    public void setSourceStructure(String sourceStructure) {
        this.sourceStructure = sourceStructure;
    }

    public String getTargetStructure() {
        return targetStructure;
    }

    public void setTargetStructure(String targetStructure) {
        this.targetStructure = targetStructure;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEnvRunId() {
        return envRunId;
    }

    public void setEnvRunId(String envRunId) {
        this.envRunId = envRunId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }
}
