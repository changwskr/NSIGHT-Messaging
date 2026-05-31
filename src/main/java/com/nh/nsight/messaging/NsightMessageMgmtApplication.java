package com.nh.nsight.messaging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {
        "com.nh.nsight.messaging",
        "com.traceoompgm"
})
public class NsightMessageMgmtApplication {

    public static void main(String[] args) {
        SpringApplication.run(NsightMessageMgmtApplication.class, args);
    }
}
