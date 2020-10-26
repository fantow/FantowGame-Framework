package com.fantow.handler.Impl;

import com.fantow.DBOperation.LoginService;
import com.fantow.Entity.UserEntity;
import com.fantow.Entity.UserInfo;
import com.fantow.Utils.UserManager;
import com.fantow.handler.ICmdHandler;
import com.fantow.message.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {

    static private final Logger LOGGER = LoggerFactory.getLogger(UserLoginCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd message) {
        if (null == ctx ||
                null == message) {
            return;
        }

        String userName = message.getUserName();
        String password = message.getPassword();

        LOGGER.info(
                "用户登陆, userName = {}, password = {}",
                userName,
                password
        );

        // 用户登陆
        UserEntity userEntity = null;

        try {
            userEntity = LoginService.getInstance().userLogin(
                    userName, password
            );
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return;
        }

        if (null == userEntity) {
            LOGGER.error("用户登陆失败, userName = {}", message.getUserName());
            return;
        }

        LOGGER.info(
                "用户登陆成功, userId = {}, userName = {}",
                userEntity.userId,
                userEntity.userName
        );

        // 新建用户,
        UserInfo newUser = new UserInfo();

        newUser.setUserId(userEntity.userId);
        newUser.setUserName(userEntity.userName);
        newUser.setHeroAvatar(userEntity.heroAvatar);
        newUser.setHP(100);

        // 并将用户加入管理器
        UserManager.addUser(newUser);

        // 将用户 Id 附着到 Channel
        ctx.channel().attr(AttributeKey.valueOf("userId")).set(newUser.getUserId());

        // 登陆结果构建者
        GameMsgProtocol.UserLoginResult.Builder
                resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();
        resultBuilder.setUserId(newUser.getUserId());
        resultBuilder.setUserName(newUser.getUserName());
        resultBuilder.setHeroAvatar(newUser.getHeroAvatar());

        // 构建结果并发送
        GameMsgProtocol.UserLoginResult newResult = resultBuilder.build();
        ctx.writeAndFlush(newResult);
    }
}
