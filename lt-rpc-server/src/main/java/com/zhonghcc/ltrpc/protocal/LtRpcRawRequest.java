package com.zhonghcc.ltrpc.protocal;

import lombok.Data;

@Data
public class LtRpcRawRequest {
    String methodName;
    String traceId;
    String authSign;
    String authId;
    byte[] data;
}
