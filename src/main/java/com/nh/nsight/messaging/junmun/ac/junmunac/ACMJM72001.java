package com.nh.nsight.messaging.junmun.ac.junmunac;

import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunApiResponse;
import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunDefinitionCDTO;
import com.nh.nsight.messaging.junmun.as.junmunas.ASMJM72001;
import com.nh.nsight.messaging.junmun.util.JunmunBizException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/junmun/definitions")
public class ACMJM72001 {

    private final ASMJM72001 asmjm72001;

    public ACMJM72001(ASMJM72001 asmjm72001) {
        this.asmjm72001 = asmjm72001;
    }

    @GetMapping("/{messageCode}")
    public ResponseEntity<JunmunApiResponse<JunmunDefinitionCDTO>> get(@PathVariable String messageCode) {
        return ResponseEntity.ok(JunmunApiResponse.ok(asmjm72001.get(messageCode)));
    }

    @GetMapping
    public ResponseEntity<JunmunApiResponse<List<JunmunDefinitionCDTO>>> list(
            @ModelAttribute JunmunDefinitionCDTO criteria) {
        return ResponseEntity.ok(JunmunApiResponse.ok(asmjm72001.list(criteria)));
    }

    @ExceptionHandler(JunmunBizException.class)
    public ResponseEntity<JunmunApiResponse<Void>> handleBiz(JunmunBizException ex) {
        return ResponseEntity.badRequest().body(JunmunApiResponse.fail(ex.getMessage()));
    }
}
