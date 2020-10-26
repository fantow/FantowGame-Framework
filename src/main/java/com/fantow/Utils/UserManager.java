package com.fantow.Utils;

import com.fantow.Entity.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class UserManager {

    private static Logger logger = LoggerFactory.getLogger(UserManager.class);

    private static Map<Integer, UserInfo> userMap = new ConcurrentHashMap<>();

    private UserManager(){

    }

    public static void addUser(UserInfo userInfo){
        if(userInfo != null){
            userMap.putIfAbsent(userInfo.getUserId(),userInfo);
        }else {
            logger.info("添加的用户对象为空");
        }
    }

    public static void removeUser(Integer userId){
        if(userId != null){
            userMap.remove(userId);
        }else {
            System.out.println("删除的用户对象为空");
        }
    }

    // 获取到UserInfo的values
    public static Collection<UserInfo> listUser(){
        return userMap.values();
    }

}
