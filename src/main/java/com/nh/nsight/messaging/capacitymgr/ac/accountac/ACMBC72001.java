package com.nh.nsight.messaging.capacitymgr.ac.accountac;

import com.nh.nsight.messaging.capacitymgr.ac.accountac.dto.AccountApiResponse;
import com.nh.nsight.messaging.capacitymgr.ac.accountac.dto.AccountCDTO;
import com.nh.nsight.messaging.capacitymgr.as.accountas.ASMBC72001;
import com.nh.nsight.messaging.capacitymgr.util.CapacityBizException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 계정 조회 Application Control — REST는 {@link AccountCDTO}만 사용.
 */
@RestController
@RequestMapping("/api/capacitymgr/account")
public class ACMBC72001 {

    private final ASMBC72001 asmBc72001;

    public ACMBC72001(ASMBC72001 asmBc72001) {
        this.asmBc72001 = asmBc72001;
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountApiResponse<AccountCDTO>> get(@PathVariable String accountNumber) {
        return ResponseEntity.ok(AccountApiResponse.ok(asmBc72001.get(accountNumber)));
    }

    @GetMapping
    public ResponseEntity<AccountApiResponse<List<AccountCDTO>>> list(
            @ModelAttribute AccountCDTO criteria) {
        List<AccountCDTO> accounts = asmBc72001.list(criteria);
        return ResponseEntity.ok(AccountApiResponse.okList(accounts));
    }

    @ExceptionHandler(CapacityBizException.class)
    public ResponseEntity<AccountApiResponse<Void>> handleBiz(CapacityBizException ex) {
        return ResponseEntity.badRequest().body(AccountApiResponse.fail(ex.getMessage()));
    }
}
