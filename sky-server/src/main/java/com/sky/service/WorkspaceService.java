package com.sky.service;

import com.sky.vo.BusinessDataVO;

public interface WorkspaceService {

    /**
     * 查询今日运营数据
     * Query today's business data
     */
    BusinessDataVO getBusinessData();

}
