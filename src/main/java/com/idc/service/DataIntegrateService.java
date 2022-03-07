package com.idc.service;

import com.alibaba.fastjson.JSONObject;

/**
 * 数据汇总处理
 */
public interface DataIntegrateService {
    public boolean insertData(String dataSource, JSONObject jsonData);

    public String getAlldata(String dataSource);
}
