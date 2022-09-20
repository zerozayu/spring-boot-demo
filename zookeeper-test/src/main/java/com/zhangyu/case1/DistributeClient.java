package com.zhangyu.case1;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangyu
 * @date 2022/9/20 08:46
 */
public class DistributeClient {

    private ZooKeeper zk;
    private String connectString = "localhost:2182,localhost:2183,localhost:2184";
    private int sessionTimeout = 2000;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        // 1 获取 zk 连接
        DistributeClient client = new DistributeClient();
        client.getConnect();

        // 2 监听 /servers 下的节点变化
        client.getServerList();

        // 3 执行业务逻辑
        client.business();
    }

    private void business() throws InterruptedException {
        Thread.sleep(Integer.MAX_VALUE);
    }

    private void getServerList() throws InterruptedException, KeeperException {
        List<String> children = zk.getChildren("/servers", true);

        ArrayList<String> servers = new ArrayList<>();
        for (String child : children) {
            byte[] data = zk.getData("/servers/" + child, false, null);

            servers.add(new String(data));
        }

        System.out.println(servers);
    }

    private void getConnect() throws IOException {
        zk = new ZooKeeper(connectString, sessionTimeout, watchedEvent -> {
            try {
                getServerList();
            } catch (InterruptedException | KeeperException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
