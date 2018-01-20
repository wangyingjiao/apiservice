package com.thinkgem.jeesite.app.interceptor;

import com.alibaba.fastjson.JSON;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.entity.technician.AppServiceTechnicianInfo;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;
import springfox.documentation.spring.web.json.Json;

import java.util.List;

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
        clearToken(token);
        cluster.setex(tokenKey + uuid, expire, token.toString());
        cluster.lpush(tokenKey + phone, uuid);
        cluster.expire(tokenKey + phone, expire);

        return token;
    }

    public void updateToken(Token token) {
        cluster.expire(tokenKey + token.getToken(), expire);
        cluster.expire(tokenKey + token.getPhone(), expire);
    }

    //将Json类型的token转换成String  核实token
    public Token verifyToken(Token token) {
        try {
            //uuid
            String s = cluster.get(tokenKey + token.getToken());
            Token object = JSON.parseObject(s, Token.class);
            return object;
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteToken(String token) {
        cluster.del(tokenKey + token);
    }

    public void clearToken(Token token) {
        String phone = token.getPhone();
        if (StringUtils.isNotBlank(phone)) {
            Long llen = cluster.llen(tokenKey + phone);
            List<String> uuids = cluster.lrange(tokenKey + phone, 0, llen - 1);
            for (String uuid : uuids) {
                cluster.del(tokenKey + uuid);
            }
        }
        cluster.del(tokenKey + phone);
    }

}
