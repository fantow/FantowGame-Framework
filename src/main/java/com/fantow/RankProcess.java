package com.fantow;

import com.alibaba.fastjson.JSONObject;
import com.fantow.Entity.VictoryMessage;
import com.fantow.Rank.RankService;
import com.fantow.Utils.RedisUtil;
import com.fantow.mq.MQConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

// 专门负责处理Rank的进程
public class RankProcess {

    private static Logger logger = LoggerFactory.getLogger(RankProcess.class);

    public static void main(String[] args) throws MQClientException {

        RedisUtil.init();
//        try {
//            MQConsumer consumer = new MQConsumer();
//            consumer.init();
//        } catch (MQClientException e) {
//            e.printStackTrace();
//        }

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consum1");

        try {
            consumer.setNamesrvAddr("192.168.0.100:9876");
            consumer.subscribe("victory","*");
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    for(MessageExt message : msgs){
                        VictoryMessage victoryMessage = JSONObject.parseObject(message.getBody(),VictoryMessage.class);

                        logger.info("接收到胜利的消息...,{} --> {}",victoryMessage.getWinnerId(),victoryMessage.getLoserId());

                        // 更新Redis中的击倒数
                        RankService.getInstance().refreshRank(victoryMessage.getWinnerId(),victoryMessage.getLoserId());
                        System.out.println("更新击倒数结束..");
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });

        } catch (MQClientException e) {
            e.printStackTrace();
        }

        consumer.start();
        logger.info("consumer 启动成功......");

    }
}
