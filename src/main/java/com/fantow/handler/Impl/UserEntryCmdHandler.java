package com.fantow.handler.Impl;

import com.fantow.Entity.UserInfo;
import com.fantow.Utils.Broadcaster;
import com.fantow.Utils.UserManager;
import com.fantow.handler.ICmdHandler;
import com.fantow.message.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {

    private static Logger logger = LoggerFactory.getLogger(UserEntryCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd o) {
        System.out.println("服务器端接收到UserEntryCmd 请求");
//        GameMsgProtocol.UserEntryCmd message = (GameMsgProtocol.UserEntryCmd) o;
//        int userId = message.
//        String heroAvatar = message.getHeroAvatar();
//
//        // 将userId保存在其channel中，用于其他命令使用
//        System.out.println("存入userId: " + userId);
//        ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);
//
//        // 把接收到的消息广播给所有用户
//        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
//        resultBuilder.setUserId(userId);
//        resultBuilder.setHeroAvatar(heroAvatar);
//
//        UserInfo userInfo = new UserInfo();
//        userInfo.setUserId(userId);
//        userInfo.setHeroAvatar(heroAvatar);
//
//        // 角色血量初始化为100
//        userInfo.setHP(100);
//
//        UserManager.addUser(userInfo);
//
//        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
//
//        // 其实不应该在这里调用的，但是前端的问题，导致WoElseIsHereCMD无法被先连接的客户端调用
////                Iterator<Channel> iterator = channelGroup.iterator();
////                while (iterator.hasNext()) {
////                    Channel channel = iterator.next();
////                    channel.writeAndFlush(newResult);
////                }
//
//        Broadcaster.broadCast(newResult);

        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if(userId == null){
            return;
        }

        UserInfo userInfo = UserManager.getUserInfo(userId);
        if(userInfo == null){
            logger.info("用户不存在");
        }

        // 获取英雄形象
        String heroAvatar = userInfo.getHeroAvatar();

        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(userId);
        resultBuilder.setHeroAvatar(heroAvatar);

        // 构建结果并发送
        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();

        Broadcaster.broadCast(newResult);
    }

}
