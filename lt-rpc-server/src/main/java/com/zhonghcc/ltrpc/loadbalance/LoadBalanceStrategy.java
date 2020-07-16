package com.zhonghcc.ltrpc.loadbalance;

import com.zhonghcc.ltrpc.register.LtRpcNode;

import java.util.List;

public interface LoadBalanceStrategy {
    LtRpcNode choseNode(List<LtRpcNode> ltRpcNodes);
}
