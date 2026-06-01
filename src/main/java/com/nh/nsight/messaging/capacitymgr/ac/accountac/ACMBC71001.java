package com.nh.nsight.messaging.capacitymgr.ac.accountac;

import com.nh.nsight.messaging.capacitymgr.ac.accountac.dto.AccountApiResponse;
import com.nh.nsight.messaging.capacitymgr.ac.accountac.dto.AccountCDTO;
import com.nh.nsight.messaging.capacitymgr.as.accountas.ASMBC71001;
import com.nh.nsight.messaging.capacitymgr.zcommonutil.CapacityBizException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 계정 생성 Application Control — REST는 {@link AccountCDTO}만 사용.
 */
@RestController
@RequestMapping("/api/capacitymgr/account")
public class ACMBC71001 {

    private final ASMBC71001 asmBc71001;

    public ACMBC71001(ASMBC71001 asmBc71001) {
        this.asmBc71001 = asmBc71001;
    }

    @PostMapping("/create")
    public ResponseEntity<AccountApiResponse<AccountCDTO>> create(@RequestBody AccountCDTO accountCDTO) {
        AccountCDTO created = asmBc71001.create(accountCDTO);
        return ResponseEntity.ok(AccountApiResponse.ok(created, "계정이 생성되었습니다."));
    }

    @GetMapping("/create/sample")
    public ResponseEntity<AccountApiResponse<AccountCDTO>> createSample() {
        AccountCDTO sample = new AccountCDTO();
        sample.setAccountNumber("ACC-SAMPLE-001");
        sample.setName("샘플계좌");
        sample.setAccountType("SAVINGS");
        sample.setStatus("ACTIVE");
        sample.setCurrency("KRW");
        sample.setNetAmount("1000");
        return create(sample);
    }

    @ExceptionHandler(CapacityBizException.class)
    public ResponseEntity<AccountApiResponse<Void>> handleBiz(CapacityBizException ex) {
        return ResponseEntity.badRequest().body(AccountApiResponse.fail(ex.getMessage()));
    }
}
