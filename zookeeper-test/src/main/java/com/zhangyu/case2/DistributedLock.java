package com.zhangyu.case2;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 分布式锁,zookeeper 原生实现,死等
 *
 * @author zhangyu
 * @date 2022/9/20 16:11
 */
public class DistributedLock {

    private ZooKeeper zk;
    private String connectString = "localhost:2182,localhost:2183,localhost:2184";
    private static int sessionTimeout = 2000;
    private CountDownLatch connectLatch = new CountDownLatch(1);
    private CountDownLatch waitLatch = new CountDownLatch(1);
    private String waitPath;
    private String currentNode;

    public DistributedLock() throws IOException, InterruptedException, KeeperException {
        // 建立连接
        zk = new ZooKeeper(connectString, sessionTimeout, watchedEvent -> {
            // 如果连接上 zk, connectLatch 可以释放
            if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                connectLatch.countDown();
            }

            // waitLatch 可以释放
            if (watchedEvent.getType() == Watcher.Event.EventType.NodeDeleted
                    && watchedEvent.getPath().equals(waitPath)){
                waitLatch.countDown();
            }

        });
        // 等待 zk 正常连接后
        connectLatch.await();

        // 判断根节点 /locks 是否存在
        Stat stat = zk.exists("/locks", false);
        if (stat == null) {
            // 创建根节点
            zk.create("/locks", "locks".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    public void zkLock() {
        // 创建对应的带序号临时节点
        try {
            currentNode = zk.create("/locks/seq-", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            // 判断创建的节点是不是最小的序号,是-获取到锁;不是则监听序号前一个节点
            List<String> children = zk.getChildren("/locks", false);
            // 如果 children 只有一个值,获得锁
            if (children.size() == 1) {
                return;
            } else {

                Collections.sort(children);

                // 获取节点名称 seq-000000000
                String thisNode = currentNode.substring("/locks/".length());
                // 获取节点在里面的位置
                int index = children.indexOf(thisNode);

                if (index == -1) {
                    System.out.println("数据异常");
                } else if (index == 0) {
                    System.out.println("获得锁");
                } else {
                    // 需要监听前一个节点变化
                    waitPath = children.get(index - 1);
                    zk.getData(waitPath, true, null);

                    // 等待监听
                    waitLatch.await();

                    System.out.println("监听前一个节点变化");
                }
            }
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void zkUnlock() {
        // 删除该节点
        try {
            zk.delete(currentNode, 1);
        } catch (InterruptedException | KeeperException e) {
            throw new RuntimeException(e);
        }
    }
}
