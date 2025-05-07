package com.x.xrpc.springboot.starter.bootstrap;

import com.x.rpc.RpcApplication;
import com.x.rpc.config.RpcConfig;
import com.x.rpc.server.tcp.VertxTcpServer;
import com.x.xrpc.springboot.starter.annotation.EnableRpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author lingpfeng
 * @date 2025/05/07 10:22:38
 * @description Rpc初始化启动类，用于启动RPC框架
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {
    /**
     * Spring 初始化时执行，初始化 RPC 框架
     * implements ImportBeanDefinitionRegistrar: 这是关键。当一个配置类（通常是带有 @Configuration 注解的类）通过 @Import(RpcInitBootstrap.class) 导入这个类时，
     * Spring 容器在处理配置类时会调用 ImportBeanDefinitionRegistrar 接口的 registerBeanDefinitions 方法。这提供了一个在常规 Bean 定义加载之后，
     * 但在 Bean 实例化之前，以编程方式注册更多 Bean 定义或执行其他初始化逻辑的机会。
     *
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取 EnableRpc 注解的属性值
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName())
                .get("needServer");

        // RPC 框架初始化（配置和注册中心）
        RpcApplication.init();

        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 启动服务器
        if (needServer) {
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());
        } else {
            log.info("不启动 server");
        }

    }
}
