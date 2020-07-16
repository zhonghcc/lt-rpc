package com.zhonghcc.ltrpc.register;

import lombok.Data;

@Data
public class LtRpcNode {
    String host;
    int port;
    int serviceLevel;
    Class interfaceClass;
    Class implementClass;
}
