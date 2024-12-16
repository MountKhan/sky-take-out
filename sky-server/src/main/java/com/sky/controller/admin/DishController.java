package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.impl.DishServiceImpl;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 * dish management
 */
@RestController
@RequestMapping("admin/dish")
@Slf4j
@Api(tags = "菜品管理")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * add new dish
     */
    @PostMapping
    @ApiOperation(value = "新增菜品")
    public Result addDish(@RequestBody DishDTO dishDTO){
        log.info("新增菜品，{}",dishDTO);
        dishService.addDish(dishDTO);

        String key = "dish_" + dishDTO.getCategoryId();

        //清理缓存数据
        //delete from cache
        cleanCache(key);


        return Result.success();
    }

    /**
     * 菜品分页查询
     * dishes pagination query
     */
    @GetMapping("/page")
    @ApiOperation(value = "菜品分页查询")
    public Result<PageResult> dishesPaginationQuery(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询，{}",dishPageQueryDTO);
        PageResult pageResult= dishService.dishPaginationQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     * Batch delete dishes
     */
    @DeleteMapping
    @ApiOperation(value = "删除菜品")
    public Result deleteByIds(Long[] ids){
        log.info("删除菜品，{}", Arrays.toString(ids));
        dishService.deleteByIds(ids);

        //清理所有菜品缓存数据(所有以dish_开头的数据)
        //delete all dishes in cache(All data starting with dish_)
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 根据id查询菜品
     * query dish by id
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询菜品")
    public Result<DishVO> selectDishById(@PathVariable Long id){
        log.info("根据id查询菜品，{}",id);
        DishVO dishVO = dishService.selectByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * modify(update) dish
     */
    @PutMapping
    @ApiOperation(value = "修改菜品")
    public Result updateDish(@RequestBody DishDTO dishDTO){
        log.info("修改菜品，{}",dishDTO);
        dishService.updateDishWithFlavor(dishDTO);

        //清理所有菜品缓存数据(所有以dish_开头的数据)
        //delete all dishes in cache(All data starting with dish_)
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 调整菜品的起售/停售状态
     * Adjust the able/enable status of dishes
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "调整菜品的起售/停售状态")
    public Result changeDishSaleStatus(@PathVariable Integer status,Long id){
        log.info("根据id更改菜品的起售，停售状态");
        dishService.changeDishSaleStatus(status,id);

        //清理所有菜品缓存数据(所有以dish_开头的数据)
        //delete all dishes in cache(All data starting with dish_)
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 根据分类id查找菜品
     * query dishes by category id
     */
    @GetMapping("/list")
    @ApiOperation(value = "根据分类id查找菜品")
    public Result<List<Dish>> selectByCategoryId(Long categoryId){
        log.info("根据分类id查找菜品,{}",categoryId);
        List<Dish> list = dishService.selectByCategoryId(categoryId);
        return Result.success(list);
    }

    /**
     * 清理缓存数据
     * clean redis cache
     */
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);

    }
}
