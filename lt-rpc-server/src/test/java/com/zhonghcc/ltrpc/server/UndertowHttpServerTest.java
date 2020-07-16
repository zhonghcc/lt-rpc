package com.zhonghcc.ltrpc.server;

import com.zhonghcc.ltrpc.TestService;
import com.zhonghcc.ltrpc.protocal.LtRpcProcessor;
import com.zhonghcc.ltrpc.protocal.LtRpcCommonProcessor;
import com.zhonghcc.ltrpc.protocal.serializer.ProtostuffMessageWrapper;
import com.zhonghcc.ltrpc.register.LtRpcNodeRegister;
import com.zhonghcc.ltrpc.register.zk.ZkRegister;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class UndertowHttpServerTest {

    @Test
    public void start() {
        class ServerProxyImpl implements TestService {
            public String sayHello(String a){
                log.info("call test {}",a);
                return "echo "+a;
            }
        }
        LtRpcProcessor processor = new LtRpcCommonProcessor();
        processor.injectMessageWrapper(new ProtostuffMessageWrapper());
        processor.injectServerImpl(new ServerProxyImpl());

        LtRpcNodeRegister nodeRegister = new ZkRegister();

        UndertowHttpServer ltRpcServer = new UndertowHttpServer();
        ltRpcServer.setPort(8080);
        ltRpcServer.setProcessor(processor);
        ltRpcServer.setNodeRegister(nodeRegister);

        ltRpcServer.start();
        while(true){
            try {
                Thread.sleep(50000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}