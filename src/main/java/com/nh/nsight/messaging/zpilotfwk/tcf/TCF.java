package com.nh.nsight.messaging.zpilotfwk.tcf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.nh.nsight.messaging.zpilotfwk.common.as.SP_COMMON;
import com.nh.nsight.messaging.zpilotfwk.config.ZpilotFwkProperties;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.CommonUtil;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.SessionContext;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.TxHelper;

/**
 * TCF(Transaction Control Framework) - STF - BTF - ETF ???????.
 */
@Service
public class TCF {

  private final ISpService businessService;
  private final ZpilotFwkProperties properties;
  private final TCF self;

  private String activeTransactionMode = "container";

  @Autowired
  public TCF(SP_COMMON spCommon, ZpilotFwkProperties properties, @Lazy TCF self) {
    this.businessService = spCommon;
    this.properties = properties;
    this.self = self;
  }

  public TCF(ISpService businessService) {
    this.businessService = businessService;
    this.properties = null;
    this.self = null;
  }

  public String getBusinessServiceName() {
    return businessService.getClass().getSimpleName();
  }

  public EPlatonEvent execute(EPlatonEvent event, SessionContext sessionContext) {
    String defaultMode = properties != null
        ? properties.getTransaction().getDefaultMode()
        : "container";
    return execute(event, sessionContext, defaultMode);
  }

  public EPlatonEvent execute(EPlatonEvent event, SessionContext sessionContext, String transactionMode) {
    String mode = resolveTransactionMode(transactionMode);
    activeTransactionMode = mode;
    try {
      if (self == null) {
        return runOrchestration(event, sessionContext, mode);
      }

      logTransactionStatus("before", mode);
      EPlatonEvent result = "usertransaction".equals(mode)
          ? self.executeInNewTransaction(event, sessionContext)
          : self.executeInTransaction(event, sessionContext);
      logTransactionStatus("after", mode);
      return result;
    } finally {
      activeTransactionMode = "container";
    }
  }

  public EPlatonEvent execute_sample(EPlatonEvent event, SessionContext sessionContext, String transactionMode) {
    return execute(event, sessionContext, transactionMode);
  }

  @Transactional(rollbackFor = Exception.class)
  public EPlatonEvent executeInTransaction(EPlatonEvent event, SessionContext sessionContext) {
    EPlatonEvent result = runOrchestration(event, sessionContext, activeTransactionMode);
    applyRollbackByErrorCode(result);
    return result;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public EPlatonEvent executeInNewTransaction(EPlatonEvent event, SessionContext sessionContext) {
    EPlatonEvent result = runOrchestration(event, sessionContext, activeTransactionMode);
    applyRollbackByErrorCode(result);
    return result;
  }

  private EPlatonEvent runOrchestration(EPlatonEvent event, SessionContext sessionContext, String transactionMode) {
    String serviceName = businessService.getClass().getSimpleName();

    LOGEJ.getInstance().printf(5, event,
        "====================================================================[TCF] start");

    markPhase(event, "STF", true);
    LOGEJ.getInstance().printf(5, event, "====================================================[STF] start");
    event = STF.create(transactionMode, sessionContext).execute(event);
    markPhase(event, "STF", false);
    LOGEJ.getInstance().printf(5, event, "====================================================[STF] end");

    if (isSuccess(event)) {
      LOGEJ.getInstance().printf(5, event,
          "====================================================[" + serviceName + "] start");
      markPhase(event, serviceName, true);
      event = businessService.execute(event);
      markPhase(event, serviceName, false);
      LOGEJ.getInstance().printf(5, event,
          "====================================================[" + serviceName + "] end errorcode="
              + event.getTPSVCINFODTO().getErrorcode());
    }

    markPhase(event, "ETF", true);
    LOGEJ.getInstance().printf(5, event, "====================================================[ETF] start");
    if (event.getCommon() != null && event.getCommon().getSystemOutTime() == null) {
      event.getCommon().setSystemOutTime(CommonUtil.GetSysTime());
    }
    markPhase(event, "ETF", false);
    LOGEJ.getInstance().printf(5, event, "====================================================[ETF] end");

    logTrace(event, serviceName);
    LOGEJ.getInstance().printf(5, event,
        "====================================================================[TCF] end");
    return event;
  }

  private String resolveTransactionMode(String transactionMode) {
    if (transactionMode != null && !transactionMode.isBlank()) {
      return transactionMode.trim();
    }
    if (properties != null) {
      return properties.getTransaction().getDefaultMode();
    }
    return "container";
  }

  private void applyRollbackByErrorCode(EPlatonEvent event) {
    if (!isSuccess(event)) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
  }

  private boolean isSuccess(EPlatonEvent event) {
    if (event == null || event.getTPSVCINFODTO() == null) {
      return false;
    }
    String errorcode = event.getTPSVCINFODTO().getErrorcode();
    if (errorcode == null || errorcode.isEmpty()) {
      return false;
    }
    return errorcode.charAt(0) == 'I';
  }

  private void logTransactionStatus(String phase, String mode) {
    boolean active = TransactionSynchronizationManager.isActualTransactionActive();
    int legacyStatus = active ? 0 : 6;
    System.out.println("***** [TCF] tx " + phase + " mode=" + mode
        + " springActive=" + active
        + " tpinfo=" + TxHelper.status2String(legacyStatus));
  }

  private void markPhase(EPlatonEvent event, String phase, boolean before) {
    if (event == null || event.getTPSVCINFODTO() == null) {
      return;
    }
    String time = CommonUtil.GetSysTime();
    if ("STF".equals(phase)) {
      if (before) {
        event.getTPSVCINFODTO().setSTF_intime(time);
        event.getTPSVCINFODTO().setLogic_level("BSTF");
      } else {
        event.getTPSVCINFODTO().setSTF_outtime(time);
        event.getTPSVCINFODTO().setLogic_level("ASTF");
      }
      return;
    }
    if ("ETF".equals(phase)) {
      if (before) {
        event.getTPSVCINFODTO().setETF_intime(time);
        event.getTPSVCINFODTO().setLogic_level("BETF");
      } else {
        event.getTPSVCINFODTO().setETF_outtime(time);
        event.getTPSVCINFODTO().setLogic_level("AETF");
      }
      return;
    }
    if (before) {
      event.getTPSVCINFODTO().setBTF_intime(time);
      event.getTPSVCINFODTO().setLogic_level("BBTF");
    } else {
      event.getTPSVCINFODTO().setBTF_outtime(time);
      event.getTPSVCINFODTO().setLogic_level("ABTF");
    }
  }

  private void logTrace(EPlatonEvent event, String serviceName) {
    if (event == null || event.getCommon() == null || event.getTPSVCINFODTO() == null) {
      return;
    }
    String line = event.getCommon().getBranchCode() + ","
        + event.getCommon().getEventNo() + ","
        + event.getCommon().getSystemInTime() + ","
        + event.getCommon().getSystemOutTime() + ","
        + event.getTPSVCINFODTO().getErrorcode() + ","
        + event.getTPSVCINFODTO().getLogic_level() + ","
        + event.getTPSVCINFODTO().getTpfq() + ","
        + serviceName;
    LOGEJ.getInstance().tcf_txprintf(event, line);
  }
}
