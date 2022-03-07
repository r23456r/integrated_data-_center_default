package com.idc.dao.mapper;

import com.idc.dao.entity.IDCNodeInfoVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDCNodeInfoMapper {
    /**
     * 批量插入数据
     *
     * @param tableName
     * @param infoVoList
     */
    void batchInsertIDCNodeInfos(@Param("tableName") String tableName, @Param("infoVoList") List<IDCNodeInfoVo> infoVoList);

    /**
     * 批量更新数据
     *
     * @param tableName
     * @param infoVoList
     */
    void batchUpdateIDCNodeInfos(@Param("tableName") String tableName, @Param("infoVoList") List<IDCNodeInfoVo> infoVoList);

    /**
     * 查询所有数据
     *
     * @param tableName
     * @return
     */
    List<IDCNodeInfoVo> selectAllIDCNodeInfos(@Param("tableName") String tableName);
}
