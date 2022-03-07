package com.idc.dao.entity;

import java.util.Date;

public class DataSourceInfo {
    private int id;  //id
    private String dataSource;  // 数据源
    private String dataName;  // 数据名称
    private String nodeInfoTblName;  // 节点表名称
    private String nodeDataTblName;  // 数据表名称
    private String iconPath;      // 图片路径
    private String datacomment;  // 数据描述
    private Date createDate;   // 创建时间
    private Date updateDate;  // 更新时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getNodeInfoTblName() {
        return nodeInfoTblName;
    }

    public void setNodeInfoTblName(String nodeInfoTblName) {
        this.nodeInfoTblName = nodeInfoTblName;
    }

    public String getNodeDataTblName() {
        return nodeDataTblName;
    }

    public void setNodeDataTblName(String nodeDataTblName) {
        this.nodeDataTblName = nodeDataTblName;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getDatacomment() {
        return datacomment;
    }

    public void setDatacomment(String datacomment) {
        this.datacomment = datacomment;
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
}
