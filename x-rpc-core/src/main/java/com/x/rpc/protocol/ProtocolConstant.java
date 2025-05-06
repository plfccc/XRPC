package com.x.rpc.protocol;

/**
 * @author lingpfeng
 * @date 2025/04/29 10:55:18
 * @description 协议相关常量
 */
public interface ProtocolConstant {

    /**
     * 消息头长度
     */
    int MESSAGE_HEADER_LENGTH = 17;

    /**
     * 协议魔数
     */
    byte PROTOCOL_MAGIC = 0x1;

    /**
     * 协议版本号
     */
    byte PROTOCOL_VERSION = 0x1;
}