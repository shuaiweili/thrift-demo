package com.git.lee.rpc.thrift.demo.manager;

/**
 * @author LISHUAIWEI
 * @date 2018/2/11 11:16
 */
public interface Closeable {

    void startup() throws Exception;
    void shutdown() throws Exception;
}
