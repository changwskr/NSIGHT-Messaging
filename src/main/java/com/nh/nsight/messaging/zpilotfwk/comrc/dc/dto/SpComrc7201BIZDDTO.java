package com.nh.nsight.messaging.zpilotfwk.comrc.dc.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.nh.nsight.messaging.zpilotfwk.tcf.support.DTO;

/** SP_COMRC 업무 DTO — order와 동일 필드 */
public class SpComrc7201BIZDDTO extends DTO {

    private Long id;
    private String orderNo;
    private String customerName;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer pageNo;
    private Integer pageSize;

    public static SpComrc7201BIZDDTO sample() {
        SpComrc7201BIZDDTO dto = new SpComrc7201BIZDDTO();
        dto.setOrderNo("ORD-COMRC-" + System.currentTimeMillis());
        dto.setCustomerName("김주문");
        dto.setAmount(new BigDecimal("15000"));
        dto.setStatus("CREATED");
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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
