package com.thinkgem.jeesite.app.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.entity.technician.AppServiceTechnicianInfo;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

@Service
public class TokenManager {

    private JedisCluster cluster = SpringContextHolder.getBean(JedisCluster.class);
    /**
     * redis 中的key
     */

    private String tokenKey;

    /**
     * key 的有效期
     */
    private int expire;

    public TokenManager() {
        String key = Global.getConfig("redis.keyPrefix");
        String app = Global.getConfig("redis.appPrefix");
        String expire = Global.getConfig("redis.appExpire");
        this.tokenKey = key + ":" + app + ":";
        this.expire = Integer.parseInt(expire);
    }

    public Token createToken(AppServiceTechnicianInfo entity) {
        String phone = entity.getTechPhone();
        String uuid = IdGen.uuid();
        Token token = new Token(uuid);
        token.setPhone(phone);

        cluster.setex(tokenKey + uuid, expire, token.toString());
        cluster.lpush(tokenKey + phone,uuid);

        return token;
    }

    public void updateToken(Token token) {
        cluster.setex(tokenKey + token.getToken(), expire, token.toString());
    }

    public boolean verifyToken(Token token) {
        try {
            String s = cluster.get(tokenKey + token.getToken());
            if (StringUtils.isNotBlank(s)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteToken(String token) {
        cluster.del(tokenKey + token);
    }

}
