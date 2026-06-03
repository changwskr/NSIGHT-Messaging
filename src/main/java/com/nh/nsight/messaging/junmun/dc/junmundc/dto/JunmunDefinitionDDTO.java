package com.nh.nsight.messaging.junmun.dc.junmundc.dto;

import java.util.Date;

public record JunmunDefinitionDDTO(
        Long definitionId,
        String messageCode,
        String messageName,
        String transactionId,
        String serviceId,
        String direction,
        String standardVersion,
        String documentRef,
        String layoutJson,
        String sampleJson,
        String description,
        String useYn,
        String createdBy,
        Date createdAt,
        String updatedBy,
        Date updatedAt
) {
}
