package com.idc.service;

public interface DataSourceService {
    public void create(String dataSource, String dataName, String datacomment);

    public void deleteTable(String dataSource);

    public void clearDataByDataSource(String dataSource);
}
