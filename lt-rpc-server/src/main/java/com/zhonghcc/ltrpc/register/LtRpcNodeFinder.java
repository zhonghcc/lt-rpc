package com.zhonghcc.ltrpc.register;

import java.util.List;

public interface LtRpcNodeFinder {
    List<LtRpcNode> findAvaliableNodes(Class interfaceClass);
}
