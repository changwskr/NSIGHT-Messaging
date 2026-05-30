package com.nh.nsight.messaging.common.context;

import java.time.OffsetDateTime;

public final class RequestContext {
    private static final ThreadLocal<Context> HOLDER = new ThreadLocal<>();

    private RequestContext() {
    }

    public static void set(Context context) {
        HOLDER.set(context);
    }

    public static Context get() {
        Context context = HOLDER.get();
        if (context == null) {
            return Context.system();
        }
        return context;
    }

    public static void clear() {
        HOLDER.remove();
    }

    public record Context(
            String guid,
            String traceId,
            String userId,
            String branchId,
            String centerId,
            String terminalId,
            String clientIp,
            OffsetDateTime requestDateTime
    ) {
        public static Context system() {
            return new Context(
                    "SYSTEM-GUID",
                    "SYSTEM-TRACE",
                    "SYSTEM",
                    "000000",
                    "LOCAL",
                    "SYSTEM-TERMINAL",
                    "127.0.0.1",
                    OffsetDateTime.now()
            );
        }
    }
}
