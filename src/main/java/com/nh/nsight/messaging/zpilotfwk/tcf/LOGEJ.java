package com.nh.nsight.messaging.zpilotfwk.tcf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** STF/ETF/TCF 공통 로깅 — {@code tcf} 패키지에 위치 */
public class LOGEJ {

    private static LOGEJ instance;

    private final Logger slf4j;

    private LOGEJ(Logger slf4j) {
        this.slf4j = slf4j;
    }

    /** {@code main()} 로컬 실행 — SLF4J 없이 {@code System.out} 사용 */
    public static LOGEJ forLocalMain() {
        return new LOGEJ(null);
    }

    /** Spring Boot 등 SLF4J classpath가 있을 때 */
    public static LOGEJ forSlf4j() {
        return new LOGEJ(LoggerFactory.getLogger("zpilotfwk.LOGEJ"));
    }

    public static void bindInstance(LOGEJ logej) {
        instance = logej;
    }

    public static LOGEJ getInstance() {
        if (instance == null) {
            instance = createDefault();
        }
        return instance;
    }

    private static LOGEJ createDefault() {
        try {
            return forSlf4j();
        } catch (NoClassDefFoundError | Exception ex) {
            return forLocalMain();
        }
    }

    public void printf(int level, Object event, String message) {
        logInfo("[" + level + "] " + message);
    }

    public void print(int level, Object event, String message) {
        printf(level, event, message);
    }

    public void eprintf(int level, Object event, Exception ex) {
        if (slf4j != null) {
            slf4j.warn("[{}] {}", level, ex.toString(), ex);
        } else {
            System.err.println("[" + level + "] " + ex);
            ex.printStackTrace(System.err);
        }
    }

    public void txprint(Object event, String message) {
        logTagged("TX", message);
    }

    public void nohead_txprintf(Object event, String message) {
        logTagged("TX", message);
    }

    public void txprintf(Object event, String message) {
        logTagged("TX", message);
    }

    public void ftxprintf(Object event, String message) {
        logTagged("FTX", message);
    }

    public void tcf_txprintf(Object event, String message) {
        logTagged("TCF", message);
    }

    private void logTagged(String tag, String message) {
        logInfo("[" + tag + "] " + message);
    }

    private void logInfo(String line) {
        if (slf4j != null) {
            slf4j.info(line);
        } else {
            System.out.println(line);
        }
    }
}
