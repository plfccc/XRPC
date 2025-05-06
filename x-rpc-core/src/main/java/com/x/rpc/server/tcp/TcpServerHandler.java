package com.x.rpc.server.tcp;

import com.x.rpc.model.RpcRequest;
import com.x.rpc.model.RpcResponse;
import com.x.rpc.protocol.ProtocolMessage;
import com.x.rpc.protocol.ProtocolMessageDecoder;
import com.x.rpc.protocol.ProtocolMessageEncoder;
import com.x.rpc.protocol.ProtocolMessageTypeEnum;
import com.x.rpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author lingpfeng
 * @date 2025/04/29 16:12:07
 * @description tcp请求处理器
 */
public class TcpServerHandler implements Handler<NetSocket> {

    @Override
    public void handle(NetSocket netSocket) {
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            // 接受请求 解码
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            }catch (IOException e){
                throw new RuntimeException("协议消息解码错误");
            }
            RpcRequest request = protocolMessage.getBody();
            //处理请求 构造响应结果对象
            RpcResponse response = new RpcResponse();
            try {
                // 得到实现类
                Class<?> implClass = LocalRegistry.get(request.getServiceName());
                // 反射调用
                Method method = implClass.getMethod(request.getMethodName(), request.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), request.getArgs());
                // 封装响应结果
                response.setData(result);
                response.setDataType(method.getReturnType());
                response.setMessage("ok");
            }catch (Exception e){
                e.printStackTrace();
                response.setMessage(e.getMessage());
                response.setException(e);
            }
            //编码 发送响应
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, response);
            try {
                netSocket.write(ProtocolMessageEncoder.encode(responseProtocolMessage));
            }catch (Exception e){
                throw new RuntimeException("协议消息编码错误");
            }
        });

        //处理连接
        netSocket.handler(bufferHandlerWrapper);
    }
}
