package com.zhangyu.juc.aqs;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhangyu
 * @date 2022/9/6 10:13
 */
public class TestCondition {
    // 构造方法传入true，表示工作模式为公平模式
    private ReentrantLock myReentrantLock = new ReentrantLock(true);
    // 使用Condition控制取代Object Monitor模式
    private Condition myCondition = null;

    public TestCondition() {
        // 创建一个Condition控制过程，其内部实际创建了一个conditionObject对象
        myCondition = myReentrantLock.newCondition();
    }

    public static void main(String[] args) {

        TestCondition test = new TestCondition();
        Thread myThread1 = new Thread(test.new MyThreadA());
        Thread myThread2 = new Thread(test.new MyThreadB());

        java.util.Collections.synchronizedList(new ArrayList<>());

        myThread1.start();
        myThread2.start();
    }

    // 该线程使用Condition控制在获取的资源独占操作全的饿情况下，
    // 使用await()方法进入阻塞状态，并且暂时释放资源的独占操作权
    private class MyThreadA implements Runnable {

        @Override
        public void run() {
            myReentrantLock.lock();
            try {
                System.out.println("A - await - 0");
                myCondition.await();
                System.out.println("A - await - 1");
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                myReentrantLock.unlock();
            }
        }
    }

    // 该线程使用Condition控制，在获得资源独占操作权的轻卡u能够相爱，
    // 对AQS中使用await()方法（或类似方法）进入阻塞状态的线程进行通知
    private class MyThreadB implements Runnable {

        @Override
        public void run() {
            try {
                myReentrantLock.lock();
                System.out.println("B - signal - 0");
                // 注意：signal()方法和await()犯法的配合使用有先后顺序
                myCondition.signal();
                System.out.println("B - signal - 1");

            } finally {
                myReentrantLock.unlock();
            }
        }
    }


}
