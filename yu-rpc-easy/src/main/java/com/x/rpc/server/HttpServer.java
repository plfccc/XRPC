package com.x.rpc.server;

/**
 * @author lingpfeng
 * @date 2025/04/26 20:11:31
 * @description 定义统一的启动服务器方法,便于后续扩展
 */
public interface HttpServer {
    void doStart(int port);
}
