package com.zhonghcc.ltrpc.protocal;

import lombok.Data;

@Data
public class LtRpcResponse {
    String traceId;
    boolean success;
    String msg;
    byte[] data;
}
