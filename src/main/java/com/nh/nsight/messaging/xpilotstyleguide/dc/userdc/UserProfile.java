package com.nh.nsight.messaging.xpilotstyleguide.dc.userdc;

import java.util.Date;

/**
 * 사용자정보 엔티티.
 */
public class UserProfile {

    private static final String ENTITY = "UserProfile";

    private String userId;
    private String userName;
    private String email;
    private String phoneNumber;
    private String roleCode;
    private String status;
    private Date createdDate;
    private Date updatedDate;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        System.out.println("★★★★★ [" + ENTITY + "] setUserId " + userId);
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        System.out.println("★★★★★ [" + ENTITY + "] setStatus " + status);
        this.status = status;
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
