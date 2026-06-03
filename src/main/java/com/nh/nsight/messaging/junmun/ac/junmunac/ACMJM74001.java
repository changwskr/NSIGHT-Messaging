package com.nh.nsight.messaging.junmun.ac.junmunac;

import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunApiResponse;
import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunBuildRequestCDTO;
import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunBuildResultCDTO;
import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunDefinitionCDTO;
import com.nh.nsight.messaging.junmun.as.junmunas.ASMJM74001;
import com.nh.nsight.messaging.junmun.util.JunmunBizException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/junmun")
public class ACMJM74001 {

    private final ASMJM74001 asmjm74001;

    public ACMJM74001(ASMJM74001 asmjm74001) {
        this.asmjm74001 = asmjm74001;
    }

    @GetMapping("/defaults")
    public ResponseEntity<JunmunApiResponse<JunmunDefinitionCDTO>> defaults() {
        return ResponseEntity.ok(JunmunApiResponse.ok(asmjm74001.defaults(), "PH1 내부표준전문 기본 정의"));
    }

    @PostMapping("/definitions/{messageCode}/build")
    public ResponseEntity<JunmunApiResponse<JunmunBuildResultCDTO>> build(
            @PathVariable String messageCode,
            @RequestBody(required = false) JunmunBuildRequestCDTO request) {
        JunmunBuildResultCDTO result = asmjm74001.build(messageCode, request);
        return ResponseEntity.ok(JunmunApiResponse.ok(result, "JSON 전문이 생성되었습니다."));
    }

    @PostMapping("/definitions/{messageCode}/validate")
    public ResponseEntity<JunmunApiResponse<JunmunBuildResultCDTO>> validate(
            @PathVariable String messageCode,
            @RequestBody Map<String, String> body) {
        String envelope = body != null ? body.get("envelopeJson") : null;
        if (envelope == null || envelope.isBlank()) {
            return ResponseEntity.badRequest().body(JunmunApiResponse.fail("envelopeJson이 필요합니다."));
        }
        JunmunBuildResultCDTO result = asmjm74001.validate(messageCode, envelope);
        String msg = result.isValid() ? "전문 검증 성공" : "전문 검증 실패";
        return ResponseEntity.ok(JunmunApiResponse.ok(result, msg));
    }

    @ExceptionHandler(JunmunBizException.class)
    public ResponseEntity<JunmunApiResponse<Void>> handleBiz(JunmunBizException ex) {
        return ResponseEntity.badRequest().body(JunmunApiResponse.fail(ex.getMessage()));
    }
}
