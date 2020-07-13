package com.zhonghcc.ltrpc.protocal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LtRpcRawRequest {
    String methodName;
    String traceId;
    String authSign;
    String authId;
    byte[] data;
}
