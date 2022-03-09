package com.idc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.idc.common.constants.URLConstants;
import com.idc.common.utils.HttpWormUtils;
import com.idc.common.utils.TextIOStreamUtils;
import com.idc.common.utils.UtilHandle;
import com.idc.service.DataService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.stereotype.Service;

@Service("XHCountryDataService")
public class XHCountryDataServiceImpl implements DataService {

    @Override
    public JSONObject getDataInfo(String id) {
        return null;
    }

    @Override
    public JSONObject getDataInfo() {
        JSONObject jsonObject = getXHCountryData2022();
        return jsonObject;
    }


    @Test
    public void test() {
//        String jsonStr = TextIOStreamUtils.readerFile("D://XHCountryData.json");
//
//        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
//        StringBuffer str = new StringBuffer();
//        jsonObject.keySet().forEach(key -> {
//            JSONObject countryData = jsonObject.getJSONObject(key);
//            countryData.keySet().forEach(item -> {
//                JSONObject itemData = countryData.getJSONObject(item);
//                if ("IDCAttribute".equals(item)) {
//                    return;
//                }
//                str.append(key);
//                str.append("->");
//                str.append(item);
//                str.append("【");
//                if (itemData.containsKey("IDCAttribute") && itemData.getJSONObject("IDCAttribute").containsKey("divison")) {
//                    str.append(itemData.getJSONObject("IDCAttribute").getString("divison"));
//                }
//                str.append("】");
//                str.append(" : ");
//                if (itemData.containsKey("IDCData")) {
//                    JSONObject data = itemData.getJSONObject("IDCData");
//                    data.keySet().forEach(time->{
//                        str.append(data.getString(time));
//                        str.append("(");
//                        str.append(time);
//                        str.append("), ");
//
//                    });
//
//
//                }
//
//
//                str.append("\n");
//            });
//
//
//        });
//        TextIOStreamUtils.writeByFileWrite("D://data.txt", str.toString());
        JSONObject jsonObject = getXHCountryData2022();
        TextIOStreamUtils.writeByFileWrite("D://datacenter//XHCountryData.json", jsonObject.toJSONString());
    }

    private JSONObject getXHCountryData2022() {
        JSONObject xhCountryData = new JSONObject();
        String html = HttpWormUtils.getHtml("https://api.cnfin.com/roll/macro2/macroTopTree");
        JSONObject xhrequest = JSONObject.parseObject(html);
        JSONArray xhData = xhrequest.getJSONArray("data");
        JSONObject jsonObject = analysisCountryData(xhData);
        return jsonObject;

    }

    private JSONObject analysisCountryData(JSONArray xhData) {
        JSONObject xhCountryData = new JSONObject();
        for (int i = 0; i < xhData.size(); i++) {
            JSONObject contryData = xhData.getJSONObject(i);
            String countryName = contryData.getString("treeName");
            JSONArray treeData = contryData.getJSONArray("treeData");
            JSONObject attribute = new JSONObject();
            attribute.put("countryName", countryName);
            JSONObject data = getCountryDetailInfo(treeData);
            xhCountryData.put(countryName, UtilHandle.setNodeInfo(attribute, data));
        }
        return xhCountryData;
    }


    private JSONObject getCountryDetailInfo(JSONArray treeData) {
        JSONObject treeJsonData = new JSONObject();
        for (int i = 0; i < treeData.size(); i++) {
            JSONObject dataO = treeData.getJSONObject(i);
            String code = dataO.getString("code");
            String chartId = dataO.getString("chartId");
            String name = dataO.getString("name");
            if (chartId == null || "".equals(chartId)) {
                continue;
            }
            JSONObject xhData = getData(chartId, name);
            String title = xhData.getString("title");

            JSONObject attribute = new JSONObject();
            attribute.put("name", name);
            attribute.put("title", title);
            treeJsonData.put(name, UtilHandle.setNodeData(attribute, getData(xhData.getString("content"))));
        }
        return treeJsonData;
    }

    private JSONObject getData(String content) {
        content = content.replace("“", "\"");
        JSONObject data = new JSONObject();
        JSONArray arr = JSONArray.parseArray(content);
        for (int i = 0; i < arr.size(); i++) {
            data.put(arr.getJSONObject(i).getString("x"), arr.getJSONObject(i).getString("y"));
        }
        return data;
    }

    private JSONObject getData(String chartId, String name) {
        String url = "https://api.cnfin.com/roll/charts/getContent?ids=" + chartId;
        String html = HttpWormUtils.getHtml(url);
        JSONObject xhrequest = JSONObject.parseObject(html);
        JSONObject xhData = xhrequest.getJSONObject("data");
        JSONArray list = xhData.getJSONArray("list");
        return list.getJSONObject(0);
    }

    private JSONObject getXHCountryData() {
        JSONObject xhCountryData = new JSONObject();
        String html = HttpWormUtils.getHtml(URLConstants.XIN_HUA_WEB_URL);
        Document doc = Jsoup.parse(html);
        Elements dataList = doc.select("[class=grid_5 dataList]");
        for (Element element : dataList) {
            Elements countryList = element.select(".country");
            for (Element country : countryList) {
                Elements countryUrlData = country.select("a");
                if (countryUrlData.size() > 0) {
                    String countryName = country.text().trim();
                    String countryUrl = countryUrlData.attr("href").trim();
                    JSONObject attribute = new JSONObject();
                    attribute.put("countryName", countryName);
                    attribute.put("countryUrl", countryUrl);
                    JSONObject data = getCountryDetailInfo(countryUrl);
                    xhCountryData.put(countryName, UtilHandle.setNodeInfo(attribute, data));
                }
            }
        }
        Elements focusList = doc.select("[class=focusList]");
        for (Element element : focusList) {
            Elements sonCountryList = element.select("h4 > a");
            for (Element sonCountry : sonCountryList) {
                String countryName = sonCountry.text().trim();
                String countryUrl = sonCountry.attr("href").trim();
                JSONObject attribute = new JSONObject();
                attribute.put("countryName", countryName);
                attribute.put("countryUrl", countryUrl);
                JSONObject data = getCountryDetailInfo(countryUrl);
                xhCountryData.put(countryName, UtilHandle.setNodeInfo(attribute, data));
            }
        }
        return xhCountryData;
    }

    private JSONObject getCountryDetailInfo(String url) {
        JSONObject itemDatas = new JSONObject();
        String html = HttpWormUtils.getHtml(url);
        Document doc = Jsoup.parse(html);
        Elements dataList = doc.select("[class=mainContent] > [class=unilist]");
        for (Element element : dataList) {
            Elements unilist = element.select("li > a");
            for (Element item : unilist) {
                String itemName = item.text().trim();
                String itemUrl = item.attr("href").trim();
                JSONObject attribute = new JSONObject();
                attribute.put("itemName", itemName);
                attribute.put("itemUrl", itemUrl);
                JSONObject data = getItemDetailInfo(itemUrl, attribute);
                itemDatas.put(itemName, UtilHandle.setNodeData(attribute, data));
            }

        }
        return itemDatas;
    }

    private JSONObject getItemDetailInfo(String itemAdd, JSONObject attribute) {
        if (itemAdd != null && itemAdd.split("/").length > 3) {
            String itemId = itemAdd.split("/")[3];
            String json = HttpWormUtils.getHtml("http://dc.xinhua08.com/?action=json&id=" + itemId);
            if (json != null) {
                json = json.substring(2, json.length() - 2);
                return converJSONdata(json, attribute);
            }
        }
        return new JSONObject();
    }

    private JSONObject converJSONdata(String json, JSONObject attribute) {
        JSONObject data = new JSONObject();
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONArray xAxisDatas = null;
        JSONArray seriesDatas = null;
        if (jsonObject.containsKey("xAxis")) {
            JSONObject xAxis = jsonObject.getJSONObject("xAxis");
            xAxisDatas = xAxis.getJSONArray("data");
        }
        if (jsonObject.containsKey("series")) {
            JSONArray series = jsonObject.getJSONArray("series");
            attribute.put("divison", series.getJSONObject(0).getString("name"));
            seriesDatas = series.getJSONObject(0).getJSONArray("data");
        }
        if (xAxisDatas != null && seriesDatas != null && xAxisDatas.size() == seriesDatas.size()) {
            for (int i = 0; i < xAxisDatas.size(); i++) {
                data.put(xAxisDatas.getString(i), seriesDatas.get(i));
            }
        }
        return data;
    }
}
