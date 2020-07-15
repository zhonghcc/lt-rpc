package com.zhonghcc.ltrpc.client;

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
    static class LtRpcClient implements InvocationHandler{


        private LtRpcSender ltRpcSender;

        public void injectSender(LtRpcSender ltRpcSender){
            this.ltRpcSender = ltRpcSender;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            log.info("before invoke {}",method.getName());



            log.info("after invoke {}",method.getName());
            return null;
        }
    }

    static ConcurrentMap<Class,Object> CACHE = new ConcurrentHashMap<>();

    public static <T> T getClient(Class<T> invokeInterface){
        if(!CACHE.containsKey(invokeInterface)){
            LtRpcClient client = new LtRpcClient();
            client.injectSender(new OkHttpSender());
            Class[] interfaces = {invokeInterface};
            Object proxy = Proxy.newProxyInstance(LtRpcClientFactory.class.getClassLoader(),interfaces,client);
            CACHE.put(invokeInterface,proxy);
        }
        return (T)CACHE.get(invokeInterface);
    }
}
