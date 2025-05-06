package com.x.rpc.loadbalancer;

import com.x.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lingpfeng
 * @date 2025/05/05 16:45:20
 * @description 轮询负载均衡器(使用JUC包下的原子整数类保证并发安全)
 */
public class RoundRobinLoadbalancer implements LoadBalancer {
    /**
     * 当前轮询的下标
     */
    private final AtomicInteger currentIndex = new AtomicInteger();
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(serviceMetaInfoList.isEmpty()){
            return null;
        }
        // 只有一个服务直接返回
        if(serviceMetaInfoList.size() == 1){
            return serviceMetaInfoList.get(0);
        }
        // 取模
        int index = currentIndex.getAndIncrement() % serviceMetaInfoList.size();

        return serviceMetaInfoList.get(index);
    }
}
