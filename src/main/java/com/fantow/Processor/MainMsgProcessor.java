package com.fantow.Processor;

import com.fantow.handler.CmdHandlerFactory;
import com.fantow.handler.ICmdHandler;
import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class MainMsgProcessor {

    private static Logger logger = LoggerFactory.getLogger(MainMsgProcessor.class);

    private ExecutorService service = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("MainMsgProcessorThread");
            return thread;
        }
    });

    private static final MainMsgProcessor processor = new MainMsgProcessor();

    private MainMsgProcessor(){

    }

    // 使用单例模式
    public static MainMsgProcessor getInstance(){
        return processor;
    }

    public void handle(ChannelHandlerContext channelHandlerContext,Object o){

        service.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("调用了线程：" + Thread.currentThread().getName() + "进行任务的处理");

                if(channelHandlerContext == null){
                    logger.info("当前channelHandlerContext为null");
                }

                try{
                    // 新客户端连接时，会使用该命令告知服务器端
                    ICmdHandler<? extends GeneratedMessageV3> handler = CmdHandlerFactory.getHandler(o.getClass());

                    if(handler != null){
                        handler.handle(channelHandlerContext,cast(o));
                    }
                }catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        });

    }


    private static <T extends GeneratedMessageV3> T cast(Object msg){
        if(msg == null){
            return null;
        }else{
            return (T) msg;
        }
    }

}
