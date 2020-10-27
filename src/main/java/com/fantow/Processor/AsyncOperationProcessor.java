package com.fantow.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

// 防止阻塞主线程，采用的异步操作处理器
// 现在的调用方式存在一个问题：就是通过单线程获取到的任务，交给多线程处理
// 最终的结果现在是直接返回给了WorkerEventLoopGroup
public class AsyncOperationProcessor {

    private static Logger logger = LoggerFactory.getLogger(AsyncOperationProcessor.class);

    private static final AsyncOperationProcessor asyncProcessor = new AsyncOperationProcessor();

    // 单线程池数组
    private static final ExecutorService[] esArray = new ExecutorService[8];

    private AsyncOperationProcessor(){

    }

    public static void init(){
        for(int i = 0;i < esArray.length;i++){
            final String threadName = "SingleThread - " + i;
            esArray[i] = Executors.newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName(threadName);
                    return thread;
                }
            });
        }
    }


//    private static ExecutorService service = Executors.newFixedThreadPool(10, new ThreadFactory() {
//        @Override
//        public Thread newThread(Runnable r) {
//            Thread thread = new Thread(r);
//            thread.setName("Async Thread");
//            return thread;
//        }
//    });

    public static AsyncOperationProcessor getInstance(){
        return asyncProcessor;
    }


    public void asyncProcess(IAsyncOperation op) throws ExecutionException, InterruptedException {
        if(op == null){
            logger.info("异步任务为空");
            return;
        }

        // 这里改成HashCode也行
        int bindId = op.getBindId();
        int index = bindId % esArray.length;

        ExecutorService service = esArray[index];

        service.submit(new Runnable() {
            @Override
            public void run() {
                op.doAsync();
                // 使用主线程的单线程执行op.finish()，从而使得op执行结果返回给主线程
                MainMsgProcessor.getInstance().process(() -> op.doFinish());
            }
        });
    }

}
