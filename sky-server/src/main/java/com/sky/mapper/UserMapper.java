package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据open id查询用户
     * select user by open id
     */
    @Select("select * from user where openid=#{openId}")
    User getByOpenId(String openId);

    /**
     * 创建新用户
     * insert user
     */
    void insert(User user);
}
