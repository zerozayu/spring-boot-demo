package com.zhangyu.juc.pattern.TwoPhaseTermination;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * 两阶段终止模式-使用 volatile 优化
 *
 * @author zhangyu
 * @date 2023/1/28 10:28
 */
@Slf4j(topic = "两阶段终止-volatile")
public class TwoPhaseTermination2 {

    private Thread monitor;
    private volatile static boolean stop = false;

    // 启动监控线程
    public void start() {
        monitor = new Thread(() -> {
            while (true) {
                if (stop) {
                    log.debug("料理后事...");
                    break;
                }

                try {
                    // 情况一,睡眠时发生异常
                    TimeUnit.SECONDS.sleep(2);
                    // 情况二,执行监控记录时被打断,打断标志正常设置为 true
                    log.debug("执行监控记录...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        monitor.start();
    }

    // 停止监控线程
    public void stop() {
        stop = true;
        // 可以同时结合使用 interrupt,打断 sleep,使其不执行过多的 sleep 时间
        monitor.interrupt();
    }

    @Test
    public void test() throws InterruptedException {
        TwoPhaseTermination2 twoPhaseTermination2 = new TwoPhaseTermination2();
        twoPhaseTermination2.start();
        Thread.sleep(7000);
        twoPhaseTermination2.stop();
    }
}
