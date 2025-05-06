package com.x.rpc;

import com.x.rpc.config.RegistryConfig;
import com.x.rpc.config.RpcConfig;
import com.x.rpc.constant.RpcConstant;
import com.x.rpc.model.ServiceMetaInfo;
import com.x.rpc.registry.Registry;
import com.x.rpc.registry.RegistryFactory;
import com.x.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lingpfeng
 * @date 2025/04/27 21:19:11
 * @description RPC框架应用 相当于holder 用于存放全局配置 双检锁单例模式
 */
@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化，支持传入自定义配置
     * @param config 自定义配置
     */
    public static void init(RpcConfig config)  {
        rpcConfig = config;
        log.info("rpc config: {}", rpcConfig);
        // 初始化注册中心
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        // 正常的逻辑上来说,还需要将服务的接口信息注册到注册中心,但是这里为了方便,就不注册了
//        registry.register(new ServiceMetaInfo());
        log.info("rpc registry init success");

        //创建并注册 Shutdown Hook，JVM 退出时执行操作
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * 初始化
     */
    public static void init()  {
        RpcConfig newConfig ;
        try {
            newConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        }catch (Exception e) {
            log.warn("rpc config init failed, use default config");
            newConfig = new RpcConfig();
        }
        init(newConfig);
    }

    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }

}
