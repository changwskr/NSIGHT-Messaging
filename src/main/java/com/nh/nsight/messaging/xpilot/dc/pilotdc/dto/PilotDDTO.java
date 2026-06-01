package com.nh.nsight.messaging.xpilot.dc.pilotdc.dto;

import java.util.Date;

/** DC 내부 Pilot DTO */
public class PilotDDTO {

    private String pilotId;
    private String pilotName;
    private String targetModule;
    private String sourceStructure;
    private String targetStructure;
    private String status;
    private String envRunId;
    private String note;
    private Date createdDate;
    private Date updatedDate;

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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
