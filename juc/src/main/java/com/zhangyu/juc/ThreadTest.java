package com.zhangyu.juc;

/**
 * @author zhangyu
 * @date 2022/9/13 10:45
 */
public class ThreadTest {

    public static void main(String[] args) {
        System.out.println("main by thread:" + Thread.currentThread().getName());

        Helper helper = new Helper("Java Thread Anywhere");

        Thread thread = new Thread(helper);

        thread.setName("A-Worker-Thread");

        thread.start();
    }

    static class Helper implements Runnable {
        private final String message;

        Helper(String message) {
            this.message = message;
        }

        private void doSomething(String message) {
            System.out.println("doSomething by thread:" + Thread.currentThread().getName());
            System.out.println("doSomething with " + message);
        }

        @Override
        public void run() {
            doSomething(message);
        }
    }
}
