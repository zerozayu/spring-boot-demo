package com.zhangyu.case1;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * 分布式 server
 *
 * @author zhangyu
 * @date 2022/9/19 22:26
 */
public class DistributeServer {

    private String connectString = "localhost:2182,localhost:2183,localhost:2184";
    private int sessionTimeout = 2000;
    private ZooKeeper zk;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        // 1 获取 zk 连接
        DistributeServer server = new DistributeServer();
        server.getConnect();

        // 2 注册服务器到 zk 集群
        server.regist(args[0]);

        // 3 启动业务逻辑(sleep)
        server.business();
    }

    private void business() throws InterruptedException {
        Thread.sleep(Integer.MAX_VALUE);
    }

    private void regist(String hostname) throws InterruptedException, KeeperException {
        String s = zk.create("/servers/" + hostname, hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        System.out.println(hostname + " is online");
    }

    private void getConnect() throws IOException {
        zk = new ZooKeeper(connectString, sessionTimeout, watchedEvent -> {

        });
    }


}
