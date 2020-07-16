package com.zhonghcc.ltrpc.server;

import com.zhonghcc.ltrpc.protocal.LtRpcProcessor;
import com.zhonghcc.ltrpc.register.LtRpcNodeRegister;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public abstract class AbstractRpcServer implements LtRpcServer{

    private LtRpcProcessor processor;
    private LtRpcNodeRegister nodeRegister;
    private int port;
    private String host;

    @Override
    public void start() {
        startRpcServer();

        registerNode();
    }

    private void registerNode() {
    }

    protected abstract void startRpcServer();
}
