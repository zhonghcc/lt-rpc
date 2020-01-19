package com.zhonghcc.ltrpc.protocal;

import lombok.Data;

@Data
public class LtRpcRawResponse {
    String traceId;
    boolean success;
    String msg;
    byte[] data;
}
