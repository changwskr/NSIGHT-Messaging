package com.nh.nsight.messaging.zpilotfwk.tcf.support;

public class DefaultCommonManagementSB implements ICommonManagementSB {

    @Override
    public String getBaseCurrency(String bankCode) {
        return "KRW";
    }

    @Override
    public String getBusinessDate(String bankCode) {
        return CommonUtil.GetSysDate();
    }
}
