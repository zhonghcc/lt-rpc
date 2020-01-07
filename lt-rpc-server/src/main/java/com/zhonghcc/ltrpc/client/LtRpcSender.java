package com.zhonghcc.ltrpc.client;

import com.zhonghcc.ltrpc.protocal.LtRpcRequest;
import com.zhonghcc.ltrpc.protocal.LtRpcResponse;
import com.zhonghcc.ltrpc.register.LtRpcNode;

public interface LtRpcSender {

    LtRpcResponse sendRpc(LtRpcRequest request,LtRpcNode node);
}
