package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

    /**
     * 菜品分页查询
     * dishes pagination query
     */
    Page<DishVO> dishPaginationQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品
     * query dish by id
     */
    @Select("select * from dish where id=#{id}")
    Dish getById(Long id);

    /**
     * 根据id删除菜品
     * delete dish by id
     */
    @Delete("delete from dish where id=#{id}")
    void deleteById(Long id);

    /**
     * 批量删除菜品
     * delete dishes in batches
     */
    void deleteInBatches(Long[] ids);

    /**
     * 更新菜品
     * update dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据分类id查找菜品
     * query dishes by category id
     */
    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> selectByCategoryId(Long categoryId);

    /**
     * 查询菜品
     * select dishes
     */
    @Select("select * from dish where category_id = #{categoryId} and status = #{status}")
    List<Dish> select(Dish dish);
}
