package com.zhangyu.juc.pool;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

   /**
 * 自定义实现线程池
 * <br>
 * <img src="../../../../../resources/static/自定义线程池.png"/>
 *
 * @author zhangyu
 * @date 2023/2/1 16:35
 */
public class TestPool {


}

class ThreadPool {
    // 1.任务队列
    private BlockingQueue<Runnable> taskQueue;

    // 2.线程集合
    private final HashSet<Worker> workers = new HashSet<>();

    // 3.核心线程数
    private int coreSize;

    // 4.获取任务时的超时时间
    private long timeout;
    private TimeUnit timeUnit;

    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit, int queueCapacity) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(queueCapacity);
    }

    // 线程池执行相关任务
    public void execute(Runnable task) {
        // 当任务没有超过 coreSize 时,直接交给 worker 对象执行
        // 如果任务超过 coreSize 时,加入任务队列暂存
        if (workers.size() < coreSize) {
            Worker worker = new Worker(task);
            workers.add(worker);
            worker.start();
        } else {
            taskQueue.put(task);
        }
    }

    // 自定义线程实现
    class Worker extends Thread {
        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            // 执行任务
            // 1)当 task 不为空的时候,执行任务
            // 2)当 task 执行完毕,再接着从任务队列获取任务并执行
            // while (task != null || (task = taskQueue.take()) != null) {
            while (task != null || (task = taskQueue.poll(timeout, timeUnit)) != null) {
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    task = null;
                }
            }
            synchronized (workers) {
                workers.remove(this);
            }
        }
    }

}

class BlockingQueue<T> {
    // 1.任务队列
    private Deque<T> queue = new ArrayDeque<>();

    // 2.锁
    private ReentrantLock lock = new ReentrantLock();

    // 3.生产者条件变量
    private Condition fullWaitSet = lock.newCondition();

    // 4.消费者条件变量
    private Condition emptyWaitSet = lock.newCondition();

    // 5.容量
    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    // poll 增强
    public T poll(long timeout, TimeUnit unit) {
        lock.lock();
        try {
            // 将 timeout 统一转换为纳秒
            long nanos = unit.toNanos(timeout);

            while (queue.isEmpty()) {
                // 返回的是剩余时间
                if (nanos < 0) {
                    return null;
                }
                try {
                    nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    // 阻塞获取
    public T take() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    // offer 增强
    public boolean offer(T task, long timeout, TimeUnit timeUnit) {
        lock.lock();
        try {
            long nanos = timeUnit.toNanos(timeout);
            while (queue.size() == capacity) {
                if (nanos <= 0){
                    return false;
                }
                try {
                    nanos = fullWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(task);
            emptyWaitSet.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    // 阻塞添加
    public void put(T element) {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                try {
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(element);
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }
    }

    // 获取大小
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
}
