package com.fantow.handler;

import com.fantow.handler.Impl.UserEntryCmdHandler;
import com.fantow.handler.Impl.UserMoveToCmdHandler;
import com.fantow.handler.Impl.WhoElseIsHereCmdHandler;
import com.fantow.message.GameMsgProtocol;
import com.google.protobuf.GeneratedMessageV3;

import java.util.HashMap;
import java.util.Map;

// 生成Handler的工厂类
public class CmdHandlerFactory {

    // class --> handler实例
    private static Map<Class<?>,ICmdHandler<? extends GeneratedMessageV3>> handlerMap = new HashMap<>();

    private CmdHandlerFactory(){

    }

    public static void init(){
        handlerMap.put(GameMsgProtocol.UserEntryCmd.class,new UserEntryCmdHandler());
        handlerMap.put(GameMsgProtocol.WhoElseIsHereCmd.class,new WhoElseIsHereCmdHandler());
        handlerMap.put(GameMsgProtocol.UserMoveToCmd.class,new UserMoveToCmdHandler());
    }


    public static ICmdHandler<? extends GeneratedMessageV3> getHandler(Class<?> msgClazz){
        if(msgClazz == null){
            return null;
        }

        return handlerMap.get(msgClazz);
    }


}
