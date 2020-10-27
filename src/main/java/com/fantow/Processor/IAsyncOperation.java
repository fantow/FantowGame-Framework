package com.fantow.Processor;

public interface IAsyncOperation {

    // 获取绑定的线程Id
    default int getBindId(){
        return 0;
    }

    // 执行异步操作
    void doAsync();

    // 执行完成逻辑
    default void doFinish(){

    }
}
