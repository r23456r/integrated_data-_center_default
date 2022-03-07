package com.idc.vo;

import com.idc.dao.entity.IDCNodeDataVo;
import com.idc.dao.entity.IDCNodeInfoVo;

import java.util.ArrayList;
import java.util.List;

public class IntegratedDataVo {
    private String dataSource; // 数据源
    private List<IDCNodeInfoVo> nodeInfos; // 节点对象
    private List<IDCNodeDataVo> nodeDatas; // 数据对象

    private List<IDCNodeInfoVo> updateNodeInfos; // 节点对象(更新）
    private List<IDCNodeDataVo> updateNodeDatas; // 节点对象(更新）

    public IntegratedDataVo() {

    }

    public IntegratedDataVo(String dataSource) {
        this.dataSource = dataSource;
        this.nodeDatas = new ArrayList<>();
        this.nodeInfos = new ArrayList<>();
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public List<IDCNodeInfoVo> getNodeInfos() {
        return nodeInfos;
    }

    public void setNodeInfos(List<IDCNodeInfoVo> nodeInfos) {
        this.nodeInfos = nodeInfos;
    }

    public List<IDCNodeDataVo> getNodeDatas() {
        return nodeDatas;
    }

    public void setNodeDatas(List<IDCNodeDataVo> nodeDatas) {
        this.nodeDatas = nodeDatas;
    }

    public List<IDCNodeInfoVo> getUpdateNodeInfos() {
        return updateNodeInfos;
    }

    public void setUpdateNodeInfos(List<IDCNodeInfoVo> updateNodeInfos) {
        this.updateNodeInfos = updateNodeInfos;
    }

    public List<IDCNodeDataVo> getUpdateNodeDatas() {
        return updateNodeDatas;
    }

    public void setUpdateNodeDatas(List<IDCNodeDataVo> updateNodeDatas) {
        this.updateNodeDatas = updateNodeDatas;
    }
}
