package com.nh.nsight.messaging.zpilotfwk.order.as;

import com.nh.nsight.messaging.zpilotfwk.order.dc.DC_ORDER;
import com.nh.nsight.messaging.zpilotfwk.order.dc.dto.SpOrder7101BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonErrDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonEvent;
import com.nh.nsight.messaging.zpilotfwk.tcf.ISpService;
import com.nh.nsight.messaging.zpilotfwk.tcf.ZpilotFwkBizException;
import com.nh.nsight.messaging.zpilotfwk.tcf.routing.SpRouteKeyResolver;

import org.springframework.stereotype.Service;

@Service
public class SP_ORDER implements ISpService {

    private static final String AS = "SP_ORDER";

    private final DC_ORDER dcOrder;

    public SP_ORDER(DC_ORDER dcOrder) {
        this.dcOrder = dcOrder;
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
            case "7101" -> create(event);
            case "7102" -> search(event);
            default -> fail(event, "EORD0001", "Unknown txCode: " + txCode);
        };
    }

    private EPlatonEvent create(EPlatonEvent event) {
        SpOrder7101BIZDDTO requestDto = event.getRequestAs(SpOrder7101BIZDDTO.class);
        if (requestDto == null) {
            throw new ZpilotFwkBizException("bizData는 필수입니다.");
        }
        SpOrder7101BIZDDTO saved = dcOrder.create(requestDto);
        event.setResponse(saved);
        System.out.println("***** [" + AS + "] create END id=" + saved.getId());
        return event;
    }

    private EPlatonEvent search(EPlatonEvent event) {
        SpOrder7101BIZDDTO condition = event.getRequestAs(SpOrder7101BIZDDTO.class);
        if (condition == null) {
            condition = new SpOrder7101BIZDDTO();
        }
        var list = dcOrder.search(condition);
        event.setResponse(list.isEmpty() ? new SpOrder7101BIZDDTO() : list.get(0));
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
