package com.x.rpc.proxy;

import com.x.rpc.RpcApplication;

import java.lang.reflect.Proxy;

/**
 * @author lingpfeng
 * @date 2025/04/27 17:04:36
 * @description  服务代理工厂
 */
public class ServiceProxyFactory {
    /**
     * 根据服务类型获取代理对象
     * @param serviceClass
     * @return
     * @param <T>
     */
    public static <T> T getProxy(Class<T> serviceClass){
        if (RpcApplication.getRpcConfig().isMock()){
            return getMockProxy(serviceClass);
        }

        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()
        );
    }

    private static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockServiceProxy()
        );
    }

}
