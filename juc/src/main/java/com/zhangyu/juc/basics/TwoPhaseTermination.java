package com.zhangyu.juc.basics;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 两阶段终止模式
 * <br>
 * <img src="../../../../../resources/static/两阶段终止.png" />
 * @author zhangyu
 * @date 2023/1/10 14:15
 */
@Slf4j(topic = "c.TwoPhaseTermination")
public class TwoPhaseTermination {

    private Thread monitor;

    // 启动监控线程
    public void start() {
        monitor = new Thread(() -> {
            while (true) {
                Thread current = Thread.currentThread();
                if (current.isInterrupted()) {
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
                    // 因为 sleep 出现异常后,会清除打断标记(置为 false)
                    // 重新设置打断标志
                    current.interrupt();
                }
            }
        });

        monitor.start();
    }

    // 停止监控线程
    public void stop() {
        monitor.interrupt();
    }
}

class Main {
    public static void main(String[] args) throws InterruptedException {
        TwoPhaseTermination twoPhaseTermination = new TwoPhaseTermination();
        twoPhaseTermination.start();
        Thread.sleep(7000);
        twoPhaseTermination.stop();
    }
}
