package com.git.lee.rpc.thrift.demo.manager;

import com.git.lee.rpc.thrift.demo.model.Node;
import com.git.lee.rpc.thrift.demo.util.NetworkInterfaceHelper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;

/**
 * @author LISHUAIWEI
 * @date 2018/2/11 12:01
 */
@Singleton
public class ContainerContext {

    private Node node;

    @Inject
    public ContainerContext(Config config) {
        int port = config.getInt("default.port");
        this.node = new Node(port, NetworkInterfaceHelper.INSTANCE.getLocalHostAddress());
    }

    public Node getCurrNode() {
        return node;
    }
}
