package com.zhonghcc.ltrpc.protocal.serializer;

import com.zhonghcc.ltrpc.protocal.LtRpcRawRequest;
import com.zhonghcc.ltrpc.protocal.LtRpcRawResponse;
import com.zhonghcc.ltrpc.protocal.LtRpcRequest;
import com.zhonghcc.ltrpc.protocal.LtRpcResponse;

public class ProtostuffMessageWrapper implements LtRpcMessageWrapper{

    static private LtRpcSerializer serializer = new ProtostuffSerializer();

    @Override
    public LtRpcRawRequest serialize(LtRpcRequest request) {
        LtRpcRawRequest result = LtRpcRawRequest.builder()
            .authId(request.getAuthId())
            .authSign(request.getAuthSign())
            .methodName(request.getMethodName())
            .traceId(request.getTraceId())
            .data(serializer.serialize(request.getData(),request.getDataClass()))
            .build();
        return result;
    }

    @Override
    public LtRpcRequest deserialize(LtRpcRawRequest data, Class clazz) {
        LtRpcRequest result = new LtRpcRequest();
        result.setAuthId(data.getAuthId());
        result.setAuthSign(data.getAuthSign());
        result.setMethodName(data.getMethodName());
        result.setTraceId(data.getTraceId());
        result.setDataClass(clazz);
        result.setData(serializer.deserialize(data.getData(),clazz));
        return result;
    }

    @Override
    public LtRpcRawResponse serialize(LtRpcResponse response) {
        LtRpcRawResponse result = new LtRpcRawResponse();
        result.setSuccess(response.isSuccess());
        result.setMsg(response.getMsg());
        result.setTraceId(response.getTraceId());
        result.setData(serializer.serialize(response.getData(),response.getDataClass()));
        return result;
    }

    @Override
    public LtRpcResponse deserialize(LtRpcRawResponse data, Class clazz) {
        LtRpcResponse result = new LtRpcResponse();
        result.setSuccess(data.isSuccess());
        result.setMsg(data.getMsg());
        result.setTraceId(data.getTraceId());
        result.setDataClass(clazz);
        result.setData(serializer.deserialize(data.getData(),clazz));
        return result;
    }
}
