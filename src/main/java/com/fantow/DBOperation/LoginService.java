package com.fantow.DBOperation;

import com.alibaba.fastjson.JSONObject;
import com.fantow.DBOperation.DB.IUserDao;
import com.fantow.Entity.UserEntity;
import com.fantow.Processor.AsyncOperationProcessor;
import com.fantow.Processor.IAsyncOperation;
import com.fantow.Utils.RedisUtil;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class LoginService {

    // 日志对象
    static private final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    // 单例对象
    static private final LoginService _instance = new LoginService();

    private static AsyncOperationProcessor asyncOperationProcessor = AsyncOperationProcessor.getInstance();

    // 私有化类默认构造器
    private LoginService() {
    }

    public static LoginService getInstance() {
        return _instance;
    }

    // 通过添加一个回调函数，处理异步请求成功后的逻辑调用
    // Function<T,R> 其中T为参数，R为返回值类型
    public void userLogin(String userName, String password, Function<UserEntity,Void> callback) throws ExecutionException, InterruptedException {
        if (null == userName ||
                null == password) {
            return;
        }

        // 这样写，可以通过内部类的方式，在类的方法中调用callback函数，如果直接在写类的时候覆盖doFinish()方法，是无法获取到这个callback的
        AsyncGetUserEntity asyncOperation = new AsyncGetUserEntity(userName, password){

            // doFinish()会回到MainMsgProcessor中执行
            @Override
            public void doFinish() {
                if(callback != null){
                    callback.apply(getResultEntity());
                }
            }

            @Override
            public int getBindId() {
                return userName.hashCode();
            }
        };

        asyncOperationProcessor.asyncProcess(asyncOperation);
    }

    // 更新Redis中的用户数据
    private void updateBasicInfoInRedis(UserEntity userEntity){
        Jedis jedis = RedisUtil.getJedis();

        if(jedis == null){
            LOGGER.info("Jedis 为空");
        }

        int userId = userEntity.userId;

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("userName",userEntity.userName);
        jsonObject.put("heroAvatar",userEntity.heroAvatar);

        jedis.hset("User_" + userId,"BasicInfo",jsonObject.toJSONString());


    }




    public class AsyncGetUserEntity implements IAsyncOperation {
        private final String userName;
        private final String password;
        private UserEntity resultEntity;

        public AsyncGetUserEntity(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        public UserEntity getResultEntity() {
            return resultEntity;
        }

        @Override
        public int getBindId() {
            return 0;
        }

        @Override
        public void doAsync() {
            try (SqlSession mySqlSession = MySqlSessionFactory.openSession()) {
                // 获取 DAO 对象,
                IUserDao dao = mySqlSession.getMapper(IUserDao.class);

                // 更间用户名称获取用户实体
                UserEntity userEntity = dao.getUserByName(userName);

                LOGGER.info("使用异步线程：" + Thread.currentThread().getName());

                if (null != userEntity) {
                    // 判断用户密码
                    if (!password.equals(userEntity.password)) {
                        // 用户密码错误,
                        LOGGER.error(
                                "用户密码错误, userId = {}, userName = {}",
                                userEntity.userId,
                                userName
                        );

                        throw new RuntimeException("用户密码错误");
                    }
                } else {
                    // 如果用户实体为空, 则新建用户!
                    userEntity = new UserEntity();
                    userEntity.userName = userName;
                    userEntity.password = password;
                    userEntity.heroAvatar = "Hero_Shaman"; // 默认使用萨满

                    // 将用户实体添加到数据库
                    dao.insertInto(userEntity);
                }

                if(userEntity != null){
                    resultEntity = userEntity;
                }

                // 将数据同步到Redis中
                LoginService.getInstance().updateBasicInfoInRedis(userEntity);


//                // 通过Callback.apply()调用回调函数
//                callback.apply(userEntity);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                return;
            }
        }
    }

}
