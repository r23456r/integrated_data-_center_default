package com.idc.controller;

import com.idc.common.utils.TextIOStreamUtils;
import com.idc.service.DataIntegrateService;
import com.idc.service.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/datasource")
public class DataSourceController {


    @Autowired
    private DataSourceService dataSourceService;


    @Autowired
    private DataIntegrateService dataIntegrateService;

    @ResponseBody
    @RequestMapping("/create")
    public String createDataSource() {
        dataSourceService.create("XHCountryData", "新华数据网", "http://dc.xinhua08.com/");
        return "ok";
    }

    @ResponseBody
    @RequestMapping("/deleteTable")
    public String deleteTable() {
        dataSourceService.deleteTable("GlobalFirePower");
        return "ok";
    }


    @ResponseBody
    @RequestMapping("/clear")
    public String clearDataSource() {
        dataSourceService.clearDataByDataSource("GlobalFirePower");
        return "ok";
    }

    @ResponseBody
    @RequestMapping("/getAlldata")
    public String getAlldata() {
        //GlobalFirePower
        String jsonStr = dataIntegrateService.getAlldata("XHCountryData");
        //XHCountryData
        TextIOStreamUtils.writeByFileWrite("D://XHCountryData.json", jsonStr);
        return jsonStr;
    }

}
