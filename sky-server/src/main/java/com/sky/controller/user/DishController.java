package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品（包含口味）
     * Search dishes by category id (including flavors)
     */
    @GetMapping("/list")
    @ApiOperation(value = "根据分类id查询菜品")
    public Result<List<DishVO>> selectDishesByCategoryId(Long categoryId) {
        log.info("根据分类id查询菜品，{}", categoryId);

        //构造redis中的key，格式为：dish_categoryId
        //Construct the key in Redis with the format: dish_categoryId
        String key = "dish_" + categoryId;

        //查询redis是否存在菜品数据
        // Check if the dish data exists in Redis
        List<DishVO> listInRedis = (List<DishVO>) redisTemplate.opsForValue().get(key);

        if(listInRedis != null && !listInRedis.isEmpty()){
            //如果存在，直接返回，无序查询数据库
            // If it exists, return it directly without querying the database
            return Result.success(listInRedis);
        }


        //如果不存在，查询数据库，将查询到的数据放入redis
        // If it doesn't exist, query the database and store the retrieved data in Redis
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE).build();
        List<DishVO> list = dishService.listWithFlavor(dish);
        redisTemplate.opsForValue().set(key,list);
        return Result.success(list);
    }
}
