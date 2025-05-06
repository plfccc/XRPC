package com.x;

import com.x.rpc.config.RpcConfig;
import com.x.rpc.utils.ConfigUtils;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws InterruptedException {
        RpcConfig rpcConfig = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpcConfig);
        Thread.sleep(10000);
        System.out.println(rpcConfig);
    }
}
