package com.jxau.store.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.jxau.store.manage.mapper")
public class StoreManageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreManageServiceApplication.class, args);
    }

}
