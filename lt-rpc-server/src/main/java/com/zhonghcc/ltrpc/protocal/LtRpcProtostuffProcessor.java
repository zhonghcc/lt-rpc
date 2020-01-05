package com.zhonghcc.ltrpc.protocal;

import io.protostuff.ProtobufIOUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LtRpcProtostuffProcessor extends LtRpcProcessor{

    Object proxy;
    Map<String,Method> methodMap = new HashMap<>();




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
    public LtRpcResponse processRpc(LtRpcRequest request) {
        String methodName = request.getMethodName();
        //TODO auth
        //TODO trace
        //执行方法
        if(methodMap.containsKey(methodName)){
            Method method = methodMap.get(methodName);
            Object data = request.getData();
        }else{
            log.error("未实现Server函数 {}",methodName);
            throw new IllegalArgumentException("未实现Server函数 "+methodName);
        }
        return null;
    }
}
