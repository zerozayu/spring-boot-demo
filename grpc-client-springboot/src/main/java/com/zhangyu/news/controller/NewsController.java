package com.zhangyu.news.controller;

import com.zhangyu.news.proto.NewsProto;
import com.zhangyu.news.proto.NewsServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangyu
 * @date 2023/1/5 10:49
 */
@RestController
@RequestMapping("/grpc")
public class NewsController {
    @GrpcClient("grpc-server")
    private NewsServiceGrpc.NewsServiceBlockingStub blockingStub;

    @GetMapping("/news/list")
    public String listNews(@RequestParam(value = "date") String date) {

        NewsProto.NewsResponse response = blockingStub.list(NewsProto.NewsRequest.newBuilder().setDate(date).build());

        return response.getTitle();
    }
}
