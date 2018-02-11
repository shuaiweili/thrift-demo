package com.git.lee.rpc.thrift.demo.server.netty;


import io.netty.buffer.ByteBuf;

/**
 * @author LISHUAIWEI
 * @date 2018/2/6 11:58
 */
public class ThriftMessage {

    private final ByteBuf buffer;
    private final ThriftTransportType transportType;

    public ThriftMessage(ByteBuf buffer, ThriftTransportType transportType) {
        this.buffer = buffer;
        this.transportType = transportType;
    }

    public ByteBuf getBuffer() {
        return buffer;
    }

    public ThriftTransportType getTransportType() {
        return transportType;
    }
}
