package com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac.dto;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.FwAuditLog;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.FwTxLog;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.FwTxStatus;
import com.nh.nsight.messaging.xpilotframewrok.dc.frameworkdc.dto.FwLogSearchDDTO;
import com.nh.nsight.messaging.xpilotframewrok.common.context.FwProcessContext;

import java.util.List;
import java.util.Map;

/**
 * AC 계층 DTO 변환 유틸리티.
 * REST 요청/응답 DTO(C)와 DC 도메인·검색 DTO(D) 및 처리 컨텍스트 간 변환을 담당한다.
 */
public final class FwCDtoConverter {

    private FwCDtoConverter() {
    }

    /**
     * REST 검색 파라미터를 DC 검색 DTO로 변환한다.
     *
     * @param guid       글로벌 고유 식별자
     * @param traceId    추적 ID
     * @param serviceId  서비스 ID
     * @param userId     사용자 ID
     * @param resultCode 결과 코드
     * @param actionType 행위 유형 (감사 로그 검색 시 사용)
     * @param pageNo     페이지 번호
     * @param pageSize   페이지 크기
     * @return DC 계층 검색 조건 DTO
     */
    public static FwLogSearchDDTO toSearchDDTO(String guid, String traceId, String serviceId,
            String userId, String resultCode, String actionType, int pageNo, int pageSize) {
        FwLogSearchDDTO dto = new FwLogSearchDDTO();
        dto.setGuid(guid);
        dto.setTraceId(traceId);
        dto.setServiceId(serviceId);
        dto.setUserId(userId);
        dto.setResultCode(resultCode);
        dto.setActionType(actionType);
        dto.setPageNo(pageNo);
        dto.setPageSize(pageSize);
        return dto;
    }

    /**
     * DC 트랜잭션 로그 엔티티를 AC 응답 DTO로 변환한다.
     *
     * @param row DC 트랜잭션 로그 (null이면 null 반환)
     * @return AC 성능 로그 응답 DTO
     */
    public static FwLogResponse toLogResponse(FwTxLog row) {
        if (row == null) {
            return null;
        }
        FwLogResponse res = new FwLogResponse();
        res.setLogId(row.getLogId());
        res.setGuid(row.getGuid());
        res.setTraceId(row.getTraceId());
        res.setServiceId(row.getServiceId());
        res.setUserId(row.getUserId());
        res.setRequestUri(row.getRequestUri());
        res.setHttpMethod(row.getHttpMethod());
        res.setResultCode(row.getResultCode());
        res.setErrorCode(row.getErrorCode());
        res.setApId(row.getApId());
        res.setDbTime(row.getDbTime());
        res.setExtTime(row.getExtTime());
        res.setTotalTime(row.getTotalTime());
        res.setLogTime(row.getLogTime());
        return res;
    }

    /**
     * DC 트랜잭션 로그 목록을 AC 응답 DTO 목록으로 변환한다.
     *
     * @param rows DC 트랜잭션 로그 목록
     * @return AC 성능 로그 응답 DTO 목록
     */
    public static List<FwLogResponse> toLogResponseList(List<FwTxLog> rows) {
        return rows.stream().map(FwCDtoConverter::toLogResponse).toList();
    }

    /**
     * 성능 로그 목록 조회 결과를 페이징 표준 응답으로 조립한다.
     *
     * @param rows     조회된 로그 목록
     * @param criteria 검색·페이징 조건
     * @param total    전체 건수
     * @return 페이징 메타데이터를 포함한 표준 응답
     */
    public static StandardResponse<List<FwLogResponse>> toPagedLogResponse(
            List<FwLogResponse> rows, FwLogSearchDDTO criteria, long total) {
        return StandardResponse.successPage(
                "XPFW-TX-LIST-001", "xpilotFrameworkPerfLogList", rows,
                criteria.getSafePageNo(), criteria.getSafePageSize(), total);
    }

    /**
     * 거래 처리 결과를 표준 성공 응답으로 조립한다.
     *
     * @param body AS 계층 처리 결과
     * @return 표준 응답 래퍼
     */
    public static StandardResponse<Map<String, Object>> toProcessResponse(Map<String, Object> body) {
        return StandardResponse.success("XPFW-PROCESS-001", "xpilotFrameworkProcess", body);
    }

    /**
     * DC 감사 로그 엔티티를 AC 응답 DTO로 변환한다.
     *
     * @param row DC 감사 로그 (null이면 null 반환)
     * @return AC 감사 로그 응답 DTO
     */
    public static FwAuditLogResponse toAuditResponse(FwAuditLog row) {
        if (row == null) {
            return null;
        }
        FwAuditLogResponse res = new FwAuditLogResponse();
        res.setAuditId(row.getAuditId());
        res.setGuid(row.getGuid());
        res.setUserId(row.getUserId());
        res.setBranchId(row.getBranchId());
        res.setMenuId(row.getMenuId());
        res.setFunctionId(row.getFunctionId());
        res.setCustomerId(row.getCustomerId());
        res.setActionType(row.getActionType());
        res.setAccessPurpose(row.getAccessPurpose());
        res.setMaskingLevel(row.getMaskingLevel());
        res.setResultCode(row.getResultCode());
        res.setClientIp(row.getClientIp());
        res.setAuditTime(row.getAuditTime());
        return res;
    }

    /**
     * DC 감사 로그 목록을 AC 응답 DTO 목록으로 변환한다.
     *
     * @param rows DC 감사 로그 목록
     * @return AC 감사 로그 응답 DTO 목록
     */
    public static List<FwAuditLogResponse> toAuditResponseList(List<FwAuditLog> rows) {
        return rows.stream().map(FwCDtoConverter::toAuditResponse).toList();
    }

    /**
     * DC 거래 상태 엔티티를 AC 응답 DTO로 변환한다.
     * 조회 결과가 없으면 UNKNOWN 상태 기본값을 반환한다.
     *
     * @param row DC 거래 상태 (null 가능)
     * @return AC 거래 상태 응답 DTO
     */
    public static FwTxStatusResponse toStatusResponse(FwTxStatus row) {
        FwTxStatusResponse res = new FwTxStatusResponse();
        // 조회 결과 없음 → UNKNOWN 기본 응답
        if (row == null) {
            res.setStatus("UNKNOWN");
            res.setRetryAllowedYn("Y");
            res.setMessage("거래 상태를 찾을 수 없습니다.");
            res.setResultCode("COM-0000");
            return res;
        }
        res.setStatus(row.getStatus());
        // PROCESSING·UNKNOWN 상태만 재시도 허용
        res.setRetryAllowedYn("PROCESSING".equals(row.getStatus()) || "UNKNOWN".equals(row.getStatus()) ? "Y" : "N");
        // 상태별 사용자 안내 메시지 매핑
        res.setMessage(switch (row.getStatus()) {
            case "SUCCESS" -> "이미 정상 처리된 거래입니다.";
            case "PROCESSING" -> "처리 중인 거래입니다.";
            case "FAIL" -> "실패한 거래입니다.";
            default -> "거래 상태: " + row.getStatus();
        });
        res.setResultCode(row.getResultCode() != null ? row.getResultCode() : "COM-0000");
        return res;
    }

    /**
     * AC 거래 처리 요청 DTO를 파이프라인 공유 컨텍스트로 변환한다.
     *
     * @param request AC 거래 처리 요청 DTO (null이면 빈 컨텍스트 반환)
     * @return 전처리·후처리 파이프라인에서 사용하는 처리 컨텍스트
     */
    public static FwProcessContext toProcessContext(FwProcessRequestCDTO request) {
        FwProcessContext ctx = new FwProcessContext();
        if (request == null) {
            return ctx;
        }
        ctx.setTransactionId(request.getTransactionId());
        ctx.setServiceId(request.getServiceId());
        ctx.setIdempotencyKey(request.getIdempotencyKey());
        ctx.setActionType(request.getActionType());
        ctx.setMenuId(request.getMenuId());
        ctx.setFunctionId(request.getFunctionId());
        ctx.setCustomerId(request.getCustomerId());
        ctx.setAccessPurpose(request.getAccessPurpose());
        ctx.setRequestUri(request.getRequestUri());
        ctx.setHttpMethod(request.getHttpMethod());
        // 기본 마스킹 수준 설정
        ctx.setMaskingLevel("GENERAL");
        return ctx;
    }
}
