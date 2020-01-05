package com.zhonghcc.ltrpc.protocal;

public interface LtRpcProcessor {

    void injectServerImpl(Object proxy);

    LtRpcResponse processRpc(LtRpcRequest request);
}
