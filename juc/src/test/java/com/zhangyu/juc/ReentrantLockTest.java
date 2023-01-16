package com.zhangyu.juc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 可重入锁
 *
 * @author zhangyu
 * @date 2023/1/16 09:14
 */
@Slf4j
public class ReentrantLockTest {
    private static ReentrantLock lock = new ReentrantLock();

    @Test
    public void test1(){
        Thread t1 = new Thread(() -> {
            try {
                // 如果没有竞争那么此方法就会获取 lock 对象锁
                // 如果有竞争就进入阻塞队列,可以被其他线程用 interrupt 方法打断
                log.debug("尝试获得锁");
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.debug("没有获得锁,返回");
                return;
            }
            try {
                log.debug("获取到锁");
            }finally {
                lock.unlock();
            }
        }, "t1");

        t1.start();
    }
}
