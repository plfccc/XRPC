package com.x.rpc.loadbalancer;

import com.x.rpc.model.ServiceMetaInfo;

import javax.sound.midi.MetaEventListener;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author lingpfeng
 * @date 2025/05/05 17:00:55
 * @description 一致性哈希负载均衡器(利用treeMap)
 */
public class ConsistentHashLoadBalancer implements LoadBalancer{

    private final TreeMap<Integer,ServiceMetaInfo> virtualNodes = new TreeMap<>();

    private static final int VIRTUAL_NODE_NUM = 100;


    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(serviceMetaInfoList.isEmpty()){
            return null;
        }
        // 构建虚拟节点环
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress()+"#"+i);
                virtualNodes.put(hash,serviceMetaInfo);
            }
        }
        // 获取调用请求的Hash值
        int hash = getHash(requestParams);
        // 选择最接近且大于等于调用请求hash的虚拟节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null){
            // 没有大于等于调用请求Hash值的节点也就是说超过环形了 选择第一个
            entry = virtualNodes.firstEntry();
        }

        return entry.getValue();
    }

    private int getHash(Object key) {
        return key.hashCode();
    }
}
