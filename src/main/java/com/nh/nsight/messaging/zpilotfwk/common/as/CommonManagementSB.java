package com.nh.nsight.messaging.zpilotfwk.common.as;

import com.nh.nsight.messaging.zpilotfwk.tcf.support.CommonUtil;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.ICommonManagementSB;

import org.springframework.stereotype.Service;

@Service
public class CommonManagementSB implements ICommonManagementSB {

    @Override
    public String getBaseCurrency(String bankCode) {
        return "KRW";
    }

    @Override
    public String getBusinessDate(String bankCode) {
        return CommonUtil.GetSysDate();
    }
}
