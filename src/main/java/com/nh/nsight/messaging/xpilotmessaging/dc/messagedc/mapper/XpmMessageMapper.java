package com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.mapper;

import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.XpmMessage;
import com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.dto.MessageSearchDDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface XpmMessageMapper {

    void insertMessage(XpmMessage message);

    XpmMessage selectById(@Param("messageId") Long messageId);

    XpmMessage selectByCode(@Param("messageCode") String messageCode);

    List<XpmMessage> selectMessages(MessageSearchDDTO condition);

    long countMessages(MessageSearchDDTO condition);

    int updateMessage(XpmMessage message);

    int deleteById(@Param("messageId") Long messageId);
}
