package com.git.lee.rpc.thrift.demo.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * @author LISHUAIWEI
 * @date 2018/2/6 12:05
 */
public class NettyDispatcher extends SimpleChannelInboundHandler<TNettyTransport> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyDispatcher.class);
    private final TProcessorFactory processorFactory;
    private final TProtocolFactory protocolFactory;
    private final Executor executor;

    public NettyDispatcher(ThriftServerDef def) {
        this.processorFactory = def.getProcessorFactory();
        this.protocolFactory = def.getInProtocolFactory();
        this.executor = def.getExecutor();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TNettyTransport transport) throws Exception {
        executor.execute(() -> {
            TProtocol protocol = protocolFactory.getProtocol(transport);
            try {
                processorFactory.getProcessor(transport).process(protocol, null);
            } catch (TException e) {
                LOGGER.error("process inProtocol error", e);
                closeChannel(ctx);
            } finally {
                transport.release();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }

    private void closeChannel(ChannelHandlerContext ctx) {
        if (ctx.channel().isOpen()) {
            ctx.channel().close();
        }
    }
}
