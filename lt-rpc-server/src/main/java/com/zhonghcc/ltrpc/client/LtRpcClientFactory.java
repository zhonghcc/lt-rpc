package com.zhonghcc.ltrpc.client;

import com.zhonghcc.ltrpc.loadbalance.LoadBalanceStrategy;
import com.zhonghcc.ltrpc.loadbalance.RandomStrategy;
import com.zhonghcc.ltrpc.protocal.LtRpcRequest;
import com.zhonghcc.ltrpc.protocal.LtRpcResponse;
import com.zhonghcc.ltrpc.register.LtRpcNode;
import com.zhonghcc.ltrpc.register.LtRpcNodeFinder;
import com.zhonghcc.ltrpc.register.zk.ZkFinder;
import com.zhonghcc.ltrpc.trace.TraceLocal;
import lombok.Builder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class LtRpcClientFactory {
    @Slf4j
    @Setter
    @Builder
    static class LtRpcClient implements InvocationHandler {


        private LtRpcSender ltRpcSender;
        private LtRpcNodeFinder ltRpcNodeFinder;
        private Class interfaceClass;
        private LoadBalanceStrategy loadBalanceStrategy;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            log.debug("before invoke {}", method.getName());
            LtRpcRequest request = new LtRpcRequest();
            Class[] classes = method.getParameterTypes();
            if (classes != null && classes.length >= 1) {
                request.setData(args[0]);
                request.setDataClass(classes[0]);
            }
            request.setMethodName(method.getName());
            request.setTraceId(TraceLocal.getOrNewTraceId());
            request.setAuthId("");
            request.setAuthSign("");
            List<LtRpcNode> nodes = ltRpcNodeFinder.findAvaliableNodes(interfaceClass);
            LtRpcNode chosedNode = loadBalanceStrategy.choseNode(nodes);
            log.debug("chosedNode {}",chosedNode);
            LtRpcResponse response;
            if(chosedNode!=null){
                response = ltRpcSender.sendRpc(request, chosedNode);
                log.debug("after invoke {},traceId={},success={},message={}",
                        method.getName(), response.getTraceId(), response.isSuccess(), response.getMsg());
                return response.getData();
            }else{
                response = new LtRpcResponse();
                response.setSuccess(false);
                response.setMsg("no valid endpoint founded");
                throw new RuntimeException("no valid endpoint founded");
            }

        }

    }

    static ConcurrentMap<Class, Object> CACHE = new ConcurrentHashMap<>();

    public static <T> T getClient(Class<T> invokeInterface) {
        if (!CACHE.containsKey(invokeInterface)) {
            LtRpcClient client = LtRpcClient.builder()
                    .ltRpcSender(new OkHttpSender())
                    .ltRpcNodeFinder(new ZkFinder())
                    .interfaceClass(invokeInterface)
                    .loadBalanceStrategy(new RandomStrategy())
                    .build();
            Class[] interfaces = {invokeInterface};
            Object proxy = Proxy.newProxyInstance(LtRpcClientFactory.class.getClassLoader(), interfaces, client);
            CACHE.put(invokeInterface, proxy);
        }
        return (T) CACHE.get(invokeInterface);
    }
}
