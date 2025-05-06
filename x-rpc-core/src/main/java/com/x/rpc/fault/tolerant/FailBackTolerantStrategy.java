package com.x.rpc.fault.tolerant;

import com.x.rpc.model.RpcResponse;

import java.util.Map;

/**
 * @author lingpfeng
 * @date 2025/05/06 17:44:25
 * @description TODO
 */
public class FailBackTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // todo 可自行扩展，获取降级的服务并调用
        return null;
    }
}
