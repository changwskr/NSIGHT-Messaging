package com.nh.nsight.messaging.zpilotfwk.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Import;

import com.nh.nsight.messaging.zpilotfwk.tcf.LOGEJ;

import com.nh.nsight.messaging.zpilotfwk.tcf.config.TcfConfig;
import com.nh.nsight.messaging.zpilotfwk.tcf.support.TransactionControlDAO;

@Configuration

@EnableConfigurationProperties(ZpilotFwkProperties.class)

@Import(TcfConfig.class)

public class ZpilotFwkConfig {

    @Bean

    TransactionControlDAO transactionControlDAO() {

        return new TransactionControlDAO();

    }

    @Bean

    LOGEJ logej() {

        LOGEJ logej = LOGEJ.forSlf4j();

        LOGEJ.bindInstance(logej);

        return logej;

    }

}
