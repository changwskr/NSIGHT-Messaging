package com.nh.nsight.messaging.zpilotfwk.comrc.as;

import com.nh.nsight.messaging.zpilotfwk.comrc.dc.DC_COMRC;
import com.nh.nsight.messaging.zpilotfwk.comrc.dc.dto.SpComrc7201BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonErrDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonEvent;
import com.nh.nsight.messaging.zpilotfwk.tcf.ISpService;
import com.nh.nsight.messaging.zpilotfwk.tcf.ZpilotFwkBizException;
import com.nh.nsight.messaging.zpilotfwk.tcf.routing.SpRouteKeyResolver;

import org.springframework.stereotype.Service;

@Service
public class SP_COMRC implements ISpService {

    private static final String AS = "SP_COMRC";

    private final DC_COMRC dcComrc;

    public SP_COMRC(DC_COMRC dcComrc) {
        this.dcComrc = dcComrc;
    }

    @Override
    public String serviceId() {
        return AS;
    }

    @Override
    public EPlatonEvent execute(EPlatonEvent event) {
        validateEvent(event);
        String txCode = SpRouteKeyResolver.transactionCode(event, AS);
        System.out.println("***** [" + AS + "] execute START txCode=" + txCode);

        return switch (txCode) {
            case "7201" -> create(event);
            case "7202" -> search(event);
            default -> fail(event, "ECOM0001", "Unknown txCode: " + txCode);
        };
    }

    private EPlatonEvent create(EPlatonEvent event) {
        SpComrc7201BIZDDTO requestDto = event.getRequestAs(SpComrc7201BIZDDTO.class);
        if (requestDto == null) {
            throw new ZpilotFwkBizException("bizData는 필수입니다.");
        }
        SpComrc7201BIZDDTO saved = dcComrc.create(requestDto);
        event.setResponse(saved);
        System.out.println("***** [" + AS + "] create END id=" + saved.getId());
        return event;
    }

    private EPlatonEvent search(EPlatonEvent event) {
        SpComrc7201BIZDDTO condition = event.getRequestAs(SpComrc7201BIZDDTO.class);
        if (condition == null) {
            condition = new SpComrc7201BIZDDTO();
        }
        var list = dcComrc.search(condition);
        event.setResponse(list.isEmpty() ? new SpComrc7201BIZDDTO() : list.get(0));
        System.out.println("***** [" + AS + "] search END size=" + list.size());
        return event;
    }

    private EPlatonEvent fail(EPlatonEvent event, String code, String message) {
        event.getTPSVCINFODTO().setErrorcode(code);
        event.getTPSVCINFODTO().setError_message(message);
        event.setErr(EPlatonErrDTO.of(code, message));
        return event;
    }

    private void validateEvent(EPlatonEvent event) {
        if (event == null) {
            throw new ZpilotFwkBizException("EPlatonEvent는 필수입니다.");
        }
        if (event.getTPSVCINFODTO() == null) {
            throw new ZpilotFwkBizException("TPSVCINFODTO는 필수입니다.");
        }
        if (event.getCommon() == null || event.getCommon().getEventNo() == null
                || event.getCommon().getEventNo().isBlank() || "*".equals(event.getCommon().getEventNo())) {
            throw new ZpilotFwkBizException("eventNo는 필수입니다.");
        }
    }
}
