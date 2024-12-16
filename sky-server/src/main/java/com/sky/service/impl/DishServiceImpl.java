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

import java.util.ArrayList;
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
        if (setmealIds != null && !setmealIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
//        for (Long id : ids) {
//            //删除菜品
//            //delete dish by id
//            dishMapper.deleteById(id);
//            //删除关联 flavor
//            //delete associated flavor
//            dishFlavorMapper.deleteById(id);
//        }

        //批量删除菜品
        //delete dishes in batches
        dishMapper.deleteInBatches(ids);

        //批量删除关联的口味数据
        //delete associated flavor in batches
        dishFlavorMapper.deleteInBatches(ids);

    }

    /**
     * 根据id查询菜品
     * query dish by id
     */
    @Override
    public DishVO selectByIdWithFlavor(Long id) {
        //查询菜品
        //query dish
        Dish dish = dishMapper.getById(id);

        //查询关联的口味
        //query flavor
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        //封装数据
        //encapsulation
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    /**
     * 修改菜品
     * modify(update) dish
     */
    @Transactional
    @Override
    public void updateDishWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        //修改菜品
        //update dish
        dishMapper.update(dish);

        //修改关联的口味(先删除后新增)
        //update associated flavor(delete first,then add new flavor)
        dishFlavorMapper.deleteById(dishDTO.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dish.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 调整菜品的起售/停售状态
     * Adjust the able/enable status of dishes
     */
    @Override
    public void changeDishSaleStatus(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setStatus(status);
        dish.setId(id);

        dishMapper.update(dish);
    }

    /**
     * 根据分类id查找菜品
     * query dishes by category id
     */
    @Override
    public List<Dish> selectByCategoryId(Long categoryId) {
        return dishMapper.selectByCategoryId(categoryId);
    }

    /**
     * 根据分类id查询菜品（包含口味）
     * Search dishes by category id (including flavors)
     */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.select(dish);
        List<DishVO> dishVOList = new ArrayList<>();

        //给有关联口味的菜品添加口味
        //Add additional flavors to dishes that already have associated ones
        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());
            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }
        return dishVOList;
    }
}
