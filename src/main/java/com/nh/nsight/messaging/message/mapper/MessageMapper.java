package com.nh.nsight.messaging.message.mapper;

import com.nh.nsight.messaging.message.dto.MessageSearchCondition;
import com.nh.nsight.messaging.message.thing.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    int insertMessage(Message message);
    Message selectById(@Param("messageId") Long messageId);
    Message selectByCode(@Param("messageCode") String messageCode);
    List<Message> selectMessages(MessageSearchCondition condition);
    long countMessages(MessageSearchCondition condition);
    int updateMessage(Message message);
    int deleteById(@Param("messageId") Long messageId);
}
