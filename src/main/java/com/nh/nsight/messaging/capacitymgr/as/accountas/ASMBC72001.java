package com.nh.nsight.messaging.capacitymgr.as.accountas;

import com.nh.nsight.messaging.capacitymgr.ac.accountac.dto.AccountCDTO;
import com.nh.nsight.messaging.capacitymgr.ac.accountac.dto.AccountCDtoConverter;
import com.nh.nsight.messaging.capacitymgr.dc.accountdc.DCAccount;
import com.nh.nsight.messaging.capacitymgr.dc.accountdc.dto.AccountDDTO;
import com.nh.nsight.messaging.capacitymgr.zcommonutil.CapacityBizException;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 계정 조회 Application Service — AC·AS 경계는 {@link AccountCDTO}.
 */
@Service
public class ASMBC72001 {

    private final DCAccount dcAccount;

    public ASMBC72001(DCAccount dcAccount) {
        this.dcAccount = dcAccount;
    }

    public AccountCDTO get(String accountNumber) {
        AccountDDTO criteria = new AccountDDTO();
        criteria.setAccountNumber(accountNumber);
        AccountDDTO found = dcAccount.getAccount(criteria);
        if (found == null) {
            throw new CapacityBizException("계좌를 찾을 수 없습니다: " + accountNumber);
        }
        return AccountCDtoConverter.toCDto(found);
    }

    public List<AccountCDTO> list() {
        return AccountCDtoConverter.toCDtoList(dcAccount.getListAccount(null));
    }

    public List<AccountCDTO> list(AccountCDTO criteria) {
        return AccountCDtoConverter.toCDtoList(
                dcAccount.getListAccount(AccountCDtoConverter.toDDto(criteria)));
    }
}
