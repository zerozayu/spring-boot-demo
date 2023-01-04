package com.zhangyu.grpc.sms;

import com.zhangyu.grpc.sms.proto.SmsProto;
import com.zhangyu.grpc.sms.proto.SmsServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Iterator;

/**
 * @author zhangyu
 * @date 2023/1/4 15:37
 */
public class SmsClient {
    public static final String host = "localhost";
    public static int port = 9999;

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

        try {
            SmsServiceGrpc.SmsServiceBlockingStub blockingStub = SmsServiceGrpc.newBlockingStub(channel);
            SmsProto.SmsRequest request = SmsProto.SmsRequest.newBuilder()
                    .setContent("短信1")
                    .addPhoneNumber("17888888888")
                    .addPhoneNumber("17888888889")
                    .addPhoneNumber("17888888810")
                    .addPhoneNumber("17888888811")
                    .addPhoneNumber("17888888812")
                    .addPhoneNumber("17888888813")
                    .addPhoneNumber("17888888814")
                    .build();

            Iterator<SmsProto.SmsResponse> smsResponseIterator = blockingStub.sendSms(request);

            while (smsResponseIterator.hasNext()) {
                SmsProto.SmsResponse next = smsResponseIterator.next();
                System.out.println(next.getResult());
            }
        } finally {
            channel.shutdown();
        }
    }
}
