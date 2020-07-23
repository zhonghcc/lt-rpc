package com.zhonghcc.ltrpc.client;

import com.zhonghcc.ltrpc.TestService;
import com.zhonghcc.ltrpc.protocal.LtRpcRequest;
import com.zhonghcc.ltrpc.protocal.LtRpcResponse;
import com.zhonghcc.ltrpc.register.LtRpcNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OkHttpSenderTest {

    @Test
    public void sendRpc() throws InterruptedException {

//        LtRpcSender sender = new OkHttpSender();
//        LtRpcRequest request = new LtRpcRequest();
//        request.setTraceId("uuid");
//        request.setMethodName("test");
//        request.setAuthId("");
//        request.setAuthSign("");
//        String str = "Say Hello!";
//
//        request.setData(str);
//        request.setDataClass(String.class);
//        System.out.println(request);
//        LtRpcNode mockNode = new LtRpcNode();
//        mockNode.setHost("localhost");
//        mockNode.setPort(8080);
//        LtRpcResponse response = sender.sendRpc(request,mockNode);
//        System.out.println(response);
        TestService service = LtRpcClientFactory.getClient(TestService.class);

        long start = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for(int i=0;i<10000;i++) {
            try {
                final int b = i;
                executor.submit(()->{
                    String ret = service.sayHello("hello"+b);
                    log.info(ret);
                });
            }catch (Exception e){
                log.error("exception {}", ExceptionUtils.getStackTrace(e));
            }
        }
        executor.awaitTermination(300, TimeUnit.SECONDS);
        log.info("total cost={}",System.currentTimeMillis()-start);



    }
}