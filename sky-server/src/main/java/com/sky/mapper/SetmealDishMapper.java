package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据dish的id数组查询关联的setmeal
     * Queries the associated setmeal by dish ids
     */
    List<Long> getSetmealIdsByDishIds(Long[] ids);

    /**
     * 给套餐里添加菜品
     * add dishes in setmeal_dish(multiple items)
     */
    void insert(List<SetmealDish> setmealDishes);

    /**
     * 根据id查找setmeal
     * select by setmeal id
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> selectById(Long id);

    /**
     * 根据setmeal id 删除 setmeal dishes
     * delete setmeal dishes by setmeal id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteById(Long id);

    /**
     * 删除关联的setmeal dishes
     * delete associated setmeal dishes
     */
    void deleteInBatches(Long[] ids);
}
