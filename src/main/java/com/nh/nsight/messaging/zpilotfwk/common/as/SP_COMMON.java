package com.nh.nsight.messaging.zpilotfwk.common.as;

import java.math.BigDecimal;

import com.nh.nsight.messaging.zpilotfwk.common.dc.DC_COMMON;
import com.nh.nsight.messaging.zpilotfwk.common.dc.dto.SpCommon7001BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.order.ac.AC_SP_ORDER;
import com.nh.nsight.messaging.zpilotfwk.order.ac.dto.SpOrder7101REQCDTO;
import com.nh.nsight.messaging.zpilotfwk.order.dc.dto.SpOrder7101BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonCommonDTO;
import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonEvent;
import com.nh.nsight.messaging.zpilotfwk.tcf.ISpService;
import com.nh.nsight.messaging.zpilotfwk.tcf.ZpilotFwkBizException;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class SP_COMMON implements ISpService {

    private static final String AS = "SP_COMMON";
    private static final String LINKED_ORDER_EVENT_NO = "SP_ORDER7101";

    private final DC_COMMON dcCommon;
    private final AC_SP_ORDER acSpOrder;

    public SP_COMMON(DC_COMMON dcCommon, @Lazy AC_SP_ORDER acSpOrder) {
        this.dcCommon = dcCommon;
        this.acSpOrder = acSpOrder;
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

        ///////////////////////////////////////////////////
        EPlatonEvent orderResult = invokeSpOrder(event);
        if (!isSuccess(orderResult)) {
            return orderResult;
        }
        event = orderResult;
        ///////////////////////////////////////////////////

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
     * 공통 등록 전 연계 주문을 생성한다.
     * {@code bizData.name}을 고객명으로 매핑해 {@code AC_SP_ORDER} → TCF → SP_ORDER 경로로 호출한다.
     */
    private EPlatonEvent invokeSpOrder(EPlatonEvent event) {
        SpCommon7001BIZDDTO commonRequest = event.getRequestAs(SpCommon7001BIZDDTO.class);
        if (commonRequest == null || commonRequest.getName() == null || commonRequest.getName().isBlank()) {
            return event;
        }

        SpOrder7101BIZDDTO orderBizData = toOrderRequest(commonRequest);
        SpOrder7101REQCDTO orderRequest = toOrderAcRequest(event, orderBizData);

        System.out.println("***** [" + AS + "] invoke AC_SP_ORDER eventNo=" + LINKED_ORDER_EVENT_NO
                + " orderNo=" + orderBizData.getOrderNo());
        EPlatonEvent orderResult = acSpOrder.executeInternal(orderRequest, null);

        mergeOrderOutcome(event, orderResult);

        SpOrder7101BIZDDTO orderResponse = orderResult.getResponseAs(SpOrder7101BIZDDTO.class);
        if (orderResponse != null) {
            System.out.println("***** [" + AS + "] AC_SP_ORDER linked orderId=" + orderResponse.getId()
                    + " orderNo=" + orderResponse.getOrderNo());
        }

        return event;
    }

    private SpOrder7101REQCDTO toOrderAcRequest(EPlatonEvent event, SpOrder7101BIZDDTO orderBizData) {
        SpOrder7101REQCDTO request = new SpOrder7101REQCDTO();
        EPlatonCommonDTO orderCommon = new EPlatonCommonDTO();
        EPlatonCommonDTO.copyTo(orderCommon, event.getCommon());
        orderCommon.setEventNo(LINKED_ORDER_EVENT_NO);
        orderCommon.setOperationName("AC_SP_ORDER.execute");
        request.setCommon(orderCommon);
        request.setBizData(orderBizData);
        return request;
    }

    private void mergeOrderOutcome(EPlatonEvent event, EPlatonEvent orderResult) {
        if (event == null || orderResult == null || event.getTPSVCINFODTO() == null
                || orderResult.getTPSVCINFODTO() == null) {
            return;
        }
        event.getTPSVCINFODTO().setErrorcode(orderResult.getTPSVCINFODTO().getErrorcode());
        event.getTPSVCINFODTO().setError_message(orderResult.getTPSVCINFODTO().getError_message());
        event.setErr(orderResult.getErr());
        if (isSuccess(orderResult)) {
            event.setResponse(orderResult.getResponse());
        }
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
