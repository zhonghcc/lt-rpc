package com.zhonghcc.ltrpc.client;

import com.google.common.base.Throwables;
import com.zhonghcc.ltrpc.protocal.LtRpcMessage;
import com.zhonghcc.ltrpc.protocal.LtRpcRequest;
import com.zhonghcc.ltrpc.protocal.LtRpcResponse;
import com.zhonghcc.ltrpc.register.LtRpcNode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;

@Slf4j
public class OkHttpSender implements LtRpcSender {
    public static final MediaType JSON
            = MediaType.get("application/ltrpc; charset=utf-8");

    @Override
    public LtRpcResponse sendRpc(LtRpcRequest rpcRequest, LtRpcNode node) {
        OkHttpClient client = new OkHttpClient();

//            RequestBody body = RequestBody.create(json, JSON);
        LtRpcResponse rpcResponse = new LtRpcResponse();
        RequestBody body = RequestBody.create(rpcRequest.getData());
        Request request = new Request.Builder()
                .url("http://" + node.getHost() + ":" + node.getPort() + "/")
                .post(body)
                .addHeader(LtRpcMessage.FIELD_METHOD_NAME, rpcRequest.getMethodName())
                .addHeader(LtRpcMessage.FIELD_TRACE_ID, rpcRequest.getTraceId())
                .addHeader(LtRpcMessage.FIELD_AUTH_ID, rpcRequest.getAuthId())
                .addHeader(LtRpcMessage.FIELD_AUTH_SIGN, rpcRequest.getAuthSign())
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
}




