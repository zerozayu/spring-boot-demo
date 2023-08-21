package com.zhangyu.springcloudstreamdemo.binder;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * 自定义消息通道
 *
 * @author zhangyu
 * @date 2023/8/18 11:17
 */
public interface LogStreamBinder {

    String DB_CONFIG_TOPIC = "dbConfigTopic";

    String OPERATE_LOG_TOPIC = "operateLogTopic";

    /**
     * 表示了输入消息通道的名称，
     * 同时我们还定义了一个方法返回一个SubscribableChannel对象，该对象用来维护消息通道订阅者
     *
     * @return
     */
    @Input(DB_CONFIG_TOPIC)
    SubscribableChannel dbConfig();

    /**
     * 描述了输出消息通道的名称，
     * s然后这里我们也定义了一个返回MessageChannel对象的方法，该对象中有一个向消息通道发送消息的方法
     *
     * @return
     */
    @Output(OPERATE_LOG_TOPIC)
    MessageChannel operateLog();
}
