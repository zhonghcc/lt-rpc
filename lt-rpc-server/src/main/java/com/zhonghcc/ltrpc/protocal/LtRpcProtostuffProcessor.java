package com.zhonghcc.ltrpc.protocal;

import com.google.common.base.Throwables;
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
public class LtRpcProtostuffProcessor implements LtRpcProcessor{

    Object proxy;
    private static Map<String,Method> methodMap = new HashMap<>();

    private static Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();
    private static LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

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
            Class paramType = method.getParameterTypes()[0];
            Class responseType = method.getReturnType();
            Schema paramSchema = getSchema(paramType);
            Schema responseSchema = getSchema(responseType);
            byte[] paramData = request.getData();
            Object paramObj = deserialize(paramData,paramType);
            method.setAccessible(true);
            try {
                Object responseObj = method.invoke(this.proxy, paramObj);
                byte[] responseData = serialize(responseObj,responseType);
                LtRpcResponse ltRpcResponse = new LtRpcResponse();
                ltRpcResponse.setSuccess(true);
                ltRpcResponse.setMsg("success");
                ltRpcResponse.setTraceId(request.getTraceId());
                ltRpcResponse.setData(responseData);
                return ltRpcResponse;
            }catch (IllegalAccessException| InvocationTargetException e){
                log.error("调用失败{},{},e={}",methodName,methodMap.get(methodName), Throwables.getStackTraceAsString(e));
                throw new RuntimeException("调用失败 "+methodName);
            }

        }else{
            log.error("未实现Server函数 {}",methodName);
            throw new IllegalArgumentException("未实现Server函数 "+methodName);
        }
    }

    public static <T> byte[] serialize(T obj, Class<T> clazz) {
        Schema<T> schema = getSchema(clazz);
        byte[] data;
        try {
            data = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }

        return data;
    }

    public static <T> T deserialize(byte[] data, Class<T> clazz) {
        Schema<T> schema = getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }


    private static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) schemaCache.get(clazz);
        if (Objects.isNull(schema)) {
            //这个schema通过RuntimeSchema进行懒创建并缓存
            //所以可以一直调用RuntimeSchema.getSchema(),这个方法是线程安全的
            schema = RuntimeSchema.getSchema(clazz);
            if (Objects.nonNull(schema)) {
                schemaCache.put(clazz, schema);
            }
        }

        return schema;
    }
}
