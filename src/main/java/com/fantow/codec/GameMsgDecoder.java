package com.fantow.codec;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import com.fantow.message.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// 协议结构 消息长度(2字节) 消息类型(2字节) 消息体
public class GameMsgDecoder extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(GameMsgDecoder.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        try {
            if (ctx == null || msg == null) {
                return;
            }

            // 如果传入的不是二进制类型，直接返回null
            if (!(msg instanceof BinaryWebSocketFrame)) {
                return;
            }

            BinaryWebSocketFrame webSocketFrame = (BinaryWebSocketFrame) msg;
            ByteBuf byteBuf = webSocketFrame.content();

            // 消息长度
            short contextLength = byteBuf.readShort();

            // 消息类型
            short contextType = byteBuf.readShort();

            // 消息内容
            byte[] msgBody = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(msgBody);

            GeneratedMessageV3 cmd = null;

            switch (contextType) {
                case GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE:
                    // 将使用msgBody中的数字填充UserEntryCmd的结构
                    cmd = GameMsgProtocol.UserEntryCmd.parseFrom(msgBody);
                    break;
                case GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE:
                    cmd = GameMsgProtocol.WhoElseIsHereCmd.parseFrom(msgBody);

                default:
                    break;
            }

            // 如果cmd != null
            // 触发下一个ChannelInboundHandler的channelRead()方法
            if (cmd != null) {
                ctx.fireChannelRead(cmd);
            }
        }catch (Exception ex){
            logger.error(ex.getMessage(),ex);
        }

    }
}
