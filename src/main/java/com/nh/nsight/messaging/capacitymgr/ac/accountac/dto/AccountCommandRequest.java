package com.nh.nsight.messaging.capacitymgr.ac.accountac.dto;

/**
 * 명령 라우팅 요청 (CREATE / GET / LIST).
 */
public class AccountCommandRequest {

    private String command;
    private AccountCDTO account;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public AccountCDTO getAccount() {
        return account;
    }

    public void setAccount(AccountCDTO account) {
        this.account = account;
    }
}
