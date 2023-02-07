package com.zhangyu.juc.basics;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Semaphore;

/**
 * 信号量测试
 *
 * @author zhangyu
 * @date 2023/2/6 14:57
 */
@Slf4j
public class SemaphoreTest {

    @Test
    public void test() throws InterruptedException {
        Semaphore semaphore = new Semaphore(3);

        for (int i = 0; i < 10; ++i) {
            new Thread(() -> {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    log.debug("running...");
                    Thread.sleep(1000);
                    log.debug("end...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }
            }).start();
        }

        Thread.sleep(10000);
    }
}
