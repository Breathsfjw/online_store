package com.jxau.store.fjw.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.jxau.store.fjw.user.mapper")
public class StoreFjwUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreFjwUserApplication.class, args);
    }

}
