package com.zhonghcc.ltrpc.client;

import com.zhonghcc.ltrpc.TestService;
import com.zhonghcc.ltrpc.protocal.LtRpcRequest;
import com.zhonghcc.ltrpc.protocal.LtRpcResponse;
import com.zhonghcc.ltrpc.register.LtRpcNode;
import org.junit.Test;

public class OkHttpSenderTest {

    @Test
    public void sendRpc() {

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
        String ret = service.sayHello("hello");
        System.out.println(ret);


    }
}