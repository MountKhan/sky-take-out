package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    //微信登录接口地址
    //WeChat login url
    public static final String WeChatLoginUrl = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    /**
     * 微信用户登录
     * wechat user login
     */
    @Override
    public User WeChatLogin(UserLoginDTO userLoginDTO) {

        String openid = getOpenId(userLoginDTO.getCode());

        //判断open id是否为空
        // Check whether the open id is empty
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断当前用户是否是新用户
        // Check whether the current user is a new user
        User user = userMapper.getByOpenId(openid);


        //新用户：自动注册
        // If new user: Automatic registration
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        //返回用户对象
        // Return the user object
        return user;
    }

    /**
     * 调用微信官方的接口服务，获取微信登录用户的open id
     * Call the official WeChat interface service to obtain the open id of the WeChat login user
     */
    private String getOpenId(String code){
        //调用微信接口服务，获得当前用户的open id
        // Call the WeChat interface service to obtain the open id of the current user
        HashMap<String,String> map = new HashMap<>();
        map.put("appId",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");

        String json = HttpClientUtil.doGet(WeChatLoginUrl, map);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");

        return openid;
    }
}
