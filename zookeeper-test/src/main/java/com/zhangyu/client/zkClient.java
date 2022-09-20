package com.zhangyu.client;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * zk 客户端
 *
 * @author zhangyu
 * @date 2022/9/19 11:06
 */
public class zkClient {
    private final String connectString = "localhost:2181";
    private final int sessionTimeout = 2000;
    ZooKeeper zooKeeper;


    @Before
    public void init() throws IOException {
        zooKeeper = new ZooKeeper(connectString, sessionTimeout, watchedEvent -> {
            // 注册监听
            System.out.println("---------watcher thread start----------");
            List<String> children = null;
            try {
                children = zooKeeper.getChildren("/", true);
            } catch (KeeperException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (String child : children) {
                System.out.println(child);
            }
            System.out.println("---------watcher thread end----------");
        });
    }

    @Test
    public void exist() throws InterruptedException, KeeperException {
        Stat zhangyu = zooKeeper.exists("/zhangyu", false);

        System.out.println(zhangyu == null ? "not exist" : "exist");
    }


    @Test
    public void getChildren() throws InterruptedException, KeeperException {
        System.out.println("---------main thread start----------");

        // 此处为 true 是意味着使用 init 方法里的 watcher,可以一直监听自己,如果此处 new watcher 的话还是只会调用一次
        List<String> children = zooKeeper.getChildren("/", true);

        for (String child : children) {
            System.out.println(child);
        }
        System.out.println("---------start sleep----------");

        Thread.sleep(Integer.MAX_VALUE);
        System.out.println("---------main thread end----------");
    }

    @Test
    public void create() throws InterruptedException, KeeperException {
        String s = zooKeeper.create("/sanguo", "sanguo".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }


}
