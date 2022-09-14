package com.zhangyu.juc;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 工作者线程的示例代码
 *
 * @author zhangyu
 * @date 2022/9/13 15:43
 */
public class WorkerThread {

    public static void main(String[] args) {
        Helper helper = new Helper();
        helper.init();

        // 此处,helper 的客户端线程为 main 线程
        helper.submit("Something...");
    }

    static class Helper {
        private final BlockingQueue<String> workQueue = new ArrayBlockingQueue<String>(100);

        // 用于处理队列 workQueue 中的任务的工作者线程
        private final Thread workThread = new Thread(() -> {
            String task = null;
            while (true) {
                try {
                    task = workQueue.take();
                } catch (InterruptedException e) {
                    break;
                }
                System.out.println(doProcess(task));
            }
        });

        public void init() {
            workThread.start();
        }

        protected String doProcess(String task) {
            return task + "->processed.";
        }

        public void submit(String task) {
            try {
                workQueue.put(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
