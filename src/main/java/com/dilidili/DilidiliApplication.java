package com.dilidili;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dilidili.dao.mapper") // 扫描 Java 接口所在的包
public class DilidiliApplication {
    public static void main(String[] args) {
        SpringApplication.run(DilidiliApplication.class, args);
    }
}