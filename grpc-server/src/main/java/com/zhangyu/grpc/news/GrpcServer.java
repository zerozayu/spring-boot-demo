package com.zhangyu.grpc.news;

import com.zhangyu.grpc.news.service.NewsService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * @author zhangyu
 * @date 2023/1/3 17:16
 */
public class GrpcServer {
    public static final int port = 9999;

    public static void main(String[] args) throws InterruptedException, IOException {
        Server server = ServerBuilder
                .forPort(port)
                .addService(new NewsService())
                .build()
                .start();
        System.out.println(String.format("gRPC 服务端启动成功,端口号: [%d].", port));
        server.awaitTermination();
    }
}
