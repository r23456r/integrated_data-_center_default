package com.idc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 主程序
 */
@SpringBootApplication
@MapperScan("com.idc.dao.mapper")
@EnableAsync
public class IntergrateDataCenterApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(IntergrateDataCenterApplication.class, args);

    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(IntergrateDataCenterApplication.class);
    }
}
