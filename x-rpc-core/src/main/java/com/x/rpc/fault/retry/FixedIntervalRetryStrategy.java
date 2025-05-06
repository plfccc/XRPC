package com.x.rpc.fault.retry;

import com.github.rholder.retry.*;
import com.x.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author lingpfeng
 * @date 2025/05/06 12:55:39
 * @description 固定重试策略
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener(){
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        // 重试次数
                        log.info("重试次数:{}",attempt.getAttemptNumber());
                    }
                }).build();
        return retryer.call(callable);
    }
}
