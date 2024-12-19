package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 数据统计相关接口
 * Interfaces related to data statistics
 */
@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api(tags = "管理端-数据统计相关接口")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 营业额统计
     * Revenue Statistics
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation(value = "营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("营业额统计，从 {} 到 {}",begin,end);
        TurnoverReportVO turnoverReportVO = reportService.getTurnoverStatistics(begin,end);
        return Result.success(turnoverReportVO);
    }
}
