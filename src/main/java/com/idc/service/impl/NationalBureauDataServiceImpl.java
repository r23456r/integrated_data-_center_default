package com.idc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.idc.common.constants.Constants;
import com.idc.common.constants.URLConstants;
import com.idc.common.utils.HttpWormUtils;
import com.idc.common.utils.TextIOStreamUtils;
import com.idc.service.DataService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service("NationalBureauDataService")
public class NationalBureauDataServiceImpl implements DataService {

    private final Map<String, String> nationalMap;
    private final Map<String, String> nationalMap2;
    private final String filePath;

    public NationalBureauDataServiceImpl() {
        nationalMap = new HashMap<>();
//        nationalMap.put("hgyd", "月度数据");
//        nationalMap.put("hgjd", "季度数据");
//        nationalMap.put("hgnd", "年度数据");
        nationalMap.put("fsyd", "分省月度数据");
        nationalMap.put("fsjd", "分省季度数据");
        nationalMap.put("fsnd", "分省年度数据");
        nationalMap.put("csyd", "主要城市月度价格");
        nationalMap.put("csnd", "主要城市年度数据");
        nationalMap.put("gatyd", "港澳台月度数据");
        nationalMap.put("gatnd", "港澳台年度数据");
        nationalMap.put("gjyd", "主要国家(地区)月度数据");
        nationalMap.put("gjydsdj", "三大经济体月度数据");
        nationalMap.put("gjydsc", "国际市场月度商品价格");
        nationalMap.put("gjnd", "主要国家(地区)年度数据");

        nationalMap2 = new HashMap<>();
        for (String key : nationalMap.keySet()) {
            nationalMap2.put(nationalMap.get(key), key);
        }
        filePath = "D:\\national";
    }


    @Override
    public JSONObject getDataInfo(String id) {
        return null;
    }

    @Override
    public JSONObject getDataInfo() {
        return null;
    }

    @Test
    public void ss() {
        readNationalBureauMainView();
//        readNationalData();
    }

    /**
     * ------------------------------------------------------------------------
     * 组织数据
     * -------------------------------------------------------------------------
     */
    private JSONObject readNationalData() {
        JSONObject jsonObject = new JSONObject();
        for (String key : nationalMap2.keySet()) {
            String jsonStr = TextIOStreamUtils.readerFile(filePath + "\\" + key + "\\" + key + "_keyData.json");
            JSONObject keyDataJson = JSONObject.parseObject(jsonStr);
            JSONObject jsonDAta = getJSONDataByFileName(keyDataJson, key);
            jsonObject.put(key, jsonDAta);
            TextIOStreamUtils.writeByFileWrite(filePath + "\\" + key + "_JSONDATA.json", jsonDAta.toJSONString());
            jsonObject.put(Constants.IDC_TYPE, "0");
            jsonObject.put(Constants.IDC_ATTRIBUTE, new JSONObject());
        }
        TextIOStreamUtils.writeByFileWrite(filePath + "\\nationalAllData.json", jsonObject.toJSONString());
        return jsonObject;
    }

    private JSONObject getJSONDataByFileName(JSONObject oJson, String fileName) {
        JSONObject jsonData = new JSONObject();
        if (oJson.containsKey("id")) {
            jsonData.put(Constants.IDC_TYPE, "0");
            jsonData.put(Constants.IDC_ATTRIBUTE, oJson);
            getJsonDataInFile(jsonData, oJson.getString("id"), fileName);
        } else {
            for (String key : oJson.keySet()) {
                jsonData.put(key, getJSONDataByFileName(oJson.getJSONObject(key), fileName));
                jsonData.put(Constants.IDC_TYPE, "0");
                jsonData.put(Constants.IDC_ATTRIBUTE, new JSONObject());
            }
        }
        return jsonData;
    }

    private void getJsonDataInFile(JSONObject jsonData, String id, String fileName) {
        File idFile = new File(filePath + "\\" + fileName + "\\" + id);
        System.out.println(idFile.getPath());
        File[] sonFiles = idFile.listFiles();
        if (sonFiles != null && sonFiles.length > 0) {
            if (sonFiles.length == 1 && sonFiles[0].getName().equals("data.json")) {
                String jsonStr = TextIOStreamUtils.readerFile(sonFiles[0].getPath());
                JSONObject keyDataJson = JSONObject.parseObject(jsonStr);
                analysisJsonData(keyDataJson, jsonData);
            } else {
                for (File file : sonFiles) {
                    String jsonStr = TextIOStreamUtils.readerFile(file.getPath());
                    JSONObject keyDataJson = JSONObject.parseObject(jsonStr);
                    JSONObject jsonItem = new JSONObject();
                    jsonItem.put(Constants.IDC_TYPE, "0");
                    jsonItem.put(Constants.IDC_ATTRIBUTE, new JSONObject());
                    analysisJsonData(keyDataJson, jsonItem);
                    jsonData.put(file.getName().replace(".json", ""), jsonItem);
                }
            }
        }
    }


    private void analysisJsonData(JSONObject keyDataJson, JSONObject jsonData) {
        Map<String, JSONObject> attrMap = new HashMap<>();
        Map<String, JSONObject> dataMap = new HashMap<>();
        JSONArray wdnodes = keyDataJson.getJSONObject("returndata").getJSONArray("wdnodes");
        JSONArray datanodes = keyDataJson.getJSONObject("returndata").getJSONArray("datanodes");
        for (int i = 0; i < wdnodes.size(); i++) {
            JSONObject wdsItem = wdnodes.getJSONObject(i);
            if (wdsItem.getString("wdcode").equals("zb")) {
                JSONArray nodes = wdsItem.getJSONArray("nodes");
                for (int j = 0; j < nodes.size(); j++) {
                    JSONObject node = nodes.getJSONObject(j);
                    attrMap.put(node.getString("code"), node);
                }
            } else if (wdsItem.getString("wdcode").equals("sj")) {
            }
        }

        for (int i = 0; i < datanodes.size(); i++) {
            JSONObject dataNode = datanodes.getJSONObject(i);
            JSONArray wds = dataNode.getJSONArray("wds");
            String zb = "";
            String sj = "";
            String data = dataNode.getJSONObject("data").getString("data");
            for (int j = 0; j < wds.size(); j++) {
                JSONObject wd = wds.getJSONObject(j);
                if (wd.getString("wdcode").equals("zb")) {
                    zb = wd.getString("valuecode");
                } else if (wd.getString("wdcode").equals("sj")) {
                    sj = wd.getString("valuecode");
                }
            }
            JSONObject itemData = null;
            if (dataMap.containsKey(zb)) {
                itemData = dataMap.get(zb);
            } else {
                itemData = new JSONObject();
            }
            itemData.put(sj, data);
            dataMap.put(zb, itemData);
        }

        for (String code : attrMap.keySet()) {
            JSONObject nodeData = new JSONObject();
            JSONObject node = attrMap.get(code);
            String name = node.getString("name");
            JSONObject itemData = dataMap.get(code);
            nodeData.put(Constants.IDC_TYPE, "1");
            nodeData.put(Constants.IDC_ATTRIBUTE, node);
            nodeData.put(Constants.IDC_DATA, itemData);
            jsonData.put(name, nodeData);
        }

    }

    /**
     * ------------------------------------------------------------------------
     * 爬取数据
     * -------------------------------------------------------------------------
     */
    public void readNationalBureauMainView() {
        String html = HttpWormUtils.getHtml(URLConstants.NATIONAL_BUREAU_DATA_INDEX);
        Document doc = Jsoup.parse(html);
        Elements fieldList = doc.select("a[href~=^/easyquery.htm?]");
        createDataByField(fieldList);
    }

    public void createDataByField(Elements fieldList) {
        for (Element field : fieldList) {
            System.out.println(field.text());
            if (nationalMap2.containsKey(field.text())) {
                String filedCode = nationalMap2.get(field.text());
                HttpWormUtils.getHtml(URLConstants.NATIONAL_BUREAU_DATA + field.attr("href"));
                File file = new File("D://national//" + field.text());
                file.mkdir();
                // 获取otherWDS
                JSONObject otherWds = getOtherWds(filedCode);
                JSONObject fieldData = getFiledTreeData(filedCode, field.text(), "zb", otherWds);
                TextIOStreamUtils.writeByFileWrite("D://national//" + field.text() + "//" + field.text() + "_keyData.json", fieldData.toJSONString());
            }
        }
    }

    private JSONObject getFiledTreeData(String filedCode, String fieldName, String id, JSONObject otherWds) {
        JSONObject returnJson = new JSONObject();
        JSONArray jsonArray = getChildNode(id, filedCode);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonData = jsonArray.getJSONObject(i);
            boolean isParent = jsonData.getBoolean("isParent");
            if (isParent) {
                returnJson.put(jsonData.getString("name"), getFiledTreeData(filedCode, fieldName, jsonData.getString("id"), otherWds));
            } else {
                returnJson.put(jsonData.getString("name"), setQueryData(jsonData, fieldName, otherWds));
            }

        }
        return returnJson;
    }

    private JSONObject setQueryData(JSONObject jsonData, String fieldName, JSONObject otherWds) {
        String id = jsonData.getString("id");
        File file = new File("D://national//" + fieldName + "//" + id);
//        if (!id.equals("A06050101")) {
//            return jsonData;
//        }
        file.mkdir();
        JSONObject returnObject;
        if (otherWds.containsKey("reg")) {
            JSONArray regs = otherWds.getJSONArray("reg");
            for (int i = 0; i < regs.size(); i++) {
                String path = "D://national//" + fieldName + "//" + id + "//" + regs.getJSONObject(i).getString("name") + ".json";
                if (!checkData(path)) {
                    System.out.println(path);
                    JSONObject data = getQueryData(jsonData, "%5B%7B%22wdcode%22%3A%22reg%22%2C%22valuecode%22%3A%22" +
                            regs.getJSONObject(i).getString("code") + "%22%7D%5D", otherWds);
                    TextIOStreamUtils.writeByFileWrite(path, data.toJSONString());
                }
            }
        } else {
            String path = "D://national//" + fieldName + "//" + id + "//" + "data.json";
            if (!checkData(path)) {
                System.out.println(path);
                returnObject = getQueryData(jsonData, "[]", otherWds);
                TextIOStreamUtils.writeByFileWrite(path, returnObject.toJSONString());
            }
        }
        return jsonData;
    }


    private boolean checkData(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.length() > 2) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }


    private JSONObject getQueryData(JSONObject jsonData, String wds, JSONObject otherWds) {
        Map<String, String> paramData = new HashMap<>();
        String sj = otherWds.getString("sj");
        paramData.put("m", "QueryData");
        paramData.put("dbcode", jsonData.getString("dbcode"));

        paramData.put("rowcode", "zb");
        paramData.put("colcode", "sj");
        paramData.put("wds", wds);
        paramData.put("dfwds", "%5B%7B%22wdcode%22%3A%22zb%22%2C%22valuecode%22%3A%22" + jsonData.getString("id") +
                "%22%7D%2C%7B%22wdcode%22%3A%22sj%22%2C%22valuecode%22%3A%22" + sj + "%22%7D%5D");
        paramData.put("k1", Long.toString(System.currentTimeMillis()));
        paramData.put("h", "1");
        String data = HttpWormUtils.getHtmlGet("https://data.stats.gov.cn/easyquery.htm", paramData);
        return JSONObject.parseObject(data);
//        return getQueryData2(jsonData, wds, otherWds.getString("sj"));

    }

    private JSONObject getQueryData2(JSONObject jsonData, String wds, String sjCode) {
        Map<String, String> paramData = new HashMap<>();
        paramData.put("m", "QueryData");
        paramData.put("dbcode", jsonData.getString("dbcode"));
        paramData.put("rowcode", "zb");
        paramData.put("colcode", "sj");
        paramData.put("wds", "[]");
        paramData.put("dfwds", "%5B%7B%22wdcode%22%3A%22sj%22%2C%22valuecode%22%3A%22" + sjCode + "%22%7D%5D");
        paramData.put("k1", Long.toString(System.currentTimeMillis()));
        String data = HttpWormUtils.getHtmlGet("https://data.stats.gov.cn/easyquery.htm", paramData);
        return JSONObject.parseObject(data);
    }

    private JSONObject getOtherWds(String filedCode) {
        Map<String, String> paramData = new HashMap<>();
        paramData.put("dbcode", filedCode);
        paramData.put("rowcode", "zb");
        paramData.put("colcode", "sj");
        paramData.put("m", "getOtherWds");
        paramData.put("k1", Long.toString(System.currentTimeMillis()));
        paramData.put("wds", "[]");
        String data = HttpWormUtils.getHtmlPost(URLConstants.NATIONAL_BUREAU_DATA_EASYQUERY, paramData);

        JSONObject otherWds = JSONObject.parseObject(data);
        JSONObject resoutData = new JSONObject();
        JSONArray otherWdArr = otherWds.getJSONArray("returndata");
        for (int i = 0; i < otherWdArr.size(); i++) {
            JSONObject otherWd = otherWdArr.getJSONObject(i);
            String wdcode = otherWd.getString("wdcode");
            if ("sj".equals(wdcode)) {
                JSONArray nodes = otherWd.getJSONArray("nodes");
                JSONObject node = nodes.getJSONObject(nodes.size() - 1);
                resoutData.put("sj", nodes.getJSONObject(nodes.size() - 1).getString("code"));
            } else if ("reg".equals(wdcode)) {
                resoutData.put("reg", otherWd.getJSONArray("nodes"));
            }

        }

        return resoutData;
    }

    private JSONArray getChildNode(String id, String filedCode) {
        Map<String, String> paramData = new HashMap<>();
        paramData.put("id", id);
        paramData.put("dbcode", filedCode);
        paramData.put("wdcode", "zb");
        paramData.put("m", "getTree");
        String data = HttpWormUtils.getHtmlPost(URLConstants.NATIONAL_BUREAU_DATA_EASYQUERY, paramData);
        return JSONArray.parseArray(data);
    }
}
