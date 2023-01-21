package com.zhangyu.juc.pattern;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 同步模式-交替执行
 * abcabcabc
 *
 * @author zhangyu
 * @date 2023/1/16 17:06
 */
@Slf4j(topic = "AlternateExec")
public class AlternateExec {

    /**
     * wait/notify
     * 输出内容         等待标记    下一个标记
     * a                1           2
     * b                2           3
     * c                3           1
     */
    @Test
    public void testWaitNotify() {
        WaitNotify wn = new WaitNotify(1, 5);

        new Thread(() -> {
            wn.print("a", 1, 2);
        }).start();
        new Thread(() -> {
            wn.print("b", 2, 3);
        }).start();
        new Thread(() -> {
            wn.print("c", 3, 1);
        }).start();
    }

    class WaitNotify {

        /**
         * 打印
         */
        public void print(String str, int waitFlag, int nextFlag) {
            for (int i = 0; i < loopNumber; ++i) {
                synchronized (this) {
                    while (waitFlag != this.flag) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(str);
                    this.flag = nextFlag;
                    this.notifyAll();
                }
            }
        }

        /**
         * 等待标记
         */
        private int flag;

        /**
         * 循环次数
         */
        private int loopNumber;

        public WaitNotify(int flag, int loopNumber) {
            this.flag = flag;
            this.loopNumber = loopNumber;
        }
    }

}
