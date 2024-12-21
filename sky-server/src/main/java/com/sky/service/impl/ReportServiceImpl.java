package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkspaceService workspaceService;



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
     * 统计菜品和套餐中的销量top10
     * Statistics of the top 10 sales in dishes and set meals.
     */
    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> top10Seals = orderMapper.getTop10Seals(beginTime, endTime);


        List<String> names = top10Seals.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");

        List<Integer> numbers = top10Seals.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");

        return SalesTop10ReportVO
                .builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    /**
     * 导出Excel报表
     * Export excel report
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {


        LocalDate localDateBegin = LocalDate.now().minusDays(30);
        LocalDateTime localDateTimeBegin = LocalDateTime.of(localDateBegin, LocalTime.MIN);
        LocalDate localDateEnd = LocalDate.now().minusDays(1);
        LocalDateTime localDateTimeEnd = LocalDateTime.of(localDateEnd, LocalTime.MAX);

        BusinessDataVO businessDataVO = workspaceService.getBusinessDataByDates(localDateTimeBegin, localDateTimeEnd);


        //通过POI写入数据到Excel
        //Write data to Excel using POI
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/template.xlsx");


        try {
            //基于模板文件创建新的Excel文件
            //create form based on template file
            XSSFWorkbook excel = new XSSFWorkbook(in);

            XSSFSheet sheet1 = excel.getSheet("Sheet1");

            //填充数据---时间段
            //fill in form---time period
            sheet1.getRow(1)
                    .getCell(1)
                    .setCellValue("from "+localDateBegin+" to "+localDateEnd);

            //填充数据---营业额,订单完成率,新增用户数
            //fill in form---turnover,completion rate,new users
            XSSFRow row3 = sheet1.getRow(3);
            row3.getCell(2).setCellValue(businessDataVO.getTurnover());
            row3.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row3.getCell(6).setCellValue(businessDataVO.getNewUsers());

            //填充数据---有效订单数，平均客单价
            //fill in form---valid orders,unit price
            XSSFRow row4 = sheet1.getRow(4);
            row4.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row4.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            //填充数据---明细数据
            //fill in form---detail data
            for (int i = 0; i < 30; i++) {
                LocalDate date = localDateBegin.plusDays(i);
                BusinessDataVO businessData = workspaceService.getBusinessDataByDates(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                XSSFRow row = sheet1.getRow(7 + i);

                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());

            }

            //通过输出流将Excel文件发送给客户端浏览器
            //Send the Excel file to the client's browser via output stream
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            excel.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
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


