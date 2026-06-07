package com.nh.nsight.messaging.zpilotfwk.common.dc.dto;

import java.time.LocalDateTime;

import com.nh.nsight.messaging.zpilotfwk.tcf.support.DTO;

/**
 * SP_COMMON 7001 업무 DTO — AC / AS / DC 공용.
 * <p>
 * CRUD 필드와 목록 조회 조건({@code pageNo}, {@code pageSize})을 함께 담는다.
 * </p>
 */
public class SpCommon7001BIZDDTO extends DTO {

    private Long id;
    private String name;
    private Integer age;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer pageNo;
    private Integer pageSize;

    public static SpCommon7001BIZDDTO sample() {
        SpCommon7001BIZDDTO dto = new SpCommon7001BIZDDTO();
        dto.setName("홍길동");
        dto.setAge(30);
        dto.setPhoneNumber("010-1234-5678");
        return dto;
    }

    public int getOffset() {
        return (getSafePageNo() - 1) * getSafePageSize();
    }

    public int getSafePageNo() {
        return pageNo == null || pageNo < 1 ? 1 : pageNo;
    }

    public int getSafePageSize() {
        return pageSize == null || pageSize < 1 ? 10 : pageSize;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
