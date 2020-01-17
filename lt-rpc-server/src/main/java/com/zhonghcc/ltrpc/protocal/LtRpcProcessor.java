package com.zhonghcc.ltrpc.protocal;

import com.zhonghcc.ltrpc.protocal.serializer.LtRpcSerializer;

public interface LtRpcProcessor {

    void injectServerImpl(Object proxy);

    void injectSerializer(LtRpcSerializer serializer);

    LtRpcResponse processRpc(LtRpcRequest request);
}
