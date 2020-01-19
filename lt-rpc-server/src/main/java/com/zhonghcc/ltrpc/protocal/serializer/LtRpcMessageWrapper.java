package com.zhonghcc.ltrpc.protocal.serializer;

import com.zhonghcc.ltrpc.protocal.LtRpcRawRequest;
import com.zhonghcc.ltrpc.protocal.LtRpcRawResponse;
import com.zhonghcc.ltrpc.protocal.LtRpcRequest;
import com.zhonghcc.ltrpc.protocal.LtRpcResponse;

public interface LtRpcMessageWrapper {

    public LtRpcRawRequest serialize(LtRpcRequest request);

    public LtRpcRequest deserialize(LtRpcRawRequest data, Class clazz);

    public LtRpcRawResponse serialize(LtRpcResponse request);

    public LtRpcResponse deserialize(LtRpcRawResponse data, Class clazz);
}
