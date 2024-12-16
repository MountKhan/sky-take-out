package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 新增分类/save category
     */
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into category(type,name,sort,status,create_time,update_time,create_user,update_user) " +
            "values(#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void saveCategory(Category category);

    /**
     * 分类的分页查询/category pagination query
     */
    Page<Category> paginationQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 删除分类/delete category
     */
    @Delete("delete from category where id=#{id}")
    void deleteById(Integer id);

    /**
     * 更新分类信息/update category
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateCategory(Category category);


    /**
     * 根据类型查询分类/query category by type
     */
    @Select("select * from category where type=#{type}")
    List<Category> queryCategoryByType(Integer type);

    /**
     * 根据类型查询分类 1为菜品分类 2为套餐分类
     * Query category 1 for dishes and 2 for set meals according to type
     */
    List<Category> selectByType(Integer type);
}
