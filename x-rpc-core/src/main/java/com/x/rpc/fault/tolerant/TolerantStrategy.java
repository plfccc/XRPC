package com.x.rpc.fault.tolerant;

import com.x.rpc.model.RpcResponse;

import java.util.Map;

/**
 * @author lingpfeng
 * @date 2025/05/06 17:42:52
 * @description TODO
 */
public interface TolerantStrategy {
    /**
     * 容错
     *
     * @param context 上下文，用于传递数据
     * @param e       异常
     * @return
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
