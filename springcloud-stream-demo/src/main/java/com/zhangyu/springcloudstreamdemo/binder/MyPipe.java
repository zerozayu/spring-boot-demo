package com.zhangyu.springcloudstreamdemo.binder;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 自定义管道
 *
 * @author zhangyu
 * @date 2023/8/18 14:48
 */
public interface MyPipe {

    //这里使用Processor.OUTPUT是因为要同一个管道，或者名称相同
    // @Input(Processor.OUTPUT)
    // SubscribableChannel input();

    //还可以如下这样=====二选一即可==========
    //方法2
    String INPUT = "output";
    @Input(MyPipe.INPUT)
    SubscribableChannel input();
}
