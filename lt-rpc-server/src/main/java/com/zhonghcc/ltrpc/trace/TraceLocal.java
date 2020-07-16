package com.zhonghcc.ltrpc.trace;

import java.util.UUID;

public class TraceLocal {
    private static ThreadLocal<TraceContext> trace = new ThreadLocal<>();

    public static String getOrNewTraceId() {
        if (trace.get() != null) {
            return trace.get().getTraceId();
        } else {
            String uuid = UUID.randomUUID().toString();
            String traceId = uuid.replace("-", "");
            TraceContext context = new TraceContext();
            context.setTraceId(traceId);
            trace.set(context);
            return traceId;
        }
    }

    public static void setTraceId(String traceId) {
        TraceContext context = new TraceContext();
        context.setTraceId(traceId);
        trace.set(context);
    }
}
