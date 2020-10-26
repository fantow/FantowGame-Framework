package com.fantow.handler;

import com.fantow.handler.Impl.*;
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

    // 需要更新
    public static void init(){
        handlerMap.put(GameMsgProtocol.UserEntryCmd.class,new UserEntryCmdHandler());
        handlerMap.put(GameMsgProtocol.WhoElseIsHereCmd.class,new WhoElseIsHereCmdHandler());
        handlerMap.put(GameMsgProtocol.UserMoveToCmd.class,new UserMoveToCmdHandler());
        handlerMap.put(GameMsgProtocol.UserAttkCmd.class,new UserAttkCmdHandler());
        handlerMap.put(GameMsgProtocol.UserLoginCmd.class,new UserLoginCmdHandler());
    }


    public static ICmdHandler<? extends GeneratedMessageV3> getHandler(Class<?> msgClazz){
        if(msgClazz == null){
            return null;
        }

        return handlerMap.get(msgClazz);
    }


}
