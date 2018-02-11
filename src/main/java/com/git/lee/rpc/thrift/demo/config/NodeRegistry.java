package com.git.lee.rpc.thrift.demo.config;

import com.git.lee.rpc.thrift.demo.model.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author LISHUAIWEI
 * @date 2018/2/5 19:20
 */
public class NodeRegistry {

    private List<Node> nodes = new ArrayList<>();
    private AtomicLong nodeIndex = new AtomicLong(0);
    private NodeRegistry() {}

    private static class NodeRegistryHolder {
        private static NodeRegistry instance = new NodeRegistry();
    }

    public static NodeRegistry getInstance() {
        return NodeRegistryHolder.instance;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void addNode(String host, int port) {
        addNode(new Node(port, host));
    }

    public void addNode(String hostPort) {
        String[] host_port = hostPort.split(":");
        addNode(host_port[0], Integer.valueOf(host_port[1]));
    }

    public void removeNode(String host, int port) {
        removeNode(find(host, port));
    }

    public void removeNode(Node node) {
        nodes.remove(node);
    }

    public void removeNode(String hostPort) {
        String[] host_port = hostPort.split(":");
        removeNode(host_port[0], Integer.valueOf(host_port[1]));
    }

    public int getNodeSize() {
        return nodes.size();
    }

    public Node getNode() {
        if (nodes.isEmpty()) return null;
        long next = nodeIndex.getAndAdd(1) % getNodeSize();
        return nodes.get((int)next);
    }

    private Node find(String host, int port) {
        for (Node node : nodes) {
            if (host.equals(node.getHost()) && port == node.getPort()) {
                return node;
            }
        }
        return new Node(port, host);
    }
}
