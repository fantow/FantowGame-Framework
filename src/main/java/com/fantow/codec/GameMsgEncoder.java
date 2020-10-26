package com.fantow.codec;

import com.fantow.Utils.GameMsgRecognizer;
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


            msgCode = GameMsgRecognizer.getMsgCodeByMsgClass(msg.getClass());
            if(msgCode == -1){
                logger.info("无法识别的类型...");
                return;
            }


            // 获取到消息体
            byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();

            // 将消息的length，code和body封装到BinaryWebSocketFrame
            ByteBuf buf = ctx.alloc().buffer();

            buf.writeShort((short)msgBody.length);
            buf.writeShort((short) msgCode);
            buf.writeBytes(msgBody);

            BinaryWebSocketFrame outputFrame = new BinaryWebSocketFrame(buf);

            super.write(ctx,outputFrame,promise);
        }catch (Exception ex){
            logger.info(ex.getMessage(),ex);
        }

    }
}
