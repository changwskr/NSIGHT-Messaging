package com.nh.nsight.messaging.zpilotfwk.order.dc.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.nh.nsight.messaging.zpilotfwk.tcf.support.DTO;

/** SP_ORDER 업무 DTO — AC / AS / DC 공용 */
public class SpOrder7101BIZDDTO extends DTO {

    private Long id;
    private String orderNo;
    private String customerName;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer pageNo;
    private Integer pageSize;

    public static SpOrder7101BIZDDTO sample() {
        SpOrder7101BIZDDTO dto = new SpOrder7101BIZDDTO();
        dto.setOrderNo("ORD-" + System.currentTimeMillis());
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
