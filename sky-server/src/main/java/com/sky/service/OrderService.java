package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 用户下单
     * suer submit order
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 查询历史订单（分页查询）
     * query history order（pagination）
     */
    PageResult paginationHistoryOrder(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 通过id查询订单
     * query order by id
     */
    OrderVO queryById(Long id);


    /**
     * 取消订单
     * cancel order
     */
    void userCancelById(Long id);

    /**
     * 再来一单
     * reorder
     */
    void repetition(Long id);

    /**
     * 订单查询
     * order query
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个订单状态数量统计
     * Statistics of the order count for each status.
     */
    OrderStatisticsVO statistic();

    /**
     * 接单
     * confirm order
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     * reject order
     */
    void reject(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 取消订单
     * cancel order
     */
    void cancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 查询订单详情
     * Query Order Details
     */
    OrderVO queryDetail(Long id);

    /**
     * 派送订单
     * deliver order
     */
    void deliver(Long id);

    /**
     * 完成订单
     * complete order
     */
    void complete(Long id);

    /**
     * 催单
     * remind order
     */
    void reminder(Long id);

//    /**
//     * 更改订单状态
//     * change order status
//     */
//    void changeOrderStatus(Orders orders);
}
