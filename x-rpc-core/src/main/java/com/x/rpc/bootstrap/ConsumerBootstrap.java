package com.x.rpc.bootstrap;

import com.x.rpc.RpcApplication;

/**
 * @author lingpfeng
 * @date 2025/05/07 08:59:54
 * @description 消费方服务启动类
 */
public class ConsumerBootstrap {

    public static void init(){
        //RPC框架初始化（配置和注册中心）
        RpcApplication.init();
    }
}
