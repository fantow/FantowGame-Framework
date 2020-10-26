package com.fantow.handler.Impl;

import com.fantow.Entity.UserInfo;
import com.fantow.Utils.Broadcaster;
import com.fantow.Utils.UserManager;
import com.fantow.handler.ICmdHandler;
import com.fantow.message.GameMsgProtocol;
import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd o) {
        System.out.println("服务器端接收到UserEntryCmd 请求");
        GameMsgProtocol.UserEntryCmd message = (GameMsgProtocol.UserEntryCmd) o;
        int userId = message.getUserId();
        String heroAvatar = message.getHeroAvatar();

        // 将userId保存在其channel中，用于其他命令使用
        System.out.println("存入userId: " + userId);
        ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);

        // 把接收到的消息广播给所有用户
        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(userId);
        resultBuilder.setHeroAvatar(heroAvatar);

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setHeroAvatar(heroAvatar);

        UserManager.addUser(userInfo);

        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();

        // 其实不应该在这里调用的，但是前端的问题，导致WoElseIsHereCMD无法被先连接的客户端调用
//                Iterator<Channel> iterator = channelGroup.iterator();
//                while (iterator.hasNext()) {
//                    Channel channel = iterator.next();
//                    channel.writeAndFlush(newResult);
//                }

        Broadcaster.broadCast(newResult);
    }
}
