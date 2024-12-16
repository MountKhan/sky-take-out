package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    /**
     * 新增套餐
     * add setmeal
     */
    void addSetmeal(SetmealDTO setmealDTO);

    /**
     * 分页查询套餐
     * setmeal pagination query
     */
    PageResult setmealPaginationQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询套餐
     * query setmeal by id
     */
    SetmealVO selectById(Long id);

    /**
     * 修改套餐
     * update setmeal
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 批量删除套餐
     * delete setmeal in batches
     */
    void deleteInBatches(Long[] ids);

    /**
     * 套餐的起售/停售
     * able/enable setmeal
     */
    void startOrStop(Integer status, Long id);

    /**
     * 用户端：根据分类id查询当前可获得的套餐
     * user:Query the currently enable setmeal by categoryId
     */
    List<Setmeal> select(Setmeal setmeal);

    /**
     * 根据套餐id查询包含的菜品
     * user:query associated dishes by categoryId
     */
    List<DishItemVO> getDishByCategoryId(Long id);
}
