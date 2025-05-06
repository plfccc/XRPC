package com.x.rpc.config;

import com.x.rpc.fault.retry.RetryStrategyKeys;
import com.x.rpc.fault.tolerant.TolerantStrategyKeys;
import com.x.rpc.loadbalancer.LoadBalancer;
import com.x.rpc.loadbalancer.LoadBalancerKeys;
import com.x.rpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC 框架配置
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name = "x-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort = 8080;

    /**
     * 模拟调用
     * 用于测试，不真实发送请求，直接返回结果
     */
    private boolean mock = false;

    private String serializer = SerializerKeys.JDK;

    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.NO_RETRY;

    /**
     * 容错策略
     */
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

}
