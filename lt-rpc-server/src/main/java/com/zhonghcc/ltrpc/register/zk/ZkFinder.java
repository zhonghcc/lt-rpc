package com.zhonghcc.ltrpc.register.zk;

import com.google.gson.Gson;
import com.zhonghcc.ltrpc.register.LtRpcNode;
import com.zhonghcc.ltrpc.register.LtRpcNodeFinder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.AddWatchMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

@Slf4j
public class ZkFinder implements LtRpcNodeFinder {

    private static List<LtRpcNode> EMPTY_NODES = new ArrayList<>();
    private volatile List<LtRpcNode> CACHED_NODES;

    @Override
    public List<LtRpcNode> findAvaliableNodes(Class interfaceClass) {

        try {
            if(CACHED_NODES!=null){
                return CACHED_NODES;
            }

            String interfacePath = ZkCommonClient.getInterfacePath(interfaceClass.getName());
            boolean parentExist = (ZkCommonClient.getClient().checkExists().forPath(interfacePath) != null);
            if (!parentExist) {
                log.error("interface class path not exist {}", interfacePath);
                throw new RuntimeException("interface class path not exist " + interfacePath);
            }


            return updateNodes(interfacePath);


        }catch (Exception e){
            log.error("create node error {}", ExceptionUtils.getStackTrace(e));
            return EMPTY_NODES;
        }

    }

    public List<LtRpcNode> updateNodes(String interfacePath) throws Exception{
        List<String> nodes = ZkCommonClient.getClient().getChildren().forPath(interfacePath);
        if(nodes!=null){
            List<LtRpcNode> tempNodes = new ArrayList<>(nodes.size());
            for(String  item:nodes){
                try {
                    String nodePath = interfacePath + "/" + item;
                    byte[] rawData = ZkCommonClient.getClient().getData().forPath(nodePath);
                    String[] addressInfos = StringUtils.split(item, ":");
                    String data = new String(rawData);
                    Gson gson = new Gson();
                    LtRpcNode node = gson.fromJson(data, LtRpcNode.class);
                    if (node.getHost().equals(addressInfos[0]) &&
                            node.getPort() == Integer.parseInt(addressInfos[1])) {
                        tempNodes.add(node);
                    }
                }catch (Exception e){
                    log.error("deal one node error {}",item);
                }
            }
            log.info("update nodes={}",tempNodes);
            CACHED_NODES=tempNodes;
            return CACHED_NODES;
        }
        return EMPTY_NODES;
    }

    public void startLisener(String interfacePath) throws Exception{
        ZkCommonClient.getClient().getChildren().usingWatcher((CuratorWatcher)event -> {
            log.info("curatorWatcher {}",event);
        }).forPath(interfacePath);

        CuratorCache cache = CuratorCache.build(ZkCommonClient.getClient(), interfacePath);
        CuratorCacheListener listener = CuratorCacheListener.builder()
                .forPathChildrenCache(interfacePath, ZkCommonClient.getClient(), (client, event) -> {
                    log.info("zkpath changes {},{}",interfacePath,event);
                    updateNodes(interfacePath);

                }).build();
        cache.listenable().addListener(listener);
        cache.start();
    }
}
