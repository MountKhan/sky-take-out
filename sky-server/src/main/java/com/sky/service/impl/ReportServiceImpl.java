package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Max;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 营业额统计
     * Revenue Statistics
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {

        //存放从begin到end的每天的日期
        //Store the dates for each day from 'begin' to 'end'.
        List<LocalDate> dateList = new ArrayList<>();

        if (begin.isAfter(end)) {
            throw new IllegalArgumentException("传入的日期有误");
        }

        while (!begin.equals(end)) {
            dateList.add(begin);
            begin = begin.plusDays(1);
        }

        List<Double> turnoverList = new ArrayList<>();

        for (LocalDate localDate : dateList) {
            //查询localDate这一天状态为"COMPLETED = 5"的订单的总金额
            //Query the total amount of orders with the status "COMPLETED = 5" on the specified LocalDate.
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnoverList.add(turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ",")).build();


//        begin = dateList.get(0);
//        end = dateList.get(dateList.size()-1);
//        Map<String, String> turnoverMap = orderMapper.sumByDate(begin,end,Orders.COMPLETED);
//        log.info("debug:{}",turnoverMap);
//        for (LocalDate localDate : dateList) {
//            if(turnoverMap.get(localDate) == null || turnoverMap.get(localDate).equals(0.0)){
//                turnoverList.add(0.0);
//            }else{
//                turnoverList.add(Double.parseDouble(turnoverMap.get(localDate)));
//            }
//        }
//        return TurnoverReportVO.builder()
//                .dateList(StringUtils.join(dateList,","))
//                .turnoverList(StringUtils.join(turnoverList,",")).build();
    }

    /**
     * 用户统计
     * user statistics
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //存放从begin到end的每天的日期
        //Store the dates for each day from 'begin' to 'end'.
        List<LocalDate> dateList = new ArrayList<>();

        if (begin.isAfter(end)) {
            throw new IllegalArgumentException("传入的日期有误");
        }

        while (!begin.equals(end)) {
            dateList.add(begin);
            begin = begin.plusDays(1);
        }

        //存放每天新增用户数量
        // Store the number of new users added each day
        List<Integer> newUserList = new ArrayList<>();

        //存放总用户数量
        // Store the total number of users
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap();

            map.put("end", endTime);
            Integer totalUser = userMapper.countByMap(map);
            totalUserList.add(totalUser);

            map.put("begin", beginTime);
            Integer newUser = userMapper.countByMap(map);
            newUserList.add(newUser);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    /**
     * 统计订单
     * order statistics
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //存放从begin到end的每天的日期
        //Store the dates for each day from 'begin' to 'end'.
        List<LocalDate> dateList = new ArrayList<>();

        if (begin.isAfter(end)) {
            throw new IllegalArgumentException("传入的日期有误");
        }

        while (!begin.equals(end)) {
            dateList.add(begin);
            begin = begin.plusDays(1);
        }

        //存放每天的订单总数
        //Store the total number of orders for each day
        List<Integer> orderCountList = new ArrayList<>();

        //存放每天有效的订单总数
        //Store the total number of valid orders for each day
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            //查询每天的订单总数
            //query the total number of orders for each day
            Integer orderCount = getOrderCount(beginTime, endTime, null);

            //查询每天有效的订单总数
            //query the total number of valid orders for each day
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        // 计算所选日期范围内的订单总数
        // Calculate the total number of orders within the selected date range
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();

        // 计算所选日期范围内的有效订单数
        // Calculate the number of valid orders within the selected date range
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        // 计算订单完成率
        // Calculate the order completion rate
        Double orderCompletionRate = (!totalOrderCount.equals(0) ? validOrderCount.doubleValue() / totalOrderCount : 0.0);

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 统计符合条件的订单数量
     * order statistics
     */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);

        return orderMapper.getOrderCount(map);
    }
}


