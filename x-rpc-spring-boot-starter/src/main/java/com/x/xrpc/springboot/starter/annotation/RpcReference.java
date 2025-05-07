package com.x.xrpc.springboot.starter.annotation;

import com.x.rpc.constant.RpcConstant;
import com.x.rpc.fault.retry.RetryStrategyKeys;
import com.x.rpc.fault.tolerant.TolerantStrategyKeys;
import com.x.rpc.loadbalancer.LoadBalancerKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lingpfeng
 * @date 2025/05/07 09:31:58
 * @description 服务消费者注解(用于注入服务)
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {
    /**
     * 服务接口类
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 版本
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 负载均衡器
     */
    String loadBalancer() default LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */
    String retryStrategy() default RetryStrategyKeys.NO_RETRY;

    /**
     * 容错策略
     */
    String tolerantStrategy() default TolerantStrategyKeys.FAIL_FAST;

    /**
     * 模拟调用
     */
    boolean mock() default false;
}
