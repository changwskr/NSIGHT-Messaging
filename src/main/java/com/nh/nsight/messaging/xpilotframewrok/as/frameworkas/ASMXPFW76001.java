package com.nh.nsight.messaging.xpilotframewrok.as.frameworkas;

import com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac.dto.FwProcessRequestCDTO;
import com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac.dto.FwCDtoConverter;
import com.nh.nsight.messaging.xpilotframewrok.common.context.FwProcessContext;
import com.nh.nsight.messaging.xpilotframewrok.common.pipeline.FwProcessOrchestrator;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * AS(Application Service) 계층 — 프로그램 ID: MXPFW76001.
 * xpilotFramework 거래 처리 파이프라인(전처리·업무·후처리) 실행을 담당한다.
 */
@Service
public class ASMXPFW76001 {

    /** 로그 출력용 AS 프로그램 식별자 */
    private static final String AS = "ASMXPFW76001";

    /** 전처리·업무·후처리 오케스트레이터 */
    private final FwProcessOrchestrator orchestrator;

    public ASMXPFW76001(FwProcessOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    /**
     * 거래 처리 요청을 수신하여 파이프라인을 실행한다.
     *
     * @param request AC 거래 처리 요청 DTO
     * @return 업무 처리 결과 Map
     */
    public Map<String, Object> process(FwProcessRequestCDTO request) {
        // START 로그
        System.out.println("★★★★★★★ [" + AS + "] process START transactionId=" + request.getTransactionId());
        // 요청 DTO → 파이프라인 공유 컨텍스트 변환
        FwProcessContext context = FwCDtoConverter.toProcessContext(request);
        // 오케스트레이터에 전처리·업무·후처리 실행 위임
        Map<String, Object> result = orchestrator.execute(context, () -> Map.of(
                "transactionId", context.getTransactionId(),
                "serviceId", context.getServiceId(),
                "processed", true
        ));
        // END 로그
        System.out.println("★★★★★★★ [" + AS + "] process END transactionId=" + request.getTransactionId());
        return result;
    }
}
