package com.sky.service.impl;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 营业额统计
     * Revenue Statistics
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {

        //存放从begin到end的每天的日期
        //Store the dates for each day from 'begin' to 'end'.
        List<LocalDate> dateList = new ArrayList<>();

        if (begin.isAfter(end)){
            throw new IllegalArgumentException("传入的日期有误");
        }

        while(!begin.equals(end)){
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
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status",Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnoverList.add(turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList,",")).build();


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
}
