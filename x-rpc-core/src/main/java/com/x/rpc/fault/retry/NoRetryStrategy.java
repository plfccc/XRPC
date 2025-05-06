package com.x.rpc.fault.retry;

import com.x.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * @author lingpfeng
 * @date 2025/05/06 11:57:19
 * @description  不重试策略
 */
public class NoRetryStrategy implements RetryStrategy {
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
