package com.zhangyu.juc.basics;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 循环栅栏测试
 *
 * @author zhangyu
 * @date 2023/2/6 15:50
 */
@Slf4j
public class CyclicBarrierTest {

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(2);
        CyclicBarrier barrier = new CyclicBarrier(2, () -> {
            log.debug("task1 task2 finished...");
        });

        for (int i = 0; i < 3; ++i) {
            service.submit(() -> {
                log.debug("task1 begin...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    barrier.await();
                } catch (BrokenBarrierException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            service.submit(() -> {
                log.debug("task2 begin...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    barrier.await();
                } catch (BrokenBarrierException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        service.shutdown();
    }
}
