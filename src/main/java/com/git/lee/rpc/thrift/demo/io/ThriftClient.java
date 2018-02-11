package com.git.lee.rpc.thrift.demo.io;

import com.git.lee.rpc.thrift.demo.config.NodeRegistry;
import com.git.lee.rpc.thrift.demo.model.Message;
import com.git.lee.rpc.thrift.demo.model.Node;
import com.git.lee.rpc.thrift.demo.service.MessageService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;


/**
 * @author LISHUAIWEI
 * @date 2018/2/5 19:19
 */
public class ThriftClient {

    private int senderTimeout;
    private ThriftConnection connection;

    public ThriftClient(int senderTimeout) {
        this.senderTimeout = senderTimeout;
    }

    public boolean send(Message message)  {
        MessageService.Client client = getClient();
        if (client == null) return false;
        try {
            client.send(message);
            return true;
        } catch (TException e) {
            closeConnection();
        }
        return false;
    }

    public MessageService.Client getClient() {
        openConnection();
        return connection.getClient();
    }

    public boolean openConnection() {
        if (connection == null || !connection.isOpen()) {
            if (connection != null) {
                connection.closeConnection();
            }
            connection = new ThriftConnection(senderTimeout);
            connection.openConnection();
        }
        return isOpen();
    }

    public boolean isOpen() {
        return connection != null && connection.isOpen();
    }

    public void closeConnection() {
        if (connection != null) {
            connection.closeConnection();
        }
    }

    private class ThriftConnection {
        private TSocket socket;
        private TFramedTransport transport;
        private TBinaryProtocol protocol;
        private MessageService.Client client;
        private Node currNode;
        private int senderTimeout;

        public ThriftConnection(int senderTimeout) {
            this.senderTimeout = senderTimeout;
        }

        public void openConnection() {
            int nodeSize = NodeRegistry.getInstance().getNodeSize();
            if (nodeSize < 1) return;
            for (int i = 0; i < nodeSize; i++) {
                try {
                    currNode = NodeRegistry.getInstance().getNode();
                    if (currNode == null) return;
                    socket = new TSocket(currNode.getHost(), currNode.getPort());
                    socket.setTimeout(senderTimeout);
                    transport = new TFramedTransport(socket);
//                    TProtocol p = new TCompactProtocol(transport);
                    protocol = new TBinaryProtocol(transport);
                    client = new MessageService.Client(protocol);
                    transport.open();
                    return;
                } catch (TTransportException e) {
                    closeConnection();
                }
            }
        }

        public MessageService.Client getClient() {
            return client;
        }

        public boolean isOpen() {
            return transport != null && transport.isOpen() && socket != null && socket.isOpen() && protocol != null && client != null;
        }

        public void closeConnection() {
            if (transport != null && transport.isOpen()) {
                try {
                    transport.close();
                } catch (Exception e) {}
                transport = null;
            }
            if (socket != null && socket.isOpen()) {
                try {
                    socket.close();
                } catch (Exception ignore) {}
                socket = null;
            }
            if (protocol != null) {
                protocol = null;
            }
            if (client != null) {
                client = null;
            }
            currNode = null;
        }
    }
}
