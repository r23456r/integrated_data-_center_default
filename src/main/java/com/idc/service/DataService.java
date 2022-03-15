package com.idc.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.scheduling.annotation.Async;

public interface DataService {
    /**
     * 获取信息数据
     * @return
     */
    JSONObject getDataInfo(String id) throws InterruptedException;
    JSONObject getDataInfo();
}
