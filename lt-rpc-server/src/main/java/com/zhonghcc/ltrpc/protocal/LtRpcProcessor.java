package com.zhonghcc.ltrpc.protocal;

import com.zhonghcc.ltrpc.protocal.serializer.LtRpcMessageWrapper;
import com.zhonghcc.ltrpc.protocal.serializer.LtRpcSerializer;

public interface LtRpcProcessor {

    void injectServerImpl(Object proxy);

    void injectMessageWrapper(LtRpcMessageWrapper wrapper);

    LtRpcResponse processRpc(LtRpcRequest request);

    LtRpcRawResponse processRawRpc(LtRpcRawRequest request);
}
