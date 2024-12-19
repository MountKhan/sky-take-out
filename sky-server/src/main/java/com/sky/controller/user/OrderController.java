package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
@Api(tags = "C端-订单相关接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * suer submit order
     */
    @PostMapping("/submit")
    @ApiOperation(value = "用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单，{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 查询历史订单（分页查询）
     * query history order（pagination）
     */
    @GetMapping("/historyOrders")
    @ApiOperation(value = "查询历史订单")
    public Result<PageResult> paginationHistoryOrder(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("分页查询历史订单");
        PageResult pageResult = orderService.paginationHistoryOrder(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 通过id查询订单
     * query order by id
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation(value = "查询订单详情")
    public Result<OrderVO> queryById(@PathVariable Long id){
        log.info("查询订单详情，{}",id);
        OrderVO orderVO = orderService.queryById(id);
        return Result.success(orderVO);
    }

//    /**
//     * 取消订单
//     * cancel order
//     */
//    @PutMapping("/cancel/{id}")
//    @ApiOperation(value = "取消订单")
//    public Result cancleOrder(@PathVariable Long id){
//        log.info("取消订单，{}",id);
//        Orders orders = Orders.builder().id(id).status(Orders.CANCELLED).build();
//        orderService.changeOrderStatus(orders);
//        return Result.success();
//    }
    /**
     * 取消订单
     * cancel order
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation(value = "取消订单")
    public Result cancleOrder(@PathVariable Long id){
        log.info("取消订单，{}",id);
        orderService.userCancelById(id);
        return Result.success();
    }

    /**
     * 再来一单
     * reorder
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation(value = "再来一单")
    public Result repetition(@PathVariable Long id){
        log.info("再来一单，{}",id);
        orderService.repetition(id);
        return Result.success();
    }

    /**
     * 催单
     * remind order
     */
    @GetMapping("/reminder/{id}")
    @ApiOperation(value = "催单")
    public Result reminder(@PathVariable Long id){
        log.info("用户催单，{}",id);
        orderService.reminder(id);
        return Result.success();
    }
}
