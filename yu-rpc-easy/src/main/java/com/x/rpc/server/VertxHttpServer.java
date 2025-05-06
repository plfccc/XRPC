package com.x.rpc.server;

import io.vertx.core.Vertx;

/**
 * @author lingpfeng
 * @date 2025/04/26 20:13:53
 * @description 基于vertx的http服务器
 */
public class VertxHttpServer implements HttpServer{
    @Override
    public void doStart(int port) {
        //创建Vert.x实例
        Vertx vertx = Vertx.vertx();
        //创建HttpServer实例
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        //设置请求处理器
        server.requestHandler(new HttpServerHandler());

        //启动服务器
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Server is now listening on port " + port);
            } else {
                System.out.println("Failed to start server!");
            }
        });

    }
}
