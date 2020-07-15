package com.zhonghcc.ltrpc.server;

import com.zhonghcc.ltrpc.TestService;
import com.zhonghcc.ltrpc.TestService2;
import com.zhonghcc.ltrpc.protocal.LtRpcProcessor;
import com.zhonghcc.ltrpc.protocal.LtRpcCommonProcessor;
import com.zhonghcc.ltrpc.protocal.serializer.ProtostuffMessageWrapper;
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

        class TestService2Impl implements TestService2 {


            @Override
            public String sayHelloToHer(String hello) {
                log.info("call her {}",hello);
                return "echo "+hello;
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