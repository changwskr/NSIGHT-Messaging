package com.nh.nsight.messaging.zpilotfwk.common.as;

import java.math.BigDecimal;

import com.nh.nsight.messaging.zpilotfwk.common.dc.DC_COMMON;
import com.nh.nsight.messaging.zpilotfwk.common.dc.dto.SpCommon7001BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.order.as.SP_ORDER;
import com.nh.nsight.messaging.zpilotfwk.order.dc.dto.SpOrder7101BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonEvent;
import com.nh.nsight.messaging.zpilotfwk.tcf.ISpService;
import com.nh.nsight.messaging.zpilotfwk.tcf.ZpilotFwkBizException;

import org.springframework.stereotype.Service;

@Service
public class SP_COMMON implements ISpService {

    private static final String AS = "SP_COMMON";
    private static final String LINKED_ORDER_EVENT_NO = "SP_ORDER7101";

    private final DC_COMMON dcCommon;
    private final SP_ORDER spOrder;

    public SP_COMMON(DC_COMMON dcCommon, SP_ORDER spOrder) {
        this.dcCommon = dcCommon;
        this.spOrder = spOrder;
    }

    @Override
    public String serviceId() {
        return AS;
    }

    @Override
    public EPlatonEvent execute(EPlatonEvent event) {
        return process(event);
    }

    public EPlatonEvent process(EPlatonEvent event) {
        String eventNo = event != null && event.getCommon() != null ? event.getCommon().getEventNo() : null;
        System.out.println("***** [" + AS + "] process START eventNo=" + eventNo);

        validateEvent(event);

        EPlatonEvent orderResult = invokeSpOrder(event);
        if (!isSuccess(orderResult)) {
            return orderResult;
        }
        event = orderResult;

        SpCommon7001BIZDDTO requestDto = event.getRequestAs(SpCommon7001BIZDDTO.class);
        if (requestDto != null) {
            System.out.println("***** [" + AS + "] request IDTO name=" + requestDto.getName()
                    + " age=" + requestDto.getAge()
                    + " phoneNumber=" + requestDto.getPhoneNumber());
            SpCommon7001BIZDDTO saved = dcCommon.create(requestDto);
            event.setResponse(saved);
        }

        System.out.println("***** [" + AS + "] process END eventNo="
                + (event.getCommon() != null ? event.getCommon().getEventNo() : null)
                + " errorcode=" + event.getTPSVCINFODTO().getErrorcode());
        return event;
    }

    /**
     * 공통 등록 전 연계 주문(BTF)을 생성한다.
     * 동일 TCF 트랜잭션 안에서 {@code SP_ORDER}를 직접 호출한다.
     */
    private EPlatonEvent invokeSpOrder(EPlatonEvent event) {
        SpCommon7001BIZDDTO commonRequest = event.getRequestAs(SpCommon7001BIZDDTO.class);
        if (commonRequest == null || commonRequest.getName() == null || commonRequest.getName().isBlank()) {
            return event;
        }

        String originalEventNo = event.getCommon().getEventNo();
        SpOrder7101BIZDDTO orderRequest = toOrderRequest(commonRequest);

        event.getCommon().setEventNo(LINKED_ORDER_EVENT_NO);
        event.setRequest(orderRequest);

        System.out.println("***** [" + AS + "] invoke SP_ORDER eventNo=" + LINKED_ORDER_EVENT_NO
                + " orderNo=" + orderRequest.getOrderNo());
        EPlatonEvent orderResult = spOrder.execute(event);

        event.getCommon().setEventNo(originalEventNo);
        event.setRequest(commonRequest);

        SpOrder7101BIZDDTO orderResponse = orderResult.getResponseAs(SpOrder7101BIZDDTO.class);
        if (orderResponse != null) {
            System.out.println("***** [" + AS + "] SP_ORDER linked orderId=" + orderResponse.getId()
                    + " orderNo=" + orderResponse.getOrderNo());
        }

        return orderResult;
    }

    private SpOrder7101BIZDDTO toOrderRequest(SpCommon7001BIZDDTO commonRequest) {
        SpOrder7101BIZDDTO orderRequest = new SpOrder7101BIZDDTO();
        String suffix = commonRequest.getPhoneNumber() != null
                ? commonRequest.getPhoneNumber().replaceAll("\\D", "")
                : String.valueOf(System.currentTimeMillis());
        orderRequest.setOrderNo("ORD-COMMON-" + suffix);
        orderRequest.setCustomerName(commonRequest.getName());
        orderRequest.setAmount(BigDecimal.ZERO);
        orderRequest.setStatus("CREATED");
        return orderRequest;
    }

    private boolean isSuccess(EPlatonEvent event) {
        if (event == null || event.getTPSVCINFODTO() == null) {
            return false;
        }
        String errorcode = event.getTPSVCINFODTO().getErrorcode();
        return errorcode != null && !errorcode.isEmpty() && errorcode.charAt(0) == 'I';
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
