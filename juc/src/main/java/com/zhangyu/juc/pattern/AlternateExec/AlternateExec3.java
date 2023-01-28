package com.zhangyu.juc.pattern.AlternateExec;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.LockSupport;

/**
 * 同步模式-交替执行,使用 park/unpark 实现
 *
 * @author zhangyu
 * @date 2023/1/28 09:06
 */
public class AlternateExec3 {
    Thread t1 = null;
    Thread t2 = null;
    Thread t3 = null;

    @Test
    public void testParkUnpark() {
        ParkUnpark parkUnpark = new ParkUnpark(5);
        t1 = new Thread(() -> {
            parkUnpark.print("a", t2);
        });
        t2 = new Thread(() -> {
            parkUnpark.print("b", t3);
        });
        t3 = new Thread(() -> {
            parkUnpark.print("c", t1);
        });

        t1.start();
        t2.start();
        t3.start();

        LockSupport.unpark(t1);
    }

    class ParkUnpark {

        public void print(String str, Thread next) {
            for (int i = 0; i < loopNumber; ++i) {
                LockSupport.park();
                System.out.print(str);
                LockSupport.unpark(next);
            }
        }

        private int loopNumber;

        public ParkUnpark(int loopNumber) {
            this.loopNumber = loopNumber;
        }
    }
}
