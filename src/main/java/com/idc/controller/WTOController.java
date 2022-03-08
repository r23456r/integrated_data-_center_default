package com.idc.controller;

import cn.hutool.Hutool;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.idc.service.DataIntegrateService;
import com.idc.service.DataService;
import com.idc.service.impl.WTODataServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

@Controller
@RequestMapping("/wto")
public class WTOController {

    @Autowired
    private DataIntegrateService dataIntegrateService;

    @Autowired
    @Qualifier(value = "WTODataServiceImpl")
    private DataService dataService;

    @ResponseBody
    @GetMapping("/getData")
    public String getData() throws IOException {
        for (Integer integer : Arrays.asList(156, 840, 392, 408, 410, 643, 826, 040, 804, 923, 918, 928)) {
            JSONObject jsonObject = dataService.getDataInfo(integer);
            writeFile("C:\\Users\\PandaIP\\Desktop\\"+integer+".json", jsonObject.toString());
            System.out.println(jsonObject.toJSONString());
        }
        return "success";
    }
    public static void writeFile(String filePath, String sets)
            throws IOException {
        FileWriter fw = new FileWriter(filePath);
        PrintWriter out = new PrintWriter(fw);
        out.write(sets);
        out.println();
        fw.close();
        out.close();
    }

    @Test
    public void test() throws IOException {
        writeFile("C:\\Users\\PandaIP\\Desktop\\"+1+".json", "111");
    }
}
