package com.zhonghcc.ltrpc.register.zk;

import com.zhonghcc.ltrpc.register.LtRpcNode;
import com.zhonghcc.ltrpc.register.LtRpcNodeRegister;

public class ZkRegister implements LtRpcNodeRegister {
    @Override
    public boolean registerNode(LtRpcNode node) {
        return false;
    }
}
