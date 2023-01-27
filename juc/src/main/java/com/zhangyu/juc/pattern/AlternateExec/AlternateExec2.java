package com.zhangyu.juc.pattern.AlternateExec;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 同步模式-交替执行 abcabcabc
 * 使用 await/single 实现
 *
 * @author zhangyu
 * @date 2023/1/27 19:07
 */
@Slf4j(topic = "AlternateExec2")
public class AlternateExec2 {

    @Test
    public void TestAwaitSingle() throws InterruptedException {
        AwaitSingle awaitSingle = new AwaitSingle(5);
        Condition a = awaitSingle.newCondition();
        Condition b = awaitSingle.newCondition();
        Condition c = awaitSingle.newCondition();

        new Thread(() -> {
            awaitSingle.print("a", a, b);
        }).start();
        new Thread(() -> {
            awaitSingle.print("b", b, c);
        }).start();
        new Thread(() -> {
            awaitSingle.print("c", c, a);
        }).start();

        Thread.sleep(1000);
        awaitSingle.lock();
        try {
            log.debug("开始...");
            a.signal();
        } finally {
            awaitSingle.unlock();
        }

    }

    class AwaitSingle extends ReentrantLock {
        private int loopNumber;

        public AwaitSingle(int loopNumber) {
            this.loopNumber = loopNumber;
        }

        /**
         * @param str     打印内容
         * @param current 进入哪一间休息室
         * @param next    下一间休息室
         */
        public void print(String str, Condition current, Condition next) {
            for (int i = 0; i < loopNumber; ++i) {
                this.lock();
                try {
                    current.await();
                    log.debug(str);
                    next.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    this.unlock();
                }
            }
        }
    }
}
