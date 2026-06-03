package com.nh.nsight.messaging.junmun.ac.junmunac;

import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunApiResponse;
import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunDefinitionCDTO;
import com.nh.nsight.messaging.junmun.as.junmunas.ASMJM71001;
import com.nh.nsight.messaging.junmun.util.JunmunBizException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/junmun/definitions")
public class ACMJM71001 {

    private final ASMJM71001 asmjm71001;

    public ACMJM71001(ASMJM71001 asmjm71001) {
        this.asmjm71001 = asmjm71001;
    }

    @PostMapping
    public ResponseEntity<JunmunApiResponse<JunmunDefinitionCDTO>> create(
            @RequestBody JunmunDefinitionCDTO request) {
        JunmunDefinitionCDTO created = asmjm71001.create(request);
        return ResponseEntity.ok(JunmunApiResponse.ok(created, "전문 정의가 등록되었습니다."));
    }

    @ExceptionHandler(JunmunBizException.class)
    public ResponseEntity<JunmunApiResponse<Void>> handleBiz(JunmunBizException ex) {
        return ResponseEntity.badRequest().body(JunmunApiResponse.fail(ex.getMessage()));
    }
}
