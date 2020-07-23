package com.zhonghcc.ltrpc.server;

import com.zhonghcc.ltrpc.TestService;
import com.zhonghcc.ltrpc.TestService2;
import com.zhonghcc.ltrpc.protocal.LtRpcProcessor;
import com.zhonghcc.ltrpc.protocal.LtRpcCommonProcessor;
import com.zhonghcc.ltrpc.protocal.serializer.ProtostuffMessageWrapper;
import com.zhonghcc.ltrpc.register.LtRpcNodeRegister;
import com.zhonghcc.ltrpc.register.zk.ZkRegister;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class UndertowHttpServerTest {

    @Test
    public void start() {
        class ServerProxyImpl implements TestService {
            public String sayHello(String a){
                log.info("call test {}",a);
                i.incrementAndGet();
                return "echo "+a ;
            }
        }

        class TestService2Impl implements TestService2 {


            @Override
            public String sayHelloToHer(String hello) {
                log.info("call her {}",hello);
                return "echo "+hello;
            }
        }

        TestService impl = new ServerProxyImpl();
        LtRpcProcessor processor = new LtRpcCommonProcessor();
        processor.injectMessageWrapper(new ProtostuffMessageWrapper());
        processor.injectServerImpl(impl);

        LtRpcNodeRegister nodeRegister = new ZkRegister();

        for(int i=0;i<4;i++) {
            Random random = new Random();
            int port = 10000 + random.nextInt(100);
            UndertowHttpServer ltRpcServer = new UndertowHttpServer();
            ltRpcServer.setPort(port);
            ltRpcServer.setProcessor(processor);
            ltRpcServer.setNodeRegister(nodeRegister);
            ltRpcServer.setServiceInterface(TestService.class);
            ltRpcServer.setServiceImpl(impl);

            ltRpcServer.start();
        }
        while(true){
            try {
                Thread.sleep(50000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}