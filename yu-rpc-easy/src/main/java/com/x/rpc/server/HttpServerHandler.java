package com.x.rpc.server;

import com.x.rpc.model.RpcRequest;
import com.x.rpc.model.RpcResponse;
import com.x.rpc.registry.LocalRegistry;
import com.x.rpc.serializer.JdkSerializer;
import com.x.rpc.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.lang.reflect.Method;

/**
 * @author lingpfeng
 * @date 2025/04/27 07:52:44
 * @description 请求处理器
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest httpServerRequest) {
        //指定序列化器
        Serializer serializer = new JdkSerializer();
        //打印日志
        System.out.println("Received request: " + httpServerRequest.method() + " " + httpServerRequest.uri());
        //异步处理请求
        httpServerRequest.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            }catch (Exception e){
                e.printStackTrace();
            }
            //构建相应结果
            RpcResponse rpcResponse = new RpcResponse();
            if (rpcRequest == null){
                rpcResponse.setMessage("rpcRequest is null");
                doResponse(httpServerRequest,rpcResponse,serializer);
                return;
            }
            //调用服务
            try {
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(),rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(),rpcRequest.getArgs());
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");

            }catch (Exception e){
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                doResponse(httpServerRequest,rpcResponse,serializer);
            }
            //响应
            doResponse(httpServerRequest,rpcResponse,serializer);
        });

    }

    /**
     * 响应
     * @param httpServerRequest
     * @param rpcResponse
     * @param serializer
     */
    private void doResponse(HttpServerRequest httpServerRequest, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse response = httpServerRequest.response()
                .putHeader("content-type", "application/json");
        try {
            byte[] bytes = serializer.serialize(rpcResponse);
            response.end(Buffer.buffer(bytes));
        }catch (Exception e){
            e.printStackTrace();
            response.end(Buffer.buffer());
        }
    }
}
