package com.nh.nsight.messaging.zpilotfwk.tcf.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ZpilotFwkSpringBridge implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        ZpilotFwkContext.setSpringSupplier(context::getBean);
    }
}
