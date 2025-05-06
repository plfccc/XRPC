package com.x.rpc.loadbalancer;

import com.x.rpc.spi.SpiLoader;

/**
 * @author lingpfeng
 * @date 2025/05/05 17:29:17
 * @description 负载均衡器工厂类
 */
public class LoadBalancerFactory  {
    static {
        SpiLoader.load(LoadBalancer.class);
    }
    // 默认的负载均衡器
    private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadbalancer();

    public static LoadBalancer getInstance(String key){
        return SpiLoader.getInstance(LoadBalancer.class,key);
    }

}
