package com.sky.service;

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
}
