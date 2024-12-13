package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {
    /**
     * 根据分类id查询菜品数量
     * Query the number of dishes according to the category id
     */
    @Select("select count(0) from dish where category_id = #{id}")
    Integer countByCategoryId(Integer id);

    /**
     * 添加新菜品
     * add new dish
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);
}
