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

import java.util.Date;

@Service("GlobalFirePowerDataService")
public class GlobalFirePowerDataServiceImpl implements DataService {

    @Override
    public JSONObject getDataInfo(String id) {
        return null;
    }

    @Override
    public JSONObject getDataInfo() {
        JSONObject jsonObject = getGlobalFirePowerData();
        return jsonObject;
    }

    @Test
    public void test(){
        JSONObject jsonObject = getGlobalFirePowerData();
        TextIOStreamUtils.writeByFileWrite("D://datacenter//GlobalFirePowerData.json", jsonObject.toJSONString());
    }

    private JSONObject getGlobalFirePowerData() {
        JSONObject globalFirePowerData = new JSONObject();
        String html = HttpWormUtils.getHtml(URLConstants.GLOBAL_FIRE_POWER_COUNTRIES_WEB_URL);
        Document doc = Jsoup.parse(html);

        Elements dataList = doc.select("a[title~=^Military strength values of]");
        for (Element element : dataList) {
            JSONObject jsonObject = new JSONObject();
            String dataUrl = URLConstants.GLOBAL_FIRE_POWER_WEB_URL + element.attr("href").trim();
            String rankNumContainer = element.select("[class=rankNumContainer]").text();
            String countryName = element.select("[class=countryName]").text();
            String shortFormName = element.select("[class=shortFormName]").text();
            String pwrIndxScore = element.select("span[class=textLarge] > span[class=textDkGray]").text();
            JSONObject attribute = new JSONObject();
            attribute.put("dataUrl", dataUrl);
            attribute.put("countryName", countryName);
            attribute.put("shortFormName", shortFormName);
            JSONObject data = getCountryDetilInfo(dataUrl);
            data.put("rankNumContainer", UtilHandle.createDataByYYYYMM(new Date(), rankNumContainer));
            data.put("pwrIndxScore", UtilHandle.createDataByYYYYMM(new Date(), pwrIndxScore.replace("PwrIndx Score: ", "").trim()));
            globalFirePowerData.put(countryName, UtilHandle.setNodeInfo(attribute, data));
        }
        return globalFirePowerData;
    }

    private JSONObject getCountryDetilInfo(String url) {
        JSONObject jsonObject = new JSONObject();
        String html = HttpWormUtils.getHtml(url);
        Document doc = Jsoup.parse(html);
        Elements buttonList = doc.select("button[class=\"collapsible\"]");
        Elements divList = doc.select("div[class=\"contentSpecs\"]");
        for (int i = 0; i < buttonList.size(); i++) {
            String category = buttonList.get(i).text().replace("[+]", "").trim();
            jsonObject.put(category, UtilHandle.setNodeInfoOnly(getItemInfo(divList.get(i))));
        }
        return jsonObject;
    }

    private JSONObject getItemInfo(Element item) {
        JSONObject itemInfo = new JSONObject();
        Elements overViemItems = item.select("div[class=\"overviewHolder\"] > span[class=\"textWhite\"]");
        if (overViemItems.size() > 0) {
            for (Element overViemItem : overViemItems) {
                String itemName = overViemItem.select("span[class=\"textBold textNormal textShadow\"]").text();
                JSONObject attribute = new JSONObject();
                attribute.put("itemName", itemName);
                JSONObject data = UtilHandle.createDataByYYYYMM(new Date(),
                        overViemItem.select("div[class=\"overviewRankHolder\"]").text());
                itemInfo.put(itemName, UtilHandle.setNodeInfo(attribute, data));
            }
        }
        Elements otherItems = item.select("div[class=\"specsGenContainers picTrans3 zoom\"]");
        if (otherItems.size() > 0) {
            for (Element otherItem : otherItems) {

                String itemName = otherItem.select("span[class=\"textLarge textYellow textBold textShadow\"]").text();
                JSONObject attribute = new JSONObject();
                attribute.put("itemName", itemName);
                JSONObject data = UtilHandle.createDataByYYYYMM(new Date(),otherItem.select("span").last().text());
                itemInfo.put(itemName, UtilHandle.setNodeInfo(attribute, data));
            }
        }

        return itemInfo;
    }
}
