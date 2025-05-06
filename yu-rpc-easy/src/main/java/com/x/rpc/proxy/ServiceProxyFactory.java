package com.x.rpc.proxy;

import java.lang.reflect.Proxy;

/**
 * @author lingpfeng
 * @date 2025/04/27 17:04:36
 * @description  服务代理工厂
 */
public class ServiceProxyFactory {
    public static <T> T getProxy(Class<T> serviceClass){
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()
        );
    }
}
