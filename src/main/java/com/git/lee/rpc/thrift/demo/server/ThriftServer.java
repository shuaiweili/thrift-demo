package com.git.lee.rpc.thrift.demo.server;

import com.git.lee.rpc.thrift.demo.service.MessageService;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author LISHUAIWEI
 * @date 2018/2/6 11:13
 */
public class ThriftServer implements Server{
    private final static Logger LOGGER = LoggerFactory.getLogger(ThriftServer.class);
    private TServer server;
    private TThreadedSelectorServer.Args args;

    @Override
    public void startup(int port) {
        Thread thread = new Thread(() -> {
            try {
                MessageService.Processor processor = new MessageService.Processor(new ThriftHandler());
                TNonblockingServerSocket transport = new TNonblockingServerSocket(port);
                args = new TThreadedSelectorServer.Args(transport)
                        .acceptPolicy(TThreadedSelectorServer.Args.AcceptPolicy.FAST_ACCEPT)
                        .transportFactory(new TFramedTransport.Factory())
//                        .protocolFactory(new TCompactProtocol.Factory())
                        .selectorThreads(128).workerThreads(32);
                server = new TThreadedSelectorServer(args.processor(processor));
                LOGGER.info("Start thrift server.");
                server.serve();
            } catch (Exception e) {
                LOGGER.error("start thrift server error:", e);
            }
        });
        thread.start();
    }

    @Override
    public void shutdown() {
        try {
            if (server != null && server.isServing()) {
                server.stop();
            }
            if (args != null && args.getExecutorService() != null) {
                args.getExecutorService().shutdown();
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
