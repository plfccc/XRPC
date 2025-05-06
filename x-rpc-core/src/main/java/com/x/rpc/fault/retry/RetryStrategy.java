package com.x.rpc.fault.retry;

import com.x.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * @author lingpfeng
 * @date 2025/05/06 11:52:35
 * @description 重试策略
 */
public interface RetryStrategy {
    /**
     * 重试
     * @param callable
     * @return
     * @throws Exception
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
