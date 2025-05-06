package com.x.rpc.fault.tolerant;

import com.x.rpc.model.RpcResponse;

import java.util.Map;

/**
 * @author lingpfeng
 * @date 2025/05/06 17:43:12
 * @description TODO
 */
public class FailFastTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("服务报错",e);
    }
}
