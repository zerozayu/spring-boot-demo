package com.zhangyu.juc.basics;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 可重入锁条件变量test
 *
 * @author zhangyu
 * @date 2023/1/16 10:12
 */
@Slf4j(topic = "ReentrantLock")
public class ReentrantLockTest {

    private static boolean hasCigarette = false;
    private static boolean hasTakeout = false;
    private final static ReentrantLock ROOM = new ReentrantLock();

    // 等烟的休息室
    private final static Condition waitCigarette = ROOM.newCondition();
    // 等外卖的休息室
    private final static Condition waitTakeout = ROOM.newCondition();

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            ROOM.lock();
            try {
                log.debug("烟送到了没?[{}]", hasCigarette);
                while (!hasCigarette) {
                    waitCigarette.await();
                }
                log.debug("可以干活了");
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.debug("t1被打断了");
            } finally {
                ROOM.unlock();
            }
        }).start();

        new Thread(() -> {
            ROOM.lock();
            try {
                log.debug("外卖送到了没?[{}]", hasTakeout);
                while (!hasTakeout) {
                    waitTakeout.await();
                }
                log.debug("可以干活了");
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.debug("t2被打断了");
            } finally {
                ROOM.unlock();
            }
        }, "t2").start();

        Thread.sleep(1000);
        new Thread(() -> {
            ROOM.lock();
            try {
                hasCigarette = true;
                log.debug("烟送到了");
                waitCigarette.signal();
            } finally {
                ROOM.unlock();
            }
        }, "送烟的").start();

        Thread.sleep(1000);
        new Thread(() -> {
            ROOM.lock();
            try {
                hasTakeout = true;
                log.debug("外卖送到了");
                waitTakeout.signal();
            } finally {
                ROOM.unlock();
            }
        }, "送外卖的").start();
    }
}
