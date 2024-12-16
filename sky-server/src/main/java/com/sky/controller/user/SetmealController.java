package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Slf4j
@Api(tags = "C端-套餐浏览接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 用户端：根据分类id查询当前可获得的套餐
     * user:Query the currently enable setmeal by categoryId
     */
    @Cacheable(cacheNames = "setmealCache",key = "#categoryId")//setmealCache::categoryId
    @ApiOperation(value = "根据分类id查询套餐")
    @GetMapping("/list")
    public Result<List<Setmeal>> getSetmealByCategoryId(Long categoryId){
        log.info("根据分类id查询套餐");

        //用户端只能查询当前status为enable的套餐
        //The client can query only the package whose status is enable
        Setmeal setmeal = new Setmeal();
        setmeal.setCategoryId(categoryId);
        setmeal.setStatus(StatusConstant.ENABLE);

        List<Setmeal> list= setmealService.select(setmeal);
        return Result.success(list);
    }

    /**
     * 根据套餐id查询包含的菜品
     * user:query associated dishes by categoryId
     */
    @ApiOperation(value = "根据套餐id查询包含的菜品")
    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> getDishesByCategoryId(@PathVariable Long id){
        log.info("根据套餐id查询包含的菜品");
        List<DishItemVO> list = setmealService.getDishByCategoryId(id);
        return Result.success(list);
    }
}
