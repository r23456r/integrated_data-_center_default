/**
  * Copyright 2022 bejson.com 
  */
package com.idc.dao.entity;

import lombok.Data;

/**
 * Auto-generated: 2022-03-07 17:21:45
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class IndicatorBean {

    private String code;
    private String name;
    private String categoryCode;
    private String categoryLabel;
    private String subcategoryCode;
    private String subcategoryLabel;
    private String unitCode;
    private String unitLabel;
    private int startYear;
    private int endYear;
    private String frequencyCode;
    private String frequencyLabel;
    private int numberReporters;
    private String numberPartners;
    private String productSectorClassificationCode;
    private String productSectorClassificationLabel;
    private String hasMetadata;
    private int numberDecimals;
    private int numberDatapoints;
    private String updateFrequency;
    private String description;
    private int sortOrder;
}