package com.idc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.idc.common.utils.JsonVoConvertUtils;
import com.idc.common.utils.TextIOStreamUtils;
import com.idc.common.utils.UtilHandle;
import com.idc.dao.entity.DataSourceInfo;
import com.idc.dao.entity.IDCNodeDataVo;
import com.idc.dao.entity.IDCNodeInfoVo;
import com.idc.dao.mapper.DataSourceInfoMapper;
import com.idc.dao.mapper.IDCNodeDataMapper;
import com.idc.dao.mapper.IDCNodeInfoMapper;
import com.idc.service.DataIntegrateService;
import com.idc.vo.IntegratedDataVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("DataIntegrateServiceImpl")
public class DataIntegrateServiceImpl implements DataIntegrateService {

    @Autowired
    private IDCNodeInfoMapper idcNodeInfoMapper;

    @Autowired
    private IDCNodeDataMapper idcNodeDataMapper;

    @Autowired
    private DataSourceInfoMapper dataSourceInfoMapper;

    @Override
    public boolean insertData(String dataSource, JSONObject jsonData) {
        // 查询表名称
        DataSourceInfo dataSourceInfo = dataSourceInfoMapper.selectDataByDataSource(dataSource);
        if (dataSourceInfo == null) {
            return false;
        }
        if (jsonData == null || jsonData.isEmpty()) {
            return false;
        }
        IntegratedDataVo integratedDataVo = JsonVoConvertUtils.jsonToVo(jsonData, dataSource);
        List<IDCNodeInfoVo> idcNodeInfoVos = idcNodeInfoMapper.selectAllIDCNodeInfos(dataSourceInfo.getNodeInfoTblName());
        List<IDCNodeDataVo> idcNodeDataVos = idcNodeDataMapper.selectAllIDCNodeDatas(dataSourceInfo.getNodeDataTblName());
        UtilHandle.compareIDCNode(idcNodeInfoVos, idcNodeDataVos, integratedDataVo);
        // 数据更新
        updateDatastoDB(dataSourceInfo, integratedDataVo);
        return true;
    }

    @Test
    public void test(){
        String jsonStr= getAlldata("GlobalFirePower");

//        System.out.println(jsonObject.toJSONString());
    }

    @Override
    public String getAlldata(String dataSource) {

        // 查询表名称
        DataSourceInfo dataSourceInfo = dataSourceInfoMapper.selectDataByDataSource(dataSource);
        if (dataSourceInfo == null) {
            return "";
        }

        List<IDCNodeInfoVo> idcNodeInfoVos = idcNodeInfoMapper.selectAllIDCNodeInfos(dataSourceInfo.getNodeInfoTblName());
        List<IDCNodeDataVo> idcNodeDataVos = idcNodeDataMapper.selectAllIDCNodeDatas(dataSourceInfo.getNodeDataTblName());

        return JsonVoConvertUtils.voToJson(idcNodeInfoVos, idcNodeDataVos).toJSONString();
    }

    private void updateDatastoDB(DataSourceInfo dataSourceInfo, IntegratedDataVo integratedDataVo) {
        if (integratedDataVo.getNodeInfos() != null && !integratedDataVo.getNodeInfos().isEmpty()) {
            batchIDCNodeInfos(dataSourceInfo.getNodeInfoTblName(), integratedDataVo.getNodeInfos(), "insert");
        }
        if (integratedDataVo.getNodeDatas() != null && !integratedDataVo.getNodeDatas().isEmpty()) {
            batchIDCNodeDatas(dataSourceInfo.getNodeDataTblName(), integratedDataVo.getNodeDatas(), "insert");
        }
        if (integratedDataVo.getUpdateNodeInfos() != null && !integratedDataVo.getUpdateNodeInfos().isEmpty()) {
            batchIDCNodeInfos(dataSourceInfo.getNodeInfoTblName(), integratedDataVo.getUpdateNodeInfos(), "update");
        }
        if (integratedDataVo.getUpdateNodeDatas() != null && !integratedDataVo.getUpdateNodeDatas().isEmpty()) {
            batchIDCNodeDatas(dataSourceInfo.getNodeDataTblName(), integratedDataVo.getUpdateNodeDatas(), "update");
        }
    }

    private void batchIDCNodeInfos(String tableName, List<IDCNodeInfoVo> batchList, String type) {
        int length = batchList.size();
        int groupSize = 10000;
        int num = (length + groupSize - 1) / groupSize;
        for (int i = 0; i < num; i++) {
            int fromIndex = i * groupSize;
            int toIndex = Math.min((i + 1) * groupSize, length);
            if ("insert".equals(type)) {
                idcNodeInfoMapper.batchInsertIDCNodeInfos(tableName, batchList.subList(fromIndex, toIndex));
            } else {
                idcNodeInfoMapper.batchUpdateIDCNodeInfos(tableName, batchList.subList(fromIndex, toIndex));
            }

        }
    }

    private void batchIDCNodeDatas(String tableName, List<IDCNodeDataVo> batchList, String type) {
        int length = batchList.size();
        int groupSize = 10000;
        int num = (length + groupSize - 1) / groupSize;
        for (int i = 0; i < num; i++) {
            int fromIndex = i * groupSize;
            int toIndex = (i + 1) * groupSize < length ? (i + 1) * groupSize : length;
            if ("insert".equals(type)) {
                idcNodeDataMapper.batchInsertIDCNodeDatas(tableName, batchList.subList(fromIndex, toIndex));
            } else {
                idcNodeDataMapper.batchUpdateIDCNodeDatas(tableName, batchList.subList(fromIndex, toIndex));
            }
        }
    }


}
