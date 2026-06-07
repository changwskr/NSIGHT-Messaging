package com.nh.nsight.messaging.zpilotfwk.comrc.ac;

import com.nh.nsight.messaging.zpilotfwk.comrc.ac.dto.SpComrc7201REQCDTO;
import com.nh.nsight.messaging.zpilotfwk.comrc.ac.dto.SpComrc7201RESCDTO;
import com.nh.nsight.messaging.zpilotfwk.comrc.ac.dto.SpComrcApiResponse;
import com.nh.nsight.messaging.zpilotfwk.comrc.dc.dto.SpComrc7201BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.config.ZpilotFwkProperties;
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
@RequestMapping("/api/zpilotfwk/sp-comrc")
public class AC_SP_COMRC {

    private static final String AC = "AC_SP_COMRC";
    private static final String DEFAULT_EVENT_NO = "SP_COMRC7201";

    private final TCF tcf;
    private final ZpilotFwkProperties properties;

    public AC_SP_COMRC(TCF tcf, ZpilotFwkProperties properties) {
        this.tcf = tcf;
        this.properties = properties;
    }

    @PostMapping("/execute")
    public ResponseEntity<SpComrcApiResponse<SpComrc7201RESCDTO>> execute(
            @RequestBody(required = false) SpComrc7201REQCDTO request,
            @RequestParam(required = false) String transactionMode) {
        String mode = resolveTransactionMode(transactionMode);
        System.out.println("***** [" + AC + "] execute START mode=" + mode);

        EPlatonEvent event = null;
        try {
            event = toEvent(request);
            SessionContext sessionContext = new SessionContext();

            LOGEJ.getInstance().printf(5, event,
                    "====================================================================[AC] start");

            if (!prepare(event)) {
                acError(event, "EAC0002", "prepare() failed");
                return ResponseEntity.ok(SpComrcApiResponse.ok(toResult(event)));
            }

            EPlatonEvent result = tcf.execute(event, sessionContext, mode);
            return ResponseEntity.ok(SpComrcApiResponse.ok(toResult(result)));
        } catch (Exception ex) {
            if (event != null) {
                acError(event, "EAC0003", getClass().getName() + ".execute():" + ex);
                return ResponseEntity.internalServerError()
                        .body(SpComrcApiResponse.ok(toResult(event)));
            }
            return ResponseEntity.internalServerError()
                    .body(SpComrcApiResponse.fail(ex.getMessage()));
        }
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

    private EPlatonEvent toEvent(SpComrc7201REQCDTO request) {
        EPlatonEvent event = new EPlatonEvent();
        EPlatonCommonDTO common = event.getCommon();

        if (request != null && request.getCommon() != null) {
            EPlatonCommonDTO.copyTo(common, request.getCommon());
        } else {
            EPlatonCommonDTO.copyTo(common, comrcCommonSample());
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

        SpComrc7201BIZDDTO requestDto = request != null && request.getBizData() != null
                ? request.getBizData()
                : SpComrc7201BIZDDTO.sample();
        event.setRequest(requestDto);
        return event;
    }

    private static EPlatonCommonDTO comrcCommonSample() {
        EPlatonCommonDTO dto = EPlatonCommonDTO.sample();
        dto.setEventNo(DEFAULT_EVENT_NO);
        dto.setReqName("SP_COMRC_TEST");
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

    private SpComrc7201RESCDTO toResult(EPlatonEvent result) {
        SpComrc7201RESCDTO data = new SpComrc7201RESCDTO();
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
        SpComrc7201BIZDDTO responseDto = result.getResponseAs(SpComrc7201BIZDDTO.class);
        if (responseDto != null) {
            data.setBizData(responseDto);
        } else {
            data.setBizData(result.getRequestAs(SpComrc7201BIZDDTO.class));
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
