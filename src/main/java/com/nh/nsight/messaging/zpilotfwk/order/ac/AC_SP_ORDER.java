package com.nh.nsight.messaging.zpilotfwk.order.ac;

import com.nh.nsight.messaging.zpilotfwk.config.ZpilotFwkProperties;
import com.nh.nsight.messaging.zpilotfwk.order.ac.dto.SpOrder7101REQCDTO;
import com.nh.nsight.messaging.zpilotfwk.order.ac.dto.SpOrder7101RESCDTO;
import com.nh.nsight.messaging.zpilotfwk.order.ac.dto.SpOrderApiResponse;
import com.nh.nsight.messaging.zpilotfwk.order.dc.dto.SpOrder7101BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonCommonDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonErrDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonEvent;
import com.nh.nsight.messaging.zpilotfwk.tcf.LOGEJ;
import com.nh.nsight.messaging.zpilotfwk.tcf.TCF;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.CommonUtil;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.SessionContext;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/zpilotfwk/sp-order")
public class AC_SP_ORDER {

    private static final String AC = "AC_SP_ORDER";
    private static final String DEFAULT_EVENT_NO = "SP_ORDER7101";

    private final TCF tcf;
    private final ZpilotFwkProperties properties;

    public AC_SP_ORDER(TCF tcf, ZpilotFwkProperties properties) {
        this.tcf = tcf;
        this.properties = properties;
    }

    @PostMapping("/execute")
    public ResponseEntity<SpOrderApiResponse<SpOrder7101RESCDTO>> execute(
            @RequestBody(required = false) SpOrder7101REQCDTO request,
            @RequestParam(required = false) String transactionMode) {
        String mode = resolveTransactionMode(transactionMode);
        System.out.println("***** [" + AC + "] execute START mode=" + mode);

        try {
            EPlatonEvent result = executeInternal(request, mode);
            SpOrder7101RESCDTO data = toResult(result);

            LOGEJ.getInstance().printf(5, result,
                    "====================================================================[AC] end");
            return ResponseEntity.ok(SpOrderApiResponse.ok(data));
        } catch (Exception ex) {
            EPlatonEvent event = toEvent(request);
            acError(event, "EAC0003", getClass().getName() + ".execute():" + ex);
            return ResponseEntity.internalServerError()
                    .body(SpOrderApiResponse.ok(toResult(event)));
        }
    }

    /**
     * REST 외부(AS 연계 등)에서 AC 파이프라인(prepare → TCF)을 그대로 호출한다.
     */
    public EPlatonEvent executeInternal(SpOrder7101REQCDTO request, String transactionMode) {
        String mode = resolveTransactionMode(transactionMode);
        EPlatonEvent event = toEvent(request);
        SessionContext sessionContext = new SessionContext();

        LOGEJ.getInstance().printf(5, event,
                "====================================================================[AC] start");

        if (!prepare(event)) {
            acError(event, "EAC0002", "prepare() failed");
            return event;
        }

        LOGEJ.getInstance().printf(5, event,
                "====================================================[AC->TCF] execute call mode=" + mode);
        EPlatonEvent result = tcf.execute(event, sessionContext, mode);
        LOGEJ.getInstance().printf(5, result,
                "====================================================[AC->TCF] execute return errorcode="
                        + result.getTPSVCINFODTO().getErrorcode());
        return result;
    }

    private String resolveTransactionMode(String transactionMode) {
        if (transactionMode != null && !transactionMode.isBlank()) {
            return transactionMode.trim();
        }
        return properties.getTransaction().getDefaultMode();
    }

    private boolean prepare(EPlatonEvent event) {
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
        if (event.getErr().getErrcode() == null || event.getErr().getErrcode().isEmpty()) {
            event.setErr(EPlatonErrDTO.of("IZZ000", ""));
        }
        return true;
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

    private EPlatonEvent toEvent(SpOrder7101REQCDTO request) {
        EPlatonEvent event = new EPlatonEvent();
        EPlatonCommonDTO common = event.getCommon();

        if (request != null && request.getCommon() != null) {
            EPlatonCommonDTO.copyTo(common, request.getCommon());
        } else {
            EPlatonCommonDTO.copyTo(common, orderCommonSample());
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
        if (isBlankOrStar(common.getEventNo())) {
            common.setEventNo(DEFAULT_EVENT_NO);
        }

        event.setErr(EPlatonErrDTO.of("IZZ000", ""));
        syncTpmsvcFromCommon(event, common);

        SpOrder7101BIZDDTO requestDto = request != null && request.getBizData() != null
                ? request.getBizData()
                : SpOrder7101BIZDDTO.sample();
        event.setRequest(requestDto);
        return event;
    }

    private static EPlatonCommonDTO orderCommonSample() {
        EPlatonCommonDTO dto = EPlatonCommonDTO.sample();
        dto.setEventNo(DEFAULT_EVENT_NO);
        dto.setReqName("SP_ORDER_TEST");
        dto.setOperationName(AC + ".execute");
        return dto;
    }

    private void syncTpmsvcFromCommon(EPlatonEvent event, EPlatonCommonDTO common) {
        event.getTPSVCINFODTO().setSystem_name(valueOrDefault(common.getSystemName(), "LOCAL"));
        event.getTPSVCINFODTO().setOperation_name(valueOrDefault(common.getOperationName(), AC + ".execute"));
        event.getTPSVCINFODTO().setTpfq(valueOrDefault(common.getTpfq(), "200"));
        event.getTPSVCINFODTO().setTx_timer(valueOrDefault(common.getTxTimer(), "60"));
        event.getTPSVCINFODTO().setHostseq(valueOrDefault(common.getHostseq(), "HOST-0001"));
        event.getTPSVCINFODTO().setOrgseq(valueOrDefault(common.getOrgseq(), "ORG-0001"));
    }

    private SpOrder7101RESCDTO toResult(EPlatonEvent result) {
        SpOrder7101RESCDTO data = new SpOrder7101RESCDTO();
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
        SpOrder7101BIZDDTO responseDto = result.getResponseAs(SpOrder7101BIZDDTO.class);
        if (responseDto != null) {
            data.setBizData(responseDto);
        } else {
            data.setBizData(result.getRequestAs(SpOrder7101BIZDDTO.class));
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
