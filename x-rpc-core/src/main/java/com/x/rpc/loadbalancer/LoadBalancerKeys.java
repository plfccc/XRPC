package com.x.rpc.loadbalancer;

/**
 * @author lingpfeng
 * @date 2025/05/05 17:25:36
 * @description 负载均衡器键名常量
 */
public interface LoadBalancerKeys {

    String ROUND_ROBIN = "roundRobin";

    String RANDOM  = "random";

    String CONSISTENT_HASH = "consistentHash";

}
