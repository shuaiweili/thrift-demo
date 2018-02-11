package com.git.lee.rpc.thrift.demo.coordinator;

import com.git.lee.rpc.thrift.demo.guice.GuiceInstanceFactory;
import com.git.lee.rpc.thrift.demo.io.ThriftClient;
import com.git.lee.rpc.thrift.demo.manager.NodeManager;
import com.git.lee.rpc.thrift.demo.serializer.CodecTest;
import com.git.lee.rpc.thrift.demo.server.ThriftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author LISHUAIWEI
 * @date 2018/2/11 15:14
 */
public class MixAppTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(MixAppTest.class);

    public static void main(String[] args) throws Exception {
        NodeManager nodeManager = GuiceInstanceFactory.getInstance(NodeManager.class);
        startServer(nodeManager.getCurrNode().getPort());
        nodeManager.startup();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                nodeManager.shutdown();
            } catch (Exception e) {
                LOGGER.error("shutdown error:", e);
            }
        }));

        Thread.sleep(10000);


        //这里仅是测试，真实环境Client和Server肯定是两个应用，Client要从某个地方获取可用节点的信息，而不是现在Server和Client公用一个NodeRegistry
        ThriftClient client = new ThriftClient(5000);
        client.send(CodecTest.buildMessage());

        Thread.sleep(60000);
        client.send(CodecTest.buildMessage());
    }

    private static void startServer(int port) {
        ThriftServer server = new ThriftServer();
        server.startup(port);
    }
}
