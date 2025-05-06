package com.x.rpc.protocol;

import com.x.rpc.serializer.Serializer;
import com.x.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

/**
 * @author lingpfeng
 * @date 2025/04/29 14:54:32
 * @description 编码器
 */
public class ProtocolMessageEncoder {

    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws Exception {
        if(protocolMessage == null || protocolMessage.getHeader() == null){
            // 协议消息头为空，直接返回空的Buffer
            return Buffer.buffer();
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();
        //依次向缓冲区中写入各个字段
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());
        //得到序列化器
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if(serializerEnum == null){
            throw new Exception("该序列化器不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        //序列化消息体
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        //写入消息体的长度
        buffer.appendInt(bodyBytes.length);
        //写入消息体
        buffer.appendBytes(bodyBytes);
        return buffer;
    }

}
