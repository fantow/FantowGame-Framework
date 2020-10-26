package com.fantow.DBOperation.DB;

import com.fantow.Entity.UserEntity;
import org.apache.ibatis.annotations.Param;

public interface IUserDao {

    // 根据用户名称获取用户实体
    UserEntity getUserByName(@Param("userName") String userName);

    // 添加用户实体
    void insertInto(UserEntity newUserEntity);
}
