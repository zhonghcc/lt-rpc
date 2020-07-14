package com.zhonghcc.ltrpc.client;

import com.google.common.base.Throwables;
import com.zhonghcc.ltrpc.protocal.*;
import com.zhonghcc.ltrpc.protocal.serializer.LtRpcMessageWrapper;
import com.zhonghcc.ltrpc.protocal.serializer.LtRpcSerializer;
import com.zhonghcc.ltrpc.protocal.serializer.ProtostuffMessageWrapper;
import com.zhonghcc.ltrpc.protocal.serializer.ProtostuffSerializer;
import com.zhonghcc.ltrpc.register.LtRpcNode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;

@Slf4j
public class OkHttpSender implements LtRpcSender {
    public static final MediaType JSON
            = MediaType.get("application/ltrpc; charset=utf-8");

    LtRpcMessageWrapper wrapper = new ProtostuffMessageWrapper();
    @Override
    public LtRpcResponse sendRpc(LtRpcRequest rpcRequest, LtRpcNode node) {
        OkHttpClient client = new OkHttpClient();

        LtRpcRawRequest rawRequest = wrapper.serialize(rpcRequest);
        LtRpcRawResponse rawResponse = new LtRpcRawResponse();
        RequestBody body = RequestBody.create(rawRequest.getData());
        Request request = new Request.Builder()
                .url("http://" + node.getHost() + ":" + node.getPort() + "/")
                .post(body)
                .addHeader(LtRpcMessage.FIELD_METHOD_NAME, rawRequest.getMethodName())
                .addHeader(LtRpcMessage.FIELD_TRACE_ID, rawRequest.getTraceId())
                .addHeader(LtRpcMessage.FIELD_AUTH_ID, rawRequest.getAuthId())
                .addHeader(LtRpcMessage.FIELD_AUTH_SIGN, rawRequest.getAuthSign())
                .build();
        try {
            Response httpResponse = client.newCall(request).execute();
            byte[] responseData = httpResponse.body().bytes();

            rawResponse.setData(responseData);
            rawResponse.setTraceId(httpResponse.header(LtRpcMessage.FIELD_TRACE_ID));
            rawResponse.setMsg(httpResponse.header(LtRpcMessage.FIELD_MSG));
            //TODO success false
            rawResponse.setSuccess(Boolean.getBoolean(httpResponse.header(LtRpcMessage.FIELD_SUCCESS)));
            //TODO wrapper interface
            LtRpcResponse response = wrapper.deserialize(rawResponse,String.class);
            return response;
        } catch (IOException e) {
            log.error("request error exception {}", Throwables.getStackTraceAsString(e));
            LtRpcResponse response = new LtRpcResponse();
            response.setMsg(e.getMessage());
            response.setSuccess(false);
            return response;
        }
    }
    public static void main(String[] args){
        System.out.println(1);
    }
}




