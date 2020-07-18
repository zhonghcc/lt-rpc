package com.zhonghcc.ltrpc.client;

import com.zhonghcc.ltrpc.TestService;
import com.zhonghcc.ltrpc.protocal.LtRpcRequest;
import com.zhonghcc.ltrpc.protocal.LtRpcResponse;
import com.zhonghcc.ltrpc.register.LtRpcNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Test;

@Slf4j
public class OkHttpSenderTest {

    @Test
    public void sendRpc()  {

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

        for(int i=0;i<100;i++) {
            try {
                String ret = service.sayHello("hello");
                System.out.println(ret);
                Thread.sleep(3000);
            }catch (Exception e){
                log.error("exception {}", ExceptionUtils.getStackTrace(e));
            }
        }



    }
}