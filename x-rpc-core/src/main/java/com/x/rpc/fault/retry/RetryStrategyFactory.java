package com.x.rpc.fault.retry;

import com.x.rpc.spi.SpiLoader;

/**
 * @author lingpfeng
 * @date 2025/05/06 13:27:20
 * @description 重试策略工厂(用于获取重试器对象)
 */
public class RetryStrategyFactory {
    static {
        SpiLoader.load(RetryStrategy.class);
    }
    // 默认的重试策略
    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();

    public static RetryStrategy getInstance(String key){
        return SpiLoader.getInstance(RetryStrategy.class,key);
    }
}
