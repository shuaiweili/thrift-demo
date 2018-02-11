package com.git.lee.rpc.thrift.demo.zookeeper;

import com.git.lee.rpc.thrift.demo.config.NodeRegistry;
import com.git.lee.rpc.thrift.demo.manager.Closeable;
import com.git.lee.rpc.thrift.demo.util.StringUtils;
import com.git.lee.rpc.thrift.demo.util.ZKPathUtils;
import com.git.lee.rpc.thrift.demo.util.ZKUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * @author LISHUAIWEI
 * @date 2018/2/11 10:36
 */
@Singleton
public class ClusterStateMachine implements Closeable {

    private final static Logger LOGGER = LoggerFactory.getLogger(ClusterStateMachine.class);

    @Inject
    private CuratorFramework curator;

    private TreeCache nodeCache;


    @Override
    public void startup()  throws Exception{
        String clusterPath = ZKPathUtils.CLUSTER_PATH;
        try {
            ZKUtils.createPersistentPathIfNotExist(curator, clusterPath);
            nodeCache = new TreeCache(curator, clusterPath);
            nodeCache.getListenable().addListener(new ClusterListener());
            nodeCache.start();
        } catch (Exception e) {
            LOGGER.error("ADD cluster state machine for path [{}], error, please check.", clusterPath, e);
        }
    }

    @Override
    public void shutdown() throws Exception {
        if (nodeCache != null) {
            nodeCache.close();
        }
    }

    class ClusterListener implements TreeCacheListener{
        private final Pattern pattern = Pattern.compile("^/cluster/.+$");
        @Override
        public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent event) throws Exception {
            if (event.getData() != null) {
                String path = event.getData().getPath();
                if (pattern.matcher(path).find()) {
                    LOGGER.info("cluster state machine trigger, path: [{}], event type is {}.", path, event.getType());
                    String node = ZKPathUtils.splitToName(path);
                    if (StringUtils.isEmpty(node)) {
                        LOGGER.warn("node name is null or empty, please check!");
                        return;
                    }
                    switch (event.getType()) {
                        case NODE_ADDED:
                            NodeRegistry.getInstance().addNode(node);
                            break;
                        case NODE_REMOVED:
                            NodeRegistry.getInstance().removeNode(node);
                            break;
                    }
                }
            }
        }
    }
}
