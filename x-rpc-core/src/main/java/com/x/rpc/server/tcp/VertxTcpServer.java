package com.x.rpc.server.tcp;

import com.x.rpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;


/**
 * @author lingpfeng
 * @date 2025/04/29 13:55:13
 * @description tcp服务器
 */
public class VertxTcpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        io.vertx.core.net.NetServer server = vertx.createNetServer();
        // 使用TcpServerHandler处理连接和请求
        server.connectHandler(new TcpServerHandler());
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("TCP Server is now listening on port " + port);
            }else {
                System.out.println("Failed to start TCP Server:"+result.cause());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8080);
    }
}
