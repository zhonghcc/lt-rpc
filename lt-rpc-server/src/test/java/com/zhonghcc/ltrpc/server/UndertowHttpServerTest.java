package com.zhonghcc.ltrpc.server;

import com.zhonghcc.ltrpc.protocal.LtRpcProcessor;
import com.zhonghcc.ltrpc.protocal.LtRpcCommonProcessor;
import com.zhonghcc.ltrpc.protocal.serializer.ProtostuffMessageWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class UndertowHttpServerTest {

    @Test
    public void start() {
        class ServerProxyImpl{
            public String test(String a){
                log.info("call test {}",a);
                return "echo "+a;
            }
        }
        LtRpcProcessor processor = new LtRpcCommonProcessor();
        processor.injectMessageWrapper(new ProtostuffMessageWrapper());
        processor.injectServerImpl(new ServerProxyImpl());
        LtRpcServer ltRpcServer = new UndertowHttpServer(8080,processor);
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