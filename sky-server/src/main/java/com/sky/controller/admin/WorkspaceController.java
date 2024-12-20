package com.sky.controller.admin;

import com.sky.mapper.DishMapper;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.OrderService;
import com.sky.service.SetmealService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin/workspace")
@Api(tags = "管理端-工作台相关接口")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private DishService dishService;
    @Autowired
    private OrderService orderService;

    /**
     * 查询今日运营数据
     * Query today's business data
     */
    @ApiOperation(value = "查询今日运营数据")
    @GetMapping("/businessData")
    public Result<BusinessDataVO> getBusinessData(){
        log.info("查询今日运营数据");
        BusinessDataVO businessDataVO = workspaceService.getBusinessData();
        return Result.success(businessDataVO);
    }

    /**
     * 查询套餐总览
     * Query setmeals overview
     */
    @GetMapping("/overviewSetmeals")
    @ApiOperation(value = "查询套餐总览")
    public Result<SetmealOverViewVO> getSetmealsOverview(){
        log.info("查询套餐总览");
        SetmealOverViewVO setmealOverViewVO = setmealService.getSetmealsOverview();
        return Result.success(setmealOverViewVO);
    }

    /**
     * 查询菜品总览
     * Query dishes overview
     */
    @GetMapping("/overviewDishes")
    @ApiOperation(value = "查询菜品总览")
    public Result<DishOverViewVO> getDishesOverview(){
        log.info("查询菜品总览");
        DishOverViewVO dishOverViewVO = dishService.getDishesOverview();
        return Result.success(dishOverViewVO);
    }

    /**
     * 查询订单管理数据
     * Query orders overview
     */
    @GetMapping("/overviewOrders")
    @ApiOperation(value = "查询订单管理数据")
    public Result<OrderOverViewVO> getOrdersOverview(){
        OrderOverViewVO orderOverViewVO = orderService.getOrdersOverview();
        return Result.success(orderOverViewVO);
    }
}
