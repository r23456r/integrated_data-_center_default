package com.idc.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.idc.common.constants.Constants;
import com.idc.common.utils.UtilHandle;
import com.idc.dao.entity.IndicatorBean;
import com.idc.dao.entity.ReporterBean;
import com.idc.dao.entity.WtoBean;
import com.idc.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@Service("WTODataServiceImpl")
@Slf4j
public class WTODataServiceImpl implements DataService {

    private final String KEY = "1dc531027a3b48a588e167c449bdb739";
    private final String DATA_URL = "https://api.wto.org/timeseries/v1/data";
    private final String INDICATORS_URL = "https://api.wto.org/timeseries/v1/indicators?i=all&t=all&pc=all&tp=all&frq=all&lang=1";
    private final String REPORTERS_URL = "https://api.wto.org/timeseries/v1/reporters?ig=all&reg=all&gp=all&lang=1";
    private int time = 10000;

    @Override
    public JSONObject getDataInfo(String id) {
        JSONObject wtoJson = new JSONObject();
        List<String> indicators = getIndicators();
        int i = 0;
        List<WtoBean> allBeans = new ArrayList<>(12);
        for (String indicator : indicators) {
            log.info("size:#{}" + "now:#{}", indicators.size(), i++);
            if (i > 70) {
                log.info("慢了");
                continue;
            }
            List<WtoBean> wtoSourceList = getWtoSourceList(indicator, id);
            allBeans.addAll(wtoSourceList);
        }
        Map<String, List<WtoBean>> collect = allBeans.stream().collect(Collectors.groupingBy(WtoBean::getReportingEconomy));
        for (String reportingEconomy : collect.keySet()) {
            List<WtoBean> singlebeans = collect.get(reportingEconomy);
            assembleJSON(wtoJson, singlebeans);
        }
        try {
            writeFile("C:\\Users\\PandaIP\\Desktop\\file\\vital.json", wtoJson.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wtoJson;
    }

    public void assembleJSON(JSONObject wtoJson, List<WtoBean> wtoBeans) {
//        JSONObject data1 = new JSONObject();
//        JSONObject data1Detail = new JSONObject();
//        JSONObject idc1WithAttr = new JSONObject();
//        JSONObject idc1WithData = new JSONObject();
        //巴西
        Map<String, List<WtoBean>> collect = wtoBeans.stream().collect(Collectors.groupingBy(WtoBean::getIndicator));
        JSONObject treeJsonData = new JSONObject();
        for (String indicator : collect.keySet()) {
            List<WtoBean> groupByIndcatorBeans = collect.get(indicator);
            JSONObject attrB = new JSONObject();
            attrB.put("indicator", indicator);
            JSONObject dataB = new JSONObject();
            for (WtoBean wtoBean : groupByIndcatorBeans) {
                dataB.put(String.valueOf(wtoBean.getYear()),wtoBean.getValue());
            }
            JSONObject resultB = UtilHandle.setNodeData(attrB, dataB);
            treeJsonData.put(indicator, resultB);
        }
        wtoJson.put(wtoBeans.get(0).getReportingEconomy(), treeJsonData);
        log.info("wtoJsonSize: {}", wtoJson.toJSONString());
    }
//        for (WtoBean bean : wtoBeans) {
//
//            //巴西-idcAttribute
//            JSONObject IDCAttr1 = new JSONObject();
//            IDCAttr1.put("reportingEconomy", bean.getReportingEconomy());
//            data1.put(Constants.IDC_ATTRIBUTE, IDCAttr1);
//
//            // 巴西-通货-attribute
//            idc1WithAttr.put("indicator", bean.getIndicator());
//            data1Detail.put(Constants.IDC_ATTRIBUTE, idc1WithAttr);
//            // 巴西-通货-data
//            idc1WithData.put(String.valueOf(bean.getYear()), bean.getValue());
//            data1Detail.put(Constants.IDC_DATA, idc1WithData);
//            //
//            data1.put(bean.getIndicator(), data1Detail);
//            wtoJson.put(bean.getReportingEconomy(), data1);
//        }


    @Override
    public JSONObject getDataInfo() {
        JSONObject wtoJson = new JSONObject();
        List<String> indicators = getIndicators();
        JSONObject data1 = new JSONObject();
        int i = 0;
        List<WtoBean> wtoBeans = new ArrayList<>(12);
        for (String indicator : indicators) {
            log.info("size:#{}" + "now:#{}", indicators.size(), i++);
            wtoBeans.addAll(getWtoSourceList(indicator, ""));
        }
        log.info("已获取所有待解析Bean,size:{}", wtoBeans.size());
        log.info("已获取所有待解析Beans :{}", JSONObject.toJSONString(wtoBeans));

//        log.info("wtoJSONBeans:{}", JSONObject.toJSONString(wtoBeans));
        wtoBeans.forEach(bean -> {
            JSONObject data1Detail = new JSONObject();
            JSONObject idc1WithData = new JSONObject();
            JSONObject idc1WithAttr = new JSONObject();
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

//            log.info(wtoJson.toJSONString());
//            try {
//                writeFile("C:\\Users\\PandaIP\\Desktop\\file\\" + bean.getReportingEconomy() + ".json", wtoJson.toJSONString());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        });
        try {
            writeFile("C:\\Users\\PandaIP\\Desktop\\file\\vital.json", wtoJson.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wtoJson;
    }

    public List<String> getReporters() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("subscription-key", KEY);
        return JSONObject.parseArray(HttpUtil.get(REPORTERS_URL, paramMap), ReporterBean.class).stream().map(ReporterBean::getCode).collect(Collectors.toList());
    }

    public List<WtoBean> getWtoSourceList(String IndicatorCode, String id) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("i", IndicatorCode);
        paramMap.put("r", id == null ? "all" : id);
        paramMap.put("max", "1000000");
        paramMap.put("fmt", "json");
        paramMap.put("mode", "full");
        paramMap.put("lang", 1);
        paramMap.put("meta", false);
        paramMap.put("subscription-key", KEY);
        String response = null;
        try {
            response = HttpUtil.get(DATA_URL, paramMap,100000);
        } catch (Exception e) {
            return new ArrayList<>();
        }

        JSONArray dataset = JSONObject.parseObject(response).getJSONArray("Dataset");
        if (dataset == null) {
            return null;
        }
        return dataset.toJavaList(WtoBean.class);
    }

    public List<String> getIndicators() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("subscription-key", KEY);
        String response = HttpUtil.get(INDICATORS_URL, paramMap);
        return JSONObject.parseArray(response, IndicatorBean.class).stream().map(IndicatorBean::getCode).collect(Collectors.toList());
    }

    public static void writeFile(String filePath, String sets)
            throws IOException {
//        new File(filePath).delete();
        FileWriter fw = new FileWriter(filePath);
        PrintWriter out = new PrintWriter(fw);
        out.write(sets);
        out.println();
        fw.close();
        out.close();
    }


    @Test
    public void testGetAll() {
        getDataInfo(null);
    }

    //
    @Test
    public void testGet10() {
        String s = "156," + "840," + "392," + "408," + "410," + "643," + "826," + "040," + "804," + "923," + "918," + "928";
//        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();
//        String s = "923";
        for (String id : Arrays.asList(s)) {
            getDataInfo(id);
        }
    }


}
