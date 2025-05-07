package com.x.xrpc.springboot.starter.annotation;

import com.x.xrpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import com.x.xrpc.springboot.starter.bootstrap.RpcInitBootstrap;
import com.x.xrpc.springboot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lingpfeng
 * @date 2025/05/07 09:31:41
 * @description 启用RPC注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcConsumerBootstrap.class, RpcProviderBootstrap.class})
public @interface EnableRpc {
    /**
     * 需要启动server
     * @return
     */
    boolean needServer() default true;
}
