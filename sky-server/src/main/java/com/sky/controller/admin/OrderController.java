package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Api(tags = "管理端-订单相关接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 管理端的订单查询
     * admin:order query
     */
    @GetMapping("/conditionSearch")
    @ApiOperation(value = "订单条件查询")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("订单查询，{}",ordersPageQueryDTO);
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 各个订单状态数量统计
     * Statistics of the order count for each status.
     */
    @GetMapping("/statistics")
    @ApiOperation(value = "各个订单状态数量统计")
    public Result statistic(){
        log.info("统计各个状态的订单数量");
        OrderStatisticsVO orderStatisticsVO = orderService.statistic();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 接单
     * confirm order
     */
    @PutMapping("/confirm")
    @ApiOperation(value = "接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("接单,{}",ordersConfirmDTO);
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 拒单
     * reject order
     */
    @PutMapping("/rejection")
    @ApiOperation(value = "拒单")
    public Result reject(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        log.info("拒单，{}",ordersRejectionDTO);
        orderService.reject(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 取消订单
     * cancel order
     */
    @PutMapping("/cancel")
    @ApiOperation(value = "取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("取消订单，{}",ordersCancelDTO);
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 查询订单详情
     * Query Order Details
     */
    @GetMapping("/details/{id}")
    @ApiOperation(value = "查询订单详情")
    public Result<OrderVO> queryDetail(@PathVariable Long id){
        log.info("查询订单详情,{}",id);
        OrderVO orderVO = orderService.queryDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 派送订单
     * deliver order
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation(value = "派送订单")
    public Result deliver(@PathVariable Long id){
        log.info("派送订单,{}",id);
        orderService.deliver(id);
        return Result.success();
    }

    /**
     * 完成订单
     * complete order
     */
    @PutMapping("/complete/{id}")
    @ApiOperation(value = "完成订单")
    public Result complete(@PathVariable Long id){
        log.info("完成订单，{}",id);
        orderService.complete(id);
        return Result.success();
    }

}
