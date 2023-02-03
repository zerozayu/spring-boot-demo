package com.zhangyu.juc.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * ForkJoin 线程池
 *
 * @author zhangyu
 * @date 2023/2/3 16:38
 */
public class TestForkJoinPool {
    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        // forkJoinPool.invoke(new MyTask(10));
        System.out.println(forkJoinPool.invoke(new MyTask1(1, 10)));
    }

}

@Slf4j(topic = "MyTask1")
class MyTask1 extends RecursiveTask<Integer> {
    private int begin;

    private int end;

    public MyTask1(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        if (begin == end) {
            return begin;
        }
        if (begin + 1 == end) {
            return begin + end;
        }

        int mid = (begin + end) >> 1;

        MyTask1 t1 = new MyTask1(begin, mid);
        t1.fork();
        MyTask1 t2 = new MyTask1(mid + 1, end);
        t2.fork();

        int result = t1.join() + t2.join();
        return result;
    }
}

@Slf4j(topic = "MyTask")
class MyTask extends RecursiveTask<Integer> {
    private int n;

    public MyTask(int n) {
        this.n = n;
    }

    @Override
    protected Integer compute() {
        // 终止条件: 如果 n 已经为 1,可以求结果了
        if (n == 1) {
            log.debug("join() {}", n);
            return n;
        }

        // 将任务进行拆分(fork)
        MyTask t1 = new MyTask(n - 1);
        log.debug("fork() {} + {}", n, t1);
        t1.fork();

        int result = n + t1.join();
        log.debug("join() {} + {} = {}", n, t1, result);
        return result;
    }
}
