package com.git.lee.rpc.thrift.demo.server.netty;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TProtocolFactory;

import java.util.concurrent.Executor;

/**
 * @author LISHUAIWEI
 * @date 2018/2/6 17:51
 */
public class ThriftServerDef {
    private final int serverPort;
    private final int maxFrameSize;
    private final TProcessorFactory processorFactory;
    private final TProtocolFactory inProtocolFact;

    private final Executor executor;
    private final String name;

    public ThriftServerDef(String name, int serverPort, int maxFrameSize, TProcessorFactory factory, TProtocolFactory inProtocolFact, Executor executor) {
        this.name = name;
        this.serverPort = serverPort;
        this.maxFrameSize = maxFrameSize;
        this.processorFactory = factory;
        this.inProtocolFact = inProtocolFact;
        this.executor = executor;
    }

    public static ThriftServerDefBuilder newBuilder() {
        return new ThriftServerDefBuilder();
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getMaxFrameSize() {
        return maxFrameSize;
    }

    public TProcessorFactory getProcessorFactory() {
        return processorFactory;
    }

    public TProtocolFactory getInProtocolFactory() {
        return inProtocolFact;
    }

    public Executor getExecutor() {
        return executor;
    }

    public String getName() {
        return name;
    }
}

