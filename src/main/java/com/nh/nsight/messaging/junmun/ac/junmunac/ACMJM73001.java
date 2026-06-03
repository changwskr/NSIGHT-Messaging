package com.nh.nsight.messaging.junmun.ac.junmunac;

import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunApiResponse;
import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunDefinitionCDTO;
import com.nh.nsight.messaging.junmun.as.junmunas.ASMJM73001;
import com.nh.nsight.messaging.junmun.util.JunmunBizException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/junmun/definitions")
public class ACMJM73001 {

    private final ASMJM73001 asmjm73001;

    public ACMJM73001(ASMJM73001 asmjm73001) {
        this.asmjm73001 = asmjm73001;
    }

    @PutMapping("/{messageCode}")
    public ResponseEntity<JunmunApiResponse<JunmunDefinitionCDTO>> update(
            @PathVariable String messageCode,
            @RequestBody JunmunDefinitionCDTO request) {
        request.setMessageCode(messageCode);
        return ResponseEntity.ok(JunmunApiResponse.ok(asmjm73001.update(request), "전문 정의가 수정되었습니다."));
    }

    @DeleteMapping("/{messageCode}")
    public ResponseEntity<JunmunApiResponse<Void>> delete(@PathVariable String messageCode) {
        asmjm73001.delete(messageCode);
        return ResponseEntity.ok(JunmunApiResponse.ok(null, "전문 정의가 삭제되었습니다."));
    }

    @ExceptionHandler(JunmunBizException.class)
    public ResponseEntity<JunmunApiResponse<Void>> handleBiz(JunmunBizException ex) {
        return ResponseEntity.badRequest().body(JunmunApiResponse.fail(ex.getMessage()));
    }
}
