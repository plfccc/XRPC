package com.x.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lingpfeng
 * @date 2025/04/27 07:48:11
 * @description RPC响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RpcResponse implements Serializable {
    /**
     * 响应数据
     */
    private Object data;

    /**
     * 响应数据类型(预留)
     */
    private Class<?> dataType;

    /**
     * 相应信息
     */
    private String message;
    /**
     * 异常信息
     */
    private Exception exception;
}
