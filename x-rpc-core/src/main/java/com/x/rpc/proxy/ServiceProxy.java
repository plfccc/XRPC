package com.x.rpc.proxy;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.x.rpc.RpcApplication;
import com.x.rpc.config.RpcConfig;
import com.x.rpc.constant.RpcConstant;
import com.x.rpc.fault.retry.RetryStrategy;
import com.x.rpc.fault.retry.RetryStrategyFactory;
import com.x.rpc.fault.tolerant.TolerantStrategy;
import com.x.rpc.fault.tolerant.TolerantStrategyFactory;
import com.x.rpc.loadbalancer.LoadBalancer;
import com.x.rpc.loadbalancer.LoadBalancerFactory;
import com.x.rpc.model.RpcRequest;
import com.x.rpc.model.RpcResponse;
import com.x.rpc.model.ServiceMetaInfo;
import com.x.rpc.protocol.*;
import com.x.rpc.registry.Registry;
import com.x.rpc.registry.RegistryFactory;
import com.x.rpc.serializer.JdkSerializer;
import com.x.rpc.serializer.Serializer;
import com.x.rpc.serializer.SerializerFactory;
import com.x.rpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;


import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 服务代理（JDK 动态代理）
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {


        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        RpcConfig rpcConfig = null;
        RpcResponse rpcResponse = null;

        try {
//             发送请求
//            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
//            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
//            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
//            serviceMetaInfo.setServiceName(rpcRequest.getServiceName());
//            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
//            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
//            if (CollUtil.isEmpty(serviceMetaInfoList)) {
//                throw new RuntimeException("暂无服务地址");
//            }
//
//            // 负载均衡
//            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
//            // 将调用方法名用作负载均衡参数
//            Map<String,Object> requestParams = new HashMap<>();
//            requestParams.put("methodName",rpcRequest.getMethodName());
//            loadBalancer.select(requestParams,serviceMetaInfoList);


            // 从配置中获取服务信息(用于本地测试)
            rpcConfig = RpcApplication.getRpcConfig();
            ServiceMetaInfo selectedServiceMetaInfo = new ServiceMetaInfo();
            selectedServiceMetaInfo.setServiceName(rpcRequest.getServiceName());
            selectedServiceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            selectedServiceMetaInfo.setServicePort(rpcConfig.getServerPort());
            System.out.println("发送RPC请求到：" + selectedServiceMetaInfo.getServiceHost() + ":" + selectedServiceMetaInfo.getServicePort());
            // 发送TCP请求(使用重试机制)
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            rpcResponse = retryStrategy.doRetry(() -> {
                return VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);
            });

            return rpcResponse.getData();
//            try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
//                    .body(bodyBytes)
//                    .execute()) {
//                byte[] result = httpResponse.bodyBytes();
//                // 反序列化
//                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
//                return rpcResponse.getData();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        } catch (Exception e) {
            // 容错机制
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
            rpcResponse = tolerantStrategy.doTolerant(null, e);
        }
        return rpcResponse.getData();
    }
}
