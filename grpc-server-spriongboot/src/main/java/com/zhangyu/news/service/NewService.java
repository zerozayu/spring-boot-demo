package com.zhangyu.news.service;

import com.zhangyu.news.proto.NewsProto;
import com.zhangyu.news.proto.NewsServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * @author zhangyu
 * @date 2023/1/5 09:38
 */
@GrpcService()
public class NewService extends NewsServiceGrpc.NewsServiceImplBase {
    @Override
    public void list(NewsProto.NewsRequest request, StreamObserver<NewsProto.NewsResponse> responseObserver) {
        String date = request.getDate();
        NewsProto.NewsResponse response = NewsProto.NewsResponse.newBuilder().setTitle("新闻-" + date).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
