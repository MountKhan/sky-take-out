package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.io.Serializable;
import java.time.LocalDate;

public interface ReportService {

    /**
     * 营业额统计
     * Revenue Statistics
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 用户统计
     * user statistics
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计订单
     * order statistics
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计菜品和套餐中的销量top10
     * Statistics of the top 10 sales in dishes and set meals.
     */
    SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end);
}
