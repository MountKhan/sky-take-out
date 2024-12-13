package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;

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
}
