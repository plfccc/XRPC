package com.x.rpc.registry;

import com.x.rpc.spi.SpiLoader;

/**
 * @author lingpfeng
 * @date 2025/04/28 20:55:57
 * @description 注册中心工厂 (用于获得注册中心对象)
 */
public class RegistryFactory {
    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认注册中心
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }

}
