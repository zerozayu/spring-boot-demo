package com.zhangyu.grpc.sms;

import com.zhangyu.grpc.sms.proto.SmsProto;
import com.zhangyu.grpc.sms.proto.SmsServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

/**
 * @author zhangyu
 * @date 2023/1/4 16:48
 */
public class SmsClient1 {

    private SmsServiceGrpc.SmsServiceStub asyncStub = null;
    public static final String host = "localhost";
    public static final int port = 9999;

    /**
     * 监听服务器返回的响应
     */
    StreamObserver<SmsProto.PhoneNumberResponse> responseObserver = new StreamObserver<>() {
        @Override
        public void onNext(SmsProto.PhoneNumberResponse phoneNumberResponse) {
            System.out.println("111" + phoneNumberResponse.getResult());
        }

        @Override
        public void onError(Throwable throwable) {
            throwable.printStackTrace();
        }

        @Override
        public void onCompleted() {
            System.out.println("处理完毕");
        }
    };

    public static void main(String[] args) throws InterruptedException {
        SmsClient1 client = new SmsClient1();
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        try {
            client.asyncStub = SmsServiceGrpc.newStub(channel);
            client.createPhone();
        } finally {
            channel.shutdown();
        }
    }

    public void createPhone() throws InterruptedException {
        StreamObserver<SmsProto.PhoneNumberRequest> requestObserver = asyncStub.createPhone(responseObserver);

        try {
            for (int i = 0; i < 3; ++i) {
                SmsProto.PhoneNumberRequest request = SmsProto.PhoneNumberRequest.newBuilder().setPhoneNumber("1788888888" + i).build();
                requestObserver.onNext(request);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            // 通知服务器所有的请求已经发完了
            requestObserver.onCompleted();

            // 因为是异步,所以必须休眠
            Thread.sleep(1000);
        }


    }
}
