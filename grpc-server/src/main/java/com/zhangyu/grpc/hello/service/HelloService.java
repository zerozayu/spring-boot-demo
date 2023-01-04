package com.zhangyu.grpc.hello.service;

import com.zhangyu.grpc.hello.proto.HelloProto;
import com.zhangyu.grpc.hello.proto.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;

/**
 * @author zhangyu
 * @date 2023/1/4 13:54
 */
public class HelloService extends HelloServiceGrpc.HelloServiceImplBase {
    @Override
    public void sayHello(HelloProto.HelloRequest request, StreamObserver<HelloProto.HelloResponse> responseObserver) {
        try {
            String name = request.getName();
            HelloProto.HelloResponse response = HelloProto.HelloResponse.newBuilder().setResult("Hi, " + name).build();
            responseObserver.onNext(response);
        } finally {
            responseObserver.onCompleted();
        }

    }
}
