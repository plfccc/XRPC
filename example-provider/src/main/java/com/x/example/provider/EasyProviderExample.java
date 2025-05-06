package com.x.example.provider;

import com.x.example.common.service.UserService;
import com.x.rpc.RpcApplication;
import com.x.rpc.registry.LocalRegistry;
import com.x.rpc.server.HttpServer;
import com.x.rpc.server.VertxHttpServer;
import com.x.rpc.server.tcp.VertxTcpServer;

/**
 * @author lingpfeng
 * @date 2025/04/26 19:22:29
 * @description TODO
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        //rpc框架初始化
        RpcApplication.init();
        //注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        //启动web服务
        HttpServer httpServer = new VertxTcpServer();

        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
