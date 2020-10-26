package com.fantow.handler.Impl;

import com.fantow.Utils.Broadcaster;
import com.fantow.handler.ICmdHandler;
import com.fantow.message.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd o) {
        System.out.println("服务器端接收到UserMoveToCmd 请求");

        // 如果是用户移动的命令
        GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();

        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if(userId == null){
            return;
        }

        GameMsgProtocol.UserMoveToCmd message = (GameMsgProtocol.UserMoveToCmd) o;

        resultBuilder.setMoveUserId(userId);
        resultBuilder.setMoveToPosX(message.getMoveToPosX());
        resultBuilder.setMoveToPosY(message.getMoveToPosY());

        GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();

        // 将移动消息广播
        Broadcaster.broadCast(newResult);
    }
}
