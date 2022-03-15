package com.idc.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.idc.common.constants.Constants;
import com.idc.common.translate.TransApi;
import com.idc.common.translate.TransVo;
import com.idc.common.utils.HttpUtils;
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
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Service("WTODataServiceImpl")
@Slf4j
public class WTODataServiceImpl implements DataService {

    private final String KEY = "1dc531027a3b48a588e167c449bdb739";
    private final String DATA_URL = "https://api.wto.org/timeseries/v1/data";

    private final String COUNT_URL = "https://api.wto.org/timeseries/v1/data_count";
    private final String INDICATORS_URL = "https://api.wto.org/timeseries/v1/indicators?i=all&t=all&pc=all&tp=all&frq=all&lang=1";
    private final String REPORTERS_URL = "https://api.wto.org/timeseries/v1/reporters?ig=all&reg=all&gp=all&lang=1";

    private final Map indicatorMap = getMap(getIndicatorNames());
    private final Map reporterMap = getMap(getReporterNames());

    @Override
    public JSONObject getDataInfo(String id) throws InterruptedException {
        JSONObject wtoJson = new JSONObject();
        List<String> indicators = getIndicators();
        int i = 0;
        List<WtoBean> allBeans = new ArrayList<>(12);
        List<String> bigSize = new ArrayList<>();
        for (String indicator : indicators) {
            log.info("size:#{}" + "now:#{}", indicators.size(), i++);
//            if (i < 70) {
//                log.info("跳过前80指标");
//                continue;
//            }
//                if (i >100) {
//                    log.info("跳过前80指标");
//                    continue;
//                }
//                Integer listCount = getWtoSourceListCount(indicator, id);
//                log.info("listCount: {}",listCount);
//    //            Thread.sleep(1000);
//                if (listCount < 800) {
//                Thread.sleep(500);
//    //                bigSize.add(indicator);
//    //                continue;
//                }

            List<WtoBean> wtoSourceList = getWtoSourceList(indicator, id);
            if (wtoSourceList != null) {
                allBeans.addAll(wtoSourceList);
            }
        }
        log.info("bigSize:{}", bigSize.toString());
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

        Map<String, List<WtoBean>> collect = wtoBeans.stream().collect(Collectors.groupingBy(WtoBean::getIndicator));
        JSONObject treeJsonData = new JSONObject();
        for (String indicator : collect.keySet()) {
            List<WtoBean> groupByIndcatorBeans = collect.get(indicator);
            JSONObject attrB = new JSONObject();
            //翻译不准确的，暂时屏蔽
            if (indicatorMap.get(indicator)==null) {
                continue;
            }
            String enIndicator = String.valueOf(indicatorMap.get(indicator));
            attrB.put("indicator",enIndicator );
            JSONObject dataB = new JSONObject();
            for (WtoBean wtoBean : groupByIndcatorBeans) {
                dataB.put(String.valueOf(wtoBean.getYear()), wtoBean.getValue());
            }
            JSONObject resultB = UtilHandle.setNodeData(attrB, dataB);
            treeJsonData.put(enIndicator, resultB);
        }
        wtoJson.put(String.valueOf(reporterMap.get(wtoBeans.get(0).getReportingEconomy())), treeJsonData);
    }

    @Override
    public JSONObject getDataInfo() {
        return null;
    }

    public List<String> getReporters() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("subscription-key", KEY);
        return JSONObject.parseArray(HttpUtils.toGet(REPORTERS_URL, paramMap), ReporterBean.class).stream().map(ReporterBean::getCode).collect(Collectors.toList());
    }

    public List<String> getReporterNames() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("subscription-key", KEY);
        return JSONObject.parseArray(HttpUtils.toGet(REPORTERS_URL, paramMap), ReporterBean.class).stream().map(ReporterBean::getName).collect(Collectors.toList());
    }

    public List<WtoBean> getWtoSourceList(String IndicatorCode, String id) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("i", IndicatorCode);
        paramMap.put("r", id == null ? "all" : id);
        paramMap.put("max", "2000");
        paramMap.put("fmt", "json");
        paramMap.put("mode", "full");
        paramMap.put("lang", 1);
        paramMap.put("meta", false);
        paramMap.put("subscription-key", KEY);
        String response;
        try {
            response = HttpUtils.toGet(DATA_URL, paramMap);
        } catch (Exception e) {
            return new ArrayList<>();
        }

        JSONArray dataset = JSONObject.parseObject(response).getJSONArray("Dataset");
        if (dataset == null) {
            return null;
        }
        return dataset.toJavaList(WtoBean.class);
    }

    public Integer getWtoSourceListCount(String IndicatorCode, String id) {
//        https://api.wto.org/timeseries/v1/data_count?i=TP_E_0130&r=156&subscription-key=1dc531027a3b48a588e167c449bdb739
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("i", IndicatorCode);
        paramMap.put("r", id == null ? "all" : id);
        paramMap.put("subscription-key", KEY);
        String response = HttpUtils.toGet(COUNT_URL, paramMap);
        return Integer.valueOf(response);
    }


    public List<String> getIndicators() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("subscription-key", KEY);
        String response = HttpUtils.toGet(INDICATORS_URL, paramMap);
        return JSONObject.parseArray(response, IndicatorBean.class).stream().map(IndicatorBean::getCode).collect(Collectors.toList());
    }

    public List<String> getIndicatorNames() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("subscription-key", KEY);
        String response = HttpUtils.toGet(INDICATORS_URL, paramMap);
        return JSONObject.parseArray(response, IndicatorBean.class).stream().map(IndicatorBean::getName).collect(Collectors.toList());
    }

    public static void writeFile(String filePath, String sets)
            throws IOException {
        FileWriter fw = new FileWriter(filePath);
        PrintWriter out = new PrintWriter(fw);
        out.write(sets);
        out.println();
        fw.close();
        out.close();
    }


    @Test
    public void testGetAll() throws InterruptedException {
        getDataInfo(null);
    }

    public Map<String, String> getMap(List<String> list) {
        StringBuilder sb = new StringBuilder();
        //分隔符必须使用换行符，否则翻译类Api无法准备换行识别
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i != list.size() - 1) {
                sb.append("\n");
            }
        }

        String APP_ID = "20220310001118799";
        String SECURITY_KEY = "_I40DqQDM5E0quByUAlu";
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
        String transResult = api.getTransResult(sb.toString(), "auto", "zh");
        List<TransVo.Trans_result> resultList = JSONObject.parseObject(transResult, TransVo.class).getTrans_result();
        Map<String, String> map = new HashMap<>();
        for (TransVo.Trans_result result : resultList) {
            map.put(result.getSrc(), result.getDst());
        }
        return map;
    }

    //
    @Test
    public void testGet10() throws InterruptedException {
        String s = "156," + "840," + "392," + "408," + "410," + "643," + "826," + "040," + "804," + "923," + "918," + "928";
        for (String id : Arrays.asList(s)) {
            getDataInfo(id);
        }
    }

    @Test
    public void countryTest() {
        List<String> counrtys = getReporterNames();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < counrtys.size(); i++) {
            sb.append(counrtys.get(i));
            if (i != counrtys.size() - 1) {
                sb.append("\n");
            }
        }
        String s = sb.toString();
        System.out.println(s);
        String APP_ID = "20220310001118799";
        String SECURITY_KEY = "_I40DqQDM5E0quByUAlu";
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
        String transResult = api.getTransResult(s, "auto", "zh");
        List<TransVo.Trans_result> trans_result = JSONObject.parseObject(transResult, TransVo.class).getTrans_result();
        Map map = new HashMap();
        for (TransVo.Trans_result result : trans_result) {
            map.put(result.getSrc(), result.getDst());
        }
        System.out.println(map.keySet().size());
        System.out.println(map.toString());
    }

}
