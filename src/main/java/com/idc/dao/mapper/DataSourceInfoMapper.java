package com.idc.dao.mapper;

import com.idc.dao.entity.DataSourceInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DataSourceInfoMapper {
    /**
     * 插入数据
     *
     * @param dataInfo
     */
    void insertDataSourceInfo(@Param("dataInfo") DataSourceInfo dataInfo);

    /**
     * 根据数据源查询查询数据
     *
     * @param dataSource
     * @return
     */
    DataSourceInfo selectDataByDataSource(@Param("dataSource") String dataSource);

    /**
     * 根据数据源查询删除数据
     *
     * @param dataSource
     */
    void deleteDataByDataSource(@Param("dataSource") String dataSource);

    /**
     * 清空数据表
     *
     * @param tableName
     */
    void clearTable(@Param("tableName") String tableName);

    /**
     * 清空数据表
     *
     * @param tableName
     */
    void deleteTable(@Param("tableName") String tableName);

    /**
     * 创建节点表
     *
     * @param tableName
     * @param tableComment
     */
    void createNodeInfoTable(@Param("tableName") String tableName, @Param("tableComment") String tableComment);

    /**
     * 创数据表点表
     *
     * @param tableName
     * @param tableComment
     */
    void createNodeDataTable(@Param("tableName") String tableName, @Param("tableComment") String tableComment);
}
