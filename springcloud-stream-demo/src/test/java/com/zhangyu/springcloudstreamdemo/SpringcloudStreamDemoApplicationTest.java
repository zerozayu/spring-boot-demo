package com.zhangyu.springcloudstreamdemo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author zhangyu
 * @date 2023/8/18 14:56
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SpringcloudStreamDemoApplication.class, SpringcloudStreamDemoApplicationTest.class})
@EnableBinding({Processor.class})
class SpringcloudStreamDemoApplicationTest {

    @Autowired
    private Processor source;//注入接口和注入MessageChannel的区别在于发送时需不需要调用接口内的方法

    @Test
    public void testPublish() {
        source.input().send(MessageBuilder.withPayload("Message from MyPipe.").build());

        //假设注入了MessageChannel messageChannel; 因为绑定的是Source这个接口，
        //所以会使用其中的唯一产生MessageChannel的方法，那么下边的代码会是
        //messageChannel.send(MessageBuilder.withPayload("Message from MyPipe").build());
    }
}