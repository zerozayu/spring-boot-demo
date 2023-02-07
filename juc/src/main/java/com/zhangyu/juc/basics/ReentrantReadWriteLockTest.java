package com.zhangyu.juc.basics;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁测试
 *
 * @author zhangyu
 * @date 2023/2/6 10:03
 */
public class ReentrantReadWriteLockTest {

    /**
     * 读读不互斥,读写/写写互斥
     */
    @Test
    public void test1(){
        ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = rw.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = rw.writeLock();

    }
}
