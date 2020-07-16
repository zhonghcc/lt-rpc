package com.zhonghcc.ltrpc.register.zk;

import com.zhonghcc.ltrpc.register.LtRpcNode;
import com.zhonghcc.ltrpc.register.LtRpcNodeFinder;

import java.util.List;

public class ZkFinder implements LtRpcNodeFinder {
    @Override
    public List<LtRpcNode> findAvaliableNodes(Class interfaceClass) {
        return null;
    }
}
