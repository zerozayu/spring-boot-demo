package com.zhangyu.grpc.sms.service;

import com.google.protobuf.ProtocolStringList;
import com.zhangyu.grpc.sms.proto.SmsProto;
import com.zhangyu.grpc.sms.proto.SmsServiceGrpc;
import io.grpc.stub.StreamObserver;

/**
 * @author zhangyu
 * @date 2023/1/4 15:14
 */
public class SmsService extends SmsServiceGrpc.SmsServiceImplBase {
    @Override
    public void sendSms(SmsProto.SmsRequest request, StreamObserver<SmsProto.SmsResponse> responseObserver) {
        try {
            String content = request.getContent();
            ProtocolStringList phoneNumberList = request.getPhoneNumberList();
            for (String phoneNumber : phoneNumberList) {
                SmsProto.SmsResponse response = SmsProto.SmsResponse.newBuilder()
                        .setResult(content + "-" + phoneNumber + "信息已发送").build();
                responseObserver.onNext(response);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            responseObserver.onCompleted();
        }

    }

    @Override
    public StreamObserver<SmsProto.PhoneNumberRequest> createPhone(StreamObserver<SmsProto.PhoneNumberResponse> responseObserver) {

        return new StreamObserver<>() {
            int i = 0;

            @Override
            public void onNext(SmsProto.PhoneNumberRequest phoneNumberRequest) {
                System.out.println(phoneNumberRequest.getPhoneNumber() + " already submit.");
                ++i;
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(SmsProto.PhoneNumberResponse.newBuilder().setResult("一共导入了" + i + "条手机号码").build());
                responseObserver.onCompleted();
            }
        };

    }
}
