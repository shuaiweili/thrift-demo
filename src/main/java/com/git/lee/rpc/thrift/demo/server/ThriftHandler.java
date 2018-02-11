package com.git.lee.rpc.thrift.demo.server;

import com.git.lee.rpc.thrift.demo.model.Message;
import com.git.lee.rpc.thrift.demo.service.MessageService;
import org.apache.thrift.TException;


/**
 * @author LISHUAIWEI
 * @date 2018/2/6 10:14
 */
public class ThriftHandler implements MessageService.Iface {
    @Override
    public void send(Message message) throws TException {
        System.out.println("server receive message: " + message);
    }
}
