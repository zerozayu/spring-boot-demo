package com.zhangyu.juc.pattern.Balking;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * 同步模式-犹豫模式
 * 用在一个线程发现另一个线程或本线程已经做了某一件相同的是,那么本线程就无需再做了,直接结束返回
 *
 * @author zhangyu
 * @date 2023/1/28 10:44
 */
@Slf4j(topic = "犹豫模式Balking")
public class Balking1 {

    private Thread monitor;
    private volatile boolean stop = false;
    private boolean starting = false;

    // 启动监控线程
    public void start() {
        // 犹豫模式
        synchronized (this) {
            if (starting) {
                return;
            }
            starting = true; 
        }

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
        }, "monitor");

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
        Balking1 balking1 = new Balking1();
        balking1.start();
        balking1.start();


        Thread.sleep(10000);
        balking1.stop();
    }
}
