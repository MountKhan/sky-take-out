package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     * add new dish
     */
    @Transactional
    @Override
    public void addDish(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //向数据库的dish插入数据（1条）
        //Insert data into the database dish (1 item)
        dishMapper.insert(dish);

        //获取dishId
        //get dishId
        Long dishId = dish.getId();

        //向数据库的dish_flavor插入数据（多条）
        //Insert data to the database dish_flavor (multiple items)
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {

            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishId);
            }

            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * dishes pagination query
     */
    @Override
    public PageResult dishPaginationQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.dishPaginationQuery(dishPageQueryDTO);
        long total = page.getTotal();
        List<DishVO> result = page.getResult();
        return new PageResult(total, result);
    }

    /**
     * 批量删除菜品
     * Batch delete dishes
     */
    @Transactional
    @Override
    public void deleteByIds(Long[] ids) {
        //是否存在起售中的菜品
        //can't delete dish which is for sale
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //是否与套餐关联
        //can't delete dish that have benn associated with dish_flavor
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        for (Long id : ids) {
            //删除菜品
            //delete dish by id
            dishMapper.deleteById(id);
            //删除关联 flavor
            //delete associated flavor
            dishFlavorMapper.deleteById(id);
        }
    }
}
