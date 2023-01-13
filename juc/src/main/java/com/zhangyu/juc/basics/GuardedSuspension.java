package com.zhangyu.juc.basics;

import com.zhangyu.juc.utils.DownloadUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * 保护性暂停模式
 * <br>
 * <img src="../../../../../resources/static/保护性暂停.png" />
 *
 * @author zhangyu
 * @date 2023/1/13 10:23
 */
@Slf4j()
public class GuardedSuspension {
    public static void main(String[] args) {
        GuardedObject guardedObject = new GuardedObject();
        new Thread(() -> {
            log.debug("等待结果");
            System.out.println(guardedObject.getResponse(2000));
        }, "t1").start();

        new Thread(() -> {
            log.debug("执行任务");
            try {
                List<String> download = DownloadUtils.download();
                guardedObject.setResponse(download);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "t2").start();
    }
}

@Data
class GuardedObject {
    // 结果
    private Object response;


    /**
     * 获取结果
     *
     * @param timeout 等待超时时间
     * @return 结果
     */
    public Object getResponse(long timeout) {
        synchronized (this) {
            // 开始时间 10:00:00
            long begin = System.currentTimeMillis();
            // 经历的时间
            long passedTime = 0;
            while (null == response) {
                long waitTime = timeout - passedTime;
                // 经历的时间超过了超时时间,跳出循环
                if (waitTime <= 0) {
                    break;
                }
                try {
                    // 此处若是等待 timeout 的时间,假设假设为 2s
                    // 假设 1 秒后发生了虚假唤醒,即唤醒之后不是自己的业务逻辑,
                    // 则再次循环时还需要等待两秒,不合理
                    this.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 计算 passedTime
                passedTime = System.currentTimeMillis() - begin;
            }
        }
        return response;
    }

    public void setResponse(Object response) {
        synchronized (this) {
            // 将结果给成员变量赋值
            this.response = response;
            this.notifyAll();
        }
    }
}
