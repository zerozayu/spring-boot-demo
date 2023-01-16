package com.zhangyu.juc.pattern;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 同步模式-顺序控制
 *
 * @author zhangyu
 * @date 2023/1/16 11:14
 */
@Slf4j(topic = "sequential")
public class SequentialControl {

    /**
     * wait/notify版
     * 先执行t2,再执行t1
     */
    static boolean t2Runned = false;

    @Test
    public void testWaitNotify() {
        final Object lock = new Object();

        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                while (!t2Runned) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("t1");
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                log.debug("t2");
                t2Runned = true;
                lock.notify();
            }
        }, "t2");

        t1.start();
        t2.start();
    }

    /**
     * 使用 park() 和 unpark() 测试
     */
    @Test
    public void testPark() {
        Thread t1 = new Thread(() -> {
            LockSupport.park();
            log.debug("t1 运行");
        }, "t1");


        Thread t2 = new Thread(() -> {
            log.debug("t2 运行");
            LockSupport.unpark(t1);
        }, "t2");

        t1.start();
        t2.start();
    }

    /**
     * 使用 await() 和 single() 测试
     */
    @Test
    public void testAwaitSingle() {
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        new Thread(() -> {
            lock.lock();
            try {
                condition.await();
                log.debug("t1");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "t1").start();

        new Thread(() -> {
            lock.lock();
            try {
                log.debug("t2");
                condition.signal();
            } finally {
                lock.unlock();
            }
        }, "t2").start();
    }

}
