package com.fantow;

import com.fantow.Processor.MainMsgProcessor;
import com.fantow.Utils.Broadcaster;
import com.fantow.Utils.UserManager;
import com.fantow.message.GameMsgProtocol;
import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameMessageHandler extends SimpleChannelInboundHandler<Object> {

    private static Logger logger = LoggerFactory.getLogger(GameMessageHandler.class);

//    private MainMsgProcessor processor = MainMsgProcessor.getInstance();


    // 当建立一个channel后，将这个channel存入ChannelGroup中
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(ctx == null){
            logger.info("当前Channel 为null");
            return;
        }

        try {
            super.channelActive(ctx);

            Broadcaster.addChannel(ctx.channel());;
            logger.info("将channel:{} 添加进ChannelGroup",ctx.channel());
        }catch (Exception ex){
            logger.error(ex.getMessage(),ex);
        }
    }

    // 当从Decoder的消息处理完，到这个Handler时
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
//        processor.handle(channelHandlerContext,o);
        MainMsgProcessor.getInstance().handle(channelHandlerContext,o);

//        System.out.println("调用了线程：" + Thread.currentThread().getName() + "进行任务的处理");
//
//        if(channelHandlerContext == null){
//            logger.info("当前channelHandlerContext为null");
//        }
//
//        try{
//            // 新客户端连接时，会使用该命令告知服务器端
//            ICmdHandler<? extends GeneratedMessageV3> handler = CmdHandlerFactory.getHandler(o.getClass());
//
//            if(handler != null){
//                handler.handle(channelHandlerContext,cast(o));
//            }
//        }catch (Exception ex) {
//            logger.error(ex.getMessage(), ex);
//        }


    }


    // 同理，当一个channel不可用时，将该channel从ChannelGroup中移除
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(ctx == null){
            logger.info("当前channel 为null");
            return;
        }

        try {
//            super.channelInactive(ctx);

            // 发送客户端离线消息
            Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

            if(userId == null){
                logger.info("离线请求发送失败，获取到的userId 为 null");
                return;
            }
            UserManager.removeUser(userId);

            GameMsgProtocol.UserQuitResult.Builder resultBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
            resultBuilder.setQuitUserId(userId);

            GameMsgProtocol.UserQuitResult newResult = resultBuilder.build();

            // 需要主动给其他客户端推送离线消息
            Broadcaster.broadCast(newResult);

            Broadcaster.removeChannel(ctx.channel());
            logger.info("将channel:{} 移除",ctx.channel());
        }catch (Exception ex){
            logger.error(ex.getMessage(),ex);
        }
    }


    private static <T extends GeneratedMessageV3> T cast(Object msg){
        if(msg == null){
            return null;
        }else{
            return (T) msg;
        }
    }


}
