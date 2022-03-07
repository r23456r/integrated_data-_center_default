package com.idc.dao.entity;

import java.util.Date;
import java.util.Map;

public class IDCNodeInfoVo {
    private String id; // ID
    private String fatherId; // 父节点ID
    private String nodeName;  // 节点名称
    private String iDCAttributei; // 属性数据
    private int iDCType; // 是否包含数据
    private Date createDate;   // 创建时间
    private Date updateDate;  // 更新时间
    private String voKey;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }


    public String getiDCAttributei() {
        return iDCAttributei;
    }

    public void setiDCAttributei(String iDCAttributei) {
        this.iDCAttributei = iDCAttributei;
    }

    public int getiDCType() {
        return iDCType;
    }

    public void setiDCType(int iDCType) {
        this.iDCType = iDCType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getVoKey(Map<String, IDCNodeInfoVo> idcNodeInfoVoMap) {
        if (this.voKey != null && !"".equals(this.voKey)) {
            return this.voKey;
        }
        String key = "";
        if (idcNodeInfoVoMap.containsKey(this.fatherId)) {
            key = idcNodeInfoVoMap.get(this.fatherId).getVoKey(idcNodeInfoVoMap) + "_";
        }
        this.voKey = key + this.nodeName;
        return this.voKey;
    }
}
