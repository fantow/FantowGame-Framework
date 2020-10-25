package com.fantow;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.log4j.PropertyConfigurator;

public class ServerMain {
    public static void main(String[] args) {
        PropertyConfigurator.configure(ServerMain.class.getClassLoader().getResourceAsStream("jog4j.properties"));

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();



    }
}
