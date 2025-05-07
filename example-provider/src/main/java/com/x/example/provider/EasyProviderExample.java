package com.x.example.provider;

import com.x.example.common.service.UserService;
import com.x.rpc.RpcApplication;
import com.x.rpc.bootstrap.ProviderBootstrap;
import com.x.rpc.config.RegistryConfig;
import com.x.rpc.config.RpcConfig;
import com.x.rpc.model.ServiceMetaInfo;
import com.x.rpc.model.ServiceRegisterInfo;
import com.x.rpc.registry.LocalRegistry;
import com.x.rpc.registry.Registry;
import com.x.rpc.registry.RegistryFactory;
import com.x.rpc.server.HttpServer;
import com.x.rpc.server.VertxHttpServer;
import com.x.rpc.server.tcp.VertxTcpServer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lingpfeng
 * @date 2025/04/26 19:22:29
 * @description TODO
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        //要注册的服务
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<UserService> userServiceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(userServiceRegisterInfo);
        //初始化
        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
