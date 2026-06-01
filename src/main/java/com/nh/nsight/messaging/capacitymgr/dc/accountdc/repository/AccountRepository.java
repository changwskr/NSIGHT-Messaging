package com.nh.nsight.messaging.capacitymgr.dc.accountdc.repository;

import com.nh.nsight.messaging.capacitymgr.dc.accountdc.Account;
import com.nh.nsight.messaging.capacitymgr.dc.accountdc.dto.AccountDDTO;

import java.util.Date;
import java.util.List;

/**
 * 계정 DC 저장소 인터페이스 — {@link Account} 엔티티 기준 CRUD.
 */
public interface AccountRepository {

    Account findByAccountNumber(String accountNumber);

    List<Account> findAll(AccountDDTO criteria);

    boolean existsByAccountNumber(String accountNumber);

    int insert(Account account);

    int update(Account account);

    int deleteByAccountNumber(String accountNumber);

    int updateBalance(String accountNumber, double netAmount);

    int updateLastTransaction(String accountNumber, Date lastTransaction);
}
