package com.git.lee.rpc.thrift.demo;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;

/**
 * @author LISHUAIWEI
 * @date 2018/2/11 10:28
 */
public class ThriftModule extends AbstractModule {
    @Override
    protected void configure() {
        Config config = ConfigFactory.load();
        bind(Config.class).toInstance(config);
        bindZookeeper(config);
    }

    private void bindZookeeper(Config config) {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(config.getString("zookeeper.connect")).namespace(config.getString("zookeeper.namespace"))
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000))
                .connectionTimeoutMs(60000).build();
        client.start();
        bind(CuratorFramework.class).toInstance(client);
    }
}
