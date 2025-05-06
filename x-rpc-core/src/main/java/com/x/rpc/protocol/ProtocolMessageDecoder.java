package com.x.rpc.protocol;

import com.x.rpc.model.RpcRequest;
import com.x.rpc.model.RpcResponse;
import com.x.rpc.serializer.Serializer;
import com.x.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * @author lingpfeng
 * @date 2025/04/29 15:21:52
 * @description 协议解码器
 */
public class ProtocolMessageDecoder {
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        // 解析协议消息
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        //校验魔术信息
        byte magic = buffer.getByte(0);
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("Invalid protocol magic");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));
        //解决粘包问题只读取指定长度的数据
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());
        //得到序列化器
        ProtocolMessageSerializerEnum protocolMessageSerializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (protocolMessageSerializerEnum==null) {
            throw new RuntimeException("Invalid protocol serializer");
        }
        Serializer serializer = SerializerFactory.getInstance(protocolMessageSerializerEnum.getValue());
        // 根据消息的类型进行反序列化
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if (messageTypeEnum==null) {
            throw new RuntimeException("Invalid protocol message type");
        }
        switch (messageTypeEnum){
            case REQUEST:
                 RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
                 return new ProtocolMessage<>(header,request);
            case RESPONSE:
                RpcResponse response = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header,response);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("Invalid protocol message type");
        }


    }
}
