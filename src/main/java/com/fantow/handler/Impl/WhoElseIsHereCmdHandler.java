package com.fantow.handler.Impl;

import com.fantow.Entity.UserInfo;
import com.fantow.Utils.Broadcaster;
import com.fantow.Utils.UserManager;
import com.fantow.handler.ICmdHandler;
import com.fantow.message.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class WhoElseIsHereCmdHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd message) {
        // 返回当前所有在线的用户信息
        // 如果没有这个方法，新连接的客户端无法感知之前连接的客户端。
        // 而旧客户端可以感知新连接的客户端

        System.out.println("服务器端接收到WhoElseIsHereCmd 请求");

        // 这个WhoElseIsHereResult是一个集合
        GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

        Integer mySelfId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        for(UserInfo userInfo : UserManager.listUser()){
            if(mySelfId != null && userInfo.getUserId() == mySelfId){
                System.out.println("检测重复..");
                continue;
            }

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();

            userInfoBuilder.setUserId(userInfo.getUserId());
            userInfoBuilder.setHeroAvatar(userInfo.getHeroAvatar());

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder moveStateBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();

            // 填充moveState结构
            moveStateBuilder.setFromPosX(userInfo.getMoveState().getFromPosx());
            moveStateBuilder.setFromPosY(userInfo.getMoveState().getFromPosy());
            moveStateBuilder.setToPosX(userInfo.getMoveState().getToPosx());
            moveStateBuilder.setToPosY(userInfo.getMoveState().getToPosy());
            moveStateBuilder.setStartTime(userInfo.getMoveState().getStartTime());

            userInfoBuilder.setMoveState(moveStateBuilder.build());

            resultBuilder.addUserInfo(userInfoBuilder.build());
        }

        GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
        Broadcaster.broadCast(newResult);
    }
}
