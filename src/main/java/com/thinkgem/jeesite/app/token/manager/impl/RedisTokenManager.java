package com.thinkgem.jeesite.app.token.manager.impl;

import com.thinkgem.jeesite.app.token.manager.TokenManager;
import com.thinkgem.jeesite.app.token.manager.entity.Token;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.util.Date;

@Service
public class RedisTokenManager implements TokenManager {

    private static JedisCluster cluster = SpringContextHolder.getBean(JedisCluster.class);
    private static String tokenKey = Global.getConfig("redis.keyPrefix") + ":" + "app" + ":";
    private static String tokenExpire = Global.getConfig("tokenExpire");

    @Override
    public Token createToken(String mobile) {
        String uuid = IdGen.uuid();
        cluster.setex(tokenKey + uuid, Integer.parseInt(tokenExpire), mobile);
        return new Token(mobile, uuid);
    }

    @Override
    public boolean checkToken(Token token) {
        if (cluster.exists(tokenKey + token.getToken())
                && cluster.get(tokenKey + token.getToken()).equals(token.getMobile())) {
            cluster.pexpire(tokenKey + token.getToken(), new Date().getTime());
            return true;
        }
        return false;
    }

    @Override
    @Deprecated
    public Token getToken(String auth) {
        return null;
    }


    @Override
    public void deleteToken(Token token) {
        if (cluster.exists(tokenKey + token.getToken())) {
            cluster.del(tokenKey + token.getToken());
        }
    }

    @Override
    @Deprecated
    public void clearToken(String mobile) {

    }
}
