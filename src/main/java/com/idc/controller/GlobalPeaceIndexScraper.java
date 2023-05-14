package com.idc.controller;

import antlr.StringUtils;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.idc.common.utils.UtilHandle;
import com.idc.dao.entity.GlobalPeaceVo;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class GlobalPeaceIndexScraper {

    public static void main(String[] args) throws Exception {


        // Set up URL and connection
        String url = "https://en.wikipedia.org/wiki/Global_Peace_Index";
//        String result = HttpUtils.vpnGet(url);
        File tmpTmp = new File("C:\\Users\\PandaIP\\Desktop\\tmp.html");
//        FileUtil.writeBytes(result.getBytes(StandardCharsets.UTF_8), tmpTmp);
        String result = FileUtil.readString(tmpTmp, StandardCharsets.UTF_8);
        Document doc = Jsoup.parse(result, url);
        List<GlobalPeaceVo> list = new ArrayList<>();
        File outFile = new File("C:\\Users\\PandaIP\\Desktop\\vital_2.json");

        // Find the table of interest and extract the rows
        List<Integer> tableIds = Arrays.asList(1,2,3);
        for (Integer tableId : tableIds) {
            Element table = doc.select("table").get(tableId);

            Elements rows = table.select("tr");
            Elements elements = table.select("th[colspan=2]");
            List<String> years = new ArrayList<>();
            for (int i = 0; i < elements.size(); i++) {
                years.add(elements.get(i).text().substring(0, 4));
            }
            System.out.println(years);

            // Create output file and writer
            // Loop over rows and write to file
            for (int i = 2; i < rows.size(); i++) {
                String country = "";
                Element row = rows.get(i);
                Elements cols = row.select("td");
                System.out.println("当前col Size： " + cols.size());
                int count1 = Integer.parseInt(years.get(0));
                for (int i1 = 0; i1 < cols.size(); i1++) {
                    Element element = cols.get(i1);
                    if (element.text().equals("—")) {
                        System.out.println("年份: " + count1--);
                    }
                    if (element.attributes().size() == 0) {

                        if (element.childNodeSize() != 1) {
                            country = element.text();
                        }

                        if (element.text().contains(".")) {
                            String score = element.text();
                            String rank = cols.get(i1 - 1).text();
                            GlobalPeaceVo peaceVo = new GlobalPeaceVo(country.replace(" ", ""), rank, score, String.valueOf(count1));
                            System.out.println("-----------" + peaceVo.toString());
                            list.add(peaceVo);
                            count1--;
                        }
                    }
                }
            }
        }


        // Clean up
        m1(list);
        System.out.println("Data saved to: " + outFile.getAbsolutePath());
    }


    private static void m1(List<GlobalPeaceVo> wtoBeans) throws IOException {
        // translate data
        File transJsonFile = new File("C:\\Users\\PandaIP\\Desktop\\translate.json");
        String translateJson = FileUtil.readString(transJsonFile, StandardCharsets.UTF_8);
        JSONObject transJson = JSONObject.parseObject(translateJson);

        Map jsonResult = new LinkedHashMap();
        Map<String, List<GlobalPeaceVo>> collect = wtoBeans.stream().collect(Collectors.groupingBy(GlobalPeaceVo::getDate));
        Map<String, Object> treeJsonData = new LinkedHashMap();
        for (String date : collect.keySet()) {
            Map attrB = new LinkedHashMap<>();
            attrB.put("date", date);
            Map dataB = new LinkedHashMap();
            Map<String, List<GlobalPeaceVo>> countryMap = collect.get(date).stream().collect(Collectors.groupingBy(GlobalPeaceVo::getCountry));
            List<String> sortedList = countryMap.keySet().stream().sorted().collect(Collectors.toList());
            for (String country : sortedList) {
                Map attrC = new LinkedHashMap<>();
                Map dataC = new LinkedHashMap();
                if (StringUtil.isBlank(country)) {
                    continue;
                }
                attrC.put("countryEn", country);
                Object countryCn = transJson.get(country);
                if (countryCn == null) {
                    continue;
                }
                attrC.put("countryCn", countryCn);
                List<GlobalPeaceVo> globalPeaceVos = countryMap.get(country);
                for (GlobalPeaceVo globalPeaceVo : globalPeaceVos) {
                    dataC.put("rank", globalPeaceVo.getRank());
                    dataC.put("score", globalPeaceVo.getScore());
                }
                if (dataC == null) {
                    System.out.println("-------??");
                }
                dataB.put(countryCn, UtilHandle.setNodeDataMap(attrC, dataC));
            }
            if (dataB == null) {
                System.out.println("-------??");
            }
            Map resultB = UtilHandle.setNodeDataMap(attrB, dataB);
            treeJsonData.put(date, resultB);
        }
        //date重排序
        Map<String,Object> dateMap = new LinkedHashMap<>();
        List<String> sortedDateList = treeJsonData.keySet().stream().sorted().collect(Collectors.toList());
        for (String date : sortedDateList) {
            dateMap.put(date, treeJsonData.get(date));
        }
        jsonResult.put("Global_Peace_Index", dateMap);

        writeFile("C:\\Users\\PandaIP\\Desktop\\vital_2.json", JSONObject.toJSONString(jsonResult));
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
}
