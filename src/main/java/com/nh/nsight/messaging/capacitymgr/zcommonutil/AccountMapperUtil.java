package com.nh.nsight.messaging.capacitymgr.zcommonutil;

import com.nh.nsight.messaging.capacitymgr.dc.accountdc.Account;
import com.nh.nsight.messaging.capacitymgr.dc.accountdc.dto.AccountDDTO;

public final class AccountMapperUtil {

    private AccountMapperUtil() {
    }

    public static Account toEntity(AccountDDTO source) {
        if (source == null) {
            return null;
        }
        Account target = new Account();
        copyFields(source, target);
        return target;
    }

    public static AccountDDTO toDDto(Account source) {
        if (source == null) {
            return null;
        }
        AccountDDTO target = new AccountDDTO();
        copyFields(source, target);
        return target;
    }

    private static void copyFields(Account source, AccountDDTO target) {
        target.setAccountNumber(source.getAccountNumber());
        target.setName(source.getName());
        target.setIdentificationNumber(source.getIdentificationNumber());
        target.setInterestRate(source.getInterestRate());
        target.setLastTransaction(source.getLastTransaction());
        target.setPassword(source.getPassword());
        target.setNetAmount(source.getNetAmount());
        target.setAccountType(source.getAccountType());
        target.setStatus(source.getStatus());
        target.setCurrency(source.getCurrency());
        target.setCreatedDate(source.getCreatedDate());
        target.setUpdatedDate(source.getUpdatedDate());
    }

    private static void copyFields(AccountDDTO source, Account target) {
        target.setAccountNumber(source.getAccountNumber());
        target.setName(source.getName());
        target.setIdentificationNumber(source.getIdentificationNumber());
        target.setInterestRate(source.getInterestRate());
        target.setLastTransaction(source.getLastTransaction());
        target.setPassword(source.getPassword());
        target.setNetAmount(source.getNetAmount());
        target.setAccountType(source.getAccountType());
        target.setStatus(source.getStatus());
        target.setCurrency(source.getCurrency());
        target.setCreatedDate(source.getCreatedDate());
        target.setUpdatedDate(source.getUpdatedDate());
    }
}
