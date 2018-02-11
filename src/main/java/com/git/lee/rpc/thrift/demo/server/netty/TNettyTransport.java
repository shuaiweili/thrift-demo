package com.git.lee.rpc.thrift.demo.server.netty;

import io.netty.buffer.ByteBuf;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import io.netty.channel.Channel;

/**
 * @author LISHUAIWEI
 * @date 2018/2/6 11:55
 */
public class TNettyTransport extends TTransport {
    private final Channel channel;
    private final ThriftMessage in;

    public TNettyTransport(Channel channel, ByteBuf in) {
        this(channel, new ThriftMessage(in, ThriftTransportType.UNKNOWN));
    }

    public TNettyTransport(Channel channel, ThriftMessage in) {
        this.channel = channel;
        this.in = in;
        in.getBuffer().retain();
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void open() throws TTransportException {

    }

    @Override
    public void close() {
        channel.close();
    }

    @Override
    public int read(byte[] buf, int off, int len) throws TTransportException {
        int _read = Math.min(in.getBuffer().readableBytes(), len);
        in.getBuffer().readBytes(buf, off, _read);
        return _read;
    }

    @Override
    public void write(byte[] buf, int off, int len) throws TTransportException {

    }

    public void release() {
        in.getBuffer().release();
    }
}
