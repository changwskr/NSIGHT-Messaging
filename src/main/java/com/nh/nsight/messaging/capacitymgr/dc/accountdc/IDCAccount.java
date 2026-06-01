package com.nh.nsight.messaging.capacitymgr.dc.accountdc;

import com.nh.nsight.messaging.capacitymgr.dc.accountdc.dto.AccountDDTO;

import java.util.List;

public interface IDCAccount {

    AccountDDTO getAccount(AccountDDTO criteria);

    void createAccount(AccountDDTO account);

    void updateAccount(AccountDDTO account);

    void deleteAccount(AccountDDTO account);

    List<AccountDDTO> getListAccount(AccountDDTO criteria);
}
