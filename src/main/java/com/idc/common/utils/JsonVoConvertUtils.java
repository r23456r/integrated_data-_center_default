package com.idc.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.idc.dao.entity.IDCNodeDataVo;
import com.idc.dao.entity.IDCNodeInfoVo;
import com.idc.vo.IntegratedDataVo;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonVoConvertUtils {

    public static IntegratedDataVo jsonToVo(JSONObject jsonData, String dataSource) {
        IntegratedDataVo integratedDataVo = new IntegratedDataVo(dataSource);
        createNode(dataSource, jsonData, integratedDataVo, "0");
        return integratedDataVo;
    }

    public static JSONObject voToJson(List<IDCNodeInfoVo> idcNodeInfoVos, List<IDCNodeDataVo> idcNodeDataVos) {
        JSONObject jsonObject = new JSONObject();
        Map<String, List<IDCNodeInfoVo>> nodeInfoMap = idcNodeInfoVos.stream().collect(Collectors.groupingBy(IDCNodeInfoVo::getFatherId));
        Map<String, List<IDCNodeDataVo>> nodedataMap = idcNodeDataVos.stream().collect(Collectors.groupingBy(IDCNodeDataVo::getNodeId));

        if (nodeInfoMap.containsKey("0")) {
            // 找到公共父节点
            String rootId = nodeInfoMap.get("0").get(0).getId();
            jsonObject = getSonJsonObject(rootId, jsonObject, nodeInfoMap, nodedataMap);
        }
        return jsonObject;
    }

    private static JSONObject getSonJsonObject(String fatherId, JSONObject jsonObject,
                                               Map<String, List<IDCNodeInfoVo>> nodeInfoMap,
                                               Map<String, List<IDCNodeDataVo>> nodedataMap) {
        if (nodeInfoMap.containsKey(fatherId)) {
            List<IDCNodeInfoVo> sonList = nodeInfoMap.get(fatherId);
            sonList.forEach(son -> {
                JSONObject sonObject = new JSONObject();
                if (son.getiDCAttributei() != null && !"".equals(son.getiDCAttributei())) {
                    sonObject.put("IDCAttribute", JSONObject.parseObject(son.getiDCAttributei()));
                }
                if (1 == son.getiDCType() && nodedataMap.containsKey(son.getId())) {
                    sonObject.put("IDCData", getIDCDataJson(nodedataMap.get(son.getId())));
                }
                sonObject.put("IDCType", son.getiDCType());
                sonObject = getSonJsonObject(son.getId(), sonObject, nodeInfoMap, nodedataMap);
                jsonObject.put(son.getNodeName(), sonObject);
            });
        }
        return jsonObject;
    }

    private static JSONObject getIDCDataJson(List<IDCNodeDataVo> nodeDataVos) {
        JSONObject jsonObject = new JSONObject();
        nodeDataVos.forEach(node -> {
            jsonObject.put(node.getParamKey(), node.getParamValue());
        });
        return jsonObject;
    }


    private static void createNode(String nodeName, JSONObject jsonData, IntegratedDataVo integratedDataVo, String fatherId) {
        String id = UUIDUtils.getUUID();
        String IDCAttribute = "";
        boolean isIncludeData = false;
        for (String key : jsonData.keySet()) {
            switch (key) {
                case "IDCData":
                    isIncludeData = true;
                    createNodeData(integratedDataVo, jsonData.getJSONObject(key), id);
                    break;
                case "IDCAttribute":
                    IDCAttribute = jsonData.getJSONObject("IDCAttribute").toJSONString();
                    break;
                default:
                    createNode(key, jsonData.getJSONObject(key), integratedDataVo, id);
                    break;
            }
        }
        createNodeObject(integratedDataVo, nodeName, id, fatherId, IDCAttribute, isIncludeData);
    }


    private static void createNodeData(IntegratedDataVo integratedDataVo, JSONObject jsonData, String nodeId) {
        if (jsonData.size() > 0) {
            for (String key : jsonData.keySet()) {
                IDCNodeDataVo idcNodeDataVo = new IDCNodeDataVo();
                idcNodeDataVo.setNodeId(nodeId);
                idcNodeDataVo.setParamKey(key);
                idcNodeDataVo.setParamValue(jsonData.getString(key));
                idcNodeDataVo.setCreateDate(new Date());
                idcNodeDataVo.setUpdateDate(new Date());
                integratedDataVo.getNodeDatas().add(idcNodeDataVo);
            }
        }
    }

    private static void createNodeObject(IntegratedDataVo integratedDataVo, String nodeName, String id,
                                         String fatherId, String IDCAttribute, boolean isIncludeData) {
        IDCNodeInfoVo infoVo = new IDCNodeInfoVo();
        infoVo.setId(id);
        infoVo.setFatherId(fatherId);
        infoVo.setNodeName(nodeName);
        infoVo.setiDCAttributei(IDCAttribute);
        if (isIncludeData) {
            infoVo.setiDCType(1);
        } else {
            infoVo.setiDCType(0);
        }
        infoVo.setCreateDate(new Date());
        infoVo.setUpdateDate(new Date());
        integratedDataVo.getNodeInfos().add(infoVo);
    }
}
