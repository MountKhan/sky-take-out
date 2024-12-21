package com.sky.service;

import com.sky.vo.BusinessDataVO;

import java.time.LocalDateTime;

public interface WorkspaceService {

    /**
     * 查询今日运营数据
     * Query today's business data
     */
    BusinessDataVO getBusinessData();

    BusinessDataVO getBusinessDataByDates(LocalDateTime begin, LocalDateTime end);
}
