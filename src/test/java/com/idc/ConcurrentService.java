package com.idc;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.idc.common.constants.Constants;
import com.idc.dao.entity.IndicatorBean;
import com.idc.dao.entity.WtoBean;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ConcurrentService {

    private static final String KEY = "1dc531027a3b48a588e167c449bdb739";
    private static final String DATA_URL = "https://api.wto.org/timeseries/v1/data";
    private static final String INDICATORS_URL = "https://api.wto.org/timeseries/v1/indicators?i=all&t=all&pc=all&tp=all&frq=all&lang=1";

    private static final AtomicInteger count = new AtomicInteger(0);
    private static final AtomicInteger error_count = new AtomicInteger(0);
    //并发数量
    private static CountDownLatch cdl;
    private static final Integer THREAD_NUM = 2000;

    public static void main(String[] args) {
        cdl = new CountDownLatch(THREAD_NUM);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10,
                10,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.DiscardOldestPolicy());

        System.out.println("线程数: " + THREAD_NUM);
        long startTime = System.currentTimeMillis();

        for (Integer id : Arrays.asList(156, 840, 392, 408, 410, 643, 826, 040, 804, 923, 918, 928)) {
            executor.execute(new ConcurrentService.Run(cdl, id));
        }


        // 等待
        try {
            cdl.await();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage() + "\t\t" + e.getCause());
        }
        executor.shutdown();
    }

    /**
     * 线程类
     */
    private static class Run implements Runnable {
        private final CountDownLatch startLatch;
        private Integer id;

        public Run(CountDownLatch startLatch, Integer id) {
            this.startLatch = startLatch;
            this.id = id;
        }

        @Override
        public void run() {
            // 减一
            cdl.countDown();
            //todo
            toTxt(id);
            count.getAndIncrement();

        }
    }

    public static void toTxt(Integer id) {
        JSONObject wtoJson = new JSONObject();
        List<String> indicators = getIndicators();
        JSONObject data1 = new JSONObject();
        int i = 0;
        for (String indicator : indicators) {
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

                try {
                    writeFile("C:\\Users\\PandaIP\\Desktop\\" + id + ".json", wtoJson.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static List<WtoBean> getWtoSourceList(String IndicatorCode, Integer id) {
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

    public static List<String> getIndicators() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("subscription-key", KEY);
        String response = HttpUtil.get(INDICATORS_URL, paramMap);
        return JSONObject.parseArray(response, IndicatorBean.class).stream().map(IndicatorBean::getCode).collect(Collectors.toList());
    }

    public static void writeFile(String filePath, String sets)
            throws IOException {
        new File(filePath).delete();
        FileWriter fw = new FileWriter(filePath);
        PrintWriter out = new PrintWriter(fw);
        out.write(sets);
        out.println();
        fw.close();
        out.close();
    }
}
