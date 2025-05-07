package com.x.rpc.bootstrap;

import com.x.rpc.RpcApplication;
import com.x.rpc.config.RegistryConfig;
import com.x.rpc.config.RpcConfig;
import com.x.rpc.model.ServiceMetaInfo;
import com.x.rpc.model.ServiceRegisterInfo;
import com.x.rpc.registry.LocalRegistry;
import com.x.rpc.registry.Registry;
import com.x.rpc.registry.RegistryFactory;
import com.x.rpc.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * @author lingpfeng
 * @date 2025/05/07 08:28:14
 * @description 服务提供者启动类
 */
public class ProviderBootstrap {

    /**
     * 初始化
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        // RPC 框架初始化（配置和注册中心）
        RpcApplication.init();
        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            // 本地注册
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            // 注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " 服务注册失败", e);
            }
        }

        // 启动服务器
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}
