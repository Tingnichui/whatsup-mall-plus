package com.chunhui;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan(value = "com.chunhui.dao")
@SpringBootApplication
public class WhatsupMallPlusApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhatsupMallPlusApplication.class, args);
    }

}
