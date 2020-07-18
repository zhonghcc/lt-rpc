package com.zhonghcc.ltrpc.register.zk;

import com.google.gson.Gson;
import com.zhonghcc.ltrpc.register.LtRpcNode;
import com.zhonghcc.ltrpc.register.LtRpcNodeRegister;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

@Slf4j
public class ZkRegister implements LtRpcNodeRegister {


    @Override
    public boolean registerNode(LtRpcNode node) {
        try {
//            boolean parentExist = (ZkCommonClient.getClient().checkExists().forPath(ZkCommonClient.ROOT_PATH)!=null);
//            if(!parentExist){
//                String ret = ZkCommonClient.getClient().create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(ZkCommonClient.ROOT_PATH);
//                log.info("create parent path ret={}",ret);
//            }
            String parentPath =ZkCommonClient.getInterfacePath(node.getInterfaceClass());
            boolean parentExist = (ZkCommonClient.getClient().checkExists().forPath(parentPath)!=null);
            if(!parentExist){
                String ret = ZkCommonClient.getClient().create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(parentPath);
                log.info("create path {} ,ret={}",parentPath,ret);
            }

            String currentPath = parentPath+"/"+node.getHost()+":"+node.getPort();
            boolean currentExist = (ZkCommonClient.getClient().checkExists().forPath(currentPath)!=null);
            if(!currentExist){
                Gson gson = new Gson();
                String strNode = gson.toJson(node);
                String ret = ZkCommonClient.getClient().create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(currentPath,strNode.getBytes());
                log.info("create path {} ,ret={}",currentPath,ret);
            }


            return true;

        }catch(Exception e){
            log.error("create node error {}",ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

//    public static void main(String[] args){
//        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
//        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", retryPolicy);
//        client.start();
//        try{
//            List<String> paths = client.getChildren().forPath("/");
//            log.info("paths={}",paths);
//
//        }catch (Exception e){
//            log.error("zkclient error {}", ExceptionUtils.getStackTrace(e));
//        }
//    }
}
