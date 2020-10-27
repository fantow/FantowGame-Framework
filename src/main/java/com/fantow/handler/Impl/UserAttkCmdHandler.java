package com.fantow.handler.Impl;

import com.fantow.DBOperation.LoginService;
import com.fantow.Entity.UserInfo;
import com.fantow.Entity.VictoryMessage;
import com.fantow.Utils.Broadcaster;
import com.fantow.Utils.UserManager;
import com.fantow.handler.ICmdHandler;
import com.fantow.message.GameMsgProtocol;
import com.fantow.mq.MQProducer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {

    private static Logger logger = LoggerFactory.getLogger(UserAttkCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd message) {
        if(ctx == null || message == null){
            return;
        }

        // 获取到攻击指令发起者Id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if(userId == null){
            return;
        }

        // 获取到被攻击用户Id
        UserInfo targetUserInfo = UserManager.getUserInfo(message.getTargetUserId());

        if(targetUserInfo == null){
            GameMsgProtocol.UserAttkResult.Builder builder = GameMsgProtocol.UserAttkResult.newBuilder();
            builder.setAttkUserId(userId);
            builder.setTargetUserId(-1);
            Broadcaster.broadCast(builder.build());
            return;
        }

        // 这里写死了，一刀 -20
        targetUserInfo.setHP(targetUserInfo.getHP() - 20);

        // 将攻击事件广播
        GameMsgProtocol.UserAttkResult.Builder builder = GameMsgProtocol.UserAttkResult.newBuilder();

        builder.setAttkUserId(userId);
        builder.setTargetUserId(targetUserInfo.getUserId());

        GameMsgProtocol.UserAttkResult result = builder.build();

        // 广播攻击消息
        Broadcaster.broadCast(result);


        GameMsgProtocol.UserSubtractHpResult.Builder newBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        newBuilder.setTargetUserId(targetUserInfo.getUserId());
        newBuilder.setSubtractHp(20);

        // 广播减血消息
        Broadcaster.broadCast(newBuilder.build());

        // 判断死亡逻辑
        GameMsgProtocol.UserDieResult.Builder userDieBuilder = GameMsgProtocol.UserDieResult.newBuilder();
        if(targetUserInfo.getHP() <= 0){
            userDieBuilder.setTargetUserId(targetUserInfo.getUserId());
            Broadcaster.broadCast(userDieBuilder.build());

            // 发送消息
            MQProducer.getInstance().sendMessage("victory",new VictoryMessage(userId,targetUserInfo.getUserId()));
        }

    }
}
