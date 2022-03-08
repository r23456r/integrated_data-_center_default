package com.idc.service;

import com.alibaba.fastjson.JSONObject;

public interface DataService {
    /**
     * 获取信息数据
     * @return
     */
    public JSONObject getDataInfo(Integer id);
    public JSONObject getDataInfo();
}
