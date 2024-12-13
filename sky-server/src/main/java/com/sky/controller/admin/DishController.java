package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.impl.DishServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

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

    /**
     * 新增菜品
     * add new dish
     */
    @PostMapping
    @ApiOperation(value = "新增菜品")
    public Result addDish(@RequestBody DishDTO dishDTO){
        log.info("新增菜品，{}",dishDTO);
        dishService.addDish(dishDTO);
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
        return Result.success();
    }
}
