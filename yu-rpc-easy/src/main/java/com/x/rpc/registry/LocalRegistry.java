package com.x.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lingpfeng
 * @date 2025/04/26 21:14:29
 * @description 本地的注册中心
 */
public class LocalRegistry {

    /**
     * 本地注册信息
     */
    public static final Map<String, Class<?>> localRegistry = new ConcurrentHashMap<>();

    /**
     * 注册服务
     * @param serviceName
     * @param implClass
     */
    public static void register(String serviceName, Class<?> implClass) {
        localRegistry.put(serviceName, implClass);
    }

    /**
     * 获取服务
     * @param serviceName
     * @return
     */
    public static Class<?> get(String serviceName) {
        return localRegistry.get(serviceName);
    }
    /**
     * 移除服务
     * @param serviceName
     */
    public static void remove(String serviceName) {
        localRegistry.remove(serviceName);
    }

}
