package com.nh.nsight.messaging.junmun.ac.junmunac.dto;

import com.nh.nsight.messaging.junmun.dc.junmundc.dto.JunmunDefinitionDDTO;
import com.nh.nsight.messaging.junmun.util.JunmunBizException;
import com.nh.nsight.messaging.junmun.util.JunmunPh1StandardCatalog;

import java.util.ArrayList;
import java.util.List;

public final class JunmunCDtoConverter {

    private JunmunCDtoConverter() {
    }

    public static JunmunDefinitionDDTO toDomain(JunmunDefinitionCDTO request) {
        if (request == null) {
            throw new JunmunBizException("전문 정의 요청이 비어 있습니다.");
        }
        return new JunmunDefinitionDDTO(
                request.getDefinitionId(),
                trim(request.getMessageCode()),
                trim(request.getMessageName()),
                trim(request.getTransactionId()),
                trim(request.getServiceId()),
                normalizeDirection(request.getDirection()),
                request.getStandardVersion() != null ? request.getStandardVersion() : JunmunPh1StandardCatalog.STANDARD_VERSION,
                request.getDocumentRef(),
                request.getLayoutJson(),
                request.getSampleJson(),
                request.getDescription(),
                request.getUseYn() != null ? request.getUseYn() : "Y",
                request.getCreatedBy(),
                request.getCreatedAt(),
                request.getUpdatedBy(),
                request.getUpdatedAt()
        );
    }

    public static JunmunDefinitionCDTO toCDto(JunmunDefinitionDDTO domain) {
        if (domain == null) {
            return null;
        }
        JunmunDefinitionCDTO dto = new JunmunDefinitionCDTO();
        dto.setDefinitionId(domain.definitionId());
        dto.setMessageCode(domain.messageCode());
        dto.setMessageName(domain.messageName());
        dto.setTransactionId(domain.transactionId());
        dto.setServiceId(domain.serviceId());
        dto.setDirection(domain.direction());
        dto.setStandardVersion(domain.standardVersion());
        dto.setDocumentRef(domain.documentRef());
        dto.setLayoutJson(domain.layoutJson());
        dto.setSampleJson(domain.sampleJson());
        dto.setDescription(domain.description());
        dto.setUseYn(domain.useYn());
        dto.setCreatedBy(domain.createdBy());
        dto.setCreatedAt(domain.createdAt());
        dto.setUpdatedBy(domain.updatedBy());
        dto.setUpdatedAt(domain.updatedAt());
        return dto;
    }

    public static List<JunmunDefinitionCDTO> toCDtoList(List<JunmunDefinitionDDTO> domains) {
        List<JunmunDefinitionCDTO> list = new ArrayList<>();
        if (domains == null) {
            return list;
        }
        for (JunmunDefinitionDDTO domain : domains) {
            list.add(toCDto(domain));
        }
        return list;
    }

    public static JunmunDefinitionCDTO defaultPh1Request() {
        JunmunDefinitionCDTO dto = new JunmunDefinitionCDTO();
        dto.setMessageCode(JunmunPh1StandardCatalog.DEFAULT_MESSAGE_CODE);
        dto.setMessageName("계좌조회 요청 (PH1 내부표준전문)");
        dto.setTransactionId(JunmunPh1StandardCatalog.DEFAULT_TRANSACTION_ID);
        dto.setServiceId(JunmunPh1StandardCatalog.DEFAULT_SERVICE_ID);
        dto.setDirection("REQ");
        dto.setStandardVersion(JunmunPh1StandardCatalog.STANDARD_VERSION);
        dto.setDocumentRef(JunmunPh1StandardCatalog.DOCUMENT_REF);
        dto.setLayoutJson(JunmunPh1StandardCatalog.defaultLayoutJson());
        dto.setSampleJson(JunmunPh1StandardCatalog.defaultSampleEnvelopeJson());
        dto.setDescription("AA PH1 내부표준전문 — 시스템/공통/업무 헤더 + JSON 업무본문");
        dto.setUseYn("Y");
        dto.setCreatedBy("ARCHITECT");
        dto.setUpdatedBy("ARCHITECT");
        return dto;
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }

    private static String normalizeDirection(String direction) {
        if (direction == null || direction.isBlank()) {
            return "REQ";
        }
        return direction.trim().toUpperCase();
    }
}
