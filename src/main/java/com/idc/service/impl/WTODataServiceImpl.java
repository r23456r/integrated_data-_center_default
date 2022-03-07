package com.idc.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.idc.common.utils.UtilHandle;
import com.idc.dao.entity.WtoBean;
import com.idc.service.DataService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("WTODataServiceImpl")
public class WTODataServiceImpl implements DataService {

    private final String KEY = "1dc531027a3b48a588e167c449bdb739";
    private final String DATA_URL = "https://api.wto.org/timeseries/v1/data";
    private final String INDICATORS_URL = "https://api.wto.org/timeseries/v1/indicators?i=all&t=all&pc=all&tp=all&frq=all&lang=1";
    @Override
    public JSONObject getDataInfo() {
        String response = HttpUtil.get(INDICATORS_URL);
        JSONArray dataset = JSONObject.parseObject(response).getJSONArray("");

        JSONObject wtoJson = new JSONObject();
        for (WtoBean bean : getWtoSourceList()) {
            JSONObject data = new JSONObject();
            JSONObject attribute = new JSONObject();
            attribute.put("countryName", bean.getReportingEconomy());
            wtoJson.put(bean.getReportingEconomy(), UtilHandle.setNodeInfo(attribute, data));
        }
        return wtoJson;
    }

    public List<WtoBean> getWtoSourceList() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("i", "TP_A_0010");
        paramMap.put("r", 156);
        paramMap.put("max", "1000000");
        paramMap.put("fmt", "json");
        paramMap.put("mode", "full");
        paramMap.put("lang", 1);
        paramMap.put("meta", false);
        paramMap.put("subscription-key", KEY);
        String response = HttpUtil.get(DATA_URL, paramMap);
        JSONArray dataset = JSONObject.parseObject(response).getJSONArray("Dataset");
        return dataset.toJavaList(WtoBean.class);
    }
}
