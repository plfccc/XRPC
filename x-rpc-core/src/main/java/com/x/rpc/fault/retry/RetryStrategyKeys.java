package com.x.rpc.fault.retry;

/**
 * @author lingpfeng
 * @date 2025/05/06 13:26:22
 * @description 重试策略键名常量
 */
public interface RetryStrategyKeys {

    String NO_RETRY = "noRetry";

    String FIXED_INTERVAL_RETRY = "fixedIntervalRetry";
}
