package com.zhonghcc.ltrpc.protocal;

import lombok.Data;

@Data
public class LtRpcResponse {
    String traceId;
    Object data;
}
