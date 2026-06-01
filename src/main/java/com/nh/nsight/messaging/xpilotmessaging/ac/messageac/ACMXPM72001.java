package com.nh.nsight.messaging.xpilotmessaging.ac.messageac;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.message.dto.MessageResponse;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageCDtoConverter;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageSearchCDTO;
import com.nh.nsight.messaging.xpilotmessaging.as.messageas.ASMXPM72001;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.dto.MessageSearchDDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/xpilotmessaging/messages")
public class ACMXPM72001 {

    private static final String AC = "ACMXPM72001";

    private final ASMXPM72001 asmxpm72001;

    public ACMXPM72001(ASMXPM72001 asmxpm72001) {
        this.asmxpm72001 = asmxpm72001;
    }

    @GetMapping("/{messageId}")
    public StandardResponse<MessageResponse> getMessage(@PathVariable Long messageId) {
        System.out.println("-------3[" + AC + "] getMessage START messageId=" + messageId);
        MessageResponse response = MessageCDtoConverter.toResponse(asmxpm72001.get(messageId));
        StandardResponse<MessageResponse> result = StandardResponse.success("XPM-DETAIL-001", "xpilotMessageDetail",
                response);
        System.out.println("------4[" + AC + "] getMessage END messageId=" + messageId);
        return result;
    }

    @GetMapping
    public StandardResponse<List<MessageResponse>> searchMessages(
            @RequestParam(required = false) String messageType,
            @RequestParam(required = false) String channelCode,
            @RequestParam(required = false) String useYn,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "3") int pageSize) {
        System.out.println("[" + AC + "] searchMessages START pageNo=" + pageNo + " pageSize=" + pageSize);
        MessageSearchCDTO criteria = new MessageSearchCDTO();
        criteria.setMessageType(messageType);
        criteria.setChannelCode(channelCode);
        criteria.setUseYn(useYn);
        criteria.setPageNo(pageNo);
        criteria.setPageSize(pageSize);
        List<MessageResponse> response = MessageCDtoConverter.toResponseList(asmxpm72001.search(criteria));
        long totalCount = asmxpm72001.count(criteria);
        MessageSearchDDTO condition = MessageCDtoConverter.toSearchDDto(criteria);
        StandardResponse<List<MessageResponse>> result = StandardResponse.successPage(
                "XPM-LIST-001",
                "xpilotMessageList",
                response,
                condition.getSafePageNo(),
                condition.getSafePageSize(),
                totalCount);
        System.out.println("[" + AC + "] searchMessages END totalCount=" + totalCount);
        return result;
    }
}
