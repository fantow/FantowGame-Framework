package com.fantow.handler.Impl;

import com.fantow.Entity.UserInfo;
import com.fantow.Utils.Broadcaster;
import com.fantow.Utils.UserManager;
import com.fantow.handler.ICmdHandler;
import com.fantow.message.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;


// 在该条消息中，客户端会推给服务器角色移动的起始+终止坐标+起始时间
// 实现这个同步逻辑，也要求前端有这个功能
public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd o) {
        System.out.println("服务器端接收到UserMoveToCmd 请求");

        // 如果是用户移动的命令
        GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();

        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if(userId == null){
            System.out.println("userId为空");
            return;
        }

        GameMsgProtocol.UserMoveToCmd message = (GameMsgProtocol.UserMoveToCmd) o;
        System.out.println("接收到移动消息： " + message);

        // 统一的移动时间
        long startTime = System.currentTimeMillis();

        UserInfo userInfo = UserManager.getUserInfo(userId);

        System.out.println("message: " + " fromX:" + message.getMoveFromPosX() + " fromY:" + message.getMoveFromPosY() + " toX:" + message.getMoveToPosX() + " toY:" + message.getMoveToPosY());

        System.out.println(userInfo.getMoveState());

        // 这里是获取到UserInfo的引用，并将这些数据写给了UserInfo
        userInfo.getMoveState().setFromPosx(message.getMoveFromPosX());
        userInfo.getMoveState().setFromPosy(message.getMoveFromPosY());
        userInfo.getMoveState().setToPosx(message.getMoveToPosX());
        userInfo.getMoveState().setToPosy(message.getMoveToPosY());
        userInfo.getMoveState().setStartTime(startTime);

        resultBuilder.setMoveUserId(userId);
        resultBuilder.setMoveFromPosX(message.getMoveFromPosX());
        resultBuilder.setMoveFromPosY(message.getMoveFromPosY());
        resultBuilder.setMoveToPosX(message.getMoveToPosX());
        resultBuilder.setMoveToPosY(message.getMoveToPosY());

        // 需要使用时间戳同步操作
        resultBuilder.setMoveStartTime(startTime);

        GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();

        // 将移动消息广播
        Broadcaster.broadCast(newResult);
    }
}
