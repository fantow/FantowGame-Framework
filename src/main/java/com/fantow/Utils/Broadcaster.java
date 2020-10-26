package com.fantow.Utils;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// 广播消息用
public final class Broadcaster {

    private static Logger logger = LoggerFactory.getLogger(Broadcaster.class);

    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    // 添加信道到channelGroup
    public static void addChannel(Channel channel){
        if(channel != null){
            channelGroup.add(channel);
        }else {
            System.out.println("需要添加的channel 为空");
        }
    }

    // 移除信道
    public static void removeChannel(Channel channel){
        if(channel != null){
            channelGroup.remove(channel);
        }else{
            logger.info("需要移除的channel 为空");
        }
    }

    // 广播消息用
    public static void broadCast(Object msg){
        if(msg != null){
            channelGroup.writeAndFlush(msg);
        }else {
            logger.info("需要广播的消息为空");
        }
    }



}
