package com.fantow.codec;

import com.fantow.message.GameMsgProtocol;
import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(GameMsgEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(ctx == null){
            logger.info("channelHandlerContext 为null");
            return;
        }

        int msgCode = -1;

        try {
            if(!(msg instanceof GeneratedMessageV3)){
                super.write(ctx,msg,promise);
                return;
            }

            // 处理用户UserEntry请求
            if(msg instanceof GameMsgProtocol.UserEntryResult){
                msgCode = GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE;
            }else if(msg instanceof GameMsgProtocol.WhoElseIsHereResult){
                // 处理用户WhoElseIsHere请求
                msgCode = GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE;

            } else{
                logger.error("无法识别的类型...");
                super.write(ctx,msg,promise);
                return;
            }

            // 获取到消息体
            byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();

            // 将消息的length，code和body封装到BinaryWebSocketFrame
            ByteBuf buf = ctx.alloc().buffer();

            buf.writeShort(msgBody.length);
            buf.writeShort(msgCode);
            buf.writeBytes(msgBody);

            BinaryWebSocketFrame outputFrame = new BinaryWebSocketFrame(buf);

            super.write(ctx,outputFrame,promise);
        }catch (Exception ex){
            logger.info(ex.getMessage(),ex);
        }

    }
}
