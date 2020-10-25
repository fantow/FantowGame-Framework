package com.fantow;

import com.fantow.codec.GameMsgDecoder;
import com.fantow.codec.GameMsgEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMain {

    private static Logger logger = LoggerFactory.getLogger(ServerMain.class);

    private static int PORT = 12345;

    public static void main(String[] args) {
        PropertyConfigurator.configure(ServerMain.class.getClassLoader().getResourceAsStream("log4j.properties"));

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                new HttpServerCodec(),
                                // 确保下一个Handler中只会接收到一个完成的HTTP请求或响应
                                new HttpObjectAggregator(65535),
                                // 处理Websocket升级握手相关操作
                                new WebSocketServerProtocolHandler("/websocket"),
                                // 自定义的消息解码器
                                new GameMsgDecoder(),
                                new GameMsgEncoder(),
                                new GameMessageHandler()
                            );

                        }
                    });

//            BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
            bootstrap.option(ChannelOption.SO_BACKLOG,128);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);

            ChannelFuture channelFuture = bootstrap.bind(PORT).sync();

            if(channelFuture.isSuccess()){
                logger.info("服务器启动成功......");
            }

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
