package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

//    /**
//     * 更改订单状态
//     * change order status
//     */
//    @Update("update orders set status = #{status} where id = #{id}")
//    void updateStatus(Orders orders);
}
