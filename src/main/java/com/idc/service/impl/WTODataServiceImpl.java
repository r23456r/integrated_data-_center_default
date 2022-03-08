package com.idc.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.idc.common.constants.Constants;
import com.idc.common.utils.UtilHandle;
import com.idc.dao.entity.IndicatorBean;
import com.idc.dao.entity.WtoBean;
import com.idc.service.DataIntegrateService;
import com.idc.service.DataService;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("WTODataServiceImpl")
@Slf4j
public class WTODataServiceImpl implements DataService {

    private final String KEY = "1dc531027a3b48a588e167c449bdb739";
    private final String DATA_URL = "https://api.wto.org/timeseries/v1/data";
    private final String INDICATORS_URL = "https://api.wto.org/timeseries/v1/indicators?i=all&t=all&pc=all&tp=all&frq=all&lang=1";

    @Override
    public JSONObject getDataInfo(Integer id) {
        JSONObject wtoJson = new JSONObject();
        List<String> indicators = getIndicators();
        JSONObject data1 = new JSONObject();
        int i = 0;
        for (String indicator : indicators) {
            log.info("size:#{}"+"now:#{}",indicators.size(),i++);
            JSONObject data1Detail = new JSONObject();
            JSONObject idc1WithData = new JSONObject();
            JSONObject idc1WithAttr = new JSONObject();
            getWtoSourceList(indicator, id).forEach(bean -> {
                //巴西
                //巴西-idcAttribute
                JSONObject IDCAttr1 = new JSONObject();
                IDCAttr1.put("reportingEconomy", bean.getReportingEconomy());
                data1.put(Constants.IDC_ATTRIBUTE, IDCAttr1);


                // 巴西-通货-attribute
                idc1WithAttr.put("indicator", bean.getIndicator());
                data1Detail.put(Constants.IDC_ATTRIBUTE, idc1WithAttr);
                // 巴西-通货-data
                idc1WithData.put(String.valueOf(bean.getYear()), bean.getValue());
                data1Detail.put(Constants.IDC_DATA, idc1WithData);
                //
                data1.put(bean.getIndicator(), data1Detail);
                wtoJson.put(bean.getReportingEconomy(), data1);

                log.info("-------------");
                log.info(wtoJson.toJSONString());
                log.info("-------------");


//
//                JSONObject attribute1 = new JSONObject();
//                data1.put(String.valueOf(bean.getYear()), bean.getValue());
//                JSONObject attribute0 = new JSONObject();
//                attribute0.put(indicator, data1);
//                UtilHandle.setNodeInfo()
//                attribute0.put("indicator", bean.getIndicator());
//                wtoJson.put(bean.getReportingEconomy(), UtilHandle.setNodeData(attribute0, data1));
//                log.info("-------------");
//                log.info(wtoJson.toJSONString());
//                log.info("-------------");

            });

        }

        return wtoJson;
    }

    @Override
    public JSONObject getDataInfo() {
        return null;
    }

    public List<WtoBean> getWtoSourceList(String IndicatorCode,Integer id) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("i", IndicatorCode);
        paramMap.put("r", id);
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

    @Test
    public List<String> getIndicators() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("subscription-key", KEY);
        String response = HttpUtil.get(INDICATORS_URL, paramMap);
        return JSONObject.parseArray(response, IndicatorBean.class).stream().map(IndicatorBean::getCode).collect(Collectors.toList());
    }
}
