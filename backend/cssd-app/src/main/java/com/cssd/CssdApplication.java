package com.cssd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cssd.infra.mapper")
public class CssdApplication {
    public static void main(String[] args) {
        SpringApplication.run(CssdApplication.class, args);
    }
}
