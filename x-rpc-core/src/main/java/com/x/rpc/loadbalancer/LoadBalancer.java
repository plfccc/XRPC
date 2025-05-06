package com.x.rpc.loadbalancer;

import com.x.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * @author lingpfeng
 * @date 2025/05/05 09:28:33
 * @description  负载均衡器(消费端使用)
 */
public interface LoadBalancer {
    /**
     * 选择服务调用
     * @param requestParams 请求参数
     * @param serviceMetaInfoList 可用的服务列表
     * @return
     */
    public ServiceMetaInfo select(Map<String,Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
