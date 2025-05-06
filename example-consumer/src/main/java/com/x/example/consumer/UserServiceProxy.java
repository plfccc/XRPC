package com.x.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.x.example.common.model.User;
import com.x.example.common.service.UserService;
import com.x.rpc.model.RpcRequest;
import com.x.rpc.model.RpcResponse;
import com.x.rpc.serializer.JdkSerializer;
import com.x.rpc.serializer.Serializer;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author lingpfeng
 * @date 2025/04/27 16:38:46
 * @description 静态代理类
 */
public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {
        //指定序列化器
        Serializer serializer = new JdkSerializer();

        //构建请求对象
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();
        try {
            byte[] bytes = serializer.serialize(rpcRequest);
            byte[] result = null;
            //发送请求
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bytes)
                    .execute()){
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }
}
