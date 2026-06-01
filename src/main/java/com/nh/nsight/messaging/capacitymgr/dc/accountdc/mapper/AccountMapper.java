package com.nh.nsight.messaging.capacitymgr.dc.accountdc.mapper;

import com.nh.nsight.messaging.capacitymgr.dc.accountdc.Account;
import com.nh.nsight.messaging.capacitymgr.dc.accountdc.dto.AccountDDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 계정 MyBatis Mapper — {@link Account} 엔티티 CRUD.
 */
@Mapper
public interface AccountMapper {

    Account selectByAccountNumber(@Param("accountNumber") String accountNumber);

    List<Account> selectAll(@Param("criteria") AccountDDTO criteria);

    int countByAccountNumber(@Param("accountNumber") String accountNumber);

    int insert(Account account);

    int update(Account account);

    int deleteByAccountNumber(@Param("accountNumber") String accountNumber);

    int updateBalance(@Param("accountNumber") String accountNumber, @Param("netAmount") double netAmount);

    int updateLastTransaction(
            @Param("accountNumber") String accountNumber,
            @Param("lastTransaction") Date lastTransaction
    );
}
