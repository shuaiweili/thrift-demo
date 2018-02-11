package com.git.lee.rpc.thrift.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author LISHUAIWEI
 * @date 2018/2/5 19:17
 */
public class Node {

    private int port;
    private String host;

    public Node(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    @JsonIgnore
    public String getNodePath() {
        return host + ":" + port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (port != node.port) return false;
        return host != null ? host.equals(node.host) : node.host == null;
    }

    @Override
    public int hashCode() {
        int result = port;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        return result;
    }
}
