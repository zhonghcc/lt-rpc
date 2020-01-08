package com.zhonghcc.ltrpc.server;

import com.zhonghcc.ltrpc.protocal.LtRpcProcessor;
import com.zhonghcc.ltrpc.protocal.LtRpcProtostuffProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.*;

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
        LtRpcProcessor processor = new LtRpcProtostuffProcessor();
        processor.injectServerImpl(new ServerProxyImpl());
        LtRpcServer ltRpcServer = new UndertowHttpServer(8080,processor);
        ltRpcServer.start();
    }
}