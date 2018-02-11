package com.git.lee.rpc.thrift.demo.manager;

import com.git.lee.rpc.thrift.demo.model.Node;
import com.git.lee.rpc.thrift.demo.util.JsonUtils;
import com.git.lee.rpc.thrift.demo.util.ZKPathUtils;
import com.git.lee.rpc.thrift.demo.util.ZKUtils;
import com.git.lee.rpc.thrift.demo.zookeeper.ClusterStateMachine;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.curator.framework.CuratorFramework;


/**
 * @author LISHUAIWEI
 * @date 2018/2/11 11:14
 */
@Singleton
public class NodeManager implements Closeable{
    @Inject
    private CuratorFramework curator;
    @Inject
    private ContainerContext context;
    @Inject
    private ClusterStateMachine clusterStateMachine;

    @Override
    public void startup()  throws Exception{
        registerNode();
        clusterStateMachine.startup();
    }

    @Override
    public void shutdown()  throws Exception{
        Node node = getCurrNode();
        if (node != null) {
            ZKUtils.deletePath(curator, ZKPathUtils.getNodePath(node.getNodePath()));
        }
        if (curator != null) {
            curator.close();
        }
    }

    public Node getCurrNode() {
        return context.getCurrNode();
    }

    private void registerNode() throws Exception {
        Node node = context.getCurrNode();
        String path = ZKPathUtils.getNodePath(node.getNodePath());
        if (!ZKUtils.exists(curator, path)) {
            ZKUtils.createPersistentPath(curator, path);
        }
    }
}
