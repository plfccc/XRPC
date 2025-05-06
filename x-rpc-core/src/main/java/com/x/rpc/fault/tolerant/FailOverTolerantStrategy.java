package com.x.rpc.fault.tolerant;

import com.x.rpc.model.RpcResponse;

import java.util.Map;

/**
 * @author lingpfeng
 * @date 2025/05/06 17:44:46
 * @description TODO
 */
public class FailOverTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // todo 可自行扩展，获取其他服务节点并调用
        return null;
    }
}
