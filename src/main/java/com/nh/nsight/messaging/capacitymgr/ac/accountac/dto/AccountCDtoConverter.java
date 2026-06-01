package com.nh.nsight.messaging.capacitymgr.ac.accountac.dto;

import com.nh.nsight.messaging.capacitymgr.dc.accountdc.dto.AccountDDTO;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * CDTO(AC·AS) ↔ DDTO(DC) 변환.
 */
public final class AccountCDtoConverter {

    private AccountCDtoConverter() {
    }

    public static AccountDDTO toDDto(AccountCDTO source) {
        if (source == null) {
            return null;
        }
        AccountDDTO target = new AccountDDTO();
        target.setAccountNumber(source.getAccountNumber());
        target.setName(source.getName());
        target.setAccountType(source.getAccountType());
        target.setStatus(source.getStatus());
        target.setCurrency(source.getCurrency());
        target.setIdentificationNumber(source.getIdentificationNumber());
        target.setPassword(source.getPassword());
        target.setNetAmount(parseDouble(source.getNetAmount()));
        target.setInterestRate(parseDouble(source.getInterestRate()));
        target.setLastTransaction(parseDate(source.getLastTransaction()));
        target.setCreatedDate(parseDate(source.getCreatedDate()));
        target.setUpdatedDate(parseDate(source.getUpdatedDate()));
        return target;
    }

    public static AccountCDTO toCDto(AccountDDTO source) {
        if (source == null) {
            return null;
        }
        AccountCDTO target = new AccountCDTO();
        target.setAccountNumber(source.getAccountNumber());
        target.setName(source.getName());
        target.setAccountType(source.getAccountType());
        target.setStatus(source.getStatus());
        target.setCurrency(source.getCurrency());
        target.setIdentificationNumber(source.getIdentificationNumber());
        target.setPassword(source.getPassword());
        target.setNetAmount(formatDouble(source.getNetAmount()));
        target.setInterestRate(formatDouble(source.getInterestRate()));
        target.setLastTransaction(formatDate(source.getLastTransaction()));
        target.setCreatedDate(formatDate(source.getCreatedDate()));
        target.setUpdatedDate(formatDate(source.getUpdatedDate()));
        return target;
    }

    public static List<AccountCDTO> toCDtoList(List<AccountDDTO> sources) {
        List<AccountCDTO> result = new ArrayList<>();
        if (sources == null) {
            return result;
        }
        for (AccountDDTO source : sources) {
            result.add(toCDto(source));
        }
        return result;
    }

    private static Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Double.valueOf(value.trim());
    }

    private static String formatDouble(Double value) {
        return value == null ? null : String.valueOf(value);
    }

    private static Date parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Date.from(Instant.parse(value.trim()));
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private static String formatDate(Date value) {
        return value == null ? null : value.toInstant().toString();
    }
}
