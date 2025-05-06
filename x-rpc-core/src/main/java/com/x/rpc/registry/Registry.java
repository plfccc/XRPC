package com.x.rpc.registry;



import com.x.rpc.config.RegistryConfig;
import com.x.rpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心
 *
 */
public interface Registry {



    /**
     * 初始化
     *
     * @param registryConfig
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务（服务端）
     *
     * @param serviceMetaInfo
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 注销服务（服务端）
     *
     * @param serviceMetaInfo
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo);

    /**
     * 服务发现（获取某服务的所有节点，消费端）
     *
     * @param serviceKey 服务键名
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 服务销毁
     */
    void destroy();

    /**
     * 心跳检测(服务提供方)
     */
    void heartBeat();

    /**
     * 监听（服务消费方）
     * @param serviceKey
     */
    void watch(String serviceKey);
}
