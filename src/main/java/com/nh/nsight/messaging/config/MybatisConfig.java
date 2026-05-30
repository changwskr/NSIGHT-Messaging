package com.nh.nsight.messaging.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {
        "com.nh.nsight.messaging.message.mapper",
        "com.nh.nsight.messaging.file.mapper",
        "com.nh.nsight.messaging.transactionmgr.mapper"
})
public class MybatisConfig {
}
