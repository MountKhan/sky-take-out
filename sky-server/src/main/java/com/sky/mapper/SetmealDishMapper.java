package com.sky.mapper;

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
}
