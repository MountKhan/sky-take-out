package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    /**
     * 新增分类/save category
     * @param categoryDTO
     */
    void saveCategory(CategoryDTO categoryDTO);

    /**
     * 分类的分页查询/category pagination query
     * @return
     */
    PageResult categoryPaginationQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 删除分类/delete category
     * @param id
     */
    void deleteById(Integer id);

    /**
     * 根据id更新员工信息/ update employee attributes by id
     */
    void updateCategoryById(CategoryDTO categoryDTO);

    /**
     * 启用/禁用分类 enable/disable category
     * @param status
     * @param id
     */
    void startOrStop(Integer status, long id);


    /**
     * 根据类型查询分类/query category by type
     */
    List<Category> queryCategoryByType(Integer type);
}
