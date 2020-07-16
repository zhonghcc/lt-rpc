package com.zhonghcc.ltrpc.client;

import com.zhonghcc.ltrpc.protocal.LtRpcRequest;
import com.zhonghcc.ltrpc.protocal.LtRpcResponse;
import com.zhonghcc.ltrpc.register.LtRpcNode;
import com.zhonghcc.ltrpc.trace.TraceLocal;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class LtRpcClientFactory {
    @Slf4j
    static class LtRpcClient implements InvocationHandler {


        private LtRpcSender ltRpcSender;

        public void injectSender(LtRpcSender ltRpcSender) {
            this.ltRpcSender = ltRpcSender;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            log.info("before invoke {}", method.getName());
            LtRpcRequest request = new LtRpcRequest();
            Class[] classes = method.getParameterTypes();
            if (classes != null && classes.length >= 1) {
                request.setData(args[0]);
                request.setDataClass(classes[0]);
            }
            request.setMethodName(method.getName());
            request.setTraceId(TraceLocal.getOrNewTraceId());
            request.setAuthId("");
            request.setAuthSign("");
            LtRpcNode mockNode = new LtRpcNode();
            mockNode.setHost("localhost");
            mockNode.setPort(8080);
            LtRpcResponse response = ltRpcSender.sendRpc(request, mockNode);

            log.info("after invoke {},traceId={},success={},message={}",
                    method.getName(), response.getTraceId(), response.isSuccess(), response.getMsg());
            return response.getData();
        }
    }

    static ConcurrentMap<Class, Object> CACHE = new ConcurrentHashMap<>();

    public static <T> T getClient(Class<T> invokeInterface) {
        if (!CACHE.containsKey(invokeInterface)) {
            LtRpcClient client = new LtRpcClient();
            client.injectSender(new OkHttpSender());
            Class[] interfaces = {invokeInterface};
            Object proxy = Proxy.newProxyInstance(LtRpcClientFactory.class.getClassLoader(), interfaces, client);
            CACHE.put(invokeInterface, proxy);
        }
        return (T) CACHE.get(invokeInterface);
    }
}
