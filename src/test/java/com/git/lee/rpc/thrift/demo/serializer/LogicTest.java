package com.git.lee.rpc.thrift.demo.serializer;

import com.git.lee.rpc.thrift.demo.config.NodeRegistry;
import com.git.lee.rpc.thrift.demo.io.ThriftClient;
import com.git.lee.rpc.thrift.demo.model.Message;
import com.git.lee.rpc.thrift.demo.model.Node;
import com.git.lee.rpc.thrift.demo.server.Server;
import com.git.lee.rpc.thrift.demo.server.ThriftServer;
import com.git.lee.rpc.thrift.demo.server.netty.NettyTcpServer;
import org.junit.Test;

import java.io.IOException;

/**
 * @author LISHUAIWEI
 * @date 2018/2/6 11:21
 */
public class LogicTest {
    private final static int PORT = 2890;
    private final static int TIMEOUT = 5000;

    @Test
    public void send() throws IOException {
        startThriftServer();
//        startNettyServer();
        addNode();
        ThriftClient client = new ThriftClient(TIMEOUT);
        Message message = CodecTest.buildMessage();
        client.send(message);
        System.in.read();
    }

    private void startThriftServer() {
        Server server = new ThriftServer();
        server.startup(PORT);
    }

    private void startNettyServer() {
        Server server = new NettyTcpServer();
        server.startup(PORT);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addNode() {
        NodeRegistry.getInstance().addNode(new Node(PORT, "localhost"));
    }
}
