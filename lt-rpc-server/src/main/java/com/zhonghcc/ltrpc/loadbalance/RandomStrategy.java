package com.zhonghcc.ltrpc.loadbalance;

import com.zhonghcc.ltrpc.register.LtRpcNode;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomStrategy implements LoadBalanceStrategy{
    @Override
    public LtRpcNode choseNode(List<LtRpcNode> ltRpcNodes) {
        if(ltRpcNodes==null || ltRpcNodes.size()==0){
            return null;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int length = ltRpcNodes.size();
        int index = random.nextInt(length);
        return ltRpcNodes.get(index);

    }

//    public static void main(String[] args){
//        ThreadLocalRandom random = ThreadLocalRandom.current();
//        int length = 1;
//        int index = random.nextInt(length);
//        System.out.println(index);
//    }
}
