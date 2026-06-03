package com.nh.nsight.messaging.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {
        "com.nh.nsight.messaging.message.mapper",
        "com.nh.nsight.messaging.file.mapper",
        "com.nh.nsight.messaging.transactionmgr.mapper",
        "com.nh.nsight.messaging.capacitymgr.dc.accountdc.mapper",
        "com.nh.nsight.messaging.xpilot.dc.pilotdc.mapper",
        "com.nh.nsight.messaging.xpilotmessaging.dc.messagedc.mapper",
        "com.nh.nsight.messaging.xpilotfile.dc.filedc.mapper",
        "com.nh.nsight.messaging.xpilottransactionmgr.dc.transactiondc.mapper",
        "com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.mapper",
        "com.nh.nsight.messaging.junmun.dc.junmundc.mapper"
})
public class MybatisConfig {
}
