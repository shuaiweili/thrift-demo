package com.git.lee.rpc.thrift.demo.serializer;

import com.alibaba.fastjson.JSON;
import com.git.lee.rpc.thrift.demo.model.Message;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LISHUAIWEI
 * @date 2018/2/6 10:54
 */
public class CodecTest {

    public static void main(String[] args) throws TException {
        Message message = buildMessage();
        codec(message);
    }

    private static void codec(Message message) throws TException {
        //encode
        TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
        //warm up
        for (int i = 0; i < 100; i++) {
            serializer.serialize(message);
        }

        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            serializer.serialize(message);
        }
        System.out.println("thrift序列化耗时：" + (System.currentTimeMillis() - start) + " ms");

        //json
        //warm up
        for (int i = 0; i < 100; i++) {
            JSON.toJSONBytes(message);
        }

        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            JSON.toJSONBytes(message);
        }
        System.out.println("json序列化耗时：" + (System.currentTimeMillis() - start) + " ms");

        //decode
        byte[] data1 = serializer.serialize(message);
        byte[] data2 = JSON.toJSONBytes(message);

        TDeserializer deserializer = new TDeserializer();
        //warmup
        for (int i = 0; i < 100; i++) {
            deserializer.deserialize(new Message(), data1);
            JSON.parseObject(data2, Message.class);
        }

        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            deserializer.deserialize(new Message(), data1);
        }
        System.out.println("thrift反序列化耗时：" + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            JSON.parseObject(data2, Message.class);
        }
        System.out.println("json反序列化耗时：" + (System.currentTimeMillis() - start) + " ms");
    }

    public static Message buildMessage() {
        Message message = new Message();
        message.timestamp = System.currentTimeMillis();
        message.name = "message_";

        Map<String, String> tags = new HashMap<>();
        tags.put("tag_key1", "tag_value1");
        tags.put("tag_key2", "tag_value2");
        tags.put("tag_key3", "tag_value3");
        tags.put("tag_key4", "tag_value4");

        Message child1 = new Message();
        child1.timestamp = System.currentTimeMillis();
        child1.name = "child1";

        message.tags = tags;
        message.addToChildren(child1);
        return message;
    }


}
