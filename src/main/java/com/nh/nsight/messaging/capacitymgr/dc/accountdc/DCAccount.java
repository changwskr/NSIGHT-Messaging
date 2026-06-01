package com.nh.nsight.messaging.capacitymgr.dc.accountdc;

import com.nh.nsight.messaging.capacitymgr.dc.accountdc.dto.AccountDDTO;
import com.nh.nsight.messaging.capacitymgr.dc.accountdc.repository.AccountRepository;
import com.nh.nsight.messaging.capacitymgr.zcommonutil.AccountMapperUtil;
import com.nh.nsight.messaging.capacitymgr.zcommonutil.CapacityBizException;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 계정 Domain Component — DTO 변환 및 {@link AccountRepository} 호출.
 */
@Repository
public class DCAccount implements IDCAccount {

    private final AccountRepository accountRepository;

    public DCAccount(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountDDTO getAccount(AccountDDTO criteria) {
        if (criteria == null || criteria.getAccountNumber() == null) {
            return null;
        }
        Account account = accountRepository.findByAccountNumber(criteria.getAccountNumber());
        return AccountMapperUtil.toDDto(account);
    }

    @Override
    public void createAccount(AccountDDTO accountDDTO) {
        if (accountDDTO == null || accountDDTO.getAccountNumber() == null) {
            throw new CapacityBizException("계좌번호는 필수입니다.");
        }
        if (accountRepository.existsByAccountNumber(accountDDTO.getAccountNumber())) {
            throw new CapacityBizException("이미 존재하는 계좌번호입니다: " + accountDDTO.getAccountNumber());
        }
        Date now = new Date();
        if (accountDDTO.getCreatedDate() == null) {
            accountDDTO.setCreatedDate(now);
        }
        accountDDTO.setUpdatedDate(now);
        int rows = accountRepository.insert(AccountMapperUtil.toEntity(accountDDTO));
        if (rows == 0) {
            throw new CapacityBizException("계정 생성에 실패했습니다.");
        }
    }

    @Override
    public void updateAccount(AccountDDTO accountDDTO) {
        if (accountDDTO == null || accountDDTO.getAccountNumber() == null) {
            throw new CapacityBizException("계좌번호는 필수입니다.");
        }
        Account existing = accountRepository.findByAccountNumber(accountDDTO.getAccountNumber());
        if (existing == null) {
            throw new CapacityBizException("계좌를 찾을 수 없습니다: " + accountDDTO.getAccountNumber());
        }
        Account updated = AccountMapperUtil.toEntity(accountDDTO);
        updated.setCreatedDate(existing.getCreatedDate());
        updated.setUpdatedDate(new Date());
        int rows = accountRepository.update(updated);
        if (rows == 0) {
            throw new CapacityBizException("계정 수정에 실패했습니다.");
        }
    }

    @Override
    public void deleteAccount(AccountDDTO accountDDTO) {
        if (accountDDTO == null || accountDDTO.getAccountNumber() == null) {
            throw new CapacityBizException("계좌번호는 필수입니다.");
        }
        int rows = accountRepository.deleteByAccountNumber(accountDDTO.getAccountNumber());
        if (rows == 0) {
            throw new CapacityBizException("계좌를 찾을 수 없습니다: " + accountDDTO.getAccountNumber());
        }
    }

    @Override
    public List<AccountDDTO> getListAccount(AccountDDTO criteria) {
        List<AccountDDTO> result = new ArrayList<>();
        for (Account account : accountRepository.findAll(criteria)) {
            result.add(AccountMapperUtil.toDDto(account));
        }
        return result;
    }
}
