package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

public interface UserService {
    /**
     * 微信用户登录
     * wechat user login
     */
    User WeChatLogin(UserLoginDTO userLoginDTO);
}
