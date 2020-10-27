package com.fantow.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// 消息生产者，用于发送用户积分情况
public class MQProducer {

    private static Logger logger = LoggerFactory.getLogger(MQProducer.class);

    private static MQProducer producer = new MQProducer();

    private static DefaultMQProducer _producer = null;

    private MQProducer() {
    }

    public static MQProducer getInstance(){
        return producer;
    }

    public static void init(){
        try {
            DefaultMQProducer producer = new DefaultMQProducer("GameMQ");
            producer.setNamesrvAddr("192.168.0.100:9876");
            producer.start();
            logger.info("MQ连接成功");

            _producer = producer;
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(String topic,Object msg){
        System.out.println("发送消息...  topic: " + topic);
        if(topic == null || msg == null){
            return;
        }

        Message newMsg = new Message();
        newMsg.setTopic(topic);

        // 将消息以Json格式发出
        newMsg.setBody(JSONObject.toJSONBytes(msg));

        try {
            _producer.send(newMsg);

        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }

    }
}
