package com.nh.nsight.messaging.zpilotfwk.common.as;

import com.nh.nsight.messaging.zpilotfwk.common.dc.DC_COMMON;
import com.nh.nsight.messaging.zpilotfwk.common.dc.dto.SpCommon7001BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonEvent;
import com.nh.nsight.messaging.zpilotfwk.tcf.ISpService;

import org.springframework.stereotype.Service;

@Service
public class SP_COMMON implements ISpService {

    private static final String AS = "SP_COMMON";

    private final DC_COMMON dcCommon;

    public SP_COMMON(DC_COMMON dcCommon) {
        this.dcCommon = dcCommon;
    }

    @Override
    public EPlatonEvent execute(EPlatonEvent event) {
        return process(event);
    }

    public EPlatonEvent process(EPlatonEvent event) {
        String eventNo = event != null && event.getCommon() != null ? event.getCommon().getEventNo() : null;
        System.out.println("????? [" + AS + "] process START eventNo=" + eventNo);

        validateEvent(event);

        SpCommon7001BIZDDTO requestDto = event.getRequestAs(SpCommon7001BIZDDTO.class);
        if (requestDto != null) {
            System.out.println("????? [" + AS + "] request IDTO name=" + requestDto.getName()
                    + " age=" + requestDto.getAge()
                    + " phoneNumber=" + requestDto.getPhoneNumber());
            SpCommon7001BIZDDTO saved = dcCommon.create(requestDto);
            event.setResponse(saved);
        }

        EPlatonEvent result = event;

        System.out.println("????? [" + AS + "] process END eventNo="
                + (result.getCommon() != null ? result.getCommon().getEventNo() : null)
                + " errorcode=" + result.getTPSVCINFODTO().getErrorcode());
        return result;
    }

    private void validateEvent(EPlatonEvent event) {
        if (event == null) {
            throw new ZpilotFwkBizException("EPlatonEvent? ?????.");
        }
        if (event.getTPSVCINFODTO() == null) {
            throw new ZpilotFwkBizException("TPSVCINFODTO? ?????.");
        }
        if (event.getCommon() == null || event.getCommon().getEventNo() == null
                || event.getCommon().getEventNo().isBlank() || "*".equals(event.getCommon().getEventNo())) {
            throw new ZpilotFwkBizException("eventNo? ?? ?????.");
        }
    }
}
