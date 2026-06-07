package com.nh.nsight.messaging.zpilotfwk.tcf.support;

import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonEvent;

public class TransactionControlDAO {

    public boolean DB_INSERTinlog(EPlatonEvent event) {
        return true;
    }

    public boolean DB_INSERToutlog(EPlatonEvent event) {
        return true;
    }

    public String queryForBusinessDate(String bankCode) {
        return CommonUtil.GetSysDate();
    }

    public String GetBizDate() {
        return CommonUtil.GetSysDate();
    }
}
