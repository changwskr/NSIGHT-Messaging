package com.nh.nsight.messaging.capacitymgr.as.accountas;

import com.nh.nsight.messaging.capacitymgr.ac.accountac.dto.AccountCDTO;
import com.nh.nsight.messaging.capacitymgr.zcommonutil.CapacityBizException;

import org.springframework.stereotype.Service;

/**
 * 명령 라우팅 Application Service (CREATE / GET / LIST) — 입출력 {@link AccountCDTO}.
 */
@Service
public class ASMBC74001 {

    private final ASMBC71001 asmBc71001;
    private final ASMBC72001 asmBc72001;

    public ASMBC74001(ASMBC71001 asmBc71001, ASMBC72001 asmBc72001) {
        this.asmBc71001 = asmBc71001;
        this.asmBc72001 = asmBc72001;
    }

    public Object execute(String command, AccountCDTO accountCDTO) {
        String normalized = command == null ? "" : command.trim().toUpperCase();
        return switch (normalized) {
            case "CREATE" -> asmBc71001.create(accountCDTO);
            case "GET" -> {
                if (accountCDTO == null || accountCDTO.getAccountNumber() == null) {
                    throw new CapacityBizException("GET command에는 accountNumber가 필요합니다.");
                }
                yield asmBc72001.get(accountCDTO.getAccountNumber());
            }
            case "LIST" -> asmBc72001.list(accountCDTO);
            default -> throw new CapacityBizException("지원하지 않는 command: " + command);
        };
    }
}
