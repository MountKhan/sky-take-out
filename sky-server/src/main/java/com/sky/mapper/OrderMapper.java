package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Mapper
public interface OrderMapper {



    /**
     * 插入订单数据
     * insert order
     */
    void insert(Orders orders);

    /**
     * 查询当前用户的订单数据
     * select order about current user
     */
    Page<Orders> select(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     * select order by id
     */
    @Select("select * from orders where id = #{id}")
    Orders selectById(Long id);

    /**
     * 更新订单
     * update order
     */
    void update(Orders orders);

    /**
     * 各个订单状态数量统计
     * Statistics of the order count for each status.
     */
    @Select("select count(0) from orders where status = #{status}")
    Integer statusCount(Integer status);

    /**
     * 查询指定订单状态，指定下单时间的订单
     * Query orders with a specified status and a specified order time.
     */
    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime time);

    /**
     * 根据开始日期，结束日期，订单状态 统计营业额
     * Calculate the total revenue based on the start date, end date, and order status.
     */
    Double sumByMap(Map map);

    /**
     */
    @MapKey("orderTime")
    Map<String, String> sumByDate(LocalDate begin, LocalDate end, Integer status);

    /**
     * 统计订单数量
     * order statistics
     */
    Integer getOrderCount(Map map);

    /**
     * 统计菜品和套餐中的销量top10
     * Statistics of the top 10 sales in dishes and set meals.
     */
    List<GoodsSalesDTO> getTop10Seals(LocalDateTime begin,LocalDateTime end);


//    /**
//     * 更改订单状态
//     * change order status
//     */
//    @Update("update orders set status = #{status} where id = #{id}")
//    void updateStatus(Orders orders);
}
