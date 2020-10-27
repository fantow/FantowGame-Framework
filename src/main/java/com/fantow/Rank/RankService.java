package com.fantow.Rank;


import com.alibaba.fastjson.JSONObject;
import com.fantow.Entity.RankItemEntity;
import com.fantow.Processor.AsyncOperationProcessor;
import com.fantow.Processor.IAsyncOperation;
import com.fantow.Processor.MainMsgProcessor;
import com.fantow.Utils.RedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

// 处理排行榜逻辑
public class RankService {

    private static final RankService rankService = new RankService();

    public RankService() {

    }

    public static RankService getInstance(){
        return rankService;
    }


    public void getRankList(Function<List<RankItemEntity>,Void> callback) throws ExecutionException, InterruptedException {

        IAsyncOperation asyncGetRank = new AsyncGetRank(){
            @Override
            public void doFinish() {
                callback.apply(this.getRankList());
            }
        };

        AsyncOperationProcessor.getInstance().asyncProcess(asyncGetRank);

    }

    // 更新击倒数排名
    public void refreshRank(int winnerId,int loserId){
        if(winnerId <= 0 || loserId <= 0){
            System.out.println("存在空Id");
            return ;
        }

        Jedis jedis = RedisUtil.getJedis();

        jedis.hincrBy("User_" + winnerId,"Win",1);
        jedis.hincrBy("User_" + loserId,"Lose",1);

        String winCountStr = jedis.hget("User_" + winnerId,"Win");
        int winCount = Integer.parseInt(winCountStr);

        jedis.zadd("Rank",winCount,String.valueOf(winnerId));
    }



    class AsyncGetRank implements IAsyncOperation{

        List<RankItemEntity> list = new ArrayList<>();

        private Jedis jedis = RedisUtil.getJedis();

        public List<RankItemEntity> getRankList(){
            return list;
        }


        @Override
        public void doAsync() {
            if(jedis == null){
                return;
            }

            AtomicInteger rankCount = new AtomicInteger(1);
            List<RankItemEntity> list1 = new ArrayList<>();

            // 获取到前10个排名
            Set<Tuple> valueSet = jedis.zrevrangeWithScores("Rank", 0, 9);
            for(Tuple tuple : valueSet){
                if(tuple == null){
                    continue;
                }

                // userId
                int userId = Integer.parseInt(tuple.getElement());

                // 获取用户信息
                // 在redis中，哈希结构相当于一个Map结构
                String jsonStr = jedis.hget("User_" + userId ,"BasicInfo");

                if(jsonStr == null){
                    continue;
                }

                RankItemEntity itemEntity = new RankItemEntity();
                itemEntity.setUserId(userId);
                itemEntity.setRandId(rankCount.addAndGet(1));
                itemEntity.setScore((int)tuple.getScore());

                JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                itemEntity.setUserName(jsonObject.getString("userName"));
                itemEntity.setHeroAvatar(jsonObject.getString("heroAvatar"));

                list1.add(itemEntity);
            }


            list = list1;
        }

    }

}
