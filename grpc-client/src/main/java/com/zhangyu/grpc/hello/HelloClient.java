package com.zhangyu.grpc.hello;

import com.zhangyu.grpc.hello.proto.HelloProto;
import com.zhangyu.grpc.hello.proto.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * @author zhangyu
 * @date 2023/1/4 14:41
 */
public class HelloClient {
    public static final String host = "localhost";
    public static final int port = 9999;

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, 9999).usePlaintext().build();
        try {
            HelloServiceGrpc.HelloServiceBlockingStub blockingStub = HelloServiceGrpc.newBlockingStub(channel);
            HelloProto.HelloRequest zhangyu = HelloProto.HelloRequest.newBuilder().setName("lisi").build();
            HelloProto.HelloResponse response = blockingStub.sayHello(zhangyu);

            System.out.println(response.getResult());
        } finally {
            channel.shutdown();
        }

    }
}
