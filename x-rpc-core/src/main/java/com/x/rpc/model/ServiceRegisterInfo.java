package com.x.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lingpfeng
 * @date 2025/05/07 08:24:55
 * @description 服务注册信息实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRegisterInfo<T> {
    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 实现类
     */
    private Class<? extends T> implClass;

}
