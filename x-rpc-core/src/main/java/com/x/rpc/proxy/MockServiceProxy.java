package com.x.rpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author lingpfeng
 * @date 2025/04/28 07:28:27
 * @description Mock 服务代理(jdk动态代理)
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 根据方法的返回值类型生成特定的默认值对象
        Class<?> returnType = method.getReturnType();
        log.info("mock service invoke:{}", method.getName());
        return getDefaultObject(returnType);
    }

    /**
     * 根据方法的返回值类型生成特定的默认值对象
     *
     * @param returnType
     * @return
     */
    private Object getDefaultObject(Class<?> returnType) {
        if (returnType.isPrimitive()) {
            // 基本类型的默认值
            if (returnType == boolean.class) {
                return false;
            } else if (returnType == byte.class) {
                return (byte) 0;
            } else if (returnType == short.class) {
                return (short) 0;
            } else if (returnType == int.class) {
                return 0;
            } else if (returnType == long.class) {
                return 0L;
            } else if (returnType == float.class) {
                return 0.0f;
            } else if (returnType == double.class) {
                return 0.0d;
            } else if (returnType == char.class) {
                return '\u0000';
            }
        }
        //对象类型的默认值
        return null;
    }
}
