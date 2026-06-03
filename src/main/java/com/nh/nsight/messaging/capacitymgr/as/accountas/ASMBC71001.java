package com.nh.nsight.messaging.capacitymgr.as.accountas;

import com.nh.nsight.messaging.capacitymgr.ac.accountac.dto.AccountCDTO;
import com.nh.nsight.messaging.capacitymgr.ac.accountac.dto.AccountCDtoConverter;
import com.nh.nsight.messaging.capacitymgr.dc.accountdc.DCAccount;
import com.nh.nsight.messaging.capacitymgr.dc.accountdc.dto.AccountDDTO;
import com.nh.nsight.messaging.capacitymgr.util.CapacityBizException;

import org.springframework.stereotype.Service;

/**
 * ?? ?? Application Service — AC·AS ??? {@link AccountCDTO}.
 */
@Service
public class ASMBC71001 {

    private final DCAccount dcAccount;

    public ASMBC71001(DCAccount dcAccount) {
        this.dcAccount = dcAccount;
    }

    public AccountCDTO create(AccountCDTO accountCDTO) {
        validateAccountNumber(accountCDTO);
        AccountDDTO ddto = AccountCDtoConverter.toDDto(accountCDTO);
        dcAccount.createAccount(ddto);
        AccountDDTO criteria = new AccountDDTO();
        criteria.setAccountNumber(ddto.getAccountNumber());
        return AccountCDtoConverter.toCDto(dcAccount.getAccount(criteria));
    }

    private void validateAccountNumber(AccountCDTO accountCDTO) {
        if (accountCDTO == null || accountCDTO.getAccountNumber() == null
                || accountCDTO.getAccountNumber().isBlank()) {
            throw new CapacityBizException("????? ?????.");
        }
    }
}
