package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增分类/save category
     * @param categoryDTO
     */
    @Override
    public void saveCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        category.setStatus(StatusConstant.DISABLE);//enable by default
//        category.setCreateTime(LocalDateTime.now());
//        category.setCreateUser(BaseContext.getCurrentId());
//        category.setUpdateTime(LocalDateTime.now());
//        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.saveCategory(category);
    }

    /**
     * 分页查询/category pagination query
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult categoryPaginationQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        Page<Category> page = categoryMapper.paginationQuery(categoryPageQueryDTO);
        long total = page.getTotal();
        List<Category> result = page.getResult();
        return new PageResult(total,result);
    }

    /**
     *删除分类/delete category
     * @param id
     */
    @Override
    public void deleteById(Integer id) {

        //如果当前分类下有菜品，就不能删除
        //If there are dishes in the current category, it cannot be deleted
        Integer cnt = dishMapper.countByCategoryId(id);
        if(cnt>0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        //如果当前分类关联了套餐，不能删除
        //If the current category is associated with a package, it cannot be deleted
        cnt = setmealMapper.countByCategoryId(id);
        if(cnt>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        categoryMapper.deleteById(id);
    }

    /**
     * 根据id更新员工信息/ update employee attributes by id
     * @param categoryDTO
     */
    @Override
    public void updateCategoryById(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);

        //更改最后更新时间和最后操作人
        //record the last update time and last operator's id
//        category.setUpdateUser(BaseContext.getCurrentId());
//        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.updateCategory(category);
    }

    /**
     * 启用/禁用分类 enable/disable category
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, long id) {
        Category category = Category.builder()
                .status(status)
                .id(id)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        categoryMapper.updateCategory(category);
    }

    /**
     * 根据类型查询分类/query category by type
     */
    @Override
    public List<Category> queryCategoryByType(Integer type) {
        return categoryMapper.queryCategoryByType(type);
    }


}
