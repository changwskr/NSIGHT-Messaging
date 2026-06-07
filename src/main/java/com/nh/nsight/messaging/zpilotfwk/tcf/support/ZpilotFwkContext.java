package com.nh.nsight.messaging.zpilotfwk.tcf.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * STF/ETF 등 레거시 {@code new} 생성 클래스에서 Bean을 조회하기 위한 브릿지.
 * Spring 클래스를 import하지 않아 main() 단독 실행 시에도 로드 가능하다.
 */
public final class ZpilotFwkContext {

    @FunctionalInterface
    public interface BeanSupplier {
        Object getBean(Class<?> type);
    }

    private static final Map<Class<?>, Object> localBeans = new ConcurrentHashMap<>();
    private static BeanSupplier springSupplier;

    private ZpilotFwkContext() {
    }

    public static void registerLocalBean(Class<?> type, Object bean) {
        localBeans.put(type, bean);
    }

    /** {@link ZpilotFwkSpringBridge}에서 Spring 컨텍스트를 연결한다. */
    public static void setSpringSupplier(BeanSupplier supplier) {
        springSupplier = supplier;
    }

    public static void clearLocalBeans() {
        localBeans.clear();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> type) {
        Object localBean = localBeans.get(type);
        if (localBean != null) {
            return (T) localBean;
        }
        if (springSupplier != null) {
            return (T) springSupplier.getBean(type);
        }
        throw new IllegalStateException("Bean not found: " + type.getName());
    }
}
