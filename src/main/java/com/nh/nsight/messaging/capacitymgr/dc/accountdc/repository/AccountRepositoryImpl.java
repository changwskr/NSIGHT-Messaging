package com.nh.nsight.messaging.capacitymgr.dc.accountdc.repository;

import com.nh.nsight.messaging.capacitymgr.dc.accountdc.Account;
import com.nh.nsight.messaging.capacitymgr.dc.accountdc.dto.AccountDDTO;
import com.nh.nsight.messaging.capacitymgr.dc.accountdc.mapper.AccountMapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * {@link AccountRepository} 구현 — {@link AccountMapper}(MyBatis) 위임.
 */
@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountMapper accountMapper;

    public AccountRepositoryImpl(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @Override
    public Account findByAccountNumber(String accountNumber) {
        if (accountNumber == null) {
            return null;
        }
        return accountMapper.selectByAccountNumber(accountNumber);
    }

    @Override
    public List<Account> findAll(AccountDDTO criteria) {
        return accountMapper.selectAll(criteria);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return accountNumber != null && accountMapper.countByAccountNumber(accountNumber) > 0;
    }

    @Override
    public int insert(Account account) {
        if (account == null || account.getAccountNumber() == null) {
            return 0;
        }
        return accountMapper.insert(account);
    }

    @Override
    public int update(Account account) {
        if (account == null || account.getAccountNumber() == null) {
            return 0;
        }
        return accountMapper.update(account);
    }

    @Override
    public int deleteByAccountNumber(String accountNumber) {
        if (accountNumber == null) {
            return 0;
        }
        return accountMapper.deleteByAccountNumber(accountNumber);
    }

    @Override
    public int updateBalance(String accountNumber, double netAmount) {
        if (accountNumber == null) {
            return 0;
        }
        return accountMapper.updateBalance(accountNumber, netAmount);
    }

    @Override
    public int updateLastTransaction(String accountNumber, Date lastTransaction) {
        if (accountNumber == null) {
            return 0;
        }
        return accountMapper.updateLastTransaction(accountNumber, lastTransaction);
    }
}
