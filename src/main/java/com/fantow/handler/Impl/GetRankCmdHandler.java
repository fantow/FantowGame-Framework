package com.fantow.handler.Impl;

import com.fantow.Entity.RankItemEntity;
import com.fantow.Rank.RankService;
import com.fantow.Utils.Broadcaster;
import com.fantow.Utils.RedisUtil;
import com.fantow.handler.ICmdHandler;
import com.fantow.message.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GetRankCmdHandler implements ICmdHandler<GameMsgProtocol.GetRankCmd> {

    private static List<RankItemEntity> list = new ArrayList<>();

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.GetRankCmd message) throws ExecutionException, InterruptedException {
        if(ctx == null || message == null){
            return;
        }


        // 这里getRankList()传入的是一个callback函数，并不是将list作为一个参数传入的
        RankService.getInstance().getRankList((list)->{
            if(list == null){
                list = Collections.emptyList();
            }

            GameMsgProtocol.GetRankResult.Builder rankResultBuilder = GameMsgProtocol.GetRankResult.newBuilder();

            for(RankItemEntity entity : list){
                if(entity == null){
                    continue;
                }

                GameMsgProtocol.GetRankResult.RankItem.Builder itemBuilder = GameMsgProtocol.GetRankResult.RankItem.newBuilder();
                itemBuilder.setRankId(entity.getRandId());
                itemBuilder.setUserId(entity.getUserId());
                itemBuilder.setWin(entity.getScore());
                itemBuilder.setUserName(entity.getUserName());
                itemBuilder.setHeroAvatar(entity.getHeroAvatar());

                rankResultBuilder.addRankItem(itemBuilder);
            }

            System.out.println("RankEntity 共有" + list.size());

            GameMsgProtocol.GetRankResult result = rankResultBuilder.build();

            ctx.writeAndFlush(result);
            return null;
        });
    }

//    // 更新击倒数排名
//    public void refreshRank(int winnerId,int loserId){
//        if(winnerId <= 0 || loserId <= 0){
//            return ;
//        }
//
//        Jedis jedis = RedisUtil.getJedis();
//
//        jedis.hincrBy("User_" + winnerId,"Win",1);
//        jedis.hincrBy("User_" + loserId,"Lose",1);
//
//        String winCountStr = jedis.hget("User_" + winnerId,"Win");
//        int winCount = Integer.parseInt(winCountStr);
//
//        jedis.zadd("Rank",winCount,String.valueOf(winnerId));
//    }
}
