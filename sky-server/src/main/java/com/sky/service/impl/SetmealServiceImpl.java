package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    public SetmealServiceImpl(SetmealMapper setmealMapper) {
        this.setmealMapper = setmealMapper;
    }

    /**
     * 新增套餐
     * add setmeal
     */
    @Transactional
    @Override
    public void addSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);

        //获取套餐id
        //get setmeal id
        Long setmealId = setmeal.getId();

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }

        //在setmeal_dish增添对应菜品(多条)
        //add dishes in setmeal_dish(multiple items)
        setmealDishMapper.insert(setmealDishes);
    }

    /**
     * 分页查询套餐
     * setmeal pagination query
     */
    @Override
    public PageResult setmealPaginationQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.setmealPaginationQuery(setmealPageQueryDTO);
        long total = page.getTotal();
        List<SetmealVO> result = page.getResult();
        return new PageResult(total, result);
    }

    /**
     * 根据id查询套餐
     * query setmeal by id
     */
    @Override
    public SetmealVO selectById(Long id) {
        Setmeal setmeal = setmealMapper.selectById(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.selectById(id);

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐
     * update setmeal
     */
    @Transactional
    @Override
    public void update(SetmealDTO setmealDTO) {
        //修改套餐
        //update setmeal
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(dish -> dish.setSetmealId(setmeal.getId()));

        //修改关联的菜品(先删除后添加)
        //update associated setmeal dishes（delete odd items, then add new items）
        setmealDishMapper.deleteById(setmeal.getId());
        setmealDishMapper.insert(setmealDishes);
    }

    /**
     * 批量删除套餐
     * delete setmeal in batches
     */
    @Transactional
    @Override
    public void deleteInBatches(Long[] ids) {
        //起售中的套餐不能删除
        //setmeal on sale cannot be deleted
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.selectById(id);
            if (setmeal.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        //删除套餐
        //delete setmeal in batches
        setmealMapper.deleteInBatches(ids);

        //删除关联的setmeal dishes
        //delete associated setmeal dishes
        setmealDishMapper.deleteInBatches(ids);
    }

    /**
     * 套餐的起售/停售
     * able/enable setmeal
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        // 包含停售菜品的套餐不能起售
        // can't enable setmeal which contains disabled dishes
        if (status == StatusConstant.ENABLE) {
            List<SetmealDish> setmealDishes = setmealDishMapper.selectById(id);
            for (SetmealDish setmealDish : setmealDishes) {
                Long dishId = setmealDish.getDishId();
                Dish dish = dishMapper.getById(dishId);
                if (dish.getStatus() == 0) {
                    throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }
        Setmeal setmeal = Setmeal.builder().id(id).status(status).build();
        setmealMapper.update(setmeal);
    }

    /**
     * 用户端：根据分类id查询当前可获得的套餐
     * user:Query the currently enable setmeal by categoryId
     */
    @Override
    public List<Setmeal> select(Setmeal setmeal) {
        return setmealMapper.select(setmeal);
    }

    /**
     * 根据套餐id查询包含的菜品
     * user:query associated dishes by categoryId
     */
    @Override
    public List<DishItemVO> getDishByCategoryId(Long id) {
        return setmealMapper.getDishesBySetmealId(id);
    }

}
