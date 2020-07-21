package com.zhonghcc.ltrpc.server;

import com.zhonghcc.ltrpc.protocal.LtRpcProcessor;
import com.zhonghcc.ltrpc.register.LtRpcNode;
import com.zhonghcc.ltrpc.register.LtRpcNodeRegister;
import com.zhonghcc.ltrpc.utils.INetAddressUtil;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Data
public abstract class AbstractRpcServer implements LtRpcServer{

    private LtRpcProcessor processor;
    private LtRpcNodeRegister nodeRegister;
    private Class serviceInterface;
    private Object serviceImpl;
    private int port;
    private String host;

    @Override
    public void start() {
        if(serviceInterface==null || serviceImpl==null || processor==null){
            throw new RuntimeException("RpcServer Exception, service null");
        }
        Class[] interfaceList = serviceImpl.getClass().getInterfaces();
        boolean flag = false;
        for(Class iClass : interfaceList){
            if(iClass.equals(serviceInterface)){
                flag = true;
            }
        }
        if(!flag){
            throw new RuntimeException("RpcServer Exception, interface not match Object");
        }
        startRpcServer();

        registerNode();
    }

    private void registerNode() {
        LtRpcNode node = new LtRpcNode();
        node.setImplementClass(serviceImpl.getClass().getName());
        node.setInterfaceClass(serviceInterface.getName());
        node.setPort(port);

        InetAddress ip4 = null;
        try {
            ip4 = INetAddressUtil.getLocalHostLANAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException("RpcServer Exception, unknown host ip");
        }
        if(ip4==null){
            throw new RuntimeException("RpcServer Exception, unknown host ip");
        }
        node.setHost(ip4.getHostAddress());
        log.info("registerNode {}",node);
        boolean ret = nodeRegister.registerNode(node);
        if(!ret){
            throw new RuntimeException("RpcServer Exception, register fail");
        }

    }

    protected abstract void startRpcServer();
}
