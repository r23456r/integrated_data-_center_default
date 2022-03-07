package com.idc.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.idc.common.constants.URLConstants;
import com.idc.common.utils.HttpWormUtils;
import com.idc.common.utils.UtilHandle;
import com.idc.dao.entity.WtoBean;
import com.idc.service.DataIntegrateService;
import com.idc.service.DataService;
import com.sun.deploy.net.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("WTODataServiceImpl")
public class WTODataServiceImpl implements DataService {

    private final String KEY = "1dc531027a3b48a588e167c449bdb739";
    private String URL = "https://api.wto.org/timeseries/v1/data";

    @Override
    public JSONObject getDataInfo() {
        JSONObject wtoJson = new JSONObject();
        List<WtoBean> list = new ArrayList<>();
//        for (WtoBean bean : getWtoSourceList) {
//            JSONObject data = new JSONObject();
//            JSONObject attribute = new JSONObject();
//            attribute.put("countryName", bean.getReportingEconomy());
//            wtoJson.put(bean.getReportingEconomy(), UtilHandle.setNodeInfo(attribute, data));
//        }
        return wtoJson;
    }

    @Test
    public void getWtoSourceList() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("i", "TP_A_0010");
        paramMap.put("r", 156);
        paramMap.put("max", "1000000");
        paramMap.put("fmt", "json");
        paramMap.put("mode", "full");
        paramMap.put("lang", 1);
        paramMap.put("meta", false);
        paramMap.put("subscription-key", KEY);
        String response = HttpUtil.get(URL, paramMap);
        JSONArray dataset = JSONObject.parseObject(response).getJSONArray("Dataset");
        List<WtoBean> beans = dataset.toJavaList(WtoBean.class);
        System.out.println(beans.toString());
    }
}
