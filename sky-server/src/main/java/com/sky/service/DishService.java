package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品
     * add new dish
     */
    void addDish(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * dishes pagination query
     */
    PageResult dishPaginationQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     * Batch delete dishes
     */
    void deleteByIds(Long[] ids);

    /**
     * 根据id查询菜品
     * query dish by id
     */
    DishVO selectByIdWithFlavor(Long id);

    /**
     * 修改菜品
     * modify(update) dish
     */
    void updateDishWithFlavor(DishDTO dishDTO);

    /**
     * 调整菜品的起售/停售状态
     * Adjust the able/enable status of dishes
     */
    void changeDishSaleStatus(Integer status, Long id);

    /**
     * 根据分类id查找菜品
     * query dishes by category id
     */
    List<Dish> selectByCategoryId(Long categoryId);
}
