package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入口味数据
     * Insert flavor data in batches
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据id删除菜品
     * delete flavor by id
     */
    @Delete("delete from dish_flavor where dish_id=#{dishId}")
    void deleteById(Long dishId);

    /**
     * 批量删除关联的口味数据
     * delete associated flavor in batches
     */
    void deleteInBatches(Long[] ids);

    /**
     * 根据dish id 查询口味
     * select flavor by dish id
     */
    @Select("select * from dish_flavor where dish_id=#{dishId}")
    List<DishFlavor> getByDishId(Long dishId);
}
