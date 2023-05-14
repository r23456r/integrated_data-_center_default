package com.idc.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.idc.common.constants.Constants;
import com.idc.dao.entity.IDCNodeDataVo;
import com.idc.dao.entity.IDCNodeInfoVo;
import com.idc.vo.IntegratedDataVo;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class UtilHandle {
    public static JSONObject setNodeInfo(JSONObject attribute, JSONObject data) {
        JSONObject nodeData = data;
        if (data == null) {
            nodeData = new JSONObject();
        }
        nodeData.put(Constants.IDC_ATTRIBUTE, attribute);
        return nodeData;
    }

    public static JSONObject setNodeData(JSONObject attribute, JSONObject data) {
        JSONObject nodeData = new JSONObject();
        nodeData.put(Constants.IDC_DATA, data);
        nodeData.put(Constants.IDC_ATTRIBUTE, attribute);
        return nodeData;
    }
    public static Map setNodeDataMap(Map<Object,Object> attribute, Map<Object,Object> data) {
        Map nodeData = new LinkedHashMap<>();
        nodeData.put(Constants.IDC_DATA, data);
        nodeData.put(Constants.IDC_ATTRIBUTE, attribute);
        return nodeData;
    }
    public static JSONObject setNodeInfoOnly(JSONObject data) {
        JSONObject nodeData = data;
        if (data == null) {
            nodeData = new JSONObject();
        }
        return nodeData;
    }

    public static JSONObject setNodeAttribute(JSONObject attribute) {
        JSONObject nodeData = new JSONObject();
        nodeData.put(Constants.IDC_ATTRIBUTE, attribute);
        return nodeData;
    }


    public static JSONObject createDataByYYYYMM(Date date, String data) {
        JSONObject returnData = new JSONObject();
        JSONObject nodeData = new JSONObject();
        nodeData.put(DateFormatUtils.formatDateForYYYYMM(date), data);
        returnData.put("IDCData", nodeData);
        return returnData;
    }

    public static void compareIDCNode(List<IDCNodeInfoVo> oldNodes, List<IDCNodeDataVo> oldDatas, IntegratedDataVo newVo) {
        if (oldNodes == null || oldNodes.isEmpty()) {
            return;
        }
        Map<String, IDCNodeInfoVo> oldNodeMap = oldNodes.stream().collect(Collectors.toMap(IDCNodeInfoVo::getId, a -> a));
        Map<String, List<IDCNodeDataVo>> oldDataMap = oldDatas.stream().collect(Collectors.groupingBy(IDCNodeDataVo::getNodeId));
        Map<String, IDCNodeInfoVo> newNodeMap = newVo.getNodeInfos().stream().collect(Collectors.toMap(IDCNodeInfoVo::getId, a -> a));
        Map<String, List<IDCNodeDataVo>> newDataMap = newVo.getNodeDatas().stream().collect(Collectors.groupingBy(IDCNodeDataVo::getNodeId));

        List<IDCNodeInfoVo> addNodeList = new ArrayList<>();
        List<IDCNodeInfoVo> updateNodeList = new ArrayList<>();
        List<IDCNodeDataVo> addDataList = new ArrayList<>();
        List<IDCNodeDataVo> updateDataList = new ArrayList<>();

        Map<String, String> idRelationMap = new HashMap<>();

        // 根据关键key值 组织 nodeMap
        Map<String, IDCNodeInfoVo> oldNodeMapByKey = createIDCNodeInfoVoMapByKey(oldNodeMap);
        Map<String, IDCNodeInfoVo> newNodeMapByKey = createIDCNodeInfoVoMapByKey(newNodeMap);

        newNodeMapByKey.keySet().forEach(key -> {
            if (oldNodeMapByKey.containsKey(key)) {
                if (!oldNodeMapByKey.get(key).getiDCAttributei().equals(newNodeMapByKey.get(key).getiDCAttributei())) {
                    newNodeMapByKey.get(key).setId(oldNodeMapByKey.get(key).getId());
                    updateNodeList.add(newNodeMapByKey.get(key));
                }
                idRelationMap.put(newNodeMapByKey.get(key).getId(), oldNodeMapByKey.get(key).getId());
            } else {
                addNodeList.add(newNodeMapByKey.get(key));
            }
        });

        newDataMap.keySet().forEach(key -> {
            if (idRelationMap.containsKey(key)) {
                String oldNodeId = idRelationMap.get(key);
                List<IDCNodeDataVo> newDataList = newDataMap.get(key);
                List<IDCNodeDataVo> oldDataList = oldDataMap.containsKey(oldNodeId) ? oldDataMap.get(oldNodeId) : null;

                for (IDCNodeDataVo newData : newDataList) {
                    newData.setNodeId(oldNodeId);
                    boolean isExist = false;
                    inner:
                    for (IDCNodeDataVo oldData : oldDataList) {
                        if (newData.getParamKey().equals(oldData.getParamKey())) {
                            isExist = true;
                            if (!newData.getParamValue().equals(oldData.getParamValue())) {
                                newData.setId(oldData.getId());
                                updateDataList.add(newData);
                            }
                            break inner;
                        }
                    }
                    if (!isExist) {
                        addDataList.add(newData);
                    }
                }
            } else {
                addDataList.addAll(newDataMap.get(key));
            }
        });
        newVo.setNodeInfos(addNodeList);
        newVo.setNodeDatas(addDataList);
        newVo.setUpdateNodeDatas(updateDataList);
        newVo.setUpdateNodeInfos(updateNodeList);
    }

    public static Map<String, IDCNodeInfoVo> createIDCNodeInfoVoMapByKey(Map<String, IDCNodeInfoVo> idcNodeInfoVoMap) {
        Map<String, IDCNodeInfoVo> iDCNodeInfoVoMapByKey = new HashMap<>();
        idcNodeInfoVoMap.values().forEach(entity -> {
            iDCNodeInfoVoMapByKey.put(entity.getVoKey(idcNodeInfoVoMap), entity);
        });
        return iDCNodeInfoVoMapByKey;
    }
}
