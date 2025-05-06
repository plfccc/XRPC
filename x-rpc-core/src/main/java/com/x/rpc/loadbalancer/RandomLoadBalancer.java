package com.x.rpc.loadbalancer;

import com.x.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author lingpfeng
 * @date 2025/05/05 16:56:28
 * @description 随机的负载均衡器(利用java自带的随机器)
 */
public class RandomLoadBalancer implements LoadBalancer{
    private final Random random = new Random();
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(serviceMetaInfoList.isEmpty()){
            return null;
        }
        if (serviceMetaInfoList.size() == 1){
            return serviceMetaInfoList.get(0);
        }
        int index = random.nextInt(serviceMetaInfoList.size());
        return serviceMetaInfoList.get(index);
    }
}
