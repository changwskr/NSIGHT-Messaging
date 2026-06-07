package com.nh.nsight.messaging.zpilotfwk.mn;

import com.nh.nsight.messaging.NsightMessageMgmtApplication;
import com.nh.nsight.messaging.zpilotfwk.common.dc.dto.SpCommon7001BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.config.ZpilotFwkProperties;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonCommonDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonErrDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonEvent;
import com.nh.nsight.messaging.zpilotfwk.tcf.LOGEJ;
import com.nh.nsight.messaging.zpilotfwk.tcf.TCF;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.CommonUtil;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.DefaultCommonManagementSB;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.ICommonManagementSB;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.SessionContext;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.TPMSVCAPI;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.TransactionControlDAO;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.ZpilotFwkContext;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * MN(Management) 공통 모듈 — 로컬 {@code main()} 테스트용.
 */
public class MN_SP_COMMON {

    private static final String MN = "MN_SP_COMMON";

    private final TCF tcf;
    private final ZpilotFwkProperties properties;
    private EPlatonEvent eplevent;
    private SessionContext ctx;

    public MN_SP_COMMON(TCF tcf, ZpilotFwkProperties properties) {
        this.tcf = tcf;
        this.properties = properties;
    }

    public EPlatonEvent execute(EPlatonEvent peplatonEvent, SessionContext sessionContext) {
        return execute(peplatonEvent, sessionContext, properties.getTransaction().getDefaultMode());
    }

    public EPlatonEvent execute(EPlatonEvent peplatonEvent, SessionContext sessionContext, String transactionMode) {
        try {
            eplevent = peplatonEvent;
            ctx = sessionContext;

            LOGEJ.getInstance().printf(5, peplatonEvent,
                    "====================================================================[MN] start");

            if (!MN_SPprepare(peplatonEvent)) {
                MN_SPerror(peplatonEvent, "EMN0001", "MN_SPprepare() failed");
                LOGEJ.getInstance().printf(5, peplatonEvent,
                        "====================================================================[MN] end");
                return peplatonEvent;
            }

            LOGEJ.getInstance().printf(5, peplatonEvent,
                    "====================================================[MN->TCF] execute call mode="
                            + transactionMode);

            eplevent = tcf.execute(peplatonEvent, sessionContext, transactionMode);

            LOGEJ.getInstance().printf(5, eplevent,
                    "====================================================[MN->TCF] execute return errorcode="
                            + eplevent.getTPSVCINFODTO().getErrorcode());
        } catch (Exception ex) {
            MN_SPerror(peplatonEvent, "EMN0002", getClass().getName() + ".execute():" + ex);
            LOGEJ.getInstance().printf(10, peplatonEvent, "MN_execute() exception");
            LOGEJ.getInstance().eprintf(10, peplatonEvent, ex);
        }

        LOGEJ.getInstance().printf(5, eplevent,
                "====================================================================[MN] end");
        return eplevent;
    }

    public EPlatonEvent executeSample(EPlatonEvent peplatonEvent, SessionContext sessionContext,
            String transactionMode) {
        return execute(peplatonEvent, sessionContext, transactionMode);
    }

    private boolean MN_SPprepare(EPlatonEvent event) {
        try {
            if (event == null) {
                return false;
            }
            if (event.getCommon() == null) {
                event.setCommon(new EPlatonCommonDTO());
            }
            if (event.getTPSVCINFODTO() == null) {
                return false;
            }

            EPlatonCommonDTO common = event.getCommon();
            if (common.getSystemInTime() == null || "*".equals(common.getSystemInTime())) {
                common.setSystemInTime(CommonUtil.GetSysTime());
            }
            if (common.getSystemDate() == null || "*".equals(common.getSystemDate())) {
                common.setSystemDate(CommonUtil.GetSysDate());
            }

            String errorCode = event.getTPSVCINFODTO().getErrorcode();
            if (errorCode == null || errorCode.isEmpty()) {
                event.getTPSVCINFODTO().setErrorcode("IZZ000");
            }

            LOGEJ.getInstance().printf(4, event, "MN_SPprepare success eventNo=" + common.getEventNo());
            return true;
        } catch (Exception ex) {
            LOGEJ.getInstance().eprintf(10, event, ex);
            return false;
        }
    }

    private void MN_SPerror(EPlatonEvent event, String errorCode, String message) {
        if (event == null || event.getTPSVCINFODTO() == null) {
            return;
        }
        char first = event.getTPSVCINFODTO().getErrorcode().charAt(0);
        if (first == 'I') {
            event.getTPSVCINFODTO().setErrorcode(errorCode);
            event.getTPSVCINFODTO().setError_message(message);
        } else if (first == 'E') {
            event.getTPSVCINFODTO().setErrorcode(errorCode + "|" + event.getTPSVCINFODTO().getErrorcode());
            event.getTPSVCINFODTO().setError_message(message);
        }
    }

    public EPlatonEvent getEPlatonEvent() {
        return eplevent;
    }

    public static void main(String[] args) {
        String transactionMode = args.length > 0 ? args[0] : "container";
        System.out.println("★★★★★ [" + MN + "] main START mode=" + transactionMode);

        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(NsightMessageMgmtApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);

        try {
            ZpilotFwkProperties properties = ctx.getBean(ZpilotFwkProperties.class);
            bootstrapLocalEnvironment(properties);

            MN_SP_COMMON mn = new MN_SP_COMMON(ctx.getBean(TCF.class), properties);
            SessionContext sessionContext = new SessionContext();
            EPlatonEvent event = createSampleEvent();

            EPlatonEvent result = mn.execute(event, sessionContext, transactionMode);
            printResult(result);
        } catch (Exception ex) {
            System.err.println("★★★★★ [" + MN + "] main FAIL: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            ctx.close();
        }

        System.out.println("★★★★★ [" + MN + "] main END");
    }

    private static void bootstrapLocalEnvironment(ZpilotFwkProperties properties) {
        ZpilotFwkContext.registerLocalBean(ICommonManagementSB.class, new DefaultCommonManagementSB());
        ZpilotFwkContext.registerLocalBean(TransactionControlDAO.class, new TransactionControlDAO());
        LOGEJ.bindInstance(LOGEJ.forLocalMain());
        TPMSVCAPI.createLocal(properties.getTransaction().getDefaultMode());
    }

    private static EPlatonEvent createSampleEvent() {
        EPlatonEvent event = new EPlatonEvent();
        EPlatonCommonDTO common = event.getCommon();
        EPlatonCommonDTO.copyTo(common, EPlatonCommonDTO.sample());
        common.setSystemDate(CommonUtil.GetSysDate());
        common.setSystemInTime(CommonUtil.GetSysTime());
        common.setBusinessDate(CommonUtil.GetSysDate());

        event.setErr(EPlatonErrDTO.of("IZZ000", ""));
        event.getTPSVCINFODTO().setSystem_name("LOCAL");
        event.getTPSVCINFODTO().setOperation_name("MN_SP_COMMON.main");
        event.getTPSVCINFODTO().setTpfq("200");
        event.getTPSVCINFODTO().setTx_timer("60");
        event.getTPSVCINFODTO().setHostseq("HOST-0001");
        event.getTPSVCINFODTO().setOrgseq("ORG-0001");
        event.setRequest(SpCommon7001BIZDDTO.sample());
        return event;
    }

    private static void printResult(EPlatonEvent result) {
        if (result == null) {
            System.out.println("★★★★★ [" + MN + "] result=null");
            return;
        }
        System.out.println("★★★★★ [" + MN + "] result errorcode="
                + result.getTPSVCINFODTO().getErrorcode());
        System.out.println("★★★★★ [" + MN + "] result message="
                + result.getTPSVCINFODTO().getError_message());
        System.out.println("★★★★★ [" + MN + "] result eventNo="
                + result.getCommon().getEventNo());
        System.out.println("★★★★★ [" + MN + "] result transactionNo="
                + result.getCommon().getTransactionNo());
        System.out.println("★★★★★ [" + MN + "] result systemOutTime="
                + result.getCommon().getSystemOutTime());
    }
}
