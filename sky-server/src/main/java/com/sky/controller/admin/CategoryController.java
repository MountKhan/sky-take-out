package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.sky.result.Result;

import java.net.InetAddress;
import java.util.List;

/**
 * 分类管理
 */
@Api(tags = "分类相关接口")
@RestController
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类/save category
     */
    @ApiOperation(value = "新增分类")
    @PostMapping
    public Result saveCategroy(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类，{}",categoryDTO);
        categoryService.saveCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 分类的分页查询/category pagination query
     */
    @ApiOperation(value = "分类分页查询")
    @GetMapping("/page")
    public Result<PageResult> categoryPaginationQuery(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分类的分页查询，{}",categoryPageQueryDTO);
        PageResult pageResult = categoryService.categoryPaginationQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除分类/delete category
     */
    @ApiOperation(value = "删除分类")
    @DeleteMapping
    public Result deleteById(Integer id){
        log.info("根据id删除分类，{}",id);
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 根据id分类信息/ update category attributes by id
     */
    @ApiOperation(value = "修改分类")
    @PutMapping
    public Result updateCategoryById(@RequestBody CategoryDTO categoryDTO){
        log.info("根据id修改分类信息，{}",categoryDTO);
        categoryService.updateCategoryById(categoryDTO);
        return Result.success();
    }

    /**
     * 启用/禁用分类 enable/disable category
     */
    @ApiOperation(value = "设置分类的状态")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status,long id){
        log.info("根据id设置分类的状态 id:{},status:{}",id,status);
        categoryService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 根据类型查询分类/query category by type
     */
    @ApiOperation(value = "根据类型查询分类")
    @GetMapping("/list")
    public Result<List<Category>> queryCategoryByType(Integer type){
        log.info("根据类型查询分类，{}",type);
        List<Category> list = categoryService.queryCategoryByType(type);
        return Result.success(list);
    }
}
