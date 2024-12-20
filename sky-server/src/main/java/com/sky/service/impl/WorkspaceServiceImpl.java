package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 查询今日运营数据
     * Query today's business data
     */
    @Override
    public BusinessDataVO getBusinessData() {

        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);


        Map map = new HashMap();
        map.put("begin",begin);
        map.put("end",end);

        //获取今日订单总数
        //get today's total number of orders
        Integer orderCount = orderMapper.getOrderCount(map);

        //获取今日营业额
        //get today's turnover
        map.put("status", Orders.COMPLETED);
        Double turnover = orderMapper.sumByMap(map);


        //获取今日有效订单数
        //get today's number of valid orders
        Integer validOrderCount = orderMapper.getOrderCount(map);

        //获取今日新增用户数
        //get today's number of new users
        Integer newUserCount = userMapper.countByMap(map);

        Double orderCompletionRate = (orderCount == 0 ?0 : validOrderCount.doubleValue()/orderCount);

        Double unitPrice = (orderCount == 0 ? 0 : turnover/orderCount);

        return BusinessDataVO
                .builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUserCount)
                .build();
    }
}
