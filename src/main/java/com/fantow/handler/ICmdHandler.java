package com.fantow.handler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutionException;

public interface ICmdHandler<T extends GeneratedMessageV3>{

    void handle(ChannelHandlerContext ctx,T message) throws ExecutionException, InterruptedException;

}
