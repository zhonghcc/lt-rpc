package com.zhonghcc.ltrpc.client;

import com.google.common.base.Throwables;
import com.zhonghcc.ltrpc.protocal.LtRpcMessage;
import com.zhonghcc.ltrpc.protocal.LtRpcRawRequest;
import com.zhonghcc.ltrpc.protocal.LtRpcRequest;
import com.zhonghcc.ltrpc.protocal.LtRpcResponse;
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
        LtRpcResponse rpcResponse = new LtRpcResponse();
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
            Response response = client.newCall(request).execute();
            byte[] responseData = response.body().bytes();

            rpcResponse.setData(responseData);
            rpcResponse.setTraceId(response.header(LtRpcMessage.FIELD_TRACE_ID));
            rpcResponse.setMsg(response.header(LtRpcMessage.FIELD_MSG));
            rpcResponse.setSuccess(Boolean.getBoolean(response.header(LtRpcMessage.FIELD_SUCCESS)));
            return rpcResponse;
        } catch (IOException e) {
            log.error("request error exception {}", Throwables.getStackTraceAsString(e));
            rpcResponse.setMsg(e.getMessage());
            rpcResponse.setSuccess(false);
            return rpcResponse;
        }
    }
    public static void main(String[] args){
        System.out.println(1);
    }
}




