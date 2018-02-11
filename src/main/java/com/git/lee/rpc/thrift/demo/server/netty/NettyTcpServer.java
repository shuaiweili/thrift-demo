package com.git.lee.rpc.thrift.demo.server.netty;

import com.git.lee.rpc.thrift.demo.server.Server;
import com.git.lee.rpc.thrift.demo.server.ThriftHandler;
import com.git.lee.rpc.thrift.demo.service.MessageService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.thrift.TProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author LISHUAIWEI
 * @date 2018/2/6 16:44
 */
public class NettyTcpServer implements Server{

    private final static Logger LOGGER = LoggerFactory.getLogger(NettyTcpServer.class);
    private final static int THREAD_NUM = 3;
    private ChannelFuture future;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;

    @Override
    public void startup(int port) {
        Thread thread = new Thread(() -> {
            boolean linux = osMatch("Linux") || osMatch("LINUX");
            bossGroup = linux ? new EpollEventLoopGroup(1) : new NioEventLoopGroup(1);
            workerGroup = linux ? new EpollEventLoopGroup(THREAD_NUM) : new NioEventLoopGroup(THREAD_NUM);

            try {
                bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup);
                if (linux) {
                    bootstrap.channel(EpollServerSocketChannel.class);
                } else {
                    bootstrap.channel(NioServerSocketChannel.class);
                }

                MessageService.Iface handler = new ThriftHandler();
                TProcessor processor = new MessageService.Processor<>(handler);
                ThriftServerDef def = new ThriftServerDefBuilder().withProcessor(processor).build();

                bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("frameDecoder", new ThriftFrameDecoder(def.getMaxFrameSize(), def.getInProtocolFactory()));
                        pipeline.addLast("dispatcher", new NettyDispatcher(def));
                    }
                });
                bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
                bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
                bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
                bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
                bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);

                try {
                    LOGGER.info("Started netty transport server {}:{}", def.getName(), port);
                    future = bootstrap.bind(port).sync();
                    future.channel().closeFuture().sync().channel();
                }catch (Exception e) {
                    LOGGER.error("Started netty server failed:", e);
                }
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        });
        thread.start();
    }

    @Override
    public void shutdown() {
        try {
            System.out.println("Started shutdown netty transport server " );
            if (future != null) {
                future.channel().closeFuture();
            }
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.out.println("Shutdown netty thrift server success.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean osMatch(String osNamePrefix) {
        String os = System.getProperty("os.name");
        return os != null && os.startsWith(osNamePrefix);
    }
}
