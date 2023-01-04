package com.zhangyu.grpc.news;

import com.zhangyu.grpc.news.proto.NewsProto;
import com.zhangyu.grpc.news.proto.NewsServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.List;

/**
 * Hello world!
 */
public class GRPCClient {

    public static final String host = "localhost";

    public static int port = 9999;

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        try {

            NewsServiceGrpc.NewsServiceBlockingStub blockingStub = NewsServiceGrpc.newBlockingStub(channel);
            NewsProto.NewsRequest newsRequest = NewsProto.NewsRequest.newBuilder().setDate("20230104").build();

            NewsProto.NewsResponse response = blockingStub.list(newsRequest);

            List<NewsProto.News> newsList = response.getNewsList();
            for (NewsProto.News news : newsList) {
                System.out.println(news.getTitle() + ":" + news.getContent());
                System.out.println(news.getCreateTime());
            }
        } finally {
            channel.shutdown();
        }


    }
}
