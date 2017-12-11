package com.thinkgem.jeesite.app.token.manager;

import com.thinkgem.jeesite.app.token.manager.entity.Token;

public interface TokenManager {
    /**
     * 技师登录后新建token
     * @param username
     * @return
     */
    Token createToken(String mobile);


    /**
     * 检查token是否有啸
     * @param token
     */
    boolean checkToken(Token token);

    /**
     * 通过字符串获取token
     * @param auth
     */
    @Deprecated
    Token getToken(String auth);

    /**
     * 删除token
     * @param token
     */
    void deleteToken(Token token);

    /**
     * 清除用户所有token
     * @param username
     */
    @Deprecated
    void clearToken(String mobile);
}
