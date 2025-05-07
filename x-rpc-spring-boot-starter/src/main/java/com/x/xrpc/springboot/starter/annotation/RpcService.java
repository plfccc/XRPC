package com.x.xrpc.springboot.starter.annotation;

import com.x.rpc.constant.RpcConstant;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lingpfeng
 * @date 2025/05/07 09:32:15
 * @description 服务提供者注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
    /**
     * 服务接口类
     */
    Class<?> interfaceClass() default void.class;
    /**
     * 服务版本
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;
}
