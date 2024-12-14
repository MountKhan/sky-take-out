package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     * add setmeal
     */
    @PostMapping
    @ApiOperation(value = "新增套餐")
    public Result addSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐，{}",setmealDTO);
        setmealService.addSetmeal(setmealDTO);
        return Result.success();
    }

    /**
     * 分页查询套餐
     * setmeal pagination query
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询套餐")
    public Result<PageResult> setmealPaginationQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("分页查询,{}",setmealPageQueryDTO);
        PageResult pageResult = setmealService.setmealPaginationQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询套餐
     * query setmeal by id
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询套餐")
    public Result<SetmealVO> selectById(@PathVariable Long id){
        log.info("根据id查询套餐，{}",id);
        SetmealVO setmealVO = setmealService.selectById(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐
     * update setmeal
     */
    @PutMapping
    @ApiOperation(value = "修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐,{}",setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }

    /**
     * 批量删除套餐
     * delete setmeal in batches
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result deleteInBatches(Long[] ids){
        log.info("批量删除套餐，{}",ids);
        setmealService.deleteInBatches(ids);
        return Result.success();
    }

    /**
     * 套餐的起售/停售
     * able/enable setmeal
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "套餐的起售/停售")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("根据id：{}，起售/停售套餐：{}",id,status);
        setmealService.startOrStop(status,id);
        return Result.success();
    }
}
