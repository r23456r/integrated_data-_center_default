package com.idc.service.impl;

import com.idc.dao.entity.DataSourceInfo;
import com.idc.dao.mapper.DataSourceInfoMapper;
import com.idc.service.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("DataSourceServiceImpl")
public class DataSourceServiceImpl implements DataSourceService {
    @Autowired
    private DataSourceInfoMapper dataSourceInfoMapper;

    @Override
    public void create(String dataSource, String dataName, String datacomment) {
        DataSourceInfo dataSourceInfo = createDataSourceInfo(dataSource, dataName, datacomment);
        dataSourceInfoMapper.insertDataSourceInfo(dataSourceInfo);
        dataSourceInfoMapper.createNodeInfoTable(dataSourceInfo.getNodeInfoTblName(),
                dataSourceInfo.getDataName() + "节点表");
        dataSourceInfoMapper.createNodeDataTable(dataSourceInfo.getNodeDataTblName(),
                dataSourceInfo.getDataName() + "节点表");
    }

    @Override
    public void deleteTable(String dataSource) {
        DataSourceInfo dataSourceInfo = dataSourceInfoMapper.selectDataByDataSource(dataSource);
        if (dataSourceInfo == null) {
            return;
        }
        dataSourceInfoMapper.deleteTable(dataSourceInfo.getNodeDataTblName());
        dataSourceInfoMapper.deleteTable(dataSourceInfo.getNodeInfoTblName());
        dataSourceInfoMapper.deleteDataByDataSource(dataSource);
    }

    @Override
    public void clearDataByDataSource(String dataSource) {
        DataSourceInfo dataSourceInfo = dataSourceInfoMapper.selectDataByDataSource(dataSource);
        if (dataSourceInfo == null) {
            return;
        }
        dataSourceInfoMapper.clearTable(dataSourceInfo.getNodeDataTblName());
        dataSourceInfoMapper.clearTable(dataSourceInfo.getNodeInfoTblName());
    }

    private DataSourceInfo createDataSourceInfo(String dataSource, String dataName, String datacomment) {
        DataSourceInfo dataSourceInfo = new DataSourceInfo();
        dataSourceInfo.setDataSource(dataSource);
        dataSourceInfo.setDataName(dataName);
        dataSourceInfo.setNodeDataTblName("idc_node_data_" + dataSource.toLowerCase());
        dataSourceInfo.setNodeInfoTblName("idc_node_info_" + dataSource.toLowerCase());
        dataSourceInfo.setDatacomment(datacomment);
        dataSourceInfo.setCreateDate(new Date());
        dataSourceInfo.setUpdateDate(new Date());
        return dataSourceInfo;
    }

}
