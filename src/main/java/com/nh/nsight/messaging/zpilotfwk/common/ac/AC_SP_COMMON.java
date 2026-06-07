package com.nh.nsight.messaging.zpilotfwk.common.ac;

import com.nh.nsight.messaging.zpilotfwk.config.ZpilotFwkProperties;
import com.nh.nsight.messaging.zpilotfwk.common.ac.dto.SpCommonApiResponse;
import com.nh.nsight.messaging.zpilotfwk.common.dc.dto.SpCommon7001BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.common.ac.dto.SpCommon7001REQCDTO;
import com.nh.nsight.messaging.zpilotfwk.common.ac.dto.SpCommon7001RESCDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.CommonUtil;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.SessionContext;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonCommonDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonErrDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonEvent;
import com.nh.nsight.messaging.zpilotfwk.tcf.LOGEJ;
import com.nh.nsight.messaging.zpilotfwk.tcf.TCF;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AC(Application Controller) ? SP_COMMON ?? ???.
 */
@RestController
@RequestMapping("/api/zpilotfwk/sp-common")
public class AC_SP_COMMON {

    private static final String AC = "AC_SP_COMMON";

    private final TCF tcf;
    private final ZpilotFwkProperties properties;

    public AC_SP_COMMON(TCF tcf, ZpilotFwkProperties properties) {
        this.tcf = tcf;
        this.properties = properties;
    }

    @PostMapping("/execute")
    public ResponseEntity<SpCommonApiResponse<SpCommon7001RESCDTO>> execute(
            @RequestBody(required = false) SpCommon7001REQCDTO request,
            @RequestParam(required = false) String transactionMode) {
        String mode = resolveTransactionMode(transactionMode);
        System.out.println("????? [" + AC + "] execute START mode=" + mode);

        EPlatonEvent event = null;
        try {
            event = toEvent(request);
            SessionContext sessionContext = new SessionContext();

            LOGEJ.getInstance().printf(5, event,
                    "====================================================================[AC] start");

            if (!prepare(event)) {
                acError(event, "EAC0002", "prepare() failed");
                SpCommon7001RESCDTO data = toResult(event);
                LOGEJ.getInstance().printf(5, event,
                        "====================================================================[AC] end");
                return ResponseEntity.ok(SpCommonApiResponse.ok(data));
            }

            LOGEJ.getInstance().printf(5, event,
                    "====================================================[AC->TCF] execute call mode=" + mode);

            EPlatonEvent result = tcf.execute(event, sessionContext, mode);

            LOGEJ.getInstance().printf(5, result,
                    "====================================================[AC->TCF] execute return errorcode="
                            + result.getTPSVCINFODTO().getErrorcode());

            SpCommon7001RESCDTO data = toResult(result);

            System.out.println("????? [" + AC + "] execute END errorcode="
                    + (data.getErr() != null ? data.getErr().getErrcode() : "-"));
            LOGEJ.getInstance().printf(5, result,
                    "====================================================================[AC] end");
            return ResponseEntity.ok(SpCommonApiResponse.ok(data));
        } catch (Exception ex) {
            if (event != null) {
                acError(event, "EAC0003", getClass().getName() + ".execute():" + ex);
                LOGEJ.getInstance().eprintf(10, event, ex);
                return ResponseEntity.internalServerError()
                        .body(SpCommonApiResponse.ok(toResult(event)));
            }
            System.out.println("????? [" + AC + "] execute FAIL " + ex.getMessage());
            return ResponseEntity.internalServerError()
                    .body(SpCommonApiResponse.fail(ex.getMessage()));
        }
    }

    private String resolveTransactionMode(String transactionMode) {
        if (transactionMode != null && !transactionMode.isBlank()) {
            return transactionMode.trim();
        }
        return properties.getTransaction().getDefaultMode();
    }

    private boolean prepare(EPlatonEvent event) {
        try {
            if (event == null || event.getTPSVCINFODTO() == null) {
                return false;
            }
            if (event.getCommon() == null) {
                event.setCommon(new EPlatonCommonDTO());
            }

            EPlatonCommonDTO common = event.getCommon();
            if (common.getSystemInTime() == null || "*".equals(common.getSystemInTime())) {
                common.setSystemInTime(CommonUtil.GetSysTime());
            }
            if (common.getSystemDate() == null || "*".equals(common.getSystemDate())) {
                common.setSystemDate(CommonUtil.GetSysDate());
            }

            String errorCode = event.getErr().getErrcode();
            if (errorCode == null || errorCode.isEmpty()) {
                event.setErr(EPlatonErrDTO.of("IZZ000", ""));
            }

            LOGEJ.getInstance().printf(4, event, "AC prepare success eventNo=" + common.getEventNo());
            return true;
        } catch (Exception ex) {
            LOGEJ.getInstance().eprintf(10, event, ex);
            return false;
        }
    }

    private void acError(EPlatonEvent event, String errorCode, String message) {
        if (event == null || event.getTPSVCINFODTO() == null) {
            return;
        }
        EPlatonErrDTO err = event.getErr();
        char first = err.getErrcode().charAt(0);
        if (first == 'I') {
            err.setErrcode(errorCode);
            err.setErrorMessage(message);
        } else if (first == 'E') {
            err.setErrcode(errorCode + "|" + err.getErrcode());
            err.setErrorMessage(message);
        }
        event.setErr(err);
    }

    private EPlatonEvent toEvent(SpCommon7001REQCDTO request) {
        EPlatonEvent event = new EPlatonEvent();
        EPlatonCommonDTO common = event.getCommon();

        if (request != null && request.getCommon() != null) {
            EPlatonCommonDTO.copyTo(common, request.getCommon());
        } else {
            EPlatonCommonDTO.copyTo(common, EPlatonCommonDTO.sample());
        }

        if (isBlankOrStar(common.getSystemDate())) {
            common.setSystemDate(CommonUtil.GetSysDate());
        }
        if (isBlankOrStar(common.getBusinessDate())) {
            common.setBusinessDate(CommonUtil.GetSysDate());
        }
        if (isBlankOrStar(common.getSystemInTime())) {
            common.setSystemInTime(CommonUtil.GetSysTime());
        }

        event.setErr(EPlatonErrDTO.of("IZZ000", ""));
        syncTpmsvcFromCommon(event, common);

        SpCommon7001BIZDDTO requestDto = request != null && request.getBizData() != null
                ? request.getBizData()
                : SpCommon7001BIZDDTO.sample();
        event.setRequest(requestDto);

        return event;
    }

    private void syncTpmsvcFromCommon(EPlatonEvent event, EPlatonCommonDTO common) {
        event.getTPSVCINFODTO().setSystem_name(valueOrDefault(common.getSystemName(), "LOCAL"));
        event.getTPSVCINFODTO().setOperation_name(valueOrDefault(common.getOperationName(), AC + ".execute"));
        event.getTPSVCINFODTO().setTpfq(valueOrDefault(common.getTpfq(), "200"));
        event.getTPSVCINFODTO().setTx_timer(valueOrDefault(common.getTxTimer(), "60"));
        event.getTPSVCINFODTO().setHostseq(valueOrDefault(common.getHostseq(), "HOST-0001"));
        event.getTPSVCINFODTO().setOrgseq(valueOrDefault(common.getOrgseq(), "ORG-0001"));
    }

    private SpCommon7001RESCDTO toResult(EPlatonEvent result) {
        SpCommon7001RESCDTO data = new SpCommon7001RESCDTO();
        if (result == null) {
            data.setErr(EPlatonErrDTO.of("EAC0001", "result is null"));
            return data;
        }
        EPlatonErrDTO err = result.getErr();
        data.setErr(EPlatonErrDTO.of(err.getErrcode(), err.getErrorMessage()));
        if (result.getCommon() != null) {
            EPlatonCommonDTO commonEcho = new EPlatonCommonDTO();
            EPlatonCommonDTO.copyTo(commonEcho, result.getCommon());
            data.setCommon(commonEcho);
        }

        SpCommon7001BIZDDTO responseDto = result.getResponseAs(SpCommon7001BIZDDTO.class);
        if (responseDto != null) {
            data.setBizData(responseDto);
        } else {
            data.setBizData(result.getRequestAs(SpCommon7001BIZDDTO.class));
        }
        return data;
    }

    private static boolean isBlankOrStar(String value) {
        return value == null || value.isBlank() || "*".equals(value.trim());
    }

    private static String valueOrDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }
}
