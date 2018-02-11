package com.git.lee.rpc.thrift.demo.server;

/**
 * @author LISHUAIWEI
 * @date 2018/2/6 11:52
 */
public interface Server {

    void startup(int port);

    void shutdown();
}
