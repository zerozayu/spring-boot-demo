package com.zhangyu.juc.basics;

import java.util.concurrent.locks.StampedLock;

/**
 * 邮戳锁测试
 *
 * @author zhangyu
 * @date 2023/2/6 14:23
 */
public class StampedLockTest {


    class DataContainerStamped {
        private int data;
        private final StampedLock lock = new StampedLock();

        public DataContainerStamped(int data) {
            this.data = data;
        }

        public int read(int readTime) throws InterruptedException {
            long stamp = lock.tryOptimisticRead();

            Thread.sleep(readTime);

            if (lock.validate(stamp)) {
                return data;
            }

            // 锁升级
            try {
                stamp = lock.readLock();
                Thread.sleep(readTime);
                return data;
            } finally {
                lock.unlockRead(stamp);
            }
        }
    }
}
