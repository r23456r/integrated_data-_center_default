package com.idc.dao.mapper;

import com.idc.dao.entity.IDCNodeDataVo;
import com.idc.dao.entity.IDCNodeInfoVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDCNodeDataMapper {
    /**
     * 批量插入数据
     *
     * @param tableName
     * @param dataVoList
     */
    void batchInsertIDCNodeDatas(@Param("tableName") String tableName, @Param("dataVoList") List<IDCNodeDataVo> dataVoList);

    /**
     * 批量更新数据
     *
     * @param tableName
     * @param dataVoList
     */
    void batchUpdateIDCNodeDatas(@Param("tableName") String tableName, @Param("dataVoList") List<IDCNodeDataVo> dataVoList);

    /**
     * 查询所有数据
     *
     * @param tableName
     * @return
     */
    List<IDCNodeDataVo> selectAllIDCNodeDatas(@Param("tableName") String tableName);
}
