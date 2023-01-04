package com.zhangyu.grpc.news.service;

import com.zhangyu.grpc.news.proto.NewsProto;
import com.zhangyu.grpc.news.proto.NewsServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.apache.http.client.utils.DateUtils;

/**
 * @author zhangyu
 * @date 2023/1/3 16:24
 */
public class NewsService extends NewsServiceGrpc.NewsServiceImplBase {
    @Override
    public void list(NewsProto.NewsRequest request, StreamObserver<NewsProto.NewsResponse> responseObserver) {
        String date = request.getDate();
        NewsProto.NewsResponse newList = null;
        try {
            NewsProto.NewsResponse.Builder builder = NewsProto.NewsResponse.newBuilder();
            for (int i = 0; i < 100; ++i) {
                NewsProto.News news = NewsProto.News.newBuilder()
                        .setId(i)
                        .setTitle("新闻标题" + i)
                        .setContent("新闻内容" + i)
                        .setCreateTime(DateUtils.parseDate(date, new String[]{"yyyyMMdd"}).getTime())
                        .build();
                builder.addNews(news);
            }
            newList = builder.build();
        } catch (Exception e) {
            responseObserver.onError(e);
        } finally {
            responseObserver.onNext(newList);
        }
        responseObserver.onCompleted();
    }
}
