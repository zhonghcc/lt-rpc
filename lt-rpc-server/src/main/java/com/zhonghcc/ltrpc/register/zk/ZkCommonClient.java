package com.zhonghcc.ltrpc.register.zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZkCommonClient {
    public static String ROOT_PATH="/services";
    private static CuratorFramework instance;

    public static String getInterfacePath(String interfaceClass){
        return ROOT_PATH+"/"+interfaceClass;
    }

    public static synchronized CuratorFramework getClient(){
        if(instance==null){
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", retryPolicy);
            client.start();
            instance = client;
        }
        return instance;
    }
}
