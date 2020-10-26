package com.fantow.Utils;

import com.fantow.message.GameMsgProtocol;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GameMsgRecognizer {

    private static final Logger logger = LoggerFactory.getLogger(GameMsgRecognizer.class);

    // code --> msgBody
    private static final Map<Integer, GeneratedMessageV3> codeAndMsgBodyMap = new HashMap<>();

    // class --> code
    private static final Map<Class<?>,Integer> msgClazzAndCodeMap = new HashMap<>();

    private GameMsgRecognizer(){

    }

    public static void init(){
        Class<?>[] classes = GameMsgProtocol.class.getDeclaredClasses();

        for(Class<?> clazz : classes){

            // 只处理与指定请求/响应相关的类型
            if(!GeneratedMessageV3.class.isAssignableFrom(clazz)){
                continue;
            }

            String clazzName = clazz.getSimpleName().toLowerCase();

            for(GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()){
                String strMsgCode = msgCode.name();

                strMsgCode = strMsgCode.replaceAll("_","").toLowerCase();

                if(!strMsgCode.startsWith(clazzName)){
                    continue;
                }

                try{
                    Object returnObj = clazz.getDeclaredMethod("getDefaultInstance").invoke(clazz);
//                    logger.info("{} <==> {}",clazz.getName(),msgCode.getNumber());

                    codeAndMsgBodyMap.put(msgCode.getNumber(),(GeneratedMessageV3) returnObj);

                    msgClazzAndCodeMap.put(clazz,msgCode.getNumber());
                }catch (Exception ex){
                    logger.error(ex.getMessage(),ex);
                }
            }
        }
    }

    // 根据消息编号，获取构建者
    public static Message.Builder getBuilderByMsgCode(int msgCode){
        if(msgCode < 0){
            return null;
        }

        GeneratedMessageV3 msg = codeAndMsgBodyMap.get(msgCode);
        if(msg == null){
            return null;
        }

        // 获取到指定类型的消息builder
        return msg.newBuilderForType();
    }

    // 根据消息的类型，获取对应的消息编号
    public static int getMsgCodeByMsgClass(Class<?> msgClazz){
        if(msgClazz == null){
            return -1;
        }

        Integer msgCode = msgClazzAndCodeMap.get(msgClazz);
        if(msgCode != null){
            return msgCode;
        }else{
            return -1;
        }


    }




}
