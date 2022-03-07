package com.idc.controller;

import com.alibaba.fastjson.JSONObject;
import com.idc.service.DataIntegrateService;
import com.idc.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/globalfirepower")
public class GlobalFirePowerController {

    @Autowired
    private DataIntegrateService dataIntegrateService;

    @Qualifier("GlobalFirePowerDataService")
    @Autowired
    private DataService dataService;

    @ResponseBody
    @RequestMapping("/getdatabyweb")
    public String getDataByWeb() {
        JSONObject jsonObject = dataService.getDataInfo();
        dataIntegrateService.insertData("GlobalFirePower", jsonObject);
        return jsonObject.toJSONString();
    }

}
