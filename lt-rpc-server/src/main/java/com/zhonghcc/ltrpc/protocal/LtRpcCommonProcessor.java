package com.zhonghcc.ltrpc.protocal;

import com.google.common.base.Throwables;
import com.zhonghcc.ltrpc.protocal.serializer.LtRpcMessageWrapper;
import com.zhonghcc.ltrpc.protocal.serializer.LtRpcSerializer;
import com.zhonghcc.ltrpc.trace.TraceLocal;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class LtRpcCommonProcessor implements LtRpcProcessor{

    Object proxy;
    LtRpcMessageWrapper wrapper;
    private static Map<String,Method> methodMap = new HashMap<>();




    @Override
    public void injectServerImpl(Object proxy) {
        this.proxy = proxy;
        Class cls = proxy.getClass();
        Method[] methods = cls.getDeclaredMethods();
        for(Method method : methods){
            //不处理非公共函数
            if(!Modifier.isPublic(method.getModifiers())){
                continue;
            }
            String methodName = method.getName();
            //不允许函数重载
            if(methodMap.containsKey(methodName)){
                log.error("不允许实现Server重载函数 {}",methodName);
                throw new IllegalArgumentException("不允许实现Server重载函数 "+methodName);
            }
            methodMap.put(methodName,method);
        }
    }

    @Override
    public void injectMessageWrapper(LtRpcMessageWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public LtRpcRawResponse processRawRpc(LtRpcRawRequest rawRequest){
        String methodName = rawRequest.getMethodName();
        String traceId = rawRequest.getTraceId();
        TraceLocal.setTraceId(traceId);
        if(methodMap.containsKey(methodName)) {
            Method method = methodMap.get(methodName);
            Class paramType = method.getParameterTypes()[0];
            LtRpcRequest request = wrapper.deserialize(rawRequest,paramType);
            LtRpcResponse ltRpcResponse = this.processRpc(request);
            LtRpcRawResponse rawResponse = wrapper.serialize(ltRpcResponse);
            return rawResponse;
        }else{
            log.error("未实现Server函数 {}",methodName);
            throw new IllegalArgumentException("未实现Server函数 "+methodName);
        }
    }

    @Override
    public LtRpcResponse processRpc(LtRpcRequest request) {
        String methodName = request.getMethodName();
        //TODO auth
        //TODO trace
        //执行方法

        Method method = methodMap.get(methodName);
        Class responseType = method.getReturnType();
        method.setAccessible(true);
        try {
            Object responseObj = method.invoke(this.proxy, request.getData());
            LtRpcResponse ltRpcResponse = new LtRpcResponse();
            ltRpcResponse.setSuccess(true);
            ltRpcResponse.setMsg("success");
            ltRpcResponse.setTraceId(request.getTraceId());
            ltRpcResponse.setData(responseObj);
            ltRpcResponse.setDataClass(responseType);
            return ltRpcResponse;
        }catch (IllegalAccessException| InvocationTargetException e){
            log.error("调用失败{},{},e={}",methodName,methodMap.get(methodName), Throwables.getStackTraceAsString(e));
            throw new RuntimeException("调用失败 "+methodName);
        }
    }


}
