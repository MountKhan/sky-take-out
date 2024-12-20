package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单明细数据
     * insert order detail in batches
     */
    void insertBatch(List<OrderDetail> orderDetailList);

    /**
     * 根据order id 查询订单细节
     * query order detail by orderId
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> selectByOrderId(Long id);

}
