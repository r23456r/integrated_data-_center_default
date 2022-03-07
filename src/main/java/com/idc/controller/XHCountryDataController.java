package com.idc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.idc.service.DataIntegrateService;
import com.idc.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/xhcountrydata")
public class XHCountryDataController {

    @Autowired
    private DataIntegrateService dataIntegrateService;

    @Qualifier("XHCountryDataService")
    @Autowired
    private DataService dataService;

    @ResponseBody
    @RequestMapping("/getdatabyweb")
    public String getDataByWeb() {
        JSONObject jsonObject = dataService.getDataInfo();
        dataIntegrateService.insertData("XHCountryData", jsonObject);
        return jsonObject.toJSONString();
    }


}
