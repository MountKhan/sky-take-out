package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "店铺相关接口")
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置店铺营业状态
     * Set the store operating status
     */
    @PutMapping("/{status}")
    @ApiOperation(value = "设置店铺营业状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺营业状态，{}",status == 1 ? "open":"close");

        redisTemplate.opsForValue().set(KEY,status);

        return Result.success();
    }

    /**
     * 获取店铺当前营业状态
     * get current stop operating status
     */
    @GetMapping("/status")
    @ApiOperation(value = "获取店铺当前营业状态")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取店铺当前营业状态为 {}",status==1?"open":"close");
        return Result.success(status);
    }
}
