package com.fantow;

import com.fantow.Entity.UserInfo;
import com.fantow.message.GameMsgProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GameMessageHandler extends SimpleChannelInboundHandler<Object> {

    private static Logger logger = LoggerFactory.getLogger(GameMessageHandler.class);

    // 这是一个容器，底层用ConcurrentHashMap记录<ChannelId,Channel>
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static Map<Integer, UserInfo> userInfoMap = new HashMap<>();

    // 当建立一个channel后，将这个channel存入ChannelGroup中
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(ctx == null){
            logger.info("当前Channel 为null");
            return;
        }

        try {
            super.channelActive(ctx);
            channelGroup.add(ctx.channel());
            logger.info("将channel:{} 添加进ChannelGroup",ctx.channel());
        }catch (Exception ex){
            logger.error(ex.getMessage(),ex);
        }

    }

    // 当从Decoder的消息处理完，到这个Handler时
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if(channelHandlerContext == null){
            logger.info("当前channelHandlerContext为null");
        }

        try{
            // 新客户端连接时，会使用该命令告知服务器端
            if(o instanceof GameMsgProtocol.UserEntryCmd) {
                GameMsgProtocol.UserEntryCmd cmd = (GameMsgProtocol.UserEntryCmd) o;

                int userId = cmd.getUserId();
                String heroAvatar = cmd.getHeroAvatar();

                // 把接收到的消息广播给所有用户
                GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
                resultBuilder.setUserId(userId);
                resultBuilder.setHeroAvatar(heroAvatar);

                UserInfo userInfo = new UserInfo();
                userInfo.setUserId(userId);
                userInfo.setHeroAvatar(heroAvatar);

                userInfoMap.putIfAbsent(userId, userInfo);

                GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();

                // 其实不应该在这里调用的，但是前端的问题，导致WoElseIsHereCMD无法被先连接的客户端调用
                Iterator<Channel> iterator = channelGroup.iterator();
                while (iterator.hasNext()) {
                    Channel channel = iterator.next();
                    channel.writeAndFlush(newResult);
                }

//                channelHandlerContext.writeAndFlush(newResult);
            }else if(o instanceof GameMsgProtocol.WhoElseIsHereCmd){
                // 返回当前所有在线的用户信息
                // 如果没有这个方法，新连接的客户端无法感知之前连接的客户端。
                // 而旧客户端可以感知新连接的客户端

                // 这个WhoElseIsHereResult是一个集合
                GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

                for(UserInfo userInfo : userInfoMap.values()){
                    GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();

                    userInfoBuilder.setUserId(userInfo.getUserId());
                    userInfoBuilder.setHeroAvatar(userInfo.getHeroAvatar());

                    resultBuilder.addUserInfo(userInfoBuilder.build());
                }

                GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
                channelHandlerContext.writeAndFlush(newResult);
            }



        }catch (Exception ex){
            logger.error(ex.getMessage(),ex);
        }

    }


    // 同理，当一个channel不可用时，将该channel从ChannelGroup中移除
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(ctx == null){
            logger.info("当前channel 为null");
            return;
        }

        try {
            super.channelInactive(ctx);
            channelGroup.remove(ctx.channel());
            logger.info("将channel:{} 移除",ctx.channel());
        }catch (Exception ex){
            logger.error(ex.getMessage(),ex);
        }
    }
}
